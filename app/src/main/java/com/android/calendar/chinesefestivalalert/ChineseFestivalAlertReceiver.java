/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.chinesefestivalalert;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Receives android.intent.action.EVENT_REMINDER intents and handles event
 * reminders. The intent URI specifies an alert id in the CalendarAlerts
 * database table. This class also receives the BOOT_COMPLETED intent so that it
 * can add a status bar notification if there are Calendar event alarms that
 * have not been dismissed. It also receives the TIME_CHANGED action so that it
 * can fire off snoozed alarms that have become ready. The real work is done in
 * the AlertService class.
 * 
 * To trigger this code after pushing the apk to device: adb shell am broadcast
 * -a "android.intent.action.EVENT_REMINDER" -n
 * "com.android.calendar/.alerts.AlertReceiver"
 */
public class ChineseFestivalAlertReceiver extends BroadcastReceiver {

    private static final String TAG = "ChineseFestivalAlertReceiver";
    private String CANCEL_NOTIFY = "com.android.calendar.cancel_notify";
    NotificationManager mNotificationManager;
    private Context mContext;
    private static final long messageMinInterval = 1 * 60 * 60 * 1000;
    private long mLastMessageMills = 0;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1: {
                basicfounction.downloadXML(mContext, this);
                break;
            }
            case 2: {
                boolean isSetTomorrow = msg.arg1 == 0;
                Log.d(TAG, "lxg SetTomorrow = " + isSetTomorrow);
                if (!isSetTomorrow) {
                    try {
                        ChineseFestivalRawData.refrashData();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                basicfounction.creatDownloadAlarm(mContext, isSetTomorrow);
            }
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "lxg onReceive, Intent.action=" + action
                + ", LastMessageMills = " + mLastMessageMills
                + ", SystemMills = " + System.currentTimeMillis());
        mContext = context;
        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        boolean isSendMessage = false;
        if (mLastMessageMills == 0) {
            mLastMessageMills = System.currentTimeMillis();
            isSendMessage = true;
        } else {
            isSendMessage = System.currentTimeMillis() - mLastMessageMills > messageMinInterval ? true
                    : false;
        }

        if (CANCEL_NOTIFY.equals(action)) {
            mNotificationManager.cancel(1);
            Log.v(TAG, "CANCEL_NOTIFY");
        } else if (((action.equals(Intent.ACTION_BOOT_COMPLETED) || action
                .equals(Intent.ACTION_TIME_CHANGED) && isSendMessage))
                || action.equals(basicfounction.DOWNLOADXML)) {
            Message message = handler.obtainMessage();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
}
