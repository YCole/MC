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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer.DrawableContainerState;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.SeekBar;
import android.os.Build;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.ShapeDrawable;

import com.android.calendar.R;
//import com.hct.gios.widget.NinePatchDrawable.NinePatchState;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * A SeekBar is an extension of ProgressBar that adds a draggable thumb. The
 * user can touch the thumb and drag left or right to set the current progress
 * level or use the arrow keys. Placing focusable widgets to the left or right
 * of a SeekBar is discouraged.
 * <p>
 * Clients of the SeekBar can attach a {@link SeekBar.OnSeekBarChangeListener}
 * to be notified of the user's actions.
 * 
 * @attr ref android.R.styleable#SeekBar_thumb
 */
public class SeekBarHCT extends SeekBar implements SetColorable {

    LayerDrawable mProgressDrawable;
    ScaleDrawable secondDrawable;
    ScaleDrawable primaryDrawable;
    BitmapDrawable mSecondDrawable;
    BitmapDrawable mBgDrawable;
    BitmapDrawable mEN_Drawable;
    BitmapDrawable mPRESS_Drawable;
    BitmapDrawable mSELECTED_Drawable;
    BitmapDrawable mNORMAL_Drawable;

    public static final int[] STATE_DISABLE = { -android.R.attr.state_enabled };
    public static final int[] STATE_DISABLE_PRESSED = {
            -android.R.attr.state_enabled, android.R.attr.state_pressed };
    public static final int[] STATE_PRESSED = { android.R.attr.state_pressed };
    public static final int[] NORMAL = {
    // android.R.attr.state_enabled, android.R.attr.state_pressed
    };

    // for JB
    public static final int[] STATE_ENABLE = { -android.R.attr.state_enabled };

    public static final int[] STATE_SELECTED = { android.R.attr.state_selected,
            -android.R.attr.state_checked };

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public SeekBarHCT(Context context) {
        this(context, null);
    }

