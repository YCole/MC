package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.calendar.R;

/**
 * ScheduleEqueryFragment Created by liaozhenbin on 2017/7/5.
 */

public class ScheduleEqueryFragment extends Fragment {
    private EditText searchEditText;
    private List<ScheduleEvents> queryEventsList;
    public Activity mainActivity;

    private ListView queryscheduleListView;
    public ImageView searchDeleteIcon;
    private String titleString = "";
    private RelativeLayout rlNoSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_schedule,
                container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = getActivity();
        searchDeleteIcon = (ImageView) mainActivity
                .findViewById(R.id.iv_search_delete);
        queryscheduleListView = (ListView) view
                .findViewById(R.id.query_schedule_listview);
        searchEditText = (EditText) mainActivity.findViewById(R.id.et_search);
        rlNoSchedule = (RelativeLayout) mainActivity
                .findViewById(R.id.rl_no_schedule);
        queryEventsList = new ArrayList<>();
        searchEditText.setFocusable(true);
        searchEditText.setFocusableInTouchMode(true);
        searchEditText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) searchEditText
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(searchEditText, 0);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int arg1, int arg2,
                    int arg3) {
                titleString = searchEditText.getText().toString();
                if (queryEventsList != null) {
                    queryEventsList.clear();
                }
                if (!titleString.isEmpty()) {
                    searchDeleteIcon.setVisibility(View.VISIBLE);
                    queryCalendarData(titleString);
                } else {
                    searchDeleteIcon.setVisibility(View.INVISIBLE);
                }
                queryscheduleListView.setAdapter(new ScheduleAdapter(
                        mainActivity, queryEventsList));
                if (titleString.length() > 0 && queryEventsList.size() == 0) {
                    rlNoSchedule.setVisibility(View.VISIBLE);
                } else {
                    rlNoSchedule.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                    int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });

        searchDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDeleteIcon.setVisibility(View.INVISIBLE);
                searchEditText.setText("");
            }

        });

        queryscheduleListView
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(mainActivity,
                                ScheduleDetailsActivity.class);
                        // Bundle bundle = new Bundle();
                        // ScheduleEvents event = queryEventsList.get(arg2);
                        // bundle.putSerializable("event", event);
                        // intent.putExtras(bundle);
                        intent.putExtra("id", queryEventsList.get(arg2).getId());
                        mainActivity.startActivity(intent);
                    }
                });
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.e("fushuo", "titleString=" + titleString);
        if (!titleString.isEmpty()) {
            if (null != queryEventsList) {
                queryEventsList.clear();
            }
            searchDeleteIcon.setVisibility(View.VISIBLE);
            queryCalendarData(titleString);
        } else {
            searchDeleteIcon.setVisibility(View.INVISIBLE);
        }
        queryscheduleListView.setAdapter(new ScheduleAdapter(mainActivity,
                queryEventsList));
    }

    public void queryCalendarData(String s) {
        ContentResolver cr = mainActivity.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[] { Manifest.permission.READ_CALENDAR }, 0);
            return;
        }
        String[] selectionArgs = { "%" + s + "%" };
        Cursor cursor = cr.query(uri, null, "title like ?", selectionArgs,
                "dtstart");
        int i = 0;
        String lastDay = null;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String eventLocation = cursor.getString(cursor
                    .getColumnIndex("eventLocation"));
            String description = cursor.getString(cursor
                    .getColumnIndex("description"));
            int eventColor = cursor.getInt(cursor.getColumnIndex("eventColor"));
            long dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
            long dtend = cursor.getLong(cursor.getColumnIndex("dtend"));
            String rrule = cursor.getString(cursor.getColumnIndex("rrule"));
            int allDay = cursor.getInt(cursor.getColumnIndex("allDay"));
            ScheduleEvents events = new ScheduleEvents(id, title,
                    eventLocation, description, eventColor, dtstart, dtend,
                    rrule, allDay);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dtstart);
            String dayString = formatter.format(calendar.getTime());
            if (i == 0) {
                events.setType(ScheduleAdapter.TYPE_TITLE_CONTENT_FIRST);

                lastDay = dayString;
            } else if (dayString != null && dayString.equals(lastDay)) {
                events.setType(ScheduleAdapter.TYPE_CONTENT_MIDDLE);
            } else {
                lastDay = dayString;
                events.setType(ScheduleAdapter.TYPE_TITLE_CONTENT);
            }
            queryEventsList.add(events);
            i++;
        }
    }

}
