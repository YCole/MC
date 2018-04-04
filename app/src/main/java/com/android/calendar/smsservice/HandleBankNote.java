package com.android.calendar.smsservice;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;

public class HandleBankNote extends Activity {

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(2);

        finish();
    }
}
