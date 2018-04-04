
package com.android.calendar.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.android.calendar.CalendarApplication;
import com.android.calendar.R;
import com.android.calendar.event.Constant;
import com.android.calendar.event.EventBean;
import com.android.calendar.event.MeetingPerson;
import com.android.calendar.event.ReminderBean;
import com.android.calendar.event.RepeatBean;
import com.apkfuns.logutils.LogUtils;
import com.hct.calendar.utils.TimeUtils;

import android.R.integer;
import android.annotation.NonNull;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

/**
 * <pre>
 *                                                                       
 *          .,:,,,                                        .::,,,::.          
 *        .::::,,;;,                                  .,;;:,,....:i:         
 *        :i,.::::,;i:.      ....,,:::::::::,....   .;i:,.  ......;i.        
 *        :;..:::;::::i;,,:::;:,,,,,,,,,,..,.,,:::iri:. .,:irsr:,.;i.        
 *        ;;..,::::;;;;ri,,,.                    ..,,:;s1s1ssrr;,.;r,        
 *        :;. ,::;ii;:,     . ...................     .;iirri;;;,,;i,        
 *        ,i. .;ri:.   ... ............................  .,,:;:,,,;i:        
 *        :s,.;r:... ....................................... .::;::s;        
 *        ,1r::. .............,,,.,,:,,........................,;iir;        
 *        ,s;...........     ..::.,;:,,.          ...............,;1s        
 *       :i,..,.              .,:,,::,.          .......... .......;1,       
 *      ir,....:rrssr;:,       ,,.,::.     .r5S9989398G95hr;. ....,.:s,      
 *     ;r,..,s9855513XHAG3i   .,,,,,,,.  ,S931,.,,.;s;s&BHHA8s.,..,..:r:     
 *    :r;..rGGh,  :SAG;;G@BS:.,,,,,,,,,.r83:      hHH1sXMBHHHM3..,,,,.ir.    
 *   ,si,.1GS,   sBMAAX&MBMB5,,,,,,:,,.:&8       3@HXHBMBHBBH#X,.,,,,,,rr    
 *   ;1:,,SH:   .A@&&B#&8H#BS,,,,,,,,,.,5XS,     3@MHABM&59M#As..,,,,:,is,   
 *  .rr,,,;9&1   hBHHBB&8AMGr,,,,,,,,,,,:h&&9s;   r9&BMHBHMB9:  . .,,,,;ri.  
 *  :1:....:5&XSi;r8BMBHHA9r:,......,,,,:ii19GG88899XHHH&GSr.      ...,:rs.  
 *  ;s.     .:sS8G8GG889hi.        ....,,:;:,.:irssrriii:,.        ...,,i1,  
 *  ;1,         ..,....,,isssi;,        .,,.                      ....,.i1,  
 *  ;h:               i9HHBMBBHAX9:         .                     ...,,,rs,  
 *  ,1i..            :A#MBBBBMHB##s                             ....,,,;si.  
 *  .r1,..        ,..;3BMBBBHBB#Bh.     ..                    ....,,,,,i1;   
 *   :h;..       .,..;,1XBMMMMBXs,.,, .. :: ,.               ....,,,,,,ss.   
 *    ih: ..    .;;;, ;;:s58A3i,..    ,. ,.:,,.             ...,,,,,:,s1,    
 *    .s1,....   .,;sh,  ,iSAXs;.    ,.  ,,.i85            ...,,,,,,:i1;     
 *     .rh: ...     rXG9XBBM#M#MHAX3hss13&&HHXr         .....,,,,,,,ih;      
 *      .s5: .....    i598X&&A&AAAAAA&XG851r:       ........,,,,:,,sh;       
 *      . ihr, ...  .         ..                    ........,,,,,;11:.       
 *         ,s1i. ...  ..,,,..,,,.,,.,,.,..       ........,,.,,.;s5i.         
 *          .:s1r,......................       ..............;shs,           
 *          . .:shr:.  ....                 ..............,ishs.             
 *              .,issr;,... ...........................,is1s;.               
 *                 .,is1si;:,....................,:;ir1sr;,                  
 *                    ..:isssssrrii;::::::;;iirsssssr;:..                    
 *                         .,::iiirsssssssssrri;;:.
 * </pre>
 */

