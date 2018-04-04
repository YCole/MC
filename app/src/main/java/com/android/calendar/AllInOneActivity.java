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

package com.android.calendar;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.Attendees.ATTENDEE_STATUS;
import gm.widget.GomeSwitch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Outline;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SearchView;
import android.widget.SearchView.OnSuggestionListener;
import android.widget.TextView;

import com.android.calendar.CalendarController.EventHandler;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.CalendarViewAdapter.OnTimeCurrentCallBack;
import com.android.calendar.agenda.AgendaFragment;
import com.android.calendar.event.ScheduleActivity;
import com.android.calendar.event.ScheduleDetailsActivity;
import com.android.calendar.month.HolidayAndTermActivity;
import com.android.calendar.selectcalendars.SelectVisibleCalendarsFragment;
import com.android.calendar.utils.YearUtils;
import com.android.calendar.view.YearPagerFragment;
import com.apkfuns.logutils.LogUtils;
import com.gome.gmtimepicker.view.GMTimePicker;
import com.gm.internal.menu.FloatActionMenuView;
import com.gm.internal.menu.FloatActionMenuView.OnFloatActionMenuSelectedListener;
import com.google.gson.Gson;
import java.io.IOException;
import com.hct.calendar.domain.Holiday;
import com.hct.calendar.domain.Holiday.DataBean.InnerBean;
import com.hct.calendar.http.NetAccess;
import com.hct.calendar.month.MonthFragment;
import com.hct.calendar.month.ViewMode;
import com.hct.calendar.month.ViewMode.ModeChangeListener;
import com.hct.calendar.ui.OneLineCheckboxLayout;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.gios.widget.DatePickerDialogHCT;
import com.hct.gios.widget.DatePickerHCT;

