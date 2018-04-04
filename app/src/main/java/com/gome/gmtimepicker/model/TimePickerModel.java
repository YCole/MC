package com.gome.gmtimepicker.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.text.TextUtils;

import com.gome.gmtimepicker.util.FormatterFactory;
import com.gome.gmtimepicker.util.Lunar;
import com.gome.gmtimepicker.util.LunarUtils;

/**
 * @author Felix.Liang
 */
public class TimePickerModel implements ITimePickerModel {

    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private Calendar mCurrentDate;
    private Calendar mTempDate;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private SimpleDateFormat mDateFormat;
    private OnDateChangedListener mDateChangeWatcher;

    public TimePickerModel(Locale locale, OnDateChangedListener listener) {
        mCurrentDate = Calendar.getInstance(locale);
        mTempDate = Calendar.getInstance(locale);
        mMaxDate = Calendar.getInstance(locale);
        mMinDate = Calendar.getInstance(locale);
        mDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
        mDateChangeWatcher = listener;
    }

    @Override
    public void setDateRange(int startYear, int endYear, String minDate,
            String maxDate) {
        mTempDate.clear();
        if (!TextUtils.isEmpty(minDate)) {
            if (!parseDate(minDate, mTempDate)) {
                mTempDate.set(startYear, 0, 1);
            }
        } else {
            mTempDate.set(startYear, 0, 1);
        }
        setMinDate(mTempDate.getTimeInMillis());
        mTempDate.clear();
        if (!TextUtils.isEmpty(maxDate)) {
            if (!parseDate(maxDate, mTempDate)) {
                mTempDate.set(endYear, 11, 31);
            }
        } else {
            mTempDate.set(endYear, 11, 31);
        }
        setMaxDate(mTempDate.getTimeInMillis());
        mDateChangeWatcher.onTimeChanged(mMinDate, mMaxDate, mCurrentDate);
    }

    private boolean isNewDate(int year, int month, int dayOfMonth) {
        return (mCurrentDate.get(Calendar.YEAR) != year
                || mCurrentDate.get(Calendar.MONTH) != month || mCurrentDate
                    .get(Calendar.DAY_OF_MONTH) != dayOfMonth);
    }

