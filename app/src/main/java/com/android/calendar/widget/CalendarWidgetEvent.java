package com.android.calendar.widget;

import java.util.Calendar;
import java.util.LinkedList;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;

public class CalendarWidgetEvent {
    private Context mContext;
    private String TAG = "CalendarWidgetEvent";
    private boolean mRegisterObserver = false;
    private int month;
    private int year;
    protected static final int QUERY_TOKEN = 0;
    protected QueryHandler mQueryHandler;
    private static final String[] CALENDAR_PROJECTION = new String[] {
            "dtstart", "dtend", "eventStatus", };
    private final static CalendarWidget calendarWidget = new CalendarWidget();
    private LinkedList<Integer> eventTimes = new LinkedList<Integer>() {
    };
    private static final int DTSTART_COLOUM = 0;
    private static final int DTEND_COLOUM = 1;
    private static final int EVENTSTATUS_COLOUM = 2;

    private static final int ONE_DAY_MIILIS = 24 * 60 * 60 * 1000;

    public CalendarWidgetEvent(Context context, int y, int m) {
        mContext = context;
        year = y;
        month = m;
        init(context);
    }

    public void init(Context context) {
        if (mQueryHandler == null) {
            mQueryHandler = new QueryHandler(context.getContentResolver());
        }

        if (!mRegisterObserver) {
            context.getContentResolver().registerContentObserver(resolveUri(),
                    true, mCalendarObserver);
            mRegisterObserver = true;
        }
        getMonthSchedule(year, month);
    }

    private ContentObserver mCalendarObserver = new ContentObserver(
            new Handler()) {
        // @Override
        public void onChange(boolean selfChange) {
            Log.d(TAG, "lxg CalendarWidgetEvent mCalendarObserver");
            Time t = new Time();
            t.setToNow();
            SharedPreferences displayMonth = mContext.getSharedPreferences(
                    "display_month", 0);
            int currentYear = displayMonth.getInt("year", t.year);
            int currentMonth = displayMonth.getInt("month", t.month);
            getMonthSchedule(currentYear, currentMonth);
        }
    };

    public LinkedList<Integer> getEvent() {
        return eventTimes;
    }

    public void unregisterContentObserver() {
        if (mCalendarObserver != null && mRegisterObserver) {
            mContext.getContentResolver().unregisterContentObserver(
                    mCalendarObserver);
            mRegisterObserver = false;
        }
    }

    public void getMonthSchedule(int y, int m) {
        year = y;
        month = m;
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.YEAR, year);
        ca.set(Calendar.MONTH, month);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        long startTime = ca.getTimeInMillis() - 6 * ONE_DAY_MIILIS;
        int endDay = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
        ca.set(Calendar.DAY_OF_MONTH, endDay);
        long endTime = ca.getTimeInMillis() + 14 * ONE_DAY_MIILIS;
        query(getSelection(startTime, endTime));
    }

    private void query(String selection) {
        Uri uri = resolveUri();
        mQueryHandler.cancelOperation(QUERY_TOKEN);
        mQueryHandler.startQuery(QUERY_TOKEN, null, uri, CALENDAR_PROJECTION,
                selection, null, "dtstart" + " ASC");
    }

    private String getSelection(long start, long end) {
        return "dtstart>=" + start + " AND " + "dtstart<" + end;
    }

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            synchronized (QueryHandler.this) {
                Log.d(TAG, "lxg CalendarWidgetEvent onQueryComplete");
                eventTimes.clear();
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int eventStatus = cursor.getInt(EVENTSTATUS_COLOUM);
                        if (eventStatus == 2)
                            continue;
                        int startIndex = getDayIndex(cursor
                                .getLong(DTSTART_COLOUM));
                        int endIndex = getDayIndex(cursor.getLong(DTEND_COLOUM));
                        for (int i = startIndex; i <= endIndex; i++) {
                            eventTimes.add(i);
                        }
                        Log.d(TAG,
                                "lxg CalendarWidgetEvent onQueryComplete index = "
                                        + getDayIndex(cursor
                                                .getLong(DTSTART_COLOUM)));
                    }
                    calendarWidget.updateWidgetEvent(mContext, eventTimes);
                } else {
                    Log.e(TAG, "lxg CalendarWidgetEvent query calender error");
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }

            }
        }

        protected void onUpdateComplete(int token, Object cookie, int result) {
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
        }
    }

    private int getDayIndex(long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int startPosition = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        calendar.setTimeInMillis(time);
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int index = date + startPosition - 1;
        return index;
    }

    protected Uri resolveUri() {
        // return Uri.parse("content://calendar/events");
        return Events.CONTENT_URI;
    }
}
