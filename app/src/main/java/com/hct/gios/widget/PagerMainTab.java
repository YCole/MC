/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.Locale;

import com.android.calendar.R;

public class PagerMainTab extends HorizontalScrollView {

    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[] { android.R.attr.textSize,
            android.R.attr.textColor };
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF328bde;
    private int underlineColor = 0xFF328bde;// 0xFFE9E9E9;
    private int dividerColor = 0xFFFaFaFa;// 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = false;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 26;// 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;

    private int tabTextSize = 20;// 12;
    private int tabTextColor = 0xFFFaFaFa;// 0xFF666666;
    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;

    private int tabBackgroundResId;
    private Locale locale;

    public PagerMainTab(Context context) {
        this(context, null);
    }

    public PagerMainTab(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagerMainTab(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs,
                R.styleable.PagerSlidingTabStrip);

        indicatorColor = a.getColor(
                R.styleable.PagerSlidingTabStrip_pstsIndicatorColor,
                indicatorColor);
        underlineColor = a.getColor(
                R.styleable.PagerSlidingTabStrip_pstsUnderlineColor,
                underlineColor);
        dividerColor = a
                .getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor,
                        dividerColor);
        indicatorHeight = a.getDimensionPixelSize(
                R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight,
                indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(
                R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight,
                underlineHeight);
        dividerPadding = a.getDimensionPixelSize(
                R.styleable.PagerSlidingTabStrip_pstsDividerPadding,
                dividerPadding);
        tabPadding = a.getDimensionPixelSize(
                R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight,
                tabPadding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.L) {
            tabBackgroundResId = R.drawable.item_background_borderless_material;
            tabBackgroundResId = a.getResourceId(
                    R.styleable.PagerSlidingTabStrip_pstsTabBackground,
                    tabBackgroundResId);
        } else {
            tabBackgroundResId = R.drawable.item_background_transparent;
            tabBackgroundResId = a.getResourceId(
                    R.styleable.PagerSlidingTabStrip_pstsTabBackground,
                    tabBackgroundResId);
        }
        shouldExpand = a
                .getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand,
                        shouldExpand);
        scrollOffset = a
                .getDimensionPixelSize(
                        R.styleable.PagerSlidingTabStrip_pstsScrollOffset,
                        scrollOffset);
        textAllCaps = a.getBoolean(
                R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }
    }

    public void setBackgroundColor(int color) {
        this.underlineColor = color;
        invalidate();
    }

    public void setViewPager(ViewPager pager) {
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }

    public void notifyDataSetChanged() {

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i,
                        ((IconTabProvider) pager.getAdapter())
                                .getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @SuppressLint("NewApi")
                    @Override
                    public void onGlobalLayout() {

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                            getViewTreeObserver().removeGlobalOnLayoutListener(
                                    this);
                        } else {
                            getViewTreeObserver().removeOnGlobalLayoutListener(
                                    this);
                        }

                        currentPosition = pager.getCurrentItem();
                        scrollToChild(currentPosition, 0);
                    }
                });

    }

    private void addTextTab(final int position, String title) {

        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        tab.setSingleLine();

        addTab(position, tab);
    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }

    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(position);
            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        setShouldExpand(true);
        if (tab instanceof TextView) {
            // if (position == 0) {
            // ((TextView)
            // tab).setTextColor(getResources().getColor(R.color.deep_green));//set
            // text color
            // } else {
            // ((TextView)
            // tab).setTextColor(getResources().getColor(R.color.light_green));//set
            // text color
            // }
            ((TextView) tab).setTextColor(0xff666666);// set text color
        }
        tabsContainer
                .addView(tab, position, shouldExpand ? expandedTabLayoutParams
                        : defaultTabLayoutParams);
    }

    public void setClickableForAllTab(boolean isTrue) {
        for (int i = 0; i < tabsContainer.getChildCount(); i++) {
            tabsContainer.getChildAt(i).setClickable(isTrue);
        }
    }

    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // setAllCaps() is only available from API 14, so the upper case
                // is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString()
                                .toUpperCase(locale));
                    }
                }
            }
        }

    }

    private void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("chh", "onDraw");

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();
        Log.d("chh", "height" + height);
        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, 0, tabsContainer.getWidth(),
                height/*-underlineHeight*/, rectPaint);

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        Bitmap bmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.tab_selected_hct);

        int bmpw = bmp.getWidth();
        int bmph = bmp.getHeight();
        Log.d("chh", "bmpw" + bmpw);
        Log.d("chh", "bmph" + bmph);

        float dLeft = (lineRight + lineLeft - bmpw) / 2;
        float dRight = (lineRight + lineLeft + bmpw) / 2;

        // if there is an offset, start interpolating left and right coordinates
        // between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset)
                    * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset)
                    * lineRight);

            dLeft = (lineRight - lineLeft - bmpw) / 2 + lineLeft;
            dRight = (lineRight - lineLeft + bmpw) / 2 + lineLeft;

        }

        // canvas.drawRect(lineLeft, height - indicatorHeight, lineRight,
        // height, rectPaint);
        // canvas.drawRect(dLeft, height - indicatorHeight, dRight, height,
        // rectPaint);

        canvas.drawBitmap(bmp, dLeft, height - bmph, null);

        // draw divider

        dividerPaint.setColor(dividerColor);

        // Bitmap line =
        // BitmapFactory.decodeResource(getResources(),R.drawable.bottom_line);
        // int linew = line.getWidth();
        // int lineh = line.getHeight();
        // Log.d("chh", "linew"+linew );
        // Log.d("chh", "lineh"+lineh );
        for (int i = 0; i < tabCount - 1; i++) {
            View tab = tabsContainer.getChildAt(i);
            // canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(),
            // height - dividerPadding, dividerPaint);
            // canvas.drawLine(tab.getRight()-1, dividerPadding,
            // tab.getRight()-1, height - dividerPadding, dividerPaint);
            Log.d("chh", "tab.getRight()" + tab.getRight());
            Log.d("chh", ",dividerPadding" + dividerPadding);
            canvas.drawRect(tab.getRight() - 4, dividerPadding - 4,
                    tab.getRight() + 4, dividerPadding + 4, dividerPaint);
            // canvas.drawBitmap(line,tab.getRight(),70,null);
        }
    }

    public void setCurrentTab(int pos) {
        currentPosition = pos;
        scrollToChild(currentPosition, 0);
    }

    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {

        currentPosition = position;
        currentPositionOffset = positionOffset;

        scrollToChild(position, (int) (positionOffset * tabsContainer
                .getChildAt(position).getWidth()));

        invalidate();

        if (delegatePageListener != null) {
            delegatePageListener.onPageScrolled(position, positionOffset,
                    positionOffsetPixels);
        }
    }

    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            scrollToChild(pager.getCurrentItem(), 0);
        }
        if (delegatePageListener != null) {
            delegatePageListener.onPageScrollStateChanged(state);
        }
    }

    public void onPageSelected(int position) {
        View view = tabsContainer.getChildAt(position);
        // if (view instanceof ImageButton) {//
        // ((ImageButton) view).setImageResource(R.drawable.ic_input_add);
        // } else if (view instanceof TextView) {
        // if (view instanceof TextView) {
        // setColor(position);
        // }
        if (delegatePageListener != null) {
            delegatePageListener.onPageSelected(position);
        }
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset,
                int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer
                    .getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }
            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            View view = tabsContainer.getChildAt(position);
            // if (view instanceof ImageButton) {//
            // ((ImageButton) view).setImageResource(R.drawable.ic_input_add);
            // } else if (view instanceof TextView) {
            // if (view instanceof TextView) {
            // setColor(position);
            // }
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

    /**
     * 
     * @param position
     */
    /*
     * private void setColor(int position) { TextView textView0 = (TextView)
     * tabsContainer.getChildAt(0); TextView textView1 = (TextView)
     * tabsContainer.getChildAt(1); TextView textView2 = (TextView)
     * tabsContainer.getChildAt(2); switch (position) { case 0:
     * textView0.setTextColor(getResources().getColor(R.color.deep_green));
     * textView1.setTextColor(getResources().getColor(R.color.light_green));
     * textView2.setTextColor(getResources().getColor(R.color.light_green));
     * break; case 1:
     * textView0.setTextColor(getResources().getColor(R.color.light_green));
     * textView1.setTextColor(getResources().getColor(R.color.deep_green));
     * textView2.setTextColor(getResources().getColor(R.color.light_green));
     * break; case 2:
     * textView0.setTextColor(getResources().getColor(R.color.light_green));
     * textView1.setTextColor(getResources().getColor(R.color.light_green));
     * textView2.setTextColor(getResources().getColor(R.color.deep_green));
     * break; default: break; } }
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
