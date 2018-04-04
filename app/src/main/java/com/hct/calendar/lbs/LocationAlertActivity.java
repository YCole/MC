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

package com.hct.calendar.lbs;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
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
import com.android.calendar.alerts.AlertUtils;
import com.android.calendar.alerts.ClassToPlayAlert;
import com.hct.gios.widget.ActivityHCT;

/**
 * The alert panel that pops up when there is a calendar event alarm. This
 * activity is started by an intent that specifies an event id.
 */
public class LocationAlertActivity extends ActivityHCT implements
        OnClickListener {
    private static final String TAG = "LocationAlertActivity";

    private boolean doUpdateAlertNotification = false;

    // add by mf4.0
    public static final String EVENTS_EXT_ACTION_DATA = "action_data";
    public static final String EVENTS_EXT_ACTION_DATA1 = "action_data1";
    public static final String EVENTS_EXT_ACTION_DATA2 = "action_data2";
    public static final String EVENTS_EXT_EVENT_TYPE = "eventType";
    private static final String[] PROJECTION = new String[] {
            CalendarContract.Events._ID, // 0
            CalendarContract.Events.TITLE, // 1
            CalendarContract.Events.EVENT_LOCATION, // 2
            EVENTS_EXT_ACTION_DATA, // 3
            EVENTS_EXT_ACTION_DATA1,// 4
            EVENTS_EXT_ACTION_DATA2,// 5
            EVENTS_EXT_EVENT_TYPE,// 6
            CalendarContract.Events.EVENT_COLOR, };

    public static final int INDEX_ROW_ID = 0;
    public static final int INDEX_TITLE = 1;
    public static final int INDEX_EVENT_LOCATION = 2;
    public static final int INDEX_EXT_ACTION_DATA = 3;
    public static final int INDEX_EXT_ACTION_DATA1 = 4;
    public static final int INDEX_EXT_ACTION_DATA2 = 5;
    public static final int INDEX_EXT_EVENT_TYPE = 6;
    public static final int INDEX_COLOR = 7;

    private static final String SELECTION = EVENTS_EXT_EVENT_TYPE + "=1 and "
            + EVENTS_EXT_ACTION_DATA2 + "=?";

    private LocationAlertAdapter mAdapter;
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

    private void dismissFiredAlarms() {

    }

    private void dismissAlarm(long eventId, long fenceId) {
        ContentValues values = new ContentValues(1 /* size */);
        values.put(PROJECTION[INDEX_EXT_ACTION_DATA2], "");
        String selection = PROJECTION[INDEX_EXT_ACTION_DATA2] + "= ? ";
        mQueryHandler.startUpdate(0, null, CalendarContract.Events.CONTENT_URI,
                values, selection,
                new String[] { String.valueOf(fenceId) } /* selectionArgs */,
                Utils.UNDO_DELAY);
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

            }
        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
            // Ignore
            doUpdateAlertNotification = true;
            LbsServiceHelper.deleteLocationFence(getApplicationContext(),
                    getFenceIdFromIntent(), -1);
        }
    }

    public static void scheduleAlarm(Context context, AlarmManager manager,
            long alarmTime) {

    }

    private final OnItemClickListener mViewListener = new OnItemClickListener() {

        @SuppressLint("NewApi")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long i) {
            LocationAlertActivity alertActivity = LocationAlertActivity.this;
            Cursor cursor = alertActivity.getItemForView(view);

            long eventId = cursor.getLong(LocationAlertActivity.INDEX_ROW_ID);
            Intent eventIntent = AlertUtils.buildEventViewIntent(
                    LocationAlertActivity.this, eventId, 0, 0);
            if (Utils.isJellybeanOrLater()) {
                TaskStackBuilder.create(LocationAlertActivity.this)
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
        mAdapter = new LocationAlertAdapter(this, R.layout.hct_alert_item);

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
        mSnoozeAllButton.setText(R.string.location_alert_next_reminder_label);
        mDismissAllButton.setText(R.string.location_alert_got_it_label);
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

    private int getFenceIdFromIntent() {
        int fenceId = getIntent().getIntExtra("fenceId", -1);
        Log.v(TAG, "getFenceIdFromIntent fenceId #" + fenceId);
        return fenceId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the cursor is null, start the async handler. If it is not null
        // just requery.
        if (mCursor == null) {
            Uri uri = CalendarContract.Events.CONTENT_URI;
            mQueryHandler.startQuery(0, null, uri, PROJECTION, SELECTION,
                    new String[] { String.valueOf(getFenceIdFromIntent()) },
                    " _id asc ");
        } else {
            if (!mCursor.requery()) {
                Log.w(TAG, "Cursor#requery() failed.");
                mCursor.close();
                mCursor = null;
            }
        }
        // mAlertCalss.requestFocus();
    }

    void closeActivityIfEmpty() {
        if (mCursor != null && !mCursor.isClosed() && mCursor.getCount() == 0) {
            LocationAlertActivity.this.finish();
        }
    }

    @Override
    protected void onPause() {
        if (mAlertCalss != null) {
            mAlertCalss.pauseAlert();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop doUpdateAlertNotification = "
                + doUpdateAlertNotification);
        super.onStop();
        if (doUpdateAlertNotification) {
            // AlertService.updateAlertNotification(this);
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
        if (mAlertCalss != null) {
            mAlertCalss.stopAlert();
        }
        if (v == mSnoozeAllButton) {
            finish();
        } else if (v == mDismissAllButton) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancelAll();

            dismissAlarm(-1, getFenceIdFromIntent());

            finish();
        }
    }

    private static ContentValues makeContentValues(long eventId, long begin,
            long end, long alarmTime, int minutes) {
        ContentValues values = new ContentValues();
        values.put(CalendarAlerts.EVENT_ID, eventId);
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
}
