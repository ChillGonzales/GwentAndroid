package com.example.colin.gwentsite;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button getCardsButton;
    static MainActivity instance;
    private String _baseUri = "https://api.gwentapi.com/v0/";
    private String cardsEndpoint = "cards";

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
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, resultFilter);

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
                mServiceIntent.setData(Uri.parse(_baseUri + cardsEndpoint));
                MainActivity.this.startService(mServiceIntent);
            }
        });
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public void getCardsCallback(String cardData) {
        Toast toast = Toast.makeText(getApplicationContext(), cardData, Toast.LENGTH_LONG);
        toast.show();
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
