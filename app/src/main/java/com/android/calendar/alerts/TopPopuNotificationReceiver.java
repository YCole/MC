package com.android.calendar.alerts;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;

import com.android.calendar.utils.EventUtils;

public class TopPopuNotificationReceiver extends BroadcastReceiver {
    private static final String ACTIONIKNOW = "com.calendar.BRODCAST_NOTIFICATION_I_KNOW";
    private static final String ACTIONAFTERFIVE = "com.calendar.BRODCAST_NOTIFICATION_AFTER_FIVE";
    private static final int REMINDERS_INDEX_MINUTES = 1;
    private static ClassToPlayAlert mAlertCalss;
    private Context mContext;
    private long mEventId;
    private long startTime;
    private long endTime;
    private long initDelay = 0;
    private boolean allDay;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        mContext = context;

        if (null == intent) {
            return;
        }

        String action = intent.getAction();
        long eventId = intent.getLongExtra("eventId", 0);
        startTime = intent.getLongExtra("startTime", 0);
        endTime = intent.getLongExtra("endTime", 0);
        allDay = intent.getBooleanExtra("allDay", false);

        mEventId = eventId;
        getInitDelay();
        if (null == action) {
            return;
        }
        // if (null == mAlertCalss) {
        // mAlertCalss = new ClassToPlayAlert(context);
        // }

        if (action.equals(ACTIONIKNOW)) {
            updateAlarmStatus();
            stopActionAboutNotification();
        } else if (action.equals(ACTIONAFTERFIVE)) {
            // AlertService.mIsNotifiedId = -1;
            stopActionAboutNotification();
            updateAlarmTime(5);
        }
    }

    private void stopActionAboutNotification() {
        // need stop ring change by zyp
        // mAlertCalss.stopAlert();
        // need delete item for The notification panel change by zyp
        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(mContext.NOTIFICATION_SERVICE);

        notificationManager.cancel((int) mEventId);
    }

    protected void updateAlarmStatus() {
        // TODO Auto-generated method stub
        if (allDay) {
            EventUtils.updateScheduleEventHasAlarm(mContext, mEventId, false);

        }

    }

    public boolean updateAlarmTime(int minute) {
        ContentResolver contentResolver = mContext.getContentResolver();

        Cursor cursor = contentResolver.query(Reminders.CONTENT_URI,
                AlarmScheduler.REMINDERS_PROJECTION, Reminders.EVENT_ID
                        + " = ?", new String[] { "" + mEventId }, null);
        cursor.moveToFirst();
        int reminderMinutes = cursor.getInt(REMINDERS_INDEX_MINUTES);
        reminderMinutes = reminderMinutes - minute;
        ContentValues event = new ContentValues();
        event.put(Reminders.MINUTES, reminderMinutes);
        // update Reminders.MINUTES
        int count = contentResolver.update(Reminders.CONTENT_URI, event, ""
                + Reminders.EVENT_ID + "=?", new String[] { "" + mEventId });
        if (count > 0) {
            return true;
        }
        return false;
    }

    private void getInitDelay() {
        // TODO Auto-generated method stub
        Cursor mCursor = mContext.getContentResolver().query(
                Events.CONTENT_URI, new String[] { Events.UID_2445 },
                "" + Events._ID + "=?", new String[] { "" + mEventId }, null);
        if (null != mCursor && mCursor.moveToFirst()) {
            initDelay = mCursor
                    .getLong(mCursor.getColumnIndex(Events.UID_2445));
        }
    }

}
