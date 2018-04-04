package com.hct.calendar.month;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.android.calendar.CalendarController;
import com.android.calendar.Event;
import com.android.calendar.Utils;

public class MonthViewPagerAdapter extends PagerAdapter {
    private final static int PAGE_COUNT = (CalendarController.MAX_CALENDAR_YEAR
            - CalendarController.MIN_CALENDAR_YEAR + 1) * 12;
    private final static int PAGE_ROWS = 6;
    private final static String TAG = "MonthViewPagerAdapter";
    private Context mContext;
    private HashMap<String, Integer> mWeekParams;
    private Handler mHandler;
    private ViewPager mViewPager;
    private MonthByWeekAdapter mWeekAdapter = null;
    private GridView mCurrGridView = null;

    public MonthViewPagerAdapter(Context context,
            HashMap<String, Integer> weekParams, Handler h) {
        mContext = context;
        mWeekParams = weekParams;
        mHandler = h == null ? new Handler() : h;
        mWeekAdapter = new MonthByWeekAdapter(mContext, mWeekParams, mHandler);
    }

    public MonthViewPagerAdapter(Context context,
            HashMap<String, Integer> weekParams) {
        this(context, weekParams, null);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (object instanceof GridView) {
            GridView gv = (GridView) object;
            MonthGridViewAdpapter adpapter = (MonthGridViewAdpapter) gv
                    .getAdapter();
            mWeekAdapter.unregisterDataSetObserver(adpapter
                    .getAdapterDataSetObserver());
        }
        ((ViewPager) container).removeView((View) object);
    }

    private int getStartPos(int monthPos) {
        Time t = getStartWeekTimeByPage(monthPos);
        int pos = Utils.getWeeksSinceEpochFromJulianDay(
                Time.getJulianDay(t.normalize(true), t.gmtoff),
                mWeekParams.get(WeeksAdapter.WEEK_PARAMS_WEEK_START));
        return pos;
    }

    public Time getStartWeekTimeByPage(int monthPos) {
        int year = monthPos / 12 + CalendarController.MIN_CALENDAR_YEAR;
        int month = (monthPos + 1) % 12 == 0 ? 12 : (monthPos + 1) % 12;
        Time t = new Time(mWeekAdapter.getSelectedDay().timezone);
        t.set(1, month - 1, year);
        return t;
    }

    public int getStartWeekByPage(int monthPos) {
        Time t = getStartWeekTimeByPage(monthPos);
        int pos = Utils.getWeeksSinceEpochFromJulianDay(
                Time.getJulianDay(t.normalize(true), t.gmtoff),
                mWeekParams.get(WeeksAdapter.WEEK_PARAMS_WEEK_START));
        return pos;
    }

    public int getStartMonthJulianDay(Time t) {
        Time time = new Time(t);
        time.set(1, time.month, time.year);
        int weekStart = mWeekParams.get(WeeksAdapter.WEEK_PARAMS_WEEK_START);
        int pos = Utils
                .getWeeksSinceEpochFromJulianDay(
                        Time.getJulianDay(time.normalize(true), time.gmtoff),
                        weekStart);
        int JulianDay = Utils.getFirstJulianFromWeeksSinceEpoch(pos, weekStart);
        return JulianDay;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GridView gridView = new GridView(mContext);
        MonthGridViewAdpapter mGridViewAdapter = new MonthGridViewAdpapter(
                mContext, mWeekAdapter, getStartPos(position),
                getStartWeekTimeByPage(position));
        gridView.setAdapter(mGridViewAdapter);
        gridView.setNumColumns(1);
        gridView.setTag(position);
        container.addView(gridView);
        mWeekAdapter.registerDataSetObserver(mGridViewAdapter
                .getAdapterDataSetObserver());
        return gridView;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        mCurrGridView = (GridView) object;
    }

    public GridView getPrimaryItem() {
        return mCurrGridView;
    }

    public void setViewItemHeight(int h) {
        mWeekAdapter.setItemHeight(h);
    }

