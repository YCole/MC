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

import android.annotation.Widget;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.R;
import com.hct.gios.widget.NumberPickerHCT.OnValueChangeListener;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class is a widget for selecting a date. The date can be selected by a
 * year, month, and day spinners or a {@link CalendarView}. The set of spinners
 * and the calendar view are automatically synchronized. The client can
 * customize whether only the spinners, or only the calendar view, or both to be
 * displayed. Also the minimal and maximal date from which dates to be selected
 * can be customized.
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.
 * </p>
 * <p>
 * For a dialog using this view, see {@link android.app.DatePickerDialog}.
 * </p>
 * 
 * @attr ref android.R.styleable#DatePicker_startYear
 * @attr ref android.R.styleable#DatePicker_endYear
 * @attr ref android.R.styleable#DatePicker_maxDate
 * @attr ref android.R.styleable#DatePicker_minDate
 * @attr ref android.R.styleable#DatePicker_spinnersShown
 * @attr ref android.R.styleable#DatePicker_calendarViewShown
 */
@Widget
public class DateTimePickerHCT extends FrameLayout {

    private static final String LOG_TAG = DatePicker.class.getSimpleName();

    private static final String DATE_FORMAT = "MM/dd/yyyy";

    private static final int DEFAULT_START_YEAR = 1900;

    private static final int DEFAULT_END_YEAR = 2100;

    private static final boolean DEFAULT_CALENDAR_VIEW_SHOWN = true;

    private static final boolean DEFAULT_SPINNERS_SHOWN = true;

    private static final boolean DEFAULT_ENABLED_STATE = true;

    private final LinearLayout mSpinners;

    private final NumberPickerHCT mDaySpinner;

    private final NumberPickerHCT mMonthSpinner;

    private final NumberPickerHCT mYearSpinner;

    private final NumberPickerHCT mDividerSpinner;

    private final NumberPickerHCT mDividerSpace;

    private final EditText mDaySpinnerInput;

    private final EditText mMonthSpinnerInput;

    private final EditText mYearSpinnerInput;

    private final CalendarView mCalendarView;

    private Locale mCurrentLocale;

    private OnDateChangedListener mOnDateChangedListener;

    private String[] mShortMonths;

    private final java.text.DateFormat mDateFormat = new SimpleDateFormat(
            DATE_FORMAT);

    private int mNumberOfMonths;

    private Calendar mTempDate;

    private Calendar mMinDate;

    private Calendar mMaxDate;

    private Calendar mCurrentDate;

    private boolean mIsEnabled = DEFAULT_ENABLED_STATE;

    private static final int HOURS_IN_HALF_DAY = 12;

    private static final OnTimeChangedListener NO_OP_CHANGE_LISTENER = new OnTimeChangedListener() {

        public void onTimeChanged(DateTimePickerHCT view, int hourOfDay,
                int minute) {

        }
    };
    // state
    private boolean mIs24HourView;

    private boolean mIsAm;

    // ui components
    private final NumberPickerHCT mHourSpinner;

    private final NumberPickerHCT mMinuteSpinner;

    private final NumberPickerHCT mAmPmSpinner;

    private final EditText mHourSpinnerInput;

    private final EditText mMinuteSpinnerInput;

    private final EditText mAmPmSpinnerInput;

    // private final TextView mDivider;

    // Note that the legacy implementation of the TimePicker is
    // using a button for toggling between AM/PM while the new
    // version uses a NumberPickerHCT spinner. Therefore the code
    // accommodates these two cases to be backwards compatible.
    private final Button mAmPmButton;

    private final String[] mAmPmStrings;
    private int mSelectorWhellPaintColor = 0xff1a4fc3;
    private int mUPdownWhellPaintColor = 0x8a000000;

    // callbacks
    private OnTimeChangedListener mOnTimeChangedListener;

    private Calendar mTempCalendar;

    /**
     * The callback used to indicate the user changes\d the date.
     */
    public interface OnDateChangedListener {

        /**
         * Called upon a date change.
         * 
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
        void onDateChanged(DateTimePickerHCT view, int year, int monthOfYear,
                int dayOfMonth);
    }

    public interface OnTimeChangedListener {

        /**
         * @param view
         *            The view associated with this listener.
         * @param hourOfDay
         *            The current hour.
         * @param minute
         *            The current minute.
         */
        void onTimeChanged(DateTimePickerHCT view, int hourOfDay, int minute);
    }

    public DateTimePickerHCT(Context context) {
        this(context, null);
    }

