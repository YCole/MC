/*=========================================================
when                   who             what,where,why                                                                    comment tag
2011-3-1            duanqingpeng    if the vcal protacal do not containe tz info,the time is diff with ori calendar HCT_DQP_CQNJ00251298
===========================================================*/
package com.android.calendar.vcalendar;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.ContentValues;
import android.text.format.Time;
import android.util.Log;

import com.android.calendar.vcalendar.ICalendar.Component;
import com.android.calendar.vcalendar.ICalendar.Parameter;
import com.android.calendar.vcalendar.ICalendar.Property;

public class XCalendarProcessor_V10 implements IXCalendarProcessor {
    /** VCAL NAME **/
    public static final String VEVENT_STRING = "VEVENT";
    public static final String TIMEZONE_STRING = "TZ";
    public static final String EVENT_TITLE_STRING = "SUMMARY";
    public static final String ENCODING_STRING = "ENCODING";
    public static final String CHARSET_STRING = "CHARSET";
    public static final String VCALENDAR_STRING = "VCALENDAR";
    public static final String DTSTARTTIME_STRING = "DTSTART";
    public static final String DTENDTIME_STRING = "DTEND";
    public static final String ALLDAY_STRING = "X-BORQS-ALL-DAY";
    public static final String DESCRIPTION_STRING = "DESCRIPTION";
    public static final String LOCATION_STRING = "LOCATION";
    public static final String TRANS_STRING = "TRANSP";
    public static final String AALARM_STRING = "AALARM";
    public static final String VERSION_STRING = "VERSION";
    public static final String PRINTABLE_STRING = "QUOTED-PRINTABLE";
    public static final String CATEGORY_STRING = "CATEGORIES";
    public static final String CLASS_STRING = "CLASS";
    public static final String REC_RULE = "RRULE";
    /** VCAL NAME **/
    /** delete begin HCT_DQP_CQNJ00251298 **/
    // public static final String TIMEZONE_DEFAULT_STRING = "Asia/Shangh";
    /** delete end HCT_DQP_CQNJ00251298 **/
    public static final String VERSION_VALUE_STRING = "1.0";
    public static final String CATEGORY_DEFAULT_STRING = "MISCELLANEOUS";
    public static final String DURATION_DEFAULT = "P3600S";
    /** modified begin HCT_DQP_CQNJ00251298 **/
    private static String mstrTimeZone = TimeZone.getDefault().getID();
    /** modified end HCT_DQP_CQNJ00251298 **/
    // private static final String FILE_EXT = ".vcs";
    private static final String FILE_EXT = ".vcs";

    public boolean GetCalendarObject(Component component,
            ContentValues EventsInfo, ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart) {
        // TODO Auto-generated method stub
        GetCalendarObject GetObject = new GetCalendarObject();
        return GetObject.GetCalendarObject(component, EventsInfo,
                arrReminderInfo, AttendeeInfo, nWeekStart);
    }

    public boolean GetCalendarString(StringBuilder builFileName,
            StringBuilder builXCalendarInfo, ContentValues EventsInfo,
            ArrayList<ContentValues> arrReminderInfo,
            ContentValues AttendeeInfo, int nWeekStart) {
        GetCalendarString GetCalString = new GetCalendarString();
        return GetCalString.GetCalendarString(builFileName, builXCalendarInfo,
                EventsInfo, arrReminderInfo, AttendeeInfo, nWeekStart);

    }

    public class GetCalendarObject {
        private int mnWeekStart = 0;
        private Component mComponent = null;
        private ContentValues mEventsInfo = null;
        private ArrayList<ContentValues> marrReminder;
        private Time mStartTime = null;

        public boolean GetCalendarObject(Component component,
                ContentValues EventsInfo,
                ArrayList<ContentValues> arrReminderInfo,
                ContentValues AttendeeInfo, int nWeekStart) {
            // TODO Auto-generated method stub
            // get events object
            try {
                mComponent = component;
                mEventsInfo = EventsInfo;
                marrReminder = arrReminderInfo;
                mnWeekStart = nWeekStart;
                GetEventInfos();
                GetReminderInfos();
            } catch (Exception e) {
                VCalUtil.Log(e, "GetCalendarObject.GetCalendarObject exception");
                return false;
            }

            return true;
        }

