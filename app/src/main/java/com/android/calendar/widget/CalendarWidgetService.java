package com.android.calendar.widget;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import android.Manifest;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Instances;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.widget.CalendarAppWidgetModel.EventInfo;
import com.android.calendar.widget.CalendarAppWidgetModel.RowInfo;

//import com.hct.calendar.weather.WeatherController;

public class CalendarWidgetService extends RemoteViewsService {
    private final String TAG = "CalendarWidgetService";
    private static final boolean DEBUG = true;
    private final static CalendarWidget calendarWidget = new CalendarWidget();
    static final int MAX_DAYS = 1;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent arg0) {
        // TODO Auto-generated method stub
        if (DEBUG)
            Log.d(TAG, "lxg CalendarWidgetFactory onGetViewFactory");
        return new CalendarWidgetFactory(getApplicationContext(), arg0);
    }

    public static class CalendarWidgetFactory extends BroadcastReceiver
            implements RemoteViewsService.RemoteViewsFactory,
            Loader.OnLoadCompleteListener<Cursor> {
        private Context mContext;
        private int mAppWidgetId;
        private CursorLoader mLoader;
        private final String TAG = "TimeWidgetService.CalendarWidgetFactory";
        static final int EVENT_MAX_COUNT = 100;
        private static CalendarAppWidgetModel mModel;
        private static final String EVENT_SELECTION = Calendars.VISIBLE + "=1";
        private static final String EVENT_SELECTION_HIDE_DECLINED = Calendars.VISIBLE
                + "=1 AND "
                + Instances.SELF_ATTENDEE_STATUS
                + "!="
                + Attendees.ATTENDEE_STATUS_DECLINED;

        private static final int INDEX_ALL_DAY = 0;
        private static final int INDEX_BEGIN = 1;
        private static final int INDEX_END = 2;
        private static final int INDEX_TITLE = 3;
        private static final int INDEX_EVENT_LOCATION = 4;
        private static final int INDEX_EVENT_ID = 5;
        private static final int INDEX_START_DAY = 6;
        private static final int INDEX_END_DAY = 7;
        private static final int INDEX_DISPLAY_COLOR = 8;
        private static final int INDEX_SELF_ATTENDEE_STATUS = 9;
        static final String[] EVENT_PROJECTION = new String[] {
                Instances.ALL_DAY, Instances.BEGIN, Instances.END,
                Instances.TITLE, Instances.EVENT_LOCATION, Instances.EVENT_ID,
                Instances.START_DAY, Instances.END_DAY,
                Instances.DISPLAY_COLOR, // If SDK < 16, set to
                                         // Instances.CALENDAR_COLOR.
                Instances.SELF_ATTENDEE_STATUS, };
        private static final String EVENT_SORT_ORDER = Instances.START_DAY
                + " ASC, " + Instances.START_MINUTE + " ASC, "
                + Instances.END_DAY + " ASC, " + Instances.END_MINUTE
                + " ASC LIMIT " + EVENT_MAX_COUNT;
        static final int WIDGET_UPDATE_THROTTLE = 500;
        private static Object mLock = new Object();
        private final ExecutorService executor = Executors
                .newSingleThreadExecutor();
        private final Handler mHandler = new Handler();
        private static final AtomicInteger currentVersion = new AtomicInteger(0);
        private static volatile int mSerialNum = 0;
        private int mLastSerialNum = -1;
        private LinkedList<Integer> eventTimes = new LinkedList<Integer>() {
        };
        private static final int ONE_DAY_MIILIS = 24 * 60 * 60 * 1000;

        private final Runnable mTimezoneChanged = new Runnable() {
            @Override
            public void run() {
                if (mLoader != null) {
                    mLoader.forceLoad();
                }
            }
        };

        private Runnable createUpdateLoaderRunnable(final String selection,
                final PendingResult result, final int version) {
            return new Runnable() {
                @Override
                public void run() {
                    // If there is a newer load request in the queue, skip
                    // loading.
                    if (mLoader != null && version >= currentVersion.get()) {
                        Uri uri = createLoaderUri();
                        mLoader.setUri(uri);
                        mLoader.setSelection(selection);
                        synchronized (mLock) {
                            mLastSerialNum = ++mSerialNum;
                        }
                        mLoader.forceLoad();
                    }
                    result.finish();
                }
            };
        }

        private final BroadcastReceiver mDateTimeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "lxg receive time changed broadcast....");
                calendarWidget.updateWidgetWhenDateChanged(context);
            }
        };

        protected CalendarWidgetFactory(Context context, Intent intent) {
            mContext = context;
            mAppWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);

        }

        public CalendarWidgetFactory() {
            // This is being created as part of onReceive
        }

        @Override
        public void onCreate() {
            Log.d(TAG, "Calendar service onCreate");
            IntentFilter datetimeFilter = new IntentFilter(
                    Intent.ACTION_TIME_TICK);
            mContext.registerReceiver(mDateTimeReceiver, datetimeFilter);
            String selection = queryForSelection();
            initLoader(selection);
        }

        public void initLoader(String selection) {
            refreshWeather();
            Uri uri = createLoaderUri();
            if (DEBUG)
                Log.d(TAG, "Calendar service initLoader, selection = "
                        + selection + ", uri=" + uri);
            if (!Utils.checkSelfPermission(mContext,
                    Manifest.permission.READ_CALENDAR)) {
                return;
            }
            mLoader = new CursorLoader(mContext, uri, EVENT_PROJECTION,
                    selection, null, null);
            mLoader.setUpdateThrottle(WIDGET_UPDATE_THROTTLE);
            synchronized (mLock) {
                mLastSerialNum = ++mSerialNum;
            }
            mLoader.registerListener(mAppWidgetId, this);
            mLoader.startLoading();
        }

        private void refreshWeather() {
            if (!Utils.isAbroadBranch(mContext)) {
                /*
                 * WeatherController.getController(
                 * mContext.getApplicationContext()) .requestRefreshWeather();
                 */
            }
        }

        private Uri createLoaderUri() {
            Time t = new Time();
            t.setToNow();
            SharedPreferences displayMonth = mContext.getSharedPreferences(
                    "display_month", 0);

            int year = displayMonth.getInt("year", t.year);
            int month = displayMonth.getInt("month", t.month);
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.YEAR, year);
            ca.set(Calendar.MONTH, month);
            ca.set(Calendar.DAY_OF_MONTH, 1);
            ca.set(Calendar.HOUR_OF_DAY, 0);
            ca.set(Calendar.MINUTE, 0);
            ca.set(Calendar.SECOND, 0);
            ca.set(Calendar.MILLISECOND, 0);
            long startTime = ca.getTimeInMillis() - 6 * ONE_DAY_MIILIS;
            int endDay = ca.getActualMaximum(Calendar.DAY_OF_MONTH);
            ca.set(Calendar.DAY_OF_MONTH, endDay);
            long endTime = ca.getTimeInMillis() + 14 * ONE_DAY_MIILIS;

            Uri uri = Uri.withAppendedPath(Instances.CONTENT_URI,
                    Long.toString(startTime) + "/" + endTime);
            return uri;
        }

        private String queryForSelection() {
            return Utils.getHideDeclinedEvents(mContext) ? EVENT_SELECTION_HIDE_DECLINED
                    : EVENT_SELECTION;
        }

        @Override
        public void onLoadComplete(Loader<Cursor> loader, Cursor cursor) {
            // TODO Auto-generated method stub
            if (cursor == null) {
                return;
            }
            // If a newer update has happened since we started clean up and
            // return
            synchronized (mLock) {
                if (cursor.isClosed()) {
                    Log.d(TAG, "lxg Got a closed cursor from onLoadComplete");
                    return;
                }

                if (DEBUG)
                    Log.d(TAG, "Calendar service mLastSerialNum : "
                            + mLastSerialNum + " mSerialNum : " + mSerialNum);
                if (mLastSerialNum != mSerialNum) {
                    return;
                }
                int startDay = cursor.getColumnIndex(Instances.START_DAY);
                if (DEBUG)
                    Log.d(TAG, "lxg eventInfo startDay1=" + startDay);
                handEvent(cursor);
                String tz = Utils.getTimeZone(mContext, mTimezoneChanged);

                // Copy it to a local static cursor.
                MatrixCursor matrixCursor = Utils
                        .matrixCursorFromCursor(cursor);
                if (matrixCursor == null) {
                    Log.d(TAG,
                            "Got a closed or null cursor from onLoadComplete 1");
                    return;
                }
                try {
                    mModel = buildAppWidgetModel(mContext, matrixCursor, tz);
                } finally {
                    if (matrixCursor != null) {
                        matrixCursor.close();
                    }

                    if (cursor != null) {
                        cursor.close();
                    }
                }
                // RowInfo rowInfo = mModel.mRowInfos.get(0);
                AppWidgetManager widgetManager = AppWidgetManager
                        .getInstance(mContext);
                if (mAppWidgetId == -1) {
                    int[] ids = widgetManager.getAppWidgetIds(CalendarWidget
                            .getComponentName(mContext));

                    widgetManager.notifyAppWidgetViewDataChanged(ids,
                            R.id.events_list);
                } else {
                    widgetManager.notifyAppWidgetViewDataChanged(mAppWidgetId,
                            R.id.events_list);
                }
            }
        }

        private void handEvent(Cursor cursor) {
            if (DEBUG)
                Log.d(TAG,
                        "Calendar service handEvent count = "
                                + cursor.getCount() + ", Context="
                                + mContext.getPackageName());
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                int startDay = cursor.getInt(INDEX_START_DAY);
                int endDay = cursor.getInt(INDEX_END_DAY);
                int julianDay = startDayCurrentMonth();
                for (int i = startDay; i <= endDay; i++) {
                    eventTimes.add(i - julianDay - 1);
                }
            }
            calendarWidget.updateWidgetEvent(mContext, eventTimes);
        }

        private int startDayCurrentMonth() {
            Time t = new Time();
            SharedPreferences displayMonth = mContext.getSharedPreferences(
                    "display_month", 0);
            int year = displayMonth.getInt("year", t.year);
            int month = displayMonth.getInt("month", t.month);
            t.set(0, 0, 0, 0, month, year);
            t.normalize(true);

            int julianToday = Time.getJulianDay(t.toMillis(false), t.gmtoff);
            return julianToday;
        }

        static void updateTextView(RemoteViews views, int id, int visibility,
                String string) {
            views.setViewVisibility(id, visibility);
            if (visibility == View.VISIBLE) {
                views.setTextViewText(id, string);
            }
        }

        protected static CalendarAppWidgetModel buildAppWidgetModel(
                Context context, Cursor cursor, String timeZone) {
            CalendarAppWidgetModel model = new CalendarAppWidgetModel(context,
                    timeZone);
            model.buildFromCursor(cursor, timeZone);
            return model;
        }

        @Override
        public int getCount() {
            if (mModel == null) {
                return 1;
            }
            return Math.max(1, mModel.mRowInfos.size());
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            if (mModel == null || mModel.mRowInfos.isEmpty()
                    || position >= getCount()) {
                return 0;
            }
            RowInfo rowInfo = mModel.mRowInfos.get(position);
            if (rowInfo.mType == RowInfo.TYPE_DAY) {
                return rowInfo.mIndex;
            }
            EventInfo eventInfo = mModel.mEventInfos.get(rowInfo.mIndex);
            long prime = 31;
            long result = 1;
            result = prime * result
                    + (int) (eventInfo.id ^ (eventInfo.id >>> 32));
            result = prime * result
                    + (int) (eventInfo.start ^ (eventInfo.start >>> 32));
            return result;
        }

        @Override
        public RemoteViews getLoadingView() {
            // TODO Auto-generated method stub
            if (DEBUG)
                Log.d(TAG, "lxg getLoadingView");
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.calendar_widget_no_events);
            return views;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            // TODO Auto-generated method stub
            RemoteViews views = new RemoteViews(mContext.getPackageName(),
                    R.layout.calendar_widget_no_events);
            return views;
            /**
             * if (position < 0 || position >= getCount()) { return null; } if
             * (mModel == null || mModel.mEventInfos.isEmpty() ||
             * mModel.mRowInfos.isEmpty()) { RemoteViews views = new
             * RemoteViews(mContext.getPackageName(),
             * R.layout.calendar_widget_no_events); final Intent intent =
             * CalendarAppWidgetProvider .getLaunchFillInIntent(mContext, 0, 0,
             * 0, false); views.setOnClickFillInIntent(R.id.appwidget_no_events,
             * intent); return views; } RemoteViews views; RowInfo rowInfo =
             * mModel.mRowInfos.get(position); final EventInfo eventInfo =
             * mModel.mEventInfos.get(rowInfo.mIndex); views = new
             * RemoteViews(mContext.getPackageName(),
             * R.layout.calendar_widget_item); int displayColor =
             * Utils.getDisplayColorFromColor(eventInfo.color);
             * views.setViewVisibility(R.id.agenda_item_color, View.VISIBLE);
             * views.setInt(R.id.agenda_item_color, "setBackgroundColor",
             * displayColor); final long now = System.currentTimeMillis();
             * 
             * if (!eventInfo.allDay) { updateTextView(views, R.id.when,
             * eventInfo.visibWhen, eventInfo.when); } else { updateTextView(
             * views, R.id.when, eventInfo.visibWhen,
             * mContext.getResources().getString(
             * R.string.edit_event_all_day_label)); } // updateTextView(views,
             * R.id.where, eventInfo.visibWhere, // eventInfo.where);
             * updateTextView(views, R.id.title, eventInfo.visibTitle,
             * eventInfo.title); long start = eventInfo.start; long end =
             * eventInfo.end; // An element in ListView. if (eventInfo.allDay) {
             * String tz = Utils.getTimeZone(mContext, null); Time recycle = new
             * Time(); start = Utils.convertAlldayLocalToUTC(recycle, start,
             * tz); end = Utils.convertAlldayLocalToUTC(recycle, end, tz); }
             * final Intent fillInIntent = CalendarAppWidgetProvider
             * .getLaunchFillInIntent(mContext, eventInfo.id, start, end,
             * eventInfo.allDay);
             * views.setOnClickFillInIntent(R.id.calendar_widget_row,
             * fillInIntent); return views;
             */
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 5;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public void onDataSetChanged() {
            // TODO Auto-generated method stub

        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            if (mLoader != null) {
                mLoader.reset();
            }
            mContext.unregisterReceiver(mDateTimeReceiver);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (DEBUG)
                Log.d(TAG, "lxg intent = " + intent.getAction()
                        + ", Context = " + context.getPackageName());
            mContext = context;
            if (intent.getAction().equals("android.intent.action.DATE_CHANGED")
                    || intent.getAction().equals(
                            "android.intent.action.TIMEZONE_CHANGED")
                    || intent.getAction().equals(
                            "android.intent.action.TIME_SET")) {
                Time time = new Time();
                time.setToNow();
                SharedPreferences displayMonth = context.getSharedPreferences(
                        "display_month", 0);
                SharedPreferences.Editor editorDisplayMonth = displayMonth
                        .edit();
                editorDisplayMonth.putInt("year", time.year);
                editorDisplayMonth.putInt("month", time.month);
                editorDisplayMonth.putInt("day", time.monthDay);
                editorDisplayMonth.commit();
            }
            final PendingResult result = goAsync();
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Calendar service run");
                    // We always complete queryForSelection() even if the load
                    // task ends up being
                    // canceled because of a more recent one. Optimizing this to
                    // allow
                    // canceling would require keeping track of all the
                    // PendingResults
                    // (from goAsync) to abort them. Defer this until it becomes
                    // a problem.
                    final String selection = queryForSelection();
                    if (mLoader == null) {
                        mAppWidgetId = -1;
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                initLoader(selection);
                                result.finish();
                            }
                        });
                    } else {
                        mHandler.post(createUpdateLoaderRunnable(selection,
                                result, currentVersion.incrementAndGet()));
                    }
                }
            });
        }
    }

}
