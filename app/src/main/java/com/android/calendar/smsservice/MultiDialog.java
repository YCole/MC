package com.android.calendar.smsservice;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.android.calendar.R;

public class MultiDialog extends Activity {

    private ListView list;

    private List<String> reminderlist = new ArrayList<String>();
    private int selected;
    private boolean beShare;

    @Override
    protected void onCreate(Bundle icicle) {
        Log.i("SMSReceiver", "Dialog onCreate");

        super.onCreate(icicle);

        beShare = getIntent().getBooleanExtra("share", false);

        // ----- init view -----
        setContentView(R.layout.multidialog);

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

        list = (ListView) findViewById(R.id.reminderlist);

        int size = 0;
        if (beShare) {
            size = StringHandler.agendalist.size();
            for (int i = 0; i < size; i++) {
                String listitem = StringHandler.agendalist.get(i).Subject;
                listitem = listitem.replaceAll("\n", " ");
                reminderlist.add(listitem);
            }
        } else {
            size = StringHandler.reminder.size();
            for (int i = 0; i < size; i++) {
                String listitem = StringHandler.reminder.get(i).Message;
                listitem = listitem.replaceAll("\n", " ");
                reminderlist.add(listitem);
            }
        }
        try {
            list.setAdapter(new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, reminderlist));
        } catch (Exception e) {
            e.getMessage();
        }
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Log.i("SMSReceiver", "Dialog setOnItemClickListener :" + arg2);
                selected = arg2;
                Intent notificationIntent = new Intent(getApplicationContext(),
                        Dialog.class);
                if (beShare) {
                    notificationIntent.putExtra("share", true);
                }
                notificationIntent.putExtra("reminder", selected);
                notificationIntent.putExtra("multiselect", true);
                startActivityForResult(notificationIntent, 100);
            }
        });

        findViewById(R.id.cancel).setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        cancel();
                    }
                });

        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("SMSReceiver", "requestCode = " + requestCode);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Log.i("SMSReceiver", "RESULT_CANCELED");

            reminderlist.clear();
            int size = 0;
            if (beShare) {
                size = StringHandler.agendalist.size();
            } else {
                size = StringHandler.reminder.size();
            }
            if (size == 0) {
                finish();
            }
            if (beShare) {
                for (int i = 0; i < size; i++) {
                    String listitem = StringHandler.agendalist.get(i).Subject;
                    listitem = listitem.replaceAll("\n", " ");
                    reminderlist.add(listitem);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    String listitem = StringHandler.reminder.get(i).Message;
                    listitem = listitem.replaceAll("\n", " ");
                    reminderlist.add(listitem);
                }
            }
            try {
                list.setAdapter(new ArrayAdapter(this,
                        android.R.layout.simple_list_item_1, reminderlist));
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    @Override
    protected void onDestroy() {
        NotificationManager nm = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (beShare) {
            StringHandler.agendalist.clear();
            nm.cancel(1);
        } else {
            StringHandler.reminder.clear();
            nm.cancel(0);
        }

        super.onDestroy();
    }

    private void cancel() {
        finish();
    }

}
