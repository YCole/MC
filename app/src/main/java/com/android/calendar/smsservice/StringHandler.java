package com.android.calendar.smsservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.util.Log;

import com.android.calendar.R;

public class StringHandler {
    private static final String TAG = "SMSReceiver";

    public static List<Smartreminder> reminder = new ArrayList<Smartreminder>();
    public static List<agenda> agendalist = new ArrayList<agenda>();

    public static List<Date> date = new ArrayList<Date>();
    public static List<Date> dateend = new ArrayList<Date>();

    public static List<Date> time = new ArrayList<Date>();

    public static class agenda {
        String Message;
        String Address;
        String Subject;
        String Location;
        Long Starttime;
        Long Endtime;
        List<Integer> AdvTime = new ArrayList<Integer>(0);
        String Repeat;
        boolean AllDay = false;;
    }

    private static final String black_list[] = { "10086", "10010", "10016",
            "8888", "10198", "8408028", "10001", "10006", "95555", "95588",
            "95566", "95533", "95559", "95561", "11185", "95595", "95568",
            "95501", "95577", "95528", "95558", "95511", "95526", "95508",
            "96523", "95537" };

    private static final String black_list2[] = { "10655386", "10628637",
            "10628727", "10655188", "10655930", "10628722", "10065112" };

    private static final String bank_string[][] = { { "95555", "0" },
            { "1065795555", "0" }, { "1065502010095555", "0" },
            { "95588", "1" }, { "1258395588", "1" }, { "95566", "2" },
            { "106575595566", "2" }, { "95533", "3" }, { "106980095533", "3" },
            { "95559", "4" }, { "10657101409888", "4" },
            { "106590896699888", "4" }, { "95561", "5" }, { "11185", "6" },
            { "1065755801851111", "6" }, { "95595", "7" }, { "95568", "8" },
            { "10657109009556800", "8" }, { "109009556800", "8" },
            { "95501", "9" }, { "95577", "10" },
            { "106575257489095577", "10" }, { "106550571609095577", "10" },
            { "95528", "11" }, { "95558", "12" }, { "9555801", "12" },
            { "106980095558", "12" }, { "106595511", "13" },
            { "106575596120", "13" }, { "10657924365", "13" },
            { "95511", "13" }, { "106575257489601169", "14" },
            { "106550571609601000", "14" }, { "95526", "14" },
            { "1065752574896699", "15" }, { "106598052022", "15" },
            { "106575120058", "15" }, { "95508", "16" },
            { "10655964628", "17" }, { "106575552686", "17" },
            { "10657525748996699", "17" }, { "1065905711000996523", "18" },
            { "1065752551996523", "18" }, { "96523", "18" },
            { "106575257489396588", "19" }, { "106583096588", "19" },
            { "1065905596588", "19" }, { "106550571609096400", "20" },
            { "106980096400", "20" }, { "1065752574896528", "21" },
            { "10698009652896528", "21" }, { "1065902196288", "22" },
            { "1065796288", "22" }, { "1065502196288", "22" },
            { "106575065999", "23" }, { "057788998888", "24" },
            { "106575257489096899", "25" }, { "106980096899", "25" },
            { "106980096016", "26" }, { "106380096511", "27" },
            { "1065793811", "28" }, { "106575257489338", "28" },
            { "1065902193811", "28" }, { "1065589896358", "29" },
            { "95537", "29" }, { "10657552006558", "30" },
            { "106550210096558", "30" }, { "1065752574896558", "30" },
            { "106575257489280888", "31" }, { "1065902596098", "31" },
            { "1065751962999", "32" }, { "10657525748962999", "32" },
            { "1065905711000962999", "32" } };
    private static final String AdKeyword = "\u4f53\u9a8c|\u514d\u8d39|\u8be6\u8be2|((\u62a2|\u8ba2|\u5b9a|\u56e2)\u8d2d)|"
            + "\u70ed\u7ebf|\u9650\u91cf|\u559c\u8baf|\u70ed\u9500|\u624b\u673a\u62a5|"
            + "\u52b2\u7206|\u780d\u4ef7|\u4f18\u60e0|\u54c1\u9274|\u56de\u9988|"
            + "\u62bd\u5956|\u5efb\u8baf|\u83b7\u8d60|\u4fc3\u9500";

    private static String[] Bankname = null;

    private static final String Time_StrFormat[] = {
            "(?<![0-9])(0?[1-9]|1[0-2])(\u65f6|\u70b9|:)([1-5][0-9]|0?[0-9])(AM|PM)",
            "(AM|PM)(0?[1-9]|1[0-2])(\u65f6|\u70b9|:)([1-5][0-9]|0?[0-9])(?![0-9])",
            "(?<![0-9])(0?[1-9]|1[0-9]|2[0-3])(\u65f6|\u70b9|:)([1-5][0-9]|0?[0-9])(?![0-9])",
            "(?<![0-9])(0?[1-9]|1[0-2])(\u65f6|\u70b9|:)(AM|PM)",
            "(AM|PM)(0?[1-9]|1[0-2])(\u65f6|\u70b9)",
            "(?<!([0-9]|AM|PM))(0?[1-9]|1[0-9]|2[0-3])(\u65f6|\u70b9)" };

    private static final String Time_Format[] = { "hh:mmaa", "aahh:mm",
            "HH:mm", "hh:aa", "aahh:", "HH:" };

    private static final String Date_StrFormat[] = {
            "(?<![0-9])([0-9]{4}[-\\.]((0?[1-9]|1[0-2])[-\\.](1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])[-\\.](29|30)|(0?[13578]|1[02])[-\\.]31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)[-\\.]0?2[-\\.]29)",
            "(?<![0-9])(((0?[1-9]|1[0-2])[-\\.](1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])[-\\.](29|30)|(0?[13578]|1[02])[-\\.]31)[-\\.][0-9]{4}|0?2[-\\.]29[-\\.]([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00))",
            "(?<![0-9])(((1[0-9]|2[0-8]|0?[1-9])[-\\.](0?[1-9]|1[0-2])|(29|30)[-\\.](0?[13-9]|1[0-2])|31[-\\.](0?[13578]|1[02]))[-\\.][0-9]{4}|29[-\\.]0?2[-\\.]([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00))",
            "(?<![0-9])(((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)-[0-9]{2}|0?2-29-(0[048]|[2468][048]|[13579][26]))",
            "(?<![0-9])(((1[0-9]|2[0-8]|0?[1-9])-(0?[1-9]|1[0-2])|(29|30)-(0?[13-9]|1[0-2])|31-(0?[13578]|1[02]))-[0-9]{2}|29-0?2-(0[048]|[2468][048]|[13579][26]))",
            "(?<![0-9])([0-9]{2}-((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)|(0[048]|[2468][048]|[13579][26])-0?2-29)",
            "((J[aA][nN]|M[aA][rR]|M[aA][yY]|J[uU][lL]|A[uU][gG]|O[cC][tT]|D[eE][cC]) +?([12][0-9]|3[01]|0?[1-9])|A[pP][rR]|J[uU][nN]|S[eE][pP]|N[oO][vV] +?([12][0-9]|30|0?[1-9])|F[eE][bB] +?([12][0-9]|0?[1-9])) +?[0-9]{4}",
            "(?<![0-9])([0-9]{4}[\u5e74]((0?[1-9]|1[0-2])[\u6708](1[0-9]|2[0-8]|0?[1-9])[\u65e5]|(0?[13-9]|1[0-2])[\u6708](29|30)[\u65e5]|(0?[13578]|1[02])[\u6708]31[\u65e5])|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)[\u5e74]0?2[\u6708]29[\u65e5])",
            "(?<![0-9])([0-9]{2}[\u5e74]((0?[1-9]|1[0-2])[\u6708](1[0-9]|2[0-8]|0?[1-9])[\u65e5]|(0?[13-9]|1[0-2])[\u6708](29|30)[\u65e5]|(0?[13578]|1[02])[\u6708]31[\u65e5])|(0[048]|[2468][048]|[13579][26])[\u5e74]0?2[\u6708]29[\u65e5])",

            "(?<![-0-9]|[a-z]|[A-Z]|#|$|@|\uff04|\uffe5|\u533a|\u5ea7|\u697c)((0?[1-9]|1[0-2])-(1[0-9]|2[0-9]|0?[1-9])|(0?[13-9]|1[0-2])-30|(0?[13578]|1[02])-31)(?![-0-9]|%|\u2103)",
            "(?<![-0-9]|[a-z]|[A-Z]|#|$|@|\uff04|\uffe5|\u533a|\u5ea7|\u697c)((1[0-9]|2[0-9]|0?[1-9])-(0?[1-9]|1[0-2])|30-(0?[13-9]|1[0-2])|31-(0?[13578]|1[02]))(?![-0-9]|%|\u2103)",
            "(?<!([0-9]|\u5e74))((0?[1-9]|1[0-2])\u6708(1[0-9]|2[0-9]|0?[1-9])\u65e5|(0?[13-9]|1[0-2])\u670830\u65e5|(0?[13578]|1[02])\u670831\u65e5)",
            "((J[aA][nN]|M[aA][rR]|M[aA][yY]|J[uU][lL]|A[uU][gG]|O[cC][tT]|D[eE][cC]) +?([12][0-9]|3[01]|0?[1-9])|A[pP][rR]|J[uU][nN]|S[eE][pP]|N[oO][vV] +?([12][0-9]|30|0?[1-9])|F[eE][bB] +?([12][0-9]|0?[1-9]))(?!( [0-9]{4}|[0-9]))" };
    private static final String Date_Format[] = { "yyyy-MM-dd", "MM-dd-yyyy",
            "dd-MM-yyyy", "MM-dd-yy", "dd-MM-yy", "yy-MM-dd", "MMM dd yyyy",
            "yyyy\u5e74MM\u6708dd\u65e5", "yy\u5e74MM\u6708dd\u65e5",

            "MM-dd", "dd-MM", "MM\u6708dd\u65e5", "MMM dd" };

