package com.android.calendar.event;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

/**
 * FragmentFactory
 * 
 * @author chenhuaiyu
 */
public class FragmentFactory {
    public static Fragment getFragmentByTag(Activity pActivity, String pTag) {
        FragmentManager fm = pActivity.getFragmentManager();
        Fragment fragment = fm.findFragmentByTag(pTag);
        if (fragment != null) {
            return fragment;
        } else {
            if (ScheduleFragment.TAG.equals(pTag)) {
                return new ScheduleFragment();
            } else if (MeetingFragment.TAG.equals(pTag)) {
                return new MeetingFragment();
            } else if (BirthdayFragment.TAG.equals(pTag)) {
                return new BirthdayFragment();
            } else {
                return null;
            }
        }
    }
}