        private void GetEventInfos() {
            AddPropertyValue(VEVENT_STRING, EVENT_TITLE_STRING,
                    EVENT_TITLE_COL_STRING);
            AddTimeZoneString();
            AddTime(VEVENT_STRING, DTSTARTTIME_STRING, START_COL_STRING);
            AddTime(VEVENT_STRING, DTENDTIME_STRING, END_COL_STRING);
            AddAllDay();
            AddPropertyValue(VEVENT_STRING, DESCRIPTION_STRING,
                    DESCRIPTION_COL_STRING);
            AddPropertyValue(VEVENT_STRING, LOCATION_STRING,
                    LOCATION_COL_STRING);
            // alarm
            AddHasAlarm();
            AddRecRule();
            // not support trans now
            mEventsInfo.put(TRANS_COL_STRING, 0);
            mEventsInfo.put(VISIBILITY_COL_STRING, 0);
            mEventsInfo.put(HAS_ATTENDEE_COL_STRING, 0);
        }

        private void AddRecRule() {
            Property prop = mComponent.getFirstPropertyInComponent(
                    VEVENT_STRING, REC_RULE);
            if (null == prop) {
                return;
            }
            String strRuleOri = prop.getValue();
            if (strRuleOri == null || strRuleOri.equals("")) {
                return;
            }
            String strRecRule = VCalUtil.GetV20RuleFromV10(strRuleOri,
                    mStartTime, mnWeekStart);
            if (strRecRule == null || strRecRule.equals("")) {
                return;
            }

            mEventsInfo.put(RRULE_COL_STRING, strRecRule);

            // mEventsInfo.put(DURATION_COL_STRING, DURATION_DEFAULT);
            long start = mEventsInfo.getAsLong(START_COL_STRING);
            long end = mEventsInfo.getAsLong(END_COL_STRING);
            boolean isAllDay = "1".equals(mEventsInfo
                    .getAsString(ALLDAY_COL_STRING));
            String durationStr = VCalUtil.getDurationString(start, end,
                    isAllDay);
            mEventsInfo.put(DURATION_COL_STRING, durationStr);

            mEventsInfo.put(END_COL_STRING, (Long) null);
        }

        private void GetReminderInfos() {
            List<Property> props = mComponent.getPropertiesInComponent(
                    VEVENT_STRING, AALARM_STRING);
            if (null == props) {
                VCalUtil.Log("has no reminder");
                return;
            }
            for (Property prop : props) {
                String strReminder = prop.getValue();
                Time tmReminder = new Time(mstrTimeZone);
                /***
                 * HCT_MODIFY jianghejie if the AALARM with the value of
                 * "default" in the vcs file we can not parse it ,just retrun
                 */
                try {
                    tmReminder.parse(strReminder);
                } catch (Exception e) {
                    mEventsInfo.put(HAS_ALARM_COL_STRING, 0);
                    return;
                }
                /*** HCT_MODIFY end */
                tmReminder.parse(strReminder);
                long lMillReminder = tmReminder.normalize(true);

                String strStartTime = GetComponentValue(VEVENT_STRING,
                        DTSTARTTIME_STRING);
                Time tmStart = new Time(mstrTimeZone);
                tmStart.parse(strStartTime);
                long lMillStart = tmStart.normalize(true);

                long lInterval = (lMillStart - lMillReminder) / 1000 / 60;
                ContentValues ReminderValue = new ContentValues();
                ReminderValue.put(MIN_COL_STRING, lInterval);
                // /****/**/
                ReminderValue.put(METHOD_COL_STRING, 1);
                marrReminder.add(ReminderValue);
            }
        }

        private void AddHasAlarm() {
            List<Property> props = mComponent.getPropertiesInComponent(
                    VEVENT_STRING, AALARM_STRING);
            if (null != props && 1 <= props.size()) {
                mEventsInfo.put(HAS_ALARM_COL_STRING, 1);
            } else {
                mEventsInfo.put(HAS_ALARM_COL_STRING, 0);
            }
        }

        private void AddAllDay() {
            String strAllDay = GetComponentValue(VEVENT_STRING, ALLDAY_STRING);
            if (null == strAllDay || strAllDay.equals("0")) {
                mEventsInfo.put(ALLDAY_COL_STRING, 0);
            } else {
                mEventsInfo.put(ALLDAY_COL_STRING, 1);
                mEventsInfo.put(TZ_COL_STRING, Time.TIMEZONE_UTC);
            }
        }

        private void AddTime(String strComponentName, String strProperty,
                String strCol) {
            String strTime = GetComponentValue(strComponentName, strProperty);
            Time tmTime = new Time(mstrTimeZone);
            tmTime.parse(strTime);
            long lMill = tmTime.normalize(true);
            if (strProperty.equals(DTSTARTTIME_STRING)) {
                mStartTime = tmTime;
            }
            VCalUtil.Log("add time property is " + strCol + "value is " + lMill);
            mEventsInfo.put(strCol, lMill);
        }