    public static void HandleMessage(Context context, String messagebody,
            String messageaddress) {

        date.clear();
        dateend.clear();

        if (HandleBankMessage(context, messagebody, messageaddress)) {
            return;
        }

        if (InBlackList(context, messageaddress)) {
            return;
        }

        if (HasAdKeyword(context, messagebody, messageaddress)) {
            return;
        }
        HandleNormalMessage(context, messagebody, messageaddress);
    }

    public static void SaveFormatMsg(Context context, int Id, String AddressName) {
        Log.i(TAG, "SaveFormatMsg");
        String message_from = context.getString(R.string.message_from);
        saveEvent(context, agendalist.get(Id).Subject + message_from
                + AddressName, agendalist.get(Id).Location, null,
                agendalist.get(Id).Starttime, agendalist.get(Id).Endtime,
                agendalist.get(Id).AdvTime, agendalist.get(Id).Repeat,
                agendalist.get(Id).AllDay);
        agendalist.remove(Id);
        if (agendalist.size() == 0) {
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(1);
        }

        return;
    }

    public static void SaveNormalMessage(Context context, int Id, int dateId,
            String AddressName) {
        Log.i(TAG, "SaveNormalMessage");
        String message_from = context.getString(R.string.message_from);

        boolean bWholeDay = reminder.get(Id).datestartlist.get(dateId)
                .getSeconds() == 30 ? true : false;
        Log.i(TAG, "SaveNormalMessage, bWholeDay = " + bWholeDay);

        long ldate = reminder.get(Id).datestartlist.get(dateId).getTime();
        long ldateend = reminder.get(Id).dateendlist.get(dateId).getTime();
        Log.i(TAG, "SaveNormalMessage, ldate = " + ldate);
        Time timestart = new Time();
        timestart.set(ldate);
        Log.i(TAG, "SaveNormalMessage, timestart = " + timestart.year + "-"
                + timestart.month + "-" + timestart.monthDay + " "
                + timestart.hour + ":" + timestart.minute);
        Time timeend = new Time();
        timeend.set(ldateend);

        timestart.normalize(true);
        timeend.normalize(true);

        saveEvent(context, timestart.toMillis(true), timeend.toMillis(true),
                bWholeDay, reminder.get(Id).Message + message_from
                        + AddressName);
        reminder.remove(Id);
        if (reminder.size() == 0) {
            NotificationManager nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(0);
        }

        return;
    }

    /*
     * public static boolean IsMobilePhoneNum (String address) { String
     * mobile_num[] = {"13","15","180","185","186","187","188","189"};
     * 
     * address = address.replaceAll("+86", "");
     * 
     * if (address.length() != 11 ) { return false; }
     * 
     * for (int i=0; i<mobile_num.length; i++) { if
     * (address.indexOf(mobile_num[i]) == 0){ Log.i(TAG,
     * "IsMobilePhoneNum, mobile_num"
     * +i+" find, mobile_num = "+mobile_num[i]+", address = "+address); return
     * true; } }
     * 
     * return false; }
     */

    private static boolean HandleBankMessage(Context context,
            String messagebody, String messageaddress) {
        Log.i(TAG, "HandleBankMessage " + messagebody + messageaddress);
        String keyword = "(\u8fd8\u6b3e|\u5e94\u8fd8)";
        String keyword1 = "\u8fd8\u6b3e";
        if (!Pattern.compile(keyword).matcher(messagebody).find()) {
            return false;
        }
        ;

        Bankname = context.getResources().getStringArray(
                R.array.bank_string_array);

        for (int i = 0; i < bank_string.length; i++) {
            if (messageaddress.equals(bank_string[i][0])) {
                Log.i(TAG, "HandleBankMessage , messageaddress equal[" + i
                        + "]");

                if (GetDate(messagebody) == true) {
                    Time timestart = new Time();
                    timestart.set(date.get(0).getTime());
                    Log.i(TAG, "HandleBankMessage, timestart = "
                            + timestart.year + "-" + timestart.month + "-"
                            + timestart.monthDay + " " + timestart.hour + ":"
                            + timestart.minute);
                    Time timeend = new Time();
                    timeend.set(0, timestart.minute, timestart.hour + 1,
                            timestart.monthDay, timestart.month, timestart.year);
                    timestart.normalize(true);
                    timeend.normalize(true);

                    int id = Integer.parseInt(bank_string[i][1]);
                    String Subject = Bankname[id] + keyword1;

                    List<Integer> Adv_Time = new ArrayList<Integer>(0);
                    Adv_Time.add(0);
                    Adv_Time.add(1440);

                    saveEvent(context, Subject, null, messagebody,
                            timestart.toMillis(true), timeend.toMillis(true),
                            Adv_Time, null, false);

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String datestr = df.format(date.get(0));

                    SendBankSMSNotify(context, Subject,
                            context.getString(R.string.saved, datestr));
                    return true;
                }
            }
        }

        return false;
    }

    private static void HandleNormalMessage(Context context,
            String messagebody, String messageaddress) {
        Log.i(TAG, "HandleNormalMessage");

        messagebody = messagebody.replaceAll(
                "\n\u3010\u53d1\u9001\u81eaHCT\u667a\u80fd\u624b\u673a\u3011",
                "");

        if (GetFormatedMsg(context, messagebody, messageaddress) == true) {
            SendShareNotify(context);
            return;
        }

        String tempstr = ReplaceHyphenChar(messagebody);
        tempstr = ReplaceChnNum(tempstr);

        /*
         * if (GetDateTime(tempstr) == true) { Smartreminder smartreminder= new
         * Smartreminder(messageaddress, messagebody);
         * smartreminder.addDate(date, dateend); reminder.add(smartreminder);
         * SendSMSNotify(context); return; }
         */

        if (GetDate(tempstr) == true) {
            Smartreminder smartreminder = new Smartreminder(messageaddress,
                    messagebody);
            smartreminder.addDate(date, dateend);
            reminder.add(smartreminder);
            Log.i(TAG, "reminder.added, size = " + reminder.size());
            SendSMSNotify(context);
        }
    }

