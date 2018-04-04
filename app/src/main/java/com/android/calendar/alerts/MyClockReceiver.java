package com.android.calendar.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.calendar.event.EventNotifyActivity;

public class MyClockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        long eventId = intent.getLongExtra("eventId", 0);
        long startTime = intent.getLongExtra("StartTime", 0);
        long endTime = intent.getLongExtra("endTime", 0);
        boolean allDay = intent.getBooleanExtra("allDay", false);
        String title = intent.getStringExtra("title");
        Log.e("fushuo", "MyClockReceiver--->eventId=" + eventId + "---->title="
                + title);
        Bundle bundle = new Bundle();
        bundle.putLong("eventId", eventId);
        bundle.putLong("StartTime", startTime);
        bundle.putLong("endTime", endTime);
        bundle.putString("title", title);
        bundle.putBoolean("allDay", allDay);
        intent = new Intent(context, EventNotifyActivity.class);
        intent.putExtra("data", bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // context.startActivity(intent);
    }

}
