package com.gome.gmtimepicker.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.res.Resources;
import android.widget.NumberPicker;

import com.android.calendar.R;

/**
 * @author Felix.Liang
 */
public class FormatterFactory {

    private static final Calendar sTempDate = Calendar.getInstance();
    private static final Calendar sMinDate = Calendar.getInstance();
    private static final Calendar sCurrentDate = Calendar.getInstance();
    private static String[] sWeekdays;
    private static String[] sMonths;
    private static String[] sLunarDays;
    private static String[] sLunarMonth;

    public static void setMinTime(long min) {
        sMinDate.setTimeInMillis(min);
    }

    private FormatterFactory() {
    }

    public static NumberPicker.Formatter getSolarInstance(
            final Resources resources, int type) {
        NumberPicker.Formatter formatter = null;
        switch (type) {
        case Type.TYPE_YEAR:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.valueOf(value
                            + resources.getString(R.string.year_unit));
                }
            };
            break;
        case Type.TYPE_MONTH:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if (sMonths == null)
                        sMonths = new DateFormatSymbols().getShortMonths();
                    return sMonths[value];
                }
            };
            break;
        case Type.TYPE_DAY:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.valueOf(value
                            + resources.getString(R.string.day_unit));
                }
            };
            break;
        case Type.TYPE_HOUR_12:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if (value == 0 || value == 12) {
                        return String.valueOf(12);
                    }
                    return String.valueOf(value % 12);
                }
            };
            break;
        case Type.TYPE_HOUR:
            break;
        case Type.TYPE_MINUTE:
        case Type.TYPE_SECOND:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if (value <= 9)
                        return String.valueOf("0" + value);
                    else
                        return String.valueOf(value);
                }
            };
            break;
        case Type.TYPE_COMPLEX_DATE:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return createComplexDate(value, resources, false);
                }
            };
            break;
        }
        return formatter;
    }

    private static String createComplexDate(int days, Resources resources,
            boolean isLunar) {
        long millis = sMinDate.getTimeInMillis() + days
                * (1000L * 60 * 60 * 24);
        sTempDate.clear();
        sTempDate.setTimeInMillis(millis);
        if (sWeekdays == null)
            sWeekdays = new DateFormatSymbols().getShortWeekdays();
        if (isLunar) {
            Lunar lunar = LunarUtils.solar2Lunar(sTempDate.get(Calendar.YEAR),
                    sTempDate.get(Calendar.MONTH) + 1,
                    sTempDate.get(Calendar.DAY_OF_MONTH));
            return lunar.getStringLunarMonth(resources)
                    + lunar.getStringLunarDay(resources) + " "
                    + sWeekdays[sTempDate.get(Calendar.DAY_OF_WEEK)];
        } else {
            if (sMonths == null)
                sMonths = new DateFormatSymbols().getShortMonths();
            return String.format(resources.getString(R.string.complex_format),
                    sWeekdays[sTempDate.get(Calendar.DAY_OF_WEEK)],
                    sMonths[sTempDate.get(Calendar.MONTH)],
                    sTempDate.get(Calendar.DAY_OF_MONTH));
        }
    }

    public static NumberPicker.Formatter getLunarInstance(
            final Resources resources, int type) {
        NumberPicker.Formatter formatter = null;
        switch (type) {
        case Type.TYPE_YEAR:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return String.valueOf(value
                            + resources.getString(R.string.year_unit));
                }
            };
            break;
        case Type.TYPE_MONTH:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    Lunar lunar = LunarUtils.solar2Lunar(
                            sCurrentDate.get(Calendar.YEAR),
                            sCurrentDate.get(Calendar.MONTH) + 1,
                            sCurrentDate.get(Calendar.DAY_OF_MONTH));
                    final int leapMonth = LunarUtils.getLeapMonth(lunar
                            .getLunarYear());
                    if (sLunarMonth == null)
                        sLunarMonth = resources
                                .getStringArray(R.array.lunar_month);
                    if (leapMonth == 0) {
                    	if(value >= sLunarMonth.length){
                    		return "";
                    	}
                        return sLunarMonth[value % 13];
                    } else {
                        if (value < leapMonth) {
                            return sLunarMonth[value];
                        } else if (value == leapMonth) {
                            return resources.getString(R.string.leap)
                                    + sLunarMonth[value - 1];
                        } else {
                            return sLunarMonth[value - 1];
                        }
                    }
                }
            };
            break;
        case Type.TYPE_DAY:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    if (sLunarDays == null)
                        sLunarDays = resources
                                .getStringArray(R.array.lunar_day);
                    return sLunarDays[value];
                }
            };
            break;
        case Type.TYPE_COMPLEX_DATE:
            formatter = new NumberPicker.Formatter() {
                @Override
                public String format(int value) {
                    return createComplexDate(value, resources, true);
                }
            };
            break;
        }
        return formatter;
    }

    public static void clearCache() {
        sMonths = null;
        sWeekdays = null;
    }

    public static void setCurrentTime(Calendar currentTime) {
        sCurrentDate.setTimeInMillis(currentTime.getTimeInMillis());
    }
}
