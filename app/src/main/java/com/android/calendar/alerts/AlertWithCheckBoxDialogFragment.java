/*
 * Copyright (C) 2013 Google Inc.
 * Licensed to The Android Open Source Project.
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

package com.android.calendar.alerts;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.android.calendar.R;
import com.hct.gios.widget.AlertDialog;
import com.hct.gios.widget.CheckBoxHCT;

/**
 * A {@link DialogFragment} that shows multiple values, and lets you select 0 to
 * {@link #MAX_SELECTED_VALUES} of them.
 */
public class AlertWithCheckBoxDialogFragment extends DialogFragment {

    public static final String SHARED_PREFERENCE_NAME = "AlertModeProvider";
    public static final String NOTICE_PERMISSION_MODE = "notice_permission_mode";
    private CheckBoxHCT never_mind = null;

    // Public no-args constructor needed for fragment re-instantiation
    public AlertWithCheckBoxDialogFragment() {
    };

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Context context = getActivity();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.alert_with_checkbox_dialog, null);

        SharedPreferences MyPreferences = context.getSharedPreferences(
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        final Editor editor = MyPreferences.edit();
        never_mind = (CheckBoxHCT) view.findViewById(R.id.donot_remind);
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        R.style.Theme_HCT_Light_Dialog_Alert));
        builder.setTitle(R.string.warm_attention_title)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton(R.string.quit_action, new OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog,
                            final int which) {
                        getActivity().finish();
                    }
                })
                .setPositiveButton(R.string.accept_action,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                    final int whichButton) {
                                editor.putBoolean(NOTICE_PERMISSION_MODE,
                                        never_mind.isChecked());
                                editor.commit();
                            }
                        });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                    return true; // pretend we've processed it
                else
                    return false; // pass on to be processed as normal
            }
        });

        return dialog;
    }
}
