package com.hct.gios.widget;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    static String MY_SWITCH_COLOR = "myswitchcolor";
    static String MY_RADIO_COLOR = "myraidocolor";
    static String MY_CHECK_COLOR = "mycheckcolor";
    static String STORE_COLOR = "color_store";
    public static int DEFAULT_COLOR = 0xff318bde;
    public static final String Version = "4.0.1.0505";

    public static int dpToPx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    public static int getSwitchColor(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_SWITCH_COLOR, 0);
        return sp.getInt(STORE_COLOR, 0);
    }

    public static void storeSwitchColor(Context context, int color) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_SWITCH_COLOR, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STORE_COLOR, color);
        editor.commit();
    }

    public static int getRadioColor(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_RADIO_COLOR, 0);
        return sp.getInt(STORE_COLOR, 0);
    }

    public static void storeRadioColor(Context context, int color) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_RADIO_COLOR, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STORE_COLOR, color);
        editor.commit();
    }

    public static int getCheckColor(Context context) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_CHECK_COLOR, 0);
        return sp.getInt(STORE_COLOR, 0);
    }

    public static void storeCheckColor(Context context, int color) {
        SharedPreferences sp;
        sp = context.getSharedPreferences(MY_CHECK_COLOR, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(STORE_COLOR, color);
        editor.commit();
    }

    public static String getVsersion() {

        return Version;
    }
}
