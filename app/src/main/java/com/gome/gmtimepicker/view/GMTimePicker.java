package com.gome.gmtimepicker.view;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.android.calendar.R;
import com.gome.gmtimepicker.presenter.ITimePickerPresenter;
import com.gome.gmtimepicker.presenter.TimePickerPresenter;
import com.gome.gmtimepicker.util.FormatterFactory;
import com.gome.gmtimepicker.util.Lunar;
import com.gome.gmtimepicker.util.LunarUtils;
import com.gome.gmtimepicker.util.Type;

/**
 * @author Felix.Liang
 */
@SuppressWarnings("unused")
public class GMTimePicker extends LinearLayout implements ITimePickerView {

    private static final int DEFAULT_START_YEAR = 1900;

    private static final int DEFAULT_END_YEAR = 2100;

    public static final int FLAG_HOUR = 0x4;

    public static final int FLAG_MINUTE = 0x2;

    public static final int FLAG_SECOND = 0x1;

    private static final int DATE_PICKER_SHIFT = 3;

    public static final int FLAG_COMPLEX_DATE = 0x1 << DATE_PICKER_SHIFT;

    public static final int FLAG_DAY = 0x2 << DATE_PICKER_SHIFT;

    public static final int FLAG_MONTH = 0x4 << DATE_PICKER_SHIFT;

    public static final int FLAG_YEAR = 0x8 << DATE_PICKER_SHIFT;

    private static final int DATE_PICKER_MASK = FLAG_YEAR | FLAG_MONTH
            | FLAG_DAY | FLAG_COMPLEX_DATE;

    private static final int TIME_PICKER_MASK = FLAG_HOUR | FLAG_MINUTE
            | FLAG_SECOND;

    private static final int DEFAULT_MODE = FLAG_YEAR | FLAG_MONTH | FLAG_DAY;

    private static final int PICKER_MASK = DATE_PICKER_MASK | TIME_PICKER_MASK;

    private static final String KEY_LUNAR_MODE = "LunarMode";

    private static final String KEY_PARENT_STATE = "ParentState";

    private static final String LOG_TAG = "Debug_GMTimePicker";

    @IntDef({ FLAG_YEAR, FLAG_MONTH, FLAG_DAY, FLAG_COMPLEX_DATE, FLAG_HOUR,
            FLAG_MINUTE, FLAG_SECOND })
    @Retention(RetentionPolicy.SOURCE)
    @interface ComponentFlag {
    }

    private int mMode;

    private boolean mLunarDateMode;

    private GMPicker mYearPicker;
    private GMPicker mMonthPicker;
    private GMPicker mDayOfMonthPicker;
    private GMPicker mAmPmPicker;
    private GMPicker mHourPicker;
    private GMPicker mMinutePicker;
    private GMPicker mSecondPicker;
    private GMPicker mComplexDatePicker;
    private GMPicker mLunarYearPicker;
    private GMPicker mLunarMonthPicker;
    private GMPicker mLunarDayPicker;

    private String[] mAmPmStrings;

