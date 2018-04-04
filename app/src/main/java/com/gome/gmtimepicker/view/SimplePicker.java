package com.gome.gmtimepicker.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;

/**
 * @author Felix.Liang
 */
public class SimplePicker extends NumberPicker {

    private static final String LOG_TAG = "Debug_SimplePicker";

    private static final float TEXT_SIZE = 14;

    public SimplePicker(Context context) {
        this(context, null);
    }

    public SimplePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        hidePickerSelectionDivider();
        setWrapSelectorWheel(false);
        formatValueByOne();
        setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setInputEditText();
    }

    private void hidePickerSelectionDivider() {
        setParentField("mSelectionDivider",
                new ColorDrawable(Color.TRANSPARENT));
        setParentField("mSelectionDividerHeight", 0);
    }

    private void formatValueByOne() {
        invokeParentMethod("changeValueByOne", true);
    }

    private void setParentField(String name, Object newValue) {
        try {
            Field field = NumberPicker.class.getDeclaredField(name);
            field.setAccessible(true);
            field.set(this, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Log.e(LOG_TAG, String.valueOf(e.getMessage()));
        }
    }

    private void setInputEditText() {
        try {
            Field field = NumberPicker.class.getDeclaredField("mInputText");
            field.setAccessible(true);
            Object editText = field.get(this);
            Method setVisibility = editText.getClass().getMethod(
                    "setVisibility", int.class);
            setVisibility.setAccessible(true);
            setVisibility.invoke(editText, View.GONE);
        } catch (Exception e) {
            Log.e(LOG_TAG, String.valueOf(e.getMessage()));
        }
    }

    private void invokeParentMethod(String name, Object param) {
        try {
            Class cls;
            if (param instanceof Boolean)
                cls = boolean.class;
            else
                cls = param.getClass();
            Method method = NumberPicker.class.getDeclaredMethod(name, cls);
            method.setAccessible(true);
            method.invoke(this, param);
        } catch (NoSuchMethodException | IllegalAccessException
                | InvocationTargetException e) {
            Log.e(LOG_TAG, String.valueOf(e.getMessage()));
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (child instanceof EditText) {
            EditText editText = (EditText) child;
            editText.setTextSize(TEXT_SIZE);
        }
    }
}
