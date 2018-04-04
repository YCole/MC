package com.android.calendar.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentResolver;
import android.content.Context;
import android.util.Log;

import com.android.calendar.R;

/**
 * get the weekdays
 * 
 * Created by liaozhenbin on 2017/6/19
 */
public class DateUtils {
    /**
     * get the day counts
     * 
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            return 31;
        case 4:
        case 6:
        case 9:
        case 11:
            return 30;
        case 2:
            if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                return 29;
            } else {
                return 28;
            }
        default:
            return -1;
        }
    }

    /**
     * return the month first day is wilth weekday
     * 
     * @param year
     * @param month
     * @return Sunday 1 Monday 2 Tuesday 3 Wednesday 4 Thursday 5 Friday 6
     *         Saturday 7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static boolean is24(Context context) {
        ContentResolver cv = context.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        if (strTimeFormat != null && strTimeFormat.equals("24")) {
            return true;
        } else
            return false;
    }

    public static String getNormalTime(Context context, long time) {
        SimpleDateFormat formatTime = new SimpleDateFormat(
                DateUtils.is24(context) ? "HH:mm" : "hh:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        String s = formatTime.format(calendar.getTime());
        if (DateUtils.is24(context)) {
            return s;
        } else {
            String sAgeFormatString = calendar.get(Calendar.AM_PM) == 0 ? context
                    .getResources().getString(R.string.time_am) : context
                    .getResources().getString(R.string.time_pm);
            return String.format(sAgeFormatString, s);
        }
    }

    public static String getNormalDate(Context context, long time, int formatId) {
        SimpleDateFormat formatDate = new SimpleDateFormat(
                context.getString(formatId));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return formatDate.format(calendar.getTime());
    }

    public static int getYear(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.YEAR);

    }

    public static int getMonth(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.MONTH);

    }

    public static int getDay(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.DATE);

    }

    public static int getHour(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.HOUR_OF_DAY);

    }

    public static int getMinute(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);

        return calendar.get(Calendar.MINUTE);

    }

    public static int getDuration(String durationStr) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(durationStr);
        String string = m.replaceAll("").trim();
        int i = Integer.valueOf(string);
        return i;
    }
}
