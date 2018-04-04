package com.hct.calendar.lbs;

import java.util.ArrayList;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import com.hct.calendar.lbs.HctRingtoneManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.CalendarUtils;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.alerts.AlertService;
import com.android.calendar.event.EditEventHelper;
import com.hct.calendar.lbs.LbsServiceHelper.LocationFenceModel;

public class LocationFenceService extends IntentService {
    private final static String TAG = "LocationFenceService";
    public static final String LOCATION_REMIND_ACTION = "com.hct.lbs.fenceMonitor";
    public static final String LOCATION_FENCE_CREATE_ACTION = "com.hct.lbs.fenceCreate";
    public static final String LOCATION_FENCE_UPDATE_ACTION = "com.hct.lbs.fenceUpdate";
    public static final String LOCATION_FENCE_DELETE_ACTION = "com.hct.lbs.fenceDelete";

    public static final String NOTIFICATION_DELETE_FENCEN_ACTION = "notification_delete_fence";

    public static final String LBS_EXTRA_RESULT_KEY = "lbsResult";
    public static final String LBS_EXTRA_JSON_FENCEN_ID = "fenceId";
    public static final String LBS_EXTRA_JOSN_PACKAGE_NAME = "packageName";
    public static final String LBS_EXTRA_JOSN_FENCEN_RADIUS = "radius";
    public static final String LBS_EXTRA_JOSN_FENCEN_EVENT_ID = "eventId";
    private static boolean DEBUG = false;

    public LocationFenceService() {
        super("LocationFenceService");
    }

    public static void processLocationBroadcastIntent(Context context,
            Intent intent) {
        Intent i = new Intent(context, LocationFenceService.class);
        i.putExtra(Intent.EXTRA_INTENT, intent);
        context.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        final Intent broadcastIntent = intent
                .getParcelableExtra(Intent.EXTRA_INTENT);
        if (broadcastIntent == null) {
            Log.w(TAG, "fatal intent request");
            return;
        }
        final String broadcastAction = broadcastIntent.getAction();
        if (LOCATION_REMIND_ACTION.equals(broadcastAction)) {

            onLocationFencenAlert(broadcastIntent);

        } else if (LOCATION_FENCE_CREATE_ACTION.equals(broadcastAction)) {
            //

        } else if (LOCATION_FENCE_UPDATE_ACTION.equals(broadcastAction)) {

            //

        } else if (LOCATION_FENCE_DELETE_ACTION.equals(broadcastAction)) {

            onLocationFencenDeleteCompleted(broadcastIntent);

        } else if (NOTIFICATION_DELETE_FENCEN_ACTION.equals(broadcastAction)) {
            onLocationNotificationDelete(broadcastIntent);
        } else {
            Log.w(TAG, "action is not handle,intent is " + intent);
        }
    }

    /**
     * may be disable when create complete fence
     * 
     * @param intent
     */
    private void onLocationFencenCreateCompleted(Intent intent) {

    }

    /**
     * 
     * @param intent
     */
    private void onLocationFencenDeleteCompleted(Intent intent) {
        int fenceId = intent.getIntExtra(LBS_EXTRA_JSON_FENCEN_ID, -1);
        if (fenceId != -1) {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(LOCATION_PROJECTS[INDEX_EVENT_EXT_ACTION_DATA2],
                    "");
            int res = getContentResolver().update(Events.CONTENT_URI,
                    contentValues, " eventType = 1 and action_data2 = ? ",
                    new String[] { String.valueOf(fenceId) });
            if (res == 1) {
                Log.v(TAG, "onLocationFencenDeleteCompleted delete fenceId #"
                        + fenceId);
            }
        }
    }

    /**
     * @param intent
     */
    private void onLocationFencenUpdateCompleted(Intent intent) {

    }

    private void onLocationNotificationDelete(Intent intent) {
        int fenceId = intent.getIntExtra(LBS_EXTRA_JSON_FENCEN_ID, -1);
        if (fenceId == -1) {
            return;
        }
        updateLocationEventFence(fenceId);
        LbsServiceHelper.deleteLocationFence(getApplicationContext(), fenceId,
                -1);
    }

