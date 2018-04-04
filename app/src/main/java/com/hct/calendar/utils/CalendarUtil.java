package com.hct.calendar.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.text.TextUtils;
import android.view.WindowManager;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;

public class CalendarUtil {

    private final static int[] LUNAR_INFO = { 0x04bd8, 0x04ae0, 0x0a570,
            0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2,
            0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0,
            0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50,
            0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566,
            0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0,
            0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4,
            0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550,
            0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950,
            0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260,
            0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0,
            0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6,
            0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40,
            0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3,
            0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960,
            0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0,
            0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9,
            0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0,
            0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65,
            0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0,
            0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2,
            0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

    private final static int MIN_YEAR = 1900;
    private final static int MAX_YEAR = 2049;
    private static boolean isLeapYear;
    private final static String START_DATE = "19000130";

    private static int getLeapMonth(int year) {
        return (int) (LUNAR_INFO[year - 1900] & 0xf);
    }

    private static int getLeapMonthDays(int year) {
        if (getLeapMonth(year) != 0) {
            if ((LUNAR_INFO[year - 1900] & 0xf0000) == 0) {
                return 29;
            } else {
                return 30;
            }
        } else {
            return 0;
        }
    }

    private static int getMonthDays(int lunarYeay, int month) throws Exception {
        if ((month > 31) || (month < 0)) {
            throw (new Exception("month error！"));
        }
        int bit = 1 << (16 - month);
        if (((LUNAR_INFO[lunarYeay - 1900] & 0x0FFFF) & bit) == 0) {
            return 29;
        } else {
            return 30;
        }
    }

    private static int getYearDays(int year) {
        int sum = 29 * 12;
        for (int i = 0x8000; i >= 0x8; i >>= 1) {
            if ((LUNAR_INFO[year - 1900] & 0xfff0 & i) != 0) {
                sum++;
            }
        }
        return sum + getLeapMonthDays(year);
    }

    @Deprecated
    private static int daysBetween2(Date startDate, Date endDate) {
        long between_days = (endDate.getTime() - startDate.getTime())
                / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    private static int daysBetween(Date startDate, Date endDate) {
        int days = 0;
        Calendar can1 = Calendar.getInstance();
        can1.setTime(startDate);
        Calendar can2 = Calendar.getInstance();
        can2.setTime(endDate);
        int year1 = can1.get(Calendar.YEAR);
        int year2 = can2.get(Calendar.YEAR);

        Calendar can = null;
        if (can1.before(can2)) {
            days -= can1.get(Calendar.DAY_OF_YEAR);
            days += can2.get(Calendar.DAY_OF_YEAR);
            can = can1;
        } else {
            days -= can2.get(Calendar.DAY_OF_YEAR);
            days += can1.get(Calendar.DAY_OF_YEAR);
            can = can2;
        }
        for (int i = 0; i < Math.abs(year2 - year1); i++) {
            days += can.getActualMaximum(Calendar.DAY_OF_YEAR);
            can.add(Calendar.YEAR, 1);
        }
        return days;
    }

    private static void checkLunarDate(int lunarYear, int lunarMonth,
            int lunarDay, boolean leapMonthFlag) throws Exception {
        if ((lunarYear < MIN_YEAR) || (lunarYear > MAX_YEAR)) {
            throw (new Exception("fatal date！"));
        }
        if ((lunarMonth < 1) || (lunarMonth > 12)) {
            throw (new Exception("fatal date！"));
        }
        if ((lunarDay < 1) || (lunarDay > 30)) {
            throw (new Exception("fatal date！"));
        }

        int leap = getLeapMonth(lunarYear);
        if ((leapMonthFlag == true) && (lunarMonth != leap)) {
            throw (new Exception("fatal date！！"));
        }
    }

    public static String lunarToSolar(String lunarDate, boolean leapMonthFlag)
            throws Exception {
        int lunarYear = Integer.parseInt(lunarDate.substring(0, 4));
        int lunarMonth = Integer.parseInt(lunarDate.substring(4, 6));
        int lunarDay = Integer.parseInt(lunarDate.substring(6, 8));

        checkLunarDate(lunarYear, lunarMonth, lunarDay, leapMonthFlag);

        int offset = 0;

        for (int i = MIN_YEAR; i < lunarYear; i++) {
            int yearDaysCount = getYearDays(i);
            offset += yearDaysCount;
        }
        int leapMonth = getLeapMonth(lunarYear);

        if (leapMonthFlag & leapMonth != lunarMonth) {
            throw (new Exception("fatal date！！"));
        }

        if (leapMonth == 0 || (lunarMonth < leapMonth)
                || (lunarMonth == leapMonth && !leapMonthFlag)) {
            for (int i = 1; i < lunarMonth; i++) {
                int tempMonthDaysCount = getMonthDays(lunarYear, i);
                offset += tempMonthDaysCount;
            }

            if (lunarDay > getMonthDays(lunarYear, lunarMonth)) {
                throw (new Exception("fatal date！"));
            }
            offset += lunarDay;
        } else {
            for (int i = 1; i < lunarMonth; i++) {
                int tempMonthDaysCount = getMonthDays(lunarYear, i);
                offset += tempMonthDaysCount;
            }
            if (lunarMonth > leapMonth) {
                int temp = getLeapMonthDays(lunarYear);
                offset += temp;

                if (lunarDay > getMonthDays(lunarYear, lunarMonth)) {
                    throw (new Exception("fatal date！！"));
                }
                offset += lunarDay;
            } else {
                int temp = getMonthDays(lunarYear, lunarMonth);
                offset += temp;

                if (lunarDay > getLeapMonthDays(lunarYear)) {
                    throw (new Exception("fatal date！！"));
                }
                offset += lunarDay;
            }
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date myDate = null;
        myDate = formatter.parse(START_DATE);
        Calendar c = Calendar.getInstance();
        c.setTime(myDate);
        c.add(Calendar.DATE, offset);
        myDate = c.getTime();

        return formatter.format(myDate);
    }

    public static String[] solarToLunar(String solarDate) throws Exception {
        int i;
        int temp = 0;
        int lunarYear;
        int lunarMonth;
        int lunarDay;
        boolean leapMonthFlag = false;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date myDate = null;
        Date startDate = null;
        try {
            myDate = formatter.parse(solarDate);
            startDate = formatter.parse(START_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int offset = daysBetween(startDate, myDate);

        for (i = MIN_YEAR; i <= MAX_YEAR; i++) {
            temp = getYearDays(i);
            if (offset - temp < 1) {
                break;
            } else {
                offset -= temp;
            }
        }
        lunarYear = i;

        int leapMonth = getLeapMonth(lunarYear);
        if (leapMonth > 0) {
            isLeapYear = true;
        } else {
            isLeapYear = false;
        }

        for (i = 1; i <= 12; i++) {
            if (i == leapMonth + 1 && isLeapYear) {
                temp = getLeapMonthDays(lunarYear);
                isLeapYear = false;
                leapMonthFlag = true;
                i--;
            } else {
                temp = getMonthDays(lunarYear, i);
            }
            offset -= temp;
            if (offset <= 0) {
                break;
            }
        }

        offset += temp;
        lunarMonth = i;
        lunarDay = offset;
        String date = lunarYear + "/" + lunarMonth + "/" + lunarDay;
        String result[] = date.split("/");
        return result;
    }

    public static String getmonth(String s) {
        String monthStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.monthstr);
        String month = "";
        switch (s) {
        case "1":
            month = monthStr[0];
            break;
        case "2":
            month = monthStr[1];
            break;
        case "3":
            month = monthStr[2];
            break;
        case "4":
            month = monthStr[3];
            break;
        case "5":
            month = monthStr[4];
            break;
        case "6":
            month = monthStr[5];
            break;
        case "7":
            month = monthStr[6];
            break;
        case "8":
            month = monthStr[7];
            break;
        case "9":
            month = monthStr[8];
            break;
        case "10":
            month = monthStr[9];
            break;
        case "11":
            month = monthStr[10];
            break;
        case "12":
            month = monthStr[11];
            break;
        }
        return month;
    }

    public static String getDay(String s) {
        String day = "";
        String dayStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.daystr2);
        switch (s) {
        case "1":
            day = dayStr[0];
            break;
        case "2":
            day = dayStr[1];
            break;
        case "3":
            day = dayStr[2];
            break;
        case "4":
            day = dayStr[3];
            break;
        case "5":
            day = dayStr[4];
            break;
        case "6":
            day = dayStr[5];
            break;
        case "7":
            day = dayStr[6];
            break;
        case "8":
            day = dayStr[7];
            break;
        case "9":
            day = dayStr[8];
            break;
        case "10":
            day = dayStr[9];
            break;
        case "11":
            day = dayStr[10];
            break;
        case "12":
            day = dayStr[11];
        case "13":
            day = dayStr[12];
            break;
        case "14":
            day = dayStr[13];
            break;
        case "15":
            day = dayStr[14];
            break;
        case "16":
            day = dayStr[15];
            break;
        case "17":
            day = dayStr[16];
            break;
        case "18":
            day = dayStr[17];
            break;
        case "19":
            day = dayStr[18];
            break;
        case "20":
            day = dayStr[19];
            break;
        case "21":
            day = dayStr[20];
            break;
        case "22":
            day = dayStr[21];
            break;
        case "23":
            day = dayStr[22];
            break;
        case "24":
            day = dayStr[23];
            break;
        case "25":
            day = dayStr[24];
            break;
        case "26":
            day = dayStr[25];
            break;
        case "27":
            day = dayStr[26];
            break;
        case "28":
            day = dayStr[27];
            break;
        case "29":
            day = dayStr[28];
            break;
        case "30":
            day = dayStr[29];
            break;
        }
        return day;
    }

    public static int getIntDay(String s) {
        int day = 0;
        String dayStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.daystr2);
        String dayStr2[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.daystr);
        if (TextUtils.equals(s, dayStr[0])) {
            day = 1;
        } else if (TextUtils.equals(s, dayStr[1])) {
            day = 2;
        } else if (TextUtils.equals(s, dayStr[2])) {
            day = 3;
        } else if (TextUtils.equals(s, dayStr[3])) {
            day = 4;
        } else if (TextUtils.equals(s, dayStr[4])) {
            day = 5;
        } else if (TextUtils.equals(s, dayStr[5])) {
            day = 6;
        } else if (TextUtils.equals(s, dayStr[6])) {
            day = 7;
        } else if (TextUtils.equals(s, dayStr[7])) {
            day = 8;
        } else if (TextUtils.equals(s, dayStr[8])) {
            day = 9;
        } else if (TextUtils.equals(s, dayStr[9])
                || TextUtils.equals(s, dayStr2[9])) {
            day = 10;
        } else if (TextUtils.equals(s, dayStr[10])) {
            day = 11;
        } else if (TextUtils.equals(s, dayStr[11])) {
            day = 12;
        } else if (TextUtils.equals(s, dayStr[12])) {
            day = 13;
        } else if (TextUtils.equals(s, dayStr[13])) {
            day = 14;
        } else if (TextUtils.equals(s, dayStr[14])) {
            day = 15;
        } else if (TextUtils.equals(s, dayStr[15])) {
            day = 16;
        } else if (TextUtils.equals(s, dayStr[16])) {
            day = 17;
        } else if (TextUtils.equals(s, dayStr[17])) {
            day = 18;
        } else if (TextUtils.equals(s, dayStr[18])) {
            day = 19;
        } else if (TextUtils.equals(s, dayStr[19])) {
            day = 20;
        } else if (TextUtils.equals(s, dayStr[20])) {
            day = 21;
        } else if (TextUtils.equals(s, dayStr[21])) {
            day = 22;
        } else if (TextUtils.equals(s, dayStr[22])) {
            day = 23;
        } else if (TextUtils.equals(s, dayStr[23])) {
            day = 24;
        } else if (TextUtils.equals(s, dayStr[24])) {
            day = 25;
        } else if (TextUtils.equals(s, dayStr[25])) {
            day = 26;
        } else if (TextUtils.equals(s, dayStr[26])) {
            day = 27;
        } else if (TextUtils.equals(s, dayStr[27])) {
            day = 28;
        } else if (TextUtils.equals(s, dayStr[28])) {
            day = 29;
        } else if (TextUtils.equals(s, dayStr[29])) {
            day = 30;
        }
        return day;
    }

    public static int getIntMonth(String s) {
        int month = 0;
        String monthStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.monthstr2);

        if (TextUtils.equals(s, monthStr[0])) {
            month = 1;
        } else if (TextUtils.equals(s, monthStr[1])) {
            month = 2;
        } else if (TextUtils.equals(s, monthStr[2])) {
            month = 3;
        } else if (TextUtils.equals(s, monthStr[3])) {
            month = 4;
        } else if (TextUtils.equals(s, monthStr[4])) {
            month = 5;
        } else if (TextUtils.equals(s, monthStr[5])) {
            month = 6;
        } else if (TextUtils.equals(s, monthStr[6])) {
            month = 7;
        } else if (TextUtils.equals(s, monthStr[7])) {
            month = 8;
        } else if (TextUtils.equals(s, monthStr[8])) {
            month = 9;
        } else if (TextUtils.equals(s, monthStr[9])) {
            month = 10;
        } else if (TextUtils.equals(s, monthStr[10])) {
            month = 11;
        } else if (TextUtils.equals(s, monthStr[11])) {
            month = 12;
        }
        return month;
    }

    public static int getIntMonth2(String s) {
        int month = 0;
        String monthStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.monthstr);

        if (TextUtils.equals(s, monthStr[0])) {
            month = 1;
        } else if (TextUtils.equals(s, monthStr[1])) {
            month = 2;
        } else if (TextUtils.equals(s, monthStr[2])) {
            month = 3;
        } else if (TextUtils.equals(s, monthStr[3])) {
            month = 4;
        } else if (TextUtils.equals(s, monthStr[4])) {
            month = 5;
        } else if (TextUtils.equals(s, monthStr[5])) {
            month = 6;
        } else if (TextUtils.equals(s, monthStr[6])) {
            month = 7;
        } else if (TextUtils.equals(s, monthStr[7])) {
            month = 8;
        } else if (TextUtils.equals(s, monthStr[8])) {
            month = 9;
        } else if (TextUtils.equals(s, monthStr[9])) {
            month = 10;
        } else if (TextUtils.equals(s, monthStr[10])) {
            month = 11;
        } else if (TextUtils.equals(s, monthStr[11])) {
            month = 12;
        }
        return month;
    }

