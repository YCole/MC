package com.android.calendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.calendar.alerts.AlertService;
import com.android.calendar.event.Constant;
import com.android.calendar.event.EventBean;
import com.android.calendar.utils.AlarmPreference;
import com.android.calendar.utils.EventUtils;
import gm.app.GomeAlertDialog;
import gm.widget.GomeSwitch;
import com.hct.calendar.utils.CalendarUtil;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CalendarSettings extends Activity implements OnClickListener, OnSharedPreferenceChangeListener {
    private ImageView iv_back;
    private TextView tv_week_summary;
    private SharedPreferences sp;
    private String[] weekArray;
    private TextView tv_vibrate_summary;
    private String[] vibrateArray;
    private TextView tv_alert_summary;
    private String[] alertArray;
    private GomeSwitch gs_contact;
    private GomeSwitch gs_almanac;
    private GomeSwitch gs_weather;
    private GomeSwitch gs_vibrate;
    private ViewGroup layout_sms;
    private ViewGroup layout_contact;
    private ViewGroup layout_almanac;
    private ViewGroup layout_weather;
    private ViewGroup layout_week;
    private ViewGroup layout_account;
    private ViewGroup layout_ring;
    private ViewGroup layout_vibrate_temp;
    // private ViewGroup layout_vibrate;
    private ViewGroup layout_alert;
    private ImageView iv_sms;
    private ImageView iv_week;
    private ImageView iv_account;
    private ImageView iv_ring;
    private ImageView iv_vibrate;
    private ImageView iv_alert;
    public static final int CONTACT = 0;
    public static final int ALMANAC = 1;
    public static final int WEATHER = 2;
    public static final int VIBRATE = 3;
    public static final String SETTINGS_SP_NAME = "settings";
    private int currentType = 0;
    private int selectedRingtone = 0;
    private ArrayList<String> titleLists;
    private static final String[] permissionsArray = new String[] { Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS };
    private List<String> permissionsList = new ArrayList<String>();
    private boolean isGetPermission;

    // private String selectedRingtoneUri =
    // "content://settings/system/notification_sound";

    String selectedRingtoneUri;
    private String mDefaultRingtoneUri;

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String[] vibrate_list = new String[] { "always", "silent", "never" };
    private int requestCode = 10;
    final static int SET_MORE = 123;
    private AlarmPreference mAlarmPref;
    private TextView tv_ring_name;
    private ViewGroup layout_ring_none;
    private ImageView iv_ring_none;
    private View line_almanac;
    private View line_weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_settings);
        // StatusBarUtils.setStatusBarLightMode(this,
        // Color.parseColor("#f1f2f3"));
        mAlarmPref = (AlarmPreference) new AlarmPreference(CalendarSettings.this);
        initViews();
        initListeners();
        initDefaultData();

    }

    private void initViews() {
        // TODO Auto-generated method stub
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_sms = (ImageView) findViewById(R.id.iv_sms);
        iv_week = (ImageView) findViewById(R.id.iv_week);
        iv_account = (ImageView) findViewById(R.id.iv_account);
        iv_ring = (ImageView) findViewById(R.id.iv_ring);
        iv_vibrate = (ImageView) findViewById(R.id.iv_vibrate);
        iv_alert = (ImageView) findViewById(R.id.iv_alert);
        tv_week_summary = (TextView) findViewById(R.id.tv_week_summary);
        tv_vibrate_summary = (TextView) findViewById(R.id.tv_vibrate_summary);
        tv_alert_summary = (TextView) findViewById(R.id.tv_alert_summary);
        gs_contact = (GomeSwitch) findViewById(R.id.gs_contact);
        gs_almanac = (GomeSwitch) findViewById(R.id.gs_almanac);
        gs_weather = (GomeSwitch) findViewById(R.id.gs_weather);
        gs_vibrate = (GomeSwitch) findViewById(R.id.gs_vibrate);
        layout_sms = (ViewGroup) findViewById(R.id.layout_sms);
        layout_contact = (ViewGroup) findViewById(R.id.layout_contact);
        layout_almanac = (ViewGroup) findViewById(R.id.layout_almanac);
        layout_weather = (ViewGroup) findViewById(R.id.layout_weather);
        layout_week = (ViewGroup) findViewById(R.id.layout_week);
        layout_account = (ViewGroup) findViewById(R.id.layout_account);
        layout_ring = (ViewGroup) findViewById(R.id.layout_ring);
        // layout_vibrate = (ViewGroup) findViewById(R.id.layout_vibrate);
        layout_alert = (ViewGroup) findViewById(R.id.layout_alert);
        layout_vibrate_temp = (ViewGroup) findViewById(R.id.layout_vibrate_temp);
        tv_ring_name = (TextView) findViewById(R.id.tv_ring_name);
        layout_ring_none = (ViewGroup) findViewById(R.id.layout_ring_none);
        iv_ring_none = (ImageView) findViewById(R.id.iv_ring_none);
        line_almanac = (View) findViewById(R.id.line_almanac);
        line_weather = (View) findViewById(R.id.line_weather);

    }

    private void initListeners() {
        // TODO Auto-generated method stub
        iv_back.setOnClickListener(this);
        iv_sms.setOnClickListener(this);
        iv_week.setOnClickListener(this);
        iv_account.setOnClickListener(this);
        iv_ring.setOnClickListener(this);
        iv_vibrate.setOnClickListener(this);
        iv_alert.setOnClickListener(this);
        layout_sms.setOnClickListener(this);
        layout_week.setOnClickListener(this);
        layout_account.setOnClickListener(this);
        layout_ring.setOnClickListener(this);
        // layout_vibrate.setOnClickListener(this);
        layout_alert.setOnClickListener(this);
        layout_contact.setOnClickListener(this);
        layout_almanac.setOnClickListener(this);
        layout_weather.setOnClickListener(this);
        layout_vibrate_temp.setOnClickListener(this);
        iv_ring_none.setOnClickListener(this);
        layout_ring_none.setOnClickListener(this);
        gs_contact.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = CONTACT;
                Log.e("fushuo", "isChecked=" + isChecked);
                updateGomeSwitch(currentType, isChecked);
            }
        });
        gs_almanac.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = ALMANAC;
                updateGomeSwitch(currentType, isChecked);
            }
        });
        gs_weather.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = WEATHER;
                updateGomeSwitch(currentType, isChecked);
            }
        });
        gs_vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                // TODO Auto-generated method stub
                updateGomeSwitch(VIBRATE, isChecked);
            }
        });
    }

    private void initDefaultData() {
        mDefaultRingtoneUri = RingtoneManager.getOriginNotificationRingtone(getApplicationContext()).toString();
        sp = getSharedPreferences(SETTINGS_SP_NAME, Context.MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        Uri defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        String defaultRingtoneString;
        if (null != defaultRingtoneUri && !TextUtils.isEmpty(AlertService.getMediaUri(this, defaultRingtoneUri))) {
            defaultRingtoneString = defaultRingtoneUri.toSafeString();
        } else {
            defaultRingtoneString = mDefaultRingtoneUri;
        }

        selectedRingtoneUri = sp.getString("ringtone_uri", defaultRingtoneString);

        if (!selectedRingtoneUri.equals("")&&TextUtils.isEmpty(AlertService.getMediaUri(this, Uri.parse(selectedRingtoneUri)))) {
            selectedRingtoneUri = defaultRingtoneString;
        }

        // TODO Auto-generated method stub
        if (CalendarUtil.isEnglish()) {
            gs_almanac.setEnabled(false);
            //gs_weather.setEnabled(false);
            layout_almanac.setVisibility(View.GONE);
            line_almanac.setVisibility(View.GONE);
            //layout_weather.setVisibility(View.GONE);
            //line_weather.setVisibility(View.GONE);
        } else {
            gs_almanac.setEnabled(true);
            gs_weather.setEnabled(true);
            layout_almanac.setVisibility(View.VISIBLE);
            line_almanac.setVisibility(View.VISIBLE);
            layout_weather.setVisibility(View.VISIBLE);
            line_weather.setVisibility(View.VISIBLE);
        }

        weekArray = getResources().getStringArray(R.array.preferences_week_start_day_labels);
        vibrateArray = getResources().getStringArray(R.array.prefEntries_alerts_vibrateWhen);
        alertArray = getResources().getStringArray(R.array.preferences_default_reminder_labels);
        if (sp.contains("preferences_week_start_day")) {
            Log.e("fushuo123", "sp");
            tv_week_summary.setText(weekArray[sp.getInt("preferences_week_start_day", 0)]);
            tv_vibrate_summary.setText(vibrateArray[sp.getInt("vibrate_index", 2)]);
            tv_alert_summary.setText(alertArray[sp.getInt("alert_index", 3)]);
            Log.e("mmm", "" + sp.getBoolean("iscontactasync", false));
            gs_contact.setChecked(sp.getBoolean("iscontactasync", true));
            gs_almanac.setChecked(sp.getBoolean("isshowalmanac", true));
            gs_weather.setChecked(sp.getBoolean("isshowweather", true));
            // selectedRingtoneUri = sp.getString("ringtone_uri",
            // "content://settings/system/notification_sound");
            // Log.d("PRODUCTION-2877", "zyp ---4---" + selectedRingtoneUri);
        } else {
            Log.e("fushuo123", "default");
            tv_week_summary.setText(weekArray[0]);
            sp.edit().putInt("preferences_week_start_day", 0).commit();
            tv_vibrate_summary.setText(vibrateArray[2]);
            tv_alert_summary.setText(alertArray[3]);
            sp.edit().putInt("alert", 3).commit();
            gs_contact.setChecked(true);
            // sp.edit().putBoolean("iscontactasync", true).commit();
            gs_almanac.setChecked(true);
            sp.edit().putBoolean("isshowalmanac", true).commit();
            gs_weather.setChecked(true);
            sp.edit().putBoolean("isshowweather", true).commit();
            sp.edit().putInt("account_index", 0).commit();
            sp.edit().putInt("ringtone_index", 73).commit();
            sp.edit().putInt("vibrate_index", 2).commit();
            sp.edit().putInt("alert_index", 3).commit();
            sp.edit().putString("vibrate", vibrate_list[2]).commit();
            // sp.edit().putString("ringtone_uri",
            // selectedRingtoneUri).commit();
        }
        gs_vibrate.setChecked(sp.getBoolean("isvibrate", true));

        if (null == selectedRingtoneUri || selectedRingtoneUri.length() == 0) {
            Log.d("zyp", "zyp ---5---");
            mAlarmPref.mAlert = null;
        } else {
            Uri uri = Uri.parse(selectedRingtoneUri);
            Log.d("zyp", "zyp ---3---" + uri);
            mAlarmPref.mAlert = uri;
        }

        updateAlarm(mAlarmPref.mAlert);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
        case R.id.iv_back:
            Log.e("fushuo123", "iv_back");
            finish();
            break;
        case R.id.layout_sms:
        case R.id.iv_sms:
            Log.e("fushuo123", "sms");
            Intent intent = new Intent(this, SmsEventActivity.class);
            startActivity(intent);
            break;
        case R.id.layout_week:
        case R.id.iv_week:
            Log.e("fushuo123", "week");
            showWeekReminderChoiceDialog();
            break;
        case R.id.layout_account:
        case R.id.iv_account:
            Log.e("fushuo123", "account");
            showAccountChoiceDialog();
            break;
        case R.id.layout_ring:
        case R.id.iv_ring:
        case R.id.layout_ring_none:
        case R.id.iv_ring_none:
            showRingtonePage();
            break;
        case R.id.layout_vibrate:
        case R.id.iv_vibrate:
            Log.e("fushuo123", "vibrate");
            showVibrateChoiceDialog();
            break;
        case R.id.layout_alert:
        case R.id.iv_alert:
            Log.e("fushuo123", "alert");
            showAlertChoiceDialog();
            break;
        case R.id.layout_contact:
            gs_contact.setChecked(!gs_contact.isChecked());
            break;
        case R.id.layout_almanac:
            gs_almanac.setChecked(!gs_almanac.isChecked());
            break;
        case R.id.layout_weather:
            gs_weather.setChecked(!gs_weather.isChecked());
            break;
        case R.id.layout_vibrate_temp:
            gs_vibrate.setChecked(!gs_vibrate.isChecked());
            break;
        default:
            break;
        }

    }

    private void showAlertChoiceDialog() {
        // TODO Auto-generated method stub
        // CalendarUtil.darkBackground(CalendarSettings.this);
        final String[] single_list = getResources().getStringArray(R.array.preferences_default_reminder_labels);
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(this);
        builder.setTitle(getString(R.string.preferences_default_reminder_dialog));
        builder.setSingleChoiceItems(single_list, !sp.contains("alert_index") ? 3 : sp.getInt("alert_index", 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putInt("alert_index", which).commit();
                        tv_alert_summary.setText(single_list[which]);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(CalendarSettings.this);
            }
        });
        GomeAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showVibrateChoiceDialog() {
        // TODO Auto-generated method stub
        // CalendarUtil.darkBackground(CalendarSettings.this);
        final String[] single_list = getResources().getStringArray(R.array.prefEntries_alerts_vibrateWhen);
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prefDialogTitle_vibrateWhen));
        builder.setSingleChoiceItems(single_list, !sp.contains("vibrate_index") ? 2 : sp.getInt("vibrate_index", 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putInt("vibrate_index", which).commit();
                        sp.edit().putString("vibrate", vibrate_list[which]).commit();
                        tv_vibrate_summary.setText(single_list[which]);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(CalendarSettings.this);
            }
        });
        GomeAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showRingtonePage() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        mAlarmPref.onPrepareRingtonePickerIntent(intent);
        startActivityForResult(intent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (null == uri) {
                selectedRingtoneUri = "";
            } else {
                selectedRingtoneUri = uri.toString();
            }
            Log.d("PRODUCTION-2877", "selectedRingtoneUri: " + selectedRingtoneUri);
            sp.edit().putString("ringtone_uri", selectedRingtoneUri).commit();
            updateAlarm(uri);
        }
    }

    private void updateAlarm(Uri ringtoneUri) {
        try {
            if (ringtoneUri != null) {
                mAlarmPref.mAlert = AlarmPreference.getActualDefaultRingtoneUri(CalendarSettings.this, ringtoneUri,
                        selectedRingtoneUri);
                Ringtone ringtone = RingtoneManager.getRingtone(CalendarSettings.this, mAlarmPref.mAlert);
                if (ringtone.getUri().equals(Uri.parse("content://media/internal/audio/media/107"))) {
                    selectedRingtoneUri = "content://settings/system/notification_sound";
                    ringtoneUri = Uri.parse(selectedRingtoneUri);
                    mAlarmPref.mAlert = AlarmPreference.getActualDefaultRingtoneUri(CalendarSettings.this, ringtoneUri,
                            selectedRingtoneUri);
                    ringtone = RingtoneManager.getRingtone(CalendarSettings.this, mAlarmPref.mAlert);
                }
                if (ringtone.getUri().equals(Uri.parse("content://settings/system/notification_sound"))) {
                    selectedRingtoneUri = mDefaultRingtoneUri;
                    ringtoneUri = Uri.parse(selectedRingtoneUri);
                    mAlarmPref.mAlert = AlarmPreference.getActualDefaultRingtoneUri(CalendarSettings.this, ringtoneUri,
                            selectedRingtoneUri);
                    ringtone = RingtoneManager.getRingtone(CalendarSettings.this, mAlarmPref.mAlert);
                }

                Log.d("zyp", "zyp   --1--   :" + ringtone.getTitle(this));

                layout_ring_none.setVisibility(View.GONE);
                layout_ring.setVisibility(View.VISIBLE);
                tv_ring_name.setText(ringtone.getTitle(this));
            } else {
                mAlarmPref.mAlert = null;
                Log.d("zyp", "zyp   --2--   :" + mAlarmPref.mAlert);
                layout_ring_none.setVisibility(View.VISIBLE);
                layout_ring.setVisibility(View.GONE);
                tv_ring_name.setText("");
            }

        } catch (Exception e) {

        }
    }

    private void showAccountChoiceDialog() {
        // TODO Auto-generated method stub
        // CalendarUtil.darkBackground(CalendarSettings.this);
        String[] single_list = getResources().getStringArray(R.array.preferences_add_account_labels);
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(this);
        builder.setTitle(getString(R.string.preferences_add_account));
        builder.setSingleChoiceItems(single_list, !sp.contains("account_index") ? 0 : sp.getInt("account_index", 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putInt("account_index", which).commit();
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(CalendarSettings.this);
            }
        });
        GomeAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showWeekReminderChoiceDialog() {
        final String[] single_list = getResources().getStringArray(R.array.preferences_week_start_day_labels);
        // CalendarUtil.darkBackground(CalendarSettings.this);
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(CalendarSettings.this);
        builder.setTitle(getString(R.string.preferences_week_start_day_title));
        builder.setSingleChoiceItems(single_list, sp.getInt("preferences_week_start_day", 0),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sp.edit().putInt("preferences_week_start_day", which).commit();
                        Log.e("fushuoliao", "preferences_week_start_day=" + which);
                        tv_week_summary.setText(single_list[which]);
                        dialog.dismiss();
                        Log.d("2877", "getFirstDayOfWeek: " + Utils.getFirstDayOfWeek(getApplicationContext()));
                    }
                });
        builder.setNegativeButton(getString(R.string.cancel_action), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(CalendarSettings.this);
            }
        });
        GomeAlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateGomeSwitch(int currentType, boolean isChecked) {

        if (currentType == CONTACT) {
            // Log.e("fushuo", "isGetPermission=" + isGetPermission);
            //// checkRequiredPermission(this);
            // Log.e("fushuo", "isGetPermission 2=" + isGetPermission);
            // if (!isGetPermission) {
            // return;
            // }
            Settings.System.putInt(getContentResolver(), "openConcatsSync", isChecked ? 0 : 1);
            gs_contact.setChecked(isChecked);
            Log.e("2877", "isChecked=" + isChecked);
            sp.edit().putBoolean("iscontactasync", isChecked).commit();
        } else if (currentType == ALMANAC) {
            gs_almanac.setChecked(isChecked);
            sp.edit().putBoolean("isshowalmanac", isChecked).commit();
        } else if (currentType == WEATHER) {
            gs_weather.setChecked(isChecked);
            sp.edit().putBoolean("isshowweather", isChecked).commit();
        } else if (currentType == VIBRATE) {
            gs_vibrate.setChecked(isChecked);
            sp.edit().putBoolean("isvibrate", isChecked).commit();
            sp.edit().putInt("vibrate_index", isChecked ? 0 : 2).commit();
            sp.edit().putString("vibrate", vibrate_list[isChecked ? 0 : 2]).commit();
        }
    }

    private void syncCotactsBirthday() {
        ContentResolver cr = getContentResolver();
        Uri uri = ContactsContract.Data.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.CommonDataKinds.Event.DATA,
                ContactsContract.Contacts.DISPLAY_NAME };
        String selection = ContactsContract.Contacts.Data.MIMETYPE + "='"
                + ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "'" + " and "
                + ContactsContract.CommonDataKinds.Event.TYPE + "='"
                + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + "'";
        Cursor cursor = cr.query(uri, projection, selection, null, null);
        List<ContactsContract.Contacts> listcontacts = new ArrayList<ContactsContract.Contacts>();
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.birthday_date_format_deafult));
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String birthday = cursor.getString(0);
                    String name = cursor.getString(1);
                    try {
                        Date date = format.parse(birthday);
                        cal.setTime(date);
                        int month = cal.get(Calendar.MONTH);
                        int day = cal.get(Calendar.DAY_OF_MONTH);
                        Calendar birthdayCalendar = Calendar.getInstance();
                        int year = birthdayCalendar.get(Calendar.YEAR);
                        Log.d("liaozhenbin", year + "-" + month + "-" + day);
                        birthdayCalendar.set(year, month, day);
                        long birthdayTime = birthdayCalendar.getTimeInMillis();
                        EventBean bean = new EventBean();
                        bean.title = CalendarApplication.getContext().getString(R.string.event_birth_front) + name;
                        bean.end = birthdayTime;
                        bean.start = birthdayTime;
                        bean.sync = "sync";
                        bean.color = Constant.BIRTHDAY;

                        EventUtils.addBirthdayEvent(getApplicationContext(), bean, false);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        try {
                            Date date = format1.parse(birthday);
                            cal.setTime(date);
                            int month = cal.get(Calendar.MONTH);
                            int day = cal.get(Calendar.DAY_OF_MONTH);
                            Calendar birthdayCalendar = Calendar.getInstance();
                            int year = birthdayCalendar.get(Calendar.YEAR);
                            Log.d("liaozhenbin", year + "-" + month + "-" + day);
                            birthdayCalendar.set(year, month, day);
                            long birthdayTime = birthdayCalendar.getTimeInMillis();
                            EventBean bean = new EventBean();
                            bean.title = CalendarApplication.getContext().getString(R.string.event_birth_front) + name;
                            bean.end = birthdayTime;
                            bean.start = birthdayTime;
                            bean.sync = "sync";
                            bean.color = Constant.BIRTHDAY;
                            EventUtils.addBirthdayEvent(getApplicationContext(), bean, false);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        } finally {
                            cursor = null;
                        }
                    }

                } while (cursor.moveToNext());
            }
        }
    }

    private void enSyncCotactsBirthday() {
        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        cr.delete(uri, "original_sync_id=?", new String[] { "sync" });
    }

    private void getRingtoneTitleList() {
        int count = 0;
        int total = 0;
        titleLists = new ArrayList<String>();
        RingtoneManager manager = new RingtoneManager(this);

        manager.setType(RingtoneManager.TYPE_NOTIFICATION);
        Cursor cursor = manager.getCursor();
        total = cursor.getCount();
        Log.e("xxx", "total=" + total);
        if (cursor.moveToFirst()) {
            do {
                String str = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                Log.e("fushuo", str);
                titleLists.add(str);

            } while (cursor.moveToNext());

        }
        titleLists.add(0, getString(R.string.noringtone));
        Log.e("fushuo222", titleLists.size() + "");
        cursor.close();
    }

    private void checkRequiredPermission(final Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        if (permissionsList.size() == 0) {
            isGetPermission = true;
            return;
        }
        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case REQUEST_CODE_ASK_PERMISSIONS:
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    isGetPermission = true;
                } else {
                    isGetPermission = false;
                    Toast.makeText(CalendarSettings.this, "permission denied!!! " + permissions[i], Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
        default:
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals("iscontactasync")) {
            if (sp.getBoolean("iscontactasync", true)) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        syncCotactsBirthday();
                    }
                }).start();
            } else {
                enSyncCotactsBirthday();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            sp.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }
}
