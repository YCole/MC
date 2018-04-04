/*
 * Copyright (C) 2011 The Android Open Source Project
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

package com.android.calendar.selectcalendars;

import android.app.ActionBar;
import android.app.ExpandableListActivity;
import android.content.AsyncQueryHandler;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;

import com.android.calendar.R;
import com.android.calendar.Utils;

public class SelectSyncedCalendarsMultiAccountActivity extends
        ExpandableListActivity implements View.OnClickListener {
    private static final String TAG = "Calendar";
    private static final String EXPANDED_KEY = "is_expanded";
    private static final String ACCOUNT_UNIQUE_KEY = "ACCOUNT_KEY";
    private MatrixCursor mAccountsCursor = null;
    private ExpandableListView mList;
    private SelectSyncedCalendarsMultiAccountAdapter mAdapter;
    private static final String[] PROJECTION = new String[] {
            Calendars._ID,
            Calendars.ACCOUNT_TYPE,
            Calendars.ACCOUNT_NAME,
            Calendars.ACCOUNT_TYPE + " || " + Calendars.ACCOUNT_NAME + " AS "
                    + ACCOUNT_UNIQUE_KEY, };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.select_calendars_multi_accounts_fragment);
        mList = getExpandableListView();
        mList.setGroupIndicator(null);
        mList.setEmptyView(findViewById(R.id.sync_calendars_noEventLayout));
        ImageView noeventimage = (ImageView) this
                .findViewById(R.id.noEventView);
        Drawable noeventdrawable = (Drawable) this.getResources().getDrawable(
                R.drawable.event_note);
        noeventdrawable.setTint(this.getResources().getColor(
                R.color.no_event_color));
        noeventimage.setImageDrawable(noeventdrawable);
        // Start a background sync to get the list of calendars from the server.
        Utils.startCalendarMetafeedSync(null);

        findViewById(R.id.btn_done).setOnClickListener(this);
        findViewById(R.id.btn_discard).setOnClickListener(this);
        mList.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_done) {
            if (mAdapter != null) {
                mAdapter.doSaveAction();
            }
            finish();
        } else if (view.getId() == R.id.btn_discard) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            mAdapter.startRefreshStopDelay();
        }
        new AsyncQueryHandler(getContentResolver()) {
            @Override
            protected void onQueryComplete(int token, Object cookie,
                    Cursor cursor) {
                mAccountsCursor = Utils.matrixCursorFromCursor(cursor);
                if (mAccountsCursor == null) {
                    Log.d(TAG,
                            "Got a closed or null cursor from onLoadComplete 5");
                    return;
                }

                mAdapter = new SelectSyncedCalendarsMultiAccountAdapter(
                        findViewById(R.id.calendars).getContext(),
                        mAccountsCursor,
                        SelectSyncedCalendarsMultiAccountActivity.this);
                mList.setAdapter(mAdapter);
                mList.setGroupIndicator(null);

                // TODO initialize from sharepref
                int count = mList.getCount();
                for (int i = 0; i < count; i++) {
                    mList.expandGroup(i);
                }
            }
        }.startQuery(0, null, Calendars.CONTENT_URI, PROJECTION, "1) and "
                + Calendars.ACCOUNT_TYPE + " not in ('"
                + CalendarContract.ACCOUNT_TYPE_LOCAL
                + "','My calendar') GROUP BY (" + ACCOUNT_UNIQUE_KEY, // Cheap
                                                                      // hack to
                                                                      // make
                                                                      // WHERE a
                                                                      // GROUP
                                                                      // BY
                                                                      // query
                null /* selectionArgs */, Calendars.ACCOUNT_NAME /* sort order */);
        // TODO change to something that supports group by queries.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAdapter != null) {
            mAdapter.cancelRefreshStopDelay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.closeChildrenCursors();
        }
        if (mAccountsCursor != null && !mAccountsCursor.isClosed()) {
            mAccountsCursor.close();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        boolean[] isExpanded;
        mList = getExpandableListView();
        mList.setGroupIndicator(null);
        if (mList != null) {
            int count = mList.getCount();
            isExpanded = new boolean[count];
            for (int i = 0; i < count; i++) {
                isExpanded[i] = mList.isGroupExpanded(i);
            }
        } else {
            isExpanded = null;
        }
        mList.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        outState.putBooleanArray(EXPANDED_KEY, isExpanded);
        // TODO Store this to preferences instead so it remains on restart
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        mList = getExpandableListView();
        mList.setGroupIndicator(null);
        boolean[] isExpanded = state.getBooleanArray(EXPANDED_KEY);
        mList.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        if (mList != null && isExpanded != null
                && mList.getCount() >= isExpanded.length) {
            for (int i = 0; i < isExpanded.length; i++) {
                if (isExpanded[i] && !mList.isGroupExpanded(i)) {
                    mList.expandGroup(i);
                } else if (!isExpanded[i] && mList.isGroupExpanded(i)) {
                    mList.collapseGroup(i);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,
                ActionBar.DISPLAY_HOME_AS_UP);
        getActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(
                        R.color.cale_actionbar_background_color)));
        getWindow().setStatusBarColor(
                getResources()
                        .getColor(R.color.cale_actionbar_background_color));
        getActionBar().setDisplayShowHomeEnabled(false);
        // getActionBar().setElevation(16);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
