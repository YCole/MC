package com.android.calendar.vcalendar;

import java.util.ArrayList;

import android.content.ContentValues;
import android.provider.CalendarContract.Events;

import com.android.calendar.vcalendar.ICalendar.Component;

public interface IXCalendarProcessor {
    /** Calendar Database Col **/
    public final static String EVENT_TITLE_COL_STRING = "title";
    public final static String TZ_COL_STRING = "eventTimezone";
    public final static String START_COL_STRING = "dtstart";
    public final static String END_COL_STRING = "dtend";
    public final static String ALLDAY_COL_STRING = "allDay";
    public final static String DESCRIPTION_COL_STRING = "description";
    public final static String LOCATION_COL_STRING = "eventLocation";
    public static final String TRANS_COL_STRING = Events.ACCESS_LEVEL /* "transparency" */;
    public static final String VISIBILITY_COL_STRING = Events.AVAILABILITY /* "visibility" */;
    public static final String HAS_ALARM_COL_STRING = "hasAlarm";
    public static final String HAS_ATTENDEE_COL_STRING = "hasAttendeeData";
    public static final String RRULE_COL_STRING = "rrule";
    public static final String DURATION_COL_STRING = "duration";

    /** attendee **/
    /** attendee **/

    /** reminder **/
    public static final String MIN_COL_STRING = "minutes";
    public static final String METHOD_COL_STRING = "method";
    /** reminder **/
    /** Calendar Database Col **/

    // som public string
    public final static String ENCODE_PRINTABLE_STRING = "QUOTED-PRINTABLE";
    public final static String CHARSET_UTF8_STRING = "UTF-8";
    public final static String DEFAULT_TITLE = "hctvcaltitle";

    public boolean GetCalendarObject(Component component,
            ContentValues EventsInfo, ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart);

    public boolean GetCalendarString(StringBuilder builFileName,
            StringBuilder builXCalendarInfo, ContentValues EventsInfo,
            ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart);
}
