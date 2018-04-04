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

package com.hct.calendar.lbs;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.android.calendar.R;
import com.android.calendar.Utils;

public class LocationAlertAdapter extends ResourceCursorAdapter {

    private static LocationAlertActivity alertActivity;
    private static boolean mFirstTime = true;
    private static int mTitleColor;
    private static int mOtherColor; // non-title fields
    private static int mPastEventColor;

    public LocationAlertAdapter(LocationAlertActivity activity, int resource) {
        super(activity, resource, null);
        alertActivity = activity;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        View square = view.findViewById(R.id.color_square);
        int color = Utils.getDisplayColorFromColor(cursor
                .getInt(LocationAlertActivity.INDEX_COLOR));
        Drawable eventicon = context.getResources().getDrawable(
                R.drawable.widget_event_circle_draw);
        eventicon.setTint(color);
        square.setBackground(eventicon);

        // Repeating info
        View repeatContainer = view.findViewById(R.id.repeat_icon);

        repeatContainer.setVisibility(View.GONE);
        int eventType = cursor
                .getInt(LocationAlertActivity.INDEX_EXT_EVENT_TYPE);

        String eventName = cursor.getString(LocationAlertActivity.INDEX_TITLE);
        String location = cursor
                .getString(LocationAlertActivity.INDEX_EVENT_LOCATION);

        updateView(context, view, eventName, location);
    }

    public static void updateView(Context context, View view, String eventName,
            String location) {
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

        titleView.setTextColor(mTitleColor);
        whenView.setTextColor(mOtherColor);
        whereView.setTextColor(mOtherColor);

        // What
        if (eventName == null || eventName.length() == 0) {
            eventName = res.getString(R.string.no_title_label);
        }
        titleView.setText(eventName);

        // When
        String when;
        int flags;

        flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE;

        if (DateFormat.is24HourFormat(context)) {
            flags |= DateUtils.FORMAT_24HOUR;
        }

        // Where
        if (location == null || location.length() == 0) {
        } else {
            whereView.setVisibility(View.GONE);
            whenView.setText(location);
            // whereView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onContentChanged() {
        super.onContentChanged();

        // Prevent empty popup notification.
        alertActivity.closeActivityIfEmpty();
    }
}