    private void onLocationFencenAlert(Intent intent) {
        int fenceId = intent.getIntExtra(LBS_EXTRA_JSON_FENCEN_ID, -1);
        if (fenceId == -1) {
            return;
        }
        Log.v(TAG, intent.getStringExtra("monitorFenceResult"));
        LocationFenceModel calendarData = queryLocationEventDataByFenceId(fenceId);
        if (calendarData == null) {
            Log.v(TAG, " may be event is not lbs or  delete");
            LbsServiceHelper.deleteLocationFence(this, fenceId, -1);
            return;
        }
        generateLocationAlerts(this, calendarData.eventTitle,
                calendarData.location, calendarData.location,
                calendarData.eventId, fenceId);
    }

    public static boolean generateLocationAlerts(Context context,
            String eventName, String location, String description,
            long eventId, int fencenId) {
        NotificationLocationMgr nm = new NotificationMgrWrapper(context,
                (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE));
        SharedPreferences prefs = GeneralPreferences
                .getSharedPreferences(context);
        NotificationPrefs notificationPrefs = new NotificationPrefs(context,
                prefs, false);
        NotificationInfo info = new NotificationInfo(eventName, location,
                description, eventId, fencenId, true);
        postNotification(info, description, context, true, notificationPrefs,
                nm, fencenId, prefs);
        return true;
    }

    private static void postNotification(NotificationInfo info,
            String summaryText, Context context, boolean highPriority,
            NotificationPrefs prefs, NotificationLocationMgr notificationMgr,
            int notificationId, SharedPreferences sPrefs) {
        int priorityVal = Notification.PRIORITY_DEFAULT;
        boolean doPopup = sPrefs.getBoolean(
                GeneralPreferences.KEY_ALERTS_POPUP, false);
        if (highPriority && doPopup) {
            Intent alertIntent = new Intent();
            alertIntent.setClass(context, LocationAlertActivity.class);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            alertIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
            alertIntent.putExtra(LBS_EXTRA_JSON_FENCEN_ID, notificationId);
            context.startActivity(alertIntent);
            // calendarPlayAlert = true;
            // mAlertCalss = new ClassToPlayAlert(context);
            // mAlertCalss.requestFocus();
            priorityVal = Notification.PRIORITY_HIGH;
        }

        String tickerText = getTickerText(info.eventName, info.location);
        NotificationWrapper notification = makeExpandingNotification(context,
                info.eventName, summaryText, info.description, info.eventId,
                notificationId, prefs.getDoPopup(), priorityVal);

        boolean quietUpdate = true;
        String ringtone = NotificationPrefs.EMPTY_RINGTONE;
        if (info.newAlert) {
            quietUpdate = prefs.quietUpdate;

            // If we've already played a ringtone, don't play any more sounds so
            // only
            // 1 sound per group of notifications.
            ringtone = prefs.getRingtoneAndSilence();
        }
        addNotificationOptions(context, notification, quietUpdate, tickerText,
                prefs.getDefaultVibrate(), ringtone, true, sPrefs);

        // Post the notification.
        notificationMgr.notify(notificationId, notification);

        if (DEBUG) {
            Log.d(TAG, "Posting individual alarm notification, eventId:"
                    + info.eventId + ", notificationId:" + notificationId
                    + (TextUtils.isEmpty(ringtone) ? ", quiet" : ", LOUD")
                    + (highPriority ? ", high-priority" : ""));
        }
    }

