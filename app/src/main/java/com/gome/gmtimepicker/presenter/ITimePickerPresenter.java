package com.gome.gmtimepicker.presenter;

import java.util.Calendar;

/**
 * @author Felix.Liang
 */
public interface ITimePickerPresenter {

    void initDateRange(int startYear, int endYear, String minDate,
            String maxDate);

    void updateDate(int year, int month, int dayOfMonth);

    void updateTime(int hour, int minute, int second);

    void updateTime(long timeInMillis);

    void updateValue(int type, int oldVal, int newVal);

    Calendar getCurrentDate();
}
