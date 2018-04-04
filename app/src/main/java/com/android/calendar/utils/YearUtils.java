package com.android.calendar.utils;

import java.util.Calendar;

/**
 * get the years
 * 
 * Created by liaozhenbin on 2017/6/19
 */
public class YearUtils {
    public static String LeftPad_Tow_Zero(int str) {
        java.text.DecimalFormat format = new java.text.DecimalFormat("00");
        return format.format(str);
    }

    /**
     * @param mPageNumber
     * @param calendar2
     * @return
     */
    public static Calendar getSelectCalendar(int mPageNumber) {
        Calendar calendar = Calendar.getInstance();
        if (mPageNumber > 500) {
            for (int i = 0; i < mPageNumber - 500; i++) {
                calendar = setNextViewItem(calendar);
            }
        } else if (mPageNumber < 500) {
            for (int i = 0; i < 500 - mPageNumber; i++) {
                calendar = setPrevViewItem(calendar);
            }
        } else {

        }
        return calendar;
    }

    /**
     * year--
     * 
     * @param calendar
     * @return
     */
    private static Calendar setPrevViewItem(Calendar calendar) {
        int iMonthViewCurrentMonth = calendar.get(Calendar.MONTH);
        int iMonthViewCurrentYear = calendar.get(Calendar.YEAR);
        iMonthViewCurrentYear--;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calendar.set(Calendar.YEAR, iMonthViewCurrentYear);
        return calendar;
    }

    /**
     * year++
     * 
     * @param calendar
     * @return
     */
    private static Calendar setNextViewItem(Calendar calendar) {
        int iMonthViewCurrentMonth = calendar.get(Calendar.MONTH);
        int iMonthViewCurrentYear = calendar.get(Calendar.YEAR);
        iMonthViewCurrentYear++;
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, iMonthViewCurrentMonth);
        calendar.set(Calendar.YEAR, iMonthViewCurrentYear);
        return calendar;
    }

    public static int getSelectPosition(String yearStr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 500 + year - calendar.get(Calendar.YEAR);
    }

}
