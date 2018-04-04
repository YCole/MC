package com.hct.calendar.month;

import java.util.ArrayList;

import android.util.Log;

import com.google.android.collect.Lists;

public class ViewMode {

    public interface ModeChangeListener {
        void onViewModeChanged(int newMode);
    }

    public static final int HALF_MONTH = 1;
    public static final int HALF_MONTH_WEEK = 2;
    public static final int FULL_MONTH = 3;
    public static final int FULL_MONTH_WEEK = 4;
    public static final int UNKNOWN = 0;

    // Key used to save this {@link ViewMode}.
    private static final String VIEW_MODE_KEY = "view-mode";
    private final ArrayList<ModeChangeListener> mListeners = Lists
            .newArrayList();
    private int mMode = UNKNOWN;

    public static final String LOG_TAG = "ViewMode";

    private static final String[] MODE_NAMES = { "Unknown", "Half Month",
            "Half Month Week", "Full Month", "Full Month Week", };

    public ViewMode() {
    }

    @Override
    public String toString() {
        return "[mode=" + MODE_NAMES[mMode] + "]";
    }

    public String getModeString() {
        return MODE_NAMES[mMode];
    }

    public void addListener(ModeChangeListener listener) {
        mListeners.add(listener);
    }

    private void dispatchModeChange() {
        ArrayList<ModeChangeListener> list = new ArrayList<ModeChangeListener>(
                mListeners);
        for (ModeChangeListener listener : list) {
            assert (listener != null);
            listener.onViewModeChanged(mMode);
        }
    }

    public void enterHalfMonthMode() {
        setModeInternal(HALF_MONTH);
    }

    public void enterHalfMonthWeekMode() {
        setModeInternal(HALF_MONTH_WEEK);
    }

    public void enterFullMonthMode() {
        setModeInternal(FULL_MONTH);
    }

    public void enterFullMonthWeekMode() {
        setModeInternal(FULL_MONTH_WEEK);
    }

    public void enterWeekMode() {
        if (isFullMonthMode()) {
            enterFullMonthWeekMode();
        } else if (isHalfMonthMode()) {
            enterHalfMonthWeekMode();
        }
    }

    public void enterMonthMode() {
        if (isFullMonthWeekMode()) {
            enterFullMonthMode();
        } else if (isHalfMonthWeekMode()) {
            enterHalfMonthMode();
        }
    }

    /**
     * @return The current mode.
     */
    public int getMode() {
        return mMode;
    }

    /**
     * Return whether the current mode is considered a list mode.
     */
    public boolean isHalfMonthMode() {
        return isHalfMonthMode(mMode);
    }

    public static boolean isHalfMonthMode(final int mode) {
        return mode == HALF_MONTH;
    }

    public boolean isHalfMonthWeekMode() {
        return isHalfMonthWeekMode(mMode);
    }

    public static boolean isHalfMonthWeekMode(final int mode) {
        return mode == HALF_MONTH_WEEK;
    }

    public boolean isFullMonthMode() {
        return isFullMonthMode(mMode);
    }

    public static boolean isFullMonthMode(final int mode) {
        return mode == FULL_MONTH;
    }

    public boolean isFullMonthWeekMode() {
        return isFullMonthWeekMode(mMode);
    }

    public static boolean isFullMonthWeekMode(final int mode) {
        return mode == FULL_MONTH_WEEK;
    }

    public boolean isWeekMode() {
        return isWeekMode(mMode);
    }

    public static boolean isWeekMode(final int mode) {
        return mode == FULL_MONTH_WEEK || mode == HALF_MONTH_WEEK;
    }

    public boolean isMonthMode() {
        return isMonthMode(mMode);
    }

    public static boolean isMonthMode(final int mode) {
        return mode == FULL_MONTH || mode == HALF_MONTH;
    }

    public void removeListener(ModeChangeListener listener) {
        mListeners.remove(listener);
    }

    private boolean setModeInternal(int mode) {
        if (mMode == mode) {
            if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
                Log.d(LOG_TAG, String.format(
                        "ViewMode: debouncing change attempt mode=%s", mode),
                        new Error());
            } else {
                Log.d(LOG_TAG, String.format(
                        "ViewMode: debouncing change attempt mode=%s", mode));
            }
            return false;
        }

        if (Log.isLoggable(LOG_TAG, Log.DEBUG)) {
            Log.d(LOG_TAG, String.format(
                    "ViewMode: executing change old=%s new=%s", mMode, mode),
                    new Error());
        } else {
            Log.i(LOG_TAG, String.format(
                    "ViewMode: executing change old=%s new=%s", mMode, mode));
        }
        mMode = mode;
        dispatchModeChange();
        return true;
    }
}