    public SeekBarHCT(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.seekBarStyle);
    }

    public SeekBarHCT(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // color = Settings.System.getInt(context.getContentResolver(),
            // "color_ccl", Utils.DEFAULT_COLOR);
            color = context.getResources().getColor(R.color.gos_common_pb);
        } else {
            color = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBar);
        color = a.getColor(R.styleable.SeekBar_android_color, color);
        a.recycle();

        SetColor(color);

    }

    @SuppressLint("NewApi")
    public void SetColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {

            setThumb(ChangeColorDrawable(color));
            Resources r = mContext.getResources();
            Drawable bagtem = r
                    .getDrawable(R.drawable.scrubber_track_hct_light);
            Drawable prgtem = r
                    .getDrawable(R.drawable.scrubber_primary_hct_light);
            bagtem.setTint(0x10000000);
            prgtem.setTint(color);
            secondDrawable = new ScaleDrawable(prgtem, Gravity.LEFT, 1.0f, -1);
            Drawable[] draws = { bagtem, bagtem, secondDrawable };

            int[] ids = { com.android.internal.R.id.background,
                    com.android.internal.R.id.secondaryProgress,
                    com.android.internal.R.id.progress };
            mProgressDrawable = new LayerDrawable(draws);
            mProgressDrawable.setId(0, com.android.internal.R.id.background);
            mProgressDrawable.setId(1,
                    com.android.internal.R.id.secondaryProgress);
            mProgressDrawable.setId(2, com.android.internal.R.id.progress);
            setProgressDrawable(mProgressDrawable);
        } else {
            SetColorJB(color);
        }
    }

    public void SetColor(int color, int bgColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {

            setThumb(ChangeColorDrawable(color));
            Resources r = mContext.getResources();
            Drawable bagtem = r
                    .getDrawable(R.drawable.scrubber_track_hct_light);
            Drawable prgtem = r
                    .getDrawable(R.drawable.scrubber_primary_hct_light);
            // bagtem.setTint(r.getColor(R.color.secondary_text_material_light));
            bagtem.setTint(bgColor);
            prgtem.setTint(color);
            secondDrawable = new ScaleDrawable(prgtem, Gravity.LEFT, 1.0f, -1);
            Drawable[] draws = { bagtem, bagtem, secondDrawable };

            int[] ids = { com.android.internal.R.id.background,
                    com.android.internal.R.id.secondaryProgress,
                    com.android.internal.R.id.progress };
            mProgressDrawable = new LayerDrawable(draws);
            mProgressDrawable.setId(0, com.android.internal.R.id.background);
            mProgressDrawable.setId(1,
                    com.android.internal.R.id.secondaryProgress);
            mProgressDrawable.setId(2, com.android.internal.R.id.progress);
            setProgressDrawable(mProgressDrawable);
        } else {
            SetColorJB(color);
        }
    }

    public Drawable ChangeColorDrawable(int color) {
        AnimatedStateListDrawable mAnimatedStateListDrawable = new AnimatedStateListDrawable();
        // TransitionDrawable mTransitionDrawable = new TransitionDrawable();
        // AnimationDrawable mAnimationDrawable = new AnimationDrawable();
        DrawableContainerState Drstate = (DrawableContainerState) mAnimatedStateListDrawable
                .getConstantState();
        Drstate.setConstantSize(true);

        // item1
        BitmapDrawable btdrawable = (BitmapDrawable) getResources()
                .getDrawable(R.drawable.scrubber_control_off_mtrl_alpha);
        btdrawable.setAlpha(255);
        btdrawable.setGravity(Gravity.CENTER);
        btdrawable.setTint(color);// android:tint="?android:attr/colorControlActivated"
        mAnimatedStateListDrawable.addState(STATE_DISABLE_PRESSED, btdrawable,
                0);

        // item2
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_off_mtrl_alpha);
        btdrawable.setAlpha(255);
        btdrawable.setGravity(Gravity.CENTER);
        btdrawable.setTint(color);// android:tint="?android:attr/colorControlNormal"
        mAnimatedStateListDrawable.addState(STATE_DISABLE, btdrawable, 0);

        // item3
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_005);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimatedStateListDrawable.addState(STATE_PRESSED, btdrawable,
                R.id.pressed);

        // item4
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_000);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimatedStateListDrawable.addState(NORMAL, btdrawable,
                R.id.not_pressed);

        // transition 1
        int fromId = R.id.not_pressed;
        int toId = R.id.pressed;
        boolean reversible = false;
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_000);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_001);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_002);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_003);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_004);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_to_pressed_mtrl_005);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable.addFrame(btdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable, reversible);

        // transition 2
        fromId = R.id.pressed;
        toId = R.id.not_pressed;
        reversible = false;
        AnimationDrawable mAnimationDrawable2 = new AnimationDrawable();

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_000);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_001);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_002);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_003);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_004);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.scrubber_control_from_pressed_mtrl_005);
        btdrawable.setTint(color);
        btdrawable.setGravity(Gravity.CENTER);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable2, reversible);
        return mAnimatedStateListDrawable;
    }

    public Drawable ChangeColorDrawableJB(int id, int color, boolean disable) {
        Resources res = getResources();
        BitmapDrawable btdrawable = (BitmapDrawable) res.getDrawable(id);
        return new BitmapDrawable(res, JavaChanger.setColorWidget(
                btdrawable.getBitmap(), color, disable));
    }

    @SuppressLint("NewApi")
    public void SetColorJB(int color) {
        StateListDrawable mTempDrawable = new StateListDrawable();
        mTempDrawable.addState(
                STATE_ENABLE,
                ChangeColorDrawableJB(R.drawable.scrubber_control_disabled_hct,
                        color, true));
        mTempDrawable.addState(
                STATE_PRESSED,
                ChangeColorDrawableJB(R.drawable.scrubber_control_pressed_hct,
                        color, false));
        mTempDrawable.addState(
                STATE_SELECTED,
                ChangeColorDrawableJB(R.drawable.scrubber_control_focused_hct,
                        color, false));
        mTempDrawable.addState(
                NORMAL,
                ChangeColorDrawableJB(R.drawable.scrubber_control_normal_hct,
                        color, false));

        setThumb(mTempDrawable);
        Resources r = mContext.getResources();
        Drawable bagtem = r
                .getDrawable(R.drawable.scrubber_track_hct_light_holo);
        Drawable prgtem = r.getDrawable(R.drawable.scrubber_primary_hct_light);
        // NinePatchDrawable prgsrc=(NinePatchDrawable) prgtem;
        // NinePatchState ninepathstat=(NinePatchState)
        // prgsrc.getConstantState();
        // NinePatch ninepatch=ninepathstat.getNinePatch();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)r.getDrawable(R.drawable.scrubber_primary_holo);

        // if(btDrawable !=null)
        // {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.scrubber_primary_hct_light);
        Bitmap btDrawable = JavaChanger.setColor(bitmap, color);
        NinePatch ninepatch2 = new NinePatch(btDrawable,
                bitmap.getNinePatchChunk());

        NinePatchDrawable testNinePatch = new NinePatchDrawable(ninepatch2);

        secondDrawable = new ScaleDrawable(testNinePatch, Gravity.LEFT, 1.0f,
                -1);
        // }/*else{
        // secondDrawable=new ScaleDrawable(prgtem, Gravity.LEFT, 1.0f, -1);
        // }
        Drawable[] draws = { bagtem, bagtem, secondDrawable };

        int[] ids = { com.android.internal.R.id.background,
                com.android.internal.R.id.secondaryProgress,
                com.android.internal.R.id.progress };
        mProgressDrawable = new LayerDrawable(draws);
        mProgressDrawable.setId(0, com.android.internal.R.id.background);
        mProgressDrawable.setId(1, com.android.internal.R.id.secondaryProgress);
        mProgressDrawable.setId(2, com.android.internal.R.id.progress);
        setProgressDrawable(mProgressDrawable);
    }

}
