package com.android.calendar.widget;

import java.util.Calendar;
import java.util.LinkedList;

import zte.lib.calendar.CalendarUtils;
import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
// import android.os.SystemProperties;
import android.provider.CalendarContract.Calendars;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.android.calendar.AllInOneActivity;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.month.CalendarConvertTools;

//import com.hct.calendar.weather.WeatherController;

public class CalendarWidget extends GiosWidgetBase {

    private static final String TAG = "CalendarWidget";
    private static final boolean DEBUG = true;
    private static final String COMPONENT = PACKAGENAME + "/"
            + CalendarWidget.class.getName();
    private static final String CLICK_PRE_MONTH_ACTION = "com.android.calendar.widget.premonth";
    private static final String CLICK_NEXT_MONTH_ACTION = "com.android.calendar.widget.nextmonth";
    private static final String CLICK_CURRENT_MONTH_ACTION = "com.android.calendar.widget.currentmonth";
    private static int MONTH_COUNT = 42;
    private CalendarConvertTools cct;
    private static final String MY_CALENDAR = "My calendar";
    private Resources res;
    private String workString;
    private String weekString;
    private LinkedList<Integer> mEventTimes = new LinkedList<Integer>() {
    };

    private static final int[] MONTH_ALL_ID = {
            R.id.typeb_calendar_week1_day1_text,
            R.id.typeb_calendar_week1_day2_text,
            R.id.typeb_calendar_week1_day3_text,
            R.id.typeb_calendar_week1_day4_text,
            R.id.typeb_calendar_week1_day5_text,
            R.id.typeb_calendar_week1_day6_text,
            R.id.typeb_calendar_week1_day7_text,
            R.id.typeb_calendar_week2_day1_text,
            R.id.typeb_calendar_week2_day2_text,
            R.id.typeb_calendar_week2_day3_text,
            R.id.typeb_calendar_week2_day4_text,
            R.id.typeb_calendar_week2_day5_text,
            R.id.typeb_calendar_week2_day6_text,
            R.id.typeb_calendar_week2_day7_text,
            R.id.typeb_calendar_week3_day1_text,
            R.id.typeb_calendar_week3_day2_text,
            R.id.typeb_calendar_week3_day3_text,
            R.id.typeb_calendar_week3_day4_text,
            R.id.typeb_calendar_week3_day5_text,
            R.id.typeb_calendar_week3_day6_text,
            R.id.typeb_calendar_week3_day7_text,
            R.id.typeb_calendar_week4_day1_text,
            R.id.typeb_calendar_week4_day2_text,
            R.id.typeb_calendar_week4_day3_text,
            R.id.typeb_calendar_week4_day4_text,
            R.id.typeb_calendar_week4_day5_text,
            R.id.typeb_calendar_week4_day6_text,
            R.id.typeb_calendar_week4_day7_text,
            R.id.typeb_calendar_week5_day1_text,
            R.id.typeb_calendar_week5_day2_text,
            R.id.typeb_calendar_week5_day3_text,
            R.id.typeb_calendar_week5_day4_text,
            R.id.typeb_calendar_week5_day5_text,
            R.id.typeb_calendar_week5_day6_text,
            R.id.typeb_calendar_week5_day7_text,
            R.id.typeb_calendar_week6_day1_text,
            R.id.typeb_calendar_week6_day2_text,
            R.id.typeb_calendar_week6_day3_text,
            R.id.typeb_calendar_week6_day4_text,
            R.id.typeb_calendar_week6_day5_text,
            R.id.typeb_calendar_week6_day6_text,
            R.id.typeb_calendar_week6_day7_text };

    private static final int[] MONTH_ALL_LUNAR_ID = {
            R.id.typeb_calendar_week1_day1_text2,
            R.id.typeb_calendar_week1_day2_text2,
            R.id.typeb_calendar_week1_day3_text2,
            R.id.typeb_calendar_week1_day4_text2,
            R.id.typeb_calendar_week1_day5_text2,
            R.id.typeb_calendar_week1_day6_text2,
            R.id.typeb_calendar_week1_day7_text2,
            R.id.typeb_calendar_week2_day1_text2,
            R.id.typeb_calendar_week2_day2_text2,
            R.id.typeb_calendar_week2_day3_text2,
            R.id.typeb_calendar_week2_day4_text2,
            R.id.typeb_calendar_week2_day5_text2,
            R.id.typeb_calendar_week2_day6_text2,
            R.id.typeb_calendar_week2_day7_text2,
            R.id.typeb_calendar_week3_day1_text2,
            R.id.typeb_calendar_week3_day2_text2,
            R.id.typeb_calendar_week3_day3_text2,
            R.id.typeb_calendar_week3_day4_text2,
            R.id.typeb_calendar_week3_day5_text2,
            R.id.typeb_calendar_week3_day6_text2,
            R.id.typeb_calendar_week3_day7_text2,
            R.id.typeb_calendar_week4_day1_text2,
            R.id.typeb_calendar_week4_day2_text2,
            R.id.typeb_calendar_week4_day3_text2,
            R.id.typeb_calendar_week4_day4_text2,
            R.id.typeb_calendar_week4_day5_text2,
            R.id.typeb_calendar_week4_day6_text2,
            R.id.typeb_calendar_week4_day7_text2,
            R.id.typeb_calendar_week5_day1_text2,
            R.id.typeb_calendar_week5_day2_text2,
            R.id.typeb_calendar_week5_day3_text2,
            R.id.typeb_calendar_week5_day4_text2,
            R.id.typeb_calendar_week5_day5_text2,
            R.id.typeb_calendar_week5_day6_text2,
            R.id.typeb_calendar_week5_day7_text2,
            R.id.typeb_calendar_week6_day1_text2,
            R.id.typeb_calendar_week6_day2_text2,
            R.id.typeb_calendar_week6_day3_text2,
            R.id.typeb_calendar_week6_day4_text2,
            R.id.typeb_calendar_week6_day5_text2,
            R.id.typeb_calendar_week6_day6_text2,
            R.id.typeb_calendar_week6_day7_text2 };

