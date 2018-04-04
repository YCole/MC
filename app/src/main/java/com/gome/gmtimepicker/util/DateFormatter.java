package com.gome.gmtimepicker.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;

import com.android.calendar.R;

public class DateFormatter {

    private static String[] sMonths;
    private static Calendar sTempDate;
    private static String[] sAmPm;

    public static String format(long time, Resources resources) {
        if (sTempDate == null)
            sTempDate = Calendar.getInstance();
        sTempDate.setTimeInMillis(time);
        return format(sTempDate, resources);
    }

    public static String format(Calendar date, Resources resources) {
        if (sMonths == null)
            sMonths = new DateFormatSymbols().getShortMonths();
        return String.format(resources.getString(R.string.ymd_format),
                date.get(Calendar.YEAR), sMonths[date.get(Calendar.MONTH)],
                date.get(Calendar.DAY_OF_MONTH));
    }

    public static String formatClock(long time, Context context) {
        if (sTempDate == null)
            sTempDate = Calendar.getInstance();
        sTempDate.setTimeInMillis(time);
        return formatClock(sTempDate, context);
    }

    public static String formatClock(Calendar date, Context context) {
        final boolean is24Hour = DateFormat.is24HourFormat(context);
        final Resources resources = context.getResources();
        int hour;
        int min = sTempDate.get(Calendar.MINUTE);
        String minText = min < 10 ? "0" + min : String.valueOf(min);
        String hourText;
        if (sAmPm == null) {
            sAmPm = new DateFormatSymbols().getAmPmStrings();
        }
        if (is24Hour) {
            hour = sTempDate.get(Calendar.HOUR_OF_DAY);
            hourText = hour < 10 ? "0" + hour : String.valueOf(hour);
            return hourText + ":" + minText;
        } else {
            int amPm = sTempDate.get(Calendar.AM_PM);
            hour = sTempDate.get(Calendar.HOUR);
            if (hour == 0)
                hour = 12;
            hourText = hour < 10 ? "0" + hour : String.valueOf(hour);
            return String.format(resources.getString(R.string.format_12text),
                    hourText, minText, sAmPm[amPm]);
        }
    }

    public static void clearCache() {
        if (sMonths != null)
            sMonths = null;
        if (sAmPm != null)
            sAmPm = null;
    }
}
