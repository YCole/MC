package com.android.calendar.event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

public class Transformer implements ViewPager.PageTransformer {
    private int maxTranslateOffsetX;
    private ViewPager viewPager;

    public Transformer(Context context) {
        this.maxTranslateOffsetX = dp2px(context, 180);
    }

    @SuppressLint("NewApi")
    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }

        int leftInScreen = view.getLeft() - viewPager.getScrollX();
        int centerXInViewPager = leftInScreen + view.getMeasuredWidth() / 2;
        int offsetX = centerXInViewPager - viewPager.getMeasuredWidth() / 2;
        float offsetRate = (float) offsetX * 0.125f
                / viewPager.getMeasuredWidth();
        float scaleFactor = 1 - Math.abs(offsetRate);
        if (scaleFactor > 0) {
            view.setScaleX(scaleFactor);
            view.setScaleY(scaleFactor);
            view.setTranslationX(-maxTranslateOffsetX * offsetRate);
            view.setAlpha((float) (1 - Math.abs(position) * 0.4));
        }
    }

    /**
     * dp������ת��
     */
    private int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }
}
