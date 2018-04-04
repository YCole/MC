package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.android.calendar.CalendarApplication;
import com.android.calendar.CalendarSettings;
import com.android.calendar.R;
import com.android.calendar.event.RepeatBean.REPEAT;
import com.android.calendar.utils.EventUtils;
import com.apkfuns.logutils.LogUtils;
import gm.app.GomeAlertDialog;
import gm.widget.GomeSwitch;

import com.gome.gmtimepicker.util.DateFormatter;
import com.gome.gmtimepicker.view.GMTimePicker;
import com.hct.calendar.ui.ScheduleMoreLayout;
import com.hct.calendar.ui.WheelView;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.DateUtils;
import com.hct.calendar.utils.JudgeDate;
import com.hct.calendar.utils.ScreenInfo;

import android.R.integer;
import android.R.string;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScheduleFragment extends Fragment {
    public final static String TAG = "schedule";
    private EditText etTitle;
    private ViewGroup allDayLayout;
    private GomeSwitch gs_allday;
    private View startTimeLayout;
    private View endTimeLayout;
    private View reminderLayout;
    private View repeatLayout;
    private EditText etRemark;
    // private View accountLayout;
    // private TextView tv_account;
    private EditText etLocation;
    private TextView tvStartTime;
    private TextView tvStartDate;
    private TextView tvEndTime;
    private TextView tvEndDate;
    private ImageView iv_arrow;
    private PopupWindow mPopupWindow;
    private boolean isSetStartTime;
    private int eventId = -1;
    private SharedPreferences sp_setting;
    private String[] remindStrings;
    private String[] allDayRemindStrings;
    private String[] remindTextStrings;
    // private SharedPreferences sp;
    // private final int[] minuteLists = new int[] { -1, 0, 5, 10, 30, 60, 60 *
    // 24, 60 * 24 * 7 };
    // private final int[] allDayMinuteLists = new int[] { -1, 0, 60 * 24, 2 *
    // 60 * 24 };
    private GomeSwitch gs_nongli;
    private Context mContext;
    private int selectedScheduleYear = 0;
    private int selectedScheduleYear1 = 0;
    private int selectedScheduleMonth = 0;
    private int selectedScheduleDay = 0;
    private int selectedScheduleHour = 0;
    private int selectedScheduleHourend = 0;
    private int selectedScheduleMimute = 0;
    private int selectedScheduleMimuteend = 0;
    int allDay = 0;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    public Calendar mStart;
    public Calendar mEnd;
    private TextView tvReminderText;
    private TextView tvRepeatText;
    private long endTime;
    private List<ReminderBean> reminderBeans = EventUtils.getReminderBeans();
    private List<RepeatBean> repeatBeans = EventUtils.getRepeatBeans();
    // private WheelWeekMain wheelWeekMainDate;
    protected boolean isLunar;
    // public boolean isSetEnd;
    private boolean isShowDialog = false;
    private GMTimePicker timePicker;
    private Calendar mTemplate = Calendar.getInstance(Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initData() {
        mContext = getContext();
        beginTime = getArguments().getLong("beginTime");
        endTime = beginTime + EventUtils.ONE_HOUR * 1;
        remindTextStrings = mContext.getResources().getStringArray(R.array.schedule_reminds_time_str);
        remindStrings = mContext.getResources().getStringArray(R.array.preferences_default_reminder_labels);
        allDayRemindStrings = mContext.getResources().getStringArray(R.array.preferences_allday_reminder_labels);
        // sp = getContext().getSharedPreferences("schedule",
        // Context.MODE_PRIVATE);
        sp_setting = mContext.getSharedPreferences(CalendarSettings.SETTINGS_SP_NAME, Context.MODE_PRIVATE);
        LogUtils.e("left Fragment=== " + EventUtils.getFullDateTimeStr(beginTime) + " --> "
                + EventUtils.getFullDateTimeStr(endTime));
        // Calendar calendarInit = Calendar.getInstance();
        // int yearinit = calendarInit.get(Calendar.YEAR);
        // int monthinit = calendarInit.get(Calendar.MONTH) + 1;
        // int dateinit = calendarInit.get(Calendar.DATE);
        // int hourinit = calendarInit.get(Calendar.HOUR_OF_DAY);
        // int minuteinit = calendarInit.get(Calendar.MINUTE);
        // selectedScheduleYear = yearinit;
        // selectedScheduleMonth = monthinit;
        // selectedScheduleDay = dateinit;
        //
        // if (minuteinit <= 30 && minuteinit >= 0) {
        // selectedScheduleHour = hourinit;
        // selectedScheduleHourend = hourinit + 1;
        // selectedScheduleMimute = 30;
        // selectedScheduleMimuteend = 30;
        // } else if (minuteinit > 30 && minuteinit < 59) {
        // selectedScheduleHour = hourinit + 1;
        // selectedScheduleHourend = hourinit + 2;
        // selectedScheduleMimute = 00;
        // selectedScheduleMimuteend = 00;
        // }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initViews(view);
        initDefaultData();
        eventId = getArguments().getInt("id");
        if (eventId != -1) {
            initEditData();
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        updateTimeText(beginTime, true);
        updateTimeText(endTime, false);
        Log.e("liaozhenbin", "onResume");
    }

    public void showWeekBottoPopupWindow(boolean isStartTime) {
        isShowDialog = true;
        isSetStartTime = isStartTime;
        HideSoftInputMethod();
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);
        TextView timeTitle = (TextView) menuView.findViewById(R.id.tv_pop_title);
        timeTitle.setText(isStartTime ? getResources().getString(R.string.accessibility_pick_start_time)
                : getResources().getString(R.string.accessibility_pick_end_time));
        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95), LayoutParams.WRAP_CONTENT);

        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        timePicker.setMode(GMTimePicker.FLAG_COMPLEX_DATE | GMTimePicker.FLAG_HOUR | GMTimePicker.FLAG_MINUTE);
        timePicker.setCurrentTime(isStartTime ? beginTime : endTime);

        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(iv_arrow, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.3f);
        getActivity().getWindow().setNavigationBarColor(getResources().getColor(R.color.choose_time_nav));

        Button tv_cancle = (Button) menuView.findViewById(R.id.tv_cancle);
        Button tv_ensure = (Button) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        // OneLineCheckboxLayout cb_show = (OneLineCheckboxLayout)
        // menuView.findViewById(R.id.cb_show_yang);
        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);
        ViewGroup nongliViewGroup = (ViewGroup) menuView.findViewById(R.id.view_nongli);
        nongliViewGroup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                gs_nongli.setChecked(!gs_nongli.isChecked());
            }
        });
        // cb_show.setTextColor("#D0D0D0");
        if (CalendarUtil.isEnglish()) {
            nongliViewGroup.setVisibility(View.INVISIBLE);
        }
        gs_nongli.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean checked) {
                isLunar = checked;
                // TODO Auto-generated method stub
                // wheelWeekMainDate.setShowYang(checked);
                timePicker.setLunarDate(checked);
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mPopupWindow.dismiss();
                backgroundAlpha(1f);
                mPopupWindow = null;
                isSetStartTime = false;
                isLunar = false;
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Calendar date = timePicker.getCurrentTime();
                final long timeInMillis = date.getTimeInMillis();
                if (isSetStartTime) {
                    if (endTime <= timeInMillis) {
                        endTime = timeInMillis + 1000 * 60 * 60;
                        updateTimeText(endTime, false);
                    }
                } else {
                    if (beginTime > timeInMillis) {
//                        beginTime = timeInMillis - 1000 * 60 * 60;
                        endTime =   beginTime;
//                        updateTimeText(endTime, false);
                        date.setTimeInMillis(endTime);
                    }
                }
                updateTimeText(date, isSetStartTime);

                mPopupWindow.dismiss();
                backgroundAlpha(1f);
                mPopupWindow = null;
            }
        });
    }

    private void updateTimeText(Calendar date, boolean isStart) {
        String time = com.android.calendar.utils.DateUtils.getNormalTime(getContext(), date.getTimeInMillis());
        String ymd = DateFormatter.format(date, getResources());
        if (isStart) {
            if (mStart == null)
                mStart = Calendar.getInstance(Locale.getDefault());
            mStart.setTimeInMillis(date.getTimeInMillis());
            beginTime = mStart.getTimeInMillis();
            tvStartDate.setText(ymd);
            tvStartTime.setText(time);
        } else {
            if (mEnd == null)
                mEnd = Calendar.getInstance(Locale.getDefault());
            mEnd.setTimeInMillis(date.getTimeInMillis());
            endTime = mEnd.getTimeInMillis();
            tvEndDate.setText(ymd);
            tvEndTime.setText(time);
        }
    }

    private void updateTimeText(long timeInMillis, boolean isStart) {
        mTemplate.clear();
        mTemplate.setTimeInMillis(timeInMillis);
        updateTimeText(mTemplate, isStart);
    }

    private void HideSoftInputMethod() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    class PopupDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
            mPopupWindow = null;
            isLunar = false;
            // isSetEnd = false;
            isShowDialog = false;
        }

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    private void initDefaultData() {
        tvStartTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(mContext, beginTime));
        tvEndTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(mContext, endTime));
        tvStartDate.setText(
                com.android.calendar.utils.DateUtils.getNormalDate(mContext, beginTime, R.string.date_format_deafult));
        tvEndDate.setText(
                com.android.calendar.utils.DateUtils.getNormalDate(mContext, endTime, R.string.date_format_deafult));

        // Log.e("fushuo00000", "single_list[sp_setting.getI =" +
        // single_list[sp_setting.getInt("alert_index", 0)]);

        reminderType = sp_setting.getInt("alert_index", 3);

        tvReminderText.setText(remindTextStrings[reminderType]);

        // Log.e("fushuo00000", "reminderMinute111 =" + reminderType);

        // selectedScheduleYear =
        // com.android.calendar.utils.DateUtils.getYear(beginTime);
        // selectedScheduleMonth =
        // com.android.calendar.utils.DateUtils.getMonth(beginTime) + 1;
        // selectedScheduleDay =
        // com.android.calendar.utils.DateUtils.getDay(beginTime);
        // Calendar c = Calendar.getInstance();
        // int yearinit = c.get(Calendar.YEAR);
        // int monthinit = c.get(Calendar.MONTH) + 1;
        // int dateinit = c.get(Calendar.DATE);
        // int hourinit = c.get(Calendar.HOUR_OF_DAY);
        // int minuteinit = c.get(Calendar.MINUTE);
        // int secondinit = c.get(Calendar.SECOND);
        // String str = yearinit + "-" + monthinit + "-" + dateinit + " " +
        // hourinit + ":" + minuteinit + ":" + secondinit;
        // String str2 = selectedScheduleYear + "-" + selectedScheduleMonth +
        // "-" + selectedScheduleDay + " " + hourinit
        // + ":" + minuteinit + ":" + secondinit;
        // selectedScheduleYear1 = resultValue(str, str2);
        allDayLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(isShowDialog) {
                    return;
                }
                // TODO Auto-generated method stub
                if (gs_allday.isChecked()) {
                    gs_allday.setChecked(false);
                } else {
                    gs_allday.setChecked(true);
                }
            }
        });
        gs_allday.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if(isShowDialog) {
                    return;
                }
                if (gs_allday.isChecked()) {
                    tvStartTime.setVisibility(View.INVISIBLE);
                    tvEndTime.setVisibility(View.INVISIBLE);
                    // if (eventId == -1) {
                    reminderType = 1 + remindStrings.length;
                    // }
                } else {
                    tvStartTime.setVisibility(View.VISIBLE);
                    tvEndTime.setVisibility(View.VISIBLE);
                    reminderType = 1;
                }
                tvReminderText.setText(remindTextStrings[reminderType]);
            }
        });

        startTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToastHelper.show(getActivity(), "start time");
                // StartDatePickerFragment datePickerFragment = new
                // StartDatePickerFragment();
                // datePickerFragment.show(getFragmentManager(), "datePicker");
                if (isShowDialog == false) {
                    // isSetStartTime = true;
                    showWeekBottoPopupWindow(true);
                }
            }
        });
        endTimeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ToastHelper.show(getActivity(), "end XXXX time");
                // EndDatePickerFragment datePickerFragment = new
                // EndDatePickerFragment();
                // datePickerFragment.show(getFragmentManager(),
                // "datePickerXX");
                if (isShowDialog == false) {
                    if (null == mPopupWindow) {
                        // isSetEnd = true;
                        showWeekBottoPopupWindow(false);
                    }
                }
            }
        });
        reminderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDialog == false)
                    showReminderChoiceDialog();
            }
        });
        repeatLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDialog == false)
                    showRepeatChoiceDialog();
            }
        });

        // accountLayout.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // showAccountChoiceDialog();
        // }
        // });
    }

    // private void showAccountChoiceDialog() {
    // // TODO Auto-generated method stub
    // CalendarUtil.darkBackground(getActivity());
    // final String[] single_list =
    // getResources().getStringArray(R.array.schedule_add_account_labels);
    // GomeAlertDialog.Builder builder = new
    // GomeAlertDialog.Builder(getContext());
    // builder.setTitle(getString(R.string.preferences_add_account));
    // builder.setSingleChoiceItems(single_list, !sp.contains("account_index") ?
    // 0 : sp.getInt("account_index", 0),
    // new DialogInterface.OnClickListener() {
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    //// sp.edit().putInt("account_index", which).commit();
    // tv_account.setText(single_list[which]);
    // dialog.dismiss();
    // }
    // });
    // builder.setNegativeButton(getString(R.string.cancel_action), new
    // DialogInterface.OnClickListener() {
    //
    // @Override
    // public void onClick(DialogInterface dialog, int which) {
    // // TODO Auto-generated method stub
    // dialog.dismiss();
    // }
    // });
    // builder.setOnDismissListener(new OnDismissListener() {
    //
    // @Override
    // public void onDismiss(DialogInterface dialog) {
    // // TODO Auto-generated method stub
    // CalendarUtil.whiteBackground(getActivity());
    // }
    // });
    // GomeAlertDialog dialog = builder.create();
    // HideSoftInputMethod();
    // dialog.show();
    // }

    private void initViews(View view) {

        etTitle = (EditText) view.findViewById(R.id.schedule_title_et);

        allDayLayout = (ViewGroup) view.findViewById(R.id.all_day_layout);
        gs_allday = (GomeSwitch) view.findViewById(R.id.gs_allday);

        startTimeLayout = view.findViewById(R.id.start_time);
        tvStartTime = (TextView) view.findViewById(R.id.start_time_time);
        tvStartDate = (TextView) view.findViewById(R.id.start_time_date);
        endTimeLayout = view.findViewById(R.id.end_time);
        tvEndTime = (TextView) view.findViewById(R.id.end_time_time);
        tvEndDate = (TextView) view.findViewById(R.id.end_time_date);

        reminderLayout = view.findViewById(R.id.reminder_layout);
        tvReminderText = (TextView) view.findViewById(R.id.reminder_text_tv);

        repeatLayout = view.findViewById(R.id.repeat_layout);
        tvRepeatText = (TextView) view.findViewById(R.id.repeat_text_tv);
        ScheduleMoreLayout sml = (ScheduleMoreLayout) view.findViewById(R.id.schedule_more);

        // accountLayout = sml.getAccountLayout();
        // tv_account = sml.getAccountTextView();
        etRemark = sml.getRemarkEditText();

        etLocation = sml.getLocationEditText();
        iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
    }

    int reminderWhich = 0;
    protected int reminderType;
    @REPEAT
    private int repeate;
    private int selectItemdb = 0;

    private void showReminderChoiceDialog() {
        // CalendarUtil.darkBackground(getActivity());
        isShowDialog = true;
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getString(R.string.event_alert_title));
        if (gs_allday.isChecked()) {
            builder.setSingleChoiceItems(allDayRemindStrings, reminderType - remindStrings.length,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            reminderType = which + remindStrings.length;
                            tvReminderText.setText(remindTextStrings[reminderType]);
                            dialog.dismiss();
                        }
                    });
        } else {
            builder.setSingleChoiceItems(remindStrings, reminderType, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reminderType = which;
                    tvReminderText.setText(remindTextStrings[reminderType]);
                    dialog.dismiss();
                }
            });
        }
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(getActivity());
                isShowDialog = false;
            }
        });
        builder.setNegativeButton(getContext().getString(R.string.discard_label),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
        GomeAlertDialog dialog = builder.create();
        HideSoftInputMethod();
        dialog.show();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // sp.edit().remove("index").commit();
        // sp.edit().clear().commit();
    }

    int repeatWhich = 0;

    private void showRepeatChoiceDialog() {
        // CalendarUtil.darkBackground(getActivity());
        isShowDialog = true;
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(getActivity());
        builder.setTitle(getContext().getString(R.string.event_again_dialog));
        final String[] items = EventUtils.getRepeatBeans(repeatBeans);
        builder.setSingleChoiceItems(items, repeatWhich, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                repeatWhich = which;
                tvRepeatText.setText(items[which]);
                repeate = repeatBeans.get(which).repeate;
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // TODO Auto-generated method stub
                // CalendarUtil.whiteBackground(getActivity());
                isShowDialog = false;
            }
        });
        builder.setNegativeButton(getContext().getString(R.string.discard_label),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        // TODO Auto-generated method stub

                    }
                });
        GomeAlertDialog dialog = builder.create();
        HideSoftInputMethod();
        dialog.show();
    }

    public boolean saveData(boolean isEdit) {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String desc = etRemark.getText().toString().trim();
        int allDay = gs_allday.isChecked() ? 1 : 0;
        EventBean bean = new EventBean();
        bean.title = title;
        bean.location = location;
        bean.allDay = allDay;
        bean.desc = desc;
        long endT = mEnd != null ? mEnd.getTime().getTime() : endTime;
        long startT = mStart != null ? mStart.getTime().getTime() : beginTime;
        bean.end = DateUtils.moveSecond(endT);
        bean.start = DateUtils.moveSecond(startT);
        if (endT < startT) {
            Toast.makeText(getActivity(), getActivity().getString(R.string.alert_wrong_time), Toast.LENGTH_LONG).show();
            return false;
        }
        // Log.e("fushuo00000", "reminderMinute=" + reminderMinute);
        Log.e("fushuo00000", "mStart=" + startT);
        Log.e("fushuo00000", "mEnd=" + endT);
        bean.reminder = reminderType;
        // fushuo begin
        bean.color = Constant.SCHEDULE;
        if (repeate == RepeatBean.ONCE) {
            bean.rule = null;
        } else if (repeate == RepeatBean.EVERY_DAY) {
            bean.rule = "FREQ=DAILY;WKST=SU";
        } else if (repeate == RepeatBean.EVERY_HOUR) {
            bean.rule = "FREQ=HOURLY;WKST=SU";
        } else if (repeate == RepeatBean.EVERY_WEEK) {
            // bean.rule = "FREQ=WEEKLY;WKST=SU";
            bean.rule = "FREQ=WEEKLY;WKST=SU";
        } else if (repeate == RepeatBean.EVERY_MONTH) {
            bean.rule = "FREQ=MONTHLY;WKST=SU";
        } else if (repeate == RepeatBean.EVERY_YEAR) {
            bean.rule = "FREQ=YEARLY;WKST=SU";
        }
        // fushuo end
        if (TextUtils.isEmpty(bean.title)) {
            return false;
        }
        if (isEdit) {
            EventUtils.updateScheduleEvent(getActivity(), bean, eventId);
        } else {
            EventUtils.addScheduleEvent(getActivity(), bean);
        }
        return true;
    }

    private void initEditData() {
        String title = null;
        // String attend = null;
        String desc = null;
        String location = null;
        long dtstart = 0;
        long dtend = 0;
        int edReminderType = 0;
        String duration;
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, null, "_id=?", new String[] { "" + eventId },
                null);
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(Events.TITLE));
            dtstart = cursor.getLong(cursor.getColumnIndex(Events.DTSTART));
            selectItemdb = cursor.getInt(cursor.getColumnIndex(Events.IS_ORGANIZER));
            duration = cursor.getString(cursor.getColumnIndex(Events.DURATION));
            if (mStart == null) {
                mStart = Calendar.getInstance();
            }
            mStart.setTimeInMillis(dtstart);
            dtend = cursor.getLong(cursor.getColumnIndex(Events.DTEND));
            if (mEnd == null) {
                mEnd = Calendar.getInstance();
            }
            if (dtend == 0 && null != duration) {
                dtend = dtstart + com.android.calendar.utils.DateUtils.getDuration(duration) * 1000;
            }
            mEnd.setTimeInMillis(dtend);
            edReminderType = cursor.getInt(cursor.getColumnIndex(Events.ORIGINAL_INSTANCE_TIME));
            // Log.e("fushuo00000", "reminderTime=reminderMinute : " +
            // reminderTime);
            location = cursor.getString(cursor.getColumnIndex(Events.EVENT_LOCATION));
            desc = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION));
            allDay = cursor.getInt(cursor.getColumnIndex(Events.ALL_DAY));
            String rrule = cursor.getString(cursor.getColumnIndex(Events.RRULE));
            Log.d("zyp", "zyp ---rrule------   :" + rrule);
            rruleString = getRruleString(rrule);
            Log.d("zyp", "zyp ---rruleString------   :" + rruleString);

        }
        tvStartTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(mContext, dtstart));
        tvStartDate.setText(
                com.android.calendar.utils.DateUtils.getNormalDate(mContext, dtstart, R.string.date_format_deafult));
        tvEndTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(mContext, dtend));
        tvEndDate.setText(
                com.android.calendar.utils.DateUtils.getNormalDate(mContext, dtend, R.string.date_format_deafult));

        etTitle.setText(title);

        etLocation.setText(location);
        etRemark.setText(desc);
        beginTime = dtstart;
        endTime = dtend;
        // selectedScheduleYear =
        // com.android.calendar.utils.DateUtils.getYear(dtstart);
        // selectedScheduleMonth =
        // com.android.calendar.utils.DateUtils.getMonth(dtstart) + 1;
        // selectedScheduleDay =
        // com.android.calendar.utils.DateUtils.getDay(dtstart);
        // selectedScheduleHour =
        // com.android.calendar.utils.DateUtils.getHour(dtstart);
        // selectedScheduleMimute =
        // com.android.calendar.utils.DateUtils.getMinute(dtstart);
        // selectedScheduleHourend =
        // com.android.calendar.utils.DateUtils.getHour(dtend);
        // selectedScheduleMimuteend =
        // com.android.calendar.utils.DateUtils.getMinute(dtend);
        // Calendar c = Calendar.getInstance();
        // int yearinit = c.get(Calendar.YEAR);
        // int monthinit = c.get(Calendar.MONTH) + 1;
        // int dateinit = c.get(Calendar.DATE);
        // int hourinit = c.get(Calendar.HOUR_OF_DAY);
        // int minuteinit = c.get(Calendar.MINUTE);
        // int secondinit = c.get(Calendar.SECOND);
        // String str = yearinit + "-" + monthinit + "-" + dateinit + " " +
        // hourinit + ":" + minuteinit + ":" + secondinit;
        // String str2 = selectedScheduleYear + "-" + selectedScheduleMonth +
        // "-" + selectedScheduleDay + " " + hourinit
        // + ":" + minuteinit + ":" + secondinit;
        // selectedScheduleYear1 = resultValue(str, str2);
        if (allDay == 1) {
            gs_allday.setChecked(true);
            tvStartTime.setVisibility(View.INVISIBLE);
            tvEndTime.setVisibility(View.INVISIBLE);
        }
        // String[] allday_single_list =
        // getResources().getStringArray(R.array.preferences_allday_reminder_labels);
        // switch (reminderTime) {
        // case 0:
        // tvReminderText.setText((1 == allDay) ? allday_single_list[1] :
        // this.getString(R.string.event_on_time));
        // break;
        // case 60:
        // tvReminderText.setText(getResources().getString(R.string.one_hour_reminder));
        // break;
        // case 60 * 24:
        // tvReminderText.setText(
        // (1 == allDay) ? allday_single_list[2] :
        // getResources().getString(R.string.one_day_reminder));
        // break;
        // case 60 * 24 * 2:
        // if (1 == allDay)
        // tvReminderText.setText(allday_single_list[3]);
        // break;
        // case 60 * 24 * 7:
        // tvReminderText.setText(getResources().getString(R.string.one_week_reminder));
        // break;
        // case -1:
        // tvReminderText.setText(getResources().getString(R.string.dismiss_label));
        // break;
        // default:
        // tvReminderText.setText(reminderTime > 0
        // ? this.getString(R.string.event_in_advance) + reminderTime +
        // this.getString(R.string.event_minute)
        // : this.getString(R.string.event_on_time));
        // break;
        // }
        Log.d("liaozhenbin", "reminderType: " + reminderType + edReminderType);
        reminderType = edReminderType;
        tvReminderText.setText(remindTextStrings[edReminderType]);

        tvRepeatText.setText(rruleString);

    }

    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";
    private long beginTime;
    private String rruleString;

    /**
     * 
     * http://blog.csdn.net/zhaoshuyu111/article/details/53195142?ref=myread
     * 
     * @param context
     * @param title
     * @param description
     * @param beginTime
     */
    public static void addCalendarEvent(Context context, String title, String description, String location, int allDay,
            long beginTime, long endTime) {
        int calId = 1;
        if (calId < 0) {
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        event.put("calendar_id", calId);
        event.put(Events.ALL_DAY, allDay);
        event.put(CalendarContract.Events.DTSTART, beginTime);
        event.put(CalendarContract.Events.DTEND, endTime);
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        event.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {

            return;
        }
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        values.put(CalendarContract.Reminders.MINUTES, 10);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);

        if (uri == null) {
            return;
        }
    }

    // add kaikai.zhao
    // public static int differentDays(Date date1, Date date2) {
    // Calendar cal1 = Calendar.getInstance();
    // cal1.setTime(date1);
    //
    // Calendar cal2 = Calendar.getInstance();
    // cal2.setTime(date2);
    // int day1 = cal1.get(Calendar.DAY_OF_YEAR);
    // int day2 = cal2.get(Calendar.DAY_OF_YEAR);
    //
    // int year1 = cal1.get(Calendar.YEAR);
    // int year2 = cal2.get(Calendar.YEAR);
    // if (year1 != year2) // ��?��??��
    // {
    // int timeDistance = 0;
    // for (int i = year1; i < year2; i++) {
    // if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) // ����?��
    // {
    // timeDistance += 366;
    // } else // 2?��?����?��
    // {
    // timeDistance += 365;
    // }
    // }
    //
    // return timeDistance + (day2 - day1);
    // } else // 2?��??��
    // {
    // return day2 - day1;
    // }
    // }
    //
    // // add kaikai
    // public static int differentValue(String dateStr, String dateStr2) {
    // Date date2 = null;
    // Date date = null;
    // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // try {
    // date2 = format.parse(dateStr2);
    // date = format.parse(dateStr);
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return differentDays(date, date2);
    //
    // }
    //
    // public static int resultValue(String str, String str2) {
    // int i = 0;
    // int j = 0;
    // if (str.compareTo(str2) < 0) {
    // i = differentValue(str, str2);
    // j = i;
    // } else {
    // i = differentValue(str2, str);
    // j = -i;
    // }
    // return j;
    // }

    private String getRruleString(String s) {
        String rruleString = null;
        if (s == null) {
            rruleString = this.getString(R.string.event_one_time);
            repeatWhich = 0;
            repeate = repeatBeans.get(0).repeate;
        } else if (s.equals("FREQ=DAILY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_day);
            repeatWhich = 1;
            repeate = repeatBeans.get(1).repeate;
        } else if (s.equals("FREQ=HOURLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_hour);
            repeatWhich = 2;
            repeate = repeatBeans.get(2).repeate;
        } else if (s.equals("FREQ=WEEKLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_week);
            repeatWhich = 3;
            repeate = repeatBeans.get(3).repeate;
        } else if (s.equals("FREQ=MONTHLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_month);
            repeatWhich = 4;
            repeate = repeatBeans.get(4).repeate;
        } else if (s.equals("FREQ=YEARLY;WKST=SU")) {
            rruleString = this.getString(R.string.event_one_year);
            repeatWhich = 5;
            repeate = repeatBeans.get(5).repeate;
        }
        return rruleString;
    }
}
