package com.hct.calendar.lbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationFenceReceiver extends BroadcastReceiver {

    private final static String TAG = "LocationFenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) {
            Log.w(TAG, "action is null");
            return;
        }
        if (!context.getPackageName().equals(
                intent.getStringExtra("packageName"))) {
            Log.w(TAG,
                    "packageName is null or not calendar package,"
                            + intent.getStringExtra("packageName"));
            return;
        }
        LocationFenceService.processLocationBroadcastIntent(context, intent);
    }
}
