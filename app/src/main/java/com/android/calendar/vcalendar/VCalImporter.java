package com.android.calendar.vcalendar;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.util.Log;

public class VCalImporter {

    private Context mCon = null;
    private int mnWkStartDay = 0;

    VCalImporter(Context con) {
        mCon = con;
        // GetFirstWeekDay();
    }

    /*
     * private void GetFirstWeekDay () { SharedPreferences prefs =
     * PreferenceManager.getDefaultSharedPreferences(mCon); String strWkStarSet
     * = prefs.getString(CalendarPreferenceActivity.KEY_FIRSTDAY,"");
     * 
     * if (null != strWkStarSet) {
     * if(strWkStarSet.equals(CalendarPreferenceActivity.FIRSTDAY_SUN)){
     * mnWkStartDay = Calendar.SUNDAY; }else
     * if(strWkStarSet.equals(CalendarPreferenceActivity.FIRSTDAY_MON)){
     * mnWkStartDay = Calendar.MONDAY; }else { mnWkStartDay = Calendar.SUNDAY; }
     * }else { mnWkStartDay = Calendar.SUNDAY; } }
     */
    public boolean SaveImportCalendar(String strFilePath, long calendarId) {
        XCalendarProcessor Importer = new XCalendarProcessor(mCon,
                XCalendarProcessor.XCALENDAR_V10);
        ContentValues EventValue = new ContentValues();
        //
        ArrayList<ContentValues> arrReminder = new ArrayList();
        VCalUtil.Log("the import file is " + strFilePath);
        if (!Importer.ImportXCalFromFile(strFilePath, EventValue, arrReminder,
                null, 0)) {
            Log.i("lwt", "SaveImportCalendar fasle.1");
            return false;
        }
        // add account
        EventValue.put(Events.CALENDAR_ID, calendarId);
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int nidxEvent = ops.size();
        Builder b = ContentProviderOperation.newInsert(Events.CONTENT_URI)
                .withValues(EventValue);
        ops.add(b.build());

        for (ContentValues value : arrReminder) {
            Builder b1 = ContentProviderOperation.newInsert(
                    Reminders.CONTENT_URI).withValues(value);
            b1.withValueBackReference(Reminders.EVENT_ID, nidxEvent);
            ops.add(b1.build());
        }

        try {
            // TODO Move this to background thread
            ContentProviderResult[] results = mCon.getContentResolver()
                    .applyBatch(android.provider.CalendarContract.AUTHORITY,
                            ops);
            for (int i = 0; i < results.length; i++) {
                VCalUtil.Log("import vcal results = " + results[i].toString());
            }

        } catch (RemoteException e) {
            Log.i("lwt", "SaveImportCalendar fasle.2");
            VCalUtil.Log(e,
                    "import vcal insert record Ignoring unexpected remote exception");
            return false;
        } catch (OperationApplicationException e) {
            Log.i("lwt", "SaveImportCalendar fasle.3");
            VCalUtil.Log(e,
                    "import vcal insert record Ignoring unexpected exception");
            return false;
        }
        return true;
    }
}