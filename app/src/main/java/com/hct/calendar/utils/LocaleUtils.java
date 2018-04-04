package com.hct.calendar.utils;

import java.util.Locale;

import android.annotation.NonNull;
import android.content.Context;

import com.apkfuns.logutils.LogUtils;

public class LocaleUtils {

    private LocaleUtils() {

    }

    public static String getLocaleLanguage(@NonNull Context context) {

        if (null == context) {
            return "zh";
        }

        if (null == context.getResources()) {
            return "zh";
        }

        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        return language;
    }

    public static String getLocaleCountry(@NonNull Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String country = locale.getCountry();
        return country;
    }

    public static boolean isChinese(@NonNull Context context) {
        return "zh".equalsIgnoreCase(getLocaleLanguage(context));
    }

    public static String getLang(@NonNull Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        // lang = zh , CN
        LogUtils.e("lang = " + language + " , " + country);
        String lang = String.format("%s-%s", language, country);
        LogUtils.e(lang);
        return lang;
    }
}
