package com.gome.gmtimepicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.calendar.R;
import com.gome.gmtimepicker.util.SizeTransformer;

/**
 * @author Felix.Liang
 */
@SuppressWarnings("unused")
public class GMPicker extends LinearLayout {

    private SimplePicker mPickerValue;
    private TextView mTvUnit;
    private int mUnitTextColor = -1;
    private float mUnitTextSize = -1;
    private boolean mWillShowUnit = true;

    public GMPicker(Context context) {
        this(context, null);
    }

    public GMPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GMPicker(Context context, @Nullable AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GMPicker(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView(context);
        initFromAttributes(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setupView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.content_picker_component, this, true);
        mPickerValue = (SimplePicker) findViewById(R.id.picker_item_value);
        mTvUnit = (TextView) findViewById(R.id.tv_item_unit);
        mTvUnit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPickerValue.dispatchTouchEvent(event);
                return true;
            }
        });
    }

    private void initFromAttributes(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.GMPicker, defStyleAttr, defStyleRes);
        final String unit = array.getString(R.styleable.GMPicker_unit);
        setUnit(unit);
        final float unitTextSize = array.getDimensionPixelSize(
                R.styleable.GMPicker_unitTextSize, sp2px(15));
        setUnitTextSize(unitTextSize);
        final int unitTextColor = array.getColor(
                R.styleable.GMPicker_unitTextColor, 0xFF000000);
        setUnitTextColor(unitTextColor);
        final boolean showUnit = array.getBoolean(
                R.styleable.GMPicker_willShowUnit, false);
        setWillShowUnit(showUnit);
        array.recycle();
    }

    public boolean willShowUnit() {
        return mWillShowUnit;
    }

    public void setWillShowUnit(boolean show) {
        if (mWillShowUnit != show) {
            mTvUnit.setVisibility(show ? VISIBLE : GONE);
            mWillShowUnit = show;
        }
    }

    public String getUnit() {
        return mTvUnit.getText().toString();
    }

    public void setUnit(String unit) {
        if (TextUtils.isEmpty(unit)) {
            mTvUnit.setText(unit);
            mTvUnit.setVisibility(GONE);
        } else if (!unit.equals(mTvUnit.getText().toString())) {
            mTvUnit.setText(unit);
            mTvUnit.setVisibility(VISIBLE);
        }
    }

    public int getUnitTextColor() {
        return mUnitTextColor;
    }

    public void setUnitTextColor(int color) {
        if (mUnitTextColor != color) {
            mTvUnit.setTextColor(color);
            mUnitTextColor = color;
        }
    }

    public float getUnitTextSize() {
        return mUnitTextSize;
    }

    public void setUnitTextSize(float size) {
        if (mUnitTextSize != size) {
            mTvUnit.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            mUnitTextSize = size;
        }
    }

    private int dp2px(float dpValue) {
        return SizeTransformer.dp2px(getContext(), dpValue);
    }

    private int sp2px(float spValue) {
        return SizeTransformer.sp2px(getContext(), spValue);
    }

    public void setMaxValue(int maxValue) {
        mPickerValue.setMaxValue(maxValue);
    }

    public void setMinValue(int minValue) {
        mPickerValue.setMinValue(minValue);
    }

    public void setValue(int value) {
        mPickerValue.setValue(value);
    }

    public void setDisplayValues(String[] displayValues) {
        mPickerValue.setDisplayedValues(displayValues);
    }

    public void setFormatter(NumberPicker.Formatter formatter) {
        mPickerValue.setFormatter(formatter);
    }

    public void setOnValueChangeListener(
            NumberPicker.OnValueChangeListener listener) {
        mPickerValue.setOnValueChangedListener(listener);
    }

    public void setOnLongPressUpdateInternal(int millis) {
        mPickerValue.setOnLongPressUpdateInterval(millis);
    }

    public void setWrapSelectorWheel(boolean wrapSelectorWheel) {
        mPickerValue.setWrapSelectorWheel(wrapSelectorWheel);
    }

    public void refresh() {
        mPickerValue.invalidate();
    }

    public NumberPicker getPicker() {
        return mPickerValue;
    }
}
