package com.hct.calendar.event;

import android.database.Cursor;

public class EventEntity {
    public static final String[] CALENDAR_PROJECTION = new String[] {
            "dtstart", "dtend", "title", "description", "rrule", "allDay",
            "eventColor", "_id" };

    public EventEntity() {

    }

    public EventEntity(Cursor cursor) {
        if (null != cursor) {
            restore(cursor);
        }

    }

    public long startTime;
    public long endTime;
    public String title;
    public String description;
    public String rrule;
    public String location;
    public String timezone;
    public int allDay;
    public int eventColor;
    public int eventId;
    public int startDay;
    public int endDay;

    public String isHasAgenda;

    // huangli
    public String lunarDate;
    public String yii;
    public String jii;
    public String dateJson;
    // weather
    public String district;
    public int weatherIcon;
    public String temp;
    public String weather;
    public String air;
    public String wind;
    public String wet;
    public String airCondition;

    public static final int PROJECTION_TITLE_INDEX = 0;
    public static final int PROJECTION_LOCATION_INDEX = 1;
    public static final int PROJECTION_ALL_DAY_INDEX = 2;
    public static final int PROJECTION_COLOR_INDEX = 3;
    public static final int PROJECTION_TIMEZONE_INDEX = 4;
    public static final int PROJECTION_EVENT_ID_INDEX = 5;
    public static final int PROJECTION_BEGIN_INDEX = 6;
    public static final int PROJECTION_END_INDEX = 7;
    public static final int PROJECTION_START_DAY_INDEX = 9;
    public static final int PROJECTION_END_DAY_INDEX = 10;
    public static final int PROJECTION_START_MINUTE_INDEX = 11;
    public static final int PROJECTION_END_MINUTE_INDEX = 12;
    public static final int PROJECTION_HAS_ALARM_INDEX = 13;
    public static final int PROJECTION_RRULE_INDEX = 14;
    public static final int PROJECTION_RDATE_INDEX = 15;
    public static final int PROJECTION_SELF_ATTENDEE_STATUS_INDEX = 16;
    public static final int PROJECTION_ORGANIZER_INDEX = 17;
    public static final int PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX = 18;
    public static final int PROJECTION_DISPLAY_AS_ALLDAY = 19;

    public void restore(Cursor cursor) {
        startDay = cursor.getInt(PROJECTION_START_DAY_INDEX);
        endDay = cursor.getInt(PROJECTION_END_DAY_INDEX);
        title = cursor.getString(PROJECTION_TITLE_INDEX);
        startTime = cursor.getLong(PROJECTION_BEGIN_INDEX);
        endTime = cursor.getLong(PROJECTION_END_INDEX);
        eventColor = cursor.getInt(PROJECTION_COLOR_INDEX);
        eventId = cursor.getInt(PROJECTION_EVENT_ID_INDEX);
        allDay = cursor.getInt(PROJECTION_ALL_DAY_INDEX);
        location = cursor.getString(PROJECTION_LOCATION_INDEX);
        timezone = cursor.getString(PROJECTION_TIMEZONE_INDEX);
    }
}
