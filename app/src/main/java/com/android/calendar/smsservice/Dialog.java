package com.android.calendar.smsservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
//import com.hct.statistics.sdk.HCTStatistics;
//import com.hct.statistics.sdk.comm.ConstantDefine.SendMode;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calendar.CalendarUtils;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.AlertDialog.Builder;

/*
 import android.content.ContentResolver;
 import android.content.SharedPreferences;
 import android.content.DialogInterface;
 import android.content.res.Configuration;
 import android.graphics.PixelFormat;
 import android.os.Bundle;
 import android.os.Handler;
 import android.util.DisplayMetrics;
 import android.view.KeyEvent;
 import android.view.View;
 import android.view.ViewGroup;
 import android.view.LayoutInflater;
 import android.view.WindowManager;
 import android.widget.Button;
 import android.widget.Toast;
 import android.widget.TextView;

 import android.app.AlertDialog;
 import android.app.AlertDialog.Builder;
 import android.app.Dialog;
 import android.content.DialogInterface;
 import android.view.WindowManager;
 import android.app.StatusBarManager;

 import java.util.Calendar;

 import android.media.AudioManager;
 import android.media.AudioManager.OnAudioFocusChangeListener;
 */
/**
 * Alarm Clock alarm alert: pops visible indicator and plays alarm tone
 */

public class Dialog extends Activity {

    private TextView addressText;
    private TextView nameText;
    private TextView requestText;
    private AlertDialog mDialog;

    private ListView list;
    private int Id;
    private boolean beShare;
    private boolean multiselect;
    private boolean isByProxy;
    private boolean isAccepted;

    private String Number = null;
    private String Text;
    private List<String> timelist = new ArrayList<String>();
    private int selected;
    private Date date;

    private String DeviceId;
    String AddressName;
    private final static String URL_BASE = "http://cloud.hctdevice.com/dcs/";
    private final static String ENDPOINT_BASE = URL_BASE
            + "DataCollection?cmd=saveData";

    /*
     * Runnable downloadRun = new Runnable(){
     * 
     * @Override public void run() { // TODO Auto-generated method stub
     * Log.i("SMSReceiver", "downloadRun"); SendData(); } };
     */

