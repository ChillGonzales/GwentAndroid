package com.example.colin.gwentsite;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button getCardsButton;
    static MainActivity instance;
    private String _baseUri = "https://api.gwentapi.com/v0/";
    private String cardsEndpoint = "cards";
    private String variationEndpoint = "variations";
    private JSONObject cardsJson = null;
    private ArrayList<Bitmap> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup broadcast receiver and intent service
        IntentFilter resultFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        final CardResultReceiver receiver = new CardResultReceiver();
        final VariationReceiver varReceiver = new VariationReceiver();
        final ArtworkReceiver artworkReceiver = new ArtworkReceiver();

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, resultFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(varReceiver, resultFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(artworkReceiver, resultFilter);

        //Setup click event
        getCardsButton = (Button) findViewById(R.id.getCardsButton);
        getCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                /*
                 * Creates a new Intent to start the RSSPullService
                 * IntentService. Passes a URI in the
                 * Intent's "data" field.
                 */
                Intent mServiceIntent = new Intent(MainActivity.this, RetrieveCardsIntentService.class);
                mServiceIntent.setData(Uri.parse(_baseUri + cardsEndpoint + "?limit=30&offset=40"));
                MainActivity.this.startService(mServiceIntent);
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void getCardsCallback(String cardData) {
        //Toast toast = Toast.makeText(getApplicationContext(), cardData, Toast.LENGTH_LONG);
        //toast.show();
        try {
            cardsJson = new JSONObject(cardData);
            JSONArray cardArray = (JSONArray) cardsJson.get("results");

            for (int i = 0; i < cardArray.length(); i++) {
                // Get card info from results array
                JSONObject card = cardArray.getJSONObject(i);
                String[] split = card.get("href").toString().split("/");
                CardInfo ci = new CardInfo(card.get("name").toString(), split[split.length - 1]);

                // Get variation detail from card UUID
                //Intent mVariationIntent = new Intent(MainActivity.this, RetrieveVariationIntentService.class);
                //mVariationIntent.setData(Uri.parse(_baseUri + cardsEndpoint + "/" + ci.uuid + "/" + variationEndpoint));
                //MainActivity.this.startService(mVariationIntent);
                //break;

                //ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                //URL url = classLoader.getResource("./src/main/assets/" + ci.uuid + ".info");
                //File file = new File("C:\\Users\\Colin\\Documents\\src\\GwentSite\\app\\src\\main\\assets\\" + ci.uuid + ".vrtn");

                //Read text from file
                StringBuilder text = new StringBuilder();

                try {
                    InputStream input = getApplicationContext().getAssets().open(ci.uuid + ".vrtn");
                    BufferedReader br = new BufferedReader(new InputStreamReader(input));
                    String line;

                    while ((line = br.readLine()) != null) {
                        text.append(line);
                        text.append('\n');
                    }
                    br.close();
                    getVariationCallback(text.toString());
                }
                catch (Exception e) {
                    //You'll need to add proper error handling here
                    Toast toast1 = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
                    toast1.show();
                }
            }
            if (images != null) {
                GridView list = (GridView) findViewById(R.id.gridView);
                list.setAdapter(new ImageAdapter(getApplicationContext(), images));
            }
        } catch (Exception ex) {
        }
    }
    public void getVariationCallback(String variationData) {
        try {
            //Toast toast = Toast.makeText(getApplicationContext(), variationData, Toast.LENGTH_LONG);
            //toast.show();
            JSONObject arr = new JSONObject(variationData);
            // Get artwork data from variation
            Intent mArtworkIntent = new Intent(MainActivity.this, RetrieveArtworkIntentService.class);
            JSONObject art = new JSONObject(arr.get("Art").toString());
            mArtworkIntent.setData(Uri.parse(art.get("ThumbnailImage").toString()));
            MainActivity.this.startService(mArtworkIntent);
        } catch (Exception e)
        {
            Toast toast = Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    }
    public void getArtworkCallback(Bitmap artData) {
        images.add(artData);
        //ImageView img = (ImageView) findViewById(R.id.cardArt);
        //img.setImageBitmap(artData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
