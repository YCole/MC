/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.calendar.month;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TimeZone;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calendar.AsyncQueryService;
import com.android.calendar.R;
import com.android.calendar.Utils;

public class HolidayAndTermAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = "HolidayAdapter";
    private static final boolean DEBUG = false;
    private static final String MY_CALENDAR = "My calendar";

    private static int mInsertToken;
    private static int mDeleteToken;

    private AsyncQueryService mService;
    private CalendarConvertTools mConvertTool;
    private Context mContext;
    private LayoutInflater mInflater;
    Resources mRes;
    private int mLayout;
    private int mOrientation;
    private SpecialDayRow[] mData;
    private Cursor mCursor;
    private int mRowCount = 0;

    private int todayJulianDay;
    private HashSet<Integer> mRemindarOnJulianDays = new HashSet<Integer>();
    private HashMap<Integer, Long> mRemindarOnEventIds = new HashMap<Integer, Long>();

    private int mIdColumn;
    private int mTtileColumn;
    private int mDtstartColumn;
    private int mDtendColumn;
    private boolean hasClicked;

    private class SpecialDayRow {
        int julianDay;
        String specialDayName;
        String solarDate;
        String lunarDate;
        String weekDay;
        String daysInterval;
        String description;
    }

    public HolidayAndTermAdapter(Context context, int layout, Cursor c) {
        super();
        mLayout = layout;
        mOrientation = context.getResources().getConfiguration().orientation;
        Log.i(TAG, "HolidayAndTermAdapter->initData()");
        initData(c);
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRes = context.getResources();

        mContext = context;
        mConvertTool = new CalendarConvertTools(context);
        mService = new AsyncQueryService(context) {
            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                Log.d(TAG, "onInsertComplete");
                // Second action of inserting reminder is completed
                if (cookie == null)
                    return;
                // First action of inserting reminder is completed and add a
                // reminder again
                if (cookie instanceof Long) {
                    Log.i(TAG, "addReminder");
                    addReminder((Long) cookie, false);
                    return;
                }
                // Action of inserting event is completed and add the first
                // reminder
                long eventId = ContentUris.parseId(uri);
                Log.i(TAG, "cookie = " + cookie);
                mRemindarOnJulianDays.add((Integer) cookie);
                mRemindarOnEventIds.put((Integer) cookie, eventId);
                addReminder(eventId, true);
                notifyDataSetChanged();
                /*
                 * Intent intent = new Intent(Intent.ACTION_EDIT, uri);
                 * intent.setClass(mContext, EditEventActivity.class);
                 * intent.putExtra("editMode", true);
                 * mContext.startActivity(intent);
                 */
            }

            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                Log.d(TAG, "onDeleteComplete");
                mRemindarOnJulianDays.remove((Integer) cookie);
                mRemindarOnEventIds.remove((Integer) cookie);
                notifyDataSetChanged();
            }
        };

        Time today = new Time();
        today.setToNow();
        todayJulianDay = today.getJulianDay(today.toMillis(true), today.gmtoff);
    }

    private void initData(Cursor c) {
        if (mCursor != null && c != mCursor) {
            mCursor.close();
        }
        if (c == null) {
            Log.d(TAG, "initData: cursor = null");
            mCursor = c;
            mRemindarOnJulianDays.clear();
            mRemindarOnEventIds.clear();
            return;
        }

        Log.d(TAG, "initData: reminders' count = " + c.getCount());

        mCursor = c;
        mIdColumn = c.getColumnIndexOrThrow(Events._ID);
        mTtileColumn = c.getColumnIndexOrThrow(Events.TITLE);
        mDtstartColumn = c.getColumnIndexOrThrow(Events.DTSTART);
        mDtendColumn = c.getColumnIndexOrThrow(Events.DTEND);

        mRemindarOnJulianDays.clear();
        mRemindarOnEventIds.clear();

        Time reminderOnTime = new Time();
        c.moveToPosition(-1);
        while (c.moveToNext()) {
            long eventId = c.getLong(mIdColumn);
            long startMillis = c.getLong(mDtstartColumn);
            reminderOnTime.set(startMillis);
            int reminderOnJulianDay = reminderOnTime.getJulianDay(startMillis,
                    reminderOnTime.gmtoff);
            Log.i(TAG, "initData: reminderOnJulianDay = " + reminderOnJulianDay);
            mRemindarOnJulianDays.add(reminderOnJulianDay);
            mRemindarOnEventIds.put(reminderOnJulianDay, eventId);
        }
    }

    public void changeCursor(Cursor c) {
        Log.i(TAG, "changeCursor->initData()");
        initData(c);
        notifyDataSetChanged();
    }

    public void setSpecialDayRows(ArrayList<Integer> specialDays) {
        if (specialDays == null || specialDays.size() == 0) {
            Log.d(TAG, "setSpecialDayRows: mRowCount = 0, mData = null");
            mRowCount = 0;
            mData = null;
            return;
        }

        mRowCount = specialDays.size();
        mData = new SpecialDayRow[mRowCount];
        if (DEBUG) {
            Log.d(TAG, "setSpecialDayRows: mRowCount = " + mRowCount);
        }

        Time solarDate = new Time();
        int p = 0;
        Iterator<Integer> iterator = specialDays.iterator();
        while (iterator.hasNext()) {
            int theJulianDay = iterator.next().intValue();
            solarDate.setJulianDay(theJulianDay);

            String specialDayName = "";
            StringBuffer specialDayDescription = new StringBuffer();
            String chnFesQingming = mRes.getString(R.string.chnFes_qingming);
            String termQingming = mRes.getString(R.string.term_qingming);
            if (mRowCount == 24) {
                specialDayName = mConvertTool
                        .getTermStringFromSolarDate(solarDate);
                if (specialDayName.equals(chnFesQingming)) {
                    specialDayName = termQingming;
                }
            } else {
                specialDayName = mConvertTool.getChineseFestivalFromSolar(
                        solarDate, specialDayDescription);

                if (specialDayName.equals("")) {
                    specialDayName = mConvertTool.getChnHolFromSolor(solarDate,
                            specialDayDescription);
                }
            }
            if (DEBUG) {
                Log.d(TAG, "setSpecialDayRows: specialDayDescription = "
                        + specialDayDescription.toString());
            }

            int interval = theJulianDay - todayJulianDay;
            String daysInterval = "";
            if (interval == -1) {
                daysInterval = mRes.getString(R.string.yesterday);
            } else if (interval == 0) {
                daysInterval = mRes.getString(R.string.today);
            } else if (interval == 1) {
                daysInterval = mRes.getString(R.string.tomorrow);
            } else if (interval > 1) {
                daysInterval = mRes.getString(R.string.days_interval, interval);
            }

            mData[p] = new SpecialDayRow();
            mData[p].julianDay = theJulianDay;
            mData[p].specialDayName = specialDayName;
            mData[p].description = specialDayDescription.toString();
            mData[p].solarDate = (solarDate.month + 1) + "/"
                    + solarDate.monthDay;
            mData[p].lunarDate = mConvertTool
                    .getLunarStringWithoutYear(solarDate);
            mData[p].weekDay = DateUtils.formatDateRange(mContext,
                    solarDate.toMillis(true), solarDate.toMillis(true),
                    DateUtils.FORMAT_SHOW_WEEKDAY);
            mData[p].daysInterval = daysInterval;
            p++;
        }

        notifyDataSetChanged();
    }

    // @Override
    // public View getView(final int position, View convertView, ViewGroup
    // parent) {
    // if (position >= mRowCount) {
    // return null;
    // }
    // int julianDay = mData[position].julianDay;
    // String specialDayName = mData[position].specialDayName;
    // String solarDate = mData[position].solarDate;
    // String lunarDate = mData[position].lunarDate;
    // String weekDay = mData[position].weekDay;
    // String daysInterval = mData[position].daysInterval;
    // View view;
    // if (convertView == null) {
    // view = mInflater.inflate(mLayout, parent, false);
    // } else {
    // view = convertView;
    // }
    // TextView solarDateView = (TextView) view.findViewById(R.id.solar_date);
    // solarDateView.setText(mContext.getString(R.string.data_tip_all,
    // specialDayName, solarDate));
    //
    // TextView lunarDateView = (TextView) view.findViewById(R.id.lunar_date);
    // lunarDateView.setText(mContext.getString(R.string.lunar_weekday_divider,lunarDate,weekDay));
    //
    //
    // TextView daysIntervalView = (TextView)
    // view.findViewById(R.id.days_interval);
    // daysIntervalView.setText(daysInterval);
    //
    // ImageView reminderCheckButton = (ImageView)
    // view.findViewById(R.id.reminder_check);
    // if (mRemindarOnJulianDays.contains(julianDay)) {
    // Drawable a = mRes.getDrawable(R.drawable.notifications);
    // a.setTint(mRes.getColor(R.color.cale_list_icon_color));
    // reminderCheckButton.setImageDrawable(a);
    //
    // } else {
    // Drawable b = mRes.getDrawable(R.drawable.notifications_none);
    // b.setTint(mRes.getColor(R.color.cale_list_icon_color));
    // reminderCheckButton.setImageDrawable(b);
    // }
    //
    // if (julianDay - todayJulianDay == 0) { // today
    // Log.d(TAG, "lxg getview julianDay - todayJulianDay == 0");
    // //
    // view.setBackgroundColor(mRes.getColor(R.color.holiday_term_list_today_color));
    // view.setBackgroundResource(R.drawable.agenda_item_bg_primary);
    // solarDateView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_holiday_color));
    // lunarDateView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_color));
    // daysIntervalView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_color));
    // } else if (julianDay - todayJulianDay > 0) { // future
    // view.setBackgroundResource(R.drawable.agenda_item_bg_primary);
    // solarDateView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_holiday_color));
    // lunarDateView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_color));
    // daysIntervalView.setTextColor(mRes.getColor(R.color.holiday_term_list_text_color));
    // } else if (julianDay - todayJulianDay < 0) { // past
    // solarDateView.setTextColor(mRes.getColor(R.color.color06_transparency06));
    // lunarDateView.setTextColor(mRes.getColor(R.color.color06_transparency06));
    // daysIntervalView.setTextColor(mRes.getColor(R.color.color06_transparency06));
    // view.setBackgroundResource(R.drawable.agenda_item_bg_primary);
    // Drawable d = mRes.getDrawable(R.drawable.notifications_none);
    // d.setTint(mRes.getColor(R.color.color06_transparency06));
    // reminderCheckButton.setImageDrawable(d);
    // }
    //
    // reminderCheckButton.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // if (hasClicked == false) {
    // reminderCheck(position);
    // hasClicked = true;
    // Handler handler = new Handler();
    // handler.postDelayed(new Runnable() {
    // @Override
    // public void run() {
    // hasClicked = false;
    // }
    // }, 500);
    // }
    // }
    // });
    //
    // return view;
    // }

    /* modify by Yusong.Liang on 2017.9.22:start */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_festival_or_term,
                    parent, false);
            holder = new ViewHolder();
            holder.tvTitle = (TextView) convertView
                    .findViewById(R.id.tv_item_title);
            holder.tvSubTitle = (TextView) convertView
                    .findViewById(R.id.tv_item_sub_title);
            holder.tvEnter = (TextView) convertView
                    .findViewById(R.id.tv_item_enter);
            holder.ivIcon = (ImageView) convertView
                    .findViewById(R.id.iv_item_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final SpecialDayRow data = mData[position];
        final int julianDay = data.julianDay;
        final String specialDayName = data.specialDayName;
        final String solarDate = data.solarDate;
        final String lunarDate = data.lunarDate;
        final String weekDay = data.weekDay;
        final String daysInterval = data.daysInterval;
        holder.tvTitle.setText(String.format("%s(%s)", specialDayName,
                solarDate));
        holder.tvSubTitle.setText(String.format("%s(%s)", lunarDate, weekDay));
        holder.tvEnter.setText(daysInterval);
        setItemState(holder, julianDay - todayJulianDay >= 0);
        return convertView;
    }

    private static class ViewHolder {
        private TextView tvTitle;
        private TextView tvSubTitle;
        private TextView tvEnter;
        private ImageView ivIcon;
    }

    private void setItemState(ViewHolder holder, boolean enable) {
        if (holder != null) {
            holder.tvTitle.setEnabled(enable);
            holder.tvSubTitle.setEnabled(enable);
            holder.tvEnter.setEnabled(enable);
            holder.ivIcon.setEnabled(enable);
        }
    }

    /* modify by Yusong.Liang on 2017.9.22:end */

    public void reminderCheck(int position) {
        if (!Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            return;
        }
        if (position >= mRowCount) {
            return;
        }
        int julianDay = mData[position].julianDay;
        if (mRemindarOnJulianDays.contains(julianDay)) {
            reminderOff(position);
        } else if (julianDay - todayJulianDay > 0) {
            reminderOn(position);
        }
    }

    public void reminderOn(int position) {
        if (!Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            return;
        }
        int theJulianDay = mData[position].julianDay;
        Time startTime = new Time();
        startTime.setJulianDay(theJulianDay);

        String eventTitle = mData[position].specialDayName;
        String eventTimeZone = TimeZone.getDefault().getID();
        long startMillis = startTime.toMillis(true) + 8
                * DateUtils.HOUR_IN_MILLIS;
        long endMillis = startMillis + DateUtils.HOUR_IN_MILLIS;
        int calendar_id = -1;
        Cursor cursor = mContext.getContentResolver().query(
                Calendars.CONTENT_URI, new String[] { Calendars._ID },
                Calendars.ACCOUNT_NAME + "='" + MY_CALENDAR + "'", null /*
                                                                         * selection
                                                                         * args
                                                                         */,
                null /* sort order */);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            calendar_id = cursor.getInt(0);
        }
        ContentValues values = new ContentValues();
        values.put(Events.CALENDAR_ID, calendar_id);
        values.put(Events.EVENT_TIMEZONE, eventTimeZone);
        values.put(Events.TITLE, eventTitle);
        values.put(Events.DESCRIPTION, mData[position].description);
        values.put(Events.ALL_DAY, 0);
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DURATION, (String) null);
        values.put(Events.DTEND, endMillis);
        values.put(Events.HAS_ALARM, 1);
        mInsertToken = mService.getNextToken();
        mService.startInsert(mInsertToken, theJulianDay, Events.CONTENT_URI,
                values, 0);
        Toast.makeText(mContext,
                eventTitle + mRes.getString(R.string.reminder_on),
                Toast.LENGTH_SHORT).show();
    }

    public void reminderOff(int position) {
        if (!Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            return;
        }
        int theJulianDay = mData[position].julianDay;
        String eventTitle = mData[position].specialDayName;

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI,
                mRemindarOnEventIds.get(theJulianDay));
        mDeleteToken = mService.getNextToken();
        mService.startDelete(mDeleteToken, theJulianDay, uri, null, null, 0);
        Toast.makeText(mContext,
                eventTitle + mRes.getString(R.string.reminder_off),
                Toast.LENGTH_SHORT).show();
    }

    public void addReminder(long eventId, boolean isFirst) {
        Log.d(TAG, "add reminder");
        if (!Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(Reminders.EVENT_ID, eventId);
        if (isFirst) {
            values.put(Reminders.MINUTES, 720);
        } else {
            values.put(Reminders.MINUTES, 0);
        }
        values.put(Reminders.METHOD, Reminders.METHOD_ALERT);

        mInsertToken = mService.getNextToken();
        if (isFirst) {
            mService.startInsert(mInsertToken, eventId, Reminders.CONTENT_URI,
                    values, 0);
        } else {
            mService.startInsert(mInsertToken, null, Reminders.CONTENT_URI,
                    values, 0);
        }
    }

    public int getCount() {
        return mRowCount;
    }

    public Object getItem(int position) {
        if (position >= mRowCount) {
            return null;
        }
        SpecialDayRow item = mData[position];
        return item;
    }

    public long getItemId(int position) {
        if (position >= mRowCount) {
            return todayJulianDay;
        }
        return mData[position].julianDay;
    }

    public int getJulianDay(int position) {
        if (position >= mRowCount) {
            return todayJulianDay;
        }
        return mData[position].julianDay;
    }

    public int findPositionAfter(int julianday) {
        for (int i = 0; i < mRowCount; i++) {
            if (mData[i].julianDay >= julianday) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
