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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.NotificationCompat;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.TextAppearanceSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.alerts.AlertService.NotificationWrapper;
import com.android.calendar.chinesefestivalalert.basicfounction;
import com.android.calendar.event.ScheduleDetailsActivity;

/**
 * Receives android.intent.action.EVENT_REMINDER intents and handles event
 * reminders. The intent URI specifies an alert id in the CalendarAlerts
 * database table. This class also receives the BOOT_COMPLETED intent so that it
 * can add a status bar notification if there are Calendar event alarms that
 * have not been dismissed. It also receives the TIME_CHANGED action so that it
 * can fire off snoozed alarms that have become ready. The real work is done in
 * the AlertService class.
 * 
 * To trigger this code after pushing the apk to device: adb shell am broadcast
 * -a "android.intent.action.EVENT_REMINDER" -n
 * "com.android.calendar/.alerts.AlertReceiver"
 */
public class AlertReceiver extends BroadcastReceiver {
    private static final String TAG = "AlertReceiver";

    private static final String MAP_ACTION = "com.android.calendar.MAP";
    private static final String CALL_ACTION = "com.android.calendar.CALL";
    private static final String MAIL_ACTION = "com.android.calendar.MAIL";
    private static final String SEND_HOLIDAY_ACTION = "android.intent.action.SEND_HOLIDAY_TO_ALARM";
    private static final String EXTRA_EVENT_ID = "eventid";

    // The broadcast for notification refreshes scheduled by the app. This is to
    // distinguish the EVENT_REMINDER broadcast sent by the provider.
    public static final String EVENT_REMINDER_APP_ACTION = "com.android.calendar.EVENT_REMINDER_APP";

    static final Object mStartingServiceSync = new Object();
    static PowerManager.WakeLock mStartingService;
    private static final Pattern mBlankLinePattern = Pattern.compile(
            "^\\s*$[\n\r]", Pattern.MULTILINE);

    public static final String ACTION_DISMISS_OLD_REMINDERS = "removeOldReminders";
    private static final int NOTIFICATION_DIGEST_MAX_LENGTH = 3;

    private static final String GEO_PREFIX = "geo:";
    private static final String TEL_PREFIX = "tel:";
    private static final int MAX_NOTIF_ACTIONS = 3;
    private static final String DELETE_ACTION = "delete";

    private AlarmManager mAlarmManager;
    private Intent mNotificationIntent;
    private PendingIntent mNotificationSender;
    private Context mContext;
    private static ClassToPlayAlert mAlertCalss;

