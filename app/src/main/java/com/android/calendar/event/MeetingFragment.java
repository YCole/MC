package com.android.calendar.event;

import gm.app.GomeAlertDialog;
import gm.widget.GomeSwitch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.app.PendingIntent;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.calendar.CalendarApplication;
import com.android.calendar.CalendarSettings;
import com.android.calendar.R;
import com.android.calendar.utils.EventUtils;
import com.android.calendar.view.ZanyEditText;
import com.apkfuns.logutils.LogUtils;
import com.gome.gmtimepicker.util.DateFormatter;
import com.gome.gmtimepicker.view.GMTimePicker;
import com.hct.calendar.ui.FlowLayout;
import com.hct.calendar.ui.MeetingMoreLayout;
//import com.hct.calendar.ui.OneLineCheckboxLayout.OnCheckedChangeListener;
import com.hct.calendar.ui.TagAdapter;
import com.hct.calendar.ui.TagFlowLayout;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.DateUtils;

public class MeetingFragment extends Fragment {
    public final static String TAG = "meeting";
    private static final int MAX_ATTENDING_COUNT = 100;

    private EditText etTitle;
    private View startTimeLayout;
    private View endTimeLayout;
    private TextView tvStartTime;
    private TextView tvStartDate;
    private TextView tvEndTime;
    private TextView tvEndDate;
    private View reminderLayout;
    private String mReminderStr = CalendarApplication.getContext().getString(
            R.string.event_on_time);
    private ImageView ivAddContacts;
    private ZanyEditText etPeople;
    private TagFlowLayout meetingTagLayout;
    private List<MeetingPerson> meetingPersonList;
    private TagAdapter tagAdapter;
    private HashSet<String> meetingPersonSet;
    // private String yearStr =
    // CalendarApplication.getContext().getString(R.string.event_year);
    // private String monthStr1 =
    // CalendarApplication.getContext().getString(R.string.event_month_format);
    // private String dayStr1 =
    // CalendarApplication.getContext().getString(R.string.event_day_format);
    public Calendar mStart;
    public Calendar mEnd;
    private TextView tvReminderText;
    // fushuo begin
    private EditText location_et;
    private EditText etRemark;
    private int reminderType;
    private List<ReminderBean> reminderBeans = EventUtils.getReminderBeans();
    private long endTime;
    private long beginTime;
    private CheckBox mCb;
    private View mCbView;
    private boolean isSend = false;
    private PopupWindow mPopupWindow;
    // private WheelWeekMain wheelWeekMainDate;
    private ImageView iv_arrow;
    private boolean isSetStartTime;
    protected boolean isLunar;
    // private SharedPreferences sp;
    private int eventId = -1;
    // private boolean isSetEnd;
    private SharedPreferences sp_setting;
    // private String[] single_list;
    String[] remindStrings;
    private GomeSwitch gs_nongli;
    private Context mContext;
    private boolean isShowDialog = false;
    private GMTimePicker timePicker;
    private Calendar mTemplate = Calendar.getInstance(Locale.getDefault());

    private String categoryMeetingStr;
    private String eventShareTitleStr;
    private String eventShareTimeZoneStr;
    private String whenLabelStr;
    private String eventShareMeetingroomStr;
    private String eventShareMeetingpeopleStr;
    private String eventShareRemarkStr;

    // fushuo end
    public MeetingFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initData() {
        mContext = getContext();
        beginTime = getArguments().getLong("beginTime");
        endTime = beginTime + EventUtils.ONE_HOUR * 1;
        // single_list =
        // mContext.getResources().getStringArray(R.array.preferences_default_reminder_labels);
        // sp = mContext.getSharedPreferences("meeting", Context.MODE_PRIVATE);
        sp_setting = mContext.getSharedPreferences(
                CalendarSettings.SETTINGS_SP_NAME, Context.MODE_PRIVATE);
        remindStrings = mContext.getResources().getStringArray(
                R.array.preferences_default_reminder_labels);
        Log.e("fushuo", "alert_index=" + sp_setting.getInt("alert_index", 0));
        LogUtils.e("left Fragment=== "
                + EventUtils.getFullDateTimeStr(beginTime) + " --> "
                + EventUtils.getFullDateTimeStr(endTime));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.meeting_fragment_layout, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        initData();
        findAllView(view);
        initDefaultData();
        setAllListener();
        eventId = getArguments().getInt("id");
        if (eventId != -1) {
            initEditData();
        }
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
        categoryMeetingStr = getString(R.string.category_meeting);
        eventShareTitleStr = getString(R.string.event_share_title);
        eventShareTimeZoneStr = getString(R.string.event_share_time_zone);
        whenLabelStr = getString(R.string.when_label);
        eventShareMeetingroomStr = getString(R.string.event_share_meetingroom);
        eventShareMeetingpeopleStr = getString(R.string.event_share_meetingpeople);
        eventShareRemarkStr = getString(R.string.event_share_remark);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // tvReminderText.setText(single_list[sp_setting.getInt("alert_index",
        // 0)]);
        // reminderMinute = minuteLists[sp_setting.getInt("alert_index", 0)];
        updateTimeText(beginTime, true);
        updateTimeText(endTime, false);
        Log.e("fushuo", "reminderMinute=" + reminderType);
    }

