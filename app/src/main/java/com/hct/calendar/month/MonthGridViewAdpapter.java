package com.hct.calendar.month;

import java.util.HashMap;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.format.Time;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.calendar.Utils;

public class MonthGridViewAdpapter extends BaseAdapter {
    private int mWeekNums = 6;

    private Context mContext = null;
    private MonthByWeekAdapter mAdapter = null;
    private int mOffsetPos = 0;
    private Time mCurrMonthStartTime = null;

    private DataSetObserver mDataSetObserver = new DataSetObserver() {
        public void onChanged() {
            notifyDataSetChanged();
        };
    };

    public MonthGridViewAdpapter(Context context, MonthByWeekAdapter adapter,
            int pos, Time t) {
        mContext = context;
        mAdapter = adapter;
        mOffsetPos = pos;
        mCurrMonthStartTime = new Time(t);
    }

    private int calcMonthWeekNums(Time t) {
        int sWeekNums = Utils.getWeekOfMonthNumsByTime(t,
                mAdapter.mFirstDayOfWeek);
        return sWeekNums == 4 ? 5 : sWeekNums;
    }

    @Override
    public int getCount() {
        return mWeekNums;
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup root) {
        MonthWeekEventsView view = null;
        if (v != null) {
            view = (MonthWeekEventsView) v;
            HashMap<String, Integer> drawingParams = (HashMap<String, Integer>) view
                    .getTag();
            drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH,
                    mCurrMonthStartTime.month);
            drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT,
                    mAdapter.getItemHeight());
            drawingParams.put(SimpleWeekView.VIEW_PARAMS_SHOW_WK_NUM,
                    mAdapter.mShowWeekNumber ? 1 : 0);
            view.setWeekParams(drawingParams, mAdapter.mHomeTimeZone);
            mAdapter.sendEventsToView(view);
        } else {
            view = (MonthWeekEventsView) mAdapter.getView(
                    position + mOffsetPos, v, root);
        }
        if (position == 0) {
            mAdapter.mFocusMonth = mCurrMonthStartTime.month;
        }
        view.setWeekOfMonthIndex(position);
        return view;
    }

    private boolean isMonthNow(Time t) {
        Time now = new Time(mAdapter.getSelectedDay().timezone);
        now.setToNow();
        if (now.month == t.month && now.year == t.year) {
            return true;
        }
        return false;
    }

    public DataSetObserver getAdapterDataSetObserver() {
        return mDataSetObserver;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
