package com.hct.calendar.event;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EventListAdapter extends BaseAdapter {

    private List<EventItem> mArrayList;
    private Context mContext;
    private boolean isLast;
    private boolean isShowWeather;
    private boolean showHuangli;

    public EventListAdapter(Context context, List<EventEntity> lists) {
        mContext = context;
        buildEventItem(lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mArrayList.get(position).getView(convertView, parent);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return EventItem.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mArrayList.get(position).getType();
    }

    @Override
    public Object getItem(int position) {
        if (mArrayList == null) {
            return null;
        }
        return mArrayList.get(position);
    }

    @Override
    public int getCount() {
        if (mArrayList == null) {
            return 0;
        } else {
            return (mArrayList.size());
        }
    }

    private void buildEventItem(List<EventEntity> lists) {
        if (mArrayList == null) {
            mArrayList = new ArrayList<EventItem>();
        }
        mArrayList.clear();

        if (lists != null && lists.size() > 0) {
            boolean isHasAgenda = false;
            boolean isHashuangli = false;
            for (int i = 0; i < lists.size(); i++) {
                String title = lists.get(i).title;
                String dataJson = lists.get(i).dateJson;
                if (null != title && title.length() > 0) {
                    isHasAgenda = true;
                }
                if (null != dataJson && dataJson.length() > 0) {
                    isHashuangli = true;
                }

                if (i + 1 == lists.size()) {
                    isLast = true;
                }
                mArrayList.add(EventItem.ofEvent(mContext, lists.get(i),
                        isShowWeather, showHuangli, isHasAgenda, isHashuangli));
            }
        }
    }

    public void reBuildEventItem(List<EventEntity> lists) {
        buildEventItem(lists);
        notifyDataSetChanged();
    }

    public void setShowState(boolean showHuangli2, boolean isShowWeather2,
            ArrayList<EventEntity> agendaArrayList) {
        this.showHuangli = showHuangli2;
        this.isShowWeather = isShowWeather2;
        reBuildEventItem(agendaArrayList);
    }
}
