package com.example.colin.gwentsite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

/**
 * Created by Colin on 7/6/2017.
 */

public class CardResultReceiver extends BroadcastReceiver {
    // Called when the BroadcastReceiver gets an Intent it's registered to receive
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity.getInstance().getCardsCallback((String) intent.getExtras().get(Constants.CARD_PAGE_RESULT));
    }
}
