package com.hct.calendar.ui;

import android.annotation.AttrRes;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

import com.android.calendar.R;

/**
 * Created by cat on 2017/6/10.
 */

public class SelectorGroup extends FrameLayout {

    public enum Checked {
        SCHEDULE, MEETING, BIRTHDAY,
    }

    private CheckStatedChanged mListener;

    private View mRootLayout;
    private CheckedTextView tvMiddle;
    private CheckedTextView tvLeft;
    private CheckedTextView tvRight;

    private Checked mCurrentChecked = Checked.SCHEDULE;

    public SelectorGroup(@NonNull Context context) {
        this(context, null);
    }

    public SelectorGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorGroup(@NonNull Context context,
            @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mRootLayout = inflater.inflate(R.layout.event_create_select_layout,
                null);
        this.addView(mRootLayout, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        tvMiddle = (CheckedTextView) mRootLayout
                .findViewById(R.id.event_create_title_meeting);
        tvLeft = (CheckedTextView) mRootLayout
                .findViewById(R.id.event_create_title_event);
        tvRight = (CheckedTextView) mRootLayout
                .findViewById(R.id.event_create_title_birthday);
        updateCheckState(mCurrentChecked);

        tvLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvLeft.isChecked()) {
                    mCurrentChecked = Checked.SCHEDULE;
                    updateCheckState(mCurrentChecked);
                }
            }
        });

        tvMiddle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvMiddle.isChecked()) {
                    mCurrentChecked = Checked.MEETING;
                    updateCheckState(mCurrentChecked);
                }
            }
        });
        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!tvRight.isChecked()) {
                    mCurrentChecked = Checked.BIRTHDAY;
                    updateCheckState(mCurrentChecked);
                }
            }
        });
    }

    public void updateCheckState(@NonNull final Checked current) {
        tvLeft.setChecked(current == Checked.SCHEDULE);
        tvMiddle.setChecked(current == Checked.MEETING);
        tvRight.setChecked(current == Checked.BIRTHDAY);
        if (mListener != null) {
            mListener.onCheckChanged(current);
        }
    }

    public Checked getCurrentCheck() {
        return mCurrentChecked;
    }

    public void setCurrentCheck(Checked currentChecked) {
        mCurrentChecked = currentChecked;
    }

    public void setCheckStatedChangedListener(CheckStatedChanged changedListener) {
        mListener = changedListener;
    }

    public interface CheckStatedChanged {
        void onCheckChanged(Checked current);
    }
}