public class EventUtils {

    public static final long ONE_HOUR = 1 * 60 * 60 * 1000;
    public static final int ALL_DAY = 1;
    public static final String INDEX = "index";
    public final static int[] remindMinuteArrays = { -1, 0, 5, 10, 15, 30, 60, 60 * 24, 60 * 24 * 7, -1, 0, 60 * 24,
            2 * 60 * 24 };
    // public final static int[] allDayMinuteArrays = { -1, 0, 60 * 24, 2 * 60 *
    // 24 };

    /**
     * @param dateTime
     * @return return a string like 2017-1-11 22:11:35
     */
    public static String getFullDateTimeStr(long dateTime) {
        Date date = new Date(dateTime);
        SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String format = sFormat.format(date);
        return format;
    }

    /**
     * @param dateTime
     * @return return a string like 2017-1-11
     */
    public static String getDateStr(long dateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String yearStr = CalendarApplication.getContext().getString(R.string.event_year);
        String monthStr1 = CalendarApplication.getContext().getString(R.string.event_month);
        String dayStr1 = CalendarApplication.getContext().getString(R.string.event_day);
        String date = year + yearStr + (month + 1) + monthStr1 + day + dayStr1;
        return date;
    }

    /**
     * 
     * @param dateTime
     * @return return a string like: 16:33
     */
    public static String getTimeStr(long dateTime) {
        Date date = new Date(dateTime);
        // HH:mm 24hour hh:mm 12h
        SimpleDateFormat sFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String format = sFormat.format(date);
        return format;
    }

    public static void addScheduleEvent(@NonNull final Context context, final EventBean bean) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = bean.start;
                long end = bean.end;
                String title = bean.title;
                String desc = bean.desc;
                int allDay = bean.allDay;
                String location = bean.location;
                int reminder = bean.reminder;
                int color = bean.color;
                LogUtils.e("b: " + start + " e: " + end);
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.EVENT_COLOR, "" + color);
                event.put(Events.ORGANIZER, bean.attend);
                event.put(Events.DESCRIPTION, desc);
                event.put("calendar_id", 1); // 1 --> my calendar
                event.put(Events.ALL_DAY, allDay); // all day
                int hasAlarm = remindMinuteArrays[reminder] < 0 ? 0 : 1;
                event.put(Events.HAS_ALARM, hasAlarm); // alarm
                Log.e("liaozhenbin", "HAS_ALARM: " + hasAlarm);
                event.put(Events.ORIGINAL_INSTANCE_TIME, bean.reminder);
                event.put(Events.EVENT_COLOR, Constant.SCHEDULE); // color
                if (bean.rule != null) {
                    event.put(Events.DURATION, getDurationStr(start, end));
                }
                event.put(Events.RRULE, bean.rule);
                event.put(Events.EVENT_LOCATION, location); // location
                event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                if (ALL_DAY == allDay) {
                    long presentStart = start;
                    long presentEnd = end;
                    long allDayStart = TimeUtils.getAllDayTime(presentStart, true);
                    long addDayEnd = TimeUtils.getAllDayTime(presentEnd, false);
                    event.put(Events.DTSTART, allDayStart); // begin time
                    event.put(Events.DTEND, bean.rule != null ? null : addDayEnd); // end
                                                                                   // time
                } else {
                    event.put(Events.DTSTART, start); // begin time
                    event.put(Events.DTEND, bean.rule != null ? null : end); // end time
                }

                Uri newEvent = context.getContentResolver().insert(Events.CONTENT_URI, event);
                if(hasAlarm == 1){
                    long eventId = Long.parseLong(newEvent.getLastPathSegment());
                    ContentValues values = new ContentValues();
                    values.put(Reminders.EVENT_ID, eventId);

                    if (ALL_DAY == allDay) {
                        values.put(Reminders.MINUTES, -480 + remindMinuteArrays[reminder]);
                    } else {
                        values.put(Reminders.MINUTES, remindMinuteArrays[reminder]);
                    }

                    values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    ContentResolver cr1 = context.getContentResolver(); //
                    cr1.insert(Reminders.CONTENT_URI, values); //
                }
               
            }
        }).start();
        
    }

    public static void updateScheduleEvent(@NonNull final Context context, final EventBean bean, final int eventId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String title = bean.title;
                long end = bean.end;
                long start = bean.start;
                int reminder = bean.reminder;
                String location = bean.location;
                String desc = bean.desc;
                int allDay = bean.allDay;
                // int selectItem = bean.selectItem;
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.DTSTART, start);
                event.put(Events.EVENT_LOCATION, location);
                event.put(Events.ORIGINAL_INSTANCE_TIME, bean.reminder);
                event.put(Events.DESCRIPTION, desc);
                event.put(Events.ALL_DAY, allDay);
                int hasAlarm = remindMinuteArrays[reminder] < 0 ? 0 : 1;
                // event.put(Events.IS_ORGANIZER, selectItem);
                // if (bean.rule != null) {
                // event.put(Events.DURATION, getDurationStr(start, end));
                // }
                // event.put(Events.DTEND, bean.rule != null ? null : end);
                event.put(Events.DURATION, bean.rule != null ? getDurationStr(start, end) : null);
                event.put(Events.DTEND, bean.rule != null ? null : end);
                event.put(Events.RRULE, bean.rule);
                // context.getContentResolver().delete(Instances.CONTENT_URI.buildUpon().build(),
                // "" + Instances.EVENT_ID + "=?", new String[] { "" + eventId
                // });
                long a = System.currentTimeMillis();
                context.getContentResolver().update(Events.CONTENT_URI, event, "" + Events._ID + "=?",
                        new String[] { "" + eventId });
                Log.d("2877", "updateScheduleEvent:" + (System.currentTimeMillis() - a));

