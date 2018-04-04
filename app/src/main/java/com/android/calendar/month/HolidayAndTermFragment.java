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

import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.android.calendar.AllInOneActivity;
import com.android.calendar.AsyncQueryService;
import com.android.calendar.R;

public class HolidayAndTermFragment extends Fragment implements
        AdapterView.OnItemClickListener, OnItemSelectedListener {
    private static final String TAG = "HolidayFragment";
    private static final boolean DEBUG = false;
    /**
     * Added By zhangrui 2012.11.12 action for Intent from
     * HollidayAndTermFragment begin
     */
    public static final String ACTION_HOLIDAYORTERM = "com.android.calendar.ACTION_HOLIDAYORTERM";
    /**
     * Added By zhangrui 2012.11.12 action for Intent from
     * HollidayAndTermFragment end
     */

    private static final String[] PROJECTION = new String[] { Events._ID,
            Events.TITLE, Events.DTSTART, Events.DTEND, };

    public static String HOLIDAY_AND_TERM_VIEW = null;

    private static int mQueryToken;
    private static int mListItemLayout = R.layout.holiday_and_term_item;

    private int mViewType = 0;
    private long mInitMillis = -1;
    private float mMinimumFlingVelocity;
    private Time mTime = new Time();
    private ArrayList<Integer> mSpecailDays = new ArrayList();
    private CalendarConvertTools mConvertTool;

    private View mView = null;
    private ListView mList;
    // private GridView mGridView;
    private View mItemView;
    // private HorizontalScrollView mHorizonView;

    private HolidayAndTermAdapter mAdapter;
    private Activity mContext;
    private AsyncQueryService mService;
    private Cursor mCursor;
    // private GestureDetector mGestureDetector;

    private int width;
    private float mScale;

    /*
     * class MyGestureListener extends SimpleOnGestureListener {
     * 
     * @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float
     * velocityX, float velocityY) { // TODO decide how to handle flings float
     * absX = Math.abs(velocityX); float absY = Math.abs(velocityY); Log.d(TAG,
     * "velX: " + velocityX + " velY: " + velocityY); if (absX > absY && absX >
     * mMinimumFlingVelocity) { if(velocityX > 0) { mTime.year--; } else {
     * mTime.year++; } mTime.month = 0; mTime.monthDay = 1;
     * mTime.normalize(true); initSpecialDays(); } return false; } }
     */

    public HolidayAndTermFragment() {
    }

    public HolidayAndTermFragment(int viewType, long millis) {
        mViewType = viewType;
        HOLIDAY_AND_TERM_VIEW = Integer.toString(mViewType);
        mInitMillis = millis;
        if (mInitMillis == -1) {
            mTime.setToNow();
        } else {
            mTime.set(mInitMillis);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mContext = activity;
        mConvertTool = new CalendarConvertTools(activity);
        // mGestureDetector = new GestureDetector(activity, new
        // MyGestureListener());
        ViewConfiguration viewConfig = ViewConfiguration.get(activity);
        mMinimumFlingVelocity = viewConfig.getScaledMinimumFlingVelocity() * 4;
        mService = new AsyncQueryService(activity) {
            @Override
            protected void onQueryComplete(int token, Object cookie,
                    Cursor cursor) {
                Log.i(TAG, "changeCursor onAttach");
                mAdapter.changeCursor(cursor);
                mCursor = cursor;
                initSpecialDays();
            }
        };
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 5;
        Log.i(TAG, "onAttach width = " + width);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mCursor != null) {
            Log.i(TAG, "changeCursor onDetach");
            mAdapter.changeCursor(null);
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        /*
         * Date date = new Date(); Log.i("SMSReceiver", "time.year = "+
         * date.getYear());
         * 
         * selected = date.getYear() - 50;
         */

        mView = inflater.inflate(R.layout.holiday_and_term_fragment, null);
        mList = (ListView) mView.findViewById(R.id.list);

        // mHorizonView =
        // (HorizontalScrollView)mView.findViewById(R.id.horizontalScrollView);
        // mGridView = (GridView)mView.findViewById(R.id.gridview);
        /**
         * Log.i("SMSReceiver", "onCreateView width = " + width); LayoutParams
         * lp = mGridView.getLayoutParams(); Log.i("SMSReceiver", "lp.width =  "
         * + lp.width); Log.i("SMSReceiver", "lp.height =  " + lp.height);
         * lp.width = width * 58; mGridView.setLayoutParams(lp);
         * 
         * mItemView = inflater.inflate(R.layout.holiday_year_item, null);
         */

        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new HolidayAndTermAdapter(mContext, mListItemLayout, null);
        try {
            mList.setAdapter(mAdapter);
        } catch (Exception e) {
            e.getMessage();
        }
        mList.setOnItemClickListener(this);
        // mList.setOnTouchListener(this);

        /**
         * try{ mGridView.setAdapter(saImageItems); }catch(Exception e) {
         * e.getMessage(); } mGridView.setOnItemClickListener(new
         * OnItemClickListener() { public void onItemClick(AdapterView<?>
         * parent, View v, int position, long id) { selected = position;
         * Log.i("SMSReceiver", "onItemClick position = "+ position); try{
         * mGridView.setAdapter(saImageItems); }catch(Exception e) {
         * e.getMessage(); } mTime.year = 1970+selected; mTime.month = 0;
         * mTime.monthDay = 1; mTime.normalize(true); initSpecialDays();
         * 
         * } });
         * 
         * Log.i("SMSReceiver", "onActivityCreated width = " + width);
         * 
         * mHorizonView.post(new Runnable() {
         * 
         * @Override public void run() {
         *           mHorizonView.scrollTo((selected-2)*width, 0); } });
         */
        ((HolidayAndTermActivity) getActivity())
                .registerSpinnerSelectListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        if (mAdapter == null || mAdapter.getCount() <= position) {
            return;
        } else { // Here added by zhangrui 2012.11.12 Intent to AllInOneActivity
            int julianDapter = mAdapter.getJulianDay(position);
            Time startTime = new Time();
            startTime.setJulianDay(julianDapter);
            long startMillis = startTime.toMillis(true) + 8
                    * DateUtils.HOUR_IN_MILLIS;
            long endMillis = startMillis + DateUtils.HOUR_IN_MILLIS;
            final Intent intent = new Intent();
            intent.setClass(getActivity(), AllInOneActivity.class);
            // intent.setAction(ACTION_HOLIDAYORTERM);
            intent.setAction(Intent.ACTION_VIEW);
            intent.putExtra(EXTRA_EVENT_BEGIN_TIME, startMillis);
            intent.putExtra(EXTRA_EVENT_END_TIME, endMillis);
            getActivity().startActivity(intent);
        }
    }

    /*
     * @Override public boolean onTouch(View v, MotionEvent event) { return
     * mGestureDetector.onTouchEvent(event); }
     */

    @Override
    public void onResume() {
        super.onResume();

        String selection = buidSelection();
        if (DEBUG) {
            Log.i(TAG, "onResume selection = " + selection);
        }
        mQueryToken = mService.getNextToken();
        mService.startQuery(mQueryToken, null, Events.CONTENT_URI, PROJECTION,
                selection, null, Events.DTSTART);

    }

    public void initSpecialDays() {
        Time time = new Time();
        time.set(0, 0, 0, 1, 0, mTime.year);
        time.normalize(true);
        int theJulianDay = time.getJulianDay(time.toMillis(true), time.gmtoff);
        mSpecailDays.clear();
        Time solarDate = new Time();
        int lastJulianDay = theJulianDay + 365;
        while (theJulianDay <= lastJulianDay) {
            solarDate.setJulianDay(theJulianDay);
            if (solarDate.year != mTime.year)
                break;
            // 20160329 add qingmingjie to Holiday View by zhxj
            if (mViewType == 0 /* holiday */
                    && (isFestival(solarDate) || isHoliday(solarDate) || (isTerm(solarDate)
                            && solarDate.month == Calendar.APRIL && (solarDate.monthDay == 4
                            || solarDate.monthDay == 5 || solarDate.monthDay == 6)))) {
                mSpecailDays.add(theJulianDay);
            } else if (mViewType == 1 /* term */&& isTerm(solarDate)) {
                mSpecailDays.add(theJulianDay);
            }

            theJulianDay++;
        }

        mAdapter.setSpecialDayRows(mSpecailDays);
        int julianDay = mTime.getJulianDay(mTime.toMillis(true), mTime.gmtoff);
        int gotoPosition = mAdapter.findPositionAfter(julianDay);
        mList.setSelection(gotoPosition);
    }

    public boolean isHoliday(Time time) {
        String holiday = mConvertTool.getChnHolFromSolor(time);
        if (holiday.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isFestival(Time time) {
        String festival = mConvertTool.getChineseFestivalFromSolar(time);
        if (festival.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isTerm(Time time) {
        String term = mConvertTool.getTermStringFromSolarDate(time);
        if (term.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public String buidSelection() {
        String[] festivals = mConvertTool.getFestivals();
        String[] holidays = mConvertTool.getHolidays();
        String[] terms = mConvertTool.getTerms();

        String selection = Events.TITLE + " in (";
        if (mViewType == 0 && holidays != null && festivals != null) {
            for (int i = 0; i < holidays.length; i++) {
                selection += "'" + holidays[i] + "',";
            }
            for (int i = 0; i < festivals.length; i++) {
                if (i == festivals.length - 1) {
                    selection += "'" + festivals[i] + "')";
                } else {
                    selection += "'" + festivals[i] + "',";
                }
            }
        } else if (mViewType == 1 && terms != null) {
            for (int i = 0; i < terms.length; i++) {
                if (i == terms.length - 1) {
                    selection += "'" + terms[i] + "')";
                } else {
                    selection += "'" + terms[i] + "',";
                }
            }
        }

        return selection;
    }

    public int getViewType() {
        return mViewType;
    }

    public long getFirstVisibleTime() {
        if (mList == null) {
            return -1;
        }
        int position = mList.getFirstVisiblePosition();
        int julianDay = mAdapter.getJulianDay(position);
        Time firstVisibleTime = new Time();
        firstVisibleTime.setJulianDay(julianDay);
        return firstVisibleTime.toMillis(true);
    }

    private Time nowTime = new Time();

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
            long id) {
        try {
            // mContext.getActionBar().setListNavigationCallbacks(saImageItems,
            // this);
        } catch (Exception e) {
            e.getMessage();
        }
        mTime.year = 1970 + position;
        nowTime.setToNow();
        Log.d(TAG, "lxg nowTime.year = " + nowTime.year + "mTime.year, = "
                + mTime.year);
        if (nowTime.year != mTime.year) {
            Log.d(TAG, "lxg if(nowTime.year != mTime.year){");
            mTime.month = 0;
            mTime.monthDay = 1;
        } else {
            mTime.month = nowTime.month;
            mTime.monthDay = nowTime.monthDay;
        }
        mTime.normalize(true);
        initSpecialDays();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }
}
