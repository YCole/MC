/*
 * Copyright (C) 2014 The Android Open Source Project
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

import android.annotation.NonNull;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.CollapsibleActionView;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ActionMenuView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ActionMenuPresenter;

import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.SubMenuBuilder;
import com.android.internal.widget.DecorToolbar;
import com.android.calendar.R;

import java.util.ArrayList;
import java.util.List;

public class TabButtonBar extends ViewGroup {
    private static final String TAG = "TabButtonBar";

    private ActionMenuView mMenuView;
    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private ImageButton mNavButtonView;
    private ImageButton mSelelctButtonView;
    private ImageView mLogoView;
    private PagerSecond mTabBar;
    private int mActionFlag = 0;
    private int mActionColor = 0;
    private int mCurrentMode = 0;

    private Drawable mCollapseIcon;
    private ImageButton mCollapseButtonView;
    View mExpandedActionView;

    /** Context against which to inflate popup menus. */
    private Context mPopupContext;

    /** Theme resource against which to inflate popup menus. */
    private int mPopupTheme;

    private int mTitleTextAppearance;
    private int mSubtitleTextAppearance;
    private int mNavButtonStyle;

    private int mButtonGravity;

    private int mMaxButtonHeight;

    private int mTitleMarginStart;
    private int mTitleMarginEnd;
    private int mTitleMarginTop;
    private int mTitleMarginBottom;

    // private final RtlSpacingHelper mContentInsets = new RtlSpacingHelper();

    private int mGravity = Gravity.START | Gravity.CENTER_VERTICAL;

    private CharSequence mTitleText;
    private CharSequence mSubtitleText;

    private int mTitleTextColor;
    private int mSubtitleTextColor;
    private int mNavColor;
    private int mTitleColor;

    private boolean mEatingTouch;

    // Clear me after use.
    private final ArrayList<View> mTempViews = new ArrayList<View>();

    private final int[] mTempMargins = new int[2];

    private OnMenuItemClickListener mOnMenuItemClickListener;

    private final ActionMenuView.OnMenuItemClickListener mMenuViewItemClickListener = new ActionMenuView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mOnMenuItemClickListener != null) {
                return mOnMenuItemClickListener.onMenuItemClick(item);
            }
            return false;
        }
    };

    // private ToolbarWidgetWrapper mWrapper;
    private ActionMenuPresenter mOuterActionMenuPresenter;
    private ExpandedActionViewMenuPresenter mExpandedMenuPresenter;
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private MenuBuilder.Callback mMenuBuilderCallback;

    private boolean mCollapsible;

    private final Runnable mShowOverflowMenuRunnable = new Runnable() {
        @Override
        public void run() {
            showOverflowMenu();
        }
    };

    public void setCurrentMode(int mode) {
        mCurrentMode = mode;
        if (mCurrentMode == 1) {
            ensureNavButtonView();
            ensureTitleView();
            ensureSelectButtonView();
            if (mTabBar != null && mTabBar.getParent() != null) {
                removeView(mTabBar);
            }
            if (mMenuView != null && mMenuView.getParent() != null) {
                // removeView(mMenuView);
                mMenuView.setVisibility(View.GONE);
            }
            if (mCollapseButtonView != null
                    && mCollapseButtonView.getParent() != null) {
                // removeView(mCollapseButtonView);
                mCollapseButtonView.setVisibility(View.GONE);
            }
            if (mExpandedActionView != null
                    && mExpandedActionView.getParent() != null) {
                // removeView(mExpandedActionView);
                mExpandedActionView.setVisibility(View.GONE);
            }

        } else {
            if (mTitleTextView != null && mTitleTextView.getParent() != null) {
                removeView(mTitleTextView);
            }
            if (mNavButtonView != null && mNavButtonView.getParent() != null) {
                removeView(mNavButtonView);
            }
            if (mSelelctButtonView != null
                    && mSelelctButtonView.getParent() != null) {
                removeView(mSelelctButtonView);
            }
            getTabBar();
            if (mMenuView != null && mMenuView.getParent() != null) {
                // addSystemView(mMenuView);
                mMenuView.setVisibility(View.VISIBLE);
            }
            if (mCollapseButtonView != null
                    && mCollapseButtonView.getParent() != null) {
                // addSystemView(mCollapseButtonView);
                mCollapseButtonView.setVisibility(View.VISIBLE);
            }
            if (mExpandedActionView != null
                    && mExpandedActionView.getParent() != null) {
                // addSystemView(mExpandedActionView);
                mExpandedActionView.setVisibility(View.VISIBLE);
            }
        }

    }

    public void setActionModeColor(int color, int textColor) {
        mNavColor = color;
        mTitleColor = textColor;

    }

    public ImageButton getSelectAllButton() {
        return mSelelctButtonView;
    }

    public ImageButton getNavButton() {
        return mNavButtonView;
    }

    private void ensureNavButtonView() {
        if (mNavButtonView == null) {
            mNavButtonView = new ImageButton(getContext());
            mNavButtonView.setImageResource(R.drawable.ic_ab_back_material);
            mNavButtonView.setBackgroundColor(0x00000000);
            Drawable dr = mNavButtonView.getDrawable();
            dr.setTint(mNavColor);
            final LayoutParams lp = generateDefaultLayoutParams();
            lp.gravity = Gravity.START
                    | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            mNavButtonView.setLayoutParams(lp);
            /*
             * mNavButtonView.setOnClickListener(new View.OnClickListener() {
             * 
             * @Override public void onClick(View arg0) { // TODO Auto-generated
             * method stub setCurrentMode(0); } });
             */
            addSystemView(mNavButtonView);
        } else {
            addSystemView(mNavButtonView);
        }
    }

    private void ensureTitleView() {
        if (mTitleTextView == null) {
            final Context context = getContext();
            mTitleTextView = new TextView(context);
            mTitleTextView.setSingleLine();

            mTitleTextView
                    .setTextAppearance(
                            context,
                            android.R.style.TextAppearance_Material_Widget_Toolbar_Title);
            mTitleColor = getResources().getColor(R.color.gos_common_acb_txt);
            mTitleTextView.setTextColor(mTitleColor);
            mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            mTitleTextView.setTextSize(14);
            final LayoutParams lp = generateDefaultLayoutParams();
            mTitleTextView.setLayoutParams(lp);
            addSystemView(mTitleTextView);
        } else {
            mTitleColor = getResources().getColor(R.color.gos_common_acb_txt);
            mTitleTextView.setTextColor(mTitleColor);
            addSystemView(mTitleTextView);
        }
    }

    private void ensureSelectButtonView() {
        if (mSelelctButtonView == null) {
            mSelelctButtonView = new ImageButton(getContext());
            mSelelctButtonView.setImageResource(R.drawable.done_all);
            mSelelctButtonView.setBackgroundColor(0x00000000);
            Drawable dr = mSelelctButtonView.getDrawable();
            dr.setTint(mNavColor);
            final LayoutParams lp = generateDefaultLayoutParams();
            lp.gravity = Gravity.START
                    | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            mSelelctButtonView.setLayoutParams(lp);
            addSystemView(mSelelctButtonView);
        } else {
            addSystemView(mSelelctButtonView);
        }
    }

    public void setTitle(String str) {
        if (mTitleTextView != null) {
            mTitleTextView.setText(str);
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
    }

    public TabButtonBar(Context context) {
        this(context, null);
    }

    public TabButtonBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabButtonBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TabButtonBar(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setActionMenuColor(getResources().getColor(R.color.gos_common_acb_icon));
        setBackgroundColor(getResources().getColor(R.color.gos_common_acb));
        int elevation = getResources().getDimensionPixelSize(
                R.dimen.actionbar_elevation);
        setElevation(elevation);

    }

    /**
     * Specifies the theme to use when inflating popup menus. By default, uses
     * the same theme as the toolbar itself.
     * 
     * @param resId
     *            theme used to inflate popup menus
     * @see #getPopupTheme()
     */
    public void setPopupTheme(int resId) {
        if (mPopupTheme != resId) {
            mPopupTheme = resId;
            if (resId == 0) {
                mPopupContext = mContext;
            } else {
                mPopupContext = new ContextThemeWrapper(mContext, resId);
            }
        }
    }

    /**
     * @return resource identifier of the theme used to inflate popup menus, or
     *         0 if menus are inflated against the toolbar theme
     * @see #setPopupTheme(int)
     */
    public int getPopupTheme() {
        return mPopupTheme;
    }

    /**
     * @hide
     */
    public boolean canShowOverflowMenu() {
        return getVisibility() == VISIBLE && mMenuView != null
                && mMenuView.isOverflowReserved();
    }

    /**
     * Check whether the overflow menu is currently showing. This may not
     * reflect a pending show operation in progress.
     * 
     * @return true if the overflow menu is currently showing
     */
    public boolean isOverflowMenuShowing() {
        return mMenuView != null && mMenuView.isOverflowMenuShowing();
    }

    /**
     * @hide
     */
    public boolean isOverflowMenuShowPending() {
        return mMenuView != null && mMenuView.isOverflowMenuShowPending();
    }

    /**
     * Show the overflow items from the associated menu.
     * 
     * @return true if the menu was able to be shown, false otherwise
     */
    public boolean showOverflowMenu() {
        return mMenuView != null && mMenuView.showOverflowMenu();
    }

    /**
     * Hide the overflow items from the associated menu.
     * 
     * @return true if the menu was able to be hidden, false otherwise
     */
    public boolean hideOverflowMenu() {
        return mMenuView != null && mMenuView.hideOverflowMenu();
    }

    public void dismissPopupMenus() {
        if (mMenuView != null) {
            mMenuView.dismissPopupMenus();
        }
    }

    public boolean hasExpandedActionView() {
        return mExpandedMenuPresenter != null
                && mExpandedMenuPresenter.mCurrentExpandedItem != null;
    }

    /**
     * Collapse a currently expanded action view. If this Toolbar does not have
     * an expanded action view this method has no effect.
     * 
     * <p>
     * An action view may be expanded either directly from the
     * {@link android.view.MenuItem MenuItem} it belongs to or by user action.
     * </p>
     * 
     * @see #hasExpandedActionView()
     */
    public void collapseActionView() {
        final MenuItemImpl item = mExpandedMenuPresenter == null ? null
                : mExpandedMenuPresenter.mCurrentExpandedItem;
        if (item != null) {
            item.collapseActionView();
        }
    }

    public boolean collapseActionViewBack() {
        if (hasExpandedActionView()) {
            collapseActionView();
            return true;
        }
        return false;
    }

    public Menu getMenu() {
        ensureMenu();
        return mMenuView.getMenu();
    }

    private void ensureMenu() {
        ensureMenuView();
        if (mMenuView.peekMenu() == null) {
            // Initialize a new menu for the first time.
            final MenuBuilder menu = (MenuBuilder) mMenuView.getMenu();
            if (mExpandedMenuPresenter == null) {
                mExpandedMenuPresenter = new ExpandedActionViewMenuPresenter();
            }
            mMenuView.setExpandedActionViewsExclusive(true);
            menu.addMenuPresenter(mExpandedMenuPresenter, mPopupContext);
        }
    }

    private void ensureMenuView() {
        if (mMenuView == null) {
            mMenuView = new ActionMenuView(getContext());
            mMenuView.setPopupTheme(mPopupTheme);
            mMenuView.setOnMenuItemClickListener(mMenuViewItemClickListener);
            mMenuView.setMenuCallbacks(mActionMenuPresenterCallback,
                    mMenuBuilderCallback);
            final LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);// generateDefaultLayoutParams();
            lp.gravity = Gravity.RIGHT
                    | (mButtonGravity & Gravity.VERTICAL_GRAVITY_MASK);
            // lp.gravity = Gravity.RIGHT ;
            mMenuView.setLayoutParams(lp);
            addSystemView(mMenuView);
        }
    }

    private int layoutChildLeft(View child, int left, int[] collapsingMargins,
            int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int l = lp.leftMargin - collapsingMargins[0];
        left += Math.max(0, l);
        collapsingMargins[0] = Math.max(0, -l);
        final int top = getChildTop(child, alignmentHeight);
        final int childWidth = child.getMeasuredWidth();
        child.layout(left, top, left + childWidth,
                top + child.getMeasuredHeight());
        left += childWidth + lp.rightMargin;
        return left;
    }

    private int layoutChildRight(View child, int right,
            int[] collapsingMargins, int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int r = lp.rightMargin - collapsingMargins[1];
        right -= Math.max(0, r);
        collapsingMargins[1] = Math.max(0, -r);
        final int top = getChildTop(child, alignmentHeight);
        final int childWidth = child.getMeasuredWidth();

        child.layout(right - childWidth, top, right,
                top + child.getMeasuredHeight());
        right -= childWidth + lp.leftMargin;
        return right;
    }

    private int getChildVerticalGravity(int gravity) {
        final int vgrav = gravity & Gravity.VERTICAL_GRAVITY_MASK;
        switch (vgrav) {
        case Gravity.TOP:
        case Gravity.BOTTOM:
        case Gravity.CENTER_VERTICAL:
            return vgrav;
        default:
            return mGravity & Gravity.VERTICAL_GRAVITY_MASK;
        }
    }

    private int getChildTop(View child, int alignmentHeight) {
        final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        final int childHeight = child.getMeasuredHeight();
        final int alignmentOffset = alignmentHeight > 0 ? (childHeight - alignmentHeight) / 2
                : 0;
        switch (getChildVerticalGravity(lp.gravity)) {
        case Gravity.TOP:
            return getPaddingTop() - alignmentOffset;

        case Gravity.BOTTOM:
            return getHeight() - getPaddingBottom() - childHeight
                    - lp.bottomMargin - alignmentOffset;

        default:
        case Gravity.CENTER_VERTICAL:
            final int paddingTop = getPaddingTop();
            final int paddingBottom = getPaddingBottom();
            final int height = getHeight();
            final int space = height - paddingTop - paddingBottom;
            int spaceAbove = (space - childHeight) / 2;
            if (spaceAbove < lp.topMargin) {
                spaceAbove = lp.topMargin;
            } else {
                final int spaceBelow = height - paddingBottom - childHeight
                        - spaceAbove - paddingTop;
                if (spaceBelow < lp.bottomMargin) {
                    spaceAbove = Math.max(0, spaceAbove
                            - (lp.bottomMargin - spaceBelow));
                }
            }
            return paddingTop + spaceAbove;
        }
    }

    private MenuInflater getMenuInflater() {
        return new MenuInflater(getContext());
    }

    /**
     * Inflate a menu resource into this toolbar.
     * 
     * <p>
     * Inflate an XML menu resource into this toolbar. Existing items in the
     * menu will not be modified or removed.
     * </p>
     * 
     * @param resId
     *            ID of a menu resource to inflate
     */
    public void inflateMenu(int resId) {
        getMenuInflater().inflate(resId, getMenu());
    }

    /**
     * Set a listener to respond to menu item click events.
     * 
     * <p>
     * This listener will be invoked whenever a user selects a menu item from
     * the action buttons presented at the end of the toolbar or the associated
     * overflow.
     * </p>
     * 
     * @param listener
     *            Listener to set
     */
    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    private void ensureCollapseButtonView() {
        if (mCollapseButtonView == null) {
            mCollapseButtonView = new ImageButton(getContext(), null, 0,
                    mNavButtonStyle);
            mCollapseButtonView.setImageDrawable(mCollapseIcon);
            final LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);// generateDefaultLayoutParams();
            // lp.gravity = Gravity.START | (mButtonGravity &
            // Gravity.VERTICAL_GRAVITY_MASK);
            // lp.mViewType = LayoutParams.EXPANDED;
            mCollapseButtonView.setLayoutParams(lp);
            mCollapseButtonView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapseActionView();
                }
            });
        }
    }

    private void addSystemView(View v) {
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        /*
         * final LayoutParams lp; if (vlp == null) { lp =
         * generateDefaultLayoutParams(); } else if (!checkLayoutParams(vlp))l {
         * lp = generateLayoutParams(vlp); } else { lp = (LayoutParams) vlp; }
         */
        // lp.mViewType = LayoutParams.SYSTEM;
        Log.e("TabButtonBar", "vlp=" + vlp);
        addView(v, vlp);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState state = new SavedState(super.onSaveInstanceState());

        if (mExpandedMenuPresenter != null
                && mExpandedMenuPresenter.mCurrentExpandedItem != null) {
            state.expandedMenuItemId = mExpandedMenuPresenter.mCurrentExpandedItem
                    .getItemId();
        }

        state.isOverflowOpen = isOverflowMenuShowing();

        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        final SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        final Menu menu = mMenuView != null ? mMenuView.peekMenu() : null;
        if (ss.expandedMenuItemId != 0 && mExpandedMenuPresenter != null
                && menu != null) {
            final MenuItem item = menu.findItem(ss.expandedMenuItemId);
            if (item != null) {
                item.expandActionView();
            }
        }

        if (ss.isOverflowOpen) {
            postShowOverflowMenu();
        }
    }

    private void postShowOverflowMenu() {
        removeCallbacks(mShowOverflowMenuRunnable);
        post(mShowOverflowMenuRunnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowOverflowMenuRunnable);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Toolbars always eat touch events, but should still respect the touch
        // event dispatch
        // contract. If the normal View implementation doesn't want the events,
        // we'll just silently
        // eat the rest of the gesture without reporting the events to the
        // default implementation
        // since that's what it expects.

        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mEatingTouch = false;
        }

        if (!mEatingTouch) {
            final boolean handled = super.onTouchEvent(ev);
            if (action == MotionEvent.ACTION_DOWN && !handled) {
                mEatingTouch = true;
            }
        }

        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL) {
            mEatingTouch = false;
        }

        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*
         * int width = 0; int height = 0; int childState = 0;
         * 
         * final int[] collapsingMargins = mTempMargins; final int
         * marginStartIndex; final int marginEndIndex;
         * 
         * marginStartIndex = 0; marginEndIndex = 1;
         * 
         * 
         * // System views measure first.
         * 
         * int navWidth = 0;
         * 
         * 
         * 
         * 
         * 
         * 
         * int menuWidth = 0; if (shouldLayout(mMenuView)) {
         * measureChildConstrained(mMenuView, widthMeasureSpec, width,
         * heightMeasureSpec, 0, mMaxButtonHeight); menuWidth =
         * mMenuView.getMeasuredWidth() + getHorizontalMargins(mMenuView);
         * height = Math.max(height, mMenuView.getMeasuredHeight() +
         * getVerticalMargins(mMenuView)); childState =
         * combineMeasuredStates(childState, mMenuView.getMeasuredState()); }
         * 
         * 
         * 
         * 
         * 
         * if (shouldLayout(mExpandedActionView)) { width +=
         * measureChildCollapseMargins(mExpandedActionView, widthMeasureSpec,
         * width, heightMeasureSpec, 0, collapsingMargins); height =
         * Math.max(height, mExpandedActionView.getMeasuredHeight() +
         * getVerticalMargins(mExpandedActionView)); childState =
         * combineMeasuredStates(childState,
         * mExpandedActionView.getMeasuredState()); } width += getPaddingLeft()
         * + getPaddingRight(); height += getPaddingTop() + getPaddingBottom();
         * 
         * final int measuredWidth = resolveSizeAndState( Math.max(width,
         * getSuggestedMinimumWidth()), widthMeasureSpec, childState &
         * MEASURED_STATE_MASK); final int measuredHeight = resolveSizeAndState(
         * Math.max(height, getSuggestedMinimumHeight()), heightMeasureSpec,
         * childState << MEASURED_HEIGHT_STATE_SHIFT);
         * 
         * setMeasuredDimension(measuredWidth, shouldCollapse() ? 0 :
         * measuredHeight);
         */
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeigth = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeigth);
        // TODO Auto-generated method stub
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            Log.v(TAG, "measureWidth is " + v.getMeasuredWidth()
                    + "measureHeight is " + v.getMeasuredHeight());
            int widthSpec = 0;
            int heightSpec = 0;
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            if (params.width > 0) {
                widthSpec = MeasureSpec.makeMeasureSpec(params.width,
                        MeasureSpec.EXACTLY);
            } else if (params.width == -1) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
                        MeasureSpec.EXACTLY);
            } else if (params.width == -2) {
                widthSpec = MeasureSpec.makeMeasureSpec(measureWidth,
                        MeasureSpec.AT_MOST);
            }
            if (params.height > 0) {
                heightSpec = MeasureSpec.makeMeasureSpec(params.height,
                        MeasureSpec.EXACTLY);
            } else if (params.height == -1) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureHeigth,
                        MeasureSpec.EXACTLY);
            } else if (params.height == -2) {
                heightSpec = MeasureSpec.makeMeasureSpec(measureWidth,
                        MeasureSpec.AT_MOST);
            }
            v.measure(widthSpec, heightSpec);
        }
        int width = 0;
        int height = 0;
        int childState = 0;
        final int[] collapsingMargins = mTempMargins;
        collapsingMargins[0] = collapsingMargins[1] = 0;
        if (shouldLayout(mExpandedActionView)) {
            width += measureChildCollapseMargins(mExpandedActionView,
                    widthMeasureSpec, width, heightMeasureSpec, 0,
                    collapsingMargins);
            height = Math.max(height, mExpandedActionView.getMeasuredHeight()
                    + getVerticalMargins(mExpandedActionView));
            childState = combineMeasuredStates(childState,
                    mExpandedActionView.getMeasuredState());
        }

    }

    public PagerSecond getTabBar() {
        if (mTabBar == null) {
            mTabBar = new PagerSecond(getContext());
            // / Modify by sanxin.qiu on 2017/1/4 begin
            final LayoutParams lp = new LayoutParams(Utils.dpToPx(getContext(),
                    252), Utils.dpToPx(getContext(), 43));// generateDefaultLayoutParams();
            // / Modify by sanxin.qiu on 2017/1/4 end
            // lp.gravity = Gravity.RIGHT | (mButtonGravity &
            // / Gravity.VERTICAL_GRAVITY_MASK);
            // lp.gravity = Gravity.RIGHT ;
            mTabBar.setLayoutParams(lp);
            mTabBar.setTextSize(16);
            addSystemView(mTabBar);
        } else {
            addSystemView(mTabBar);
        }

        return mTabBar;

    }

    // / Modify by sanxin.qiu on 2017/1/4 begin
    public PagerSecond getTabBar(int width) {
        if (mTabBar == null) {
            mTabBar = new PagerSecond(getContext());
            final LayoutParams lp = new LayoutParams(Utils.dpToPx(getContext(),
                    width), Utils.dpToPx(getContext(), 43));// generateDefaultLayoutParams();
            // lp.gravity = Gravity.RIGHT | (mButtonGravity &
            // / Gravity.VERTICAL_GRAVITY_MASK);
            // lp.gravity = Gravity.RIGHT ;
            mTabBar.setLayoutParams(lp);
            mTabBar.setTextSize(16);
            addSystemView(mTabBar);
        } else {
            addSystemView(mTabBar);
        }

        return mTabBar;
    }

    // / Modify by sanxin.qiu on 2017/1/4 end

    private boolean shouldCollapse() {
        if (!mCollapsible)
            return false;

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            if (shouldLayout(child) && child.getMeasuredWidth() > 0
                    && child.getMeasuredHeight() > 0) {
                return false;
            }
        }
        return true;
    }

    private void measureChildConstrained(View child, int parentWidthSpec,
            int widthUsed, int parentHeightSpec, int heightUsed,
            int heightConstraint) {
        final MarginLayoutParams lp = (MarginLayoutParams) child
                .getLayoutParams();

        int childWidthSpec = getChildMeasureSpec(parentWidthSpec, mPaddingLeft
                + mPaddingRight + lp.leftMargin + lp.rightMargin + widthUsed,
                lp.width);
        int childHeightSpec = getChildMeasureSpec(parentHeightSpec, mPaddingTop
                + mPaddingBottom + lp.topMargin + lp.bottomMargin + heightUsed,
                lp.height);

        final int childHeightMode = MeasureSpec.getMode(childHeightSpec);
        if (childHeightMode != MeasureSpec.EXACTLY && heightConstraint >= 0) {
            final int size = childHeightMode != MeasureSpec.UNSPECIFIED ? Math
                    .min(MeasureSpec.getSize(childHeightSpec), heightConstraint)
                    : heightConstraint;
            childHeightSpec = MeasureSpec.makeMeasureSpec(size,
                    MeasureSpec.EXACTLY);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    private int measureChildCollapseMargins(View child,
            int parentWidthMeasureSpec, int widthUsed,
            int parentHeightMeasureSpec, int heightUsed, int[] collapsingMargins) {
        final MarginLayoutParams lp = (MarginLayoutParams) child
                .getLayoutParams();

        final int leftDiff = lp.leftMargin - collapsingMargins[0];
        final int rightDiff = lp.rightMargin - collapsingMargins[1];
        final int leftMargin = Math.max(0, leftDiff);
        final int rightMargin = Math.max(0, rightDiff);
        final int hMargins = leftMargin + rightMargin;
        collapsingMargins[0] = Math.max(0, -leftDiff);
        collapsingMargins[1] = Math.max(0, -rightDiff);

        final int childWidthMeasureSpec = getChildMeasureSpec(
                parentWidthMeasureSpec, mPaddingLeft + mPaddingRight + hMargins
                        + widthUsed, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(
                parentHeightMeasureSpec, mPaddingTop + mPaddingBottom
                        + lp.topMargin + lp.bottomMargin + heightUsed,
                lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        return child.getMeasuredWidth() + hMargins;
    }

    private int getHorizontalMargins(View v) {
        final MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
        return mlp.getMarginStart() + mlp.getMarginEnd();
    }

    private int getVerticalMargins(View v) {
        final MarginLayoutParams mlp = (MarginLayoutParams) v.getLayoutParams();
        return mlp.topMargin + mlp.bottomMargin;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // super.onLayout(changed, l, t, r, b);
        final int width = getWidth();
        final int height = getHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();
        int left = paddingLeft;
        int right = width - paddingRight;
        final int alignmentHeight = getMinimumHeight();
        final int[] collapsingMargins = new int[2];
        collapsingMargins[0] = collapsingMargins[1] = 0;
        if (shouldLayout(mNavButtonView)) {
            left = Utils.dpToPx(getContext(), 16);
            left = layoutChildLeft(mNavButtonView, left, collapsingMargins,
                    alignmentHeight);

        }
        if (shouldLayout(mTitleTextView)) {
            left = Utils.dpToPx(getContext(), 56);
            left = layoutChildLeft(mTitleTextView, left, collapsingMargins,
                    alignmentHeight);

        }
        if (shouldLayout(mSelelctButtonView)) {

            right = layoutChildRight(mSelelctButtonView, right,
                    collapsingMargins, alignmentHeight);

        }

        if (shouldLayout(mCollapseButtonView)) {

            left = layoutChildLeft(mCollapseButtonView, left,
                    collapsingMargins, alignmentHeight);

        }

        if (shouldLayout(mMenuView)) {

            setMenuColor();
            right = layoutChildRight(mMenuView, right, collapsingMargins,
                    alignmentHeight);

        }

        // collapsingMargins[0] = Math.max(0, getContentInsetLeft() - left);
        // collapsingMargins[1] = Math.max(0, getContentInsetRight() - (width -
        // paddingRight - right));
        // left = Math.max(left, getContentInsetLeft());
        // right = Math.min(right, width - paddingRight -
        // getContentInsetRight());
        if (mTabBar != null)
            mTabBar.setVisibility(View.VISIBLE);
        if (shouldLayout(mExpandedActionView)) {

            left = layoutChildLeft(mExpandedActionView, left,
                    collapsingMargins, alignmentHeight);
            mTabBar.setVisibility(View.GONE);

        }
        if (shouldLayout(mTabBar)) {

            left = layoutChildLeft(mTabBar, left, collapsingMargins,
                    alignmentHeight);

        }
    }

    private boolean shouldLayout(View view) {
        return view != null && view.getParent() == this
                && view.getVisibility() != GONE;
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return super.checkLayoutParams(p) && p instanceof LayoutParams;
    }

    /**
     * Force the toolbar to collapse to zero-height during measurement if it
     * could be considered "empty" (no visible elements with nonzero measured
     * size)
     * 
     * @hide
     */
    public void setCollapsible(boolean collapsible) {
        mCollapsible = collapsible;
        requestLayout();
    }

    /**
     * Must be called before the menu is accessed
     * 
     * @hide
     */
    public void setMenuCallbacks(MenuPresenter.Callback pcb,
            MenuBuilder.Callback mcb) {
        mActionMenuPresenterCallback = pcb;
        mMenuBuilderCallback = mcb;
    }

    /**
     * Interface responsible for receiving menu item click events if the items
     * themselves do not have individual item click listeners.
     */
    public interface OnMenuItemClickListener {
        /**
         * This method will be invoked when a menu item is clicked if the item
         * itself did not already handle the event.
         * 
         * @param item
         *            {@link MenuItem} that was clicked
         * @return <code>true</code> if the event was handled,
         *         <code>false</code> otherwise.
         */
        public boolean onMenuItemClick(MenuItem item);
    }

    static class SavedState extends BaseSavedState {
        public int expandedMenuItemId;
        public boolean isOverflowOpen;

        public SavedState(Parcel source) {
            super(source);
            expandedMenuItemId = source.readInt();
            isOverflowOpen = source.readInt() != 0;
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(expandedMenuItemId);
            out.writeInt(isOverflowOpen ? 1 : 0);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    private class ExpandedActionViewMenuPresenter implements MenuPresenter {
        MenuBuilder mMenu;
        MenuItemImpl mCurrentExpandedItem;

        @Override
        public void initForMenu(Context context, MenuBuilder menu) {
            // Clear the expanded action view when menus change.
            if (mMenu != null && mCurrentExpandedItem != null) {
                mMenu.collapseItemActionView(mCurrentExpandedItem);
            }
            mMenu = menu;
        }

        @Override
        public MenuView getMenuView(ViewGroup root) {
            return null;
        }

        @Override
        public void updateMenuView(boolean cleared) {
            // Make sure the expanded item we have is still there.
            if (mCurrentExpandedItem != null) {
                boolean found = false;

                if (mMenu != null) {
                    final int count = mMenu.size();
                    for (int i = 0; i < count; i++) {
                        final MenuItem item = mMenu.getItem(i);
                        if (item == mCurrentExpandedItem) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    // The item we had expanded disappeared. Collapse.
                    collapseItemActionView(mMenu, mCurrentExpandedItem);
                }
            }
        }

        @Override
        public void setCallback(Callback cb) {
        }

        @Override
        public boolean onSubMenuSelected(SubMenuBuilder subMenu) {
            return false;
        }

        @Override
        public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
        }

        @Override
        public boolean flagActionItems() {
            return false;
        }

        @Override
        public boolean expandItemActionView(MenuBuilder menu, MenuItemImpl item) {
            ensureCollapseButtonView();
            if (mCollapseButtonView.getParent() != TabButtonBar.this) {
                addView(mCollapseButtonView);
            }
            mExpandedActionView = item.getActionView();
            mCurrentExpandedItem = item;
            if (mExpandedActionView.getParent() != TabButtonBar.this) {
                final LayoutParams lp = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);// generateDefaultLayoutParams();
                // lp.gravity = Gravity.START | (mButtonGravity &
                // Gravity.VERTICAL_GRAVITY_MASK);
                // lp.mViewType = LayoutParams.EXPANDED;
                mExpandedActionView.setLayoutParams(lp);
                addView(mExpandedActionView);
            }

            // setChildVisibilityForExpandedActionView(true);
            requestLayout();
            item.setActionViewExpanded(true);

            if (mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) mExpandedActionView)
                        .onActionViewExpanded();
            }

            return true;
        }

        @Override
        public boolean collapseItemActionView(MenuBuilder menu,
                MenuItemImpl item) {
            // Do this before detaching the actionview from the hierarchy, in
            // case
            // it needs to dismiss the soft keyboard, etc.
            if (mExpandedActionView instanceof CollapsibleActionView) {
                ((CollapsibleActionView) mExpandedActionView)
                        .onActionViewCollapsed();
            }

            removeView(mExpandedActionView);
            removeView(mCollapseButtonView);
            mExpandedActionView = null;

            // setChildVisibilityForExpandedActionView(false);
            mCurrentExpandedItem = null;
            requestLayout();
            item.setActionViewExpanded(false);

            return true;
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        public Parcelable onSaveInstanceState() {
            return null;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
        }
    }

    public static class LayoutParams extends ActionBar.LayoutParams {
        static final int CUSTOM = 0;
        static final int SYSTEM = 1;
        static final int EXPANDED = 2;

        int mViewType = CUSTOM;

        public LayoutParams(@NonNull Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        public LayoutParams(int gravity) {
            this(WRAP_CONTENT, MATCH_PARENT, gravity);
        }

        public LayoutParams(LayoutParams source) {
            super(source);

            mViewType = source.mViewType;
        }

        public LayoutParams(ActionBar.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            // ActionBar.LayoutParams doesn't have a MarginLayoutParams
            // constructor.
            // Fake it here and copy over the relevant data.
            copyMarginsFrom(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public void setActionMenuColor(final int color) {
        mActionFlag = 1;
        mActionColor = color;
        /*
         * if(mExpandedActionView==null){
         * 
         * return ; }
         * 
         * ((ViewGroup)mExpandedActionView).setOnHierarchyChangeListener(new
         * ViewGroup.OnHierarchyChangeListener() {
         * 
         * @Override public void onChildViewRemoved(View parent, View child) {
         * // TODO Auto-generated method stub
         * 
         * }
         * 
         * @Override public void onChildViewAdded(View parent, View child) {
         * 
         * //setAllChildViewsColor(child,color); if (child instanceof TextView){
         * 
         * //((TextView)view).setTextColor(color);
         * 
         * Drawable[] dras=((TextView)child).getCompoundDrawables();
         * if(dras[0]!=null){ dras[0].setTint(color);
         * ((TextView)child).setCompoundDrawables(dras[0], null, null, null); }
         * }
         * 
         * } });
         */
        /*
         * if(mExpandedActionView==null){ return ; } for (int i = 0; i
         * <((ViewGroup) mExpandedActionView).getChildCount(); i++) { View view
         * = ((ViewGroup) mExpandedActionView).getChildAt(i); if (view
         * instanceof TextView){
         * 
         * //((TextView)view).setTextColor(color);
         * 
         * Drawable[] dras=((TextView)view).getCompoundDrawables();
         * if(dras[0]!=null){ dras[0].setTint(color);
         * ((TextView)view).setCompoundDrawables(dras[0], null, null, null); } }
         * }
         */
    }

    void setMenuColor() {

        if (mActionFlag == 0) {
            return;
        }
        for (int i = 0; i < ((ViewGroup) mMenuView).getChildCount(); i++) {
            View view = ((ViewGroup) mMenuView).getChildAt(i);
            if (view instanceof TextView) {

                // ((TextView)view).setTextColor(color);

                Drawable[] dras = ((TextView) view).getCompoundDrawables();
                if (dras[0] != null) {
                    // dras[0].setColorFilter(Color.RED,
                    // PorterDuff.Mode.MULTIPLY);
                    dras[0].setTint(mActionColor);
                    // dras[0].setTintMode(PorterDuff.Mode.SRC_IN);
                    ((TextView) view).setCompoundDrawables(dras[0], null, null,
                            null);
                }
            }
            if (view instanceof ImageView) {
                Drawable drawable = ((ImageView) view).getDrawable();

                if (drawable != null) {
                    // drawable.setColorFilter(mActionColor,
                    // PorterDuff.Mode.MULTIPLY);
                    drawable.setTint(mActionColor);
                    // drawable.setTintMode(PorterDuff.Mode.SRC_IN);
                    ((ImageView) view).setImageDrawable(drawable);
                }

            }
        }

        mActionFlag = 0;
    }
}