    private final NumberPicker.OnValueChangeListener mOnValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            if (picker == mYearPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_YEAR, oldVal, newVal);
            } else if (picker == mMonthPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_MONTH, oldVal, newVal);
            } else if (picker == mDayOfMonthPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_DAY, oldVal, newVal);
            } else if (picker == mHourPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_HOUR, oldVal, newVal);
            } else if (picker == mMinutePicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_MINUTE, oldVal, newVal);
            } else if (picker == mSecondPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_SECOND, oldVal, newVal);
            } else if (picker == mAmPmPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_AM_PM, oldVal, newVal);
            } else if (picker == mComplexDatePicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_COMPLEX_DATE, oldVal,
                        newVal);
            } else if (picker == mLunarYearPicker.getPicker()) {
                mTimePresenter
                        .updateValue(Type.TYPE_LUNAR_YEAR, oldVal, newVal);
            } else if (picker == mLunarMonthPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_LUNAR_MONTH, oldVal,
                        newVal);
            } else if (picker == mLunarDayPicker.getPicker()) {
                mTimePresenter.updateValue(Type.TYPE_LUNAR_DAY, oldVal, newVal);
            } else {
                throw new IllegalArgumentException("No such picker!");
            }
        }
    };

    private OnTimeChangeListener mOnTimeChangeListener;
    private ITimePickerPresenter mTimePresenter;

    public GMTimePicker(Context context) {
        this(context, null);
    }

    public GMTimePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GMTimePicker(Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GMTimePicker(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
        setupValueChangeListener();
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setupView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_time_picker, this, true);
        mYearPicker = (GMPicker) findViewById(R.id.year_picker);
        mMonthPicker = (GMPicker) findViewById(R.id.month_picker);
        mDayOfMonthPicker = (GMPicker) findViewById(R.id.day_picker);
        mAmPmPicker = (GMPicker) findViewById(R.id.am_pm_picker);
        mHourPicker = (GMPicker) findViewById(R.id.hour_picker);
        mMinutePicker = (GMPicker) findViewById(R.id.minute_picker);
        mSecondPicker = (GMPicker) findViewById(R.id.second_picker);
        mComplexDatePicker = (GMPicker) findViewById(R.id.complex_date_picker);
        mLunarYearPicker = (GMPicker) findViewById(R.id.lunar_year_picker);
        mLunarMonthPicker = (GMPicker) findViewById(R.id.lunar_month_picker);
        mLunarDayPicker = (GMPicker) findViewById(R.id.lunar_day_picker);
    }

    private void setupValueChangeListener() {
        mYearPicker.setOnValueChangeListener(mOnValueChangeListener);
        mMonthPicker.setOnValueChangeListener(mOnValueChangeListener);
        mDayOfMonthPicker.setOnValueChangeListener(mOnValueChangeListener);
        mAmPmPicker.setOnValueChangeListener(mOnValueChangeListener);
        mHourPicker.setOnValueChangeListener(mOnValueChangeListener);
        mMinutePicker.setOnValueChangeListener(mOnValueChangeListener);
        mSecondPicker.setOnValueChangeListener(mOnValueChangeListener);
        mComplexDatePicker.setOnValueChangeListener(mOnValueChangeListener);
        mLunarMonthPicker.setOnValueChangeListener(mOnValueChangeListener);
        mLunarYearPicker.setOnValueChangeListener(mOnValueChangeListener);
        mLunarDayPicker.setOnValueChangeListener(mOnValueChangeListener);
    }

    private void initFromAttributes(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.GMTimePicker, defStyleAttr, defStyleRes);
        final int mode = array.getInteger(R.styleable.GMTimePicker_mode,
                DEFAULT_MODE);
        setMode(mode);
        DateFormatSymbols symbols = new DateFormatSymbols();
        mAmPmStrings = symbols.getAmPmStrings();
        mTimePresenter = new TimePickerPresenter(this);
        final int startYear = array.getInteger(
                R.styleable.GMTimePicker_startYear, DEFAULT_START_YEAR);
        final int endYear = array.getInteger(R.styleable.GMTimePicker_endYear,
                DEFAULT_END_YEAR);
        final String maxDate = array
                .getString(R.styleable.GMTimePicker_maxDate);
        final String minDate = array
                .getString(R.styleable.GMTimePicker_minDate);
        mLunarDateMode = array.getBoolean(R.styleable.GMTimePicker_lunarDate,
                false);
        array.recycle();
        initPickers();
        onSolarLunarChanged();
        mTimePresenter.initDateRange(startYear, endYear, minDate, maxDate);
    }

    private void initPickers() {
        initDatePicker();
        initTimePicker();
    }

    private void initDatePicker() {
        mDayOfMonthPicker.setOnLongPressUpdateInternal(100);
        mMonthPicker.setOnLongPressUpdateInternal(200);
        mYearPicker.setOnLongPressUpdateInternal(100);
        mComplexDatePicker.setOnLongPressUpdateInternal(100);
        initDateFormatter();
    }

    private void initDateFormatter() {
        final Resources resources = getResources();
        mDayOfMonthPicker.setFormatter(FormatterFactory.getSolarInstance(
                resources, Type.TYPE_DAY));
        mMonthPicker.setFormatter(FormatterFactory.getSolarInstance(resources,
                Type.TYPE_MONTH));
        mYearPicker.setFormatter(FormatterFactory.getSolarInstance(resources,
                Type.TYPE_YEAR));
        mLunarDayPicker.setFormatter(FormatterFactory.getLunarInstance(
                resources, Type.TYPE_DAY));
        mLunarMonthPicker.setFormatter(FormatterFactory.getLunarInstance(
                resources, Type.TYPE_MONTH));
        mLunarYearPicker.setFormatter(FormatterFactory.getLunarInstance(
                resources, Type.TYPE_YEAR));
        if (mLunarDateMode)
            mComplexDatePicker.setFormatter(FormatterFactory.getLunarInstance(
                    resources, Type.TYPE_COMPLEX_DATE));
        else
            mComplexDatePicker.setFormatter(FormatterFactory.getSolarInstance(
                    resources, Type.TYPE_COMPLEX_DATE));
    }

    private void initTimePicker() {
        mAmPmPicker.setOnLongPressUpdateInternal(100);
        mHourPicker.setOnLongPressUpdateInternal(100);
        mMinutePicker.setOnLongPressUpdateInternal(100);
        mSecondPicker.setOnLongPressUpdateInternal(100);
        mHourPicker.setMinValue(0);
        mHourPicker.setMaxValue(23);
        mHourPicker.setWrapSelectorWheel(true);
        if (is24Hour())
            mHourPicker.setFormatter(FormatterFactory.getSolarInstance(
                    getResources(), Type.TYPE_HOUR));
        else
            mHourPicker.setFormatter(FormatterFactory.getSolarInstance(
                    getResources(), Type.TYPE_HOUR_12));
        mMinutePicker.setMinValue(0);
        mMinutePicker.setMaxValue(59);
        mMinutePicker.setWrapSelectorWheel(true);
        mMinutePicker.setFormatter(FormatterFactory.getSolarInstance(
                getResources(), Type.TYPE_MINUTE));
        mSecondPicker.setMinValue(0);
        mSecondPicker.setMaxValue(59);
        mSecondPicker.setWrapSelectorWheel(true);
        mSecondPicker.setFormatter(FormatterFactory.getSolarInstance(
                getResources(), Type.TYPE_SECOND));
        mAmPmPicker.setMinValue(0);
        mAmPmPicker.setMaxValue(1);
        mAmPmPicker.setDisplayValues(mAmPmStrings);
    }

    public int getMode() {
        return mMode;
    }

    private boolean willShowDatePicker() {
        return (mMode & DATE_PICKER_MASK) != 0;
    }

    private boolean willShowTimePicker() {
        return (mMode & TIME_PICKER_MASK) != 0;
    }

    public void setMode(int mode) {
        final int validMode = mode & PICKER_MASK;
        if (validMode != mMode) {
            mMode = validMode;
            onModeChanged();
        }
    }

    private void onModeChanged() {
        Log.i(LOG_TAG, "onModeChanged: year > " + willShowComponent(FLAG_YEAR)
                + "\tmonth > " + willShowComponent(FLAG_MONTH) + "\tday > "
                + willShowComponent(FLAG_DAY) + "\tcomplex > "
                + willShowComponent(FLAG_COMPLEX_DATE) + "\thour > "
                + willShowComponent(FLAG_HOUR) + "\tmin > "
                + willShowComponent(FLAG_MINUTE) + "\tsec > "
                + willShowComponent(FLAG_SECOND));
        mYearPicker
                .setVisibility(willShowComponent(FLAG_YEAR) ? VISIBLE : GONE);
        mLunarYearPicker.setVisibility(willShowComponent(FLAG_YEAR) ? VISIBLE
                : GONE);
        mMonthPicker.setVisibility(willShowComponent(FLAG_MONTH) ? VISIBLE
                : GONE);
        mLunarMonthPicker.setVisibility(willShowComponent(FLAG_MONTH) ? VISIBLE
                : GONE);
        mDayOfMonthPicker.setVisibility(willShowComponent(FLAG_DAY) ? VISIBLE
                : GONE);
        mLunarDayPicker.setVisibility(willShowComponent(FLAG_DAY) ? VISIBLE
                : GONE);
        mHourPicker
                .setVisibility(willShowComponent(FLAG_HOUR) ? VISIBLE : GONE);
        mAmPmPicker
                .setVisibility((!is24Hour() && willShowComponent(FLAG_HOUR)) ? VISIBLE
                        : GONE);
        mMinutePicker.setVisibility(willShowComponent(FLAG_MINUTE) ? VISIBLE
                : GONE);
        mSecondPicker.setVisibility(willShowComponent(FLAG_SECOND) ? VISIBLE
                : GONE);
        mComplexDatePicker
                .setVisibility(willShowComponent(FLAG_COMPLEX_DATE) ? VISIBLE
                        : GONE);
    }

    private boolean is24Hour() {
        return DateFormat.is24HourFormat(getContext());
    }

    public boolean willShowComponent(@ComponentFlag int flag) {
        return (mMode & flag) != 0;
    }

    @Override
    public void onTimeChanged(Calendar min, Calendar max, final Calendar current) {
        Log.i(LOG_TAG,
                "onTimeChanged: current > Y:" + current.get(Calendar.YEAR)
                        + "\tM:" + (current.get(Calendar.MONTH) + 1) + "\tD:"
                        + current.get(Calendar.DAY_OF_MONTH) + "\tH:"
                        + current.get(Calendar.HOUR) + "\tm:"
                        + current.get(Calendar.MINUTE) + "\ts:"
                        + current.get(Calendar.SECOND));
        Log.i(LOG_TAG,
                "onTimeChanged: min > Y:" + min.get(Calendar.YEAR) + "\tM:"
                        + (min.get(Calendar.MONTH) + 1) + "\tD:"
                        + min.get(Calendar.DAY_OF_MONTH) + "\tH:"
                        + min.get(Calendar.HOUR) + "\tm:"
                        + min.get(Calendar.MINUTE) + "\ts:"
                        + min.get(Calendar.SECOND));
        Log.i(LOG_TAG,
                "onTimeChanged: max > Y:" + max.get(Calendar.YEAR) + "\tM:"
                        + (max.get(Calendar.MONTH) + 1) + "\tD:"
                        + max.get(Calendar.DAY_OF_MONTH) + "\tH:"
                        + max.get(Calendar.HOUR) + "\tm:"
                        + max.get(Calendar.MINUTE) + "\ts:"
                        + max.get(Calendar.SECOND));
        final Lunar lunar = LunarUtils.solar2Lunar(current.get(Calendar.YEAR),
                current.get(Calendar.MONTH) + 1,
                current.get(Calendar.DAY_OF_MONTH));
        final int maxDays = computeDaysFromTarget(max, min);
        final int currentDays = computeDaysFromTarget(current, min);
        if (current.equals(min)) {
            Log.i(LOG_TAG, "current == min");
            mDayOfMonthPicker.setMinValue(current.get(Calendar.DAY_OF_MONTH));
            mDayOfMonthPicker.setMaxValue(current
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            mDayOfMonthPicker.setWrapSelectorWheel(false);
            mLunarDayPicker.setWrapSelectorWheel(false);
            mMonthPicker.setMinValue(current.get(Calendar.MONTH));
            mMonthPicker.setMaxValue(current.getActualMaximum(Calendar.MONTH));
            mMonthPicker.setWrapSelectorWheel(false);
            mLunarMonthPicker.setWrapSelectorWheel(false);
            mLunarDayPicker.setMinValue(lunar.getLunarDay() - 1);
            mLunarMonthPicker.setMinValue(lunar.getRelativeLunarMonth() - 1);
        } else if (current.equals(max)) {
            Log.i(LOG_TAG, "current == max");
            mDayOfMonthPicker.setMinValue(current
                    .getActualMinimum(Calendar.DAY_OF_MONTH));
            mDayOfMonthPicker.setMaxValue(current.get(Calendar.DAY_OF_MONTH));
            mDayOfMonthPicker.setWrapSelectorWheel(false);
            mLunarDayPicker.setWrapSelectorWheel(false);
            mLunarDayPicker.setMaxValue(lunar.getLunarDay() - 1);
            mMonthPicker.setMinValue(current.getActualMinimum(Calendar.MONTH));
            mMonthPicker.setMaxValue(current.get(Calendar.MONTH));
            mMonthPicker.setWrapSelectorWheel(false);
            mLunarMonthPicker.setWrapSelectorWheel(false);
            mLunarMonthPicker.setMaxValue(lunar.getRelativeLunarMonth() - 1);
        } else {
            Log.i(LOG_TAG, "min < current < max");
            mDayOfMonthPicker.setMinValue(1);
            mDayOfMonthPicker.setMaxValue(current
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            mDayOfMonthPicker.setWrapSelectorWheel(true);
            mLunarDayPicker.setMinValue(0);
            mLunarDayPicker.setMaxValue(LunarUtils.daysInLunarMonth(
                    lunar.getLunarYear(), lunar.getRelativeLunarMonth()) - 1);
            mLunarDayPicker.setWrapSelectorWheel(true);
            mMonthPicker.setMinValue(0);
            mMonthPicker.setMaxValue(11);
            mMonthPicker.setWrapSelectorWheel(true);
            mLunarMonthPicker.setMinValue(0);
            mLunarMonthPicker.setMaxValue(LunarUtils.hasLeapMonth(lunar
                    .getLunarYear()) ? 12 : 11);
            mLunarMonthPicker.setWrapSelectorWheel(true);
        }
        mYearPicker.setMinValue(min.get(Calendar.YEAR));
        mYearPicker.setMaxValue(max.get(Calendar.YEAR));
        mYearPicker.setWrapSelectorWheel(false);
        mYearPicker.setValue(current.get(Calendar.YEAR));
        mLunarYearPicker.setMinValue(min.get(Calendar.YEAR) - 1);
        mLunarYearPicker.setMaxValue(max.get(Calendar.YEAR));
        mLunarYearPicker.setValue(lunar.getLunarYear());
        mLunarYearPicker.setWrapSelectorWheel(false);
        mLunarDayPicker.setValue(lunar.getLunarDay() - 1);
        mLunarMonthPicker.setValue(lunar.getRelativeLunarMonth() - 1);
        mMonthPicker.setValue(current.get(Calendar.MONTH));
        mDayOfMonthPicker.setValue(current.get(Calendar.DAY_OF_MONTH));
        mAmPmPicker.setValue(current.get(Calendar.AM_PM));
        mHourPicker.setValue(current.get(Calendar.HOUR_OF_DAY));
        mMinutePicker.setValue(current.get(Calendar.MINUTE));
        mSecondPicker.setValue(current.get(Calendar.SECOND));
        mComplexDatePicker.setMinValue(0);
        mComplexDatePicker.setMaxValue(maxDays);
        mComplexDatePicker.setValue(currentDays);
        if (mOnTimeChangeListener != null)
            mOnTimeChangeListener.onTimeChanged(current);
    }

    private int computeDaysFromTarget(Calendar source, Calendar target) {
        return (int) ((source.getTimeInMillis() - target.getTimeInMillis()) / (1000L * 60 * 60 * 24));
    }

    public void setCurrentDate(int year, int month, int dayOfMonth) {
        mTimePresenter.updateDate(year, month, dayOfMonth);
    }

    public void setCurrentTime(int hour, int minute, int second) {
        mTimePresenter.updateTime(hour, minute, second);
    }

    public void setCurrentTime(long timeInMillis) {
        mTimePresenter.updateTime(timeInMillis);
    }

    public void setLunarDate(boolean lunar) {
        if (!supportLunar())
            return;
        if (mLunarDateMode != lunar) {
            mLunarDateMode = lunar;
            onSolarLunarChanged();
        }
    }

    public Calendar getCurrentTime() {
        return mTimePresenter.getCurrentDate();
    }

    private void onSolarLunarChanged() {
        clearDateDisplayValues();
        updateDatePickerVisibility();
        final Resources resources = getResources();
        if (mLunarDateMode)
            mComplexDatePicker.setFormatter(FormatterFactory.getLunarInstance(
                    resources, Type.TYPE_COMPLEX_DATE));
        else
            mComplexDatePicker.setFormatter(FormatterFactory.getSolarInstance(
                    resources, Type.TYPE_COMPLEX_DATE));
        mComplexDatePicker.refresh();
    }

    private void updateDatePickerVisibility() {
        mDayOfMonthPicker
                .setVisibility((willShowComponent(FLAG_DAY) && !mLunarDateMode) ? VISIBLE
                        : GONE);
        mMonthPicker
                .setVisibility((willShowComponent(FLAG_MONTH) && !mLunarDateMode) ? VISIBLE
                        : GONE);
        mYearPicker
                .setVisibility((willShowComponent(FLAG_YEAR) && !mLunarDateMode) ? VISIBLE
                        : GONE);
        mLunarDayPicker
                .setVisibility((willShowComponent(FLAG_DAY) && mLunarDateMode) ? VISIBLE
                        : GONE);
        mLunarMonthPicker
                .setVisibility((willShowComponent(FLAG_MONTH) && mLunarDateMode) ? VISIBLE
                        : GONE);
        mLunarYearPicker
                .setVisibility((willShowComponent(FLAG_YEAR) && mLunarDateMode) ? VISIBLE
                        : GONE);
    }

    private void clearDateDisplayValues() {
        mDayOfMonthPicker.setDisplayValues(null);
        mMonthPicker.setDisplayValues(null);
        mYearPicker.setDisplayValues(null);
        mComplexDatePicker.setDisplayValues(null);
    }

    public boolean isLunarDate() {
        return mLunarDateMode;
    }

    private boolean supportLunar() {
        Locale locale = getResources().getConfiguration().locale;
        return locale != null
                && locale.getLanguage().equals(new Locale("zh").getLanguage());
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        final boolean visible = visibility == VISIBLE;
        if (!visible) {
            FormatterFactory.clearCache();
        } else {
            initPickers();
            onModeChanged();
            updateDatePickerVisibility();
        }
    }

    public void setOnTimeChangeListener(OnTimeChangeListener listener) {
        mOnTimeChangeListener = listener;
    }

    public interface OnTimeChangeListener {

        void onTimeChanged(Calendar time);
    }
}