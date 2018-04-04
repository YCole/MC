/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.alerts;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarAlerts;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.android.calendar.AsyncQueryService;
import com.android.calendar.EventInfoActivity;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.alerts.AlertService.NotificationMgrWrapper;
import com.android.calendar.alerts.GlobalDismissManager.AlarmId;
import com.hct.gios.widget.ActivityHCT;

/**
 * The alert panel that pops up when there is a calendar event alarm. This
 * activity is started by an intent that specifies an event id.
 */
public class AlertActivity extends ActivityHCT implements OnClickListener {
    private static final String TAG = "AlertActivity";

    private boolean doUpdateAlertNotification = false;

    // add by mf4.0
    public static final String EVENTS_EXT_ACTION_DATA = "action_data";
    public static final String EVENTS_EXT_EVENT_TYPE = "eventType";

    private static final String[] PROJECTION = new String[] {
            CalendarAlerts._ID, // 0
            CalendarAlerts.TITLE, // 1
            CalendarAlerts.EVENT_LOCATION, // 2
            CalendarAlerts.ALL_DAY, // 3
            CalendarAlerts.BEGIN, // 4
            CalendarAlerts.END, // 5
            CalendarAlerts.EVENT_ID, // 6
            CalendarAlerts.DISPLAY_COLOR, // 7
            CalendarAlerts.RRULE, // 8
            CalendarAlerts.HAS_ALARM, // 9
            CalendarAlerts.STATE, // 10
            CalendarAlerts.ALARM_TIME, // 11
            EVENTS_EXT_ACTION_DATA, // 12
            EVENTS_EXT_EVENT_TYPE,// 13
            CalendarAlerts.RDATE,// 14
    };

    public static final int INDEX_ROW_ID = 0;
    public static final int INDEX_TITLE = 1;
    public static final int INDEX_EVENT_LOCATION = 2;
    public static final int INDEX_ALL_DAY = 3;
    public static final int INDEX_BEGIN = 4;
    public static final int INDEX_END = 5;
    public static final int INDEX_EVENT_ID = 6;
    public static final int INDEX_COLOR = 7;
    public static final int INDEX_RRULE = 8;
    public static final int INDEX_HAS_ALARM = 9;
    public static final int INDEX_STATE = 10;
    public static final int INDEX_ALARM_TIME = 11;
    public static final int INDEX_EXT_ACTION_DATA = 12;
    public static final int INDEX_EXT_EVENT_TYPE = 13;
    public static final int INDEX_RDATE = 14;

    private static final String SELECTION = CalendarAlerts.STATE + "=?";
    private static final String[] SELECTIONARG = new String[] { Integer
            .toString(CalendarAlerts.STATE_FIRED) };

    private AlertAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private Cursor mCursor;
    private ListView mListView;
    private Button mSnoozeAllButton;
    private Button mDismissAllButton;
    public static final long SNOOZE_DELAY = 5 * 60 * 1000L;
    public static final int NOTIFICATION_ID = 0;

    private TelephonyManager mTelManager;
    private ToneGenerator mToneGenerator;

    private static ClassToPlayAlert mAlertCalss;

    // private boolean pauseStopAlert = false;

    static {
        if (!Utils.isJellybeanOrLater()) {
            PROJECTION[INDEX_COLOR] = CalendarAlerts.CALENDAR_COLOR;
        }
    }

