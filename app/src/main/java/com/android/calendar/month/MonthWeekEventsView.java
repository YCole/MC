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

package com.android.calendar.month;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract.Attendees;
//import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

import com.android.calendar.AllInOneActivity;
import com.android.calendar.Event;
import com.android.calendar.R;
import com.android.calendar.Utils;

public class MonthWeekEventsView extends SimpleWeekView {

    private static final String TAG = "MonthWeekEventsView";

    private static final boolean DEBUG_LAYOUT = false;

    public static final String VIEW_PARAMS_ORIENTATION = "orientation";
    public static final String VIEW_PARAMS_ANIMATE_TODAY = "animate_today";

    /* NOTE: these are not constants, and may be multiplied by a scale factor */
    private static int TEXT_SIZE_MONTH_NUMBER = 21;
    private static int TEXT_SIZE_EVENT = 11;
    private static int TEXT_SIZE_EVENT_TITLE = 14;
    private static int TEXT_SIZE_MORE_EVENTS = 12;
    private static int TEXT_SIZE_MONTH_NAME = 14;
    private static int TEXT_SIZE_WEEK_NUM = 12;
    // HCT_MODIFY lixiange MF3.0 add lunar Calendar
    private static int TEXT_SIZE_LUNAR_INFO = 10;
    private static int TEXT_SIZE_WORK_WEEK = 9;
    // HCT_MODIFY lixiange MF3.0 add lunar Calendar

    private static int DNA_MARGIN = 4;
    private static int DNA_ALL_DAY_HEIGHT = 4;
    private static int DNA_MIN_SEGMENT_HEIGHT = 4;
    private static int DNA_WIDTH = 8;
    private static int DNA_ALL_DAY_WIDTH = 32;
    private static int DNA_SIDE_PADDING = 6;
    private static int CONFLICT_COLOR = Color.BLACK;
    private static int EVENT_TEXT_COLOR = Color.WHITE;

    private static int DEFAULT_EDGE_SPACING = 0;
    private static int SIDE_PADDING_MONTH_NUMBER = 4;
    private static int TOP_PADDING_MONTH_NUMBER = 4;
    private static int TOP_PADDING_WEEK_NUMBER = 4;
    private static int SIDE_PADDING_WEEK_NUMBER = 20;
    private static int DAY_SEPARATOR_OUTER_WIDTH = 0;
    private static int DAY_SEPARATOR_INNER_WIDTH = 1;
    private static int DAY_SEPARATOR_VERTICAL_LENGTH = 53;
    private static int DAY_SEPARATOR_VERTICAL_LENGHT_PORTRAIT = 64;
    private static int MIN_WEEK_WIDTH = 50;
    private static int WORK_WEEK_DAY_WIDTH = 13;
    private static int WORK_WEEK_DAY_TOP = 1;
    private static int EVENT_LEFT_SPACING = 17;

    private static int EVENT_X_OFFSET_LANDSCAPE = 38;
    private static int EVENT_Y_OFFSET_LANDSCAPE = 8;
    private static int EVENT_Y_OFFSET_PORTRAIT = 7;
    private static int EVENT_SQUARE_WIDTH = 6;
    private static int EVENT_ORAL_WIDTH = 5;
    private static int EVENT_SQUARE_BORDER = 2;
    private static int EVENT_LINE_PADDING = 2;
    private static int EVENT_LINE_EXTRA_PADDING = 2;
    private static int EVENT_RIGHT_PADDING = 4;
    private static int EVENT_BOTTOM_PADDING = 3;

    private static int TODAY_HIGHLIGHT_WIDTH = 2;

    private static int SPACING_WEEK_NUMBER = 24;
    private static boolean mInitialized = false;
    private static boolean mShowDetailsInMonth;

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
    protected int mSelectDayIndex = -1;
    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */

    protected Time mToday = new Time();
    protected boolean mHasToday = false;
    protected int mTodayIndex = -1;
    protected int mTodayIndexDisplay = -1;
    protected int mOrientation = Configuration.ORIENTATION_LANDSCAPE;
    protected List<ArrayList<Event>> mEvents = null;
    protected ArrayList<Event> mUnsortedEvents = null;
    HashMap<Integer, Utils.DNAStrand> mDna = null;
    // This is for drawing the outlines around event chips and supports up to 10
    // events being drawn on each day. The code will expand this if necessary.
    protected FloatRef mEventOutlines = new FloatRef(10 * 4 * 4 * 7);

    protected static StringBuilder mStringBuilder = new StringBuilder(50);
    // TODO recreate formatter when locale changes
    protected static Formatter mFormatter = new Formatter(mStringBuilder,
            Locale.getDefault());

    protected Paint mMonthNamePaint;
    protected TextPaint mEventPaint;
    protected TextPaint mSolidBackgroundEventPaint;
    protected TextPaint mFramedEventPaint;
    protected TextPaint mDeclinedEventPaint;
    protected TextPaint mEventExtrasPaint;
    protected TextPaint mEventDeclinedExtrasPaint;
    protected Paint mWeekNumPaint;
    protected Paint mDNAAllDayPaint;
    protected Paint mDNATimePaint;
    protected Paint mEventSquarePaint;

    // HCT_MODIFY lixiange MF3.0 add lunar Calendar
    protected Paint mLunarInfoPaint;
    protected int mMonthLunarColor;
    protected int mFocusMonthWeekendColor;
    protected int mUnFocusMonthWeekendColor;
    protected int mDNAColor; // HCT_MODIFY longgang p340IV 2 DNA color
    protected int mSelectedDayColor; // HCT_MODIFY longgang P897A21 selected day
    private Drawable mWorkDayDrawable;
    private Drawable mWeekDayDrawable;
    private int mWorkWeekDayColor;
    private String workString;
    private String weekString;
    private int work_week_text_color;
    private int work_color;
    private int week_color;
    // HCT_MODIFY lixiange MF3.0 add lunar Calendar

    protected Drawable mTodayDrawable;

    protected int mMonthNumHeight;
    protected int mMonthNumAscentHeight;
    protected int mEventHeight;
    protected int mEventAscentHeight;
    protected int mExtrasHeight;
    protected int mExtrasAscentHeight;
    protected int mExtrasDescent;
    protected int mWeekNumAscentHeight;

    protected int mMonthBGColor;
    protected int mMonthBGOtherColor;
    protected int mMonthBGTodayColor;
    protected int mMonthNumColor;
    protected int mMonthNumOtherColor;
    protected int mMonthNumTodayColor;
    protected int mMonthNameColor;
    protected int mMonthNameOtherColor;
    protected int mMonthEventColor;
    protected int mMonthDeclinedEventColor;
    protected int mMonthDeclinedExtrasColor;
    protected int mMonthEventExtraColor;
    protected int mMonthEventOtherColor;
    protected int mMonthEventExtraOtherColor;
    protected int mMonthWeekNumColor;
    protected int mMonthBusyBitsBgColor;
    protected int mMonthBusyBitsBusyTimeColor;
    protected int mMonthBusyBitsConflictTimeColor;
    private int mClickedDayIndex = -1;
    private int mClickedDayColor;
    private static final int mClickedAlpha = 128;

    protected int mEventChipOutlineColor = 0xFFFFFFFF;
    protected int mDaySeparatorInnerColor;
    protected int mTodayAnimateColor;

    // private boolean mAnimateToday;
    // private int mAnimateTodayAlpha = 0;
    // private ObjectAnimator mTodayAnimator = null;

    // private final TodayAnimatorListener mAnimatorListener = new
    // TodayAnimatorListener();
    //
    // class TodayAnimatorListener extends AnimatorListenerAdapter {
    // private volatile Animator mAnimator = null;
    // private volatile boolean mFadingIn = false;
    //
    // @Override
    // public void onAnimationEnd(Animator animation) {
    // synchronized (this) {
    // if (mAnimator != animation) {
    // animation.removeAllListeners();
    // animation.cancel();
    // return;
    // }
    // if (mFadingIn) {
    // if (mTodayAnimator != null) {
    // mTodayAnimator.removeAllListeners();
    // mTodayAnimator.cancel();
    // }
    // mTodayAnimator = ObjectAnimator.ofInt(
    // MonthWeekEventsView.this, "animateTodayAlpha", 255,
    // 0);
    // mAnimator = mTodayAnimator;
    // mFadingIn = false;
    // mTodayAnimator.addListener(this);
    // mTodayAnimator.setDuration(600);
    // mTodayAnimator.start();
    // } else {
    // mAnimateToday = false;
    // mAnimateTodayAlpha = 0;
    // mAnimator.removeAllListeners();
    // mAnimator = null;
    // mTodayAnimator = null;
    // invalidate();
    // }
    // }
    // }
    //
    // public void setAnimator(Animator animation) {
    // mAnimator = animation;
    // }
    //
    // public void setFadingIn(boolean fadingIn) {
    // mFadingIn = fadingIn;
    // }
    //
    // }

