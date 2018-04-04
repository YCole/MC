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

package com.android.calendar;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.backup.BackupManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
// import android.os.SystemProperties;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.CalendarContract;
import android.provider.CalendarContract.CalendarCache;
import android.provider.SearchRecentSuggestions;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.calendar.alerts.AlertReceiver;
import com.android.calendar.smsservice.BlackListActivity;
import com.android.timezonepicker.TimeZoneInfo;
import com.android.timezonepicker.TimeZonePickerDialog;
import com.android.timezonepicker.TimeZonePickerDialog.OnTimeZoneSetListener;
import com.android.timezonepicker.TimeZonePickerUtils;
//import com.hct.calendar.weather.WeatherController;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.PreferenceUtils;

@TargetApi(23)
public class GeneralPreferences extends PreferenceActivity implements
        OnSharedPreferenceChangeListener, OnPreferenceChangeListener,
        OnTimeZoneSetListener {
    private static final String TAG = "GeneralPreferences";
    // The name of the shared preferences file. This name must be maintained for
    // historical
    // reasons, as it's what PreferenceManager assigned the first time the file
    // was created.
    public static final String SHARED_PREFS_NAME = "com.android.calendar_preferences";
    static final String SHARED_PREFS_NAME_NO_BACKUP = "com.android.calendar_preferences_no_backup";

    private static final String FRAG_TAG_TIME_ZONE_PICKER = "TimeZonePicker";

    // Preference keys
    public static final String KEY_HIDE_DECLINED = "preferences_hide_declined";
    public static final String KEY_WEEK_START_DAY = "preferences_week_start_day";
    public static final String KEY_SHOW_WEEK_NUM = "preferences_show_week_num";
    public static final String KEY_AUTO_UPDATE_FESTIVAL = "preferences_auto_download_festival";
    public static final String KEY_UPDATE_CHINESE_FESTIVAL = "preferences_manual_download_festival";
    public static final String KEY_DAYS_PER_WEEK = "preferences_days_per_week";
    public static final String KEY_SKIP_SETUP = "preferences_skip_setup";
    public static final String KEY_FIRST_LOAD = "preferences_first_load";

    public static final String KEY_SUBSCRIBE_ALMANAC = "preferences_subscribe_almanac";
    public static final String KEY_SUBSCRIBE_WEATHER = "preferences_subscribe_weather";

    public static final String KEY_CLEAR_SEARCH_HISTORY = "";// "preferences_clear_search_history";

    // public static final String KEY_ALERTS_CATEGORY =
    // "preferences_alerts_category";
    public static final String KEY_GENERAL_CATEGORY = "preferences_general_category";
    public static final String KEY_ALERTS = "preferences_alerts";
    public static final String KEY_ALERTS_VIBRATE = "preferences_alerts_vibrate";
    public static final String KEY_ALERTS_VIBRATE_WHEN = "preferences_alerts_vibrateWhen";
    public static final String KEY_ALERTS_RINGTONE = "preferences_alerts_ringtone";
    public static final String KEY_ALERTS_POPUP = "preferences_alerts_popup";
    public static final String KEY_BLACKLIST = "preference_blacklist";
    public static final String KEY_SHOW_CONTROLS = "preferences_show_controls";
    public static final String KEY_CONTACT_SYNC = "contact_sync";
    public static final String KEY_DEFAULT_REMINDER = "preferences_default_reminder";
    public static final String KEY_SMART_REMINDER = "preference_smart_reminder";
    public static final String KEY_ABOUT = "build_version";
    public static final int NO_REMINDER = -1;
    public static final String NO_REMINDER_STRING = "-1";
    public static final int REMINDER_DEFAULT_TIME = 10; // in minutes

    public static final String KEY_DEFAULT_CELL_HEIGHT = "preferences_default_cell_height";
    public static final String KEY_VERSION = "preferences_version";
    public static final String KEY_FULL_SIZE_ON_AGENDA = "preferences_full_size_no_agenda";

    /** Key to SharePreference for default view (CalendarController.ViewType) */
    public static final String KEY_START_VIEW = "preferred_startView";
    /**
     * Key to SharePreference for default detail view
     * (CalendarController.ViewType) Typically used by widget
     */
    public static final String KEY_DETAILED_VIEW = "preferred_detailedView";
    public static final String KEY_DEFAULT_CALENDAR = "preference_defaultCalendar";

    // These must be in sync with the array preferences_week_start_day_values
    public static final String WEEK_START_DEFAULT = "-1";
    public static final String WEEK_START_SATURDAY = "7";
    public static final String WEEK_START_SUNDAY = "1";
    public static final String WEEK_START_MONDAY = "2";

    private boolean mHideMenuButtons = false;

    // These keys are kept to enable migrating users from previous versions
    private static final String KEY_ALERTS_TYPE = "preferences_alerts_type";
    private static final String ALERT_TYPE_ALERTS = "0";
    private static final String ALERT_TYPE_STATUS_BAR = "1";
    private static final String ALERT_TYPE_OFF = "2";
    static final String KEY_HOME_TZ_ENABLED = "preferences_home_tz_enabled";
    static final String KEY_HOME_TZ = "preferences_home_tz";

    // Default preference values
    public static final int DEFAULT_START_VIEW = CalendarController.ViewType.MONTH;
    public static final int DEFAULT_DETAILED_VIEW = CalendarController.ViewType.AGENDA;
    public static final boolean DEFAULT_SHOW_WEEK_NUM = false;
    // This should match the XML file.
    public static final String DEFAULT_RINGTONE = "content://settings/system/notification_sound";
    public static final String WEATHER_SWITCH_KEY = "weather_switch";
    public static final String SHOWWEATHER_KEY = "weather_switch";
    SwitchPreference mAlert;
    SwitchPreference mVibrate;
    RingtonePreference mRingtone;
    SwitchPreference mUseHomeTZ;
    ListPreference mHomeTZ;
    TimeZonePickerUtils mTzPickerUtils;
    ListPreference mWeekStart;
    ListPreference mDefaultReminder;
    Preference mAbout;
    SwitchPreference mReceiverEnable;
    ListPreference mVibrateWhen;
    Preference mBlacklist;
    SwitchPreference contactsSync;
    SwitchPreference huangliSwitch;
    SwitchPreference weatherSwitch;
    // private String mTimeZoneId;
    private static CharSequence[][] mTimezones;
    private String mPreRingtone;

    /** Return a properly configured SharedPreferences instance */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    /** Set the default shared preferences in the proper context */
    public static void setDefaultValues(Context context) {
        PreferenceManager.setDefaultValues(context, SHARED_PREFS_NAME,
                Context.MODE_PRIVATE, R.xml.general_preferences, false);
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // final Activity activity = getActivity();
        getActionBar().setDisplayOptions(
                ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        // Make sure to always use the same preferences file regardless of the
        // package name
        // we're running under
        final PreferenceManager preferenceManager = getPreferenceManager();
        final SharedPreferences sharedPreferences = getSharedPreferences(this);
        preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);
        // setActionBarContentColor(getResources().getColor(R.color.cale_actionbar_icon_color),
        // getResources().getColor(R.color.cale_actionbar_text_color));
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.general_preferences);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference p = preferenceScreen.findPreference(KEY_SHOW_WEEK_NUM);
        preferenceScreen.removePreference(p);
        mAlert = (SwitchPreference) preferenceScreen.findPreference(KEY_ALERTS);
        preferenceScreen.removePreference(mAlert);
        mVibrateWhen = (ListPreference) preferenceScreen
                .findPreference(KEY_ALERTS_VIBRATE_WHEN);
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            PreferenceGroup mAlertGroup = (PreferenceGroup) preferenceScreen
                    .findPreference(KEY_GENERAL_CATEGORY);
            mAlertGroup.removePreference(mVibrateWhen);
        } else {
            mVibrateWhen.setSummary(mVibrateWhen.getEntry());
        }

        mRingtone = (RingtonePreference) preferenceScreen
                .findPreference(KEY_ALERTS_RINGTONE);
        huangliSwitch = (SwitchPreference) preferenceScreen
                .findPreference(PreferenceUtils.SHOWALMANC_KEY);
        weatherSwitch = (SwitchPreference) preferenceScreen
                .findPreference(SHOWWEATHER_KEY);
        if (CalendarUtil.isEnglish()) {
            huangliSwitch.setChecked(false);
            huangliSwitch.setEnabled(false);
            weatherSwitch.setChecked(false);
            weatherSwitch.setEnabled(false);
        }
        mUseHomeTZ = (SwitchPreference) preferenceScreen
                .findPreference(KEY_HOME_TZ_ENABLED);
        mWeekStart = (ListPreference) preferenceScreen
                .findPreference(KEY_WEEK_START_DAY);
        mDefaultReminder = (ListPreference) preferenceScreen
                .findPreference(KEY_DEFAULT_REMINDER);
        contactsSync = (SwitchPreference) preferenceScreen
                .findPreference(KEY_CONTACT_SYNC);
        mHomeTZ = (ListPreference) preferenceScreen.findPreference(KEY_HOME_TZ);
        preferenceScreen.removePreference(mUseHomeTZ);
        preferenceScreen.removePreference(mHomeTZ);
        mWeekStart.setSummary(mWeekStart.getEntry());
        mDefaultReminder.setSummary(mDefaultReminder.getEntry());
        mReceiverEnable = (SwitchPreference) preferenceScreen
                .findPreference(KEY_SMART_REMINDER);
        // boolean bSmartReminder = Utils.getConfigBool(this,
        // R.bool.smart_reminder);
        // String isAbroad = SystemProperties.get("ro.gios.custom", "home");
        Boolean isAbroad = Utils.isAbroadBranch(this);
        // if (bSmartReminder == false || isAbroad.equals("abroad")) {
        if (true) {
            PreferenceGroup mAlertGroup = (PreferenceGroup) preferenceScreen
                    .findPreference(KEY_GENERAL_CATEGORY);
            mAlertGroup.removePreference(mReceiverEnable);
        }

        mBlacklist = (Preference) preferenceScreen
                .findPreference(KEY_BLACKLIST);
        // This triggers an asynchronous call to the provider to refresh the
        // data in shared pref
        // mTimeZoneId = Utils.getTimeZone(this, null);

        SharedPreferences prefs = CalendarUtils.getSharedPreferences(this,
                Utils.SHARED_PREFS_NAME);

        // Utils.getTimeZone will return the currentTimeZone instead of the one
        // in the shared_pref if home time zone is disabled. So if home tz is
        // off, we will explicitly read it.
        // if (!prefs.getBoolean(KEY_HOME_TZ_ENABLED, false)) {
        // mTimeZoneId = prefs.getString(KEY_HOME_TZ,
        // Time.getCurrentTimezone());
        // }
        mPreRingtone = prefs.getString(KEY_ALERTS_RINGTONE, DEFAULT_RINGTONE);
        Log.d(TAG, "zxj PreRingtone=" + mPreRingtone);

        /*
         * mHideDeclined.set.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         * mUseHomeTZ.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         * mReceiverEnable.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         * mAlert.SetColor(getResources().getColor(R.color.cale_checkbox_color))
         * ;
         * mPopup.SetColor(getResources().getColor(R.color.cale_checkbox_color))
         * ; mAutoUpdateFestival.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         * mSubscribeAlmanacChb.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         * mSubscribeWeatherChb.SetColor(getResources().getColor(R.color.
         * cale_checkbox_color));
         */
        /**
         * mHomeTZ.setOnPreferenceClickListener(new OnPreferenceClickListener()
         * {
         * 
         * @Override public boolean onPreferenceClick(Preference preference) {
         *           showTimezoneDialog(); return true; } });
         */
        String tz = mHomeTZ.getValue();
        mTimezones = (new TimezoneAdapter(this, tz)).getAllTimezones();

        if (mTzPickerUtils == null) {
            mTzPickerUtils = new TimeZonePickerUtils(this);
        }
        /**
         * CharSequence timezoneName = mTzPickerUtils.getGmtDisplayName(this,
         * mTimeZoneId, System.currentTimeMillis(), false);
         * mHomeTZ.setSummary(timezoneName != null ? timezoneName :
         * mTimeZoneId);
         * 
         * TimeZonePickerDialog tzpd = (TimeZonePickerDialog)
         * this.getFragmentManager()
         * .findFragmentByTag(FRAG_TAG_TIME_ZONE_PICKER); if (tzpd != null) {
         * tzpd.setOnTimeZoneSetListener(this); }
         */
        mHomeTZ.setEntryValues(mTimezones[0]);
        mHomeTZ.setEntries(mTimezones[1]);
        CharSequence tzName = mHomeTZ.getEntry();
        if (TextUtils.isEmpty(tzName)) {
            tzName = Utils.getTimeZone(this, null);
        }
        if (isAbroad) {
            mHomeTZ.setSummary(null);
        } else {
            mHomeTZ.setSummary(tzName);
        }

        migrateOldPreferences(sharedPreferences);

        updateChildPreferences();

        preferenceScreen.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        setPreferenceListeners(this);
    }

    private void showTimezoneDialog() {
        // final Activity activity = getActivity();
        if (this == null) {
            return;
        }

        Bundle b = new Bundle();
        b.putLong(TimeZonePickerDialog.BUNDLE_START_TIME_MILLIS,
                System.currentTimeMillis());
        b.putString(TimeZonePickerDialog.BUNDLE_TIME_ZONE,
                Utils.getTimeZone(this, null));

        FragmentManager fm = this.getFragmentManager();
        TimeZonePickerDialog tzpd = (TimeZonePickerDialog) fm
                .findFragmentByTag(FRAG_TAG_TIME_ZONE_PICKER);
        if (tzpd != null) {
            tzpd.dismiss();
        }
        tzpd = new TimeZonePickerDialog();
        tzpd.setArguments(b);
        tzpd.setOnTimeZoneSetListener(this);
        tzpd.show(fm, FRAG_TAG_TIME_ZONE_PICKER);
    }

    @Override
    public void onStart() {
        super.onStart();
        // getPreferenceScreen().getSharedPreferences()
        // .registerOnSharedPreferenceChangeListener(this);
        setPreferenceListeners(this);
        PreferenceGroup mAlertGroup = (PreferenceGroup) getPreferenceScreen()
                .findPreference(KEY_GENERAL_CATEGORY);

    }

    /**
     * Sets up all the preference change listeners to use the specified
     * listener.
     */
    private void setPreferenceListeners(OnPreferenceChangeListener listener) {
        mUseHomeTZ.setOnPreferenceChangeListener(listener);
        mHomeTZ.setOnPreferenceChangeListener(listener);
        mWeekStart.setOnPreferenceChangeListener(listener);
        mDefaultReminder.setOnPreferenceChangeListener(listener);
        mRingtone.setOnPreferenceChangeListener(listener);
        // mVibrate.setOnPreferenceChangeListener(listener);
        mVibrateWhen.setOnPreferenceChangeListener(listener);
        mReceiverEnable.setOnPreferenceChangeListener(listener);
        contactsSync.setOnPreferenceChangeListener(listener);
    }

    @Override
    public void onStop() {
        // getPreferenceScreen().getSharedPreferences()
        // .unregisterOnSharedPreferenceChangeListener(this);
        // setPreferenceListeners(null);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        // Activity a = getActivity();
        if (key.equals(KEY_ALERTS)) {
            updateChildPreferences();
            if (this != null) {
                Intent intent = new Intent();
                intent.setClass(this, AlertReceiver.class);
                if (mAlert.isChecked()) {
                    intent.setAction(AlertReceiver.ACTION_DISMISS_OLD_REMINDERS);
                } else {
                    intent.setAction(AlertReceiver.EVENT_REMINDER_APP_ACTION);
                }
                this.sendBroadcast(intent);
            }
        }
        if (this != null) {
            BackupManager.dataChanged(this.getPackageName());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_add_account) {
            Intent nextIntent = new Intent(Settings.ACTION_ADD_ACCOUNT);
            final String[] array = { "com.android.calendar" };
            nextIntent.putExtra(Settings.EXTRA_AUTHORITIES, array);
            nextIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(nextIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mHideMenuButtons) {
            // getMenuInflater().inflate(R.menu.settings_title_bar, menu);
        }
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);
        // setIndicatorColor(getResources().getColor(R.color.cale_actionbar_background_color));
        getActionBar().setDisplayShowHomeEnabled(false);
        return true;
    }

    /**
     * Handles time zone preference changes
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == contactsSync) {
            Log.d("gomeliao", preference.getKey());
            if (contactsSync.isChecked() != (Boolean) newValue) {
                boolean value = (Boolean) (newValue);
                Log.d("gomeliao", "set: " + value);
                if (value) {
                    Settings.System.putInt(getApplicationContext()
                            .getContentResolver(), "openConcatsSync", 0);
                    // syncCotactsBirthday();
                } else {
                    Settings.System.putInt(getApplicationContext()
                            .getContentResolver(), "openConcatsSync", 1);
                    enSyncCotactsBirthday();
                }
                try {
                    int a = Settings.System.getInt(getApplicationContext()
                            .getContentResolver(), "openConcatsSync");
                    Log.d("gomeliao", "openConcatsSync: " + a);
                } catch (SettingNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                contactsSync.setChecked(value);
            }
            return true;
        }
        String tz;
        // final Activity activity = getActivity();
        if (preference == mUseHomeTZ) {
            if ((Boolean) newValue) {
                tz = mHomeTZ.getValue();
            } else {
                tz = CalendarCache.TIMEZONE_TYPE_AUTO;
            }
            Utils.setTimeZone(this, tz);
            return true;
        } /*
           * else if (preference == mHideDeclined) {
           * mHideDeclined.setChecked((Boolean) newValue); Intent intent = new
           * Intent(Utils.getWidgetScheduledUpdateAction(this));
           * intent.setDataAndType(CalendarContract.CONTENT_URI,
           * Utils.APPWIDGET_DATA_TYPE); this.sendBroadcast(intent); return
           * true; }
           */else if (preference == mHomeTZ) {
            tz = (String) newValue;
            // We set the value here so we can read back the entry
            mHomeTZ.setValue(tz);
            mHomeTZ.setSummary(mHomeTZ.getEntry());
            Utils.setTimeZone(this, tz);
        } else if (preference == mWeekStart) {
            mWeekStart.setValue((String) newValue);
            mWeekStart.setSummary(mWeekStart.getEntry());
        } else if (preference == mDefaultReminder) {
            mDefaultReminder.setValue((String) newValue);
            mDefaultReminder.setSummary(mDefaultReminder.getEntry());
        } else if (preference == mRingtone) {
            // Log.d(TAG,"lxg (String) newValue = " + (String) newValue);
            // if (newValue instanceof String) {
            // Utils.setRingTonePreference(this, (String) newValue);
            // String ringtone = getRingtoneTitleFromUri(this, (String)
            // newValue);
            // mRingtone.setSummary(ringtone == null ? "" : ringtone);
            // }
            if (newValue instanceof String) {
                Utils.setRingTonePreference(this, (String) newValue);
                String newRingtong = (String) newValue;
                Log.d(TAG,
                        "newValue: "
                                + newRingtong
                                + ", is 'external' prefix: "
                                + newRingtong
                                        .startsWith("content://media/external")
                                + ", isEmpty: "
                                + TextUtils.isEmpty(newRingtong));
                if (!TextUtils.isEmpty(newRingtong)
                        && (newRingtong.startsWith("content://media/external"))) {
                    Utils.checkAndRequestPermission(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE);
                }
            }

            return true;
        } else if (preference == mVibrateWhen) {
            Log.d(TAG, "mVibrateWhen(String) newValue = " + (String) newValue);
            mVibrateWhen.setValue((String) newValue);
            mVibrateWhen.setSummary(mVibrateWhen.getEntry());
        }
        /**
         * else if (preference == mVibrate) { mVibrate.setChecked((Boolean)
         * newValue); return true; }
         */
        else if (preference == mReceiverEnable) {
            showAletDialog();
        } else {
            return true;
        }

        return false;
    }

    private void enSyncCotactsBirthday() {
        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        cr.delete(uri, "original_sync_id=?", new String[] { "sync" });
    }

    /**
     * private void syncCotactsBirthday() { ContentResolver cr =
     * getContentResolver(); Uri uri = ContactsContract.Data.CONTENT_URI; String
     * [] projection = new
     * String[]{ContactsContract.CommonDataKinds.Event.DATA,ContactsContract
     * .Contacts.DISPLAY_NAME}; String selection =
     * ContactsContract.Contacts.Data.MIMETYPE+"='"+
     * ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE+"'"+" and "+
     * ContactsContract.CommonDataKinds.Event.TYPE+"='"+
     * ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY+"'"; Cursor cursor =
     * cr.query(uri, projection, selection, null, null);
     * List<ContactsContract.Contacts> listcontacts = new
     * ArrayList<ContactsContract.Contacts>(); if(cursor!=null){
     * if(cursor.moveToFirst()){ do{ String birthday = cursor.getString(0);
     * String name = cursor.getString(1); SimpleDateFormat format = new
     * SimpleDateFormat("yyyy-MM-dd"); try { Date date = format.parse(birthday);
     * Calendar cal = Calendar.getInstance(); cal.setTime(date); int month =
     * cal.get(Calendar.MONTH); int day = cal.get(Calendar.DAY_OF_MONTH);
     * Calendar birthdayCalendar = Calendar.getInstance(); int year =
     * birthdayCalendar.get(Calendar.YEAR);
     * Log.d("liaozhenbin",year+"-"+month+"-"+day);
     * birthdayCalendar.set(year,month,day); long birthdayTime =
     * birthdayCalendar.getTimeInMillis(); EventBean bean = new EventBean();
     * bean.title =
     * CalendarApplication.getContext().getString(R.string.event_birth_front
     * )+name; bean.end = birthdayTime; bean.start = birthdayTime; bean.sync =
     * "sync"; //fushuo begin bean.color=Constant.BIRTHDAY;
     * EventUtils.addBirthdayEvent(getApplicationContext(), bean); } catch
     * (ParseException e) { e.printStackTrace(); }
     * 
     * }while(cursor.moveToNext()); } } }
     */

    private void showAletDialog() {
        if (mReceiverEnable.isChecked()) {
            mReceiverEnable.setChecked(false);
            return;
        } else {
            mReceiverEnable.setChecked(true);
        }

        final SharedPreferences spDefault = this.getSharedPreferences(
                "Default_Smart_Reminder", Context.MODE_PRIVATE);
        if (spDefault.getInt("First", 100) != 100)
            return;

        View v = this.getLayoutInflater().inflate(R.layout.reminder_dialog,
                null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.smart_reminder_title)
                .setView(v)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                                // finish();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                mReceiverEnable.setChecked(false);
                                // finish();
                            }
                        }).create();
        dialog.show();

        CheckBox onButton = (CheckBox) v.findViewById(R.id.reminderButton);
        onButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                if (isChecked) {
                    SharedPreferences.Editor ed = spDefault.edit();
                    ed.putInt("First", 200);
                    ed.commit();
                }
            }
        });
    }

    public String getRingtoneTitleFromUri(Context context, String uri) {
        if (TextUtils.isEmpty(uri)) {
            return null;
        }

        Ringtone ring = RingtoneManager.getRingtone(this, Uri.parse(uri));
        if (ring != null) {
            return ring.getTitle(context);
        }
        return null;
    }

    /**
     * If necessary, upgrades previous versions of preferences to the current
     * set of keys and values.
     * 
     * @param prefs
     *            the preferences to upgrade
     */
    private void migrateOldPreferences(SharedPreferences prefs) {
        // If needed, migrate vibration setting from a previous version

        // mVibrate.setChecked(Utils.getDefaultVibrate(this, prefs));
        if (!prefs.contains(KEY_ALERTS_VIBRATE_WHEN)
                && prefs.contains(KEY_ALERTS_VIBRATE)) {
            int stringId = prefs.getBoolean(KEY_ALERTS_VIBRATE, false) ? R.string.prefDefault_alerts_vibrate_true
                    : R.string.prefDefault_alerts_vibrate_false;
            Log.d(TAG, "lxg prefs.getBoolean(KEY_ALERTS_VIBRATE, false) = "
                    + prefs.getBoolean(KEY_ALERTS_VIBRATE, false));
            mVibrateWhen.setValue(this.getString(stringId));
        }

        // If needed, migrate the old alerts type setting
        if (!prefs.contains(KEY_ALERTS) && prefs.contains(KEY_ALERTS_TYPE)) {
            String type = prefs.getString(KEY_ALERTS_TYPE,
                    ALERT_TYPE_STATUS_BAR);
            if (type.equals(ALERT_TYPE_OFF)) {
                mAlert.setChecked(false);
            } else if (type.equals(ALERT_TYPE_STATUS_BAR)) {
                mAlert.setChecked(true);
            } else if (type.equals(ALERT_TYPE_ALERTS)) {
                mAlert.setChecked(true);
            }
            // clear out the old setting
            prefs.edit().remove(KEY_ALERTS_TYPE).commit();
        }
    }

    /**
     * Keeps the dependent settings in sync with the parent preference, so for
     * example, when notifications are turned off, we disable the preferences
     * for configuring the exact notification behavior.
     */
    private void updateChildPreferences() {
        if (mAlert.isChecked()) {
            // mVibrate.setEnabled(true);
            mVibrateWhen.setEnabled(true);
            mRingtone.setEnabled(true);
        } else {
            // mVibrate.setEnabled(false);
            mVibrateWhen.setSummary(mVibrateWhen.getEntry());
            // Log.v("allen",(String)mVibrateWhen.getSummary());
            mVibrateWhen.setEnabled(false);
            mRingtone.setEnabled(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        final String key = preference.getKey();
        if (KEY_CLEAR_SEARCH_HISTORY.equals(key)) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this, Utils.getSearchAuthority(this),
                    CalendarRecentSuggestionsProvider.MODE);
            suggestions.clearHistory();
            Toast.makeText(this, R.string.search_history_cleared,
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (preference == mBlacklist) {
            Intent intent = new Intent(this, BlackListActivity.class);
            startActivity(intent);
            return true;
        } else if (preference == mAbout) {
            // Intent intent = new Intent(this, AboutPreferences.class);
            // startActivity(intent);
            // mAbout.setSummary(mAbout.getEntry());
            return true;
        } else {
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }
    }

    @Override
    public void onTimeZoneSet(TimeZoneInfo tzi) {
        if (mTzPickerUtils == null) {
            mTzPickerUtils = new TimeZonePickerUtils(this);
        }

        final CharSequence timezoneName = mTzPickerUtils.getGmtDisplayName(
                this, tzi.mTzId, System.currentTimeMillis(), false);
        mHomeTZ.setSummary(timezoneName);
        Utils.setTimeZone(this, tzi.mTzId);
    }

    @Override
    public void onDestroy() {
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        setPreferenceListeners(null);
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case Utils.REQUEST_PERMISSIONS_EXTERNAL_STORAGE:
            if (grantResults != null && grantResults.length > 0) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    SharedPreferences prefs = getSharedPreferences(this);
                    CalendarUtils.setSharedPreference(prefs,
                            KEY_ALERTS_RINGTONE, mPreRingtone);
                    String permissionName = getResources().getString(
                            R.string.permissions_name_storage);
                    Toast.makeText(
                            this,
                            this.getString(R.string.permissions_denied_tip,
                                    permissionName), Toast.LENGTH_LONG).show();
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