    public DateTimePickerHCT(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.datePickerStyle);
    }

    public void setColor(int color) {
        mDaySpinner.setColor(mUPdownWhellPaintColor, color);
        mMonthSpinner.setColor(mUPdownWhellPaintColor, color);
        mYearSpinner.setColor(mUPdownWhellPaintColor, color);
        mDividerSpinner.setColor(mUPdownWhellPaintColor, color);
        mHourSpinner.setColor(mUPdownWhellPaintColor, color);
        mMinuteSpinner.setColor(mUPdownWhellPaintColor, color);
        mAmPmSpinner.setColor(mUPdownWhellPaintColor, color);

    }

    public DateTimePickerHCT(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // initialization based on locale
        setCurrentLocale(Locale.getDefault());

        TypedArray attributesArray = context.obtainStyledAttributes(attrs,
                android.R.styleable.DatePicker, defStyle, 0);
        boolean spinnersShown = attributesArray.getBoolean(
                android.R.styleable.DatePicker_spinnersShown,
                DEFAULT_SPINNERS_SHOWN);
        boolean calendarViewShown = attributesArray.getBoolean(
                android.R.styleable.DatePicker_calendarViewShown,
                DEFAULT_CALENDAR_VIEW_SHOWN);
        int startYear = attributesArray.getInt(
                android.R.styleable.DatePicker_startYear, DEFAULT_START_YEAR);
        int endYear = attributesArray.getInt(
                android.R.styleable.DatePicker_endYear, DEFAULT_END_YEAR);
        String minDate = attributesArray
                .getString(android.R.styleable.DatePicker_minDate);
        String maxDate = attributesArray
                .getString(android.R.styleable.DatePicker_maxDate);
        // int layoutResourceId =
        // attributesArray.getResourceId(R.styleable.DatePicker_internalLayout,
        // R.layout.date_picker);
        int layoutResourceId = R.layout.datetime_picker_holohct;
        attributesArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResourceId, this, true);

        OnValueChangeListener onChangeListener = new OnValueChangeListener() {
            public void onValueChange(NumberPickerHCT picker, int oldVal,
                    int newVal) {
                updateInputState();
                mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
                // take care of wrapping of days and months to update greater
                // fields
                if (picker == mDaySpinner) {
                    int maxDayOfMonth = mTempDate
                            .getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (oldVal == maxDayOfMonth && newVal == 1) {
                        mTempDate.add(Calendar.DAY_OF_MONTH, 1);
                    } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                        mTempDate.add(Calendar.DAY_OF_MONTH, -1);
                    } else {
                        mTempDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
                    }
                } else if (picker == mMonthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        mTempDate.add(Calendar.MONTH, 1);
                    } else if (oldVal == 0 && newVal == 11) {
                        mTempDate.add(Calendar.MONTH, -1);
                    } else {
                        mTempDate.add(Calendar.MONTH, newVal - oldVal);
                    }
                } else if (picker == mYearSpinner) {
                    mTempDate.set(Calendar.YEAR, newVal);
                } else {
                    throw new IllegalArgumentException();
                }
                // now set the date to the adjusted one
                setDate(mTempDate.get(Calendar.YEAR),
                        mTempDate.get(Calendar.MONTH),
                        mTempDate.get(Calendar.DAY_OF_MONTH));
                updateSpinners();
                updateCalendarView();
                notifyDateChanged();
            }
        };

        mSpinners = (LinearLayout) findViewById(R.id.pickers);

        // calendar view day-picker

        mCalendarView = (CalendarView) findViewById(R.id.calendar_view);
        mCalendarView
                .setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    public void onSelectedDayChange(CalendarView view,
                            int year, int month, int monthDay) {
                        setDate(year, month, monthDay);
                        updateSpinners();
                        notifyDateChanged();
                    }
                });

        // day
        mDaySpinner = (NumberPickerHCT) findViewById(R.id.day);
        // mDaySpinner.setFormatter(NumberPickerHCT.getTwoDigitFormatter());
        mDaySpinner.setOnLongPressUpdateInterval(100);
        mDaySpinner.setOnValueChangedListener(onChangeListener);
        mDaySpinnerInput = (EditText) mDaySpinner
                .findViewById(R.id.numberpicker_input);
        mDaySpinner.setInputSize(25);
        mDaySpinner.setSelectorSize(18);
        // month
        mMonthSpinner = (NumberPickerHCT) findViewById(R.id.month);
        mMonthSpinner.setMinValue(0);
        mMonthSpinner.setMaxValue(mNumberOfMonths - 1);
        // mMonthSpinner.setFormatter(NumberPickerHCT.getTwoDigitFormatter());
        mMonthSpinner.setDisplayedValues(mShortMonths);
        mMonthSpinner.setOnLongPressUpdateInterval(200);
        mMonthSpinner.setOnValueChangedListener(onChangeListener);
        mMonthSpinnerInput = (EditText) mMonthSpinner
                .findViewById(R.id.numberpicker_input);
        mMonthSpinner.setInputSize(25);
        mMonthSpinner.setSelectorSize(18);

        // year
        mYearSpinner = (NumberPickerHCT) findViewById(R.id.year);
        mYearSpinner.setOnLongPressUpdateInterval(100);
        mYearSpinner.setOnValueChangedListener(onChangeListener);
        mYearSpinnerInput = (EditText) mYearSpinner
                .findViewById(R.id.numberpicker_input);
        mYearSpinner.setInputSize(25);
        mYearSpinner.setSelectorSize(18);
        // mYearSpinner.setDisableInput(mCurrentDate.get(Calendar.YEAR));

        // show only what the user required but make sure we
        // show something and the spinners have higher priority
        if (!spinnersShown && !calendarViewShown) {
            setSpinnersShown(true);
        } else {
            setSpinnersShown(spinnersShown);
            setCalendarViewShown(calendarViewShown);
        }

        // set the min date giving priority of the minDate over startYear
        mTempDate.clear();
        if (!TextUtils.isEmpty(minDate)) {
            if (!parseDate(minDate, mTempDate)) {
                mTempDate.set(startYear, 0, 1);
            }
        } else {
            mTempDate.set(startYear, 0, 1);
        }
        setMinDate(mTempDate.getTimeInMillis());

        // set the max date giving priority of the maxDate over endYear
        mTempDate.clear();
        if (!TextUtils.isEmpty(maxDate)) {
            if (!parseDate(maxDate, mTempDate)) {
                mTempDate.set(endYear, 11, 31);
            }
        } else {
            mTempDate.set(endYear, 11, 31);
        }
        setMaxDate(mTempDate.getTimeInMillis());

        // initialize to current date
        mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH), null);

        mDividerSpace = (NumberPickerHCT) findViewById(R.id.space);
        mDividerSpace.setInputSize(25);
        mDividerSpace.setMinValue(0);
        mDividerSpace.setMaxValue(0);
        String[] space = { " " };
        mDividerSpace.setDisplayedValues(space);
        mDividerSpace.setEnabled(false);

        // hour
        mHourSpinner = (NumberPickerHCT) findViewById(R.id.hour);
        mHourSpinner
                .setOnValueChangedListener(new NumberPickerHCT.OnValueChangeListener() {
                    public void onValueChange(NumberPickerHCT spinner,
                            int oldVal, int newVal) {
                        updateInputState();
                        if (!is24HourView()) {
                            if ((oldVal == HOURS_IN_HALF_DAY - 1 && newVal == HOURS_IN_HALF_DAY)
                                    || (oldVal == HOURS_IN_HALF_DAY && newVal == HOURS_IN_HALF_DAY - 1)) {
                                mIsAm = !mIsAm;
                                updateAmPmControl();
                            }
                        }
                        onTimeChanged();
                    }
                });
        mHourSpinnerInput = (EditText) mHourSpinner
                .findViewById(R.id.numberpicker_input);
        mHourSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mHourSpinner.setInputSize(25);
        mHourSpinner.setSelectorSize(18);

        // divider (only for the new widget style)
        /*
         * mDivider = (TextView) findViewById(R.id.divider); if (mDivider !=
         * null) { mDivider.setText(R.string.time_picker_separator); }
         */
        mDividerSpinner = (NumberPickerHCT) findViewById(R.id.divider);
        mDividerSpinner.setInputSize(25);
        mDividerSpinner.setMinValue(0);
        mDividerSpinner.setMaxValue(0);
        String[] divider = { ":" };
        mDividerSpinner.setDisplayedValues(divider);
        mDividerSpinner.setEnabled(false);

        // mHourSpinner.setDividerWidth(mDivider.getWidth());
        // minute
        mMinuteSpinner = (NumberPickerHCT) findViewById(R.id.minute);
        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setInputSize(25);
        mMinuteSpinner.setSelectorSize(18);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setFormatter(NumberPickerHCT.getTwoDigitFormatter());
        mMinuteSpinner
                .setOnValueChangedListener(new NumberPickerHCT.OnValueChangeListener() {
                    public void onValueChange(NumberPickerHCT spinner,
                            int oldVal, int newVal) {
                        updateInputState();
                        int minValue = mMinuteSpinner.getMinValue();
                        int maxValue = mMinuteSpinner.getMaxValue();
                        if (oldVal == maxValue && newVal == minValue) {
                            int newHour = mHourSpinner.getValue() + 1;
                            if (!is24HourView() && newHour == HOURS_IN_HALF_DAY) {
                                mIsAm = !mIsAm;
                                updateAmPmControl();
                            }
                            mHourSpinner.setValue(newHour);
                        } else if (oldVal == minValue && newVal == maxValue) {
                            int newHour = mHourSpinner.getValue() - 1;
                            if (!is24HourView()
                                    && newHour == HOURS_IN_HALF_DAY - 1) {
                                mIsAm = !mIsAm;
                                updateAmPmControl();
                            }
                            mHourSpinner.setValue(newHour);
                        }
                        onTimeChanged();
                    }
                });
        mMinuteSpinnerInput = (EditText) mMinuteSpinner
                .findViewById(R.id.numberpicker_input);
        mMinuteSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        /* Get the localized am/pm strings and use them in the spinner */
        mAmPmStrings = new DateFormatSymbols().getAmPmStrings();

        // am/pm
        View amPmView = findViewById(R.id.amPm);
        if (amPmView instanceof Button) {
            mAmPmSpinner = null;
            mAmPmSpinnerInput = null;
            mAmPmButton = (Button) amPmView;
            mAmPmButton.setOnClickListener(new OnClickListener() {
                public void onClick(View button) {
                    button.requestFocus();
                    mIsAm = !mIsAm;
                    updateAmPmControl();
                    onTimeChanged();
                }
            });
        } else {
            mAmPmButton = null;
            mAmPmSpinner = (NumberPickerHCT) amPmView;
            mAmPmSpinner.setMinValue(0);
            mAmPmSpinner.setMaxValue(1);
            mAmPmSpinner.setInputSize(13);
            mAmPmSpinner.setSelectorSize(13);
            mAmPmSpinner.setDisplayedValues(mAmPmStrings);
            mAmPmSpinner.setOnValueChangedListener(new OnValueChangeListener() {
                public void onValueChange(NumberPickerHCT picker, int oldVal,
                        int newVal) {
                    updateInputState();
                    picker.requestFocus();
                    mIsAm = !mIsAm;
                    updateAmPmControl();
                    onTimeChanged();
                }
            });
            mAmPmSpinnerInput = (EditText) mAmPmSpinner
                    .findViewById(R.id.numberpicker_input);
            mAmPmSpinnerInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }

        // update controls to initial state
        updateHourControl();
        updateAmPmControl();

        setOnTimeChangedListener(NO_OP_CHANGE_LISTENER);

        // set to current time
        setCurrentHour(mTempCalendar.get(Calendar.HOUR_OF_DAY));
        setCurrentMinute(mTempCalendar.get(Calendar.MINUTE));

        reorderSpinners();
        // re-order the number spinners to match the current date format
        // reorderSpinners();
        if (!isEnabled()) {
            setEnabled(false);
        }

        // set the content descriptions
        setContentDescriptions();

        // If not explicitly specified this view is important for accessibility.
        if (getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
        setColor(mSelectorWhellPaintColor);
    }

    /**
     * Gets the minimal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * <p>
     * Note: The default minimal date is 01/01/1900.
     * <p>
     * 
     * @return The minimal supported date.
     */
    public long getMinDate() {
        return mCalendarView.getMinDate();
    }

    /**
     * Sets the minimal date supported by this {@link NumberPicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * 
     * @param minDate
     *            The minimal supported date.
     */
    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMinDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMinDate.setTimeInMillis(minDate);
        mCalendarView.setMinDate(minDate);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    /**
     * Gets the maximal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * <p>
     * Note: The default maximal date is 12/31/2100.
     * <p>
     * 
     * @return The maximal supported date.
     */
    public long getMaxDate() {
        return mCalendarView.getMaxDate();
    }

    /**
     * Sets the maximal date supported by this {@link DatePicker} in
     * milliseconds since January 1, 1970 00:00:00 in
     * {@link TimeZone#getDefault()} time zone.
     * 
     * @param maxDate
     *            The maximal supported date.
     */
    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMaxDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
        mCalendarView.setMaxDate(maxDate);
        if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
            updateCalendarView();
        }
        updateSpinners();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mDaySpinner.setEnabled(enabled);
        mMonthSpinner.setEnabled(enabled);
        mYearSpinner.setEnabled(enabled);
        mCalendarView.setEnabled(enabled);
        mMinuteSpinner.setEnabled(enabled);

        /*
         * if (mDivider != null) { mDivider.setEnabled(enabled); }
         */
        mHourSpinner.setEnabled(enabled);
        if (mAmPmSpinner != null) {
            mAmPmSpinner.setEnabled(enabled);
        } else {
            mAmPmButton.setEnabled(enabled);
        }
        mIsEnabled = enabled;

    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_TIME;
        if (mIs24HourView) {
            flags |= DateUtils.FORMAT_24HOUR;
        } else {
            flags |= DateUtils.FORMAT_12HOUR;
        }
        mCurrentDate.set(Calendar.HOUR_OF_DAY, getCurrentHour());
        mCurrentDate.set(Calendar.MINUTE, getCurrentMinute());
        String selectedDateUtterance = DateUtils.formatDateTime(mContext,
                mCurrentDate.getTimeInMillis(), flags);

        event.getText().add(selectedDateUtterance);
        /*
         * mTempCalendar.set(Calendar.HOUR_OF_DAY, getCurrentHour());
         * mTempCalendar.set(Calendar.MINUTE, getCurrentMinute());
         * selectedDateUtterance = DateUtils.formatDateTime(mContext,
         * mTempCalendar.getTimeInMillis(), flags);
         * event.getText().add(selectedDateUtterance);
         */

    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(DateTimePickerHCT.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(DateTimePickerHCT.class.getName());
    }

    private void updateHourControl() {
        if (is24HourView()) {
            mHourSpinner.setMinValue(0);
            mHourSpinner.setMaxValue(23);
            mHourSpinner.setFormatter(NumberPickerHCT.getTwoDigitFormatter());
        } else {
            mHourSpinner.setMinValue(1);
            mHourSpinner.setMaxValue(12);
            mHourSpinner.setFormatter(null);
        }
    }

    private void updateAmPmControl() {
        if (is24HourView()) {
            if (mAmPmSpinner != null) {
                mAmPmSpinner.setVisibility(View.GONE);
            } else {
                mAmPmButton.setVisibility(View.GONE);
            }
        } else {
            int index = mIsAm ? Calendar.AM : Calendar.PM;
            if (mAmPmSpinner != null) {
                mAmPmSpinner.setValue(index);
                mAmPmSpinner.setVisibility(View.VISIBLE);
            } else {
                mAmPmButton.setText(mAmPmStrings[index]);
                mAmPmButton.setVisibility(View.VISIBLE);
            }
        }
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
    }

    private void onTimeChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnTimeChangedListener != null) {
            mOnTimeChangedListener.onTimeChanged(this, getCurrentHour(),
                    getCurrentMinute());
        }
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    /**
     * Gets whether the {@link CalendarView} is shown.
     * 
     * @return True if the calendar view is shown.
     * @see #getCalendarView()
     */
    public boolean getCalendarViewShown() {
        return (mCalendarView.getVisibility() == View.VISIBLE);
    }

    /**
     * Gets the {@link CalendarView}.
     * 
     * @return The calendar view.
     * @see #getCalendarViewShown()
     */
    public CalendarView getCalendarView() {
        return mCalendarView;
    }

    /**
     * Sets whether the {@link CalendarView} is shown.
     * 
     * @param shown
     *            True if the calendar view is to be shown.
     */
    public void setCalendarViewShown(boolean shown) {
        mCalendarView.setVisibility(shown ? VISIBLE : GONE);
    }

    /**
     * Gets whether the spinners are shown.
     * 
     * @return True if the spinners are shown.
     */
    public boolean getSpinnersShown() {
        return mSpinners.isShown();
    }

    /**
     * Sets whether the spinners are shown.
     * 
     * @param shown
     *            True if the spinners are to be shown.
     */
    public void setSpinnersShown(boolean shown) {
        mSpinners.setVisibility(shown ? VISIBLE : GONE);
    }

    /**
     * Sets the current locale.
     * 
     * @param locale
     *            The current locale.
     */
    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }

        mCurrentLocale = locale;

        mTempDate = getCalendarForLocale(mTempDate, locale);
        mMinDate = getCalendarForLocale(mMinDate, locale);
        mMaxDate = getCalendarForLocale(mMaxDate, locale);
        mCurrentDate = getCalendarForLocale(mCurrentDate, locale);

        mNumberOfMonths = mTempDate.getActualMaximum(Calendar.MONTH) + 1;
        mShortMonths = new String[mNumberOfMonths];
        for (int i = 0; i < mNumberOfMonths; i++) {
            // mShortMonths[i] = DateUtils.getMonthString(Calendar.JANUARY + i,
            // DateUtils.LENGTH_MEDIUM);
            mShortMonths[i] = String.valueOf(i + 1);
        }
        mTempCalendar = Calendar.getInstance(locale);
    }

    /**
     * Gets a calendar for locale bootstrapped with the value of a given
     * calendar.
     * 
     * @param oldCalendar
     *            The old calendar.
     * @param locale
     *            The locale.
     */
    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            final long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }

    /**
     * Reorders the spinners according to the date format that is explicitly set
     * by the user and if no such is set fall back to the current locale's
     * default format.
     */
    private void reorderSpinners() {
        mSpinners.removeAllViews();
        char[] order = DateFormat.getDateFormatOrder(getContext());
        final int spinnerCount = order.length;
        for (int i = 0; i < spinnerCount; i++) {
            switch (order[i]) {
            case DateFormat.DATE:
                mSpinners.addView(mDaySpinner);
                setImeOptions(mDaySpinner, spinnerCount, i);
                break;
            case DateFormat.MONTH:
                mSpinners.addView(mMonthSpinner);
                setImeOptions(mMonthSpinner, spinnerCount, i);
                break;
            case DateFormat.YEAR:
                mSpinners.addView(mYearSpinner);
                setImeOptions(mYearSpinner, spinnerCount, i);
                break;
            default:
                throw new IllegalArgumentException();
            }
        }
        mSpinners.addView(mDividerSpace);
        mSpinners.addView(mHourSpinner);
        mSpinners.addView(mDividerSpinner);
        mSpinners.addView(mMinuteSpinner);
        mSpinners.addView(mAmPmSpinner);

    }

    /**
     * Updates the current date.
     * 
     * @param year
     *            The year.
     * @param month
     *            The month which is <strong>starting from zero</strong>.
     * @param dayOfMonth
     *            The day of the month.
     */
    public void updateDate(int year, int month, int dayOfMonth) {
        if (!isNewDate(year, month, dayOfMonth)) {
            return;
        }
        setDate(year, month, dayOfMonth);
        updateSpinners();
        updateCalendarView();
        notifyDateChanged();
    }

    // Override so we are in complete control of save / restore for this widget.
    @Override
    protected void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getYear(), getMonth(),
                getDayOfMonth(), getCurrentHour(), getCurrentMinute());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setDate(ss.mYear, ss.mMonth, ss.mDay);
        updateSpinners();
        updateCalendarView();
        setCurrentHour(ss.getHour());
        setCurrentMinute(ss.getMinute());
    }

    /**
     * Initialize the state. If the provided values designate an inconsistent
     * date the values are normalized before updating the spinners.
     * 
     * @param year
     *            The initial year.
     * @param monthOfYear
     *            The initial month <strong>starting from zero</strong>.
     * @param dayOfMonth
     *            The initial day of the month.
     * @param onDateChangedListener
     *            How user is notified date is changed by user, can be null.
     */
    public void init(int year, int monthOfYear, int dayOfMonth,
            OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear, dayOfMonth);
        updateSpinners();
        updateCalendarView();
        mOnDateChangedListener = onDateChangedListener;
    }

    /**
     * Parses the given <code>date</code> and in case of success sets the result
     * to the <code>outDate</code>.
     * 
     * @return True if the date was parsed.
     */
    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            Log.w(LOG_TAG, "Date: " + date + " not in format: " + DATE_FORMAT);
            return false;
        }
    }

    private boolean isNewDate(int year, int month, int dayOfMonth) {
        return (mCurrentDate.get(Calendar.YEAR) != year
                || mCurrentDate.get(Calendar.MONTH) != dayOfMonth || mCurrentDate
                    .get(Calendar.DAY_OF_MONTH) != month);
    }

    private void setDate(int year, int month, int dayOfMonth) {
        mCurrentDate.set(year, month, dayOfMonth);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
    }

    private void updateSpinners() {
        // set the spinner ranges respecting the min and max dates
        if (mCurrentDate.equals(mMinDate)) {
            mDaySpinner.setMinValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
            mDaySpinner.setMaxValue(mCurrentDate
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            mDaySpinner.setWrapSelectorWheel(false);
            mMonthSpinner.setDisplayedValues(null);
            mMonthSpinner.setMinValue(mCurrentDate.get(Calendar.MONTH));
            mMonthSpinner.setMaxValue(mCurrentDate
                    .getActualMaximum(Calendar.MONTH));
            mMonthSpinner.setWrapSelectorWheel(false);
        } else if (mCurrentDate.equals(mMaxDate)) {
            mDaySpinner.setMinValue(mCurrentDate
                    .getActualMinimum(Calendar.DAY_OF_MONTH));
            mDaySpinner.setMaxValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
            mDaySpinner.setWrapSelectorWheel(false);
            mMonthSpinner.setDisplayedValues(null);
            mMonthSpinner.setMinValue(mCurrentDate
                    .getActualMinimum(Calendar.MONTH));
            mMonthSpinner.setMaxValue(mCurrentDate.get(Calendar.MONTH));
            mMonthSpinner.setWrapSelectorWheel(false);
        } else {
            mDaySpinner.setMinValue(1);
            mDaySpinner.setMaxValue(mCurrentDate
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            mDaySpinner.setWrapSelectorWheel(true);
            mMonthSpinner.setDisplayedValues(null);
            mMonthSpinner.setMinValue(0);
            mMonthSpinner.setMaxValue(11);
            mMonthSpinner.setWrapSelectorWheel(true);
        }

        // make sure the month names are a zero based array
        // with the months in the month spinner
        String[] displayedValues = Arrays.copyOfRange(mShortMonths,
                mMonthSpinner.getMinValue(), mMonthSpinner.getMaxValue() + 1);
        mMonthSpinner.setDisplayedValues(displayedValues);

        // year spinner range does not change based on the current date
        mYearSpinner.setMinValue(mMinDate.get(Calendar.YEAR));
        mYearSpinner.setMaxValue(mMaxDate.get(Calendar.YEAR));
        mYearSpinner.setWrapSelectorWheel(false);

        // set the spinner values
        mYearSpinner.setValue(mCurrentDate.get(Calendar.YEAR));

        mMonthSpinner.setValue(mCurrentDate.get(Calendar.MONTH));
        mDaySpinner.setValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Updates the calendar view with the current date.
     */
    private void updateCalendarView() {
        mCalendarView.setDate(mCurrentDate.getTimeInMillis(), false, false);
    }

    public void setOnTimeChangedListener(
            OnTimeChangedListener onTimeChangedListener) {
        mOnTimeChangedListener = onTimeChangedListener;
    }

    /**
     * @return The current hour in the range (0-23).
     */
    public Integer getCurrentHour() {
        int currentHour = mHourSpinner.getValue();
        if (is24HourView()) {
            return currentHour;
        } else if (mIsAm) {
            return currentHour % HOURS_IN_HALF_DAY;
        } else {
            return (currentHour % HOURS_IN_HALF_DAY) + HOURS_IN_HALF_DAY;
        }
    }

    /**
     * Set the current hour.
     */
    public void setCurrentHour(Integer currentHour) {
        // why was Integer used in the first place?
        if (currentHour == null || currentHour == getCurrentHour()) {
            return;
        }
        if (!is24HourView()) {
            // convert [0,23] ordinal to wall clock display
            if (currentHour >= HOURS_IN_HALF_DAY) {
                mIsAm = false;
                if (currentHour > HOURS_IN_HALF_DAY) {
                    currentHour = currentHour - HOURS_IN_HALF_DAY;
                }
            } else {
                mIsAm = true;
                if (currentHour == 0) {
                    currentHour = HOURS_IN_HALF_DAY;
                }
            }
            updateAmPmControl();
        }
        mHourSpinner.setValue(currentHour);
        onTimeChanged();
    }

    /**
     * Set whether in 24 hour or AM/PM mode.
     * 
     * @param is24HourView
     *            True = 24 hour mode. False = AM/PM.
     */
    public void setIs24HourView(Boolean is24HourView) {
        if (mIs24HourView == is24HourView) {
            return;
        }
        mIs24HourView = is24HourView;
        // cache the current hour since spinner range changes
        int currentHour = getCurrentHour();
        updateHourControl();
        // set value after spinner range is updated
        setCurrentHour(currentHour);
        updateAmPmControl();
    }

    /**
     * @return true if this is in 24 hour view else false.
     */
    public boolean is24HourView() {
        return mIs24HourView;
    }

    /**
     * @return The current minute.
     */
    public Integer getCurrentMinute() {
        return mMinuteSpinner.getValue();
    }

    /**
     * Set the current minute (0-59).
     */
    public void setCurrentMinute(Integer currentMinute) {
        if (currentMinute == getCurrentMinute()) {
            return;
        }
        mMinuteSpinner.setValue(currentMinute);
        onTimeChanged();
    }

    /**
     * @return The selected year.
     */

    public int getYear() {
        return mCurrentDate.get(Calendar.YEAR);
    }

    /**
     * @return The selected month.
     */
    public int getMonth() {
        return mCurrentDate.get(Calendar.MONTH);
    }

    /**
     * @return The selected day of month.
     */
    public int getDayOfMonth() {
        return mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Notifies the listener, if such, for a change in the selected date.
     */
    private void notifyDateChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(),
                    getDayOfMonth());
        }
    }

    /**
     * Sets the IME options for a spinner based on its ordering.
     * 
     * @param spinner
     *            The spinner.
     * @param spinnerCount
     *            The total spinner count.
     * @param spinnerIndex
     *            The index of the given spinner.
     */
    private void setImeOptions(NumberPickerHCT spinner, int spinnerCount,
            int spinnerIndex) {
        final int imeOptions;
        if (spinnerIndex < spinnerCount - 1) {
            imeOptions = EditorInfo.IME_ACTION_NEXT;
        } else {
            imeOptions = EditorInfo.IME_ACTION_DONE;
        }
        TextView input = (TextView) spinner
                .findViewById(R.id.numberpicker_input);
        input.setImeOptions(imeOptions);
    }

    private void setContentDescriptions() {
        // Day
        trySetContentDescription(mDaySpinner, R.id.increment,
                R.string.date_picker_increment_day_button);
        trySetContentDescription(mDaySpinner, R.id.decrement,
                R.string.date_picker_decrement_day_button);
        // Month
        trySetContentDescription(mMonthSpinner, R.id.increment,
                R.string.date_picker_increment_month_button);
        trySetContentDescription(mMonthSpinner, R.id.decrement,
                R.string.date_picker_decrement_month_button);
        // Year
        trySetContentDescription(mYearSpinner, R.id.increment,
                R.string.date_picker_increment_year_button);
        trySetContentDescription(mYearSpinner, R.id.decrement,
                R.string.date_picker_decrement_year_button);
        trySetContentDescription(mMinuteSpinner, R.id.increment,
                R.string.time_picker_increment_minute_button);
        trySetContentDescription(mMinuteSpinner, R.id.decrement,
                R.string.time_picker_decrement_minute_button);
        // Hour
        trySetContentDescription(mHourSpinner, R.id.increment,
                R.string.time_picker_increment_hour_button);
        trySetContentDescription(mHourSpinner, R.id.decrement,
                R.string.time_picker_decrement_hour_button);
        // AM/PM
        if (mAmPmSpinner != null) {
            trySetContentDescription(mAmPmSpinner, R.id.increment,
                    R.string.time_picker_increment_set_pm_button);
            trySetContentDescription(mAmPmSpinner, R.id.decrement,
                    R.string.time_picker_decrement_set_am_button);
        }
    }

    private void trySetContentDescription(View root, int viewId,
            int contDescResId) {
        View target = root.findViewById(viewId);
        if (target != null) {
            target.setContentDescription(mContext.getString(contDescResId));
        }
    }

    private void updateInputState() {
        // Make sure that if the user changes the value and the IME is active
        // for one of the inputs if this widget, the IME is closed. If the user
        // changed the value via the IME and there is a next input the IME will
        // be shown, otherwise the user chose another means of changing the
        // value and having the IME up makes no sense.
        InputMethodManager inputMethodManager = InputMethodManager
                .peekInstance();
        if (inputMethodManager != null) {
            if (inputMethodManager.isActive(mYearSpinnerInput)) {
                mYearSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mMonthSpinnerInput)) {
                mMonthSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mDaySpinnerInput)) {
                mDaySpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mHourSpinnerInput)) {
                mHourSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mMinuteSpinnerInput)) {
                mMinuteSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mAmPmSpinnerInput)) {
                mAmPmSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }

    }

    /**
     * Class for managing state storing/restoring.
     */
    private static class SavedState extends BaseSavedState {

        private final int mYear;

        private final int mMonth;

        private final int mDay;
        private final int mHour;

        private final int mMinute;

        /**
         * Constructor called from {@link DatePicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int year, int month, int day,
                int hour, int minute) {
            super(superState);
            mYear = year;
            mMonth = month;
            mDay = day;
            mHour = hour;
            mMinute = minute;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mYear = in.readInt();
            mMonth = in.readInt();
            mDay = in.readInt();
            mHour = in.readInt();
            mMinute = in.readInt();
        }

        public int getHour() {
            return mHour;
        }

        public int getMinute() {
            return mMinute;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mYear);
            dest.writeInt(mMonth);
            dest.writeInt(mDay);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }

        @SuppressWarnings("all")
        // suppress unused and hiding
        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
