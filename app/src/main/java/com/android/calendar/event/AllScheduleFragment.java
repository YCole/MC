package com.android.calendar.event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.calendar.R;
import com.android.calendar.event.ScheduleAdapter.OnShowItemClickListener;
import com.android.calendar.utils.PermissionUtils;
import com.android.calendar.utils.PermissionUtils.OnPermissionListener;

import android.Manifest;
import android.R.integer;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * AllScheduleFragment Created by liaozhenbin on 2017/7/5.
 */

public class AllScheduleFragment extends Fragment implements OnPermissionListener {
    private ListView scheduleListView;
    private List<ScheduleEvents> eventsList;
    private ScheduleActivity mainActivity;
    private static final int PERMISSIONS_REQUEST = 1000;
    // fushuo begin
    long firstClick;
    long secondClick;
    private static boolean isShowTop;
    private static boolean isShowAll;

    String[] permissions = { Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR,

    };

    private ScheduleAdapter mAdapter;

    public ScheduleAdapter getmAdapter() {
        return mAdapter;
    }

    private List<ScheduleEvents> selectedList;

    private TextView tvSelectNum;
    private TextView tvSelect;
    private ImageView ivSelectBackIcon;
    private boolean isDeleteStatus;
    private RelativeLayout rlNoSchedule;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        initPermission();
        firstClick = System.currentTimeMillis();
        return view;
    }

    private void initPermission() {
        // TODO Auto-generated method stub
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermissions(getContext(), PERMISSIONS_REQUEST, permissions, this);
        }

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mainActivity = (ScheduleActivity) getActivity();
        eventsList = new ArrayList<>();
        selectedList = new ArrayList<>();
        findAllView(view);
        getCalendarData();
        mAdapter = new ScheduleAdapter(getActivity(), eventsList);
        scheduleListView.setAdapter(mAdapter);

        super.onViewCreated(view, savedInstanceState);
        mAdapter.setOnShowItemClickListener(new OnShowItemClickListener() {
            @Override
            public void onShowItemClick(ScheduleEvents event) {
                // TODO Auto-generated method stub
                if (event.isChecked() && !selectedList.contains(event)) {
                    selectedList.add(event);
                } else if (!event.isChecked() && selectedList.contains(event)) {
                    selectedList.remove(event);
                }
                if (selectedList.size() == (eventsList.size() - 1)) {
                    isShowAll = true;
                    tvSelect.setText(getContext().getString(R.string.event_cancel_select_all));
                } else {
                    isShowAll = false;
                    tvSelect.setText(getContext().getString(R.string.event_select_all));
                }
                tvSelectNum.setText(getContext().getString(R.string.event_selected) + selectedList.size()
                        + getContext().getString(R.string.event_xiang));
                if (selectedList.size() == 0) {
                    Log.d("liaozhenbin", "size: " + selectedList.size());
                    mainActivity.setDelete(false);
                } else {
                    mainActivity.setDelete(true);
                    Log.d("liaozhenbin", "size: " + selectedList.size());
                }
            }
        });
    }

    private void findAllView(View view) {
        tvSelectNum = (TextView) mainActivity.findViewById(R.id.event_select_num);
        tvSelect = (TextView) mainActivity.findViewById(R.id.event_select_finish);
        scheduleListView = (ListView) view.findViewById(R.id.schedule_listview);
        ivSelectBackIcon = (ImageView) mainActivity.findViewById(R.id.event_select_cancel);
        rlNoSchedule = (RelativeLayout) mainActivity.findViewById(R.id.rl_no_schedule);

        scheduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                secondClick = System.currentTimeMillis();
                // Bundle bundle = new Bundle();
                // ScheduleEvents event = eventsList.get(arg2);
                // bundle.putSerializable("event", event);
                // intent.putExtras(bundle);
                if (!isDeleteStatus && secondClick - firstClick > 500) {
                    Intent intent = new Intent(getActivity(), ScheduleDetailsActivity.class);
                    int id = eventsList.get(arg2).getId();
                    if (id > 0) {
                        intent.putExtra("id", id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getActivity().startActivityForResult(intent, 1);
                    }
                }
                firstClick = secondClick;
            }
        });

        scheduleListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                mainActivity.setTitleStatus(ScheduleActivity.TITLE_DETELE);
                scheduleListView.setLongClickable(false);
                for (ScheduleEvents event : eventsList) {
                    event.setShow(true);
                }
                ScheduleEvents event = eventsList.get(position);
                event.setChecked(true);
                selectedList.add(event);
                mAdapter.notifyDataSetChanged();
                // TODO Auto-generated method stub
                // if () {
                // return false;
                // } else {
                // isShowTop = true;
                // for (ScheduleEvents event : eventsList) {
                // event.setShow(true);
                // }
                // ScheduleEvents event = eventsList.get(position);
                // event.setChecked(true);
                // selectedList.add(event);
                // mAdapter.notifyDataSetChanged();
                // // showOpervate();
                // scheduleListView.setLongClickable(false);
                // }

                return true;
            }
        });

        tvSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mainActivity.setDelete(!isShowAll);
                if (!isShowAll) {
                    isShowAll = true;
                    tvSelect.setText(getContext().getString(R.string.event_cancel_select_all));
                    for (int location = 0; location < eventsList.size() - 1; location++) {
                        ScheduleEvents bean = eventsList.get(location);
                        if (!bean.isChecked()) {
                            bean.setChecked(true);
                            if (!selectedList.contains(bean)) {
                                selectedList.add(bean);
                            }
                        }
                    }
                } else {
                    isShowAll = false;
                    tvSelect.setText(getContext().getString(R.string.event_select_all));
                    selectedList.clear();
                    tvSelectNum.setText(getContext().getString(R.string.event_selected) + "0"
                            + getContext().getString(R.string.event_xiang));
                    for (ScheduleEvents bean : eventsList) {
                        bean.setChecked(false);
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });

        ivSelectBackIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanDelete();
                scheduleListView.setLongClickable(true);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("test_1", "destroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("test_1", "destroy()");
    }

    public void getCalendarData() {
        eventsList.clear();
        ContentResolver cr = getActivity().getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        if (ActivityCompat.checkSelfPermission(mainActivity,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            // ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[]
            // permissions,
            // int[] grantResults)
            // to handle the case where the user grants the permission. See the
            // documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Cursor cursor = cr.query(uri, null, null, null, "dtstart");
        int i = 0;
        String lastDay = null;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String eventLocation = cursor.getString(cursor.getColumnIndex("eventLocation"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            int eventColor = cursor.getInt(cursor.getColumnIndex("eventColor"));
            long dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
            long dtend = cursor.getLong(cursor.getColumnIndex("dtend"));
            String rrule = cursor.getString(cursor.getColumnIndex("rrule"));
            int allDay = cursor.getInt(cursor.getColumnIndex("allDay"));
            ScheduleEvents events = new ScheduleEvents(id, title, eventLocation, description, eventColor, dtstart,
                    dtend, rrule, allDay);
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
            eventsList.add(events);
            i++;
        }
        ScheduleEvents s = new ScheduleEvents(-1, "", "", "", 0, 0, 0, "", 0);
        s.setType(ScheduleAdapter.TYPE_NULL);
        eventsList.add(s);

        if (null != eventsList && eventsList.size() > 0) {
            if (eventsList.size() == 1) {
                int id = eventsList.get(0).id;
                if (id == -1) {
                    rlNoSchedule.setVisibility(View.VISIBLE);
                } else {
                    rlNoSchedule.setVisibility(View.GONE);
                }
            } else {
                rlNoSchedule.setVisibility(View.GONE);
            }

        } else {
            rlNoSchedule.setVisibility(View.VISIBLE);
        }

    }

    public List<ScheduleEvents> getScheduleList() {
        return eventsList;
    }

    public List<ScheduleEvents> getSelectedList() {
        return selectedList;
    }

    /**
     * renovate
     */
    public void renovateData() {
        getCalendarData();
        mAdapter.notifyDataSetChanged();

    }

    public void cleanDelete() {
        selectedList.clear();
        for (ScheduleEvents bean : eventsList) {
            bean.setChecked(false);
            bean.setShow(false);
        }
        mAdapter.notifyDataSetChanged();
        tvSelect.setText(getContext().getString(R.string.event_select_all));
        mainActivity.setTitleStatus(ScheduleActivity.TITLE_INDEX);
        isShowAll = false;
    }

    public void setListLongClick() {
        if (scheduleListView != null)
            scheduleListView.setLongClickable(true);
        if (selectedList != null)
            selectedList.clear();
    }

    @Override
    public void onPermissionGranted() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPermissionDenied(String[] deniedPermissions) {
        // TODO Auto-generated method stub

    }

    public boolean isDeleteStatus() {
        return isDeleteStatus;
    }

    public void setDeleteStatus(boolean isDeleteStatus) {
        this.isDeleteStatus = isDeleteStatus;
    }

}
