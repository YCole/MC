package com.android.calendar.smsservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// import android.os.SystemProperties;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        return;
    }

    /*
     * @Override public void onReceive(Context context, Intent intent) {
     * 
     * String isAbroad = SystemProperties.get("ro.gios.custom", "home");
     * if(isAbroad.equals("abroad")){
     * Log.d(TAG,"lxg SMSReceiver isAbroad.equals abroad"); return; } boolean
     * bSmartReminder = Utils.getConfigBool(context, R.bool.smart_reminder);
     * 
     * SharedPreferences prefs =
     * GeneralPreferences.getSharedPreferences(context); boolean isEnable =
     * prefs.getBoolean(GeneralPreferences.KEY_SMART_REMINDER, false); if
     * (!isEnable || !bSmartReminder) { Log.i(TAG, "lxg onReceive, Disabled");
     * return; } Log.i(TAG, "lxg onReceive, Enabled");
     * 
     * String message = new String(); String address = null;
     * 
     * SmsMessage[] smsMessage = Intents.getMessagesFromIntent(intent);
     * 
     * /*Bundle bundle = intent.getExtras(); Object messages[] = (Object[])
     * bundle.get("pdus"); SmsMessage smsMessage[] = new
     * SmsMessage[messages.length]; for (int n = 0; n < messages.length; n++) {
     * smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]); }
     */
    /*
     * for (SmsMessage currMsg : smsMessage) { if (address == null) { address =
     * currMsg.getDisplayOriginatingAddress(); } message = message +
     * currMsg.getDisplayMessageBody(); //StringHandler.HandleMessage(context,
     * currMsg.getDisplayMessageBody(), currMsg.getDisplayOriginatingAddress());
     * } Log.i(TAG, "onReceive, message = "+message);
     * StringHandler.HandleMessage(context, message, address);
     * 
     * }
     */

}
