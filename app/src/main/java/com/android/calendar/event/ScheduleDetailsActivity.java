package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;
import com.android.calendar.event.BirthdayFragment.PopupDismissListener;
import com.android.calendar.utils.DateUtils;
import com.android.calendar.utils.EventUtils;
import com.gm.internal.menu.FloatActionMenuView;
import com.gm.internal.menu.FloatActionMenuView.OnFloatActionMenuSelectedListener;
import com.gome.gmtimepicker.util.DateFormatter;
import com.gome.gmtimepicker.view.GMTimePicker;
import com.hct.calendar.ui.OneLineCheckboxLayout;
import com.hct.calendar.ui.OneLineCheckboxLayout.OnCheckedChangeListener;
import com.hct.calendar.utils.CalendarUtil;

import android.R.integer;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import gm.app.GomeAlertDialog;
import gm.app.GomeAlertDialog.Builder;
import gm.widget.GomeSwitch;

public class ScheduleDetailsActivity extends Activity {
    private FloatActionMenuView fView;
    private ImageView backIcon;
    public TextView titleView;
    public TextView startTimeView;
    public TextView startDateView;
    public TextView endTimeView;
    public TextView endDateView;
    private TextView reminderView;
    private ViewGroup reminderViewGroup;
    private TextView descView;
    private TextView locationView;
    private ImageView dianImageView;
    private ViewGroup repeatLayout;
    // public ScheduleEvents event;
    private TextView rruleTextView;
    private int eventId;
    private int allDay;

    private ViewGroup timeViewGroup;
    private ViewGroup allDayViewGroup;
    private TextView alldayTextView;

    private ViewGroup locationGroup;
    private String[] remindTextStrings;
    private String[] dialogRemindStrings;
    private String[] allDayRemindStrings;
    private long dtstart;
    boolean isAdvance;
    private GomeSwitch gs_nongli;
    PopupWindow mPopupWindow;
    GMTimePicker timePicker;

    String rruleString = null;
    String title = null;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    int eventColor;
    String timeArea;
    String location;
    // String attend;
    StringBuilder attendStringBuilder;
    String desc;
    int reminderTime;
    ContentResolver resolver;
    String allDayString;
    private TextView tvTitleFirst;
    private TextView tvTitleSecond;
    private ViewGroup rlAttendee;
    private TextView tvContentAttendee;
    private TextView tvBirthdayDate;
    private ViewGroup desGroup;
    private boolean mIsSchedule;
    private long mBeginTime;
    private long mEndTime;
    private ViewGroup birthdayGroup;
    private String advance3Day = CalendarApplication.getContext().getString(R.string.advance_3days);
    private String birthdayReminder = CalendarApplication.getContext().getString(R.string.birthday_reminder);

