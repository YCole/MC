package com.android.calendar.event;

//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//
//import com.android.calendar.CalendarApplication;
//import com.android.calendar.R;
//import com.android.calendar.utils.DateUtils;
//import com.hct.calendar.ui.NumericWheelAdapter;
//import com.hct.calendar.ui.OnWheelScrollListener;
//import com.hct.calendar.ui.WeekAdapter;
//import com.hct.calendar.ui.WheelView;
//import com.hct.calendar.utils.CalendarUtil;
//
//import android.R.integer;
//import android.app.LoadedApk;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;

public class WheelWeekMain {
    // private View view;
    // private WheelView wv_year;
    // private WheelView wv_hours;
    // private WheelView wv_mins;
    // public int screenheight;
    // private boolean hasSelectTime;
    // private List<Map<String, String>> list = new ArrayList<>();
    // private boolean isShow;
    // // private static Calendar calendar = java.util.Calendar.getInstance();
    // private Calendar calendar;
    // private String selectedYMD;
    // // private String selectedHour = calendar.get(Calendar.HOUR_OF_DAY) +
    // ":";
    // private String selectedHour;
    // private String currentHour = "";
    // // private String selectedMinute = "" + calendar.get(Calendar.MINUTE);
    // private String selectedMinute;
    // private TextView tv_select_date;
    // private boolean isFlingYear;
    // private boolean isShowTime;
    // private String TAG = "WheelWeekMain";
    // // private int selectedYear = calendar.get(Calendar.YEAR);
    // // private int selectedMonth = calendar.get(Calendar.MONDAY) + 1;
    // // private int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    // // private String selectedHOUR = calendar.get(Calendar.HOUR_OF_DAY) + "";
    // // private String selectedMINUTE = calendar.get(Calendar.MINUTE) + "";
    // private int selectedYear;
    // private int selectedMonth;
    // private int selectedDay;
    // private String selectedHOUR;
    // private String selectedMINUTE;
    // private String[] lunar;
    // private String currentMinute;
    // private boolean isFlingDate;
    // private boolean isBirthDay;
    // // private String yearStr =
    // // CalendarApplication.getContext().getString(R.string.event_year);
    // // private String monthStr1 =
    // // CalendarApplication.getContext().getString(R.string.event_month);
    // // private String dayStr1 =
    // // CalendarApplication.getContext().getString(R.string.event_day);
    // // private String amPm =
    // // CalendarApplication.getContext().getString(R.string.event_morning);
    // private String yearStr;
    // private String monthStr1;
    // private String dayStr1;
    // private String amPm;
    // private String birthHour = "1";
    // private String birthMinute = "00";
    // private int birthMonth = 1;
    // private int birthDay = 1;
    // // private int jumpYear = Calendar.getInstance().get(Calendar.YEAR);
    // private int jumpYear;
    // private int jumpMonth = 1;
    // private int jumpDay = 1;
    // private int selectedMeetingYear = 0;
    // private int selectedMeetingYear1 = 0;
    // private int selectedMeetingMonth = 0;
    // private int selectedMeetingDay = 0;
    // private int selectedMeetingHour = 0;
    // private int selectedMeetingHourend = 0;
    // private int selectedMeetingMimute = 0;
    // private int selectedMeetingMimuteend = 0;
    // int selectedBirthdayMonth = 0;
    // int selectedScheduleYear = 0;
    // public int getSelectedScheduleYear() {
    // return selectedScheduleYear;
    // }
    //
    // public void setSelectedScheduleYear(int selectedScheduleYear) {
    // this.selectedScheduleYear = selectedScheduleYear;
    // }
    // int selectedScheduleYearInit=0;
    // public int getSelectedScheduleYearInit() {
    // return selectedScheduleYearInit;
    // }
    //
    // public void setSelectedScheduleYearInit(int selectedScheduleYearInit) {
    // this.selectedScheduleYearInit = selectedScheduleYearInit;
    // }
    // int selectedScheduleMonth = 0;
    // public int getSelectedScheduleMonth() {
    // return selectedScheduleMonth;
    // }
    //
    // public void setSelectedScheduleMonth(int selectedScheduleMonth) {
    // this.selectedScheduleMonth = selectedScheduleMonth;
    // }
    //
    // int selectedScheduleDay = 0;
    //
    // public int getSelectedScheduleDay() {
    // return selectedScheduleDay;
    // }
    //
    // public void setSelectedScheduleDay(int selectedScheduleDay) {
    // this.selectedScheduleDay = selectedScheduleDay;
    // }
    // int selectedScheduleHour = 0;
    // int selectedScheduleMinute = 0;
    // public int getSelectedScheduleHour() {
    // return selectedScheduleHour;
    // }
    //
    // public void setSelectedScheduleHour(int selectedScheduleHour) {
    // this.selectedScheduleHour = selectedScheduleHour;
    // }
    //
    // public int getSelectedScheduleMinute() {
    // return selectedScheduleMinute;
    // }
    //
    // public void setSelectedScheduleMinute(int selectedScheduleMinute) {
    // this.selectedScheduleMinute = selectedScheduleMinute;
    // }
    // int selectedScheduleHourend=0;
    // public int getSelectedScheduleHourend() {
    // return selectedScheduleHourend;
    // }
    //
    // public void setSelectedScheduleHourend(int selectedScheduleHourend) {
    // this.selectedScheduleHourend = selectedScheduleHourend;
    // }
    // int selectedScheduleMimuteend=0;
    //
    // public int getSelectedScheduleMimuteend() {
    // return selectedScheduleMimuteend;
    // }
    //
    // public void setSelectedScheduleMimuteend(int selectedScheduleMimuteend) {
    // this.selectedScheduleMimuteend = selectedScheduleMimuteend;
    // }
    // public int getSelectedMeetingYearInit() {
    // return selectedMeetingYear;
    // }
    //
    // public void setSelectedMeetingYearInit(int selectedMeetingYear) {
    // this.selectedMeetingYear = selectedMeetingYear;
    // }
    //
    // public int getSelectedMeetingYear() {
    // return selectedMeetingYear1;
    // }
    //
    // public void setSelectedMeetingYear(int selectedMeetingYear1) {
    // this.selectedMeetingYear1 = selectedMeetingYear1;
    // }
    //
    // public int getSelectedMeetingMonth() {
    // return selectedMeetingMonth;
    // }
    //
    // public void setSelectedMeetingMonth(int selectedMeetingMonth) {
    // this.selectedMeetingMonth = selectedMeetingMonth;
    // }
    //
    // public int getSelectedMeetingDay() {
    // return selectedMeetingDay;
    // }
    //
    // public void setSelectedMeetingDay(int selectedMeetingDay) {
    // this.selectedMeetingDay = selectedMeetingDay;
    // }
    //
    // public int getSelectedMeetingHour() {
    // return selectedMeetingHour;
    // }
    //
    // public void setSelectedMeetingHour(int selectedMeetingHour) {
    // this.selectedMeetingHour = selectedMeetingHour;
    // }
    //
    // public int getSelectedMeetingHourend() {
    // return selectedMeetingHourend;
    // }
    //
    // public void setSelectedMeetingHourend(int selectedMeetingHourend) {
    // this.selectedMeetingHourend = selectedMeetingHourend;
    // }
    //
    // public int getSelectedMeetingMimute() {
    // return selectedMeetingMimute;
    // }
    //
    // public void setSelectedMeetingMimute(int selectedMeetingMimute) {
    // this.selectedMeetingMimute = selectedMeetingMimute;
    // }
    //
    // public int getSelectedMeetingMimuteend() {
    // return selectedMeetingMimuteend;
    // }
    //
    // public void setSelectedMeetingMimuteend(int selectedMeetingMimuteend) {
    // this.selectedMeetingMimuteend = selectedMeetingMimuteend;
    // }
    //
    // public static List<Map<String, String>> yangList = new ArrayList<>();
    // public static List<Map<String, String>> yingList = new ArrayList<>();
    //
    // public int getSelectedBirthdayMonth() {
    // return selectedBirthdayMonth;
    // }
    //
    // public void setSelectedBirthdayMonth(int selectedBirthdayMonth) {
    // birthMonth = selectedBirthdayMonth+1;
    // this.selectedBirthdayMonth = selectedBirthdayMonth;
    // }
    //
    // public int getSelectedBirthdayDay() {
    // return selectedBirthdayDay;
    // }
    //
    // public void setSelectedBirthdayDay(int selectedBirthdayDay) {
    // birthDay = selectedBirthdayDay + 1;
    // this.selectedBirthdayDay = selectedBirthdayDay;
    // }
    //
    // int selectedBirthdayDay = 0;
    //
    // public boolean isJumpNong() {
    // return isJumpNong;
    // }
    //
    // public int getJumpYear() {
    // return jumpYear;
    // }
    //
    // public int getJumpMonth() {
    // return jumpMonth;
    // }
    //
    // public int getJumpDay() {
    // return jumpDay;
    // }
    //
    // public int getBirthMonth() {
    // return birthMonth;
    // }
    //
    // public int getBirthDay() {
    // return birthDay;
    // }
    //
    // public void setBirthDay(boolean isBirthDay) {
    // this.isBirthDay = isBirthDay;
    // }
    //
    // public String getamPm() {
    // return this.amPm;
    // }
    //
    // public String getbirthHour() {
    // return this.birthHour;
    // }
    //
    // public String getbirthMinute() {
    // return this.birthMinute;
    // }
    //
    // public boolean isFlingDate() {
    // return isFlingDate;
    // }
    //
    // public int getSelectedYear() {
    // return selectedYear;
    // }
    //
    // public int getSelectedMonth() {
    // return selectedMonth;
    // }
    //
    // public int getSelectedDay() {
    // return selectedDay;
    // }
    //
    // public String getSelectedHOUR() {
    // return selectedHOUR;
    // }
    //
    // public String getSelectedMINUTE() {
    // return selectedMINUTE;
    // }
    //
    // public void setSelectedYear(int selectedYear) {
    // this.selectedYear = selectedYear;
    // }
    //
    // public void setSelectedMonth(int selectedMonth) {
    // this.selectedMonth = selectedMonth;
    // }
    //
    // public void setSelectedDay(int selectedDay) {
    // this.selectedDay = selectedDay;
    // }
    //
    // public void setSelectedHOUR(String selectedHOUR) {
    // this.selectedHOUR = selectedHOUR;
    // }
    //
    // public void setSelectedMINUTE(String selectedMINUTE) {
    // this.selectedMINUTE = selectedMINUTE;
    // }
    //
    // public View getView() {
    // return view;
    // }
    //
    // public void setView(View view) {
    // this.view = view;
    // }
    //
    // // public WheelWeekMain(View view, boolean hasSelectTime, boolean b, int
    // // selectedYearStart, int selectedMonthStart,
    // // int selectedDayStart, String selectedHOURStart, String
    // // selectedMINUTEStart, int selectedYearEnd,
    // // int selectedMonthEnd, int selectedDayEnd, String selectedHOUREnd,
    // String
    // // selectedMINUTEEnd) {
    // public WheelWeekMain(View view, boolean hasSelectTime) {
    // super();
    // this.view = view;
    // this.hasSelectTime = hasSelectTime;
    // calendar = Calendar.getInstance();
    //
    // // add by zyp for GMOS-3873 start
    // initInitializeData();
    // initDateTimePicker();
    // // add by zyp for GMOS-3873 end
    //
    // selectedYMD = getCurrentLunar();
    //
    // int hour = calendar.get(Calendar.HOUR_OF_DAY);
    // if (hour < 12) {
    // selectedHour =
    // CalendarApplication.getContext().getString(R.string.event_morning)
    // + calendar.get(Calendar.HOUR_OF_DAY) + ":";
    // } else {
    // selectedHour =
    // CalendarApplication.getContext().getString(R.string.event_afternoon)
    // + calendar.get(Calendar.HOUR_OF_DAY) + ":";
    // }
    // if (CalendarUtil.isEnglish()) {
    // yearStr = " ";
    // }
    // currentHour = selectedHour;
    // selectedMinute = "" + calendar.get(Calendar.MINUTE);
    // currentMinute = selectedMinute;
    // setView(view);
    // initViews();
    // initListeners();
    // }
    //
    // private void initInitializeData() {
    // // TODO Auto-generated method stub
    // selectedHour = calendar.get(Calendar.HOUR_OF_DAY) + ":";
    // selectedMinute = "" + calendar.get(Calendar.MINUTE);
    //
    // selectedYear = calendar.get(Calendar.YEAR);
    // selectedMonth = calendar.get(Calendar.MONDAY) + 1;
    // selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    // selectedHOUR = calendar.get(Calendar.HOUR_OF_DAY) + "";
    // selectedMINUTE = calendar.get(Calendar.MINUTE) + "";
    //
    // yearStr =
    // CalendarApplication.getContext().getString(R.string.event_year);
    // monthStr1 =
    // CalendarApplication.getContext().getString(R.string.event_month);
    // dayStr1 = CalendarApplication.getContext().getString(R.string.event_day);
    // amPm =
    // CalendarApplication.getContext().getString(R.string.event_morning);
    //
    // jumpYear = Calendar.getInstance().get(Calendar.YEAR);
    // }
    //
    // private void initViews() {
    // wv_year = (WheelView) view.findViewById(R.id.year);
    // wv_hours = (WheelView) view.findViewById(R.id.hour);
    // wv_mins = (WheelView) view.findViewById(R.id.mins);
    // tv_select_date = (TextView) view.findViewById(R.id.tv_select_date);
    // updateCurrentTime();
    // }
    //
    // private void updateCurrentTime() {
    // if (isShow) {
    // int minute = Calendar.getInstance().get(Calendar.MINUTE);
    // String minuteStr = "";
    // if (minute < 10) {
    // minuteStr = "0" + minute;
    // } else {
    // minuteStr = "" + minute;
    // }
    //
    //
    // tv_select_date.setText(getCurrentLunar() + "    " + currentHour + "" +
    // minuteStr);
    // } else if (isJumpDate) {
    // jumpYear = Calendar.getInstance().get(Calendar.YEAR);
    // jumpMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
    // jumpDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    // if (isJumpNong) {
    // tv_select_date.setText(Calendar.getInstance().get(Calendar.YEAR)
    // +
    // CalendarApplication.getContext().getString(R.string.event_init_luanr_date));
    // } else {
    // tv_select_date.setText(Calendar.getInstance().get(Calendar.YEAR)
    // + CalendarApplication.getContext().getString(R.string.event_init_date));
    // }
    //
    // } else {
    // String monthStr = "";
    // int month = calendar.get(Calendar.MONTH) + 1;
    // if (month < 10) {
    // monthStr = "0" + month;
    // } else {
    // monthStr = "" + month;
    // }
    // String dayStr = "";
    // int day = calendar.get(Calendar.DAY_OF_MONTH);
    // if (day < 10) {
    // dayStr = "0" + day;
    // } else {
    // dayStr = "" + day;
    // }
    //
    //
    // tv_select_date
    // .setText("" + calendar.get(Calendar.YEAR) + yearStr + monthStr +
    // monthStr1 + dayStr + dayStr1);
    //
    // }
    //
    // }
    //
    // private void initListeners() {
    // wv_year.addScrollingListener(new OnWheelScrollListener() {
    // @Override
    // public void onScrollingStarted(WheelView wheel) {
    //
    // }
    //
    // @Override
    // public void onScrollingFinished(WheelView wheel) {
    // if (isBirthDay) {
    // amPm = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // } else if (isJumpDate) {
    // jumpYear =
    // Integer.parseInt(wheel.getAdapter().getItem(wheel.getCurrentItem()));
    // updateJumpTitle(jumpYear, jumpMonth, jumpDay);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // Calendar calendar = Calendar.getInstance();
    // calendar.set(jumpYear, getJumpMonth(), 0);
    // int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    // NumericWheelAdapter adapter = new NumericWheelAdapter(1, maxDay, 0);
    // minute_view.setAdapter(adapter);
    // if (getJumpDay() > maxDay) {
    // minute_view.setCurrentItem(0);
    // }
    // } else {
    // StringBuilder sb = new StringBuilder();
    // isFlingDate = true;
    // if (isShow) {
    // selectedYear = Integer
    // .parseInt(wheel.getAdapter().getYear(wheel.getCurrentItem()).substring(0,
    // 4));
    // String date = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if
    // (date.startsWith(CalendarApplication.getContext().getString(R.string.event_eleven))
    // ||
    // date.startsWith(CalendarApplication.getContext().getString(R.string.event_shier)))
    // {
    //
    // selectedDay = CalendarUtil
    // .getIntDay(wheel.getAdapter().getItem(wheel.getCurrentItem()).substring(3,
    // 5));
    // } else {
    // selectedDay = CalendarUtil
    // .getIntDay(wheel.getAdapter().getItem(wheel.getCurrentItem()).substring(2,
    // 4));
    // }
    // selectedMonth = CalendarUtil
    // .getIntMonth(wheel.getAdapter().getItem(wheel.getCurrentItem()).substring(0,
    // 2));
    //
    // sb.append(wheel.getAdapter().getYear(wheel.getCurrentItem()))
    // .append(wheel.getAdapter().getItem(wheel.getCurrentItem()));
    // selectedYMD = sb.toString();
    // tv_select_date.setText(selectedYMD + "    " + selectedHour +
    // selectedMinute);
    // } else {
    // selectedYear = Integer
    // .parseInt(wheel.getAdapter().getYear(wheel.getCurrentItem()).substring(0,
    // 4));
    // selectedMonth = Integer
    // .parseInt(wheel.getAdapter().getItem(wheel.getCurrentItem()).substring(0,
    // 2));
    // selectedDay = Integer
    // .parseInt(wheel.getAdapter().getItem(wheel.getCurrentItem()).substring(3,
    // 5));
    // sb.append(wheel.getAdapter().getYear(wheel.getCurrentItem()))
    // .append(wheel.getAdapter().getItem(wheel.getCurrentItem()));
    // selectedYMD = sb.toString().substring(0, 11);
    // tv_select_date.setText(selectedYMD);
    // }
    // }
    // }
    // });
    //
    // wv_hours.addScrollingListener(new OnWheelScrollListener() {
    // @Override
    // public void onScrollingStarted(WheelView wheel) {
    //
    // }
    //
    // @Override
    // public void onScrollingFinished(WheelView wheel) {
    // if (isBirthDay) {
    // birthHour = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // return;
    // } else if (isChooseBirthDay) {
    // String birthMonthStr =
    // wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if (isBirthNong) {
    // birthMonth = CalendarUtil.getIntMonth2(birthMonthStr);
    // } else {
    // birthMonth = Integer.parseInt(birthMonthStr);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // Calendar calendar = Calendar.getInstance();
    // calendar.set(getJumpYear(), birthMonth, 0);
    // int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    // NumericWheelAdapter adapter = new NumericWheelAdapter(1, maxDay, 0);
    // minute_view.setAdapter(adapter);
    // if (getBirthDay() > maxDay) {
    // minute_view.setCurrentItem(0);
    // }
    // }
    // } else if (isJumpDate) {
    // String monthStr = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if (!isJumpNong) {
    // jumpMonth = Integer.parseInt(monthStr);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // Calendar calendar = Calendar.getInstance();
    // calendar.set(getJumpYear(), jumpMonth, 0);
    // int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    // NumericWheelAdapter adapter = new NumericWheelAdapter(1, maxDay, 0);
    // minute_view.setAdapter(adapter);
    // if (getJumpDay() > maxDay) {
    // minute_view.setCurrentItem(0);
    // }
    // } else {
    // jumpMonth = CalendarUtil.getIntMonth2(monthStr);
    // }
    // updateJumpTitle(jumpYear, jumpMonth, jumpDay);
    // } else {
    // StringBuilder sb = new StringBuilder();
    // int hour =
    // Integer.parseInt(wheel.getAdapter().getItem(wheel.getCurrentItem()));
    // selectedHOUR = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if (hour < 12) {
    // sb.append(CalendarApplication.getContext().getString(R.string.event_morning)).append(hour)
    // .append(":");
    // } else {
    // sb.append(CalendarApplication.getContext().getString(R.string.event_afternoon)).append(hour)
    // .append(":");
    // }
    // selectedHour = sb.toString();
    // if (isShow) {
    // tv_select_date.setText(selectedYMD + "    " + selectedHour +
    // selectedMinute);
    // }
    // }
    //
    // }
    // });
    // wv_mins.addScrollingListener(new OnWheelScrollListener() {
    // @Override
    // public void onScrollingStarted(WheelView wheel) {
    //
    // }
    //
    // @Override
    // public void onScrollingFinished(WheelView wheel) {
    // if (isBirthDay) {
    // birthMinute = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // return;
    // } else if (isChooseBirthDay) {
    // String birthDayStr = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if (isBirthNong) {
    // birthDay = CalendarUtil.getIntDay(birthDayStr);
    // } else {
    // // 1
    // birthDay = Integer.parseInt(birthDayStr);
    // }
    // } else if (isJumpDate) {
    // String dayStr = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // if (!isJumpNong) {
    // jumpDay = Integer.parseInt(dayStr);
    // } else {
    // jumpDay = CalendarUtil.getIntDay(dayStr);
    // }
    // updateJumpTitle(jumpYear, jumpMonth, jumpDay);
    // } else {
    // selectedMinute = wheel.getAdapter().getItem(wheel.getCurrentItem());
    // selectedMINUTE = selectedMinute;
    // if (isShow) {
    // tv_select_date.setText(selectedYMD + "    " + selectedHour +
    // selectedMinute);
    // }
    // }
    // }
    // });
    // }
    //
    // private void updateJumpTitle(int jumpYear2, int jumpMonth2, int jumpDay2)
    // {
    // // TODO Auto-generated method stub
    // if (isJumpNong) {
    // tv_select_date.setText(
    // jumpYear2 + yearStr + CalendarUtil.getmonth(jumpMonth2 + "") +
    // CalendarUtil.getDay(jumpDay2 + ""));
    // } else {
    // tv_select_date.setText(jumpYear2 + yearStr + jumpMonth2 + monthStr1 +
    // jumpDay2 + dayStr1);
    // }
    // }
    //
    // public void formatDate(List<Map<String, String>> dateList, int y1, int i,
    // int j) {
    // Log.e(TAG, "initDateTimePicker: 2--isShow=-->" + isShow + "---->time=" +
    // System.currentTimeMillis());
    // String strM;
    // String strD;
    // if (i <= 9) {
    // strM = "0" + i;
    // } else {
    // strM = String.valueOf(i);
    // }
    // if (j <= 9) {
    // strD = "0" + j;
    // } else {
    // strD = String.valueOf(j);
    // }
    // Map<String, String> map = new HashMap<>();
    // if (isShow) {
    // try {
    // lunar = null;
    // Log.e(TAG, "initDateTimePicker: 8--isShow=-->" + isShow + "---->year=" +
    // y1 + strM + strD);
    // lunar = CalendarUtil.getLunar(y1 + "", strM, strD);
    // map.put("year", lunar[0]);
    // map.put("data", lunar[1]);
    // Log.e(TAG, "formatDate: " + lunar[0] + "---->" + lunar[1]);
    // list.add(map);
    // Log.e(TAG, "initDateTimePicker: 3--isShow=-->" + isShow + "---->time=" +
    // System.currentTimeMillis());
    // } catch (Exception e) {
    // e.printStackTrace();
    // map.put("year", "" + y1 + yearStr);
    // map.put("data", strM + monthStr1 + strD + dayStr1 + getWeek(y1 + "-" + i
    // + "-" + j));
    // list.add(map);
    // }
    // } else {
    // map.put("year", "" + y1 + yearStr);
    // map.put("data", strM + monthStr1 + strD + dayStr1 + getWeek(y1 + "-" + i
    // + "-" + j));
    // list.add(map);
    // Log.e(TAG, "initDateTimePicker: 4--isShow=-->" + isShow + "---->time=" +
    // System.currentTimeMillis());
    // }
    // }
    //
    // private String getWeek(String pTime) {
    //
    // String Week = "";
    // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    // Calendar c = Calendar.getInstance();
    // try {
    // c.setTime(format.parse(pTime));
    // } catch (ParseException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // String weekStr[] =
    // CalendarApplication.getContext().getResources().getStringArray(R.array.week);
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
    // Week += weekStr[0];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
    // Week += weekStr[1];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.TUESDAY) {
    // Week += weekStr[2];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
    // Week += weekStr[3];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY) {
    // Week += weekStr[4];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
    // Week += weekStr[5];
    // }
    // if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
    // Week += weekStr[6];
    // }
    // return Week;
    // }
    //
    // public void initAdapter(boolean isEnd) {
    // wv_year = (WheelView) view.findViewById(R.id.year);
    // Calendar c = Calendar.getInstance();
    // wv_year.setAdapter(new WeekAdapter(0,
    // CalendarApplication.yangList.size(), CalendarApplication.yangList));
    // wv_year.setCyclic(true);
    // wv_year.setCurrentItem(CalendarApplication.current+selectedScheduleYear);
    //
    // selectedYear=selectedScheduleYearInit;
    // selectedMonth=selectedScheduleMonth;
    // selectedDay=selectedScheduleDay;
    // if(isEnd){
    //
    // selectedHOUR = selectedScheduleHourend+"";
    // selectedMinute = selectedScheduleMimuteend+"";
    // selectedMINUTE = selectedScheduleMimuteend+"";
    // }else{
    // selectedHOUR = selectedScheduleHour+"";
    // selectedMinute = selectedScheduleMinute+"";
    // selectedMINUTE = selectedScheduleMinute+"";
    //
    // }
    // wv_hours = (WheelView) view.findViewById(R.id.hour);
    // wv_mins = (WheelView) view.findViewById(R.id.mins);
    // wv_hours.setAdapter(new NumericWheelAdapter(0, 23, 0));
    // wv_hours.setCyclic(true);
    // wv_hours.setLabel("");
    // int minute = 0;
    // if (isEnd) {
    // // wv_hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY) + 1);
    // wv_hours.setCurrentItem(selectedScheduleHourend);
    // selectedHOUR = selectedScheduleHourend+"";
    // minute = selectedScheduleMimuteend;
    // // selectedHOUR = c.get(Calendar.HOUR_OF_DAY) + 1 + "";
    // } else {
    // // wv_hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
    // wv_hours.setCurrentItem(selectedScheduleHour);
    // minute = selectedScheduleMinute;
    // }
    // // int minute = c.get(Calendar.MINUTE);
    // if (minute < 10) {
    // selectedMINUTE = "0" + minute;
    // } else {
    // selectedMINUTE = "" + minute;
    // }
    // wv_mins.setAdapter(new NumericWheelAdapter(0, 59, 0));
    // wv_mins.setCyclic(true);
    // wv_mins.setLabel("");
    // // wv_mins.setCurrentItem(c.get(Calendar.MINUTE));
    // if (isEnd) {
    // wv_mins.setCurrentItem(selectedScheduleMimuteend);
    // }else{
    // wv_mins.setCurrentItem(selectedScheduleMinute);
    // }
    // int textSize = 0;
    // if (hasSelectTime)
    // textSize = (screenheight / 140) * 4;
    // else
    // textSize = (screenheight / 140) * 4;
    // wv_year.TEXT_SIZE = textSize;
    // wv_hours.TEXT_SIZE = textSize;
    // wv_mins.TEXT_SIZE = textSize;
    // }
    //
    // public void initMeetingAdapter(boolean isEnd) {
    // wv_year = (WheelView) view.findViewById(R.id.year);
    // Calendar c = Calendar.getInstance();
    // wv_year.setAdapter(new WeekAdapter(0,
    // CalendarApplication.yangList.size(), CalendarApplication.yangList));
    // wv_year.setCyclic(true);
    // wv_year.setCurrentItem(CalendarApplication.current+selectedMeetingYear1);
    //
    // selectedYear=selectedMeetingYear;
    // selectedMonth=selectedMeetingMonth;
    // selectedDay=selectedMeetingDay;
    // if(isEnd){
    //
    // selectedHOUR = selectedMeetingHourend+"";
    // selectedMinute = selectedMeetingMimuteend+"";
    // selectedMINUTE = selectedMeetingMimuteend+"";
    // }else{
    // selectedHOUR = selectedMeetingHour+"";
    // selectedMinute = selectedMeetingMimute+"";
    // selectedMINUTE = selectedMeetingMimute+"";
    //
    // }
    // wv_hours = (WheelView) view.findViewById(R.id.hour);
    // wv_mins = (WheelView) view.findViewById(R.id.mins);
    // wv_hours.setAdapter(new NumericWheelAdapter(0, 23, 0));
    // wv_hours.setCyclic(true);
    // wv_hours.setLabel("");
    // int minute = 0;
    // if (isEnd) {
    // // wv_hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY) + 1);
    // wv_hours.setCurrentItem(selectedMeetingHourend);
    // selectedHOUR = selectedMeetingHourend+"";
    // minute = selectedMeetingMimuteend;
    // // selectedHOUR = c.get(Calendar.HOUR_OF_DAY) + 1 + "";
    // } else {
    // // wv_hours.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
    // wv_hours.setCurrentItem(selectedMeetingHour);
    // minute = selectedMeetingMimute;
    // }
    // // int minute = c.get(Calendar.MINUTE);
    // if (minute < 10) {
    // selectedMINUTE = "0" + minute;
    // } else {
    // selectedMINUTE = "" + minute;
    // }
    // wv_mins.setAdapter(new NumericWheelAdapter(0, 59, 0));
    // wv_mins.setCyclic(true);
    // wv_mins.setLabel("");
    // // wv_mins.setCurrentItem(c.get(Calendar.MINUTE));
    // if(isEnd){
    // wv_mins.setCurrentItem(selectedMeetingMimuteend);
    // }else{
    // wv_mins.setCurrentItem(selectedMeetingMimute);
    // }
    // int textSize = 0;
    // if (hasSelectTime)
    // textSize = (screenheight / 140) * 4;
    // else
    // textSize = (screenheight / 140) * 4;
    // wv_year.TEXT_SIZE = textSize;
    // wv_hours.TEXT_SIZE = textSize;
    // wv_mins.TEXT_SIZE = textSize;
    // }
    //
    // public void setShowYang(boolean isShow) {
    // this.isShow = isShow;
    // updateCurrentTime();
    // if (isShow) {
    // wv_year.setAdapter(new WeekAdapter(0,
    // CalendarApplication.yingList.size(), CalendarApplication.yingList));
    // } else {
    // wv_year.setAdapter(new WeekAdapter(0,
    // CalendarApplication.yangList.size(), CalendarApplication.yangList));
    // }
    // wv_year.setCyclic(true);
    // wv_year.setCurrentItem(CalendarApplication.current);
    // wv_hours.setCurrentItem(calendar.get(Calendar.HOUR_OF_DAY));
    // wv_mins.setCurrentItem(Calendar.getInstance().get(Calendar.MINUTE));
    // }
    //
    // public String getCurrentLunar() {
    //
    // int year = calendar.get(Calendar.YEAR);
    // int month = calendar.get(Calendar.MONTH) + 1;
    // int day = calendar.get(Calendar.DAY_OF_MONTH);
    // String monthStr = "";
    // String dayStr = "";
    // if (month < 10) {
    // monthStr = "0" + month;
    // } else {
    // monthStr = "" + month;
    // }
    // if (day < 10) {
    // dayStr = "0" + day;
    // } else {
    // dayStr = "" + day;
    // }
    // try {
    // System.out.println("" + year + monthStr + dayStr);
    // String result[] = CalendarUtil.solarToLunar("" + year + monthStr +
    // dayStr);
    //
    // System.out.println("day=" + result[2]);
    // return "" + result[0] + yearStr + CalendarUtil.getmonth(result[1]) +
    // CalendarUtil.getDay(result[2]);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return "";
    // }
    //
    // private List<Map<String, String>> amPmList = new ArrayList<>();
    // private List<Map<String, String>> hourList = new ArrayList<>();
    // private List<Map<String, String>> minuteList = new ArrayList<>();
    // private boolean isChooseBirthDay;
    // private boolean isBirthNong;
    // private boolean isJumpDate;
    // private boolean isJumpNong;
    //
    // public void setIsJumpDate(boolean isJumpDate) {
    // this.isJumpDate = isJumpDate;
    // }
    //
    // public boolean isBirthNong() {
    // return isBirthNong;
    // }
    //
    // public void initBirthDayAdapter() {
    // // TODO Auto-generated method stub
    // initBirthdayData();
    // isBirthDay = true;
    // WheelView year_view = (WheelView) view.findViewById(R.id.year);
    // WheelView hour_view = (WheelView) view.findViewById(R.id.hour);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // year_view.setwidthProportion(0.25f);
    // hour_view.setwidthProportion(0.25f);
    // minute_view.setwidthProportion(0.25f);
    // year_view.setAdapter(new WeekAdapter(0, amPmList.size(), amPmList));
    // hour_view.setAdapter(new NumericWheelAdapter(1, 12, 0));
    // hour_view.setCyclic(true);
    // minute_view.setAdapter(new NumericWheelAdapter(0, 59, 0));
    // minute_view.setCyclic(true);
    // hour_view.setLabel(CalendarApplication.getContext().getString(R.string.event_hour));
    // minute_view.setLabel(CalendarApplication.getContext().getString(R.string.event_minute_short));
    // }
    //
    // private void initBirthdayData() {
    // // TODO Auto-generated method stub
    // Map<String, String> map = new HashMap<>();
    // map.put("year", "");
    // map.put("data",
    // CalendarApplication.getContext().getString(R.string.event_morning));
    // amPmList.add(map);
    //
    // Map<String, String> map2 = new HashMap<>();
    // map2.put("year", "");
    // map2.put("data",
    // CalendarApplication.getContext().getString(R.string.event_afternoon));
    // amPmList.add(map2);
    //
    // }
    //
    // public void setIsChooseBirthDay(boolean isChooseBirthDay) {
    // this.isChooseBirthDay = isChooseBirthDay;
    // }
    //
    // public void initChooseBirthDayAdapter(boolean isBirthNong) {
    // // TODO Auto-generated method stub
    // isChooseBirthDay = true;
    // WheelView year_view = (WheelView) view.findViewById(R.id.year);
    // WheelView hour_view = (WheelView) view.findViewById(R.id.hour);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // year_view.setVisibility(View.GONE);
    // Locale locale =
    // CalendarApplication.getContext().getResources().getConfiguration().locale;
    // String language = locale.getLanguage();
    // boolean isEnglish = false;
    // if (language.endsWith("en")) {
    // isEnglish = true;
    // }
    // if (!isBirthNong) {
    // hour_view.setwidthProportion(0.32f);
    // minute_view.setwidthProportion(0.32f);
    // hour_view.setAdapter(new NumericWheelAdapter(1, 12, 0));
    // hour_view.setCyclic(true);
    // hour_view.setCurrentItem(selectedBirthdayMonth);
    // minute_view.setAdapter(new NumericWheelAdapter(1, 31, 0));
    // minute_view.setCyclic(true);
    // minute_view.setCurrentItem(selectedBirthdayDay);
    // hour_view.setLabel(monthStr1);
    // minute_view.setLabel(dayStr1);
    // if (isEnglish) {
    // hour_view.setLabel("M");
    // minute_view.setLabel("D");
    // }
    // } else {
    // hour_view.setwidthProportion(0.48f);
    // minute_view.setwidthProportion(0.48f);
    // hour_view.setAdapter(new NumericWheelAdapter(1, 12,
    // NumericWheelAdapter.CATEGORY_MONTH));
    // hour_view.setCyclic(true);
    // minute_view.setAdapter(new NumericWheelAdapter(1, 30,
    // NumericWheelAdapter.CATEGORY_DAY));
    // minute_view.setCyclic(true);
    // hour_view.setLabel("");
    // minute_view.setLabel("");
    // }
    //
    // }
    //
    // public void setShowBirthNong(boolean checked) {
    // isBirthNong = checked;
    // initChooseBirthDayAdapter(isBirthNong);
    // }
    //
    // public void initChooseJumpDateAdapter(boolean checked) {
    // // TODO Auto-generated method stub
    // isJumpDate = true;
    // updateCurrentTime();
    // WheelView year_view = (WheelView) view.findViewById(R.id.year);
    // WheelView hour_view = (WheelView) view.findViewById(R.id.hour);
    // WheelView minute_view = (WheelView) view.findViewById(R.id.mins);
    // int min = CalendarApplication.yearLists.get(0);
    // int max =
    // CalendarApplication.yearLists.get(CalendarApplication.yearLists.size() -
    // 1);
    // year_view.setAdapter(new NumericWheelAdapter(min, max,
    // NumericWheelAdapter.CATEGORY_YEAR));
    // year_view.setCyclic(true);
    // year_view.setLabel(yearStr);
    // if (!isJumpNong) {
    // year_view.setwidthProportion(0.22f);
    // hour_view.setwidthProportion(0.22f);
    // minute_view.setwidthProportion(0.22f);
    // hour_view.setAdapter(new NumericWheelAdapter(1, 12, 0));
    // hour_view.setCyclic(true);
    // minute_view.setAdapter(new NumericWheelAdapter(1,
    // DateUtils.getMonthDays(calendar.get(Calendar.YEAR),
    // calendar.get(Calendar.MONTH)), 0));
    // minute_view.setCyclic(true);
    // hour_view.setLabel(monthStr1);
    // minute_view.setLabel(dayStr1);
    // if (CalendarUtil.isEnglish()) {
    // hour_view.setLabel(" ");
    // minute_view.setLabel(" ");
    // year_view.setLabel(" ");
    // year_view.setwidthProportion(0.25f);
    // hour_view.setwidthProportion(0.25f);
    // minute_view.setwidthProportion(0.25f);
    // }
    // hour_view.setCurrentItem(calendar.get(Calendar.MONTH));
    // minute_view.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
    // year_view.setCurrentItem(calendar.get(Calendar.YEAR) -
    // CalendarApplication.yearLists.get(0));
    // } else {
    // hour_view.setwidthProportion(0.36f);
    // minute_view.setwidthProportion(0.36f);
    // hour_view.setAdapter(new NumericWheelAdapter(1, 12,
    // NumericWheelAdapter.CATEGORY_MONTH));
    // hour_view.setCyclic(true);
    // minute_view.setAdapter(new NumericWheelAdapter(1, 30,
    // NumericWheelAdapter.CATEGORY_DAY));
    // minute_view.setCyclic(true);
    // hour_view.setLabel("");
    // minute_view.setLabel("");
    // if (CalendarUtil.isEnglish()) {
    // year_view.setLabel("");
    // }
    // String date = getCurrentLunar();
    // int lunarYear = Integer.parseInt(date.substring(0, 4));
    // int lunarMonth = CalendarUtil.getIntMonth(date.substring(5, 7));
    // int lunarDay = 0;
    // if (date.length() == 9) {
    // lunarDay = CalendarUtil.getIntDay(date.substring(7, 9));
    // } else if (date.length() == 10) {
    // lunarDay = CalendarUtil.getIntDay(date.substring(8, 10));
    // }
    // jumpYear = lunarYear;
    // jumpMonth = lunarMonth;
    // jumpDay = lunarDay;
    // hour_view.setCurrentItem(lunarMonth - 1);
    // minute_view.setCurrentItem(lunarDay - 1);
    // year_view.setCurrentItem(lunarYear -
    // CalendarApplication.yearLists.get(0));
    // }
    // // hour_view.setCurrentItem(calendar.get(Calendar.MONTH));
    // // minute_view.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH)-1);
    // //
    // year_view.setCurrentItem(calendar.get(Calendar.YEAR)-CalendarApplication.yearLists.get(0));
    // }
    //
    // public void setShowNongInJump(boolean checked) {
    // // TODO Auto-generated method stub
    // isJumpNong = checked;
    // updateCurrentTime();
    // initChooseJumpDateAdapter(isJumpNong);
    // }
    //
    // private void initDateTimePicker() {
    // // yangList.clear();
    // // yingList.clear();
    // Calendar c = Calendar.getInstance();
    // CalendarApplication.current = 0;
    // int y = c.get(Calendar.YEAR);
    // int y1 = y - 1;
    // int y2 = y + 1;
    // for (; y1 <= y2; y1++) {
    // for (int i = 1; i < 13; i++) {
    // if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12)
    // {
    // for (int j = 1; j < 32; j++) {
    // // formatDate(y1, i, j);
    // }
    // } else if (i == 4 || i == 6 || i == 9 || i == 11) {
    // for (int j = 1; j < 31; j++) {
    // // formatDate(y1, i, j);
    // }
    // } else if (i == 2) {
    // if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
    // for (int j = 1; j < 30; j++) {
    // // formatDate(y1, i, j);
    // }
    // } else {
    // for (int j = 1; j < 29; j++) {
    // // formatDate(y1, i, j);
    // }
    // }
    // }
    // }
    // }
    // int year1 = c.get(Calendar.YEAR);
    // int y3 = year1 - 1;
    // int month1 = c.get(Calendar.MONTH);
    // int day1 = c.get(Calendar.DAY_OF_MONTH);
    //
    // for (; y3 <= year1 - 1; y3++) {
    // for (int i = 1; i < 13; i++) {
    // if (i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12)
    // {
    // CalendarApplication.current += 31;
    // } else if (i == 4 || i == 6 || i == 9 || i == 11) {
    // CalendarApplication.current += 30;
    // } else if (i == 2) {
    // if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
    // CalendarApplication.current += 29;
    // } else {
    // CalendarApplication.current += 28;
    // }
    // }
    // }
    // }
    // for (int w = 1; w < month1 + 1; w++) {
    // if (w == 1 || w == 3 || w == 5 || w == 7 || w == 8 || w == 10 || w == 12)
    // {
    // CalendarApplication.current += 31;
    // } else if (w == 4 || w == 6 || w == 9 || w == 11) {
    // CalendarApplication.current += 30;
    // } else if (w == 2) {
    // if ((y % 4 == 0 && y % 100 != 0) || y % 400 == 0) {
    // CalendarApplication.current += 29;
    // } else {
    // CalendarApplication.current += 28;
    // }
    // }
    // }
    // CalendarApplication.current += day1 - 1;
    //
    // }
    //
    // private void formatDate(int y1, int i, int j) {
    // String strM;
    // String strD;
    // if (i <= 9) {
    // strM = "0" + i;
    // } else {
    // strM = String.valueOf(i);
    // }
    // if (j <= 9) {
    // strD = "0" + j;
    // } else {
    // strD = String.valueOf(j);
    // }
    // Map<String, String> map = new HashMap<>();
    // map.put("year", "" + y1 + yearStr);
    // if (CalendarUtil.isEnglish()) {
    // map.put("data", strM + monthStr1 + strD + dayStr1 + " " + getWeek(y1 +
    // "-" + i + "-" + j));
    // } else {
    // map.put("data", strM + monthStr1 + strD + dayStr1 + getWeek(y1 + "-" + i
    // + "-" + j));
    // }
    // yangList.add(map);
    // Map<String, String> yingMap = new HashMap<>();
    // String[] lunar = CalendarUtil.getLunar(y1 + "", strM, strD);
    // yingMap.put("year", lunar[0]);
    // yingMap.put("data", lunar[1]);
    // // Log.e("fushuo", "formatDate: " + lunar[0] + "---->" + lunar[1]);
    // yingList.add(yingMap);
    // }

}
