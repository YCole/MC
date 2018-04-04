package com.android.calendar.alerts;

import java.io.IOException;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.android.calendar.CalendarApplication;
import com.android.calendar.CalendarSettings;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.Utils;
import com.apkfuns.logutils.LogUtils;

/**
 * @apiNote: This is a help for play Sound ! when send event notification !
 * 
 */
public class ClassToPlayAlert {
    private static final int FOCUSCHANGE = 0x0100;
    private AudioManager mAudioManager;
    private static MediaPlayer mMediaPlayer;
    private Context mcontext;
    private OnAudioFocusChangeListener mAudioFocusListener;
    private static final String TAG = "AlertService";
    private RingToneThread mThread;

    public ClassToPlayAlert(Context context) {
        mcontext = context;
        mAudioManager = (AudioManager) context
                .getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = new OnAudioFocusChangeListener() {
            public void onAudioFocusChange(int focusChange) {
                Log.i(TAG, "lxg focusChange = " + focusChange);
                abandonFocus();
                // stopAlert();
                // mServiceHandler.obtainMessage(FOCUSCHANGE, focusChange,
                // 0).sendToTarget();
            }
        };
    }

    public void requestFocus() {
        int result = mAudioManager.requestAudioFocus(mAudioFocusListener,
                AudioManager.STREAM_NOTIFICATION,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            playAlert(mcontext);
        }

    }

    public void abandonFocus() {
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        stopAlert();
    }

    private void playAlert(Context context) {
        SharedPreferences prefs = GeneralPreferences
                .getSharedPreferences(context);
        int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

        // String reminderRingtone = prefs.getString(
        // GeneralPreferences.KEY_ALERTS_RINGTONE, null);
        SharedPreferences sp = CalendarApplication.getContext()
                .getSharedPreferences(CalendarSettings.SETTINGS_SP_NAME,
                        Context.MODE_PRIVATE);
        // ringtone = Utils.getRingTonePreference(context);
        String reminderRingtone = sp.getString("ringtone_uri", "");

        Uri mUri;
        String mediaUri = getMediaUri(context, Uri.parse(reminderRingtone));

        if (mediaUri == null && !TextUtils.isEmpty(reminderRingtone)) {
            // mUri =
            // RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                    RingtoneManager.TYPE_NOTIFICATION);
        } else {
            mUri = TextUtils.isEmpty(reminderRingtone) ? null : Uri
                    .parse(reminderRingtone);
        }
        Log.i(TAG, "lxg mUri = " + mUri);
        if (mUri == null) {
            return;
        }
        if (volume > 0) {
            try {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setOnErrorListener(new OnErrorListener() {
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e(TAG, "Error occurred while playing audio.");
                        if (mMediaPlayer != null) {
                            mMediaPlayer.stop();
                            mMediaPlayer.release();
                            mMediaPlayer = null;
                        }
                        return true;
                    }
                });

                if (mMediaPlayer == null) {
                    Log.i(TAG, "mMediaPlayer = null");
                }
                mMediaPlayer.reset();
                mMediaPlayer.setVolume(volume, volume);
                mMediaPlayer.setDataSource(context, mUri);
                startAlert();
            } catch (IOException ex) {
                Log.e(TAG, "playAlert IOException ");
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "playAlert IllegalArgumentException ");
            } catch (IllegalStateException ie) {
                Log.e(TAG, "playAlert IllegalStateException ");
            }
        }
    }

    private void startAlert() throws java.io.IOException,
            IllegalArgumentException, IllegalStateException {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        // mAudioManager.requestAudioFocus(mAudioFocusListener,
        // AudioManager.STREAM_NOTIFICATION,
        // AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        mMediaPlayer.setLooping(false);
        mMediaPlayer.prepare();
        Log.e("byd123", "aaaa---->start");
        mMediaPlayer.start();
        mThread = new RingToneThread();
        mThread.start();

    }

    class RingToneThread extends Thread {
        public volatile boolean exit = false;

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while (!exit) {
                try {
                    sleep(1000);
                    Log.e("byd123", "aaaa---->finish");
                    mAudioManager.abandonAudioFocus(mAudioFocusListener);
                    mThread.exit = true;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

        }
    }

    public void stopAlert() {
        if (mMediaPlayer == null)
            return;
        if (!(mMediaPlayer instanceof MediaPlayer))
            return;
        try {
            mMediaPlayer.stop();
        } catch (Throwable e) {
            // throws IllegalStateException
            LogUtils.e("catched exception: " + e.getMessage());
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
        // mThread.stop();
        // mThread.destroy();
        try {
            // stop thread
            if (null != mThread) {
                mThread.exit = true;
                mThread.join();
            }

        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void pauseAlert() {
        if (mMediaPlayer == null)
            return;
        if (!(mMediaPlayer instanceof MediaPlayer))
            return;
        try {
            mMediaPlayer.pause();
        } catch (Throwable e) {
            // void MediaPlayer#pause() throws IllegalStateException
            LogUtils.e("catched exception: " + e.getMessage());
        }
        mAudioManager.abandonAudioFocus(mAudioFocusListener);
    }

    private String getMediaUri(Context context, Uri uri) {
        String path = null;
        try {
            if ((uri.toString()).startsWith("content://media")) {
                ContentResolver mContentResolver = context.getContentResolver();
                Cursor cursor = null;
                String sUri = uri.toString();
                if (Utils.checkSelfPermission(context.getApplicationContext(),
                        Manifest.permission.READ_CALENDAR)) {
                    if (context instanceof Activity) {
                        if ((!sUri.startsWith("content://media/external"))
                                || (sUri.startsWith("content://media/external") && Utils
                                        .checkAndRequestPermission(
                                                (Activity) context,
                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                Utils.REQUEST_PERMISSIONS_NULL))) {
                            cursor = mContentResolver.query(uri, null, null,
                                    null, null);
                        } else {
                            Log.i(TAG, "2. sUri = " + sUri);
                        }
                    } else {
                        cursor = mContentResolver.query(uri, null, null, null,
                                null);
                    }
                }
                if (cursor != null) {
                    if (cursor.getCount() == 1) {
                        // get the content's file path if possible
                        cursor.moveToFirst();
                        try {
                            path = cursor.getString(cursor
                                    .getColumnIndexOrThrow("_data"));
                        } catch (IllegalArgumentException e) {
                            path = null;
                        }
                    }
                    cursor.close();
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "2. permission denied: " + uri.toString()
                    + ", Exception: " + e);
        }
        return path;
    }
}
