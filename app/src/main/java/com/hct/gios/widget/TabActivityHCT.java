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

import com.android.calendar.R;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.TabActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.content.res.Resources;
import android.widget.ImageView;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * <p>
 * For apps developing against {@link android.os.Build.VERSION_CODES#HONEYCOMB}
 * or later, tabs are typically presented in the UI using the new
 * {@link ActionBar#newTab() ActionBar.newTab()} and related APIs for placing
 * tabs within their action bar area.
 * </p>
 * 
 * <p>
 * A replacement for TabActivity can also be implemented by directly using
 * TabHost. You will need to define a layout that correctly uses a TabHost with
 * a TabWidget as well as an area in which to display your tab content. A
 * typical example would be:
 * </p>
 * 
 * {@sample development/samples/Support4Demos/res/layout/fragment_tabs.xml
 * complete}
 * 
 * <p>
 * The implementation needs to take over responsibility for switching the shown
 * content when the user switches between tabs.
 * 
 * {@sample
 * development/samples/Support4Demos/src/com/example/android/supportv4/app/
 * FragmentTabs.java complete}
 * 
 * <p>
 * Also see the <a href="{@docRoot}
 * resources/samples/Support4Demos/src/com/example
 * /android/supportv4/app/FragmentTabsPager.html"> Fragment Tabs Pager</a>
 * sample for an example of using the support library's ViewPager to allow the
 * user to swipe the content to switch between tabs.
 * </p>
 * 
 * @deprecated New applications should use Fragments instead of this class; to
 *             continue to run on older devices, you can use the v4 support
 *             library which provides a version of the Fragment API that is
 *             compatible down to {@link android.os.Build.VERSION_CODES#DONUT}.
 */
@Deprecated
public class TabActivityHCT extends TabActivity {
    int initActionBarFlag = 0;
    ViewGroup actionBarView;
    ImageView mArrowImage = null;
    int mColor = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setActionBarContentColor(getResources().getColor(R.color.common_controls_color),getResources().getColor(R.color.gos_common_acb_txt));

    }

    public void setIndicatorColor(int color) {
        int themeColor = getResources().getColor(R.color.gos_common_acb);
        if (getActionBar() == null) {
            getWindow().setStatusBarColor(themeColor);
        } else {

            getActionBar().setBackgroundDrawable(new ColorDrawable(themeColor));
            getActionBar().setDisplayUseLogoEnabled(false);
            getWindow().setStatusBarColor(themeColor);

        }
        setInitActionBarContentColor();

    }

    public void setIndicatorColor(boolean fillActionBar,
            boolean fillSplitAactionBar, int color) {

        setIndicatorColor(color);
    }

    public void setInitActionBarContentColor() {
        if (initActionBarFlag == 0)
            setActionBarContentColor(
                    getResources().getColor(R.color.gos_common_acb_icon),
                    getResources().getColor(R.color.gos_common_acb_txt));
    }

    public void setActionBarContentColor(final int color, final int textcolor) {
        // getActionBar().setBackgroundDrawable(new ColorDrawable(0xffffffff));
        initActionBarFlag = 1;
        if (getActionBar() == null) {
            return;
        }

        int actionbarId = Resources.getSystem().getIdentifier("action_bar",
                "id", "android");

        View decor = getWindow().getDecorView();
        if (decor != null) {

            Drawable dr = getResources().getDrawable(
                    R.drawable.ic_ab_back_material);
            dr.setTint(color);
            getActionBar().setHomeAsUpIndicator(dr);
            actionBarView = (ViewGroup) decor.findViewById(actionbarId);
            if (actionBarView == null) {
                return;
            }
            // actionBarView.setBackground(new ColorDrawable(0xff00ffff));
            actionBarView.setElevation(9.0f);
            actionBarView
                    .setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

                        @Override
                        public void onChildViewRemoved(View parent, View child) {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void onChildViewAdded(View parent, View child) {

                            setAllChildViewsColor(child, color, textcolor);

                        }
                    });

        }

    }

    public void setAllChildViewsColor(View view, final int color,
            final int textColor) {

        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            int searchId = Resources.getSystem().getIdentifier("search_bar",
                    "id", "android");
            if (searchId == vp.getId()) {
                return;
            }
            vp.setOnHierarchyChangeListener(new ViewGroup.OnHierarchyChangeListener() {

                @Override
                public void onChildViewRemoved(View parent, View child) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void onChildViewAdded(View parent, View child) {

                    setAllChildViewsColor(child, color, textColor);

                }
            });
            // vp.setBackground(new ColorDrawable(0xff00ff00));

            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                setAllChildViewsColor(viewchild, color, textColor);
            }

        } else {

            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();

                if (drawable != null) {
                    drawable.setTint(color);
                    ((ImageView) view).setImageDrawable(drawable);
                }
                // view.setBackgroundResource(R.drawable.item_background_material);
            } else if (view instanceof TextView) {

                ((TextView) view).setTextColor(textColor);

                Drawable[] dras = ((TextView) view).getCompoundDrawables();
                if (dras[0] != null) {
                    dras[0].setTint(color);
                    ((TextView) view).setCompoundDrawables(dras[0], null, null,
                            null);
                }
            }

            return;
        }

    }

    public void fillActionbarTab(boolean fillTab) {

    }
}