    private static final int[] MONTH_ALL_LAYOUT_ID = {
            R.id.typeb_calendar_week1_day1, R.id.typeb_calendar_week1_day2,
            R.id.typeb_calendar_week1_day3, R.id.typeb_calendar_week1_day4,
            R.id.typeb_calendar_week1_day5, R.id.typeb_calendar_week1_day6,
            R.id.typeb_calendar_week1_day7, R.id.typeb_calendar_week2_day1,
            R.id.typeb_calendar_week2_day2, R.id.typeb_calendar_week2_day3,
            R.id.typeb_calendar_week2_day4, R.id.typeb_calendar_week2_day5,
            R.id.typeb_calendar_week2_day6, R.id.typeb_calendar_week2_day7,
            R.id.typeb_calendar_week3_day1, R.id.typeb_calendar_week3_day2,
            R.id.typeb_calendar_week3_day3, R.id.typeb_calendar_week3_day4,
            R.id.typeb_calendar_week3_day5, R.id.typeb_calendar_week3_day6,
            R.id.typeb_calendar_week3_day7, R.id.typeb_calendar_week4_day1,
            R.id.typeb_calendar_week4_day2, R.id.typeb_calendar_week4_day3,
            R.id.typeb_calendar_week4_day4, R.id.typeb_calendar_week4_day5,
            R.id.typeb_calendar_week4_day6, R.id.typeb_calendar_week4_day7,
            R.id.typeb_calendar_week5_day1, R.id.typeb_calendar_week5_day2,
            R.id.typeb_calendar_week5_day3, R.id.typeb_calendar_week5_day4,
            R.id.typeb_calendar_week5_day5, R.id.typeb_calendar_week5_day6,
            R.id.typeb_calendar_week5_day7, R.id.typeb_calendar_week6_day1,
            R.id.typeb_calendar_week6_day2, R.id.typeb_calendar_week6_day3,
            R.id.typeb_calendar_week6_day4, R.id.typeb_calendar_week6_day5,
            R.id.typeb_calendar_week6_day6, R.id.typeb_calendar_week6_day7 };

    private static final int[] MONTH_EVENT_ID = { R.id.agenda_event_color_11,
            R.id.agenda_event_color_12, R.id.agenda_event_color_13,
            R.id.agenda_event_color_14, R.id.agenda_event_color_15,
            R.id.agenda_event_color_16, R.id.agenda_event_color_17,
            R.id.agenda_event_color_21, R.id.agenda_event_color_22,
            R.id.agenda_event_color_23, R.id.agenda_event_color_24,
            R.id.agenda_event_color_25, R.id.agenda_event_color_26,
            R.id.agenda_event_color_27, R.id.agenda_event_color_31,
            R.id.agenda_event_color_32, R.id.agenda_event_color_33,
            R.id.agenda_event_color_34, R.id.agenda_event_color_35,
            R.id.agenda_event_color_36, R.id.agenda_event_color_37,
            R.id.agenda_event_color_41, R.id.agenda_event_color_42,
            R.id.agenda_event_color_43, R.id.agenda_event_color_44,
            R.id.agenda_event_color_45, R.id.agenda_event_color_46,
            R.id.agenda_event_color_47, R.id.agenda_event_color_51,
            R.id.agenda_event_color_52, R.id.agenda_event_color_53,
            R.id.agenda_event_color_54, R.id.agenda_event_color_55,
            R.id.agenda_event_color_56, R.id.agenda_event_color_57,
            R.id.agenda_event_color_61, R.id.agenda_event_color_62,
            R.id.agenda_event_color_63, R.id.agenda_event_color_64,
            R.id.agenda_event_color_65, R.id.agenda_event_color_66,
            R.id.agenda_event_color_67 };
    private static final int[] MONTH_WEATHER_ID = { R.id.weather_icon_11,
            R.id.weather_icon_12, R.id.weather_icon_13, R.id.weather_icon_14,
            R.id.weather_icon_15, R.id.weather_icon_16, R.id.weather_icon_17,
            R.id.weather_icon_21, R.id.weather_icon_22, R.id.weather_icon_23,
            R.id.weather_icon_24, R.id.weather_icon_25, R.id.weather_icon_26,
            R.id.weather_icon_27, R.id.weather_icon_31, R.id.weather_icon_32,
            R.id.weather_icon_33, R.id.weather_icon_34, R.id.weather_icon_35,
            R.id.weather_icon_36, R.id.weather_icon_37, R.id.weather_icon_41,
            R.id.weather_icon_42, R.id.weather_icon_43, R.id.weather_icon_44,
            R.id.weather_icon_45, R.id.weather_icon_46, R.id.weather_icon_47,
            R.id.weather_icon_51, R.id.weather_icon_52, R.id.weather_icon_53,
            R.id.weather_icon_54, R.id.weather_icon_55, R.id.weather_icon_56,
            R.id.weather_icon_57, R.id.weather_icon_61, R.id.weather_icon_62,
            R.id.weather_icon_63, R.id.weather_icon_64, R.id.weather_icon_65,
            R.id.weather_icon_66, R.id.weather_icon_67 };

    int[] WeatheriIconId = new int[] { R.drawable.sunny_outline,
            R.drawable.cloudy_outline, R.drawable.overcast_outline,
            R.drawable.shower_outline, R.drawable.thunder_shower_outline,
            R.drawable.thunderstorm_with_hail_outline,
            R.drawable.sleet_outline, R.drawable.light_rain_outline,
            R.drawable.moderate_rain_outline, R.drawable.heavy_rain_outline,
            R.drawable.rainstorm_outline, -1, -1,
            R.drawable.snow_shower_outline, R.drawable.light_snow_outline,
            R.drawable.moderate_snow_outline, R.drawable.heavy_snow_outline,
            R.drawable.blizzard_outline, R.drawable.fog_outline,
            R.drawable.freezing_rain_outline, R.drawable.sandstorm_outline, -1,
            -1, -1, -1, -1, -1, -1, -1, R.drawable.dust_outline, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            R.drawable.haze_outline };

    private static final int[] MONTH_TODO_ID = { R.id.agenda_todo_color_11,
            R.id.agenda_todo_color_12, R.id.agenda_todo_color_13,
            R.id.agenda_todo_color_14, R.id.agenda_todo_color_15,
            R.id.agenda_todo_color_16, R.id.agenda_todo_color_17,
            R.id.agenda_todo_color_21, R.id.agenda_todo_color_22,
            R.id.agenda_todo_color_23, R.id.agenda_todo_color_24,
            R.id.agenda_todo_color_25, R.id.agenda_todo_color_26,
            R.id.agenda_todo_color_27, R.id.agenda_todo_color_31,
            R.id.agenda_todo_color_32, R.id.agenda_todo_color_33,
            R.id.agenda_todo_color_34, R.id.agenda_todo_color_35,
            R.id.agenda_todo_color_36, R.id.agenda_todo_color_37,
            R.id.agenda_todo_color_41, R.id.agenda_todo_color_42,
            R.id.agenda_todo_color_43, R.id.agenda_todo_color_44,
            R.id.agenda_todo_color_45, R.id.agenda_todo_color_46,
            R.id.agenda_todo_color_47, R.id.agenda_todo_color_51,
            R.id.agenda_todo_color_52, R.id.agenda_todo_color_53,
            R.id.agenda_todo_color_54, R.id.agenda_todo_color_55,
            R.id.agenda_todo_color_56, R.id.agenda_todo_color_57,
            R.id.agenda_todo_color_61, R.id.agenda_todo_color_62,
            R.id.agenda_todo_color_63, R.id.agenda_todo_color_64,
            R.id.agenda_todo_color_65, R.id.agenda_todo_color_66,
            R.id.agenda_todo_color_67 };
    private static final int[] MONTH_WEEK_ID = { R.id.calendar_week_0,
            R.id.calendar_week_1, R.id.calendar_week_2, R.id.calendar_week_3,
            R.id.calendar_week_4, R.id.calendar_week_5, R.id.calendar_week_6 };

