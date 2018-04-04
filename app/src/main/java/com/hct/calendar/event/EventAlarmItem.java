package com.hct.calendar.event;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract.Attendees;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.ColorChipView;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.event.ScheduleDetailsActivity;
import com.hct.calendar.almanac.AlmanacActivity;

public class EventAlarmItem extends EventItem {

    private EventEntity mEntity;
    private StringBuilder mStringBuilder;
    private Formatter mFormatter;
    private Time mTime;
    private String mTimeZone;
    private boolean isShowWeather;
    private boolean showHuangli;
    private int visible = 0;
    private int invisible = 4;
    private int gone = 8;
    private boolean isBolHasAgenda;
    private boolean isBolHashuangli;
    public final String WEATHER_TEM_UNIT_CHANGE = "weather_tem_unit_change";

    public EventAlarmItem(Context context, EventEntity entity,
            boolean isShowWeather, boolean showHuangli, boolean isHasAgenda,
            boolean isHashuangli) {
        super(context);
        mEntity = entity;
        this.isShowWeather = isShowWeather;
        this.showHuangli = showHuangli;
        isBolHasAgenda = isHasAgenda;
        isBolHashuangli = isHashuangli;
    }

    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            if (mTime != null) {
                mTime.timezone = Utils.getTimeZone(mContext, mTZUpdater);
            }

        }
    };

    @Override
    public View getView(View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = (View) mInflater.inflate(R.layout.agenda_event_item,
                    parent, false);
            viewHolder = new ViewHolder();

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.agendaEvent = (RelativeLayout) convertView
                .findViewById(R.id.agenda_event);
        viewHolder.titleTv = (TextView) convertView.findViewById(R.id.title);
        viewHolder.whenTv = (TextView) convertView.findViewById(R.id.when);
        viewHolder.whereTv = (TextView) convertView.findViewById(R.id.where);

        viewHolder.almanacItemIt = (RelativeLayout) convertView
                .findViewById(R.id.almanac_item_it);
        viewHolder.huangliText1 = (TextView) convertView
                .findViewById(R.id.almanac_text1);
        viewHolder.huangliText2 = (TextView) convertView
                .findViewById(R.id.almanac_text2);
        viewHolder.huangliText3 = (TextView) convertView
                .findViewById(R.id.almanac_text3);
        viewHolder.almanac_view = (View) convertView
                .findViewById(R.id.almanac_view);

        viewHolder.weatherItemIt = (ViewGroup) convertView
                .findViewById(R.id.weather_layout);
        viewHolder.tv_district = (TextView) convertView
                .findViewById(R.id.tv_district);
        viewHolder.iv_weather = (ImageView) convertView
                .findViewById(R.id.iv_weather);
        viewHolder.tv_temp = (TextView) convertView.findViewById(R.id.tv_temp);
        viewHolder.tv_weather = (TextView) convertView
                .findViewById(R.id.tv_weather);
        viewHolder.tv_air_condition = (TextView) convertView
                .findViewById(R.id.tv_air_condition);
        viewHolder.tv_air = (TextView) convertView.findViewById(R.id.tv_air);
        viewHolder.tv_wet = (TextView) convertView.findViewById(R.id.tv_wet);
        viewHolder.tv_wind = (TextView) convertView.findViewById(R.id.tv_wind);
        viewHolder.weather_view = (View) convertView
                .findViewById(R.id.weather_view);

        viewHolder.line = (View) convertView.findViewById(R.id.line);

        mStringBuilder = new StringBuilder(50);
        mFormatter = new Formatter(mStringBuilder, Locale.getDefault());
        ColorChipView colorChipView = (ColorChipView) convertView
                .findViewById(R.id.agenda_item_color);
        colorChipView.setDrawStyle(ColorChipView.DRAW_CIRCLE);
        String title = mEntity.title;
        String location = mEntity.location;
        String eventTz = mEntity.timezone;
        long startTime = mEntity.startTime;
        long endTime = mEntity.endTime;
        int allDay = mEntity.allDay;

        // huangli
        String lunarDate = mEntity.lunarDate == null ? "" : mEntity.lunarDate;
        String yii = mEntity.yii == null ? "" : mEntity.yii;
        String jii = mEntity.jii == null ? "" : mEntity.jii;
        String dataJson = mEntity.dateJson == null ? "" : mEntity.dateJson;
        // weather
        String district = mEntity.district == null ? "" : mEntity.district;
        int weatherIcon = mEntity.weatherIcon;
        String temp = mEntity.temp == null ? "" : mEntity.temp;
        String tempLow = "";
        String tempHigh = "";

        Log.e("zyp", "zyp ------temp---    :" + temp);
        String temperatureUnit = Settings.System.getString(
                mContext.getContentResolver(), WEATHER_TEM_UNIT_CHANGE);
        Log.d("zyp", "zyp ------temperatureUnit---    :" + temperatureUnit);
        if (temp.length() > 0 && temp.contains("/")) {
            String[] tempArr = temp.split("/");
            if (null != temperatureUnit && temperatureUnit.length() > 0) {
                Log.d("zyp",
                        "zyp ------mContext.getString(R.string.temp_unit_C)---    :"
                                + mContext.getString(R.string.temp_unit_C));
                if (temperatureUnit.equals(mContext
                        .getString(R.string.temp_unit_C))) {
                    // weather app`s unit is °C
                    String firstTemp = tempArr[0];
                    String firstTempUnit = firstTemp.substring(
                            firstTemp.length() - 1, firstTemp.length());
                    Log.d("zyp", "zyp ------firstTempUnit---    :"
                            + firstTempUnit);
                    if (null != firstTempUnit
                            && !firstTempUnit.equals(mContext
                                    .getString(R.string.temp_unit_C))) {
                        // now unit is °F
                        // need switch unit to °C
                        String firstTempNum = firstTemp.substring(0,
                                firstTemp.length() - 1);
                        Log.d("zyp", "zyp ------firstTempNum---    :"
                                + firstTempNum);
                        int tempC = (int) ((Integer.parseInt(firstTempNum) - 32) / (1.8));
                        tempLow = tempC
                                + mContext.getString(R.string.temp_unit_C);
                    }

                    String secondTemp = tempArr[1];
                    String secondTempUnit = secondTemp.substring(
                            secondTemp.length() - 1, secondTemp.length());
                    if (null != secondTempUnit
                            && !secondTempUnit.equals(mContext
                                    .getString(R.string.temp_unit_C))) {
                        // now unit is °F
                        // need switch unit to °C
                        String secondTempNum = secondTemp.substring(0,
                                secondTemp.length() - 1);
                        int tempC = (int) ((Integer.parseInt(secondTempNum) - 32) / (1.8));
                        tempHigh = tempC
                                + mContext.getString(R.string.temp_unit_C);
                    }

                } else {
                    // weather app`s unit is °F
                    String firstTemp = tempArr[0];
                    String firstTempUnit = firstTemp.substring(
                            firstTemp.length() - 1, firstTemp.length());
                    if (null != firstTempUnit
                            && !firstTempUnit.equals(mContext
                                    .getString(R.string.temp_unit_F))) {
                        // now unit is °C
                        // need switch unit to °F
                        String firstTempNum = firstTemp.substring(0,
                                firstTemp.length() - 1);
                        int tempF = (int) ((Integer.parseInt(firstTempNum) * 1.8) + 32);
                        tempLow = tempF
                                + mContext.getString(R.string.temp_unit_F);
                    }

                    String secondTemp = tempArr[1];
                    String secondTempUnit = secondTemp.substring(
                            secondTemp.length() - 1, secondTemp.length());
                    if (null != secondTempUnit
                            && !secondTempUnit.equals(mContext
                                    .getString(R.string.temp_unit_F))) {
                        // now unit is °C
                        // need switch unit to °F
                        String secondTempNum = secondTemp.substring(0,
                                secondTemp.length() - 1);
                        int tempF = (int) ((Integer.parseInt(secondTempNum) * 1.8) + 32);
                        tempHigh = tempF
                                + mContext.getString(R.string.temp_unit_F);
                    }
                }
            }
        }
        if (null != tempLow && null != tempHigh && tempLow.length() > 0
                && tempHigh.length() > 0) {
            temp = tempLow + "/" + tempHigh;
        }

        String weather = mEntity.weather == null ? "" : mEntity.weather;
        String air = mEntity.air == null ? "" : mEntity.air;
        String wind = mEntity.wind == null ? "" : mEntity.wind;
        String wet = mEntity.wet == null ? "" : mEntity.wet;
        String airCondition = mEntity.airCondition == null ? ""
                : mEntity.airCondition;

        //
        String isHasAgenda = mEntity.isHasAgenda;

        viewHolder.huangliText1.setText(mContext.getString(R.string.nongli,
                lunarDate));
        viewHolder.huangliText2.setText(mContext.getString(R.string.yii, yii));
        viewHolder.huangliText3.setText(mContext.getString(R.string.jii, jii));

        viewHolder.tv_district.setText(district);
        if (weatherIcon != 0) {
            viewHolder.iv_weather.setImageResource(weatherIcon);
        }
        viewHolder.tv_temp.setText(temp);
        viewHolder.tv_weather.setText(weather);
        viewHolder.tv_air_condition.setText(airCondition);
        viewHolder.tv_air.setText(air);
        viewHolder.tv_wet.setText(wet);
        viewHolder.tv_wind.setText(wind);

        if (title == null || title.length() == 0) {
            viewHolder.agendaEvent.setVisibility(View.GONE);
        } else {
            viewHolder.agendaEvent.setVisibility(View.VISIBLE);
        }

        if (lunarDate == null || lunarDate.length() == 0) {
            viewHolder.almanacItemIt.setVisibility(View.GONE);
        } else {
            // almanacItemIt.setVisibility(View.VISIBLE);
            if (showHuangli) {
                viewHolder.almanacItemIt.setVisibility(View.VISIBLE);
                if (isBolHasAgenda) {
                    viewHolder.almanac_view.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.almanac_view.setVisibility(View.GONE);
                }

            } else {
                viewHolder.almanacItemIt.setVisibility(View.GONE);
            }

        }

        if (weather == null || weather.length() == 0) {
            viewHolder.weatherItemIt.setVisibility(View.GONE);
        } else {
            // weatherItemIt.setVisibility(View.VISIBLE);
            if (!isShowWeather) {
                viewHolder.weatherItemIt.setVisibility(View.GONE);
            } else {
                // if (CalendarUtil.isEnglish()) {
                // viewHolder.weatherItemIt.setVisibility(View.GONE);
                // } else {
                viewHolder.weatherItemIt.setVisibility(View.VISIBLE);
                if (isBolHasAgenda || isBolHashuangli) {
                    viewHolder.weather_view.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.weather_view.setVisibility(View.GONE);
                }
                // }
            }
        }

        int eventColor = Utils
                .getDisplayColorFromColor((Integer) mEntity.eventColor);
        viewHolder.titleTv.setText(title);

        if (location != null && location.length() > 0) {
            viewHolder.whereTv.setVisibility(View.VISIBLE);
            viewHolder.whereTv.setText(location);
        } else {
            viewHolder.whereTv.setVisibility(View.GONE);
        }

        // When
        // long begin = cursor.getLong(AgendaWindowAdapter.INDEX_BEGIN);
        // long end = cursor.getLong(AgendaWindowAdapter.INDEX_END);
        // String eventTz =
        // cursor.getString(AgendaWindowAdapter.INDEX_TIME_ZONE);
        int flags = 0;
        String whenString;
        // It's difficult to update all the adapters so just query this each
        // time we need to build the view.
        String tzString = Utils.getTimeZone(mContext, mTZUpdater);
        if (allDay != 1) {
            flags = DateUtils.FORMAT_SHOW_TIME;
            if (DateFormat.is24HourFormat(mContext)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
            mStringBuilder.setLength(0);
            whenString = DateUtils.formatDateRange(mContext, mFormatter,
                    startTime, endTime, flags, tzString).toString();
            if (allDay != 1 && !TextUtils.equals(tzString, eventTz)) {
                String displayName;
                // Figure out if this is in DST
                Time date = new Time(tzString);
                date.set(startTime);

                TimeZone tz = TimeZone.getTimeZone(tzString);
                if (tz == null || tz.getID().equals("GMT")) {
                    displayName = tzString;
                } else {
                    displayName = tz.getDisplayName(date.isDst != 0,
                            TimeZone.SHORT);
                }
//                whenString += " (" + displayName + ")";
            }
            viewHolder.whenTv.setText(whenString);
        } else {
            viewHolder.whenTv.setText(R.string.edit_event_all_day_label);
        }

        /*
         * if(allDay==1){ whenTv.setText((R.string.edit_event_all_day_label));
         * }else{ whenTv.setText(getTimeString(startTime) + "-" +
         * getTimeString(endTime)); }
         */
        colorChipView.setColor(eventColor);

        initClick(viewHolder.agendaEvent, viewHolder.almanacItemIt, dataJson,
                viewHolder.weatherItemIt);

        return convertView;
    }

    private void initClick(RelativeLayout agendaEvent,
            RelativeLayout almanacItemIt, final String dataJson,
            ViewGroup weatherItemIt) {
        // TODO Auto-generated method stub
        agendaEvent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int eventId = mEntity.eventId;
                if (eventId < 0) {
                    return;
                }
                Intent intent = new Intent(mContext,
                        ScheduleDetailsActivity.class);
                intent.putExtra("id", eventId);
                mContext.startActivity(intent);
            }
        });

        almanacItemIt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mContext.startActivity(new Intent(mContext,
                        AlmanacActivity.class)
                        .putExtra("almanacInfo", dataJson));

            }
        });

        weatherItemIt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClassName("com.gome.weather2",
                        "com.gome.weather2.activity.MainActivity");
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public boolean isItemEnabled() {
        return true;
    }

    @Override
    public int getType() {
        return VIEW_EVENT_NORMAL;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        long agendaStartTime = mEntity.startTime;
        long agendaEndTime = mEntity.endTime;
        int eventId = mEntity.eventId;
        int allDay = mEntity.allDay;
        long holderStartTime = mEntity.startTime;
        if (allDay != -1) {
            boolean allDayBoolean = allDay == 0 ? false : true;
            CalendarController controller = CalendarController
                    .getInstance(mContext);
            controller.sendEventRelatedEventWithExtra(this,
                    EventType.VIEW_EVENT, eventId, agendaStartTime,
                    agendaEndTime, 0, 0, CalendarController.EventInfo
                            .buildViewExtraLong(Attendees.ATTENDEE_STATUS_NONE,
                                    allDayBoolean), holderStartTime);
        }
    }

    public String getTimeString(long time) {
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = new Date(time);
        if (DateFormat.is24HourFormat(mContext)) {
            return dateFormat24.format(date);
        } else {
            return dateFormat.format(date);
        }
    }

    private class ViewHolder {
        RelativeLayout agendaEvent;
        TextView titleTv;
        TextView whenTv;
        TextView whereTv;

        RelativeLayout almanacItemIt;
        TextView huangliText1;
        TextView huangliText2;
        TextView huangliText3;
        View almanac_view;

        ViewGroup weatherItemIt;
        TextView tv_district;
        ImageView iv_weather;
        TextView tv_temp;
        TextView tv_weather;
        TextView tv_air_condition;
        TextView tv_air;
        TextView tv_wet;
        TextView tv_wind;
        View weather_view;

        View line;

    }

}
