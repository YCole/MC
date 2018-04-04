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
import android.os.Build;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.View;
import android.widget.TextView;
import android.provider.Settings;

import com.android.calendar.R;
import com.hct.gios.widget.Utils;

/**
 * Used to group {@link Preference} objects and provide a disabled title above
 * the group.
 * 
 * <div class="special reference"> <h3>Developer Guides</h3>
 * <p>
 * For information about building a settings UI with Preferences, read the <a
 * href="{@docRoot}guide/topics/ui/settings.html">Settings</a> guide.
 * </p>
 * </div>
 */
public class PreferenceCategory extends PreferenceGroup {
    private static final String TAG = "PreferenceCategory";
    private int mColor = -1;

    public PreferenceCategory(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            try {
                mColor = context.getResources().getColor(R.color.gos_common_st);
            } catch (Exception e) {

            }
        } else {
            mColor = Settings.System.getInt(context.getContentResolver(),
                    "common_controller_color", Utils.DEFAULT_COLOR);
        }
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PreferenceCategory);
        mColor = a.getColor(R.styleable.PreferenceCategory_android_color,
                mColor);
        a.recycle();
    }

    public PreferenceCategory(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.preferenceCategoryStyle);
    }

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    @Override
    protected boolean onPrepareAddPreference(Preference preference) {
        if (preference instanceof PreferenceCategory) {
            throw new IllegalArgumentException("Cannot add a " + TAG
                    + " directly to a " + TAG);
        }

        return super.onPrepareAddPreference(preference);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        if (titleView != null) {
            titleView.setTextColor(mColor);
        }
    }

    public void SetColor(int color) {
        mColor = color;
    }
}
