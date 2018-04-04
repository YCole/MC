package com.hct.calendar.lbs;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HctRingtoneManager {
    private static final String TAG = "HctRingtoneManager";

    public static int getValue(String type) {
        int ret = -1;
        try {
            Class<?> c = Class.forName("android.media.RingtoneManager");
            Field f = c.getField(type);
            ret = (int) f.get(c);
        } catch (Exception ex) {
            Log.e(TAG, "getValue() Exception ex="+ex);
        }
        Log.d(TAG, "getValue() ret="+ret);
        return ret;
    }

    public static void setActualDefaultRingtoneUri(Context cx, int type, Uri ringUri) {
        try {
            Class<?> c = Class.forName("android.media.RingtoneManager");
            Method method = c.getMethod("setActualDefaultRingtoneUri", Context.class, int.class, Uri.class);
            method.invoke(c, cx, type, ringUri);
        } catch (Exception ex) {
            Log.e(TAG, "setActualDefaultRingtoneUri() Exception ex="+ex);
        }
    }
}
