package com.hct.calendar.event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class EventItem {

    public final String TAG = "EventItem";

    public static final int VIEW_EVENT_NORMAL = 0;

    public static final int VIEW_EVENT_LOCATION = 1;

    public static final int VIEW_EVENT_CALL = 2;

    public static final int VIEW_WEATHER = 3;

    public static final int VIEW_ALMANAC = 4;

    private static final int LAST_FIELD = VIEW_ALMANAC + 1;

    protected Context mContext = null;
    protected LayoutInflater mInflater = null;

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long mLastClickTime = 0l;

    public EventItem(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    public static EventAlarmItem ofEvent(Context context, EventEntity entity,
            boolean isShowWeather, boolean showHuangli, boolean isHasAgenda,
            boolean isHashuangli) {
        return new EventAlarmItem(context, entity, isShowWeather, showHuangli,
                isHasAgenda, isHashuangli);
    }

    public abstract View getView(View convertView, ViewGroup parent);

    public static int getViewTypeCount() {
        return LAST_FIELD;
    }

    public abstract boolean isItemEnabled();

    public abstract int getType();

    public final void performClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
            mLastClickTime = currentTime;
            onClick(v);
        }
    }

    public void onClick(View v) {
    }

}