    private List<ReminderBean> reminderBeans = EventUtils.getReminderBeans();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_details_activity);
        Log.d("2877", "ScheduleDetailsActivity: " + this.toString());
        getActionBar().hide();
        // StatusBarUtils.setStatusBarLightMode(this,
        // Color.parseColor("#f1f2f3"));
        Intent intent = this.getIntent();
        eventId = (int) intent.getExtra("id");
        // event = (ScheduleEvents) intent.getSerializableExtra("event");
        boolean cancelNotification = intent.getExtra("notification") == null ? false
                : (boolean) intent.getExtra("notification");
        if (cancelNotification) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel((int) eventId);
        }
        findAllView();
        initData();
        setAllListener();
    }

    private void initData() {
        attendStringBuilder = new StringBuilder();
        remindTextStrings = getResources().getStringArray(R.array.schedule_reminds_time_str);
        allDayRemindStrings = getResources().getStringArray(R.array.preferences_allday_reminder_labels);
        dialogRemindStrings = getResources().getStringArray(R.array.preferences_default_reminder_labels);
        dtstart = 0;
        long dtend = 0;
        isAdvance = false;
        String duration;
        resolver = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Log.e("fushuo00000", "eventId=" + eventId);
        Cursor cursor = resolver.query(uri, null, "_id=?", new String[] { "" + eventId }, null);
        while (cursor.moveToNext()) {
            String rrule = cursor.getString(cursor.getColumnIndex("rrule"));
            rruleString = getRruleString(rrule);
            title = cursor.getString(cursor.getColumnIndex("title"));
            dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
            dtend = cursor.getLong(cursor.getColumnIndex("dtend"));
            eventColor = cursor.getInt(cursor.getColumnIndex("eventColor"));
            timeArea = cursor.getString(cursor.getColumnIndex(Events.EVENT_TIMEZONE));
            location = cursor.getString(cursor.getColumnIndex(Events.EVENT_LOCATION));
            desc = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION));
            reminderTime = cursor.getInt(cursor.getColumnIndex(Events.ORIGINAL_INSTANCE_TIME));
            Log.e("liaozhenbin", "reminderTime=" + reminderTime);
            allDay = cursor.getInt(cursor.getColumnIndex(Events.ALL_DAY));
            duration = cursor.getString(cursor.getColumnIndex(Events.DURATION));
            isAdvance = cursor.getInt(cursor.getColumnIndex(Events.AVAILABILITY)) == 1;
            if (dtend == 0 && null != duration) {
                dtend = dtstart + DateUtils.getDuration(duration) * 1000;
            }
        }
        Log.d("2877", "dtstart: " + dtstart);
        Log.d("2877", "dtend: " + dtend);

        Cursor cursor2 = resolver.query(Attendees.CONTENT_URI, null, "event_id=?", new String[] { "" + eventId }, null);
        while (cursor2.moveToNext()) {
            String name = cursor2.getString(cursor2.getColumnIndex(Attendees.ATTENDEE_NAME));
            attendStringBuilder.append(name);
            attendStringBuilder.append(";");
        }

        titleView.setText(title);
        mBeginTime = dtstart;
        mEndTime = dtend;
        if (allDay == 1) {
            timeViewGroup.setVisibility(ViewGroup.GONE);
            allDayViewGroup.setVisibility(View.VISIBLE);
            SimpleDateFormat formatDate = new SimpleDateFormat(getString(R.string.date_format_week));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dtstart);
            allDayString = formatDate.format(calendar.getTime());
            alldayTextView.setText(allDayString);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTimeInMillis(dtend);

            startDate = formatDate.format(calendar.getTime());
            endDate = formatDate.format(endCalendar.getTime());
            startTime = DateUtils.getNormalTime(this, dtstart);
            endTime = DateUtils.getNormalTime(this, dtend);

        } else {
            timeViewGroup.setVisibility(ViewGroup.VISIBLE);
            allDayViewGroup.setVisibility(View.GONE);
            SimpleDateFormat formatDate = new SimpleDateFormat(getString(R.string.date_format_deafult));
            SimpleDateFormat formatTime = new SimpleDateFormat(DateUtils.is24(this) ? "HH:mm" : "hh:mm");
            Calendar stCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            stCalendar.setTimeInMillis(dtstart);
            endCalendar.setTimeInMillis(dtend);

            startDate = formatDate.format(stCalendar.getTime());

            endDate = formatDate.format(endCalendar.getTime());

            startTime = DateUtils.getNormalTime(this, dtstart);

            endTime = DateUtils.getNormalTime(this, dtend);

            startTimeView.setText(DateUtils.getNormalTime(this, dtstart));
            startDateView.setText(startDate);
            endTimeView.setText(DateUtils.getNormalTime(this, dtend));
            endDateView.setText(endDate);
            Log.d("2877", "endDate: " + endDate);
        }
        Log.d("zyp", "zyp -------      :" + rruleString);
        if (!getString(R.string.event_one_time).equals(rruleString)) {
            repeatLayout.setVisibility(View.VISIBLE);
            rruleTextView.setText(rruleString);
        } else {
            repeatLayout.setVisibility(View.GONE);
            rruleTextView.setText(rruleString);
        }
        // String[] allday_single_list =
        // getResources().getStringArray(R.array.preferences_allday_reminder_labels);
        descView.setText(desc);
        locationView.setText(location);
        reminderView.setText(remindTextStrings[reminderTime]);
        // switch (reminderTime) {
        // case 0:
        // reminderView.setText((1 == allDay) ? allday_single_list[1] :
        // this.getString(R.string.event_on_time));
        // break;
        // case 60:
        // reminderView.setText(getResources().getString(R.string.one_hour_reminder));
        // break;
        // case 60 * 24:
        // reminderView.setText(
        // (1 == allDay) ? allday_single_list[2] :
        // getResources().getString(R.string.one_day_reminder));
        // break;
        // case 60 * 24 * 2:
        // if (1 == allDay)
        // reminderView.setText(allday_single_list[3]);
        // break;
        // case 60 * 24 * 7:
        // reminderView.setText(getResources().getString(R.string.one_week_reminder));
        // break;
        // case -1:
        // reminderView.setText(getResources().getString(R.string.dismiss_label));
        // break;
        // default:
        // reminderView.setText(reminderTime > 0
        // ? this.getString(R.string.event_in_advance) + reminderTime +
        // this.getString(R.string.event_minute)
        // : this.getString(R.string.event_on_time));
        // break;
        // }

        int color = R.drawable.dian_shape_other;
        String firstExtra = getString(R.string.remarks);
        String secondExtra = getString(R.string.location);
        mIsSchedule = true;
        switch (eventColor) {
        case Constant.BIRTHDAY:
            color = R.drawable.dian_shape_birthday;
            desGroup.setVisibility(View.GONE);
            locationGroup.setVisibility(View.GONE);
            timeViewGroup.setVisibility(ViewGroup.GONE);
            birthdayGroup.setVisibility(View.VISIBLE);
            tvBirthdayDate.setText(startDate);
            reminderView.setText(
                    String.format(isAdvance ? advance3Day : birthdayReminder, DateUtils.getNormalTime(this, dtstart)));
            mIsSchedule = false;
            break;
        case Constant.MEETING:
            color = R.drawable.dian_shape_meetting;
            secondExtra = getString(R.string.conference_room);
            rlAttendee.setVisibility(View.VISIBLE);
            tvContentAttendee.setText(attendStringBuilder.toString());
            mIsSchedule = false;
            break;
        default:
            break;
        }
        tvTitleFirst.setText(firstExtra);
        tvTitleSecond.setText(secondExtra);
        dianImageView.setBackgroundResource(color);
    }

    private void updateStartEndTimeText() {
        if (mBeginTime != 0 && mEndTime != 0) {
            String beginDate = DateFormatter.format(mBeginTime, getResources());
            String beginTime = DateFormatter.formatClock(mBeginTime, this);
            String endDate = DateFormatter.format(mEndTime, getResources());
            String endTime = DateFormatter.formatClock(mEndTime, this);
            startTimeView.setText(beginTime);
            startDateView.setText(beginDate);
            endTimeView.setText(endTime);
            endDateView.setText(endDate);
        }
    }

    private String getRruleString(String s) {
        String rruleString = null;
        if (s == null) {
            rruleString = this.getString(R.string.event_one_time);
        } else if (s.equals("FREQ=DAILY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_day);
        } else if (s.equals("FREQ=HOURLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_hour);
        } else if (s.equals("FREQ=WEEKLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_week);
        } else if (s.equals("FREQ=MONTHLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_month);
        } else if (s.equals("FREQ=YEARLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_year);
        }
        return rruleString;
    }

    private void findAllView() {
        fView = (FloatActionMenuView) findViewById(R.id.floatMenu);
        backIcon = (ImageView) findViewById(R.id.schedule_back);
        titleView = (TextView) findViewById(R.id.tv_title);
        startTimeView = (TextView) findViewById(R.id.start_time_time);
        startDateView = (TextView) findViewById(R.id.start_time_date);
        endTimeView = (TextView) findViewById(R.id.end_time_time);
        endDateView = (TextView) findViewById(R.id.end_time_date);
        rruleTextView = (TextView) findViewById(R.id.repeat_text_tv);
        reminderView = (TextView) findViewById(R.id.reminder_text_tv);
        reminderViewGroup = (ViewGroup) findViewById(R.id.reminder_layout);
        descView = (TextView) findViewById(R.id.des_text_tv);
        locationView = (TextView) findViewById(R.id.location_text_tv);
        dianImageView = (ImageView) findViewById(R.id.dian);
        repeatLayout = (ViewGroup) findViewById(R.id.repeat_layout);
        timeViewGroup = (ViewGroup) findViewById(R.id.time_view);
        allDayViewGroup = (ViewGroup) findViewById(R.id.all_day_layout);
        alldayTextView = (TextView) findViewById(R.id.allday_date);
        locationGroup = (ViewGroup) findViewById(R.id.location_layout);
        tvTitleFirst = (TextView) findViewById(R.id.tv_des);
        tvTitleSecond = (TextView) findViewById(R.id.tv_location);
        rlAttendee = (ViewGroup) findViewById(R.id.attendee_layout);
        tvContentAttendee = (TextView) findViewById(R.id.tv_attendee_content);
        tvBirthdayDate = (TextView) findViewById(R.id.tv_birthday);
        desGroup = (ViewGroup) findViewById(R.id.des_layout);
        birthdayGroup = (ViewGroup) findViewById(R.id.birthday_layout);
    }

    private void setAllListener() {
        reminderViewGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                showReminderChoiceDialog();
            }
        });
        fView.setOnFloatActionMenuSelectedListener(new OnFloatActionMenuSelectedListener() {

            @Override
            public boolean onFloatActionItemSelected(MenuItem item) {
                final int itemId = item.getItemId();
                switch (itemId) {
                case R.id.float_menu_edit:
                    Intent intent = new Intent(ScheduleDetailsActivity.this, EditEventActivity.class);
                    intent.putExtra("id", eventId);
                    intent.putExtra(Events.EVENT_COLOR, eventColor);
                    intent.putExtra("editEvent", true);
                    ScheduleDetailsActivity.this.startActivityForResult(intent, 1);
                    break;
                case R.id.float_menu_enjoy:
                    Intent intentEnjoy = new Intent(Intent.ACTION_SEND);
                    StringBuilder eventString = new StringBuilder();

                    switch (eventColor) {
                    case Constant.SCHEDULE:
                    default:
                        eventString.append(getString(R.string.category_activity)).append('\n')
                                .append(getString(R.string.event_share_title) + title).append('\n');
                        eventString.append(getString(R.string.event_share_time_zone) + timeArea).append('\n')
                                .append(getString(R.string.when_label) + startDate).append('\n');
                        eventString.append(startTime + "-" + endTime).append('\n');
                        if (!TextUtils.isEmpty(location)) {
                            eventString.append(getString(R.string.where_label) + location).append('\n');
                        }
                        if (!TextUtils.isEmpty(desc)) {
                            eventString.append(getString(R.string.event_share_remark) + desc).append('\n');
                        }

                        eventString.append(getString(R.string.event_share_alert) + remindTextStrings[reminderTime]);

                        break;
                    case Constant.MEETING:
                        eventString.append(getString(R.string.category_meeting)).append('\n')
                                .append(getString(R.string.event_share_title) + title).append('\n');
                        eventString.append(getString(R.string.event_share_time_zone) + timeArea).append('\n')
                                .append(getString(R.string.when_label) + startDate).append('\n');
                        eventString.append(startTime + "-" + endTime).append('\n');
                        if (!TextUtils.isEmpty(location)) {
                            eventString.append(getString(R.string.event_share_meetingroom) + location).append('\n');
                        }
                        if (!TextUtils.isEmpty(attendStringBuilder.toString())) {
                            eventString.append(getString(R.string.event_share_meetingpeople)).append('\n')
                                    .append(attendStringBuilder.toString()).append('\n');
                        }
                        if (!TextUtils.isEmpty(desc)) {
                            eventString.append(getString(R.string.event_share_remark) + desc);
                        }

                        break;
                    case Constant.BIRTHDAY:
                        eventString.append(getString(R.string.category_birthday)).append('\n')
                                .append(getString(R.string.birthday_person) + ":" + title).append('\n');
                        eventString.append(getString(R.string.event_share_time_zone) + timeArea).append('\n')
                                .append(getString(R.string.event_share_time) + startDate).append('\n');
                        eventString.append(getString(R.string.event_share_alert) + rruleString);
                        break;
                    // default:
                    // eventString.append(getString(R.string.category_activity)).append('\n')
                    // .append(getString(R.string.event_share_title) +
                    // title).append('\n');
                    // eventString.append(getString(R.string.event_share_time_zone)
                    // + timeArea).append('\n')
                    // .append(getString(R.string.when_label) +
                    // startDate).append('\n');
                    // eventString.append(startTime + "-" +
                    // endTime).append('\n')
                    // .append(getString(R.string.where_label) +
                    // location).append('\n')
                    // .append(getString(R.string.event_share_remark) +
                    // desc);
                    //
                    // eventString.append('\n')
                    // .append(getString(R.string.event_share_alert) +
                    // remindTextStrings[reminderTime]);

                    // break;
                    }
                    intentEnjoy.putExtra(Intent.EXTRA_TEXT, eventString.toString());
                    intentEnjoy.setType("text/plain");
                    startActivity(Intent.createChooser(intentEnjoy, getString(R.string.share_label)));
                    break;
                case R.id.float_menu_delete:
                    showDeleteDialog();
                    break;

                default:
                    break;
                }
                return true;
            }

        });

        backIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                ScheduleDetailsActivity.this.finish();
            }
        });

        if (mIsSchedule) {
            locationGroup.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (location != null && !location.isEmpty()) {
                        // Uri mUri = Uri.parse("geo:39.940409,116.355257?q=" +
                        // location);
                        // Intent mIntent = new Intent(Intent.ACTION_VIEW,
                        // mUri);
                        // try {
                        // startActivity(mIntent);
                        // } catch (Exception e) {
                        // e.printStackTrace();
                        // }

                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        String data;
                        if (isAvailable("com.autonavi.minimap")) {
                            data = String.format("androidamap://poi?sourceApplication=softname&keywords=%s", location);
                            Uri uri = Uri.parse(data);
                            intent.setData(uri);
                            startActivity(intent);
                        } else if (isAvailable("com.baidu.BaiduMap")) {
                            data = String.format("baidumap://map/geocoder?src=openApiDemo&address=%s", location);

                        } else {
                            data = String.format("http://api.map.baidu.com/geocoder?address=%s&output=html", location);
                        }
                        Uri uri = Uri.parse(data);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private boolean isAvailable(String packageName) {
        final PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        return packageNames.contains(packageName);
    }

    private void showDeleteDialog() {
        // TODO Auto-generated method stub
        Builder builder = new GomeAlertDialog.Builder(this);
        // builder.setMessage(getString(R.string.event_share_delete));
        builder.setTitle(getString(R.string.event_share_delete));
        builder.setPositiveButton(getString(R.string.event_share_confirm), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                DeleteSelectedEvents();

            }

        });
        builder.setNegativeButton(getString(R.string.event_share_cancel), new OnClickListener() {

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
        int num = resolver.delete(uri, "_id=?", new String[] { "" + eventId });

        // remove Notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel((int) eventId);
        // remove remind type
        SharedPreferences sp = getSharedPreferences("schedule", Context.MODE_PRIVATE);
        String key = EventUtils.INDEX + eventId;
        sp.edit().remove(key).commit();

        if (num < 0) {
            Log.d("liao", "fail!");
        } else {
            ScheduleDetailsActivity.this.setResult(RESULT_OK);
        }
        ScheduleDetailsActivity.this.finish();
    }

    private void showReminderChoiceDialog() {
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(this);
        GomeAlertDialog dialog;
        switch (eventColor) {
        case Constant.SCHEDULE:
        default:
            builder.setTitle(getString(R.string.event_alert_title));
            if (allDay == 1) {
                builder.setSingleChoiceItems(allDayRemindStrings, reminderTime - dialogRemindStrings.length,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reminderTime = which + dialogRemindStrings.length;
                                reminderView.setText(remindTextStrings[reminderTime]);
                                dialog.dismiss();
                            }
                        });
            } else {
                builder.setSingleChoiceItems(dialogRemindStrings, reminderTime, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reminderTime = which;
                        reminderView.setText(remindTextStrings[reminderTime]);
                        EventUtils.updateRemind(ScheduleDetailsActivity.this, eventId, eventColor, reminderTime);
                        dialog.dismiss();
                    }
                });
            }
            builder.setNegativeButton(getString(R.string.discard_label), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog = builder.create();
            dialog.show();

            break;
        case Constant.MEETING:
            builder.setTitle(getString(R.string.event_alert_title));
            builder.setSingleChoiceItems(dialogRemindStrings, reminderTime, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reminderView.setText(dialogRemindStrings[which]);
                    reminderTime = which;
                    EventUtils.updateRemind(ScheduleDetailsActivity.this, eventId, eventColor, reminderTime);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.discard_label), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                }
            });
            dialog = builder.create();
            dialog.show();
            break;
        case Constant.BIRTHDAY:
            showBirthdayRemindPopup();
            break;

        }

    }

    private void showBirthdayRemindPopup() {
        // isShowDialog = true;
        // HideSoftInputMethod();
        WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        // int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);

        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95), LayoutParams.WRAP_CONTENT);
        // ScreenInfo screenInfoDate = new ScreenInfo(getActivity());
        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        timePicker.setMode(GMTimePicker.FLAG_HOUR | GMTimePicker.FLAG_MINUTE);
        timePicker.setCurrentTime(dtstart);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(reminderView, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.3f);
        getWindow().setNavigationBarColor(getResources().getColor(R.color.choose_time_nav));
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        TextView tv_select_date = (TextView) menuView.findViewById(R.id.tv_select_date);
        tv_select_date.setVisibility(View.INVISIBLE);
        tv_pop_title.setText(getString(R.string.event_alert_date));
        final OneLineCheckboxLayout cb_show = (OneLineCheckboxLayout) menuView.findViewById(R.id.cb_show_yang);
        cb_show.setText(getString(R.string.event_alert_three_day));
        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);
        ViewGroup nongliViewGroup = (ViewGroup) menuView.findViewById(R.id.view_nongli);
        nongliViewGroup.setVisibility(View.GONE);
        cb_show.setVisibility(View.VISIBLE);
        cb_show.setTextColor("#D0D0D0");
        Log.d("liaozhenbin", "setChecked: " + isAdvance);
        cb_show.setChecked(isAdvance);
        cb_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onChange(boolean checked) {
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isAdvance = cb_show.isChecked();
                Log.d("liaozhenbin", "isAdvance: " + isAdvance);
                dtstart = timePicker.getCurrentTime().getTimeInMillis();
                reminderView.setText(String.format(isAdvance ? advance3Day : birthdayReminder,
                        DateUtils.getNormalTime(ScheduleDetailsActivity.this, dtstart)));
                EventUtils.updateRemind(ScheduleDetailsActivity.this, eventId,dtstart,isAdvance);
                mPopupWindow.dismiss();
            }
        });
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
            mPopupWindow = null;
        }

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            setResult(RESULT_OK);
            initData();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        DateFormatter.clearCache();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Cursor cursor = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, "_id=?",
                new String[] { "" + eventId }, null);
        if (!(cursor != null && cursor.moveToFirst())) {
            finish();
            return;
        }
        updateStartEndTimeText();
    }

}