    private void dismissFiredAlarms() {
        ContentValues values = new ContentValues(1 /* size */);
        values.put(PROJECTION[INDEX_STATE], CalendarAlerts.STATE_DISMISSED);
        String selection = CalendarAlerts.STATE + "="
                + CalendarAlerts.STATE_FIRED;
        mQueryHandler.startUpdate(0, null, CalendarAlerts.CONTENT_URI, values,
                selection, null /* selectionArgs */, Utils.UNDO_DELAY);

        if (mCursor == null) {
            Log.e(TAG,
                    "Unable to globally dismiss all notifications because cursor was null.");
            return;
        }
        if (mCursor.isClosed()) {
            Log.e(TAG,
                    "Unable to globally dismiss all notifications because cursor was closed.");
            return;
        }
        if (!mCursor.moveToFirst()) {
            Log.e(TAG,
                    "Unable to globally dismiss all notifications because cursor was empty.");
            return;
        }

        List<AlarmId> alarmIds = new LinkedList<AlarmId>();
        do {
            long eventId = mCursor.getLong(INDEX_EVENT_ID);
            long eventStart = mCursor.getLong(INDEX_BEGIN);
            alarmIds.add(new AlarmId(eventId, eventStart));
        } while (mCursor.moveToNext());
        initiateGlobalDismiss(alarmIds);
    }

    private void dismissAlarm(long id, long eventId, long startTime) {
        ContentValues values = new ContentValues(1 /* size */);
        values.put(PROJECTION[INDEX_STATE], CalendarAlerts.STATE_DISMISSED);
        String selection = CalendarAlerts._ID + "=" + id;
        mQueryHandler.startUpdate(0, null, CalendarAlerts.CONTENT_URI, values,
                selection, null /* selectionArgs */, Utils.UNDO_DELAY);

        List<AlarmId> alarmIds = new LinkedList<AlarmId>();
        alarmIds.add(new AlarmId(eventId, startTime));
        initiateGlobalDismiss(alarmIds);
    }

    public void dismissAlarmAndFinishActivity(long id, long eventId,
            long startTime) {
        dismissAlarm(id, eventId, startTime);
        finish();
    }

    @SuppressWarnings("unchecked")
    private void initiateGlobalDismiss(List<AlarmId> alarmIds) {
        new AsyncTask<List<AlarmId>, Void, Void>() {
            @Override
            protected Void doInBackground(List<AlarmId>... params) {
                GlobalDismissManager.dismissGlobally(getApplicationContext(),
                        params[0]);
                return null;
            }
        }.execute(alarmIds);
    }

    private class QueryHandler extends AsyncQueryService {
        public QueryHandler(Context context) {
            super(context);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            // Only set mCursor if the Activity is not finishing. Otherwise
            // close the cursor.
            if (!isFinishing()) {
                mCursor = cursor;
                mAdapter.changeCursor(cursor);
                mListView.setSelection(cursor.getCount() - 1);

                if (cursor.getCount() < 2) {
                    mSnoozeAllButton.setText(R.string.snooze_label);
                    mDismissAllButton.setText(R.string.dismiss_label);
                }
                // The results are in, enable the buttons
                mSnoozeAllButton.setEnabled(true);
                // The results are in, enable the buttons
                mDismissAllButton.setEnabled(true);
            } else {
                cursor.close();
            }
        }

        @Override
        protected void onInsertComplete(int token, Object cookie, Uri uri) {
            if (uri != null) {
                Long alarmTime = (Long) cookie;

                if (alarmTime != 0) {
                    // Set a new alarm to go off after the snooze delay.
                    // TODO make provider schedule this automatically when
                    // inserting an alarm
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    scheduleAlarm(AlertActivity.this, alarmManager, alarmTime);
                }
            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            // Ignore
            doUpdateAlertNotification = true;
        }
    }

    public static void scheduleAlarm(Context context, AlarmManager manager,
            long alarmTime) {

        if (manager == null) {
            manager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
        }

        Intent intent = new Intent(CalendarContract.ACTION_EVENT_REMINDER);
        intent.setData(ContentUris.withAppendedId(CalendarContract.CONTENT_URI,
                alarmTime));
        intent.putExtra(CalendarContract.CalendarAlerts.ALARM_TIME, alarmTime);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
    }

