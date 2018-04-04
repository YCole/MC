package com.hct.calendar.month;

import java.util.ArrayList;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class MonthWeekSwitcher implements OnTouchListener {

    private View mLinearLayout;
    public float mDistance;
    // the begin Y of slide
    private float mBegin;
    // the end Y of slide
    private float mEnd;
    // the process Y os slide
    private float mProcess;
    // the status of drop mothView
    public static final int NORMAL_MOTHVIEW = 1;
    public static final int DROP_NORMAL_MOTHVIEW = 2;
    public static final int TOP_MONTHVIEW = 3;
    public static Boolean isMove = false;
    private int mMonthStatus = NORMAL_MOTHVIEW;
    private List<AgendaTouchStateListener> mAgendaTouchStateListeners = new ArrayList<AgendaTouchStateListener>();
    private AgendaMoveListener mAgendaMoveListener = AgendaMoveListener.EMPTY;
    private GestureDetector mGesture;
    private AgendaGestureListener mGestureListener;
    protected int mDisOffset = 0;
    private int mState = AgendaTouchStateListener.AGENDA_NORMAL;
    private int mLastState = AgendaTouchStateListener.AGENDA_NORMAL;
    private float e2YTemp;

    public MonthWeekSwitcher(View v) {
        mLinearLayout = v;
        mGestureListener = new AgendaGestureListener();
        mGesture = new GestureDetector(v.getContext(), mGestureListener);
    }

    public void setDistance(float d) {
        this.mDistance = d;
    }

    public int getDistanceOffset() {
        return mDisOffset;
    }

    public void setAgendaMoveListener(AgendaMoveListener l) {
        if (l == null) {
            return;
        }
        mAgendaMoveListener = l;
    }

    public void addAgendaTouchStateListener(
            AgendaTouchStateListener agendaTouchStateListener) {
        if (agendaTouchStateListener == null) {
            return;
        }
        if (this.mAgendaTouchStateListeners.contains(agendaTouchStateListener)) {
            return;
        }
        this.mAgendaTouchStateListeners.add(agendaTouchStateListener);
    }

    private void changeState(int state) {
        if (mState == AgendaTouchStateListener.AGENDA_UNDROP) {
            mState = AgendaTouchStateListener.AGENDA_NORMAL;
        }
        if (mState != AgendaTouchStateListener.AGENDA_MOVE) {
            mLastState = mState;
        }
        mState = state;
        for (int i = 0; i < mAgendaTouchStateListeners.size(); i++) {
            mAgendaTouchStateListeners.get(i).onStateChange(state);
        }

    }

    public void gotoAgendaTop() {
        startAnimator(AgendaTouchStateListener.AGENDA_TOP);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mGesture.onTouchEvent(event);
        int action = event.getAction();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            mBegin = event.getRawY();
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            isMove = false;

            mEnd = event.getY();
            if (mLinearLayout.getTranslationY() == mDisOffset
                    || mLinearLayout.getTranslationY() == -mDistance) {
                if (mLinearLayout.getTranslationY() == mDisOffset) {
                    changeState(AgendaTouchStateListener.AGENDA_NORMAL);
                    mMonthStatus = NORMAL_MOTHVIEW;
                } else {
                    changeState(AgendaTouchStateListener.AGENDA_TOP);
                    mMonthStatus = TOP_MONTHVIEW;
                }
            } else {
                if (mLastState != AgendaTouchStateListener.AGENDA_DROP) {
                    startAnimator(AgendaTouchStateListener.AGENDA_MOVE);
                }
            }
            mBegin = 0;
            break;
        case MotionEvent.ACTION_MOVE:
            final int rawY = (int) event.getRawY();
            if (mBegin == 0) {
                mBegin = rawY;
            }
            mProcess = rawY;
            if (mMonthStatus == NORMAL_MOTHVIEW && mProcess > mBegin
                    && MonthFragment.itemHeight == MonthFragment.beginHeight
                    && mLinearLayout.getTranslationY() >= 0) {
                // drop monthView
                changeState(AgendaTouchStateListener.AGENDA_DROP);
                mMonthStatus = DROP_NORMAL_MOTHVIEW;
                e2YTemp = 0;
                isMove = true;
            } else if ((mProcess < mBegin)
                    && (MonthFragment.itemHeight == MonthFragment.dropHeight)) {
                changeState(AgendaTouchStateListener.AGENDA_UNDROP);
                mMonthStatus = NORMAL_MOTHVIEW;
                isMove = true;
            }
            break;
        default:
            break;
        }
        return true;
    }

    private void startAnimator(int state) {
        ValueAnimator animator = createAnimator(state);
        animator.setTarget(mLinearLayout);
        animator.setDuration(100);
        animator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mLinearLayout.setTranslationY((Float) animation
                        .getAnimatedValue());
                mAgendaMoveListener.onAgendaMove((Float) animation
                        .getAnimatedValue());
            }
        });
        animator.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                changeState(AgendaTouchStateListener.AGENDA_MOVE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mLinearLayout.getTranslationY() == mDisOffset) {
                    changeState(AgendaTouchStateListener.AGENDA_NORMAL);
                    mMonthStatus = NORMAL_MOTHVIEW;
                    e2YTemp = 0;
                } else if (mLinearLayout.getTranslationY() == -mDistance) {
                    changeState(AgendaTouchStateListener.AGENDA_TOP);
                    mMonthStatus = TOP_MONTHVIEW;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private ValueAnimator createAnimator(int targetState) {
        ValueAnimator animator = null;
        float startY = mLinearLayout.getTranslationY();
        if (targetState == AgendaTouchStateListener.AGENDA_TOP) {
            animator = ValueAnimator.ofFloat(startY, -mDistance);
        } else {
            float minFireDistance = mDistance / 6;
            if (mLastState == AgendaTouchStateListener.AGENDA_NORMAL) {
                if (Math.abs(startY) < minFireDistance) {
                    animator = ValueAnimator.ofFloat(startY, mDisOffset);
                } else {
                    animator = ValueAnimator.ofFloat(startY, -mDistance);
                }
            } else if (mLastState == AgendaTouchStateListener.AGENDA_TOP) {
                if ((startY + mDistance) < minFireDistance) {
                    animator = ValueAnimator.ofFloat(startY, -mDistance);
                } else {
                    animator = ValueAnimator.ofFloat(startY, mDisOffset);
                }
            }
        }
        return animator;
    }

    public final class AgendaGestureListener implements OnGestureListener {
        private float mOffset = 0;

        @Override
        public boolean onDown(MotionEvent e) {

            mOffset = mLinearLayout.getTranslationY();
            if (e2YTemp == 0) {
                e2YTemp = e.getRawY();
            }

            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                float velocityY) {

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            if (isMove && mMonthStatus != DROP_NORMAL_MOTHVIEW) {
                return false;
            }
            if (MonthFragment.itemHeight > MonthFragment.beginHeight) {
                return true;
            }

            if (e1 == null) {
                return false;
            }
            float eY = e2.getRawY() - e1.getRawY();
            float oldY = mLinearLayout.getTranslationY();

            float e2Y = e2.getRawY();
            if (e2YTemp == 0) {
                e2YTemp = e2Y;
            }

            if (e2Y == e2YTemp) {
                return false;

            }
            if (mMonthStatus == NORMAL_MOTHVIEW && e2Y - e2YTemp > 0
                    && e2YTemp != 0) {
                e2YTemp = e2Y;

                return false;
            }

            if (oldY <= 0
                    && ((mOffset + eY) <= mDisOffset && (mOffset + eY) >= -mDistance)) {

                mLinearLayout.setTranslationY(mOffset + eY);
                mAgendaMoveListener.onAgendaMove(mOffset + eY);
                changeState(AgendaTouchStateListener.AGENDA_MOVE);
            }
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

    }

    public static interface AgendaMoveListener {
        AgendaMoveListener EMPTY = new AgendaMoveListener() {
            @Override
            public boolean onAgendaMove(float distanceY) {
                return false;
            }
        };

        public boolean onAgendaMove(float distanceY);
    }

    public static interface AgendaTouchStateListener {
        public static final int AGENDA_NORMAL = 0;
        public static final int AGENDA_MOVE = 1;
        public static final int AGENDA_TOP = 2;
        // modified by zhangjunjie for drop mothView
        public static final int AGENDA_DROP = 3;
        public static final int AGENDA_UNDROP = 4;

        public void onStateChange(int state);

        public static AgendaTouchStateListener EMPTY = new AgendaTouchStateListener() {
            @Override
            public void onStateChange(int state) {

            }
        };
    }
}
