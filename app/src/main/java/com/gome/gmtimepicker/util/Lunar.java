package com.gome.gmtimepicker.util;

import android.content.res.Resources;

import com.android.calendar.R;

/**
 * @author Felix.Liang
 */
@SuppressWarnings("unused")
public class Lunar {

    private boolean isLeap;
    private int lunarDay;
    private int lunarMonth;
    private int lunarYear;
    private static String[] sLunarMonths;
    private static String[] sLunarDays;

    public boolean isLeap() {
        return isLeap;
    }

    public void setLeap(boolean leap) {
        isLeap = leap;
    }

    public int getLunarDay() {
        return lunarDay;
    }

    public void setLunarDay(int lunarDay) {
        this.lunarDay = lunarDay;
    }

    public int getLunarMonth() {
        return lunarMonth;
    }

    public void setLunarMonth(int lunarMonth) {
        this.lunarMonth = lunarMonth;
    }

    public int getLunarYear() {
        return lunarYear;
    }

    public void setLunarYear(int lunarYear) {
        this.lunarYear = lunarYear;
    }

    public String getStringLunarYear(Resources resources) {
        return String.valueOf(lunarYear
                + resources.getString(R.string.year_unit));
    }

    public String getStringLunarMonth(Resources resources) {
        if (sLunarMonths == null)
            sLunarMonths = resources.getStringArray(R.array.lunar_month);
        if (lunarMonth > 0)
            return isLeap ? resources.getString(R.string.leap)
                    + sLunarMonths[lunarMonth - 1]
                    : sLunarMonths[lunarMonth - 1];
        else
            return "";
    }

    public String getStringLunarDay(Resources resources) {
        if (sLunarDays == null)
            sLunarDays = resources.getStringArray(R.array.lunar_day);
        if (lunarDay > 0)
            return sLunarDays[lunarDay - 1];
        else
            return "";
    }

    public int getRelativeLunarMonth() {
        int leapMonth = LunarUtils.getLeapMonth(lunarYear);
        if (leapMonth == 0)
            return lunarMonth;
        else {
            if (lunarMonth < leapMonth)
                return lunarMonth;
            else if (lunarMonth == leapMonth) {
                return isLeap ? lunarMonth + 1 : lunarMonth;
            } else {
                return lunarMonth + 1;
            }
        }
    }

    @Override
    public String toString() {
        return "Lunar{" + "isLeap=" + isLeap + ", lunarDay=" + lunarDay
                + ", lunarMonth=" + lunarMonth + ", lunarYear=" + lunarYear
                + '}';
    }
}
