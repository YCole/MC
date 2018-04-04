package com.android.calendar.view;

import java.util.Calendar;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.calendar.AllInOneActivity;
import com.android.calendar.R;
import com.android.calendar.utils.YearUtils;

/**
 * MyFragment
 * 
 * Created by liaozhenbin on 2017/6/19
 */
public class YearPagerFragment extends Fragment {
    private ViewPager viewPager;
    private View view;

    // public static OnYearCallBack mCallBack;

    // public static interface OnYearCallBack {
    // void currentYear(int year);
    // }

    // public void setOnYearCallBack(OnYearCallBack callBack) {
    // mCallBack = callBack;
    // }

    private int yearPosition;

    public int getYearPosition() {
        return yearPosition;
    }

    public void setYearPosition(int yearPosition) {
        this.yearPosition = yearPosition;
    }

    public YearPagerFragment() {

    }

    // public YearPagerFragment(int year){
    // super();
    // yearPosition = year;
    // }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.yearpager_fragment, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        /* modify for GMOS-8405 by Yusong.Liang on 2017.9.25:start */
        // final ScreenSlidePagerAdapter screenSlidePagerAdapter = new
        // ScreenSlidePagerAdapter(getFragmentManager());
        final ScreenSlidePagerAdapter screenSlidePagerAdapter = new ScreenSlidePagerAdapter(
                getChildFragmentManager());
        /* modify for GMOS-8405 by Yusong.Liang on 2017.9.25:end */

        viewPager.setAdapter(screenSlidePagerAdapter);
        viewPager.setCurrentItem(yearPosition);
        Log.d("gomeliao", "yearPosition: " + yearPosition);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Calendar calendar = YearUtils.getSelectCalendar(position);
                // if (mCallBack != null) {
                // mCallBack.currentYear(calendar.get(Calendar.YEAR));
                // }
                // Log.d("gomeliao", "onPageSelected: " +
                // calendar.get(Calendar.YEAR) + "-" + position + "!");
                ((AllInOneActivity) getActivity()).setYear(calendar
                        .get(Calendar.YEAR));
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                    int positionOffsetPixels) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return view;

    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(android.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public YearViewFragment getItem(int position) {
            return (YearViewFragment) YearViewFragment.create(position);
        }

        @Override
        public int getCount() {
            return 1000;
        }
    }
}
