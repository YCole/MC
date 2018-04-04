package com.android.calendar.view;

import java.util.Calendar;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.android.calendar.CalendarController;
import com.android.calendar.CalendarController.EventType;
import com.android.calendar.CalendarController.ViewType;
import com.android.calendar.R;
import com.android.calendar.utils.YearUtils;
import com.hct.calendar.month.MonthByWeekAdapter;

/**
 * Year View
 * 
 * Created by liaozhenbin on 2017/6/19
 */
public class YearViewFragment extends Fragment {
    private View view;
    private GridView gridView;
    private int mPageNumber;
    public static final String ARG_PAGE = "page";
    Calendar calendar = Calendar.getInstance();
    private int mCurrYear = calendar.get(Calendar.YEAR);
    // liao
    private CalendarController mController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.month_grid_fragment, container, false);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridView.setAdapter(new GridAdapter(view.getContext()));
        gridView.setHorizontalSpacing(30);
        // gridView.setVerticalSpacing(10);
        initDate();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Log.d("liao", mCurrYear + "-" + (position + 1));
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, mCurrYear);
                calendar.set(Calendar.MONTH, position);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                Time time = new Time();
                time.set(calendar.getTimeInMillis());
                MonthByWeekAdapter.setTime(time);
                if (mCurrYear <= 2037) {
                    mController.sendEvent(this, EventType.GO_TO, time, null,
                            -1, ViewType.MONTH);
                    mController.setMonthViewVisible();
                } else {
                    Toast.makeText(
                            getActivity(),
                            getActivity().getResources().getString(
                                    R.string.jump_to_long_year),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        Calendar calendar = YearUtils.getSelectCalendar(mPageNumber);
        mCurrYear = calendar.get(Calendar.YEAR);
        mController = CalendarController.getInstance(getActivity());
        Log.d("liao", mCurrYear + "-" + mPageNumber + "!");
    }

    private void initDate() {

    }

    class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 12;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.month_item_view, null);
                viewHolder.data = (MonthView) convertView
                        .findViewById(R.id.my_data);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.data.setSelectYearMonth(mCurrYear, position, 0);
            return convertView;
        }

        public class ViewHolder {
            public MonthView data;
        }
    }

    public static YearViewFragment create(int pageNumber) {
        YearViewFragment fragment = new YearViewFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }
}