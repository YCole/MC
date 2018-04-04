package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.utils.DateUtils;
import com.google.gson.Gson;
import com.hct.calendar.almanac.AlmanacActivity;
import com.hct.calendar.data.Action;
import com.hct.calendar.data.AlmanacData;
import com.hct.calendar.domain.AlmanacBean;
import com.hct.calendar.domain.AlmanacBody;
import com.hct.calendar.domain.AlmanacBody.DataBean.JiBean;
import com.hct.calendar.domain.AlmanacBody.DataBean.YiBean;
import com.hct.calendar.domain.AlmanacItem;
import com.hct.calendar.http.AlmanacApi;
import com.hct.calendar.ui.FlowLayout;
import com.hct.calendar.ui.TagAdapter;
import com.hct.calendar.ui.TagFlowLayout;
import com.hct.calendar.utils.DensityUtils;
import com.hct.calendar.utils.LocaleUtils;

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/7/31.
 */

public class EventCardFragment extends Fragment {
    private int mSelectYear;
    private int mSelectMonth;
    private int mSelectDay;
    private String dayString = "";
    private long mSelectTime;
    private TextView mDay;
    private TextView mWeekDay;
    private Context mContext;
    String dateString;
    List<YiBean> yiList;
    List<JiBean> jiiList;
    StringBuilder almanacJson;

    private ListView mEventListView;
    private List<ScheduleEvents> eventsList;
    private SharedPreferences sp;
    private TextView tempView;
    private ImageView weatherImage;
    private TextView weatherView;
    private EventAdapter mAdapter;
    // add by zyp for GMOS-4146
    private Activity mActivity;