    public static String[] getLunar(String year, String month, String day) {
        String lunar[] = new String[2];
        Date sDObj;

        String s = "";

        int SY, SM, SD;

        int sy;

        SY = Integer.parseInt(year);

        SM = Integer.parseInt(month);

        SD = Integer.parseInt(day);

        sy = (SY - 4) % 12;

        Calendar cl = Calendar.getInstance();

        cl.set(SY, SM - 1, SD);

        sDObj = cl.getTime();

        Lunar1(sDObj);
        String year1 = CalendarApplication.getContext().getString(
                R.string.event_year);
        String month1 = CalendarApplication.getContext().getString(
                R.string.event_month);
        lunar[0] = "" + getYear() + year1;
        if (getMonth() < 13) {
            lunar[1] = monthNong[getMonth()] + month1 + cDay(getDay());
        }
        System.out.println(lunar[0]);
        System.out.println(lunar[1]);
        return lunar;
    }

    public static boolean isValidDate(String str) {
        try {
            String date_str = str.substring(0, 4) + "-" + str.substring(4, 6)
                    + "-" + str.substring(6, 8);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            Date date = format.parse(date_str);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    private static String[] monthNong = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.monthnong);

    private static int getYear() {

        return (year);

    }

    private static int getMonth() {

        return (month);

    }

    private static int getDay() {

        return (day);

    }

    private static String cDay(int d) {
        String dayStr[] = CalendarApplication.getContext().getResources()
                .getStringArray(R.array.daystr);
        String s;

        switch (d) {

        case 10:

            s = dayStr[9];

            break;

        case 20:

            s = CalendarApplication.getContext()
                    .getString(R.string.event_ershi);

            break;

        case 30:

            s = CalendarApplication.getContext().getString(
                    R.string.event_sanshi);

            break;

        default:

            s = nStr2[(int) (d / 10)];

            s += nStr1[d % 10];

        }

        return (s);

    }

    private static String[] nStr2 = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.nStr2);
    private static String[] nStr1 = CalendarApplication.getContext()
            .getResources().getStringArray(R.array.nStr1);