    private final OnItemClickListener mViewListener = new OnItemClickListener() {

        @SuppressLint("NewApi")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long i) {
            AlertActivity alertActivity = AlertActivity.this;
            Cursor cursor = alertActivity.getItemForView(view);

            long alarmId = cursor.getLong(INDEX_ROW_ID);
            long eventId = cursor.getLong(AlertActivity.INDEX_EVENT_ID);
            long startMillis = cursor.getLong(AlertActivity.INDEX_BEGIN);

            // Mark this alarm as DISMISSED
            dismissAlarm(alarmId, eventId, startMillis);

            // build an intent and task stack to start EventInfoActivity with
            // AllInOneActivity
            // as the parent activity rooted to home.
            long endMillis = cursor.getLong(AlertActivity.INDEX_END);
            Intent eventIntent = AlertUtils.buildEventViewIntent(
                    AlertActivity.this, eventId, startMillis, endMillis);

            if (Utils.isJellybeanOrLater()) {
                TaskStackBuilder.create(AlertActivity.this)
                        .addParentStack(EventInfoActivity.class)
                        .addNextIntent(eventIntent).startActivities();
            } else {
                alertActivity.startActivity(eventIntent);
            }

            alertActivity.finish();
        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        setContentView(R.layout.alert_activity);
        setTitle(R.string.alert_title);

        mQueryHandler = new QueryHandler(this);
        mAdapter = new AlertAdapter(this, R.layout.hct_alert_item);

        mListView = (ListView) findViewById(R.id.alert_container);
        mListView.setItemsCanFocus(true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mViewListener);

        mSnoozeAllButton = (Button) findViewById(R.id.snooze_all);
        mSnoozeAllButton.setOnClickListener(this);
        mDismissAllButton = (Button) findViewById(R.id.dismiss_all);
        mDismissAllButton.setOnClickListener(this);

        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        setIndicatorColor(getResources().getColor(
                R.color.cale_actionbar_background_color));
        setActionBarContentColor(
                getResources().getColor(R.color.cale_actionbar_icon_color),
                getResources().getColor(R.color.cale_actionbar_text_color));

        getActionBar().setDisplayShowHomeEnabled(false);

        // Disable the buttons, since they need mCursor, which is created
        // asynchronously
        mSnoozeAllButton.setEnabled(false);
        mDismissAllButton.setEnabled(false);

        // pauseStopAlert = false;
        mTelManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        playVoiceWhenCall();

        mAlertCalss = new ClassToPlayAlert(this);
    }

    private void playVoiceWhenCall() {
        if (mTelManager.getCallState() != TelephonyManager.CALL_STATE_IDLE) {
            mToneGenerator = new ToneGenerator(AudioManager.STREAM_DTMF, 100);
            mToneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the cursor is null, start the async handler. If it is not null
        // just requery.
        if (mCursor == null) {
            Uri uri = CalendarAlerts.CONTENT_URI_BY_INSTANCE;
            mQueryHandler.startQuery(0, null, uri, PROJECTION, SELECTION,
                    SELECTIONARG,
                    CalendarContract.CalendarAlerts.DEFAULT_SORT_ORDER);
        } else {
            if (!mCursor.requery()) {
                Log.w(TAG, "Cursor#requery() failed.");
                mCursor.close();
                mCursor = null;
            }
        }
        mAlertCalss.requestFocus();
    }

    void closeActivityIfEmpty() {
        if (mCursor != null && !mCursor.isClosed() && mCursor.getCount() == 0) {
            AlertActivity.this.finish();
        }
    }

    @Override
    protected void onPause() {
        /**
         * if(pauseStopAlert){ AlertService.alertClassStopAlert();
         * pauseStopAlert = false; } else { pauseStopAlert = true; }
         */
        if (mAlertCalss != null) {
            mAlertCalss.pauseAlert();
        }
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop doUpdateAlertNotification = "
                + doUpdateAlertNotification);
        // AlertService.alertClassStopAlert();
        // if(mAlertCalss != null){
        // mAlertCalss.pauseAlert();
        // }
        super.onStop();
        // Can't run updateAlertNotification in main thread
        /**
         * AsyncTask task = new AsyncTask<Context, Void, Void>() {
         * 
         * @Override protected Void doInBackground(Context ... params) {
         *           AlertService.updateAlertNotification(params[0]); return
         *           null; } }.execute(this);
         */
        if (doUpdateAlertNotification) {
            AlertService.updateAlertNotification(this);
        }

        if (mCursor != null) {
            mCursor.deactivate();
        }
    }

