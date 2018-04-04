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

import android.app.ListActivity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListActivityHCT extends ListActivity {
    private LinearLayout mBackground;
    private LinearLayout mSplitBackground;
    private int mFullScreenSet = 0;
    private int indicatorFlag = 0;
    private LinearLayout.LayoutParams mLayoutParams = null;
    private FrameLayout.LayoutParams mFrameParams = null;
    private final int INDICATOR_HEIGHT = 25;
    private final int ACTIONBAR_HEIGHT = 52;
    private final int SPLIT_ACTIONBAR_HEIGHT = 52;
    private final int ACTIONBAR_TAB_HEIGHT = 52;
    private int mActionbarHeight = ACTIONBAR_HEIGHT;
    private int mActionbarTabHeight = 0;
    private int mSplitActionbarHeight = 0;
    private int mSpiltflag = 0;
    private final int SPLIT_ACTIONBAR_COLOR = 0xffe2e2e2;
    ToolBarHCT mToobar = null;
    int mScreenWidth = 0;
    public static int LEFT_BUTTON = 0;
    public static int RIGHT_BUTTON = 1;
    ViewGroup actionBarView;
    int initActionBarFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            // setTheme(R.style.Theme_HCT_Light);
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            mScreenWidth = metrics.widthPixels;
            mToobar = new ToolBarHCT(this, mScreenWidth);
            mToobar.setElevation(50);
            // mToobar.setBackgroundColor(0xffe2e2e2);
        } else {
            // setTheme(R.style.Theme_HCT_Light_Holo);
            mBackground = new LinearLayout(this);
            mSplitBackground = new LinearLayout(this);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // setActionBarContentColor(getResources().getColor(R.color.common_controls_color),getResources().getColor(R.color.actionbar_theme_textcolor));

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
            // actionBarView.setBackground(new ColorDrawable(0xff00ffff));
            if (actionBarView == null) {
                return;
            }
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
        int searchButton = Resources.getSystem().getIdentifier("search_button",
                "id", "android");
        int searchMag = Resources.getSystem().getIdentifier("search_mag_icon",
                "id", "android");
        int searchGo = Resources.getSystem().getIdentifier("search_go_btn",
                "id", "android");
        int searchVoice = Resources.getSystem().getIdentifier(
                "search_voice_btn", "id", "android");
        int searchClose = Resources.getSystem().getIdentifier(
                "search_close_btn", "id", "android");

        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            int searchId = Resources.getSystem().getIdentifier("search_bar",
                    "id", "android");
            if (searchId == vp.getId()) {
                // return;
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
                if (view.getId() != searchClose && view.getId() != searchButton
                        && view.getId() != searchMag
                        && view.getId() != searchGo
                        && view.getId() != searchVoice) {
                    Drawable drawable = ((ImageView) view).getDrawable();

                    if (drawable != null) {
                        drawable.setTint(color);
                        ((ImageView) view).setImageDrawable(drawable);
                    }
                    // view.setBackgroundResource(R.drawable.item_background_material);
                }
            } else if (view instanceof TextView) {

                ((TextView) view).setTextColor(textColor);
                TextView tv = (TextView) view;
                int searchId = Resources.getSystem().getIdentifier(
                        "search_src_text", "id", "android");
                if (searchId == tv.getId()) {
                    ((TextView) view).setTextColor(getResources().getColor(
                            R.color.gos_common_acb_tf_txt));
                    ((TextView) view).setHintTextColor(getResources().getColor(
                            R.color.gos_common_acb_tf_txt_watermark));
                }

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

    public void setContentView(int layoutResID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            super.setContentView(layoutResID);
        } else {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(layoutResID, null);
            setContentView(view);
        }
    }

    public void setContentView(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            super.setContentView(view);
        } else {
            if (indicatorFlag == 0) {
                if (mFullScreenSet == 0) {
                    LinearLayout mainLayout = new LinearLayout(this);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    if (mBackground.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) mBackground.getParent();
                        vgroup.removeView(mBackground);
                    }
                    if (mLayoutParams == null) {
                        mainLayout.addView(mBackground,
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        Utils.dpToPx(this, 77)));
                    } else {
                        mainLayout.addView(mBackground, mLayoutParams);
                    }
                    if (view.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) view.getParent();
                        vgroup.removeView(view);
                    }
                    mainLayout.addView(view, new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                    if (mSplitBackground.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) mSplitBackground
                                .getParent();
                        vgroup.removeView(mSplitBackground);
                    }
                    mainLayout.addView(mSplitBackground);

                    super.setContentView(mainLayout);
                    FusrceenActivity.assistActivity(this);
                } else {
                    FrameLayout layout = new FrameLayout(this);
                    layout.addView(view, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(mBackground, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));

                    super.setContentView(layout);
                    FusrceenActivity.assistActivity(this);
                }
            } else {
                super.setContentView(view);
            }
        }

    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            super.setContentView(view, params);
        } else {
            if (indicatorFlag == 0) {
                if (mFullScreenSet == 0) {
                    LinearLayout mainLayout = new LinearLayout(this);
                    mainLayout.setOrientation(LinearLayout.VERTICAL);
                    if (mBackground.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) mBackground.getParent();
                        vgroup.removeView(mBackground);
                    }
                    if (mLayoutParams == null) {
                        mainLayout.addView(mBackground,
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        Utils.dpToPx(this, 77)));
                    } else {
                        mainLayout.addView(mBackground, mLayoutParams);
                    }
                    if (view.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) view.getParent();
                        vgroup.removeView(view);
                    }
                    mainLayout.addView(view, new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                    if (mSplitBackground.getParent() != null) {
                        ViewGroup vgroup = (ViewGroup) mSplitBackground
                                .getParent();
                        vgroup.removeView(mSplitBackground);
                    }
                    mainLayout.addView(mSplitBackground);

                    super.setContentView(mainLayout, params);
                    FusrceenActivity.assistActivity(this);
                } else {
                    FrameLayout layout = new FrameLayout(this);
                    layout.addView(view, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    layout.addView(mBackground, new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
                    super.setContentView(layout, params);
                    FusrceenActivity.assistActivity(this);
                }
            } else {
                super.setContentView(view, params);
            }
        }

    }

    public void setIndicatorColorChange(int color) {

        int themeColor = color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            if (getActionBar() == null) {
                getWindow().setStatusBarColor(themeColor);
            } else {

                getActionBar().setBackgroundDrawable(
                        new ColorDrawable(themeColor));
                getActionBar().setDisplayUseLogoEnabled(false);
                getWindow().setStatusBarColor(themeColor);

            }
        } else {
            if (mFullScreenSet == 1) {
                if (getActionBar() == null) {
                    mFrameParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT));
                    mBackground.setLayoutParams(mFrameParams);
                    mBackground.setBackgroundColor(color);
                } else {
                    mFrameParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT
                                    + mActionbarHeight));
                    mBackground.setLayoutParams(mFrameParams);
                    getActionBar().setBackgroundDrawable(
                            new ColorDrawable(color));
                    mBackground.setBackgroundColor(color);
                    getActionBar().setDisplayUseLogoEnabled(false);

                    FrameLayout.LayoutParams splitFrameLayoutParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, mSplitActionbarHeight));
                    mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
                    mSplitBackground.setLayoutParams(splitFrameLayoutParams);
                }
            } else {
                if (getActionBar() == null) {
                    mLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT));
                    mBackground.setLayoutParams(mLayoutParams);
                    mBackground.setBackgroundColor(color);
                } else {
                    mLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT
                                    + mActionbarHeight));
                    mBackground.setLayoutParams(mLayoutParams);
                    getActionBar().setBackgroundDrawable(
                            new ColorDrawable(color));
                    mBackground.setBackgroundColor(color);
                    getActionBar().setDisplayUseLogoEnabled(false);

                    LinearLayout.LayoutParams mSplitLinearLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, mSplitActionbarHeight));
                    mSplitBackground.setLayoutParams(mSplitLinearLayoutParams);
                    mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
                }
            }
        }
        setInitActionBarContentColor();

    }

    public void setIndicatorColor(int color) {

        int themeColor = getResources().getColor(R.color.gos_common_acb);
        // int themeColor=color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            if (getActionBar() == null) {
                getWindow().setStatusBarColor(themeColor);
            } else {

                getActionBar().setBackgroundDrawable(
                        new ColorDrawable(themeColor));
                getActionBar().setDisplayUseLogoEnabled(false);
                getWindow().setStatusBarColor(themeColor);

            }
        } else {
            if (mFullScreenSet == 1) {
                if (getActionBar() == null) {
                    mFrameParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT));
                    mBackground.setLayoutParams(mFrameParams);
                    mBackground.setBackgroundColor(color);
                } else {
                    mFrameParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT
                                    + mActionbarHeight));
                    mBackground.setLayoutParams(mFrameParams);
                    getActionBar().setBackgroundDrawable(
                            new ColorDrawable(color));
                    mBackground.setBackgroundColor(color);
                    getActionBar().setDisplayUseLogoEnabled(false);

                    FrameLayout.LayoutParams splitFrameLayoutParams = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, mSplitActionbarHeight));
                    mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
                    mSplitBackground.setLayoutParams(splitFrameLayoutParams);
                }
            } else {
                if (getActionBar() == null) {
                    mLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT));
                    mBackground.setLayoutParams(mLayoutParams);
                    mBackground.setBackgroundColor(color);
                } else {
                    mLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, INDICATOR_HEIGHT
                                    + mActionbarHeight));
                    mBackground.setLayoutParams(mLayoutParams);
                    getActionBar().setBackgroundDrawable(
                            new ColorDrawable(color));
                    mBackground.setBackgroundColor(color);
                    getActionBar().setDisplayUseLogoEnabled(false);

                    LinearLayout.LayoutParams mSplitLinearLayoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(this, mSplitActionbarHeight));
                    mSplitBackground.setLayoutParams(mSplitLinearLayoutParams);
                    mSplitBackground.setBackgroundColor(SPLIT_ACTIONBAR_COLOR);
                }
            }
        }
        setInitActionBarContentColor();

    }

    public void setIndicatorColor(boolean fillActionBar,
            boolean fillSplitAactionBar, int color) {

        setIndicatorColor(color);
    }

    public void addBottomBarOptionMenu(int resId) {
        mToobar.inflateMenu(resId);
        MenuClicker mc = new MenuClicker(this);
        mToobar.setVisibility(View.VISIBLE);
        mToobar.setOnMenuItemClickListener(mc);
        ViewGroup view = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content))
                .getChildAt(0);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        // mLayoutParams.bottomMargin=Utils.dpToPx(this, mSplitActionbarHeight);

        LinearLayout.LayoutParams mToobarParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mToobarParams.bottomMargin = 0;

        if (view.getParent() != null) {
            ViewGroup vgroup = (ViewGroup) view.getParent();
            vgroup.removeView(view);
        }
        layout.addView(view, mLayoutParams);
        if (mToobar.getParent() != null) {
            ViewGroup vgroup = (ViewGroup) mToobar.getParent();
            vgroup.removeView(mToobar);
        }
        layout.addView(mToobar, mToobarParams);
        mSpiltflag = 1;
        setContentView(layout);
    }

    public void fillActionbarTab(boolean fillTab) {

    }

    public void SetBottomBarVisible(boolean visible) {
        if (visible) {
            mToobar.setVisibility(View.VISIBLE);

        } else {
            mToobar.setVisibility(View.GONE);

        }
    }

    public void SetBottomButtonVisible(int poistion, boolean visible) {
        if (poistion == LEFT_BUTTON) {
            if (visible) {
                mToobar.btnLeft.setVisibility(View.VISIBLE);
                mToobar.divider.setVisibility(View.VISIBLE);
            } else {
                mToobar.btnLeft.setVisibility(View.GONE);
                mToobar.divider.setVisibility(View.GONE);
            }
        } else {
            if (visible) {
                mToobar.btnRight.setVisibility(View.VISIBLE);
                mToobar.divider.setVisibility(View.VISIBLE);
            } else {
                mToobar.btnRight.setVisibility(View.GONE);
                mToobar.divider.setVisibility(View.GONE);
            }
        }
    }

    public void SetBottomButtonEnable(int poistion, boolean enable) {
        if (poistion == LEFT_BUTTON) {
            if (enable) {
                mToobar.btnLeft.setEnabled(true);

            } else {
                mToobar.btnLeft.setEnabled(false);
            }
        } else {
            if (enable) {
                mToobar.btnRight.setEnabled(true);

            } else {
                mToobar.btnRight.setEnabled(false);
            }
        }
    }

    public void SetBottomButtonText(int poistion, String title) {
        if (poistion == LEFT_BUTTON) {
            (mToobar.btnLeft).setText(title);

        } else {

            (mToobar.btnRight).setText(title);

        }
    }

}
