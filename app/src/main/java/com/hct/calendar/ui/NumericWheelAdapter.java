package com.hct.calendar.ui;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;
    public static final int CATEGORY_NUM = 0;
    public static final int CATEGORY_MONTH = 1;
    public static final int CATEGORY_DAY = 2;
    public static final int CATEGORY_YEAR = 3;
    private String[] months = CalendarApplication.getContext().getResources()
            .getStringArray(R.array.monthstr);
    private String[] days = CalendarApplication.getContext().getResources()
            .getStringArray(R.array.daystr);

    // Values
    private int minValue;
    private int maxValue;
    private int category;
    // format
    private String format;

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE, 0);
    }

    /**
     * Constructor
     * 
     * @param minValue
     *            the wheel min value
     * @param maxValue
     *            the wheel max value
     */
    public NumericWheelAdapter(int minValue, int maxValue, int category) {
        this(minValue, maxValue, null);
        this.category = category;
    }

    /**
     * Constructor
     * 
     * @param minValue
     *            the wheel min value
     * @param maxValue
     *            the wheel max value
     * @param format
     *            the format string
     */
    public NumericWheelAdapter(int minValue, int maxValue, String format) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.format = format;
    }

    @Override
    public String getItem(int index) {
        if (category == CATEGORY_NUM) {
            if (index >= 0 && index < getItemsCount()) {
                int value = minValue + index;
                String hour = "";
                if (value < 10) {
                    hour = "0" + value;
                } else {
                    hour = "" + value;
                }
                return hour;
            }
        } else if (category == CATEGORY_MONTH) {
            if (index >= 0 && index < getItemsCount()) {
                int value = minValue + index - 1;
                return months[value];
            }
        } else if (category == CATEGORY_DAY) {
            if (index >= 0 && index < getItemsCount()) {
                int value = minValue + index - 1;
                return days[value];
            }
        } else if (category == CATEGORY_YEAR) {
            if (index >= 0 && index < getItemsCount()) {
                int value = index;
                return CalendarApplication.yearLists.get(value) + "";
            }
        }

        return null;
    }

    @Override
    public int getItemsCount() {
        return maxValue - minValue + 1;
    }

    @Override
    public int getMaximumLength() {
        int max = Math.max(Math.abs(maxValue), Math.abs(minValue));
        int maxLen = Integer.toString(max).length();
        if (minValue < 0) {
            maxLen++;
        }
        return maxLen;
    }

    @Override
    public String getYear(int index) {
        return null;
    }
}