    private static Handler sAsyncHandler;
    static {
        HandlerThread thr = new HandlerThread("AlertReceiver async");
        thr.start();
        sAsyncHandler = new Handler(thr.getLooper());
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 1:
                setTimeAction(mContext, msg.arg1);
                break;
            }
        }
    };

    @Override
    public void onReceive(final Context context, final Intent intent) {
        mContext = context;
        if (AlertService.DEBUG) {
            Log.d(TAG,
                    "lxg onReceive: a=" + intent.getAction() + " "
                            + intent.toString());
        }

        Log.e("fushuo", "onReceive intent.getAction() :" + intent.getAction());

        Log.d(TAG, "zyp ------    :" + intent.getAction());

        if (MAP_ACTION.equals(intent.getAction())) {
            // Try starting the map action.
            // If no map location is found (something changed since the
            // notification was originally
            // fired), update the notifications to express this change.
            final long eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
            if (eventId != -1) {
                URLSpan[] urlSpans = getURLSpans(context, eventId);
                Intent geoIntent = createMapActivityIntent(context, urlSpans);
                if (geoIntent != null) {
                    // Location was successfully found, so dismiss the shade and
                    // start maps.
                    context.startActivity(geoIntent);
                    closeNotificationShade(context);
                } else {
                    // No location was found, so update all notifications.
                    // Our alert service does not currently allow us to specify
                    // only one
                    // specific notification to refresh.
                    AlertService.updateAlertNotification(context);
                }
            }
        } else if (CALL_ACTION.equals(intent.getAction())) {
            // Try starting the call action.
            // If no call location is found (something changed since the
            // notification was originally
            // fired), update the notifications to express this change.
            final long eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
            if (eventId != -1) {
                URLSpan[] urlSpans = getURLSpans(context, eventId);
                Intent callIntent = createCallActivityIntent(context, urlSpans);
                if (callIntent != null) {
                    // Call location was successfully found, so dismiss the
                    // shade and start dialer.
                    context.startActivity(callIntent);
                    closeNotificationShade(context);
                } else {
                    // No call location was found, so update all notifications.
                    // Our alert service does not currently allow us to specify
                    // only one
                    // specific notification to refresh.
                    AlertService.updateAlertNotification(context);
                }
            }
        } else if (MAIL_ACTION.equals(intent.getAction())) {
            closeNotificationShade(context);

            // Now start the email intent.
            final long eventId = intent.getLongExtra(EXTRA_EVENT_ID, -1);
            if (eventId != -1) {
                Intent i = new Intent(context, QuickResponseActivity.class);
                i.putExtra(QuickResponseActivity.EXTRA_EVENT_ID, eventId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } else if (SEND_HOLIDAY_ACTION.equals(intent.getAction())) {
            Log.d(TAG, "lxg SEND_HOLIDAY_TO_ALARM ");
            isSendChineseHolidayToAlarm(context);
            Message message = handler.obtainMessage();
            message.what = 1;
            message.arg1 = 1;
            handler.sendMessage(message);
        } else if (DELETE_ACTION.equals(intent.getAction())) {
            /*
             * The user has clicked the "Clear All Notifications" buttons so
             * dismiss all Calendar alerts.
             */
            long eventId = intent.getLongExtra(AlertUtils.EVENT_ID_KEY, -1);
            int notificationId = intent.getIntExtra(
                    AlertUtils.NOTIFICATION_ID_KEY, (int) eventId);
            Log.d(TAG, "DELETE_ACTION, id = " + eventId + ", notificationId= "
                    + notificationId);
            // TODO Grab a wake lock here?
            Intent serviceIntent = new Intent(context,
                    DismissAlarmsService.class);
            serviceIntent.setAction(DismissAlarmsService.DISMISS_ACTION);
            serviceIntent.putExtra(AlertUtils.EVENT_ID_KEY, eventId);
            serviceIntent.putExtra(AlertUtils.EVENT_START_KEY,
                    intent.getLongExtra(AlertUtils.EVENT_START_KEY, -1));
            serviceIntent.putExtra(AlertUtils.EVENT_END_KEY,
                    intent.getLongExtra(AlertUtils.EVENT_END_KEY, -1));
            serviceIntent.putExtra(AlertUtils.NOTIFICATION_ID_KEY,
                    notificationId);
            context.startService(serviceIntent);
            if (null == mAlertCalss) {
                mAlertCalss = new ClassToPlayAlert(context);
            }
            mAlertCalss.abandonFocus();
        } else if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent i = new Intent();
            i.setClass(context, AlertService.class);
            i.putExtras(intent);
            i.putExtra("action", intent.getAction());
            Uri uri = intent.getData();

            // This intent might be a BOOT_COMPLETED so it might not have a Uri.
            if (uri != null) {
                i.putExtra("uri", uri.toString());
            }
            beginStartingService(context, i);
            // hct_modify lixiange MF3.0
            // setTimeAction(context);
            Message message = handler.obtainMessage();
            message.what = 1;
            message.arg1 = 1;
            handler.sendMessage(message);
        } else if ((Intent.ACTION_TIME_CHANGED.equals(intent.getAction()))
                || (Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction()))) {
            Message message = handler.obtainMessage();
            message.what = 1;
            message.arg1 = 1;
            handler.sendMessage(message);
        } else {
            Log.i(TAG, "gengbin ,recevived event notification");
            Log.i(TAG, "zyp ,recevived event notification");
            Intent i = new Intent();
            i.setClass(context, AlertService.class);
            i.putExtras(intent);
            i.putExtra("action", intent.getAction());
            Uri uri = intent.getData();

            // This intent might be a BOOT_COMPLETED so it might not have a Uri.
            if (uri != null) {
                i.putExtra("uri", uri.toString());
            }
            beginStartingService(context, i);
        }
    }

    public void setTimeAction(Context context, int addNewTriggger) {
        Log.d(TAG, "lxg setTimeAction,addNewTriggger=" + addNewTriggger);
        mAlarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        mNotificationIntent = new Intent(SEND_HOLIDAY_ACTION, null);
        mNotificationSender = PendingIntent.getBroadcast(context, 0,
                mNotificationIntent, 0);

        Calendar c = Calendar.getInstance();
        Long curTimeMillis = System.currentTimeMillis();
        c.setTimeInMillis(curTimeMillis);
        int hour = 12;
        int minute = 12;
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long triggerMillis = c.getTimeInMillis();
        if ((addNewTriggger == 1)
                && (curTimeMillis + 30 * 1000 >= triggerMillis)) {
            triggerMillis += 24 * 60 * 60 * 1000;
        }
        Log.d(TAG, "lxg curTimeMillis = " + curTimeMillis + ",triggerMillis="
                + triggerMillis);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerMillis,
                mNotificationSender);
    }

    public void isSendChineseHolidayToAlarm(Context context) {
        Time mToday = new Time();
        // mToday.timezone = tz;
        mToday.setToNow();
        mToday.normalize(true);
        boolean isSendAlarm = false;
        if (AlertService.DEBUG) {
            Log.d(TAG, "lxg mToday1 = " + mToday.hour + "," + mToday.minute
                    + ",date = " + new Date().toLocaleString());
        }
        if (mToday.hour >= 8 && mToday.hour < 22) {
            isSendAlarm = true;
        } else {
            return;
        }
        int hour = 24;
        Time time = new Time(mToday);
        time.hour += hour;
        time.normalize(true);
        Resources res = context.getResources();
        String weekDay = res.getString(R.string.show_holiday);
        String workDay = res.getString(R.string.show_workday);
        String startDayTypeDay = basicfounction.workDayWeekDayFromSolarDate(
                res, mToday);
        int weekOrWorkDay = -1;
        int dayCount = 0;
        String repeatDayTypeName = "";
        for (int i = 0; i < 8; i++) {
            String workDayWeekDay = basicfounction.workDayWeekDayFromSolarDate(
                    res, time);
            if (AlertService.DEBUG) {
                Log.d(TAG, "lxg isSendChineseHolidayToAlarm time = " + time
                        + ",workDayWeekDay =" + workDayWeekDay
                        + ",startDayTypeDay =" + startDayTypeDay);
            }
            if (workDayWeekDay == null || workDayWeekDay.equals("")
                    || workDayWeekDay.equals(startDayTypeDay)) {
                break;
            } else {
                if (dayCount == 0) {
                    repeatDayTypeName = workDayWeekDay;
                    if (workDayWeekDay.equals(weekDay)) {
                        weekOrWorkDay = 0;
                    } else if (workDayWeekDay.equals(workDay)) {
                        weekOrWorkDay = 1;
                    }
                }
                if (workDayWeekDay.equals(repeatDayTypeName)) {
                    dayCount++;
                } else {
                    break;
                }
            }
            time.hour += hour;
            time.normalize(true);
        }
        if (AlertService.DEBUG) {
            Log.d(TAG, "lxg isSendChineseHolidayToAlarm ok weekOrWorkDay = "
                    + weekOrWorkDay + ",dayCount=" + dayCount);
        }
        if (dayCount != 0 && isSendAlarm) {
            Intent intent = new Intent(
                    "android.intent.action.SEND_REMINDER_TO_ALARMING");
            intent.putExtra("weekOrWorkDay", weekOrWorkDay);
            intent.putExtra("dayCount", dayCount);
            context.sendBroadcast(intent);
            Log.d(TAG, "lxg: send success date=" + new Date().toLocaleString());
        }
    }

    /**
     * Start the service to process the current event notifications, acquiring
     * the wake lock before returning to ensure that the service will run.
     */
    public static void beginStartingService(Context context, Intent intent) {
        synchronized (mStartingServiceSync) {
            PowerManager pm = (PowerManager) context
                    .getSystemService(Context.POWER_SERVICE);
            int flag = PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.SCREEN_DIM_WAKE_LOCK;
            String action = intent.getStringExtra("action");
            boolean isBoot = Intent.ACTION_BOOT_COMPLETED.equals(action)
                    || EVENT_REMINDER_APP_ACTION.equals(action);
            if (isBoot) {
                if (mStartingService != null) {
                    mStartingService.release();
                    mStartingService = null;
                }
            }
            if (mStartingService == null) {
                // mStartingService = pm.newWakeLock(
                // PowerManager.PARTIAL_WAKE_LOCK, "StartingAlertService");
                mStartingService = pm.newWakeLock(flag, "StartingAlertService");
                mStartingService.setReferenceCounted(false);
            }
            if (AlertService.DEBUG) {
                Log.d(TAG, "lxg flag = " + flag + ", isBoot = " + isBoot);
            }
            // mStartingService.acquire();
            // mStartingService.acquire(5000); // we do not should weakLock in
            // this method !!! pythonCat say!
            context.startService(intent);
        }
    }

    /**
     * Called back by the service when it has finished processing notifications,
     * releasing the wake lock if the service is now stopping.
     */
    public static void finishStartingService(Service service, int startId) {
        synchronized (mStartingServiceSync) {
            if (mStartingService != null) {
                if (service.stopSelfResult(startId)) {
                    // mStartingService.release();
                }
            }
        }
    }

    private static PendingIntent createClickEventIntent(Context context,
            long eventId, long startMillis, long endMillis, int notificationId) {
        Log.d(TAG, "lxg --createClickEventIntent");
        return createDismissAlarmsIntent(context, eventId, startMillis,
                endMillis, notificationId, DismissAlarmsService.SHOW_ACTION);
    }

    private static PendingIntent createDeleteEventIntent(Context context,
            long eventId, long startMillis, long endMillis, int notificationId) {
        return createDismissAlarmsIntent(context, eventId, startMillis,
                endMillis, notificationId, DismissAlarmsService.DISMISS_ACTION);
    }

    private static PendingIntent createDismissAlarmsIntent(Context context,
            long eventId, long startMillis, long endMillis, int notificationId,
            String action) {
        Intent intent = new Intent();
        intent.setClass(context, DismissAlarmsService.class);
        intent.setAction(action);
        intent.putExtra(AlertUtils.EVENT_ID_KEY, eventId);
        intent.putExtra(AlertUtils.EVENT_START_KEY, startMillis);
        intent.putExtra(AlertUtils.EVENT_END_KEY, endMillis);
        intent.putExtra(AlertUtils.NOTIFICATION_ID_KEY, notificationId);

        // Must set a field that affects Intent.filterEquals so that the
        // resulting
        // PendingIntent will be a unique instance (the 'extras' don't achieve
        // this).
        // This must be unique for the click event across all reminders (so
        // using
        // event ID + startTime should be unique). This also must be unique from
        // the delete event (which also uses DismissAlarmsService).
        Uri.Builder builder = Events.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, eventId);
        ContentUris.appendId(builder, startMillis);
        intent.setData(builder.build());
        return PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createSnoozeIntent(Context context,
            long eventId, long startMillis, long endMillis, int notificationId) {
        Intent intent = new Intent();
        intent.setClass(context, SnoozeAlarmsService.class);
        intent.putExtra(AlertUtils.EVENT_ID_KEY, eventId);
        intent.putExtra(AlertUtils.EVENT_START_KEY, startMillis);
        intent.putExtra(AlertUtils.EVENT_END_KEY, endMillis);
        intent.putExtra(AlertUtils.NOTIFICATION_ID_KEY, notificationId);

        Uri.Builder builder = Events.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, eventId);
        ContentUris.appendId(builder, startMillis);
        intent.setData(builder.build());
        return PendingIntent.getService(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent createAlertActivityIntent(Context context) {
        Intent clickIntent = new Intent();
        clickIntent.setClass(context, AlertActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent
                .getActivity(context, 0, clickIntent,
                        PendingIntent.FLAG_ONE_SHOT
                                | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static NotificationWrapper makeBasicNotification(Context context,
            String title, String summaryText, long startMillis, long endMillis,
            long eventId, int notificationId, boolean doPopup, int priority) {

        Notification n = buildBasicNotification(new Notification.Builder(
                context), context, title, summaryText, startMillis, endMillis,
                eventId, notificationId, doPopup, priority, false, false);
        return new NotificationWrapper(n, notificationId, eventId, startMillis,
                endMillis, doPopup);
    }

    private static Notification buildBasicNotification(
            Notification.Builder notificationBuilder, Context context,
            String title, String summaryText, long startMillis, long endMillis,
            long eventId, int notificationId, boolean doPopup, int priority,
            boolean addActionButtons, boolean allDay) {
        Log.d(TAG, "lxg --buildBasicNotification, evnetId= " + eventId
                + ", notificationId= " + notificationId);
        Resources resources = context.getResources();
        if (title == null || title.length() == 0) {
            title = resources.getString(R.string.no_title_label);
        }

        // Create an intent triggered by clicking on the status icon, that
        // dismisses the
        // notification and shows the event.
        // PendingIntent clickIntent = createClickEventIntent(context, eventId,
        // startMillis,
        // endMillis, notificationId);

        // Create a delete intent triggered by dismissing the notification.
        // PendingIntent deleteIntent = createDeleteEventIntent(context,
        // eventId, startMillis,
        // endMillis, notificationId);
        Log.d("TAG", "lzb --buildBasicNotification, evnetId= " + eventId
                + ", notificationId= " + notificationId);
        Intent clickIntent = new Intent();
        clickIntent.putExtra("notification", true);
        clickIntent.putExtra("id", (int) eventId);
        clickIntent.setClass(context, ScheduleDetailsActivity.class);// liao
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Create an intent triggered by clicking on the
        // "Clear All Notifications" button
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, AlertReceiver.class);
        deleteIntent.setAction(DELETE_ACTION);
        deleteIntent.putExtra(AlertUtils.EVENT_ID_KEY, eventId);
        deleteIntent.putExtra(AlertUtils.EVENT_START_KEY, startMillis);
        deleteIntent.putExtra(AlertUtils.EVENT_END_KEY, endMillis);
        deleteIntent.putExtra(AlertUtils.NOTIFICATION_ID_KEY, notificationId);

        // add by zyp start
        // RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
        // R.layout.top_popup_notification);
        // click i konw
        Intent intentIKonw = new Intent(
                "com.calendar.BRODCAST_NOTIFICATION_I_KNOW");
        intentIKonw.putExtra("startTime", startMillis);
        intentIKonw.putExtra("endTime", endMillis);
        intentIKonw.putExtra("allDay", allDay);
        intentIKonw.putExtra("eventId", eventId);
        // click after five minute
        Intent intentAfterFive = new Intent(
                "com.calendar.BRODCAST_NOTIFICATION_AFTER_FIVE");
        intentAfterFive.putExtra("startTime", startMillis);
        intentAfterFive.putExtra("endTime", endMillis);
        intentAfterFive.putExtra("eventId", eventId);

        PendingIntent iKnowPendingIntent = PendingIntent.getBroadcast(context,
                notificationId, intentIKonw, PendingIntent.FLAG_UPDATE_CURRENT);
        // remoteViews.setOnClickPendingIntent(R.id.bt_i_know,
        // iKnowPendingIntent);

        PendingIntent afterFivePendingIntent = PendingIntent.getBroadcast(
                context, notificationId, intentAfterFive,
                PendingIntent.FLAG_UPDATE_CURRENT);


        if (!allDay && doPopup) {
            notificationBuilder.addAction(0,
                    context.getString(R.string.later_5min_notify),
                    afterFivePendingIntent);
        }
        notificationBuilder.addAction(0, context.getString(R.string.got_it),
                iKnowPendingIntent);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);

        // add by zyp end

        // Create the base notification.
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(summaryText);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder
                .setSmallIcon(R.drawable.gome_ic_calendar_notification);
        // notificationBuilder.setColor(context.getResources().getColor(R.color.notification_icon_background_color));

        notificationBuilder
                .setContentIntent(PendingIntent.getActivity(context,
                        notificationId, clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setDeleteIntent(PendingIntent
                .getBroadcast(context, notificationId, deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT));
        // add category for "Do not disturb" mode in setting by zhangxiaojun
        // 20151020
        notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        /**
         * // notificationBuilder.setSound(Uri.parse(
         * "content://media/external/audio/media/100")); if
         * (phoneIsInUse(context)) { // in call
         * notificationBuilder.setFullScreenIntent(
         * PendingIntent.getActivity(context, notificationId, clickIntent,
         * PendingIntent.FLAG_UPDATE_CURRENT), false); } else { // not in call
         * notificationBuilder.setFullScreenIntent(
         * PendingIntent.getActivity(context, notificationId, clickIntent,
         * PendingIntent.FLAG_UPDATE_CURRENT), true); }
         * 
         * if (doPopup) { //
         * notificationBuilder.setFullScreenIntent(createAlertActivityIntent
         * (context), // false); // notificationBuilder.setFullScreenIntent( //
         * PendingIntent.getActivity(context, 0, clickIntent, 0), false); } else
         * { // THE LOCATION OF PLAY SOUND !!!! ### // mAlertCalss = new
         * ClassToPlayAlert(context); // mAlertCalss.requestFocus(); }
         */

        // PendingIntent mapIntent = null, callIntent = null, snoozeIntent =
        // null, emailIntent = null;

        return notificationBuilder.getNotification();
        /**
         * if (addActionButtons) { // Send map, call, and email intent back to
         * ourself first for a couple reasons: // 1) Workaround issue where
         * clicking action button in notification does // not automatically
         * close the notification shade. // 2) Event information will always be
         * up to date.
         * 
         * // Create map and/or call intents. URLSpan[] urlSpans =
         * getURLSpans(context, eventId); mapIntent =
         * createMapBroadcastIntent(context, urlSpans, eventId); callIntent =
         * createCallBroadcastIntent(context, urlSpans, eventId);
         * 
         * // Create email intent for emailing attendees. emailIntent =
         * createBroadcastMailIntent(context, eventId, title);
         * 
         * // Create snooze intent. TODO: change snooze to 10 minutes.
         * snoozeIntent = createSnoozeIntent(context, eventId, startMillis,
         * endMillis, notificationId); }
         * 
         * if (Utils.isJellybeanOrLater()) { // Turn off timestamp. Log.d(TAG,
         * "lxg 1111111"); notificationBuilder.setWhen(0);
         * 
         * // Should be one of the values in Notification (ie.
         * Notification.PRIORITY_HIGH, etc). // A higher priority will encourage
         * notification manager to expand it.
         * notificationBuilder.setPriority(priority);
         * 
         * // Add action buttons. Show at most three, using the following
         * priority ordering: // 1. Map // 2. Call // 3. Email // 4. Snooze //
         * Actions will only be shown if they are applicable; i.e. with no
         * location, map will // not be shown, and with no recipients, snooze
         * will not be shown. // TODO: Get icons, get strings. Maybe show
         * preview of actual location/number? int numActions = 0; if (mapIntent
         * != null && numActions < MAX_NOTIF_ACTIONS) {
         * notificationBuilder.addAction(R.drawable.ic_map,
         * resources.getString(R.string.map_label), mapIntent); numActions++; }
         * if (callIntent != null && numActions < MAX_NOTIF_ACTIONS) {
         * notificationBuilder.addAction(R.drawable.ic_call,
         * resources.getString(R.string.call_label), callIntent); numActions++;
         * } if (emailIntent != null && numActions < MAX_NOTIF_ACTIONS) {
         * notificationBuilder.addAction(R.drawable.ic_menu_email_holo_dark,
         * resources.getString(R.string.email_guests_label), emailIntent);
         * numActions++; } if (snoozeIntent != null && numActions <
         * MAX_NOTIF_ACTIONS) {
         * notificationBuilder.addAction(R.drawable.ic_alarm_holo_dark,
         * resources.getString(R.string.snooze_label), snoozeIntent);
         * numActions++; } return notificationBuilder.getNotification();
         * 
         * } else { // Old-style notification (pre-JB). Use custom view with
         * buttons to provide // JB-like functionality (snooze/email).
         * Log.d(TAG,"lxg 22222"); Notification n =
         * notificationBuilder.getNotification();
         * 
         * // Use custom view with buttons to provide JB-like functionality
         * (snooze/email). RemoteViews contentView = new
         * RemoteViews(context.getPackageName(), R.layout.notification);
         * contentView.setImageViewResource(R.id.image,
         * R.drawable.stat_notify_calendar);
         * contentView.setTextViewText(R.id.title, title);
         * contentView.setTextViewText(R.id.text, summaryText);
         * 
         * int numActions = 0; if (mapIntent == null || numActions >=
         * MAX_NOTIF_ACTIONS) { contentView.setViewVisibility(R.id.map_button,
         * View.GONE); } else { contentView.setViewVisibility(R.id.map_button,
         * View.VISIBLE); contentView.setOnClickPendingIntent(R.id.map_button,
         * mapIntent); contentView.setViewVisibility(R.id.end_padding,
         * View.GONE); numActions++; } if (callIntent == null || numActions >=
         * MAX_NOTIF_ACTIONS) { contentView.setViewVisibility(R.id.call_button,
         * View.GONE); } else { contentView.setViewVisibility(R.id.call_button,
         * View.VISIBLE); contentView.setOnClickPendingIntent(R.id.call_button,
         * callIntent); contentView.setViewVisibility(R.id.end_padding,
         * View.GONE); numActions++; } if (emailIntent == null || numActions >=
         * MAX_NOTIF_ACTIONS) { contentView.setViewVisibility(R.id.email_button,
         * View.GONE); } else { contentView.setViewVisibility(R.id.email_button,
         * View.VISIBLE); contentView.setOnClickPendingIntent(R.id.email_button,
         * emailIntent); contentView.setViewVisibility(R.id.end_padding,
         * View.GONE); numActions++; } if (snoozeIntent == null || numActions >=
         * MAX_NOTIF_ACTIONS) {
         * contentView.setViewVisibility(R.id.snooze_button, View.GONE); } else
         * { contentView.setViewVisibility(R.id.snooze_button, View.VISIBLE);
         * contentView.setOnClickPendingIntent(R.id.snooze_button,
         * snoozeIntent); contentView.setViewVisibility(R.id.end_padding,
         * View.GONE); numActions++; }
         * 
         * n.contentView = contentView;
         * 
         * return n; }
         */
    }

    public static boolean phoneIsInUse(Context context) {
        TelecomManager tm = (TelecomManager) context
                .getSystemService(Context.TELECOM_SERVICE);
        return tm.isInCall();
    }

    /**
     * Creates an expanding notification. The initial expanded state is decided
     * by the notification manager based on the priority.
     */
    public static NotificationWrapper makeExpandingNotification(
            Context context, String title, String summaryText,
            String description, long startMillis, long endMillis, long eventId,
            int notificationId, boolean doPopup, int priority, boolean allDay) {
        Log.d(TAG, "lxg --makeExpandingNotification");
        Notification.Builder basicBuilder = new Notification.Builder(context);
        Notification notification = buildBasicNotification(basicBuilder,
                context, title, summaryText, startMillis, endMillis, eventId,
                notificationId, doPopup, priority, true, allDay);

        if (phoneIsInUse(context)) {
            // in call
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else {
            // not in call
            notification.flags |= Notification.FLAG_NO_CLEAR;
        }

        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        /**
         * if (Utils.isJellybeanOrLater()) { // Create a new-style expanded
         * notification Notification.BigTextStyle expandedBuilder = new
         * Notification.BigTextStyle(); if (description != null) { description =
         * mBlankLinePattern.matcher(description).replaceAll(""); description =
         * description.trim(); } CharSequence text; if
         * (TextUtils.isEmpty(description)) { text = summaryText; } else {
         * SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
         * stringBuilder.append(summaryText); stringBuilder.append("\n\n");
         * stringBuilder.setSpan(new RelativeSizeSpan(0.5f),
         * summaryText.length(), stringBuilder.length(), 0);
         * stringBuilder.append(description); text = stringBuilder; }
         * expandedBuilder.bigText(text);
         * basicBuilder.setStyle(expandedBuilder); notification =
         * basicBuilder.build(); }
         */
        return new NotificationWrapper(notification, notificationId, eventId,
                startMillis, endMillis, doPopup);
    }

    /**
     * Creates an expanding digest notification for expired events.
     */
    public static NotificationWrapper makeDigestNotification(Context context,
            ArrayList<AlertService.NotificationInfo> notificationInfos,
            String digestTitle, boolean expandable) {
        Log.d(TAG, "lxg --makeDigestNotification");
        if (notificationInfos == null || notificationInfos.size() < 1) {
            return null;
        }

        Resources res = context.getResources();
        int numEvents = notificationInfos.size();
        long[] eventIds = new long[notificationInfos.size()];
        long[] startMillis = new long[notificationInfos.size()];
        for (int i = 0; i < notificationInfos.size(); i++) {
            eventIds[i] = notificationInfos.get(i).eventId;
            startMillis[i] = notificationInfos.get(i).startMillis;
        }

        // Create an intent triggered by clicking on the status icon that shows
        // the alerts list.
        PendingIntent pendingClickIntent = createAlertActivityIntent(context);

        // Create an intent triggered by dismissing the digest notification that
        // clears all
        // expired events.
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, DismissAlarmsService.class);
        deleteIntent.setAction(DismissAlarmsService.DISMISS_ACTION);
        // deleteIntent.putExtra(AlertUtils.EVENT_IDS_KEY, eventIds);
        // deleteIntent.putExtra(AlertUtils.EVENT_STARTS_KEY, startMillis);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(context,
                0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (digestTitle == null || digestTitle.length() == 0) {
            digestTitle = res.getString(R.string.no_title_label);
        }

        Notification.Builder notificationBuilder = new Notification.Builder(
                context);
        notificationBuilder.setContentText(digestTitle);
        notificationBuilder
                .setSmallIcon(R.drawable.stat_notify_calendar_multiple);
        notificationBuilder.setColor(context.getResources().getColor(
                R.color.notification_icon_background_color));
        notificationBuilder.setContentIntent(pendingClickIntent);
        notificationBuilder.setDeleteIntent(pendingDeleteIntent);
        String nEventsStr = res.getQuantityString(R.plurals.Nevents, numEvents,
                numEvents);
        notificationBuilder.setContentTitle(nEventsStr);
        // add category for "Do not disturb" mode in setting by zhangxiaojun
        // 20151020
        notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        Notification n;
        if (Utils.isJellybeanOrLater()) {
            // New-style notification...

            // Set to min priority to encourage the notification manager to
            // collapse it.
            // notificationBuilder.setPriority(Notification.PRIORITY_MIN);
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);

            if (expandable) {
                // Multiple reminders. Combine into an expanded digest
                // notification.
                Notification.InboxStyle expandedBuilder = new Notification.InboxStyle();
                int i = 0;
                for (AlertService.NotificationInfo info : notificationInfos) {
                    if (i < NOTIFICATION_DIGEST_MAX_LENGTH) {
                        String name = info.eventName;
                        if (TextUtils.isEmpty(name)) {
                            name = context.getResources().getString(
                                    R.string.no_title_label);
                        }
                        String timeLocation = AlertUtils.formatTimeLocation(
                                context, info.startMillis, info.allDay,
                                info.location);

                        TextAppearanceSpan primaryTextSpan = new TextAppearanceSpan(
                                context, R.style.NotificationPrimaryText);
                        TextAppearanceSpan secondaryTextSpan = new TextAppearanceSpan(
                                context, R.style.NotificationSecondaryText);

                        // Event title in bold.
                        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
                        stringBuilder.append(name);
                        stringBuilder.setSpan(primaryTextSpan, 0,
                                stringBuilder.length(), 0);
                        stringBuilder.append("  ");

                        // Followed by time and location.
                        int secondaryIndex = stringBuilder.length();
                        stringBuilder.append(timeLocation);
                        stringBuilder.setSpan(secondaryTextSpan,
                                secondaryIndex, stringBuilder.length(), 0);
                        expandedBuilder.addLine(stringBuilder);
                        i++;
                    } else {
                        break;
                    }
                }

                // If there are too many to display, add "+X missed events" for
                // the last line.
                int remaining = numEvents - i;
                if (remaining > 0) {
                    String nMoreEventsStr = res.getQuantityString(
                            R.plurals.N_remaining_events, remaining, remaining);
                    // TODO: Add highlighting and icon to this last entry once
                    // framework allows it.
                    expandedBuilder.setSummaryText(nMoreEventsStr);
                }

                // Remove the title in the expanded form (redundant with the
                // listed items).
                expandedBuilder.setBigContentTitle("");
                notificationBuilder.setStyle(expandedBuilder);
            }

            n = notificationBuilder.build();
        } else {
            // Old-style notification (pre-JB). We only need a standard
            // notification (no
            // buttons) but use a custom view so it is consistent with the
            // others.
            n = notificationBuilder.getNotification();

            // Use custom view with buttons to provide JB-like functionality
            // (snooze/email).
            RemoteViews contentView = new RemoteViews(context.getPackageName(),
                    R.layout.notification);
            contentView.setImageViewResource(R.id.image,
                    R.drawable.stat_notify_calendar_multiple);
            contentView.setTextViewText(R.id.title, nEventsStr);
            contentView.setTextViewText(R.id.text, digestTitle);
            contentView.setViewVisibility(R.id.time, View.VISIBLE);
            contentView.setViewVisibility(R.id.map_button, View.GONE);
            contentView.setViewVisibility(R.id.call_button, View.GONE);
            contentView.setViewVisibility(R.id.email_button, View.GONE);
            contentView.setViewVisibility(R.id.snooze_button, View.GONE);
            contentView.setViewVisibility(R.id.end_padding, View.VISIBLE);
            n.contentView = contentView;

            // Use timestamp to force expired digest notification to the bottom
            // (there is no
            // priority setting before JB release). This is hidden by the custom
            // view.
            n.when = 1;
        }

        NotificationWrapper nw = new NotificationWrapper(n);
        if (AlertService.DEBUG) {
            for (AlertService.NotificationInfo info : notificationInfos) {
                nw.add(new NotificationWrapper(null, 0, info.eventId,
                        info.startMillis, info.endMillis, false));
            }
        }
        return nw;
    }

    private void closeNotificationShade(Context context) {
        Intent closeNotificationShadeIntent = new Intent(
                Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(closeNotificationShadeIntent);
    }

    private static final String[] ATTENDEES_PROJECTION = new String[] {
            Attendees.ATTENDEE_EMAIL, // 0
            Attendees.ATTENDEE_STATUS, // 1
    };
    private static final int ATTENDEES_INDEX_EMAIL = 0;
    private static final int ATTENDEES_INDEX_STATUS = 1;
    private static final String ATTENDEES_WHERE = Attendees.EVENT_ID + "=?";
    private static final String ATTENDEES_SORT_ORDER = Attendees.ATTENDEE_NAME
            + " ASC, " + Attendees.ATTENDEE_EMAIL + " ASC";

    private static final String[] EVENT_PROJECTION = new String[] {
            Calendars.OWNER_ACCOUNT, // 0
            Calendars.ACCOUNT_NAME, // 1
            Events.TITLE, // 2
            Events.ORGANIZER, // 3
    };
    private static final int EVENT_INDEX_OWNER_ACCOUNT = 0;
    private static final int EVENT_INDEX_ACCOUNT_NAME = 1;
    private static final int EVENT_INDEX_TITLE = 2;
    private static final int EVENT_INDEX_ORGANIZER = 3;

    private static Cursor getEventCursor(Context context, long eventId) {
        if (Utils.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)) {
            return context.getContentResolver().query(
                    ContentUris.withAppendedId(Events.CONTENT_URI, eventId),
                    EVENT_PROJECTION, null, null, null);
        }
        return null;
    }

    private static Cursor getAttendeesCursor(Context context, long eventId) {
        if (Utils.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)) {
            return context.getContentResolver().query(Attendees.CONTENT_URI,
                    ATTENDEES_PROJECTION, ATTENDEES_WHERE,
                    new String[] { Long.toString(eventId) },
                    ATTENDEES_SORT_ORDER);
        }
        return null;
    }

    private static Cursor getLocationCursor(Context context, long eventId) {
        if (Utils.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR)) {
            return context.getContentResolver().query(
                    ContentUris.withAppendedId(Events.CONTENT_URI, eventId),
                    new String[] { Events.EVENT_LOCATION }, null, null, null);
        }
        return null;
    }

    /**
     * Creates a broadcast pending intent that fires to AlertReceiver when the
     * email button is clicked.
     */
    private static PendingIntent createBroadcastMailIntent(Context context,
            long eventId, String eventTitle) {
        // Query for viewer account.
        String syncAccount = null;
        Cursor eventCursor = getEventCursor(context, eventId);
        try {
            if (eventCursor != null && eventCursor.moveToFirst()) {
                syncAccount = eventCursor.getString(EVENT_INDEX_ACCOUNT_NAME);
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }

        // Query attendees to see if there are any to email.
        Cursor attendeesCursor = getAttendeesCursor(context, eventId);
        try {
            if (attendeesCursor != null && attendeesCursor.moveToFirst()) {
                do {
                    String email = attendeesCursor
                            .getString(ATTENDEES_INDEX_EMAIL);
                    if (Utils.isEmailableFrom(email, syncAccount)) {
                        Intent broadcastIntent = new Intent(MAIL_ACTION);
                        broadcastIntent.setClass(context, AlertReceiver.class);
                        broadcastIntent.putExtra(EXTRA_EVENT_ID, eventId);
                        return PendingIntent.getBroadcast(context, Long
                                .valueOf(eventId).hashCode(), broadcastIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT);
                    }
                } while (attendeesCursor.moveToNext());
            }
            return null;

        } finally {
            if (attendeesCursor != null) {
                attendeesCursor.close();
            }
        }
    }

    /**
     * Creates an Intent for emailing the attendees of the event. Returns null
     * if there are no emailable attendees.
     */
    static Intent createEmailIntent(Context context, long eventId, String body) {
        // TODO: Refactor to move query part into
        // Utils.createEmailAttendeeIntent, to
        // be shared with EventInfoFragment.

        // Query for the owner account(s).
        String ownerAccount = null;
        String syncAccount = null;
        String eventTitle = null;
        String eventOrganizer = null;
        Cursor eventCursor = getEventCursor(context, eventId);
        try {
            if (eventCursor != null && eventCursor.moveToFirst()) {
                ownerAccount = eventCursor.getString(EVENT_INDEX_OWNER_ACCOUNT);
                syncAccount = eventCursor.getString(EVENT_INDEX_ACCOUNT_NAME);
                eventTitle = eventCursor.getString(EVENT_INDEX_TITLE);
                eventOrganizer = eventCursor.getString(EVENT_INDEX_ORGANIZER);
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        if (TextUtils.isEmpty(eventTitle)) {
            eventTitle = context.getResources().getString(
                    R.string.no_title_label);
        }

        // Query for the attendees.
        List<String> toEmails = new ArrayList<String>();
        List<String> ccEmails = new ArrayList<String>();
        Cursor attendeesCursor = getAttendeesCursor(context, eventId);
        try {
            if (attendeesCursor != null && attendeesCursor.moveToFirst()) {
                do {
                    int status = attendeesCursor.getInt(ATTENDEES_INDEX_STATUS);
                    String email = attendeesCursor
                            .getString(ATTENDEES_INDEX_EMAIL);
                    switch (status) {
                    case Attendees.ATTENDEE_STATUS_DECLINED:
                        addIfEmailable(ccEmails, email, syncAccount);
                        break;
                    default:
                        addIfEmailable(toEmails, email, syncAccount);
                    }
                } while (attendeesCursor.moveToNext());
            }
        } finally {
            if (attendeesCursor != null) {
                attendeesCursor.close();
            }
        }

        // Add organizer only if no attendees to email (the case when too many
        // attendees
        // in the event to sync or show).
        if (toEmails.size() == 0 && ccEmails.size() == 0
                && eventOrganizer != null) {
            addIfEmailable(toEmails, eventOrganizer, syncAccount);
        }

        Intent intent = null;
        if (ownerAccount != null
                && (toEmails.size() > 0 || ccEmails.size() > 0)) {
            intent = Utils.createEmailAttendeesIntent(context.getResources(),
                    eventTitle, body, toEmails, ccEmails, ownerAccount);
        }

        if (intent == null) {
            return null;
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            return intent;
        }
    }

    private static void addIfEmailable(List<String> emailList, String email,
            String syncAccount) {
        if (Utils.isEmailableFrom(email, syncAccount)) {
            emailList.add(email);
        }
    }

    /**
     * Using the linkify magic, get a list of URLs from the event's location. If
     * no such links are found, we should end up with a single geo link of the
     * entire string.
     */
    private static URLSpan[] getURLSpans(Context context, long eventId) {
        Cursor locationCursor = getLocationCursor(context, eventId);

        // Default to empty list
        URLSpan[] urlSpans = new URLSpan[0];
        if (locationCursor != null && locationCursor.moveToFirst()) {
            String location = locationCursor.getString(0); // Only one item in
                                                           // this cursor.
            if (location != null && !location.isEmpty()) {
                Spannable text = Utils.extendedLinkify(location, true);
                // The linkify method should have found at least one link, at
                // the very least.
                // If no smart links were found, it should have set the whole
                // string as a geo link.
                urlSpans = text.getSpans(0, text.length(), URLSpan.class);
            }
            locationCursor.close();
        }

        return urlSpans;
    }

    /**
     * Create a pending intent to send ourself a broadcast to start maps, using
     * the first map link available. If no links are found, return null.
     */
    private static PendingIntent createMapBroadcastIntent(Context context,
            URLSpan[] urlSpans, long eventId) {
        for (int span_i = 0; span_i < urlSpans.length; span_i++) {
            URLSpan urlSpan = urlSpans[span_i];
            String urlString = urlSpan.getURL();
            if (urlString.startsWith(GEO_PREFIX)) {
                Intent broadcastIntent = new Intent(MAP_ACTION);
                broadcastIntent.setClass(context, AlertReceiver.class);
                broadcastIntent.putExtra(EXTRA_EVENT_ID, eventId);
                return PendingIntent.getBroadcast(context, Long
                        .valueOf(eventId).hashCode(), broadcastIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
            }
        }

        // No geo link was found, so return null;
        return null;
    }

    /**
     * Create an intent to take the user to maps, using the first map link
     * available. If no links are found, return null.
     */
    private static Intent createMapActivityIntent(Context context,
            URLSpan[] urlSpans) {
        for (int span_i = 0; span_i < urlSpans.length; span_i++) {
            URLSpan urlSpan = urlSpans[span_i];
            String urlString = urlSpan.getURL();
            if (urlString.startsWith(GEO_PREFIX)) {
                Intent geoIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(urlString));
                geoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return geoIntent;
            }
        }

        // No geo link was found, so return null;
        return null;
    }

    /**
     * Create a pending intent to send ourself a broadcast to take the user to
     * dialer, or any other app capable of making phone calls. Use the first
     * phone number available. If no phone number is found, or if the device is
     * not capable of making phone calls (i.e. a tablet), return null.
     */
    private static PendingIntent createCallBroadcastIntent(Context context,
            URLSpan[] urlSpans, long eventId) {
        // Return null if the device is unable to make phone calls.
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return null;
        }

        for (int span_i = 0; span_i < urlSpans.length; span_i++) {
            URLSpan urlSpan = urlSpans[span_i];
            String urlString = urlSpan.getURL();
            if (urlString.startsWith(TEL_PREFIX)) {
                Intent broadcastIntent = new Intent(CALL_ACTION);
                broadcastIntent.setClass(context, AlertReceiver.class);
                broadcastIntent.putExtra(EXTRA_EVENT_ID, eventId);
                return PendingIntent.getBroadcast(context, Long
                        .valueOf(eventId).hashCode(), broadcastIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
            }
        }

        // No tel link was found, so return null;
        return null;
    }

    /**
     * Create an intent to take the user to dialer, or any other app capable of
     * making phone calls. Use the first phone number available. If no phone
     * number is found, or if the device is not capable of making phone calls
     * (i.e. a tablet), return null.
     */
    private static Intent createCallActivityIntent(Context context,
            URLSpan[] urlSpans) {
        // Return null if the device is unable to make phone calls.
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            return null;
        }

        for (int span_i = 0; span_i < urlSpans.length; span_i++) {
            URLSpan urlSpan = urlSpans[span_i];
            String urlString = urlSpan.getURL();
            if (urlString.startsWith(TEL_PREFIX)) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(urlString));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return callIntent;
            }
        }

        // No tel link was found, so return null;
        return null;
    }
}
