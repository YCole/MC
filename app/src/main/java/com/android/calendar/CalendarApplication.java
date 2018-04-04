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

package com.android.calendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.util.Log;
import android.util.SparseIntArray;

import com.apkfuns.logutils.LogUtils;
import com.facebook.stetho.Stetho;
import com.hct.calendar.utils.CalendarUtil;

public class CalendarApplication extends Application {
    public static Context mContext;
    private static CalendarApplication instance;
    public static SparseIntArray weatherIconList = new SparseIntArray();
    public static SparseIntArray weatherText = new SparseIntArray();

    public static SparseIntArray accuWeatherIcon = new SparseIntArray();
    public static SparseIntArray accuWeatherText = new SparseIntArray();
    public static List<Map<String, String>> yangList = new ArrayList<>();
    public static List<Map<String, String>> yingList = new ArrayList<>();
    public static List<Integer> yearLists = new ArrayList<>();
    public static int current = 0;
    private String yearStr;
    private String monthStr1;
    private String dayStr1;
    LunarDateThread thread;
    private final String SETTINGS_SP_NAME = "settings";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mContext = this;
        thread = new LunarDateThread();

        Stetho.initializeWithDefaults(this);
        /*
         * Ensure the default values are set for any receiver, activity,
         * service, etc. of Calendar
         */
        GeneralPreferences.setDefaultValues(this);

        // Save the version number, for upcoming 'What's new' screen. This will
        // be later be
        // moved to that implementation.
        Utils.setSharedPreference(this, GeneralPreferences.KEY_VERSION,
                Utils.getVersionCode(this));

        // Initialize the registry mapping some custom behavior.
        LogUtils.getLogConfig().configShowBorders(false);
        ExtensionsFactory.init(getAssets());

        // add by zyp start
        // initRing();
        // add by zyp end
        thread.start();
        SharedPreferences sp = getContext().getSharedPreferences("launchState",
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("isLaunch", true);
        editor.apply();
    }

    // // add by zyp
    // private void initRing() {
    // new Thread(new Runnable() {
    // @Override
    // public void run() {
    // SharedPreferences sp = getSharedPreferences(SETTINGS_SP_NAME,
    // Context.MODE_PRIVATE);
    // if (!sp.contains("ringtone_uri")) {
    // String mNotificationUri_notify = RingtoneManager
    // .getActualDefaultRingtoneUri(getContext(),
    // RingtoneManager.TYPE_NOTIFICATION).toString();
    // sp.edit().putString("ringtone_uri", mNotificationUri_notify).commit();
    // Log.d("PRODUCTION-2877", "initRing :" + mNotificationUri_notify);
    // }
    // // String selectedRingtoneUri = sp.getString("ringtone_uri",
    // // "content://settings/system/notification_sound");
    // // sp.edit().putString("ringtone_uri",
    // // selectedRingtoneUri).commit();
    // // Log.d("PRODUCTION-2877", "initRing");
    // }
    // }).start();
    //
    // }

    class LunarDateThread extends Thread {
        @Override
        public void run() {

            // Corresponding to domestic cities
            initWeatherIcon();
            initWeatherText();
            // Corresponding to international cities
            initAccuWeatherIcon();
            initAccuWeatherText();

            // yearStr = mContext.getString(R.string.event_year);
            // monthStr1 = mContext.getString(R.string.event_month);
            // dayStr1 = mContext.getString(R.string.event_day);

            // initDateTimePicker();
            // initYearDataList();
        }
    }

    protected void initYearDataList() {
        // TODO Auto-generated method stub
        yearLists.clear();
        Calendar c = Calendar.getInstance();
        for (int i = 1970; i < 2038; i++) {
            yearLists.add(i);
        }
    }

