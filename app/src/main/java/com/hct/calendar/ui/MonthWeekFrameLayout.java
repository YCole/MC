package com.hct.calendar.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.android.calendar.GeneralPreferences;
import com.android.calendar.R;
import com.android.calendar.Utils;
import com.hct.calendar.month.MonthFragment;

public class MonthWeekFrameLayout extends FrameLayout {

    public int ITEM_HEIGHT;// dp
    public final static int ITEM_COUNT = 6;

    private int mItemHeight = MonthFragment.itemHeight;// dp
    private int mMaxTranslation = 0;

    private boolean mIsFirst = true;
    private LinearLayout mAgendaLayout = null;
    private ViewPager mViewPager = null;

    private boolean isFullMonthMode = false;
    private int mMeasureHeight = 0;
    private Context mContext;
    public OnSizeChangeListener sizeChangeListener;

    public interface OnSizeChangeListener {
        void onSizeChange(int height);
    }

    public MonthWeekFrameLayout(Context context) {
        this(context, null);
    }

    public MonthWeekFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MonthWeekFrameLayout(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, -1);
    }

    public MonthWeekFrameLayout(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // modified by zhangjunjie
        // ITEM_HEIGHT = (int)
        // context.getResources().getDimension(R.dimen.month_row_height);
        // mItemHeight = ITEM_HEIGHT;
        // (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        // ITEM_HEIGHT, context.getResources().getDisplayMetrics());
        mMaxTranslation = mItemHeight * (ITEM_COUNT - 1);
        isFullMonthMode = Utils.getSharedPreference(context,
                GeneralPreferences.KEY_FULL_SIZE_ON_AGENDA, false);
        mContext = context.getApplicationContext();
    }

    @Override
    protected void onFinishInflate() {
        mAgendaLayout = (LinearLayout) findViewById(com.android.calendar.R.id.agenda_list);
        mViewPager = (ViewPager) findViewById(R.id.month_view_pager);
        SharedPreferences sp = getContext().getSharedPreferences("launchState",
                Context.MODE_PRIVATE);
        final boolean isLaunch = sp.getBoolean("isLaunch", false);
        if (isLaunch) {
            Editor editor = sp.edit();
            editor.remove("isLaunch");
            editor.apply();
        } else {
            mViewPager.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        int measureHeight = measureHeight(heightMeasureSpec);
        if (isFullMonthMode && measureHeight != mMeasureHeight) {
            mMeasureHeight = measureHeight;
            updateItemHeight(measureHeight / ITEM_COUNT);
            // mMaxTranslation = measureHeight - mItemHeight;
            if (sizeChangeListener != null) {
                sizeChangeListener.onSizeChange(mMaxTranslation);
            }
            ViewGroup.LayoutParams lp = mViewPager.getLayoutParams();
            lp.height = measureHeight;
        }
        FrameLayout.LayoutParams flp = (LayoutParams) mAgendaLayout
                .getLayoutParams();
        flp.topMargin = mItemHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;
        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);
        switch (heightMode) {
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = heightSize;
            break;
        }
        return result;
    }

    private final Runnable mShowPagerRunnable = new Runnable() {

        @Override
        public void run() {
            mViewPager.setVisibility(VISIBLE);
            if (mCallback != null) {
                mCallback.onShow();
            }
        }
    };

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewPager.getVisibility() == GONE) {
            postDelayed(mShowPagerRunnable, 50);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mAgendaLayout != null) {
            mAgendaLayout.layout(left, mItemHeight * ITEM_COUNT, right, bottom
                    + (mItemHeight * ITEM_COUNT));
        }
    }

    public void setOnSizeChangeListener(OnSizeChangeListener ocl) {
        sizeChangeListener = ocl;
    }

    public int getItemHeight() {
        return this.mItemHeight;
    }

    public int getMaxTranslation() {
        return this.mMaxTranslation;
    }

    public void updateItemHeight(int newHeight) {
        mItemHeight = newHeight;
        mMaxTranslation = mItemHeight * (ITEM_COUNT - 1);
        isFullMonthMode = Utils.getSharedPreference(mContext,
                GeneralPreferences.KEY_FULL_SIZE_ON_AGENDA, false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(mShowPagerRunnable);
        setOnSizeChangeListener(null);
    }

    public void setShowPagerCallback(ShowPagerCallback callback) {
        mCallback = callback;
    }

    private ShowPagerCallback mCallback;

    public interface ShowPagerCallback{
        void onShow();
    }
}