    @Override
    protected void onDestroy() {
        // AlertService.alertClassStopAlert();
        if (mAlertCalss != null) {
            mAlertCalss.stopAlert();
        }
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
        }
    }

    @Override
    public void onClick(View v) {
        // AlertService.alertClassStopAlert();
        if (mAlertCalss != null) {
            mAlertCalss.stopAlert();
        }
        if (v == mSnoozeAllButton) {
            long alarmTime = System.currentTimeMillis() + SNOOZE_DELAY;

            // NotificationManager nm =
            // (NotificationManager)
            // getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationMgr nm = new NotificationMgrWrapper(this,
                    (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE));
            // nm.cancel(NOTIFICATION_ID);
            nm.cancelAll();
            if (mCursor != null) {
                long scheduleAlarmTime = 0;
                mCursor.moveToPosition(-1);
                while (mCursor.moveToNext()) {
                    long eventId = mCursor.getLong(INDEX_EVENT_ID);
                    long begin = mCursor.getLong(INDEX_BEGIN);
                    long end = mCursor.getLong(INDEX_END);

                    // Set the "minutes" to zero to indicate this is a snoozed
                    // alarm. There is code in AlertService.java that checks
                    // this field.
                    ContentValues values = makeContentValues(eventId, begin,
                            end, alarmTime, 0 /* minutes */);

                    // Create a new alarm entry in the CalendarAlerts table
                    if (mCursor.isLast()) {
                        scheduleAlarmTime = alarmTime;
                    }
                    mQueryHandler.startInsert(0, scheduleAlarmTime,
                            CalendarAlerts.CONTENT_URI, values,
                            Utils.UNDO_DELAY);
                }
            } else {
                Log.d(TAG, "Cursor object is null. Ignore the Snooze request.");
            }

            dismissFiredAlarms();
            finish();
        } else if (v == mDismissAllButton) {
            // NotificationManager nm =
            // (NotificationManager)
            // getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationMgr nm = new NotificationMgrWrapper(this,
                    (NotificationManager) this
                            .getSystemService(Context.NOTIFICATION_SERVICE));
            nm.cancelAll();

            dismissFiredAlarms();
            finish();
        } else {
            Log.e(TAG, " view = " + v.toString());
        }
    }

    private static ContentValues makeContentValues(long eventId, long begin,
            long end, long alarmTime, int minutes) {
        ContentValues values = new ContentValues();
        values.put(CalendarAlerts.EVENT_ID, eventId);
        values.put(CalendarAlerts.BEGIN, begin);
        values.put(CalendarAlerts.END, end);
        values.put(CalendarAlerts.ALARM_TIME, alarmTime);
        long currentTime = System.currentTimeMillis();
        values.put(CalendarAlerts.CREATION_TIME, currentTime);
        values.put(CalendarAlerts.RECEIVED_TIME, 0);
        values.put(CalendarAlerts.NOTIFY_TIME, 0);
        values.put(CalendarAlerts.STATE, CalendarAlerts.STATE_SCHEDULED);
        values.put(CalendarAlerts.MINUTES, minutes);
        return values;
    }

    public boolean isEmpty() {
        return mCursor != null ? (mCursor.getCount() == 0) : true;
    }

    public Cursor getItemForView(View view) {
        final int index = mListView.getPositionForView(view);
        if (index < 0) {
            return null;
        }
        return (Cursor) mListView.getAdapter().getItem(index);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult,requestCode:" + requestCode);
        switch (requestCode) {
        case Utils.REQUEST_PERMISSIONS_CALLLOG:
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mAdapter != null) {
                    }
                }
            }
            break;
        default:
            super.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
            break;
        }
    }

}
