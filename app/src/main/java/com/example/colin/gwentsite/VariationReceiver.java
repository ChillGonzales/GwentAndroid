package com.example.colin.gwentsite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by colin.monroe on 7/7/2017.
 */

public class VariationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getExtras().get(Constants.VARIATION_RESULT) != null) {
            MainActivity.getInstance().getVariationCallback((String) intent.getExtras().get(Constants.VARIATION_RESULT));
        }
    }
}
