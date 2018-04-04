package com.hct.gios.widget;

import java.util.ArrayList;

import com.android.internal.view.menu.ActionMenuItemView;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.calendar.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ActionMenuPresenter;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;
import android.graphics.drawable.ColorDrawable;
import android.widget.ActionMenuView.LayoutParams;
import android.graphics.drawable.ColorDrawable;

import android.widget.ActionMenuView.OnMenuItemClickListener;
import android.widget.ImageView.ScaleType;

public class ToolBarHCT extends LinearLayout implements View.OnClickListener {
    Context mContext;
    int mScreenWidth;
    private MenuBuilder mMenu;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private ActionMenuPresenter mPresenter;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    ArrayList<MenuItemImpl> mActionItems;
    public Button btnLeft;
    public Button btnRight;
    public ImageView divider;

    public ToolBarHCT(Context context, int screenWidth) {
        super(context);
        mContext = context;
        mScreenWidth = screenWidth;

        // setBackgroundDrawable(new
        // ColorDrawable(mContext.getResources().getColor(R.color.hct_green_light)));

    }

    public ToolBarHCT(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /*
     * public ToolBarHCT(Context context, AttributeSet attrs, int defStyleAttr)
     * { super(context, attrs, defStyleAttr); }
     */
    private class MenuBuilderCallback implements MenuBuilder.Callback {
        @Override
        public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
            return mOnMenuItemClickListener != null
                    && mOnMenuItemClickListener.onMenuItemClick(item);
        }

        @Override
        public void onMenuModeChange(MenuBuilder menu) {
            if (mMenuBuilderCallback != null) {
                mMenuBuilderCallback.onMenuModeChange(menu);
            }
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        if (child instanceof ActionMenuView) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.addView(child, params);
    }

    public void inflateMenu(int resId) {

        // setShowDividers(SHOW_DIVIDER_MIDDLE);
        getMenuInflater().inflate(resId, getMenu());
        mActionItems = mMenu.getVisibleItems();
        mMenuBuilderCallback = new MenuBuilderCallback();
        AddInteralView();
    }

    public void AddInteralView() {
        setBackground(new ColorDrawable(0xffffffff));
        btnLeft = new Button(mContext);
        MenuItemImpl menuLeft = mActionItems.get(0);
        btnLeft.setId(menuLeft.getItemId());
        btnLeft.setText(menuLeft.getTitle());
        btnLeft.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.spiltbtn_default_material));

        LayoutParams lpleft = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        // lp.height=LayoutParams.MATCH_PARENT;
        lpleft.weight = 1;
        btnLeft.setLayoutParams(lpleft);
        btnLeft.setOnClickListener(this);
        addView(btnLeft);

        divider = new ImageView(mContext);
        divider.setImageResource(R.drawable.divider_vertical_holo_dark);
        divider.setScaleType(ScaleType.FIT_XY);
        LayoutParams lp = new LayoutParams(1, LayoutParams.MATCH_PARENT);

        lp.topMargin = Utils.dpToPx(mContext, 10);
        lp.bottomMargin = Utils.dpToPx(mContext, 10);
        divider.setLayoutParams(lp);
        // addView(divider);

