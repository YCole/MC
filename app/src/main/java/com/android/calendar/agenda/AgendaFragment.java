/*
 * Copyright (C) 2007 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.AsyncQueryService;
import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.CalendarEventModel;
import com.android.calendar.EventInfoFragment;
import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.StickyHeaderListView;
import com.android.calendar.Utils;
import com.android.calendar.alerts.AlertService;
import com.android.calendar.event.EditEventHelper;
//import android.app.AlertDialog;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.CheckBoxHCT;
import com.hct.gios.widget.ProgressDialog;

@SuppressLint("ResourceAsColor")
public class AgendaFragment extends Fragment implements
        CalendarController.EventHandler, OnScrollListener, ActionMode.Callback,
        LoaderCallbacks<Cursor> {

    private static final String TAG = AgendaFragment.class.getSimpleName();
    private static boolean DEBUG = false;

    protected static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";
    protected static final String BUNDLE_KEY_RESTORE_INSTANCE_ID = "key_restore_instance_id";

    private AgendaListView mAgendaListView;
    private Activity mActivity;
    private final Time mTime;
    private String mTimeZone;
    private final long mInitialTimeMillis;
    private boolean mShowEventDetailsWithAgenda;
    private CalendarController mController;
    private EventInfoFragment mEventFragment;
    private String mQuery;
    private boolean mUsedForSearch = false;
    private boolean mIsTabletConfig;
    private EventInfo mOnAttachedInfo = null;
    private boolean mOnAttachAllDay = false;
    private AgendaWindowAdapter mAdapter = null;
    private boolean mForceReplace = true;
    private long mLastShownEventId = -1;

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    private boolean mShowDelete = true;
    private ActionMode actionMode = null;
    public static boolean actionModeStarted = false;
    private LinearLayout top_layout;
    private Button action_cancel_select;
    private Button action_delete_select;
    // HCT_MODIFY lixiange MF3.0 add delete all select event

    // Tracks the time of the top visible view in order to send UPDATE_TITLE
    // messages to the action
    // bar.
    int mJulianDayOnTop = -1;

    private final Runnable mTZUpdater = new Runnable() {
        @Override
        public void run() {
            mTimeZone = Utils.getTimeZone(getActivity(), this);
            mTime.switchTimezone(mTimeZone);
        }
    };

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    private View viewSwitchBar;
    private TextView mSelectCount = null;
    private ImageView mSelectionButton = null;
    private TextView mTitle = null;
    private Menu mOptionsMenu;
    public static final String DELETE_EVENTS_ACTION = "com.android.calendar.agenda.delete_events";
    public static final int DELETE_ALL = 0;
    public static final int DELETE_SELECTED = 1;
    private int whichDelete = DELETE_ALL;
    private static HashSet<Integer> mSelectedItems = new HashSet<Integer>();
    private ArrayList<Event> mDeleteEvents = new ArrayList<Event>();
    private AsyncQueryService mService;
    private ProgressDialog mDeleteProgressDialog;
    /** MF40 */
    private TextView mHeaderViewLabel;
    private TextView mFooterViewLabel;

    private int mMinJulianDay = 0;
    private int mMaxJulianDay = 0;

    private int mCurrStartJulianDay = 0;
    private int mCurrEndJulianDay = 0;

    private LinearLayout mNoEventLayout;
    private LinearLayout mAgendaListViewLayout;

    private boolean mHideDeclined = false;
    /** MF40 */

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DELETE_EVENTS_ACTION.equals(action)) {
                if (actionModeStarted) {
                    leaveActionMode();
                } else {
                    startActionMode();
                }
            }
        }
    };

    private DialogInterface.OnClickListener mDeleteDialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int button) {
            deleteEvent();
            leaveActionMode();
        }
    };

    private DialogInterface.OnClickListener mChoiceDialogListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int button) {
            whichDelete = button;
        }
    };

    static class Event {
        long begin;
        long end;
        long id;
        boolean hasRrule;
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event

    public AgendaFragment() {
        this(0, false);
    }

    // timeMillis - time of first event to show
    // usedForSearch - indicates if this fragment is used in the search fragment
    public AgendaFragment(long timeMillis, boolean usedForSearch) {
        mInitialTimeMillis = timeMillis;
        mTime = new Time();
        mLastHandledEventTime = new Time();

        if (mInitialTimeMillis == 0) {
            mTime.setToNow();
        } else {
            mTime.set(mInitialTimeMillis);
        }
        mLastHandledEventTime.set(mTime);
        mUsedForSearch = usedForSearch;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTimeZone = Utils.getTimeZone(activity, mTZUpdater);
        mTime.switchTimezone(mTimeZone);
        mActivity = activity;
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mService = new AsyncQueryService(activity) {
            @Override
            protected void onQueryComplete(int token, Object cookie,
                    Cursor cursor) {
                if (cursor == null || cursor.getCount() == 0) {
                    return;
                }
                cursor.moveToFirst();
                CalendarEventModel mModel = new CalendarEventModel();
                EditEventHelper.setModelFromCursor(mModel, cursor);
                cursor.close();
                Event mEvent = (Event) cookie;
                deleteRepeatingEvent(mModel, mEvent, whichDelete);
            }

            @Override
            protected void onInsertComplete(int token, Object cookie, Uri uri) {
                super.onInsertComplete(token, cookie, uri);
                mInsertDeleteEventCount--;
                if (mInsertDeleteEventCount <= 0) {
                    afterAllEventDeleteComplete();
                }
            }

            @Override
            protected void onDeleteComplete(int token, Object cookie, int result) {
                mDeleteEventCount--;
                if (mDeleteEventCount <= 0) {
                    afterAllEventDeleteComplete();
                }
            }
        };
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        if (mOnAttachedInfo != null) {
            showEventInfo(mOnAttachedInfo, mOnAttachAllDay, true);
            mOnAttachedInfo = null;
        }
    }

    private void afterAllEventDeleteComplete() {
        eventsChanged();
        AlertService.updateAlertNotification(mActivity);
        if (mDeleteProgressDialog.isShowing()) {
            mDeleteProgressDialog.hide();
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        setHasOptionsMenu(true);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mController = CalendarController.getInstance(mActivity);
        mShowEventDetailsWithAgenda = Utils.getConfigBool(mActivity,
                R.bool.show_event_details_with_agenda);
        mIsTabletConfig = Utils.getConfigBool(mActivity, R.bool.tablet_config);
        if (icicle != null) {
            long prevTime = icicle.getLong(BUNDLE_KEY_RESTORE_TIME, -1);
            if (prevTime != -1) {
                mTime.set(prevTime);
                if (DEBUG) {
                    Log.d(TAG, "Restoring time to " + mTime.toString());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        int screenWidth = mActivity.getResources().getDisplayMetrics().widthPixels;
        View v = inflater.inflate(R.layout.agenda_fragment, null);
        mAgendaListView = (AgendaListView) v
                .findViewById(R.id.agenda_events_list);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mAgendaListView.setFragment(this);
        mAgendaListView.setBackgroundColor(mActivity.getResources().getColor(
                R.color.agenda_list_bg_color));
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mAgendaListView.setClickable(true);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mAgendaListView.setLongClickable(true);
        mAgendaListView.setActivity(mActivity);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        // mAgendaListView.setEmptyView(mActivity.findViewById(R.id.agenda_noEventLayout));
        mNoEventLayout = (LinearLayout) v
                .findViewById(R.id.agenda_noEventLayout);
        mAgendaListViewLayout = (LinearLayout) v
                .findViewById(R.id.agenda_listview_layout);
        top_layout = (LinearLayout) v.findViewById(R.id.top_layout);
        action_cancel_select = (Button) v
                .findViewById(R.id.action_cancel_select);
        action_delete_select = (Button) v
                .findViewById(R.id.action_delete_select);

        action_cancel_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveActionMode();
            }
        });

        action_delete_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDelete();
            }
        });

        if (savedInstanceState != null) {
            long instanceId = savedInstanceState.getLong(
                    BUNDLE_KEY_RESTORE_INSTANCE_ID, -1);
            if (instanceId != -1) {
                mAgendaListView.setSelectedInstanceId(instanceId);
            }
            // HCT_MODIFY lixiange MF3.0 add delete all select event
            int[] SelectedItems = savedInstanceState
                    .getIntArray("mSelectedItems");
            for (int i = 0; i < SelectedItems.length; i++) {
                mSelectedItems.add(SelectedItems[i]);
            }
            mAgendaListView.setSavedSelectedItems(SelectedItems);
            // HCT_MODIFY lixiange MF3.0 add delete all select event

        }

        View eventView = v.findViewById(R.id.agenda_event_info);
        if (!mShowEventDetailsWithAgenda) {
            eventView.setVisibility(View.GONE);
        }

        View topListView;
        // Set adapter & HeaderIndexer for StickyHeaderListView
        StickyHeaderListView lv = (StickyHeaderListView) v
                .findViewById(R.id.agenda_sticky_header_list);
        if (lv != null) {
            Adapter a = mAgendaListView.getAdapter();
            lv.setAdapter(a);
            if (a instanceof HeaderViewListAdapter) {
                mAdapter = (AgendaWindowAdapter) ((HeaderViewListAdapter) a)
                        .getWrappedAdapter();
                lv.setIndexer(mAdapter);
                lv.setHeaderHeightListener(mAdapter);
            } else if (a instanceof AgendaWindowAdapter) {
                mAdapter = (AgendaWindowAdapter) a;
                lv.setIndexer(mAdapter);
                lv.setHeaderHeightListener(mAdapter);
            } else {
                Log.wtf(TAG,
                        "Cannot find HeaderIndexer for StickyHeaderListView");
            }

            // Set scroll listener so that the date on the ActionBar can be set
            // while
            // the user scrolls the view
            lv.setOnScrollListener(this);
            /*
             * lv.setHeaderSeparator( getResources()
             * .getColor(R.color.agenda_list_separator_color), 1);
             */
            topListView = lv;
        } else {
            topListView = mAgendaListView;
        }

        // HCT_MODIFY lixiange MF3.0 add delete all select event
        initViews(v);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        // Since using weight for sizing the two panes of the agenda fragment
        // causes the whole
        // fragment to re-measure when the sticky header is replaced, calculate
        // the weighted
        // size of each pane here and set it

        if (!mShowEventDetailsWithAgenda) {
            ViewGroup.LayoutParams params = topListView.getLayoutParams();
            params.width = screenWidth;
            topListView.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams listParams = topListView.getLayoutParams();
            listParams.width = screenWidth * 4 / 10;
            topListView.setLayoutParams(listParams);
            ViewGroup.LayoutParams detailsParams = eventView.getLayoutParams();
            detailsParams.width = screenWidth - listParams.width;
            eventView.setLayoutParams(detailsParams);
        }
        return v;
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        super.onCreateOptionsMenu(menu, inflater);
        boolean d = menu == null;
        Log.d(TAG, "lxg onCreateOptionsMenu menu = " + d);
        mOptionsMenu = menu;
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) {
            Log.v(TAG, "OnResume to " + mTime.toString());
        }

        SharedPreferences prefs = GeneralPreferences
                .getSharedPreferences(getActivity());
        boolean hideDeclined = prefs.getBoolean(
                GeneralPreferences.KEY_HIDE_DECLINED, false);
        mHideDeclined = hideDeclined;
        mAgendaListView.setHideDeclinedEvents(hideDeclined);
        if (mLastHandledEventId != -1) {
            mAgendaListView.goTo(mLastHandledEventTime, mLastHandledEventId,
                    mQuery, true, false);
            mLastHandledEventTime = null;
            mLastHandledEventId = -1;
        } else {
            mAgendaListView.goTo(mTime, -1, mQuery, true, true);
        }
        mAgendaListView.onResume();
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        IntentFilter filter = new IntentFilter();
        filter.addAction(DELETE_EVENTS_ACTION);
        mActivity.registerReceiver(mIntentReceiver, filter);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        if (actionModeStarted) {
            leaveActionMode();
        }
        // // Register for Intent broadcasts
        // IntentFilter filter = new IntentFilter();
        // filter.addAction(Intent.ACTION_TIME_CHANGED);
        // filter.addAction(Intent.ACTION_DATE_CHANGED);
        // filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        // registerReceiver(mIntentReceiver, filter);
        //
        // mContentResolver.registerContentObserver(Events.CONTENT_URI, true,
        // mObserver);
        if (getLoaderManager().getLoader(0) != null) {
            reLoadSlideRange();
        } else {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAgendaListView == null) {
            return;
        }
        if (mShowEventDetailsWithAgenda) {
            long timeToSave;
            if (mLastHandledEventTime != null) {
                timeToSave = mLastHandledEventTime.toMillis(true);
                mTime.set(mLastHandledEventTime);
            } else {
                timeToSave = System.currentTimeMillis();
                mTime.set(timeToSave);
            }
            outState.putLong(BUNDLE_KEY_RESTORE_TIME, timeToSave);
            mController.setTime(timeToSave);
        } else {
            AgendaWindowAdapter.AgendaItem item = mAgendaListView
                    .getFirstVisibleAgendaItem();
            if (item != null) {
                long firstVisibleTime = mAgendaListView
                        .getFirstVisibleTime(item);
                if (firstVisibleTime > 0) {
                    mTime.set(firstVisibleTime);
                    mController.setTime(firstVisibleTime);
                    outState.putLong(BUNDLE_KEY_RESTORE_TIME, firstVisibleTime);
                }
                // Tell AllInOne the event id of the first visible event in the
                // list. The id will be
                // used in the GOTO when AllInOne is restored so that Agenda
                // Fragment can select a
                // specific event and not just the time.
                mLastShownEventId = item.id;
            }
        }
        if (DEBUG) {
            Log.v(TAG, "onSaveInstanceState " + mTime.toString());
        }

        // HCT_MODIFY lixiange MF3.0 add delete all select event
        int[] selectedItems = mAdapter.getSelectedItemsForSave();
        outState.putIntArray("mSelectedItems", selectedItems);
        // HCT_MODIFY lixiange MF3.0 add delete all select event

        long selectedInstance = mAgendaListView.getSelectedInstanceId();
        if (selectedInstance >= 0) {
            outState.putLong(BUNDLE_KEY_RESTORE_INSTANCE_ID, selectedInstance);
        }
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event delete use
    // actionMode start */
    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_delete_select:
            doDelete();
            return true;
        case R.id.action_cancel_select:
            leaveActionMode();
            actionMode = null;
            return true;
        }
        return true;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // mode.getMenuInflater().inflate(R.menu.multi_delete_bar, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        leaveActionMode();
        actionMode = null;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        updateActionMode(mode);
        return true;
    }

    public ActionMode startActionMode() {
        actionModeStarted = true;
        actionMode = mActivity.startActionMode(this);
        View customView = (ViewGroup) LayoutInflater.from(mActivity).inflate(
                R.layout.action_mode, null);
        /*
         * View mSelectAllButton = (View) customView
         * .findViewById(R.id.selectall_icon_text);
         */
        mSelectionButton = (ImageView) customView
                .findViewById(R.id.selection_all);
        mTitle = (TextView) customView.findViewById(R.id.title);
        mSelectCount = (TextView) customView.findViewById(R.id.select_count);
        updateSelectedCount();

        mSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAgendaListView.isSelectAll()) {
                    unMarkAll();
                } else {
                    markAll();
                }
                updateSelectedCount();
            }
        });
        actionMode.setCustomView(customView);

        top_layout.setVisibility(View.VISIBLE);

        mAgendaListView.updateMode(true);
        mAgendaListView.onResume();
        mAdapter.setHeaderFooterEnabled(false);

        hiddenSlideRange();

        return actionMode;
    }

    private void leaveActionMode() {
        if (!actionModeStarted) {
            return;
        }
        actionModeStarted = false;
        mSelectCount = null;
        mSelectionButton = null;
        mTitle = null;
        if (null != actionMode) {
            actionMode.finish();
            mAgendaListView.cancelSelect();
            mAgendaListView.updateMode(false);
            mAdapter.setHeaderFooterEnabled(true);
            mController.sendEvent(this, EventType.GO_TO, null, null, -1,
                    ViewType.AGENDA);
        }

        top_layout.setVisibility(View.GONE);
        Intent intent = new Intent(
                "android.intent.action.DISPLAY_FLOATING_BUTTON");
        getActivity().sendBroadcast(intent);

        updateSlideRange(mCurrStartJulianDay, mCurrEndJulianDay);

    }

    private void updateActionMode(final ActionMode mode) {
        if (mode == null) {
            return;
        }
        final Menu menu = mode.getMenu();
        /*
         * View optionDelete =
         * (View)menu.findItem(R.id.action_delete_select).getActionView();
         * bindActionMenuItem(optionDelete, new View.OnClickListener(){
         * 
         * @Override public void onClick(View v) {
         * onActionItemClicked(mode,menu.findItem(R.id.action_delete_select)); }
         * }, R.string.delete_label,R.drawable.ic_menu_trash_holo_light);
         */
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event delete use
    // actionMode end */
    /**
     * This cleans up the event info fragment since the FragmentManager doesn't
     * handle nested fragments. Without this, the action bar buttons added by
     * the info fragment can come back on a rotation.
     * 
     * @param fragmentManager
     */
    public void removeFragments(FragmentManager fragmentManager) {
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mController.deregisterEventHandler(R.id.agenda_event_info);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        if (getActivity().isFinishing()) {
            return;
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        Fragment f = fragmentManager.findFragmentById(R.id.agenda_event_info);
        if (f != null) {
            ft.remove(f);
        }
        ft.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mActivity.unregisterReceiver(mIntentReceiver);
        // HCT_MODIFY lixiange MF3.0 add delete all select event
        mAgendaListView.onPause();
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    @Override
    public void onDestroy() {
        if (actionModeStarted) {
            leaveActionMode();
        }
        getLoaderManager().destroyLoader(0);
        super.onDestroy();
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event

    private void goTo(EventInfo event, boolean animate) {
        if (event.selectedTime != null) {
            mTime.set(event.selectedTime);
        } else if (event.startTime != null) {
            mTime.set(event.startTime);
        }
        if (mAgendaListView == null) {
            // The view hasn't been set yet. Just save the time and use it
            // later.
            return;
        }
        mAgendaListView
                .goTo(mTime,
                        event.id,
                        mQuery,
                        true,
                        ((event.extraLong & CalendarController.EXTRA_GOTO_TODAY) != 0 && mShowEventDetailsWithAgenda) ? true
                                : false);
        AgendaAdapter.ViewHolder vh = mAgendaListView.getSelectedViewHolder();
        // Make sure that on the first time the event info is shown to recreate
        // it
        Log.d(TAG, "selected viewholder is null: " + (vh == null));
        showEventInfo(event, vh != null ? vh.allDay : false, mForceReplace);
        mForceReplace = false;
    }

    private void search(String query, Time time) {
        mQuery = query;
        if (time != null) {
            mTime.set(time);
        }
        if (mAgendaListView == null) {
            // The view hasn't been set yet. Just return.
            return;
        }
        reLoadSlideRange();
        mAgendaListView.goTo(time, -1, mQuery, true, false);
    }

    @Override
    public void eventsChanged() {
        if (mAgendaListView != null) {
            mAgendaListView.refresh(true);
            reLoadSlideRange();
        }
    }

    @Override
    public long getSupportedEventTypes() {
        return EventType.GO_TO | EventType.EVENTS_CHANGED
                | ((mUsedForSearch) ? EventType.SEARCH : 0);
    }

    private long mLastHandledEventId = -1;
    private Time mLastHandledEventTime = null;

    @Override
    public void handleEvent(EventInfo event) {
        if (event.eventType == EventType.GO_TO) {
            // TODO support a range of time
            // TODO support event_id
            // TODO figure out the animate bit
            mLastHandledEventId = event.id;
            mLastHandledEventTime = (event.selectedTime != null) ? event.selectedTime
                    : event.startTime;
            goTo(event, true);
        } else if (event.eventType == EventType.SEARCH) {
            search(event.query, event.startTime);
        } else if (event.eventType == EventType.EVENTS_CHANGED) {
            eventsChanged();
        }
    }

    public long getLastShowEventId() {
        return mLastShownEventId;
    }

    // Shows the selected event in the Agenda view
    private void showEventInfo(EventInfo event, boolean allDay,
            boolean replaceFragment) {

        // Ignore unknown events
        if (event.id == -1) {
            Log.e(TAG, "showEventInfo, event ID = " + event.id);
            return;
        }

        mLastShownEventId = event.id;

        // Create a fragment to show the event to the side of the agenda list
        if (mShowEventDetailsWithAgenda) {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager == null) {
                // Got a goto event before the fragment finished attaching,
                // stash the event and handle it later.
                mOnAttachedInfo = event;
                mOnAttachAllDay = allDay;
                return;
            }
            FragmentTransaction ft = fragmentManager.beginTransaction();

            if (allDay) {
                event.startTime.timezone = Time.TIMEZONE_UTC;
                event.endTime.timezone = Time.TIMEZONE_UTC;
            }

            if (DEBUG) {
                Log.d(TAG, "***");
                Log.d(TAG,
                        "showEventInfo: start: "
                                + new Date(event.startTime.toMillis(true)));
                Log.d(TAG,
                        "showEventInfo: end: "
                                + new Date(event.endTime.toMillis(true)));
                Log.d(TAG, "showEventInfo: all day: " + allDay);
                Log.d(TAG, "***");
            }

            long startMillis = event.startTime.toMillis(true);
            long endMillis = event.endTime.toMillis(true);
            EventInfoFragment fOld = (EventInfoFragment) fragmentManager
                    .findFragmentById(R.id.agenda_event_info);
            if (fOld == null || replaceFragment
                    || fOld.getStartMillis() != startMillis
                    || fOld.getEndMillis() != endMillis
                    || fOld.getEventId() != event.id) {
                mEventFragment = new EventInfoFragment(mActivity, event.id,
                        startMillis, endMillis, Attendees.ATTENDEE_STATUS_NONE,
                        false, EventInfoFragment.DIALOG_WINDOW_STYLE, null);
                ft.replace(R.id.agenda_event_info, mEventFragment);
                mController.registerEventHandler(R.id.agenda_event_info,
                        mEventFragment);
                ft.commit();
            } else {
                fOld.reloadEvents();
            }
        }
    }

    // OnScrollListener implementation to update the date on the pull-down menu
    // of the app

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Save scroll state so that the adapter can stop the scroll when the
        // agenda list is fling state and it needs to set the agenda list to a
        // new position
        if (mAdapter != null) {
            mAdapter.setScrollState(scrollState);
        }
    }

    // Gets the time of the first visible view. If it is a new time, send a
    // message to update
    // the time on the ActionBar
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        int julianDay = mAgendaListView
                .getJulianDayFromPosition(firstVisibleItem
                        - mAgendaListView.getHeaderViewsCount());
        // On error - leave the old view
        if (julianDay == 0) {
            return;
        }
        // If the day changed, update the ActionBar
        if (mJulianDayOnTop != julianDay) {
            mJulianDayOnTop = julianDay;
            Time t = new Time(mTimeZone);
            t.setJulianDay(mJulianDayOnTop);
            mController.setTime(t.toMillis(true));
            // Cannot sent a message that eventually may change the layout of
            // the views
            // so instead post a runnable that will run when the layout is done
            if (!mIsTabletConfig) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        Time t = new Time(mTimeZone);
                        t.setJulianDay(mJulianDayOnTop);
                        mController.sendEvent(this, EventType.UPDATE_TITLE, t,
                                t, null, -1, ViewType.CURRENT, 0, null, null);
                    }
                });
            }
        }
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event
    public void initViews(View v) {
        viewSwitchBar = (View) mActivity.findViewById(R.id.secondary_pane);
        mDeleteProgressDialog = createDeleteProgressDialog();
        mHeaderViewLabel = (TextView) v.findViewById(R.id.agenda_before_label);
        mFooterViewLabel = (TextView) v.findViewById(R.id.agenda_after_label);
    }

    public void doDelete() {
        mDeleteEvents = mAgendaListView.getSelectedEvents();
        if (mDeleteEvents == null || mDeleteEvents.size() == 0) {
            return;
        }
        if (existRepeatingEvent()) {
            View customView = (ViewGroup) LayoutInflater.from(mActivity)
                    .inflate(R.layout.delete_dialog_view, null);
            TextView message = (TextView) customView
                    .findViewById(R.id.delete_message);
            if (mDeleteEvents.size() == 1) {
                message.setText(getString(
                        R.string.delete_events_only_one_massage,
                        mDeleteEvents.size()));
            } else {
                message.setText(getString(R.string.delete_events_massage,
                        mDeleteEvents.size()));
            }
            CheckBoxHCT dCheck = (CheckBoxHCT) customView
                    .findViewById(R.id.delete_all_repeats);
            dCheck.SetColor(getResources()
                    .getColor(R.color.cale_checkbox_color));
            final boolean ischeck = dCheck.isChecked();
            dCheck.setChecked(ischeck);
            if (ischeck) {
                whichDelete = DELETE_ALL;
            } else {
                whichDelete = DELETE_SELECTED;
            }
            dCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                        boolean isChecked) {
                    if (isChecked) {
                        whichDelete = DELETE_ALL;
                    } else {
                        whichDelete = DELETE_SELECTED;
                    }
                }
            });
            // whichDelete = DELETE_ALL;
            // int labelsArrayId = R.array.delete_repeating_labels_in_agenda;
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.delete_label)
                    // .setTitle(getString(R.string.delete_events_massage,
                    // mDeleteEvents.size()))
                    // .setIconAttribute(android.R.attr.alertDialogIcon)
                    // .setSingleChoiceItems(labelsArrayId, DELETE_ALL,
                    // mChoiceDialogListener)
                    .setView(customView)
                    .setPositiveButton(android.R.string.ok,
                            mDeleteDialogListener)
                    .setNegativeButton(android.R.string.cancel, null).show();
        } else {
            new AlertDialog.Builder(mActivity)
                    .setTitle(R.string.delete_label)
                    .setMessage(
                            getString(
                                    mDeleteEvents.size() == 1 ? R.string.delete_events_only_one_massage
                                            : R.string.delete_events_massage,
                                    mDeleteEvents.size()))
                    // .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setPositiveButton(android.R.string.ok,
                            mDeleteDialogListener)
                    .setNegativeButton(android.R.string.cancel, null).show();
        }
    }

    private ProgressDialog createDeleteProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setTitle(R.string.delete_label);
        dialog.setMessage(getString(R.string.delete_dialog_message));
        dialog.setCancelable(false);
        return dialog;
    }

    public boolean existRepeatingEvent() {
        for (Event event : mDeleteEvents) {
            if (event.hasRrule) {
                return true;
            }
        }
        return false;
    }

    public void markAll() {
        mAgendaListView.selectAll();

    }

    public void unMarkAll() {
        mAgendaListView.cancelSelect();
    }

    /***
     * HCT_MODIFY jianghejie to change the selected agenda count and selected
     * state
     */
    @SuppressLint("ResourceAsColor")
    public void updateSelectedCount() {
        Resources resources = mActivity.getResources();
        if (mSelectCount != null) {
            if (mAdapter.getSelectedItemSize() == 0) {
                mSelectCount.setText(R.string.tap_to_select);
                mSelectCount.setTextColor(resources
                        .getColor(R.color.cale_actionbar_text_color));
                action_delete_select.setClickable(false);
                action_delete_select.setTextColor(0x33333333);
            } else {
                mSelectCount
                        .setText(mAdapter.getSelectedItemSize()
                                + resources
                                        .getString(R.string.agenda_select_count_descrip));
                mSelectCount.setTextColor(resources
                        .getColor(R.color.cale_actionbar_text_color));
                action_delete_select.setClickable(true);
                action_delete_select.setTextColor(0xff333333);
            }
        }

        Drawable c = resources.getDrawable(R.drawable.done_all_off);
        c.setTint(resources.getColor(R.color.colorAccount));
        Drawable d = resources.getDrawable(R.drawable.done_all);
        d.setTint(resources.getColor(R.color.cale_actionbar_icon_color));
        if (mSelectionButton != null) {
            mSelectionButton
                    .setImageDrawable((mAgendaListView.isSelectAll()) ? c : d);
        }
    }

    public int getSlideRangeMinJulianDay() {
        return mMinJulianDay;
    }

    public int getSlideRangeMaxJulianDay() {
        return mMaxJulianDay;
    }

    public void updateSlideRange(int start, int end) {
        mCurrStartJulianDay = start;
        mCurrEndJulianDay = end;
        int visible = start > mMinJulianDay ? View.VISIBLE : View.GONE;
        mHeaderViewLabel.setVisibility(visible);

        visible = end < mMaxJulianDay ? View.VISIBLE : View.GONE;
        mFooterViewLabel.setVisibility(visible);
    }

    public boolean isCanSlideDown() {
        return mCurrStartJulianDay > mMinJulianDay;
    }

    public boolean isCanSlideUp() {
        return mCurrEndJulianDay < mMaxJulianDay;
    }

    public void hiddenSlideRange() {
        mHeaderViewLabel.setVisibility(View.GONE);
        mFooterViewLabel.setVisibility(View.GONE);
    }

    public void showNoEventEmptyView(boolean show) {
        int visible = show ? View.VISIBLE : View.GONE;
        mNoEventLayout.setVisibility(visible);
        mAgendaListViewLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void updateHeaderFooter(final String startLabel,
            final String endLabel) {
        mHeaderViewLabel.setText(startLabel);
        mFooterViewLabel.setText(endLabel);
    }

    /*** HCT_MODIFY end */

    private long mDeleteEventCount = 0;
    private long mInsertDeleteEventCount = 0;

    public void deleteEvent() {
        mDeleteEventCount = 0;
        mInsertDeleteEventCount = 0;
        mDeleteProgressDialog.show();
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                int count = mDeleteEvents.size();

                for (int j = 0; j < count; j++) {
                    Event event = mDeleteEvents.get(j);
                    if (whichDelete == DELETE_SELECTED && event.hasRrule) {
                        mInsertDeleteEventCount++;
                    } else {
                        mDeleteEventCount++;
                    }
                }

                for (int i = 0; i < count; i++) {
                    Event event = mDeleteEvents.get(i);
                    if (whichDelete == DELETE_SELECTED && event.hasRrule) {
                        Uri uri = ContentUris.withAppendedId(
                                Events.CONTENT_URI, event.id);
                        mService.startQuery(mService.getNextToken(), event,
                                uri, EditEventHelper.EVENT_PROJECTION, null,
                                null, null);
                    } else {
                        deleteNormalEvent(event.id);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                // mDeleteProgressDialog.hide();
            }
        }.execute();
    }

    public void deleteNormalEvent(long eventId) {
        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        mService.startDelete(mService.getNextToken(), eventId, uri, null, null,
                Utils.UNDO_DELAY);
    }

    public void deleteExceptionEvent(long evnetId) {
        // update a recurrence exception by setting its status to "canceled"
        ContentValues values = new ContentValues();
        values.put(Events.STATUS, Events.STATUS_CANCELED);

        Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, evnetId);
        mService.startUpdate(mService.getNextToken(), null, uri, values, null,
                null, Utils.UNDO_DELAY);
    }

    public void deleteRepeatingEvent(CalendarEventModel mModel, Event event,
            int which) {
        String rRule = mModel.mRrule;
        boolean allDay = mModel.mAllDay;
        long dtstart = mModel.mStart;
        long id = mModel.mId; // mCursor.getInt(mEventIndexId);

        switch (which) {
        case DELETE_SELECTED: {
            // Create a recurrence exception by creating a new event
            // with the status "cancelled".
            ContentValues values = new ContentValues();

            // The title might not be necessary, but it makes it easier
            // to find this entry in the database when there is a problem.
            String title = mModel.mTitle;
            values.put(Events.TITLE, title);

            String timezone = mModel.mTimezone;
            long calendarId = mModel.mCalendarId;
            values.put(Events.EVENT_TIMEZONE, timezone);
            values.put(Events.ALL_DAY, allDay ? 1 : 0);
            values.put(Events.ORIGINAL_ALL_DAY, allDay ? 1 : 0);
            values.put(Events.CALENDAR_ID, calendarId);
            values.put(Events.DTSTART, event.begin);
            values.put(Events.DTEND, event.end);
            values.put(Events.ORIGINAL_SYNC_ID, mModel.mSyncId);
            values.put(Events.ORIGINAL_ID, id);
            values.put(Events.ORIGINAL_INSTANCE_TIME, event.begin);
            values.put(Events.STATUS, Events.STATUS_CANCELED);

            mService.startInsert(mService.getNextToken(), event.id,
                    Events.CONTENT_URI, values, Utils.UNDO_DELAY);
            break;
        }
        case DELETE_ALL: {
            Uri uri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
            mService.startDelete(mService.getNextToken(), null, uri, null,
                    null, Utils.UNDO_DELAY);
            break;
        }
        }
    }

    public void setHideClearAndDeleteMenu(boolean needtohide) {
        Log.i(TAG, "lxg setHideClearAndDeleteMenu needtohide = " + needtohide);
        mShowDelete = !needtohide;

        if (mOptionsMenu != null) {
            refreshClearAndDeleteMenu();
        }
    }

    private void refreshClearAndDeleteMenu() {
        MenuItem eventsDeleteMenu = mOptionsMenu
                .findItem(R.id.action_delete_events);
        Log.i(TAG, "lxg setHideClearAndDeleteMenu mShowDelete = " + mShowDelete);
        if (eventsDeleteMenu == null) {
            return;
        }
        if (!mShowDelete) {
            eventsDeleteMenu.setVisible(false);
            eventsDeleteMenu.setEnabled(false);
        } else {
            eventsDeleteMenu.setVisible(true);
            eventsDeleteMenu.setEnabled(true);
        }
    }

    private void bindActionMenuItem(View view,
            OnClickListener mOnClickListener, int title, int drawable) {
        TextView label = (TextView) view.findViewById(android.R.id.title);
        label.setText(title);
        ImageView icon = (ImageView) view.findViewById(android.R.id.icon);
        icon.setImageResource(drawable);
        view.setOnClickListener(mOnClickListener);
    }

    // HCT_MODIFY lixiange MF3.0 add delete all select event

    public void reLoadSlideRange() {
        if (isAdded()) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    private String buildQuerySelection() {

        if (mHideDeclined) {
            return Calendars.VISIBLE + "=1 AND "
                    + Instances.SELF_ATTENDEE_STATUS + "!="
                    + Attendees.ATTENDEE_STATUS_DECLINED;
        } else {
            return Calendars.VISIBLE + "=1";
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Builder builder = Uri.withAppendedPath(Events.CONTENT_URI,
                "slide_range").buildUpon();
        String q = mQuery;
        if (!TextUtils.isEmpty(q)) {
            builder.appendPath(q);
        }
        return new CursorLoader(mActivity, builder.build(), new String[] {
                "rangeStart", "rangeEnd" }, buildQuerySelection(), null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            long rangeStart = data.getLong(0);
            long rangeEnd = data.getLong(1);
            Time t = new Time();
            t.set(rangeStart);
            mMinJulianDay = t.getJulianDay(t.toMillis(true), t.gmtoff);
            t.set(rangeEnd);
            mMaxJulianDay = t.getJulianDay(t.toMillis(true), t.gmtoff);
            showNoEventEmptyView(false);
        } else {
            mMinJulianDay = Integer.MAX_VALUE;
            mMaxJulianDay = Integer.MIN_VALUE;
            showNoEventEmptyView(true);
        }
        updateSlideRange(mCurrStartJulianDay, mCurrEndJulianDay);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