//                ContentValues values = new ContentValues();
//                values.put(Reminders.EVENT_ID, eventId);
//
//                if (ALL_DAY == allDay) {
//                    values.put(Reminders.MINUTES, -480 + remindMinuteArrays[reminder]);
//                } else {
//                    values.put(Reminders.MINUTES, remindMinuteArrays[reminder]);
//                }
//
//                values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
//                //
//                context.getContentResolver().update(Reminders.CONTENT_URI, values, "" + Reminders.EVENT_ID + "=?",
//                        new String[] { "" + eventId });
                context.getContentResolver().delete(Reminders.CONTENT_URI, Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
                if(hasAlarm == 1){
                    ContentValues values = new ContentValues();
                    values.put(Reminders.EVENT_ID, eventId);

                    if (ALL_DAY == allDay) {
                        values.put(Reminders.MINUTES, -480 + remindMinuteArrays[reminder]);
                    } else {
                        values.put(Reminders.MINUTES, remindMinuteArrays[reminder]);
                    }

                    values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    ContentResolver cr1 = context.getContentResolver(); //
                    cr1.insert(Reminders.CONTENT_URI, values); //
                }
            }
        }).start();

    }

    public static void updateScheduleEventHasAlarm(@NonNull Context context, long eventId, boolean hasAlarm) {

        ContentValues event = new ContentValues();
        event.put(Reminders.MINUTES, 0);
        int count = context.getContentResolver().update(Reminders.CONTENT_URI, event, "" + Reminders.EVENT_ID + "=?",
                new String[] { "" + eventId });
    }

    public static void addMeetingEvent(@NonNull final Context context, final EventBean bean, final boolean isSendSMS) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = bean.start;
                long end = bean.end;
                String title = bean.title;
                String desc = bean.desc;
                int allDay = bean.allDay;
                String location = bean.location;
                int reminder = bean.reminder;
                int color = bean.color;
                // int selectItem = bean.selectItem;
                LogUtils.e("b: " + start + " e: " + end);
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.EVENT_COLOR, "" + color);
                event.put(Events.DESCRIPTION, desc);
                event.put("calendar_id", 1); // 1 --> my calendar
                event.put(Events.DTSTART, start); //
                event.put(Events.DTEND, end); //
                event.put(Events.ALL_DAY, allDay); //
                int hasAlarm = remindMinuteArrays[reminder] < 0 ? 0 : 1;
                event.put(Events.HAS_ALARM, hasAlarm); //
                event.put(Events.ORIGINAL_INSTANCE_TIME, bean.reminder);
                // event.put(Events.EVENT_COLOR, ); //
                event.put(Events.EVENT_LOCATION, location); //
                event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                // event.put(Events.IS_ORGANIZER, selectItem);
                event.put(Events.HAS_ATTENDEE_DATA, isSendSMS ? 1 : 0);
                Uri newEvent = context.getContentResolver().insert(Events.CONTENT_URI, event);
                long eventId = Long.parseLong(newEvent.getLastPathSegment());
                ContentValues values = new ContentValues();
                ContentResolver cr = context.getContentResolver();
                for (MeetingPerson person : bean.meetingPersonList) {
                    values.put(Attendees.EVENT_ID, eventId);
                    values.put(Attendees.ATTENDEE_NAME, person.getPersonName());
                    values.put(Attendees.ATTENDEE_EMAIL, person.getPersonPhone() + "");
                    cr.insert(Attendees.CONTENT_URI, values);
                    values.clear();
                }
                if(hasAlarm == 1){
                    values.put(Reminders.EVENT_ID, eventId);
                    values.put(Reminders.MINUTES, remindMinuteArrays[reminder]);
                    values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    cr.insert(Reminders.CONTENT_URI, values); //
                }
            }
        }).start();

    }

    public static void updateMeetingEvent(@NonNull final Context context, final EventBean bean, final int eventId,
            final boolean isSendSMS) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                String title = bean.title;
                long end = bean.end;
                long start = bean.start;
                int reminder = bean.reminder;
                String location = bean.location;
                String desc = bean.desc;
                // int selectItem = bean.selectItem;
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.DTSTART, start);
                event.put(Events.DTEND, end);
                event.put(Events.EVENT_LOCATION, location);
                event.put(Events.ORIGINAL_INSTANCE_TIME, bean.reminder);
                event.put(Events.DESCRIPTION, desc);
                // event.put(Events.IS_ORGANIZER, selectItem);
                event.put(Events.HAS_ATTENDEE_DATA, isSendSMS ? 1 : 0);
                context.getContentResolver().update(Events.CONTENT_URI, event, "" + Events._ID + "=?",
                        new String[] { "" + eventId });
                int hasAlarm = remindMinuteArrays[reminder] < 0 ? 0 : 1;
                ContentValues values = new ContentValues();
                ContentResolver cr = context.getContentResolver();
                cr.delete(Reminders.CONTENT_URI, Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
                if(hasAlarm == 1){
                    values.put(Reminders.EVENT_ID, eventId);
                    values.put(Reminders.MINUTES, remindMinuteArrays[reminder]);
                    values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    cr.insert(Reminders.CONTENT_URI, values); //
                    values.clear();
                }
               

                cr.delete(Attendees.CONTENT_URI, "" + Attendees.EVENT_ID + "=?", new String[] { "" + eventId });
                for (MeetingPerson person : bean.meetingPersonList) {
                    values.put(Attendees.EVENT_ID, eventId);
                    values.put(Attendees.ATTENDEE_NAME, person.getPersonName());
                    values.put(Attendees.ATTENDEE_EMAIL, person.getPersonPhone() + "");
                    cr.insert(Attendees.CONTENT_URI, values);
                    values.clear();
                }
//            }
//        }).start();

    }

    public static void addBirthdayEvent(final Context context, final EventBean bean, final boolean isAdvance) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long end = bean.end;
                long start = bean.start;
                String title = bean.title;
                int color = bean.color;
                String rule = bean.rule;
                LogUtils.e("b: " + start + " e: " + end);
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.EVENT_COLOR, "" + color);
                event.put("calendar_id", 1); //
                event.put(Events.DTSTART, start); //
                event.put(Events.HAS_ALARM, 1);
                event.put(Events.EVENT_COLOR, "" + color);
                event.put(Events.AVAILABILITY, isAdvance ? 1 : 0);
                event.put(Events.RRULE, rule);
                event.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
                event.put("original_sync_id", bean.sync);
                if (bean.rule != null) {
                    event.put(Events.DURATION, getDurationStr(start, end));
                }
                event.put(Events.DTEND, bean.rule != null ? null : end);
                Uri newEvent = context.getContentResolver().insert(Events.CONTENT_URI, event);
                long eventId = Long.parseLong(newEvent.getLastPathSegment());
                ContentValues values2 = new ContentValues();
                values2.put(Reminders.EVENT_ID, eventId);
                values2.put(Reminders.MINUTES, 0);
                values2.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                //
                ContentResolver cr12 = context.getContentResolver(); //
                cr12.insert(Reminders.CONTENT_URI, values2); //
                if (isAdvance) {
                    Log.d("2877", "add isAdvance!!!");
                    values2.clear();
                    values2.put(Reminders.EVENT_ID, eventId);
                    values2.put(Reminders.MINUTES, 60 * 24 * 3);
                    values2.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    cr12.insert(Reminders.CONTENT_URI, values2);
                }
            }
        }).start();

    }

    public static void updateBirthdayEvent(final Context context, final EventBean bean, final int eventId,
            final boolean isAdvance) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long end = bean.end;
                long start = bean.start;
                String title = bean.title;
                String rule = bean.rule;
                ContentValues event = new ContentValues();
                event.put(Events.TITLE, title);
                event.put(Events.DTSTART, start);
                event.put(Events.AVAILABILITY, isAdvance ? 1 : 0);
                event.put(Events.RRULE, rule);
                Log.d("2877", "rule: " + rule);
                event.put(Events.DURATION, bean.rule != null ? getDurationStr(start, end) : null);
                event.put(Events.DTEND, bean.rule != null ? null : end);
                context.getContentResolver().update(Events.CONTENT_URI, event, "" + Events._ID + "=?",
                        new String[] { "" + eventId });
                ContentResolver cr12 = context.getContentResolver();
                ContentValues values2 = new ContentValues();
                values2.put(Reminders.EVENT_ID, eventId);
                values2.put(Reminders.MINUTES, 0);
                values2.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                cr12.delete(Reminders.CONTENT_URI, "" + Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
                cr12.insert(Reminders.CONTENT_URI, values2);
                if (isAdvance) {
                    values2.clear();
                    values2.put(Reminders.EVENT_ID, eventId);
                    values2.put(Reminders.MINUTES, 60 * 24 * 3);
                    values2.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    cr12.insert(Reminders.CONTENT_URI, values2);
                }

            }
        }).start();

    }

    public static List<ReminderBean> getReminderBeans() {
        ArrayList<ReminderBean> reminderBeans = new ArrayList<ReminderBean>();
        reminderBeans.add(new ReminderBean(-1, CalendarApplication.getContext().getString(R.string.dismiss_label)));
        reminderBeans.add(new ReminderBean(0, CalendarApplication.getContext().getString(R.string.event_on_time)));
        reminderBeans.add(
                new ReminderBean(5, CalendarApplication.getContext().getString(R.string.five_minute_alert_inadvance)));
        reminderBeans.add(
                new ReminderBean(10, CalendarApplication.getContext().getString(R.string.ten_minute_alert_inadvance)));
        reminderBeans.add(new ReminderBean(15,
                CalendarApplication.getContext().getString(R.string.fifteen_minute_alert_inadvance)));
        reminderBeans.add(new ReminderBean(30,
                CalendarApplication.getContext().getString(R.string.thirty_minute_alert_inadvance)));
        reminderBeans.add(
                new ReminderBean(60, CalendarApplication.getContext().getString(R.string.one_hour_alert_inadvance)));
        return reminderBeans;
    }

    public static String[] getReminderBeans(@NonNull List<ReminderBean> beans) {
        ArrayList<String> reminderStrings = new ArrayList<String>();
        for (int i = 0; i < beans.size(); i++) {
            // if (beans.get(i).minute < 0)
            // continue;
            reminderStrings.add(beans.get(i).text);
        }
        return reminderStrings.toArray(new String[reminderStrings.size()]);
    }

    public static List<RepeatBean> getRepeatBeans() {
        ArrayList<RepeatBean> list = new ArrayList<RepeatBean>();
        list.add(new RepeatBean(RepeatBean.ONCE, CalendarApplication.getContext().getString(R.string.event_one_time)));
        list.add(new RepeatBean(RepeatBean.EVERY_DAY,
                CalendarApplication.getContext().getString(R.string.event_one_day)));
        list.add(new RepeatBean(RepeatBean.EVERY_HOUR,
                CalendarApplication.getContext().getString(R.string.event_one_hour)));
        list.add(new RepeatBean(RepeatBean.EVERY_WEEK,
                CalendarApplication.getContext().getString(R.string.event_one_week)));
        list.add(new RepeatBean(RepeatBean.EVERY_MONTH,
                CalendarApplication.getContext().getString(R.string.event_one_month)));
        list.add(new RepeatBean(RepeatBean.EVERY_YEAR,
                CalendarApplication.getContext().getString(R.string.event_one_year)));
        return list;
    }

    public static String[] getRepeatBeans(List<RepeatBean> beans) {
        ArrayList<String> reminderStrings = new ArrayList<String>();
        for (int i = 0; i < beans.size(); i++) {
            reminderStrings.add(beans.get(i).text);
        }
        return reminderStrings.toArray(new String[reminderStrings.size()]);
    }

    public static String getDurationStr(long startTime, long endTime) {
        long a = startTime;
        long b = endTime;
        int c = (int) ((b - a) / 1000);
        return "P" + c + "S";
    }

    /**
     * 
     * update remind for SCHEDULE or MEETING Created by liao on Nov 25, 2017
     */
    public static void updateRemind(Context context, int eventId, int eventColor, int remindType) {
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        switch (eventColor) {
        case Constant.SCHEDULE:
            Log.d("liaozhebin", "updateRemind SCHEDULE " + remindType);
            values.put(Events.ORIGINAL_INSTANCE_TIME, remindType);
            cr.update(Events.CONTENT_URI, values, "" + Events._ID + "=?", new String[] { "" + eventId });
            values.clear();
            values.put(Reminders.EVENT_ID, eventId);

            if (remindType > 8) {
                values.put(Reminders.MINUTES, -480 + remindMinuteArrays[remindType]);
            } else {
                values.put(Reminders.MINUTES, remindMinuteArrays[remindType]);
            }
            values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
            cr.update(Reminders.CONTENT_URI, values, "" + Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
            break;
        case Constant.MEETING:
            values.put(Events.ORIGINAL_INSTANCE_TIME, remindType);
            cr.update(Events.CONTENT_URI, values, "" + Events._ID + "=?", new String[] { "" + eventId });
            values.clear();
            values.put(Reminders.EVENT_ID, eventId);
            values.put(Reminders.MINUTES, remindMinuteArrays[remindType]);
            cr.update(Reminders.CONTENT_URI, values, "" + Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
            break;
        default:
            break;
        }

    }

    /**
     * 
     * update remind for BIRTHDAY Created by liao on Nov 25, 2017
     */
    public static void updateRemind(final Context context, final int eventId, final long startTime,
            final boolean isAdvance) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver cr = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(Events.DTSTART, startTime);
                values.put(Events.AVAILABILITY, isAdvance ? 1 : 0);
                cr.update(Events.CONTENT_URI, values, "" + Events._ID + "=?", new String[] { "" + eventId });
                //
                values.clear();
                values.put(Reminders.EVENT_ID, eventId);
                values.put(Reminders.MINUTES, 0);
                values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                cr.delete(Reminders.CONTENT_URI, "" + Reminders.EVENT_ID + "=?", new String[] { "" + eventId });
                cr.insert(Reminders.CONTENT_URI, values); //
                if (isAdvance) {
                    values.clear();
                    values.put(Reminders.EVENT_ID, eventId);
                    values.put(Reminders.MINUTES, 60 * 24 * 3);
                    values.put(Reminders.METHOD, Reminders.METHOD_ALERT);
                    cr.insert(Reminders.CONTENT_URI, values);
                }
            }

        }).start();

    }

}