        private void AddTimeZoneString() {
            /*
             * HCT_MODIFY start longgang do not import the timezone String
             * strTimeZoneString = GetComponentValue(VCALENDAR_STRING,
             * TIMEZONE_STRING); String strTimeZoneConvert = ""; if (null !=
             * strTimeZoneString) { HCT_MODIFY start longgang do not import the
             * timezone
             */

            /** modified begin HCT_DQP_CQNJ00251298 **/
            /*
             * HCT_MODIFY start longgang do not import the timezone
             * VCalUtil.Log("orginal timezone str is " + strTimeZoneString);
             * 
             * strTimeZoneConvert = VCalUtil.ParseTimeZone(strTimeZoneString);
             * mstrTimeZone = strTimeZoneConvert; }
             * 
             * VCalUtil.Log("vcal protocal the timezone is " +
             * strTimeZoneConvert); VCalUtil.Log("the timezone is " +
             * mstrTimeZone); HCT_MODIFY start longgang do not import the
             * timezone
             */
            Log.i("jianghejie", "mstrTimeZone=" + mstrTimeZone);
            // if (!strTimeZoneConvert.equals("")){
            mEventsInfo.put(TZ_COL_STRING, mstrTimeZone);
            // }
            /** modified end HCT_DQP_CQNJ00251298 **/
        }

        private String AddPropertyValue(String strComponentName,
                String strPropertyName, String strKey) {
            String strValue = null;
            strValue = GetComponentValue(strComponentName, strPropertyName);
            if (null == strValue) {
                return strValue;
            }
            mEventsInfo.put(strKey, strValue);
            return strValue;
        }

        private String GetComponentValue(String strComponentName,
                String strPropertyName) {
            String strValue = null;
            Property property = mComponent.getFirstPropertyInComponent(
                    strComponentName, strPropertyName);
            if (null == property) {
                VCalUtil.Log(String.format("Component %s,Property %s is null",
                        strComponentName, strPropertyName));
                return strValue;
            }
            String strEncoding = property
                    .getFirstParameterValue(ENCODING_STRING);
            String strCharset = property.getFirstParameterValue(CHARSET_STRING);

            strValue = property.getValue();
            if (!strValue.equals("")
                    && strEncoding.equals(ENCODE_PRINTABLE_STRING)
                    && strCharset != null && !strCharset.equals("")) {
                strValue = VCalUtil.DecodePrintable(strValue, strCharset);
            }
            return strValue;
        }
    }

    public class GetCalendarString {
        private Component mComponent = null;
        private Component mEventComponent = null;
        private ContentValues mEventsInfo = null;
        private ArrayList<ContentValues> marrReminder;
        private long mlStartTime = 0;
        private String mstrTZID = Time.TIMEZONE_UTC;
        private StringBuilder mbuidFileName;

        public boolean GetCalendarString(StringBuilder buidFileName,
                StringBuilder builXCalendarInfo, ContentValues EventsInfo,
                ArrayList<ContentValues> arrReminderInfo,
                ContentValues AttendeeInfo, int nWeekStart) {
            try {
                mEventsInfo = EventsInfo;
                marrReminder = arrReminderInfo;
                mbuidFileName = buidFileName;
                StructVCalObject();
                String strVCalInfo = mComponent.toString();
                builXCalendarInfo.append(strVCalInfo);

            } catch (Exception e) {
                VCalUtil.Log(e, "GetCalendarString.GetCalendarString exception");
                return false;
            }
            // TODO Auto-generated method stub
            return true;
        }

        private Component StructVCalObject() {
            Component component = new Component(VCALENDAR_STRING, null);
            mComponent = component;
            AddVersion();
            AddTimeZone();
            AddEventInfo();
            return component;
        }

        private void AddRRule() {
            String strRRule = mEventsInfo.getAsString(RRULE_COL_STRING);
            String strVCalRRule10 = VCalUtil.GetV10RuleFromV20(strRRule);
            if (0 == strVCalRRule10.length()) {
                return;
            }
            AddProperty(mEventComponent, REC_RULE, strVCalRRule10);
        }

        private void AddTimeZone() {
            String strTZID = mEventsInfo.getAsString(TZ_COL_STRING);
            mstrTZID = strTZID;
            if (null == strTZID) {
                VCalUtil.Log("outport vcal string, tz value not found");
                return;
            }
            // String strTZString = VCalUtil.GetVCalTZStringFromTZID(strTZID);
            long startTime = mEventsInfo.getAsLong(START_COL_STRING);
            String strTZString = VCalUtil.getVcsTZString(strTZID, startTime);
            AddProperty(null, TIMEZONE_STRING, strTZString);
        }

        private void AddVersion() {
            AddProperty(null, VERSION_STRING, VERSION_VALUE_STRING);
        }

