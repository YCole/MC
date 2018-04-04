package gm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RemoteViews.RemoteView;

/**
 * Created by chenchen on 2017/5/8.
 */
@RemoteView
public class GomeListView extends ListView {
    private static final int TOUCH_STATE_NONE = 0;
    private static final int TOUCH_STATE_X = 1;
    private static final int TOUCH_STATE_Y = 2;

    private int MAX_Y = 5;
    private int MAX_X = 3;
    private float mDownX;
    private float mDownY;
    private int mTouchState;
    private int mTouchPosition;
    private gm.widget.GomeSwipeMenuLayout mTouchView;
    private OnSwipeListener mOnSwipeListener;

    private gm.widget.GomeSwipeMenuCreator mLeftMenuCreator;
    private gm.widget.GomeSwipeMenuCreator mRightMenuCreator;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private OnMenuItemClickListener mOnLeftMenuItemClickListener;
    private Interpolator mCloseInterpolator;
    private Interpolator mOpenInterpolator;
    private boolean ISSupportLeftAndRightSlide = false;
    public static final int ACTION_MASK = 0xff;
    public GomeListView(Context context) {
        super(context);
        init();
    }

    public GomeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public GomeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        MAX_X = dp2px(MAX_X);
        MAX_Y = dp2px(MAX_Y);
        mTouchState = TOUCH_STATE_NONE;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(new gm.widget.GomeSwipeMenuAdapter(getContext(), adapter) {

            @Override
            public void createLeftMenu(GomeSwipeMenu menu) {
                if (mLeftMenuCreator != null) {
                    mLeftMenuCreator.create(menu);
                }
            }

            @Override
            public void createRightMenu(gm.widget.GomeSwipeMenu menu) {
                if (mRightMenuCreator != null) {
                    mRightMenuCreator.create(menu);
                }
            }

            @Override
            public void onItemClick(GomeSwipeMenuView view, gm.widget.GomeSwipeMenu menu, int index) {
                if (mOnMenuItemClickListener != null) {
                    mOnMenuItemClickListener.onMenuItemClick(view.getPosition(), menu, index);
                }
                if (mTouchView != null) {
                    mTouchView.smoothCloseMenu();
                }
            }
        });
    }

    public void setCloseInterpolator(Interpolator interpolator) {
        mCloseInterpolator = interpolator;
    }

    public void setOpenInterpolator(Interpolator interpolator) {
        mOpenInterpolator = interpolator;
    }

    public Interpolator getOpenInterpolator() {
        return mOpenInterpolator;
    }

    public Interpolator getCloseInterpolator() {
        return mCloseInterpolator;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (ev.getAction() != MotionEvent.ACTION_DOWN && mTouchView == null)
            return super.onTouchEvent(ev);
        //int action = MotionEventCompat.getActionMasked(ev);

        int action =ev.getAction() & ACTION_MASK;
        action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int oldPos = mTouchPosition;
                mDownX = ev.getX();
                mDownY = ev.getY();
                mTouchState = TOUCH_STATE_NONE;

                mTouchPosition = pointToPosition((int) ev.getX(), (int) ev.getY());

                if (mTouchPosition == oldPos && mTouchView != null && mTouchView.isOpen()) {
                    mTouchState = TOUCH_STATE_X;
                    mTouchView.onSwipe(ev,ISSupportLeftAndRightSlide);
                    return true;
                }

                View view = getChildAt(mTouchPosition - getFirstVisiblePosition());

                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                    mTouchView = null;
                    // return super.onTouchEvent(ev);
                }
                if (view instanceof gm.widget.GomeSwipeMenuLayout) {
                    mTouchView = (gm.widget.GomeSwipeMenuLayout) view;
                }
                if (mTouchView != null) {
                    mTouchView.onSwipe(ev,ISSupportLeftAndRightSlide);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = Math.abs((ev.getY() - mDownY));
                float dx = Math.abs((ev.getX() - mDownX));
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev,ISSupportLeftAndRightSlide);
                    }
                    getSelector().setState(new int[] { 0 });
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                } else if (mTouchState == TOUCH_STATE_NONE) {
                    if (Math.abs(dy) > MAX_Y) {
                        mTouchState = TOUCH_STATE_Y;
                    } else if (dx > MAX_X) {
                        mTouchState = TOUCH_STATE_X;
                        if (mOnSwipeListener != null) {
                            mOnSwipeListener.onSwipeStart(mTouchPosition);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_X) {
                    if (mTouchView != null) {
                        mTouchView.onSwipe(ev,ISSupportLeftAndRightSlide);
                        if (!mTouchView.isOpen()) {
                            mTouchPosition = -1;
                            mTouchView = null;
                        }
                    }
                    if (mOnSwipeListener != null) {
                        mOnSwipeListener.onSwipeEnd(mTouchPosition);
                    }
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    super.onTouchEvent(ev);
                    return true;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void smoothOpenRightMenu(int position) {
        if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof gm.widget.GomeSwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (gm.widget.GomeSwipeMenuLayout) view;
                mTouchView.smoothOpenRightMenu();
            }
        }
    }

    public void smoothOpenLeftMenu(int position) {
        if (position >= getFirstVisiblePosition() && position <= getLastVisiblePosition()) {
            View view = getChildAt(position - getFirstVisiblePosition());
            if (view instanceof gm.widget.GomeSwipeMenuLayout) {
                mTouchPosition = position;
                if (mTouchView != null && mTouchView.isOpen()) {
                    mTouchView.smoothCloseMenu();
                }
                mTouchView = (GomeSwipeMenuLayout) view;
                mTouchView.smoothOpenLeftMenu();
            }
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }

    public void setRightMenuCreator(gm.widget.GomeSwipeMenuCreator menuCreator) {
        this.mRightMenuCreator = menuCreator;
    }

    public void setLeftMenuCreator(GomeSwipeMenuCreator menuCreator) {
        this.mLeftMenuCreator = menuCreator;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnLeftMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnLeftMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.mOnSwipeListener = onSwipeListener;
    }

    public  void SetSupportLeftAndRightSlide(boolean bl){
        this.ISSupportLeftAndRightSlide =bl;
    }

    public static interface OnMenuItemClickListener {
        void onMenuItemClick(int position, GomeSwipeMenu menu, int index);
    }

    public static interface OnSwipeListener {
        void onSwipeStart(int position);

        void onSwipeEnd(int position);
    }
}
