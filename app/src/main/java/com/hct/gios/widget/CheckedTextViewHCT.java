/*
 * Copyright (C) 2007 The Android Open Source Project
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

import com.android.calendar.R;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.widget.CheckedTextView;
import android.provider.Settings;

/**
 * An extension to TextView that supports the {@link android.widget.Checkable}
 * interface. This is useful when used in a {@link android.widget.ListView
 * ListView} where the it's {@link android.widget.ListView#setChoiceMode(int)
 * setChoiceMode} has been set to something other than
 * {@link android.widget.ListView#CHOICE_MODE_NONE CHOICE_MODE_NONE}.
 * 
 * @attr ref android.R.styleable#CheckedTextView_checked
 * @attr ref android.R.styleable#CheckedTextView_checkMark
 */
public class CheckedTextViewHCT extends CheckedTextView /* implements Checkable */{

    public static final int[] STATE_ENABLE_UNCHECKED = {
            android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_ENABLE_CHECKED = {
            android.R.attr.state_enabled, android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_UNCHECKED = {
            -android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_CHECKED = {
            -android.R.attr.state_enabled, android.R.attr.state_checked };

    public CheckedTextViewHCT(Context context) {
        super(context);
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "color_ccl", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_cb);
        // setCheckMarkDrawable(getDrawable(color));
    }

    public CheckedTextViewHCT(Context context, AttributeSet attrs) {
        super(context, attrs);
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "color_ccl", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_cb);
        // setCheckMarkDrawable(getDrawable(color));
    }

    public CheckedTextViewHCT(Context context, AttributeSet attrs,
            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "color_ccl", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_cb);
        // setCheckMarkDrawable(getDrawable(color));
    }

    public CheckedTextViewHCT(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "color_ccl", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_cb);
        // setCheckMarkDrawable(getDrawable(color));
    }

    private Drawable getDrawable(int color) {

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

        return mAnimatedStateListDrawable;
    }

}
