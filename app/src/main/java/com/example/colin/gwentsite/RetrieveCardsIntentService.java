package com.example.colin.gwentsite;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Colin on 7/6/2017.
 */
public class RetrieveCardsIntentService extends IntentService {
    public RetrieveCardsIntentService(String name) {
        super(name);
    }
    public RetrieveCardsIntentService() {
        super("intent");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        try {
            //Gets data from the incoming intent
            String data = workIntent.getDataString();
            String returnVal = doInBackground(data);
            /*
             * Creates a new Intent containing a Uri object
             * BROADCAST_ACTION is a custom Intent action
            */
            Intent localIntent = new Intent(Constants.BROADCAST_ACTION)
                            // Puts the status into the Intent
                            .putExtra(Constants.EXTENDED_DATA_RESULT, returnVal);
            // Broadcasts the Intent to receivers in this app.
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        }
        catch (Exception e) {
            //Do something?
        }
    }

    private String doInBackground(String connectionStr) throws Exception {
        HttpsURLConnection apiConnection = null;
        try {
            URL endPoint = new URL(connectionStr);
            apiConnection = (HttpsURLConnection) endPoint.openConnection();
            apiConnection.setRequestMethod("GET");
            apiConnection.setRequestProperty("Accept","*/*");
            apiConnection.connect();
            if (apiConnection.getResponseCode() != 200) {
                return "An error code of " + apiConnection.getResponseCode() + " was returned by the request.";
            }
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            //JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());
            return responseStrBuilder.toString();
        } catch (Exception e){
            return e.toString();
        } finally {
            apiConnection.disconnect();
        }
    }
}
