package com.android.calendar.event;

import java.util.Calendar;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.calendar.R;

public class EventCardActivity extends Activity implements View.OnClickListener {
    private Button bt;
    private ViewPager pager;
    private MyAdapter adapter;
    private int pagerWidth;
    private ViewGroup rootView;
    Calendar c;

    private OnMonthCallBack mCallBack;

    public interface OnMonthCallBack {
        void currentYear(int year);
    }

    public void setOnYearCallBack(OnMonthCallBack callBack) {
        mCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        getWindow().setNavigationBarColor(getColor(R.color.transparent));
        getWindow().setStatusBarColor(getColor(R.color.transparent));
        setContentView(R.layout.event_card_activity);
        getActionBar().hide();
        int year = getIntent().getIntExtra("card_year", 2017);
        int month = getIntent().getIntExtra("card_month", 8);
        int day = getIntent().getIntExtra("card_day", 1);
        c = java.util.Calendar.getInstance();
        c.set(java.util.Calendar.YEAR, year);
        c.set(java.util.Calendar.MONTH, month - 1);
        c.set(java.util.Calendar.DATE, day);
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);
        pager = (ViewPager) findViewById(R.id.view_pager);
        rootView = (ViewGroup) findViewById(R.id.root_view);
        rootView.setOnClickListener(this);
        pager.setOffscreenPageLimit(3);
        pagerWidth = (int) (getResources().getDisplayMetrics().widthPixels * 4.5f / 5.0f);
        ViewGroup.LayoutParams lp = pager.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(pagerWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        } else {
            lp.width = pagerWidth;
        }
        // lp.width=950;
        lp.width = getPixelsFromDp(325);
        Log.d("gomeliao", "width: " + getPixelsFromDp(325));
        pager.setLayoutParams(lp);
        pager.setPageMargin(getPixelsFromDp(12));
        adapter = new MyAdapter(getFragmentManager());
        pager.setPageTransformer(true, new Transformer(this));
        pager.setAdapter(adapter);
        pager.setCurrentItem(500);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {

                Log.d("liaoliao", "onPageSelected: " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.root_view:
            finish();
            overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
        default:
            break;
        }
    }

    private class MyAdapter extends FragmentStatePagerAdapter {
        public MyAdapter(android.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            long s = c.getTimeInMillis();
            Log.d("liaoliao", "s: " + s);
            long time = s + (long) (position - 500) * 24 * 60 * 60 * 1000;
            // Log.d("liaoliao","position: "+position+"-time: "+time);
            return EventCardFragment.create(time);
        }

        @Override
        public int getCount() {
            return 1000;
        }
    }

    private int getPixelsFromDp(int size) {

        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        return (size * metrics.densityDpi) / DisplayMetrics.DENSITY_DEFAULT;

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit);
    }
}
