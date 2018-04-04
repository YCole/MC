/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hct.gios.widget;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.android.calendar.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.os.Build;
import android.widget.RadioButton;

/**
 * <p>
 * A radio button is a two-states button that can be either checked or
 * unchecked. When the radio button is unchecked, the user can press or click it
 * to check it. However, contrary to a {@link android.widget.CheckBox}, a radio
 * button cannot be unchecked by the user once checked.
 * </p>
 * <p>
 * Radio buttons are normally used together in a
 * {@link android.widget.RadioGroup}. When several radio buttons live inside a
 * radio group, checking one radio button unchecks all the others.
 * </p>
 * </p>
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/radiobutton.html">Radio
 * Buttons</a> guide.
 * </p>
 * <p>
 * <strong>XML attributes</strong>
 * </p>
 * <p>
 * See {@link android.R.styleable#CompoundButton CompoundButton Attributes},
 * {@link android.R.styleable#Button Button Attributes},
 * {@link android.R.styleable#TextView TextView Attributes},
 * {@link android.R.styleable#View View Attributes}
 * </p>
 */
public class RadioButtonHCT extends RadioButton implements SetColorable {

    static BitmapDrawable mEN_CECDrawable;
    BitmapDrawable mEN_UNCECDrawable;
    static BitmapDrawable mDIS_CECDrawable;
    BitmapDrawable mDIS_UNCECDrawable;
    int storeColor;
    public static final int[] STATE_ENABLE_UNCHECKED = {
            android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_ENABLE_CHECKED = {
            android.R.attr.state_enabled, android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_UNCHECKED = {
            -android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_CHECKED = {
            -android.R.attr.state_enabled, android.R.attr.state_checked };

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public RadioButtonHCT(Context context) {
        this(context, null);
    }

    public RadioButtonHCT(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.radioButtonStyle);
    }

    public RadioButtonHCT(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // color = Settings.System.getInt(context.getContentResolver(),
            // "color_ccl", Utils.DEFAULT_COLOR);
            color = context.getResources().getColor(R.color.gos_common_rb);
        } else {
            color = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RadioButton);
        color = a.getColor(R.styleable.RadioButton_android_color, color);
        a.recycle();

        // SetColor(color);
    }

    public void SetColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // SetColorForAndroid50(color);
        } else {
            // SetColorForAndroid4x(color);
        }
    }

    private void SetColorForAndroid50(int color) {
        AnimatedStateListDrawable mAnimatedStateListDrawable = new AnimatedStateListDrawable();
        // TransitionDrawable mTransitionDrawable = new TransitionDrawable();
        // AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        // item1
        BitmapDrawable btdrawable = (BitmapDrawable) getResources()
                .getDrawable(R.drawable.btn_radio_to_on_mtrl_015);
        btdrawable.setAlpha(255);
        btdrawable.setTint(color);// android:tint="?android:attr/colorControlActivated"
        mAnimatedStateListDrawable.addState(STATE_DISABLE_CHECKED, btdrawable,
                0);

        // item2
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_000);
        btdrawable.setAlpha(255);
        btdrawable.setTint(0x8a000000);// android:tint="?android:attr/colorControlNormal"
        mAnimatedStateListDrawable.addState(STATE_DISABLE_UNCHECKED,
                btdrawable, 0);

        // item3
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_015);
        btdrawable.setTint(color);
        mAnimatedStateListDrawable.addState(STATE_ENABLE_CHECKED, btdrawable,
                R.id.on);

        // item4
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_000);
        btdrawable.setTint(0x8a000000);
        mAnimatedStateListDrawable.addState(STATE_ENABLE_UNCHECKED, btdrawable,
                R.id.off);

        // transition 1
        int fromId = R.id.off;
        int toId = R.id.on;
        boolean reversible = false;
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_000);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_001);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_002);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_003);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_004);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_005);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_006);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_007);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_008);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_009);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_010);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_011);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_012);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_013);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_014);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_on_mtrl_015);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable, reversible);

        // transition 2
        fromId = R.id.on;
        toId = R.id.off;
        reversible = false;
        AnimationDrawable mAnimationDrawable2 = new AnimationDrawable();

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_000);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_001);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_002);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_003);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_004);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_005);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_006);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_007);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_008);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_009);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_010);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_011);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_012);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_013);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_014);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_radio_to_off_mtrl_015);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable2, reversible);

        setButtonDrawable(mAnimatedStateListDrawable);

    }

    private void SetColorForAndroid4x(int color) {
        StateListDrawable mTempDrawable = new StateListDrawable();

        if (mEN_CECDrawable == null || storeColor != color)
            mEN_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.radio_hct_on, color, false);
        Bitmap bt = mEN_CECDrawable.getBitmap();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.btn_check_on_hct_light);
        // btdrawable.setBitmap(bt);
        BitmapDrawable tempDrawable = new BitmapDrawable(getResources(), bt);
        mTempDrawable.addState(STATE_ENABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.radio_hct_off));
        mTempDrawable.addState(STATE_ENABLE_CHECKED, tempDrawable);
        mTempDrawable.addState(STATE_DISABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.radio_off_disabled_hct));
        if (mDIS_CECDrawable == null || storeColor != color)
            mDIS_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.radio_on_disabled_hct, color, true);
        bt = mDIS_CECDrawable.getBitmap();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.btn_check_on_hct_light);
        // btdrawable.setBitmap(bt);
        tempDrawable = new BitmapDrawable(getResources(), bt);
        mTempDrawable.addState(STATE_DISABLE_CHECKED, tempDrawable);
        setButtonDrawable(mTempDrawable);

    }

    public Drawable ChangeColorDrawable(int id, int color, boolean disable) {
        Resources res = getResources();
        BitmapDrawable btdrawable = (BitmapDrawable) res.getDrawable(id);
        return new BitmapDrawable(res, JavaChanger.setColorWidget(
                btdrawable.getBitmap(), color, disable));
    }

}
