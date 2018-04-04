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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.drawable.StateListDrawable;

import java.io.FileNotFoundException;

import com.android.calendar.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * <p>
 * A checkbox is a specific type of two-states button that can be either checked
 * or unchecked. A example usage of a checkbox inside your activity would be the
 * following:
 * </p>
 * 
 * <pre class="prettyprint">
 * public class MyActivity extends Activity {
 *     protected void onCreate(Bundle icicle) {
 *         super.onCreate(icicle);
 * 
 *         setContentView(R.layout.content_layout_id);
 * 
 *         final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_id);
 *         if (checkBox.isChecked()) {
 *             checkBox.setChecked(false);
 *         }
 *     }
 * }
 * </pre>
 * 
 * <p>
 * See the <a href="{@docRoot}
 * guide/topics/ui/controls/checkbox.html">Checkboxes</a> guide.
 * </p>
 * 
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
public class CheckBoxHCT extends CheckBox implements SetColorable {

    static BitmapDrawable mEN_CECDrawable;
    BitmapDrawable mEN_UNCECDrawable;
    static BitmapDrawable mDIS_CECDrawable;
    BitmapDrawable mDIS_UNCECDrawable;
    int storeColor;
    int bgColorflag = 0;
    int mBgColor = 0;

    public static final int[] STATE_ENABLE_UNCHECKED = {
            android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_ENABLE_CHECKED = {
            android.R.attr.state_enabled, android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_UNCHECKED = {
            -android.R.attr.state_enabled, -android.R.attr.state_checked };
    public static final int[] STATE_DISABLE_CHECKED = {
            -android.R.attr.state_enabled, android.R.attr.state_checked

    };

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public CheckBoxHCT(Context context) {
        this(context, null);
    }

    public CheckBoxHCT(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.checkboxStyle);
    }

    public CheckBoxHCT(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // color = Settings.System.getInt(context.getContentResolver(),
            // "color_ccl", Utils.DEFAULT_COLOR);
            color = context.getResources().getColor(R.color.gos_common_cb);
        } else {
            color = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CheckBox);
        color = a.getColor(R.styleable.CheckBox_android_color, color);
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

    public void SetColor(int color, int bgColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            bgColorflag = 1;
            mBgColor = bgColor;
            SetColorForAndroid50(color);
        } else {
            SetColorForAndroid4x(color);
        }
    }

    private void SetColorForAndroid50(int color) {
        AnimatedStateListDrawable mAnimatedStateListDrawable = new AnimatedStateListDrawable();
        // TransitionDrawable mTransitionDrawable = new TransitionDrawable();
        // AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        // item1
        BitmapDrawable btdrawable = (BitmapDrawable) getResources()
                .getDrawable(R.drawable.btn_check_to_on_mtrl_disable);
        btdrawable.setAlpha(66);
        btdrawable.setTint(color);// android:tint="?android:attr/colorControlActivated"
        mAnimatedStateListDrawable.addState(STATE_DISABLE_CHECKED, btdrawable,
                0);

        // item2
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_disable);
        btdrawable.setAlpha(66);

        btdrawable.setTint(0x8a000000);
        // android:tint="?android:attr/colorControlNormal"
        mAnimatedStateListDrawable.addState(STATE_DISABLE_UNCHECKED,
                btdrawable, 0);

        // item3
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_015);
        btdrawable.setTint(color);
        mAnimatedStateListDrawable.addState(STATE_ENABLE_CHECKED, btdrawable,
                R.id.on);

        // item4
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_000);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimatedStateListDrawable.addState(STATE_ENABLE_UNCHECKED, btdrawable,
                R.id.off);

        // transition 1
        int fromId = R.id.off;
        int toId = R.id.on;
        boolean reversible = false;
        AnimationDrawable mAnimationDrawable = new AnimationDrawable();

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_000);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_001);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_002);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_003);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_004);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_005);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_006);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_007);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_008);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_009);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_010);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_011);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);
        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_012);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_013);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_014);
        btdrawable.setTint(color);
        mAnimationDrawable.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_on_mtrl_015);
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
                R.drawable.btn_check_to_off_mtrl_000);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_001);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_002);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_003);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_004);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_005);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_006);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_007);
        btdrawable.setTint(color);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_008);
        btdrawable.setTint(0x8a000000);
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_009);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_010);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_011);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_012);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_013);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_014);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        btdrawable = (BitmapDrawable) getResources().getDrawable(
                R.drawable.btn_check_to_off_mtrl_015);
        if (bgColorflag == 1) {
            btdrawable.setTint(mBgColor);
        } else {
            btdrawable.setTint(0x8a000000);
        }// android
        mAnimationDrawable2.addFrame(btdrawable, 15);

        mAnimatedStateListDrawable.addTransition(fromId, toId,
                mAnimationDrawable2, reversible);

        setButtonDrawable(mAnimatedStateListDrawable);

    }

    private void SetColorForAndroid4x(int color) {
        StateListDrawable mTempDrawable = new StateListDrawable();

        mTempDrawable.addState(STATE_ENABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.btn_check_off_hct_light));
        if (mEN_CECDrawable == null || color != storeColor)
            mEN_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.btn_check_on_hct_light, color);
        Bitmap bt = mEN_CECDrawable.getBitmap();
        // BitmapDrawable
        // btdrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.btn_check_on_hct_light);
        // btdrawable.setBitmap(bt);
        BitmapDrawable tempDrawable = new BitmapDrawable(getResources(), bt);
        // Log.e("checkboxhct1","tempDrawable.getWidth="+tempDrawable.getBounds());
        mTempDrawable.addState(STATE_ENABLE_CHECKED, tempDrawable);

        // Log.e("checkboxhct2","btdrawable.getWidth="+btdrawable.getBounds());
        mTempDrawable.addState(STATE_DISABLE_UNCHECKED, getResources()
                .getDrawable(R.drawable.btn_check_off_disabled_hct_light));
        if (mDIS_CECDrawable == null || color != storeColor)
            mDIS_CECDrawable = (BitmapDrawable) ChangeColorDrawable(
                    R.drawable.btn_check_on_disabled_hct_light, color);
        bt = mDIS_CECDrawable.getBitmap();
        tempDrawable = new BitmapDrawable(getResources(), bt);
        mTempDrawable.addState(STATE_DISABLE_CHECKED, tempDrawable);
        setButtonDrawable(mTempDrawable);
        requestLayout();
        invalidate();

    }

    public Drawable ChangeColorDrawable(int id, int color) {
        Resources res = getResources();
        BitmapDrawable btdrawable = (BitmapDrawable) res.getDrawable(id);
        return new BitmapDrawable(res, JavaChanger.setColor(
                btdrawable.getBitmap(), color));
    }

}
