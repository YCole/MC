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

package com.hct.gios.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.os.Build;
import android.provider.Settings;

import com.hct.gios.widget.Utils;
import com.hct.gios.widget.SeekBarHCT;

import com.android.calendar.R;

/**
 * @hide
 */
public class SeekBarDialogPreference extends DialogPreference {
    private static final String TAG = "SeekBarDialogPreference";

    private Drawable mMyIcon;
    private int mColor = -1;

    public SeekBarDialogPreference(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            mColor = context.getResources().getColor(R.color.gos_common_pb);
            // mColor = Settings.System.getInt(context.getContentResolver(),
            // "color_ccl", Utils.DEFAULT_COLOR);
        } else {
            mColor = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SeekBarDialogPreference);
        mColor = a.getColor(R.styleable.SeekBarDialogPreference_android_color,
                mColor);
        a.recycle();

        setDialogLayoutResource(R.layout.seekbar_dialog);
        createActionButtons();

        // Steal the XML dialogIcon attribute's value
        mMyIcon = getDialogIcon();
        setDialogIcon(null);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SeekBarDialogPreference(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public SeekBarDialogPreference(Context context) {
        this(context, null);
    }

    // Allow subclasses to override the action buttons
    public void createActionButtons() {
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        final ImageView iconView = (ImageView) view
                .findViewById(android.R.id.icon);
        if (mMyIcon != null) {
            iconView.setImageDrawable(mMyIcon);
        } else {
            iconView.setVisibility(View.GONE);
        }

        final SeekBarHCT seekBar = (SeekBarHCT) view.findViewById(R.id.seekbar);
        if (seekBar != null) {
            seekBar.SetColor(mColor);
        }
    }

    protected static SeekBarHCT getSeekBar(View dialogView) {
        return (SeekBarHCT) dialogView.findViewById(R.id.seekbar);
    }

    public void SetColor(int color) {
        mColor = color;
    }
}