    private static void addNotificationOptions(Context context,
            NotificationWrapper nw, boolean quietUpdate, String tickerText,
            boolean defaultVibrate, String reminderRingtone,
            boolean showLights, SharedPreferences prefs) {
        Notification notification = nw.mNotification;
        // Quietly update notification bar. Nothing new. Maybe something just
        // got deleted.
        if (!quietUpdate) {
            // Flash ticker in status bar
            if (!TextUtils.isEmpty(tickerText)) {
                notification.tickerText = tickerText;
            }

            // Generate either a pop-up dialog, status bar notification, or
            // neither. Pop-up dialog and status bar notification may include a
            // sound, an alert, or both. A status bar notification also includes
            // a toast.
            // if (defaultVibrate) {
            // notification.defaults |= Notification.DEFAULT_VIBRATE;
            // }
            String vibrateWhen; // "always" or "silent" or "never"
            if (prefs.contains(GeneralPreferences.KEY_ALERTS_VIBRATE_WHEN)) {
                // Look up Froyo setting
                vibrateWhen = prefs.getString(
                        GeneralPreferences.KEY_ALERTS_VIBRATE_WHEN, null);
            } else if (prefs.contains(GeneralPreferences.KEY_ALERTS_VIBRATE)) {
                // No Froyo setting. Migrate pre-Froyo setting to new
                // Froyo-defined value.
                boolean vibrate = prefs.getBoolean(
                        GeneralPreferences.KEY_ALERTS_VIBRATE, false);
                vibrateWhen = vibrate ? context
                        .getString(R.string.prefDefault_alerts_vibrate_true)
                        : context
                                .getString(R.string.prefDefault_alerts_vibrate_false);
            } else {
                // No setting. Use Froyo-defined default.
                vibrateWhen = context
                        .getString(R.string.prefDefault_alerts_vibrateWhen);
            }
            boolean vibrateAlways = vibrateWhen.equals("always");
            boolean vibrateSilent = vibrateWhen.equals("silent");
            AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            boolean nowSilent = audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE;
            int nowVoice = audioManager
                    .getStreamVolume(audioManager.STREAM_SYSTEM);
            boolean isMinVolume = (nowVoice == 0);

            if (vibrateAlways || (vibrateSilent && (nowSilent || isMinVolume))) {
                Log.d(TAG, "(vibrateAlways || (vibrateSilent && nowSilent)) ");
                // notification.defaults |= Notification.DEFAULT_VIBRATE;
                long[] sVibratePattern = new long[] { 0, 500 };
                Vibrator mVibrator = (Vibrator) context
                        .getSystemService(Context.VIBRATOR_SERVICE);
                mVibrator.vibrate(sVibratePattern, -1);
            }

            reminderRingtone = prefs.getString(
                    GeneralPreferences.KEY_ALERTS_RINGTONE, null);
            if (DEBUG && !TextUtils.isEmpty(reminderRingtone)) {
                Log.v(TAG, "before reminderRingtone: " + reminderRingtone);
            }
            if (TextUtils.isEmpty(reminderRingtone)) {
                Uri mUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, android.media.RingtoneManager.TYPE_NOTIFICATION);
                notification.sound = mUri;
                CalendarUtils.setSharedPreference(prefs,
                        GeneralPreferences.KEY_ALERTS_RINGTONE,
                        mUri == null ? "" : mUri.toString());
            } else {
                Uri mUri = Uri.parse(reminderRingtone);
                if (TextUtils.isEmpty(AlertService.getMediaUri(context, mUri))) {
                    mUri = RingtoneManager.getActualDefaultRingtoneUri(
                            context, android.media.RingtoneManager.TYPE_NOTIFICATION);
                    notification.sound = mUri;
                    CalendarUtils.setSharedPreference(prefs,
                            GeneralPreferences.KEY_ALERTS_RINGTONE,
                            mUri == null ? "" : mUri.toString());
                } else {
                    notification.sound = mUri;
                }
                if (DEBUG && !TextUtils.isEmpty(reminderRingtone)) {
                    Log.v(TAG, "after reminderRingtone: " + notification.sound);
                }
            }
        }

