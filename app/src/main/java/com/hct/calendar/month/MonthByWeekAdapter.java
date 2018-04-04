/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hct.calendar.month;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.GridView;

import com.android.calendar.AllInOneActivity;
import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.Event;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.event.EventCardActivity;

public class MonthByWeekAdapter extends WeeksAdapter {
    private static final String TAG = "MonthByWeekAdapter";
    private static final boolean DEBUG = false;
    public static final String WEEK_PARAMS_IS_MINI = "mini_month";
    protected static int DEFAULT_QUERY_DAYS = 7 * 8; // 8 weeks
    private static final long ANIMATE_TODAY_TIMEOUT = 1000;

    private static final int MAXJULIANDAY = 2465425;// 2038.1.1

    protected CalendarController mController;
    protected String mHomeTimeZone;
    protected Time mTempTime;
    protected Time mToday;
    protected int mFirstJulianDay;
    protected int mQueryDays;
    protected boolean mIsMiniMonth = true;
    protected int mOrientation = Configuration.ORIENTATION_LANDSCAPE;
    private final boolean mShowAgendaWithMonth;

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
    private boolean IS_JUMP_DATE = false;
    private int lastIndex = -1;
    private float monthItemHeight;
    private int mViewParamsHeight;
    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */

    protected ArrayList<ArrayList<Event>> mEventDayList = new ArrayList<ArrayList<Event>>();
    protected ArrayList<Event> mEvents = null;

    private boolean mAnimateToday = false;
    private long mAnimateTime = 0;

    private Handler mEventDialogHandler;

    MonthWeekEventsView mClickedView;
    MonthWeekEventsView mSingleTapUpView;
    MonthWeekEventsView mLongClickedView;

    float mClickedXLocation; // Used to find which day was clicked
    long mClickTime; // Used to calculate minimum click animation time
    // Used to insure minimal time for seeing the click animation before
    // switching views
    private static final int mOnTapDelay = 100;
    // Minimal time for a down touch action before stating the click animation,
    // this insures that
    // there is no click animation on flings
    private static int mOnDownDelay;
    private static int mTotalClickDelay;
    // Minimal distance to move the finger in order to cancel the click
    // animation
    private static float mMovedPixelToCancel;

    public static interface WeatherHelper {
        public Drawable getWeatherDrawIconDrawable(Time t);

        public int getWeatherDrawIconResId(Time t);
    }

    public MonthByWeekAdapter(Context context, HashMap<String, Integer> params,
            Handler handler) {
        super(context, params);
        mEventDialogHandler = handler;
        if (params.containsKey(WEEK_PARAMS_IS_MINI)) {
            mIsMiniMonth = params.get(WEEK_PARAMS_IS_MINI) != 0;
        }
        mShowAgendaWithMonth = Utils.getConfigBool(context,
                R.bool.show_agenda_with_month);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mOnDownDelay = ViewConfiguration.getTapTimeout();
        mMovedPixelToCancel = vc.getScaledTouchSlop();
        mTotalClickDelay = mOnDownDelay + mOnTapDelay;
        monthItemHeight = context.getResources().getDimension(
                R.dimen.month_row_height);
        mViewParamsHeight = (int) context.getResources().getDimension(
                R.dimen.month_row_height);// dpToPx(mContext,
                                          // monthItemHeight);
    }

    public void animateToday() {
        mAnimateToday = true;
        mAnimateTime = System.currentTimeMillis();
    }

    protected int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /*
     * HCT_MODIFY lixiange MF3.0 add this function to jump date and hilght begin
     */
    public void jumpDate(boolean is_jump_date) {
        IS_JUMP_DATE = is_jump_date;
    }

    /*
     * HCT_MODIFY lixiange MF3.0 add this function to jump date and hilght end
     */

    @Override
    protected void init() {
        super.init();
        mGestureDetector = new GestureDetector(mContext,
                new CalendarGestureListener());
        mController = CalendarController.getInstance(mContext);
        mHomeTimeZone = Utils.getTimeZone(mContext, null);
        mSelectedDay.switchTimezone(mHomeTimeZone);
        mToday = new Time(mHomeTimeZone);
        mToday.setToNow();
        mTempTime = new Time(mHomeTimeZone);
    }

    private void updateTimeZones() {
        mSelectedDay.timezone = mHomeTimeZone;
        mSelectedDay.normalize(true);
        mToday.timezone = mHomeTimeZone;
        mToday.setToNow();
        mTempTime.switchTimezone(mHomeTimeZone);
    }

