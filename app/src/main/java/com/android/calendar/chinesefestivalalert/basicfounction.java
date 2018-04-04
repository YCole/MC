package com.android.calendar.chinesefestivalalert;

import java.util.Calendar;
import java.util.Random;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;

import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.hct.dom.DomFeedParser;

public class basicfounction {
    private static String TAG = "basicfounction";
    public static final boolean DEBUG = true;
    public static final String FESTIVAL_ACTION = "com.android.HCTSyncCalendar.chinese_festival";
    public static final String DOWNLOADXML = "com.android.calendar.downloadXML";
    // 20151214: Input new data between Dec.15 to Dec.25 every day.
    public static final int DOWNLOAD_BEGIN_DAY = 15;
    public static final int DOWNLOAD_DEANLINE_DAY = 25;
    private static final int DOWNLOAD_BEGIN_HOUR = 8;
    public static final String PREFERENCE_DOWNLOAD_TIME = "downloadXMLtime";

    // private static Uri real_uri;

    // public static boolean checkAlarm(Context context) {
    // Cursor cursor = null;
    // boolean result = false;
    //
    // Uri uri = Uri.parse("content://hct.com.cn.alarmclock/alarm");
    // Uri uri2 = Uri.parse("content://com.android.deskclock/alarm");
    //
    // ContentResolver resolver = context.getContentResolver();
    // //ContentValues values = new ContentValues();
    // String[] ALARM_QUERY_COLUMNS = {"_id", "hour", "minutes", "daysofweek",
    // "enabled"};
    //
    // try {
    // cursor = resolver.query(uri, ALARM_QUERY_COLUMNS,
    // "enabled = '1' and ( daysofweek = '31' or daysofweek = '159')", null,
    // null);
    // }catch (Exception ex) {
    // Log.i(TAG, "Error checkAlarm getAlarmsCursor ");
    // }
    //
    // if (cursor == null) {
    // Log.v(TAG, "cursor uri = null!");
    // real_uri = uri2;
    // try {
    // cursor = resolver.query(uri2, ALARM_QUERY_COLUMNS,
    // "enabled = '1' and ( daysofweek = '31' or daysofweek = '159')", null,
    // null);
    // }catch (Exception ex) {
    // Log.i(TAG, "Error checkAlarm getAlarmsCursor ");
    // }
    // } else {
    // real_uri = uri;
    // }
    //
    // if (cursor != null)
    // {
    // Log.v(TAG, "final cursor != null");
    // if (cursor.getCount() > 0) {
    // result = true;
    // }
    // cursor.close();
    // }
    // Log.v(TAG,"check alarm result = "+result);
    // return result;
    // }

    // public static boolean HasAlarmChanged(Context context, String data){
    // String strformat =
    // "id:[0-9]{1,2},hour:[0-9]{1,2},minute:[0-9]{1,2},repeat:[0-9]{1,3},enable:[0-1];";
    //
    // if (data == null) {
    // return false;
    // }
    //
    // Matcher matcher = Pattern.compile(strformat).matcher(data);
    // while (matcher.find()) {
    // String temp = matcher.group(0);
    // Log.i(TAG, "String temp = " + temp);
    // int id = -1;
    // int orihour = -1, hour = -1;
    // int oriminute = -1, minute = -1;
    // int orirepeat = -1, repeat = -1;
    // int orienable = -1, enable = -1;
    //
    // String strformat1 = "(?<=id:)(.*?)(?=,hour:)";
    // Matcher matcher1 = Pattern.compile(strformat1).matcher(temp);
    // if (matcher1.find()) {
    // Log.i(TAG, "matche id = " + matcher1.group(0));
    // id = Integer.parseInt(matcher1.group(0));
    // }
    // String strformat2 = "(?<=hour:)(.*?)(?=,minute:)";
    // Matcher matcher2 = Pattern.compile(strformat2).matcher(temp);
    // if (matcher2.find()) {
    // Log.i(TAG, "matche orihour = " + matcher2.group(0));
    // orihour = Integer.parseInt(matcher2.group(0));
    // }
    // String strformat3 = "(?<=minute:)(.*?)(?=,repeat:)";
    // Matcher matcher3 = Pattern.compile(strformat3).matcher(temp);
    // if (matcher3.find()) {
    // Log.i(TAG, "matche oriminute = " + matcher3.group(0));
    // oriminute = Integer.parseInt(matcher3.group(0));
    // }
    // String strformat4 = "(?<=repeat:)(.*?)(?=,enable:)";
    // Matcher matcher4 = Pattern.compile(strformat4).matcher(temp);
    // if (matcher4.find()) {
    // Log.i(TAG, "matche orirepeat = " + matcher4.group(0));
    // orirepeat = Integer.parseInt(matcher4.group(0));
    // }
    // String strformat5 = "(?<=enable:)(.*?)(?=;)";
    // Matcher matcher5 = Pattern.compile(strformat5).matcher(temp);
    // if (matcher5.find()) {
    // Log.i(TAG, "matche orienable = " + matcher5.group(0));
    // orienable = Integer.parseInt(matcher5.group(0));
    // }
    //
    // Cursor cursor = null;
    // //Uri uri = Uri.parse("content://hct.com.cn.alarmclock/alarm");
    // ContentResolver resolver = context.getContentResolver();
    // String[] ALARM_QUERY_COLUMNS = {"hour", "minutes", "daysofweek",
    // "enabled"};
    //
    // try {
    // cursor = resolver.query(real_uri, ALARM_QUERY_COLUMNS, "_id="+id, null,
    // null);
    // }catch (Exception ex) {
    // Log.i(TAG, "Error checkAlarm getAlarmsCursor ");
    // }
    //
    // if (cursor != null)
    // {
    // if (cursor.moveToFirst()) {
    // hour = cursor.getInt(0);
    // minute = cursor.getInt(1);
    // repeat = cursor.getInt(2);
    // enable = cursor.getInt(3);
    //
    // Log.i(TAG, "Alarm "+id+" : time = "+hour+":"+minute+", daysofweek = "
    // +repeat+", enabled = "+enable);
    // }
    // cursor.close();
    // }
    //
    // if ((orihour != hour) || (oriminute != minute) || (orirepeat != repeat)
    // || (orienable != enable)) {
    // return true;
    // }
    // }
    // return false;
    // }