    public boolean isSending() {
        return isSend;
    }

    public List<MeetingPerson> getList() {
        return meetingPersonList;
    }

    private void findAllView(final View view) {
        // fushuo begin
        MeetingMoreLayout sml = (MeetingMoreLayout) view
                .findViewById(R.id.meeting_more);

        etRemark = sml.getRemarkEditText();

        location_et = sml.getLocationEditText();
        mCb = sml.getCheckBox();
        mCbView = sml.getCheckView();
        // fushuo end
        iv_arrow = (ImageView) view.findViewById(R.id.iv_arrow);
        etTitle = (EditText) view.findViewById(R.id.meeting_title_et);
        startTimeLayout = view.findViewById(R.id.start_time);
        tvStartTime = (TextView) view.findViewById(R.id.start_time_time);
        tvStartDate = (TextView) view.findViewById(R.id.start_time_date);
        endTimeLayout = view.findViewById(R.id.end_time);
        tvEndTime = (TextView) view.findViewById(R.id.end_time_time);
        tvEndDate = (TextView) view.findViewById(R.id.end_time_date);
        reminderLayout = view.findViewById(R.id.reminder_layout);
        tvReminderText = (TextView) view.findViewById(R.id.reminder_text_tv);

        ivAddContacts = (ImageView) view.findViewById(R.id.add_contacts);
        etPeople = (ZanyEditText) view.findViewById(R.id.meeting_people_et);
        etPeople.setSingleLine();
        meetingTagLayout = (TagFlowLayout) view
                .findViewById(R.id.flow_layout_meeting_tag);
        meetingPersonList = new ArrayList<MeetingPerson>();
        meetingPersonSet = new HashSet<String>();
        tagAdapter = new TagAdapter<MeetingPerson>(meetingPersonList) {
            @Override
            public MeetingLabelView getView(FlowLayout parent,
                    final int position, MeetingPerson person) {
                MeetingLabelView labelView = new MeetingLabelView(
                        view.getContext());
                labelView.meetingPersonName.setText(person.getPersonName());
                labelView.deleteIcon
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                meetingPersonSet.remove(meetingPersonList.get(
                                        position).getPersonName());
                                meetingPersonList.remove(position);
                                Log.e("fushuo", "delete success!!!");
                                notifyDataChanged();
                            }
                        });
                return labelView;
            }
        };
        meetingTagLayout.setAdapter(tagAdapter);
    }

    private void initDefaultData() {
        tvStartTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(
                mContext, beginTime));
        tvEndTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(
                mContext, endTime));
        tvStartDate.setText(com.android.calendar.utils.DateUtils.getNormalDate(
                mContext, beginTime, R.string.date_format_deafult));
        tvEndDate.setText(com.android.calendar.utils.DateUtils.getNormalDate(
                mContext, endTime, R.string.date_format_deafult));
        reminderType = sp_setting.getInt("alert_index", 3);
        tvReminderText.setText(remindStrings[reminderType]);
        reminderLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderChoiceDialog();
            }
        });
    }

    public void showWeekBottoPopupWindow(boolean isStartTime) {
        isShowDialog = true;
        isSetStartTime = isStartTime;
        HideSoftInputMethod();
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);
        TextView timeTitle = (TextView) menuView
                .findViewById(R.id.tv_pop_title);
        timeTitle.setText(isStartTime ? getResources().getString(
                R.string.accessibility_pick_start_time) : getResources()
                .getString(R.string.accessibility_pick_end_time));

        ViewGroup nongliViewGroup = (ViewGroup) menuView.findViewById(R.id.view_nongli);
        if (CalendarUtil.isEnglish()) {
            nongliViewGroup.setVisibility(View.INVISIBLE);
        }

        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95),
                LayoutParams.WRAP_CONTENT);

        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        timePicker.setMode(GMTimePicker.FLAG_COMPLEX_DATE
                | GMTimePicker.FLAG_HOUR | GMTimePicker.FLAG_MINUTE);
        timePicker.setCurrentTime(isStartTime ? beginTime : endTime);

        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(iv_arrow, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.3f);
        getActivity().getWindow().setNavigationBarColor(
                getResources().getColor(R.color.choose_time_nav));
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);

        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);

        gs_nongli.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                isLunar = isChecked;
                // wheelWeekMainDate.setShowYang(isChecked);
                timePicker.setLunarDate(isChecked);

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
                    if (endTime < timeInMillis) {
                        endTime = timeInMillis + 1000 * 60 * 60;
                        updateTimeText(endTime, false);
                    }
                } else {
                    if (beginTime > timeInMillis) {
                        // beginTime = timeInMillis;
                        endTime = beginTime;
                        // updateTimeText(beginTime, true);
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
        String time = com.android.calendar.utils.DateUtils.getNormalTime(
                getContext(), date.getTimeInMillis());
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
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getActivity().getCurrentFocus() != null) {
            if (getActivity().getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
        WindowManager.LayoutParams lp = getActivity().getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    private void setAllListener() {
        startTimeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToastHelper.show(getActivity(), "start time");
                // StartDatePickerFragment datePickerFragment = new
                // StartDatePickerFragment();
                // datePickerFragment.show(getFragmentManager(), "datePicker");
                if (isShowDialog == false) {
                    if (mPopupWindow == null) {
                        showWeekBottoPopupWindow(true);
                        // isSetStartTime = true;
                    }
                }
            }
        });
        // end time
        endTimeLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // ToastHelper.show(getActivity(), "end XXXX time");
                // EndDatePickerFragment datePickerFragment = new
                // EndDatePickerFragment();
                // datePickerFragment.show(getFragmentManager(),
                // "datePickerXX");
                if (isShowDialog == false) {
                    if (mPopupWindow == null) {
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

        ivAddContacts.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                etPeople.setFocusable(true);
                etPeople.setFocusableInTouchMode(true);
                etPeople.requestFocus();
                // Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                // intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                // startActivityForResult(intent, 1);
                Intent intent = new Intent(
                        "android.intent.action.contacts.list.PICKMULTICONTACTS");
                intent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                startActivityForResult(intent, 1);

            }
        });

        etPeople.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etPeople.setText("");
                    meetingTagLayout.setVisibility(View.VISIBLE);
                } else {
                    StringBuilder str = new StringBuilder();
                    for (MeetingPerson person : meetingPersonList) {
                        str.append(person.getPersonName() + ";");
                        /*
                         * modify by Yusong.Liang for [PRODUCTION-3839] on
                         * 2017.10.24: start
                         */
                        // etPeople.setText(str);
                    }
                    etPeople.setText(str);
                    /*
                     * modify by Yusong.Liang for [PRODUCTION-3839] on
                     * 2017.10.24: end
                     */
                    meetingTagLayout.setVisibility(View.GONE);
                }
            }
        });

        etPeople.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int actionId, KeyEvent event) {
                Log.d("liaozhenbin", "actionId: " + actionId);
                if (actionId == KeyEvent.KEYCODE_ENTER
                        && event.getAction() == KeyEvent.ACTION_DOWN) {
                    String personName = etPeople.getText().toString().trim();
                    etPeople.setText("");
                    if (!TextUtils.isEmpty(personName)
                            && meetingPersonSet.add(personName)) {
                        if (meetingPersonList.size() < MAX_ATTENDING_COUNT) {
                            meetingPersonList.add(new MeetingPerson(personName,
                                    "-1"));
                            tagAdapter.notifyDataChanged();
                        } else {
                            onAttendingCountMax();
                        }
                    }

                    return true;
                }
                return false;
            }
        });
        mCbView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isSend) {
                    isSend = false;
                    mCb.setChecked(isSend);
                } else {
                    isSend = true;
                    mCb.setChecked(isSend);
                }
            }
        });
    }

    private void onAttendingCountMax() {
        Toast.makeText(getActivity(), R.string.message_max_meeting_people,
                Toast.LENGTH_SHORT).show();
    }

    private void showReminderChoiceDialog() {
        isShowDialog = true;
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(
                getActivity());
        builder.setTitle(getContext().getString(R.string.event_alert_title));
        builder.setSingleChoiceItems(remindStrings, reminderType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mReminderStr = remindStrings[which];
                tvReminderText.setText(mReminderStr);
                reminderType = which;
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowDialog = false;
            }
        });
        builder.setNegativeButton(getContext()
                .getString(R.string.discard_label),
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

    // fushuo begin
    private String title;
    private String location;

    public boolean saveData(boolean isEdit) {
        // title
        title = etTitle.getText().toString().trim();
        // meeting room
        location = location_et.getText().toString().trim();
        // remark
        final String desc = etRemark.getText().toString().trim();
        EventBean bean = new EventBean();
        bean.title = title;
        bean.location = location;
        bean.desc = desc;
        bean.meetingPersonList = meetingPersonList;
        final long endT = mEnd != null ? mEnd.getTime().getTime() : endTime;
        final long startT = mStart != null ? mStart.getTime().getTime() : beginTime;        
        bean.end = DateUtils.moveSecond(endT);
        bean.start = DateUtils.moveSecond(startT);
        if (endT < startT) {
            Toast.makeText(getActivity(),
                    getContext().getString(R.string.alert_wrong_time),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        bean.reminder = reminderType;
        // bean.attend = getAttendName();
        bean.color = Constant.MEETING;
        Log.e("fushuo", "" + bean.toString());
        if (TextUtils.isEmpty(bean.title)) {
            return false;
        }
        if (isEdit) {
            EventUtils.updateMeetingEvent(getActivity(), bean, eventId, isSend);
        } else {
            EventUtils.addMeetingEvent(getActivity(), bean, isSend);
        }

        if (isSending() && null != meetingPersonList) {
            Log.d("PRODUCTION-2877",
                    "meetingPersonList: " + meetingPersonList.size());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    android.telephony.SmsManager smsManager = android.telephony.SmsManager
                            .getDefault();
                    SimpleDateFormat formatDate = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(startT);
                    String startDate = formatDate.format(calendar.getTime());
                    String startTime = formatTime.format(calendar.getTime());
                    String timeArea = calendar.getTimeZone().getDisplayName();
                    calendar.setTimeInMillis(endT);
                    String finishTime = formatTime.format(calendar.getTime());
                    StringBuilder eventString = new StringBuilder();
                    eventString.append(categoryMeetingStr).append('\n').append(eventShareTitleStr + title).append('\n');
                    eventString.append(eventShareTimeZoneStr + timeArea).append('\n').append(whenLabelStr + startDate)
                            .append('\n');
                    eventString.append(startTime + "-" + finishTime).append('\n')
                            .append(eventShareMeetingroomStr + location).append('\n');
                    eventString.append(eventShareMeetingpeopleStr + getAttendName()).append('\n')
                            .append(eventShareRemarkStr + desc);
                    String message = eventString.toString();

                    List<String> divideContents = smsManager.divideMessage(eventString.toString());

                    // PendingIntent sentPI = new PendingIntent();
                    for (MeetingPerson person : meetingPersonList) {
                        String phoneNumber = person.getPersonPhone();
                        if (phoneNumber.equals("-1")) {
                            continue;
                        }
                        if (message.length() > 70) {
                            ArrayList<String> msgs = smsManager.divideMessage(message);
                            ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
                            for (int i = 0; i < msgs.size(); i++) {
                                // sentIntents.add(sentPI);
                            }
                            Log.e("2877", "sendMultipartTextMessage: " + phoneNumber);
                            smsManager.sendMultipartTextMessage(phoneNumber, null, msgs, sentIntents, null);
                        } else {
                            Log.e("2877", "sendTextMessage: " + phoneNumber);
                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                        }
                    }
                }
            }).start();

        }
        return true;
    }

    private String getAttendName() {
        // TODO Auto-generated method stub
        StringBuilder sb = new StringBuilder();
        if (null != meetingPersonList) {
            for (MeetingPerson people : meetingPersonList) {
                sb.append(people.getPersonName() + ";");
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    private void initEditData() {
        // String attend = null;
        String desc = null;
        long dtstart = 0;
        long dtend = 0;
        int hasAttdendee = 0;
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, null,
                "_id=?", new String[] { "" + eventId }, null);
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex(Events.TITLE));
            dtstart = cursor.getLong(cursor.getColumnIndex(Events.DTSTART));
            hasAttdendee = cursor.getInt(cursor
                    .getColumnIndex(Events.HAS_ATTENDEE_DATA));
            if (mStart == null) {
                mStart = Calendar.getInstance();
            }
            mStart.setTimeInMillis(dtstart);
            dtend = cursor.getLong(cursor.getColumnIndex(Events.DTEND));
            if (mEnd == null) {
                mEnd = Calendar.getInstance();
            }
            mEnd.setTimeInMillis(dtend);
            reminderType = cursor.getInt(cursor
                    .getColumnIndex(Events.ORIGINAL_INSTANCE_TIME));
            location = cursor.getString(cursor
                    .getColumnIndex(Events.EVENT_LOCATION));
            desc = cursor.getString(cursor.getColumnIndex(Events.DESCRIPTION));
        }
        isSend = hasAttdendee == 1;
        mCb.setChecked(isSend);
        Log.d("PRODUCTION-2877", "isSend:" + isSend);
        Cursor c = cr.query(Attendees.CONTENT_URI, null, "event_id=?",
                new String[] { "" + eventId }, null);
        StringBuilder attendStringBuilder = new StringBuilder();
        while (c.moveToNext()) {
            String name = c
                    .getString(c.getColumnIndex(Attendees.ATTENDEE_NAME));
            String phoneNumber = c.getString(c
                    .getColumnIndex(Attendees.ATTENDEE_EMAIL));
            meetingPersonList.add(new MeetingPerson(name, phoneNumber));
            meetingPersonSet.add(name);
            attendStringBuilder.append(name);
            attendStringBuilder.append(";");
        }

        tagAdapter.notifyDataChanged();

        tvStartTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(
                mContext, dtstart));
        tvStartDate.setText(com.android.calendar.utils.DateUtils.getNormalDate(
                mContext, dtstart, R.string.date_format_deafult));
        tvEndTime.setText(com.android.calendar.utils.DateUtils.getNormalTime(
                mContext, dtend));
        tvEndDate.setText(com.android.calendar.utils.DateUtils.getNormalDate(
                mContext, dtend, R.string.date_format_deafult));

        etTitle.setText(title);
        etPeople.setText(attendStringBuilder.toString());
        location_et.setText(location);
        etRemark.setText(desc);

        beginTime = dtstart;
        endTime = dtend;

        if (reminderType < remindStrings.length) {
            tvReminderText.setText(remindStrings[reminderType]);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name = null;
        String number = null;
        if (data != null) {

            if (requestCode == 1) {
                long[] idsList = data
                        .getLongArrayExtra("com.mediatek.contacts.list.pickcontactsresult");

                StringBuilder stringBuilder = new StringBuilder();
                final int space = MAX_ATTENDING_COUNT
                        - meetingPersonList.size();
                if (space == 0) {
                    onAttendingCountMax();
                    return;
                }
                if (idsList.length > MAX_ATTENDING_COUNT)
                    onAttendingCountMax();
                int count = Math.min(space, idsList.length);
                for (int i = 0; i < count; i++) {
                    Log.d("sanxin", "idsList[i]:" + idsList[i]);
                    stringBuilder.append(idsList[i]);
                    if (i < (count - 1)) {
                        stringBuilder.append(",");
                    }
                }

                Log.d("liaozhenbin",
                        "stringBuilder:" + stringBuilder.toString());

                Uri datauri = Uri
                        .parse("content://com.android.contacts/data?directory=0");
                Cursor cursor = getActivity()
                        .getContentResolver()
                        .query(datauri,
                                new String[] { ContactsContract.Data.DATA1,
                                        ContactsContract.Data.DISPLAY_NAME },
                                ContactsContract.Data.CONTACT_ID + " IN ("
                                        + stringBuilder.toString() + ") AND "
                                        + ContactsContract.Data.MIMETYPE
                                        + " = ?",
                                new String[] { ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE },
                                null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        number = cursor.getString(0);
                        name = cursor.getString(1);
                        if (meetingPersonSet.add(name)) {
                            meetingPersonList.add(new MeetingPerson(name,
                                    number, mStart, mEnd, location_et.getText()
                                            .toString().trim(), etTitle
                                            .getText().toString().trim()));
                        }
                    }
                    tagAdapter.notifyDataChanged();
                    cursor.close();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // sp.edit().remove("index").commit();
        // sp.edit().clear().commit();
    }

}
