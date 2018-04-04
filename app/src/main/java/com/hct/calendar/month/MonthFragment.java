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

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.security.auth.login.LoginException;

import android.Manifest;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.AuthorityEntry;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.android.calendar.CalendarApplication;
import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarSettings;
import com.android.calendar.CalendarViewAdapter;
import com.android.calendar.Event;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.event.CreateEventDialogFragment;
import com.android.calendar.event.EventCardFragment;
import com.android.calendar.event.ScheduleDetailsActivity;
import com.android.calendar.weather.LocationText;
import com.android.calendar.weather.LocationTextCallBack;
import com.android.calendar.weather.ResultCallback;
import com.apkfuns.logutils.LogUtils;
import com.android.calendar.weather.AirCondition;
import com.android.calendar.weather.CurrentConditionCallBack;
import com.android.calendar.weather.Constants;
import com.android.calendar.weather.CurrentCondition;
import com.hct.calendar.utils.Secret;
import com.hct.calendar.utils.SharedPreferencesUtils;
import com.hct.calendar.almanac.AlmanacActivity;
import com.hct.calendar.data.Action;
import com.hct.calendar.data.AlmanacData;
import com.hct.calendar.data.WeatherData;
import com.hct.calendar.domain.AlmanacBean;
import com.hct.calendar.domain.AlmanacItem;
import com.hct.calendar.domain.WeatherItem;
import com.hct.calendar.event.EventEntity;
import com.hct.calendar.event.EventItem;
import com.hct.calendar.event.EventListAdapter;
import com.hct.calendar.event.MergedAdapter;
import com.hct.calendar.http.AlmanacApi;
import com.hct.calendar.month.MonthWeekSwitcher.AgendaMoveListener;
import com.hct.calendar.month.MonthWeekSwitcher.AgendaTouchStateListener;
import com.hct.calendar.month.ViewMode.ModeChangeListener;
import com.hct.calendar.month.WeekViewSwitchController.OnWeekViewSwitchListener;
import com.hct.calendar.ui.MonthWeekFrameLayout;
import com.hct.calendar.ui.MonthWeekFrameLayout.ShowPagerCallback;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.LocaleUtils;
import com.hct.calendar.utils.NetworkUtils;
import com.hct.calendar.utils.OkHttpClientManager;
import com.hct.calendar.utils.PreferenceUtils;
import com.hct.calendar.utils.WeatherUtils;
import com.loc.i;

public class MonthFragment extends SimpleDayFragment
        implements CalendarController.EventHandler, LoaderManager.LoaderCallbacks<Cursor>, OnWeekViewSwitchListener,
        ViewMode.ModeChangeListener, MonthWeekFrameLayout.OnSizeChangeListener, AMapLocationListener {
    private static final String TAG = "MonthFragment";
    private static final String TAG_EVENT_DIALOG = "event_dialog";
    private static final boolean DEBUG_ENABLE = false;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private CreateEventDialogFragment mEventDialog;
    private boolean requestLocationPermissionSuccess = true;
    // Selection and selection args for adding event queries
    private static final String WHERE_CALENDARS_VISIBLE = Calendars.VISIBLE + "=1";
    private static final String INSTANCES_SORT_ORDER = Instances.START_DAY + "," + Instances.START_MINUTE + ","
            + Instances.TITLE;
    protected static boolean mShowDetailsInMonth = false;

    protected float mMinimumTwoMonthFlingVelocity;
    protected boolean mIsMiniMonth;
    protected boolean mHideDeclined;

    protected int mFirstLoadedJulianDay;
    protected int mLastLoadedJulianDay;
    private boolean misPageChanged = false;
    private String district = "";
    private static final int WEEKS_BUFFER = 1;
    // How long to wait after scroll stops before starting the loader
    // Using scroll duration because scroll state changes don't update
    // correctly when a scroll is triggered programmatically.
    private static final int LOADER_DELAY = 200;
    // The minimum time between requeries of the data if the db is
    // changing
    private static final int LOADER_THROTTLE_DELAY = 500;
    private static int MAXJULIANDAY = 2465425; // 2038.1.1
    private static int MINTIME = -28800001; // 1970.1.1

    private CursorLoader mLoader;
    // private AlmanacAsyncTaskLoader mAlmanacLoaderHandler;
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
    private ArrayList<EventEntity> agendaArrayList;
    private AgendaListView listView;
    private LinearLayout mWeekView;
    private ViewSwitcher mViewSwitcher;
    private WeekViewSwitchController mViewSwitchController;
    private boolean mIsMonthViewMode = true;
    private MonthWeekFrameLayout mMonthWeekMainLayout;
    private Runnable mSelectMonthFirstDay = new SelectMonthFirstDay();
    private MonthWeekSwitcher mMonthWeekSwitcher;
    private ViewMode mViewMode;
    private View separatorLine;
    private LinearLayout noEventLayout;
    private ImageView noEventView;
    private EventListAdapter mListViewAdapter;
    // private EventFooterAdapter mFooterAdapter;
    private MergedAdapter<ListAdapter> mMergedAdapter;
    private Long mTouchDay;
    private Cursor cEvents;
    // private Cursor mAlmanacCursor;
    protected boolean mHasTouchDayChange;
    public static boolean mIsSelectDayTodaySame = true;

    private final int LOAD_EVENT_ID = 0;
    private final int LOAD_ALMANAC_ID = 1;

    private long mQueryStartTime = 0l;
    private long mQueryEndTime = 0l;
    private boolean mEnableAlmanacShow = false;
    /**
     * private TextView scheduleTime; private TextView scheduleTitle; private
     * TextView scheduleTime2; private TextView scheduleTitle2;
     */
    // private ImageView triangle;

    public static int itemHeight = 84;
    public static final int beginHeight = 84;
    public static final int dropHeight = 170;
    private static boolean isAnimator = false;
    private SharedPreferences mSettingPreference;

    private View splitView;
    // add by zyp for GMOS-4146
    private Activity mActivity;
    // add by zyp for GMOS-2337
    private LinearLayout blankView;
    private SharedPreferences spToList;
    private boolean mIsHasAgenda;

    private String districtText;
    private int weatherIconRes;
    private String temp;
    private String weather;
    private String air;
    private String wind;
    private String wet;
    private int airCondition;
    private final int INIT_WEATHER = 0;
    private final int INIT_WEATHER_HUANGLI = 1;
    private final int INIT_WEATHER_DATA = 2;
    private final int INIT_WEATHER_AIR = 3;
    private final int INIT_WEATHER_DISTRICT = 4;
    private Locale mLocale;
    private SharedPreferences mWeatherPreference;
    private final static String  WEATHER_SP_NAME = "weather";
    static class WeatherItem {
        static String AIR = "air";
        static String AIRCONDITION = "air_Condition";
        static String DISTRICT = "district";
        static String ICON = "icon";
        static String TEMP = "temp";
        static String WEATHER = "weather";
        static String WET = "wet";
        static String WIND = "wind";
    }

    private Handler mEventDialogHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            final FragmentManager manager = getFragmentManager();
            if (msg.what == 1) {
                if (manager != null) {
                    Time day = (Time) msg.obj;
                    // mEventDialog = new CreateEventDialogFragment(day, this);
                    // mEventDialog.show(manager, TAG_EVENT_DIALOG);
                }
            } else if (msg.what == 2) {
                mHandler.postDelayed(mUpdateLoader, LOADER_DELAY);
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
            if (mViewMode.isWeekMode()) {
                mViewSwitchController.updateTimeZone(tz);
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
                mLoader = (CursorLoader) getLoaderManager().initLoader(LOAD_EVENT_ID, null, MonthFragment.this);
            }
        }
    };

    private class SelectDayRunnable implements Runnable {
        final private Time selectTime = new Time();

        public SelectDayRunnable setSelectTime(Time t) {
            selectTime.set(t);
            return this;
        }

        @Override
        public void run() {
            if (DEBUG_ENABLE) {
                Log.v(TAG, "mSelectDayRunnable-->selectTime#" + selectTime.toString());
            }
            mAdapter.setSelectMonthDay(selectTime.toMillis(true));
        }

    }

    private SelectDayRunnable mSelectDayRunnable = new SelectDayRunnable();

    private boolean isTounchDayInUpdateRange(long touch) {
        Time t = new Time(mSelectedDay.timezone);
        t.set(touch);
        int currJulianDay = Time.getJulianDay(t.toMillis(true), t.gmtoff);
        if (currJulianDay >= mFirstLoadedJulianDay && currJulianDay <= mLastLoadedJulianDay) {
            return true;
        }
        return false;
    }

    private BroadcastReceiver mProviderChangeBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            mHandler.postDelayed(mUpdateLoader, LOADER_DELAY);
        }

    };
    private ViewGroup huangliLayout;
    private ImageView huangliIcon;
    private TextView huangliText1;
    private TextView huangliText2;
    private TextView huangliText3;

    private ViewGroup weatherLayout;
    private ImageView weatherIcon;
    private TextView weatherText1;
    private TextView weatherText2;
    private TextView weatherText3;
    private TextView tv_district;
    private ImageView iv_weather;
    private TextView tv_temp;
    private TextView tv_weather;
    private TextView tv_air;
    private TextView tv_wet;
    private TextView tv_wind;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private TextView tv_air_condition;

    /**
     * Updates the uri used by the loader according to the current position of
     * the listview.
     * 
     * @return The new Uri to use
     */
    private Uri updateUri() {
        SimpleWeekView child = getFirstGridItem();
        if (child != null) {
            int julianDay = child.getFirstJulianDay();
            mFirstLoadedJulianDay = julianDay;
        }
        // -1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mFirstLoadedJulianDay - 1);
        long start = mTempTime.toMillis(true);
        mLastLoadedJulianDay = mFirstLoadedJulianDay + (mNumWeeks + 2 * WEEKS_BUFFER) * 7;
        if (mLastLoadedJulianDay >= MAXJULIANDAY) {
            mLastLoadedJulianDay = MAXJULIANDAY - 1;
        }
        // +1 to ensure we get all day events from any time zone
        mTempTime.setJulianDay(mLastLoadedJulianDay + 1);
        long end = mTempTime.toMillis(true);
        // Create a new uri with the updated times
        Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start);
        ContentUris.appendId(builder, end);

        mQueryStartTime = start;
        mQueryEndTime = end;

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
        if (mHideDeclined) {
            where += " AND " + Instances.SELF_ATTENDEE_STATUS + "!=" + Attendees.ATTENDEE_STATUS_DECLINED;
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
        mActivity = activity;
        mTZUpdater.run();
        if (DEBUG_ENABLE)
            Log.d(TAG, "onAttach, SelectedDay = " + mSelectedDay);
        if (mAdapter != null) {
            mAdapter.setSelectedDay(mSelectedDay);
        }

        mIsDetached = false;
        ViewConfiguration viewConfig = ViewConfiguration.get(activity);
        mMinimumTwoMonthFlingVelocity = viewConfig.getScaledMaximumFlingVelocity() / 2;
        Resources res = activity.getResources();
        mShowCalendarControls = Utils.getConfigBool(activity, R.bool.show_calendar_controls);
        // Synchronized the loading time of the month's events with the
        // animation of the
        // calendar controls.
        if (mShowCalendarControls) {
            mEventsLoadingDelay = res.getInteger(R.integer.calendar_controls_animation_time);
        }
        // mShowDetailsInMonth = res.getBoolean(R.bool.show_details_in_month);
        mEnableAlmanacShow = Utils.isEnableAlmanacShow(activity);
    }

    @Override
    public void onDetach() {
        mIsDetached = true;
        super.onDetach();
        if (mShowCalendarControls) {
            if (mViewPager != null) {
                mViewPager.removeCallbacks(mLoadingRunnable);
            }
        }
        if (null != mLocationClient) {
            mLocationClient.stopLocation();
            mLocationClient.unRegisterLocationListener(this);
            mLocationClient = null;
        }
    }

    @Override
    protected void setUpAdapter() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);

        HashMap<String, Integer> weekParams = new HashMap<String, Integer>();
        weekParams.put(WeeksAdapter.WEEK_PARAMS_NUM_WEEKS, mNumWeeks);
        weekParams.put(WeeksAdapter.WEEK_PARAMS_SHOW_WEEK, mShowWeekNumber ? 1 : 0);
        weekParams.put(WeeksAdapter.WEEK_PARAMS_WEEK_START, mFirstDayOfWeek);
        weekParams.put(MonthByWeekAdapter.WEEK_PARAMS_IS_MINI, mIsMiniMonth ? 1 : 0);
        weekParams.put(WeeksAdapter.WEEK_PARAMS_JULIAN_DAY,
                Time.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff));
        weekParams.put(WeeksAdapter.WEEK_PARAMS_DAYS_PER_WEEK, mDaysPerWeek);
        if (mAdapter == null) {
            // mAdapter = new MonthViewPagerAdapter(getActivity(), weekParams,
            // mEventDialogHandler);
            mAdapter = new MonthViewPagerAdapter(getMyActivity(), weekParams, mEventDialogHandler);
            mAdapter.registerDataSetObserver(mObserver);
        } else {
            mAdapter.updateParams(weekParams);
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        v = inflater.inflate(R.layout.hct_full_month_by_week, container, false);

        LinearLayout month = (LinearLayout) v.findViewById(R.id.month);

        mDayNamesHeader = (ViewGroup) v.findViewById(R.id.day_names);
        // click day
        eventLayout = (LinearLayout) v.findViewById(R.id.agenda_list);
        LogUtils.e("TTT find eventLayout here --- " + eventLayout);
        listView = (AgendaListView) v.findViewById(R.id.listview);

        mWeekView = (LinearLayout) v.findViewById(R.id.week_view);
        mViewSwitcher = (ViewSwitcher) v.findViewById(R.id.week_switcher);
        mMonthWeekMainLayout = (MonthWeekFrameLayout) v.findViewById(R.id.month_main_layout);
        mMonthWeekMainLayout.setShowPagerCallback(new ShowPagerCallback() {

            @Override
            public void onShow() {
                doResumeUpdates();
            }
        });
        separatorLine = (View) v.findViewById(R.id.separator_line);
        agendaArrayList = new ArrayList<EventEntity>();
        noEventLayout = (LinearLayout) v.findViewById(R.id.noEventLayout);
        blankView = (LinearLayout) v.findViewById(R.id.blankView);
        huangliLayout = (ViewGroup) v.findViewById(R.id.almanac_item_it);
        huangliText1 = (TextView) huangliLayout.findViewById(R.id.almanac_text1);
        huangliText2 = (TextView) huangliLayout.findViewById(R.id.almanac_text2);
        huangliText3 = (TextView) huangliLayout.findViewById(R.id.almanac_text3);

        weatherLayout = (ViewGroup) v.findViewById(R.id.weather_layout);
        noEventView = (ImageView) v.findViewById(R.id.noEventView);
        Drawable d = this.getResources().getDrawable(R.drawable.icon_no_event);
        d.setTint(this.getResources().getColor(R.color.no_event_color));
        noEventView.setImageDrawable(d);
        splitView = v.findViewById(R.id.split_view);
        tv_district = (TextView) weatherLayout.findViewById(R.id.tv_district);
        iv_weather = (ImageView) weatherLayout.findViewById(R.id.iv_weather);
        tv_temp = (TextView) weatherLayout.findViewById(R.id.tv_temp);
        tv_weather = (TextView) weatherLayout.findViewById(R.id.tv_weather);
        tv_air_condition = (TextView) weatherLayout.findViewById(R.id.tv_air_condition);
        tv_air = (TextView) weatherLayout.findViewById(R.id.tv_air);
        tv_wet = (TextView) weatherLayout.findViewById(R.id.tv_wet);
        tv_wind = (TextView) weatherLayout.findViewById(R.id.tv_wind);        mLocale = mContext.getResources().getConfiguration().locale;
        mSettingPreference = getMyActivity().getSharedPreferences(CalendarSettings.SETTINGS_SP_NAME, Context.MODE_PRIVATE);
        mWeatherPreference = getMyActivity().getSharedPreferences(WEATHER_SP_NAME, Context.MODE_PRIVATE);
        initHandler.sendEmptyMessage(INIT_WEATHER_HUANGLI);
        return v;
    }