    private void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) == mMaxDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
        if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
    }

    private void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) == mMinDate
                        .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMinDate.setTimeInMillis(minDate);
        FormatterFactory.setMinTime(minDate);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        }
    }

    @Override
    public void setDate(int year, int month, int dayOfMonth) {
        if (!isNewDate(year, month, dayOfMonth))
            return;
        mCurrentDate.set(year, month, dayOfMonth);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
        mDateChangeWatcher.onTimeChanged(mMinDate, mMaxDate, mCurrentDate);
    }

    @Override
    public void setYear(int year) {
        mTempDate.clear();
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        mTempDate.set(Calendar.YEAR, year);
        setDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH),
                mTempDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setMonth(int oldMonth, int newMonth) {
        mTempDate.clear();
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        if (oldMonth == 11 && newMonth == 0) {
            mTempDate.add(Calendar.MONTH, 1);
        } else if (oldMonth == 0 && newMonth == 11) {
            mTempDate.add(Calendar.MONTH, -1);
        } else {
            mTempDate.add(Calendar.MONTH, newMonth - oldMonth);
        }
        setDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH),
                mTempDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setDayOfMonth(int oldDay, int newDay) {
        mTempDate.clear();
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        int maxDayOfMonth = mTempDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        if (oldDay == maxDayOfMonth && newDay == 1) {
            mTempDate.add(Calendar.DAY_OF_MONTH, 1);
        } else if (oldDay == 1 && newDay == maxDayOfMonth) {
            mTempDate.add(Calendar.DAY_OF_MONTH, -1);
        } else {
            mTempDate.add(Calendar.DAY_OF_MONTH, newDay - oldDay);
        }
        setDate(mTempDate.get(Calendar.YEAR), mTempDate.get(Calendar.MONTH),
                mTempDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setTime(int hour, int minute, int second) {
        if (!isNewTime(hour, minute, second))
            return;
        mCurrentDate.set(mCurrentDate.get(Calendar.YEAR),
                mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH), hour, minute, second);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
        mDateChangeWatcher.onTimeChanged(mMinDate, mMaxDate, mCurrentDate);
    }

    @Override
    public void setTime(long time) {
        if (mCurrentDate.getTimeInMillis() == time)
            return;
        mCurrentDate.setTimeInMillis(time);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
        mDateChangeWatcher.onTimeChanged(mMinDate, mMaxDate, mCurrentDate);
    }

    @Override
    public void setHour(int oldHour, int newHour) {
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        if (oldHour == 23 && newHour == 0) {
            mTempDate.add(Calendar.HOUR_OF_DAY, 1);
        } else if (oldHour == 0 && newHour == 23) {
            mTempDate.add(Calendar.HOUR_OF_DAY, -1);
        } else {
            mTempDate.add(Calendar.HOUR_OF_DAY, newHour - oldHour);
        }
        setTime(mTempDate.getTimeInMillis());
    }

    @Override
    public void setMinute(int oldMinute, int newMinute) {
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        if (oldMinute == 59 && newMinute == 0) {
            mTempDate.add(Calendar.MINUTE, 1);
        } else if (oldMinute == 0 && newMinute == 59) {
            mTempDate.add(Calendar.MINUTE, -1);
        } else {
            mTempDate.add(Calendar.MINUTE, newMinute - oldMinute);
        }
        setTime(mTempDate.getTimeInMillis());
    }

    @Override
    public void setSecond(int oldSecond, int newSecond) {
        mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
        if (oldSecond == 59 && newSecond == 0) {
            mTempDate.add(Calendar.SECOND, 1);
        } else if (oldSecond == 0 && newSecond == 59) {
            mTempDate.add(Calendar.SECOND, -1);
        } else {
            mTempDate.add(Calendar.SECOND, newSecond - oldSecond);
        }
        setTime(mTempDate.getTimeInMillis());
    }

    @Override
    public void setAmPm(int oldVal, int newVal) {
        int hour = mCurrentDate.get(Calendar.HOUR);
        if (oldVal == 0 && newVal == 1) {
            setHour(hour, hour + 12);
        } else if (oldVal == 1 && newVal == 0) {
            setHour(hour, hour - 12);
        }
    }

    @Override
    public void setDayFromMin(int oldVal, int newVal) {
        final int dVal = newVal - oldVal;
        mTempDate.clear();
        mTempDate.setTimeInMillis(mMinDate.getTimeInMillis() + oldVal
                * (24L * 60 * 60 * 1000));
        oldVal = mTempDate.get(Calendar.DAY_OF_MONTH);
        setDayOfMonth(oldVal, oldVal + dVal);
    }

    @Override
    public void setLunarMonth(int oldLunarMonth, int newLunarMonth) {
        final Lunar lunar = getCurrentLunarDate();
        final int leapMonth = LunarUtils.getLeapMonth(lunar.getLunarYear());
        final int maxLunarMonth = leapMonth == 0 ? 11 : 12;
        Lunar newLunar = new Lunar();
        int offset = 0;
        if (leapMonth != 0) {
            if (leapMonth == newLunarMonth) {
                newLunar.setLeap(true);
            }
            if (newLunarMonth >= leapMonth)
                offset = 1;
        }
        newLunar.setLunarDay(lunar.getLunarDay());
        newLunar.setLunarMonth(newLunarMonth + 1 - offset);
        if (oldLunarMonth == maxLunarMonth && newLunarMonth == 0) {
            newLunar.setLunarYear(lunar.getLunarYear() + 1);
        } else if (oldLunarMonth == 0 && newLunarMonth == maxLunarMonth) {
            newLunar.setLunarYear(lunar.getLunarYear() - 1);
        } else {
            newLunar.setLunarYear(lunar.getLunarYear());
        }
        Calendar date = LunarUtils.lunar2solar(newLunar);
        setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setLunarYear(int oldLunarYear, int newLunarYear) {
        final Lunar lunar = getCurrentLunarDate();
        Lunar newLunar = new Lunar();
        newLunar.setLunarDay(lunar.getLunarDay());
        newLunar.setLunarMonth(lunar.getLunarMonth());
        newLunar.setLunarYear(newLunarYear);
        Calendar date = LunarUtils.lunar2solar(newLunar);
        setDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));
    }

    private Lunar getCurrentLunarDate() {
        return LunarUtils.solar2Lunar(mCurrentDate.get(Calendar.YEAR),
                mCurrentDate.get(Calendar.MONTH) + 1,
                mCurrentDate.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void setLunarDayOfMonth(int oldLunarDay, int newLunarDay) {
        final int oldDay = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        final Lunar lunar = getCurrentLunarDate();
        int maxLunarDay = LunarUtils.daysInLunarMonth(lunar.getLunarYear(),
                lunar.getRelativeLunarMonth()) - 1;
        if (oldLunarDay == 0 && newLunarDay == maxLunarDay) {
            setDayOfMonth(oldDay, oldDay - 1);
        } else if (oldLunarDay == maxLunarDay && newLunarDay == 0) {
            setDayOfMonth(oldDay, oldDay + 1);
        } else {
            final int newDay = oldDay + newLunarDay - oldLunarDay;
            setDayOfMonth(oldDay, newDay);
        }
    }

    private boolean isNewTime(int hour, int minute, int second) {
        return (mCurrentDate.get(Calendar.HOUR_OF_DAY) != hour
                || mCurrentDate.get(Calendar.MINUTE) != minute || mCurrentDate
                    .get(Calendar.SECOND) != second);
    }

    @Override
    public Calendar getCurrentTime() {
        return mCurrentDate;
    }

    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