    @Override
    protected void onCreate(Bundle icicle) {
        Log.i("SMSReceiver", "Dialog onCreate");

        super.onCreate(icicle);

        TelephonyManager tm = (TelephonyManager) this
                .getSystemService(TELEPHONY_SERVICE);
        DeviceId = tm.getDeviceId();

        Log.i("SMSReceiver", "DeviceId = " + DeviceId);

        Id = getIntent().getIntExtra("reminder", 0);
        Log.i("SMSReceiver", "Dialog Id = " + Id);
        beShare = getIntent().getBooleanExtra("share", false);
        multiselect = getIntent().getBooleanExtra("multiselect", false);

        int size = 0;
        if (!beShare) {
            if (StringHandler.reminder.size() == 0) {
                Log.i("SMSReceiver", "reminder.size() == 0");
                NotificationManager nm = (NotificationManager) getApplicationContext()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(0);
                finish();
                return;
            }
            size = StringHandler.reminder.get(Id).datestartlist.size();
        } else if (StringHandler.agendalist.size() == 0) {
            Log.i("SMSReceiver", "agendalist.size() == 0");
            NotificationManager nm = (NotificationManager) getApplicationContext()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(1);
            finish();
            return;
        }

        // new Thread(downloadRun).start();
        SharedPreferences prefs = GeneralPreferences.getSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        /********************************************************************************
         * boolean isFirstRun =
         * prefs.getBoolean(GeneralPreferences.KEY_FIRST_RUN, true);
         * Log.i("SMSReceiver", "onCreate isFirstRun = "+isFirstRun); if
         * (isFirstRun) { Log.i("SMSReceiver", "onCreate FirstRun");
         * editor.putBoolean(GeneralPreferences.KEY_FIRST_RUN, false);
         * editor.commit(); } isByProxy = false; Log.i("SMSReceiver",
         * "Dialog onCreate isByProxy = "+isByProxy); HCTStatistics.init(this,
         * isByProxy ? SendMode.ONLYBYPROXY : SendMode.ONLYBYSELF);
         * //HCTStatistics.setProxyPackageName("com.hct.aliveupdate");
         * HCTStatistics.increaseUseTimes(this); int times =
         * prefs.getInt(GeneralPreferences.KEY_USE_TIMES, 0);
         * Log.i("SMSReceiver", "Dialog onCreate times = "+times); times++;
         * Log.i("SMSReceiver", "Dialog onCreate times++ = "+times);
         * 
         * editor.putInt(GeneralPreferences.KEY_USE_TIMES, times);
         * editor.commit(); if (times >= 10 || isFirstRun) {
         * Log.i("SMSReceiver", "Dialog onCreate times>=10");
         * Log.i("SMSReceiver", "Dialog onCreate sendCollectionInfo");
         * HCTStatistics.sendCollectionInfo();
         * editor.putInt(GeneralPreferences.KEY_USE_TIMES, 0); editor.commit();
         * }
         ****************************************************************************************/

        // ----- init view -----
        setContentView(R.layout.dialog);

        // Popup alert over black screen
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        lp.token = null;
        getWindow().setAttributes(lp);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
                        | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");

        addressText = (TextView) findViewById(R.id.dialog_address);
        nameText = (TextView) findViewById(R.id.dialog_string);
        requestText = (TextView) findViewById(R.id.dialog_request);

        if (beShare) {
            Log.i("SMSReceiver", "Dialog agenda size = "
                    + StringHandler.agendalist.size());
            nameText.setText(StringHandler.agendalist.get(Id).Message);
            AddressName = getPeople(this,
                    StringHandler.agendalist.get(Id).Address);
            Text = AddressName
                    + " "
                    + getApplicationContext().getString(
                            R.string.dialog_share_title);
        } else {
            String Messagetext = StringHandler.reminder.get(Id).Message;
            if (Messagetext.length() > 200) {
                Messagetext = Messagetext.substring(0, 200);
            }
            nameText.setText(Messagetext);

            Number = StringHandler.reminder.get(Id).Address;
            AddressName = getPeople(this, Number);
            Text = AddressName
                    + " "
                    + getApplicationContext().getString(
                            R.string.dialog_address_title);
        }
        addressText.setText(Text);

        if (beShare) {
            requestText.setText(getApplicationContext().getString(
                    R.string.request_agenda_save));
            date = new Date();
            date.setTime(StringHandler.agendalist.get(Id).Starttime);
        } else if (size == 1) {
            date = (Date) StringHandler.reminder.get(Id).datestartlist.get(0)
                    .clone();
            String datestr = new String();
            if (date.getSeconds() != 30) {
                datestr = df.format(date);
            } else {
                datestr = df1.format(date);
            }
            requestText.setText(getApplicationContext().getString(
                    R.string.request_reminder_save, datestr));
        } else {
            requestText.setText(getApplicationContext().getString(
                    R.string.request_multi_reminder_save));
        }

        list = (ListView) findViewById(R.id.timelist);

        Log.i("SMSReceiver", "Dialog date size = " + size);

        if (size > 1) {
            Log.i("SMSReceiver", "Dialog date size > 1 ");

            for (int i = 0; i < size; i++) {
                Date dt = StringHandler.reminder.get(Id).datestartlist.get(i);
                if (dt.getSeconds() != 30) {
                    timelist.add(df.format(dt));
                } else {
                    timelist.add(df1.format(dt));
                }
            }
            try {
                list.setAdapter(new ArrayAdapter(this,
                        R.layout.select_dialog_singlechoice_material, timelist));
            } catch (Exception e) {
                e.getMessage();
            }
            list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            list.setItemChecked(0, true);
            selected = 0;
            date = (Date) StringHandler.reminder.get(Id).datestartlist.get(
                    selected).clone();
            list.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                        int arg2, long arg3) {
                    Log.i("SMSReceiver", "Dialog setOnItemClickListener :"
                            + arg2);
                    selected = arg2;
                    date = (Date) StringHandler.reminder.get(Id).datestartlist
                            .get(selected).clone();
                }
            });
        } else {
            Log.i("SMSReceiver", "Dialog date size <= 1 ");
            list.setVisibility(View.GONE);
        }

        CheckBox cb1 = (CheckBox) this.findViewById(R.id.cb_close);
        cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    SharedPreferences prefs = GeneralPreferences
                            .getSharedPreferences(getApplicationContext());
                    CalendarUtils.setSharedPreference(prefs,
                            GeneralPreferences.KEY_SMART_REMINDER, false);
                    Toast.makeText(Dialog.this,
                            getString(R.string.reminder_closed),
                            Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences prefs = GeneralPreferences
                            .getSharedPreferences(getApplicationContext());
                    CalendarUtils.setSharedPreference(prefs,
                            GeneralPreferences.KEY_SMART_REMINDER, true);
                }

            }
        });

        findViewById(R.id.cancel).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        cancel(getApplicationContext());
                    }
                });

        /* dismiss button: close notification */
        findViewById(R.id.ok).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                ok(getApplicationContext());
            }
        });

        this.setFinishOnTouchOutside(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
     * private void SendData() { HttpPost request = new HttpPost(ENDPOINT_BASE);
     * JSONObject param = new JSONObject();
     * 
     * GregorianCalendar gc = new GregorianCalendar(2000, 1, 1, 0, 0, 0);
     * gc.setTimeZone(new SimpleTimeZone(0, "GMT")); long timebase =
     * gc.getTimeInMillis() / 1000; long timenow = new Date().getTime() / 1000;
     * long intelval = timenow - timebase;
     * 
     * Log.i("SMSReceiver", "timenow = " + timenow); Log.i("SMSReceiver",
     * "timebase = " + timebase); Log.i("SMSReceiver", "intelval = " +
     * String.valueOf(intelval));
     * 
     * try { param.put("type", "launch"); param.put("appid",
     * "43616c656e646172636f6c6c65637469"); param.put("app_version", "1001");
     * param.put("did", DeviceId); param.put("market", "preload");
     * param.put("count", "1"); param.put("times", String.valueOf(intelval)); }
     * catch (JSONException e) { // TODO Auto-generated catch block
     * Log.i("SMSReceiver", "param.put JSONException!"); e.printStackTrace(); }
     * 
     * StringEntity se; try { se = new StringEntity(param.toString());
     * request.setEntity(se); } catch (UnsupportedEncodingException e) { // TODO
     * Auto-generated catch block Log.i("SMSReceiver",
     * "request.setEntity UnsupportedEncodingException!"); e.printStackTrace();
     * }
     * 
     * HttpResponse httpResponse; try { httpResponse = new
     * DefaultHttpClient().execute(request); int code =
     * httpResponse.getStatusLine().getStatusCode(); Log.i("SMSReceiver",
     * "SendData code = "+ code); String retSrc =
     * EntityUtils.toString(httpResponse.getEntity()); Log.i("SMSReceiver",
     * "SendData retSrc = "+retSrc); } catch (ClientProtocolException e) { //
     * TODO Auto-generated catch block Log.i("SMSReceiver",
     * "Response ClientProtocolException!"); e.printStackTrace(); } catch
     * (IOException e) { // TODO Auto-generated catch block Log.i("SMSReceiver",
     * "Response IOException!"); e.printStackTrace(); } }
     */

    public void mToast() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date_string = df.format(date);
        Toast.makeText(Dialog.this, getString(R.string.saved, date_string),
                Toast.LENGTH_LONG).show();
    }

    private String getPeople(Context context, String nember) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
        Log.d("SMSReceiver", "getPeople ---------");
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + nember
                        + "'", null, null);

        if (cursor == null || cursor.getCount() == 0) {
            Log.d("SMSReceiver", "getPeople null or count = 0");
            return nember;
        }
        cursor.moveToPosition(0);

        int nameFieldColumnIndex = cursor
                .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
        String name = cursor.getString(nameFieldColumnIndex);
        cursor.close();
        return name;
    }

    private void cancel(Context context) {
        if (!multiselect) {
            if (beShare) {
                StringHandler.agendalist.remove(Id);
            } else {
                StringHandler.reminder.remove(Id);
                // AddCheckList(context, number);
            }
        }

        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (!beShare && StringHandler.reminder.size() == 0) {
            nm.cancel(0);
        } else if (beShare && StringHandler.agendalist.size() == 0) {
            nm.cancel(1);
        }

        if (multiselect) {
            setResult(RESULT_CANCELED);
        }

        if (!beShare && Number != null) {
            AddCheckList();
        } else {
            finish();
        }
    }

    private void ok(Context context) {
        if (beShare) {
            StringHandler.SaveFormatMsg(context, Id, AddressName);
        } else {
            StringHandler.SaveNormalMessage(context, Id, selected, AddressName);
        }
        mToast();

        if (multiselect) {
            setResult(RESULT_OK);
        }

        if (!beShare && Number != null) {
            BlackListDBHelper helper = new BlackListDBHelper(
                    getApplicationContext());
            // delete check list
            helper.del_chklist(Number);
        }

        finish();
    }

    public void ShowDialog() {
        AlertDialog.Builder builder = new Builder(Dialog.this);
        builder.setTitle(R.string.smart_reminder_title)
                .setMessage(R.string.blacklist_hint)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                BlackListDBHelper helper = new BlackListDBHelper(
                                        getApplicationContext());
                                // add blacklist
                                ContentValues values = new ContentValues();
                                values.put("number", Number);
                                helper.insert(values);
                                // delete check list
                                helper.del_chklist(Number);
                                finish();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int whichButton) {
                                BlackListDBHelper helper = new BlackListDBHelper(
                                        getApplicationContext());
                                // delete check list
                                helper.del_chklist(Number);
                                finish();
                            }
                        }).setCancelable(false);
        if (mDialog == null) {
            mDialog = builder.create();
        }
        /*
         * mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
         * );
         * mDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND
         * , WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
         * mDialog.getWindow().addFlags
         * (WindowManager.LayoutParams.FLAG_DIM_BEHIND);
         */
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void AddCheckList() {
        BlackListDBHelper helper = new BlackListDBHelper(
                getApplicationContext());
        int times = -1;
        Log.i("zhou", "number = " + Number);
        Cursor c = helper.query_chklist(Number);
        if (c != null) {
            Log.i("zhou", "c != null");
            if (c.moveToFirst()) {
                Log.i("zhou", "c.getCount() = " + c.getCount());
                times = c.getInt(c.getColumnIndex("times"));
                Log.i("zhou", "get times = " + times);
                int id = c.getInt(c.getColumnIndex("_id"));
                Log.i("zhou", "get id = " + id);
                if (times >= 2) {
                    Log.i("zhou", "times >= 2");
                    ShowDialog();
                    c.close();
                    return;
                } else {
                    times++;
                    ContentValues values = new ContentValues();
                    values.put("times", times);
                    helper.update_chklist(values, id);
                }
            } else {
                // add check list
                ContentValues values = new ContentValues();
                values.put("number", Number);
                values.put("times", 1);
                helper.insert_chklist(values);
            }
            c.close();
        }
        finish();
    }
}