        // boolean doPopup =
        // prefs.getBoolean(GeneralPreferences.KEY_ALERTS_POPUP,
        // false);
        // if (doPopup) {
        // // notification.sound = null;
        // }
    }

    public static class NotificationMgrWrapper extends NotificationLocationMgr {

        NotificationManager mNm;
        Context mContext;

        public NotificationMgrWrapper(Context context, NotificationManager nm) {
            mNm = nm;
            mContext = context;
        }

        @Override
        public void cancel(int id) {
            mNm.cancel(id);
        }

        @Override
        public void notify(int id, NotificationWrapper nw) {
            if (Utils.checkSelfPermission(mContext,
                    Manifest.permission.READ_PHONE_STATE)) {
                mNm.notify(id, nw.mNotification);
            }
        }

    }

    static class NotificationPrefs {
        boolean quietUpdate;
        private Context context;
        private SharedPreferences prefs;

        // These are lazily initialized, do not access any of the following
        // directly; use getters.
        private int doPopup = -1;
        private int defaultVibrate = -1;
        private String ringtone = null;

        private static final String EMPTY_RINGTONE = "";

        public NotificationPrefs(Context context, SharedPreferences prefs,
                boolean quietUpdate) {
            this.context = context;
            this.prefs = prefs;
            this.quietUpdate = quietUpdate;
        }

        public boolean getDoPopup() {
            if (doPopup < 0) {
                if (prefs
                        .getBoolean(GeneralPreferences.KEY_ALERTS_POPUP, false)) {
                    doPopup = 1;
                } else {
                    doPopup = 0;
                }
            }
            return doPopup == 1;
        }

        private boolean getDefaultVibrate() {
            if (defaultVibrate < 0) {
                // defaultVibrate = Utils.getDefaultVibrate(context, prefs) ? 1
                // : 0;
            }
            return defaultVibrate == 1;
        }

        public String getRingtoneAndSilence() {
            if (ringtone == null) {
                if (quietUpdate) {
                    ringtone = EMPTY_RINGTONE;
                } else {
                    ringtone = Utils.getRingTonePreference(context);
                }
            }
            String retVal = ringtone;
            ringtone = EMPTY_RINGTONE;
            return retVal;
        }
    }

    public static class NotificationWrapper {
        Notification mNotification;
        long mEventId;
        int mFenceId;
        ArrayList<NotificationWrapper> mNw;

        public NotificationWrapper(Notification n, int notificationId,
                long eventId, int fenceId, boolean doPopup) {
            mNotification = n;
            mEventId = eventId;
            mFenceId = fenceId;
            // popup?
            // notification id?
        }

        public NotificationWrapper(Notification n) {
            mNotification = n;
        }

        public void add(NotificationWrapper nw) {
            if (mNw == null) {
                mNw = new ArrayList<NotificationWrapper>();
            }
            mNw.add(nw);
        }
    }

    public static NotificationWrapper makeExpandingNotification(
            Context context, String title, String summaryText,
            String description, long eventId, int notificationId,
            boolean doPopup, int priority) {
        Notification.Builder basicBuilder = new Notification.Builder(context);
        Notification notification = buildBasicNotification(basicBuilder,
                context, title, summaryText, eventId, notificationId, doPopup,
                priority, true);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        return new NotificationWrapper(notification, notificationId, eventId,
                notificationId, doPopup);
    }

    private static Notification buildBasicNotification(
            Notification.Builder notificationBuilder, Context context,
            String title, String summaryText, long eventId, int notificationId,
            boolean doPopup, int priority, boolean addActionButtons) {
        Resources resources = context.getResources();
        if (title == null || title.length() == 0) {
            title = resources.getString(R.string.no_title_label);
        }

        Intent clickIntent = new Intent();
        clickIntent.setClass(context, LocationAlertActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        clickIntent.putExtra(LBS_EXTRA_JSON_FENCEN_ID, notificationId);
        // Create an intent triggered by clicking on the
        // "Clear All Notifications" button
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, LocationFenceReceiver.class);
        deleteIntent.setAction(NOTIFICATION_DELETE_FENCEN_ACTION);
        deleteIntent.putExtra(LBS_EXTRA_JSON_FENCEN_ID, notificationId);
        deleteIntent.putExtra("packageName", context.getPackageName());
        // Create the base notification.
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(summaryText);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder
                .setSmallIcon(R.drawable.stat_notify_calendar_statusbar);
        Bitmap largeIcom = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.stat_notify_calendar);
        notificationBuilder.setLargeIcon(largeIcom);
        notificationBuilder.setContentIntent(PendingIntent.getActivity(context,
                0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        notificationBuilder.setDeleteIntent(PendingIntent.getBroadcast(context,
                0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        // add category for "Do not disturb" mode in setting by zhangxiaojun
        // 20151020
        notificationBuilder.setCategory(Notification.CATEGORY_EVENT);
        if (doPopup) {
            notificationBuilder.setFullScreenIntent(
                    createAlertActivityIntent(context, notificationId), false);
        }
        return notificationBuilder.build();

    }

    private static PendingIntent createAlertActivityIntent(Context context,
            int fenceId) {
        Intent clickIntent = new Intent();
        clickIntent.setClass(context, LocationAlertActivity.class);
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        clickIntent.putExtra(LBS_EXTRA_JSON_FENCEN_ID, fenceId);
        return PendingIntent
                .getActivity(context, 0, clickIntent,
                        PendingIntent.FLAG_ONE_SHOT
                                | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static String getTickerText(String eventName, String location) {
        String tickerText = eventName;
        if (!TextUtils.isEmpty(location)) {
            tickerText = eventName + " - " + location;
        }
        return tickerText;
    }

    static class NotificationInfo {
        String eventName;
        String location;
        String description;
        long eventId;
        boolean newAlert;
        int fenceId;

        NotificationInfo(String eventName, String location, String description,
                long eventId, int fenceId, boolean newAlert) {
            this.eventName = eventName;
            this.location = location;
            this.description = description;
            this.eventId = eventId;
            this.newAlert = newAlert;
            this.fenceId = fenceId;
        }
    }

    private LocationFenceModel queryLocationEventData(int eventId) {
        Cursor cursor = null;
        LocationFenceModel data = null;
        try {
            cursor = getContentResolver().query(Events.CONTENT_URI,
                    LOCATION_PROJECTS, " eventType = 1 and _id = ? ",
                    new String[] { String.valueOf(eventId) }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
                data = new LocationFenceModel();
                data.eventId = cursor.getInt(INDEX_EVENT_ID);
                data.radius = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA);
                data.mapParam = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA1);
                data.fencenId = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA2);
            }
        } catch (Exception e) {
            Log.e(TAG, "queryLocationEventData error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }

    private LocationFenceModel queryLocationEventDataByFenceId(int fenceId) {
        Cursor cursor = null;
        LocationFenceModel data = null;
        try {
            cursor = getContentResolver().query(Events.CONTENT_URI,
                    LOCATION_PROJECTS, " eventType = 1 and action_data2 = ? ",
                    new String[] { String.valueOf(fenceId) }, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToNext();
                data = new LocationFenceModel();
                data.eventId = cursor.getInt(INDEX_EVENT_ID);
                data.radius = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA);
                data.mapParam = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA1);
                data.fencenId = cursor.getString(INDEX_EVENT_EXT_ACTION_DATA2);
                data.eventTitle = cursor.getString(INDEX_EVENT_TITLE);
                data.location = cursor.getString(INDEX_EVENT_LOCATION);
                data.description = cursor.getString(INDEX_EVENT_DESCRIPTION);
            }
        } catch (Exception e) {
            Log.e(TAG, "queryLocationEventData error:" + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return data;
    }

    private void updateLocationEventFence(int fenceId) {
        String selection = LOCATION_PROJECTS[INDEX_EVENT_EXT_ACTION_DATA2]
                + "= ? ";
        int res = getContentResolver().update(
                CalendarContract.Events.CONTENT_URI,
                toUpdateFenceContentValues(""), selection,
                new String[] { String.valueOf(fenceId) });

    }

    public static ContentValues toUpdateFenceContentValues(String fenceId) {
        ContentValues c = new ContentValues();
        c.put(EditEventHelper.EVENT_EXT_ACTION_DATA2, fenceId);
        return c;
    }

    private static final String[] LOCATION_PROJECTS = {
            Events._ID,// 0
            EditEventHelper.EVENT_EXT_EVENT_TYPE,// 1
            EditEventHelper.EVENT_EXT_ACTION_DATA,// 2
            EditEventHelper.EVENT_EXT_ACTION_DATA1,// 3
            EditEventHelper.EVENT_EXT_ACTION_DATA2,// 4

            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DESCRIPTION, };
    private final static int INDEX_EVENT_ID = 0;
    private final static int INDEX_EVENT_EXT_EVENT_TYPE = 1;
    private final static int INDEX_EVENT_EXT_ACTION_DATA = 2;
    private final static int INDEX_EVENT_EXT_ACTION_DATA1 = 3;
    private final static int INDEX_EVENT_EXT_ACTION_DATA2 = 4;
    private final static int INDEX_EVENT_TITLE = 5;
    private final static int INDEX_EVENT_LOCATION = 6;
    private final static int INDEX_EVENT_DESCRIPTION = 7;
}