    private static void Lunar1(Date objDate) {

        int i, leap = 0, temp = 0;

        Calendar cl = Calendar.getInstance();

        cl.set(1900, 0, 31);

        Date baseDate = cl.getTime();

        int offset = (int) ((objDate.getTime() - baseDate.getTime()) / 86400000);

        dayCyl = offset + 40;

        monCyl = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {

            temp = lYearDays(i);

            offset -= temp;

            monCyl += 12;

        }

        if (offset < 0) {

            offset += temp;

            i--;

            monCyl -= 12;

        }

        year = i;

        yearCyl = i - 1864;

        leap = leapMonth(i);

        isLeap = false;

        for (i = 1; i < 13 && offset > 0; i++) {

            if (leap > 0 && i == (leap + 1) && isLeap == false) {

                --i;

                isLeap = true;

                temp = leapDays(year);

            } else {

                temp = monthDays(year, i);

            }

            if (isLeap == true && i == (leap + 1))

                isLeap = false;

            offset -= temp;

            if (isLeap == false)

                monCyl++;

        }

        if (offset == 0 && leap > 0 && i == leap + 1)

            if (isLeap) {

                isLeap = false;

            } else {

                isLeap = true;

                --i;

                --monCyl;

            }

        if (offset < 0) {

            offset += temp;

            --i;

            --monCyl;

        }

        month = i;

        day = offset + 1;

    }

