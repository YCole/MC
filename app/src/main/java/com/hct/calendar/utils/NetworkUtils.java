package com.hct.calendar.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.calendar.CalendarApplication;

public class NetworkUtils {
    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivity = (ConnectivityManager) CalendarApplication
                .getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }
}
