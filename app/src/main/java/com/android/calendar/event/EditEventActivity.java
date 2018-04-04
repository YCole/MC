/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.android.calendar.event;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.calendar.AbstractCalendarActivity;
import com.android.calendar.AllInOneActivity;
import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarEventModel.ReminderEntry;
import com.android.calendar.utils.StatusBarUtils;
import com.apkfuns.logutils.LogUtils;
import gm.app.GomeAlertDialog;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.gome.gmtimepicker.util.DateFormatter;
import com.hct.calendar.ui.SelectorGroup;
import com.hct.calendar.ui.SelectorGroup.CheckStatedChanged;
import com.hct.calendar.ui.SelectorGroup.Checked;
import com.hct.calendar.utils.ToastHelper;

import android.Manifest;
import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * 
 * @author
 */
public class EditEventActivity extends AbstractCalendarActivity {
    private static final String TAG = "EditEventActivity";

    private static final String CHECKED_FRAGMENGT_KEY = "checked fragment";

    private static final boolean DEBUG = false;

    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";

    public static final String EXTRA_EVENT_COLOR = "event_color";

    public static final String EXTRA_EVENT_REMINDERS = "reminders";

    private static boolean mIsMultipane;

    // private EditEventFragment mEditFragment;

    private ArrayList<ReminderEntry> mReminders;

    private int mEventColor;

    private boolean mEventColorInitialized;

    private EventInfo mEventInfo;

    private String eventColor;
    private static final String MY_CALENDAR = "My calendar";

    private View cancelCreate;

    private View finishCreate;

    private TextView tvTitle;

    private SelectorGroup selector;

    private int eventId;

    boolean editEvent = false;

    private ScheduleFragment left;

    private BirthdayFragment right;

    private MeetingFragment middle;