    public int getViewItemHeight() {
        return mWeekAdapter.getItemHeight();
    }

    public int getCurrMonthViewWeekNums() {
        return getPrimaryItem().getAdapter().getCount();
    }

    @SuppressWarnings("deprecation")
    public Time getSelectedDay() {
        return mWeekAdapter.getSelectedDay();
    }

    @SuppressWarnings("deprecation")
    public void setSelectedDay(Time s) {
        mWeekAdapter.setSelectedDay(s);
    }

    public void refresh() {
        mWeekAdapter.refresh();
    }

    public void updateParams(HashMap<String, Integer> weekParams) {
        mWeekAdapter.updateParams(weekParams);
        // notifyDataSetChanged();
    }

    public void setViewPager(ViewPager v) {
        mViewPager = v;
        mWeekAdapter.setViewPager(mViewPager);
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setEvents(int d, int i, ArrayList<Event> events) {
        mWeekAdapter.setEvents(d, i, events);
        if (mCurrGridView != null) {
            ((MonthGridViewAdpapter) mCurrGridView.getAdapter())
                    .notifyDataSetChanged();
        }
    }

    public void jumpDate(boolean b) {
        mWeekAdapter.jumpDate(b);
    }

    public void animateToday() {
        mWeekAdapter.animateToday();
    }

    public void updateFocusMonth(int month) {
        //
    }

    public MonthWeekEventsView getSelectWeekView() {
        return mWeekAdapter.mClickedView;
    }

    public int getSelectWeekPostIndex() {
        return mWeekAdapter.mSelectedWeek;
    }

    public MonthWeekEventsView getClickWeekView() {
        if (mCurrGridView != null) {
            int count = mCurrGridView.getChildCount();
            if (count > 0) {
                int todayLineIndex = -1;
                int clickLineIndex = -1;
                for (int i = 0; i < count; i++) {
                    MonthWeekEventsView v = (MonthWeekEventsView) mCurrGridView
                            .getChildAt(i);
                    if (v.getClickDayIndex() != -1) {
                        clickLineIndex = i;
                    }
                    if (v.mHasToday) {
                        todayLineIndex = i;
                    }
                }
                if (clickLineIndex == -1) {
                    clickLineIndex = todayLineIndex == -1 ? 0 : todayLineIndex;
                }
                return (MonthWeekEventsView) mCurrGridView
                        .getChildAt(clickLineIndex);
            }
        }
        return null;
    }

    public void setSelectMonthDay(final long time) {
        Time touchTime = new Time(mWeekAdapter.mHomeTimeZone);
        touchTime.set(time);
        mWeekAdapter.setSelectedDay(touchTime);
        int position = Utils.getWeeksSinceEpochFromJulianDay(
                Time.getJulianDay(touchTime.normalize(true), touchTime.gmtoff),
                mWeekAdapter.mFirstDayOfWeek);
        if (mCurrGridView != null) {
            int count = mCurrGridView.getChildCount();
            if (count > 0) {
                MonthWeekEventsView v = null;
                MonthWeekEventsView targetView = null;
                for (int i = 0; i < count; i++) {
                    v = (MonthWeekEventsView) mCurrGridView.getChildAt(i);
                    v.clearClickedDay();
                    HashMap<String, Integer> drawingParams = (HashMap<String, Integer>) v
                            .getTag();
                    int wk = drawingParams.get(SimpleWeekView.VIEW_PARAMS_WEEK);
                    if (position == wk) {
                        targetView = v;
                        continue;
                    }
                }
                if (targetView != null) {
                    int numDays = targetView.getNumDays();
                    int offset = targetView.showWeekNum() ? 1 : 0;
                    for (int i = 0; i < numDays; i++) {
                        Time t = targetView.getDayFromIndex(i);
                        if (t.monthDay == touchTime.monthDay) {
                            if (targetView.isClickDayHightLight(i)) {
                                targetView.setClickedDay(i);
                                mWeekAdapter.sendClickEvent(targetView
                                        .getDayFromIndex(targetView
                                                .getClickDayIndex()));
                                return;
                            }
                        }
                    }
                }
                targetView = (MonthWeekEventsView) mCurrGridView.getChildAt(0);
                targetView.setClickedMonthFristDay();
                mWeekAdapter.sendClickEvent(targetView
                        .getDayFromIndex(targetView.getClickDayIndex()));
            }
        } else {
            Log.w(TAG, "mCurrGridView is null");
        }

    }

    private void setSelectMonthFristDayOrToday(boolean todayFrist) {
        if (mCurrGridView != null) {
            MonthWeekEventsView v = null;
            int count = mCurrGridView.getChildCount();
            if (count > 0) {
                boolean hasToday = false;
                int selectLineIndex = -1;
                int todayLineIndex = -1;
                int clickIndex = -1;
                int todayIndex = -1;
                for (int i = 0; i < count; i++) {
                    v = (MonthWeekEventsView) mCurrGridView.getChildAt(i);
                    if (v.getClickDayIndex() != -1) {
                        if (v.isClickDayHightLight(v.getClickDayIndex())) {
                            selectLineIndex = i;
                            clickIndex = v.getClickDayIndex();
                        }
                    }
                    if (v.mHasToday && v.isClickDayHightLight(v.mTodayIndex)) {
                        hasToday = true;
                        todayLineIndex = i;
                        todayIndex = v.mTodayIndex;
                    }
                    v.clearClickedDay();
                }
                if (selectLineIndex == -1
                        && todayLineIndex == -1
                        || (selectLineIndex != -1 && todayFrist && (todayLineIndex == -1))) {
                    v = (MonthWeekEventsView) mCurrGridView.getChildAt(0);
                    v.setClickedMonthFristDay();
                    mWeekAdapter.sendClickEvent(v.getDayFromIndex(v
                            .getClickDayIndex()));
                } else {
                    if (selectLineIndex == -1 || (todayFrist && hasToday)) {
                        clickIndex = todayIndex;
                        selectLineIndex = todayLineIndex;
                    }
                    v = (MonthWeekEventsView) mCurrGridView
                            .getChildAt(selectLineIndex);
                    v.setClickedDay(clickIndex);
                    mWeekAdapter.sendClickEvent(v.getDayFromIndex(clickIndex));
                }
            }
        } else {
            Log.w(TAG, "mCurrGridView is null");
        }
    }

    public void refreshWeather() {
        if (mCurrGridView != null) {
            int count = mCurrGridView.getChildCount();
            for (int i = 0; i < count; i++) {
                mCurrGridView.getChildAt(i).postInvalidate();
            }
        }
    }

    public void setSelectMonthFristDay() {
        setSelectMonthFristDayOrToday(true);
    }

    public void resumeSelectDay() {
        setSelectMonthFristDayOrToday(false);
    }

    public MonthByWeekAdapter getWrapperAdapter() {
        return mWeekAdapter;
    }

    public void updateHightMonth(int month) {
        mWeekAdapter.updateFocusMonth(month);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mCurrGridView != null) {
            ((MonthGridViewAdpapter) mCurrGridView.getAdapter())
                    .notifyDataSetChanged();
        }
        super.notifyDataSetChanged();
    }

    public void setClickedDay(int week, int clickDayIndex) {
        // TODO Auto-generated method stub
        if (mCurrGridView != null) {
            MonthWeekEventsView v = null;
            int count = mCurrGridView.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    v = (MonthWeekEventsView) mCurrGridView.getChildAt(i);
                    v.clearClickedDay();
                    HashMap<String, Integer> drawingParams = (HashMap<String, Integer>) v
                            .getTag();
                    int wk = drawingParams.get(SimpleWeekView.VIEW_PARAMS_WEEK);
                    if (week == wk) {
                        v.setClickedDay(clickDayIndex);
                    }
                }
            }
        }
    }
}
