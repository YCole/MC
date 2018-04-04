package com.gome.gmtimepicker.presenter;

import java.util.Calendar;
import java.util.Locale;

import com.gome.gmtimepicker.model.ITimePickerModel;
import com.gome.gmtimepicker.model.TimePickerModel;
import com.gome.gmtimepicker.util.FormatterFactory;
import com.gome.gmtimepicker.util.Type;
import com.gome.gmtimepicker.view.ITimePickerView;

/**
 * @author Felix.Liang
 */
public class TimePickerPresenter implements ITimePickerPresenter {

    private final ITimePickerView mView;
    private final ITimePickerModel mModel;

    public TimePickerPresenter(ITimePickerView datePickerView) {
        mView = datePickerView;
        mModel = new TimePickerModel(Locale.getDefault(),
                new ITimePickerModel.OnDateChangedListener() {
                    @Override
                    public void onTimeChanged(Calendar min, Calendar max,
                            Calendar current) {
                        FormatterFactory.setCurrentTime(current);
                        mView.onTimeChanged(min, max, current);
                    }
                });
    }

    @Override
    public void initDateRange(int startYear, int endYear, String minDate,
            String maxDate) {
        mModel.setDateRange(startYear, endYear, minDate, maxDate);
    }

    @Override
    public void updateDate(int year, int month, int dayOfMonth) {
        mModel.setDate(year, month, dayOfMonth);
    }

    @Override
    public void updateTime(int hour, int minute, int second) {
        mModel.setTime(hour, minute, second);
    }

    @Override
    public void updateTime(long timeInMillis) {
        mModel.setTime(timeInMillis);
    }

    @Override
    public void updateValue(int type, int oldVal, int newVal) {
        switch (type) {
        case Type.TYPE_YEAR:
            mModel.setYear(newVal);
            break;
        case Type.TYPE_MONTH:
            mModel.setMonth(oldVal, newVal);
            break;
        case Type.TYPE_DAY:
            mModel.setDayOfMonth(oldVal, newVal);
            break;
        case Type.TYPE_HOUR:
            mModel.setHour(oldVal, newVal);
            break;
        case Type.TYPE_MINUTE:
            mModel.setMinute(oldVal, newVal);
            break;
        case Type.TYPE_SECOND:
            mModel.setSecond(oldVal, newVal);
            break;
        case Type.TYPE_AM_PM:
            mModel.setAmPm(oldVal, newVal);
            break;
        case Type.TYPE_COMPLEX_DATE:
            mModel.setDayFromMin(oldVal, newVal);
            break;
        case Type.TYPE_LUNAR_YEAR:
            mModel.setLunarYear(oldVal, newVal);
            break;
        case Type.TYPE_LUNAR_MONTH:
            mModel.setLunarMonth(oldVal, newVal);
            break;
        case Type.TYPE_LUNAR_DAY:
            mModel.setLunarDayOfMonth(oldVal, newVal);
            break;
        }
    }

    @Override
    public Calendar getCurrentDate() {
        return mModel.getCurrentTime();
    }
}