    private void initDateTimePicker() {
        yangList.clear();
        yingList.clear();
        Calendar c = Calendar.getInstance();
        int y = c.get(Calendar.YEAR);
        int y1 = y - 1;
        int y2 = y + 1;
        for (; y1 <= y2; y1++) {
            for (int i = 1; i < 13; i++) {
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10
                        || i == 12) {
                    for (int j = 1; j < 32; j++) {
                        formatDate(y1, i, j);
                    }
                } else if (i == 4 || i == 6 || i == 9 || i == 11) {
                    for (int j = 1; j < 31; j++) {
                        formatDate(y1, i, j);
                    }
                } else if (i == 2) {
                    if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
                        for (int j = 1; j < 30; j++) {
                            formatDate(y1, i, j);
                        }
                    } else {
                        for (int j = 1; j < 29; j++) {
                            formatDate(y1, i, j);
                        }
                    }
                }
            }
        }
        int year1 = c.get(Calendar.YEAR);
        int y3 = year1 - 1;
        int month1 = c.get(Calendar.MONTH);
        int day1 = c.get(Calendar.DAY_OF_MONTH);

        for (; y3 <= year1 - 1; y3++) {
            for (int i = 1; i < 13; i++) {
                if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10
                        || i == 12) {
                    current += 31;
                } else if (i == 4 || i == 6 || i == 9 || i == 11) {
                    current += 30;
                } else if (i == 2) {
                    if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
                        current += 29;
                    } else {
                        current += 28;
                    }
                }
            }
        }
        for (int w = 1; w < month1 + 1; w++) {
            if (w == 1 || w == 3 || w == 5 || w == 7 || w == 8 || w == 10
                    || w == 12) {
                current += 31;
            } else if (w == 4 || w == 6 || w == 9 || w == 11) {
                current += 30;
            } else if (w == 2) {
                if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
                    current += 29;
                } else {
                    current += 28;
                }
            }
        }
        current += day1 - 1;

    }

    private void formatDate(int y1, int i, int j) {
        String strM;
        String strD;
        if (i <= 9) {
            strM = "0" + i;
        } else {
            strM = String.valueOf(i);
        }
        if (j <= 9) {
            strD = "0" + j;
        } else {
            strD = String.valueOf(j);
        }
        Map<String, String> map = new HashMap<>();
        map.put("year", "" + y1 + yearStr);
        if (CalendarUtil.isEnglish()) {
            map.put("data", strM + monthStr1 + strD + dayStr1 + " "
                    + getWeek(y1 + "-" + i + "-" + j));
        } else {
            map.put("data", strM + monthStr1 + strD + dayStr1
                    + getWeek(y1 + "-" + i + "-" + j));
        }
        yangList.add(map);
        Map<String, String> yingMap = new HashMap<>();
        String[] lunar = CalendarUtil.getLunar(y1 + "", strM, strD);
        yingMap.put("year", lunar[0]);
        yingMap.put("data", lunar[1]);
        // Log.e("fushuo", "formatDate: " + lunar[0] + "---->" + lunar[1]);
        yingList.add(yingMap);
    }

    private String getWeek(String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {

            c.setTime(format.parse(pTime));

        } catch (Exception e) {
            e.printStackTrace();
        }
        String weekStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.week);
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            Week += weekStr[0];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
            Week += weekStr[1];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
            Week += weekStr[2];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
            Week += weekStr[3];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
            Week += weekStr[4];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            Week += weekStr[5];
        }
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            Week += weekStr[6];
        }
        return Week;
    }

    public static CalendarApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        // TODO Auto-generated method stub
        return mContext;
    }

    private void initAccuWeatherText() {
        accuWeatherText.put(1, R.string.sunny);
        accuWeatherText.put(2, R.string.sunny);
        accuWeatherText.put(3, R.string.sunny);
        accuWeatherText.put(4, R.string.mostly_sunny);
        accuWeatherText.put(5, R.string.hazy_sunshine);
        accuWeatherText.put(6, R.string.mostly_sunny);
        accuWeatherText.put(7, R.string.mostly_sunny);
        accuWeatherText.put(8, R.string.dreary);
        accuWeatherText.put(11, R.string.fog);
        accuWeatherText.put(12, R.string.showers);
        accuWeatherText.put(13, R.string.light_rain);
        accuWeatherText.put(14, R.string.showers);
        accuWeatherText.put(15, R.string.thunderstorms);
        accuWeatherText.put(16, R.string.thunderstorms);
        accuWeatherText.put(17, R.string.showers);
        accuWeatherText.put(18, R.string.rain);
        accuWeatherText.put(19, R.string.light_snow);
        accuWeatherText.put(20, R.string.light_snow);
        accuWeatherText.put(21, R.string.light_snow);
        accuWeatherText.put(22, R.string.snow);
        accuWeatherText.put(23, R.string.light_snow);
        accuWeatherText.put(24, R.string.ice);
        accuWeatherText.put(25, R.string.sleet);
        accuWeatherText.put(26, R.string.ice);
        accuWeatherText.put(29, R.string.sleet);
        accuWeatherText.put(30, R.string.hot);
        accuWeatherText.put(31, R.string.cold);
        accuWeatherText.put(32, R.string.windy);
        accuWeatherText.put(33, R.string.clear);
        accuWeatherText.put(34, R.string.clear);
        accuWeatherText.put(35, R.string.partly_cloudy);
        accuWeatherText.put(36, R.string.partly_cloudy);
        accuWeatherText.put(37, R.string.hazy_sunshine);
        accuWeatherText.put(38, R.string.partly_cloudy);
        accuWeatherText.put(39, R.string.showers);
        accuWeatherText.put(40, R.string.showers);
        accuWeatherText.put(41, R.string.thunderstorms);
        accuWeatherText.put(42, R.string.thunderstorms);
        accuWeatherText.put(43, R.string.mostly_cloudy_flurries);
        accuWeatherText.put(44, R.string.snow);
    }

    private void initAccuWeatherIcon() {
        accuWeatherIcon.put(1, R.drawable.sunny);
        accuWeatherIcon.put(2, R.drawable.sunny);
        accuWeatherIcon.put(3, R.drawable.sunny);
        accuWeatherIcon.put(4, R.drawable.cloudy);
        accuWeatherIcon.put(5, R.drawable.haze);
        accuWeatherIcon.put(6, R.drawable.cloudy);
        accuWeatherIcon.put(7, R.drawable.cloudy);
        accuWeatherIcon.put(8, R.drawable.overcast);
        accuWeatherIcon.put(11, R.drawable.foggy);
        accuWeatherIcon.put(12, R.drawable.shower);
        accuWeatherIcon.put(13, R.drawable.lightrain);
        accuWeatherIcon.put(14, R.drawable.shower);
        accuWeatherIcon.put(15, R.drawable.thundershower);
        accuWeatherIcon.put(16, R.drawable.thundershower);
        accuWeatherIcon.put(17, R.drawable.shower);
        accuWeatherIcon.put(18, R.drawable.moderaterain);
        accuWeatherIcon.put(19, R.drawable.lightsnow);
        accuWeatherIcon.put(20, R.drawable.lightsnow);
        accuWeatherIcon.put(21, R.drawable.lightsnow);
        accuWeatherIcon.put(22, R.drawable.moderatesnow);
        accuWeatherIcon.put(23, R.drawable.lightsnow);
        accuWeatherIcon.put(24, R.drawable.icerain);
        accuWeatherIcon.put(25, R.drawable.sleet);
        accuWeatherIcon.put(26, R.drawable.icerain);
        accuWeatherIcon.put(29, R.drawable.rainandsnow);
        accuWeatherIcon.put(30, R.drawable.hot);
        accuWeatherIcon.put(31, R.drawable.cold);
        accuWeatherIcon.put(32, R.drawable.wind);
        accuWeatherIcon.put(33, R.drawable.clear);
        accuWeatherIcon.put(34, R.drawable.clear);
        accuWeatherIcon.put(35, R.drawable.partlycloud);
        accuWeatherIcon.put(36, R.drawable.partlycloud);
        accuWeatherIcon.put(37, R.drawable.haze);
        accuWeatherIcon.put(38, R.drawable.partlycloud);
        accuWeatherIcon.put(39, R.drawable.shower);
        accuWeatherIcon.put(40, R.drawable.shower);
        accuWeatherIcon.put(41, R.drawable.thundershower);
        accuWeatherIcon.put(42, R.drawable.thundershower);
        accuWeatherIcon.put(43, R.drawable.snowflurry);
        accuWeatherIcon.put(44, R.drawable.moderatesnow);
        accuWeatherIcon.put(45, R.drawable.sun_rise);
        accuWeatherIcon.put(46, R.drawable.sun_set);
    }

    private void initWeatherText() {
        weatherText.put(0, R.string.sunny);
        weatherText.put(1, R.string.cloudy);
        weatherText.put(2, R.string.dreary);
        weatherText.put(3, R.string.showers);
        weatherText.put(4, R.string.thunderstorms);
        weatherText.put(5, R.string.thunderstorm_hail);
        weatherText.put(6, R.string.rain_and_snow);
        weatherText.put(7, R.string.light_rain);
        weatherText.put(8, R.string.rain);
        weatherText.put(9, R.string.heavy_rain);
        weatherText.put(10, R.string.storm);
        weatherText.put(11, R.string.heavy_storm);
        weatherText.put(12, R.string.severe_storm);
        weatherText.put(13, R.string.mostly_cloudy_flurries);
        weatherText.put(14, R.string.light_snow);
        weatherText.put(15, R.string.snow);
        weatherText.put(16, R.string.heavy_snow);
        weatherText.put(17, R.string.snow_storm);
        weatherText.put(18, R.string.fog);
        weatherText.put(19, R.string.ice);
        weatherText.put(20, R.string.dust_storm);
        weatherText.put(21, R.string.light2moderate_rain);
        weatherText.put(22, R.string.moderate2heavy_rain);
        weatherText.put(23, R.string.heavy2sever_rain);
        weatherText.put(24, R.string.storm2heavy_rain);
        weatherText.put(25, R.string.heavy2sever_rain1);
        weatherText.put(26, R.string.light2moderate_snow);
        weatherText.put(27, R.string.moderate2heavy_snow);
        weatherText.put(28, R.string.heavy2sever_snow);
        weatherText.put(29, R.string.dust);
        weatherText.put(30, R.string.sand);
        weatherText.put(31, R.string.sandstorm);
        weatherText.put(32, R.string.dense_foggy);
        weatherText.put(33, R.string.snow);
        weatherText.put(49, R.string.moderate_foggy);
        weatherText.put(53, R.string.haze);
        weatherText.put(54, R.string.moderate_haze);
        weatherText.put(55, R.string.heavy_haze);
        weatherText.put(56, R.string.sever_haze);
        weatherText.put(57, R.string.heavy_foggy);
        weatherText.put(58, R.string.sever_foggy);
        weatherText.put(99, R.string.nothing);
    }

    private void initWeatherIcon() {
        weatherIconList.put(0, R.drawable.gome_ic_calendar_sunny);
        weatherIconList.put(1, R.drawable.gome_ic_calendar_cloudy);
        weatherIconList.put(2, R.drawable.gome_ic_calendar_partly_cloudy);
        weatherIconList.put(3, R.drawable.gome_ic_calendar_shower);
        weatherIconList.put(4, R.drawable.gome_ic_calendar_thunder_shower);
        weatherIconList.put(5, R.drawable.gome_ic_calendar_thunder_shower);
        weatherIconList.put(6, R.drawable.gome_ic_calendar_sleet);
        weatherIconList.put(7, R.drawable.gome_ic_calendar_light_rain);
        weatherIconList.put(8, R.drawable.gome_ic_calendar_rain);
        weatherIconList.put(9, R.drawable.gome_ic_calendar_heavy_rain);
        weatherIconList.put(10, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(11, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(12, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(13, R.drawable.snowflurry);
        weatherIconList.put(14, R.drawable.gome_ic_calendar_snow);
        weatherIconList.put(15, R.drawable.gome_ic_calendar_snow);
        weatherIconList.put(16, R.drawable.gome_ic_calendar_heavy_snow);
        weatherIconList.put(17, R.drawable.gome_ic_calendar_heavy_snow);
        weatherIconList.put(18, R.drawable.gome_ic_calendar_fog);
        weatherIconList.put(19, R.drawable.gome_ic_calendar_sleet);
        weatherIconList.put(20, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(21, R.drawable.gome_ic_calendar_rain);
        weatherIconList.put(22, R.drawable.gome_ic_calendar_light_rain);
        weatherIconList.put(23, R.drawable.gome_ic_calendar_heavy_rain);
        weatherIconList.put(24, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(25, R.drawable.gome_ic_calendar_storm);
        weatherIconList.put(26, R.drawable.gome_ic_calendar_snow);
        weatherIconList.put(27, R.drawable.gome_ic_calendar_snow);
        weatherIconList.put(28, R.drawable.gome_ic_calendar_heavy_snow);
        weatherIconList.put(29, R.drawable.gome_ic_calendar_dusty);
        weatherIconList.put(30, R.drawable.sand);
        weatherIconList.put(31, R.drawable.sandstorm);
        weatherIconList.put(32, R.drawable.densefoggy);
        weatherIconList.put(33, R.drawable.gome_ic_calendar_snow);

        weatherIconList.put(49, R.drawable.others);
        weatherIconList.put(53, R.drawable.gome_ic_calendar_light_haze);
        weatherIconList.put(54, R.drawable.gome_ic_calendar_light_haze);
        weatherIconList.put(55, R.drawable.gome_ic_calendar_light_haze);
        weatherIconList.put(56, R.drawable.gome_ic_calendar_light_haze);
        weatherIconList.put(57, R.drawable.gome_ic_calendar_fog);
        weatherIconList.put(58, R.drawable.gome_ic_calendar_snow);
        weatherIconList.put(99, R.drawable.none);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        Log.e("fushuo", "aaa222222222222");
        thread.start();
    }
}
