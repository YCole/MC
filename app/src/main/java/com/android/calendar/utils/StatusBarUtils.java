package com.android.calendar.utils;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class StatusBarUtils {
    public static void setStatusBarLightMode(Activity activity, int color) {
        activity.getWindow().setBackgroundDrawableResource(
                android.R.color.transparent);
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().setStatusBarColor(color);

        ViewGroup mContentView = (ViewGroup) activity.getWindow().findViewById(
                Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, true);
            ViewCompat.requestApplyInsets(mChildView);
        }

    }
}
