/*
 * Copyright (C) 2010 The Android Open Source Project
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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.TransformationMethod2;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.drawable.StateListDrawable;

import java.io.FileNotFoundException;

import com.android.calendar.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Switch;
import android.util.Log;

/**
 * A Switch is a two-state toggle switch widget that can select between two
 * options. The user may drag the "thumb" back and forth to choose the selected
 * option, or simply tap to toggle as if it were a checkbox. The
 * {@link #setText(CharSequence) text} property controls the text displayed in
 * the label for the switch, whereas the {@link #setTextOff(CharSequence) off}
 * and {@link #setTextOn(CharSequence) on} text controls the text on the thumb.
 * Similarly, the {@link #setTextAppearance(android.content.Context, int)
 * textAppearance} and the related setTypeface() methods control the typeface
 * and style of label text, whereas the
 * {@link #setSwitchTextAppearance(android.content.Context, int)
 * switchTextAppearance} and the related seSwitchTypeface() methods control that
 * of the thumb.
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/togglebutton.html">Toggle
 * Buttons</a> guide.
 * </p>
 * 
 * @attr ref android.R.styleable#Switch_textOn
 * @attr ref android.R.styleable#Switch_textOff
 * @attr ref android.R.styleable#Switch_switchMinWidth
 * @attr ref android.R.styleable#Switch_switchPadding
 * @attr ref android.R.styleable#Switch_switchTextAppearance
 * @attr ref android.R.styleable#Switch_thumb
 * @attr ref android.R.styleable#Switch_thumbTextPadding
 * @attr ref android.R.styleable#Switch_track
 */
public class SwitchHCT extends Switch implements SetColorable {

    static BitmapDrawable mEN_CECDrawable;
    BitmapDrawable mEN_UNCECDrawable;
    static BitmapDrawable mDIS_CECDrawable;
    BitmapDrawable mDIS_UNCECDrawable;
    int storeColor;
    private Context mContext;
    private boolean mOnActionBar = false;
    int mColor = 0;

    public static final int[] STATE_ENABLE_UNCHECKED = {
            android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_ENABLE_CHECKED = {
            android.R.attr.state_enabled, android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_UNCHECKED = {
            -android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_CHECKED = {
            -android.R.attr.state_enabled, android.R.attr.state_checked };

    public static final int[] STATE_THMB_UNCHECKED = {
            android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_THMB_CHECKED = {
            android.R.attr.state_enabled, android.R.attr.state_checked };

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Construct a new Switch with default styling.
     * 
     * @param context
     *            The Context that will determine this widget's theming.
     */
    public SwitchHCT(Context context) {
        this(context, null);
    }

    /**
     * Construct a new Switch with default styling, overriding specific style
     * attributes as requested.
     * 
     * @param context
     *            The Context that will determine this widget's theming.
     * @param attrs
     *            Specification of attributes that should deviate from default
     *            styling.
     */
    public SwitchHCT(Context context, AttributeSet attrs) {
        // this(context, attrs, 0);// Inject.getDefaultStyle(context));
        this(context, attrs, R.attr.switchStyle);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme
     * attribute, overriding specific style attributes as requested.
     * 
     * @param context
     *            The Context that will determine this widget's theming.
     * @param attrs
     *            Specification of attributes that should deviate from the
     *            default styling.
     * @param defStyle
     *            An attribute ID within the active theme containing a reference
     *            to the default style for this widget. e.g.
     *            android.R.attr.switchStyle.
     */
    public SwitchHCT(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTextOn("");
        setTextOff("");
        mContext = context;
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // color = Settings.System.getInt(context.getContentResolver(),
            // "color_ccl", Utils.DEFAULT_COLOR);
            color = context.getResources().getColor(R.color.gos_common_sw_on);
        } else {
            color = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }

        TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.Switch);
        color = a.getColor(R.styleable.Switch_android_color, color);
        a.recycle();

        // SetColor(color);
    }

    public SwitchHCT(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void SetOnActionBar(boolean onActionBar) {
        mOnActionBar = onActionBar;
        if (mOnActionBar) {
            SetColorForActionBar();
        }
    }

    public void SetColor(int color) {
        if (mColor == color) {
            Log.e("SwitchHCT", "do nothing=" + mColor);
            return;
        }

        mColor = color;
        if (mOnActionBar) {
            // SetColorForActionBar();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {

                // SetColorForAndroid50(color);
            } else {
                // setSwitchMinWidth(100);
                /*
                 * setThumbTextPadding(Utils.dpToPx(mContext, 12));
                 * setSwitchPadding(Utils.dpToPx(mContext, 16));
                 * SetColorForAndroid4x(color);
                 */
            }
        }
    }

    private void SetColorForActionBar() {
        int color = Settings.System.getInt(mContext.getContentResolver(),
                "color_ccd", Utils.DEFAULT_COLOR);
        color = mContext.getResources().getColor(R.color.gos_common_acb_sw_on);
        StateListDrawable mTempDrawable = new StateListDrawable();
        Drawable npdrawable1 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha1);
        Drawable npdrawable2 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha2);
        Drawable npdrawable3 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha3);
        Drawable npdrawable4 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha4);