    private static int GetTime(String messagebody) {
        Log.i(TAG, "GetTime, messagebody = " + messagebody);

        String strformat1 = "(\u4e2d\u5348|\u534a\u591c|\u591c\u91cc)(1[12]|0?[0-3])[\u65f6\u70b9]";

        time.clear();
        messagebody = messagebody.replaceAll(" +", "");

        Matcher matcher0 = Pattern.compile(strformat1).matcher(messagebody);
        if (matcher0.find()) {
            String timestr = matcher0.group(0);
            Log.i(TAG, "GetTime, matcher1 find, String temp = " + timestr);

            Matcher mchr = Pattern.compile("(1[12]|0?[0-3])").matcher(timestr);
            if (mchr.find()) {
                int num = Integer.parseInt(mchr.group(0));
                Log.i(TAG, "GetTime, mchr find, time num = " + num);

                if (num == 11) {
                    messagebody = messagebody.replaceAll("\u4e2d\u5348", "AM");
                    messagebody = messagebody.replaceAll(
                            "\u534a\u591c|\u591c\u91cc", "PM");
                } else {
                    if (num != 0) {
                        messagebody = messagebody.replaceAll("\u4e2d\u5348",
                                "PM");
                    }
                    messagebody = messagebody.replaceAll(
                            "\u534a\u591c|\u591c\u91cc", "AM");
                    messagebody = messagebody.replaceAll("AM0?0", "AM12");
                }
            }
        }

        // String temp = messagebody.replaceAll("[\u65f6\u70b9]", ":");
        String temp = messagebody.replaceAll(
                "am|\u4e0a\u5348|\u65e9\u4e0a|\u65e9\u6668|\u51cc\u6668", "AM");
        temp = temp.replaceAll(
                "pm|\u4e0b\u5348|\u5348\u540e|\u508d\u665a|\u665a\u4e0a", "PM");

        for (int i = 0; i < Time_StrFormat.length; i++) {
            Matcher matcher = Pattern.compile(Time_StrFormat[i]).matcher(temp);

            if (matcher.find()) {
                String timestr = matcher.group(0);
                timestr = timestr.replaceAll("[\u65f6\u70b9]", ":");
                Log.i(TAG, "GetTime, matcher" + i + " find, String timestr = "
                        + timestr);
                try {
                    SimpleDateFormat df = new SimpleDateFormat(Time_Format[i],
                            Locale.ENGLISH);
                    Date dt = df.parse(timestr);
                    Log.i(TAG,
                            "GetTime, Date dt = " + dt.getYear() + "-"
                                    + dt.getMonth() + "-" + dt.getDate() + " "
                                    + dt.getHours() + ":" + dt.getMinutes()
                                    + ":" + dt.getSeconds());
                    time.add(dt);
                    return 1;
                } catch (Exception e) {
                    Log.i(TAG, "GetTime, Exception");
                    continue;
                }
            }
        }
        int timeperiod = GetTimePeriod(messagebody);

        return timeperiod;
    }

    private static int GetTimePeriod(String messagebody) {
        boolean bPeriod = false;
        String PeriodWord[] = { "\u51cc\u6668", "\u65e9\u6668|\u65e9\u4e0a",
                "\u4e0a\u5348", "\u4e2d\u5348", "\u4e0b\u5348|\u5348\u540e",
                "\u508d\u665a", "\u665a\u4e0a" };
        int PeriodTime[][] = { { 0, 6 }, { 6, 9 }, { 8, 11 }, { 11, 13 },
                { 13, 18 }, { 17, 19 }, { 19, 23 } };

        Date dt = new Date();
        dt.setMinutes(0);
        Date dtend = (Date) dt.clone();

        for (int i = 0; i < PeriodWord.length; i++) {
            Matcher matcher = Pattern.compile(PeriodWord[i]).matcher(
                    messagebody);
            if (matcher.find()) {
                bPeriod = true;
                dt.setHours(PeriodTime[i][0]);
                time.add(dt);
                dtend.setHours(PeriodTime[i][1]);
                time.add(dtend);
            }
        }

        return bPeriod ? 2 : 0;
    }

