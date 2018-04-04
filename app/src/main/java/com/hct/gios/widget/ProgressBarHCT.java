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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.ProgressBar;
import com.android.calendar.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ProgressBarHCT extends ProgressBar {

    /**
     * Create a new progress bar with range 0...100 and initial progress of 0.
     * 
     * @param context
     *            the application environment
     */
    LayerDrawable mProgressDrawable;
    ScaleDrawable secondDrawable;
    ScaleDrawable primaryDrawable;
    BitmapDrawable mSecondDrawable;
    BitmapDrawable mBgDrawable;
    BitmapDrawable mEN_Drawable;
    BitmapDrawable mPRESS_Drawable;
    BitmapDrawable mSELECTED_Drawable;
    BitmapDrawable mNORMAL_Drawable;
    public static final int[] STATE_ENABLE = { -android.R.attr.state_enabled };
    public static final int[] STATE_PRESSED = { android.R.attr.state_pressed };
    public static final int[] STATE_SELECTED = { android.R.attr.state_selected,
            -android.R.attr.state_checked };
    public static final int[] STATE_NOMARL = {};

    public ProgressBarHCT(Context context) {
        this(context, null);
    }

    public ProgressBarHCT(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyle);
    }

    public ProgressBarHCT(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, 0);
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "common_controller_color", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_pb);
        SetColor(color);
    }

    /**
     * @hide
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressLint("NewApi")
    public void SetColor(int color) {

        Resources r = mContext.getResources();
        Drawable bagtem = r.getDrawable(R.drawable.scrubber_track_hct_light);
        Drawable prgtem = r.getDrawable(R.drawable.scrubber_primary_hct_light);
        // bagtem.setTint(r.getColor(R.color.secondary_text_material_light));
        bagtem.setTint(0x10000000);
        prgtem.setTint(color);
        secondDrawable = new ScaleDrawable(prgtem, Gravity.LEFT, 1.0f, -1);
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

    @SuppressLint("NewApi")
    public void SetColor(int color, Drawable background, Bitmap forground) {

        // Resources r = mContext.getResources();
        // Drawable bagtem=r.getDrawable(R.drawable.scrubber_track_holo_dark);
        // Drawable prgtem=r.getDrawable(R.drawable.scrubber_primary_holo);
        // NinePatchDrawable prgsrc=(NinePatchDrawable) prgtem;
        // NinePatchState ninepathstat=(NinePatchState)
        // prgsrc.getConstantState();
        // NinePatch ninepatch=ninepathstat.getNinePatch();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)r.getDrawable(R.drawable.scrubber_primary_holo);

        // if(btDrawable !=null)
        // {
        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
        // R.drawable.scrubber_primary_holo);
        Bitmap btDrawable = JavaChanger.setColor(forground, color);
        NinePatch ninepatch2 = new NinePatch(btDrawable,
                forground.getNinePatchChunk());

        NinePatchDrawable testNinePatch = new NinePatchDrawable(ninepatch2);

        secondDrawable = new ScaleDrawable(testNinePatch, Gravity.LEFT, 1.0f,
                -1);
        // }/*else{
        // secondDrawable=new ScaleDrawable(prgtem, Gravity.LEFT, 1.0f, -1);
        // }
        Drawable[] draws = { background, background, secondDrawable };

        int[] ids = { android.R.id.background, android.R.id.secondaryProgress,
                android.R.id.progress };
        mProgressDrawable = new LayerDrawable(draws);
        mProgressDrawable.setId(0, android.R.id.background);
        mProgressDrawable.setId(1, android.R.id.secondaryProgress);
        mProgressDrawable.setId(2, android.R.id.progress);
        setProgressDrawable(mProgressDrawable);

    }

    @SuppressLint("NewApi")
    public ProgressBarHCT(Context context, AttributeSet attrs, int defStyle,
            int styleRes) {
        super(context, attrs, defStyle, styleRes);

    }

    /**
     * Converts a drawable to a tiled version of itself. It will recursively
     * traverse layer and state list drawables.
     */

    /*
     * private Drawable tileify(Drawable drawable, boolean clip) { if (drawable
     * instanceof LayerDrawable) { LayerDrawable background = (LayerDrawable)
     * drawable; final int N = background.getNumberOfLayers(); Drawable[]
     * outDrawables = new Drawable[N]; for (int i = 0; i < N; i++) { int id =
     * background.getId(i); outDrawables[i] = tileify(background.getDrawable(i),
     * (id == R.id.progress || id == R.id.secondaryProgress)); } LayerDrawable
     * newBg = new LayerDrawable(outDrawables); for (int i = 0; i < N; i++) {
     * newBg.setId(i, background.getId(i)); } return newBg; } else if (drawable
     * instanceof StateListDrawable) { StateListDrawable in =
     * (StateListDrawable) drawable; StateListDrawable out = new
     * StateListDrawable(); int numStates = in.getStateCount(); for (int i = 0;
     * i < numStates; i++) { out.addState(in.getStateSet(i),
     * tileify(in.getStateDrawable(i), clip)); } return out; } else if (drawable
     * instanceof BitmapDrawable) { final Bitmap tileBitmap = ((BitmapDrawable)
     * drawable).getBitmap(); if (mSampleTile == null) { mSampleTile =
     * tileBitmap; } final ShapeDrawable shapeDrawable = new ShapeDrawable(
     * getDrawableShape()); final BitmapShader bitmapShader = new
     * BitmapShader(tileBitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
     * shapeDrawable.getPaint().setShader(bitmapShader); return (clip) ? new
     * ClipDrawable(shapeDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL) :
     * shapeDrawable; } return drawable; } Shape getDrawableShape() { final
     * float[] roundedCorners = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 }; return
     * new RoundRectShape(roundedCorners, null, null); }
     */
    /**
     * Convert a AnimationDrawable for use as a barberpole animation. Each frame
     * of the animation is wrapped in a ClipDrawable and given a tiling
     * BitmapShader.
     */
    /*
     * private Drawable tileifyIndeterminate(Drawable drawable) { if (drawable
     * instanceof AnimationDrawable) { AnimationDrawable background =
     * (AnimationDrawable) drawable; final int N =
     * background.getNumberOfFrames(); AnimationDrawable newBg = new
     * AnimationDrawable(); newBg.setOneShot(background.isOneShot()); for (int i
     * = 0; i < N; i++) { Drawable frame = tileifyHCT(background.getFrame(i),
     * true); frame.setLevel(10000); newBg.addFrame(frame,
     * background.getDuration(i)); } newBg.setLevel(10000); drawable = newBg; }
     * return drawable; }
     */

}