    // public static void UpdateAlarm(Context context, int id, int hour, int
    // minute, int repeat, int enable) {
    // //Uri uri = Uri.parse("content://hct.com.cn.alarmclock/alarm");
    // ContentResolver resolver = context.getContentResolver();
    // ContentValues values = new ContentValues();
    //
    // if (repeat == 0) {
    // values.put("enabled", 0);
    // enable = 0;
    // } else {
    // values.put("enabled", 1);
    // enable = 1;
    // }
    // values.put("daysofweek", repeat);
    // resolver.update(ContentUris.withAppendedId(real_uri, id), values, null,
    // null);
    //
    // String adddata =
    // "id:"+id+",hour:"+hour+",minute:"+minute+",repeat:"+repeat+",enable:"+enable+";";
    // SharedPreferences prefs =
    // context.getSharedPreferences("festival_alarm_set", 0);
    // String oridata = prefs.getString("changedalarm","");
    // oridata = oridata+adddata;
    // SharedPreferences.Editor ed = prefs.edit();
    // ed.putString("changedalarm", oridata);
    // ed.commit();
    // }
    //
    // public static void ClearSavedAlarm(Context context) {
    // SharedPreferences prefs =
    // context.getSharedPreferences("festival_alarm_set", 0);
    // SharedPreferences.Editor ed = prefs.edit();
    // ed.putString("changedalarm", "");
    // ed.commit();
    //
    // }
    //
    // public static void cancelAlarmManager(Context context, Intent intent) {
    // AlarmManager am =
    // (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    // PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
    // PendingIntent.FLAG_CANCEL_CURRENT);
    // Log.v(TAG, "cancelAlarmManager");
    // am.cancel(sender);
    // }