    private static boolean GetDate(String messagebody) {
        Date datenow = new Date();

        String strformat2[] = {
                "[\u4eca\u660e\u540e]\u65e5",
                "(\u4e0b\u4e0b|[\u4e0b\u8fd9\u672c])?\u5468[1-6|\u65e5]",
                "(?<!(\u5468|[0-9]\u6708|[0-9]))((\u4e0b\u4e0b|[\u4e0b\u8fd9\u672c])\u6708)?(3[01]|[12][0-9]|0?[1-9])\u65e5" };

        date.clear();
        dateend.clear();

        for (int i = 0; i < Date_StrFormat.length; i++) {
            Matcher matcher = Pattern.compile(Date_StrFormat[i]).matcher(
                    messagebody);

            while (matcher.find()) {
                String temp = matcher.group(0);
                Log.i(TAG, "GetDate, matcher" + i + " find,String temp = "
                        + temp);
                if (i >= 0 && i <= 2) {
                    temp = temp.replaceAll("\\.", "-");
                }

                try {
                    SimpleDateFormat df = new SimpleDateFormat(Date_Format[i],
                            Locale.ENGLISH);
                    Date dt = df.parse(temp);
                    Log.i(TAG,
                            "GetDate, Date dt = " + dt.getYear() + "-"
                                    + dt.getMonth() + "-" + dt.getDate() + " "
                                    + dt.getHours() + ":" + dt.getMinutes()
                                    + ":" + dt.getSeconds());
                    if (dt.getYear() == 70) {
                        if (datenow.getMonth() > dt.getMonth()
                                || (datenow.getMonth() == dt.getMonth() && datenow
                                        .getDate() > dt.getDate())) {
                            dt.setYear(datenow.getYear() + 1);
                        } else {
                            dt.setYear(datenow.getYear());
                        }
                        Log.i(TAG,
                                "GetDate, Date dt = " + dt.getYear() + "-"
                                        + dt.getMonth() + "-" + dt.getDate()
                                        + " " + dt.getHours() + ":"
                                        + dt.getMinutes() + ":"
                                        + dt.getSeconds());
                        Date datecheck = (Date) datenow.clone();
                        datecheck.setMonth(datenow.getMonth() + 6);
                        if (dt.after(datecheck)) {
                            Log.i(TAG, "GetDate, dt is after halfyear!");
                            continue;
                        }
                        if (dt.getMonth() == 2
                                && dt.getDate() == 1
                                && (temp.indexOf("2-29") != -1
                                        || temp.indexOf("29-02") != -1 || temp
                                        .indexOf("29-2") != -1)) {
                            Date datetemp = (Date) dt.clone();
                            datetemp.setDate(dt.getDate() - 1);
                            if (datetemp.getDate() == 29) {
                                dt = datetemp;
                            }
                        }
                    }

                    int timenum = GetTime(messagebody);
                    Date dtend = (Date) dt.clone();
                    if (timenum == 0) {
                        dt.setHours(8);
                        dt.setSeconds(30);
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(dt.getHours() + 1);
                        dateend.add(dtend);
                    } else if (timenum == 1) {
                        dt.setHours(time.get(0).getHours());
                        dt.setMinutes(time.get(0).getMinutes());
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(dt.getHours() + 1);
                        dtend.setMinutes(dt.getMinutes());
                        dateend.add(dtend);
                    } else {
                        dt.setHours(time.get(0).getHours());
                        dt.setMinutes(time.get(0).getMinutes());
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(time.get(1).getHours());
                        dtend.setMinutes(time.get(1).getMinutes());
                        dateend.add(dtend);
                    }

                } catch (Exception e) {
                    Log.i(TAG, "GetDateTime, Exception");
                    continue;
                }
            }
        }

        if (!date.isEmpty())
            return true;

        Date dt_now = new Date();
        dt_now.setTime(System.currentTimeMillis());
        dt_now.setMinutes(0);
        dt_now.setSeconds(0);

        for (int i = 0; i < strformat2.length; i++) {
            Pattern p = Pattern.compile(strformat2[i]);
            Matcher matcher = p.matcher(messagebody);
            if (matcher.find()) {
                switch (i) {
                case 0: {
                    Log.i(TAG, "GetDate, matcher2." + i + " find");
                    String temp = matcher.group(0);
                    Log.i(TAG, "GetDate, String temp = " + temp);
                    Date dt = (Date) dt_now.clone();
                    if (temp.indexOf("\u4eca") != -1) {
                        Log.i(TAG, "GetDate, today find");
                    } else if (temp.indexOf("\u660e") != -1) {
                        Log.i(TAG, "GetDate, tomorrow find");
                        dt.setDate(dt.getDate() + 1);
                    } else {
                        Log.i(TAG, "GetDate, day after tomorrow find");
                        dt.setDate(dt.getDate() + 2);
                    }

                    int timenum = GetTime(messagebody);
                    Date dtend = (Date) dt.clone();
                    if (timenum == 0) {
                        dt.setHours(8);
                        dt.setSeconds(30);
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(dt.getHours() + 1);
                        dateend.add(dtend);
                    } else if (timenum == 1) {
                        dt.setHours(time.get(0).getHours());
                        dt.setMinutes(time.get(0).getMinutes());
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(dt.getHours() + 1);
                        dateend.add(dtend);
                    } else {
                        dt.setHours(time.get(0).getHours());
                        dt.setMinutes(time.get(0).getMinutes());
                        if (!CheckDateValid(dt)) {
                            continue;
                        }
                        date.add(dt);
                        dtend.setHours(time.get(1).getHours());
                        dtend.setMinutes(time.get(1).getMinutes());
                        dateend.add(dtend);
                    }
                    break;
                }
                case 1: {
                    Log.i(TAG, "GetDate, matcher2." + i + " find");
                    String temp = matcher.group(0);
                    Log.i(TAG, "GetDate, String temp = " + temp);
                    Date dt = (Date) dt_now.clone();

                    temp = temp.replaceAll("\u65e5", "7");
                    Log.i(TAG, "GetDate, String temp = " + temp);

                    Pattern ptn = Pattern.compile("[1-7]");
                    Matcher mchr = ptn.matcher(temp);
                    if (mchr.find()) {
                        Log.i(TAG, "GetDate, mchr find");
                        int num = Integer.parseInt(mchr.group(0));
                        Log.i(TAG, "GetDate, week num = " + num);
                        if (temp.indexOf("\u8fd9") != -1
                                || temp.indexOf("\u672c") != -1) {
                            dt.setDate(dt.getDate() + num - dt.getDay());
                            Log.i(TAG, "GetDate, this week, " + dt.getMonth()
                                    + "-" + dt.getDate());
                        } else if (temp.indexOf("\u4e0b\u4e0b") != -1) {
                            dt.setDate(dt.getDate() + num - dt.getDay() + 14);
                            Log.i(TAG,
                                    "GetDate, next next week, " + dt.getMonth()
                                            + "-" + dt.getDate());
                        } else if (temp.indexOf("\u4e0b") != -1) {
                            dt.setDate(dt.getDate() + num - dt.getDay() + 7);
                            Log.i(TAG, "GetDate, next week, " + dt.getMonth()
                                    + "-" + dt.getDate());
                        } else {
                            dt.setDate(dt.getDate() + (num - dt.getDay() + 7)
                                    % 7);
                            Log.i(TAG, "GetDate, week, " + dt.getMonth() + "-"
                                    + dt.getDate());
                        }
                        int timenum = GetTime(messagebody);
                        Date dtend = (Date) dt.clone();
                        if (timenum == 0) {
                            dt.setHours(8);
                            dt.setSeconds(30);
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(dt.getHours() + 1);
                            dateend.add(dtend);
                        } else if (timenum == 1) {
                            dt.setHours(time.get(0).getHours());
                            dt.setMinutes(time.get(0).getMinutes());
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(dt.getHours() + 1);
                            dateend.add(dtend);
                        } else {
                            dt.setHours(time.get(0).getHours());
                            dt.setMinutes(time.get(0).getMinutes());
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(time.get(1).getHours());
                            dtend.setMinutes(time.get(1).getMinutes());
                            dateend.add(dtend);
                        }
                    }
                    break;
                }
                case 2: {
                    Log.i(TAG, "GetDate, matcher2." + i + " find");
                    String temp = matcher.group(0);
                    Log.i(TAG, "GetDate, String temp = " + temp);
                    Date dt = (Date) dt_now.clone();

                    Pattern ptn = Pattern.compile("(3[01]|[12][0-9]|0?[1-9])");
                    Matcher mchr = ptn.matcher(temp);
                    if (mchr.find()) {
                        Log.i(TAG, "GetDate, mchr find");
                        int num = Integer.parseInt(mchr.group(0));
                        Log.i(TAG, "GetDate, date num = " + num);

                        if (temp.indexOf("\u8fd9") != -1
                                || temp.indexOf("\u672c") != -1) {
                            dt.setDate(num);
                            Log.i(TAG, "GetDate, this month, " + dt.getMonth()
                                    + "-" + dt.getDate());
                        } else if (temp.indexOf("\u4e0b\u4e0b") != -1) {
                            int month = dt.getMonth() + 2;
                            dt.setMonth(month);
                            dt.setMonth(month);
                            dt.setDate(num);
                            Log.i(TAG,
                                    "GetDate, next next month, "
                                            + dt.getMonth() + "-"
                                            + dt.getDate());
                        } else if (temp.indexOf("\u4e0b") != -1) {
                            int month = dt.getMonth() + 1;
                            dt.setMonth(month);
                            dt.setMonth(month);
                            dt.setDate(num);
                            Log.i(TAG, "GetDate, next month, " + dt.getMonth()
                                    + "-" + dt.getDate());
                        } else {
                            Log.i(TAG, "GetDate, dtnow = " + dt.getMonth()
                                    + "-" + dt.getDate());
                            int month = dt.getMonth()
                                    + (num <= dt.getDate() ? 1 : 0);
                            Log.i(TAG, "GetDate, month = " + month);
                            dt.setMonth(month);
                            dt.setMonth(month);
                            dt.setDate(num);
                            Log.i(TAG, "GetDate, month, " + dt.getMonth() + "-"
                                    + dt.getDate());
                        }
                        int timenum = GetTime(messagebody);
                        Date dtend = (Date) dt.clone();
                        if (timenum == 0) {
                            dt.setHours(8);
                            dt.setSeconds(30);
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(dt.getHours() + 1);
                            dateend.add(dtend);
                        } else if (timenum == 1) {
                            dt.setHours(time.get(0).getHours());
                            dt.setMinutes(time.get(0).getMinutes());
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(dt.getHours() + 1);
                            dateend.add(dtend);
                        } else {
                            dt.setHours(time.get(0).getHours());
                            dt.setMinutes(time.get(0).getMinutes());
                            if (!CheckDateValid(dt)) {
                                continue;
                            }
                            date.add(dt);
                            dtend.setHours(time.get(1).getHours());
                            dtend.setMinutes(time.get(1).getMinutes());
                            dateend.add(dtend);
                        }
                    }
                    break;
                }
                default:
                    break;
                }
            }

        }

        return !date.isEmpty();
    }

    private static boolean CheckDateValid(Date dt) {
        Date datenow = new Date();
        if (dt.compareTo(datenow) <= 0) {
            return false;
        }

        for (int i = 0; i < date.size(); i++) {
            if (dt.compareTo(date.get(i)) == 0) {
                return false;
            }
        }
        return true;
    }