        npdrawable1.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable2.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable3.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable4.setTintMode(PorterDuff.Mode.MULTIPLY);

        npdrawable1.setTint(color);// ?attr/colorSwitchThumbNormal
        npdrawable1.setAlpha(76);
        mTempDrawable.addState(STATE_ENABLE_CHECKED, npdrawable1);
        npdrawable2.setTint(0xde000000);// ?attr/colorSwitchThumbNormal
        npdrawable2.setAlpha(76);
        mTempDrawable.addState(STATE_ENABLE_UNCHECKED, npdrawable2);
        npdrawable3.setTint(color);
        npdrawable3.setAlpha(26);
        mTempDrawable.addState(STATE_DISABLE_CHECKED, npdrawable3);
        npdrawable4.setTint(0xde000000);
        npdrawable4.setAlpha(26);
        mTempDrawable.addState(STATE_DISABLE_UNCHECKED, npdrawable4);

        AnimatedStateListDrawable mAnimatedStateListDrawable = new AnimatedStateListDrawable();

        // item1
        NinePatchDrawable npdrawable = (NinePatchDrawable) getResources()
                .getDrawable(R.drawable.btn_switch_to_on_mtrl_disable_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_DISABLE_UNCHECKED,
                npdrawable, 0);

        // item2
        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00012_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);// ?attr/colorControlActivated
        mAnimatedStateListDrawable.addState(STATE_ENABLE_CHECKED, npdrawable,
                R.id.on);
        // item3
        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00001_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_ENABLE_UNCHECKED, npdrawable,
                R.id.off);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_disable_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setAlpha(125);
        npdrawable.setTint(color);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_DISABLE_CHECKED, npdrawable,
                0);
        // transition 1
        int fromId = R.id.off;
        int toId = R.id.on;
        boolean reversible = false;
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00001_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00002_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00003_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00004_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00005_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00006_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00007_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00008_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00009_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00010_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00011_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00012_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);
        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable, reversible);

        // transition 2
        fromId = R.id.on;
        toId = R.id.off;
        reversible = false;
        AnimationDrawable mAnimationDrawable2 = new AnimationDrawable();

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00001_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00002_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00003_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00004_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00005_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00006_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00007_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00008_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00009_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00010_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00011_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (NinePatchDrawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00012_2);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable2, reversible);

        setThumbDrawable(mAnimatedStateListDrawable);
        setTrackDrawable(mTempDrawable);
    }

    private void SetColorForAndroid50(int color) {
        StateListDrawable mTempDrawable = new StateListDrawable();
        Drawable npdrawable1 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha1);
        Drawable npdrawable2 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha2);
        Drawable npdrawable3 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha3);
        Drawable npdrawable4 = (Drawable) getResources().getDrawable(
                R.drawable.switch_track_mtrl_alpha4);

        npdrawable1.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable2.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable3.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable4.setTintMode(PorterDuff.Mode.MULTIPLY);

        npdrawable1.setTint(color);// ?attr/colorSwitchThumbNormal
        npdrawable1.setAlpha(76);
        mTempDrawable.addState(STATE_ENABLE_CHECKED, npdrawable1);
        npdrawable2.setTint(0xde000000);// ?attr/colorSwitchThumbNormal
        npdrawable2.setAlpha(76);
        mTempDrawable.addState(STATE_ENABLE_UNCHECKED, npdrawable2);
        npdrawable3.setTint(color);
        npdrawable3.setAlpha(26);
        mTempDrawable.addState(STATE_DISABLE_CHECKED, npdrawable3);
        npdrawable4.setTint(0xde000000);
        npdrawable4.setAlpha(26);
        mTempDrawable.addState(STATE_DISABLE_UNCHECKED, npdrawable4);

        AnimatedStateListDrawable mAnimatedStateListDrawable = new AnimatedStateListDrawable();

        // item1
        Drawable npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_disable);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_DISABLE_UNCHECKED,
                npdrawable, 0);

        // item2
        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00012);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);// ?attr/colorControlActivated
        mAnimatedStateListDrawable.addState(STATE_ENABLE_CHECKED, npdrawable,
                R.id.on);
        // item3
        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00001);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_ENABLE_UNCHECKED, npdrawable,
                R.id.off);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_disable);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setAlpha(125);
        npdrawable.setTint(color);// ?attr/colorSwitchThumbNormal
        mAnimatedStateListDrawable.addState(STATE_DISABLE_CHECKED, npdrawable,
                0);
        // transition 1
        int fromId = R.id.off;
        int toId = R.id.on;
        boolean reversible = false;
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00001);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00002);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00003);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00004);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00005);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00006);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00007);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00008);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00009);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00010);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00011);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_on_mtrl_00012);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable.addFrame(npdrawable, 15);
        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable, reversible);

        // transition 2
        fromId = R.id.on;
        toId = R.id.off;
        reversible = false;
        AnimationDrawable mAnimationDrawable2 = new AnimationDrawable();

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00001);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00002);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00003);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00004);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00005);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00006);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(color);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00007);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00008);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00009);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00010);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00011);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        npdrawable = (Drawable) getResources().getDrawable(
                R.drawable.btn_switch_to_off_mtrl_00012);
        npdrawable.setTintMode(PorterDuff.Mode.MULTIPLY);
        npdrawable.setTint(0xfff1f1f1);
        mAnimationDrawable2.addFrame(npdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable2, reversible);

        setThumbDrawable(mAnimatedStateListDrawable);
        setTrackDrawable(mTempDrawable);
    }

    private void SetColorForAndroid4x(int color) {
        StateListDrawable mTempDrawable = new StateListDrawable();

        mTempDrawable.addState(STATE_ENABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.switch_bg_off));
        if (mEN_CECDrawable == null || storeColor != color)
            mEN_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.switch_bg_on, color, false);
        Bitmap bt = mEN_CECDrawable.getBitmap();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.btn_check_on_hct_light);
        // btdrawable.setBitmap(bt);
        BitmapDrawable tempDrawable = new BitmapDrawable(getResources(), bt);
        mTempDrawable.addState(STATE_ENABLE_CHECKED, tempDrawable);
        mTempDrawable.addState(STATE_DISABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.switch_bg_off));
        if (mDIS_CECDrawable == null || storeColor != color)
            mDIS_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.switch_bg_on, color, true);
        bt = mDIS_CECDrawable.getBitmap();
        tempDrawable = new BitmapDrawable(getResources(), bt);
        mTempDrawable.addState(STATE_DISABLE_CHECKED, tempDrawable);
        setTrackDrawable(mTempDrawable);

        StateListDrawable mTempThumbDrawable = new StateListDrawable();
        mTempThumbDrawable.addState(STATE_ENABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.switch_thumb));
        mTempThumbDrawable.addState(STATE_ENABLE_CHECKED, getResources()
                .getDrawable(R.drawable.switch_thumb));
        mTempThumbDrawable.addState(STATE_DISABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.switch_thumb));
        mTempThumbDrawable.addState(STATE_DISABLE_CHECKED, getResources()
                .getDrawable(R.drawable.switch_thumb));
        setThumbDrawable(mTempThumbDrawable);

    }

    public Drawable ChangeColorDrawable(int id, int color, boolean disable) {
        Resources res = getResources();
        BitmapDrawable btdrawable = (BitmapDrawable) res.getDrawable(id);
        return new BitmapDrawable(res, JavaChanger.setColorWidget(
                btdrawable.getBitmap(), color, disable));
    }
}
