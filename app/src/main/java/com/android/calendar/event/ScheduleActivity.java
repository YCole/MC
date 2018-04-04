package com.android.calendar.event;

import java.util.Calendar;
import java.util.List;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.month.HolidayAndTermActivity;
import com.android.calendar.utils.EventUtils;
import com.android.calendar.utils.StatusBarUtils;
import com.apkfuns.logutils.LogUtils;
import gm.app.GomeAlertDialog;
import gm.app.GomeAlertDialog.Builder;
import com.gm.internal.menu.FloatActionMenuView;
import com.gm.internal.menu.FloatActionMenuView.OnFloatActionMenuSelectedListener;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.ToastHelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.support.v4.view.ViewCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ScheduleActivity extends Activity implements View.OnClickListener, OnLayoutChangeListener {
    private EditText searchEditText;
    private Context mContext;
    private CalendarController mController;
    public FloatActionMenuView fView;

    private ImageView backIcon;
    private ViewGroup titleView;
    private ViewGroup searchViewTitle;
    private ViewGroup selectView;
    private ViewGroup searchView;
    private ViewGroup actionViewTitle;
    private TextView searchCancel;

    TranslateAnimation mShowAction;
    TranslateAnimation mHiddenAction;

    private int titleStatus;

    public static final int TITLE_INDEX = 0;
    public static final int TITLE_SEARCH = 1;
    public static final int TITLE_DETELE = 2;

    private int screenHeight = 0;
    private int keyHeight = 0;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        Log.d("2877",  "ScheduleActivity: "+this.toString());
        getActionBar().hide();
        // StatusBarUtils.setStatusBarLightMode(this,
        // Color.parseColor("#f1f2f3"));
        mContext = this;
        mController = CalendarController.getInstance(this);
        findAllView();
        setAllListener();
        // modify by chenhuaiyu for bug PRODUCTION-3341 fragment
        // NullPointerException
        replaceFragment(new AllScheduleFragment(), "index");
        setTitleStatus(TITLE_INDEX);
        mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        mShowAction.setDuration(500);
        mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mHiddenAction.setDuration(500);

        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        keyHeight = screenHeight / 3;
    }

    private void findAllView() {
        fView = (FloatActionMenuView) findViewById(R.id.floatMenu);
        backIcon = (ImageView) findViewById(R.id.schedule_back);
        backIcon.setOnClickListener(this);
        titleView = (ViewGroup) findViewById(R.id.title_view);
        searchViewTitle = (ViewGroup) findViewById(R.id.search_view_title);
        selectView = (ViewGroup) findViewById(R.id.select_view);
        actionViewTitle = (ViewGroup) findViewById(R.id.action_title);
        searchViewTitle.setOnClickListener(this);
        searchView = (ViewGroup) findViewById(R.id.search_view);
        searchCancel = (TextView) findViewById(R.id.tv_search_cancel);
        searchEditText = (EditText) findViewById(R.id.et_search);
        searchCancel.setOnClickListener(this);
        rootView = findViewById(R.id.root_view);
        rootView.addOnLayoutChangeListener(this);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (CalendarUtil.isEnglish())
            fView.setMenuItemsInVisible(R.id.float_menu_holiday);
    }

    private void setAllListener() {

        fView.setOnFloatActionMenuSelectedListener(new OnFloatActionMenuSelectedListener() {

            @Override
            public boolean onFloatActionItemSelected(MenuItem item) {
                final int itemId = item.getItemId();
                switch (itemId) {
                case R.id.float_menu_event_create:
                    Time t;
                    t = new Time();
                    // t.setToNow();
                    Calendar systemTime = Calendar.getInstance();
                    systemTime.setTimeInMillis(System.currentTimeMillis());
                    Calendar ca = Calendar.getInstance();
                    ca.setTimeInMillis(mController.getTime());
                    ca.set(Calendar.HOUR_OF_DAY, systemTime.get(Calendar.HOUR_OF_DAY));
                    ca.set(Calendar.MINUTE, systemTime.get(Calendar.MINUTE));
                    ca.set(Calendar.SECOND, systemTime.get(Calendar.SECOND));
                    t.set(ca.getTimeInMillis());
                    if (t.minute >= 30) {
                        t.hour++;
                        t.minute = 0;
                    } else if (t.minute >= 0 && t.minute < 30) {
                        t.minute = 30;
                    }
                    // mController.sendEventRelatedEvent(this,
                    // EventType.CREATE_EVENT, -1, t.toMillis(true), 0, 0, 0,
                    // -1);
                    ScheduleActivity.this
                            .startActivityForResult(new Intent(ScheduleActivity.this, EditEventActivity.class), 1);
                    break;
                case R.id.float_menu_calendar:
                    ScheduleActivity.this.finish();
                    break;
                // case R.id.float_menu_more:
                // showMorePopWindow(fView);
                // break;
                case R.id.float_menu_today:
                    ToastHelper.show(mContext, "today....");
                    break;

                case R.id.float_menu_holiday:
                    Intent holidayIntent = new Intent(Intent.ACTION_VIEW);
                    holidayIntent.setClass(mContext, HolidayAndTermActivity.class);
                    // holidayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(holidayIntent);
                    break;

                case R.id.float_menu_setting:
                    mController.sendEvent(this, EventType.LAUNCH_SETTINGS, null, null, 0, 0);
                    break;
                case R.id.float_menu_select_delete:
                    AllScheduleFragment fragment = (AllScheduleFragment) getFragmentManager()
                            .findFragmentByTag("index");
                    List<ScheduleEvents> selectedList = fragment.getSelectedList();
                    if (selectedList.size() > 0) {
                        showDeleteDialog();
                    }
                    break;
                }
                return true;
            }
        });

    }

    private void showDeleteDialog() {
        // TODO Auto-generated method stub
        Builder builder = new GomeAlertDialog.Builder(this);
        // builder.setMessage(this.getString(R.string.event_share_delete));
        builder.setTitle(this.getString(R.string.event_share_delete));
        builder.setPositiveButton(this.getString(R.string.event_share_confirm), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                DeleteSelectedEvents();

                setTitleStatus(TITLE_INDEX);
            }

        });
        builder.setNegativeButton(this.getString(R.string.event_share_cancel), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

            }
        });
        builder.create().show();
    }

    private void DeleteSelectedEvents() {
        ContentResolver resolver = getContentResolver();
        Uri uri = Events.CONTENT_URI;
        AllScheduleFragment fragment = (AllScheduleFragment) getFragmentManager().findFragmentByTag("index");
        List<ScheduleEvents> selectedList = fragment.getSelectedList();
        int count = 0;
        int total = selectedList.size();
        for (ScheduleEvents event : selectedList) {
            int num = resolver.delete(uri, "_id=?", new String[] { "" + event.getId() });
            if (num < 0) {
                // ToastHelper.show(mContext, "delete failed....");
                break;
            } else {
                count++;
            }

            // remove Notification
            int eventId = event.getId();
            NotificationManager notificationManager = (NotificationManager) mContext
                    .getSystemService(mContext.NOTIFICATION_SERVICE);
            notificationManager.cancel(eventId);

            // remove remind type
            SharedPreferences sp = getSharedPreferences("schedule", Context.MODE_PRIVATE);
            String key = EventUtils.INDEX + eventId;
            sp.edit().remove(key).commit();

        }
        if (count == total) {
            // ToastHelper.show(mContext, "delete success....");
        }

        fragment.getCalendarData();
        fragment.getmAdapter().notifyDataSetChanged();
    }

    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(statusColor);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }

    // private void showMorePopWindow(View view) {
    // final PopupWindow popWnd = new PopupWindow(this);
    // View contentView =
    // LayoutInflater.from(this).inflate(R.layout.pop_schedule_window_layout,
    // null);
    // contentView.findViewById(R.id.pop_tv_02).setOnClickListener(new
    // View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // LogUtils.e("tv 02 .....");
    // Intent holidayIntent = new Intent(Intent.ACTION_VIEW);
    // holidayIntent.setClass(mContext, HolidayAndTermActivity.class);
    // // holidayIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    // startActivity(holidayIntent);
    // popWnd.dismiss();
    // }
    // });
    // contentView.findViewById(R.id.pop_tv_03).setOnClickListener(new
    // View.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // mController.sendEvent(this, EventType.LAUNCH_SETTINGS, null, null, 0, 0);
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

    @Override
    public void onClick(View v) {
        if (v == searchViewTitle) {
            setTitleStatus(TITLE_SEARCH);
            // titleView.setAnimation(mHiddenAction);
            // titleView.setVisibility(View.GONE);
            // searchView.setAnimation(mShowAction);
            // searchView.setVisibility(View.VISIBLE);
            replaceFragment(new ScheduleEqueryFragment(), "search");
        } else if (v == backIcon) {
            finish();
        } else if (v == searchCancel) {
            // titleView.startAnimation(mShowAction);
            // titleView.setVisibility(View.VISIBLE);
            // searchView.setAnimation(mHiddenAction);
            // searchView.setVisibility(View.GONE);
            setTitleStatus(TITLE_INDEX);
            if (searchEditText != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
            searchEditText.setText("");
            replaceFragment(new AllScheduleFragment(), "index");
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.schedule_frame_layout, fragment, tag);
        transaction.commit();
    }

    public int getTitleStatus() {
        return titleStatus;
    }

    public void setTitleStatus(int titleStatus) {
        this.titleStatus = titleStatus;
        AllScheduleFragment fragment = (AllScheduleFragment) getFragmentManager().findFragmentByTag("index");
        switch (titleStatus) {
        case TITLE_INDEX:
            fView.inflateMenu(R.menu.schedule_menu);
            if (CalendarUtil.isEnglish())
                fView.setMenuItemsInVisible(R.id.float_menu_holiday);
            searchViewTitle.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.VISIBLE);
            actionViewTitle.setVisibility(View.VISIBLE);
            selectView.setVisibility(View.GONE);
            searchView.setVisibility(View.GONE);
            if (null != fragment && fragment.getActivity() != null && fragment.isAdded()) {
                fragment.setListLongClick();
                fragment.setDeleteStatus(false);
            }
            break;
        case TITLE_SEARCH:
            searchView.setVisibility(View.VISIBLE);
            fView.inflateMenu(R.menu.schedule_menu);
            if (CalendarUtil.isEnglish())
                fView.setMenuItemsInVisible(R.id.float_menu_holiday);
            titleView.setVisibility(View.GONE);
            if (null != fragment) {
                fragment.setDeleteStatus(false);
            }
            break;
        case TITLE_DETELE:
            searchViewTitle.setVisibility(View.GONE);
            selectView.setVisibility(View.VISIBLE);
            fView.inflateMenu(R.menu.select_delete);
            actionViewTitle.setVisibility(View.GONE);
            if (null != fragment) {
                fragment.setDeleteStatus(true);
            }

            break;
        default:
            break;
        }
    }

    public void setDelete(boolean b) {
        if (b) {
            fView.setMenuItemsEnable(R.id.float_menu_select_delete);
        } else {
            fView.setMenuItemsDisable(R.id.float_menu_select_delete);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            AllScheduleFragment fragment = (AllScheduleFragment) getFragmentManager().findFragmentByTag("index");
            if (fragment != null) {
                fragment.renovateData();
            }

        }
    }

    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight,
            int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            fView.setVisibility(View.GONE);
        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            fView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        // // TODO Auto-generated method stub
        if (titleStatus == TITLE_DETELE) {
            AllScheduleFragment fragment = (AllScheduleFragment) getFragmentManager().findFragmentByTag("index");
            fragment.cleanDelete();
        } else if (titleStatus == TITLE_INDEX) {
            super.onBackPressed();
        } else if (titleStatus == TITLE_SEARCH) {
            setTitleStatus(TITLE_INDEX);
            if (searchEditText != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            }
            searchEditText.setText("");
            replaceFragment(new AllScheduleFragment(), "index");
        }
    }
}
