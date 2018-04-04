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

package com.android.calendar.month;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.calendar.R;
import com.android.calendar.Utils;
import com.hct.calendar.utils.CalendarUtil;

public class HolidayAndTermActivity extends FragmentActivity implements
        OnClickListener { // implements
    // ActionBar.TabListener
    private static final String TAG = "HolidayActivity";

    private static final String BUNDLE_KEY_RESTORE_VIEW = "key_restore_view";
    private static final String BUNDLE_KEY_RESTORE_TIME = "key_restore_time";

    private ActionBar mActionBar;
    private int viewType = 0;
    private long timeMillis = -1;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    // private PagerSecond mPagerSecond;
    private String[] addresses = new String[2];
    // private float mScale;
    // private int HOLIDAY_AND_TERM_TITLE_SIZE = 13;

    private static final int YEAR_RANGE = 68;
    private Time mTime = new Time();
    private int selected;
    private Time nowTime = new Time();

    private ImageView mBackIcon;

    private Button mBtnFestival;

    private Button mBtnTerms;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (icicle != null) {
            viewType = icicle.getInt(BUNDLE_KEY_RESTORE_VIEW, 0);
            timeMillis = icicle.getLong(BUNDLE_KEY_RESTORE_TIME, -1);
        }
        addresses[0] = this.getResources().getString(R.string.holiday_view);
        addresses[1] = this.getResources().getString(R.string.term_view);

        setContentView(R.layout.holiday_and_term_layout);
        configureActionBar(viewType, timeMillis);
        mActionBar.hide();
    }

    private void configureActionBar(int viewType, long timeMillis) {
        mActionBar = getActionBar();
        if (mActionBar == null) {
            Log.w(TAG, "ActionBar is null.");
        } else {
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_CUSTOM);
            mActionBar.setDisplayShowHomeEnabled(false);
            getWindow().setStatusBarColor(
                    getResources().getColor(
                            R.color.cale_actionbar_background_color));
            getWindow().setBackgroundDrawable(null);
            setContentView(R.layout.activity_holiday_and_term);
            mBackIcon = (ImageView) findViewById(R.id.back_icon);
            // mPagerSecond = (PagerSecond) findViewById(R.id.tabs);
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(),
                    viewType, timeMillis);
            mViewPager.setAdapter(mPagerAdapter);
            mBtnFestival = (Button) findViewById(R.id.bt_festival);
            mBtnTerms = (Button) findViewById(R.id.bt_terms);
            mBtnFestival.setOnClickListener(this);
            mBtnTerms.setOnClickListener(this);
            mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                    if (position == 0)
                        mBtnFestival.callOnClick();
                    else if (position == 1)
                        mBtnTerms.callOnClick();
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });
            mBtnFestival.callOnClick();
            // mPagerSecond.setViewPager(mViewPager);
            // mPagerSecond.setTextColor(getResources().getColor(R.color.cale_tab_noselect_text_color),
            // getResources().getColor(R.color.cale_tab_select_text_color));
            // mPagerSecond.setTextColor(Color.RED, Color.BLUE);
            // mScale = getResources().getDisplayMetrics().density;
            // mPagerSecond.setBackgroundColor(getResources().getColor(R.color.cale_actionbar_background_color));
            // mPagerSecond.setIndicatorColor(getResources().getColor(R.color.cale_actionbar_tab_color));
            // mPagerSecond.setTextSize(HOLIDAY_AND_TERM_TITLE_SIZE);
            // HOLIDAY_AND_TERM_TITLE_SIZE = (int)
            // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            // HOLIDAY_AND_TERM_TITLE_SIZE, getResources().getDisplayMetrics());
            // int count = mPagerSecond.getChildCount();
            // for (int i = 0; i != count; i++) {
            // final int index = i;
            // mPagerSecond.getChildAt(i).setOnClickListener(new
            // OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // mPagerSecond.setCurrentTab(index);
            // mViewPager.setCurrentItem(index);
            //
            // }
            // });
            // }
            //
            // mPagerSecond.setOnPageChangeListener(new OnPageChangeListener() {
            //
            // @Override
            // public void onPageSelected(int arg0) {
            // mPagerSecond.setCurrentTab(arg0);
            // }
            //
            // @Override
            // public void onPageScrolled(int arg0, float arg1, int arg2) {
            //
            // }
            //
            // @Override
            // public void onPageScrollStateChanged(int arg0) {
            //
            // }
            // });
            mBackIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
        }
        initActionBarSpinner();
    }

    private void initActionBarSpinner() {
        ViewGroup v = (ViewGroup) LayoutInflater.from(this).inflate(
                R.layout.holiday_and_term_list, null);
        getActionBar().setCustomView(v);
        Spinner spinner = (Spinner) v.findViewById(R.id.spinner);
        Drawable spinnerimage = getDrawable(R.drawable.spinner_mtrl_am_alpha);
        spinnerimage.setTint(getResources().getColor(
                R.color.cale_actionbar_spinner_color));
        spinner.setBackground(spinnerimage);

        ArrayList<HashMap<String, Object>> ListItem = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < YEAR_RANGE; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemText", String.valueOf(1970 + i));
            ListItem.add(map);
        }
        MySimpleAdapter saImageItems = new MySimpleAdapter(this, ListItem,
                R.layout.holiday_year_item, new String[] { "ItemText" },
                new int[] { R.id.ItemText });
        spinner.setAdapter(saImageItems);
        mTime.setToNow();
        selected = mTime.year - 1970;
        ;
        spinner.setSelection(selected);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                for (int i = 0; i < mSpinnerSelectListeners.size(); i++) {
                    mSpinnerSelectListeners.get(i).onItemSelected(parent, view,
                            position, id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

    }

    public class MySimpleAdapter extends SimpleAdapter {
        private LayoutInflater mInflater;

        public MySimpleAdapter(Context context,
                List<HashMap<String, Object>> data, int resource,
                String[] from, int[] to) {
            super(context, data, resource, from, to);
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.holiday_year_item,
                        null);
                holder = new ViewHolder();
                holder.text = (TextView) convertView
                        .findViewById(R.id.ItemText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text.setText(String.valueOf(1970 + position));
            holder.text.setTextColor(HolidayAndTermActivity.this.getResources()
                    .getColor(R.color.cale_actionbar_text_color));
            /*
             * if (position == selected) { //
             * holder.text.setBackgroundResource(R.drawable.calendar_focus);
             * //holder.text.setBackgroundResource(R.drawable.holiday_year_focus
             * ); //holder.text.setTextColor(mContext.getResources().getColor(R.
             * color.text_color));
             * holder.text.setBackgroundColor(Color.argb(255, 255, 255, 255)); }
             * else { //holder.text.setBackgroundColor(Color.argb(0, 0, 0, 0));
             * holder.text.setTextColor(Color.argb(255, 255, 255, 255)); }
             */
            return convertView;
        }

        class ViewHolder {
            TextView text;
        }

    }

    private List<OnItemSelectedListener> mSpinnerSelectListeners = new ArrayList<AdapterView.OnItemSelectedListener>();

    public void registerSpinnerSelectListener(OnItemSelectedListener l) {
        if (mSpinnerSelectListeners.contains(l)) {
            return;
        }
        mSpinnerSelectListeners.add(l);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm, int viewType, long timeMillis) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return addresses[position];
        }

        @Override
        public Fragment getItem(int position) {
            int viewType = 0;
            long timeMillis = -1;
            if (position == 0) {
                viewType = 0;
            } else if (position == 1) {
                viewType = 1;
            }
            HolidayAndTermFragment f = new HolidayAndTermFragment(viewType,
                    timeMillis);
            Bundle b = new Bundle();
            b.putInt(HolidayAndTermFragment.HOLIDAY_AND_TERM_VIEW, position);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getCount() {
            return addresses.length;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.bt_festival:
            if (!mBtnFestival.isSelected()) {
                mBtnTerms.setSelected(false);
                mBtnFestival.setSelected(true);
                mViewPager.setCurrentItem(0);
            }
            break;

        case R.id.bt_terms:
            if (!mBtnTerms.isSelected()) {
                mBtnFestival.setSelected(false);
                mBtnTerms.setSelected(true);
                mViewPager.setCurrentItem(1);
            }
            break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CalendarUtil.isEnglish())
            finish();
    }
}