        btnRight = new Button(mContext);
        MenuItemImpl menuRight = mActionItems.get(1);
        btnRight.setId(menuRight.getItemId());
        btnRight.setText(menuRight.getTitle());
        btnRight.setOnClickListener(this);
        btnRight.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.spiltbtn_default_material));
        LayoutParams lpright = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        // lp.height=LayoutParams.MATCH_PARENT;
        btnRight.setLayoutParams(lpright);
        lpright.weight = 1;
        addView(btnRight);

        // lp.topMargin=Utils.dpToPx(mContext, 10);
        // lp.bottomMargin=Utils.dpToPx(mContext, 10);
        // lp.width=Utils.dpToPx(mContext, 1);
        // lp.leftMargin=(mScreenWidth/2-Utils.dpToPx(mContext, 1));

    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener listener) {
        mOnMenuItemClickListener = listener;
    }

    public Menu getMenu() {
        if (mMenu == null) {
            final Context context = getContext();
            mMenu = new MenuBuilder(context);
            mMenu.setCallback(new MenuBuilderCallback());
        }

        return mMenu;
    }

    private MenuInflater getMenuInflater() {
        return new MenuInflater(getContext());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        /*
         * final int childCount = getChildCount(); final int midVertical = (top
         * + bottom) / 2;
         * 
         * int widgetWidth=mScreenWidth/childCount;
         * 
         * int startLeft = getPaddingLeft(); for (int i = 0; i < childCount;
         * i++) { final View v = getChildAt(i); final LayoutParams lp =
         * (LayoutParams) v.getLayoutParams(); if (v.getVisibility() == GONE ) {
         * continue; }
         * 
         * startLeft += lp.leftMargin; int width = v.getMeasuredWidth(); int
         * height = v.getMeasuredHeight();
         * 
         * View grandChild = v; if(grandChild instanceof ActionMenuItemView){
         * ((TextView) grandChild).setGravity(Gravity.CENTER); }
         * v.setLayoutParams(lp);
         * 
         * v.layout(startLeft, 0, startLeft + width, height); startLeft +=
         * widgetWidth; }
         */
        /*
         * final boolean isLayoutRtl = isLayoutRtl(); for (int i = 0; i <
         * childCount; i++) { final View v = getChildAt(i); if
         * (v.getVisibility() == GONE) { continue; }
         * 
         * LayoutParams p = (LayoutParams) v.getLayoutParams(); if
         * (p.isOverflowButton) { overflowWidth = v.getMeasuredWidth(); if
         * (hasDividerBeforeChildAt(i)) { overflowWidth += dividerWidth; }
         * 
         * int height = v.getMeasuredHeight(); int r; int l; if (isLayoutRtl) {
         * l = getPaddingLeft() + p.leftMargin; r = l + overflowWidth; } else {
         * r = getWidth() - getPaddingRight() - p.rightMargin; l = r -
         * overflowWidth; } int t = midVertical - (height / 2); int b = t +
         * height; v.layout(l, t, r, b);
         * 
         * widthRemaining -= overflowWidth; hasOverflow = true; } else { final
         * int size = v.getMeasuredWidth() + p.leftMargin + p.rightMargin;
         * nonOverflowWidth += size; widthRemaining -= size; if
         * (hasDividerBeforeChildAt(i)) { nonOverflowWidth += dividerWidth; }
         * nonOverflowCount++; } }
         * 
         * if (childCount == 1 && !hasOverflow) { // Center a single child final
         * View v = getChildAt(0); final int width = v.getMeasuredWidth(); final
         * int height = v.getMeasuredHeight(); final int midHorizontal = (right
         * - left) / 2; final int l = midHorizontal - width / 2; final int t =
         * midVertical - height / 2; v.layout(l, t, l + width, t + height);
         * return; }
         * 
         * final int spacerCount = nonOverflowCount - (hasOverflow ? 0 : 1);
         * final int spacerSize = Math.max(0, spacerCount > 0 ? widthRemaining /
         * spacerCount : 0);
         * 
         * if (isLayoutRtl) { int startRight = getWidth() - getPaddingRight();
         * for (int i = 0; i < childCount; i++) { final View v = getChildAt(i);
         * final LayoutParams lp = (LayoutParams) v.getLayoutParams(); if
         * (v.getVisibility() == GONE || lp.isOverflowButton) { continue; }
         * 
         * startRight -= lp.rightMargin; int width = v.getMeasuredWidth(); int
         * height = v.getMeasuredHeight(); int t = midVertical - height / 2;
         * v.layout(startRight - width, t, startRight, t + height); startRight
         * -= width + lp.leftMargin + spacerSize; } } else { int startLeft =
         * getPaddingLeft(); for (int i = 0; i < childCount; i++) { final View v
         * = getChildAt(i); final LayoutParams lp = (LayoutParams)
         * v.getLayoutParams(); if (v.getVisibility() == GONE ||
         * lp.isOverflowButton) { continue; }
         * 
         * startLeft += lp.leftMargin; int width = v.getMeasuredWidth(); int
         * height = v.getMeasuredHeight(); int t = midVertical - height / 2;
         * v.layout(startLeft, t, startLeft + width, t + height); startLeft +=
         * width + lp.rightMargin + spacerSize; } }
         */
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        mMenuBuilderCallback.onMenuItemSelected(mMenu,
                mMenu.findItem(view.getId()));
    }
}
