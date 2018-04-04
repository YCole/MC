package com.hct.calendar.ui;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by cat on 2017/6/20.
 * 
 * @apiNote ScheduleMoreLayout
 */
public class ScheduleMoreLayout extends FrameLayout {

    private EditText mRemarkEt;
    private EditText mLocationEt;
    private View mAccountLayout;
    private TextView tv_account;

    public enum Selected {
        BUTTON, MORE_LAYOUT
    }

    private Selected mCurrentSelect = Selected.BUTTON;
    private View mRootV;
    private Button mBtnLoadMore;
    private LinearLayout mMoreLayout;

    public ScheduleMoreLayout(@NonNull Context context) {
        this(context, null);
    }

    public ScheduleMoreLayout(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleMoreLayout(@NonNull Context context,
            @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mRootV = inflater.inflate(R.layout.schedule_more_layout, this, false);
        this.addView(mRootV, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mBtnLoadMore = (Button) mRootV
                .findViewById(R.id.btn_schedule_load_more);
        mBtnLoadMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentSelect = Selected.MORE_LAYOUT;
                chooseShow(mCurrentSelect);
            }
        });
        mMoreLayout = (LinearLayout) mRootV
                .findViewById(R.id.schedule_more_layout);
        mRemarkEt = (EditText) mRootV.findViewById(R.id.remark_et);
        mLocationEt = (EditText) mRootV.findViewById(R.id.location_et);
        mAccountLayout = mRootV.findViewById(R.id.calendar_account_layout);
        tv_account = (TextView) mRootV.findViewById(R.id.tv_account);
        chooseShow(mCurrentSelect);
    }

    public TextView getAccountTextView() {
        return tv_account;
    }

    public EditText getRemarkEditText() {
        return mRemarkEt;
    }

    public EditText getLocationEditText() {
        return mLocationEt;
    }

    public Selected getCurrentSelect() {
        return mCurrentSelect;
    }

    public View getAccountLayout() {
        return mAccountLayout;
    }

    private void chooseShow(@NonNull Selected mCurrentSelect) {
        switch (mCurrentSelect) {
        case BUTTON:
        default:
            mBtnLoadMore.setVisibility(VISIBLE);
            mMoreLayout.setVisibility(GONE);
            break;
        case MORE_LAYOUT:
            mBtnLoadMore.setVisibility(GONE);
            mMoreLayout.setVisibility(VISIBLE);
            break;
        }
    }
}
