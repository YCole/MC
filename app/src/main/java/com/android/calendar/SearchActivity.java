/*
 * Copyright (C) 2010 The Android Open Source Project
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
package com.android.calendar;

import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract.Events;
import android.provider.SearchRecentSuggestions;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.calendar.CalendarController.EventInfo;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.agenda.AgendaFragment;
import com.hct.gios.widget.ActivityHCT;

public class SearchActivity extends ActivityHCT implements
        CalendarController.EventHandler {

    private static final String TAG = SearchActivity.class.getSimpleName();

    private static final boolean DEBUG = false;

    private static final int HANDLER_KEY = 0;

    protected static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";

    protected static final String BUNDLE_KEY_RESTORE_SEARCH_QUERY = "key_restore_search_query";

    // display event details to the side of the event list
    private boolean mShowEventDetailsWithAgenda;
    private static boolean mIsMultipane;

    private CalendarController mController;

    private EventInfoFragment mEventInfoFragment;

    private long mCurrentEventId = -1;

    private String mQuery;

    // private SearchView mSearchView;

    private DeleteEventHelper mDeleteEventHelper;

    private Handler mHandler;
    private BroadcastReceiver mTimeChangesReceiver;
    private ContentResolver mContentResolver;

    private InputMethodManager inputManager;

    private boolean mhiddenMenu = true;

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            eventsChanged();
        }
    };

    // runs when a timezone was changed and updates the today icon
    private final Runnable mTimeChangesUpdater = new Runnable() {
        @Override
        public void run() {
            Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater,
                    Utils.getTimeZone(SearchActivity.this, mTimeChangesUpdater));
            SearchActivity.this.invalidateOptionsMenu();
        }
    };

    private ViewGroup titleLayout;

    private EditText etSearch;

    private TextView tvHint;

    private View searchIcon;

    public Activity get() {
        return this;
    }

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // This needs to be created before setContentView
        Log.d(TAG, "lxg searchActivity");
        mController = CalendarController.getInstance(this);
        mHandler = new Handler();

        mIsMultipane = Utils.getConfigBool(this, R.bool.multiple_pane_config);
        mShowEventDetailsWithAgenda = Utils.getConfigBool(this,
                R.bool.show_event_details_with_agenda);

        setContentView(R.layout.search);

        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        mContentResolver = getContentResolver();

        if (mIsMultipane) {
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                    ActionBar.DISPLAY_SHOW_HOME);
        } else {
            getActionBar().setDisplayOptions(0,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME);
        }
        setIndicatorColor(getResources().getColor(
                R.color.cale_actionbar_background_color));
        setActionBarContentColor(
                getResources().getColor(R.color.cale_actionbar_icon_color),
                getResources().getColor(R.color.cale_actionbar_text_color));
        getActionBar().setDisplayHomeAsUpEnabled(false);
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setDisplayUseLogoEnabled(false);
        // getActionBar().setDisplayShowTitleEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(false);
        getActionBar().setTitle("");

        getActionBar().hide();
        titleLayout = (ViewGroup) findViewById(R.id.search_title_layout);
        titleLayout.addView(
                LayoutInflater.from(get()).inflate(R.layout.search_bar2,
                        titleLayout, false), new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
        etSearch = (EditText) findViewById(R.id.et_search);
        searchIcon = findViewById(R.id.search_icon);
        tvHint = (TextView) findViewById(R.id.tv_search_hint);
        // back menu !
        findViewById(R.id.search_back_btn).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.returnToCalendarHome(get());
                    }
                });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence newText, int start,
                    int before, int count) {
                String query = newText.toString();
                if (TextUtils.isEmpty(query)) {
                    tvHint.setVisibility(View.VISIBLE);
                    searchIcon.setVisibility(View.VISIBLE);
                } else {
                    tvHint.setVisibility(View.GONE);
                    searchIcon.setVisibility(View.GONE);
                }
                mQuery = query;
                mController.sendEvent(this, EventType.SEARCH, null, null, -1,
                        ViewType.CURRENT, 0, mQuery, getComponentName());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                // ignore
            }
        });
        // Must be the first to register because this activity can modify the
        // list of event handlers in it's handle method. This affects who the
        // rest of the handlers the controller dispatches to are.
        mController.registerEventHandler(HANDLER_KEY, this);

        mDeleteEventHelper = new DeleteEventHelper(this, this, false /*
                                                                      * don't
                                                                      * exit
                                                                      * when
                                                                      * done
                                                                      */);

        long millis = 0;
        if (icicle != null) {
            // Returns 0 if key not found
            millis = icicle.getLong(BUNDLE_KEY_RESTORE_TIME);
            if (DEBUG) {
                Log.v(TAG, "Restore value from icicle: " + millis);
            }
        }
        if (millis == 0) {
            // Didn't find a time in the bundle, look in intent or current time
            millis = Utils.timeFromIntentInMillis(getIntent());
        }

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query;
            if (icicle != null
                    && icicle.containsKey(BUNDLE_KEY_RESTORE_SEARCH_QUERY)) {
                query = icicle.getString(BUNDLE_KEY_RESTORE_SEARCH_QUERY);
            } else {
                query = intent.getStringExtra(SearchManager.QUERY);
            }
            if ("TARDIS".equalsIgnoreCase(query)) {
                Utils.tardis();
            }
            initFragments(millis, query);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mController.deregisterAllEventHandlers();
        CalendarController.removeInstance(this);
    }

    private void initFragments(long timeMillis, String query) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        AgendaFragment searchResultsFragment = new AgendaFragment(timeMillis,
                true);
        ft.replace(R.id.search_results, searchResultsFragment);
        mController.registerEventHandler(R.id.search_results,
                searchResultsFragment);

        ft.commit();
        Time t = new Time();
        t.set(timeMillis);
        search(query, t);
    }

    private void showEventInfo(EventInfo event) {
        if (mShowEventDetailsWithAgenda) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            mEventInfoFragment = new EventInfoFragment(this, event.id,
                    event.startTime.toMillis(false),
                    event.endTime.toMillis(false), event.getResponse(), false,
                    EventInfoFragment.DIALOG_WINDOW_STYLE, null /*
                                                                 * No reminders
                                                                 * to explicitly
                                                                 * pass in.
                                                                 */);
            ft.replace(R.id.agenda_event_info, mEventInfoFragment);
            ft.commit();
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri eventUri = ContentUris.withAppendedId(Events.CONTENT_URI,
                    event.id);
            intent.setData(eventUri);
            intent.setClass(this, EventInfoActivity.class);
            intent.putExtra(EXTRA_EVENT_BEGIN_TIME,
                    event.startTime != null ? event.startTime.toMillis(true)
                            : -1);
            intent.putExtra(EXTRA_EVENT_END_TIME,
                    event.endTime != null ? event.endTime.toMillis(true) : -1);
            startActivity(intent);
        }
        mCurrentEventId = event.id;
    }

    private void search(String searchQuery, Time goToTime) {
        // save query in recent queries
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                Utils.getSearchAuthority(this),
                CalendarRecentSuggestionsProvider.MODE);
        suggestions.saveRecentQuery(searchQuery, null);

        EventInfo searchEventInfo = new EventInfo();
        searchEventInfo.eventType = EventType.SEARCH;
        searchEventInfo.query = searchQuery;
        searchEventInfo.viewType = ViewType.AGENDA;
        if (goToTime == null) {
            goToTime = new Time();
            goToTime.setToNow();
        }
        searchEventInfo.startTime = goToTime;
        mController.sendEvent(this, searchEventInfo);
        mQuery = searchQuery;
        if (etSearch != null && mQuery != null) {
            etSearch.setText(mQuery);
            etSearch.setSelection(mQuery.length());
            etSearch.clearFocus();
        }
    }

    private void deleteEvent(long eventId, long startMillis, long endMillis) {
        mDeleteEventHelper.delete(startMillis, endMillis, eventId, -1);
        if (mIsMultipane && mEventInfoFragment != null
                && eventId == mCurrentEventId) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(mEventInfoFragment);
            ft.commit();
            mEventInfoFragment = null;
            mCurrentEventId = -1;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // From the Android Dev Guide: "It's important to note that when
        // onNewIntent(Intent) is called, the Activity has not been restarted,
        // so the getIntent() method will still return the Intent that was first
        // received with onCreate(). This is why setIntent(Intent) is called
        // inside onNewIntent(Intent) (just in case you call getIntent() at a
        // later time)."
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            search(query, null);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_KEY_RESTORE_TIME, mController.getTime());
        outState.putString(BUNDLE_KEY_RESTORE_SEARCH_QUERY, mQuery);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.setMidnightUpdater(mHandler, mTimeChangesUpdater,
                Utils.getTimeZone(this, mTimeChangesUpdater));
        // Make sure the today icon is up to date
        // invalidateOptionsMenu();
        mTimeChangesReceiver = Utils.setTimeChangesReceiver(this,
                mTimeChangesUpdater);
        mContentResolver.registerContentObserver(Events.CONTENT_URI, true,
                mObserver);
        // We call this in case the user changed the time zone
        eventsChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.resetMidnightUpdater(mHandler, mTimeChangesUpdater);
        Utils.clearTimeChangesReceiver(this, mTimeChangesReceiver);
        mContentResolver.unregisterContentObserver(mObserver);
    }

    @Override
    public void eventsChanged() {
        mController.sendEvent(this, EventType.EVENTS_CHANGED, null, null, -1,
                ViewType.CURRENT);
    }

    @Override
    public long getSupportedEventTypes() {
        return EventType.VIEW_EVENT | EventType.DELETE_EVENT;
    }

    @Override
    public void handleEvent(EventInfo event) {
        long endTime = (event.endTime == null) ? -1 : event.endTime
                .toMillis(false);
        if (event.eventType == EventType.VIEW_EVENT) {
            showEventInfo(event);
        } else if (event.eventType == EventType.DELETE_EVENT) {
            deleteEvent(event.id, event.startTime.toMillis(false), endTime);
        }
    }
}
