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

import android.widget.EditText;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.graphics.PorterDuff;
import android.content.res.Resources;
import android.util.Log;

import com.android.calendar.R;

/*
 * This is supposed to be a *very* thin veneer over TextView.
 * Do not make any changes here that do anything that a TextView
 * with a key listener and a movement method wouldn't do!
 */

/**
 * EditText is a thin veneer over TextView that configures itself to be
 * editable.
 * 
 * <p>
 * See the <a href="{@docRoot}guide/topics/ui/controls/text.html">Text
 * Fields</a> guide.
 * </p>
 * <p>
 * <b>XML attributes</b>
 * <p>
 * See {@link android.R.styleable#EditText EditText Attributes},
 * {@link android.R.styleable#TextView TextView Attributes},
 * {@link android.R.styleable#View View Attributes}
 */
public class EditTextHCT extends EditText {

    public static final int[] STATE_FOCUSED = { android.R.attr.state_focused };
    public static final int[] STATE_DEFAULT = { -android.R.attr.state_focused };

    private Context mContext;

    public EditTextHCT(Context context) {
        this(context, null);
    }

    public EditTextHCT(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public EditTextHCT(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public EditTextHCT(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        // int color = Settings.System.getInt(context.getContentResolver(),
        // "common_controller_color", Utils.DEFAULT_COLOR);
        int color = context.getResources().getColor(R.color.gos_common_sel_on);
        Log.e("EditTextHCT", "color =" + color);
        SetColor(color);
    }

    public void SetColor(int color) {
        StateListDrawable mDrawable = new StateListDrawable();
        Drawable npdrawable1 = (Drawable) getResources().getDrawable(
                R.drawable.textfield_default_mtrl_alpha);
        Drawable npdrawable2 = (Drawable) getResources().getDrawable(
                R.drawable.textfield_activated_mtrl_alpha);

        npdrawable1.setTintMode(PorterDuff.Mode.SRC_IN);
        npdrawable2.setTintMode(PorterDuff.Mode.SRC_IN);

        npdrawable1.setTint(0x1f000000);
        // npdrawable1.setAlpha(66);
        mDrawable.addState(STATE_DEFAULT, npdrawable1);

        npdrawable2.setTint(color);
        npdrawable2.setAlpha(255);
        mDrawable.addState(STATE_FOCUSED, npdrawable2);

        setBackgroundDrawable(mDrawable);
    }
}
