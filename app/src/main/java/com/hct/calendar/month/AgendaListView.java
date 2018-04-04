package com.hct.calendar.month;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import com.hct.calendar.month.MonthWeekSwitcher.AgendaTouchStateListener;

public class AgendaListView extends ListView implements
        AgendaTouchStateListener {
    private final static String TAG = "MyListView";

    protected boolean mIsTop = true;
    private int mLastY = 0;
    private float mTouchSlop;

    public AgendaListView(Context context, AttributeSet attrs,
            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                case OnScrollListener.SCROLL_STATE_IDLE:
                    if (getFirstVisiblePosition() == 0) {
                        if (getChildCount() > 0) {
                            View child = getChildAt(0);
                            if (child.getTop() == 0) {
                                mIsTop = true;
                            } else {
                                mIsTop = false;
                            }
                        }
                    } else {
                        mIsTop = false;
                    }
                    break;
                case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                    break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                if (totalItemCount == 0) {
                    return;
                }
                if (firstVisibleItem != 0) {
                    mIsTop = false;
                }
            }
        });
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public AgendaListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public AgendaListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AgendaListView(Context context) {
        this(context, null);
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
            smoothScrollToPosition(0);
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mLastY = (int) ev.getY();
            gestureTouch(ev);
        }
        return super.onInterceptTouchEvent(ev);
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
