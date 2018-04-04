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

import android.content.Context;
import android.content.res.TypedArray;
import com.hct.gios.preference.TwoStatePreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Checkable;
import com.android.calendar.R;
import android.util.Log;

/**
 * A {@link Preference} that provides checkbox widget functionality.
 * <p>
 * This preference will store a boolean into the SharedPreferences.
 * 
 * @attr ref android.R.styleable#CheckBoxPreference_summaryOff
 * @attr ref android.R.styleable#CheckBoxPreference_summaryOn
 * @attr ref android.R.styleable#CheckBoxPreference_disableDependentsState
 */
public class CheckBoxPreferenceHCT extends TwoStatePreference {

    CheckBoxHCT mCheckBox = null;
    int mColor = -1;

    public CheckBoxPreferenceHCT(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CheckBoxPreferenceHCT(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CheckBoxPreference, defStyleAttr, defStyleRes);
        setSummaryOn(a
                .getString(android.R.styleable.CheckBoxPreference_summaryOn));
        setSummaryOff(a
                .getString(android.R.styleable.CheckBoxPreference_summaryOff));
        setDisableDependentsState(a.getBoolean(
                android.R.styleable.CheckBoxPreference_disableDependentsState,
                false));
        a.recycle();
    }

    public CheckBoxPreferenceHCT(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.checkBoxPreferenceStyle);
    }

    public CheckBoxPreferenceHCT(Context context) {
        this(context, null);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Log.e("CheckBoxPreferenceHCT",
                "onBindView view=" + view.findViewById(android.R.id.checkbox));
        mCheckBox = (CheckBoxHCT) view.findViewById(android.R.id.checkbox);
        if (mColor != -1)
            mCheckBox.SetColor(mColor);

        if (mCheckBox != null && mCheckBox instanceof Checkable) {
            ((Checkable) mCheckBox).setChecked(isChecked());
        }
        syncSummaryView(view);
    }

    public void SetColor(int color) {
        // mCheckBox.SetColor(color);
        mColor = color;
    }

}
