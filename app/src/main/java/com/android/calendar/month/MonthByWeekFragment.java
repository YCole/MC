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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
//HCT_MODIFY lixiange MF3.0 add liblight day and display day event when click day 
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.ColorChipView;
import com.android.calendar.Event;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.event.CreateEventDialogFragment;

//HCT_MODIFY lixiange MF3.0 add liblight day and display day event when click day 

public class MonthByWeekFragment extends SimpleDayPickerFragment implements
        CalendarController.EventHandler, LoaderManager.LoaderCallbacks<Cursor>,
        OnScrollListener, OnTouchListener {
    private static final String TAG = "MonthFragment";
    private static final String TAG_EVENT_DIALOG = "event_dialog";

    private CreateEventDialogFragment mEventDialog;

    // Selection and selection args for adding event queries
    private static final String WHERE_CALENDARS_VISIBLE = Calendars.VISIBLE
            + "=1";
    private static final String INSTANCES_SORT_ORDER = Instances.START_DAY
            + "," + Instances.START_MINUTE + "," + Instances.TITLE;
    protected static boolean mShowDetailsInMonth = false;

    protected float mMinimumTwoMonthFlingVelocity;
    protected boolean mIsMiniMonth;
    protected boolean mHideDeclined;

    protected int mFirstLoadedJulianDay;
    protected int mLastLoadedJulianDay;

    private static final int WEEKS_BUFFER = 1;
    // How long to wait after scroll stops before starting the loader
    // Using scroll duration because scroll state changes don't update
    // correctly when a scroll is triggered programmatically.
    private static final int LOADER_DELAY = 200;
    // The minimum time between requeries of the data if the db is
    // changing
    private static final int LOADER_THROTTLE_DELAY = 500;

    private CursorLoader mLoader;
    private Uri mEventUri;
    private final Time mDesiredDay = new Time();

    private volatile boolean mShouldLoad = true;
    private boolean mUserScrolled = false;

    private int mEventsLoadingDelay;
    private boolean mShowCalendarControls;
    private boolean mIsDetached;

    // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
    // click day
    private LinearLayout eventLayout;
    private ArrayList<HashMap<String, Object>> agendaArrayList;
    private ListView listView;
    private View separatorLine;
    private HashMap<String, Object> mHashMap;
    private int eventIndex = 0;
    private LinearLayout noEventLayout;
    private ColorChipView mColorChipView;
    private ListViewAdapter mListViewAdapter;
    private Long mTouchDay;
    private Cursor cEvents;
    // private ListAdapter adapter;
    /**
     * private TextView scheduleTime; private TextView scheduleTitle; private
     * TextView scheduleTime2; private TextView scheduleTitle2;
     */
    private TextView widgetLoad;
    // private ImageView triangle;
    protected QueryHandler mQueryHandler;
    protected static final int QUERY_TOKEN = 0;
    private static final String[] CALENDAR_PROJECTION = new String[] {
            "dtstart", "dtend", "title", "description", "rrule", "allDay",
            "eventColor", "_id", "location" };
    private static final int DTSTART_COLOUM = 0;
    private static final int DTEND_COLOUM = 1;
    private static final int TITLE_COLOUM = 2;
    private static final int DESCRIPTION_COLOUM = 3;
    private static final int RRULE_COLOUM = 4;
    private static final int ALLDAY_COLOUM = 5;
    private static final int EVENT_COLOR = 6;
    private static final int EVENT_ID = 7;

    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_LOCATION_INDEX = 1;
    private static final int PROJECTION_ALL_DAY_INDEX = 2;
    private static final int PROJECTION_COLOR_INDEX = 3;
    private static final int PROJECTION_TIMEZONE_INDEX = 4;
    private static final int PROJECTION_EVENT_ID_INDEX = 5;
    private static final int PROJECTION_BEGIN_INDEX = 6;
    private static final int PROJECTION_END_INDEX = 7;
    private static final int PROJECTION_START_DAY_INDEX = 9;
    private static final int PROJECTION_END_DAY_INDEX = 10;
    private static final int PROJECTION_START_MINUTE_INDEX = 11;
    private static final int PROJECTION_END_MINUTE_INDEX = 12;
    private static final int PROJECTION_HAS_ALARM_INDEX = 13;
    private static final int PROJECTION_RRULE_INDEX = 14;
    private static final int PROJECTION_RDATE_INDEX = 15;
    private static final int PROJECTION_SELF_ATTENDEE_STATUS_INDEX = 16;
    private static final int PROJECTION_ORGANIZER_INDEX = 17;
    private static final int PROJECTION_GUESTS_CAN_INVITE_OTHERS_INDEX = 18;
    private static final int PROJECTION_DISPLAY_AS_ALLDAY = 19;
    // private ArrayList<String> tScheduleTitle = new ArrayList<String>();
    // private ArrayList<Long> tScheduleTime = new ArrayList<Long>();
    // private ArrayList<String> startTimeSort = new ArrayList<String>();
    private boolean mRegisterReceiver = false;
    private boolean mRegisterObserver = false;
    private boolean mRegisterThemeReceiver = false;
    private GestureDetector mGestureDetector;
    // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
    // click day rjc longtime press new dialog

    private Handler mEventDialogHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                final FragmentManager manager = getFragmentManager();
                if (manager != null) {
                    Time day = (Time) msg.obj;
                    mEventDialog = new CreateEventDialogFragment(day, this);
                    mEventDialog.show(manager, TAG_EVENT_DIALOG);
                }
            } else if (msg.what == 2) {
                doResumeUpdates();
            }

        }
    };

    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            String tz = Utils.getTimeZone(mContext, mTZUpdater);
            mSelectedDay.timezone = tz;
            mSelectedDay.normalize(true);
            mTempTime.timezone = tz;
            mFirstDayOfMonth.timezone = tz;
            mFirstDayOfMonth.normalize(true);
            mFirstVisibleDay.timezone = tz;
            mFirstVisibleDay.normalize(true);
            if (mAdapter != null) {
                mAdapter.refresh();
            }
        }
    };

    private final Runnable mUpdateLoader = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                if (!mShouldLoad || mLoader == null) {
                    return;
                }
                // Stop any previous loads while we update the uri
                stopLoader();

                // Start the loader again
                mEventUri = updateUri();

                mLoader.setUri(mEventUri);
                mLoader.startLoading();
                mLoader.onContentChanged();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Started loader with uri: " + mEventUri);
                }
            }
        }
    };
    // Used to load the events when a delay is needed
    Runnable mLoadingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!mIsDetached) {
                mLoader = (CursorLoader) getLoaderManager().initLoader(0, null,
                        MonthByWeekFragment.this);
            }
        }
    };

    // HCT_MODIFY lixiange MF3.0 display list agenda
    static class ViewHolder {

        public static final int DECLINED_RESPONSE = 0;
        public static final int TENTATIVE_RESPONSE = 1;
        public static final int ACCEPTED_RESPONSE = 2;

        /* Event */
        TextView title;
        TextView when;
        TextView where;
        View selectedMarker;
        LinearLayout textContainer;
        long instanceId;
        ColorChipView colorChip;
        long startTimeMilli;
        boolean allDay;
        boolean grayed;
        int julianDay;
        boolean selected;
    }

    // HCT_MODIFY lixiange MF3.0 display list agenda

    /**
     * Updates the uri used by the loader according to the current position of
     * the listview.
     * 
     * @return The new Uri to use
     */
    private Uri updateUri() {
        SimpleWeekView child = (SimpleWeekView) mListView.getChildAt(0);
        if (child != null) {
            int julianDay = child.getFirstJulianDay();
            mFirstLoadedJulianDay = julianDay;
        }
        // -1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mFirstLoadedJulianDay - 1);
        long start = mTempTime.toMillis(true);
        mLastLoadedJulianDay = mFirstLoadedJulianDay
                + (mNumWeeks + 2 * WEEKS_BUFFER) * 7;
        // +1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mLastLoadedJulianDay + 1);
        long end = mTempTime.toMillis(true);

        // Create a new uri with the updated times
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start);
        ContentUris.appendId(builder, end);
        return builder.build();
    }

    // Extract range of julian days from URI
    private void updateLoadedDays() {
        List<String> pathSegments = mEventUri.getPathSegments();
        int size = pathSegments.size();
        if (size <= 2) {
            return;
        }
        long first = Long.parseLong(pathSegments.get(size - 2));
        long last = Long.parseLong(pathSegments.get(size - 1));
        mTempTime.set(first);
        mFirstLoadedJulianDay = Time.getJulianDay(first, mTempTime.gmtoff);
        mTempTime.set(last);
        mLastLoadedJulianDay = Time.getJulianDay(last, mTempTime.gmtoff);
    }

    protected String updateWhere() {
        // TODO fix selection/selection args after b/3206641 is fixed
        String where = WHERE_CALENDARS_VISIBLE;
        if (mHideDeclined || !mShowDetailsInMonth) {
            where += " AND " + Instances.SELF_ATTENDEE_STATUS + "!="
                    + Attendees.ATTENDEE_STATUS_DECLINED;
        }
        return where;
    }

    private void stopLoader() {
        synchronized (mUpdateLoader) {
            mHandler.removeCallbacks(mUpdateLoader);
            if (mLoader != null) {
                mLoader.stopLoading();
                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Log.d(TAG, "Stopped loader from loading");
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTZUpdater.run();
        if (mAdapter != null) {
            mAdapter.setSelectedDay(mSelectedDay);
        }
        mIsDetached = false;

        mGestureDetector = new GestureDetector(activity,
                new MonthGestureListener());
        ViewConfiguration viewConfig = ViewConfiguration.get(activity);
        mMinimumTwoMonthFlingVelocity = viewConfig
                .getScaledMaximumFlingVelocity() / 2;
        Resources res = activity.getResources();
        mShowCalendarControls = Utils.getConfigBool(activity,
                R.bool.show_calendar_controls);
        // Synchronized the loading time of the month's events with the
        // animation of the
        // calendar controls.
        if (mShowCalendarControls) {
            mEventsLoadingDelay = res
                    .getInteger(R.integer.calendar_controls_animation_time);
        }
        mShowDetailsInMonth = res.getBoolean(R.bool.show_details_in_month);
    }

    @Override
    public void onDetach() {
        mIsDetached = true;
        super.onDetach();
        if (mShowCalendarControls) {
            if (mListView != null) {
                mListView.removeCallbacks(mLoadingRunnable);
            }
        }
    }

    @Override
    protected void setUpAdapter() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);

        HashMap<String, Integer> weekParams = new HashMap<String, Integer>();
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_NUM_WEEKS, mNumWeeks);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_SHOW_WEEK,
                mShowWeekNumber ? 1 : 0);
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_WEEK_START,
                mFirstDayOfWeek);
        weekParams.put(MonthByWeekAdapter.WEEK_PARAMS_IS_MINI, mIsMiniMonth ? 1
                : 0);
        weekParams
                .put(SimpleWeeksAdapter.WEEK_PARAMS_JULIAN_DAY, Time
                        .getJulianDay(mSelectedDay.toMillis(true),
                                mSelectedDay.gmtoff));
        weekParams.put(SimpleWeeksAdapter.WEEK_PARAMS_DAYS_PER_WEEK,
                mDaysPerWeek);
        if (mAdapter == null) {
            mAdapter = new MonthByWeekAdapter(getActivity(), weekParams,
                    mEventDialogHandler);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (1 == 1)
            throw new RuntimeException(
                    "com.android.calendar.month.MonthByWeekFragment.onCreateView");
        //
        View v;
        /**
         * if (mIsMiniMonth) { v = inflater.inflate(R.layout.month_by_week,
         * container, false); } else { v =
         * inflater.inflate(R.layout.full_month_by_week, container, false); }
         */
        v = inflater.inflate(R.layout.full_month_by_week, container, false);
        mDayNamesHeader = (ViewGroup) v.findViewById(R.id.day_names);
        // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
        // click day
        eventLayout = (LinearLayout) v.findViewById(R.id.agenda_list);
        listView = (ListView) v.findViewById(R.id.listview);
        separatorLine = (View) v.findViewById(R.id.separator_line);
        agendaArrayList = new ArrayList<HashMap<String, Object>>();
        noEventLayout = (LinearLayout) v.findViewById(R.id.noEventLayout);

        // arrayAdapter = new
        // ArrayAdapter<ViewHolder>(mContext,R.layout.agenda_item,agendaListView);
        /**
         * scheduleTime =
         * (TextView)v.findViewById(R.id.typeb_calendar_schedule_time);
         * scheduleTitle =
         * (TextView)v.findViewById(R.id.typeb_calendar_schedule_title);
         * scheduleTime2 =
         * (TextView)v.findViewById(R.id.typeb_calendar_schedule_time2);
         * scheduleTitle2 =
         * (TextView)v.findViewById(R.id.typeb_calendar_schedule_title2);
         * triangle = (ImageView)v.findViewById(R.id.typeb_calendar_triangle2);
         * 
         * if(eventLayout != null){ eventLayout.setOnTouchListener(new
         * OnTouchListener() {
         * 
         * @Override public boolean onTouch(View v, MotionEvent event) { try {
         *           Log.d(TAG,"lxg eventLayout touchListener");
         *           CalendarController controller =
         *           CalendarController.getInstance(mContext);
         *           controller.sendEvent(this, EventType.GO_TO, null, null, -1,
         *           ViewType.AGENDA); return v.onTouchEvent(event); } catch
         *           (ActivityNotFoundException e) { // ignore return true; } }
         *           }); }
         */

        if (mQueryHandler == null) {
            mQueryHandler = new QueryHandler(mContext.getContentResolver());
        }
        // mContext.getContentResolver().registerContentObserver(resolveUri(),
        // true, mCalendarObserver);
        mContext.getContentResolver().registerContentObserver(
                Settings.System.getUriFor("calendar_touch_day"), false,
                mCalendarTouchObserver);
        // mHandler.postDelayed(mRefreshTask, 500);
        // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
        // click day
        return v;
    }

    // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
    // click day
    private ContentObserver mCalendarObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            setScheduleLayout();
        }
    };

    private ContentObserver mCalendarTouchObserver = new ContentObserver(
            new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            ContentResolver resolver = mContext.getContentResolver();
            mTouchDay = Settings.System.getLong(resolver, "calendar_touch_day",
                    0);
            setTouchDayScheduleLayout(mTouchDay);
        }
    };

    private Runnable mRefreshTask = new Runnable() {
        public void run() {
            setScheduleLayout();
        }
    };

    // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
    // click day

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setSelector(new StateListDrawable());
        mListView.setOnTouchListener(this);
        if (!mIsMiniMonth) {
            mListView.setBackgroundColor(Color.TRANSPARENT);
        }

        // To get a smoother transition when showing this fragment, delay
        // loading of events until
        // the fragment is expended fully and the calendar controls are gone.
        mTouchDay = System.currentTimeMillis();
        if (Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            if (mShowCalendarControls) {
                mListView.postDelayed(mLoadingRunnable, mEventsLoadingDelay);
            } else {
                mLoader = (CursorLoader) getLoaderManager().initLoader(0, null,
                        this);
            }
        }
        mAdapter.setListView(mListView);

        mListViewAdapter = new ListViewAdapter(mContext, agendaArrayList,
                R.layout.agenda_event_item, new String[] { "title", "when",
                        "where" }, new int[] { R.id.title, R.id.when,
                        R.id.where });
        listView.setAdapter(mListViewAdapter);
        listView.setOnItemClickListener(new ItemClickListenerImpl());
    }

    public MonthByWeekFragment() {
        this(System.currentTimeMillis(), true);
    }

    public MonthByWeekFragment(long initialTime, boolean isMiniMonth) {
        super(initialTime);
        mIsMiniMonth = isMiniMonth;
    }

    // -----------------------------------lixiange MF3.0 HCT_MODIFY add liblight
    // day and display day event when click day-------------------------------
    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * This method refactory by suzaiqiang, replaced all method substring
         * with SimpleDateFormat class.
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            synchronized (QueryHandler.this) {
                agendaArrayList.clear();
                mListViewAdapter.notifyDataSetChanged();
                if (cursor != null && cursor.getCount() != 0) {
                    // startTimeSort.clear();
                    // tScheduleTitle.clear();
                    // tScheduleTime.clear();

                    while (cursor.moveToNext()) {
                        dealRule(cursor);
                        updateScheduleView();
                    }
                } else {

                    noScheduleView();
                }
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

        }

        @Override
        protected void onUpdateComplete(int token, Object cookie, int result) {
        }

        @Override
        protected void onDeleteComplete(int token, Object cookie, int result) {
        }
    }

    protected Uri resolveUri() {
        return Events.CONTENT_URI;
    }

    private void query(String selection) {
        Uri uri = resolveUri();
        mQueryHandler.cancelOperation(QUERY_TOKEN);
        Log.d(TAG, "lxg QUERY_TOKEN=" + QUERY_TOKEN + ",uri=" + uri
                + ",selection=" + selection);
        mQueryHandler.startQuery(QUERY_TOKEN, null, uri, CALENDAR_PROJECTION,
                selection, null, "dtstart" + " ASC");
    }

    private void setScheduleLayout() {
        long time = System.currentTimeMillis();
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        long startTime = ca.getTimeInMillis();
        long endTime = startTime + 24 * 60 * 60 * 1000;
        Log.d(TAG, "lxg setScheduleLayout startTime =" + startTime
                + ",endTime=" + endTime + ",data=" + getDate(startTime));
        // query(getSelectionByEnd(endTime));
    }

    public void setTouchDayScheduleLayout(long startTime) {
        long endTime = startTime + 24 * 60 * 60 * 1000;
        Log.d(TAG, "lxg setTouchDayScheduleLayout startTime =" + startTime
                + ",endTime=" + endTime);
        // query(getSelection(startTime, endTime));
        // query(getSelectionByEnd(endTime));
        handleDayEvent(cEvents);
    }

    private String getSelectionByEnd(long end) {
        return "dtstart<" + end;
    }

    private String getSelection(long start, long end) {
        return "dtstart>" + start + " AND " + "dtstart<" + end;
    }

    private void noScheduleView() {
        if (listView != null && noEventLayout != null) {
            listView.setVisibility(View.GONE);
            separatorLine.setVisibility(View.GONE);
            noEventLayout.setVisibility(View.VISIBLE);
        }
    }

    private void updateScheduleView() {
        if (agendaArrayList.isEmpty()) {
            noScheduleView();
            mListViewAdapter.notifyDataSetChanged();
            return;
        }
        if (listView != null && noEventLayout != null) {
            noEventLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            separatorLine.setVisibility(View.VISIBLE);
            mListViewAdapter.notifyDataSetChanged();
        }
    }

    private class ItemClickListenerImpl implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            long agendaStartTime = (Long) agendaArrayList.get(position).get(
                    "startTime");
            long agendaEndTime = (Long) agendaArrayList.get(position).get(
                    "endTime");
            int eventId = (Integer) agendaArrayList.get(position)
                    .get("eventId");
            String allDay = (String) agendaArrayList.get(position)
                    .get("allDay");
            long holderStartTime = (Long) agendaArrayList.get(position).get(
                    "startTime");
            if (!"-1".equals(allDay)) {
                Boolean allDayBoolean = Boolean.getBoolean(allDay);
                CalendarController controller = CalendarController
                        .getInstance(mContext);
                controller.sendEventRelatedEventWithExtra(this,
                        EventType.VIEW_EVENT, eventId, agendaStartTime,
                        agendaEndTime, 0, 0, CalendarController.EventInfo
                                .buildViewExtraLong(
                                        Attendees.ATTENDEE_STATUS_NONE,
                                        allDayBoolean), holderStartTime);
            }
        }
    }

    public class ListViewAdapter extends BaseAdapter {
        private List<? extends Map<String, ?>> mArrayList;
        private int resource;
        private LayoutInflater mLayoutInflater;

        public ListViewAdapter(Context context,
                List<? extends Map<String, ?>> data, int resource,
                String[] from, int[] to) {
            this.mArrayList = data;
            this.resource = resource;
            mLayoutInflater = (LayoutInflater) context
                    .getSystemService(context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = mLayoutInflater.inflate(R.layout.agenda_event_item,
                    null);
            convertView = itemView;
            TextView titleTv = (TextView) convertView.findViewById(R.id.title);
            TextView whenTv = (TextView) convertView.findViewById(R.id.when);
            TextView whereTv = (TextView) convertView.findViewById(R.id.where);
            mColorChipView = (ColorChipView) convertView
                    .findViewById(R.id.agenda_item_color);
            String title = (String) mArrayList.get(position).get("title");
            String location = (String) mArrayList.get(position).get("location");
            long startTime = (Long) mArrayList.get(position).get("startTime");
            long endTime = (Long) mArrayList.get(position).get("endTime");
            int eventColor = Utils
                    .getDisplayColorFromColor((Integer) mArrayList
                            .get(position).get("eventColor"));
            String allDay = (String) agendaArrayList.get(position)
                    .get("allDay");
            Boolean allDayBoolean = Boolean.getBoolean(allDay);
            titleTv.setText(title);
            whereTv.setText(location);
            if (allDay.equals("1")) {
                whenTv.setText((R.string.edit_event_all_day_label));
            } else {
                whenTv.setText(getTimeString(startTime) + "-"
                        + getTimeString(endTime));
            }
            mColorChipView.setColor(eventColor);
            return convertView;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            if (mArrayList == null) {
                return null;
            } else {
                if (position > 0) {
                    return mArrayList.get(position - 1);
                } else {
                    return mArrayList.get(position + 1);
                }
            }
        }

        @Override
        public int getCount() {
            if (mArrayList == null) {
                return 0;
            } else {
                return (mArrayList.size());
            }
        }
    }

    private int getDate(long time) {
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.setTimeInMillis(time);
        int date = ca.get(Calendar.DATE);
        return date;
    }

    private int getStartPosition(long time) {
        final Calendar ca = Calendar.getInstance();
        ca.setTimeInMillis(time);
        ca.set(Calendar.DAY_OF_MONTH, 1);
        int date = ca.get(Calendar.DATE);
        int startPosition = ca.get(Calendar.DAY_OF_WEEK) - 1;
        return startPosition;
    }

    private String getCompareTimeString(long time) {
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        return dateFormat24.format(date);
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

    public void dealRule(Cursor cursor) {
        int startDay = cursor.getInt(PROJECTION_START_DAY_INDEX);
        int endDay = cursor.getInt(PROJECTION_END_DAY_INDEX);
        if (mTouchDay == null) {
            mTouchDay = System.currentTimeMillis();
        }
        Time touchTime = new Time();
        touchTime.set(mTouchDay);
        int julianToday = Time.getJulianDay(touchTime.toMillis(false),
                touchTime.gmtoff);
        if (startDay <= julianToday && julianToday <= endDay) {
            todaySchedule(cursor);
        }
    }

    // allDay schedule
    public void allDaySchedule(Cursor cursor, long startTime) {
        String time = getCompareTimeString(startTime);
        // startTimeSort.add(0,time);
        // tScheduleTitle.add(0,cursor.getString(TITLE_COLOUM));
        // tScheduleTime.add(0,startTime);
    }

    public void todaySchedule(Cursor cursor) {
        String agendaTitle = cursor.getString(PROJECTION_TITLE_INDEX);
        String agendaLocation = cursor.getString(PROJECTION_LOCATION_INDEX);
        long agendaStartTime = cursor.getLong(PROJECTION_BEGIN_INDEX);
        long agendaEndTime = cursor.getLong(PROJECTION_END_INDEX);
        int eventColor = cursor.getInt(PROJECTION_COLOR_INDEX);
        int eventId = cursor.getInt(PROJECTION_EVENT_ID_INDEX);
        String allDay = cursor.getString(PROJECTION_ALL_DAY_INDEX);
        if (agendaArrayList.isEmpty()) {
            mHashMap = new HashMap<String, Object>();
            mHashMap.put("title", agendaTitle);
            mHashMap.put("location", agendaLocation);
            mHashMap.put("allDay", allDay);
            mHashMap.put("startTime", agendaStartTime);
            mHashMap.put("endTime", agendaEndTime);
            mHashMap.put("eventColor", eventColor);
            mHashMap.put("eventId", eventId);
            mHashMap.put("allDay", allDay);
            agendaArrayList.add(mHashMap);
        } else {
            for (int i = 0; i < agendaArrayList.size(); i++) {
                String mScheTime = getCompareTimeString((Long) agendaArrayList
                        .get(i).get("startTime"));
                String agendaTimeString = getCompareTimeString(agendaStartTime);
                if (agendaTimeString.compareToIgnoreCase(mScheTime) < 0) {
                    mHashMap = new HashMap<String, Object>();
                    mHashMap.put("title", agendaTitle);
                    mHashMap.put("location", agendaLocation);
                    mHashMap.put("startTime", agendaStartTime);
                    mHashMap.put("endTime", agendaEndTime);
                    mHashMap.put("eventColor", eventColor);
                    mHashMap.put("eventId", eventId);
                    mHashMap.put("allDay", allDay);
                    agendaArrayList.add(i, mHashMap);
                    break;
                }
                if (i == (agendaArrayList.size() - 1)) {
                    mHashMap = new HashMap<String, Object>();
                    mHashMap.put("title", agendaTitle);
                    mHashMap.put("location", agendaLocation);
                    mHashMap.put("startTime", agendaStartTime);
                    mHashMap.put("endTime", agendaEndTime);
                    mHashMap.put("eventColor", eventColor);
                    mHashMap.put("eventId", eventId);
                    mHashMap.put("allDay", allDay);
                    agendaArrayList.add(mHashMap);
                    break;
                }
            }
        }
    }

    // ----------------------------------lixiange MF3.0 HCT_MODIFY add liblight
    // day and display day event when click
    // day----------------------------------

    @Override
    protected void setUpHeader() {
        if (mIsMiniMonth) {
            super.setUpHeader();
            return;
        }

        mDayLabels = new String[7];
        for (int i = Calendar.SUNDAY; i <= Calendar.SATURDAY; i++) {
            mDayLabels[i - Calendar.SUNDAY] = DateUtils.getDayOfWeekString(i,
                    DateUtils.LENGTH_MEDIUM).toUpperCase();
        }
    }

    // TODO
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mIsMiniMonth) {
            return null;
        }
        CursorLoader loader;
        synchronized (mUpdateLoader) {
            mFirstLoadedJulianDay = Time.getJulianDay(
                    mSelectedDay.toMillis(true), mSelectedDay.gmtoff)
                    - (mNumWeeks * 7 / 2);
            mEventUri = updateUri();
            String where = updateWhere();

            loader = new CursorLoader(getActivity(), mEventUri,
                    Event.EVENT_PROJECTION, where,
                    null /* WHERE_CALENDARS_SELECTED_ARGS */,
                    INSTANCES_SORT_ORDER);
            loader.setUpdateThrottle(LOADER_THROTTLE_DELAY);
        }
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Returning new loader with uri: " + mEventUri);
        }
        return loader;
    }

    @Override
    public void doResumeUpdates() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);
        boolean prevHideDeclined = mHideDeclined;
        mHideDeclined = Utils.getHideDeclinedEvents(mContext);
        if (prevHideDeclined != mHideDeclined && mLoader != null) {
            mLoader.setSelection(updateWhere());
        }
        mDaysPerWeek = Utils.getDaysPerWeek(mContext);
        updateHeader();
        mAdapter.setSelectedDay(mSelectedDay);
        mTZUpdater.run();
        mTodayUpdater.run();
        mIsOnResumeGoTo = true;
        goTo(mSelectedDay.toMillis(true), false, true, false);
        if (Utils.checkSelfPermission(mContext,
                Manifest.permission.READ_CALENDAR)) {
            mLoader = (CursorLoader) getLoaderManager().restartLoader(0, null,
                    MonthByWeekFragment.this);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        synchronized (mUpdateLoader) {
            if (Log.isLoggable(TAG, Log.DEBUG) && null != data) {
                Log.d(TAG, "Found " + data.getCount()
                        + " cursor entries for uri " + mEventUri);
            }
            CursorLoader cLoader = (CursorLoader) loader;
            if (mEventUri == null) {
                mEventUri = cLoader.getUri();
                updateLoadedDays();
            }
            if (cLoader.getUri().compareTo(mEventUri) != 0) {
                // We've started a new query since this loader ran so ignore the
                // result
                return;
            }
            ArrayList<Event> events = new ArrayList<Event>();
            Event.buildEventsFromCursor(events, data, mContext,
                    mFirstLoadedJulianDay, mLastLoadedJulianDay);
            cEvents = data;
            handleDayEvent(cEvents);
            ((MonthByWeekAdapter) mAdapter).setEvents(mFirstLoadedJulianDay,
                    mLastLoadedJulianDay - mFirstLoadedJulianDay + 1, events);
        }
    }

    private void handleDayEvent(Cursor data) {
        if (data == null || data.isClosed()) {
            return;
        }
        data.moveToPosition(-1);
        agendaArrayList.clear();
        mListViewAdapter.notifyDataSetChanged();
        if (data != null && data.getCount() != 0) {
            while (data.moveToNext()) {
                dealRule(data);
                updateScheduleView();
            }
        } else {
            noScheduleView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void eventsChanged() {
        // TODO remove this after b/3387924 is resolved
        if (mLoader != null) {
            mLoader.forceLoad();
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return EventType.GO_TO | EventType.EVENTS_CHANGED;
    }

    @Override
    public void handleEvent(EventInfo event) {
        /* HCT_MODIFY lixiange for jump date and hilght begin */
        final boolean isJumpDate = event.extraLong == CalendarController.EXTRA_JUMP_DATE;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((MonthByWeekAdapter) mAdapter).jumpDate(isJumpDate);
                try {
                    mAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }, 1);
        /* HCT_MODIFY lixiange for jump date and hilght end */
        if (event.eventType == EventType.GO_TO) {
            boolean animate = true;
            if (mDaysPerWeek * mNumWeeks * 2 < Math.abs(Time.getJulianDay(
                    event.selectedTime.toMillis(true),
                    event.selectedTime.gmtoff)
                    - Time.getJulianDay(mFirstVisibleDay.toMillis(true),
                            mFirstVisibleDay.gmtoff)
                    - mDaysPerWeek
                    * mNumWeeks
                    / 2)) {
                animate = false;
            }
            mDesiredDay.set(event.selectedTime);
            mDesiredDay.normalize(true);
            boolean animateToday = (event.extraLong & CalendarController.EXTRA_GOTO_TODAY) != 0;
            boolean delayAnimation = goTo(event.selectedTime.toMillis(true),
                    animate, true, false);
            if (animateToday) {
                // If we need to flash today start the animation after any
                // movement from listView has ended.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MonthByWeekAdapter) mAdapter).animateToday();
                        mAdapter.notifyDataSetChanged();
                    }
                }, delayAnimation ? GOTO_SCROLL_DURATION : 0);
            }
        } else if (event.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }

    @Override
    protected void setMonthDisplayed(Time time, boolean updateHighlight) {
        super.setMonthDisplayed(time, updateHighlight);
        if (!mIsMiniMonth) {
            boolean useSelected = false;
            if (time.year == mDesiredDay.year
                    && time.month == mDesiredDay.month) {
                mSelectedDay.set(mDesiredDay);
                mAdapter.setSelectedDay(mDesiredDay);
                useSelected = true;
            } else {
                mSelectedDay.set(time);
                mAdapter.setSelectedDay(time);
            }
            CalendarController controller = CalendarController
                    .getInstance(mContext);
            if (mSelectedDay.minute >= 30) {
                mSelectedDay.minute = 30;
            } else {
                mSelectedDay.minute = 0;
            }
            long newTime = mSelectedDay.normalize(true);
            if (newTime != controller.getTime() && mUserScrolled) {
                long offset = useSelected ? 0 : DateUtils.WEEK_IN_MILLIS
                        * mNumWeeks / 3;
                controller.setTime(newTime + offset);
            }
            controller.sendEvent(this, EventType.UPDATE_TITLE, time, time,
                    time, -1, ViewType.CURRENT, DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_NO_MONTH_DAY
                            | DateUtils.FORMAT_SHOW_YEAR, null, null);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        synchronized (mUpdateLoader) {
            if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
                mShouldLoad = false;
                stopLoader();
                mDesiredDay.setToNow();
            } else {
                mHandler.removeCallbacks(mUpdateLoader);
                mShouldLoad = true;
                mHandler.postDelayed(mUpdateLoader, LOADER_DELAY);
            }
        }
        if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            mUserScrolled = true;
        }

        mScrollStateChangedRunnable.doScrollStateChange(view, scrollState);
    }

    class MonthGestureListener extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {
            // Log.v("allen",""+e2.getX() +e1.getX());
            /*
             * int e_1_horizontal = (int)e1.getX(); int e_2_horizontal =
             * (int)e2.getX(); int e_1_vertical = (int)e1.getY(); int
             * e_2_vertical = (int)e2.getY(); // TODO decide how to handle
             * flings int distanceX = Math.abs(e_2_horizontal - e_1_horizontal);
             * int distanceY = Math.abs(e_2_vertical - e_1_vertical); if (
             * distanceY < distanceX) { return false; }
             */
            float absX = Math.abs(velocityX);
            float absY = Math.abs(velocityY);
            Log.d(TAG, "velX: " + velocityX + " velY: " + velocityY);
            /*
             * if (absX > absY && absX > mMinimumFlingVelocity) {
             * mTempTime.set(mFirstDayOfMonth); if (velocityX > 0) { if
             * ((mTempTime.year > 1970) || ((mTempTime.year == 1970) &&
             * (mTempTime.month > 0))) { mTempTime.month--; } } else {
             * mTempTime.month++; } mTempTime.normalize(true);
             * goTo(mTempTime.toMillis(true), true, false, true);
             * 
             * } else if (absY > absX && absY > mMinimumFlingVelocity) {
             */
            if (absY > mMinimumFlingVelocity) {
                mTempTime.set(mFirstDayOfMonth);
                int diff = 1;
                // if (absY > mMinimumTwoMonthFlingVelocity) {
                // diff = 2;
                // }
                if (velocityY < 0) {
                    mTempTime.month += diff;
                } else {
                    if ((mTempTime.year > 1970)
                            || ((mTempTime.year == 1970) && (mTempTime.month > 0))) {
                        mTempTime.month -= diff;
                    }
                }
                mTempTime.normalize(true);

                goTo(mTempTime.toMillis(true), true, false, true);
            }
            // AllInOneActivity.mSelected = false;
            return true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mContext.getContentResolver().unregisterContentObserver(
                mCalendarTouchObserver);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mDesiredDay.setToNow();
        return mGestureDetector.onTouchEvent(event);
        // TODO post a cleanup to push us back onto the grid if something went
        // wrong in a scroll such as the user stopping the view but not
        // scrolling
    }
}
