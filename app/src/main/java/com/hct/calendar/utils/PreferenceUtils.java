package com.hct.calendar.utils;

import android.annotation.NonNull;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceUtils {
    public static final String SHOWALMANC_KEY = "preferences_show_almanac";

    public static SharedPreferences get(@NonNull Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean getBoolean(@NonNull Context context,
            @NonNull String key, boolean defValue) {
        return get(context).getBoolean(key, defValue);
    }

    public static String getString(@NonNull Context context,
            @NonNull String key, String defValue) {
        return get(context).getString(key, defValue);
    }
}
