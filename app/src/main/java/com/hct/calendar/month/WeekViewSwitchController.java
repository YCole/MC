package com.hct.calendar.month;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ViewSwitcher;

import com.android.calendar.CalendarController;
import com.android.calendar.Event;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.hct.calendar.month.MonthByWeekAdapter.WeatherHelper;
import com.hct.calendar.month.MonthWeekSwitcher.AgendaTouchStateListener;

public class WeekViewSwitchController implements OnTouchListener,
        AnimationListener, AgendaTouchStateListener {
    public static final String TAG = "WeekViewController";
    public static final int VIEW_TYPE = 1;

    private static final int MAXJULIANDAY = 2465425;// 2038.1.1
    private static final int MAXWEEK = 3548;// 2037.12.31

    private final static int WEEK_VIEW_COUNT = 5;
    private int mPostion = 0;
    private Context mContext = null;
    private MonthViewPagerAdapter mWeekPageAdapter;
    private ViewSwitcher mSwitcher;
    private GestureDetector mSwitchDetector;
    private CalendarController mController;
    private OnWeekViewSwitchListener mSwitchListener = null;
    private int mWeekIndexOfMonth = 0;
    private int mTouchSlop = 8;
    private boolean mIsWeekMode = false;
    private Time mCurrTime = new Time();
    private float mClickedXLocation;
    private int mSelectDayIndex = 0;
    private boolean mIsDown;
    Time mTouchday = null;
    private boolean mIsCanChangeClick;
    private int mTouchWeek;
    private int mTouchIndex;
    private boolean mIsChickChange;
    private WeatherHelper mWeatherHelper;
    private Handler mEventDialogHandler;

    public static interface OnWeekViewSwitchListener {
        public void onMonthChange(MonthWeekEventsView v, Time now);

        public void onClickDayChange(Time touchDay, int week, int clickIndex);
    }

    public WeekViewSwitchController(Context c, MonthViewPagerAdapter adapter,
            ViewSwitcher switcher, CalendarController controller, Handler h) {
        mContext = c;
        mWeekPageAdapter = adapter;
        mSwitcher = switcher;
        mSwitchDetector = new GestureDetector(mContext,
                new SwitchGestureListener());
        mController = controller;
        mTouchSlop = ViewConfiguration.get(c).getScaledTouchSlop();
        mCurrTime.setToNow();
        mEventDialogHandler = h;
    }

    public View makeView() {
        MonthWeekEventsView v = mWeekPageAdapter.getSelectWeekView();
        if (v == null) {
            mPostion = mWeekPageAdapter.getSelectWeekPostIndex();
            updateWeekIndexOfMonth(mPostion);
        }
        return buildView(v);
    }

    public void setOnWeekViewSwitchListener(OnWeekViewSwitchListener l) {
        mSwitchListener = l;
    }

    public void refresh() {
        if (mIsWeekMode) {

        } else {
            MonthWeekEventsView v = getNeedShowWeekView();
            final MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                    .getCurrentView();
            if (v == null) {

            } else {
                int weekPost = getPostionFromTag(v);
                HashMap<String, Integer> pMap = (HashMap<String, Integer>) v
                        .getTag();
                if (weekPost != mPostion || mPostion != getPostionFromTag(view)) {
                    mPostion = weekPost;
                }
                Integer height = pMap.get(SimpleWeekView.VIEW_PARAMS_HEIGHT);
                Integer oldHeight = getValuebyKeyFromTagMap(view,
                        SimpleWeekView.VIEW_PARAMS_HEIGHT);
                HashMap<String, Integer> currMap = (HashMap<String, Integer>) view
                        .getTag();
                currMap.putAll(pMap);
                view.setWeekParams(currMap, v.mTimeZone);
                if (!height.equals(oldHeight)) {
                    mSwitcher.requestLayout();
                }
                view.setClickedDay(v.getClickDayIndex());
                view.setWeekOfMonthIndex(v.getWeekOfMonthIndex());
            }
            if (view != null) {
                mSelectDayIndex = view.getClickDayIndex();
                sendEventsToView(view);
            }
        }
    }

    public void updateCurrViewHeight(int newHeight) {
        final MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        HashMap<String, Integer> pMap = (HashMap<String, Integer>) view
                .getTag();
        pMap.put(SimpleWeekView.VIEW_PARAMS_HEIGHT, newHeight);
        view.setWeekParams(pMap, view.mTimeZone);
    }

    public MonthWeekEventsView getNeedShowWeekView() {
        MonthWeekEventsView v = mWeekPageAdapter.getClickWeekView();
        if (v == null) {
            v = mWeekPageAdapter.getSelectWeekView();
        }
        return v;
    }

    public void goTo(Time t) {
        MonthWeekEventsView v = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        Time weekMaxTime = v.getCuurWeekMaxTime();
        Time weekMinTime = v.getCuurWeekMinTime();
        Time tempTime = new Time(t);
        tempTime.set(0, 0, 0, t.monthDay, t.month, t.year);
        long millis = tempTime.normalize(true);
        int position = Utils.getWeeksSinceEpochFromJulianDay(
                Time.getJulianDay(millis, tempTime.gmtoff),
                mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek);
        mPostion = position;
        mSelectDayIndex = Utils.getDayInWeekIndex(tempTime,
                mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek);
        if (position >= 0 && position <= MAXWEEK) {
            if (Time.compare(tempTime, weekMaxTime) > 0) {
                showNextInternal();
            } else if (Time.compare(tempTime, weekMinTime) < 0) {
                showPreviousInternal();
            } else {
                afterWeekViewSwitch();
            }
        }
    }

    public void showNext() {
        MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        mPostion = getPostionFromTag(view) + 1;
        // mSelectDayIndex = 0;
        if (mPostion <= MAXWEEK)
            showNextInternal();
    }

    private void showNextInternal() {
        initView(true);
        mSwitcher.setInAnimation(mContext, R.anim.week_slide_in_right);
        mSwitcher.setOutAnimation(mContext, R.anim.week_slide_out_left);
        mSwitcher.getInAnimation().setAnimationListener(this);
        mSwitcher.showNext();
    }

    public void showPrevious() {
        MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        mPostion = getPostionFromTag(view) - 1;
        // mSelectDayIndex = 0;
        if (mPostion >= 0)
            showPreviousInternal();
    }

    private void showPreviousInternal() {
        initView(false);
        mSwitcher.setInAnimation(mContext, R.anim.week_slide_in_left);
        mSwitcher.setOutAnimation(mContext, R.anim.week_slide_out_right);
        mSwitcher.getInAnimation().setAnimationListener(this);
        mSwitcher.showPrevious();
    }

    private void changeWeekView(MonthWeekEventsView view) {
        if (mSwitchListener != null && !view.isWeekInSameMonth()) {
            mSwitchListener.onMonthChange(view, view.getCuurWeekMinTime());
        }
    }

    private void initView(boolean isNext) {
        MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getNextView();
        buildView(view);
        changeWeekView(view);
    }

    private MonthWeekEventsView buildView(MonthWeekEventsView v) {
        LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        if (v == null) {
            v = new MonthWeekEventsView(mContext);
        }
        int selectedDay = -1;
        HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT,
                mWeekPageAdapter.getViewItemHeight());
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SHOW_WK_NUM,
                mWeekPageAdapter.getWrapperAdapter().mShowWeekNumber ? 1 : 0);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK_START,
                mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_NUM_DAYS,
                mWeekPageAdapter.getWrapperAdapter().mDaysPerWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK, mPostion);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH,
                getMonthFromTime(mController.getTime()));
        drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_ORIENTATION,
                Configuration.ORIENTATION_PORTRAIT);
        drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_TYPE, VIEW_TYPE);
        v.setWeekParams(drawingParams,
                mWeekPageAdapter.getWrapperAdapter().mSelectedDay.timezone);
        v.setLayoutParams(p);
        v.setWeekOfMonthIndex(Utils.getWeekNumsByTime(v.getDayFromIndex(0),
                mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek) - 1);
        v.setClickable(true);
        v.setOnTouchListener(this);
        sendEventsToView(v);
        if (Utils.isEnableWeatherShow(mContext)) {
            v.setWeatherShowEnable(true);
        } else {
            v.setWeatherShowEnable(false);
        }
        return v;
    }

    private void sendEventsToView(MonthWeekEventsView v) {
        ArrayList<ArrayList<Event>> eventDayList = mWeekPageAdapter
                .getWrapperAdapter().mEventDayList;
        if (eventDayList.size() == 0) {
            v.setEvents(null, null);
            return;
        }

        int viewJulianDay = v.getFirstJulianDay();
        int start = viewJulianDay
                - mWeekPageAdapter.getWrapperAdapter().mFirstJulianDay;
        int end = start + v.mNumDays;
        if ((viewJulianDay + v.mNumDays > MAXJULIANDAY)
                && (mWeekPageAdapter.getWrapperAdapter().mFirstJulianDay > (MAXJULIANDAY - 35))) {
            end = start + MAXJULIANDAY - viewJulianDay;
        }
        if ((viewJulianDay + v.mNumDays <= MAXJULIANDAY)) {
            if ((start < 0 || (end > eventDayList.size()))) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG,
                            "Week is outside range of loaded events. viewStart: "
                                    + viewJulianDay
                                    + " eventsStart: "
                                    + mWeekPageAdapter.getWrapperAdapter().mFirstJulianDay);
                }
                v.setEvents(null, null);
                return;
            } else {
                v.setEvents(eventDayList.subList(start, end),
                        mWeekPageAdapter.getWrapperAdapter().mEvents);
            }
        } else if ((viewJulianDay < MAXJULIANDAY)
                && (mWeekPageAdapter.getWrapperAdapter().mFirstJulianDay > (MAXJULIANDAY - 35))) {

            v.setMaxEvents(eventDayList.subList(start, end),
                    mWeekPageAdapter.getWrapperAdapter().mEvents);
        }
        v.invalidate();
    }

    public void updateEventToView() {
        MonthWeekEventsView v = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        sendEventsToView(v);
    }

    private int getMonthFromTime(long millis) {
        Time t = new Time();
        t.set(millis);
        return t.month;
    }

    private void updateWeekIndexOfMonth(int selectWeek) {
        int currItemPos = mWeekPageAdapter.getViewPager().getCurrentItem();
        int currWeekStartPos = 0;
        if (currItemPos != 0) {
            currWeekStartPos = mWeekPageAdapter.getStartWeekByPage(currItemPos);
        } else {
            // init week view
            Time t = new Time();
            t.setToNow();
            t.set(1, t.month, t.year);
            currWeekStartPos = Utils.getWeeksSinceEpochFromJulianDay(
                    Time.getJulianDay(t.normalize(true), t.gmtoff),
                    mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek);
        }
        int offSet = selectWeek - currWeekStartPos;
        mWeekIndexOfMonth = offSet;
    }

    public void updateWeekIndexOfMonth() {
        if (mIsWeekMode) {
            final MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                    .getCurrentView();
            mWeekIndexOfMonth = view.getWeekOfMonthIndex();
        } else {
            MonthWeekEventsView v = mWeekPageAdapter.getClickWeekView();
            if (v == null) {
                mPostion = mWeekPageAdapter.getSelectWeekPostIndex();
                updateWeekIndexOfMonth(mPostion);
            } else {
                mWeekIndexOfMonth = v.getWeekOfMonthIndex();
            }
        }
        Log.v(TAG, "updateWeekIndexOfMonth()-->mIsWeekMode:" + mIsWeekMode
                + ",mWeekIndexOfMonth:" + mWeekIndexOfMonth);
    }

    public int getCurrWeekIndexOfMonth() {
        updateWeekIndexOfMonth();
        return mWeekIndexOfMonth;
    }

    public void setWeekMode(boolean isWeek) {
        mIsWeekMode = isWeek;
    }

    private boolean mIsStartScroll = false;

    private int getPostionFromTag(MonthWeekEventsView selectView) {
        HashMap<String, Integer> pMap = (HashMap<String, Integer>) selectView
                .getTag();
        return pMap.get(SimpleWeekView.VIEW_PARAMS_WEEK);
    }

    private Integer getValuebyKeyFromTagMap(MonthWeekEventsView selectView,
            String key) {
        HashMap<String, Integer> pMap = (HashMap<String, Integer>) selectView
                .getTag();
        return pMap.get(key);
    }

    public class SwitchGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            int diffX = (int) (e2.getRawX() - e1.getRawX());
            int diffY = (int) (e2.getRawY() - e1.getRawY());
            if (Math.abs(diffX) > mTouchSlop
                    && Math.abs(diffY) < mTouchSlop * 4) {
                mIsStartScroll = false;
                if (diffX > 0) {
                    showPrevious();
                } else {
                    showNext();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                    .getCurrentView();
            mTouchday = view.getDayFromLocation(e.getX());
            if (mTouchday == null) {
                return false;
            }
            Log.v("TAG",
                    "mTouchday,mTouchday.getJulianDay=="
                            + mTouchday
                            + " , "
                            + mTouchday.getJulianDay(mTouchday.toMillis(true),
                                    mTouchday.gmtoff));
            if (mTouchday.getJulianDay(mTouchday.toMillis(true),
                    mTouchday.gmtoff) >= Time.EPOCH_JULIAN_DAY
                    && mTouchday.getJulianDay(mTouchday.toMillis(true),
                            mTouchday.gmtoff) < MAXJULIANDAY) {
                mClickedXLocation = e.getX();
                view.setClickedDay(mClickedXLocation);
                mWeekPageAdapter.getWrapperAdapter().sendClickEvent(mTouchday);
                HashMap<String, Integer> drawingParams = (HashMap<String, Integer>) view
                        .getTag();
                if (!view.isWeekInSameMonth()) {
                    int hightMonth = drawingParams
                            .get(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH);
                    if (hightMonth != mTouchday.month) {
                        if (mSwitchListener != null) {
                            mSwitchListener.onMonthChange(view, mTouchday);
                        }
                        drawingParams.put(
                                SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH,
                                mTouchday.month);
                        view.setWeekParams(drawingParams, mTouchday.timezone);
                        int weekNums = Utils
                                .getWeekNumsByTime(
                                        mTouchday,
                                        mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek);
                        mWeekIndexOfMonth = weekNums - 1;
                        view.setWeekOfMonthIndex(mWeekIndexOfMonth);
                    }
                }
                mTouchWeek = drawingParams.get(SimpleWeekView.VIEW_PARAMS_WEEK);
                mTouchIndex = view.getClickDayIndex();
                mSelectDayIndex = mTouchIndex;
                mIsChickChange = true;
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            if (e1 == null) {
                return false;
            }
            float y1 = e1.getRawY(), y2 = e2.getRawY();
            if (y1 - y2 > 120 || y1 - y2 < -120) {
                mIsStartScroll = true;
                return (true);
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mEventDialogHandler != null) {
                MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                        .getCurrentView();

                Message message = mEventDialogHandler.obtainMessage();
                message.obj = view.getDayFromLocation(e.getX());
                message.what = 1;
                mEventDialogHandler.sendMessage(message);
            }
            super.onLongPress(e);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (scrollAndFling(event)) {
            return true;
        }
        return false;
    }

    private boolean scrollAndFling(MotionEvent ev) {
        boolean res = false;
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            gestureTouch(ev);
            res = mSwitchDetector.onTouchEvent(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            res = mSwitchDetector.onTouchEvent(ev);
            if (res && mIsStartScroll) {
                gestureTouch(ev);
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mIsStartScroll) {
                gestureTouch(ev);
            }
            res = mSwitchDetector.onTouchEvent(ev);
            break;
        default:
            break;
        }
        return res;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        afterWeekViewSwitch();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private void afterWeekViewSwitch() {
        final MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        view.clearClickedDay();
        Time nowday = new Time();
        if (mSelectDayIndex != -1) {
            nowday = view.getDayFromIndex(mSelectDayIndex);
            if (nowday.getJulianDay(nowday.toMillis(true), nowday.gmtoff) >= Time.EPOCH_JULIAN_DAY
                    && nowday
                            .getJulianDay(nowday.toMillis(true), nowday.gmtoff) < MAXJULIANDAY) {
                view.setClickedDay(mSelectDayIndex);// select first day?
            }
        } else {
            view.updateToday(mWeekPageAdapter.getWrapperAdapter().mHomeTimeZone);
            nowday.switchTimezone(mWeekPageAdapter.getWrapperAdapter().mHomeTimeZone);
            nowday.setToNow();
            nowday.normalize(true);
            if (view.mHasToday) {
                view.setClickedDay(view.mTodayIndex);
            }
        }
        view.setWeekOfMonthIndex(Utils.getWeekNumsByTime(nowday,
                mWeekPageAdapter.getWrapperAdapter().mFirstDayOfWeek) - 1);
        if (mSwitchListener != null) {
            mSwitchListener.onMonthChange(view, nowday);
        }
        if (mTouchday == null) {
            mTouchday = new Time();
        }
        if (nowday.getJulianDay(nowday.toMillis(true), nowday.gmtoff) >= Time.EPOCH_JULIAN_DAY
                && nowday.getJulianDay(nowday.toMillis(true), nowday.gmtoff) < MAXJULIANDAY) {
            mTouchday.set(nowday);
        }
        HashMap<String, Integer> drawingParams = (HashMap<String, Integer>) view
                .getTag();
        int focusMonth = mTouchday.month;
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH, focusMonth);
        view.setWeekParams(drawingParams, mTouchday.timezone);
        mWeekPageAdapter.getWrapperAdapter().sendClickEvent(mTouchday);
        mTouchWeek = drawingParams.get(SimpleWeekView.VIEW_PARAMS_WEEK);
        mTouchIndex = view.getClickDayIndex();
        mIsChickChange = true;
    }

    @Override
    public void onStateChange(int state) {
        switch (state) {
        case AgendaTouchStateListener.AGENDA_NORMAL:
            mIsCanChangeClick = false;
            break;
        case AgendaTouchStateListener.AGENDA_MOVE:
            if (mSwitchListener != null && mIsCanChangeClick && mIsChickChange) {
                mIsCanChangeClick = false;
                mIsChickChange = false;
                mSwitchListener.onClickDayChange(mTouchday, mTouchWeek,
                        mTouchIndex);
            }
            break;
        case AgendaTouchStateListener.AGENDA_TOP:
            mIsCanChangeClick = true;
            break;
        default:
            break;
        }
    }

    private boolean gestureTouch(MotionEvent ev) {
        if (mGestureListener != null) {
            mGestureListener.onTouch(mSwitcher, ev);
        }
        return false;
    }

    private OnTouchListener mGestureListener;

    public void setTouchGestureDetector(OnTouchListener onListener) {
        mGestureListener = onListener;
    }

    protected class CalendarGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }
    }

    public void refreshWeather() {
        MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        view.postInvalidate();
    }

    public void updateTimeZone(String timeZone) {
        MonthWeekEventsView view = (MonthWeekEventsView) mSwitcher
                .getCurrentView();
        HashMap<String, Integer> pMap = (HashMap<String, Integer>) view
                .getTag();
        view.setWeekParams(pMap, timeZone);
    }
}
