package com.gome.gmtimepicker.model;

import java.util.Calendar;

/**
 * @author Felix.Liang
 */
public interface ITimePickerModel {

    void setDateRange(int startYear, int endYear, String minDate, String maxDate);

    void setDate(int year, int month, int day);

    void setTime(int hour, int minute, int second);

    void setTime(long time);

    void setYear(int year);

    void setMonth(int oldMonth, int month);

    void setDayOfMonth(int oldDay, int newDay);

    void setHour(int oldHour, int newHour);

    void setMinute(int oldMinute, int newMinute);

    void setSecond(int oldSecond, int newSecond);

    Calendar getCurrentTime();

    void setAmPm(int oldVal, int newVal);

    void setDayFromMin(int oldVal, int newVal);

    void setLunarMonth(int oldLunarMonth, int newLunarMonth);

    void setLunarYear(int oldLunarYear, int newLunarYear);

    void setLunarDayOfMonth(int oldLunarDay, int newLunarDay);

    interface OnDateChangedListener {

        void onTimeChanged(Calendar min, Calendar max, Calendar current);
    }
}
