package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.android.calendar.R;
import com.android.calendar.utils.DateUtils;

/**
 * ScheduleAdapter Created by liaozhenbin on 2017/7/6.
 */

public class ScheduleAdapter extends BaseAdapter {

    /**
     * Item,int.start 0
     */
    public static final int TYPE_TITLE_CONTENT_FIRST = 0;
    public static final int TYPE_TITLE_CONTENT = 1;
    public static final int TYPE_CONTENT_MIDDLE = 2;
    public static final int TYPE_CONTENT_END = 3;
    public static final int TYPE_NULL = 4;

    private static final int TYPE_ITEM_COUNT = 5;

    private List<ScheduleEvents> mData = new ArrayList<>();
    private Context context;
    private OnShowItemClickListener onShowItemClickListener;

    public ScheduleAdapter(Context context, List<ScheduleEvents> mData) {
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
        view = View.inflate(context, R.layout.schedule_time_view, null);
        TextView dateView = (TextView) view.findViewById(R.id.tv_date);
        TextView tileView = (TextView) view.findViewById(R.id.tv_title);
        TextView timeView = (TextView) view.findViewById(R.id.tv_time);
        View dian = view.findViewById(R.id.dian);
        // fushuo begin
        CheckBox cb = (CheckBox) view.findViewById(R.id.cb_select);
        // fushuo end
        switch (getItemViewType(position)) {
        case TYPE_TITLE_CONTENT_FIRST:
            view.findViewById(R.id.big_split).setVisibility(View.GONE);
            view.findViewById(R.id.null_view).setVisibility(View.GONE);
            break;
        case TYPE_CONTENT_MIDDLE:
            view.findViewById(R.id.big_split).setVisibility(View.GONE);
            view.findViewById(R.id.title_view).setVisibility(View.GONE);
            view.findViewById(R.id.null_view).setVisibility(View.GONE);
            break;
        case TYPE_CONTENT_END:
            view.findViewById(R.id.big_split).setVisibility(View.GONE);
            view.findViewById(R.id.title_view).setVisibility(View.GONE);
            view.findViewById(R.id.split_view).setVisibility(View.GONE);
            view.findViewById(R.id.null_view).setVisibility(View.GONE);
            break;
        case TYPE_TITLE_CONTENT:
            view.findViewById(R.id.null_view).setVisibility(View.GONE);
            break;
        case TYPE_NULL:
            view.findViewById(R.id.big_split).setVisibility(View.GONE);
            view.findViewById(R.id.title_view).setVisibility(View.GONE);
            view.findViewById(R.id.split_view).setVisibility(View.GONE);
            view.findViewById(R.id.contet_view).setVisibility(View.GONE);
            break;
        default:
            break;
        }
        final ScheduleEvents event = mData.get(position);
        //
        if (event.isShow()) {
            cb.setVisibility(View.VISIBLE);
        } else {
            cb.setVisibility(View.GONE);
        }
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
                if (isChecked) {
                    event.setChecked(true);
                } else {
                    event.setChecked(false);
                }
                //
                onShowItemClickListener.onShowItemClick(event);
            }
        });
        cb.setChecked(event.isChecked());
        long dtstart = event.getDtstart();
        long dtend = event.getDtend();
        String title = event.getTitle();

        SimpleDateFormat formatter = new SimpleDateFormat(
                context.getString(R.string.date_format_long));
        SimpleDateFormat formatTime = new SimpleDateFormat(
                DateUtils.is24(context) ? "HH:mm" : "hh:mm");
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
            String sAgeFormatString = stCalendar.get(Calendar.AM_PM) == 0 ? context
                    .getResources().getString(R.string.time_am) : context
                    .getResources().getString(R.string.time_pm);
            timeString = String.format(sAgeFormatString, startTime) + "-"
                    + String.format(sAgeFormatString, endTime);

        }

        timeView.setText(event.getAllDay() == 1 ? context
                .getString(R.string.all_day) : timeString);

        dateView.setText(dayString);
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
        dian.setBackgroundResource(colorId);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_ITEM_COUNT;
    }

    // fushuo begin
    public interface OnShowItemClickListener {
        public void onShowItemClick(ScheduleEvents event);
    }

    public void setOnShowItemClickListener(
            OnShowItemClickListener onShowItemClickListener) {
        this.onShowItemClickListener = onShowItemClickListener;
    }
}