    public void addMycalendar() {
        Cursor cursor = null;
        if (Utils.checkSelfPermission(this.getApplicationContext(), Manifest.permission.READ_CALENDAR)) {
            cursor = getContentResolver().query(Calendars.CONTENT_URI, new String[] { Calendars._ID }, null, null,
                    null);
        }

        if (cursor == null) {
            return;
        } else if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, MY_CALENDAR);
        values.put(Calendars.ACCOUNT_TYPE, MY_CALENDAR);
        values.put(Calendars.OWNER_ACCOUNT, MY_CALENDAR);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, MY_CALENDAR);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        int color = getResources().getColor(R.color.my_calendar_color);
        values.put(Calendars.CALENDAR_COLOR, color); // -14069085
        values.put(Calendars.CALENDAR_TIME_ZONE, Time.getCurrentTimezone());
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        getContentResolver().insert(Calendars.CONTENT_URI, values);
        cursor.close();
    }

    public boolean getEditEvent() {
        return editEvent;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        LogUtils.e("activity---------- edit ");
        setContentView(R.layout.simple_frame_layout);
        // StatusBarUtils.setStatusBarLightMode(this,
        // Color.parseColor("#f1f2f3"));
        addMycalendar();
        mEventInfo = getEventInfoFromIntent(icicle);
        mReminders = getReminderEntriesFromIntent();
        mEventColorInitialized = getIntent().hasExtra(EXTRA_EVENT_COLOR);
        mEventColor = getIntent().getIntExtra(Events.EVENT_COLOR, -1);
        eventColor = getIntent().getStringExtra(Events.EVENT_COLOR);
        eventId = getIntent().getIntExtra("id", -1);
        // if (eventColor == null || eventColor.equals("null")) {
        // mEventColor = -1;
        // } else {
        // mEventColor = Integer.parseInt(eventColor);
        // }
        Log.d(TAG, "lxg2 eventColor = " + eventColor + ",mEventColor =" + mEventColor);

        // mEditFragment = (EditEventFragment)
        // getFragmentManager().findFragmentById(R.id.main_frame);

        mIsMultipane = Utils.getConfigBool(this, R.bool.multiple_pane_config);

        getActionBar().hide();

        Intent intent = getIntent();
        // long beginTime = intent.getLongExtra("beginTime", 0);
        long beginTime = System.currentTimeMillis();
        editEvent = intent.getBooleanExtra("editEvent", false);

        LogUtils.e("beginT === " + beginTime);
        Bundle args = new Bundle();

        /* modify by Yusong.Liang for GMOS-6946 on 2017.9.14:start */
        // args.putLong("beginTime", beginTime);
        args.putLong("beginTime", mEventInfo.startTime.toMillis(false));
        /* modify by Yusong.Liang for GMOS-6946 on 2017.9.14:end */
        args.putInt("id", eventId);
        left = (ScheduleFragment) FragmentFactory.getFragmentByTag(this, ScheduleFragment.TAG);
        middle = (MeetingFragment) FragmentFactory.getFragmentByTag(this, MeetingFragment.TAG);
        right = (BirthdayFragment) FragmentFactory.getFragmentByTag(this, BirthdayFragment.TAG);
        left.setArguments(args);
        middle.setArguments(args);
        right.setArguments(args);
        cancelCreate = findViewById(R.id.event_create_cancel);
        cancelCreate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        finishCreate = findViewById(R.id.event_create_finish);
        finishCreate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Checked check = selector.getCurrentCheck();
                boolean isSuccess = false;
                switch (check) {
                case SCHEDULE:
                    long a = System.currentTimeMillis();
                    isSuccess = left.saveData(editEvent);
                    Log.d("2877", "" + (System.currentTimeMillis() - a));
                    break;
                case MEETING:
                    isSuccess = middle.saveData(editEvent);
                    break;
                case BIRTHDAY:
                    isSuccess = right.saveData(editEvent);
                    break;

                }
                if (isSuccess) {
                    setResult(RESULT_OK);
                    ToastHelper.show(EditEventActivity.this, EditEventActivity.this.getResources().getString(R.string.creating_event));
                    finish();
                } else {
                    finish();
                    ToastHelper.show(EditEventActivity.this,
                            EditEventActivity.this.getResources().getString(R.string.empty_event));
                }
            }
        });
        tvTitle = (TextView) findViewById(R.id.event_create_title);
        selector = (SelectorGroup) findViewById(R.id.event_create_selector);
        selector.setVisibility(editEvent ? View.GONE : View.VISIBLE);
        switch (mEventColor) {
        case Constant.SCHEDULE:
            selector.setCurrentCheck(Checked.SCHEDULE);
            tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_event));
            if (editEvent) {
                tvTitle.setText(EditEventActivity.this.getString(R.string.event_edit_event));
            }
            break;
        case Constant.MEETING:
            selector.setCurrentCheck(Checked.MEETING);
            tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_meeting));
            if (editEvent) {
                tvTitle.setText(EditEventActivity.this.getString(R.string.event_edit_meeting));
            }
            break;
        case Constant.BIRTHDAY:
            selector.setCurrentCheck(Checked.BIRTHDAY);
            tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_birth));
            if (editEvent) {
                tvTitle.setText(EditEventActivity.this.getString(R.string.event_edit_birth));
            }
            break;
        default:
            break;
        }
        Checked check = selector.getCurrentCheck();
        LogUtils.e("Checked = " + check);
        getFragmentManager().beginTransaction()// begin
                .add(R.id.main_frame, left)// add left
                .add(R.id.main_frame, middle) // add middle
                .add(R.id.main_frame, right)// add right
                .commit();
        selector.updateCheckState(check);
        selectFragment(left, right, middle, check);
        selector.setCheckStatedChangedListener(new CheckStatedChanged() {
            @Override
            public void onCheckChanged(Checked current) {
                selectFragment(left, right, middle, current);
                LogUtils.e("current = " + current);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(selector.getWindowToken(), 0);
                switch (current) {
                case SCHEDULE:
                default:
                    tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_event));
                    break;
                case MEETING:
                    tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_meeting));
                    break;
                case BIRTHDAY:
                    tvTitle.setText(EditEventActivity.this.getString(R.string.event_create_birth));
                    break;
                }
            }
        });

    }

    @SuppressWarnings("unchecked")
    private ArrayList<ReminderEntry> getReminderEntriesFromIntent() {
        Intent intent = getIntent();
        return (ArrayList<ReminderEntry>) intent.getSerializableExtra(EXTRA_EVENT_REMINDERS);
    }

    // /**
    // * HCT_MODIFY start add LBS lixiange MF3.0
    // *********************************************/
    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    // if (requestCode == 100 && data != null) {
    // mEditFragment.onAcitivityResult(data);
    // }
    // if (requestCode == 0 && data != null) {
    // mEditFragment.onAcitivityResultLocation(data);
    // }
    // mEditFragment.onActivityResult(requestCode, resultCode, data);
    // }
    //
    // /**
    // * HCT_MODIFY start add LBS lixiange MF3.0
    // *********************************************/
    //
    // @Override
    // public void onBackPressed() {
    // if (mEditFragment != null) {
    // mEditFragment.onBackPressed();
    // }
    // super.onBackPressed();
    // }

    // @Override
    // public boolean onKeyDown(int keycode, KeyEvent event) {
    // if (keycode == KeyEvent.KEYCODE_BACK) {
    // if (mEditFragment != null && !mEditFragment.isViewEmpty()) {
    // mEditFragment.onBackPressed();
    // } else {
    // finish();
    // }
    // return true;
    // }
    // return false;
    // }

    private EventInfo getEventInfoFromIntent(Bundle icicle) {
        EventInfo info = new EventInfo();
        long eventId = -1;
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            try {
                eventId = Long.parseLong(data.getLastPathSegment());
            } catch (NumberFormatException e) {
                if (DEBUG) {
                    Log.d(TAG, "Create new event");
                }
            }
        } else if (icicle != null && icicle.containsKey(BUNDLE_KEY_EVENT_ID)) {
            eventId = icicle.getLong(BUNDLE_KEY_EVENT_ID);
        }

        boolean allDay = intent.getBooleanExtra(EXTRA_EVENT_ALL_DAY, false);

        long begin = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, -1L);
        Log.d(TAG, "EditEventActivity begin : " + begin + ", eventId :" + eventId + ", taskId : " + this.getTaskId());
        if (begin == -1) {
            begin = getEditEventTime();
        }
        long end = intent.getLongExtra(EXTRA_EVENT_END_TIME, -1);
        if (end != -1) {
            info.endTime = new Time();
            if (allDay) {
                info.endTime.timezone = Time.TIMEZONE_UTC;
            }
            info.endTime.set(end);
        }
        if (begin != -1) {
            info.startTime = new Time();
            if (allDay) {
                info.startTime.timezone = Time.TIMEZONE_UTC;
            }
            info.startTime.set(begin);
        }
        info.id = eventId;
        info.eventTitle = intent.getStringExtra(Events.TITLE);
        String action = intent.getAction();
        if (action != null && action.equals("com.android.calendar.copyAndNew")) {
            info.id = -1;
        }
        // info.calendarId = intent.getLongExtra(Events.CALENDAR_ID, -1);

        if (allDay) {
            info.extraLong = CalendarController.EXTRA_CREATE_ALL_DAY;
        } else {
            info.extraLong = 0;
        }
        return info;
    }

    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // if (item.getItemId() == android.R.id.home) {
    // Utils.returnToCalendarHome(this);
    // this.finish();
    // return true;
    // } else if (item.getItemId() == R.id.action_done_event || item.getItemId()
    // == R.id.action_cancel_event) {
    // if (mEditFragment != null)
    // mEditFragment.onOptionsItemSelected(item);
    // }
    // return super.onOptionsItemSelected(item);
    // }

    private long getEditEventTime() {
        Time t = new Time();
        t.setToNow();
        if (t.minute >= 30) {
            t.hour++;
            t.minute = 0;
        } else if (t.minute >= 0 && t.minute < 30) {
            t.minute = 30;
        }
        return t.toMillis(true);
    }

    // @Override
    // public void onRequestPermissionsResult(int requestCode, String[]
    // permissions, int[] grantResults) {
    // Log.d(TAG, "onRequestPermissionsResult,requestCode:" + requestCode);
    // switch (requestCode) {
    // case Utils.REQUEST_PERMISSIONS_CONTACTS_ADD:
    // if (grantResults != null && grantResults.length > 0) {
    // if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // if (mEditFragment != null && mEditFragment.mView != null) {
    // mEditFragment.mView.setContactsGrant(true);
    // mEditFragment.mView.pickAttendeesFromContacts();
    // }
    // }
    // }
    // break;
    // case Utils.REQUEST_PERMISSIONS_CONTACTS_EDIT:
    // if (grantResults != null && grantResults.length > 0) {
    // if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // if (mEditFragment != null && mEditFragment.mView != null) {
    // mEditFragment.mView.setContactsGrant(true);
    // mEditFragment.mView.setMoreEventVisibility(true);
    // }
    // }
    // }
    // break;
    // case Utils.REQUEST_PERMISSIONS_CALLLOG:
    // if (grantResults != null && grantResults.length > 0) {
    // if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // if (mEditFragment != null && mEditFragment.mView != null) {
    // mEditFragment.mView.setCallLogGrant(true);
    // mEditFragment.mView.pickAttendeesFromContacts();
    // }
    // }
    // }
    // break;
    // case Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE:
    // if (grantResults != null && grantResults.length > 0) {
    // if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // if (mEditFragment != null && mEditFragment.mView != null) {
    // mEditFragment.mView.setSaveMap(this);
    // }
    // }
    // }
    // break;
    // default:
    // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // break;
    // }
    // }

    private void selectFragment(final Fragment left, final Fragment right, final Fragment middle, Checked current) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        switch (current) {
        case SCHEDULE:
        default:
            ft.show(left).hide(middle).hide(right).commit();
            break;
        case MEETING:
            ft.hide(left).show(middle).hide(right).commit();
            break;
        case BIRTHDAY:
            ft.hide(left).hide(middle).show(right).commit();
            break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Checked check = selector != null ? selector.getCurrentCheck() : Checked.SCHEDULE;
        outState.putSerializable(CHECKED_FRAGMENGT_KEY, check);
    }

    @Override
    protected void onRestoreInstanceState(Bundle arg0) {
        super.onRestoreInstanceState(arg0);
        Checked check = (Checked) arg0.getSerializable(CHECKED_FRAGMENGT_KEY);
        check = check == null ? Checked.SCHEDULE : check;
        selector.setCurrentCheck(check);
        selector.updateCheckState(check);
        selectFragment(left, right, middle, check);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DateFormatter.clearCache();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (eventId == -1) {
            return;
        }
        Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, "_id=?",
                new String[] { "" + eventId }, null);

        if (!(cursor != null && cursor.moveToFirst())) {
            finish();
        }

    }
}