@SuppressLint("NewApi")
public class AllInOneActivity extends AbstractCalendarActivity implements
        EventHandler,
        OnSharedPreferenceChangeListener,
        SearchView.OnQueryTextListener, // ActionBar.TabListener,
        ActionBar.OnNavigationListener, OnSuggestionListener,
        ModeChangeListener {
    private static final String TAG = "AllInOneActivity";
    private static final String RO_BUILD_DISPLAY_INTID = SystemProperties
            .get("ro.build.display.intid"); // added
                                            // //
                                            // CMCC
    private static final boolean DEBUG = false; // change to false when up.
    private static final String EVENT_INFO_FRAGMENT_TAG = "EventInfoFragment";
    private static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";
    private static final String BUNDLE_KEY_RESTORE_VIEW = "key_restore_view";
    private static final String BUNDLE_KEY_CHECK_ACCOUNTS = "key_check_for_accounts";
    private static final int HANDLER_KEY = 0;

    // Indices of buttons for the drop down menu (tabs replacement)
    // Must match the strings in the array buttons_list in arrays.xml and the
    // OnNavigationListener
    private static final int BUTTON_DAY_INDEX = 0;
    private static final int BUTTON_WEEK_INDEX = 1;
    private static final int BUTTON_MONTH_INDEX = 2;
    private static final int BUTTON_AGENDA_INDEX = 3;

    private static final long MAXDATE = 2145887999; // 2037/12/31 23:59:59
    private static final long MINDATE = 0;

    private CalendarController mController;
    private static boolean mIsMultipane;
    private static boolean mIsTabletConfig;
    private static boolean mShowAgendaWithMonth;
    private static boolean mShowEventDetailsWithAgenda;
    private boolean mOnSaveInstanceStateCalled = false;
    private boolean mBackToPreviousView = false;
    // private ContentResolver mContentResolver;
    private int mPreviousView;
    private int mCurrentView;
    private boolean mPaused = true;
    private boolean mUpdateOnResume = false;
    private boolean mHideControls = false;
    private boolean mShowSideViews = true;
    private boolean mShowWeekNum = false;
    private boolean mFullSizeNoAgenda = true;
    private TextView mHomeTime;
    private TextView mDateRange;
    private TextView mWeekTextView;
    private View mMiniMonth;
    private View mCalendarsList;
    private View mMiniMonthContainer;
    private View mSecondaryPane;
    private String mTimeZone;
    private boolean mShowCalendarControls;
    private boolean mShowEventInfoFullScreenAgenda;
    private boolean mShowEventInfoFullScreen;
    private int mWeekNum;
    private int mCalendarControlsAnimationTime;
    private int mControlsAnimateWidth;
    private int mControlsAnimateHeight;

    private long mViewEventId = -1;
    private long mIntentEventStartMillis = -1;
    private long mIntentEventEndMillis = -1;
    private int mIntentAttendeeResponse = Attendees.ATTENDEE_STATUS_NONE;
    private boolean mIntentAllDay = false;

    // Action bar and Navigation bar (left side of Action bar)
    private ActionBar mActionBar;
    private ActionBar.Tab mDayTab;
    private ActionBar.Tab mWeekTab;
    private ActionBar.Tab mMonthTab;
    private ActionBar.Tab mAgendaTab;
    private SearchView mSearchView;
    private MenuItem mSearchMenu;
    private MenuItem mControlsMenu;
    private MenuItem mFullSizeMenu;
    private Menu mOptionsMenu;
    private CalendarViewAdapter mActionBarMenuSpinnerAdapter;
    private QueryHandler mHandler;
    private boolean mCheckForAccounts = true;
    // liaozhenbin
    // private ImageView mYearImage;
    private TextView mYearTextView;
    // private TextView mMonthTextView;
    private ImageView mMonthImageView;
    private ViewGroup mMonthYearTitle;
    private ViewGroup mRootViewGroup;
    // private TextView mMonthView;

    private String mHideString;
    private String mShowString;
    private TextView tv_pop;
    // HCT_MODIFY lixiange MF3.0
    private Context mContext;
    private boolean dateSetOkClicked = false;
    public static Time mSelectDate = new Time();
    private MenuItem mHolidayAndTermMenu;
    private String mHolidayAndTermString;
    private boolean mShowHoliday;
    private MenuItem mEventsDeleteMenu;
    private FrameLayout floatingButtonContainer;
    public static final float FAB_DEPTH = 20f;
    // HCT_MODIFY lixiange MF3.0

    int mOrientation;

    private static boolean mhasCalendarPermission = false;
    // Params for animating the controls on the right
    private LayoutParams mControlsParams;
    // 20160505 delete MenuExtensions for launcher Time by zhxj
    // private AllInOneMenuExtensionsInterface mExtensions = ExtensionsFactory
    // .getAllInOneMenuExtensions();
    private boolean mFullSizeMenuVisible = true;

    private SharedPreferences sPreferences;

    private final AnimatorListener mSlideAnimationDoneListener = new AnimatorListener() {

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationEnd(android.animation.Animator animation) {
            int visibility = mShowSideViews ? View.VISIBLE : View.GONE;
            mMiniMonth.setVisibility(visibility);
            mCalendarsList.setVisibility(visibility);
            mMiniMonthContainer.setVisibility(visibility);
        }

        @Override
        public void onAnimationRepeat(android.animation.Animator animation) {
        }

        @Override
        public void onAnimationStart(android.animation.Animator animation) {
        }
    };

    private class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            mCheckForAccounts = false;
            try {
                // If the query didn't return a cursor for some reason return
                if (cursor == null || cursor.getCount() > 0 || isFinishing()) {
                    return;
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            Bundle options = new Bundle();
            options.putCharSequence("introMessage",
                    getResources().getString(R.string.create_an_account_desc));
            options.putBoolean("allowSkip", true);

            AccountManager am = AccountManager.get(AllInOneActivity.this);
            am.addAccount("com.google", CalendarContract.AUTHORITY, null,
                    options, AllInOneActivity.this,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            if (future.isCancelled()) {
                                return;
                            }
                            try {
                                Bundle result = future.getResult();
                                boolean setupSkipped = result
                                        .getBoolean("setupSkipped");

                                if (setupSkipped) {
                                    Utils.setSharedPreference(
                                            AllInOneActivity.this,
                                            GeneralPreferences.KEY_SKIP_SETUP,
                                            true);
                                }

                            } catch (Exception ignore) {
                                // The account creation process was canceled
                            }
                        }
                    }, null);
        }
    }

    private final Runnable mHomeTimeUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(AllInOneActivity.this,
                    mHomeTimeUpdater);
            updateSecondaryTitleFields(-1);
            AllInOneActivity.this.invalidateOptionsMenu();
            Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
            if (DEBUG)
                Log.d(TAG, " HomeTimeUpdater run end.");
        }
    };

    // runs every midnight/time changes and refreshes the today icon
    private final Runnable mTimeChangesUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(AllInOneActivity.this,
                    mHomeTimeUpdater);
            AllInOneActivity.this.invalidateOptionsMenu();
            Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
            if (DEBUG)
                Log.d(TAG, " TimeChangesUpdater run end.");
        }
    };

    // Create an observer so that we can update the views whenever a
    // Calendar event changes.
    // private final ContentObserver mObserver = new ContentObserver(new
    // Handler()) {
    // @Override
    // public boolean deliverSelfNotifications() {
    // return true;
    // }
    //
    // @Override
    // public void onChange(boolean selfChange) {
    // eventsChanged();
    // }
    // };

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        final Window window = getWindow();
        window.setStatusBarColor(0xFFFFFFFF);
    };

    BroadcastReceiver mCalIntentReceiver;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.setIntent(intent);
        String action = intent.getAction();
        if (DEBUG) {
            Log.d(TAG, "New intent received " + intent.toString());
        }

        // Don't change the date if we're just returning to the app's home
        long millis = -1;
        if (Intent.ACTION_VIEW.equals(action)
                && !intent.getBooleanExtra(Utils.INTENT_KEY_HOME, false)) {
            millis = parseViewAction(intent);

            if (millis == -1) {
                millis = Utils.timeFromIntentInMillis(intent, true);
            }
            if (millis != -1 && mViewEventId == -1 && mController != null) {

                Time time = new Time(mTimeZone);
                time.set(millis);
                time.normalize(true);
                long extras = CalendarController.EXTRA_GOTO_TIME
                        | CalendarController.EXTRA_GOTO_DATE;

                mController.sendEvent(this, EventType.GO_TO, time, null, time,
                        -1, ViewType.CURRENT, extras, null, null);
            }
        } else if (Intent.ACTION_MAIN.equals(action)
                && "widget".equals(intent.getStringExtra("intent_origin"))) {
            millis = parseTimefromSharePref(mContext);
            if (millis != -1 && mController != null) {
                Time time = new Time(mTimeZone);
                time.set(millis);
                time.normalize(true);
                long extras = CalendarController.EXTRA_GOTO_TIME
                        | CalendarController.EXTRA_GOTO_DATE;
                mController.sendEvent(this, EventType.GO_TO, time, null, time,
                        -1, ViewType.CURRENT, extras, null, null);
            }
        }
        int oldViewType = mController.getViewType();
        int newViewType = Utils.getViewTypeFromIntentAndSharedPref(this);
        if (oldViewType != newViewType) {
            mController.setViewType(newViewType);
            processCreate(millis, newViewType, null);
            Log.d(TAG, "onNewIntent, oldViewType = " + oldViewType
                    + ", newViewType= " + newViewType + ", timeMillis = "
                    + millis);
        }
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.DISPLAY_FLOATING_BUTTON".equals(action)) {
                if (floatingButtonContainer != null) {
                    floatingButtonContainer.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        /*
         * if (Utils.getSharedPreference(this, OtherPreferences.KEY_OTHER_1,
         * false)) { setTheme(R.style.CalendarTheme_WithActionBarWallpaper); }
         */
        super.onCreate(icicle);
        long time1 = System.currentTimeMillis();
        // setTheme(R.style.Theme_Calendar);
        // setActionBarContentColor(getResources().getColor(R.color.cale_actionbar_icon_color),
        // getResources().getColor(R.color.cale_actionbar_text_color));

        // liao
        mActionBar = getActionBar();
        mActionBar.hide();

        // getPermission();

        // 20150825 add alert dialog for consume data by zhangxiaojun
        // SharedPreferences Preferences = this.getSharedPreferences(
        // AlertWithCheckBoxDialogFragment.SHARED_PREFERENCE_NAME,
        // Context.MODE_PRIVATE);
        // boolean mode = Preferences.getBoolean(
        // AlertWithCheckBoxDialogFragment.NOTICE_PERMISSION_MODE, false);
        // if (!Utils.isAbroadBranch(this) && !mode) {
        // final DialogFragment f = new AlertWithCheckBoxDialogFragment();
        // f.show(getFragmentManager(), "");
        // }
        // 20151116: add runtime permission by zhxj
        // HCT_MODIFY lixiange MF3.0 add My Calendar
        // setIndicatorColor(0xffffa800);
        // addMycalendar();
        // HCT_MODIFY lixiange MF3.0 add My Calendar
        if (icicle != null && icicle.containsKey(BUNDLE_KEY_CHECK_ACCOUNTS)) {
            mCheckForAccounts = icicle.getBoolean(BUNDLE_KEY_CHECK_ACCOUNTS);
        }

        // This needs to be created before setContentView
        mController = CalendarController.getInstance(this);
        // HCT_MODIFY lixiange MF3.0
        mContext = this;
        // HCT_MODIFY lixiange MF3.0
        sPreferences = mContext.getSharedPreferences("data", mContext.MODE_PRIVATE);
        getHolidayData();

        // Get time from intent or icicle
        long timeMillis = -1;
        int viewType = -1;
        final Intent intent = getIntent();
        String action = intent.getAction();

        if (Intent.ACTION_MAIN.equals(action)
                && "widget".equals(intent.getStringExtra("intent_origin"))) {
            timeMillis = parseTimefromSharePref(mContext);
        } else {
            if (icicle != null) {
                timeMillis = icicle.getLong(BUNDLE_KEY_RESTORE_TIME);
                viewType = icicle.getInt(BUNDLE_KEY_RESTORE_VIEW, -1);
            } else {

                if (Intent.ACTION_VIEW.equals(action)) {
                    // Open EventInfo later
                    timeMillis = parseViewAction(intent);
                }

                if (timeMillis == -1) {
                    timeMillis = Utils.timeFromIntentInMillis(intent);
                }
            }
        }

        if (viewType == -1 || viewType > ViewType.MAX_VALUE) {
            viewType = Utils.getViewTypeFromIntentAndSharedPref(this);
        }
        if (DEBUG) {
            if (icicle != null && intent != null) {
                Log.d(TAG, "both, icicle:" + icicle.toString() + "  intent:"
                        + intent.toString());
            } else {
                Log.d(TAG, "not both, icicle:" + icicle + " intent:" + intent);
            }
        }
        if (Utils.checkAndRequestPermission(this,
                Manifest.permission.READ_CALENDAR,
                Utils.REQUEST_PERMISSIONS_CALENDAR)) {
            mhasCalendarPermission = true;
            // addCalendarAccounts();
        } else {
            viewType = ViewType.MONTH;
            mCurrentView = viewType;
        }
        // setContentView must be called before configureActionBar
        setContentView(R.layout.all_in_one);
        processCreate(timeMillis, viewType, icicle);

        // mYearImage = (ImageView) findViewById(R.id.year_title_image);
        // mMonthTextView = (TextView) findViewById(R.id.month_title_text);
        mRootViewGroup  = (ViewGroup) findViewById(R.id.root_view);
        AlphaAnimation alphaAnim = new AlphaAnimation(0.5f, 1);
        alphaAnim.setDuration(200);
        mRootViewGroup.startAnimation(alphaAnim);
        mMonthImageView = (ImageView) findViewById(R.id.month_image);
        mYearTextView = (TextView) findViewById(R.id.year_title_text);
        mMonthYearTitle = (ViewGroup) findViewById(R.id.year_month_title);
        // mMonthView = (TextView) findViewById(R.id.month_text);
        mController.setMonthView(mMonthImageView);
        mMonthYearTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mCurrentView == ViewType.YEAR) {
                    return;
                }
                mCurrentView = ViewType.YEAR;
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager
                        .beginTransaction();
                // YearPagerFragment yearPagerFragment = new YearPagerFragment(
                // YearUtils.getSelectPosition(mYearTextView.getText().toString()));
                YearPagerFragment yearPagerFragment = new YearPagerFragment();
                yearPagerFragment.setYearPosition(YearUtils
                        .getSelectPosition(mYearTextView.getText().toString()));
                // yearPagerFragment.setOnYearCallBack(new OnYearCallBack() {
                // @Override
                // public void currentYear(int year) {
                // setYear(year);
                // }
                // });

                transaction.replace(R.id.main_pane, yearPagerFragment);
                transaction.commit();
                mMonthImageView.setVisibility(View.INVISIBLE);
                // mMonthView.setVisibility(View.INVISIBLE);
            }
        });
        long millis = -1;
        if (Intent.ACTION_VIEW.equals(action) && !intent.getBooleanExtra(Utils.INTENT_KEY_HOME, false)) {
            millis = parseViewAction(intent);

            if (millis == -1) {
                millis = Utils.timeFromIntentInMillis(intent, true);
            }
            if (millis != -1 && mViewEventId == -1 && mController != null) {

                Time time = new Time(mTimeZone);
                time.set(millis);
                time.normalize(true);
                long extras = CalendarController.EXTRA_GOTO_TIME | CalendarController.EXTRA_GOTO_DATE;

                mController.sendEvent(this, EventType.GO_TO, time, null, time, -1, ViewType.CURRENT, extras, null,
                        null);
            }
        }
    }

    private void getHolidayData() {
        // TODO Auto-generated method stub
        final String holidayUrl = String.format("http://zhwnlapi.etouch.cn/Ecalender/openapi/holidays?key=%s",
                "c40fbdbd2f6d413195f6045ce03e49dc");
        final String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) ;
        Boolean hasGetHoliday = sPreferences.getBoolean(year, false);
        if (!hasGetHoliday) {
            Log.e("liaozhenbin", "getHolidayData!!!");
            final SharedPreferences.Editor editor = sPreferences.edit();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String resultJson;
                    boolean hasGet = false;
                    try {
                        resultJson = NetAccess.accessNetWork(holidayUrl);
                        Holiday bean = new Gson().fromJson(resultJson, Holiday.class);
                        if (null == bean) {
                            return;
                        }
                        List<InnerBean> data = bean.getData().getCn();
                        for (InnerBean holiday : data) {
                            editor.putInt(String.valueOf(holiday.getDate()), holiday.getStatus());
                            if (String.valueOf(holiday.getDate()).startsWith(year)){
                                hasGet = true;   
                            }
                        } 
                        editor.apply();
                        editor.putBoolean(year, hasGet);
                        editor.apply();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        
    }

    private void getPermission() {
        // TODO Auto-generated method stub
        String[] permissionArray = { Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR,
                Manifest.permission.ACCESS_COARSE_LOCATION };
        List<String> permissionList;
        permissionList = new ArrayList<>();
        for (String s : permissionArray) {
            if (ContextCompat.checkSelfPermission(this, s) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(s);
            }
        }
        int size = permissionList.size();
        if (size > 0) {
            String[] permissionArrayNeed = (String[]) permissionList
                    .toArray(new String[size]);
            ActivityCompat.requestPermissions(this, permissionArrayNeed, 0);
        }
    }

    public void setYear(int year) {
        int shengXiaoId;
        // int m = Math.abs(year - 2008) % 12;
        // if (year >= 2008) {
        // shengXiaoId = shengXiaos[m];
        // } else {
        // if (m == 0) {
        // m = 12;
        // }
        // shengXiaoId = shengXiaos[12 - m];
        // }
        // mYearImage.setImageResource(shengXiaoId);
        mYearTextView.setText(year + "");
    }

    public void setMonthVisible() {
        mMonthImageView.setVisibility(View.VISIBLE);
        // mMonthView.setVisibility(View.VISIBLE);
    }

    private void processCreate(long timeMillis, int viewType, Bundle icicle) {
        if (DEBUG) {
            Log.d(TAG, "viewType:" + viewType);
        }
        mTimeZone = Utils.getTimeZone(this, mHomeTimeUpdater);
        // Time t = new Time(mTimeZone);
        // t.set(timeMillis);

        Resources res = getResources();
        // HCT_MODIFY lixiange MF3.0 add holidy and term
        mHolidayAndTermString = res.getString(R.string.holiday_and_term);
        mShowHoliday = Utils.getConfigBool(this, R.bool.holiday_and_term);
        mShowHoliday = mShowHoliday
                && Utils.compareWithCurLang(this, Utils.SIMPLIFIED_CHINESE);
        // HCT_MODIFY lixiange MF3.0 add holidy and term
        mHideString = res.getString(R.string.hide_controls);
        mShowString = res.getString(R.string.show_controls);
        mOrientation = res.getConfiguration().orientation;
        if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mControlsAnimateWidth = (int) res
                    .getDimension(R.dimen.calendar_controls_width);
            if (mControlsParams == null) {
                mControlsParams = new LayoutParams(mControlsAnimateWidth, 0);
            }
            mControlsParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        } else {
            // Make sure width is in between allowed min and max width values
            mControlsAnimateWidth = Math
                    .max(res.getDisplayMetrics().widthPixels * 45 / 100,
                            (int) res
                                    .getDimension(R.dimen.min_portrait_calendar_controls_width));
            mControlsAnimateWidth = Math
                    .min(mControlsAnimateWidth,
                            (int) res
                                    .getDimension(R.dimen.max_portrait_calendar_controls_width));
        }

        mControlsAnimateHeight = (int) res
                .getDimension(R.dimen.calendar_controls_height);

        mHideControls = !Utils.getSharedPreference(this,
                GeneralPreferences.KEY_SHOW_CONTROLS, true);

        mFullSizeNoAgenda = Utils.getSharedPreference(this,
                GeneralPreferences.KEY_FULL_SIZE_ON_AGENDA, false);

        mIsMultipane = Utils.getConfigBool(this, R.bool.multiple_pane_config);
        mIsTabletConfig = Utils.getConfigBool(this, R.bool.tablet_config);
        mShowAgendaWithMonth = Utils.getConfigBool(this,
                R.bool.show_agenda_with_month);
        mShowCalendarControls = Utils.getConfigBool(this,
                R.bool.show_calendar_controls);
        mShowEventDetailsWithAgenda = Utils.getConfigBool(this,
                R.bool.show_event_details_with_agenda);
        mShowEventInfoFullScreenAgenda = Utils.getConfigBool(this,
                R.bool.agenda_show_event_info_full_screen);
        mShowEventInfoFullScreen = Utils.getConfigBool(this,
                R.bool.show_event_info_full_screen);
        mCalendarControlsAnimationTime = res
                .getInteger(R.integer.calendar_controls_animation_time);
        Utils.setAllowWeekForDetailView(mIsMultipane);

        if (mIsTabletConfig) {
            mDateRange = (TextView) findViewById(R.id.date_bar);
            mWeekTextView = (TextView) findViewById(R.id.week_num);
        } else {
            mDateRange = (TextView) getLayoutInflater().inflate(
                    R.layout.date_range_title, null);
        }

        // configureActionBar auto-selects the first tab you add, so we need to
        // call it before we set up our own fragments to make sure it doesn't
        // overwrite us
        configureActionBar(viewType);
        // getActionBar().setBackgroundDrawable(new ColorDrawable(0xffffa800));
        setIndicatorColor(getResources().getColor(
                R.color.cale_actionbar_background_color));
        setActionBarContentColor(
                getResources().getColor(R.color.cale_actionbar_icon_color),
                getResources().getColor(R.color.cale_actionbar_text_color));
        mHomeTime = (TextView) findViewById(R.id.home_time);
        mMiniMonth = findViewById(R.id.mini_month);
        if (mIsTabletConfig
                && mOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mMiniMonth.setLayoutParams(new RelativeLayout.LayoutParams(
                    mControlsAnimateWidth, mControlsAnimateHeight));
        }
        mCalendarsList = findViewById(R.id.calendar_list);
        mMiniMonthContainer = findViewById(R.id.mini_month_container);
        mSecondaryPane = findViewById(R.id.secondary_pane);

        // Must register as the first activity because this activity can modify
        // the list of event handlers in it's handle method. This affects who
        // the rest of the handlers the controller dispatches to are.
        mController.registerFirstEventHandler(HANDLER_KEY, this);

        initFragments(timeMillis, viewType, icicle);

        // Listen for changes that would require this to be refreshed
        // SharedPreferences prefs =
        // GeneralPreferences.getSharedPreferences(this);
        // prefs.registerOnSharedPreferenceChangeListener(this);

        SharedPreferences prefs = getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);

        final FloatActionMenuView fView = (FloatActionMenuView) findViewById(R.id.floatMenu);
        if (CalendarUtil.isEnglish())
            fView.setMenuItemsInVisible(R.id.float_menu_holiday);
        // fView.setOnFloatActionMenuSelectedListener
        fView.setOnFloatActionMenuSelectedListener(new OnFloatActionMenuSelectedListener() {

            @Override
            public boolean onFloatActionItemSelected(MenuItem item) {
                final int itemId = item.getItemId();
                switch (itemId) {
                case R.id.float_menu_event_create:
                    Time t;
                    t = new Time(mTimeZone);
                    // t.setToNow();
                    Calendar systemTime = Calendar.getInstance();
                    systemTime.setTimeInMillis(System.currentTimeMillis());
                    Calendar ca = Calendar.getInstance();
                    ca.setTimeInMillis(mController.getTime());
                    ca.set(Calendar.HOUR_OF_DAY,
                            systemTime.get(Calendar.HOUR_OF_DAY));
                    ca.set(Calendar.MINUTE, systemTime.get(Calendar.MINUTE));
                    ca.set(Calendar.SECOND, systemTime.get(Calendar.SECOND));
                    t.set(ca.getTimeInMillis());
                    Log.d(TAG, "lxg ----t.h=" + t.hour + ",m=" + t.minute
                            + ",d=" + t.monthDay);
                    if (t.minute >= 30) {
                        t.hour++;
                        t.minute = 0;
                    } else if (t.minute >= 0 && t.minute < 30) {
                        t.minute = 30;
                    }
                    mController.sendEventRelatedEvent(this,
                            EventType.CREATE_EVENT, -1, t.toMillis(true), 0, 0,
                            0, -1);
                    break;
                case R.id.float_menu_today:
                    Time tt = null;
                    int viewType = ViewType.CURRENT;
                    long extras = CalendarController.EXTRA_GOTO_TIME;
                    viewType = ViewType.CURRENT;
                    tt = new Time(mTimeZone);
                    tt.setToNow();
                    extras |= CalendarController.EXTRA_GOTO_TODAY;
                    mController.sendEvent(this, EventType.GO_TO, tt, null, tt,
                            -1, viewType, extras, null, null);
                    setMonthVisible();
                    break;
                case R.id.float_menu_event_list:
                    AllInOneActivity.this.startActivity(new Intent(
                            AllInOneActivity.this, ScheduleActivity.class));
                    break;
                // case R.id.float_menu_more:
                // showMorePopWindow(fView);
                // break;
                case R.id.float_menu_jump:
                    showSelectDateDialog(fView);
                    break;
                case R.id.float_menu_holiday:
                    Intent holidayIntent = new Intent(Intent.ACTION_VIEW);
                    holidayIntent.setClass(mContext,
                            HolidayAndTermActivity.class);
                    holidayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(holidayIntent);
                    break;
                case R.id.float_menu_setting:
                    mController.sendEvent(this, EventType.LAUNCH_SETTINGS,
                            null, null, 0, 0);
                    break;

                }
                return true;
            }
        });

        CalendarViewAdapter
                .setOnTimeCurrentCallBack(new OnTimeCurrentCallBack() {
                    @Override
                    public void currentTime(long time) {
                        Calendar select = Calendar.getInstance();
                        select.setTimeInMillis(time);
                        int month = select.get(Calendar.MONTH) + 1;
                        int year = select.get(Calendar.YEAR);
                        // liaozhenbin
                        int day = select.get(Calendar.DAY_OF_MONTH);
                        Log.d("liaoliao", "date: " + year + "-" + month + "-"
                                + day);
                        if (mCurrentView == ViewType.MONTH) {
                            setYear(year);
                        }
                        mMonthImageView.setImageResource(monthImage[month - 1]);
                        showDiff(time);
                        if (isTodayequalsSelectedDay(time)) {
                            // hide today
                            fView.setMenuItemsInVisible(R.id.float_menu_today);
                            fView.setMaxItems(3);
                        } else {
                            // show today
                            fView.setMenuItemsVisible(R.id.float_menu_today);
                            fView.setMaxItems(4);
                        }

                    }
                });
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.DISPLAY_FLOATING_BUTTON");
        mContext.registerReceiver(mIntentReceiver, filter);

    }

    // private int[] shengXiaos = { R.drawable.gome_ic_calendar_mouse,
    // R.drawable.gome_ic_calendar_cattle,
    // R.drawable.gome_ic_calendar_tiger, R.drawable.gome_ic_calendar_rabit,
    // R.drawable.gome_ic_calendar_dragen,
    // R.drawable.gome_ic_calendar_snake, R.drawable.gome_ic_calendar_hoese,
    // R.drawable.gome_ic_calendar_sheep,
    // R.drawable.gome_ic_calendar_monkey, R.drawable.gome_ic_calendar_chicken,
    // R.drawable.gome_ic_calendar_dog,
    // R.drawable.gome_ic_calendar_pig };
    private int[] monthImage = { R.drawable.gome_ic_calendar_january,
            R.drawable.gome_ic_calendar_february,
            R.drawable.gome_ic_calendar_march,
            R.drawable.gome_ic_calendar_april, R.drawable.gome_ic_calendar_may,
            R.drawable.gome_ic_calendar_jun, R.drawable.gome_ic_calendar_july,
            R.drawable.gome_ic_calendar_august,
            R.drawable.gome_ic_calendar_sept, R.drawable.gome_ic_calendar_oct,
            R.drawable.gome_ic_calendar_nov, R.drawable.gome_ic_calendar_dec };
    protected PopupWindow mPopupWindow;
    private GMTimePicker timePicker;

    // private WheelWeekMain wheelWeekMainDate;

    private void showDiff(long selectedTime) {
        Calendar calendar = Calendar.getInstance();
        String todayString = "today:"
                + calendar.get(Calendar.YEAR)
                + "-"
                + (1 + calendar.get(Calendar.MONTH) + "-" + calendar
                        .get(Calendar.DAY_OF_MONTH));
        calendar.setTimeInMillis(selectedTime);
        String selectDay = "selectDay:"
                + calendar.get(Calendar.YEAR)
                + "-"
                + (1 + calendar.get(Calendar.MONTH) + "-" + calendar
                        .get(Calendar.DAY_OF_MONTH));

        LogUtils.e("byd  " + isTodayequalsSelectedDay(selectedTime) + "  "
                + todayString + "\n" + selectDay);
    }

    private boolean isTodayequalsSelectedDay(long selectedTime) {
        Calendar today = Calendar.getInstance();
        Calendar select = Calendar.getInstance();
        select.setTimeInMillis(selectedTime);

        return today.get(Calendar.YEAR) == select.get(Calendar.YEAR)
                && today.get(Calendar.MONTH) == select.get(Calendar.MONTH)
                && today.get(Calendar.DAY_OF_MONTH) == select
                        .get(Calendar.DAY_OF_MONTH);
    }

    // private void showMorePopWindow(final View view) {
    // final PopupWindow popWnd = new PopupWindow(this);
    //
    // View contentView =
    // LayoutInflater.from(this).inflate(R.layout.pop_window_layout, null);
    // tv_pop = (TextView) contentView.findViewById(R.id.pop_tv_01);
    // tv_pop.setOnClickListener(new View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // LogUtils.e("tv 01 .....");
    // // jump2SomeDay();
    // showSelectDateDialog(view);
    // popWnd.dismiss();
    //
    // }
    // });
    // contentView.findViewById(R.id.pop_tv_02).setOnClickListener(new
    // View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // LogUtils.e("tv 02 .....");
    // Intent holidayIntent = new Intent(Intent.ACTION_VIEW);
    // holidayIntent.setClass(mContext, HolidayAndTermActivity.class);
    // holidayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    // startActivity(holidayIntent);
    // popWnd.dismiss();
    // }
    // });
    // contentView.findViewById(R.id.pop_tv_03).setOnClickListener(new
    // View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // LogUtils.e("tv 03 .....");
    // mController.sendEvent(this, EventType.LAUNCH_SETTINGS, null, null, 0, 0);
    // // ToastHelper.show(mContext, "to calendar settings...");
    // popWnd.dismiss();
    // }
    // });
    // // contentView.setBackgroundColor(Color.TRANSPARENT);
    //
    // popWnd.setElevation(10);
    // popWnd.setOutsideTouchable(true);
    // popWnd.setBackgroundDrawable(getDrawable(R.drawable.notify_bg));
    // popWnd.setContentView(contentView);
    // popWnd.setWidth(Utils.convertDp2Px(this, 160));
    // popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    // // popWnd.showAsDropDown(view);
    // // public void showAtLocation(View parent, int gravity, int x, int y)
    // int navHeight = Utils.getNormalNavigationBarHeight(this);
    // LogUtils.e("navH = " + navHeight);
    // popWnd.showAtLocation(view, Gravity.BOTTOM | Gravity.END,
    // Utils.convertDp2Px(this, 10),
    // Utils.convertDp2Px(this, 50 + 20) + navHeight);
    // }

    private void showSelectDateDialog(View view) {
        // TODO Auto-generated method stub
        HideSoftInputMethod();
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        // int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);
        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95),
                LayoutParams.WRAP_CONTENT);
        // ScreenInfo screenInfoDate = new ScreenInfo(this);
        // wheelWeekMainDate = new WheelWeekMain(menuView, false);
        // wheelWeekMainDate.screenheight = screenInfoDate.getHeight();
        // Calendar calendar = Calendar.getInstance();
        // final int year = calendar.get(Calendar.YEAR);
        // final int month = calendar.get(Calendar.MONTH);
        // final int day = calendar.get(Calendar.DAY_OF_MONTH);
        // int hours = calendar.get(Calendar.HOUR_OF_DAY);
        // int minute = calendar.get(Calendar.MINUTE);
        // Log.e("ff", "year=" + year + "month=" + month + "day=" + day +
        // "hours=" + hours + "minute=" + minute);
        // wheelWeekMainDate.initChooseJumpDateAdapter(false);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.6f);
        Button tv_cancle = (Button) menuView.findViewById(R.id.tv_cancle);
        Button tv_ensure = (Button) menuView.findViewById(R.id.tv_ensure);
        // TextView tv_pop_title = (TextView)
        // menuView.findViewById(R.id.tv_pop_title);
        OneLineCheckboxLayout cb_show = (OneLineCheckboxLayout) menuView
                .findViewById(R.id.cb_show_yang);
        cb_show.setTextColor("#D0D0D0");
        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);
        ViewGroup nongliViewGroup = (ViewGroup) menuView
                .findViewById(R.id.view_nongli);
        nongliViewGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gs_nongli.setChecked(!gs_nongli.isChecked());
            }
        });
        if (CalendarUtil.isEnglish()) {
            nongliViewGroup.setVisibility(View.INVISIBLE);
        }
        // cb_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        //
        // @Override
        // public void onChange(boolean checked) {
        // // TODO Auto-generated method stub
        // wheelWeekMainDate.setShowNongInJump(checked);
        // }
        // });
        gs_nongli
                .setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                            boolean checked) {
                        // TODO Auto-generated method stub
                        // wheelWeekMainDate.setShowNongInJump(checked);
                        timePicker.setLunarDate(checked);
                    }
                });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // int jumpDay = wheelWeekMainDate.getJumpDay();
                // int jumpMonth = wheelWeekMainDate.getJumpMonth();
                // int jumpYear = wheelWeekMainDate.getJumpYear();
                Calendar date = timePicker.getCurrentTime();
                int jumpDay = date.get(Calendar.DAY_OF_MONTH);
                int jumpMonth = date.get(Calendar.MONTH) + 1;
                int jumpYear = date.get(Calendar.YEAR);
                Time mTime = new Time(mTimeZone);
                long extras = CalendarController.EXTRA_JUMP_DATE;
                mTime.set(mController.getTime());
                int viewType = ViewType.CURRENT;
                Time t = new Time(mTimeZone);
                String jumpDayStr = "";
                String jumpMonthStr = "";
                if (jumpDay < 10) {
                    jumpDayStr = "0" + jumpDay;
                } else {
                    jumpDayStr = "" + jumpDay;
                }
                if (jumpMonth < 10) {
                    jumpMonthStr = "0" + jumpMonth;
                } else {
                    jumpMonthStr = "" + jumpMonth;
                }
                // String result = jumpYear + jumpMonthStr + jumpDayStr;
                // if (!wheelWeekMainDate.isJumpNong()) {
                // if (!CalendarUtil.isValidDate(result)) {
                // Toast.makeText(mContext,
                // mContext.getString(R.string.invalid_input_alert),
                // Toast.LENGTH_LONG)
                // .show();
                // return;
                // }

                // } else {
                // try {
                // String yangStr = CalendarUtil.lunarToSolar(result, false);
                // Log.e("fushuo", "--->yangStr=" + yangStr);
                // jumpYear = Integer.parseInt(yangStr.substring(0, 4));
                // jumpDay = Integer.parseInt(yangStr.substring(6, 8));
                // jumpMonth = Integer.parseInt(yangStr.substring(4, 6));
                // } catch (Exception e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
                // Toast.makeText(mContext,
                // mContext.getString(R.string.invalid_input_alert),
                // Toast.LENGTH_LONG)
                // .show();
                // return;
                // }
                // }
                // t.set(mTime.second, mTime.minute, mTime.hour, jumpDay,
                // jumpMonth - 1, jumpYear);
                t.set(date.getTimeInMillis());
                Log.d("liaozhenbin", jumpYear + "-" + (jumpMonth - 1) + "-"
                        + jumpDay);
                t.normalize(true);
                mController.sendEvent(this, EventType.GO_TO, t, null, t, 1,
                        viewType, extras, null, null);
                // AllInOneActivity.this.startActivity(new
                // Intent(AllInOneActivity.this,EventCardActivity.class).putExtra("card_year",
                // jumpYear).
                // putExtra("card_month", jumpMonth).putExtra("card_day",
                // jumpDay));
                mPopupWindow.dismiss();
            }
        });

    }

    private void HideSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
            mPopupWindow = null;
            // wheelWeekMainDate.setIsJumpDate(false);
        }

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        this.getWindow().setAttributes(lp);
    }

    int mWhich = 0;

    private void jump2SomeDay() {
        Log.d(TAG, "lxg onOptionsItemSelected action_jump_date");
        Time t = new Time(mTimeZone);
        /* HCT_MODIFY jianghejie 2012.9.4 jump date hilight begin */
        long extras = CalendarController.EXTRA_JUMP_DATE;
        /* HCT_MODIFY jianghejie 2012.9.4 jump date hilight end */
        t.set(mController.getTime());
        DatePickerDialogHCT dpd = new DatePickerDialogHCT(this,
                new DateSetListener(t), t.year, t.month, t.monthDay);
        DatePickerHCT picker = dpd.getDatePicker();
        picker.setMaxDate(MAXDATE * 1000);
        picker.setMinDate(MINDATE * 1000);

        dpd.setTitle(mContext.getString(R.string.jump_date));
        dpd.setColor(mContext.getResources().getColor(R.color.cale_icon_color));
        dpd.setButton(DialogInterface.BUTTON_POSITIVE,
                mContext.getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Log.d(TAG,
                                "lxg onOptionsItemSelected dateSetOkClicked = true;");
                        dateSetOkClicked = true;
                    }
                });

        dpd.setButton(DialogInterface.BUTTON_NEGATIVE,
                mContext.getText(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dateSetOkClicked = false;
                    }
                });
        dateSetOkClicked = false;
        dpd.setCanceledOnTouchOutside(true);
        dpd.show();
    }

    @SuppressLint("NewApi")
    private static final ViewOutlineProvider OVAL_OUTLINE_PROVIDER = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getWidth(), view.getHeight());
        }
    };

    private long parseViewAction(final Intent intent) {
        long timeMillis = -1;
        Uri data = intent.getData();
        if (data != null && data.isHierarchical()) {
            List<String> path = data.getPathSegments();
            if (path.size() == 2 && path.get(0).equals("events")) {
                try {
                    mViewEventId = Long.parseLong(data.getLastPathSegment());
                    if (mViewEventId != -1) {
                        mIntentEventStartMillis = intent.getLongExtra(
                                EXTRA_EVENT_BEGIN_TIME, 0);
                        mIntentEventEndMillis = intent.getLongExtra(
                                EXTRA_EVENT_END_TIME, 0);
                        Log.d("lzb", "978 EXTRA_EVENT_BEGIN_TIME: "
                                + mIntentEventStartMillis);
                        mIntentAttendeeResponse = intent
                                .getIntExtra(ATTENDEE_STATUS,
                                        Attendees.ATTENDEE_STATUS_NONE);
                        mIntentAllDay = intent.getBooleanExtra(
                                EXTRA_EVENT_ALL_DAY, false);
                        timeMillis = mIntentEventStartMillis;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if mViewEventId can't be parsed
                }
            }
        }
        return timeMillis;
    }

    private void configureActionBar(int viewType) {
        createButtonsSpinner(viewType, mIsTabletConfig);
        if (mIsMultipane) {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                    | ActionBar.DISPLAY_SHOW_HOME);
        } else {
            mActionBar.setDisplayOptions(0);
        }
    }

    @SuppressWarnings("deprecation")
    private void createButtonsSpinner(int viewType, boolean tabletConfig) {
        // If tablet configuration , show spinner with no dates
        mActionBarMenuSpinnerAdapter = new CalendarViewAdapter(this.mContext,
                viewType, !tabletConfig);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mActionBar.setListNavigationCallbacks(mActionBarMenuSpinnerAdapter,
                this);
        switch (viewType) {
        case ViewType.AGENDA:
            mActionBar.setSelectedNavigationItem(BUTTON_AGENDA_INDEX);
            break;
        case ViewType.DAY:
            mActionBar.setSelectedNavigationItem(BUTTON_MONTH_INDEX);
            break;
        case ViewType.WEEK:
            mActionBar.setSelectedNavigationItem(BUTTON_WEEK_INDEX);
            break;
        case ViewType.MONTH:
            mActionBar.setSelectedNavigationItem(BUTTON_DAY_INDEX);
            break;
        default:
            mActionBar.setSelectedNavigationItem(BUTTON_DAY_INDEX);
            break;
        }
    }

    // Clear buttons used in the agenda view
    private void clearOptionsMenu() {
        if (mOptionsMenu == null) {
            return;
        }
        MenuItem cancelItem = mOptionsMenu.findItem(R.id.action_cancel);
        if (cancelItem != null) {
            cancelItem.setVisible(false);
        }
    }

    // private void setMenuVisibale(boolean flag) {
    // if (mOptionsMenu == null) {
    // return;
    // }
    // // TODO waiting for modify.
    // if (mOptionsMenu.hasVisibleItems()) {
    // MenuItem sizeItem = mOptionsMenu.findItem(R.id.action_month_size);
    //
    // if (sizeItem != null) {
    // sizeItem.setVisible(flag);
    // }
    //
    // // mOptionsMenu.setGroupVisible(0, flag);
    // }
    // }

    @Override
    protected void onResume() {
        Log.e("liaoliao", "onResume");
        super.onResume();
        if (DEBUG)
            Log.d(TAG, "onResume, UpdateOnResume = " + mUpdateOnResume);
        // Check if the upgrade code has ever been run. If not, force a sync
        // just this one time.
        Utils.trySyncAndDisableUpgradeReceiver(this);

        // Must register as the first activity because this activity can modify
        // the list of event handlers in it's handle method. This affects who
        // the rest of the handlers the controller dispatches to are.
        mController.registerFirstEventHandler(HANDLER_KEY, this);

        mOnSaveInstanceStateCalled = false;
        // mContentResolver.registerContentObserver(
        // CalendarContract.Events.CONTENT_URI, true, mObserver);
        if (mUpdateOnResume) {
            initFragments(mController.getTime(), mController.getViewType(),
                    null);
            mUpdateOnResume = false;
        }
        Time t = new Time(mTimeZone);
        t.set(mController.getTime());
        if (mhasCalendarPermission) {
            mController.sendEvent(this, EventType.UPDATE_TITLE, t, t, -1,
                    ViewType.CURRENT, mController.getDateFlags(), null, null);
        }
        // Make sure the drop-down menu will get its date updated at midnight
        if (mActionBarMenuSpinnerAdapter != null) {
            mActionBarMenuSpinnerAdapter.refresh(this);
        }

        if (mControlsMenu != null) {
            mControlsMenu.setTitle(mHideControls ? mShowString : mHideString);
        }
        mPaused = false;

        if (mViewEventId != -1 && mIntentEventStartMillis != -1
                && mIntentEventEndMillis != -1) {
            long currentMillis = System.currentTimeMillis();
            long selectedTime = -1;
            if (currentMillis > mIntentEventStartMillis
                    && currentMillis < mIntentEventEndMillis) {
                selectedTime = currentMillis;
            }
            mController.sendEventRelatedEventWithExtra(this,
                    EventType.VIEW_EVENT, mViewEventId,
                    mIntentEventStartMillis, mIntentEventEndMillis, -1, -1,
                    EventInfo.buildViewExtraLong(mIntentAttendeeResponse,
                            mIntentAllDay), selectedTime);
            mViewEventId = -1;
            mIntentEventStartMillis = -1;
            mIntentEventEndMillis = -1;
            mIntentAllDay = false;
        }
        Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater, mTimeZone);
        // Make sure the today icon is up to date
        invalidateOptionsMenu();

        mCalIntentReceiver = Utils.setTimeChangesReceiver(this, mTimeChangesUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mController.deregisterEventHandler(HANDLER_KEY);
        mPaused = true;
        mHomeTime.removeCallbacks(mHomeTimeUpdater);
        if (mActionBarMenuSpinnerAdapter != null) {
            mActionBarMenuSpinnerAdapter.onPause();
        }
        // mContentResolver.unregisterContentObserver(mObserver);
        if (isFinishing()) {
            // Stop listening for changes that would require this to be
            // refreshed
            // SharedPreferences prefs =
            // GeneralPreferences.getSharedPreferences(this);
            SharedPreferences prefs = getSharedPreferences("settings",
                    Context.MODE_PRIVATE);
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
        // FRAG_TODO save highlighted days of the week;
        if (mController.getViewType() != ViewType.EDIT) {
            int viewType = mController.getViewType();
            int defaultViewType = Utils.getSharedPreference(this,
                    GeneralPreferences.KEY_DETAILED_VIEW,
                    GeneralPreferences.DEFAULT_DETAILED_VIEW);
            Log.d(TAG, "viewType = " + viewType + ", defaultViewType = "
                    + defaultViewType);
            Utils.setStartView(this, viewType);
            if (viewType != defaultViewType) {
                Utils.setDefaultView(this, viewType);
            }
        }

        Utils.resetMidnightUpdater(mHandler, mTimeChangesUpdater);
        Utils.clearTimeChangesReceiver(this, mCalIntentReceiver);
    }

    @Override
    protected void onUserLeaveHint() {
        mController.sendEvent(this, EventType.USER_HOME, null, null, -1,
                ViewType.CURRENT);
        super.onUserLeaveHint();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mOnSaveInstanceStateCalled = true;
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_KEY_RESTORE_TIME, mController.getTime());
        outState.putInt(BUNDLE_KEY_RESTORE_VIEW, mCurrentView);
        if (mCurrentView == ViewType.EDIT) {
            outState.putLong(BUNDLE_KEY_EVENT_ID, mController.getEventId());
        } else if (mCurrentView == ViewType.AGENDA) {
            FragmentManager fm = getFragmentManager();
            Fragment f = fm.findFragmentById(R.id.main_pane);
            if (f instanceof AgendaFragment) {
                outState.putLong(BUNDLE_KEY_EVENT_ID,
                        ((AgendaFragment) f).getLastShowEventId());
            }
        }
        outState.putBoolean(BUNDLE_KEY_CHECK_ACCOUNTS, mCheckForAccounts);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // SharedPreferences prefs =
        // GeneralPreferences.getSharedPreferences(this);
        SharedPreferences prefs = getSharedPreferences("settings",
                Context.MODE_PRIVATE);
        prefs.unregisterOnSharedPreferenceChangeListener(this);

        mController.deregisterAllEventHandlers();

        CalendarController.removeInstance(this);

        mContext.unregisterReceiver(mIntentReceiver);
    }

    private void initFragments(long timeMillis, int viewType, Bundle icicle) {
        if (DEBUG) {
            Log.d(TAG, "Initializing to " + timeMillis + " for view "
                    + viewType);
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();

        if (mShowCalendarControls) {
            Fragment miniMonthFrag = new MonthFragment(timeMillis, mTimeZone,
                    true);
            ft.replace(R.id.mini_month, miniMonthFrag);
            mController.registerEventHandler(R.id.mini_month,
                    (EventHandler) miniMonthFrag);

            Fragment selectCalendarsFrag = new SelectVisibleCalendarsFragment();
            ft.replace(R.id.calendar_list, selectCalendarsFrag);
            mController.registerEventHandler(R.id.calendar_list,
                    (EventHandler) selectCalendarsFrag);
        }
        if (!mShowCalendarControls || viewType == ViewType.EDIT) {
            mMiniMonth.setVisibility(View.GONE);
            mCalendarsList.setVisibility(View.GONE);
        }
        EventInfo info;
        if (viewType == ViewType.EDIT) {
            mPreviousView = GeneralPreferences.getSharedPreferences(this)
                    .getInt(GeneralPreferences.KEY_START_VIEW,
                            GeneralPreferences.DEFAULT_START_VIEW);

            long eventId = -1;
            Intent intent = getIntent();
            Uri data = intent.getData();
            if (data != null) {
                try {
                    eventId = Long.parseLong(data.getLastPathSegment());
                } catch (NumberFormatException e) {
                    if (DEBUG) {
                        Log.d(TAG, "Create new event");
                    }
                }
            } else if (icicle != null
                    && icicle.containsKey(BUNDLE_KEY_EVENT_ID)) {
                eventId = icicle.getLong(BUNDLE_KEY_EVENT_ID);
            }

            long begin = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, -1);
            Log.d("lzb", "1224 EXTRA_EVENT_BEGIN_TIME: "
                    + mIntentEventStartMillis);
            long end = intent.getLongExtra(EXTRA_EVENT_END_TIME, -1);
            info = new EventInfo();
            if (end != -1) {
                info.endTime = new Time();
                info.endTime.set(end);
            }
            if (begin != -1) {
                info.startTime = new Time();
                info.startTime.set(begin);
            }
            info.id = eventId;
            // We set the viewtype so if the user presses back when they are
            // done editing the controller knows we were in the Edit Event
            // screen. Likewise for eventId
            mController.setViewType(viewType);
            mController.setEventId(eventId);
        } else {
            mPreviousView = viewType;
        }

        setMainPane(ft, R.id.main_pane, viewType, timeMillis, true);
        ft.commit(); // this needs to be after setMainPane()

        Time t = new Time(mTimeZone);
        t.set(timeMillis);
        if (viewType == ViewType.AGENDA && icicle != null) {
            mController.sendEvent(this, EventType.GO_TO, t, null,
                    icicle.getLong(BUNDLE_KEY_EVENT_ID, -1), viewType);
        } else if (viewType != ViewType.EDIT) {
            mController.sendEvent(this, EventType.GO_TO, t, null, -1, viewType);
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentView == ViewType.EDIT || mBackToPreviousView) {
            mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                    mPreviousView);
        } else {
            // super.onBackPressed(); // EC:617003883382,crash bug.
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mOptionsMenu = menu;
        // getMenuInflater().inflate(R.menu.all_in_one_title_bar, menu);
        if (RO_BUILD_DISPLAY_INTID.contains("GM02")
                && RO_BUILD_DISPLAY_INTID.contains("CM")) {
            getMenuInflater().inflate(R.menu.all_in_one_title_bar, menu);
        } else { // hide account
                 // do nothing !
            getMenuInflater().inflate(R.menu.all_in_one_title_bar_no_account,
                    menu);
        }

        setActionBarContentColor(
                getResources().getColor(R.color.cale_actionbar_icon_color),
                getResources().getColor(R.color.cale_actionbar_text_color));

        mSearchMenu = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchMenu.getActionView();
        if (mSearchView != null) {
            mSearchMenu.setVisible(true);
            mSearchView.setSubmitButtonEnabled(false);
            Utils.setUpSearchView(mSearchView, this);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSuggestionListener(this);
        } else {
            // mSearchMenu.setVisible(false);
        }

        // Hide the "show/hide controls" button if this is a phone
        // or the view type is "Month" or "Agenda".

        mControlsMenu = menu.findItem(R.id.action_hide_controls);
        if (!mShowCalendarControls) {
            if (mControlsMenu != null) {
                mControlsMenu.setVisible(false);
                mControlsMenu.setEnabled(false);
            }
        } else if (mControlsMenu != null
                && mController != null
                && (mController.getViewType() == ViewType.MONTH || mController
                        .getViewType() == ViewType.AGENDA)) {
            mControlsMenu.setVisible(false);
            mControlsMenu.setEnabled(false);
        } else if (mControlsMenu != null) {
            mControlsMenu.setTitle(mHideControls ? mShowString : mHideString);
        }

        MenuItem menuItem = menu.findItem(R.id.action_today);
        if (Utils.isJellybeanOrLater()) {
            // replace the default top layer drawable of the today icon with a
            // custom drawable that shows the day of the month of today
            LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
            Utils.setTodayIcon(icon, this, mTimeZone);
        } else {
            menuItem.setIcon(R.drawable.ic_menu_today_no_date_holo_light);
        }

        // HCT_MODIFY lixiange MF3.0 add holidy and term
        mHolidayAndTermMenu = menu.findItem(R.id.action_holiday_and_term);
        // String isAbroad = SystemProperties.get("ro.gios.custom", "home");
        if (Utils.isAbroadBranch(mContext)
                || (mHolidayAndTermMenu != null && ("Holiday And Term"
                        .equals(mHolidayAndTermString) || mShowHoliday == false))) {
            Log.d(TAG, "set holidayAndTermMenu visible false");
            mHolidayAndTermMenu.setVisible(false);
            mHolidayAndTermMenu.setEnabled(false);
        }
        // HCT_MODIFY lixiange MF3.0 add holidy and term

        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mEventsDeleteMenu = menu.findItem(R.id.action_delete_events);
        if (mEventsDeleteMenu != null
                && mController.getViewType() != ViewType.AGENDA) {
            mEventsDeleteMenu.setVisible(false);
            mEventsDeleteMenu.setEnabled(false);
        }
        // HCT_MODIFY zxj MF3.0 add delete all select event
        mFullSizeMenu = menu.findItem(R.id.action_month_size);
        if (mFullSizeMenu != null) {
            mFullSizeMenu.setTitle(mFullSizeNoAgenda ? getResources()
                    .getString(R.string.show_agenda_list) : getResources()
                    .getString(R.string.hide_agenda_list));
        }

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mFullSizeMenu != null) {
            int viewType = mController.getViewType();
            if (viewType == ViewType.MONTH) {
                mFullSizeMenu.setVisible(true && mFullSizeMenuVisible);
            } else {
                mFullSizeMenu.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Time t = null;
        int viewType = ViewType.CURRENT;
        long extras = CalendarController.EXTRA_GOTO_TIME;
        final int itemId = item.getItemId();
        if (itemId == R.id.action_refresh) {
            mController.refreshCalendars();
            return true;
        } else if (itemId == R.id.action_month_size) {
            mFullSizeNoAgenda = !mFullSizeNoAgenda;
            String menuTitle = mFullSizeNoAgenda ? getResources().getString(
                    R.string.show_agenda_list) : getResources().getString(
                    R.string.hide_agenda_list);
            mFullSizeMenu.setTitle(menuTitle);
            Utils.setSharedPreference(this,
                    GeneralPreferences.KEY_FULL_SIZE_ON_AGENDA,
                    mFullSizeNoAgenda);
            Fragment ft = getFragmentManager().findFragmentById(R.id.main_pane);
            if (ft instanceof MonthFragment) {
                ((MonthFragment) ft).isHideAgendaList(mFullSizeNoAgenda);
            } else {
                Log.e(TAG, "fragment is not instance of  MonthFragment.");
            }

            return true;
        } else if (itemId == R.id.action_today) {
            viewType = ViewType.CURRENT;
            t = new Time(mTimeZone);
            t.setToNow();
            extras |= CalendarController.EXTRA_GOTO_TODAY;
        } else if (itemId == R.id.action_select_visible_calendars) {
            mController
                    .sendEvent(this, EventType.LAUNCH_SELECT_VISIBLE_CALENDARS,
                            null, null, 0, 0);
            return true;
        } else if (itemId == R.id.action_settings) {
            mController.sendEvent(this, EventType.LAUNCH_SETTINGS, null, null,
                    0, 0);
            return true;
        } else if (itemId == R.id.action_hide_controls) {
            mHideControls = !mHideControls;
            Utils.setSharedPreference(this,
                    GeneralPreferences.KEY_SHOW_CONTROLS, !mHideControls);
            item.setTitle(mHideControls ? mShowString : mHideString);
            if (!mHideControls) {
                mMiniMonth.setVisibility(View.VISIBLE);
                mCalendarsList.setVisibility(View.VISIBLE);
                mMiniMonthContainer.setVisibility(View.VISIBLE);
            }
            final ObjectAnimator slideAnimation = ObjectAnimator.ofInt(this,
                    "controlsOffset",
                    mHideControls ? 0 : mControlsAnimateWidth,
                    mHideControls ? mControlsAnimateWidth : 0);
            slideAnimation.setDuration(mCalendarControlsAnimationTime);
            ObjectAnimator.setFrameDelay(0);
            slideAnimation.start();
            return true;
        } else if (itemId == R.id.action_search) {
            Log.d(TAG, "lxg --------action_search");
            // setMenuVisibale(false);
            Intent intent = new Intent(Intent.ACTION_SEARCH).setFlags(
                    Intent.FLAG_ACTIVITY_SINGLE_TOP).setClass(this,
                    SearchActivity.class);
            startActivity(intent);
            return false;
        }
        // HCT_MODIFY,lixiange MF3.0 jump data
        else if (itemId == R.id.action_jump_date) {
            Log.d(TAG, "lxg onOptionsItemSelected action_jump_date");
            t = new Time(mTimeZone);
            /* HCT_MODIFY jianghejie 2012.9.4 jump date hilight begin */
            extras = CalendarController.EXTRA_JUMP_DATE;
            /* HCT_MODIFY jianghejie 2012.9.4 jump date hilight end */
            t.set(mController.getTime());
            DatePickerDialogHCT dpd = new DatePickerDialogHCT(this,
                    new DateSetListener(t), t.year, t.month, t.monthDay);
            DatePickerHCT picker = dpd.getDatePicker();
            picker.setMaxDate(MAXDATE * 1000);
            picker.setMinDate(MINDATE * 1000);

            dpd.setTitle(mContext.getString(R.string.jump_date));
            dpd.setColor(mContext.getResources().getColor(
                    R.color.cale_icon_color));
            dpd.setButton(DialogInterface.BUTTON_POSITIVE,
                    mContext.getText(android.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Log.d(TAG,
                                    "lxg onOptionsItemSelected dateSetOkClicked = true;");
                            dateSetOkClicked = true;
                        }
                    });

            dpd.setButton(DialogInterface.BUTTON_NEGATIVE,
                    mContext.getText(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dateSetOkClicked = false;
                        }
                    });
            dateSetOkClicked = false;
            dpd.setCanceledOnTouchOutside(true);
            dpd.show();

            return true;
        }
        // HCT_MODIFY,lixiange MF3.0 jump data
        // HCT_MODIFY lixiange MF3.0 add holidy and term
        else if (itemId == R.id.action_holiday_and_term) {
            Intent holidayIntent = new Intent(Intent.ACTION_VIEW);
            holidayIntent.setClass(this, HolidayAndTermActivity.class);
            holidayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(holidayIntent);
            return true;
        } // HCT_MODIFY lixiange MF3.0 add NBA
        else if (itemId == R.id.action_delete_events) {
            Intent intent = new Intent(AgendaFragment.DELETE_EVENTS_ACTION);
            sendBroadcast(intent);
            floatingButtonContainer.setVisibility(View.GONE);
            return true;
        }
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        else {
            Log.w(TAG, "onOptionsItemSelected, itemId not find:" + itemId);
            // 20160505 delete MenuExtensions for launcher Time by zhxj
            // return mExtensions.handleItemSelected(item, this);
            return false;
        }
        if (DEBUG)
            Log.d(TAG, "onOptionsItemSelected, itemId = " + itemId
                    + ", viewType= " + viewType + ", extras= " + extras);
        mController.sendEvent(this, EventType.GO_TO, t, null, t, -1, viewType,
                extras, null, null);
        return true;
    }

    // HCT_MODIFY,lixiange MF3.0 jump data
    private class DateSetListener implements
            DatePickerDialogHCT.OnDateSetListener {
        Time mTime;

        public DateSetListener(Time time) {
            mTime = time;
        }

        @Override
        public void onDateSet(DatePickerHCT view, int year, int month,
                int monthDay) {
            if (!dateSetOkClicked)
                return;
            int viewType = ViewType.CURRENT;
            long extras = CalendarController.EXTRA_GOTO_TIME
                    | CalendarController.EXTRA_GOTO_DATE;
            Time t = new Time(mTimeZone);
            t.set(mTime.second, mTime.minute, mTime.hour, monthDay, month, year);
            t.normalize(true);
            mSelectDate = t;
            // Long mselecttime = mSelectDate.normalize(true);
            Log.d(TAG, "lxg --mSelectDate=" + mSelectDate.toMillis(true));
            /**
             * HCT_MODIFY jianghejie if we want to go to a date we should not
             * keep time duration in agenda view
             */
            mController.sendEvent(this, EventType.GO_TO, t, null, t, 1,
                    viewType, extras, null, null);
            /** HCT_MODIFY end */
        }
    }

    // HCT_MODIFY,lixiange MF3.0 jump data

    // HCT_MODIFY lixiange MF3.0 add My Calendar
    private static final String MY_CALENDAR = "My calendar";
    private GomeSwitch gs_nongli;

    public void addMycalendar() {
        Cursor cursor = null;
        if (Utils.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)) {
            cursor = getContentResolver().query(Calendars.CONTENT_URI,
                    new String[] { Calendars._ID }, null, null /*
                                                                * selection args
                                                                */, null /*
                                                                          * sort
                                                                          * order
                                                                          */);
        }
        if (cursor == null) {
            return;
        } else if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        ContentValues values = new ContentValues();
        values.put(Calendars.ACCOUNT_NAME, MY_CALENDAR);
        values.put(Calendars.ACCOUNT_TYPE, MY_CALENDAR);
        values.put(Calendars.OWNER_ACCOUNT, MY_CALENDAR);
        values.put(Calendars.CALENDAR_DISPLAY_NAME, MY_CALENDAR);
        values.put(Calendars.VISIBLE, 1);
        values.put(Calendars.SYNC_EVENTS, 1);
        int color = getResources().getColor(R.color.my_calendar_color);
        values.put(Calendars.CALENDAR_COLOR, color); // -14069085 /* blue */
        values.put(Calendars.CALENDAR_TIME_ZONE, Time.getCurrentTimezone());
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER);
        getContentResolver().insert(Calendars.CONTENT_URI, values);
        cursor.close();
    }

    // HCT_MODIFY lixiange MF3.0 add My Calendar

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        // if (key.equals(GeneralPreferences.KEY_WEEK_START_DAY)) {
        if (key.equals("preferences_week_start_day")) {
            Log.d("2877", GeneralPreferences.KEY_WEEK_START_DAY + " changed!");
            if (mPaused) {
                mUpdateOnResume = true;
            } else {
                initFragments(mController.getTime(), mController.getViewType(),
                        null);
            }
            // send broadcast to update widget when change start week
            Intent i = new Intent("android.intent.action.GET_MONTH_EVENT");
            mContext.sendBroadcast(i);
        }
    }

    private void setMainPane(FragmentTransaction ft, int viewId, int viewType,
            long timeMillis, boolean force) {
        if (mOnSaveInstanceStateCalled) {
            return;
        }
        if (!force && mCurrentView == viewType) {
            return;
        }

        // Remove this when transition to and from month view looks fine.
        boolean doTransition = viewType != ViewType.MONTH
                && mCurrentView != ViewType.MONTH;
        FragmentManager fragmentManager = getFragmentManager();
        // Check if our previous view was an Agenda view
        // TODO remove this if framework ever supports nested fragments
        if (mCurrentView == ViewType.AGENDA) {
            // If it was, we need to do some cleanup on it to prevent the
            // edit/delete buttons from coming back on a rotation.
            Fragment oldFrag = fragmentManager.findFragmentById(viewId);
            if (oldFrag instanceof AgendaFragment) {
                ((AgendaFragment) oldFrag).removeFragments(fragmentManager);
            }
        }

        if (viewType != mCurrentView) {
            // The rules for this previous view are different than the
            // controller's and are used for intercepting the back button.
            if (mCurrentView != ViewType.EDIT && mCurrentView > 0) {
                mPreviousView = mCurrentView;
            }
            mCurrentView = viewType;
        }
        // Create new fragment
        Fragment frag = null;
        Fragment secFrag = null;
        switch (viewType) {
        case ViewType.AGENDA:
            if (mActionBar != null
                    && (mActionBar.getSelectedTab() != mAgendaTab)) {
                mActionBar.selectTab(mAgendaTab);
            }
            if (mActionBarMenuSpinnerAdapter != null) {
                mActionBar
                        .setSelectedNavigationItem(CalendarViewAdapter.AGENDA_BUTTON_INDEX);
            }
            frag = new AgendaFragment(timeMillis, false);
            mActionBar.setDisplayShowHomeEnabled(false);
            ExtensionsFactory.getAnalyticsLogger(getBaseContext()).trackView(
                    "agenda");
            break;
        case ViewType.DAY:
            if (mActionBar != null && (mActionBar.getSelectedTab() != mDayTab)) {
                mActionBar.selectTab(mDayTab);
            }
            if (mActionBarMenuSpinnerAdapter != null) {
                mActionBar
                        .setSelectedNavigationItem(CalendarViewAdapter.MONTH_BUTTON_INDEX);
            }
            frag = new DayFragment(timeMillis, 1);
            ExtensionsFactory.getAnalyticsLogger(getBaseContext()).trackView(
                    "day");
            break;
        case ViewType.MONTH:
            if (mActionBar != null
                    && (mActionBar.getSelectedTab() != mMonthTab)) {
                mActionBar.selectTab(mMonthTab);
            }
            if (mActionBarMenuSpinnerAdapter != null) {
                mActionBar
                        .setSelectedNavigationItem(CalendarViewAdapter.DAY_BUTTON_INDEX);
            }
            frag = new MonthFragment(timeMillis, mTimeZone, mFullSizeNoAgenda);
            mFullSizeMenuVisible = true;
            if (mShowAgendaWithMonth) {
                secFrag = new AgendaFragment(timeMillis, false);
            }
            ExtensionsFactory.getAnalyticsLogger(getBaseContext()).trackView(
                    "month");
            break;
        case ViewType.WEEK:
        default:
            if (mActionBar != null && (mActionBar.getSelectedTab() != mWeekTab)) {
                mActionBar.selectTab(mWeekTab);
            }
            if (mActionBarMenuSpinnerAdapter != null) {
                mActionBar
                        .setSelectedNavigationItem(CalendarViewAdapter.WEEK_BUTTON_INDEX);
            }
            frag = new DayFragment(timeMillis, 7);
            ExtensionsFactory.getAnalyticsLogger(getBaseContext()).trackView(
                    "week");
            break;
        }

        // Update the current view so that the menu can update its look
        // according to the
        // current view.
        if (mActionBarMenuSpinnerAdapter != null) {
            mActionBarMenuSpinnerAdapter.setMainView(viewType);
            if (!mIsTabletConfig) {
                Log.d(TAG, "lxg setMainPane");
                mActionBarMenuSpinnerAdapter.setTime(timeMillis);
            }
        }

        // Show date only on tablet configurations in views different than
        // Agenda
        if (!mIsTabletConfig) {
            mDateRange.setVisibility(View.GONE);
        } else if (viewType != ViewType.AGENDA) {
            mDateRange.setVisibility(View.VISIBLE);
        } else {
            mDateRange.setVisibility(View.GONE);
        }

        // Clear unnecessary buttons from the option menu when switching from
        // the agenda view
        if (viewType != ViewType.AGENDA) {
            clearOptionsMenu();
        }

        boolean doCommit = false;
        if (ft == null) {
            doCommit = true;
            ft = fragmentManager.beginTransaction();
        }

        if (doTransition) {
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        ft.replace(viewId, frag);
        if (mShowAgendaWithMonth) {

            // Show/hide secondary fragment

            if (secFrag != null) {
                ft.replace(R.id.secondary_pane, secFrag);
                mSecondaryPane.setVisibility(View.VISIBLE);
            } else {
                mSecondaryPane.setVisibility(View.GONE);
                Fragment f = fragmentManager
                        .findFragmentById(R.id.secondary_pane);
                if (f != null) {
                    ft.remove(f);
                }
                mController.deregisterEventHandler(R.id.secondary_pane);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "Adding handler with viewId " + viewId + " and type "
                    + viewType);
        }
        // If the key is already registered this will replace it
        mController.registerEventHandler(viewId, (EventHandler) frag);
        if (secFrag != null) {
            mController.registerEventHandler(viewId, (EventHandler) secFrag);
        }

        if (doCommit) {
            if (DEBUG) {
                Log.d(TAG, "setMainPane AllInOne=" + this + " finishing:"
                        + this.isFinishing());
            }
            ft.commit();
        }
    }

    private void setTitleInActionBar(EventInfo event) {
        if (event.eventType != EventType.UPDATE_TITLE || mActionBar == null) {
            return;
        }

        final long start = event.startTime.toMillis(false /* use isDst */);
        final long end;
        if (event.endTime != null) {
            end = event.endTime.toMillis(false /* use isDst */);
        } else {
            end = start;
        }
        final String msg = Utils.formatDateRange(this, start, end,
                (int) event.extraLong);
        CharSequence oldDate = mDateRange.getText();
        mDateRange.setText(msg);
        // updateSecondaryTitleFields(event.selectedTime != null ?
        // event.selectedTime
        // .toMillis(true) : start);
        if (!TextUtils.equals(oldDate, msg)) {
            mDateRange
                    .sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            if (mShowWeekNum && mWeekTextView != null) {
                mWeekTextView
                        .sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
            }
        }
    }

    private void updateSecondaryTitleFields(long visibleMillisSinceEpoch) {
        mShowWeekNum = Utils.getShowWeekNumber(this);
        mTimeZone = Utils.getTimeZone(this, mHomeTimeUpdater);
        if (visibleMillisSinceEpoch != -1) {
            int weekNum = Utils.getWeekNumberFromTime(visibleMillisSinceEpoch,
                    this);
            mWeekNum = weekNum;
        }

        if (mShowWeekNum && (mCurrentView == ViewType.WEEK) && mIsTabletConfig
                && mWeekTextView != null) {
            String weekString = getResources().getQuantityString(
                    R.plurals.weekN, mWeekNum, mWeekNum);
            mWeekTextView.setText(weekString);
            mWeekTextView.setVisibility(View.VISIBLE);
        } else if (visibleMillisSinceEpoch != -1 && mWeekTextView != null
                && mCurrentView == ViewType.DAY && mIsTabletConfig) {
            Time time = new Time(mTimeZone);
            time.set(visibleMillisSinceEpoch);
            int julianDay = Time.getJulianDay(visibleMillisSinceEpoch,
                    time.gmtoff);
            time.setToNow();
            int todayJulianDay = Time.getJulianDay(time.toMillis(false),
                    time.gmtoff);
            String dayString = Utils.getDayOfWeekString(julianDay,
                    todayJulianDay, visibleMillisSinceEpoch, this);
            mWeekTextView.setText(dayString);
            mWeekTextView.setVisibility(View.VISIBLE);
        } else if (mWeekTextView != null
                && (!mIsTabletConfig || mCurrentView != ViewType.DAY)) {
            mWeekTextView.setVisibility(View.GONE);
        }

        if (mHomeTime != null
                && (mCurrentView == ViewType.DAY
                        || mCurrentView == ViewType.WEEK || mCurrentView == ViewType.AGENDA)
                && !TextUtils.equals(mTimeZone, Time.getCurrentTimezone())) {
            Time time = new Time(mTimeZone);
            time.setToNow();
            long millis = time.toMillis(true);
            boolean isDST = time.isDst != 0;
            int flags = DateUtils.FORMAT_SHOW_TIME;
            if (DateFormat.is24HourFormat(this)) {
                flags |= DateUtils.FORMAT_24HOUR;
            }
            // Formats the time as
            String timeString = (new StringBuilder(Utils.formatDateRange(this,
                    millis, millis, flags)))
                    .append(" ")
                    .append(TimeZone.getTimeZone(mTimeZone).getDisplayName(
                            isDST, TimeZone.SHORT, Locale.getDefault()))
                    .toString();
            mHomeTime.setText(timeString);
            mHomeTime.setVisibility(View.VISIBLE);
            // Update when the minute changes
            mHomeTime.removeCallbacks(mHomeTimeUpdater);
            mHomeTime.postDelayed(mHomeTimeUpdater, DateUtils.MINUTE_IN_MILLIS
                    - (millis % DateUtils.MINUTE_IN_MILLIS));
        } else if (mHomeTime != null) {
            mHomeTime.setVisibility(View.GONE);
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return EventType.GO_TO | EventType.VIEW_EVENT | EventType.UPDATE_TITLE;
    }

    @Override
    public void handleEvent(EventInfo event) {
        long displayTime = -1;
        if (event.eventType == EventType.GO_TO) {
            if ((event.extraLong & CalendarController.EXTRA_GOTO_BACK_TO_PREVIOUS) != 0) {
                mBackToPreviousView = true;
            } else if (event.viewType != mController.getPreviousViewType()
                    && event.viewType != ViewType.EDIT) {
                // Clear the flag is change to a different view type
                mBackToPreviousView = false;
            }

            setMainPane(null, R.id.main_pane, event.viewType,
                    event.startTime.toMillis(false), false);
            if (mSearchView != null) {
                mSearchView.clearFocus();
            }

            // HCT_MODIFY lixiange MF3.0 add delete all select event
            if (mEventsDeleteMenu != null) {
                if (event.viewType == ViewType.AGENDA) {
                    mEventsDeleteMenu.setVisible(true);
                    mEventsDeleteMenu.setEnabled(true);

                } else {
                    mEventsDeleteMenu.setVisible(false);
                    mEventsDeleteMenu.setEnabled(false);
                }
            }
            // HCT_MODIFY lixiange MF3.0 add delete all select event

            // HCT_MODIFY lixiange MF3.0 add holidy and term
            if (mHolidayAndTermMenu != null
                    && ("Holiday And Term".equals(mHolidayAndTermString) || mShowHoliday == false)) {
                Log.d(TAG, "set holidayAndTermMenu visible false");
                mHolidayAndTermMenu.setVisible(false);
                mHolidayAndTermMenu.setEnabled(false);
            }
            // HCT_MODIFY lixiange MF3.0 add holidy and term

            if (mShowCalendarControls) {
                int animationSize = (mOrientation == Configuration.ORIENTATION_LANDSCAPE) ? mControlsAnimateWidth
                        : mControlsAnimateHeight;
                boolean noControlsView = event.viewType == ViewType.MONTH
                        || event.viewType == ViewType.AGENDA;
                if (mControlsMenu != null) {
                    mControlsMenu.setVisible(!noControlsView);
                    mControlsMenu.setEnabled(!noControlsView);
                }
                if (noControlsView || mHideControls) {
                    // hide minimonth and calendar frag
                    mShowSideViews = false;
                    if (!mHideControls) {
                        final ObjectAnimator slideAnimation = ObjectAnimator
                                .ofInt(this, "controlsOffset", 0, animationSize);
                        slideAnimation.addListener(mSlideAnimationDoneListener);
                        slideAnimation
                                .setDuration(mCalendarControlsAnimationTime);
                        ObjectAnimator.setFrameDelay(0);
                        slideAnimation.start();
                    } else {
                        mMiniMonth.setVisibility(View.GONE);
                        mCalendarsList.setVisibility(View.GONE);
                        mMiniMonthContainer.setVisibility(View.GONE);
                    }
                } else {
                    // show minimonth and calendar frag
                    mShowSideViews = true;
                    mMiniMonth.setVisibility(View.VISIBLE);
                    mCalendarsList.setVisibility(View.VISIBLE);
                    mMiniMonthContainer.setVisibility(View.VISIBLE);
                    if (!mHideControls
                            && (mController.getPreviousViewType() == ViewType.MONTH || mController
                                    .getPreviousViewType() == ViewType.AGENDA)) {
                        final ObjectAnimator slideAnimation = ObjectAnimator
                                .ofInt(this, "controlsOffset", animationSize, 0);
                        slideAnimation
                                .setDuration(mCalendarControlsAnimationTime);
                        ObjectAnimator.setFrameDelay(0);
                        slideAnimation.start();
                    }
                }
            }
            displayTime = event.selectedTime != null ? event.selectedTime
                    .toMillis(true) : event.startTime.toMillis(true);

            if (!mIsTabletConfig) {
                Log.d(TAG, "lxg handleEvent goto");
                mActionBarMenuSpinnerAdapter.setTime(displayTime);
            }
        } else if (event.eventType == EventType.VIEW_EVENT) {

            // If in Agenda view and "show_event_details_with_agenda" is "true",
            // do not create the event info fragment here, it will be created by
            // the Agenda
            // fragment

            if (mCurrentView == ViewType.AGENDA && mShowEventDetailsWithAgenda) {
                if (event.startTime != null && event.endTime != null) {
                    // Event is all day , adjust the goto time to local time
                    if (event.isAllDay()) {
                        Utils.convertAlldayUtcToLocal(event.startTime,
                                event.startTime.toMillis(false), mTimeZone);
                        Utils.convertAlldayUtcToLocal(event.endTime,
                                event.endTime.toMillis(false), mTimeZone);
                    }
                    mController.sendEvent(this, EventType.GO_TO,
                            event.startTime, event.endTime, event.selectedTime,
                            event.id, ViewType.AGENDA,
                            CalendarController.EXTRA_GOTO_TIME, null, null);
                } else if (event.selectedTime != null) {
                    mController.sendEvent(this, EventType.GO_TO,
                            event.selectedTime, event.selectedTime, event.id,
                            ViewType.AGENDA);
                }
            } else {
                // TODO Fix the temp hack below: && mCurrentView !=
                // ViewType.AGENDA
                if (event.selectedTime != null
                        && mCurrentView != ViewType.AGENDA) {
                    mController.sendEvent(this, EventType.GO_TO,
                            event.selectedTime, event.selectedTime, -1,
                            ViewType.CURRENT);
                }
                int response = event.getResponse();
                if ((mCurrentView == ViewType.AGENDA && mShowEventInfoFullScreenAgenda)
                        || ((mCurrentView == ViewType.DAY
                                || (mCurrentView == ViewType.WEEK) || mCurrentView == ViewType.MONTH) && mShowEventInfoFullScreen)) {
                    // start event info as activity
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri eventUri = ContentUris.withAppendedId(
                            Events.CONTENT_URI, event.id);
                    intent.setData(eventUri);
                    // intent.setClass(this, EventInfoActivity.class);
                    intent.setClass(this, ScheduleDetailsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    intent.putExtra(EXTRA_EVENT_BEGIN_TIME,
                            event.startTime.toMillis(false));
                    intent.putExtra(EXTRA_EVENT_END_TIME,
                            event.endTime.toMillis(false));
                    intent.putExtra(ATTENDEE_STATUS, response);
                    intent.putExtra("id", (int) event.id);
                    startActivity(intent);
                } else {
                    // start event info as a dialog
                    EventInfoFragment fragment = new EventInfoFragment(this,
                            event.id, event.startTime.toMillis(false),
                            event.endTime.toMillis(false), response, true,
                            EventInfoFragment.DIALOG_WINDOW_STYLE, null /*
                                                                         * No
                                                                         * reminders
                                                                         * to
                                                                         * explicitly
                                                                         * pass
                                                                         * in.
                                                                         */);
                    fragment.setDialogParams(event.x, event.y,
                            mActionBar.getHeight());
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    // if we have an old popup replace it
                    Fragment fOld = fm
                            .findFragmentByTag(EVENT_INFO_FRAGMENT_TAG);
                    if (fOld != null && fOld.isAdded()) {
                        ft.remove(fOld);
                    }
                    ft.add(fragment, EVENT_INFO_FRAGMENT_TAG);
                    ft.commit();
                }
            }
            displayTime = event.startTime.toMillis(true);
        } else if (event.eventType == EventType.UPDATE_TITLE) {
            setTitleInActionBar(event);
            if (!mIsTabletConfig) {
                // Log.d(TAG,"lxg UPDATE_TITLE");
                Long time_title = mController.getTime();
                mActionBarMenuSpinnerAdapter.setTime(time_title);
                Log.v(TAG, "lxg UPDATE_TITLE_time_title " + time_title);
            }
        }
        updateSecondaryTitleFields(displayTime);
    }

    // Needs to be in proguard whitelist
    // Specified as listener via android:onClick in a layout xml
    public void handleSelectSyncedCalendarsClicked(View v) {
        mController.sendEvent(this, EventType.LAUNCH_SETTINGS, null, null,
                null, 0, 0, CalendarController.EXTRA_GOTO_TIME, null, null);
    }

    @Override
    public void eventsChanged() {
        mController.sendEvent(this, EventType.EVENTS_CHANGED, null, null, -1,
                ViewType.CURRENT);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mSearchMenu.collapseActionView();
        mController.sendEvent(this, EventType.SEARCH, null, null, -1,
                ViewType.CURRENT, 0, query, getComponentName());
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
        case CalendarViewAdapter.MONTH_BUTTON_INDEX:
            if (mCurrentView != ViewType.DAY) {
                mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                        ViewType.DAY);
            }
            break;
        case CalendarViewAdapter.WEEK_BUTTON_INDEX:
            if (mCurrentView != ViewType.WEEK) {
                mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                        ViewType.WEEK);
            }
            break;
        case CalendarViewAdapter.DAY_BUTTON_INDEX:
            if (mCurrentView != ViewType.MONTH) {
                mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                        ViewType.MONTH);
            }
            break;
        case CalendarViewAdapter.AGENDA_BUTTON_INDEX:
            if (mCurrentView != ViewType.AGENDA) {
                mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                        ViewType.AGENDA);
            }
            break;
        /* modify start liaozhenbin 2017/6/19 */
        case CalendarViewAdapter.YEAR_BUTTON_INDEX:
            break;
        /* modify end liaozhenbin 2017/6/19 */
        default:
            Log.w(TAG, "ItemSelected event from unknown button: "
                    + itemPosition);
            Log.w(TAG, "CurrentView:" + mCurrentView + " Button:"
                    + itemPosition + " Day:" + mDayTab + " Week:" + mWeekTab
                    + " Month:" + mMonthTab + " Agenda:" + mAgendaTab);
            break;
        }
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        mSearchMenu.collapseActionView();
        return false;
    }

    @Override
    public boolean onSearchRequested() {
        if (mSearchMenu != null) {
            mSearchMenu.expandActionView();
        }
        return false;
    }

    @Override
    public void onViewModeChanged(int newMode) {
        boolean oldVisiableState = mFullSizeMenuVisible;
        if (ViewMode.isWeekMode(newMode)) {
            mFullSizeMenuVisible = false;
        } else {
            mFullSizeMenuVisible = true;
        }
        if (!(mFullSizeMenuVisible && oldVisiableState)) {
            invalidateOptionsMenu();
        }
    }

    // 20151116: add runtime permission by zhxj
    // @Override
    // public void onRequestPermissionsResult(int requestCode, String[]
    // permissions, int[] grantResults) {
    // switch (requestCode) {
    // case Utils.REQUEST_PERMISSIONS_CALENDAR:
    // if (grantResults != null && grantResults.length > 0) {
    // if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
    // // mhasCalendarPermission = true;
    // // addCalendarAccounts();
    // Utils.showMessage((Context) this,
    // getResources().getString(R.string.permissions_granted_tip),
    // getResources().getString(R.string.permissions_title), false,
    // new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // dialog.dismiss();
    // finish();
    // }
    // });
    // } else {
    // String permissionName =
    // getResources().getString(R.string.permissions_name_calendar);
    // Toast.makeText(this, this.getString(R.string.permissions_denied_tip,
    // permissionName),
    // Toast.LENGTH_LONG).show();
    // }
    // }
    // break;
    // case LocationUtils.REQUEST_LOCATION_PERMISSION:
    // if (grantResults.length > 0 && grantResults[0] ==
    // PackageManager.PERMISSION_GRANTED) {
    //
    // } else {
    // Toast.makeText(mContext, mContext.getString(R.string.permission_deny),
    // Toast.LENGTH_LONG).show();
    // LogUtils.e(
    // "Manifest.permission.ACCESS_FINE_LOCATION |
    // Manifest.permission.ACCESS_COARSE_LOCATION PERMISSION DENY");
    // }
    // break;
    // default:
    // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    // break;
    // }
    // }

    // private void addCalendarAccounts() {
    // addMycalendar();
    //
    // // Launch add google account if this is first time and there are no
    // // accounts yet
    // if (mCheckForAccounts && !Utils.getSharedPreference(this,
    // GeneralPreferences.KEY_SKIP_SETUP, false)) {
    //
    // mHandler = new QueryHandler(this.getContentResolver());
    // mHandler.startQuery(0, null, Calendars.CONTENT_URI, new String[] {
    // Calendars._ID }, null,
    // null /*
    // * selection args
    // */, null /*
    // * sort order
    // */);
    // }
    // }

    public void refreshAgendaFragment() {
        Fragment frag = this.getFragmentManager().findFragmentById(
                R.id.main_pane);
        if (frag instanceof AgendaFragment) {
            ((AgendaFragment) frag).eventsChanged();
        } else if (frag instanceof DayFragment) {
            ((DayFragment) frag).eventsChanged();
        }
    }

    private long parseTimefromSharePref(Context context) {
        long timeMillis = -1;
        Time time = new Time();
        time.setToNow();
        SharedPreferences displayMonth = context.getSharedPreferences(
                "display_month", 0);
        int year = displayMonth.getInt("year", time.year);
        int month = displayMonth.getInt("month", time.month);
        int day = displayMonth.getInt("day", 1);
        time.set(day, month, year);
        timeMillis = time.toMillis(false);
        // Log.d(TAG,"timeMillis = " + timeMillis + ", " + time.toString() );
        return timeMillis;
    }

}