    private static int monCyl, dayCyl, yearCyl;

    private static int year, month, day;

    private static boolean isLeap;

    private static int[] lunarInfo = { 0x04bd8, 0x04ae0, 0x0a570, 0x054d5,

    0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0,

    0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,

    0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40,

    0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,

    0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7,

    0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0,

    0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355,

    0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,

    0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263,

    0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0,

    0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0,

    0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46,

    0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,

    0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954,

    0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0,

    0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0,

    0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50,

    0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,

    0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6,

    0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0,

    0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0 };

    private static int lYearDays(int y) {

        int i;

        int sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {

            sum += (lunarInfo[y - 1900] & i) == 0 ? 0 : 1;

        }

        return (sum + leapDays(y));

    }

    private static int leapDays(int y) {

        if (leapMonth(y) != 0)

            return ((lunarInfo[y - 1900] & 0x10000) == 0 ? 29 : 30);

        else

            return (0);

    }

    private static int leapMonth(int y) {

        return (lunarInfo[y - 1900] & 0xf);

    }

    private static int monthDays(int y, int m) {

        return ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0 ? 29 : 30);

    }

    public static boolean isEnglish() {
        Locale locale = CalendarApplication.getContext().getResources()
                .getConfiguration().locale;
        String language = locale.getLanguage();
        boolean isEnglish = false;
        if (language.endsWith("en")) {
            isEnglish = true;
        }
        return isEnglish;
    }

    public static void whiteBackground(Activity a) {
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.alpha = 1.0f;
        a.getWindow().setAttributes(lp);
        a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public static void darkBackground(Activity a) {
        WindowManager.LayoutParams lp = a.getWindow().getAttributes();
        lp.alpha = 0.8f;
        a.getWindow().setAttributes(lp);
        a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
}