    private int[] mDayXs;

    /**
     * This provides a reference to a float array which allows for easy size
     * checking and reallocation. Used for drawing lines.
     */
    private class FloatRef {
        float[] array;

        public FloatRef(int size) {
            array = new float[size];
        }

        public void ensureSize(int newSize) {
            if (newSize >= array.length) {
                // Add enough space for 7 more boxes to be drawn
                array = Arrays.copyOf(array, newSize + 16 * 7);
            }
        }
    }

    /**
     * Shows up as an error if we don't include this.
     */
    public MonthWeekEventsView(Context context) {
        super(context);
    }

    // Sets the list of events for this week. Takes a sorted list of arrays
    // divided up by day for generating the large month version and the full
    // arraylist sorted by start time to generate the dna version.
    public void setEvents(List<ArrayList<Event>> sortedEvents,
            ArrayList<Event> unsortedEvents) {
        setEvents(sortedEvents);
        // The MIN_WEEK_WIDTH is a hack to prevent the view from trying to
        // generate dna bits before its width has been fixed.
        createDna(unsortedEvents);
    }

    /**
     * Sets up the dna bits for the view. This will return early if the view
     * isn't in a state that will create a valid set of dna yet (such as the
     * views width not being set correctly yet).
     */
    public void createDna(ArrayList<Event> unsortedEvents) {
        if (unsortedEvents == null || mWidth <= MIN_WEEK_WIDTH
                || getContext() == null) {
            // Stash the list of events for use when this view is ready, or
            // just clear it if a null set has been passed to this view
            mUnsortedEvents = unsortedEvents;
            mDna = null;
            return;
        } else {
            // clear the cached set of events since we're ready to build it now
            mUnsortedEvents = null;
        }
        // Create the drawing coordinates for dna
        if (!mShowDetailsInMonth) {
            int numDays = mEvents.size();
            int effectiveWidth = mWidth - mPadding * 2;
            if (mShowWeekNum) {
                effectiveWidth -= SPACING_WEEK_NUMBER;
            }
            DNA_ALL_DAY_WIDTH = effectiveWidth / numDays - 2 * DNA_SIDE_PADDING;
            mDNAAllDayPaint.setStrokeWidth(DNA_ALL_DAY_WIDTH);
            mDayXs = new int[numDays];
            for (int day = 0; day < numDays; day++) {
                mDayXs[day] = computeDayLeftPosition(day) + DNA_WIDTH / 2
                        + DNA_SIDE_PADDING;

            }

            int top = DAY_SEPARATOR_INNER_WIDTH + DNA_MARGIN
                    + DNA_ALL_DAY_HEIGHT + 1;
            int bottom = mHeight - DNA_MARGIN;
            mDna = Utils.createDNAStrands(mFirstJulianDay, unsortedEvents, top,
                    bottom, DNA_MIN_SEGMENT_HEIGHT, mDayXs, getContext());
        }
    }