//    private void initFooter(View v) {
//        // TODO Auto-generated method stub
//        huangliLayout = (ViewGroup) v.findViewById(R.id.almanac_item_it);
//        huangliText1 = (TextView) huangliLayout.findViewById(R.id.almanac_text1);
//        huangliText2 = (TextView) huangliLayout.findViewById(R.id.almanac_text2);
//        huangliText3 = (TextView) huangliLayout.findViewById(R.id.almanac_text3);
//
//        weatherLayout = (ViewGroup) v.findViewById(R.id.weather_layout);
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                // TODO Auto-generated method stub
//                sp = getMyActivity().getSharedPreferences(CalendarSettings.SETTINGS_SP_NAME, Context.MODE_PRIVATE);
//                initHandler.sendEmptyMessage(INIT_WEATHER_HUANGLI);
//            }
//        }).start();

        // boolean showHuangli = PreferenceUtils.getBoolean(getActivity(),
        // PreferenceUtils.SHOWALMANC_KEY, false);
        // boolean isShowWeather = PreferenceUtils.getBoolean(getActivity(),
        // GeneralPreferences.WEATHER_SWITCH_KEY, false);

 //   }

    private void addFooter(ListView listView) {
        ViewGroup mFooterParent = (ViewGroup) LayoutInflater.from(listView.getContext())
                .inflate(R.layout.almanac_blank_footer, null);

        listView.addFooterView(mFooterParent);
    }

    private Handler initHandler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case INIT_WEATHER:
                updateAdapterWeatherData();
                break;

            case INIT_WEATHER_HUANGLI:
                huangliLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.GONE);
                boolean available = NetworkUtils.isNetworkAvailable();
                Log.d(TAG, "Init Location, Internet Available: " + available);
                if (available) {
                    InitLocationListener();
                } else {
                    initHandler.sendEmptyMessage(INIT_WEATHER);
                }
                break;
            case INIT_WEATHER_DISTRICT:
                mWeatherPreference.edit().putString(WeatherItem.DISTRICT, district).commit();
                break;
            case INIT_WEATHER_DATA:
                handleWeatherRequestData((CurrentCondition) msg.obj);
                updateAdapterWeatherData();
                break;
            case INIT_WEATHER_AIR:
                handleWeatherRequestAir((AirCondition) msg.obj);
                updateAdapterWeatherData();
                break;
            }
        }
    };

    private void InitLocationListener() {
        // TODO Auto-generated method stub
        mLocationClient = new AMapLocationClient(getMyActivity());
        mLocationClient.setLocationListener(this);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setOnceLocation(true);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        itemHeight = 84;
    }
    // HCT_MODIFY lixiange MF3.0 add liblight day and display day event when
    // click day
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // mViewPager.setSelector(new StateListDrawable());
        addFooter(listView);

        if (!mIsMiniMonth) {
            mViewPager.setBackgroundColor(Color.TRANSPARENT);
        }

        // To get a smoother transition when showing this fragment, delay
        // loading of events until
        // the fragment is expended fully and the calendar controls are gone.
        mTouchDay = System.currentTimeMillis();

        if (Utils.checkAndRequestPermission(this, Manifest.permission.READ_CALENDAR,
                Utils.REQUEST_PERMISSIONS_CALENDAR)) {
            loadCalendarEventWithPermission();
        }
        mAdapter.setViewPager(mViewPager);
        mListViewAdapter = new EventListAdapter(mContext, agendaArrayList);
        mMergedAdapter = new MergedAdapter<ListAdapter>();
        /*
         * if(Utils.isAbroadBranch(mContext)){
         * mMergedAdapter.setAdapters(mListViewAdapter); }else{
         * mWeatherController = WeatherController.getController(mContext
         * .getApplicationContext()); BaseAdapter weatherAdapter =
         * mWeatherController.getWeatherListAdapter(); mFooterAdapter = new
         * EventFooterAdapter(mContext);
         * mMergedAdapter.setAdapters(mListViewAdapter, weatherAdapter,
         * mFooterAdapter);
         * mWeatherController.registerWeatherChangeObserver(mWeaDataSetObserver)
         * ; }
         */

        mMergedAdapter.setAdapters(mListViewAdapter);

        listView.setAdapter(mMergedAdapter);
        Log.e("fushuo", "count123=" + mMergedAdapter.getCount() + "---->agendaArrayList=" + agendaArrayList.size());
        // listView.setOnItemClickListener(new ItemClickListenerImpl());
        mMonthWeekSwitcher = new MonthWeekSwitcher(eventLayout);
        mMonthWeekSwitcher.setDistance(mMonthWeekMainLayout.getMaxTranslation());
        mMonthWeekSwitcher.addAgendaTouchStateListener(listView);
        // mMonthWeekSwitcher.addAgendaTouchStateListener(scrollView);
        listView.setTouchGestureDetector(mMonthWeekSwitcher);
        ((MonthViewPager) mViewPager).setTouchGestureDetector(mMonthWeekSwitcher);
        mMonthWeekSwitcher.addAgendaTouchStateListener(new AgendaTouchStateListener() {
            @Override
            public void onStateChange(int state) {
                if (state == AgendaTouchStateListener.AGENDA_TOP) {
                    // mViewPager.setVisibility(View.GONE);
                    MonthFragment.itemHeight = MonthFragment.beginHeight;
                    toggleMonthAndWeek(true);
                    mViewMode.enterWeekMode();
                    mIsMonthViewMode = false;
                    mViewSwitchController.setWeekMode(true);
                    return;
                } else if (state == AgendaTouchStateListener.AGENDA_NORMAL) {
                    toggleMonthAndWeek(false);
                    mViewMode.enterMonthMode();
                    mViewSwitchController.setWeekMode(false);
                    mIsMonthViewMode = true;
                } else if (state == AgendaTouchStateListener.AGENDA_DROP) {
                    if (isAnimator == true) {
                        return;
                    }
                    isAnimator = true;
                    toggleMonthAndWeek(false);
                    mViewMode.enterMonthMode();
                    mViewSwitchController.setWeekMode(false);
                    mIsMonthViewMode = true;
                    ValueAnimator va;
                    va = ValueAnimator.ofInt(beginHeight, dropHeight);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator arg0) {
                            // TODO Auto-generated method stub
                            int h = (Integer) arg0.getAnimatedValue();
                            MonthFragment.itemHeight = h;
                            View mView;
                            // mAdapter.notifyDataSetChanged();
                            GridView mGridView = (GridView) mViewPager.findViewWithTag(mViewPager.getCurrentItem());
                            if (mGridView != null) {
                                Log.i("zhang", "gridView notify " + mGridView.getChildCount());
                                for (int i = 0; i < mGridView.getCount(); i++) {
                                    mView = (MonthWeekEventsView) mGridView.getChildAt(i);
                                    if (null != mView) {
                                        mView.requestLayout();
                                    }
                                }
                            }
                            mAdapter.refresh();
                        }
                    });
                    va.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator arg0) {
                            // TODO Auto-generated method stub
                            updateNearbyViewPager();
                            isAnimator = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                    va.setDuration(0);
                    va.start();
                    mMonthWeekMainLayout.updateItemHeight(dropHeight);

                    if (null != mMonthWeekMainLayout) {
                        mMonthWeekMainLayout.requestLayout();
                    }

                } else if (state == AgendaTouchStateListener.AGENDA_UNDROP) {
                    if (isAnimator == true) {
                        return;
                    }
                    isAnimator = true;
                    toggleMonthAndWeek(false);
                    mViewMode.enterMonthMode();
                    mViewSwitchController.setWeekMode(false);
                    mIsMonthViewMode = true;
                    ValueAnimator va;
                    va = ValueAnimator.ofInt(dropHeight, beginHeight);
                    va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                        @Override
                        public void onAnimationUpdate(ValueAnimator arg0) {
                            // TODO Auto-generated method stub
                            int h = (Integer) arg0.getAnimatedValue();
                            MonthFragment.itemHeight = h;
                            View mView;
                            // mAdapter.notifyDataSetChanged();
                            GridView mGridView = (GridView) mViewPager.findViewWithTag(mViewPager.getCurrentItem());
                            if (mGridView != null) {
                                Log.i("zhang", "gridView notify " + mGridView.getChildCount());
                                for (int i = 0; i < mGridView.getCount(); i++) {
                                    mView = (MonthWeekEventsView) mGridView.getChildAt(i);
                                    if (null != mView) {
                                        mView.requestLayout();
                                    }

                                }
                            }
                            mAdapter.refresh();
                        }
                    });
                    va.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationRepeat(Animator arg0) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onAnimationEnd(Animator arg0) {
                            // TODO Auto-generated method stub
                            updateNearbyViewPager();
                            isAnimator = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator arg0) {
                            // TODO Auto-generated method stub

                        }
                    });
                    va.setDuration(0);
                    va.start();
                    mMonthWeekMainLayout.updateItemHeight(beginHeight);
                    if (null != mMonthWeekMainLayout) {
                        mMonthWeekMainLayout.requestLayout();
                    }
                }
                // mViewPager.setVisibility(View.VISIBLE);
            }
        });
        mMonthWeekSwitcher.setAgendaMoveListener(new AgendaMoveListener() {
            @Override
            public boolean onAgendaMove(float distanceY) {
                if (MonthFragment.itemHeight == MonthFragment.beginHeight && !MonthWeekSwitcher.isMove) {
                    viewPagerTranslation(distanceY);
                }
                return false;
            }
        });
        // mViewSwitchController = new WeekViewSwitchController(getActivity(),
        // mAdapter, mViewSwitcher,
        mViewSwitchController = new WeekViewSwitchController(getMyActivity(), mAdapter, mViewSwitcher,
                CalendarController.getInstance(mContext), mEventDialogHandler);
        mViewSwitcher.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                return mViewSwitchController.makeView();
            }
        });
        mMonthWeekSwitcher.addAgendaTouchStateListener(mViewSwitchController);
        mViewSwitchController.setOnWeekViewSwitchListener(this);
        mViewSwitchController.setTouchGestureDetector(mMonthWeekSwitcher);
        eventLayout.setOnTouchListener(mMonthWeekSwitcher);
        // if (getActivity() instanceof ViewMode.ModeChangeListener) {
        // mViewMode.addListener((ModeChangeListener) getActivity());
        // }
        if (getMyActivity() instanceof ViewMode.ModeChangeListener) {
            mViewMode.addListener((ModeChangeListener) getMyActivity());
        }
        mMonthWeekMainLayout.setOnSizeChangeListener(this);
    }

    private void viewPagerTranslation(float distanceY) {
        if (distanceY >= -mMonthWeekMainLayout.getMaxTranslation()) {
            mViewPager.setTranslationY(distanceY - mMonthWeekSwitcher.getDistanceOffset());
            if (distanceY - mMonthWeekSwitcher.getDistanceOffset() <= -mViewSwitchController.getCurrWeekIndexOfMonth()
                    * mMonthWeekMainLayout.getItemHeight()) {
                toggleMonthAndWeek(true);
            } else {
                toggleMonthAndWeek(false);
            }
        }
        if (distanceY < -mMonthWeekMainLayout.getMaxTranslation()
                && mViewPager.getTranslationY() > -mMonthWeekMainLayout.getMaxTranslation()) {
            mViewPager.setTranslationY(-mMonthWeekMainLayout.getMaxTranslation());
        }
    }

    private void toggleMonthAndWeek(boolean showWeek) {
        int visibility = showWeek ? View.VISIBLE : View.GONE;
        // if (mWeekView.getVisibility() == visibility) {
        // return;
        // }
        mWeekView.setVisibility(visibility);
        if (showWeek) {
            mViewSwitchController.refresh();
        } else {
        }
    }

    private void loadCalendarEventWithPermission() {
        if (mShowCalendarControls) {
            mViewPager.postDelayed(mLoadingRunnable, mEventsLoadingDelay);
        } else {
            mLoader = (CursorLoader) getLoaderManager().initLoader(LOAD_EVENT_ID, null, this);
        }
    }

    public MonthFragment() {
        this(System.currentTimeMillis(), Time.getCurrentTimezone(), true);
    }

    public MonthFragment(long initialTime, String initTimeZone, boolean isFullMonth) {
        super(initialTime, initTimeZone);
        mSelectedDay.set(initialTime);
        mTempTime.set(initialTime);
        mDesiredDay.switchTimezone(initTimeZone);
        mDesiredDay.set(initialTime);
        // mIsMiniMonth = isMiniMonth;
        mViewMode = new ViewMode();
        if (isFullMonth) {
            mViewMode.enterFullMonthMode();
        } else {
            mViewMode.enterHalfMonthMode();
        }
        mViewMode.addListener(this);
    }

    protected Uri resolveUri() {
        return Events.CONTENT_URI;
    }

    public void setTouchDayScheduleLayout(long startTime) {
        long endTime = startTime + 24 * 60 * 60 * 1000;
        Log.d(TAG, "lxg setTouchDayScheduleLayout startTime =" + startTime + ",endTime=" + endTime);
        handleDayEvent(cEvents);
    }

    private void showEmptyView() {
        if (listView != null && noEventLayout != null) {
            listView.setVisibility(View.VISIBLE);
            separatorLine.setVisibility(View.GONE);
            noEventLayout.setVisibility(View.VISIBLE);
            eventLayout.setOnTouchListener(mMonthWeekSwitcher);
        }
    }

    private void showListView() {
        if (listView != null && noEventLayout != null) {
            // eventLayout.setOnTouchListener(null);
            listView.setTouchGestureDetector(mMonthWeekSwitcher);
            noEventLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            separatorLine.setVisibility(View.VISIBLE);
        }
    }

    private void updateScheduleView() {
        final boolean showHuangli = mSettingPreference.getBoolean("isshowalmanac", true);
        final boolean isShowWeather = mSettingPreference.getBoolean("isshowweather", true);
        LogUtils.w("agendaArrayList.size() = " + agendaArrayList.size() + ", showHuangli = " + showHuangli);
        Log.d(TAG, " showHuangli: " + showHuangli
                + ", isShowWeather: " + isShowWeather
                + ", mMergedAdapter count: " + mMergedAdapter.getCount());
        // added by chenhuaiyu start
        Time today = new Time();
        today.set(System.currentTimeMillis());
        int julianDayToday = today.getJulianDay(today.toMillis(true), today.gmtoff);
        int julianDaySelectedDay = mSelectedDay.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff);
        if (julianDayToday == julianDaySelectedDay) {
            mIsSelectDayTodaySame = true;
        } else {
            mIsSelectDayTodaySame = false;
        }
        // added by chenhuaiyu start

        if (showHuangli) {
            // huangliLayout.setVisibility(View.VISIBLE);
        } else {
            huangliLayout.setVisibility(View.GONE);
        }
        if (isShowWeather) {
            weatherLayout.setVisibility(View.VISIBLE);
        } else {
            weatherLayout.setVisibility(View.GONE);
        }

        if (CalendarViewAdapter.mCallBack != null) {

            CalendarViewAdapter.mCallBack.currentTime(getSelectedTime());
        }
        final String day = mSelectedDay.year + "-" + (mSelectedDay.month + 1) + "-" + mSelectedDay.monthDay;
        LogUtils.e("mSelectedDay = " + day);
        final String urlStr = AlmanacApi.urlTamplate(day);
        LogUtils.e(urlStr);
        AlmanacData.getAlmanacBody(getMyActivity(), day, new Action<AlmanacBean>() {
            @Override
            public void next(final AlmanacBean bb) {
                if (MonthFragment.this.getMyActivity() == null) {
                    return;
                }
                Log.d(TAG, "process next Almanac Bean, day: " + day);
                MonthFragment.this.getMyActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (bb != null && bb.dateJson != null && showHuangli
                                && LocaleUtils.isChinese(getMyActivity())) {
                            LogUtils.d("show almanac data now !");
                            Log.d(TAG, "show almanac data now !");
                            huangliLayout.setVisibility(View.VISIBLE);

                            final AlmanacItem item = AlmanacApi.json2Item(bb.dateJson);
                            AlmanacItem itemToList = AlmanacApi.json2Item(bb.dateJson);
                            if (getMyActivity() != null && isAdded()) {
                                huangliText1.setText(getString(R.string.nongli, item.lunarDate));
                                huangliText2.setText(getString(R.string.yii, item.yii));
                                huangliText3.setText(getString(R.string.jii, item.jii));
                            }
                            // add huangli and tianqi
                            if (null != itemToList) {
                                EventEntity entityitemToList = new EventEntity(null);
                                entityitemToList.lunarDate = itemToList.lunarDate;
                                entityitemToList.jii = itemToList.jii;
                                entityitemToList.yii = itemToList.yii;
                                entityitemToList.dateJson = bb.dateJson;
                                boolean isAdd = false;
                                if(agendaArrayList.size() > 0) {
                                    for (int i = 0; i < agendaArrayList.size(); i++) {
                                        String title = agendaArrayList.get(i).title;
                                        String weather = agendaArrayList.get(i).weather;
                                        if(title != null) {
                                            continue;
                                        } else if(weather != null) {
                                            EventEntity weatherEntity = agendaArrayList.get(i);
                                            if(!isAdd) {
                                                agendaArrayList.set(i, entityitemToList);
                                                agendaArrayList.add(weatherEntity);
                                                isAdd = true;
                                                break;
                                            }
                                        } else {
                                            isAdd = true;
                                            agendaArrayList.set(i, entityitemToList);
                                        }
                                    }
                                } else {
                                    isAdd = true;
                                    agendaArrayList.add(entityitemToList);
                                }
                                if(isAdd == false) {
                                    agendaArrayList.add(entityitemToList);
                                }
                            }
                            // huangliIcon.setImageResource(R.drawable.ic_alarm_dark);
                            huangliLayout.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LogUtils.e("click almanac item now !");
                                    getMyActivity().startActivity(new Intent(getMyActivity(), AlmanacActivity.class)
                                            .putExtra("almanacInfo", bb.dateJson));
                                }
                            });

                        } else {
                            Log.e(TAG, "almanac data hide, showHuangli = " + showHuangli);
                            huangliLayout.setVisibility(View.GONE);
                        }
                        boolean isShowWeatherNow = mSettingPreference.getBoolean("isshowweather", true);
                        if (!mIsSelectDayTodaySame) {
                            isShowWeatherNow = false;
                        } else {
                            initHandler.sendEmptyMessage(INIT_WEATHER);
                        }
                        mListViewAdapter.setShowState(showHuangli, isShowWeatherNow, agendaArrayList);
                    }
                });
            }
        });

    }

    public void getWeatherToList(boolean isAddWeather) {

        spToList = getMyActivity().getSharedPreferences("weather", Context.MODE_PRIVATE);

        if (null != spToList) {

            EventEntity entityspToList = new EventEntity(null);
            entityspToList.district = spToList.getString("district",
                    getMyActivity().getString(R.string.default_district));
            entityspToList.weatherIcon = spToList.getInt("icon", R.drawable.sunny);
            entityspToList.temp = spToList.getString("temp", getMyActivity().getString(R.string.default_temp));
            Log.e("liaozhenbin", "1167 entityspToList.temp: " + temp);
            entityspToList.weather = spToList.getString("weather", getMyActivity().getString(R.string.default_weather));
            entityspToList.air = spToList.getString("air", getMyActivity().getString(R.string.default_air));
            entityspToList.wind = spToList.getString("wind", getMyActivity().getString(R.string.default_wind));
            entityspToList.wet = spToList.getString("wet", getMyActivity().getString(R.string.default_wet));
            entityspToList.airCondition = spToList.getInt("air_condition", 100) + "";

            for (int i = 0; i < agendaArrayList.size(); i++) {
                String district = agendaArrayList.get(i).district;
                String temp = agendaArrayList.get(i).temp;
                String weather = agendaArrayList.get(i).weather;
                String air = agendaArrayList.get(i).air;
                String wind = agendaArrayList.get(i).wind;
                if (null != weather && weather.length() > 0 && weather.equals(entityspToList.weather)
                        && district.equals(entityspToList.district) && temp.equals(entityspToList.temp)
                        && air.equals(entityspToList.air) && wind.equals(entityspToList.wind)) {
                    agendaArrayList.set(i, entityspToList);
                    isAddWeather = true;
                }
            }

            if (!isAddWeather) {
                agendaArrayList.add(entityspToList);
            }

        }

    };

    private class ItemClickListenerImpl implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            Log.e("fushuo", "--->id=" + id + "----->count=" + a.getCount());
            if (id < 0) {
                return;
            }
            Intent intent = new Intent(mContext, ScheduleDetailsActivity.class);
            intent.putExtra("id", agendaArrayList.get(position).eventId);
            mContext.startActivity(intent);
            // Object item = mMergedAdapter.getItem(position);
            // if (item instanceof EventItem) {
            // EventItem i = (EventItem) mMergedAdapter.getItem(position);
            // i.performClick(v);
            // }
        }
    }

    private String getCompareTimeString(long time) {
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(time);
        return dateFormat24.format(date);
    }

    private String getCompareDateString(long time) {
        SimpleDateFormat dateFormat24 = new SimpleDateFormat("yyyyMMdd");
        dateFormat24.setTimeZone(TimeZone.getTimeZone(mSelectedDay.timezone));
        Date date = new Date(time);
        return dateFormat24.format(date);
    }

    public void todaySchedule(Cursor cursor) {
        EventEntity entity = new EventEntity(cursor);
        if (agendaArrayList.isEmpty()) {
            agendaArrayList.add(entity);
            mIsHasAgenda = true;
        } else {
            for (int i = 0; i < agendaArrayList.size(); i++) {
                String mScheTime = getCompareTimeString((Long) agendaArrayList.get(i).startTime);
                String agendaTimeString = getCompareTimeString(entity.startTime);
                if (agendaTimeString.compareToIgnoreCase(mScheTime) < 0) {
                    agendaArrayList.add(i, entity);
                    mIsHasAgenda = true;
                    break;
                }
                if (i == (agendaArrayList.size() - 1)) {
                    agendaArrayList.add(entity);
                    mIsHasAgenda = true;
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
        case Utils.REQUEST_PERMISSIONS_CALENDAR:
            if (grantResults != null && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCalendarEventWithPermission();
            }
            break;
        case REQUEST_CODE_ASK_PERMISSIONS:
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    requestLocationPermissionSuccess = requestLocationPermissionSuccess && true;
                } else {
                    requestLocationPermissionSuccess = false;
                }
            }
            if (requestLocationPermissionSuccess) {
                InitLocationListener();
            } else {
                initHandler.sendEmptyMessage(INIT_WEATHER);
                Toast.makeText(getMyActivity(), getMyActivity().getString(R.string.loaction_deny_toast),
                        Toast.LENGTH_LONG).show();
            }
            break;
        default:
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

            mDayLabels[i - Calendar.SUNDAY] = DateUtils
                    .getDayOfWeekString(i,
                            CalendarUtil.isEnglish() ? DateUtils.LENGTH_MEDIUM : DateUtils.LENGTH_SHORTEST)
                    .toUpperCase();
            Log.d("liaozhenbin", "MonthFragment labels: " + mDayLabels[i - Calendar.SUNDAY]);
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
            try {
                mFirstLoadedJulianDay = mAdapter.getStartMonthJulianDay(mSelectedDay);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            mEventUri = updateUri();
            String where = updateWhere();

            // loader = new CursorLoader(getActivity(), mEventUri,
            // Event.EVENT_PROJECTION, where,
            loader = new CursorLoader(getMyActivity(), mEventUri, Event.EVENT_PROJECTION, where,
                    null /* WHERE_CALENDARS_SELECTED_ARGS */, INSTANCES_SORT_ORDER);
            loader.setUpdateThrottle(LOADER_THROTTLE_DELAY);
        }
        return loader;
    }

    @Override
    public void doResumeUpdates() {
        mFirstDayOfWeek = Utils.getFirstDayOfWeek(mContext);
        mShowWeekNumber = Utils.getShowWeekNumber(mContext);

        Log.d("2877", "doResumeUpdates---mFirstDayOfWeek---" + mFirstDayOfWeek);
        Log.d("2877", "doResumeUpdates---mShowWeekNumber---" + mShowWeekNumber);

        boolean prevHideDeclined = mHideDeclined;
        mHideDeclined = Utils.getHideDeclinedEvents(mContext);
        if (prevHideDeclined != mHideDeclined && mLoader != null) {
            mLoader.setSelection(updateWhere());
        }
        mDaysPerWeek = Utils.getDaysPerWeek(mContext);
        updateHeader();
        mTZUpdater.run();
        mTodayUpdater.run();
        mIsOnResumeGoTo = true;

        // update selectday if it's same to today
        Time today = new Time();
        today.set(System.currentTimeMillis());
        int julianDayToday = today.getJulianDay(today.toMillis(true), today.gmtoff);
        if (mIsSelectDayTodaySame) {
            mSelectedDay.setJulianDay(julianDayToday);
        }

        boolean isCurrPage = goTo(mSelectedDay.toMillis(true), false, true, false);
        if (Utils.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.READ_CALENDAR)) {
            if (this.isAdded()) {
                mLoader = (CursorLoader) getLoaderManager().restartLoader(LOAD_EVENT_ID, null, MonthFragment.this);
            }
        }
        if (DEBUG_ENABLE)
            Log.v(TAG, "doResumeUpdates, SelectedDay = " + mSelectedDay.toString());
        if (mViewMode.isMonthMode()) {
            mViewPager.post(mSelectDayRunnable.setSelectTime(mSelectedDay));
        }
    }

    @Override
    public boolean goTo(long time, boolean animate, boolean setSelected, boolean forceScroll) {
        if (DEBUG_ENABLE) {
            Log.v(TAG, "goTo:#" + mSelectedDay.toString());
        }
        if (time == -1) {
            Log.e(TAG, "time is invalid");
            return false;
        }
        // Set the selected day
        if (setSelected) {
            mSelectedDay.set(time);
            // change start by zyp for GMOS 615
            Time today = new Time();
            today.set(System.currentTimeMillis());
            int julianDayToday = today.getJulianDay(today.toMillis(true), today.gmtoff);
            int julianDaySelectedDay = mSelectedDay.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff);
            if (julianDayToday == julianDaySelectedDay) {
                mIsSelectDayTodaySame = true;
            } else {
                mIsSelectDayTodaySame = false;
            }
            // change end by zyp for GMOS 615
            mSelectedDay.normalize(true);
        }
        // If this view isn't returned yet we won't be able to load the lists
        // current position, so return after setting the selected day.
        if (!isResumed()) {
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "We're not visible yet");
            }
            return false;
        }
        mTempTime.set(time);
        int currPage = mViewPager.getCurrentItem();
        int y = mTempTime.year;
        int m = mTempTime.month;

        int monthPos = (y - CalendarController.MIN_CALENDAR_YEAR) * 12 + m;
        mViewPager.setCurrentItem(monthPos, animate);
        if (monthPos != currPage) {
            setMonthDisplayed(mTempTime, true);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        synchronized (mUpdateLoader) {
            if (null == data || data.isClosed()) {
                return;
            }
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Found " + data.getCount() + " cursor entries for uri " + mEventUri);
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
            Event.buildEventsFromCursor(events, data, mContext, mFirstLoadedJulianDay, mLastLoadedJulianDay);
            cEvents = data;
            handleDayEvent(cEvents);
            ((MonthViewPagerAdapter) mAdapter).setEvents(mFirstLoadedJulianDay,
                    mLastLoadedJulianDay - mFirstLoadedJulianDay + 1, events);
            if (mViewMode.isWeekMode()) {
                mViewSwitchController.updateEventToView();
            }
        }
    }

    @Override
    public void onResume() {
        mEnableAlmanacShow = Utils.isEnableAlmanacShow(mContext);
        /*
         * if(mWeatherController!=null){
         * mWeatherController.requestRefreshWeather(); }
         */
        if (DEBUG_ENABLE) {
            Log.d(TAG, "onResume, SelectedDay = " + mSelectedDay + ", mEnableAlmanacShow = " + mEnableAlmanacShow);
        }
        super.onResume();
        initHandler.sendEmptyMessage(INIT_WEATHER);
        registerProviderChangeReceiver();
    }

    private void registerProviderChangeReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_PROVIDER_CHANGED);
        filter.addDataScheme(ContentResolver.SCHEME_CONTENT);
        filter.addDataAuthority(new AuthorityEntry(mContext.getPackageName(), null));
        mContext.registerReceiver(mProviderChangeBroadcastReceiver, filter);
    }

    private void unregisterProviderChangeReceiver() {
        mContext.unregisterReceiver(mProviderChangeBroadcastReceiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        Time today = new Time();
        today.set(System.currentTimeMillis());
        int julianDayToday = today.getJulianDay(today.toMillis(true), today.gmtoff);
        int julianDaySelectedDay = mSelectedDay.getJulianDay(mSelectedDay.toMillis(true), mSelectedDay.gmtoff);
        if (julianDayToday == julianDaySelectedDay) {
            mIsSelectDayTodaySame = true;
        } else {
            mIsSelectDayTodaySame = false;
        }
        unregisterProviderChangeReceiver();
    }

    private void handleDayEvent(Cursor data) {
        agendaArrayList.clear();
        if ((data != null && !data.isClosed() && data.getCount() != 0)) {
            data.moveToPosition(-1);
            if (mTouchDay == null) {
                mTouchDay = System.currentTimeMillis();
            }
            Time touchTime = new Time(mSelectedDay);
            touchTime.set(mTouchDay);
            int julianToday = Time.getJulianDay(touchTime.toMillis(false), touchTime.gmtoff);
            if (mFirstLoadedJulianDay > julianToday || mLastLoadedJulianDay < julianToday) {
                mListViewAdapter.reBuildEventItem(agendaArrayList);
                return;
            }
            while (data.moveToNext()) {
                int startDay = data.getInt(EventEntity.PROJECTION_START_DAY_INDEX);
                int endDay = data.getInt(EventEntity.PROJECTION_END_DAY_INDEX);
                if (startDay <= julianToday && julianToday <= endDay) {
                    todaySchedule(data);
                }
            }
        }
        initHandler.post(new Runnable() {

            @Override
            public void run() {
                updateScheduleView();
            }
        });
        // updateScheduleView();
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
        return EventType.GO_TO | EventType.EVENTS_CHANGED | EventType.MONTH_GO_TO;
    }

    @Override
    public void handleEvent(final EventInfo event) {
        if (event.eventType == EventType.GO_TO) {
            mDesiredDay.set(event.selectedTime);
            mDesiredDay.normalize(true);
            event.selectedTime.switchTimezone(mDesiredDay.timezone);
            // boolean animateToday = (event.extraLong &
            // CalendarController.EXTRA_GOTO_TODAY) != 0;
            mHasTouchDayChange = true;
            Time MAXJULIANDAY_TIME = new Time();
            MAXJULIANDAY_TIME.setJulianDay(MAXJULIANDAY);
            if (event.selectedTime.toMillis(true) > MINTIME
                    && event.selectedTime.toMillis(true) < MAXJULIANDAY_TIME.toMillis(true)) {
                mTouchDay = preProcessTouchTime(event.selectedTime);
            }
            if (!mIsMonthViewMode) {
                mViewSwitchController.goTo(event.selectedTime);
            } else {
                if (mViewPager != null) {
                    mViewPager.removeCallbacks(mSelectMonthFirstDay);
                    mViewPager.removeCallbacks(mSelectDayRunnable);
                    boolean delayAnimation = goTo(event.selectedTime.toMillis(true), true, true, false);
                    if (delayAnimation) {
                        mViewPager.post(mSelectDayRunnable.setSelectTime(mSelectedDay));
                    }
                }
            }
        } else if (event.eventType == EventType.MONTH_GO_TO) {
            if (!mShouldLoad && mIsMonthViewMode) {
                return;
            }
            Time MAXJULIANDAY_TIME = new Time();
            MAXJULIANDAY_TIME.setJulianDay(MAXJULIANDAY);
            boolean isUserTouch = (event.extraLong & CalendarController.EXTRA_GOTO_USER_TOUCH) != 0 ? true : false;
            event.selectedTime.switchTimezone(mDesiredDay.timezone);
            Log.v(TAG, "event.selectedTime==" + event.selectedTime.toMillis(true));
            if (event.selectedTime.toMillis(true) > MINTIME
                    && event.selectedTime.toMillis(true) < MAXJULIANDAY_TIME.toMillis(true)) {
                mTouchDay = event.selectedTime.toMillis(true);
            }
            /*
             * if(mWeatherController!=null){ Time t = new Time(mSelectedDay);
             * t.set(event.selectedTime);
             * mWeatherController.onTouchDayChange(t); }
             */
            if (mViewMode.isFullMonthMode() && isUserTouch) {
                enterWeekMode();
            }
            if (mIsMonthViewMode && mTouchDay > MINTIME && mTouchDay < MAXJULIANDAY_TIME.toMillis(true)
                    && !isTouchTimeInCurrMonth()) {
                mHasTouchDayChange = true;
                goTo(mTouchDay, true, false, false);
            }
            if (mTouchDay > MINTIME && mTouchDay < MAXJULIANDAY_TIME.toMillis(true)) {
                mSelectedDay.set(mTouchDay);
            }
            Log.v(TAG, "lxg rjcmSelectedDay_mTouchDay " + mTouchDay);
            if (mTouchDay > MINTIME && mTouchDay < MAXJULIANDAY_TIME.toMillis(true)) {
                mDesiredDay.set(mTouchDay);
            }
            // if(isTounchDayInUpdateRange(mTouchDay)){
            if (mTouchDay > MINTIME && mTouchDay < MAXJULIANDAY_TIME.toMillis(true)) {
                setTouchDayScheduleLayout(mTouchDay);
            }
            Log.d("liao", "MonthFragment->handleEvent()->sendEvent");
            // CalendarController.getInstance(mContext).sendEvent(this,
            // EventType.UPDATE_TITLE, mSelectedDay, mSelectedDay,
            // mSelectedDay, -1, ViewType.CURRENT,
            // DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY |
            // DateUtils.FORMAT_SHOW_YEAR, null,
            // null);
        } else if (event.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }

    private long preProcessTouchTime(Time t) {
        Time touch = new Time(mSelectedDay.timezone);
        touch.set(t.monthDay, t.month, t.year);
        return touch.toMillis(true);
    }

    @Override
    protected void setMonthDisplayed(Time time, boolean updateHighlight) {
        super.setMonthDisplayed(time, updateHighlight);
        if (!mIsMiniMonth) {
            boolean useSelected = false;
            if (time.year == mDesiredDay.year && time.month == mDesiredDay.month) {
                mSelectedDay.set(mDesiredDay);
                mAdapter.setSelectedDay(mDesiredDay);
                useSelected = true;
            } else {
                mSelectedDay.set(time);
                mAdapter.setSelectedDay(time);
            }
            CalendarController controller = CalendarController.getInstance(mContext);
            if (mSelectedDay.minute >= 30) {
                mSelectedDay.minute = 30;
            } else {
                mSelectedDay.minute = 0;
            }
            long newTime = mSelectedDay.normalize(true);
            if (newTime != controller.getTime() && mUserScrolled) {
                long offset = useSelected ? 0 : DateUtils.WEEK_IN_MILLIS * mNumWeeks / 3;
                controller.setTime(newTime + offset);
            }
            // Log.d("liao", "MonthFragment->handleEvent()->sendEvent");
            // controller.sendEvent(this, EventType.UPDATE_TITLE, time, time,
            // time, -1, ViewType.CURRENT,
            // DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_MONTH_DAY |
            // DateUtils.FORMAT_SHOW_YEAR, null,
            // null);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        synchronized (mUpdateLoader) {
            if (state != ViewPager.SCROLL_STATE_IDLE) {
                mViewPager.removeCallbacks(mSelectMonthFirstDay);
                mViewPager.removeCallbacks(mSelectDayRunnable);
                mShouldLoad = false;
                stopLoader();
            } else {
                mHandler.removeCallbacks(mUpdateLoader);
                mViewPager.removeCallbacks(mSelectMonthFirstDay);
                mShouldLoad = true;
                mHandler.postDelayed(mUpdateLoader, LOADER_DELAY);
                if (misPageChanged) {
                    mViewPager.post(mSelectMonthFirstDay);
                    misPageChanged = false;
                }
            }
        }
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            mUserScrolled = true;
        }
    }

    public class SelectMonthFirstDay implements Runnable {
        @Override
        public void run() {
            if (mIsMonthViewMode && mShouldLoad) {
                if (mHasTouchDayChange && isTouchTimeInCurrMonth()) {
                    mHasTouchDayChange = false;
                    mAdapter.setSelectMonthDay(mTouchDay);
                } else {
                    mAdapter.setSelectMonthFristDay();
                }
            }
        }
    }

    public boolean isTouchTimeInCurrMonth() {
        Time t = new Time(mSelectedDay);
        t.set(mTouchDay);
        t.normalize(true);
        int y = t.year;
        int m = t.month;
        int monthPos = (y - CalendarController.MIN_CALENDAR_YEAR) * 12 + m;
        return monthPos == mViewPager.getCurrentItem();
    }

    @Override
    public void onPageSelected(int postion) {
        misPageChanged = true;
        super.onPageSelected(postion);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onMonthChange(MonthWeekEventsView v, Time now) {
        goTo(now.toMillis(true), true, false, false);
    }

    @Override
    public void onClickDayChange(Time touchDay, final int week, final int clickIndex) {
        mViewPager.postDelayed(new Runnable() {

            @Override
            public void run() {
                mAdapter.setClickedDay(week, clickIndex);
            }
        }, 10);

    }

    // 20150929 add to check should hide Agenda List by zhangxiaojun
    public void isHideAgendaList(boolean isHide) {
        switchMonthMode();
    }

    public void switchMonthMode() {
        if (mViewMode.isHalfMonthMode()) {
            enterFullMonthMode();
            mViewMode.enterFullMonthMode();
        } else if (mViewMode.isFullMonthMode()) {
            enterHalfMonthMode();
            mViewMode.enterHalfMonthMode();
        }
    }

    public void enterFullMonthMode() {
        if (mIsMonthViewMode) {
            int height = eventLayout.getMeasuredHeight();
            int totalHeight = height + mMonthWeekMainLayout.getItemHeight();
            int newItemHeight = totalHeight / 6;
            enterMonthModeInternal(newItemHeight, totalHeight);
        }
    }

    public void enterHalfMonthMode() {
        if (mViewMode.isFullMonthMode()) {
            int halfModeHeight = (int) mContext.getResources().getDimension(R.dimen.month_row_height);// dp
            /*
             * halfModeHeight = (int) TypedValue.applyDimension(
             * TypedValue.COMPLEX_UNIT_DIP, halfModeHeight, mContext
             * .getResources().getDisplayMetrics());
             */
            enterMonthModeInternal(halfModeHeight, halfModeHeight * 6);
        }
    }

    private void enterMonthModeInternal(int newHeight, int newTotalHeight) {
        LayoutParams params = mViewPager.getLayoutParams();
        mAdapter.setViewItemHeight(newHeight);
        mMonthWeekMainLayout.updateItemHeight(newHeight);
        mMonthWeekSwitcher.setDistance(mMonthWeekMainLayout.getMaxTranslation());
        params.height = newTotalHeight;
        mViewPager.setLayoutParams(params);
        LayoutParams switcherParam = mViewSwitcher.getLayoutParams();
        switcherParam.height = newHeight;
        mViewSwitcher.setLayoutParams(switcherParam);
    }

    public void enterWeekMode() {
        if (isTouchTimeInCurrMonth()) {
            mMonthWeekSwitcher.gotoAgendaTop();
            mViewMode.enterWeekMode();
        }
    }

    @Override
    public void onViewModeChanged(int newMode) {
        switch (newMode) {
        case ViewMode.HALF_MONTH:
            mIsMonthViewMode = true;
            break;
        case ViewMode.FULL_MONTH:
            mIsMonthViewMode = true;
            break;
        case ViewMode.FULL_MONTH_WEEK:
        case ViewMode.HALF_MONTH_WEEK:
        default:
            mIsMonthViewMode = false;
            break;
        }
    }

    @Override
    public void onSizeChange(int height) {
        mMonthWeekSwitcher.setDistance(mMonthWeekMainLayout.getMaxTranslation());
        mAdapter.setViewItemHeight(mMonthWeekMainLayout.getItemHeight());
        if (mViewMode.isWeekMode()) {
            mMonthWeekSwitcher.gotoAgendaTop();
        }
        mViewSwitchController.updateCurrViewHeight(mMonthWeekMainLayout.getItemHeight());
        LayoutParams switcherParam = mViewSwitcher.getLayoutParams();
        switcherParam.height = mMonthWeekMainLayout.getItemHeight();
        mViewSwitcher.setLayoutParams(switcherParam);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cEvents = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        // TODO Auto-generated method stub
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                district = amapLocation.getDistrict();
                Log.e(TAG, WeatherItem.DISTRICT + " : " + district
                        + ", longitude: " + new Double(amapLocation.getLongitude()).toString()
                        + ", latitude: " + new Double(amapLocation.getLatitude()).toString());
                sendMessage(INIT_WEATHER_DISTRICT, district);
                String url2 = WeatherUtils.getLocationUrl(Secret.APIKey,
                        Secret.Secret,
                        "zh-cn",
                        new Double(amapLocation.getLongitude()).toString(),
                        new Double(amapLocation.getLatitude()).toString(),
                        district);
                Log.d(TAG, "Weather Location Request URL: " + url2);
                OkHttpClientManager.getInstance().requestLocationText(url2, new LocationTextCallBack() {
                    @Override
                    public void onLocationReqSuccess(List<LocationText> result) {
                        for(LocationText item : result) {
                            String key = item.getKey();
                            //requestDataByKey(key);
                            String currentConditions = WeatherUtils.generate_url("currentconditions",
                                    "", key, Secret.APIKey, Secret.Secret,
                                    "zh-cn");
                            Log.e(TAG, "Weather Request Data URL: " + currentConditions);
                            OkHttpClientManager.getInstance().requestCurrentCondition(currentConditions,
                                    new CurrentConditionCallBack() {

                                @Override
                                public void onLocationReqSuccess(List<CurrentCondition> result) {
                                    // TODO Auto-generated method stub
                                    CurrentCondition mCurrentCondition = result.get(0);
                                    sendMessage(INIT_WEATHER_DATA, mCurrentCondition);
                                }

                                @Override
                                public void onLocationReqFailed(String errorMsg) {
                                    // TODO Auto-generated method stub
                                }
                            });

                            final String airQuality = WeatherUtils.generate_url("airquality",
                                    "forecast5day",
                                    key, Secret.APIKey, Secret.Secret,
                                    mLocale.getLanguage());
                            Log.d(TAG, "Weather Air Request URL: " + airQuality);
                            OkHttpClientManager.getInstance().requestGetByAsyn(airQuality,
                                    new ResultCallback<AirCondition>() {

                                @Override
                                public void onReqSuccess(AirCondition result) {
                                    sendMessage(INIT_WEATHER_AIR, result);
                                }

                                @Override
                                public void onReqFailed(String errorMsg) { }

                            }, AirCondition.class);
                        }
                    }

                    @Override
                    public void onLocationReqFailed(String errorMsg) {
                        Log.e(TAG, "weather request error: " + errorMsg);
                    }
                });
            } else {
                Log.e(TAG, "location Error, ErrCode:" + amapLocation.getErrorCode()
                        + ", errInfo:" + amapLocation.getErrorInfo());
                checkRequiredPermission(getMyActivity());
            }
        }
    }

    private void sendMessage(int what, Object obj) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = obj;
        initHandler.sendMessage(msg);
    }

    void handleWeatherRequestData(CurrentCondition condition) {
        Log.d(TAG, "Weather Success weather_data: "
                + condition.getLocalSource().getName());
        String weatherCode = condition.getLocalSource().getWeatherCode();
        int index = Integer.parseInt(weatherCode);
        int iconIndex = CalendarApplication.weatherIconList.get(index);
        String weather = getMyActivity().getString(CalendarApplication.weatherText.get(index));
        int windSpeed = (int) condition.getWind().getSpeed().getMetric().getValue();
        String direction = condition.getWind().getDirection().getLocalized();
        if (mLocale.getLanguage().endsWith("en")) {
            direction = condition.getWind().getDirection().getEnglish();
        } else {
            if (TextUtils.equals(direction, "W") ) {
                direction = getMyActivity().getString(R.string.wind_west);
            } else if (TextUtils.equals(direction, "S")) {
                direction = getMyActivity().getString(R.string.wind_south);
            } else if (TextUtils.equals(direction, "E")) {
                direction = getMyActivity().getString(R.string.wind_east);
            } else if (TextUtils.equals(direction, "N") ) {
                direction = getMyActivity().getString(R.string.wind_north);
            }
        }
        String wind = direction + getMyActivity().getString(R.string.wind) + WeatherUtils.getWindLevel(windSpeed);
        String wet = getMyActivity().getString(R.string.wet)
                + MessageFormat.format("{0}%", String.valueOf(condition.getRelativeHumidity()));
        CurrentCondition.TemperatureSummaryBean.Past12HourRangeBean past12HourRange;
        String temperature = "";
        String metric = (String) SharedPreferencesUtils.get(SharedPreferencesUtils.TEMPERATURE_CHANGE,
                Constants.TEMPERATURE_CHANGE, "℃");
        try {
            past12HourRange = condition.getTemperatureSummary().getPast12HourRange();

            if ("℉".equals(metric)) {
                temperature = (int) (past12HourRange.getMaximum().getImperial().getValue() + 0.5) + "℃/"
                        + (int) (past12HourRange.getMinimum().getImperial().getValue() + 0.5) + "℃";
            } else {
                temperature = (int) (past12HourRange.getMaximum().getMetric().getValue() + 0.5) + "℃/"
                        + (int) (past12HourRange.getMinimum().getMetric().getValue() + 0.5) + "℃";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mWeatherPreference.edit()
                .putInt(WeatherItem.ICON, iconIndex)
                .putString(WeatherItem.WEATHER, weather)
                .putString(WeatherItem.WIND, wind)
                .putString(WeatherItem.WET, wet)
                .putString(WeatherItem.TEMP, temperature)
                .commit();
    }

    private void handleWeatherRequestAir(AirCondition result) {
        Log.d(TAG, "Weather Success Air");
        int index = result.getIndex();
        String pollutionLevel = WeatherUtils.getPollutionLevel(index);
        mWeatherPreference.edit()
                .putString(WeatherItem.AIR, pollutionLevel)
                .putInt(WeatherItem.AIRCONDITION, index)
                .commit();
    }

    private synchronized void updateAdapterWeatherData() {
        EventEntity entityspToList = new EventEntity(null);
        entityspToList.district = mWeatherPreference.getString(WeatherItem.DISTRICT,getMyActivity().getString(R.string.default_district));
        entityspToList.weatherIcon = mWeatherPreference.getInt(WeatherItem.ICON, R.drawable.sunny);
        entityspToList.temp = mWeatherPreference.getString(WeatherItem.TEMP, getMyActivity().getString(R.string.default_temp));
        entityspToList.weather = mWeatherPreference.getString(WeatherItem.WEATHER, getMyActivity().getString(R.string.default_weather));
        entityspToList.air = mWeatherPreference.getString(WeatherItem.AIR, getMyActivity().getString(R.string.default_air));
        entityspToList.wind = mWeatherPreference.getString(WeatherItem.WIND, getMyActivity().getString(R.string.default_wind));
        entityspToList.wet = mWeatherPreference.getString(WeatherItem.WET, getMyActivity().getString(R.string.default_wet));
        entityspToList.airCondition = new Integer(mWeatherPreference.getInt(WeatherItem.AIRCONDITION, 100)).toString();
        boolean isAdd = false;
        if(agendaArrayList.size() > 0) {
            for (int i = 0; i < agendaArrayList.size(); i++) {
                String lunarDate = agendaArrayList.get(i).lunarDate;
                String title = agendaArrayList.get(i).title;
                if(lunarDate != null || title != null) {
                    continue;
                } else {
                    isAdd = true;
                    agendaArrayList.set(i, entityspToList);
                }
            }
        } else {
            agendaArrayList.add(entityspToList);
            isAdd = true;
        }
        if(isAdd == false) {
            agendaArrayList.add(entityspToList);
        }
        boolean showHuangli = mSettingPreference.getBoolean("isshowalmanac", true);
        boolean isShowWeatherNow =
                mSettingPreference.getBoolean("isshowweather", true) && mIsSelectDayTodaySame;
        mListViewAdapter.setShowState(showHuangli, isShowWeatherNow, agendaArrayList);
    }

    private static final String[] permissionsArray = new String[] { Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION };

    private List<String> permissionsList = new ArrayList<String>();

    private void checkRequiredPermission(final Activity activity) {
        for (String permission : permissionsArray) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
            }
        }
        ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    private void requestDataByCityName(String district) {
        // TODO Auto-generated method stub
        String url2 = WeatherUtils.getLocationUrl(Secret.APIKey, Secret.Secret, "zh-cn", "39.08", "118.22", district);
        Log.e("fushuo", "--->" + url2);
        OkHttpClientManager.getInstance().requestLocationText(url2, new LocationTextCallBack() {
            @Override
            public void onLocationReqSuccess(List<LocationText> result) {
                LocationText locationText = result.get(0);
                String key = locationText.getKey();
                requestDataByKey(key);
                Log.e("fushuo", "key=---->" + key);
            }

            @Override
            public void onLocationReqFailed(String errorMsg) {
                Log.e("fushuo", "---->" + errorMsg);
            }
        });
    }

    private void requestDataByKey(String key) {
        // TODO Auto-generated method stub

        // TODO Auto-generated method stub
        String currentConditions = WeatherUtils.generate_url("currentconditions", "", key, Secret.APIKey, Secret.Secret,
                "zh-cn");
        Log.e("fushuo", currentConditions);
        OkHttpClientManager.getInstance().requestCurrentCondition(currentConditions, new CurrentConditionCallBack() {

            @Override
            public void onLocationReqSuccess(List<CurrentCondition> result) {
                // TODO Auto-generated method stub
                CurrentCondition mCurrentCondition = result.get(0);
                SharedPreferences sp = getMyActivity().getSharedPreferences("weather", Context.MODE_PRIVATE);
                Log.e("fushuo11111", mCurrentCondition.getLocalSource().getWeatherCode() + "" + "pic--->"
                        + CalendarApplication.weatherIconList.get(mCurrentCondition.getWeatherIcon() - 1));
                // iv_weather
                // .setImageResource(CalendarApplication.weatherIconList.get(0));
                int index = Integer.parseInt(mCurrentCondition.getLocalSource().getWeatherCode());
                Log.e("fushuo", "index=" + index);
                iv_weather.setImageResource(CalendarApplication.weatherIconList.get(index));
                // iv_weather.setImageDrawable(getResources().getDrawable(CalendarApplication.weatherIconList.get(mCurrentCondition.getWeatherIcon()-1)));
                // Log.e("fushuo11111","fushuo");
                // iv_weather.setImageResource(R.drawable.gome_ic_calendar_sunny);
                // Log.e("fushuo11111","fushuo222");
                sp.edit().putInt("icon", CalendarApplication.weatherIconList.get(index)).commit();
                String weather = getMyActivity().getString(CalendarApplication.weatherText.get(index));
                tv_weather.setText(weather);
                sp.edit().putString("weather", weather).commit();
                int windSpeed = (int) mCurrentCondition.getWind().getSpeed().getMetric().getValue();
                String direction = mCurrentCondition.getWind().getDirection().getLocalized();
                Log.e("fushuo", "wind direction--->" + mCurrentCondition.getWind().getDirection().getLocalized());
                Log.e("fushuo", "wind--->" + direction);
                Locale locale = mContext.getResources().getConfiguration().locale;
                String language = locale.getLanguage();
                boolean isEnglish = false;

                if (language.endsWith("en")) {
                    isEnglish = true;
                }
                if (TextUtils.equals(direction, "W") && !isEnglish) {
                    direction = getMyActivity().getString(R.string.wind_west);
                } else if (TextUtils.equals(direction, "S") && !isEnglish) {
                    direction = getMyActivity().getString(R.string.wind_south);
                } else if (TextUtils.equals(direction, "E") && !isEnglish) {
                    direction = getMyActivity().getString(R.string.wind_east);
                } else if (TextUtils.equals(direction, "N") && !isEnglish) {
                    direction = getMyActivity().getString(R.string.wind_north);
                }
                if (isEnglish) {
                    direction = mCurrentCondition.getWind().getDirection().getEnglish();
                    tv_wind.setText(direction + " " + getMyActivity().getString(R.string.wind) + " "
                            + WeatherUtils.getWindLevel(windSpeed));
                } else {
                    tv_wind.setText(direction + getMyActivity().getString(R.string.wind)
                            + WeatherUtils.getWindLevel(windSpeed));
                }

                sp.edit().putString("wind",
                        direction + getMyActivity().getString(R.string.wind) + WeatherUtils.getWindLevel(windSpeed))
                        .commit();
                CurrentCondition.TemperatureBean temperature = mCurrentCondition.getTemperature();
                double mCurrentTem = temperature.getMetric().getValue();
                tv_wet.setText(getMyActivity().getString(R.string.wet)
                        + MessageFormat.format("{0}%", String.valueOf(mCurrentCondition.getRelativeHumidity())));
                sp.edit()
                        .putString("wet", getMyActivity().getString(R.string.wet)
                                + MessageFormat.format("{0}%", String.valueOf(mCurrentCondition.getRelativeHumidity())))
                        .commit();
                CurrentCondition.TemperatureSummaryBean.Past12HourRangeBean past12HourRange;
                // set temperature
                String metric = (String) SharedPreferencesUtils.get(SharedPreferencesUtils.TEMPERATURE_CHANGE,
                        Constants.TEMPERATURE_CHANGE, "℃");
                String realTemp = String
                        .valueOf((int) (mCurrentCondition.getRealFeelTemperature().getMetric().getValue() + 0.5));
                try {
                    past12HourRange = mCurrentCondition.getTemperatureSummary().getPast12HourRange();

                    if ("℉".equals(metric)) {
                        // mTvCurrentTem.setText(String.valueOf((int)
                        // (temperature.getImperial().getValue() + 0.5)));
                        // mTvDegree.setText("°");
                        // mTvFeelLike.setText(String.format(String.valueOf((int)
                        // (mCurrentCondition.getRealFeelTemperature().getImperial().getValue()
                        // + 0.5)), "°"));
                        tv_temp.setText((int) (past12HourRange.getMaximum().getImperial().getValue() + 0.5) + "℃/"
                                + (int) (past12HourRange.getMinimum().getImperial().getValue() + 0.5) + "℃");
                        sp.edit().putString("temp",
                                (int) (past12HourRange.getMaximum().getImperial().getValue() + 0.5) + "℃/"
                                        + (int) (past12HourRange.getMinimum().getImperial().getValue() + 0.5) + "℃")
                                .commit();
                    } else {
                        // mTvCurrentTem.setText(String.valueOf((int)
                        // (mCurrentTem + 0.5)));
                        // mTvDegree.setText("°");
                        // mTvFeelLike.setText(String.format(realTemp, "°"));
                        Log.d("liaozhenbin", "temp: " + (past12HourRange.getMaximum().getMetric().getValue() + 0.5));
                        tv_temp.setText((int) (past12HourRange.getMaximum().getMetric().getValue() + 0.5) + "℃/"
                                + (int) (past12HourRange.getMinimum().getMetric().getValue() + 0.5) + "℃");
                        sp.edit().putString("temp",
                                (int) (past12HourRange.getMaximum().getMetric().getValue() + 0.5) + "℃/"
                                        + (int) (past12HourRange.getMinimum().getMetric().getValue() + 0.5) + "℃")
                                .commit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLocationReqFailed(String errorMsg) {
                // TODO Auto-generated method stub

            }

        });
        // air condition
        String airQuality = WeatherUtils.generate_url("airquality", "forecast5day", key, Secret.APIKey, Secret.Secret,
                "zh-cn");
        OkHttpClientManager.getInstance().requestGetByAsyn(airQuality, new ResultCallback<AirCondition>() {
            @Override
            public void onReqSuccess(AirCondition result) {
                int index = result.getIndex();

                String pollutionLevel = WeatherUtils.getPollutionLevel(index);
                tv_air.setText(pollutionLevel);
                // change by zyp for GMOS-5652
                SharedPreferences sp = MonthFragment.this.getMyActivity().getSharedPreferences("weather",
                        Context.MODE_PRIVATE);
                sp.edit().putString("air", pollutionLevel).commit();
                tv_air_condition.setText("" + index);
                sp.edit().putInt("air_condition", index).commit();
                Log.e("fushuo123567", pollutionLevel);
            }

            @Override
            public void onReqFailed(String errorMsg) {

            }
        }, AirCondition.class);

    }

    public void updateNearbyViewPager() {
        int viewPagerNow = mViewPager.getCurrentItem();
        if (viewPagerNow < mAdapter.getCount()) {
            View mView;
            GridView mGridView = (GridView) mViewPager.findViewWithTag(viewPagerNow + 1);
            if (null != mGridView) {
                for (int i = 0; i < mGridView.getCount(); i++) {
                    mView = (MonthWeekEventsView) mGridView.getChildAt(i);
                    // change by zyp for GMOS-5644
                    if (null != mView) {
                        mView.requestLayout();
                    }

                }
            }
        }

        if (viewPagerNow > 0) {
            View mView;
            GridView mGridView = (GridView) mViewPager.findViewWithTag(viewPagerNow - 1);
            if (null != mGridView) {
                for (int i = 0; i < mGridView.getCount(); i++) {
                    mView = (MonthWeekEventsView) mGridView.getChildAt(i);
                    // change by zyp for GMOS-5644
                    if (null != mView) {
                        mView.requestLayout();
                    }
                }
            }
        }
        Log.i("zhang", "" + itemHeight);

    }

    // get attach return Activity
    public Activity getMyActivity() {
        return mActivity;
    }
    
    public int convertDp2Px(int old) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, old,
                getContext().getResources().getDisplayMetrics());
    }
}
