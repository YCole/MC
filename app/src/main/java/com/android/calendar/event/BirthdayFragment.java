package com.android.calendar.event;

import gm.app.GomeAlertDialog;
import gm.widget.GomeSwitch;

import java.util.Calendar;
import java.util.Locale;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.calendar.R;
import com.android.calendar.utils.EventUtils;
import com.gome.gmtimepicker.util.DateFormatter;
import com.gome.gmtimepicker.view.GMTimePicker;
import com.hct.calendar.ui.OneLineCheckboxLayout;
import com.hct.calendar.ui.OneLineCheckboxLayout.OnCheckedChangeListener;
import com.hct.calendar.utils.CalendarUtil;
import com.hct.calendar.utils.DateUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BirthdayFragment extends Fragment implements View.OnClickListener {
    public final static String TAG = "birthday";

    private static final int REPEAT_ONE_TIME = 0;
    private static final int REPEAT_YEARLY = 1;

    private EditText etPeople;
    private ImageView ivAddContacts;
    private View birthdayLayout;
    private View repeatLayout;
    private TextView birthdayTextView;
    private TextView repeatTextView;
    private String mRepeatStr;
    private int eventId = -1;
    private String title;
    private String startDate;
    String rruleString = null;
    private View reminderLayout;
    private PopupWindow mPopupWindow;
    // private WheelWeekMain wheelWeekMainDate;
    private ImageView icon_end1;
    private boolean isAdvance = false;
    private TextView reminder_text_tv;
    // public int selectedYear;
    // public int selectedMonth;
    // public int selectedDay;
    public boolean isSetBirthSuccess;
    private long mStartTime;
    private String[] singleList = new String[2];
    private String advance3Day;
    private String birthdayReminder;
    private GomeSwitch gs_nongli;
    private boolean isShowDialog = false;
    private Calendar mTempDate = Calendar.getInstance(Locale.getDefault());

    public boolean isSetBirthSuccess() {
        return isSetBirthSuccess;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.birthday_fragment_layout, container,
                false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        findAllView(view);
        setAllListener();
        eventId = getArguments().getInt("id");

        if (eventId != -1) {
            initEditData();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRepeatStr = getString(R.string.event_one_year);
        singleList[0] = getString(R.string.event_one_time);
        singleList[1] = getString(R.string.event_one_year);
        advance3Day = getString(R.string.advance_3days);
        birthdayReminder = getString(R.string.birthday_reminder);
    }

    private void initEditData() {
        long dtstart = 0;
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cursor = cr.query(CalendarContract.Events.CONTENT_URI, null,
                "_id=?", new String[] { "" + eventId }, null);
        while (cursor.moveToNext()) {
            title = cursor.getString(cursor.getColumnIndex("title"));
            dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
            String rrule = cursor.getString(cursor.getColumnIndex("rrule"));
            isAdvance = cursor.getInt(cursor
                    .getColumnIndex(Events.AVAILABILITY)) == 1;
            rruleString = getRruleString(rrule);
        }
        // SimpleDateFormat formatDate = new SimpleDateFormat(getContext()
        // .getString(R.string.date_format_short));
        // Calendar calendar = Calendar.getInstance();
        // calendar.setTimeInMillis(dtstart);
        mStartTime = dtstart;
        // selectedMonth = calendar.get(Calendar.MONTH);
        // selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        // Log.d("liaozhenbin", "set m: " + selectedMonth + "set d: "
        // + selectedDay);
        // startDate = formatDate.format(calendar.getTime());
        etPeople.setText(title);
        // birthdayTextView.setText(startDate);
        updateBirthdayTime(mStartTime);
        repeatTextView.setText(rruleString);

    }

    private String getRruleString(String s) {
        String rruleString = null;
        if (s == null) {
            rruleString = getContext().getString(R.string.event_one_time);
            repeatWhich = REPEAT_ONE_TIME;
        }
        // else if (s.equals("FREQ=DAILY;WKST=SU")) {
        // rruleString = getContext().getString(R.string.event_one_day);
        // } else if (s.equals("FREQ=HOURLY;WKST=SU")) {
        // rruleString = getContext().getString(R.string.event_one_hour);
        // } else if (s.equals("FREQ=WEEKLY;WKST=SU")) {
        // rruleString = getContext().getString(R.string.event_one_week);
        // } else if (s.equals("FREQ=MONTHLY;WKST=SU")) {
        // rruleString = getContext().getString(R.string.event_one_month);
        // }
        else if (s.equals("FREQ=YEARLY;WKST=SU")) {
            rruleString = getContext().getString(R.string.event_one_year);
            repeatWhich = REPEAT_YEARLY;
        }
        return rruleString;
    }

    private void findAllView(View view) {
        etPeople = (EditText) view.findViewById(R.id.birthday_people_et);
        ivAddContacts = (ImageView) view.findViewById(R.id.add_contacts);
        birthdayLayout = view.findViewById(R.id.birthday_layout);
        repeatLayout = view.findViewById(R.id.repeat_layout);
        birthdayTextView = (TextView) view.findViewById(R.id.birthday_tv);
        repeatTextView = (TextView) view.findViewById(R.id.repeat_tv);
        reminderLayout = view.findViewById(R.id.reminder_layout);
        icon_end1 = (ImageView) view.findViewById(R.id.icon_end1);
        reminder_text_tv = (TextView) view.findViewById(R.id.reminder_text_tv);
        // SimpleDateFormat formatDate = new SimpleDateFormat(getContext()
        // .getString(R.string.date_format_short));
        // Calendar calendar = Calendar.getInstance();
        //
        // // calendar.setTimeInMillis(System.currentTimeMillis());
        // mStartTime = calendar.getTimeInMillis();
        // Log.d("liaozhenbin",
        // "year" + calendar.get(Calendar.YEAR)
        // + calendar.get(Calendar.HOUR_OF_DAY));
        /* modify by Yusong.Liang for GMOS-6946 on 2017.9.14:start */
        // defaultDate = formatDate.format(calendar.getTime());
        mStartTime = getArguments().getLong("beginTime");
        // defaultDate = formatDate.format();
        /* modify by Yusong.Liang for GMOS-6946 on 2017.9.14:end */
        // calendar.setTimeInMillis(getArguments().getLong("beginTime"));
        // calendar.set(Calendar.HOUR_OF_DAY, 0);
        // calendar.set(Calendar.MINUTE, 0);
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND, 0);

        // selectedYear = calendar.get(Calendar.YEAR);
        // selectedMonth = calendar.get(Calendar.MONTH);
        // selectedDay = calendar.get(Calendar.DAY_OF_MONTH);

        // birthdayTextView.setText(defaultDate);
        updateBirthdayTime(mStartTime);
        repeatTextView.setText(singleList[1]);
    }

    private void updateBirthdayTime(long timeInMillis) {
        if (timeInMillis == 0)
            timeInMillis = System.currentTimeMillis();
        String ymd = DateFormatter.format(timeInMillis, getResources());
        String hm = DateFormatter.formatClock(timeInMillis, getContext());
        birthdayTextView.setText(ymd);
        reminder_text_tv.setText(String.format(isAdvance ? advance3Day
                : birthdayReminder, hm));
    }

    private void setAllListener() {
        ivAddContacts.setOnClickListener(this);
        birthdayLayout.setOnClickListener(this);
        repeatLayout.setOnClickListener(this);
        reminderLayout.setOnClickListener(this);
    }

    int repeatWhich = REPEAT_YEARLY;
    private GMTimePicker timePicker;

    private void showRepeatChoiceDialog() {
        // CalendarUtil.darkBackground(getActivity());
        isShowDialog = true;
        GomeAlertDialog.Builder builder = new GomeAlertDialog.Builder(
                getActivity());
        builder.setTitle(getContext().getString(R.string.event_set_again));
        builder.setSingleChoiceItems(singleList, repeatWhich,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        repeatWhich = which;
                        mRepeatStr = singleList[repeatWhich];
                        repeatTextView.setText(mRepeatStr);
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

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    // fushuo begin
    public boolean saveData(boolean isEdit) {
        // String title = etTitle.getText().toString().trim();
        // String location = etLocation.getText().toString().trim();
        // String desc = etRemark.getText().toString().trim();
        // int allDay = allDayLayout.isChecked() ? 1 : 0;
        EventBean bean = new EventBean();
        bean.title = etPeople.getText().toString().trim();
        // bean.end = date != null ? date.getTime() : 0;
        // bean.start = date != null ? date.getTime() : 0;
        bean.start = DateUtils.moveSecond(mStartTime);
        bean.end = DateUtils.moveSecond(mStartTime);

        if (repeatWhich == REPEAT_YEARLY) {
            bean.rule = "FREQ=YEARLY;WKST=SU";
        }
        // fushuo begin
        bean.color = Constant.BIRTHDAY;
        // fushuo end
        if (TextUtils.isEmpty(bean.title)) {
            return false;
        }
        if (isEdit) {
            EventUtils.updateBirthdayEvent(getActivity(), bean, eventId,
                    isAdvance);
        } else {
            EventUtils.addBirthdayEvent(getActivity(), bean, isAdvance);
        }
        return true;
    }

    // fushuo end

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.add_contacts:
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            startActivityForResult(intent, 1);
            break;
        case R.id.birthday_layout:
            // StartDatePickerFragment datePickerFragment = new
            // StartDatePickerFragment();
            // datePickerFragment.show(getFragmentManager(), "datePicker");
            if (isShowDialog == false)
                showBirthDayBottoPopupWindow();
            break;
        case R.id.repeat_layout:
            if (isShowDialog == false)
                showRepeatChoiceDialog();
            break;
        case R.id.reminder_layout:
            // if(!isSetBirthSuccess){
            // Toast.makeText(getContext(),
            // getContext().getString(R.string.set_birth_first),
            // Toast.LENGTH_LONG).show();
            // return;
            // }
            if (isShowDialog == false)
                showWeekBottoPopupWindow();
            break;
        default:
            break;
        }
    }

    private void showBirthDayBottoPopupWindow() {
        // TODO Auto-generated method stub
        isShowDialog = true;
        HideSoftInputMethod();
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        // int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);
        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95),
                LayoutParams.WRAP_CONTENT);
        // ScreenInfo screenInfoDate = new ScreenInfo(getActivity());
        // wheelWeekMainDate = new WheelWeekMain(menuView, false);
        // wheelWeekMainDate.screenheight = screenInfoDate.getHeight();
        // String time = DateUtils.currentMonth().toString();
        // Calendar calendar = Calendar.getInstance();
        // if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
        // try {
        // calendar.setTime(new Date(time));
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        // }
        timePicker.setMode(GMTimePicker.FLAG_YEAR | GMTimePicker.FLAG_MONTH
                | GMTimePicker.FLAG_DAY);
        timePicker.setCurrentTime(mStartTime);
        // wheelWeekMainDate.setSelectedBirthdayMonth(selectedMonth);
        // wheelWeekMainDate.setSelectedBirthdayDay(selectedDay - 1);
        // Log.d("liaozhenbin", "m: " + selectedMonth + "d: " + selectedDay);
        // wheelWeekMainDate.initChooseBirthDayAdapter(false);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(icon_end1, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.3f);
        getActivity().getWindow().setNavigationBarColor(
                getResources().getColor(R.color.choose_time_nav));
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView
                .findViewById(R.id.tv_pop_title);
        TextView tv_select_date = (TextView) menuView
                .findViewById(R.id.tv_select_date);
        tv_select_date.setVisibility(View.INVISIBLE);
        tv_pop_title.setText(getContext().getString(R.string.event_set_time));
        OneLineCheckboxLayout cb_show = (OneLineCheckboxLayout) menuView
                .findViewById(R.id.cb_show_yang);
        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);
        ViewGroup nongliViewGroup = (ViewGroup) menuView
                .findViewById(R.id.view_nongli);
        cb_show.setText(getContext().getString(R.string.event_lunar));
        cb_show.setTextColor("#D0D0D0");
        if (CalendarUtil.isEnglish()) {
            nongliViewGroup.setVisibility(View.INVISIBLE);
        }
        cb_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onChange(boolean checked) {
                // wheelWeekMainDate.setShowBirthNong(checked);
                timePicker.setLunarDate(checked);
            }
        });
        gs_nongli
                .setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                            boolean checked) {
                        // wheelWeekMainDate.setShowBirthNong(checked);
                        timePicker.setLunarDate(checked);
                    }
                });
        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        tv_ensure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {                
                final Calendar date = timePicker.getCurrentTime();
                mStartTime = date.getTimeInMillis();
                updateBirthdayTime(mStartTime);
                isSetBirthSuccess = true;
                mPopupWindow.dismiss();
            }
        });

    }

    public void showWeekBottoPopupWindow() {
        isShowDialog = true;
        HideSoftInputMethod();
        WindowManager manager = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        defaultDisplay.getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        // int height = outMetrics.heightPixels;
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View menuView = inflater.inflate(R.layout.show_week_popup_window, null);

        mPopupWindow = new PopupWindow(menuView, (int) (width * 0.95),
                LayoutParams.WRAP_CONTENT);
        // ScreenInfo screenInfoDate = new ScreenInfo(getActivity());
        timePicker = (GMTimePicker) menuView.findViewById(R.id.gm_time_picker);
        timePicker.setMode(GMTimePicker.FLAG_HOUR | GMTimePicker.FLAG_MINUTE);
        timePicker.setCurrentTime(mStartTime);
        mPopupWindow.setAnimationStyle(R.style.AnimationPreview);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.showAtLocation(icon_end1, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOnDismissListener(new PopupDismissListener());
        backgroundAlpha(0.3f);
        getActivity().getWindow().setNavigationBarColor(
                getResources().getColor(R.color.choose_time_nav));
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView
                .findViewById(R.id.tv_pop_title);
        TextView tv_select_date = (TextView) menuView
                .findViewById(R.id.tv_select_date);
        tv_select_date.setVisibility(View.INVISIBLE);
        tv_pop_title.setText(getContext().getString(R.string.event_alert_date));
        final OneLineCheckboxLayout cb_show = (OneLineCheckboxLayout) menuView
                .findViewById(R.id.cb_show_yang);
        cb_show.setText(getContext().getString(R.string.event_alert_three_day));
        gs_nongli = (GomeSwitch) menuView.findViewById(R.id.gs_nongli);
        ViewGroup nongliViewGroup = (ViewGroup) menuView
                .findViewById(R.id.view_nongli);
        nongliViewGroup.setVisibility(View.GONE);
        cb_show.setVisibility(View.VISIBLE);
        cb_show.setTextColor("#D0D0D0");
        cb_show.setChecked(isAdvance);
        cb_show.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onChange(boolean checked) {
            }
        });
        tv_cancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        tv_ensure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                isAdvance = cb_show.isChecked();
                final Calendar time = timePicker.getCurrentTime();
                mTempDate.clear();
                mTempDate.setTimeInMillis(mStartTime);
                mTempDate.set(Calendar.HOUR_OF_DAY,
                        time.get(Calendar.HOUR_OF_DAY));
                mTempDate.set(Calendar.MINUTE, time.get(Calendar.MINUTE));
                mStartTime = mTempDate.getTimeInMillis();
                updateBirthdayTime(mStartTime);
                mPopupWindow.dismiss();
            }
        });
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
            // wheelWeekMainDate.setBirthDay(false);
            // wheelWeekMainDate.setIsChooseBirthDay(false);
            mPopupWindow = null;
            isShowDialog = false;
        }

    }

    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        getActivity().getWindow().setAttributes(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name = null;
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                Cursor cursor = getActivity()
                        .getContentResolver()
                        .query(uri,
                                new String[] {
                                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
                                null, null, null);
                while (cursor.moveToNext()) {
                    String number = cursor.getString(0);
                    name = cursor.getString(1);
                }
                etPeople.setText(name);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateBirthdayTime(mStartTime);
    }
}