    public void setEvents(List<ArrayList<Event>> sortedEvents) {
        mEvents = sortedEvents;
        if (sortedEvents == null) {
            return;
        }
        if (sortedEvents.size() != mNumDays) {
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.wtf(TAG,
                        "Events size must be same as days displayed: size="
                                + sortedEvents.size() + " days=" + mNumDays);
            }
            mEvents = null;
            return;
        }
    }

    protected void loadColors(Context context) {
        Resources res = context.getResources();
        mMonthWeekNumColor = res.getColor(R.color.month_week_num_color);
        mMonthNumColor = res.getColor(R.color.month_day_number);
        mMonthNumOtherColor = res.getColor(R.color.month_day_number_other);
        mMonthNumTodayColor = res.getColor(R.color.month_today_number);
        mMonthNameColor = mMonthNumColor;
        mMonthNameOtherColor = mMonthNumOtherColor;
        mMonthEventColor = res.getColor(R.color.month_event_color);
        mMonthDeclinedEventColor = res
                .getColor(R.color.agenda_item_declined_color);
        mMonthDeclinedExtrasColor = res
                .getColor(R.color.agenda_item_where_declined_text_color);
        mMonthEventExtraColor = res.getColor(R.color.month_event_extra_color);
        mMonthEventOtherColor = res.getColor(R.color.month_event_other_color);
        mMonthEventExtraOtherColor = res
                .getColor(R.color.month_event_extra_other_color);
        mMonthBGTodayColor = res.getColor(R.color.month_today_bgcolor);
        mMonthBGOtherColor = res.getColor(R.color.month_other_bgcolor);
        mMonthBGColor = res.getColor(R.color.month_bgcolor);
        mDaySeparatorInnerColor = res.getColor(R.color.month_grid_lines);
        mTodayAnimateColor = res.getColor(R.color.today_highlight_color);
        mClickedDayColor = res
                .getColor(R.color.cale_monthview_today_fill_color);
        mTodayDrawable = res.getDrawable(R.drawable.today_blue_week_holo_light);
        // HCT_MODIFY lixiange MF3.0 add lunar Calendar
        mMonthLunarColor = res.getColor(R.color.month_lunar_color);
        mFocusMonthWeekendColor = res.getColor(R.color.week_saturday);
        mUnFocusMonthWeekendColor = res.getColor(R.color.month_unfocus_weekend);
        mDNAColor = res.getColor(R.color.dna_color); // HCT_MODIFY longgang
                                                     // p340IV 2 DNA color
        mSelectedDayColor = res
                .getColor(R.color.cale_monthview_selectday_border_color);
        mWorkDayDrawable = res.getDrawable(R.drawable.ic_duty_bg);
        mWeekDayDrawable = res.getDrawable(R.drawable.ic_vocation_bg);
        mWorkWeekDayColor = res.getColor(android.R.color.white);
        workString = res.getString(R.string.show_workday);
        weekString = res.getString(R.string.show_holiday);
        work_week_text_color = res.getColor(R.color.work_week_text_color);
        work_color = res.getColor(R.color.work_color);
        week_color = res.getColor(R.color.week_color);
        // HCT_MODIFY lixiange MF3.0 add lunar Calendar
    }

    /**
     * Sets up the text and style properties for painting. Override this if you
     * want to use a different paint.
     */
    @Override
    protected void initView() {
        super.initView();

        if (!mInitialized) {
            Resources resources = getContext().getResources();
            mShowDetailsInMonth = Utils.getConfigBool(getContext(),
                    R.bool.show_details_in_month);
            TEXT_SIZE_EVENT_TITLE = resources
                    .getInteger(R.integer.text_size_event_title);
            TEXT_SIZE_MONTH_NUMBER = resources
                    .getInteger(R.integer.text_size_month_number);
            SIDE_PADDING_MONTH_NUMBER = resources
                    .getInteger(R.integer.month_day_number_margin);
            CONFLICT_COLOR = resources
                    .getColor(R.color.month_dna_conflict_time_color);
            EVENT_TEXT_COLOR = resources
                    .getColor(R.color.calendar_event_text_color);
            if (mScale != 1) {
                TOP_PADDING_MONTH_NUMBER *= mScale;
                TOP_PADDING_WEEK_NUMBER *= mScale;
                SIDE_PADDING_MONTH_NUMBER *= mScale;
                SIDE_PADDING_WEEK_NUMBER *= mScale;
                SPACING_WEEK_NUMBER *= mScale;
                TEXT_SIZE_MONTH_NUMBER *= mScale;
                // HCT_MODIFY lixiange MF3.0 add lunar Calendar
                TEXT_SIZE_LUNAR_INFO *= mScale;
                mHeight *= mScale;
                TEXT_SIZE_WORK_WEEK *= mScale;
                // HCT_MODIFY lixiange MF3.0 add lunar Calendar
                TEXT_SIZE_EVENT *= mScale;
                TEXT_SIZE_EVENT_TITLE *= mScale;
                TEXT_SIZE_MORE_EVENTS *= mScale;
                TEXT_SIZE_MONTH_NAME *= mScale;
                TEXT_SIZE_WEEK_NUM *= mScale;
                DAY_SEPARATOR_OUTER_WIDTH *= mScale;
                DAY_SEPARATOR_INNER_WIDTH *= mScale;
                DAY_SEPARATOR_VERTICAL_LENGTH *= mScale;
                DAY_SEPARATOR_VERTICAL_LENGHT_PORTRAIT *= mScale;
                EVENT_X_OFFSET_LANDSCAPE *= mScale;
                EVENT_Y_OFFSET_LANDSCAPE *= mScale;
                EVENT_Y_OFFSET_PORTRAIT *= mScale;
                EVENT_SQUARE_WIDTH *= mScale;
                EVENT_ORAL_WIDTH *= mScale;
                EVENT_SQUARE_BORDER *= mScale;
                EVENT_LINE_PADDING *= mScale;
                EVENT_LINE_EXTRA_PADDING *= mScale;
                EVENT_BOTTOM_PADDING *= mScale;
                EVENT_RIGHT_PADDING *= mScale;
                DNA_MARGIN *= mScale;
                DNA_WIDTH *= mScale;
                DNA_ALL_DAY_HEIGHT *= mScale;
                DNA_MIN_SEGMENT_HEIGHT *= mScale;
                DNA_SIDE_PADDING *= mScale;
                DEFAULT_EDGE_SPACING *= mScale;
                DNA_ALL_DAY_WIDTH *= mScale;
                TODAY_HIGHLIGHT_WIDTH *= mScale;
                WORK_WEEK_DAY_WIDTH *= mScale;
                WORK_WEEK_DAY_TOP *= mScale;
                EVENT_LEFT_SPACING *= mScale;
            }
            if (!mShowDetailsInMonth) {
                TOP_PADDING_MONTH_NUMBER += DNA_ALL_DAY_HEIGHT + DNA_MARGIN;
            }
            mInitialized = true;
        }
        mPadding = DEFAULT_EDGE_SPACING;
        loadColors(getContext());
        // TODO modify paint properties depending on isMini

        mMonthNumPaint = new Paint();
        mMonthNumPaint.setFakeBoldText(false);
        mMonthNumPaint.setAntiAlias(true);
        mMonthNumPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
        mMonthNumPaint.setColor(mMonthNumColor);
        mMonthNumPaint.setStyle(Style.FILL);
        mMonthNumPaint.setTextAlign(Align.RIGHT);
        mMonthNumPaint.setTypeface(Typeface.DEFAULT);

        mMonthNumAscentHeight = (int) (-mMonthNumPaint.ascent() + 0.5f);
        mMonthNumHeight = (int) (mMonthNumPaint.descent()
                - mMonthNumPaint.ascent() + 0.5f);

        // HCT_MODIFY lixiange MF3.0 add lunar Calendar
        mLunarInfoPaint = new Paint();
        mLunarInfoPaint.setFakeBoldText(false);
        mLunarInfoPaint.setAntiAlias(true);
        mLunarInfoPaint.setTextSize(TEXT_SIZE_LUNAR_INFO);
        mLunarInfoPaint.setColor(mMonthLunarColor);
        mLunarInfoPaint.setStyle(Style.FILL);
        mLunarInfoPaint.setTextAlign(Align.RIGHT);
        mLunarInfoPaint.setTypeface(Typeface.DEFAULT);
        // HCT_MODIFY lixiange MF3.0 add lunar Calendar

        mEventPaint = new TextPaint();
        mEventPaint.setFakeBoldText(true);
        mEventPaint.setAntiAlias(true);
        mEventPaint.setTextSize(TEXT_SIZE_EVENT_TITLE);
        mEventPaint.setColor(mMonthEventColor);

        mSolidBackgroundEventPaint = new TextPaint(mEventPaint);
        mSolidBackgroundEventPaint.setColor(EVENT_TEXT_COLOR);
        mFramedEventPaint = new TextPaint(mSolidBackgroundEventPaint);

        mDeclinedEventPaint = new TextPaint();
        mDeclinedEventPaint.setFakeBoldText(true);
        mDeclinedEventPaint.setAntiAlias(true);
        mDeclinedEventPaint.setTextSize(TEXT_SIZE_EVENT_TITLE);
        mDeclinedEventPaint.setColor(mMonthDeclinedEventColor);

        mEventAscentHeight = (int) (-mEventPaint.ascent() + 0.5f);
        mEventHeight = (int) (mEventPaint.descent() - mEventPaint.ascent() + 0.5f);

        mEventExtrasPaint = new TextPaint();
        mEventExtrasPaint.setFakeBoldText(false);
        mEventExtrasPaint.setAntiAlias(true);
        mEventExtrasPaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventExtrasPaint.setTextSize(TEXT_SIZE_EVENT);
        mEventExtrasPaint.setColor(mMonthEventExtraColor);
        mEventExtrasPaint.setStyle(Style.FILL);
        mEventExtrasPaint.setTextAlign(Align.LEFT);
        mExtrasHeight = (int) (mEventExtrasPaint.descent()
                - mEventExtrasPaint.ascent() + 0.5f);
        mExtrasAscentHeight = (int) (-mEventExtrasPaint.ascent() + 0.5f);
        mExtrasDescent = (int) (mEventExtrasPaint.descent() + 0.5f);

        mEventDeclinedExtrasPaint = new TextPaint();
        mEventDeclinedExtrasPaint.setFakeBoldText(false);
        mEventDeclinedExtrasPaint.setAntiAlias(true);
        mEventDeclinedExtrasPaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventDeclinedExtrasPaint.setTextSize(TEXT_SIZE_EVENT);
        mEventDeclinedExtrasPaint.setColor(mMonthDeclinedExtrasColor);
        mEventDeclinedExtrasPaint.setStyle(Style.FILL);
        mEventDeclinedExtrasPaint.setTextAlign(Align.LEFT);

        mWeekNumPaint = new Paint();
        mWeekNumPaint.setFakeBoldText(false);
        mWeekNumPaint.setAntiAlias(true);
        mWeekNumPaint.setTextSize(TEXT_SIZE_WEEK_NUM);
        mWeekNumPaint.setColor(mWeekNumColor);
        mWeekNumPaint.setStyle(Style.FILL);
        mWeekNumPaint.setTextAlign(Align.RIGHT);

        mWeekNumAscentHeight = (int) (-mWeekNumPaint.ascent() + 0.5f);

        mDNAAllDayPaint = new Paint();
        mDNATimePaint = new Paint();
        mDNATimePaint.setColor(mMonthBusyBitsBusyTimeColor);
        mDNATimePaint.setStyle(Style.FILL_AND_STROKE);
        mDNATimePaint.setStrokeWidth(DNA_WIDTH);
        mDNATimePaint.setAntiAlias(false);
        mDNAAllDayPaint.setColor(mMonthBusyBitsConflictTimeColor);
        mDNAAllDayPaint.setStyle(Style.FILL_AND_STROKE);
        mDNAAllDayPaint.setStrokeWidth(DNA_ALL_DAY_WIDTH);
        mDNAAllDayPaint.setAntiAlias(false);

        mEventSquarePaint = new Paint();
        mEventSquarePaint.setStrokeWidth(EVENT_SQUARE_BORDER);
        mEventSquarePaint.setAntiAlias(false);

        if (DEBUG_LAYOUT) {
            Log.d("EXTRA", "mScale=" + mScale);
            Log.d("EXTRA", "mMonthNumPaint ascent=" + mMonthNumPaint.ascent()
                    + " descent=" + mMonthNumPaint.descent() + " int height="
                    + mMonthNumHeight);
            Log.d("EXTRA", "mEventPaint ascent=" + mEventPaint.ascent()
                    + " descent=" + mEventPaint.descent() + " int height="
                    + mEventHeight + " int ascent=" + mEventAscentHeight);
            Log.d("EXTRA",
                    "mEventExtrasPaint ascent=" + mEventExtrasPaint.ascent()
                            + " descent=" + mEventExtrasPaint.descent()
                            + " int height=" + mExtrasHeight);
            Log.d("EXTRA", "mWeekNumPaint ascent=" + mWeekNumPaint.ascent()
                    + " descent=" + mWeekNumPaint.descent());
        }
    }

    @Override
    public void setWeekParams(HashMap<String, Integer> params, String tz) {
        super.setWeekParams(params, tz);

        if (params.containsKey(VIEW_PARAMS_ORIENTATION)) {
            mOrientation = params.get(VIEW_PARAMS_ORIENTATION);
        }

        updateToday(tz);
        /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
        if (params.containsKey("JUMP_DATE")) {
            int selectday = params.get("JUMP_DATE");
            updateSelectDay(selectday);
            clearClickedDay();
            ContentResolver resolver = getContext().getContentResolver();
            final Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(AllInOneActivity.mSelectDate.toMillis(true));
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            long mSelectTime = ca.getTimeInMillis();
            Settings.System
                    .putLong(resolver, "calendar_touch_day", mSelectTime);
        }
        /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */

        mNumCells = mNumDays + 1;

        /**
         * if (params.containsKey(VIEW_PARAMS_ANIMATE_TODAY) && mHasToday) {
         * synchronized (mAnimatorListener) { if (mTodayAnimator != null) {
         * mTodayAnimator.removeAllListeners(); mTodayAnimator.cancel(); }
         * mTodayAnimator = ObjectAnimator.ofInt(this, "animateTodayAlpha",
         * Math.max(mAnimateTodayAlpha, 80), 255);
         * mTodayAnimator.setDuration(150);
         * mAnimatorListener.setAnimator(mTodayAnimator);
         * mAnimatorListener.setFadingIn(true);
         * mTodayAnimator.addListener(mAnimatorListener); mAnimateToday = true;
         * mTodayAnimator.start(); } }
         */
    }

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
    public void updateSelectDay(int selectday) {
        mSelectDayIndex = selectday - mFirstJulianDay;
    }

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */

    /**
     * @param tz
     */
    public boolean updateToday(String tz) {
        clearClickedDay();
        mToday.timezone = tz;
        mToday.setToNow();
        mToday.normalize(true);
        int julianToday = Time.getJulianDay(mToday.toMillis(false),
                mToday.gmtoff);
        if (julianToday >= mFirstJulianDay
                && julianToday < mFirstJulianDay + mNumDays) {
            mHasToday = true;
            mTodayIndex = julianToday - mFirstJulianDay;
            mTodayIndexDisplay = mTodayIndex;
        } else {
            mHasToday = false;
            mTodayIndex = -1;
            mTodayIndexDisplay = -1;
        }
        if (mHasToday) {
            final Calendar ca = Calendar.getInstance();
            ca.setTimeInMillis(mToday.toMillis(true));
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            long TodayTime = ca.getTimeInMillis();
            ContentResolver resolver = getContext().getContentResolver();
            Settings.System.putLong(resolver, "calendar_touch_day", TodayTime);
        }
        return mHasToday;
    }

    // public void setAnimateTodayAlpha(int alpha) {
    // mAnimateTodayAlpha = alpha;
    // invalidate();
    // }

    @Override
    protected void onDraw(Canvas canvas) {
        // FUCK !!!!
        if (1 == 1)
            throw new RuntimeException("fuck me !!!!!!!!!");
        drawBackground(canvas);
        drawHoliday(canvas);
        // drawDaySeparators(canvas);
        /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
        if (mSelectDayIndex != -1) {
            drawSelectDay(canvas);
            mSelectDayIndex = -1;
        }
        /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */
        if (mTodayIndexDisplay != -1) {// && mAnimateToday) {
            drawToday(canvas);
        }
        // lixiange MF3.0 draw one event
        // if (mShowDetailsInMonth) {
        drawEvents(canvas);
        /**
         * } else { if (mDna == null && mUnsortedEvents != null) {
         * createDna(mUnsortedEvents); } drawDNA(canvas); }
         */
        // lixiange MF3.0 draw one event
        drawClick(canvas);
        drawHoliday(canvas);
    }

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght begin */
    protected void drawSelectDay(Canvas canvas) {
        /**
         * r.top = DAY_SEPARATOR_INNER_WIDTH; r.bottom = mHeight;
         * p.setStyle(Style.STROKE); p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH / 2
         * + 1); r.left = computeDayLeftPosition(mSelectDayIndex); r.right =
         * computeDayLeftPosition(mSelectDayIndex + 1); p.setColor(0xFF46c0ce);
         * canvas.drawRect(r, p); p.setStyle(Style.FILL);
         */
        int alpha = p.getAlpha();
        Paint p = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        // p.setStyle(Style.STROKE);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH / 2);
        p.setColor(getResources().getColor(
                R.color.cale_monthview_selectday_border_color));
        r.left = computeDayLeftPosition(mSelectDayIndex)
                + DAY_SEPARATOR_INNER_WIDTH / 3;
        r.right = computeDayLeftPosition(mSelectDayIndex + 1)
                - DAY_SEPARATOR_INNER_WIDTH / 3;
        r.top = DAY_SEPARATOR_INNER_WIDTH / 3;
        r.bottom = mHeight - DAY_SEPARATOR_INNER_WIDTH / 3;

        // float cx=r.left+(r.right-r.left)/2;
        // float cy=r.top+(r.bottom-r.top)/2;
        // canvas.drawCircle(cx,cy,20,p);
        canvas.drawRect(r, p);
        p.setStyle(Style.FILL);
    }

    /* HCT_MODIFY lixiange MF3.0 for jump date and hilght end */

    protected void drawToday(Canvas canvas) {
        Paint p = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        // r.top = DAY_SEPARATOR_INNER_WIDTH + (TODAY_HIGHLIGHT_WIDTH / 2);
        // r.bottom = mHeight - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        p.setStyle(Style.STROKE);
        p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH / 2);
        p.setColor(getResources().getColor(
                R.color.cale_monthview_today_fill_color));
        // r.left = computeDayLeftPosition(mTodayIndex)
        // + (TODAY_HIGHLIGHT_WIDTH / 2);
        // r.right = computeDayLeftPosition(mTodayIndex + 1)
        // - (int) Math.ceil(TODAY_HIGHLIGHT_WIDTH / 2.0f);
        // p.setColor(mTodayAnimateColor | (mAnimateTodayAlpha << 24));
        r.left = computeDayLeftPosition(mTodayIndexDisplay)
                + DAY_SEPARATOR_INNER_WIDTH / 3;
        r.right = computeDayLeftPosition(mTodayIndexDisplay + 1)
                - DAY_SEPARATOR_INNER_WIDTH / 3;
        r.top = DAY_SEPARATOR_INNER_WIDTH / 3;
        r.bottom = mHeight - DAY_SEPARATOR_INNER_WIDTH / 3;
        // float cx=r.left+(r.right-r.left)/2;
        // float cy=r.top+(r.bottom-r.top)/2;
        // canvas.drawCircle(cx,cy,20,p);
        canvas.drawRect(r, p);
        p.setStyle(Style.FILL);
    }

    // Draw the "clicked" color on the tapped day
    private void drawClick(Canvas canvas) {
        if (mClickedDayIndex != -1) {
            int alpha = p.getAlpha();
            Paint p = new Paint(Paint.SUBPIXEL_TEXT_FLAG
                    | Paint.ANTI_ALIAS_FLAG);
            // p.setStyle(Style.STROKE);
            p.setStyle(Style.STROKE);
            p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH / 2);
            p.setColor(getResources().getColor(
                    R.color.cale_monthview_selectday_border_color));
            r.left = computeDayLeftPosition(mClickedDayIndex)
                    + DAY_SEPARATOR_INNER_WIDTH / 3;
            r.right = computeDayLeftPosition(mClickedDayIndex + 1)
                    - DAY_SEPARATOR_INNER_WIDTH / 3;
            r.top = DAY_SEPARATOR_INNER_WIDTH / 3;
            r.bottom = mHeight - DAY_SEPARATOR_INNER_WIDTH / 3;
            canvas.drawRect(r, p);
            p.setStyle(Style.FILL);
        }
    }

    // TODO move into SimpleWeekView
    // Computes the x position for the left side of the given day
    private int computeDayLeftPosition(int day) {
        int effectiveWidth = mWidth;
        int x = 0;
        int xOffset = 0;
        if (mShowWeekNum) {
            xOffset = SPACING_WEEK_NUMBER + mPadding;
            effectiveWidth -= xOffset;
        }
        x = day * effectiveWidth / mNumDays + xOffset;
        return x;
    }

    @Override
    protected void drawDaySeparators(Canvas canvas) {
        float lines[] = new float[8 * 4];
        int count = 6 * 4;
        int wkNumOffset = 0;
        int i = 0;
        if (mShowWeekNum) {
            // This adds the first line separating the week number
            int xOffset = SPACING_WEEK_NUMBER + mPadding;
            count += 4;
            lines[i++] = xOffset;
            lines[i++] = 0;
            lines[i++] = xOffset;
            lines[i++] = mHeight;
            wkNumOffset++;
        }
        count += 4;
        lines[i++] = 0;
        lines[i++] = 0;
        lines[i++] = mWidth;
        lines[i++] = 0;
        int y0 = 0;
        int y1 = mHeight;
        Paint p2 = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        p2.setColor(mDaySeparatorInnerColor);
        p2.setStrokeWidth(DAY_SEPARATOR_INNER_WIDTH * 7 / 8);
        canvas.drawLines(lines, 0, count, p2);
        /**
         * while (i < count) { int x = computeDayLeftPosition(i / 4);// -
         * wkNumOffset); lines[i++] = x; lines[i++] = y0; lines[i++] = x;
         * lines[i++] = y1; } Paint p = new Paint(Paint.SUBPIXEL_TEXT_FLAG |
         * Paint.ANTI_ALIAS_FLAG); p.setColor(mDaySeparatorInnerColor);
         * p.setStrokeWidth(DAY_SEPARATOR_INNER_WIDTH / 3);
         * canvas.drawLines(lines, 0, count, p);
         */
    }

    @Override
    protected void drawBackground(Canvas canvas) {
        int i = 0;
        int j = 0;
        int offset = 0;
        r.top = DAY_SEPARATOR_INNER_WIDTH - DAY_SEPARATOR_INNER_WIDTH * 2;
        r.bottom = mHeight - 1 / 3;
        if (mShowWeekNum) {
            i++;
            j++;
            offset++;
        }
        if (!mFocusDay[i]) {
            while (++i < mFocusDay.length && !mFocusDay[i])
                ;
            r.right = computeDayLeftPosition(i - offset);
            r.left = 0;
            p.setColor(mMonthBGColor);// mMonthBGOtherColor);
            canvas.drawRect(r, p);
            // compute left edge for i, set up r, draw
        } else if (!mFocusDay[(i = mFocusDay.length - 1)]) {
            while (--i >= offset && !mFocusDay[i])
                ;
            i++;
            // compute left edge for i, set up r, draw
            r.right = mWidth;
            r.left = computeDayLeftPosition(i - offset);
            p.setColor(mMonthBGColor);// mMonthBGOtherColor);
            canvas.drawRect(r, p);
        }

        if (mFocusDay[j]) {
            while (++j < mFocusDay.length && mFocusDay[j])
                ;
            r.right = computeDayLeftPosition(j - offset);
            r.left = 0;
            p.setColor(mMonthBGColor);
            canvas.drawRect(r, p);
            // compute left edge for j, set up r, draw
        } else if (mFocusDay[(j = mFocusDay.length - 1)]) {
            while (--j >= offset && mFocusDay[j])
                ;
            j++;
            // compute left edge for j, set up r, draw
            r.right = mWidth;
            r.left = computeDayLeftPosition(j - offset);
            p.setColor(mMonthBGColor);
            canvas.drawRect(r, p);
        }

        if (mHasToday) {
            // p.setStyle(Style.STROKE);
            // p.setStrokeWidth(TODAY_HIGHLIGHT_WIDTH/2+1);
            p.setColor(mClickedDayColor);
            r.left = computeDayLeftPosition(mTodayIndex);
            r.right = computeDayLeftPosition(mTodayIndex + 1);
            canvas.drawRect(r, p);
            p.setStyle(Style.FILL);

            Paint paint = new Paint();
            paint.setStyle(Style.STROKE);
            paint.setStrokeWidth(WORK_WEEK_DAY_TOP / 3);
            paint.setColor(mSelectedDayColor);
            r.left = computeDayLeftPosition(mTodayIndex);
            r.right = computeDayLeftPosition(mTodayIndex + 1);
            r.top = DAY_SEPARATOR_INNER_WIDTH + DAY_SEPARATOR_INNER_WIDTH / 3;
            r.bottom = mHeight;
            canvas.drawRect(r, paint);
            paint.setStyle(Style.FILL);
        }
    }

    // HCT_MODIFY lixiange MF3.0 add lunar Calendar
    /**
     * Determine whether the column position is Saturday or not.
     * 
     * @param column
     *            the column position
     * @param firstDayOfWeek
     *            the first day of week in android.text.format.Time
     * @return true if the column is Saturday position
     */
    private boolean isSaturday(int column, int firstDayOfWeek,
            boolean showWeekNum) {
        if (showWeekNum) {
            return (firstDayOfWeek == Time.SUNDAY && column == 7)
                    || (firstDayOfWeek == Time.MONDAY && column == 6)
                    || (firstDayOfWeek == Time.SATURDAY && column == 1);
        } else {
            return (firstDayOfWeek == Time.SUNDAY && column == 6)
                    || (firstDayOfWeek == Time.MONDAY && column == 5)
                    || (firstDayOfWeek == Time.SATURDAY && column == 0);
        }
    }

    /**
     * Determine whether the column position is Sunday or not.
     * 
     * @param column
     *            the column position
     * @param firstDayOfWeek
     *            the first day of week in android.text.format.Time
     * @return true if the column is Sunday position
     */
    private boolean isSunday(int column, int firstDayOfWeek, boolean showWeekNum) {
        if (showWeekNum) {
            return (firstDayOfWeek == Time.SUNDAY && column == 1)
                    || (firstDayOfWeek == Time.MONDAY && column == 7)
                    || (firstDayOfWeek == Time.SATURDAY && column == 2);
        } else {
            return (firstDayOfWeek == Time.SUNDAY && column == 0)
                    || (firstDayOfWeek == Time.MONDAY && column == 6)
                    || (firstDayOfWeek == Time.SATURDAY && column == 1);
        }

    }

    private void drawWorkWeekBackground(int i, Canvas canvas, int pInt) {
        Paint paint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
                | Paint.ANTI_ALIAS_FLAG);
        Paint paintText = new Paint(Paint.SUBPIXEL_TEXT_FLAG
                | Paint.ANTI_ALIAS_FLAG);
        paintText.setColor(work_week_text_color);
        paintText.setTextSize(TEXT_SIZE_WORK_WEEK);
        Rect r = new Rect();
        r.left = computeDayLeftPosition(i);
        i = mShowWeekNum ? 1 + i : i;
        r.right = r.left + WORK_WEEK_DAY_WIDTH;
        r.top = DAY_SEPARATOR_INNER_WIDTH - WORK_WEEK_DAY_TOP;
        r.bottom = r.top + WORK_WEEK_DAY_WIDTH;

        if (workString.equals(mWorkDayWeekDay[i])) {
            p.setColor(work_color);
            p.setTextSize(TEXT_SIZE_EVENT);
            // canvas.drawRect(r, p);
            canvas.drawText(mWorkDayWeekDay[i], pInt + WORK_WEEK_DAY_WIDTH
                    + EVENT_Y_OFFSET_PORTRAIT, TEXT_SIZE_MONTH_NAME
                    + EVENT_SQUARE_BORDER, p);
        } else if (weekString.equals(mWorkDayWeekDay[i])) {
            p.setColor(week_color);
            p.setTextSize(TEXT_SIZE_EVENT);
            // canvas.drawRect(r, p);
            canvas.drawText(mWorkDayWeekDay[i], pInt + WORK_WEEK_DAY_WIDTH
                    + EVENT_Y_OFFSET_PORTRAIT, TEXT_SIZE_MONTH_NAME
                    + EVENT_SQUARE_BORDER, p);
        }
    }

    // HCT_MODIFY lixiange MF3.0 add lunar Calendar

    private void drawHoliday(Canvas canvas) {
        Context context = getContext();
        int y;

        int i = 0;
        int offset = -1;
        int todayIndex = mTodayIndex;
        int x = 0;
        int numCount = mNumDays;
        int suqrWidth = computeDayLeftPosition(2) - computeDayLeftPosition(1);
        int p;// = computeDayLeftPosition(i)+suqrWidth;
        if (mShowWeekNum) {
            x = SIDE_PADDING_WEEK_NUMBER + mPadding;
            y = mWeekNumAscentHeight + TOP_PADDING_WEEK_NUMBER;
            canvas.drawText(mDayNumbers[0], x, y, mWeekNumPaint);
            numCount++;
            i++;
            todayIndex++;
            offset++;

        }

        y = mMonthNumAscentHeight + TOP_PADDING_MONTH_NUMBER;
        boolean isFocusMonth = mFocusDay[i];
        boolean isBold = false;
        boolean isWeekend = false;
        mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor
                : mMonthNumOtherColor);
        for (; i < numCount; i++) {
            /**
             * if (mHasToday && todayIndex == i) {
             * //mMonthNumPaint.setColor(mMonthNumTodayColor);
             * //mMonthNumPaint.setFakeBoldText(isBold = true); if (i + 1 <
             * numCount) { // Make sure the color will be set back on the next
             * // iteration isFocusMonth = !mFocusDay[i + 1]; } } else {//if
             * (mFocusDay[i] != isFocusMonth) {
             */
            isFocusMonth = mFocusDay[i];
            // }
            mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor
                    : mMonthNumOtherColor);
            if (mShowWeekNum) {
                p = computeDayLeftPosition(i - 1) + suqrWidth;
            } else {
                p = computeDayLeftPosition(i);
            }
            // HCT_MODIFY lixiange MF3.0 for P897A21 set num in the middle of
            // the day
            if (mShowWeekNum) {
                x = computeDayLeftPosition(i - offset)
                        - (mWidth - SPACING_WEEK_NUMBER + mPadding)
                        / (2 * mNumDays);
            } else {
                x = computeDayLeftPosition(i - offset) - mWidth
                        / (2 * mNumDays);
            }

            /**
             * isWeekend = isSaturday(i, Utils.getFirstDayOfWeek(context),
             * mShowWeekNum)||isSunday(i, Utils.getFirstDayOfWeek(context),
             * mShowWeekNum); Log.i(TAG,"isWeekend = " + isWeekend);
             * Log.i(TAG,"mDayNumbers[i] = " + mDayNumbers[i]); if (isWeekend) {
             * mMonthNumPaint.setColor(isFocusMonth ? mFocusMonthWeekendColor :
             * mUnFocusMonthWeekendColor); mLunarInfoPaint.setColor(isFocusMonth
             * ? mFocusMonthWeekendColor : mUnFocusMonthWeekendColor); }
             */

            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                mMonthNumPaint.setTextAlign(Align.CENTER);
                mMonthNumPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
                canvas.drawText(getContext().getString(R.string.lunar_rest), x,
                        y + 15, mMonthNumPaint);
            } else {
                mMonthNumPaint.setTextAlign(Align.CENTER);
                canvas.drawText(getContext().getString(R.string.lunar_work), x,
                        mMonthNumHeight + 7, mMonthNumPaint);
            }
        }

    }

    @Override
    protected void drawWeekNums(Canvas canvas) {
        Context context = getContext();
        int y;

        int i = 0;
        int offset = -1;
        int todayIndex = mTodayIndex;
        int x = 0;
        int numCount = mNumDays;
        int suqrWidth = computeDayLeftPosition(2) - computeDayLeftPosition(1);
        int p;// = computeDayLeftPosition(i)+suqrWidth;
        if (mShowWeekNum) {
            x = SIDE_PADDING_WEEK_NUMBER + mPadding;
            y = mWeekNumAscentHeight + TOP_PADDING_WEEK_NUMBER;
            canvas.drawText(mDayNumbers[0], x, y, mWeekNumPaint);
            numCount++;
            i++;
            todayIndex++;
            offset++;

        }

        y = mMonthNumAscentHeight + TOP_PADDING_MONTH_NUMBER;
        boolean isFocusMonth = mFocusDay[i];
        boolean isBold = false;
        boolean isWeekend = false;
        mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor
                : mMonthNumOtherColor);
        for (; i < numCount; i++) {
            /**
             * if (mHasToday && todayIndex == i) {
             * //mMonthNumPaint.setColor(mMonthNumTodayColor);
             * //mMonthNumPaint.setFakeBoldText(isBold = true); if (i + 1 <
             * numCount) { // Make sure the color will be set back on the next
             * // iteration isFocusMonth = !mFocusDay[i + 1]; } } else {//if
             * (mFocusDay[i] != isFocusMonth) {
             */
            isFocusMonth = mFocusDay[i];
            // }
            mMonthNumPaint.setColor(isFocusMonth ? mMonthNumColor
                    : mMonthNumOtherColor);
            if (mShowWeekNum) {
                p = computeDayLeftPosition(i - 1) + suqrWidth;
            } else {
                p = computeDayLeftPosition(i);
            }
            // HCT_MODIFY lixiange MF3.0 for P897A21 set num in the middle of
            // the day
            if (mShowWeekNum) {
                x = computeDayLeftPosition(i - offset)
                        - (mWidth - SPACING_WEEK_NUMBER + mPadding)
                        / (2 * mNumDays);
            } else {
                x = computeDayLeftPosition(i - offset) - mWidth
                        / (2 * mNumDays);
            }

            /**
             * isWeekend = isSaturday(i, Utils.getFirstDayOfWeek(context),
             * mShowWeekNum)||isSunday(i, Utils.getFirstDayOfWeek(context),
             * mShowWeekNum); Log.i(TAG,"isWeekend = " + isWeekend);
             * Log.i(TAG,"mDayNumbers[i] = " + mDayNumbers[i]); if (isWeekend) {
             * mMonthNumPaint.setColor(isFocusMonth ? mFocusMonthWeekendColor :
             * mUnFocusMonthWeekendColor); mLunarInfoPaint.setColor(isFocusMonth
             * ? mFocusMonthWeekendColor : mUnFocusMonthWeekendColor); }
             */
            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                mMonthNumPaint.setTextAlign(Align.CENTER);
                mMonthNumPaint.setTextSize(TEXT_SIZE_MONTH_NUMBER);
                canvas.drawText(mDayNumbers[i], x, y + 15, mMonthNumPaint);
            } else {
                mMonthNumPaint.setTextAlign(Align.CENTER);
                canvas.drawText(mDayNumbers[i], x, mMonthNumHeight + 7,
                        mMonthNumPaint);
            }

            if (isBold) {
                mMonthNumPaint.setFakeBoldText(isBold = false);
            }

            boolean hasDayInfo = !mDayInfos[i].equals("");
            if (!hasDayInfo)
                continue;

            // if (mHasToday && todayIndex == i){
            // mLunarInfoPaint.setColor(mMonthNumTodayColor);
            // }else {
            if (mFocusDay[i]) {
                mLunarInfoPaint.setColor(mMonthLunarColor);
            } else {
                mLunarInfoPaint.setColor(mMonthNumOtherColor);
            }
            // }

            if (mShowWeekNum) {
                mLunarInfoPaint.setTextSize(TEXT_SIZE_LUNAR_INFO - 2);
            } else {
                mLunarInfoPaint.setTextSize(TEXT_SIZE_LUNAR_INFO);
            }
            // HCT_MODIFY longgang for P897A21 set the text in the middle of the
            // day
            mLunarInfoPaint.setTextAlign(Align.CENTER);

            // String isAbroad = SystemProperties.get("ro.gios.custom", "home");
            Boolean isAbroad = Utils.isAbroadBranch(context);
            if (!isAbroad) {
                if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                    /**
                     * String mTodayInfos = mDayInfos[i];
                     * if(mTodayInfos.contains("/")) { String[] ary =
                     * mTodayInfos.split("/"); for(int n = 0; n < ary.length;
                     * n++){ canvas.drawText(ary[n], x + 6, y +
                     * TEXT_SIZE_MONTH_NUMBER + (TEXT_SIZE_LUNAR_INFO+1)*n + 10,
                     * mLunarInfoPaint); } }else {
                     */
                    canvas.drawText(mDayInfos[i], x, y + TEXT_SIZE_MONTH_NUMBER
                            - TODAY_HIGHLIGHT_WIDTH, mLunarInfoPaint);

                    if (!mWorkDayWeekDay[i].isEmpty()) {

                        drawWorkWeekBackground(i, canvas, p);

                        // mLunarInfoPaint.setColor(mWorkWeekDayColor);
                        // canvas.drawText(mWorkDayWeekDay[i], p +
                        // EVENT_SQUARE_WIDTH,
                        // TEXT_SIZE_LUNAR_INFO, mLunarInfoPaint);
                        if (DEBUG_LAYOUT) {
                            Log.d(TAG, "!mWorkDayWeekDay[" + i
                                    + "].isEmpty() = " + i + ", p = " + p);
                        }
                    }
                } else {
                    // canvas.drawText(mDayInfos[i], x - TEXT_SIZE_MONTH_NUMBER
                    // - 6,
                    // y, mLunarInfoPaint);
                    mLunarInfoPaint.setTextAlign(Align.CENTER);
                    if (mDayInfos[i].contains("/")) {
                        mLunarInfoPaint.setTextSize(TEXT_SIZE_LUNAR_INFO - 6);
                    }
                    if (mShowWeekNum) {
                        canvas.drawText(mDayInfos[i],
                                computeDayLeftPosition(i - 1) + mWidth
                                        / (2 * mNumDays) - 2, mMonthNumHeight
                                        + TEXT_SIZE_MONTH_NUMBER,
                                mLunarInfoPaint);
                    } else {
                        canvas.drawText(mDayInfos[i], computeDayLeftPosition(i)
                                + mWidth / (2 * mNumDays) - 2, mMonthNumHeight
                                + TEXT_SIZE_MONTH_NUMBER, mLunarInfoPaint);
                    }
                    if (!mWorkDayWeekDay[i].isEmpty()) {
                        drawWorkWeekBackground(i, canvas, p);
                        // mLunarInfoPaint.setColor(mWorkWeekDayColor);
                        // canvas.drawText(mWorkDayWeekDay[i], p -
                        // TOP_PADDING_WEEK_NUMBER,
                        // TEXT_SIZE_LUNAR_INFO - 2, mLunarInfoPaint);
                        if (DEBUG_LAYOUT) {
                            Log.d(TAG, "!mWorkDayWeekDay[" + i
                                    + "].isEmpty() = " + i + ", p = " + p);
                        }
                    }
                }
            }
        }
    }

    protected void drawEvents(Canvas canvas) {
        if (mEvents == null) {
            return;
        }

        int day = -1;
        for (ArrayList<Event> eventDay : mEvents) {
            day++;
            if (eventDay == null || eventDay.size() == 0) {
                continue;
            }
            int ySquare;
            int xSquare = computeDayLeftPosition(day)
                    + SIDE_PADDING_MONTH_NUMBER + EVENT_LEFT_SPACING;
            int rightEdge = computeDayLeftPosition(day + 1);

            if (mOrientation == Configuration.ORIENTATION_PORTRAIT) {
                // ySquare = EVENT_Y_OFFSET_PORTRAIT + mMonthNumHeight +
                // TOP_PADDING_MONTH_NUMBER;
                ySquare = (EVENT_Y_OFFSET_PORTRAIT - TOP_PADDING_MONTH_NUMBER) / 2;
                rightEdge -= SIDE_PADDING_MONTH_NUMBER + 1;
            } else {
                ySquare = EVENT_Y_OFFSET_LANDSCAPE;
                rightEdge -= EVENT_X_OFFSET_LANDSCAPE;
            }
            // Determine if everything will fit when time ranges are shown.
            boolean showTimes = true;
            Iterator<Event> iter = eventDay.iterator();
            int yTest = ySquare;
            while (iter.hasNext()) {
                Event event = iter.next();
                int newY = drawEvent(canvas, event, xSquare, yTest, rightEdge,
                        iter.hasNext(), showTimes, /* doDraw */false);
                if (newY == yTest) {
                    showTimes = false;
                    break;
                }
                yTest = newY;
                // lixiange MF3.0 draw one event
                break;
            }

            int eventCount = 0;
            iter = eventDay.iterator();
            while (iter.hasNext()) {
                Event event = iter.next();
                int newY = drawEvent(canvas, event, xSquare, ySquare,
                        rightEdge, iter.hasNext(), showTimes, /* doDraw */true);
                if (newY == ySquare) {
                    break;
                }
                eventCount++;
                ySquare = newY;
                // lixiange MF3.0 draw one event
                break;
            }

            int remaining = eventDay.size() - eventCount;
            if (remaining > 0) {
                // drawMoreEvents(canvas, remaining, xSquare);
            }
        }
    }

    protected int addChipOutline(FloatRef lines, int count, int x, int y) {
        lines.ensureSize(count + 16);
        // top of box
        lines.array[count++] = x;
        lines.array[count++] = y;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y;
        // right side of box
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;
        // left side of box
        lines.array[count++] = x;
        lines.array[count++] = y;
        lines.array[count++] = x;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH + 1;
        // bottom of box
        lines.array[count++] = x;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;
        lines.array[count++] = x + EVENT_SQUARE_WIDTH + 1;
        lines.array[count++] = y + EVENT_SQUARE_WIDTH;

        return count;
    }

    /**
     * Attempts to draw the given event. Returns the y for the next event or the
     * original y if the event will not fit. An event is considered to not fit
     * if the event and its extras won't fit or if there are more events and the
     * more events line would not fit after drawing this event.
     * 
     * @param canvas
     *            the canvas to draw on
     * @param event
     *            the event to draw
     * @param x
     *            the top left corner for this event's color chip
     * @param y
     *            the top left corner for this event's color chip
     * @param rightEdge
     *            the rightmost point we're allowed to draw on (exclusive)
     * @param moreEvents
     *            indicates whether additional events will follow this one
     * @param showTimes
     *            if set, a second line with a time range will be displayed for
     *            non-all-day events
     * @param doDraw
     *            if set, do the actual drawing; otherwise this just computes
     *            the height and returns
     * @return the y for the next event or the original y if it won't fit
     */
    protected int drawEvent(Canvas canvas, Event event, int x, int y,
            int rightEdge, boolean moreEvents, boolean showTimes, boolean doDraw) {
        /*
         * Vertical layout: (top of box) a. EVENT_Y_OFFSET_LANDSCAPE or portrait
         * equivalent b. Event title: mEventHeight for a normal event, +
         * 2xBORDER_SPACE for all-day event c. [optional] Time range
         * (mExtrasHeight) d. EVENT_LINE_PADDING
         * 
         * Repeat (b,c,d) as needed and space allows. If we have more events
         * than fit, we need to leave room for something like "+2" at the
         * bottom:
         * 
         * e. "+ more" line (mExtrasHeight)
         * 
         * f. EVENT_BOTTOM_PADDING (overlaps EVENT_LINE_PADDING) (bottom of box)
         */
        final int BORDER_SPACE = EVENT_SQUARE_BORDER + 1; // want a 1-pixel gap
                                                          // inside border
        final int STROKE_WIDTH_ADJ = EVENT_SQUARE_BORDER / 2; // adjust bounds
                                                              // for stroke
                                                              // width
        boolean allDay = event.allDay;
        int eventRequiredSpace = mEventHeight;
        if (allDay) {
            // Add a few pixels for the box we draw around all-day events.
            eventRequiredSpace += BORDER_SPACE * 2;
        } else if (showTimes) {
            // Need room for the "1pm - 2pm" line.
            eventRequiredSpace += mExtrasHeight;
        }
        // lixiange MF3.0 draw one event
        /**
         * int reservedSpace = EVENT_BOTTOM_PADDING; // leave a bit of room at
         * the bottom if (moreEvents) { // More events follow. Leave a bit of
         * space between events. eventRequiredSpace += EVENT_LINE_PADDING;
         * 
         * // Make sure we have room for the "+ more" line. (The "+ more" line
         * is expected // to be <= the height of an event line, so we won't show
         * "+1" when we could be // showing the event.) reservedSpace +=
         * mExtrasHeight; }
         * 
         * if (y + eventRequiredSpace + reservedSpace > mHeight) { // Not enough
         * space, return original y return y; } else if (!doDraw) { return y +
         * eventRequiredSpace; }
         */
        // lixiange MF3.0 draw one event
        boolean isDeclined = event.selfAttendeeStatus == Attendees.ATTENDEE_STATUS_DECLINED;
        int color = event.color;
        if (isDeclined) {
            color = Utils.getDeclinedColorFromColor(color);
        }

        int textX, textY, textRightEdge;
        RectF r = new RectF();
        // lixiange MF3.0 draw one event
        /**
         * if (allDay) { // We shift the render offset "inward", because
         * drawRect with a stroke width greater // than 1 draws outside the
         * specified bounds. (We don't adjust the left edge, since // we want to
         * match the existing appearance of the "event square".) r.left = x;
         * r.right = rightEdge - STROKE_WIDTH_ADJ; r.top = y + STROKE_WIDTH_ADJ;
         * r.bottom = y + mEventHeight + BORDER_SPACE * 2 - STROKE_WIDTH_ADJ;
         * textX = x + BORDER_SPACE; textY = y + mEventAscentHeight +
         * BORDER_SPACE; textRightEdge = rightEdge - BORDER_SPACE; } else {
         */
        r.left = x;
        r.right = x + EVENT_ORAL_WIDTH;
        r.bottom = y + EVENT_ORAL_WIDTH * 12 + EVENT_ORAL_WIDTH / 2;// mEventAscentHeight
                                                                    // ;
        r.top = r.bottom - EVENT_ORAL_WIDTH;
        textX = x + EVENT_SQUARE_WIDTH + EVENT_RIGHT_PADDING;
        textY = y + mEventAscentHeight;
        textRightEdge = rightEdge;
        // }
        // lixiange MF3.0 draw one event

        Style boxStyle = Style.STROKE;
        boolean solidBackground = false;
        if (event.selfAttendeeStatus != Attendees.ATTENDEE_STATUS_INVITED) {
            boxStyle = Style.FILL_AND_STROKE;
            if (allDay) {
                solidBackground = true;
            }
        }
        mEventSquarePaint.setStyle(boxStyle);
        mEventSquarePaint.setColor(0xffc7c7c7);
        mEventSquarePaint.setAntiAlias(true);
        // canvas.drawRect(r, mEventSquarePaint);
        canvas.drawOval(r, mEventSquarePaint);

        float avail = textRightEdge - textX;
        CharSequence text = TextUtils.ellipsize(event.title, mEventPaint,
                avail, TextUtils.TruncateAt.END);
        Paint textPaint;
        if (solidBackground) {
            // Text color needs to contrast with solid background.
            textPaint = mSolidBackgroundEventPaint;
        } else if (isDeclined) {
            // Use "declined event" color.
            textPaint = mDeclinedEventPaint;
        } else if (allDay) {
            // Text inside frame is same color as frame.
            mFramedEventPaint.setColor(mDNAColor);
            textPaint = mFramedEventPaint;
        } else {
            // Use generic event text color.
            textPaint = mEventPaint;
        }
        // lixiange MF3.0 draw event Oval
        // canvas.drawText(text.toString(), textX, textY, textPaint);
        // lixiange MF3.0 draw event Oval
        y += mEventHeight;
        if (allDay) {
            y += BORDER_SPACE * 2;
        }

        if (showTimes && !allDay) {
            // show start/end time, e.g. "1pm - 2pm"
            /**
             * textY = y + mExtrasAscentHeight; mStringBuilder.setLength(0);
             * text = DateUtils.formatDateRange(getContext(), mFormatter,
             * event.startMillis, event.endMillis, DateUtils.FORMAT_SHOW_TIME |
             * DateUtils.FORMAT_ABBREV_ALL, Utils.getTimeZone(getContext(),
             * null)).toString(); text = TextUtils.ellipsize(text,
             * mEventExtrasPaint, avail, TextUtils.TruncateAt.END);
             * canvas.drawText(text.toString(), textX, textY, isDeclined ?
             * mEventDeclinedExtrasPaint : mEventExtrasPaint); y +=
             * mExtrasHeight;
             */
        }

        y += EVENT_LINE_PADDING;

        return y;
    }

    protected void drawMoreEvents(Canvas canvas, int remainingEvents, int x) {
        int y = mHeight - (mExtrasDescent + EVENT_BOTTOM_PADDING);
        String text = getContext().getResources().getQuantityString(
                R.plurals.month_more_events, remainingEvents);
        mEventExtrasPaint.setAntiAlias(true);
        mEventExtrasPaint.setFakeBoldText(true);
        canvas.drawText(String.format(text, remainingEvents), x, y,
                mEventExtrasPaint);
        mEventExtrasPaint.setFakeBoldText(false);
    }

    /**
     * Draws a line showing busy times in each day of week The method draws
     * non-conflicting times in the event color and times with conflicting
     * events in the dna conflict color defined in colors.
     * 
     * @param canvas
     */
    protected void drawDNA(Canvas canvas) {
        // Draw event and conflict times
        if (mDna != null) {
            for (Utils.DNAStrand strand : mDna.values()) {
                if (strand.color == CONFLICT_COLOR || strand.points == null
                        || strand.points.length == 0) {
                    continue;
                }
                // mDNATimePaint.setColor(strand.color);
                mDNATimePaint.setColor(mDNAColor);
                canvas.drawLines(strand.points, mDNATimePaint);
            }
            // Draw black last to make sure it's on top
            Utils.DNAStrand strand = mDna.get(CONFLICT_COLOR);
            if (strand != null && strand.points != null
                    && strand.points.length != 0) {
                // mDNATimePaint.setColor(strand.color);
                mDNATimePaint.setColor(mDNAColor);
                canvas.drawLines(strand.points, mDNATimePaint);
            }
            if (mDayXs == null) {
                return;
            }
            int numDays = mDayXs.length;
            int xOffset = (DNA_ALL_DAY_WIDTH - DNA_WIDTH) / 2;
            if (strand != null && strand.allDays != null
                    && strand.allDays.length == numDays) {
                for (int i = 0; i < numDays; i++) {
                    // this adds at most 7 draws. We could sort it by color and
                    // build an array instead but this is easier.
                    if (strand.allDays[i] != 0) {
                        // mDNAAllDayPaint.setColor(strand.allDays[i]);
                        mDNAAllDayPaint.setColor(mDNAColor);
                        canvas.drawLine(mDayXs[i] + xOffset, DNA_MARGIN,
                                mDayXs[i] + xOffset, DNA_MARGIN
                                        + DNA_ALL_DAY_HEIGHT, mDNAAllDayPaint);
                    }
                }
            }
        }
    }

    @Override
    protected void updateSelectionPositions() {
        if (mHasSelectedDay) {
            int selectedPosition = mSelectedDay - mWeekStart;
            if (selectedPosition < 0) {
                selectedPosition += 7;
            }
            int effectiveWidth = mWidth - mPadding * 2;
            effectiveWidth -= SPACING_WEEK_NUMBER;
            mSelectedLeft = selectedPosition * effectiveWidth / mNumDays
                    + mPadding;
            mSelectedRight = (selectedPosition + 1) * effectiveWidth / mNumDays
                    + mPadding;
            mSelectedLeft += SPACING_WEEK_NUMBER;
            mSelectedRight += SPACING_WEEK_NUMBER;
        }
    }

    public int getDayIndexFromLocation(float x) {
        int dayStart = mShowWeekNum ? SPACING_WEEK_NUMBER + mPadding : mPadding;
        if (x < dayStart || x > mWidth - mPadding) {
            return -1;
        }
        // Selection is (x - start) / (pixels/day) == (x -s) * day / pixels
        return ((int) ((x - dayStart) * mNumDays / (mWidth - dayStart - mPadding)));
    }

    @Override
    public Time getDayFromLocation(float x) {
        int dayPosition = getDayIndexFromLocation(x);
        if (dayPosition == -1) {
            return null;
        }
        int day = mFirstJulianDay + dayPosition;

        Time time = new Time(mTimeZone);
        if (mWeek == 0) {
            // This week is weird...
            if (day < Time.EPOCH_JULIAN_DAY) {
                day++;
            } else if (day == Time.EPOCH_JULIAN_DAY) {
                time.set(1, 0, 1970);
                time.normalize(true);
                return time;
            }
        }

        time.setJulianDay(day);
        return time;
    }

    @Override
    public boolean onHoverEvent(MotionEvent event) {
        Context context = getContext();
        // only send accessibility events if accessibility and exploration are
        // on.
        AccessibilityManager am = (AccessibilityManager) context
                .getSystemService(Service.ACCESSIBILITY_SERVICE);
        if (!am.isEnabled() || !am.isTouchExplorationEnabled()) {
            return super.onHoverEvent(event);
        }
        if (event.getAction() != MotionEvent.ACTION_HOVER_EXIT) {
            Time hover = getDayFromLocation(event.getX());
            if (hover != null
                    && (mLastHoverTime == null || Time.compare(hover,
                            mLastHoverTime) != 0)) {
                Long millis = hover.toMillis(true);
                String date = Utils.formatDateRange(context, millis, millis,
                        DateUtils.FORMAT_SHOW_DATE);
                AccessibilityEvent accessEvent = AccessibilityEvent
                        .obtain(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED);
                accessEvent.getText().add(date);
                if (mShowDetailsInMonth && mEvents != null) {
                    int dayStart = SPACING_WEEK_NUMBER + mPadding;
                    int dayPosition = (int) ((event.getX() - dayStart)
                            * mNumDays / (mWidth - dayStart - mPadding));
                    ArrayList<Event> events = mEvents.get(dayPosition);
                    List<CharSequence> text = accessEvent.getText();
                    for (Event e : events) {
                        text.add(e.getTitleAndLocation() + ". ");
                        int flags = DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_SHOW_YEAR;
                        if (!e.allDay) {
                            flags |= DateUtils.FORMAT_SHOW_TIME;
                            if (DateFormat.is24HourFormat(context)) {
                                flags |= DateUtils.FORMAT_24HOUR;
                            }
                        } else {
                            flags |= DateUtils.FORMAT_UTC;
                        }
                        text.add(Utils.formatDateRange(context, e.startMillis,
                                e.endMillis, flags) + ". ");
                    }
                }
                sendAccessibilityEventUnchecked(accessEvent);
                mLastHoverTime = hover;
            }
        }
        return true;
    }

    public void setClickedDay(float xLocation) {
        mClickedDayIndex = getDayIndexFromLocation(xLocation);
        invalidate();
    }

    public void clearClickedDay() {
        mClickedDayIndex = -1;
        mTodayIndexDisplay = -1;
        invalidate();
    }
}
