package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calendar.R;
import com.android.calendar.alerts.ClassToPlayAlert;
import com.apkfuns.logutils.LogUtils;

public class EventNotifyActivity extends Activity {

    private View firstLayout;
    private View secondLayout;
    private long eventId;
    private long StartTime;
    private long endTime;
    private String title;
    private TextView tv_event;
    private TextView tv_time;
    private boolean allDay;
    private long initDelay = 0;
    private static ClassToPlayAlert mAlertCalss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_notify_layout);

        if (null == mAlertCalss) {
            mAlertCalss = new ClassToPlayAlert(EventNotifyActivity.this);
        }
        registerReceiver(mHomeKeyEventReceiver, new IntentFilter(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        eventId = getIntent().getBundleExtra("data").getLong("eventId", 0);
        StartTime = getIntent().getBundleExtra("data").getLong("StartTime", 0);
        endTime = getIntent().getBundleExtra("data").getLong("endTime", 0);
        title = getIntent().getBundleExtra("data").getString("title", "");
        allDay = getIntent().getBundleExtra("data").getBoolean("allDay");
        Log.e("fushuo", "onCreate--->" + eventId + "---》title=" + title);
        firstLayout = findViewById(R.id.notify_super);
        secondLayout = findViewById(R.id.notify_super_later);
        firstLayout.setVisibility(View.VISIBLE);
        secondLayout.setVisibility(View.GONE);
        getInitDelay();
        initFirstLayout();
        initSecondLayout();
        try {
            getActionBar().hide();
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    private void getInitDelay() {
        // TODO Auto-generated method stub
        Cursor mCursor = getContentResolver().query(Events.CONTENT_URI,
                new String[] { Events.UID_2445 }, "" + Events._ID + "=?",
                new String[] { "" + eventId }, null);
        if (null != mCursor && mCursor.moveToFirst()) {
            initDelay = mCursor
                    .getLong(mCursor.getColumnIndex(Events.UID_2445));
        }
    }

    public boolean updateAlarmTime(int minute) {
        long alarmTime = StartTime + minute * 60 * 1000;
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, alarmTime);
        values.put(Events.DTEND, endTime + minute * 60 * 1000);
        // insert delay time
        values.put(Events.UID_2445, "" + (minute * 60 * 1000 + initDelay));
        int count = getContentResolver().update(Events.CONTENT_URI, values,
                "" + Events._ID + "=?", new String[] { "" + eventId });
        // long alarmTime = StartTime + minute * 60 * 1000;
        // ContentValues values=new ContentValues();
        // values.put(Events.TITLE,title);
        // values.put(Events.CALENDAR_ID, 1);
        // values.put(Events.DTSTART, alarmTime);
        // values.put(Events.DTEND, endTime+minute*60*1000);
        // values.put(Events.HAS_ALARM, 1);
        // values.put(Events.DIRTY, 1);
        // values.put(Events.LAST_DATE, endTime+minute*60*1000);
        // values.put(Events.EVENT_TIMEZONE,
        // TimeZone.getDefault().getID().toString());
        // Uri uri=getContentResolver().insert(Events.CONTENT_URI, values);
        // if(uri!=null){
        // return deleteOriginEvent();
        // }
        Log.e("fushuo", "count=" + count);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private void initSecondLayout() {
        secondLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // close self
                // Toast.makeText(EventNotifyActivity.this, "close ......",
                // Toast.LENGTH_LONG).show();
                // updateAlarmStatus();
                // finish();
            }
        });
        findViewById(R.id.event_notify_later_tv).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(2);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.two_minute_alert),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        findViewById(R.id.notify_sub_later).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                    }
                });
        findViewById(R.id.event_later_5min).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(5);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.five_minute_alert),
                                Toast.LENGTH_LONG).show();
                        // updateAlarmTime(5);
                        finish();
                    }
                });
        findViewById(R.id.event_later_10min).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(10);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.ten_minute_alert),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        findViewById(R.id.event_later_30min).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(30);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.thirty_minute_alert),
                                Toast.LENGTH_LONG).show();
                        ;
                        finish();
                    }
                });
        findViewById(R.id.event_later_1hour).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(60);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.one_hour_alert),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
        findViewById(R.id.event_later_2hour).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updateAlarmTime(120);
                        Toast.makeText(
                                EventNotifyActivity.this,
                                EventNotifyActivity.this
                                        .getString(R.string.two_hour_alert),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

    }

    private void initFirstLayout() {
        tv_event = (TextView) findViewById(R.id.event_notify_desc_tv);
        tv_time = (TextView) findViewById(R.id.event_notify_time_tv);
        tv_event.setText(this.getString(R.string.alert_event) + title);
        String time[] = getEventTime();
        tv_time.setText(time[0] + "-" + time[1]);
        if (allDay) {
            tv_event.setVisibility(View.GONE);
        }
        firstLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // close self
                // updateAlarmStatus();
                // finish();
            }
        });
        findViewById(R.id.notify_sub).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do nothing !
                        // updateAlarmStatus();
                    }
                });
        findViewById(R.id.notify_sub_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // do something and close
                        // Toast.makeText(getApplicationContext(), "do something
                        // and
                        // close ....", Toast.LENGTH_SHORT).show();
                        updateAlarmStatus();
                        finish();
                    }
                });
        findViewById(R.id.notify_cancel_button).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        firstLayout.setVisibility(View.GONE);
                        secondLayout.setVisibility(View.VISIBLE);
                    }
                });
    }

    protected void updateAlarmStatus() {
        // TODO Auto-generated method stub
        ContentValues values = new ContentValues();
        values.put(Events.HAS_ALARM, 0);
        getContentResolver().update(Events.CONTENT_URI, values,
                "" + Events._ID + "=?", new String[] { "" + eventId });

    }

    private String[] getEventTime() {
        // TODO Auto-generated method stub
        String time[] = new String[2];
        Cursor mCursor = getContentResolver().query(Events.CONTENT_URI,
                new String[] { Events.DTSTART, Events.DTEND, Events.UID_2445 },
                "" + Events._ID + "=?", new String[] { "" + eventId }, null);
        if (null != mCursor && mCursor.moveToFirst()) {
            long delayTime = 0;
            long dtStart = mCursor.getLong(mCursor
                    .getColumnIndex(Events.DTSTART));
            long dtEnd = mCursor.getLong(mCursor.getColumnIndex(Events.DTEND));
            delayTime = mCursor
                    .getLong(mCursor.getColumnIndex(Events.UID_2445));
            Date realStart = new Date(dtStart - delayTime);
            Date endStart = new Date(dtEnd - delayTime);
            SimpleDateFormat format = new SimpleDateFormat(
                    this.getString(R.string.date_format));
            time[0] = format.format(realStart);
            time[1] = format.format(endStart);
            return time;
        }
        return null;
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        // need stop ring change by zyp
        if (null != mAlertCalss) {
            mAlertCalss.stopAlert();
        }
        // need delete item for The notification panel change by zyp
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel((int) eventId);

        overridePendingTransition(0, R.anim.pop_push_down_out);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(mHomeKeyEventReceiver);
    }

    // add by zyp
    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";
        String SYSTEM_HOME_KEY_LONG = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                // need stop ring
                if (null != mAlertCalss) {
                    mAlertCalss.stopAlert();
                }
                // need delete item for The notification panel
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel((int) eventId);
            }
        }
    };
}