    public static String workDayWeekDayFromSolarDate(Resources res,
            final Time tmSolarDate) {
        if (!(ChineseFestivalRawData.Month09.length > 0)) {
            Log.v(TAG, "file isn't exists");
            return "";
        }
        int fileYear = Integer.parseInt(ChineseFestivalRawData.Year[0]);
        int SolarDateYear = tmSolarDate.year;
        int SolarDateMonth = tmSolarDate.month;
        int SolarDate = tmSolarDate.monthDay;
        if (SolarDateYear == fileYear) {
            if (SolarDateMonth == Calendar.JANUARY) {
                if (ChineseFestivalRawData.Month01[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month01[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.FEBRUARY) {
                if (ChineseFestivalRawData.Month02[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month02[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.MARCH) {
                if (ChineseFestivalRawData.Month03[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month03[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.APRIL) {
                if (ChineseFestivalRawData.Month04[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month04[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.MAY) {
                if (ChineseFestivalRawData.Month05[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month05[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.JUNE) {
                if (ChineseFestivalRawData.Month06[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month06[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.JULY) {
                if (ChineseFestivalRawData.Month07[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month07[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.AUGUST) {
                if (ChineseFestivalRawData.Month08[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month08[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.SEPTEMBER) {
                if (ChineseFestivalRawData.Month09[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month09[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.OCTOBER) {
                if (ChineseFestivalRawData.Month10[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month10[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
            if (SolarDateMonth == Calendar.NOVEMBER) {
                if (ChineseFestivalRawData.Month11[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month11[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
        }
        if (SolarDateMonth == Calendar.DECEMBER) {
            if (SolarDateYear == fileYear) {
                if (ChineseFestivalRawData.Month12[SolarDate - 1].equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.Month12[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            } else if (SolarDateYear + 1 == fileYear) {
                if (ChineseFestivalRawData.lastMonth12[SolarDate - 1]
                        .equals("1")) {
                    return res.getString(R.string.show_holiday);
                } else if (ChineseFestivalRawData.lastMonth12[SolarDate - 1]
                        .equals("2")) {
                    return res.getString(R.string.show_workday);
                }
            }
        }
        // if(DEBUG) {
        // Log.d(TAG, "[SolarDateYear, fileYear]=[" + SolarDateYear + ", " +
        // fileYear + "]");
        // }
        return "";
    }

    public static void creatDownloadAlarm(Context context, boolean isTomorrow) {
        if (DEBUG) {
            Log.d(TAG, "set timorrow alarm is " + isTomorrow);
        }
        long curTimeMills = System.currentTimeMillis();
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(curTimeMills);

        int day = ca.get(Calendar.DAY_OF_MONTH);
        if (isTomorrow) {
            ca.set(Calendar.DAY_OF_MONTH, day + 1);
        } else {
            int year = ca.get(Calendar.YEAR);
            int month = ca.get(Calendar.MONTH);
            int hour = ca.get(Calendar.HOUR_OF_DAY);
            if (month == Calendar.DECEMBER) {
                if (day >= DOWNLOAD_DEANLINE_DAY) {
                    ca.set(Calendar.YEAR, year + 1);
                }
                if (day < DOWNLOAD_BEGIN_DAY) {
                    ca.set(Calendar.DAY_OF_MONTH, DOWNLOAD_BEGIN_DAY);
                } else if (hour >= DOWNLOAD_BEGIN_HOUR) {
                    ca.set(Calendar.DAY_OF_MONTH, day + 1);
                }
            } else {
                ca.set(Calendar.DAY_OF_MONTH, DOWNLOAD_BEGIN_DAY);
            }
            ca.set(Calendar.MONTH, Calendar.DECEMBER);
        }
        ca.set(Calendar.HOUR_OF_DAY, DOWNLOAD_BEGIN_HOUR);
        Random random = new Random();
        ca.set(Calendar.MINUTE, random.nextInt(59));
        SharedPreferences prefs = context.getSharedPreferences(
                PREFERENCE_DOWNLOAD_TIME, 0);
        Long downloadXMLtime = ca.getTimeInMillis();
        SharedPreferences.Editor ed = prefs.edit();
        ed.putLong("time", downloadXMLtime);
        ed.commit();
        if (DEBUG) {
            Log.d(TAG, "lxg creatDownloadAlarm, curTime =" + curTimeMills
                    + " ,ca=" + ca.getTimeInMillis() + ", downloadXMLtime="
                    + downloadXMLtime);
        }

        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        int requestCode = 0;
        Intent intent = new Intent(DOWNLOADXML);
        PendingIntent pi = PendingIntent.getBroadcast(context, requestCode,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(pi);
        am.set(AlarmManager.RTC_WAKEUP, downloadXMLtime, pi);
    }

    public static void downloadXML(Context context, Handler handler) {
        if (Utils.isAbroadBranch(context)) {
            return;
        }
        Time t = new Time();
        t.setToNow();
        if (t.year < ChineseFestivalRawData.initialYear) {
            return;
        }
        if (ChineseFestivalRawData.isInitiYear()) {
            creatDownloadAlarm(context, false);
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(
                GeneralPreferences.SHARED_PREFS_NAME, 0);
        boolean isAutoDownload = prefs.getBoolean(
                GeneralPreferences.KEY_AUTO_UPDATE_FESTIVAL, true);
        Log.d(TAG, "downloadXML, AutoDownload :" + isAutoDownload);
        if (!isAutoDownload) {
            creatDownloadAlarm(context, false);
            return;
        }

        if (DomFeedParser.isFileExist() && (localFileIsRight())) {
            creatDownloadAlarm(context, false);
        } else {
            startDownLoad(context, handler);
        }
    }

    private static boolean localFileIsRight() {
        Time t = new Time();
        t.setToNow();
        boolean result = false;
        int curYear = t.year;
        int fileYear = Integer.parseInt(ChineseFestivalRawData.Year[0]);
        //
        if ((curYear == fileYear)
                && (t.month < Calendar.DECEMBER || (t.month == Calendar.DECEMBER && t.monthDay <= DOWNLOAD_BEGIN_DAY))) {
            result = true;
        }
        if (curYear + 1 == fileYear
                && (t.month == Calendar.DECEMBER && t.monthDay > DOWNLOAD_BEGIN_DAY)) {
            result = true;
        }
        if (DEBUG) {
            Log.d(TAG, "[curYear, fileYear]=[" + curYear + ", " + fileYear
                    + "], result = " + result);
        }
        return result;
    }

    private static void startDownLoad(Context context, Handler handler) {
        if (NetworkDetector(context)) {
            if (DEBUG) {
                Log.d(TAG, "NetWork is connected!");
            }
            new DomFeedParser().download(context, handler);
        } else {
            creatDownloadAlarm(context, true);
        }
    }

    private static boolean NetworkDetector(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }
}