    /*
     * private static boolean GetDateTime(String messagebody) {
     * 
     * Date datenow = new Date();
     * 
     * Log.i(TAG, "GetDateTime, messagebody = "+messagebody); String
     * strformat[]= {
     * "(?<![0-9])([0-9]{4}-((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)-0?2-29) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])(((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)-[0-9]{4}|0?2-29-([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])(((1[0-9]|2[0-8]|0?[1-9])-(0?[1-9]|1[0-2])|(29|30)-(0?[13-9]|1[0-2])|31-(0?[13578]|1[02]))-[0-9]{4}|29-0?2-([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])(((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)-[0-9]{2}|0?2-29-(0[048]|[2468][048]|[13579][26])) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])(((1[0-9]|2[0-8]|0?[1-9])-(0?[1-9]|1[0-2])|(29|30)-(0?[13-9]|1[0-2])|31-(0?[13578]|1[02]))-[0-9]{2}|29-0?2-(0[048]|[2468][048]|[13579][26])) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])([0-9]{2}-((0?[1-9]|1[0-2])-(1[0-9]|2[0-8]|0?[1-9])|(0?[13-9]|1[0-2])-(29|30)|(0?[13578]|1[02])-31)|(0[048]|[2468][048]|[13579][26])-0?2-29) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "((J[aA][nN]|M[aA][rR]|M[aA][yY]|J[uU][lL]|A[uU][gG]|O[cC][tT]|D[eE][cC]) +?([12][0-9]|3[01]|0?[1-9])|A[pP][rR]|J[uU][nN]|S[eE][pP]|N[oO][vV] +?([12][0-9]|30|0?[1-9])|F[eE][bB] (|[12][0-9]|0?[1-9])) +?[0-9]{4} +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * 
     * "(?<![0-9])([0-9]{4}[\u5e74]((0?[1-9]|1[0-2])[\u6708](1[0-9]|2[0-8]|0?[1-9])[\u65e5]|(0?[13-9]|1[0-2])[\u6708](29|30)[\u65e5]|(0?[13578]|1[02])[\u6708]31[\u65e5])|([0-9]{2}(0[48]|[2468][048]|[13579][26])|(0[48]|[2468][048]|[13579][26])00)[\u5e74]0?2[\u6708]29[\u65e5]) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * ,
     * "(?<![0-9])([0-9]{2}[\u5e74]((0?[1-9]|1[0-2])[\u6708](1[0-9]|2[0-8]|0?[1-9])[\u65e5]|(0?[13-9]|1[0-2])[\u6708](29|30)[\u65e5]|(0?[13578]|1[02])[\u6708]31[\u65e5])|(0[048]|[2468][048]|[13579][26])[\u5e74]0?2[\u6708]29[\u65e5]) +?(0?[0-9]|1[0-9]|2[0-3]):([1-5][0-9]|0?[0-9])"
     * }; String dateformat[]= {"yyyy-MM-dd HH:mm", "MM-dd-yyyy HH:mm",
     * "dd-MM-yyyy HH:mm", "MM-dd-yy HH:mm", "dd-MM-yy HH:mm", "yy-MM-dd HH:mm",
     * "MMM dd yyyy HH:mm",
     * 
     * "yyyy\u5e74MM\u6708dd\u65e5 HH:mm", "yy\u5e74MM\u6708dd\u65e5 HH:mm" };
     * 
     * date.clear(); dateend.clear();
     * 
     * for (int i=0; i<strformat.length; i++) { Pattern p =
     * Pattern.compile(strformat[i]); Matcher matcher = p.matcher(messagebody);
     * 
     * while (matcher.find()) { Log.i(TAG, "GetDateTime, matcher"+i+" find");
     * String temp = matcher.group(0); Log.i(TAG, "GetDateTime, String temp = "
     * + temp); try { SimpleDateFormat df = new SimpleDateFormat(dateformat[i],
     * Locale.ENGLISH); Date dt = df.parse(temp); Log.i(TAG,
     * "GetDateTime, Date dt = "
     * +dt.getYear()+"-"+dt.getMonth()+"-"+dt.getDate()+
     * " "+dt.getHours()+":"+dt.getMinutes()+":"+dt.getSeconds()); if
     * (dt.compareTo(datenow) > 0) { date.add(dt); Date dtend = (Date)
     * dt.clone(); dtend.setHours(dt.getHours()+1); dateend.add(dtend); }
     * 
     * } catch (Exception e) { Log.i(TAG, "GetDateTime, Exception"); continue; }
     * } }
     * 
     * Log.i(TAG, "GetDateTime, date.size()="+date.size()); return
     * !date.isEmpty(); }
     */

