package com.android.calendar.widget;

import java.util.List;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.calendar.R;

public abstract class GiosWidgetBase extends AppWidgetProvider {
    public static final String PACKAGENAME = "com.android.launcher3";
    protected static final String TAG = "GiosWidgetBase";
    private static final String BG_COLOR = "bgColor";
    private static final String FORE_COLOR = "foreColor";

    private final String WidgetClassName = getClass().getSimpleName();

    protected abstract String getComponentName();

    protected abstract RemoteViews getRemoteView(Context context,
            int appWidgetId);

    protected abstract void updateWidgetForeColor(Context context,
            RemoteViews views, int color, int appWidgetId);

    protected abstract void initWidget(Context context, RemoteViews views,
            int appWidgetId);

    protected int getBgColor(Context context, int widgetId) {
        SharedPreferences sharedPreference = context.getSharedPreferences(
                WidgetClassName, 0);
        int bgColor = sharedPreference.getInt(BG_COLOR + "#" + widgetId,
                Color.TRANSPARENT);
        Log.i(TAG, "getBgColor: bgColor = " + bgColor + " for widgetId:"
                + widgetId);
        return bgColor;
    }

    protected void setBgColor(Context context, int widgetId, int bgColor) {
        Editor edit = context.getSharedPreferences(WidgetClassName, 0).edit();
        edit.putInt(BG_COLOR + "#" + widgetId, bgColor);
        edit.commit();
        Log.i(TAG, "setBgColor: bgColor = " + bgColor + " for widgetId:"
                + widgetId);
    }

    protected int getForeColor(Context context) {
        SharedPreferences sharedPreference = context.getSharedPreferences(
                WidgetClassName, 0);
        int textColor = sharedPreference.getInt(FORE_COLOR, Color.WHITE);
        Log.i(TAG, "getForeColor: foreColor = " + textColor);
        return textColor;
    }

    protected void setForeColor(Context context, int textColor) {
        Editor edit = context.getSharedPreferences(WidgetClassName, 0).edit();
        edit.putInt(FORE_COLOR, textColor);
        edit.commit();
        Log.i(TAG, "setForeColor: foreColor = " + textColor);
    }

    protected boolean appExist(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities == null || activities.isEmpty()) {
            return false;
        }
        return true;
    }

    protected Intent getIntent(String packageName, String clsName) {
        ComponentName component = new ComponentName(packageName, clsName);
        Intent newIntent = new Intent();
        newIntent.setComponent(component);
        newIntent.setAction(Intent.ACTION_MAIN);
        newIntent.setAction(Intent.CATEGORY_LAUNCHER);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return newIntent;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        Log.d(TAG, "onUpdate enter.");
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews views = getRemoteView(context, appWidgetIds[i]);
            updateWidgetColor(context, views, appWidgetIds[i]);
            initWidget(context, views, appWidgetIds[i]);
            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }
    }

    protected void updateWidgetColor(Context context, RemoteViews views,
            int appWidgetId) {
        int bgColor = getBgColor(context, appWidgetId);
        Log.d(TAG, "updateWidgetColor: bgColor = " + bgColor);

        if (bgColor == Color.WHITE) {
            views.setInt(R.id.gios_widget_bg, "setBackgroundColor", Color.WHITE);
            updateWidgetForeColor(context, views, Color.BLACK, appWidgetId);
        } else {
            views.setInt(R.id.gios_widget_bg, "setBackgroundColor",
                    Color.TRANSPARENT);
            int textColor = getForeColor(context);
            updateWidgetForeColor(context, views, textColor, appWidgetId);
        }
    }

}
