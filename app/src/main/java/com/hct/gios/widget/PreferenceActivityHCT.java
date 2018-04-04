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
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.android.calendar.R;
import com.hct.gios.preference.PreferenceActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is the base class for an activity to show a hierarchy of preferences to
 * the user. Prior to {@link android.os.Build.VERSION_CODES#HONEYCOMB} this
 * class only allowed the display of a single set of preference; this
 * functionality should now be found in the new {@link PreferenceFragment}
 * class. If you are using PreferenceActivity in its old mode, the documentation
 * there applies to the deprecated APIs here.
 * 
 * <p>
 * This activity shows one or more headers of preferences, each of which is
 * associated with a {@link PreferenceFragment} to display the preferences of
 * that header. The actual layout and display of these associations can however
 * vary; currently there are two major approaches it may take:
 * 
 * <ul>
 * <li>On a small screen it may display only the headers as a single list when
 * first launched. Selecting one of the header items will re-launch the activity
 * with it only showing the PreferenceFragment of that header.
 * <li>On a large screen in may display both the headers and current
 * PreferenceFragment together as panes. Selecting a header item switches to
 * showing the correct PreferenceFragment for that item.
 * </ul>
 * 
 * <p>
 * Subclasses of PreferenceActivity should implement {@link #onBuildHeaders} to
 * populate the header list with the desired items. Doing this implicitly
 * switches the class into its new "headers + fragments" mode rather than the
 * old style of just showing a single preferences list.
 * 
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>
 * For information about using {@code PreferenceActivity},
 * read the <a href="{@docRoot}, read the <a
 * href="{@docRoot}, read the <a
 * href="{@docRoot}, read the <a href="{@docRoot}
 * guide/topics/ui/settings.html">Settings</a> guide.
 * </p>
 * </div>
 * 
 * <a name="SampleCode"></a> <h3>Sample Code</h3>
 * 
 * <p>
 * The following sample code shows a simple preference activity that has two
 * different sets of preferences. The implementation, consisting of the activity
 * itself as well as its two preference fragments is:
 * </p>
 * 
 * {@sample
 * development/samples/ApiDemos/src/com/example/android/apis/preference/
 * PreferenceWithHeaders.java activity}
 * 
 * <p>
 * The preference_headers resource describes the headers to be displayed and the
 * fragments associated with them. It is:
 * 
 * {@sample development/samples/ApiDemos/res/xml/preference_headers.xml headers}
 * 
 * <p>
 * The first header is shown by Prefs1Fragment, which populates itself from the
 * following XML resource:
 * </p>
 * 
 * {@sample development/samples/ApiDemos/res/xml/fragmented_preferences.xml
 * preferences}
 * 
 * <p>
 * Note that this XML resource contains a preference screen holding another
 * fragment, the Prefs1FragmentInner implemented here. This allows the user to
 * traverse down a hierarchy of preferences; pressing back will pop each
 * fragment off the stack to return to the previous preferences.
 * 
 * <p>
 * See {@link PreferenceFragment} for information on implementing the fragments
 * themselves.
 */
public abstract class PreferenceActivityHCT extends PreferenceActivity {
    private LinearLayout mBackground;
    private LinearLayout.LayoutParams mLayoutParams = null;
    private int flag = 0;
    private LinearLayout mSplitBackground;
    private final int INDICATOR_HEIGHT = 25;
    private final int ACTIONBAR_HEIGHT = 52;
    private final int SPLIT_ACTIONBAR_HEIGHT = 52;
    private int mActionbarHeight = ACTIONBAR_HEIGHT;
    private int mSplitActionbarHeight = 0;
    private final int SPLIT_ACTIONBAR_COLOR = 0xffe2e2e2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // setTheme(R.style.Theme_HCT_Light);

        } else {
            // setTheme(R.style.Theme_HCT_Light_Holo);

        }

    }

    public void fillActionbarTab(boolean fillTab) {

    }

    public void setIndicatorColor(boolean fillActionBar,
            boolean fillSplitAactionBar, int color) {
        /*
         * if(fillActionBar){ mActionbarHeight = ACTIONBAR_HEIGHT; } else {
         * mActionbarHeight = 0; }
         * 
         * if(fillSplitAactionBar){ mSplitActionbarHeight =
         * SPLIT_ACTIONBAR_HEIGHT; } else { mSplitActionbarHeight = 0; }
         */

        setIndicatorColor(fillActionBar, fillSplitAactionBar, color);
    }

}
