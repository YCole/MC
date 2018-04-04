package com.hct.calendar.month;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.hct.calendar.month.MonthWeekSwitcher.AgendaTouchStateListener;

public class AgendaScrollView extends ScrollView implements
        AgendaTouchStateListener {
    protected boolean mIsTop = true;
    private float mTouchSlop;
    private int mLastY = 0;

    public AgendaScrollView(Context context) {
        this(context, null);
    }

    public AgendaScrollView(Context context, AttributeSet attrs,
            int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AgendaScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaScrollView(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setOnScrollChangeListener(new OnScrollChangeListener() {

            @Override
            public void onScrollChange(View arg0, int arg1, int arg2, int arg3,
                    int arg4) {
                if (arg2 > 0) {
                    mIsTop = false;
                } else {
                    mIsTop = true;
                }
            }
        });

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    private int mAgendaState = AgendaTouchStateListener.AGENDA_NORMAL;

    @Override
    public void onStateChange(int state) {
        mAgendaState = state;
        switch (state) {
        case AgendaTouchStateListener.AGENDA_NORMAL:
            isStartDrag = false;
            scrollTo(0, 0);
            break;
        case AgendaTouchStateListener.AGENDA_MOVE:
            isStartDrag = true;
            setPressed(false);
            break;
        case AgendaTouchStateListener.AGENDA_TOP:
            isStartDrag = false;
            break;
        default:
            break;
        }
    }

    private boolean isTop() {
        return mAgendaState == AgendaTouchStateListener.AGENDA_TOP;
    }

    private boolean isMove() {
        return mAgendaState == AgendaTouchStateListener.AGENDA_MOVE;
    }

    private boolean isNormal() {
        return mAgendaState == AgendaTouchStateListener.AGENDA_NORMAL;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        // return super.onInterceptTouchEvent(ev);
        // getParent().requestDisallowInterceptTouchEvent(true);
        boolean isIntercept = false;
        float startX = 0;
        float endX = 0;
        float moveX = 0;
        float startY = 0;
        float endY = 0;
        float moveY = 0;
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            startX = event.getX();
            startY = event.getY();
            isIntercept = false;
            return false;

        case MotionEvent.ACTION_MOVE:
            moveX = event.getX();
            moveY = event.getY();
            if (startY == moveY) {
                isIntercept = false;
                return false;
            } else {
                isIntercept = true;
                return true;
            }

        case MotionEvent.ACTION_UP:
            endX = event.getX();
            endY = event.getY();

        }

        // if (startY == moveY) {
        // return true;
        // } else {
        // return true;
        // }
        return isIntercept;
    }

    private boolean isStartDrag = false;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mLastY = (int) ev.getY();
            gestureTouch(ev);
            super.onTouchEvent(ev);

            break;
        case MotionEvent.ACTION_MOVE:
            if (mLastY < ev.getY() && mIsTop && !isMove()) {
                gestureTouch(ev);
            }
            if (isMove() || isNormal()) {
                gestureTouch(ev);
            } else {
                super.onTouchEvent(ev);
            }

            return false;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (!isStartDrag) {
                super.onTouchEvent(ev);
            } else {
                MotionEvent ev1 = MotionEvent.obtain(ev);
                ev1.setAction(MotionEvent.ACTION_CANCEL);
                super.onTouchEvent(ev1);
            }
            gestureTouch(ev);
            isStartDrag = false;

            break;
        }

        return true;
    }

    private boolean gestureTouch(MotionEvent ev) {
        if (mListener != null) {
            mListener.onTouch(this, ev);
        }
        return false;
    }

    private OnTouchListener mListener;

    public void setTouchGestureDetector(OnTouchListener onListener) {
        mListener = onListener;
    }
}
