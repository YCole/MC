package com.android.calendar;

import gm.widget.GomeSwitch;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SmsEventActivity extends Activity implements OnClickListener {
    private ImageView iv_back;
    private GomeSwitch gs_card;
    private GomeSwitch gs_air_ticket;
    private GomeSwitch gs_hotel;
    public static final int CARD = 0;
    public static final int AIR = 1;
    public static final int HOTEL = 2;
    private int currentType = 0;
    private SharedPreferences sp;
    private RelativeLayout gs_hotel_root;
    private RelativeLayout gs_air_ticket_root;
    private RelativeLayout gs_card_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sms_settings);
        // StatusBarUtils.setStatusBarLightMode(this,
        // Color.parseColor("#f1f2f3"));
        initViews();
        initListeners();
        initDefaultData();
    }

    private void initViews() {
        // TODO Auto-generated method stub
        iv_back = (ImageView) findViewById(R.id.iv_back);
        gs_card = (GomeSwitch) findViewById(R.id.gs_card);
        gs_air_ticket = (GomeSwitch) findViewById(R.id.gs_air_ticket);
        gs_hotel = (GomeSwitch) findViewById(R.id.gs_hotel);
        gs_card_root = (RelativeLayout) findViewById(R.id.gs_card_root);
        gs_air_ticket_root = (RelativeLayout) findViewById(R.id.gs_air_ticket_root);
        gs_hotel_root = (RelativeLayout) findViewById(R.id.gs_hotel_root);
    }

    private void initListeners() {
        // TODO Auto-generated method stub
        iv_back.setOnClickListener(this);
        gs_card_root.setOnClickListener(this);
        gs_hotel_root.setOnClickListener(this);
        gs_air_ticket_root.setOnClickListener(this);
        gs_card.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = CARD;
                updateGomeSwitch(currentType, isChecked);
            }
        });
        gs_air_ticket.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = AIR;
                updateGomeSwitch(currentType, isChecked);
            }
        });
        gs_hotel.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                // TODO Auto-generated method stub
                currentType = HOTEL;
                updateGomeSwitch(currentType, isChecked);
            }
        });
    }

    private void updateGomeSwitch(int currentType, boolean isChecked) {
        if (currentType == CARD) {
            Settings.System.putInt(getContentResolver(), "openCreditCardSync",
                    isChecked ? 0 : 1);
            gs_card.setChecked(isChecked);
            sp.edit().putBoolean("iscard", isChecked).commit();
        } else if (currentType == AIR) {
            Settings.System.putInt(getContentResolver(), "openTicketDataSync",
                    isChecked ? 0 : 1);
            gs_air_ticket.setChecked(isChecked);
            sp.edit().putBoolean("isair", isChecked).commit();
        } else if (currentType == HOTEL) {
            Settings.System.putInt(getContentResolver(), "openHotleDataSync",
                    isChecked ? 0 : 1);
            gs_hotel.setChecked(isChecked);
            sp.edit().putBoolean("ishotel", isChecked).commit();
        }
    }

    private void initDefaultData() {
        sp = getSharedPreferences(CalendarSettings.SETTINGS_SP_NAME,
                Context.MODE_PRIVATE);
        if (sp.contains("preferences_week_start_day")) {
            Log.e("fushuo123", "sp");
            gs_card.setChecked(sp.getBoolean("iscard", true));
            gs_air_ticket.setChecked(sp.getBoolean("isair", true));
            gs_hotel.setChecked(sp.getBoolean("ishotel", true));
        } else {
            Log.e("fushuo123", "default");
            gs_card.setChecked(true);
            sp.edit().putBoolean("iscard", true).commit();
            gs_air_ticket.setChecked(true);
            sp.edit().putBoolean("isair", true).commit();
            gs_hotel.setChecked(true);
            sp.edit().putBoolean("ishotel", true).commit();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
        case R.id.iv_back:
            finish();
            break;
        case R.id.gs_card_root:
            gs_card.setChecked(!gs_card.isChecked());
            break;
        case R.id.gs_air_ticket_root:
            gs_air_ticket.setChecked(!gs_air_ticket.isChecked());
            break;
        case R.id.gs_hotel_root:
            gs_hotel.setChecked(!gs_hotel.isChecked());
            break;
        default:
            break;
        }
    }
}