    private static final int[] week_header = { R.string.week_sun,
            R.string.week_mon, R.string.week_tue, R.string.week_wed,
            R.string.week_thu, R.string.week_fri, R.string.week_sat };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        if (appWidgetIds != null)
            Log.d(TAG, "lxg CalendarWidget onUpdate,appWidgetIds.size = "
                    + appWidgetIds.length);
        buildUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (DEBUG)
            Log.d(TAG, "CalendarWidget onReceive, action = " + action);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(context,
                        CalendarWidget.class));
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.calendar_widget_hct);
        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month;
        int weekday = t.weekDay;
        int today = t.monthDay;
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int currentYear = displayMonth.getInt("year", t.year);
        int currentMonth = displayMonth.getInt("month", t.month);
        int currentWeek = displayMonth.getInt("weekday", t.month);

        if (action.equals(CLICK_PRE_MONTH_ACTION)) {
            // WeatherController.getController(context.getApplicationContext()).requestRefreshWeather();
            String[] yearMonthWeekString = getPreYearMonthWeekString(context);
            int currentYear1 = displayMonth.getInt("year", t.year);
            int currentMonth1 = displayMonth.getInt("month", t.month);
            if (currentYear1 == year && currentMonth1 == month) {
                remoteViews.setViewVisibility(R.id.typeb_calendar_top_week,
                        View.VISIBLE);
                remoteViews.setTextViewText(R.id.typeb_calendar_top_week,
                        yearMonthWeekString[1]);
            } else {
                remoteViews.setViewVisibility(R.id.typeb_calendar_top_week,
                        View.GONE);
            }
            remoteViews.setTextViewText(R.id.typeb_calendar_month,
                    yearMonthWeekString[0]);
            remoteViews.setTextViewText(R.id.typeb_calendar_year,
                    yearMonthWeekString[2]);

            // String yearMonthString = getPreYearMonthString(context);
            Log.d(TAG, "lxg CLICK_PRE_MONTH_ACTION yearMonthString = "
                    + yearMonthWeekString[0]);
            // remoteViews.setTextViewText(R.id.typeb_calendar_year_and_month,
            // yearMonthString);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            Intent i = new Intent("android.intent.action.GET_MONTH_EVENT");
            context.sendBroadcast(i);
        } else if (action.equals(CLICK_NEXT_MONTH_ACTION)) {
            // WeatherController.getController(context.getApplicationContext()).requestRefreshWeather();
            String[] yearMonthWeekString = getNextYearMonthWeekString(context);
            int currentYear1 = displayMonth.getInt("year", t.year);
            int currentMonth1 = displayMonth.getInt("month", t.month);
            if (currentYear1 == year && currentMonth1 == month) {
                remoteViews.setViewVisibility(R.id.typeb_calendar_top_week,
                        View.VISIBLE);
                remoteViews.setTextViewText(R.id.typeb_calendar_top_week,
                        yearMonthWeekString[1]);
            } else {
                remoteViews.setViewVisibility(R.id.typeb_calendar_top_week,
                        View.GONE);
            }
            remoteViews.setTextViewText(R.id.typeb_calendar_month,
                    yearMonthWeekString[0]);
            remoteViews.setTextViewText(R.id.typeb_calendar_year,
                    yearMonthWeekString[2]);

            // String yearMonthString = getNextYearMonthString(context);
            // if (DEBUG)
            // Log.d(TAG,"lxg CLICK_NEXT_MONTH_ACTION yearMonthString = " +
            // yearMonthString);
            // remoteViews.setTextViewText(R.id.typeb_calendar_year_and_month,
            // yearMonthString);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            Intent i = new Intent("android.intent.action.GET_MONTH_EVENT");
            // WeatherController.getController(context.getApplicationContext()).requestRefreshWeather();
            context.sendBroadcast(i);
        } else if (action.equals(CLICK_CURRENT_MONTH_ACTION)) {
            // WeatherController.getController(context.getApplicationContext()).requestRefreshWeather();

            if (year != currentYear || month != currentMonth
                    || weekday != currentWeek) {
                String[] yearMonthWeekString = getYearMonthWeekString(context);
                remoteViews.setTextViewText(R.id.typeb_calendar_month,
                        yearMonthWeekString[0]);
                remoteViews.setTextViewText(R.id.typeb_calendar_top_week,
                        yearMonthWeekString[1]);
                remoteViews.setTextViewText(R.id.typeb_calendar_year,
                        yearMonthWeekString[2]);

                // String yearMonthString = getYearMonthString(context);
                // if (DEBUG)
                // Log.d(TAG,"lxg CLICK_NEXT_MONTH_ACTION yearMonthString = " +
                // yearMonthString);
                // remoteViews.setTextViewText(R.id.typeb_calendar_year_and_month,
                // yearMonthString);
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
                Intent i = new Intent("android.intent.action.GET_MONTH_EVENT");
                context.sendBroadcast(i);
            }
        } else {
            super.onReceive(context, intent);
        }
    }

    public void updateWidgetWhenDateChanged(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(context,
                        CalendarWidget.class));
        Time t = new Time();
        t.setToNow();
        int today = t.monthDay;
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int currentDay = displayMonth.getInt("day", -1);
        if (DEBUG)
            Log.d(TAG, "updateWidgetWhenDateChanged currentDay : " + currentDay
                    + " today : " + today);
        if (today != currentDay) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.calendar_widget_hct);
            initCalendarWidget(context, remoteViews);
            appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
            Intent i = new Intent("android.intent.action.GET_MONTH_EVENT");
            context.sendBroadcast(i);
        }
    }

    public void updateWidgetEvent(Context context,
            LinkedList<Integer> eventTimes) {
        if (DEBUG)
            Log.d(TAG, "updateWidgetEvent !! context.getPackageName() ="
                    + context.getPackageName());
        if (mEventTimes != null || mEventTimes.size() != 0) {
            mEventTimes.clear();
        }
        mEventTimes = (LinkedList<Integer>) eventTimes.clone();
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.calendar_widget_hct);
        AppWidgetManager appWidgetManager = AppWidgetManager
                .getInstance(context);
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(context,
                        CalendarWidget.class));
        if (appWidgetIds != null)
            Log.d(TAG, "appWidgetIds.size = " + appWidgetIds.length);
        for (int appWidgetId : appWidgetIds) {
            initCalendarWidgetHeader(context, remoteViews);
            initIntent(context, remoteViews);
            updateWeekHeader(context, remoteViews);
            loadMonthData(context, remoteViews);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void buildUpdate(Context context,
            AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            if (DEBUG)
                Log.d(TAG, "Calendar widget buildUpdate");
            Intent updateIntent = new Intent(context,
                    CalendarWidgetService.class);
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    appWidgetId);
            updateIntent.setData(Uri.parse(updateIntent
                    .toUri(Intent.URI_INTENT_SCHEME)));
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.calendar_widget_hct);
            remoteViews.setRemoteAdapter(appWidgetId, R.id.events_list,
                    updateIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,
                    R.id.events_list);
            final PendingIntent updateEventIntent = getLaunchPendingIntentTemplate(context);
            remoteViews.setPendingIntentTemplate(R.id.events_list,
                    updateEventIntent);

            initCalendarWidget(context, remoteViews);
            initIntent(context, remoteViews);
            Log.d(TAG, "Calendar widget build update Views!!!!");
            updateWeekHeader(context, remoteViews);
            loadMonthData(context, remoteViews);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    private void initCalendarWidget(Context context, RemoteViews remoteViews) {
        String[] yearMonthWeekString = getYearMonthWeekString(context);
        remoteViews.setTextViewText(R.id.typeb_calendar_month,
                yearMonthWeekString[0]);
        remoteViews.setTextViewText(R.id.typeb_calendar_top_week,
                yearMonthWeekString[1]);
        remoteViews.setTextViewText(R.id.typeb_calendar_year,
                yearMonthWeekString[2]);

        // String yearMonthString = getYearMonthString(context);
        // remoteViews.setTextViewText(R.id.typeb_calendar_year_and_month,
        // yearMonthString);
        // String dayString = getDayString(context);
        // remoteViews.setTextViewText(R.id.calendar_today_view, dayString);
        remoteViews.setImageViewResource(R.id.calendar_pre_month,
                R.drawable.gome_icon_arrow_left);
        remoteViews.setImageViewResource(R.id.calendar_next_month,
                R.drawable.gome_icon_arrow_right);
        /*
         * remoteViews.setInt(R.id.calendar_next_month,"setColorFilter",context.
         * getResources().getColor(R.color.cale_week_title_color));
         * remoteViews.setInt
         * (R.id.calendar_pre_month,"setColorFilter",context.getResources
         * ().getColor(R.color.cale_week_title_color));
         */
        // WeatherController.getController(context.getApplicationContext()).requestRefreshWeather();
    }

    private void updateWeekHeader(Context context, RemoteViews remoteViews) {
        int mFirstDayOfWeek = Utils.getFirstDayOfWeek(context);
        int offset = mFirstDayOfWeek - 1;
        res = context.getResources();
        for (int i = 1; i < 8; i++) {
            int position = (offset + i) % 7;
            if (DEBUG) {
                Log.d(TAG, "lxg mFirstDayOfWeek = " + mFirstDayOfWeek
                        + ",offset=" + offset + ",position = " + position);
            }
            remoteViews.setTextViewText(MONTH_WEEK_ID[i - 1],
                    res.getString(week_header[position]));
            if (position == Time.SATURDAY || position == Time.SUNDAY) {
                remoteViews.setTextColor(MONTH_WEEK_ID[i - 1], context
                        .getResources().getColor(R.color.cale_subtitle_color));
            } else {
                remoteViews
                        .setTextColor(
                                MONTH_WEEK_ID[i - 1],
                                context.getResources().getColor(
                                        R.color.cale_week_title_color));
            }
        }
    }

    private void loadMonthData(Context context, RemoteViews remoteViews) {
        Time t = new Time();
        t.setToNow();
        int today = t.monthDay;
        int month = t.month;
        int year = t.year;
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int currentYear = displayMonth.getInt("year", t.year);
        int currentMonth = displayMonth.getInt("month", t.month);
        Calendar ca = CalendarUtils.getCalendarOfSpecifyMonth(currentYear,
                currentMonth);
        int startPosition = ca.get(Calendar.DAY_OF_WEEK) - 1;
        int mFirstDayOfWeek = Utils.getFirstDayOfWeek(context);
        int currentStartPosition = startPosition;
        int Saturday = 7;
        int Sunday = 1;
        if (mFirstDayOfWeek == Time.SUNDAY) {
            currentStartPosition = startPosition;
            Saturday = 0;
            Sunday = 1;
        } else if (mFirstDayOfWeek == Time.SATURDAY) {
            Saturday = 1;
            Sunday = 2;
            currentStartPosition = startPosition + 1;
            if (currentStartPosition == 7) {
                currentStartPosition = 0;
            }
        } else if (mFirstDayOfWeek == Time.MONDAY) {
            Saturday = 6;
            Sunday = 0;
            currentStartPosition = startPosition - 1;
            if (currentStartPosition == -1) {
                currentStartPosition = 6;
            }
        }
        int maximum = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
        ca.roll(Calendar.MONTH, -1);
        int lastMonthMaximum = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (DEBUG) {
            Log.d(TAG, "loadMonthData startPos = " + startPosition
                    + ", currentStartPos = " + currentStartPosition
                    + ",maximum=" + maximum);
            Log.d(TAG, "loadMonthData year =" + year + ",month=" + month
                    + ",currentYear=" + currentYear + ",currentMonth="
                    + currentMonth);
        }
        cct = new CalendarConvertTools(context);
        Time tmSolarDate = new Time();
        res = context.getResources();
        workString = res.getString(R.string.show_workday);
        weekString = res.getString(R.string.show_holiday);
        Boolean isAbroad = Utils.isAbroadBranch(context);

        for (int i = 0; i < currentStartPosition; i++) {
            remoteViews.setTextViewText(
                    MONTH_ALL_ID[i],
                    Integer.toString(lastMonthMaximum - currentStartPosition
                            + 1 + i));
            remoteViews.setInt(MONTH_ALL_LAYOUT_ID[i], "setBackgroundResource",
                    android.R.color.transparent);
            if ((currentMonth - 1) == -1) {
                tmSolarDate.year = currentYear - 1;
                tmSolarDate.month = 11;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, currentYear - 1);
                calendar.set(Calendar.MONTH, 11);
                int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                tmSolarDate.monthDay = day - currentStartPosition + i + 1;
            } else {
                tmSolarDate.year = currentYear;
                tmSolarDate.month = currentMonth - 1;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, currentYear);
                calendar.set(Calendar.MONTH, currentMonth - 1);
                int day = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                tmSolarDate.monthDay = day - currentStartPosition + i + 1;
            }
            remoteViews.setViewVisibility(MONTH_EVENT_ID[i], View.INVISIBLE);
            remoteViews.setTextColor(MONTH_ALL_ID[i], context.getResources()
                    .getColor(R.color.hui_icon_text_color));
            // WeatherHelper weahterHelper =
            // WeatherController.getController(context.getApplicationContext());
            if (!isAbroad) {
                /*
                 * int resId =
                 * weahterHelper.getWeatherDrawIconResId(tmSolarDate);
                 * if(resId!=-1){
                 * remoteViews.setViewVisibility(MONTH_WEATHER_ID[i],
                 * View.VISIBLE);
                 * if(resId==R.drawable.sunny_outline||resId==R.drawable
                 * .overcast_outline){
                 * remoteViews.setImageViewResource(MONTH_WEATHER_ID[i], resId);
                 * remoteViews
                 * .setInt(MONTH_WEATHER_ID[i],"setColorFilter",context
                 * .getResources().getColor(R.color.weather_sun_icon_color));
                 * }else{ remoteViews.setImageViewResource(MONTH_WEATHER_ID[i],
                 * resId);
                 * remoteViews.setInt(MONTH_WEATHER_ID[i],"setColorFilter"
                 * ,context
                 * .getResources().getColor(R.color.weather_nosun_icon_color));
                 * } }else{ remoteViews.setViewVisibility(MONTH_WEATHER_ID[i],
                 * View.GONE); }
                 */
                String lunar = this.getLunar(context, tmSolarDate);
                Log.e("chy", "getLunar==" + lunar);
                if (TextUtils.isEmpty(lunar)) {
                    remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[i],
                            View.GONE);
                } else {
                    remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[i],
                            View.VISIBLE);
                    remoteViews.setTextViewText(MONTH_ALL_LUNAR_ID[i], lunar);
                    remoteViews.setTextColor(
                            MONTH_ALL_LUNAR_ID[i],
                            context.getResources().getColor(
                                    R.color.hui_icon_text_color));
                }

                /*
                 * String workDayWeekDay =
                 * basicfounction.workDayWeekDayFromSolarDate(res,tmSolarDate);
                 * if (!TextUtils.isEmpty(workDayWeekDay)) {
                 * remoteViews.setViewVisibility(MONTH_EVENT_ID[i],
                 * View.VISIBLE); if (workString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[i], "setBackgroundResource"
                 * ,R.drawable.widget_work_background);
                 * remoteViews.setTextViewText(MONTH_EVENT_ID[i],
                 * context.getString(R.string.show_workday)); } else if
                 * (weekString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[i],
                 * "setBackgroundResource", R.drawable.widget_week_background);
                 * remoteViews.setTextViewText(MONTH_EVENT_ID[i],
                 * context.getString(R.string.show_holiday)); } }
                 */
            } else {
                remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[i], View.GONE);
            }
            // remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[i], View.GONE);

            remoteViews.setViewVisibility(MONTH_TODO_ID[i], View.GONE);
            if (mEventTimes == null || mEventTimes.size() == 0) {
                continue;
            }
            if (DEBUG) {
                Log.d(TAG, "lxg mEventTimes =" + mEventTimes.toString());
            }
            for (int p = 0; p < mEventTimes.size(); p++) {
                if (i - currentStartPosition == mEventTimes.get(p)) {
                    remoteViews.setViewVisibility(MONTH_TODO_ID[i],
                            View.VISIBLE);
                    remoteViews.setImageViewResource(MONTH_TODO_ID[i],
                            R.drawable.widget_event_circle_draw_white);
                }
            }
        }

        for (int j = 0; j < maximum; j++) {
            remoteViews.setTextViewText(MONTH_ALL_ID[currentStartPosition + j],
                    Integer.toString(j + 1));
            tmSolarDate.year = currentYear;
            tmSolarDate.month = currentMonth;
            tmSolarDate.monthDay = j + 1;
            remoteViews.setViewVisibility(MONTH_EVENT_ID[currentStartPosition
                    + j], View.INVISIBLE);

            // WeatherHelper weahterHelper =
            // WeatherController.getController(context.getApplicationContext());
            if (!isAbroad) {
                /*
                 * int resId =
                 * weahterHelper.getWeatherDrawIconResId(tmSolarDate);
                 * if(resId!=-1){
                 * remoteViews.setViewVisibility(MONTH_WEATHER_ID[
                 * currentStartPosition+j], View.VISIBLE);
                 * if(resId==R.drawable.sunny_outline
                 * ||resId==R.drawable.overcast_outline){
                 * remoteViews.setImageViewResource
                 * (MONTH_WEATHER_ID[currentStartPosition+j], resId);
                 * remoteViews
                 * .setInt(MONTH_WEATHER_ID[currentStartPosition+j],"setColorFilter"
                 * ,
                 * context.getResources().getColor(R.color.weather_sun_icon_color
                 * )); }else{ remoteViews.setImageViewResource(MONTH_WEATHER_ID[
                 * currentStartPosition+j], resId);
                 * remoteViews.setInt(MONTH_WEATHER_ID
                 * [currentStartPosition+j],"setColorFilter"
                 * ,context.getResources
                 * ().getColor(R.color.weather_nosun_icon_color)); } }else{
                 * remoteViews
                 * .setViewVisibility(MONTH_WEATHER_ID[currentStartPosition+j],
                 * View.GONE); }
                 */
                String lunar = this.getLunar(context, tmSolarDate);
                if (TextUtils.isEmpty(lunar)) {
                    remoteViews.setViewVisibility(
                            MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                            View.GONE);
                } else {
                    remoteViews.setViewVisibility(
                            MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                            View.VISIBLE);
                    remoteViews
                            .setTextViewText(
                                    MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                                    lunar);
                }

                /*
                 * String workDayWeekDay =
                 * basicfounction.workDayWeekDayFromSolarDate(res,tmSolarDate);
                 * if (!TextUtils.isEmpty(workDayWeekDay)) {
                 * remoteViews.setViewVisibility
                 * (MONTH_EVENT_ID[currentStartPosition+j], View.VISIBLE); if
                 * (workString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[currentStartPosition+j],
                 * "setBackgroundResource" ,R.drawable.widget_work_background);
                 * remoteViews
                 * .setTextViewText(MONTH_EVENT_ID[currentStartPosition+j],
                 * context.getString(R.string.show_workday)); } else if
                 * (weekString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[currentStartPosition+j],
                 * "setBackgroundResource" ,R.drawable.widget_week_background);
                 * remoteViews
                 * .setTextViewText(MONTH_EVENT_ID[currentStartPosition+j],
                 * context.getString(R.string.show_holiday)); } }
                 */
            } else {
                remoteViews
                        .setViewVisibility(
                                MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                                View.GONE);
            }
            // remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[currentStartPosition+j],
            // View.GONE);

            if (today == (j + 1) && year == currentYear
                    && month == currentMonth) {
                remoteViews.setTextColor(
                        MONTH_ALL_ID[currentStartPosition + j], context
                                .getResources().getColor(R.color.text_color));
                remoteViews.setTextColor(
                        MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                        context.getResources().getColor(
                                R.color.cale_week_title_color));
                remoteViews.setInt(
                        MONTH_ALL_LAYOUT_ID[currentStartPosition + j],
                        "setBackgroundResource",
                        R.drawable.widget_today_background);
            } else {
                remoteViews.setTextColor(
                        MONTH_ALL_ID[currentStartPosition + j], context
                                .getResources().getColor(R.color.text_color));
                remoteViews.setTextColor(
                        MONTH_ALL_LUNAR_ID[currentStartPosition + j],
                        context.getResources().getColor(
                                R.color.cale_week_title_color));
                remoteViews.setInt(
                        MONTH_ALL_LAYOUT_ID[currentStartPosition + j],
                        "setBackgroundResource", android.R.color.transparent);
            }
            if (((currentStartPosition + j + 1) % 7) == Saturday
                    || ((currentStartPosition + j + 1) % 7) == Sunday) {
                remoteViews.setTextColor(
                        MONTH_ALL_ID[currentStartPosition + j],
                        context.getResources().getColor(
                                R.color.cale_week_title_color));
            }

            remoteViews.setViewVisibility(MONTH_TODO_ID[currentStartPosition
                    + j], View.GONE);
            if (mEventTimes == null || mEventTimes.size() == 0) {
                continue;
            }
            for (int p = 0; p < mEventTimes.size(); p++) {
                if (j == mEventTimes.get(p)) {
                    remoteViews.setViewVisibility(
                            MONTH_TODO_ID[currentStartPosition + j],
                            View.VISIBLE);
                    if (today == (j + 1) && year == currentYear
                            && month == currentMonth) {
                        remoteViews.setImageViewResource(
                                MONTH_TODO_ID[currentStartPosition + j],
                                R.drawable.widget_event_circle_draw);
                    } else {
                        remoteViews.setImageViewResource(
                                MONTH_TODO_ID[currentStartPosition + j],
                                R.drawable.widget_event_circle_draw);
                    }
                }
            }
        }

        int m = 1;
        for (int k = currentStartPosition + maximum; k < MONTH_COUNT; k++) {
            remoteViews.setTextViewText(MONTH_ALL_ID[k], Integer.toString(m));
            remoteViews.setInt(MONTH_ALL_LAYOUT_ID[k], "setBackgroundResource",
                    android.R.color.transparent);
            if (currentMonth + 1 == 12) {
                tmSolarDate.year = currentYear + 1;
                tmSolarDate.month = 0;
                tmSolarDate.monthDay = m;
            } else {
                tmSolarDate.year = currentYear;
                tmSolarDate.month = currentMonth + 1;
                tmSolarDate.monthDay = m;
            }
            m++;

            remoteViews.setViewVisibility(MONTH_EVENT_ID[k], View.INVISIBLE);
            remoteViews.setTextColor(MONTH_ALL_ID[k], context.getResources()
                    .getColor(R.color.hui_icon_text_color));
            // WeatherHelper weahterHelper =
            // WeatherController.getController(context.getApplicationContext());
            if (!isAbroad) {
                /*
                 * int resId =
                 * weahterHelper.getWeatherDrawIconResId(tmSolarDate);
                 * if(resId!=-1){
                 * remoteViews.setViewVisibility(MONTH_WEATHER_ID[k],
                 * View.VISIBLE);
                 * if(resId==R.drawable.sunny_outline||resId==R.drawable
                 * .overcast_outline){
                 * remoteViews.setImageViewResource(MONTH_WEATHER_ID[k], resId);
                 * remoteViews
                 * .setInt(MONTH_WEATHER_ID[k],"setColorFilter",context
                 * .getResources().getColor(R.color.weather_sun_icon_color));
                 * }else{ remoteViews.setImageViewResource(MONTH_WEATHER_ID[k],
                 * resId);
                 * remoteViews.setInt(MONTH_WEATHER_ID[k],"setColorFilter"
                 * ,context
                 * .getResources().getColor(R.color.weather_nosun_icon_color));
                 * } }else{ remoteViews.setViewVisibility(MONTH_WEATHER_ID[k],
                 * View.GONE); }
                 */
                String lunar = this.getLunar(context, tmSolarDate);
                if (TextUtils.isEmpty(lunar)) {
                    remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[k],
                            View.GONE);
                } else {
                    remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[k],
                            View.VISIBLE);
                    remoteViews.setTextViewText(MONTH_ALL_LUNAR_ID[k], lunar);
                    remoteViews.setTextColor(
                            MONTH_ALL_LUNAR_ID[k],
                            context.getResources().getColor(
                                    R.color.hui_icon_text_color));
                }

                /*
                 * String workDayWeekDay =
                 * basicfounction.workDayWeekDayFromSolarDate(res,tmSolarDate);
                 * if (!TextUtils.isEmpty(workDayWeekDay)) {
                 * remoteViews.setViewVisibility(MONTH_EVENT_ID[k],
                 * View.VISIBLE); if (workString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[k], "setBackgroundResource"
                 * ,R.drawable.widget_work_background);
                 * remoteViews.setTextViewText(MONTH_EVENT_ID[k],
                 * context.getString(R.string.show_workday)); } else if
                 * (weekString.equals(workDayWeekDay)) {
                 * remoteViews.setInt(MONTH_EVENT_ID[k], "setBackgroundResource"
                 * ,R.drawable.widget_week_background);
                 * remoteViews.setTextViewText(MONTH_EVENT_ID[k],
                 * context.getString(R.string.show_holiday)); } }
                 */
            } else {
                remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[k], View.GONE);
            }
            // remoteViews.setViewVisibility(MONTH_ALL_LUNAR_ID[k], View.GONE);

            remoteViews.setViewVisibility(MONTH_TODO_ID[k], View.GONE);
            if (mEventTimes == null || mEventTimes.size() == 0) {
                continue;
            }
            for (int p = 0; p < mEventTimes.size(); p++) {
                if (k - currentStartPosition == mEventTimes.get(p)) {
                    remoteViews.setViewVisibility(MONTH_TODO_ID[k],
                            View.VISIBLE);
                    remoteViews.setImageViewResource(MONTH_TODO_ID[k],
                            R.drawable.widget_event_circle_draw_white);
                }
            }
        }

    }

    private String getLunar(Context context, Time tmSolarDate) {
        Time lunarDate = cct.getLunarDateFromSolar(tmSolarDate);
        String mstrFes = cct.getChineseFestivalFromSolar(tmSolarDate);
        String mStrTermString = cct.getTermStringFromSolarDate(tmSolarDate);
        String mChnHol = cct.getChnHolFromSolor(tmSolarDate);
        String strLunarDate = "";
        if (lunarDate.monthDay == 1) {
            strLunarDate = cct.getLunarMonthString(lunarDate);
        } else {
            strLunarDate = cct.getLunarDayString(lunarDate);
        }
        if (!getLunarCp(context) && !("".equals(mStrTermString)))
            return mStrTermString;
        if (!("".equals(mstrFes)))
            return mstrFes;
        if (!("".equals(mChnHol))) {
            String chnFesQingming = res.getString(R.string.chnFes_qingming);
            String ChnHolFuhuo = res.getString(R.string.ChnHol_fuhuo);
            if (mChnHol.equals(ChnHolFuhuo)
                    && mStrTermString.equals(chnFesQingming)) {
                mChnHol = mStrTermString;
            }
            return mChnHol;
        }
        if (!("".equals(mStrTermString)))
            return mStrTermString;
        return strLunarDate;
    }

    private boolean getLunarCp(Context context) {
        boolean default_value;
        if (ExtendConfigHelper.ExCfgChineseFestivalFirstEnable) {
            default_value = ExtendConfigHelper.exCfgChineseFestivalFirst;
        } else {
            default_value = context.getResources().getBoolean(
                    R.bool.config_chinese_festival_first);
        }
        return default_value;
    }

    static ComponentName getComponentName(Context context) {
        return new ComponentName(context, CalendarWidget.class);
    }

    private void initIntent(Context context, RemoteViews remoteViews) {
        // start calendar
        Intent launchIntent = new Intent();
        launchIntent.setComponent(new ComponentName("com.android.calendar",
                "com.android.calendar.LaunchActivity"));
        launchIntent.setAction(Intent.ACTION_MAIN);
        launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 20160315 add for check year and month from SharedPreferences when
        // click calendar widget by zhxj
        launchIntent.putExtra("intent_origin", "widget");
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.gios_widget_bg, pendingIntent);

        // new event intent
        addMycalendar(context);
        // long editEventTime = getEditEventTime();
        ComponentName cn = new ComponentName("com.android.calendar",
                "com.android.calendar.EditEventActivity");
        Intent editEventIntent = new Intent();
        editEventIntent.setComponent(cn);
        editEventIntent.putExtra("beginTime", -1);
        // editEventIntent.putExtra("DETAIL_VIEW", true);
        // editEventIntent.setAction(Intent.ACTION_MAIN);
        // editEventIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        editEventIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingEditEventIntent = PendingIntent.getActivity(
                context, 0, editEventIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // remoteViews.setOnClickPendingIntent(R.id.calendar_new_event_view,
        // pendingEditEventIntent);

        // click pre month
        Intent intentPreMonthClick = new Intent(CLICK_PRE_MONTH_ACTION);
        PendingIntent pendingPreMonthIntent = PendingIntent.getBroadcast(
                context, 0, intentPreMonthClick, 0);
        remoteViews.setOnClickPendingIntent(
                R.id.calendar_pre_month_linearlayout, pendingPreMonthIntent);

        // click next month
        Intent intentNextMonthClick = new Intent(CLICK_NEXT_MONTH_ACTION);
        PendingIntent pendingNextMonthIntent = PendingIntent.getBroadcast(
                context, 0, intentNextMonthClick, 0);
        remoteViews.setOnClickPendingIntent(
                R.id.calendar_next_month_linearlayout, pendingNextMonthIntent);

        // click current month
        Intent intentCurrentMonthClick = new Intent(CLICK_CURRENT_MONTH_ACTION);
        PendingIntent pendingCurrentMonthIntent = PendingIntent.getBroadcast(
                context, 0, intentCurrentMonthClick, 0);
        // remoteViews.setOnClickPendingIntent(R.id.calendar_today_view,
        // pendingCurrentMonthIntent);
    }

    // HCT_MODIFY lixiange MF3.2 add My Calendar
    public void addMycalendar(Context context) {
        if (!Utils.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)) {
            return;
        }
        Cursor cursor = context.getContentResolver().query(
                Calendars.CONTENT_URI, new String[] { Calendars._ID },
                Calendars.ACCOUNT_NAME + "='" + MY_CALENDAR + "'", null /*
                                                                         * selection
                                                                         * args
                                                                         */,
                null /* sort order */);

        if (cursor == null) {
            return;
        } else if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, MY_CALENDAR);
        values.put(Calendars.ACCOUNT_TYPE, MY_CALENDAR);
        values.put(Calendars.OWNER_ACCOUNT, MY_CALENDAR);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, MY_CALENDAR);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        int color = context.getResources().getColor(R.color.my_calendar_color);
        values.put(Calendars.CALENDAR_COLOR, color); // -14069085 /* blue */
        values.put(Calendars.CALENDAR_TIME_ZONE, Time.getCurrentTimezone());
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        context.getContentResolver().insert(Calendars.CONTENT_URI, values);
        cursor.close();
    }

    // HCT_MODIFY lixiange MF3.2 add My Calendar

    static PendingIntent getLaunchPendingIntentTemplate(Context context) {
        Intent launchIntent = new Intent();
        launchIntent.setAction(Intent.ACTION_VIEW);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        launchIntent.setClass(context, AllInOneActivity.class);
        return PendingIntent.getActivity(context, 0 /* no requestCode */,
                launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private long getEditEventTime() {
        Time t = new Time();
        t.setToNow();
        if (t.minute >= 30) {
            t.hour++;
            t.minute = 0;
        } else if (t.minute >= 0 && t.minute < 30) {
            t.minute = 30;
        }
        return t.toMillis(true);
    }

    private String getDayString(Context context) {
        Time time = new Time();
        time.setToNow();
        return Integer.toString(time.monthDay);
    }

    private void initCalendarWidgetHeader(Context context,
            RemoteViews remoteViews) {
        String[] yearMonthWeekString = getCurrentYearMonthWeekString(context);
        remoteViews.setTextViewText(R.id.typeb_calendar_month,
                yearMonthWeekString[0]);
        remoteViews.setTextViewText(R.id.typeb_calendar_top_week,
                yearMonthWeekString[1]);
        remoteViews.setTextViewText(R.id.typeb_calendar_year,
                yearMonthWeekString[2]);
        String dayString = getDayString(context);
        // remoteViews.setTextViewText(R.id.calendar_today_view, dayString);
    }

    private String[] getYearMonthWeekString(Context context) {
        Time time = new Time();
        time.setToNow();
        final String yearString = context.getResources().getString(
                R.string.year);
        final String[] defaultMonthArray = context.getResources()
                .getStringArray(R.array.calendar_month_string_array);
        final String[] defaultWeekArray = context.getResources()
                .getStringArray(R.array.week);
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        SharedPreferences.Editor editorDisplayMonth = displayMonth.edit();
        editorDisplayMonth.putInt("year", time.year);
        editorDisplayMonth.putInt("month", time.month);
        editorDisplayMonth.putInt("day", time.monthDay);
        editorDisplayMonth.putInt("weekday", time.weekDay);
        editorDisplayMonth.commit();
        String[] strArray = { defaultMonthArray[time.month],
                defaultWeekArray[time.weekDay], time.year + yearString };
        return strArray;
    }

    private String[] getCurrentYearMonthWeekString(Context context) {
        Time time = new Time();
        time.setToNow();
        final String yearString = context.getResources().getString(
                R.string.year);
        final String[] defaultMonthArray = context.getResources()
                .getStringArray(R.array.calendar_month_string_array);
        final String[] defaultWeekArray = context.getResources()
                .getStringArray(R.array.week);
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int year = displayMonth.getInt("year", time.year);
        int month = displayMonth.getInt("month", time.month);
        int weekday = displayMonth.getInt("weekday", time.weekDay);
        String[] strArray = { defaultMonthArray[month],
                defaultWeekArray[weekday], year + yearString };
        return strArray;
    }

    // private String getCurrentYearString(Context context){
    // Time time =new Time();
    // time.setToNow();
    // final String yearString = context.getResources().getString(
    // R.string.year);
    // SharedPreferences displayMonth =
    // context.getSharedPreferences("display_month", 0);
    // int year = displayMonth.getInt("year", time.year);
    // return year + yearString;
    // }
    //
    // private String getCurrentMonthString(Context context){
    // Time time =new Time();
    // time.setToNow();
    // final String[] defaultMonthArray = context.getResources()
    // .getStringArray(R.array.calendar_month_string_array);
    // SharedPreferences displayMonth =
    // context.getSharedPreferences("display_month", 0);
    // int month = displayMonth.getInt("month", time.month);
    // return defaultMonthArray[month];
    // }
    //
    // private String getCurrentWeekString(Context context){
    // Time time =new Time();
    // time.setToNow();
    // final String[] defaultWeekArray = context.getResources()
    // .getStringArray(R.array.week);
    // SharedPreferences displayMonth =
    // context.getSharedPreferences("display_month", 0);
    // int week = displayMonth.getInt("weekday", time.month);
    // return defaultWeekArray[week];
    // }

    private String[] getPreYearMonthWeekString(Context context) {
        Time time = new Time();
        time.setToNow();
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int year = displayMonth.getInt("year", time.year);
        int month = displayMonth.getInt("month", time.month);
        int weekday = displayMonth.getInt("weekday", time.weekDay);
        final String yearString = context.getResources().getString(
                R.string.year);
        final String[] defaultMonthArray = context.getResources()
                .getStringArray(R.array.calendar_month_string_array);
        final String[] defaultWeekArray = context.getResources()
                .getStringArray(R.array.week);
        if (month == 0) {
            month = 11;
            year = year - 1;
        } else {
            month--;
        }
        SharedPreferences.Editor editorDisplayMonth = displayMonth.edit();
        editorDisplayMonth.putInt("year", year);
        editorDisplayMonth.putInt("month", month);
        editorDisplayMonth.putInt("day", time.monthDay);
        editorDisplayMonth.putInt("weekday", time.weekDay);
        editorDisplayMonth.commit();
        String[] strArray = { defaultMonthArray[month],
                defaultWeekArray[weekday], year + yearString };
        return strArray;
    }

    private String[] getNextYearMonthWeekString(Context context) {
        Time time = new Time();
        time.setToNow();//
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int year = displayMonth.getInt("year", time.year);
        int month = displayMonth.getInt("month", time.month);
        int weekday = displayMonth.getInt("weekday", time.weekDay);
        final String yearString = context.getResources().getString(
                R.string.year);
        final String[] defaultMonthArray = context.getResources()
                .getStringArray(R.array.calendar_month_string_array);
        final String[] defaultWeekArray = context.getResources()
                .getStringArray(R.array.week);
        if (year == CalendarConvertTools.YEAR_LUNAR_MAX && month >= 10) {
            month = 10;
        } else {
            if (month == 11) {
                month = 0;
                year = year + 1;
            } else {
                month++;
            }
        }
        SharedPreferences.Editor editorDisplayMonth = displayMonth.edit();
        editorDisplayMonth.putInt("year", year);
        editorDisplayMonth.putInt("month", month);
        editorDisplayMonth.putInt("day", time.monthDay);
        editorDisplayMonth.putInt("weekday", time.weekDay);
        editorDisplayMonth.commit();
        String[] strArray = { defaultMonthArray[month],
                defaultWeekArray[weekday], year + yearString };
        return strArray;
    }

    private boolean isChinese() {
        /*
         * final String chinese = Locale.CHINESE.getLanguage(); try {
         * IActivityManager am = ActivityManagerNative.getDefault();
         * Configuration config = am.getConfiguration(); String language =
         * config.locale.getLanguage(); if (chinese.equals(language)) return
         * true; return false; } catch (RemoteException ex) {
         * ex.printStackTrace(); }
         */
        return false;
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public String toString() {
        return "is_hctwidget";
    }

    @Override
    protected String getComponentName() {
        // TODO Auto-generated method stub
        return COMPONENT;
    }

    @Override
    protected RemoteViews getRemoteView(Context context, int appWidgetId) {
        // TODO Auto-generated method stub
        return new RemoteViews(context.getPackageName(),
                R.layout.calendar_widget_hct);
    }

    @Override
    protected void updateWidgetForeColor(Context context, RemoteViews views,
            int color, int appWidgetId) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void initWidget(Context context, RemoteViews views,
            int appWidgetId) {
        // TODO Auto-generated method stub
    }

}
