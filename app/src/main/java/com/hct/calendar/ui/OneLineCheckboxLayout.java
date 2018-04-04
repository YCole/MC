package com.hct.calendar.ui;

import gm.widget.GomeSwitch;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.calendar.R;

/**
 * Created by cat on 2017/6/20.
 * 
 * @apiNote one line checkbox layout
 */

public class OneLineCheckboxLayout extends FrameLayout {

    private View mRootV;
    private TextView tv;
    private GomeSwitch cb;
    private boolean checked;
    private String text;
    private int left_color;

    public OneLineCheckboxLayout(@NonNull Context context) {
        this(context, null);
    }

    public OneLineCheckboxLayout(@NonNull Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OneLineCheckboxLayout(@NonNull Context context,
            @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.OneLineCheckboxLayout, defStyleAttr, 0);
        checked = a
                .getBoolean(R.styleable.OneLineCheckboxLayout_checked, false);
        text = a.getString(R.styleable.OneLineCheckboxLayout_text);

        a.recycle();
        init(context);
    }

    private void init(@NonNull Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mRootV = inflater.inflate(R.layout.one_line_layout, this, false);
        this.addView(mRootV, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv = (TextView) mRootV.findViewById(R.id.schedule_all_day_tv);
        if (!TextUtils.isEmpty(text)) {
            tv.setText(text);
        }
        cb = (GomeSwitch) mRootV.findViewById(R.id.schedule_all_day_cb);
        cb.setChecked(checked);

    }

    public void setText(CharSequence text) {
        tv.setText(text);
    }

    public void setTextColor(String text) {
        tv.setTextColor(Color.parseColor(text));
    }

    public void setChecked(boolean check) {
        cb.setChecked(check);
    }

    public boolean isChecked() {
        return cb.isChecked();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mListener = listener;
    }

    private OnCheckedChangeListener mListener;

    public interface OnCheckedChangeListener {
        void onChange(boolean checked);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP) {
            checked = !checked;
            cb.setChecked(checked);
            if (mListener != null) {
                mListener.onChange(checked);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
