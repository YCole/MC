/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.calendar.utils;

import java.io.File;

import android.content.Context;
//zhangping add to fix CRDB00485831
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
//import android.util.Log;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;

import com.android.calendar.R;
import com.hct.gios.preference.RingtonePreference;

public class AlarmPreference extends RingtonePreference {
    public Uri mAlert;
    private IRingtoneChangedListener mRingtoneChangedListener;

    private Context mContext;
    private static String mDefaultRing = "content://media/internal/audio/media/107";

    //
    public interface IRingtoneChangedListener {
        public void onRingtoneChanged(Uri ringtoneUri);
    };

    public AlarmPreference(Context context) {
        this(context, null);

        mContext = context;
    }

    public AlarmPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        // setLayoutResource(layoutResId);

    }

    public void setRingtoneChangedListener(IRingtoneChangedListener listener) {
        mRingtoneChangedListener = listener;
    }

    @Override
    protected void onSaveRingtone(Uri ringtoneUri) {
        if (ringtoneUri != null) {
            mAlert = ringtoneUri;
            if (mRingtoneChangedListener != null) {
                mRingtoneChangedListener.onRingtoneChanged(ringtoneUri);
            }
        }
    }

    @Override
    protected Uri onRestoreRingtone() {
        // return mAlert;
        return getActualDefaultRingtoneUri(getContext(), mAlert, "");
    }

    // zhangping add
    @Override
    public void onPrepareRingtonePickerIntent(Intent ringtonePickerIntent) {
        super.onPrepareRingtonePickerIntent(ringtonePickerIntent);

        // Log.i("AlarmPreference","onPrepareRingtonePickerIntent().mShowDefault
        // = "+mShowDefault);
        ringtonePickerIntent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        // ringtonePickerIntent.putExtra("android.intent.extra.ringtone.SHOW_EXTERNAL",
        // true);
        ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_NOTIFICATION);
        ringtonePickerIntent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                mContext.getResources().getString(
                        R.string.preferences_alerts_ringtone_title));
        // if (mShowDefault) {
        // SharedPreferences mSharedPrefs;
        // mSharedPrefs = mContext.getSharedPreferences(AlarmClock.PREFERENCES,
        // 0);
        // Uri mUri = Uri.parse(mSharedPrefs.getString("default_alarm",
        // Settings.System.DEFAULT_RINGTONE_URI.toString()));
        // Uri mUri =
        // Uri.parse(Settings.System..toString());
        // ringtonePickerIntent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI,
        // mUri);
        // }
    }

    //
    // add for poweroff alarm when ring is extern -S-
    public static boolean isExternalSong(Context context, Uri mAlert) {
        Uri uri = mAlert;
        String volume = uri.getPathSegments().get(0);
        final String EXTERNAL_VOLUME = "external";
        if (EXTERNAL_VOLUME.equals(volume)) {
            return true;
        } else {
            return false;
        }

    }

    public static String getSongFilepath(Context context, Uri mAlert) {
        String[] projections = new String[] { "_id", "_data" };

        Cursor cursor = context.getContentResolver().query(mAlert, projections,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(1);
            cursor.close();
            return path;
        }
        if (cursor != null)
            cursor.close();
        return null;
    }

    // add for poweroff alarm when ring is extern -E-

    public static Uri getDefalutRingtoneUri(Context context) {
        Uri uri = null;
        Settings.System.putString(context.getContentResolver(),
                Settings.System.ALARM_ALERT, mDefaultRing);
        String alarmRingtone = Settings.System.getString(
                context.getContentResolver(), Settings.System.ALARM_ALERT);
        if (alarmRingtone == null || alarmRingtone.length() == 0) {
            alarmRingtone = "content://media/internal/audio/media/107";
        }
        uri = Uri.parse(alarmRingtone);
        return uri;
    }

    public static Uri getActualDefaultRingtoneUri(Context context, Uri mAlert,
            String filepath) {
        Uri uri = mAlert;
        if (uri == null || (uri != null && uri.toString().length() == 0)) {
            return null;
        }
        String volume = uri.getPathSegments().get(0);
        final String EXTERNAL_VOLUME = "external";
        if (EXTERNAL_VOLUME.equals(volume)) {
            if (!isRingToneExist(context, uri)) {
                // uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                if (isFilePathExist(filepath)) {
                    uri = getFilepathNewUri(context, filepath);
                    return uri;
                }
                String alarmRingtone = Settings.System.getString(
                        context.getContentResolver(),
                        Settings.System.ALARM_ALERT);
                if (alarmRingtone == null || alarmRingtone.length() == 0) {
                    alarmRingtone = "content://media/internal/audio/media/107";
                }
                uri = Uri.parse(alarmRingtone);
                if (isRingToneExist(context, uri)) {// modify by lijuan.dong for
                                                    // GMOS-1846
                    uri = getOriginalDefaultRingtoneUri(context);
                }
            }
        }
        return uri;
    }

    private static boolean isRingToneExist(Context context, Uri uri) {
        if (uri == null) {
            return false;
        }
        int songid = Integer.parseInt(uri.getPathSegments().get(3));
        String where = "_id=" + songid;
        Cursor cursor;
        cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, where, null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        boolean bExist = false;
        if (cursor != null) {
            bExist = (cursor.getCount() > 0);
            cursor.close();
        } else {
        }
        return bExist;
    }

    private static boolean isFilePathExist(String filepath) {
        try {

            File f = new File(filepath);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static Uri getFilepathNewUri(Context context, String filepath) {
        String ringtoneId;
        String ringtoneUri = null;
        Uri uri = null;
        // uri=content://media/external/audio/media/5832
        /*
         * Cursor ringtoneCursor = context .getContentResolver()
         * .query(Uri.parse("content://media/external/audio/media"), new
         * String[] { "_id", "_data" }, "_data=?", new String[] {filepath},
         * null);
         */

        String whereClause = MediaStore.Audio.Media.DATA + " LIKE '%"
                + filepath + "%'";

        Cursor ringtoneCursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, whereClause, null,
                null);
        try {
            if (ringtoneCursor != null && ringtoneCursor.moveToFirst()) {
                ringtoneId = ringtoneCursor.getString(ringtoneCursor
                        .getColumnIndexOrThrow("_id"));
                ringtoneUri = "content://media/external/audio/media" + "/"
                        + ringtoneId;
                uri = Uri.parse(ringtoneUri);
            }
        } catch (Exception e) {

        } finally {
            if (ringtoneCursor != null) {
                ringtoneCursor.close();
            }
        }
        if (uri == null) {
            // uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
        }
        return uri;
    }

    private static Uri getOriginalDefaultRingtoneUri(Context context) {
        String ringtoneId;
        String ringtoneUri = null;
        Uri uri = null;
        Cursor ringtoneCursor = context
                .getContentResolver()
                .query(Uri.parse("content://media/internal/audio/media"),
                        new String[] { "_id", "_data" },
                        "_data=?",
                        new String[] { "/system/media/audio/alarms/The Dawn Of The Morning.ogg" },
                        null);
        try {
            if (ringtoneCursor != null && ringtoneCursor.moveToFirst()) {
                ringtoneId = ringtoneCursor.getString(ringtoneCursor
                        .getColumnIndexOrThrow("_id"));
                ringtoneUri = "content://media/internal/audio/media" + "/"
                        + ringtoneId;
                uri = Uri.parse(ringtoneUri);
            }
        } catch (Exception e) {

        } finally {
            if (ringtoneCursor != null) {
                ringtoneCursor.close();
            }
        }
        if (uri == null) {
            // uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
        }
        return uri;
    }

}