    @Override
    public void setSelectedDay(Time selectedTime) {
        mSelectedDay.set(selectedTime);
        long millis = mSelectedDay.normalize(true);
        mSelectedWeek = Utils
                .getWeeksSinceEpochFromJulianDay(
                        Time.getJulianDay(millis, mSelectedDay.gmtoff),
                        mFirstDayOfWeek);
        notifyDataSetChanged();
        if (DEBUG)
            Log.d(TAG, " SelectedDay= " + mSelectedDay);
    }

    public void setEvents(int firstJulianDay, int numDays,
            ArrayList<Event> events) {
        if (mIsMiniMonth) {
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG,
                        "Attempted to set events for mini view. Events only supported in full"
                                + " view.");
            }
            return;
        }
        mEvents = events;
        mFirstJulianDay = firstJulianDay;
        mQueryDays = numDays;
        // Create a new list, this is necessary since the weeks are referencing
        // pieces of the old list
        ArrayList<ArrayList<Event>> eventDayList = new ArrayList<ArrayList<Event>>();
        for (int i = 0; i < numDays; i++) {
            eventDayList.add(new ArrayList<Event>());
        }

        if (events == null || events.size() == 0) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG,
                        "No events. Returning early--go schedule something fun.");
            }
            mEventDayList = eventDayList;
            refresh();
            return;
        }

        // Compute the new set of days with events
        for (Event event : events) {
            int startDay = event.startDay - mFirstJulianDay;
            int endDay = event.endDay - mFirstJulianDay + 1;
            if (startDay < numDays || endDay >= 0) {
                if (startDay < 0) {
                    startDay = 0;
                }
                if (startDay > numDays) {
                    continue;
                }
                if (endDay < 0) {
                    continue;
                }
                if (endDay > numDays) {
                    endDay = numDays;
                }
                for (int j = startDay; j < endDay; j++) {
                    eventDayList.get(j).add(event);
                }
            }
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Processed " + events.size() + " events.");
        }
        mEventDayList = eventDayList;
        refresh();
    }

    boolean isAttachedToWindow = false;

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mIsMiniMonth) {
            return super.getView(position, convertView, parent);
        }
        MonthWeekEventsView v;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        HashMap<String, Integer> drawingParams = null;
        boolean isAnimatingToday = false;
        if (isAttachedToWindow && convertView != null) {
            v = (MonthWeekEventsView) convertView;
            // Checking updateToday uses the current params instead of the new
            // params, so this is assuming the view is relatively stable
            if (mAnimateToday && v.updateToday(mSelectedDay.timezone)) {
                long currentTime = System.currentTimeMillis();
                // If it's been too long since we tried to start the animation
                // don't show it. This can happen if the user stops a scroll
                // before reaching today.
                if (currentTime - mAnimateTime > ANIMATE_TODAY_TIMEOUT) {
                    mAnimateToday = false;
                    mAnimateTime = 0;
                } else {
                    isAnimatingToday = true;
                    // There is a bug that causes invalidates to not work some
                    // of the time unless we recreate the view.
                    v = new MonthWeekEventsView(mContext);
                }
            } else {
                drawingParams = (HashMap<String, Integer>) v.getTag();
            }
        } else {
            v = new MonthWeekEventsView(mContext);
        }
        if (DEBUG)
            Log.d(TAG, "getView, isAttachedToWindow = " + isAttachedToWindow
                    + ", isAnimatingToday = " + isAnimatingToday);
        if (drawingParams == null) {
            drawingParams = new HashMap<String, Integer>();
        }
        drawingParams.clear();

        v.setLayoutParams(params);
        v.setClickable(true);
        v.setOnTouchListener(this);
        int selectedDay = -1;
        if (mSelectedWeek == position) {
            selectedDay = mSelectedDay.weekDay;
        }
        if (mViewPager != null) {
            int h = mViewPager.getMeasuredHeight();
            mViewParamsHeight = h / 6;
        }
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_HEIGHT, mViewParamsHeight);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SELECTED_DAY, selectedDay);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_SHOW_WK_NUM,
                mShowWeekNumber ? 1 : 0);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK_START,
                mFirstDayOfWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_NUM_DAYS, mDaysPerWeek);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_WEEK, position);
        drawingParams.put(SimpleWeekView.VIEW_PARAMS_FOCUS_MONTH, mFocusMonth);
        drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_ORIENTATION,
                mOrientation);
        if (Utils.isEnableWeatherShow(mContext)) {
            v.setWeatherShowEnable(true);
        } else {
            v.setWeatherShowEnable(false);
        }
        /*
         * HCT_MODIFY lixiange MF3.0 notice monthweekview this id a jumpdte
         * event to jump date and hilght begin
         */
        if (IS_JUMP_DATE == true) {
            drawingParams.put("JUMP_DATE", Time.getJulianDay(
                    AllInOneActivity.mSelectDate.toMillis(true),
                    AllInOneActivity.mSelectDate.gmtoff));
        }
        /*
         * HCT_MODIFY lixiange MF3.0 notice monthweekview this id a jumpdte
         * event to jump date and hilght end
         */

        if (isAnimatingToday) {
            drawingParams.put(MonthWeekEventsView.VIEW_PARAMS_ANIMATE_TODAY, 1);
            mAnimateToday = false;
        }
        if (DEBUG)
            Log.d(TAG, "getView, selectedDay = " + selectedDay
                    + ", IS_JUMP_DATE = " + IS_JUMP_DATE + ", SelectedWeek = "
                    + mSelectedWeek);
        v.setWeekParams(drawingParams, mSelectedDay.timezone);
        sendEventsToView(v);
        return v;
    }

    public void sendEventsToView(MonthWeekEventsView v) {
        if (mEventDayList.size() == 0) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "No events loaded, did not pass any events to view.");
            }
            v.setEvents(null, null);
            return;
        }
        int viewJulianDay = v.getFirstJulianDay();
        int start = viewJulianDay - mFirstJulianDay;
        int end = start + v.mNumDays;
        if ((viewJulianDay + v.mNumDays > MAXJULIANDAY)
                && (mFirstJulianDay > (MAXJULIANDAY - 35))) {
            end = start + MAXJULIANDAY - viewJulianDay;
        }
        if ((viewJulianDay + v.mNumDays <= MAXJULIANDAY)) {
            if ((start < 0 || (end > mEventDayList.size()))) {
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG,
                            "Week is outside range of loaded events. viewStart: "
                                    + viewJulianDay + " eventsStart: "
                                    + mFirstJulianDay);
                }
                v.setEvents(null, null);
                return;
            } else {
                v.setEvents(mEventDayList.subList(start, end), mEvents);
            }
        } else if ((viewJulianDay < MAXJULIANDAY)
                && (mFirstJulianDay > (MAXJULIANDAY - 35))) {

            v.setMaxEvents(mEventDayList.subList(start, end), mEvents);
        }
    }

    @Override
    protected void refresh() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);
        mHomeTimeZone = Utils.getTimeZone(mContext, null);
        mOrientation = mContext.getResources().getConfiguration().orientation;
        updateTimeZones();
        notifyDataSetChanged();
    }

    @Override
    protected void onDayTapped(Time day) {
        setDayParameters(day);
        /**
         * if (mShowAgendaWithMonth || mIsMiniMonth) { // If agenda view is
         * visible with month view , refresh the views // with the selected
         * day's info mController.sendEvent(mContext, EventType.GO_TO, day, day,
         * -1, ViewType.CURRENT, CalendarController.EXTRA_GOTO_DATE, null,
         * null); } else {
         */
        // Else , switch to the detailed view
        mController.sendEvent(mContext, EventType.GO_TO, day, day, -1,
                ViewType.CURRENT, CalendarController.EXTRA_GOTO_DATE, null,
                null);
        // | CalendarController.EXTRA_GOTO_BACK_TO_PREVIOUS, null, null);
        // }
    }

    private void setDayParameters(Time day) {
        String timeZone = mHomeTimeZone; // EC:617003969978,617003985556
        if (TextUtils.isEmpty(timeZone)) {
            timeZone = Utils
                    .getTimeZone(mContext.getApplicationContext(), null);
            if (TextUtils.isEmpty(timeZone)) {
                timeZone = Time.TIMEZONE_UTC;
                Log.w(TAG, "Warning:TimeZone is default UTC!");
            }
        }
        day.timezone = timeZone;
        Time currTime = new Time(timeZone);
        currTime.set(mController.getTime());
        day.hour = currTime.hour;
        day.minute = currTime.minute;
        day.allDay = false;
        day.normalize(true);
    }

    boolean mIsDown = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!(v instanceof MonthWeekEventsView)) {
            return super.onTouch(v, event);
        }
        mSingleTapUpView = (MonthWeekEventsView) v;
        mClickedXLocation = event.getX();
        if (mGestureDetector.onTouchEvent(event)) {
            mClickedView = mSingleTapUpView;
        }
        return false;
    }

    private void mDoClick() {
        if (mClickedView != null) {
            mClickedView.setClickedDay(mClickedXLocation);
            mLongClickedView = mClickedView;
            // mClickedView = null;
            // mClickedView.invalidate();
        }
    }

    public void sendClickEvent(Time clickTime) {
        sendClickEvent(clickTime, false);
    }

    public void sendClickEvent(Time clickTime, boolean isUserTouch) {
        if (clickTime == null) {
            return;
        }
        setDayParameters(clickTime);
        long extraLong = isUserTouch ? CalendarController.EXTRA_GOTO_DATE
                | CalendarController.EXTRA_GOTO_USER_TOUCH
                : CalendarController.EXTRA_GOTO_DATE;
        Log.d("liao", "week do send");
        if (time == null) {
            mController.sendEvent(mContext, EventType.MONTH_GO_TO, clickTime,
                    clickTime, -1, ViewType.CURRENT, extraLong, null, null);
        } else {
            mController.sendEvent(mContext, EventType.MONTH_GO_TO, time, time,
                    -1, ViewType.CURRENT, extraLong, null, null);
            time = null;
        }
    }

    private static Time time = null;

    public static void setTime(Time t) {
        time = t;
    }

    /**
     * This is here so we can identify events and process them
     */
    protected class CalendarGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            mClickedView = mSingleTapUpView;
            mClickTime = System.currentTimeMillis();
            MonthWeekEventsView child;
            GridView gv = (GridView) ((MonthViewPagerAdapter) mViewPager
                    .getAdapter()).getPrimaryItem();
            Time mTouchday = mClickedView.getDayFromLocation(event.getX());
            if (mTouchday == null) {
                return false;
            }
            if (mTouchday.getJulianDay(mTouchday.toMillis(true),
                    mTouchday.gmtoff) >= Time.EPOCH_JULIAN_DAY
                    && mTouchday.getJulianDay(mTouchday.toMillis(true),
                            mTouchday.gmtoff) < MAXJULIANDAY) {
                for (int i = 0; i < 6; i++) {
                    child = (MonthWeekEventsView) gv.getChildAt(i);
                    if (child != null) {
                        child.clearClickedDay();
                    }
                }
                mClickedXLocation = event.getX();
                int currClickIndex = mClickedView.getDayIndexFromLocation(event
                        .getX());
                if (mClickedView.isClickDayHightLight(currClickIndex)) {
                    mClickedView.setClickedDay(event.getX());
                    if (MonthFragment.itemHeight == MonthFragment.dropHeight) {
                        mContext.startActivity(new Intent(mContext,
                                EventCardActivity.class)
                                .putExtra("card_year", mTouchday.year)
                                .putExtra("card_month", mTouchday.month + 1)
                                .putExtra("card_day", mTouchday.monthDay));
                        ((Activity) mContext).overridePendingTransition(
                                R.anim.open_enter, R.anim.open_exit);
                    }
                    Log.i("zhang", "ggggg" + mTouchday.year + " "
                            + mTouchday.month + "" + mTouchday.monthDay);
                    mDoClick();

                }
                sendClickEvent(mTouchday, true);
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent event) {
            if (mSingleTapUpView != null) {
                Time day = mSingleTapUpView
                        .getDayFromLocation(mClickedXLocation);
                if (day != null) {
                    mSingleTapUpView
                            .performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    Message message = new Message();
                    message.obj = day;
                    message.what = 1;
                    mEventDialogHandler.sendMessage(message);
                }
                // mSingleTapUpView.clearClickedDay();
                mSingleTapUpView = null;
            }
        }
    }

    // Clear the visual cues of the click animation and related running code.
    private void clearClickedView(MonthWeekEventsView v) {
        // mListView.removeCallbacks(mDoClick);
        synchronized (v) {
            v.clearClickedDay();
        }
        // mListView = null;
        // v.invalidate();
    }

    // Perform the tap animation in a runnable to allow a delay before showing
    // the tap color.
    // This is done to prevent a click animation when a fling is done.
    /**
     * private final Runnable mDoClick = new Runnable() {
     * 
     * @Override public void run() { if (mClickedView != null) {
     *           synchronized(mClickedView) { Log.d(TAG,
     *           "lxg onTouch mDoClick ===============");
     *           mClickedView.setClickedDay(mClickedXLocation); }
     *           mLongClickedView = mClickedView; //mListView = null; // This is
     *           a workaround , sometimes the top item on the listview doesn't
     *           refresh on // invalidate, so this forces a re-draw.
     *           mListView.invalidate(); mClickedView.invalidate();
     * 
     *           } } };
     */

    // Performs the single tap operation: go to the tapped day.
    // This is done in a runnable to allow the click animation to finish before
    // switching views
    private final Runnable mDoSingleTapUp = new Runnable() {
        @Override
        public void run() {
            if (mSingleTapUpView != null) {
                Time day = mSingleTapUpView
                        .getDayFromLocation(mClickedXLocation);
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Touched day at Row=" + mSingleTapUpView.mWeek
                            + " day=" + day.toString());
                }
                if (day != null) {
                    onDayTapped(day);
                }
                clearClickedView(mSingleTapUpView);
                mSingleTapUpView = null;
            }
        }
    };

    public void setItemHeight(int h) {
        mViewParamsHeight = h;
    }

    public int getItemHeight() {
        return mViewParamsHeight;
    }
}
