package com.gm.internal.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class ActionMenuView extends LinearLayout implements com.gm.internal.menu.MenuBuilder.ItemInvoker, MenuView {
    public static final String TAG = "FloatActionMenu";

    private com.gm.internal.menu.MenuBuilder mMenu;


    private boolean mReserveOverflow;
    private int mGeneratedItemMargin = 0;
    private int mOneItemMargin = 0;
    private int mTwoItemMargin = 0;
    private int mThreeItemMargin = 0;
    private int mFourItemMargin = 0;
    private int mFiveItemMargin = 0;

    private int mSideMarginOfTow = 0;
    private int mSideMarginOfThree = 0;
    private int mSideMarginOfFour = 0;
    private int mSideMarginOfFive = 0;

    private static final int ONE_ITEM_MARGIN = 15;

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBaselineAligned(false);
        mGeneratedItemMargin = (int)context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_default);
        mTwoItemMargin = (int)context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_two);
        mThreeItemMargin = (int)context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_three);
        mFourItemMargin = (int)context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_four);
        mFiveItemMargin = (int)context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_five);

        mSideMarginOfTow = (int)context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_two);
        mSideMarginOfThree = (int)context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_three);
        mSideMarginOfFour = (int)context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_four);
        mSideMarginOfFive = (int)context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_five);

        final float density = context.getResources().getDisplayMetrics().density;
        mOneItemMargin = (int)(ONE_ITEM_MARGIN * density);
    }

    /** @hide */
    public void setOverflowReserved(boolean reserveOverflow) {
        mReserveOverflow = reserveOverflow;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        return params;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p != null) {
            final LayoutParams result = p instanceof LayoutParams
                    ? new LayoutParams((LayoutParams) p)
                    : new LayoutParams(p);
            if (result.gravity <= Gravity.NO_GRAVITY) {
                result.gravity = Gravity.CENTER_VERTICAL;
            }
            return result;
        }
        return generateDefaultLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.width=(int)getContext().getResources().getDimension(R.dimen.gome_float_menu_item_width);
        result.height=(int)getContext().getResources().getDimension(R.dimen.gome_float_menu_item_height);
        result.isOverflowButton = true;
        return result;
    }

    public boolean invokeItem(com.gm.internal.menu.MenuItemImpl item) {
        return mMenu.performItemAction(item, 0);
    }

    public int getWindowAnimations() {
        return 0;
    }

    public void initialize(com.gm.internal.menu.MenuBuilder menu) {
        mMenu = menu;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        // Previous measurement at exact format may have set margins - reset them.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            switch (childCount){
                case 1:
                    lp.leftMargin = lp.rightMargin = mOneItemMargin;
                    break;
                case 2:
                    if(i == 0){
                        lp.leftMargin = mSideMarginOfTow;
                        lp.rightMargin = mTwoItemMargin;
                    }else if(i == 1){
                        lp.rightMargin = mSideMarginOfTow;
                        lp.leftMargin = mTwoItemMargin;
                    }
                    break;
                case 3:
                    if(i == 0){
                        lp.leftMargin = mSideMarginOfThree;
                        lp.rightMargin = mThreeItemMargin;
                    }else if(i == 2){
                        lp.rightMargin = mSideMarginOfThree;
                        lp.leftMargin = mThreeItemMargin;
                    }else{
                        lp.leftMargin = lp.rightMargin = mThreeItemMargin;
                    }
                    break;
                case 4:
                    if(i == 0){
                        lp.leftMargin = mSideMarginOfFour;
                        lp.rightMargin = mFourItemMargin;
                    }else if(i == 3){
                        lp.rightMargin = mSideMarginOfFour;
                        lp.leftMargin = mFourItemMargin;
                    }else{
                        lp.leftMargin = lp.rightMargin = mFourItemMargin;
                    }
                    break;
                case 5:
                    if(i == 0){
                        lp.leftMargin = mSideMarginOfFive;
                        lp.rightMargin = mFiveItemMargin;
                    }else if(i == 4){
                        lp.rightMargin = mSideMarginOfFive;
                        lp.leftMargin = mFiveItemMargin;
                    }else{
                        lp.leftMargin = lp.rightMargin = mFiveItemMargin;
                    }
                    break;
                default:
                    lp.leftMargin = lp.rightMargin = mGeneratedItemMargin;
                    break;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        /** @hide */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean isOverflowButton;

        /** @hide */
        @ViewDebug.ExportedProperty(category = "layout")
        public int cellsUsed;

        /** @hide */
        @ViewDebug.ExportedProperty(category = "layout")
        public int extraPixels;

        /** @hide */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean expandable;

        /** @hide */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean preventEdgeOffset;

        /** @hide */
        public boolean expanded;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((LinearLayout.LayoutParams) other);
            isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            isOverflowButton = false;
        }

        /** @hide */
        public LayoutParams(int width, int height, boolean isOverflowButton) {
            super(width, height);
            this.isOverflowButton = isOverflowButton;
        }
    }
}
