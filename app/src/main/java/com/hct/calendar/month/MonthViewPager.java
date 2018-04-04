package com.hct.calendar.month;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MonthViewPager extends ViewPager {
    private final static String TAG = "MonthViewPager";
    private GestureDetector mGestureDetector = null;
    private boolean misCanUpOrDown = true;

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (ViewPager.SCROLL_STATE_IDLE == arg0) {
                misCanUpOrDown = true;
            } else {
                misCanUpOrDown = false;
            }
        }
    };

    public MonthViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context,
                new MonthViewUpAndDownGestureListtener());
        addOnPageChangeListener(mOnPageChangeListener);
    }

    public MonthViewPager(Context context) {
        this(context, null);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isUpOrDown = mGestureDetector.onTouchEvent(ev);
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            gestureTouch(ev);
            break;
        case MotionEvent.ACTION_MOVE:
            if (isUpOrDown) {
                gestureTouch(ev);
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            gestureTouch(ev);
            break;
        default:
            break;
        }
        return super.dispatchTouchEvent(ev);
    }

    class MonthViewUpAndDownGestureListtener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            if (e1 == null) {
                return false;
            }
            float y1 = e1.getRawY(), y2 = e2.getRawY();
            Log.d(TAG, "To UP" + "(" + y1 + "," + y2 + ")");
            if (y1 - y2 > 200) {
                return (true);
            } else if (y1 - y2 < -200) {
                return (true);
            }
            return false;
        }
    }

    private boolean gestureTouch(MotionEvent ev) {
        if (mListener != null && misCanUpOrDown) {
            mListener.onTouch(this, ev);
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mListener = null;
        removeOnPageChangeListener(mOnPageChangeListener);
    }

    private OnTouchListener mListener;

    public void setTouchGestureDetector(OnTouchListener onListener) {
        mListener = onListener;
    }
}