    private static boolean GetFormatedMsg(Context context, String messagebody,
            String messageaddress) {
        Log.i(TAG, "GetFormatedMsg, messagebody = " + messagebody);
        String strformat = "\u4e3b\u9898:(.*?)\n\u5730\u70b9:(.*?)\n[0-9]{4}/[0-9]{2}/[0-9]{2}(.*?)\n\u63d0\u9192:(.*?)\n\u91cd\u590d:(.*?)\u3002";
        Pattern p = Pattern.compile(strformat);
        Matcher matcher = p.matcher(messagebody);

        Date date_start = new Date();
        Date date_end = new Date();
        String Repeat = new String();
        String Reminder = new String();
        if (matcher.find()) {
            agenda agendatemp = new agenda();
            agendalist.add(agendatemp);

            agendatemp.Message = messagebody;
            agendatemp.Address = messageaddress;
            Log.i(TAG, "GetFormatedMsg, matcher find");
            String temp = matcher.group(0);
            Log.i(TAG, "GetFormatedMsg, String temp = " + temp);

            String strformat1 = "(?<=\u4e3b\u9898:)(.*?)(?=\n\u5730\u70b9)";
            Pattern p1 = Pattern.compile(strformat1);
            Matcher matcher1 = p1.matcher(temp);
            if (matcher1.find()) {
                agendatemp.Subject = matcher1.group(0);
                Log.i(TAG, "GetFormatedMsg, String Subject = "
                        + agendatemp.Subject);
            }

            String strformat2 = "(?<=\u5730\u70b9:)(.*?)(?=\n[0-9]{4})";
            Pattern p2 = Pattern.compile(strformat2);
            Matcher matcher2 = p2.matcher(temp);
            if (matcher2.find()) {
                agendatemp.Location = matcher2.group(0);
                Log.i(TAG, "GetFormatedMsg, String Location = "
                        + agendatemp.Location);
            }

            String strformat3 = "[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}-[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}";
            Pattern p3 = Pattern.compile(strformat3);
            Matcher matcher3 = p3.matcher(temp);
            String strformat4 = "[0-9]{4}/[0-9]{2}/[0-9]{2}-[0-9]{4}/[0-9]{2}/[0-9]{2}";
            Pattern p4 = Pattern.compile(strformat4);
            Matcher matcher4 = p4.matcher(temp);
            String strformat5 = "[0-9]{4}/[0-9]{2}/[0-9]{2}";
            Pattern p5 = Pattern.compile(strformat5);
            Matcher matcher5 = p5.matcher(temp);

            if (matcher3.find()) {
                String DateTime = matcher3.group(0);
                Log.i(TAG, "GetFormatedMsg, String DateTime = " + DateTime);
                String dateformat = "yyyy/MM/dd HH:mm";
                SimpleDateFormat df = new SimpleDateFormat(dateformat);

                String starttimeformat = "[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}(?=-)";
                Pattern ptrn_start = Pattern.compile(starttimeformat);
                Matcher matcher_start = ptrn_start.matcher(DateTime);
                if (matcher_start.find()) {
                    String StartTime = matcher_start.group(0);
                    Log.i(TAG, "GetFormatedMsg, StartTime = " + StartTime);
                    try {
                        date_start = df.parse(StartTime);
                        Log.i(TAG,
                                "GetFormatedMsg, date_start = "
                                        + date_start.getYear() + "-"
                                        + date_start.getMonth() + "-"
                                        + date_start.getDate() + " "
                                        + date_start.getHours() + ":"
                                        + date_start.getMinutes() + ":"
                                        + date_start.getSeconds());
                    } catch (Exception e) {
                        Log.i(TAG, "GetFormatedMsg, Exception");
                    }
                }

                String endtimeformat = "(?<=-)[0-9]{4}/[0-9]{2}/[0-9]{2} [0-9]{2}:[0-9]{2}";
                Pattern ptrn_end = Pattern.compile(endtimeformat);
                Matcher matcher_end = ptrn_end.matcher(DateTime);
                if (matcher_end.find()) {
                    String EndTime = matcher_end.group(0);
                    Log.i(TAG, "GetFormatedMsg, EndTime = " + EndTime);
                    try {
                        date_end = df.parse(EndTime);
                        Log.i(TAG,
                                "GetFormatedMsg, date_end = "
                                        + date_end.getYear() + "-"
                                        + date_end.getMonth() + "-"
                                        + date_end.getDate() + " "
                                        + date_end.getHours() + ":"
                                        + date_end.getMinutes() + ":"
                                        + date_end.getSeconds());
                    } catch (Exception e) {
                        Log.i(TAG, "GetFormatedMsg, Exception");
                    }
                }
            } else if (matcher4.find()) {
                agendatemp.AllDay = true;
                String DateTime = matcher4.group(0);
                Log.i(TAG, "GetFormatedMsg, String DateTime = " + DateTime);
                String dateformat = "yyyy/MM/dd";
                SimpleDateFormat df = new SimpleDateFormat(dateformat);

                String starttimeformat = "[0-9]{4}/[0-9]{2}/[0-9]{2}(?=-)";
                Pattern ptrn_start = Pattern.compile(starttimeformat);
                Matcher matcher_start = ptrn_start.matcher(DateTime);
                if (matcher_start.find()) {
                    String StartTime = matcher_start.group(0);
                    Log.i(TAG, "GetFormatedMsg, StartTime = " + StartTime);
                    try {
                        date_start = df.parse(StartTime);
                        date_start.setDate(date_start.getDate() + 1);
                        Log.i(TAG,
                                "GetFormatedMsg, date_start = "
                                        + date_start.getYear() + "-"
                                        + date_start.getMonth() + "-"
                                        + date_start.getDate() + " "
                                        + date_start.getHours() + ":"
                                        + date_start.getMinutes() + ":"
                                        + date_start.getSeconds());
                    } catch (Exception e) {
                        Log.i(TAG, "GetFormatedMsg, Exception");
                    }
                }

                String endtimeformat = "(?<=-)[0-9]{4}/[0-9]{2}/[0-9]{2}";
                Pattern ptrn_end = Pattern.compile(endtimeformat);
                Matcher matcher_end = ptrn_end.matcher(DateTime);
                if (matcher_end.find()) {
                    String EndTime = matcher_end.group(0);
                    Log.i(TAG, "GetFormatedMsg, EndTime = " + EndTime);
                    try {
                        date_end = df.parse(EndTime);
                        date_end.setDate(date_end.getDate() + 2);
                        Log.i(TAG,
                                "GetFormatedMsg, date_end = "
                                        + date_end.getYear() + "-"
                                        + date_end.getMonth() + "-"
                                        + date_end.getDate() + " "
                                        + date_end.getHours() + ":"
                                        + date_end.getMinutes() + ":"
                                        + date_end.getSeconds());
                    } catch (Exception e) {
                        Log.i(TAG, "GetFormatedMsg, Exception");
                    }
                }
            } else if (matcher5.find()) {
                agendatemp.AllDay = true;
                String DateTime = matcher5.group(0);
                Log.i(TAG, "GetFormatedMsg, String DateTime = " + DateTime);
                String dateformat = "yyyy/MM/dd";
                SimpleDateFormat df = new SimpleDateFormat(dateformat);

                try {
                    date_start = df.parse(DateTime);
                    date_start.setDate(date_start.getDate() + 1);
                    date_end = (Date) date_start.clone();
                    date_end.setDate(date_start.getDate() + 1);
                    Log.i(TAG,
                            "GetFormatedMsg, date_start = "
                                    + date_start.getYear() + "-"
                                    + date_start.getMonth() + "-"
                                    + date_start.getDate() + " "
                                    + date_start.getHours() + ":"
                                    + date_start.getMinutes() + ":"
                                    + date_start.getSeconds());
                    Log.i(TAG,
                            "GetFormatedMsg, date_end = " + date_end.getYear()
                                    + "-" + date_end.getMonth() + "-"
                                    + date_end.getDate() + " "
                                    + date_end.getHours() + ":"
                                    + date_end.getMinutes() + ":"
                                    + date_end.getSeconds());
                } catch (Exception e) {
                    Log.i(TAG, "GetFormatedMsg, Exception");
                }

            }

            String strformat6 = "(?<=\u63d0\u9192)(.*?)(?=\n\u91cd\u590d)";
            Pattern p6 = Pattern.compile(strformat6);
            Matcher matcher6 = p6.matcher(temp);
            if (matcher6.find()) {
                Reminder = matcher6.group(0);
                Log.i(TAG, "GetFormatedMsg, String Reminder = " + Reminder);

                String strformat_1 = "(?<=(:|;))(.*?)(?=;)";
                Pattern p_1 = Pattern.compile(strformat_1);
                Matcher matcher_1 = p_1.matcher(Reminder);

                while (matcher_1.find()) {
                    String Reminder1 = matcher_1.group(0);
                    if (Reminder1.indexOf("\u51c6\u65f6") != -1) {
                        agendatemp.AdvTime.add(0);
                    } else {
                        String format_min = "(?<=\u63d0\u524d)(.*?)(?=\u5206\u949f)";
                        Pattern ptn_min = Pattern.compile(format_min);
                        Matcher matcher_min = ptn_min.matcher(Reminder1);

                        String format_hour = "(?<=\u63d0\u524d)(.*?)(?=\u5c0f\u65f6)";
                        Pattern ptn_hour = Pattern.compile(format_hour);
                        Matcher matcher_hour = ptn_hour.matcher(Reminder1);

                        String format_day = "(?<=\u63d0\u524d)(.*?)(?=\u5929)";
                        Pattern ptn_day = Pattern.compile(format_day);
                        Matcher matcher_day = ptn_day.matcher(Reminder1);

                        if (matcher_min.find()) {
                            agendatemp.AdvTime.add(Integer.parseInt(matcher_min
                                    .group(0)));
                        } else if (matcher_hour.find()) {
                            agendatemp.AdvTime.add(Integer
                                    .parseInt(matcher_hour.group(0)) * 60);
                        } else if (matcher_day.find()) {
                            agendatemp.AdvTime.add(Integer.parseInt(matcher_day
                                    .group(0)) * 1440);
                        }
                    }
                }
            }

            String strformat7 = "(?<=\u91cd\u590d:)(.*?)\u3002";
            Pattern p7 = Pattern.compile(strformat7);
            Matcher matcher7 = p7.matcher(temp);
            if (matcher7.find()) {
                Repeat = matcher7.group(0);
                Log.i(TAG, "GetFormatedMsg, String Repeat = " + Repeat);

                if (Repeat.indexOf("\u4e00\u6b21\u6027") != -1) {
                    agendatemp.Repeat = null;
                } else if (Repeat.indexOf("\u6bcf\u5929") != -1) {
                    agendatemp.Repeat = "FREQ=DAILY;WKST=SU";
                } else if (Repeat.indexOf("\u5de5\u4f5c\u65e5") != -1) {
                    agendatemp.Repeat = "FREQ=WEEKLY;WKST=SU;BYDAY=MO,TU,WE,TH,FR";
                } else if (Repeat.indexOf("\u6bcf\u5e74") != -1) {
                    agendatemp.Repeat = "FREQ=YEARLY;WKST=SU";
                } else {
                    String format_weekly = "(?<=\u6bcf\u5468)(.*?)(?=\u3002)";
                    Pattern ptn_weekly = Pattern.compile(format_weekly);
                    Matcher matcher_weekly = ptn_weekly.matcher(Repeat);

                    String format_monthweek = "(?<=\u6bcf\u6708\u7b2c)(.*?)\u4e2a\u5468(.*?)(?=\u3002)";
                    Pattern ptn_monthweek = Pattern.compile(format_monthweek);
                    Matcher matcher_monthweek = ptn_monthweek.matcher(Repeat);

                    String format_monthly = "(?<=\u6bcf\u6708)(.*?)(?=\u65e5)";
                    Pattern ptn_monthly = Pattern.compile(format_monthly);
                    Matcher matcher_monthly = ptn_monthly.matcher(Repeat);

                    if (matcher_weekly.find()) {
                        String weeknum = matcher_weekly.group(0);
                        Log.i(TAG, "GetFormatedMsg, String weeknum = "
                                + weeknum);
                        String replace_tab[][] = { { "\u65e5", "SU" },
                                { "\u516d", "SA" }, { "\u4e94", "FR" },
                                { "\u56db", "TH" }, { "\u4e09", "WE" },
                                { "\u4e8c", "TU" }, { "\u4e00", "MO" } };

                        for (int i = 0; i < replace_tab.length; i++) {
                            weeknum = weeknum.replaceAll(replace_tab[i][0],
                                    replace_tab[i][1]);
                            Log.i(TAG, "GetFormatedMsg, i = " + i
                                    + ", weeknum replaced = " + weeknum);
                        }
                        agendatemp.Repeat = "FREQ=WEEKLY;WKST=SU;BYDAY="
                                + weeknum;
                    } else if (matcher_monthweek.find()) {
                        String monthweek = matcher_monthweek.group(0);

                        String replace_tab[][] = { { "\u516d\u4e2a", "6" },
                                { "\u4e94\u4e2a", "5" },
                                { "\u56db\u4e2a", "4" },
                                { "\u4e09\u4e2a", "3" },
                                { "\u4e8c\u4e2a", "2" },
                                { "\u4e00\u4e2a", "1" },
                                { "\u5468\u65e5", "SU" },
                                { "\u5468\u516d", "SA" },
                                { "\u5468\u4e94", "FR" },
                                { "\u5468\u56db", "TH" },
                                { "\u5468\u4e09", "WE" },
                                { "\u5468\u4e8c", "TU" },
                                { "\u5468\u4e00", "MO" } };

                        for (int i = 0; i < replace_tab.length; i++) {
                            monthweek = monthweek.replaceAll(replace_tab[i][0],
                                    replace_tab[i][1]);
                            Log.i(TAG, "GetFormatedMsg, i = " + i
                                    + ", weeknum replaced = " + monthweek);
                        }
                        agendatemp.Repeat = "FREQ=MONTHLY;WKST=SU;BYDAY="
                                + monthweek;
                    } else if (matcher_monthly.find()) {
                        String monthdaynum = matcher_monthly.group(0);
                        agendatemp.Repeat = "FREQ=MONTHLY;WKST=SU;BYMONTHDAY="
                                + monthdaynum;
                    }
                    agendatemp.Repeat = null;
                }
            }

            long ldate = date_start.getTime();
            long ldateend = date_end.getTime();
            Time timestart = new Time();
            timestart.set(ldate);
            Time timeend = new Time();
            timeend.set(ldateend);
            timestart.normalize(true);
            timeend.normalize(true);

            agendatemp.Starttime = timestart.toMillis(true);
            agendatemp.Endtime = timeend.toMillis(true);

            // saveEvent(context, Subject, Location, timestart.toMillis(true),
            // timeend.toMillis(true), adv_time, Repeat, allDay);

            return true;
        }
        return false;
    }

