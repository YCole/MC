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

package com.hct.gios.widget;

import com.hct.gios.widget.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import com.hct.gios.widget.DateTimePickerHCT;
import com.hct.gios.widget.DateTimePickerHCT.OnDateChangedListener;
import com.hct.gios.widget.DateTimePickerHCT.OnTimeChangedListener;
import android.widget.TextView;
import java.util.Calendar;

import com.android.calendar.R;

/**
 * A simple dialog containing an {@link android.widget.DatePicker}.
 * 
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 */
public class DateTimePickerDialogHCT extends AlertDialog implements
        OnClickListener, OnDateChangedListener, OnTimeChangedListener {
    public interface OnTimeSetListener {

        /**
         * @param view
         *            The view associated with this listener.
         * @param hourOfDay
         *            The hour that was set.
         * @param minute
         *            The minute that was set.
         */
        void onTimeSet(DateTimePickerHCT view, int hourOfDay, int minute);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final OnTimeSetListener mTimeCallback;

    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";

    private final DateTimePickerHCT mDateTimePicker;
    private final OnDateSetListener mDateCallBack;
    private final Calendar mCalendar;
    private TextView tvWeekday;

    private boolean mTitleNeedsUpdate = true;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view
         *            The view associated with this listener.
         * @param year
         *            The year that was set.
         * @param monthOfYear
         *            The month that was set (0-11) for compatibility with
         *            {@link java.util.Calendar}.
         * @param dayOfMonth
         *            The day of the month that was set.
         */
        void onDateSet(DateTimePickerHCT view, int year, int monthOfYear,
                int dayOfMonth);
    }

    /**
     * @param context
     *            The context the dialog is to run in.
     * @param callBack
     *            How the parent is notified that the date is set.
     * @param year
     *            The initial year of the dialog.
     * @param monthOfYear
     *            The initial month of the dialog.
     * @param dayOfMonth
     *            The initial day of the dialog.
     */
    public DateTimePickerDialogHCT(Context context, OnDateSetListener callBack,
            OnTimeSetListener timecallBack, int year, int monthOfYear,
            int dayOfMonth, int hourOfDay, int minute, boolean is24HourView) {
        this(context, 0, callBack, timecallBack, year, monthOfYear, dayOfMonth,
                hourOfDay, minute, is24HourView);
    }

    /**
     * @param context
     *            The context the dialog is to run in.
     * @param theme
     *            the theme to apply to this dialog
     * @param callBack
     *            How the parent is notified that the date is set.
     * @param year
     *            The initial year of the dialog.
     * @param monthOfYear
     *            The initial month of the dialog.
     * @param dayOfMonth
     *            The initial day of the dialog.
     */
    public DateTimePickerDialogHCT(Context context, int theme,
            OnDateSetListener datecallBack, OnTimeSetListener timecallBack,
            int year, int monthOfYear, int dayOfMonth, int hourOfDay,
            int minute, boolean is24HourView) {
        super(context, theme);

        mDateCallBack = datecallBack;
        mTimeCallback = timecallBack;
        mInitialHourOfDay = hourOfDay;
        mInitialMinute = minute;
        mIs24HourView = is24HourView;

        mCalendar = Calendar.getInstance();

        Context themeContext = getContext();
        setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.cancel), this);
        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.ok), this);
        setTitle(R.string.datetime_dialog_title);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.datetime_picker_dialoghct, null);
        setView(view);
        mDateTimePicker = (DateTimePickerHCT) view
                .findViewById(R.id.datePicker);
        mDateTimePicker.init(year, monthOfYear, dayOfMonth, this);
        mDateTimePicker.setIs24HourView(mIs24HourView);
        mDateTimePicker.setCurrentHour(mInitialHourOfDay);
        mDateTimePicker.setCurrentMinute(mInitialMinute);
        mDateTimePicker.setOnTimeChangedListener(this);
        tvWeekday = (TextView) view.findViewById(R.id.week);
        updateTitle(year, monthOfYear, dayOfMonth);

    }

    public void setColor(int color) {
        mDateTimePicker.setColor(color);
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateSet();
        tryNotifyTimeSet();
    }

    public void onTimeChanged(DateTimePickerHCT view, int hourOfDay, int minute) {
        /* do nothing */
    }

    private void tryNotifyTimeSet() {
        if (mTimeCallback != null) {
            mDateTimePicker.clearFocus();
            mTimeCallback.onTimeSet(mDateTimePicker,
                    mDateTimePicker.getCurrentHour(),
                    mDateTimePicker.getCurrentMinute());
        }
    }

    public void onDateChanged(DateTimePickerHCT view, int year, int month,
            int day) {
        mDateTimePicker.init(year, month, day, this);
        updateTitle(year, month, day);
    }

    /**
     * Gets the {@link DatePicker} contained in this dialog.
     * 
     * @return The calendar view.
     */
    public DateTimePickerHCT getDatePicker() {
        return mDateTimePicker;
    }

    /**
     * Sets the current date.
     * 
     * @param year
     *            The date year.
     * @param monthOfYear
     *            The date month.
     * @param dayOfMonth
     *            The date day of month.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mDateTimePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mDateTimePicker.setCurrentHour(hourOfDay);
        mDateTimePicker.setCurrentMinute(minutOfHour);
    }

    private void tryNotifyDateSet() {
        if (mDateCallBack != null) {
            mDateTimePicker.clearFocus();
            mDateCallBack
                    .onDateSet(mDateTimePicker, mDateTimePicker.getYear(),
                            mDateTimePicker.getMonth(),
                            mDateTimePicker.getDayOfMonth());
        }
    }

    @Override
    protected void onStop() {
        tryNotifyDateSet();
        tryNotifyTimeSet();
        super.onStop();
    }

    private void updateTitle(int year, int month, int day) {
        /*
         * if (!mDateTimePicker.getCalendarViewShown()) {
         * mCalendar.set(Calendar.YEAR, year); mCalendar.set(Calendar.MONTH,
         * month); mCalendar.set(Calendar.DAY_OF_MONTH, day); String title =
         * DateUtils.formatDateTime(getContext(), mCalendar.getTimeInMillis(),
         * DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY |
         * DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_ABBREV_MONTH |
         * DateUtils.FORMAT_ABBREV_WEEKDAY); setTitle(title); mTitleNeedsUpdate
         * = true; } else { if (mTitleNeedsUpdate) { mTitleNeedsUpdate = false;
         * setTitle(R.string.date_picker_dialog_title); } }
         */
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        String title = DateUtils.formatDateTime(getContext(),
                mCalendar.getTimeInMillis(), DateUtils.FORMAT_SHOW_WEEKDAY);
        tvWeekday.setText(title);

    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mDateTimePicker.getYear());
        state.putInt(MONTH, mDateTimePicker.getMonth());
        state.putInt(DAY, mDateTimePicker.getDayOfMonth());
        state.putInt(HOUR, mDateTimePicker.getCurrentHour());
        state.putInt(MINUTE, mDateTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mDateTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        mDateTimePicker.init(year, month, day, this);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mDateTimePicker.setIs24HourView(savedInstanceState
                .getBoolean(IS_24_HOUR));
        mDateTimePicker.setCurrentHour(hour);
        mDateTimePicker.setCurrentMinute(minute);
    }
}