    private SharedPreferences settingSP;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_card_fragment, container, false);
        mEventListView = (ListView) view.findViewById(R.id.event_list);
        mDay = (TextView) view.findViewById(R.id.tv_day);
        mWeekDay = (TextView) view.findViewById(R.id.tv_week_day);
        mWeekDay.setText(dayString);
        mDay.setText(mSelectDay + getResources().getString(R.string.event_day));

        tempView = (TextView) view.findViewById(R.id.temp_tv);
        weatherImage = (ImageView) view.findViewById(R.id.iv_weather);
        weatherView = (TextView) view.findViewById(R.id.tv_des);
        settingSP = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
        sp = mContext.getSharedPreferences("weather", Context.MODE_PRIVATE);
        String temp = sp.getString("temp", "");
        int iconId = sp.getInt("icon", 0);
        String locationString = sp.getString("district", "");
        String weatherString = sp.getString("weather", "");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, mSelectYear);
        calendar.set(Calendar.MONTH, mSelectMonth);
        calendar.set(Calendar.DAY_OF_MONTH, mSelectDay);
        // Log.d("liaozhenbin",
        // mSelectYear+"-"+mSelectMonth+"-"+mSelectDay+"-"+isToday(new
        // Date(mSelectYear, mSelectMonth, mSelectDay)));
        if (!TextUtils.isEmpty(temp) && iconId != 0 && !TextUtils.isEmpty(locationString) && isToday(calendar.getTime())
                && settingSP.getBoolean("isshowweather", true)) {
            tempView.setVisibility(View.VISIBLE);
            weatherImage.setVisibility(View.VISIBLE);
            weatherView.setVisibility(View.VISIBLE);
            tempView.setText(temp);
            weatherImage.setImageResource(iconId);
            weatherView.setText(weatherString + "," + locationString);
        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        Calendar c = Calendar.getInstance();
        mSelectTime = getArguments().getLong("time");
        c.setTimeInMillis(mSelectTime);
        mSelectDay = c.get(Calendar.DAY_OF_MONTH);
        mSelectYear = c.get(Calendar.YEAR);
        mSelectMonth = c.get(Calendar.MONTH);
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        dayString = formatter.format(c.getTime());
        Log.d("liaoliao", "date: " + mSelectYear + "-" + mSelectMonth + "-" + mSelectDay + "-" + mSelectTime);
        Log.d("gomeliao", "time: " + mSelectTime + "-" + (mSelectTime + 24 * 60 * 60 * 1000));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

    }

    public static EventCardFragment create(long s) {
        EventCardFragment fragment = new EventCardFragment();
        Bundle args = new Bundle();
        args.putLong("time", s);
        fragment.setArguments(args);
        return fragment;
    }

    public void getCalendarData(Context context, long s) {
        eventsList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Log.d("liaoliao", "select s: " + s);
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[]
            // permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the
            // documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor cursor = cr.query(uri, null, "dtstart >= " + s + " and dtstart <" + (s + 24 * 60 * 60 * 1000), null,
                "dtstart");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String eventLocation = cursor.getString(cursor.getColumnIndex("eventLocation"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            int eventColor = cursor.getInt(cursor.getColumnIndex("eventColor"));
            long dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
            long dtend = cursor.getLong(cursor.getColumnIndex("dtend"));
            String rrule = cursor.getString(cursor.getColumnIndex("rrule"));
            int allDay = cursor.getInt(cursor.getColumnIndex("allDay"));
            ScheduleEvents events = new ScheduleEvents(id, title, eventLocation, description, eventColor, dtstart,
                    dtend, rrule, allDay);
            eventsList.add(events);
            Log.d("gomeliao", title);
        }
        if (cursor != null) {
            cursor.close();
        }
        ScheduleEvents almanac = new ScheduleEvents(-1, "", "", "", 0, 0, 0, "", 0);
        almanac.setType(100);
        eventsList.add(almanac);
        // String string = mSelectYear + "-" + mSelectMonth + "-" + mSelectDay;
        // AlmanacData.getAlmanacBody(getContext(), string, new
        // Action<AlmanacBean>() {
        // @Override
        // public void next(final AlmanacBean data) {
        // final AlmanacItem item = AlmanacApi.json2Item(data.dateJson);
        // dateString = item.lunarDate;
        // AlmanacBody body = new Gson().fromJson(data.dateJson,
        // AlmanacBody.class);
        // yiList = yiiList(body);
        // jiiList = jiiList(body);
        //
        // }
        // });

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("fushuo", "aaaa");
        // getCalendarData(getActivity(), mSelectTime);
        getCalendarData(getMyActivity(), mSelectTime);
        // mAdapter = new EventAdapter(getActivity(), eventsList);
        mAdapter = new EventAdapter(getMyActivity(), eventsList);
        mEventListView.setAdapter(mAdapter);
        mEventListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int type = eventsList.get(position).getType();
                if (type != 100) {
                    Intent intent = new Intent(mContext, ScheduleDetailsActivity.class);
                    intent.putExtra("id", eventsList.get(position).getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public class EventAdapter extends BaseAdapter {

        private List<ScheduleEvents> mData = new ArrayList<>();
        private Context context;

        public EventAdapter(Context context, List<ScheduleEvents> mData) {
            this.context = context;
            this.mData = mData;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ScheduleEvents event = mData.get(position);
            if (event.getType() != 100) {
                view = View.inflate(context, R.layout.events_item, null);
                TextView tileView = (TextView) view.findViewById(R.id.tv_title);
                TextView timeView = (TextView) view.findViewById(R.id.tv_time);
                ImageView dian = (ImageView) view.findViewById(R.id.dian);

                long dtstart = event.getDtstart();
                long dtend = event.getDtend();
                String title = event.getTitle();

                SimpleDateFormat formatter = new SimpleDateFormat(context.getString(R.string.date_format_long));
                SimpleDateFormat formatTime = new SimpleDateFormat(DateUtils.is24(context) ? "HH:mm" : "hh:mm");
                Calendar stCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                stCalendar.setTimeInMillis(dtstart);
                endCalendar.setTimeInMillis(dtend);

                String dayString = formatter.format(stCalendar.getTime());

                String startTime = formatTime.format(stCalendar.getTime());
                String endTime = formatTime.format(endCalendar.getTime());

                String timeString = "";
                if (DateUtils.is24(context)) {
                    timeString = startTime + "-" + endTime;
                } else {
                    String sAgeFormatString = stCalendar.get(Calendar.AM_PM) == 0
                            ? context.getResources().getString(R.string.time_am)
                            : context.getResources().getString(R.string.time_pm);
                    timeString = String.format(sAgeFormatString, startTime) + "-"
                            + String.format(sAgeFormatString, endTime);

                }

                timeView.setText(event.getAllDay() == 1 ? context.getString(R.string.all_day) : timeString);
                tileView.setText(title);
                int colorId = R.drawable.dian_shape_other;
                switch (event.getEventColor()) {
                case Constant.SCHEDULE:
                    colorId = R.drawable.dian_shape_other;
                    break;
                case Constant.MEETING:
                    colorId = R.drawable.dian_shape_meetting;
                    break;
                case Constant.BIRTHDAY:
                    colorId = R.drawable.dian_shape_birthday;
                    break;
                }
                dian.setImageResource(colorId);
            } else {
                view = View.inflate(context, R.layout.event_card_almanac, null);
                final TextView dateTextView = (TextView) view.findViewById(R.id.almanac_text1);
                final TagFlowLayout yiiLayout = (TagFlowLayout) view.findViewById(R.id.almanac_yii_layout);
                final TagFlowLayout jiiLayout = (TagFlowLayout) view.findViewById(R.id.almanac_jii_layout);
                final View rootAlmanacView = view.findViewById(R.id.root_almanac);
                String string = mSelectYear + "-" + (mSelectMonth + 1) + "-" + mSelectDay;
                AlmanacData.getAlmanacBody(getContext(), string, new Action<AlmanacBean>() {
                    @Override
                    public void next(final AlmanacBean data) {
                        if (null != data) {
                            almanacJson = new StringBuilder(data.dateJson);
                            final AlmanacItem item = AlmanacApi.json2Item(data.dateJson);
                            if (item == null || EventCardFragment.this.getMyActivity() == null) {
                                return;
                            }
                            EventCardFragment.this.getMyActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dateTextView.setText(item.lunarDate);
                                    AlmanacBody body = new Gson().fromJson(data.dateJson, AlmanacBody.class);
                                    if(body == null){
                                        return;
                                    }
                                    List<YiBean> yi = yiiList(body);
                                    yiiLayout.setAdapter(new TagAdapter<YiBean>(yi) {
                                        @Override
                                        public View getView(FlowLayout parent, int position, YiBean t) {
                                            TextView tv = itemTextView();
                                            if (tv == null) {
                                                return null;
                                            }
                                            tv.setText(t.old);
                                            return tv;
                                        }
                                    });
                                    List<JiBean> jiiList = jiiList(body);
                                    jiiLayout.setAdapter(new TagAdapter<JiBean>(jiiList) {
                                        @Override
                                        public View getView(FlowLayout parent, int position, JiBean t) {
                                            TextView tv = itemTextView();
                                            if (tv == null) {
                                                return null;
                                            }
                                            tv.setText(t.old);
                                            return tv;
                                        }
                                    });
                                    if (null != almanacJson && LocaleUtils.isChinese(mContext)
                                            && settingSP.getBoolean("isshowalmanac", true)) {
                                        rootAlmanacView.setVisibility(View.VISIBLE);
                                        rootAlmanacView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View arg0) {
                                                mContext.startActivity(new Intent(mContext, AlmanacActivity.class)
                                                        .putExtra("almanacInfo", almanacJson.toString()));
                                            }
                                        });
                                    }

                                }
                            });
                        }
                    }
                });

            }

            // }
            return view;
        }
    }

    private TextView itemTextView() {
        if (getMyActivity() == null) {
            return null;
        }
        TextView tView = new TextView(getMyActivity());
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.width = MarginLayoutParams.WRAP_CONTENT;
        params.height = MarginLayoutParams.WRAP_CONTENT;
        params.leftMargin = DensityUtils.dp2px(getMyActivity(), 4);
        tView.setLayoutParams(params);
        // #ffXXXXXX
        tView.setTextSize(13);
        tView.setTextColor(Color.parseColor("#e5000000"));
        return tView;
    }

    private List<YiBean> yiiList(AlmanacBody body) {
        List<YiBean> yi = body.data.yi;
        if (yi.size() > 14) {
            ArrayList<YiBean> temp = new ArrayList<YiBean>();
            for (int i = 0; i < 14; i++) {
                temp.add(yi.get(i));
            }
            yi = temp;
        }
        return yi;
    }

    private List<JiBean> jiiList(AlmanacBody body) {
        List<JiBean> ji = body.data.ji;
        if (ji.size() > 14) {
            ArrayList<JiBean> temp = new ArrayList<JiBean>();
            for (int i = 0; i < 14; i++) {
                temp.add(ji.get(i));
            }
            ji = temp;
        }
        return ji;
    }

    public boolean isToday(Date date) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        if (fmt.format(date).toString().equals(fmt.format(new Date(System.currentTimeMillis())).toString())) {
            return true;
        } else {
            return false;
        }
    }

    // get attach return Activity
    public Activity getMyActivity() {
        return mActivity;
    }
}
