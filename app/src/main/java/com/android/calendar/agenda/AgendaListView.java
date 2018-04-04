/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.calendar.agenda;

import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static com.android.calendar.CalendarController.EVENT_EDIT_ON_LAUNCH;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarEventModel.ReminderEntry;
import com.android.calendar.DeleteEventHelper;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.android.calendar.agenda.AgendaAdapter.ViewHolder;
import com.android.calendar.agenda.AgendaFragment.Event;
import com.android.calendar.agenda.AgendaWindowAdapter.AgendaItem;
import com.android.calendar.agenda.AgendaWindowAdapter.DayAdapterInfo;
import com.android.calendar.event.EditEventActivity;
import com.android.calendar.event.EditEventHelper;
import com.android.calendar.vcalendar.VCalUtil;
import com.android.calendar.vcalendar.XCalendarProcessor;
//import android.widget.HCTPopupWindow;
import com.hct.gios.widget.HCTPopupWindow;

public class AgendaListView extends ListView implements OnItemClickListener,
        OnItemLongClickListener {

    private static final String TAG = "AgendaListView";
    private static final boolean DEBUG = false;
    private static final int EVENT_UPDATE_TIME = 300000; // 5 minutes

    private AgendaWindowAdapter mWindowAdapter;
    private DeleteEventHelper mDeleteEventHelper;
    private Context mContext;
    private String mTimeZone;
    private Time mTime;
    private AgendaFragment mFragment;
    private boolean mShowEventDetailsWithAgenda;
    private Handler mHandler = null;

    // HCT_MODIFY.lixiange MF3.0 long press
    private CharSequence[] mLongPressItems;
    private String mLongPressTitle;
    protected final Resources mResources;
    private Activity mActivity;
    private static final String[] EVENT_PROJECTION = new String[] { Events._ID, // 0
                                                                                // do
                                                                                // not
                                                                                // remove;
                                                                                // used
                                                                                // in
                                                                                // DeleteEventHelper
            Events.TITLE, // 1 do not remove; used in DeleteEventHelper
            Events.RRULE, // 2 do not remove; used in DeleteEventHelper
            Events.ALL_DAY, // 3 do not remove; used in DeleteEventHelper
            Events.CALENDAR_ID, // 4 do not remove; used in DeleteEventHelper
            Events.DTSTART, // 5 do not remove; used in DeleteEventHelper
            Events._SYNC_ID, // 6 do not remove; used in DeleteEventHelper
            Events.EVENT_TIMEZONE, // 7 do not remove; used in DeleteEventHelper
            Events.DESCRIPTION, // 8
            Events.EVENT_LOCATION, // 9
            Calendars.CALENDAR_ACCESS_LEVEL, // 10
            Calendars.CALENDAR_COLOR, // 11
            Events.HAS_ATTENDEE_DATA, // 12
            Events.ORGANIZER, // 13
            Events.HAS_ALARM, // 14
            Calendars.MAX_REMINDERS, // 15
            Calendars.ALLOWED_REMINDERS, // 16
            Events.ORIGINAL_SYNC_ID, // 17 do not remove; used in
                                     // DeleteEventHelper
            Events.DTEND, // 18
            Events.DURATION, // 19
    };
    private static final int EVENT_INDEX_ID = 0;
    private static final int EVENT_INDEX_TITLE = 1;
    private static final int EVENT_INDEX_RRULE = 2;
    private static final int EVENT_INDEX_ALL_DAY = 3;
    private static final int EVENT_INDEX_CALENDAR_ID = 4;
    private static final int EVENT_INDEX_SYNC_ID = 6;
    private static final int EVENT_INDEX_EVENT_TIMEZONE = 7;
    private static final int EVENT_INDEX_DESCRIPTION = 8;
    private static final int EVENT_INDEX_EVENT_LOCATION = 9;
    private static final int EVENT_INDEX_ACCESS_LEVEL = 10;
    private static final int EVENT_INDEX_COLOR = 11;
    private static final int EVENT_INDEX_HAS_ATTENDEE_DATA = 12;
    private static final int EVENT_INDEX_ORGANIZER = 13;
    private static final int EVENT_INDEX_HAS_ALARM = 14;
    private static final int EVENT_INDEX_MAX_REMINDERS = 15;
    private static final int EVENT_INDEX_ALLOWED_REMINDERS = 16;
    private static final int EVENT_INDEX_DTSTART = 5;
    private static final int EVENT_INDEX_DTEND = 18;
    private static final int EVENT_INDEX_DURATION = 19;
    // HCT_MODIFY.lixiange MF3.0 long press

    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(mContext, this);
            mTime.switchTimezone(mTimeZone);
        }
    };

    // runs every midnight and refreshes the view in order to update the
    // past/present
    // separator
    private final Runnable mMidnightUpdater = new Runnable() {
        @Override
        public void run() {
            refresh(true);
            Utils.setMidnightUpdater(mHandler, mMidnightUpdater, mTimeZone);
        }
    };

    // Runs every EVENT_UPDATE_TIME to gray out past events
    private final Runnable mPastEventUpdater = new Runnable() {
        @Override
        public void run() {
            if (updatePastEvents() == true) {
                refresh(true);
            }
            setPastEventsUpdater();
        }
    };

    public AgendaListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getResources();
        initView(context);
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    public void setFragment(Fragment f) {
        mFragment = (AgendaFragment) f;

    }

    public AgendaFragment getFragment() {
        return mFragment;
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event

    private void initView(Context context) {
        mContext = context;
        mTimeZone = Utils.getTimeZone(context, mTZUpdater);
        mTime = new Time(mTimeZone);
        mTime.setToNow();
        setOnItemClickListener(this);
        setOnItemLongClickListener(this);
        setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setVerticalScrollBarEnabled(false);
        mWindowAdapter = new AgendaWindowAdapter(context, this,
                Utils.getConfigBool(context,
                        R.bool.show_event_details_with_agenda));
        mWindowAdapter.setSelectedInstanceId(-1/* TODO:instanceId */);
        setAdapter(mWindowAdapter);
        setCacheColorHint(context.getResources().getColor(
                R.color.agenda_item_not_selected));
        mDeleteEventHelper = new DeleteEventHelper(context, null, false /*
                                                                         * don't
                                                                         * exit
                                                                         * when
                                                                         * done
                                                                         */);
        mShowEventDetailsWithAgenda = Utils.getConfigBool(mContext,
                R.bool.show_event_details_with_agenda);
        // Hide ListView dividers, they are done in the item views themselves
        setDivider(null);
        setDividerHeight(0);

        mHandler = new Handler();
    }

    // Sets a thread to run every EVENT_UPDATE_TIME in order to update the list
    // with grayed out past events
    private void setPastEventsUpdater() {

        // Run the thread in the nearest rounded EVENT_UPDATE_TIME
        long now = System.currentTimeMillis();
        long roundedTime = (now / EVENT_UPDATE_TIME) * EVENT_UPDATE_TIME;
        mHandler.removeCallbacks(mPastEventUpdater);
        mHandler.postDelayed(mPastEventUpdater, EVENT_UPDATE_TIME
                - (now - roundedTime));
    }

    // Stop the past events thread
    private void resetPastEventsUpdater() {
        mHandler.removeCallbacks(mPastEventUpdater);
    }

    // Go over all visible views and checks if all past events are grayed out.
    // Returns true is there is at least one event that ended and it is not
    // grayed out.
    private boolean updatePastEvents() {

        int childCount = getChildCount();
        boolean needUpdate = false;
        long now = System.currentTimeMillis();
        Time time = new Time(mTimeZone);
        time.set(now);
        int todayJulianDay = Time.getJulianDay(now, time.gmtoff);

        // Go over views in list
        for (int i = 0; i < childCount; ++i) {
            View listItem = getChildAt(i);
            Object o = listItem.getTag();
            if (o instanceof AgendaByDayAdapter.ViewHolder) {
                // day view - check if day in the past and not grayed yet
                AgendaByDayAdapter.ViewHolder holder = (AgendaByDayAdapter.ViewHolder) o;
                if (holder.julianDay <= todayJulianDay && !holder.grayed) {
                    needUpdate = true;
                    break;
                }
            } else if (o instanceof AgendaAdapter.ViewHolder) {
                // meeting view - check if event in the past or started already
                // and not grayed yet
                // All day meetings for a day are grayed out
                AgendaAdapter.ViewHolder holder = (AgendaAdapter.ViewHolder) o;
                if (!holder.grayed
                        && ((!holder.allDay && holder.startTimeMilli <= now) || (holder.allDay && holder.julianDay <= todayJulianDay))) {
                    needUpdate = true;
                    break;
                }
            }
        }
        return needUpdate;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mWindowAdapter.close();
    }

    // Implementation of the interface OnItemClickListener
    @Override
    public void onItemClick(AdapterView<?> a, View v, int position, long id) {
        if (id != -1) {
            if (AgendaFragment.actionModeStarted) {
                selectItemByPosition(position);
                mFragment.updateSelectedCount();
                return;
            }

            // Switch to the EventInfo view
            AgendaItem item = mWindowAdapter.getAgendaItemByPosition(position);
            long oldInstanceId = mWindowAdapter.getSelectedInstanceId();
            mWindowAdapter.setSelectedView(v);

            // If events are shown to the side of the agenda list , do nothing
            // when the same event is selected , otherwise show the selected
            // event.

            if (item != null
                    && (oldInstanceId != mWindowAdapter.getSelectedInstanceId() || !mShowEventDetailsWithAgenda)) {
                long startTime = item.begin;
                long endTime = item.end;
                // Holder in view holds the start of the specific part of a
                // multi-day event ,
                // use it for the goto
                long holderStartTime;
                Object holder = v.getTag();
                if (holder instanceof AgendaAdapter.ViewHolder) {
                    holderStartTime = ((AgendaAdapter.ViewHolder) holder).startTimeMilli;
                } else {
                    holderStartTime = startTime;
                }
                if (item.allDay) {
                    startTime = Utils.convertAlldayLocalToUTC(mTime, startTime,
                            mTimeZone);
                    endTime = Utils.convertAlldayLocalToUTC(mTime, endTime,
                            mTimeZone);
                }
                mTime.set(startTime);
                CalendarController controller = CalendarController
                        .getInstance(mContext);
                controller.sendEventRelatedEventWithExtra(this,
                        EventType.VIEW_EVENT, item.id, startTime, endTime, 0,
                        0, CalendarController.EventInfo.buildViewExtraLong(
                                Attendees.ATTENDEE_STATUS_NONE, item.allDay),
                        holderStartTime);
            }
        }
    }

    // HCT_MODIFY.lixiange MF3.0 long press Implementation of the interface
    // OnItemLongClickListener
    @Override
    public boolean onItemLongClick(AdapterView<?> a, View v, int position,
            long id) {
        if (id != -1) {
            if (AgendaFragment.actionModeStarted) {
                selectItemByPosition(position);
                mFragment.updateSelectedCount();
                return true;
            }
            final AgendaItem event = mWindowAdapter
                    .getAgendaItemByPosition(position);
            final int clickPosition = position;
            int flags = DateUtils.FORMAT_SHOW_WEEKDAY;

            Cursor cursor = mContext.getContentResolver().query(
                    Events.CONTENT_URI, new String[] { Events.TITLE, // 0
                            Events.DESCRIPTION, // 1
                            Events.EVENT_LOCATION, // 2
                            Events.AVAILABILITY, // 3
                            Events.ACCESS_LEVEL, // 4
                            Events.RRULE // 5
                    }, Events._ID + "='" + event.id + "'", null /*
                                                                 * selection
                                                                 * args
                                                                 */, null /*
                                                                           * sort
                                                                           * order
                                                                           */);
            if (cursor != null || cursor.getCount() > 0) {
                cursor.moveToFirst();
                mLongPressTitle = cursor.getString(0);
            }

            if (cursor != null) {
                cursor.close();
                cursor = null;
            }

            if (mLongPressTitle == null || mLongPressTitle.isEmpty()) {
                mLongPressTitle = Utils.formatDateRange(mContext, event.begin,
                        event.begin, flags);
            }
            mLongPressItems = new CharSequence[] {
                    mResources.getString(R.string.edit_label),
                    mResources.getString(R.string.share_label),
                    mResources
                            .getString(R.string.copy_and_new_event_dialog_option),
                    mResources.getString(R.string.delete_label)

            };

            final HCTPopupWindow mHCTPopup = new HCTPopupWindow(mContext);
            OnItemClickListener mItemClickListener = new OnItemClickListener() {
                public void onItemClick(AdapterView a, View v, int position,
                        long id) {
                    Log.d(TAG, "lxg HCTPopupWindow ---- ");
                    if (position == 0) {
                        /**
                         * Uri uri =
                         * ContentUris.withAppendedId(Events.CONTENT_URI,
                         * event.id); Intent intent = new
                         * Intent(Intent.ACTION_EDIT, uri);
                         * intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin);
                         * intent.putExtra(EXTRA_EVENT_END_TIME, event.end);
                         * intent.setClass(mActivity, EditEventActivity.class);
                         * intent.putExtra(EVENT_EDIT_ON_LAUNCH, true);
                         * mContext.startActivity(intent);
                         */
                        Uri uri = ContentUris.withAppendedId(
                                Events.CONTENT_URI, event.id);

                        Cursor cursor = mContext.getContentResolver().query(
                                Events.CONTENT_URI,
                                new String[] { Events.TITLE, // 0
                                        Events.DESCRIPTION, // 1
                                        Events.EVENT_LOCATION, // 2
                                        Events.AVAILABILITY, // 3
                                        Events.ACCESS_LEVEL, // 4
                                        Events.RRULE, // 5
                                        Events.EVENT_COLOR // 6
                                }, Events._ID + "='" + event.id + "'", null,
                                null);

                        // Log.d(TAG,"lxg EVENT_COLOR = " +
                        // cursor.getString(6));
                        Intent intent = new Intent(Intent.ACTION_EDIT, uri);
                        intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin);
                        intent.putExtra(EXTRA_EVENT_END_TIME, 0);
                        intent.setClass(mContext, EditEventActivity.class);
                        intent.putExtra(EVENT_EDIT_ON_LAUNCH, true);
                        /**
                         * intent.putExtra(EditEventActivity.
                         * EXTRA_EVENT_REMINDERS, EventViewUtils
                         * .reminderItemsToReminders(mReminderViews,
                         * mReminderMinuteValues, mReminderMethodValues));
                         */
                        if (cursor != null || cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            intent.putExtra(Events.TITLE, cursor.getString(0));
                            intent.putExtra(Events.DESCRIPTION,
                                    cursor.getString(1));
                            intent.putExtra(Events.EVENT_LOCATION,
                                    cursor.getString(2));
                            intent.putExtra(Events.AVAILABILITY,
                                    cursor.getInt(3));
                            intent.putExtra(Events.ACCESS_LEVEL,
                                    cursor.getInt(4));
                            intent.putExtra(Events.RRULE, cursor.getString(5));
                            intent.putExtra(Events.EVENT_COLOR,
                                    cursor.getString(6));
                        }
                        if (cursor != null) {
                            cursor.close();
                            cursor = null;
                        }

                        mContext.startActivity(intent);
                    } else if (position == 1) {
                        SendCalendar(event.id);
                    } else if (position == 2) {
                        Uri uri = ContentUris.withAppendedId(
                                Events.CONTENT_URI, event.id);

                        Cursor cursor = mContext.getContentResolver().query(
                                Events.CONTENT_URI,
                                new String[] { Events.TITLE, // 0
                                        Events.DESCRIPTION, // 1
                                        Events.EVENT_LOCATION, // 2
                                        Events.AVAILABILITY, // 3
                                        Events.ACCESS_LEVEL, // 4
                                        Events.RRULE, // 5
                                        Events.EVENT_COLOR // 6
                                }, Events._ID + "='" + event.id + "'", null,
                                null);

                        Intent intent = new Intent(
                                "com.android.calendar.copyAndNew", uri);
                        intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin);
                        intent.putExtra(EXTRA_EVENT_END_TIME, 0);
                        intent.setClass(mContext, EditEventActivity.class);
                        intent.putExtra(EVENT_EDIT_ON_LAUNCH, true);
                        if (cursor != null || cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            intent.putExtra(Events.TITLE, cursor.getString(0));
                            intent.putExtra(Events.DESCRIPTION,
                                    cursor.getString(1));
                            intent.putExtra(Events.EVENT_LOCATION,
                                    cursor.getString(2));
                            intent.putExtra(Events.AVAILABILITY,
                                    cursor.getInt(3));
                            intent.putExtra(Events.ACCESS_LEVEL,
                                    cursor.getInt(4));
                            intent.putExtra(Events.RRULE, cursor.getString(5));
                            intent.putExtra(Events.EVENT_COLOR,
                                    cursor.getString(6));
                        }
                        if (cursor != null) {
                            cursor.close();
                            cursor = null;
                        }

                        mContext.startActivity(intent);
                    } else if (position == 3) {
                        DeleteEventHelper deleteHelper = new DeleteEventHelper(
                                mActivity, mActivity, false);
                        // deleteHelper.delete(event.begin, event.end, event.id,
                        // position);
                        // 20160415:add for delete selected allday error.
                        long beginTime = event.begin;
                        long endTime = event.end;
                        if (event.allDay) {
                            beginTime = Utils.convertAlldayLocalToUTC(null,
                                    beginTime, mTimeZone);
                            endTime = Utils.convertAlldayLocalToUTC(null,
                                    endTime, mTimeZone);
                            if (DEBUG) {
                                Log.d(TAG, "beginTime = " + beginTime
                                        + ", endTime = " + endTime);
                            }
                        }
                        deleteHelper.delete(beginTime, endTime, event.id, -1);
                    }
                    mHCTPopup.dismiss();
                }
            };
            mHCTPopup.setItems(mLongPressItems, mItemClickListener);
            mHCTPopup.showAtLocation(v, a);
            /**
             * new AlertDialog.Builder(mContext).setTitle(mLongPressTitle)
             * .setItems(mLongPressItems, new DialogInterface.OnClickListener()
             * {
             * 
             * @Override public void onClick(DialogInterface dialog, int which)
             *           { if (which == 0) { Uri uri =
             *           ContentUris.withAppendedId(Events.CONTENT_URI,
             *           event.id); Intent intent = new
             *           Intent(Intent.ACTION_EDIT, uri);
             *           intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin);
             *           intent.putExtra(EXTRA_EVENT_END_TIME, event.end);
             *           intent.setClass(mActivity, EditEventActivity.class);
             *           intent.putExtra(EVENT_EDIT_ON_LAUNCH, true);
             *           mContext.startActivity(intent); } else if(which == 1) {
             *           SendCalendar(event.id); }else if(which == 2){ Uri uri =
             *           ContentUris.withAppendedId(Events.CONTENT_URI,
             *           event.id);
             * 
             *           Cursor cursor = mContext.getContentResolver().query(
             *           Events.CONTENT_URI, new String[] { Events.TITLE, // 0
             *           Events.DESCRIPTION, // 1 Events.EVENT_LOCATION, // 2
             *           Events.AVAILABILITY, // 3 Events.ACCESS_LEVEL, // 4
             *           Events.RRULE //5 }, Events._ID + "='" + event.id + "'",
             *           null , null );
             * 
             *           Intent intent = new
             *           Intent("com.android.calendar.copyAndNew", uri);
             *           intent.putExtra(EXTRA_EVENT_BEGIN_TIME, event.begin);
             *           intent.putExtra(EXTRA_EVENT_END_TIME, 0);
             *           intent.setClass(mContext, EditEventActivity.class);
             *           intent.putExtra(EVENT_EDIT_ON_LAUNCH, true); if(cursor
             *           != null || cursor.getCount() > 0) {
             *           cursor.moveToFirst(); intent.putExtra(Events.TITLE,
             *           cursor.getString(0));
             *           intent.putExtra(Events.DESCRIPTION,
             *           cursor.getString(1));
             *           intent.putExtra(Events.EVENT_LOCATION,
             *           cursor.getString(2));
             *           intent.putExtra(Events.AVAILABILITY, cursor.getInt(3));
             *           intent.putExtra(Events.ACCESS_LEVEL, cursor.getInt(4));
             *           intent.putExtra(Events.RRULE, cursor.getString(5)); }
             *           if(cursor != null){ cursor.close(); cursor = null; }
             * 
             *           mContext.startActivity(intent); }else if (which == 3){
             *           DeleteEventHelper deleteHelper = new DeleteEventHelper(
             *           mActivity, mActivity, false);
             *           deleteHelper.delete(event.begin, event.end, event.id,
             *           which); } } }).show().setCanceledOnTouchOutside(true);
             */
        }
        return true;
    }

    // HCT_MODIFY.lixiange MF3.0 long press

    private float mLastTouchY = 0;
    private int mTouchPos = AdapterView.INVALID_POSITION;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean res = super.onTouchEvent(ev);
        // TODO Auto-generated method stub
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastTouchY = ev.getY();
            mTouchPos = pointToPosition((int) ev.getX(), (int) ev.getY());
            break;

        case MotionEvent.ACTION_UP:
            if (Math.abs(ev.getY() - mLastTouchY) > 120) {
                if (ev.getY() > mLastTouchY && getFirstVisiblePosition() == 0) {
                    mWindowAdapter.loadMorePullDown();
                } else if (ev.getY() < mLastTouchY
                        && getLastVisiblePosition() >= getCount() - 1) {
                    mWindowAdapter.loadMorePullUp();
                }
            }
            break;
        default:
            break;
        }
        return res;
    }

    private boolean canTouchToPull() {
        return mTouchPos == AdapterView.INVALID_POSITION
                || !(mTouchPos >= 0 && mTouchPos < mWindowAdapter.getCount());
    }

    public void goTo(Time time, long id, String searchQuery, boolean forced,
            boolean refreshEventInfo) {
        if (time == null) {
            time = mTime;
            long goToTime = getFirstVisibleTime(null);
            if (goToTime <= 0) {
                goToTime = System.currentTimeMillis();
            }
            time.set(goToTime);
        }
        mTime.set(time);
        mTime.switchTimezone(mTimeZone);
        mTime.normalize(true);
        if (DEBUG) {
            Log.d(TAG, "Goto with time " + mTime.toString());
        }
        mWindowAdapter
                .refresh(mTime, id, searchQuery, forced, refreshEventInfo);
    }

    public void refresh(boolean forced) {
        mWindowAdapter.refresh(mTime, -1, null, forced, false);
    }

    public void deleteSelectedEvent() {
        int position = getSelectedItemPosition();
        AgendaItem agendaItem = mWindowAdapter
                .getAgendaItemByPosition(position);
        if (agendaItem != null) {
            mDeleteEventHelper.delete(agendaItem.begin, agendaItem.end,
                    agendaItem.id, -1);
        }
    }

    public View getFirstVisibleView() {
        Rect r = new Rect();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View listItem = getChildAt(i);
            listItem.getLocalVisibleRect(r);
            if (r.top >= 0) { // if visible
                return listItem;
            }
        }
        return null;
    }

    public long getSelectedTime() {
        int position = getSelectedItemPosition();
        if (position >= 0) {
            AgendaItem item = mWindowAdapter.getAgendaItemByPosition(position);
            if (item != null) {
                return item.begin;
            }
        }
        return getFirstVisibleTime(null);
    }

    public AgendaAdapter.ViewHolder getSelectedViewHolder() {
        return mWindowAdapter.getSelectedViewHolder();
    }

    public long getFirstVisibleTime(AgendaItem item) {
        AgendaItem agendaItem = item;
        if (item == null) {
            agendaItem = getFirstVisibleAgendaItem();
        }
        if (agendaItem != null) {
            Time t = new Time(mTimeZone);
            t.set(agendaItem.begin);
            // Save and restore the time since setJulianDay sets the time to
            // 00:00:00
            int hour = t.hour;
            int minute = t.minute;
            int second = t.second;
            t.setJulianDay(agendaItem.startDay);
            t.hour = hour;
            t.minute = minute;
            t.second = second;
            if (DEBUG) {
                t.normalize(true);
                Log.d(TAG, "first position had time " + t.toString());
            }
            return t.normalize(false);
        }
        return 0;
    }

    public void updateMode(boolean nowDeteleMode) {
        mWindowAdapter.showCheckBox(nowDeteleMode);
    }

    public AgendaItem getFirstVisibleAgendaItem() {
        int position = getFirstVisiblePosition();
        if (DEBUG) {
            Log.v(TAG, "getFirstVisiblePosition = " + position);
        }

        // mShowEventDetailsWithAgenda == true implies we have a sticky header.
        // In that case
        // we may need to take the second visible position, since the first one
        // maybe the one
        // under the sticky header.
        if (mShowEventDetailsWithAgenda) {
            View v = getFirstVisibleView();
            if (v != null) {
                Rect r = new Rect();
                v.getLocalVisibleRect(r);
                if (r.bottom - r.top <= mWindowAdapter.getStickyHeaderHeight()) {
                    position++;
                }
            }
        }

        return mWindowAdapter.getAgendaItemByPosition(position, false /*
                                                                       * startDay
                                                                       * = date
                                                                       * separator
                                                                       * date
                                                                       * instead
                                                                       * of
                                                                       * actual
                                                                       * event
                                                                       * startday
                                                                       */);

    }

    public int getJulianDayFromPosition(int position) {
        DayAdapterInfo info = mWindowAdapter.getAdapterInfoByPosition(position);
        if (info != null) {
            return info.dayAdapter.findJulianDayFromPosition(position
                    - info.offset);
        }
        return 0;
    }

    // Finds is a specific event (defined by start time and id) is visible
    public boolean isAgendaItemVisible(Time startTime, long id) {

        if (id == -1 || startTime == null) {
            return false;
        }

        View child = getChildAt(0);
        // View not set yet, so not child - return
        if (child == null) {
            return false;
        }
        int start = getPositionForView(child);
        long milliTime = startTime.toMillis(true);
        int childCount = getChildCount();
        int eventsInAdapter = mWindowAdapter.getCount();

        for (int i = 0; i < childCount; i++) {
            if (i + start >= eventsInAdapter) {
                break;
            }
            AgendaItem agendaItem = mWindowAdapter.getAgendaItemByPosition(i
                    + start);
            if (agendaItem == null) {
                continue;
            }
            if (agendaItem.id == id && agendaItem.begin == milliTime) {
                View listItem = getChildAt(i);
                if (listItem.getTop() <= getHeight()
                        && listItem.getTop() >= mWindowAdapter
                                .getStickyHeaderHeight()) {
                    return true;
                }
            }
        }
        return false;
    }

    public long getSelectedInstanceId() {
        return mWindowAdapter.getSelectedInstanceId();
    }

    public void setSelectedInstanceId(long id) {
        mWindowAdapter.setSelectedInstanceId(id);
    }

    // Move the currently selected or visible focus down by offset amount.
    // offset could be negative.
    public void shiftSelection(int offset) {
        shiftPosition(offset);
        int position = getSelectedItemPosition();
        if (position != INVALID_POSITION) {
            setSelectionFromTop(position + offset, 0);
        }
    }

    private void shiftPosition(int offset) {
        if (DEBUG) {
            Log.v(TAG, "Shifting position " + offset);
        }

        View firstVisibleItem = getFirstVisibleView();

        if (firstVisibleItem != null) {
            Rect r = new Rect();
            firstVisibleItem.getLocalVisibleRect(r);
            // if r.top is < 0, getChildAt(0) and getFirstVisiblePosition() is
            // returning an item above the first visible item.
            int position = getPositionForView(firstVisibleItem);
            setSelectionFromTop(position + offset, r.top > 0 ? -r.top : r.top);
            if (DEBUG) {
                if (firstVisibleItem.getTag() instanceof AgendaAdapter.ViewHolder) {
                    ViewHolder viewHolder = (AgendaAdapter.ViewHolder) firstVisibleItem
                            .getTag();
                    Log.v(TAG, "Shifting from " + position + " by " + offset
                            + ". Title " + viewHolder.title.getText());
                } else if (firstVisibleItem.getTag() instanceof AgendaByDayAdapter.ViewHolder) {
                    AgendaByDayAdapter.ViewHolder viewHolder = (AgendaByDayAdapter.ViewHolder) firstVisibleItem
                            .getTag();
                    Log.v(TAG, "Shifting from " + position + " by " + offset
                            + ". Date  " + viewHolder.dateView.getText());
                } else if (firstVisibleItem instanceof TextView) {
                    Log.v(TAG, "Shifting: Looking at header here. "
                            + getSelectedItemPosition());
                }
            }
        } else if (getSelectedItemPosition() >= 0) {
            if (DEBUG) {
                Log.v(TAG, "Shifting selection from "
                        + getSelectedItemPosition() + " by " + offset);
            }
            setSelection(getSelectedItemPosition() + offset);
        }
    }

    public void setHideDeclinedEvents(boolean hideDeclined) {
        mWindowAdapter.setHideDeclinedEvents(hideDeclined);
    }

    public void onResume() {
        mTZUpdater.run();
        Utils.setMidnightUpdater(mHandler, mMidnightUpdater, mTimeZone);
        setPastEventsUpdater();
        mWindowAdapter.onResume();
    }

    public void onPause() {
        Utils.resetMidnightUpdater(mHandler, mMidnightUpdater);
        resetPastEventsUpdater();
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    public void selectItemByPosition(int position) {
        mWindowAdapter.selectItemByPosition(position);
        try {
            mWindowAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void selectAll() {
        mWindowAdapter.selectAll();
        try {
            mWindowAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void cancelSelect() {
        mWindowAdapter.cancelSelect();
        try {
            mWindowAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public boolean isSelectAll() {
        return mWindowAdapter.isSelectAll();
    }

    public ArrayList<Event> getSelectedEvents() {
        ArrayList<Event> mSelectedEvents = new ArrayList<Event>();

        ArrayList<AgendaItem> events = mWindowAdapter.getSelectedEvents();
        Iterator<AgendaItem> it = events.iterator();
        while (it.hasNext()) {
            AgendaItem event = it.next();

            Event mEvent = new Event();
            mEvent.id = event.id;
            mEvent.begin = event.begin;
            mEvent.end = event.end;
            mEvent.hasRrule = event.hasRrule;
            // 20160415:add for delete selected allday error.
            if (event.allDay) {
                mEvent.begin = Utils.convertAlldayLocalToUTC(null, event.begin,
                        mTimeZone);
                mEvent.end = Utils.convertAlldayLocalToUTC(null, event.end,
                        mTimeZone);
                if (DEBUG) {
                    Log.d(TAG, " beginTime = " + mEvent.begin + ", endTime = "
                            + mEvent.begin);
                }
            }

            mSelectedEvents.add(mEvent);
        }

        return mSelectedEvents;
    }

    public void setSavedSelectedItems(int[] SelectedDatas) {
        mWindowAdapter.setSavedSelectedItems(SelectedDatas);
    }

    /* HCT_MODIFY longgang for share event start */

    /**
     * Loads an integer array asset into a list.
     */
    private static ArrayList<Integer> loadIntegerArray(Resources r, int resNum) {
        int[] vals = r.getIntArray(resNum);
        int size = vals.length;
        ArrayList<Integer> list = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            list.add(vals[i]);
        }

        return list;
    }

    // HCT_MODIFY.lixiange MF3.0 long press
    public void initReminders(ArrayList<ReminderEntry> mOriginalReminders,
            Cursor cursor) {
        ArrayList<Integer> mReminderMethodValues = loadIntegerArray(
                mContext.getResources(), R.array.reminder_methods_values);
        // Add reminders
        mOriginalReminders.clear();
        while (cursor.moveToNext()) {
            int minutes = cursor
                    .getInt(EditEventHelper.REMINDERS_INDEX_MINUTES);
            int method = cursor.getInt(EditEventHelper.REMINDERS_INDEX_METHOD);

            if (method != Reminders.METHOD_DEFAULT
                    && !mReminderMethodValues.contains(method)) {
                // Stash unsupported reminder types separately so we don't alter
                // them in the UI
            } else {
                mOriginalReminders.add(ReminderEntry.valueOf(minutes, method));
            }
        }
        // Sort appropriately for display (by time, then type)
        Collections.sort(mOriginalReminders);
    }

    public ContentValues getEventInfo(Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();

        ContentValues eventValue = new ContentValues();
        String title = cursor.getString(EVENT_INDEX_TITLE);
        if (title == null || title.length() == 0) {
            title = mContext.getResources().getString(R.string.no_title_label);
        }
        eventValue.put(Events.TITLE, title);
        eventValue.put(Events.DESCRIPTION,
                cursor.getString(EVENT_INDEX_DESCRIPTION));
        eventValue.put(Events.EVENT_LOCATION,
                cursor.getString(EVENT_INDEX_EVENT_LOCATION));
        eventValue.put(Events.DTSTART, cursor.getString(EVENT_INDEX_DTSTART));
        String dtend = cursor.getString(EVENT_INDEX_DTEND);
        if (dtend == null || dtend.equals("")) {
            String durationStr = cursor.getString(EVENT_INDEX_DURATION);
            if (durationStr != null) {
                long durationMillis = VCalUtil.getDurationMillis(durationStr);
                long startMillis = cursor.getLong(EVENT_INDEX_DTSTART);
                eventValue.put(Events.DTEND, startMillis + durationMillis);
            } else {
                eventValue.put(Events.DTEND, "0");
            }
        } else {
            eventValue.put(Events.DTEND, dtend);
        }
        eventValue.put(Events.ALL_DAY, cursor.getString(EVENT_INDEX_ALL_DAY));
        eventValue.put(Events.RRULE, cursor.getString(EVENT_INDEX_RRULE));
        eventValue.put(Events.EVENT_TIMEZONE,
                cursor.getString(EVENT_INDEX_EVENT_TIMEZONE));

        return eventValue;
    }

    public ArrayList<ContentValues> getReminderInfo(
            ArrayList<ReminderEntry> mOriginalReminders) {
        ArrayList<ContentValues> arrReminder = new ArrayList<ContentValues>();
        for (ReminderEntry re : mOriginalReminders) {
            ContentValues reminder = new ContentValues();
            reminder.put(Reminders.MINUTES, re.getMinutes());
            reminder.put(Reminders.METHOD, re.getMethod());

            arrReminder.add(reminder);
        }
        return arrReminder;
    }

    private void SendCalendar(long eventID) {
        ArrayList<ReminderEntry> mOriginalReminders = new ArrayList<ReminderEntry>();
        Cursor cursor = mContext.getContentResolver().query(Events.CONTENT_URI,
                EVENT_PROJECTION, Events._ID + "='" + eventID + "'", null /*
                                                                           * selection
                                                                           * args
                                                                           */,
                null /* sort order */);

        ContentValues eventInfo = getEventInfo(cursor);
        initReminders(mOriginalReminders, cursor);
        ArrayList<ContentValues> reminderInfo = getReminderInfo(mOriginalReminders);

        XCalendarProcessor mVCalProc = new XCalendarProcessor(mContext,
                XCalendarProcessor.XCALENDAR_V10);
        String filePath = mVCalProc.OutportXCalToFile(eventInfo, reminderInfo,
                null, 0);

        if (filePath != null) {
            File file = new File(filePath);

            Intent intent = new Intent();
            intent.setType("text/x-vcalendar");
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                mContext.startActivity(Intent.createChooser(intent, mContext
                        .getResources().getString(R.string.share_label)));
            } catch (android.content.ActivityNotFoundException ex) {
            }
        } else {
            // Toast t = Toast.makeText(this, "Fail to Write File",
            // Toast.LENGTH_SHORT);
            // t.show();
        }
    }
    // HCT_MODIFY.lixiange MF3.0 long press
}