    private static void SendBankSMSNotify(Context context, String title,
            String notice) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.stat_event;
        Bitmap largeIcom = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notifi_event);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, null, when);
        notification.largeIcon = largeIcom;
        Intent notificationIntent = new Intent(context, HandleBankNote.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent = contentIntent;

        notification.setLatestEventInfo(context, title, notice, contentIntent);
        mNotificationManager.notify(2, notification);
    }

    private static void SendShareNotify(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.stat_event;
        Bitmap largeIcom = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notifi_event);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, null, when);
        notification.largeIcon = largeIcom;
        Intent notificationIntent = new Intent();

        if (agendalist.size() > 1) {
            notificationIntent.setClass(context, MultiDialog.class);
            notificationIntent.putExtra("share", true);
        } else {
            notificationIntent.setClass(context, Dialog.class);
            notificationIntent.putExtra("reminder", 0);
            notificationIntent.putExtra("share", true);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent = contentIntent;
        notification.setLatestEventInfo(context,
                context.getString(R.string.smart_reminder_title),
                context.getString(R.string.smart_reminder_notice),
                contentIntent);
        mNotificationManager.notify(1, notification);
    }

    private static void SendSMSNotify(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.drawable.stat_event;
        Bitmap largeIcom = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.notifi_event);
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, null, when);
        notification.largeIcon = largeIcom;
        Intent notificationIntent = new Intent();

        if (reminder.size() > 1) {
            notificationIntent.setClass(context, MultiDialog.class);
        } else {
            notificationIntent.setClass(context, Dialog.class);
            notificationIntent.putExtra("reminder", 0);
        }

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.contentIntent = contentIntent;
        notification.setLatestEventInfo(context,
                context.getString(R.string.smart_reminder_title),
                context.getString(R.string.smart_reminder_notice),
                contentIntent);
        mNotificationManager.notify(0, notification);
    }

    private static void saveEvent(Context context, long TimeStart,
            long TimeEnd, boolean bWholeDay, String content) {
        ContentValues values = new ContentValues();
        values.put("calendar_id", -1);
        String strTimeZone = TimeZone.getDefault().getID();
        values.put("eventTimezone", strTimeZone);
        values.put("title", content);
        values.put("dtstart", TimeStart);
        values.put("dtend", TimeEnd);
        values.put("hasAlarm", 1);

        Uri testUri = context.getContentResolver().insert(Events.CONTENT_URI,
                values);
        Log.i(TAG, "savebirthday TestUri=" + testUri);
        if (testUri == null)
            return;
        String strNewID = testUri.getLastPathSegment();
        ContentValues valuesReminder = new ContentValues();
        // Uri uri1 = Uri.parse("content://com.android.HCTCalendar/reminders");
        int advTime = 10;

        if (bWholeDay) {
            advTime = 0;
            valuesReminder.put("event_id", strNewID);
            valuesReminder.put("minutes", advTime);
            valuesReminder.put("method", 1);
            context.getContentResolver().insert(Reminders.CONTENT_URI,
                    valuesReminder);
            advTime = 720;
        }
        valuesReminder.put("event_id", strNewID);
        valuesReminder.put("minutes", advTime);
        valuesReminder.put("method", 1);
        context.getContentResolver().insert(Reminders.CONTENT_URI,
                valuesReminder);
    }

    private static void saveEvent(Context context, String Subject,
            String Location, String Description, long TimeStart, long TimeEnd,
            List<Integer> advTime, String Repeat, boolean allDay) {

        // Uri uri = Uri.parse("content://com.android.HCTCalendar/events");
        ContentValues values = new ContentValues();
        values.put("calendar_id", -1);
        if (allDay) {
            values.put("eventTimezone", "UTC");
        } else {
            String strTimeZone = TimeZone.getDefault().getID();
            values.put("eventTimezone", strTimeZone);
        }
        values.put("title", Subject);
        values.put("eventLocation", Location);
        values.put("description", Description);
        values.put("dtstart", TimeStart);
        if (allDay) {
            values.put("dtend", TimeEnd);
            values.put("lastDate", TimeEnd);
            values.put("allDay", 1);
        } else if (Repeat == null) {
            values.put("dtend", TimeEnd);
            values.put("lastDate", TimeEnd);
        } else {
            long seconds = (TimeEnd - TimeStart) / 1000;
            String strduration = "P" + seconds + "S";
            values.put("duration", strduration);
        }
        if (Repeat != null) {
            values.put("rrule", Repeat);
        }

        if (advTime.size() >= 0) {
            values.put("hasAlarm", 1);
        }

        Uri testUri = context.getContentResolver().insert(Events.CONTENT_URI,
                values);
        Log.i(TAG, "savebirthday TestUri=" + testUri);
        if (testUri == null)
            return;

        String strNewID = testUri.getLastPathSegment();
        ContentValues valuesReminder = new ContentValues();
        for (int i = 0; i < advTime.size(); i++) {
            int time = advTime.get(i);
            valuesReminder.put("event_id", strNewID);
            valuesReminder.put("minutes", time);
            valuesReminder.put("method", 1);
            // Uri uri1 =
            // Uri.parse("content://com.android.HCTCalendar/reminders");
            context.getContentResolver().insert(Reminders.CONTENT_URI,
                    valuesReminder);

        }
    }

    private static boolean InBlackList(Context context, String messageaddress) {
        BlackListDBHelper helper = new BlackListDBHelper(context);
        Cursor c = helper.query(messageaddress);
        if (c != null) {
            int count = c.getCount();
            c.close();
            if (count > 0) {
                Log.i(TAG, "InBlackList, number = " + messageaddress);
                return true;
            }
        }

        for (int i = 0; i < black_list.length; i++) {
            Pattern p = Pattern.compile(black_list[i] + "[0-9]?");
            Matcher matcher = p.matcher(messageaddress);

            if (matcher.find()) {
                Log.i(TAG, "InBlackList, matcher" + i + " find, black_list = "
                        + black_list[i]);
                return true;
            }
        }

        for (int i = 0; i < black_list2.length; i++) {
            if (messageaddress.equals(black_list2[i])) {
                return true;
            }
        }
        return false;
    }

    private static boolean HasAdKeyword(Context context, String messagebody,
            String messageaddress) {
        Log.d(TAG, "HasAdKeyword, messageaddress = " + messageaddress);
        messageaddress = messageaddress.replaceAll("\\+86", "");
        Log.d(TAG, "HasAdKeyword, messageaddress2 = " + messageaddress);
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " like '%"
                        + messageaddress + "'", null, null);
        if (cursor != null && cursor.getCount() != 0) {
            Log.d(TAG, "HasAdKeyword, Contact number!");
            cursor.close();
            return false;
        }
        cursor.close();

        String[] projection = { CallLog.Calls.NUMBER, CallLog.Calls.DATE };
        cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                projection,
                CallLog.Calls.NUMBER + " = '" + messageaddress + "'", null,
                null);
        if (cursor != null && cursor.getCount() != 0) {
            Log.d(TAG, "HasAdKeyword, CallLog number!");
            int dateFieldColumnIndex = cursor
                    .getColumnIndex(CallLog.Calls.DATE);
            long datenow = System.currentTimeMillis();
            long oneweek = (long) 7 * 24 * 3600 * 1000;
            Log.d(TAG, "HasAdKeyword, index = " + dateFieldColumnIndex
                    + ", oneweek =" + oneweek);
            while (cursor.moveToNext()) {
                long date = cursor.getLong(dateFieldColumnIndex);
                Log.d(TAG, "HasAdKeyword, datenow = " + datenow + ", date ="
                        + date);
                if (datenow - date < oneweek) {
                    cursor.close();
                    return false;
                }
            }
        }
        cursor.close();
        Matcher matcher = Pattern.compile(AdKeyword).matcher(messagebody);
        if (matcher.find()) {
            Log.i(TAG, "HasAdKeyword, matcher " + matcher.group(0));
            return true;
        }

        return false;
    }

    private static String ReplaceChnNum(String src) {
        Log.i(TAG, "ReplaceChnNum, src = " + src);

        String replace_tab[][] = { { "\u62fe", "\u5341" },
                { "\u7396", "\u4e5d" }, { "\u634c", "\u516b" },
                { "\u67d2", "\u4e03" }, { "\u9646", "\u516d" },
                { "\u4f0d", "\u4e94" }, { "\u8086", "\u56db" },
                { "\u53c1", "\u4e09" }, { "[\u8d30\u4e24]", "\u4e8c" },
                { "\u58f9", "\u4e00" }, /* uppercase chinese 1-10 to lowercase */

                { "\u4e94\u5341\u4e5d", "59" }, { "\u4e94\u5341\u516b", "58" },
                { "\u4e94\u5341\u4e03", "57" }, { "\u4e94\u5341\u516d", "56" },
                { "\u4e94\u5341\u4e94", "55" }, { "\u4e94\u5341\u56db", "54" },
                { "\u4e94\u5341\u4e09", "53" }, { "\u4e94\u5341\u4e8c", "52" },
                { "\u4e94\u5341\u4e00", "51" }, { "\u4e94\u5341", "50" },

                { "\u56db\u5341\u4e5d", "49" }, { "\u56db\u5341\u516b", "48" },
                { "\u56db\u5341\u4e03", "47" }, { "\u56db\u5341\u516d", "46" },
                { "\u56db\u5341\u4e94", "45" }, { "\u56db\u5341\u56db", "44" },
                { "\u56db\u5341\u4e09", "43" }, { "\u56db\u5341\u4e8c", "42" },
                { "\u56db\u5341\u4e00", "41" }, { "\u56db\u5341", "40" },

                { "\u4e09\u5341\u4e5d", "39" }, { "\u4e09\u5341\u516b", "38" },
                { "\u4e09\u5341\u4e03", "37" }, { "\u4e09\u5341\u516d", "36" },
                { "\u4e09\u5341\u4e94", "35" }, { "\u4e09\u5341\u56db", "34" },
                { "\u4e09\u5341\u4e09", "33" }, { "\u4e09\u5341\u4e8c", "32" },
                { "\u4e09\u5341\u4e00", "31" }, { "\u4e09\u5341", "30" },

                { "\u4e8c\u5341\u4e5d", "29" }, { "\u4e8c\u5341\u516b", "28" },
                { "\u4e8c\u5341\u4e03", "27" }, { "\u4e8c\u5341\u516d", "26" },
                { "\u4e8c\u5341\u4e94", "25" }, { "\u4e8c\u5341\u56db", "24" },
                { "\u4e8c\u5341\u4e09", "23" }, { "\u4e8c\u5341\u4e8c", "22" },
                { "\u4e8c\u5341\u4e00", "21" }, { "\u4e8c\u5341", "20" },

                { "\u5341\u4e5d", "19" }, { "\u5341\u516b", "18" },
                { "\u5341\u4e03", "17" }, { "\u5341\u516d", "16" },
                { "\u5341\u4e94", "15" }, { "\u5341\u56db", "14" },
                { "\u5341\u4e09", "13" }, { "\u5341\u4e8c", "12" },
                { "\u5341\u4e00", "11" }, { "\u5341", "10" },

                { "\u4e5d", "9" }, { "\u516b", "8" }, { "\u4e03", "7" },
                { "\u516d", "6" }, { "\u4e94", "5" }, { "\u56db", "4" },
                { "\u4e09", "3" }, { "\u4e8c", "2" }, { "\u4e00", "1" },
                { "[\u96f6\u3007]", "0" } }; // chinese 59 - 1 , to number

        src = src.replaceAll("\u5345", "\u4e09\u5341"); // sa to san shi (30)
        src = src.replaceAll("\u5eff", "\u4e8c\u5341"); // nian to er shi (20)
        Log.i(TAG, "ReplaceChnNum, src replaced 1 = " + src);

        for (int i = 0; i < replace_tab.length; i++) {
            src = src.replaceAll(replace_tab[i][0], replace_tab[i][1]);
        }

        Log.i(TAG, "ReplaceChnNum, src replaced = " + src);

        return src;
    }

    private static String ReplaceHyphenChar(String src) {
        Log.i(TAG, "ReplaceHyphenChar, src = " + src);

        src = src.replaceAll("[\u70b9\u65f6]\u534a", "\u70b930"); // X dian/shi
                                                                  // ban to
                                                                  // dian30
        src = src.replaceAll("[\u70b9\u65f6]\u4e00\u523b", "\u70b915"); // X
                                                                        // dian/shi
                                                                        // yi ke
                                                                        // to
                                                                        // dian15
        src = src.replaceAll("[\u70b9\u65f6]\u4e09\u523b", "\u70b945"); // X
                                                                        // dian/shi
                                                                        // san
                                                                        // ke to
                                                                        // dian45
        src = src.replaceAll("\uff1a", ":"); // wide ":" to normal
        src = src.replaceAll("[,\u3000]", " "); // wide space or "," to nomal
                                                // space
        src = src.replaceAll("[\uff0f\uff3c\uff0e\uff0d\u3002/\\\\]", "-"); // wide
                                                                            // "/"
                                                                            // "\" "." "-", normal "/" "\", to "-"

        src = src.replaceAll("[\u53f7\u5929]", "\u65e5"); // hao/tian to ri
        src = src.replaceAll("\u661f\u671f|\u793c\u62dc", "\u5468"); // xing qi
                                                                     // / li bai
                                                                     // to zhou
        src = src.replaceAll("\u4e2a", ""); // ge to ""

        src = src.replaceAll("(st|nd|rd|th)", "");

        Log.i(TAG, "ReplaceHyphenChar, src replaced = " + src);

        return src;
    }
}
