/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.calendar.alerts;

import java.util.Locale;
import java.util.TimeZone;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.android.calendar.R;
import com.android.calendar.Utils;

public class AlertAdapter extends ResourceCursorAdapter implements
        View.OnClickListener {

    private static AlertActivity alertActivity;
    private static boolean mFirstTime = true;
    private static int mTitleColor;
    private static int mOtherColor; // non-title fields
    private static int mPastEventColor;

    private final int EVENT_TYPE_CALL = 2;

    public AlertAdapter(AlertActivity activity, int resource) {
        super(activity, resource, null);
        alertActivity = activity;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View square = view.findViewById(R.id.color_square);
        int color = Utils.getDisplayColorFromColor(cursor
                .getInt(AlertActivity.INDEX_COLOR));
        Drawable eventicon = context.getResources().getDrawable(
                R.drawable.notification_circle);
        eventicon.setTint(color);
        square.setBackground(eventicon);

        // Repeating info
        View repeatContainer = view.findViewById(R.id.repeat_icon);
        String rrule = cursor.getString(AlertActivity.INDEX_RRULE);
        String rdatte = cursor.getString(AlertActivity.INDEX_RDATE);
        boolean isLunarRepeat = !TextUtils.isEmpty(rdatte);
        if (!TextUtils.isEmpty(rrule) || isLunarRepeat) {
            repeatContainer.setVisibility(View.VISIBLE);
        } else {
            repeatContainer.setVisibility(View.GONE);
        }
        int eventType = cursor.getInt(AlertActivity.INDEX_EXT_EVENT_TYPE);
        // is phone alert
        if (eventType == EVENT_TYPE_CALL) {
            String actionData = cursor
                    .getString(AlertActivity.INDEX_EXT_ACTION_DATA);
            ImageView callView = (ImageView) view.findViewById(R.id.call);
            long alarmId = cursor.getLong(AlertActivity.INDEX_ROW_ID);
            long eventId = cursor.getLong(AlertActivity.INDEX_EVENT_ID);
            long startMillis = cursor.getLong(AlertActivity.INDEX_BEGIN);
            Drawable callimage = (Drawable) context.getResources().getDrawable(
                    R.drawable.call);
            callimage.setTint(R.color.call_back_color);
            callView.setImageDrawable(callimage);
            callView.setVisibility(View.VISIBLE);
            callView.setTag(new ActionDataHolder(alarmId, eventId, startMillis,
                    actionData));
            callView.setOnClickListener(this);
        }

        /*
         * // Reminder boolean hasAlarm =
         * cursor.getInt(AlertActivity.INDEX_HAS_ALARM) != 0; if (hasAlarm) {
         * AgendaAdapter.updateReminder(view, context,
         * cursor.getLong(AlertActivity.INDEX_BEGIN),
         * cursor.getLong(AlertActivity.INDEX_EVENT_ID)); }
         */

        String eventName = cursor.getString(AlertActivity.INDEX_TITLE);
        String location = cursor.getString(AlertActivity.INDEX_EVENT_LOCATION);
        long startMillis = cursor.getLong(AlertActivity.INDEX_BEGIN);
        long endMillis = cursor.getLong(AlertActivity.INDEX_END);
        boolean allDay = cursor.getInt(AlertActivity.INDEX_ALL_DAY) != 0;

        updateView(context, view, eventName, location, startMillis, endMillis,
                allDay,
                (isLunarRepeat && Utils.isEnableLunarRecurrence(mContext)));
    }

    public static void updateView(Context context, View view, String eventName,
            String location, long startMillis, long endMillis, boolean allDay,
            boolean showLunar) {
        Resources res = context.getResources();

        TextView titleView = (TextView) view.findViewById(R.id.event_title);
        TextView whenView = (TextView) view.findViewById(R.id.when);
        TextView whereView = (TextView) view.findViewById(R.id.where);
        if (mFirstTime) {
            mPastEventColor = res.getColor(R.color.alert_past_event);
            mTitleColor = res.getColor(R.color.alert_event_title);
            mOtherColor = res.getColor(R.color.alert_event_other);
            mFirstTime = false;
        }

        if (endMillis < System.currentTimeMillis()) {
            titleView.setTextColor(mPastEventColor);
            whenView.setTextColor(mPastEventColor);
            whereView.setTextColor(mPastEventColor);
        } else {
            titleView.setTextColor(mTitleColor);
            whenView.setTextColor(mOtherColor);
            whereView.setTextColor(mOtherColor);
        }

        // What
        if (eventName == null || eventName.length() == 0) {
            eventName = res.getString(R.string.no_title_label);
        }
        titleView.setText(eventName);

        // When
        String when;
        int flags;
        String tz = Utils.getTimeZone(context, null);
        if (allDay) {
            flags = DateUtils.FORMAT_UTC | DateUtils.FORMAT_SHOW_WEEKDAY
                    | DateUtils.FORMAT_SHOW_DATE;
            tz = Time.TIMEZONE_UTC;
        } else {
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE;
        }
        if (DateFormat.is24HourFormat(context)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }

        Time time = new Time(tz);
        time.set(startMillis);
        boolean isDST = time.isDst != 0;
        StringBuilder sb = new StringBuilder();
        if (showLunar) {
            Time currTime = new Time(tz);
            currTime.setToNow();
            sb.append(Utils.formatLunarDateRange(context, startMillis,
                    endMillis, tz, allDay, flags));
            // sb.append(Utils.getDisplayedDatetime(startMillis,endMillis,currTime.toMillis(true),tz,allDay,true,context));
        } else {
            sb.append(Utils.formatDateRange(context, startMillis, endMillis,
                    flags));
        }

        if (!allDay && tz != Time.getCurrentTimezone()) {
            sb.append(" ").append(
                    TimeZone.getTimeZone(tz).getDisplayName(isDST,
                            TimeZone.SHORT, Locale.getDefault()));
        }

        when = sb.toString();
        whenView.setText(when);

        // Where
        if (location == null || location.length() == 0) {
            whereView.setVisibility(View.GONE);
        } else {
            whereView.setText(location);
            whereView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();

        // Prevent empty popup notification.
        alertActivity.closeActivityIfEmpty();
    }

    @Override
    public void onClick(View v) {
        if (Utils.checkAndRequestPermission(alertActivity,
                Manifest.permission.READ_CALL_LOG,
                Utils.REQUEST_PERMISSIONS_CALLLOG)) {
            startCallActivity(v);
        }
    }

    private static class ActionDataHolder {
        public long alarmId = 0l;
        public long eventId = 0l;
        public long startMillis = 0l;
        public String actionData = null;

        public ActionDataHolder(long aId, long eId, long sMillis, String data) {
            alarmId = aId;
            eventId = eId;
            startMillis = sMillis;
            actionData = data;
        }
    }

    public void startCallActivity(View v) {
        ActionDataHolder holder = (ActionDataHolder) v.getTag();
        Uri uri = Uri.parse(holder.actionData);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        alertActivity.startActivity(intent);
        alertActivity.dismissAlarmAndFinishActivity(holder.alarmId,
                holder.eventId, holder.startMillis);

    }
}