        private void AddProperty(Component comp, String strPropname,
                String strValue) {
            Property prop = new Property(strPropname);
            prop.setValue(strValue);
            if (null == comp) {
                mComponent.addProperty(prop);
            } else {
                comp.addProperty(prop);
            }
        }

        private void AddEventInfo() {
            mEventComponent = new Component(VEVENT_STRING, mComponent);
            mComponent.addChild(mEventComponent);
            AddSummary();
            long mlStartTime = mEventsInfo.getAsLong(START_COL_STRING);
            AddTime(START_COL_STRING, DTSTARTTIME_STRING);
            long lEndTime = mEventsInfo.getAsLong(END_COL_STRING);
            // rec event
            if (0 == lEndTime) {
                lEndTime = mlStartTime;
            }
            Time tm = new Time(mstrTZID);
            tm.set(lEndTime);
            String strEndTimeString = tm.format2445();
            AddProperty(mEventComponent, DTENDTIME_STRING, strEndTimeString);

            // AddTime(END_COL_STRING, DTENDTIME_STRING);
            AddCategory();
            AddClass();
            AddAllDay();
            AddDescription();
            AddLocation();
            AddAlarm();
            AddRRule();
        }

        private void AddDescription() {
            AddEncodeStringProperty(mEventComponent, DESCRIPTION_COL_STRING,
                    DESCRIPTION_STRING, null);
        }

        private void AddLocation() {
            AddEncodeStringProperty(mEventComponent, LOCATION_COL_STRING,
                    LOCATION_STRING, null);
        }

        private void AddAllDay() {
            String strIsAllDay = mEventsInfo.getAsString(ALLDAY_COL_STRING);
            if (null == strIsAllDay) {
                return;
            }
            AddProperty(mEventComponent, ALLDAY_STRING, strIsAllDay);
        }

        private void AddClass() {
            AddProperty(mEventComponent, CLASS_STRING, "PUBLIC");
        }

        private void AddCategory() {
            AddEncodeStringProperty(mEventComponent, null, CATEGORY_STRING,
                    CATEGORY_DEFAULT_STRING);
        }

        private void AddAlarm() {
            long lStartTime = mEventsInfo.getAsLong(START_COL_STRING);
            if (null != marrReminder) {
                for (ContentValues Reminder : marrReminder) {
                    long lReminderMin = Reminder.getAsLong(MIN_COL_STRING);
                    long lReminder = lReminderMin * 60 * 1000;
                    long lReminderTime = lStartTime - lReminder;
                    Time tm = new Time(mstrTZID);
                    tm.set(lReminderTime);
                    String strReminder = tm.format2445();
                    AddProperty(mEventComponent, AALARM_STRING, strReminder);

                }
            }
        }

        private void AddTime(String strKey, String strPropertyName) {
            long lTime = mEventsInfo.getAsLong(strKey);
            // default timezone
            Time tm = new Time(mstrTZID);
            tm.set(lTime);
            String strTimeString = tm.format2445();
            AddProperty(mEventComponent, strPropertyName, strTimeString);
        }

        private void AddEncodeStringProperty(Component comp, String strKey,
                String strVcalField, String strDefaultValue) {
            String strValue = null;
            if (null == strDefaultValue) {
                strValue = mEventsInfo.getAsString(strKey);
            } else {
                strValue = strDefaultValue;
            }
            if (null == strValue) {
                VCalUtil.Log(String
                        .format("outport vcal string, %s value not found",
                                strVcalField));
                return;
            }
            String strEncodingValue = VCalUtil.EncodePrintable(strValue,
                    CHARSET_UTF8_STRING);
            Property prop = new Property(strVcalField);
            Parameter paramEncode = new Parameter(ENCODING_STRING,
                    PRINTABLE_STRING);
            prop.addParameter(paramEncode);
            Parameter paramCS = new Parameter(CHARSET_STRING,
                    CHARSET_UTF8_STRING);
            prop.addParameter(paramCS);
            prop.setValue(strEncodingValue);
            if (null != comp) {
                comp.addProperty(prop);
            } else {
                mComponent.addProperty(prop);
            }
        }

        private void AddSummary() {
            // struct file name
            String strTitle = mEventsInfo.getAsString(EVENT_TITLE_COL_STRING);
            if (null == strTitle || 0 == strTitle.length()) {
                strTitle = DEFAULT_TITLE;
            }
            mbuidFileName.append(strTitle);
            mbuidFileName.append(FILE_EXT);
            AddEncodeStringProperty(mEventComponent, EVENT_TITLE_COL_STRING,
                    EVENT_TITLE_STRING, null);
        }
    }
}
