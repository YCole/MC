package com.android.calendar.vcalendar;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.TimeZone;

import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

import com.android.calendarcommon2.DateException;
import com.android.calendarcommon2.EventRecurrence;

public class VCalUtil {
    private static final String LogTag = "+++CALENDAR+++";
    private static final String[] WEEK_STRING_ARRAY = { "MO", "TU", "WE", "TH",
            "FR", "SA", "SU" };

    // printable decode
    public static String DecodePrintable(String strPrintable, String Charset) {
        if (strPrintable == null) {
            return "";
        }
        try {
            strPrintable = strPrintable.replaceAll("=\n", "");
            byte[] bytes = strPrintable.getBytes("US-ASCII");
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                if (b != 95) {
                    bytes[i] = b;
                } else {
                    bytes[i] = 32;
                }
            }
            if (bytes == null) {
                return "";
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (int i = 0; i < bytes.length; i++) {
                int b = bytes[i];
                if (b == '=') {
                    try {
                        int u = Character.digit((char) bytes[++i], 16);
                        int l = Character.digit((char) bytes[++i], 16);
                        if (u == -1 || l == -1) {
                            continue;
                        }
                        buffer.write((char) ((u << 4) + l));
                    } catch (ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                } else {
                    buffer.write(b);
                }
            }
            return new String(buffer.toByteArray(), Charset);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // printable encode
    public static String EncodePrintable(String strBeEncoded, String Charset) {
        char[] encode = strBeEncoded.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < encode.length; i++) { /*
                                                   * change (encode[i] >= '!')
                                                   * to (encode[i] >= ' ') ,
                                                   * modify by likai 2012-5-30
                                                   */
            if ((encode[i] >= ' ') && (encode[i] <= '~') && (encode[i] != '=')
                    && (encode[i] != '\n')) {
                sb.append(encode[i]);
            } else if (encode[i] == '=') {
                sb.append("=3D");
            } else if (encode[i] == '\n') {
                sb.append("\n");
            } else {
                StringBuffer sbother = new StringBuffer();
                sbother.append(encode[i]);
                String ss = sbother.toString();
                byte[] buf = null;
                try {
                    buf = ss.getBytes(Charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                if (buf.length == 3) {
                    for (int j = 0; j < 3; j++) {
                        String s16 = String
                                .valueOf(Integer.toHexString(buf[j]));
                        // Take out the last two bits of the Hex number system
                        // of Chinese character ,or the after two bits of equal
                        // mark for =E8,
                        // three of them commissary a Chinese character
                        char c16_6;
                        char c16_7;
                        if (s16.charAt(6) >= 97 && s16.charAt(6) <= 122) {
                            c16_6 = (char) (s16.charAt(6) - 32);
                        } else {
                            c16_6 = s16.charAt(6);
                        }
                        if (s16.charAt(7) >= 97 && s16.charAt(7) <= 122) {
                            c16_7 = (char) (s16.charAt(7) - 32);
                        } else {
                            c16_7 = s16.charAt(7);
                        }
                        sb.append("=" + c16_6 + c16_7);
                    }
                }
            }
        }
        return sb.toString();
    }

    public static void Log(String strLog) {
        Log.i(LogTag, strLog);
    }

    public static void Log(Exception e, String strExtra) {
        Log.e(LogTag, strExtra, e);
    }

    // convert the vcal1.0 timezone format to java timezone string.
    public static String ParseTimeZone(String strTz) {
        try {
            if (strTz.equals("")) {
                return "";
            }
            String strTimeZone = "";
            String[] arrTzString = strTz.split(":");
            String strHour = arrTzString[0];
            String strSymbol = strHour.substring(0, 1);
            String strHourNum = "";
            String strMin = "";
            if (2 == arrTzString.length) {
                strHourNum = strHour.substring(1, 3);
                strMin = arrTzString[1];
            } else if (1 == arrTzString.length) {
                strHourNum = strHour.substring(1);
            }
            /*** HCT_MODIFY jianghejie if tz is not HCT format */
            if (!isNumeric(strHourNum) || !isNumeric(strMin)) {
                return strTz;
            }
            /*** HCT_MODIFY end */
            int nHour = Integer.parseInt(strHourNum);

            int nMin = 0;
            if (!strMin.equals("")) {
                nMin = Integer.parseInt(strMin);
            }
            int nOffset = nHour * 3600 * 1000 + nMin * 60 * 1000;
            if (strSymbol.equals("-")) {
                nOffset = -nOffset;
            }
            String[] arrTimeZoneString = TimeZone.getAvailableIDs(nOffset);
            return arrTimeZoneString[0];
        } catch (Exception e) {
            Log(e, "parse the tz string exception");
            return "";
        }
    }

    /*** HCT_MODIFY jianghejie if tz is not HCT format */
    public static boolean isNumeric(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    /*** HCT_MODIFY end */
    // convert from timezone id to vcal tz format
    public static String GetVCalTZStringFromTZID(String strTZString) {
        TimeZone tz = TimeZone.getTimeZone(strTZString);
        long lOffset = tz.getRawOffset();
        long lHOffset = lOffset / 3600 / 1000;
        long lMOffset = (lOffset - lHOffset * 3600 * 1000) / 1000 / 60;
        String strSymbol = lOffset >= 0 ? "+" : "-";
        lHOffset = Math.abs(lHOffset);
        lMOffset = Math.abs(lMOffset);
        return String.format("%s%02d:%02d", strSymbol, lHOffset, lMOffset);
    }

    public static String getVcsTZString(String tzID, long time) {
        TimeZone tz = TimeZone.getTimeZone(tzID);
        long offset = tz.getOffset(time);
        long hourOffset = offset / 3600 / 1000;
        long minOffset = (offset - hourOffset * 3600 * 1000) / 1000 / 60;
        hourOffset = Math.abs(hourOffset);
        minOffset = Math.abs(minOffset);
        String symbol = offset >= 0 ? "+" : "-";
        return String.format("%s%02d:%02d", symbol, hourOffset, minOffset);
    }

    public static String GetV20RuleFromV10(String strRecuString,
            Time tmStartTime, int nWeekStart) {
        tmStartTime.normalize(true);
        String strVCalStringV20 = null;
        EventRecurrence recur = new EventRecurrence();
        char cIndicator = strRecuString.charAt(0);
        String[] arrRecSplit = strRecuString.split(" ");
        switch (cIndicator) {
        case 'D': {
            recur.freq = EventRecurrence.DAILY;
            break;
        }
        case 'W': {
            final int WORK_DAY_COUNT = 5;
            ArrayList<String> arrStrWeek = new ArrayList();
            int nCount = 0;
            for (String strWeekString : WEEK_STRING_ARRAY) {
                arrStrWeek.add(strWeekString);
            }

            for (String strWeek : arrRecSplit) {
                if (arrStrWeek.contains(strWeek)) {
                    nCount++;
                }
            }
            if (0 == nCount) {
                Log("in onvertVCal10RecuStringToVCal20 week count is " + nCount
                        + " error");
                return strVCalStringV20;
            }
            int[] byDay = new int[nCount];
            int[] byDayNum = new int[nCount];
            // work day
            if (WORK_DAY_COUNT == nCount) {
                byDay[0] = EventRecurrence.MO;
                byDay[1] = EventRecurrence.TU;
                byDay[2] = EventRecurrence.WE;
                byDay[3] = EventRecurrence.TH;
                byDay[4] = EventRecurrence.FR;
            } else if (1 == nCount) {
                // some day in week
                byDay[0] = EventRecurrence.timeDay2Day(tmStartTime.weekDay);
            } else {
                // now not support other weekly event
                Log("in onvertVCal10RecuStringToVCal20 week count is " + nCount
                        + " not support");
                return strVCalStringV20;
            }

            for (int nDay = 0; nDay < nCount; nDay++) {
                byDayNum[nDay] = 0;
            }
            recur.freq = EventRecurrence.WEEKLY;
            recur.byday = byDay;
            recur.bydayNum = byDayNum;
            recur.bydayCount = nCount;
            break;
        }
        case 'M': {

            final int MONTH_DATE_NUM_IDX = 2;
            String strMIndicator = strRecuString.substring(0, 2);
            String strDate = arrRecSplit[MONTH_DATE_NUM_IDX];
            // not support last day
            if ('-' == strDate.charAt(strDate.length() - 1)) {
                Log("in onvertVCal10RecuStringToVCal20 monthly is "
                        + strRecuString + " not support last day");
                return strVCalStringV20;
            }
            if (strMIndicator.equals("MD")) {

                recur.bydayCount = 0;
                recur.bymonthdayCount = 1;
                int[] byMonthDay = new int[1];
                byMonthDay[0] = tmStartTime.monthDay;
                recur.bymonthday = byMonthDay;
            } else if (strMIndicator.equals("MP")) {
                int[] ByDayNum = new int[1];
                int[] ByDay = new int[1];
                int nWeekNum = 1 + ((tmStartTime.monthDay - 1) / 7);
                if (5 == nWeekNum) {
                    nWeekNum = -1;
                }
                ByDayNum[0] = nWeekNum;
                ByDay[0] = EventRecurrence.timeDay2Day(tmStartTime.weekDay);
                recur.bydayCount = 1;
                recur.bymonthdayCount = 0;
                recur.byday = ByDay;
                recur.bydayNum = ByDayNum;
            } else {
                Log("in onvertVCal10RecuStringToVCal20 monthly is "
                        + strRecuString + " String error");
                return strVCalStringV20;
            }
            recur.freq = EventRecurrence.MONTHLY;
            break;
        }
        case 'Y': {
            recur.freq = EventRecurrence.YEARLY;
            break;
        }
        default:
            return null;
        }
        recur.wkst = nWeekStart;
        strVCalStringV20 = recur.toString();
        return strVCalStringV20;

    }

    static public String GetV10RuleFromV20(String strV20String) {
        final String BYDAY_STR = "BYDAY";
        final String BYMONTHDAY_STR = "BYMONTHDAY";
        final String OCCURRENCE_FOREVER = " #0";
        String strV10String = "";
        if (null == strV20String || 0 == strV20String.length()) {
            Log("in getV10RuleFromV20 strV20String is null");
            return "";
        }
        String[] arr20String = strV20String.split(";");
        String strFreq = arr20String[0];
        final int INDICATOR_IDX_INT = 5;
        char cIndicator = strFreq.charAt(INDICATOR_IDX_INT);
        switch (cIndicator) {
        // every day
        case 'D': {
            strV10String = "D1" + OCCURRENCE_FOREVER;
            break;
        }
        case 'W': {

            int nBydayLen = BYDAY_STR.length();
            String strByDayString = "";
            if (3 != arr20String.length) {
                Log("not support the week rule string.string is "
                        + strV20String);
                return "";
            }
            strByDayString = arr20String[2];
            String strWeekDay = strByDayString.substring(nBydayLen + 1);
            if (null == strWeekDay || 0 == strWeekDay.length()) {
                Log("not support the week rule string.the strWeekDay is empty");
            }
            // only one day in week
            if (WEEK_STRING_ARRAY[0].length() == strWeekDay.length()) {
                strV10String = "W1 " + strWeekDay + OCCURRENCE_FOREVER;
            } else {
                String[] arrWeekDay = strWeekDay.split(",");
                strV10String = "W1";
                for (String strWDay : arrWeekDay) {
                    strV10String += " ";
                    strV10String += strWDay;
                }
                strV10String += OCCURRENCE_FOREVER;
            }
            break;
        }
        case 'M': {
            if (3 != arr20String.length) {
                Log("not support the month rule string.string is "
                        + strV20String);
                return "";
            }
            String strMonDay = arr20String[2];
            String[] arrMonthday = strMonDay.split("=");
            if (null == arrMonthday || 2 != arrMonthday.length) {
                Log("not support the month rule,arrMonthday is empty. string.string is "
                        + strV20String);
                return "";
            }

            if (arrMonthday[0].equals(BYMONTHDAY_STR)) {
                String strMonthDay = arrMonthday[1];
                strV10String = "MD1 " + strMonthDay + OCCURRENCE_FOREVER;
            } else {
                String strWeekDay = arrMonthday[1];
                if (3 != strWeekDay.length()) {
                    Log("not support the month rule,strWeekDay length is not 3. string.string is "
                            + strV20String);
                    return "";
                }
                String strIdx = strWeekDay.substring(0, 1);
                String strWDay = strWeekDay.substring(1);
                strV10String = "MP1 " + strIdx + "+" + " " + strWDay
                        + OCCURRENCE_FOREVER;
            }
            break;

        }
        case 'Y': {
            strV10String = "YM1" + OCCURRENCE_FOREVER;
            break;
        }
        }
        return strV10String;
    }

    public static long getDurationMillis(String durationStr) {
        Duration duration = new Duration();
        try {
            duration.parse(durationStr);
        } catch (DateException e) {
            Log.w(VCalUtil.LogTag, "error parsing duration for " + durationStr,
                    e);
            duration.sign = 1;
            duration.weeks = 0;
            duration.days = 0;
            duration.hours = 0;
            duration.minutes = 0;
            duration.seconds = 0;
            durationStr = "+P0S";
        }

        return duration.getMillis();
    }

    public static String getDurationString(long start, long end,
            boolean isAllDay) {
        String duration = "P3600S";
        if (end > start) {
            if (isAllDay) {
                // if it's all day compute the duration in days
                long days = (end - start + DateUtils.DAY_IN_MILLIS - 1)
                        / DateUtils.DAY_IN_MILLIS;
                duration = "P" + days + "D";
            } else {
                // otherwise compute the duration in seconds
                long seconds = (end - start) / DateUtils.SECOND_IN_MILLIS;
                duration = "P" + seconds + "S";
            }
        } else {

            // If no good duration info exists assume the default
            if (isAllDay) {
                duration = "P1D";
            }
        }
        return duration;
    }
}
