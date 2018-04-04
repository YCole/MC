package com.hct.gios.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.android.calendar.R;

public class HCTDaysOfWeek extends LinearLayout {

    private ToggleButton[] mWeekButton = new ToggleButton[7];
    private int mDays;
    private View mView;
    private boolean isEnabled = true;
    private static int mCheckedColor = 0xffff0000;// 0xff389af4;
    private final int mUncheckedColor = 0xffe7e7e7;

    public HCTDaysOfWeek(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.hct_days_of_week, null);
        mView = view;
        addView(view);
    }

    public void setBackGroundColor(int color) {
        mCheckedColor = color;
    }

    public void setEnabled(boolean enable) {
        isEnabled = enable;
        for (int i = 0; i < 7; i++) {
            mWeekButton[i].setEnabled(enable);
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setDays(int days) {
        mDays = days;
        InitToggleButton();
    }

    /**
     * @param day
     *            Mon=0 ... Sun=6
     * @return true if given day is set
     */
    private boolean isSet(int day) {
        return ((mDays & (1 << day)) > 0);
    }

    private void set(int day, boolean set) {
        if (set) {
            mDays |= (1 << day);
        } else {
            mDays &= ~(1 << day);
        }
        setButtonBackGround(mWeekButton[day]);
    }

    public int getDays() {
        return mDays;
    }

    // Returns days of week encoded in an array of booleans.
    public boolean[] getBooleanArray() {
        boolean[] ret = new boolean[7];
        for (int i = 0; i < 7; i++) {
            ret[i] = isSet(i);
        }
        return ret;
    }

    /**
     * @return true if alarm is set to repeat
     */
    public boolean isRepeatSet() {
        // return mDays != 0;
        return ((mDays & 0x80) == 0);
    }

    /**
     * @return true if alarm is set to repeat every day
     */
    public boolean isEveryDaySet() {
        return mDays == 0x7f;
    }

    private void setChecked() {
        for (int day = 0; day < 7; day++) {
            if (isSet(day)) {
                mWeekButton[day].setChecked(true);
            } else {
                mWeekButton[day].setChecked(false);
            }
        }

        for (int day = 0; day < 7; day++) {
            setButtonBackGround(mWeekButton[day]);
        }
    }

    private void InitToggleButton() {
        mWeekButton[0] = (ToggleButton) mView.findViewById(R.id.repeat_mon);
        mWeekButton[1] = (ToggleButton) mView.findViewById(R.id.repeat_tue);
        mWeekButton[2] = (ToggleButton) mView.findViewById(R.id.repeat_wed);
        mWeekButton[3] = (ToggleButton) mView.findViewById(R.id.repeat_thu);
        mWeekButton[4] = (ToggleButton) mView.findViewById(R.id.repeat_fri);
        mWeekButton[5] = (ToggleButton) mView.findViewById(R.id.repeat_sat);
        mWeekButton[6] = (ToggleButton) mView.findViewById(R.id.repeat_sun);

        mWeekButton[0]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(0, isChecked);
                    }
                });

        mWeekButton[1]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(1, isChecked);
                    }
                });

        mWeekButton[2]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(2, isChecked);
                    }
                });
        mWeekButton[3]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(3, isChecked);
                    }
                });
        mWeekButton[4]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(4, isChecked);
                    }
                });
        mWeekButton[5]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(5, isChecked);
                    }
                });
        mWeekButton[6]
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView,
                            boolean isChecked) {
                        set(6, isChecked);
                    }
                });

        setChecked();
        setButtonBackGround();
    }

    private void setButtonBackGround(ToggleButton toggleButton) {
        Drawable backgroundDrawable = toggleButton.getBackground();
        if (toggleButton.isChecked()) {
            backgroundDrawable.setColorFilter(mCheckedColor, Mode.SRC);
        } else {
            backgroundDrawable.setColorFilter(mUncheckedColor, Mode.SRC);
        }
    }

    private void setButtonBackGround() {
        for (int i = 0; i < 7; i++) {
            setButtonBackGround(mWeekButton[i]);
        }
    }
}
