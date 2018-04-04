/*
 *add by hct.gengbin
 */

package com.hct.gios.widget;

import com.android.calendar.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class HCTIndexFastScroller {
    final private static String TAG = "HCTIndexFastScroller";
    private float mIndexbarWidth;
    private float mIndexbarMargin;
    private float mPreviewPadding;
    private float mPreviewSize;
    private float mDensity;
    private float mScaledDensity;
    private float mAlphaRate;
    public int mState = STATE_HIDDEN;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private int mCurrentListIndex = -1;
    private boolean mIsShowPreview = false;
    private boolean mIsScrolling = false;
    private ListView mListView = null;
    private SectionIndexer mIndexer = null;
    private String[] mSections = null;
    private String[] mListContents = null;
    private RectF mIndexbarRect;
    private boolean mTouchScroller = false;
    private static final int STATE_HIDDEN = 0;
    private static final int STATE_SHOWING = 1;
    private static final int STATE_SHOWN = 2;
    private static final int STATE_HIDING = 3;

    private boolean isStopedScroll = false;
    private boolean mIsIndexing = false;
    private boolean mHideScrollBar = false;
    private boolean mToastTextDisPlay = true;
    private Adapter mAdapter;
    public boolean mShowScrollerBar = true;
    public boolean mShowPreviewNotOnScrollBar = true;
    public float mIndexBarTopMargin = 0;
    private int mScrollerBarTextColor = 0xff999999;
    private Context mContext = null;
    private GestureDetector mGestureDetector = null;
    private String strThumbText = null;
    private int lastListIndexInCenter = -1;
    private BitmapDrawable drawable = null;
    private Bitmap bitmap = null;
    private Paint indexbarPaint = null;
    private Paint indexPaint = null;
    private Paint previewPaint = null;
    private Paint previewTextPaint = null;

    public HCTIndexFastScroller(Context context, ListView listView) {
        mContext = context;
        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mListView = listView;
        setAdapter(mListView.getAdapter());

        mIndexbarWidth = 16 * mDensity; // mod gios 4.0
        mIndexbarMargin = 0 * mDensity;
        mPreviewPadding = 12 * mDensity;
        mPreviewSize = 81 * mDensity;
        mIndexBarTopMargin = mIndexBarTopMargin * mDensity;
        listView.setOnScrollListener(scrollListener);

        mIndexbarRect = new RectF(0, 0, 1, 1);

        drawable = (BitmapDrawable) mContext.getResources().getDrawable(
                R.drawable.fastscroll_label_hct_light);
        bitmap = drawable.getBitmap();

        indexbarPaint = new Paint();
        indexbarPaint.setColor(0x999999);
        indexbarPaint.setAlpha(0);
        indexbarPaint.setAntiAlias(true);

        indexPaint = new Paint();
        indexPaint.setAntiAlias(true);
        indexPaint.setTextSize(12 * mScaledDensity);// mod gios 4.0

        previewPaint = new Paint();
        previewPaint.setColor(Color.BLACK);

        previewTextPaint = new Paint();
        previewTextPaint.setColor(Color.WHITE);
        previewTextPaint.setAntiAlias(true);
        previewTextPaint.setTextSize(45 * mDensity);
    }

    // for displaying first String when scrolling listView.
    OnScrollListener scrollListener = new OnScrollListener() {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            case OnScrollListener.SCROLL_STATE_FLING:
                mIsScrolling = true;
                isStopedScroll = false;
                Log.i(TAG, "onScrollStateChanged STATE_SHOWING");
                setState(STATE_SHOWING);
                break;
            default:
                isStopedScroll = true;
                // modify by xianyi 2014.12.23 begin
                // mCurrentSection =
                // getSectionByListPos(mListView.getFirstVisiblePosition());//+4
                int pos = mListView.getFirstVisiblePosition()
                        - mListView.getHeaderViewsCount();
                if (pos < 0)
                    pos = 0;
                mCurrentSection = getSectionByListPos(pos);
                // modify by xianyi 2014.12.23 end
                Log.i(TAG, "onScrollStateChanged STATE_HIDING");

                setState(STATE_HIDING);
                break;
            }
        }

        @Override
        public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            // add by xianyi 2014.12.23 begin
            if (mIsScrolling && !isStopedScroll) {
                Log.i(TAG, "onScroll STATE_SHOWING");
                setState(STATE_SHOWING);
            }
            // add by xianyi 2014.12.23 end
        }
    };

    private String getItemTextThumbs(int position) {
        if (mAdapter != null) {
            boolean isHctAdapter = (mAdapter instanceof HCTCompositeCursorAdapter);
            if (isHctAdapter) {
                HCTCompositeCursorAdapter hctAdapter = (HCTCompositeCursorAdapter) mAdapter;
                String thumbsText = hctAdapter.getItemTitle(position);
                return thumbsText != null ? thumbsText.substring(0, 1) : null;
            } else {
                return mAdapter.getItem(position).toString().substring(0, 1);
            }
        }
        return null;
    }

    public void draw(Canvas canvas) {
        if (mSections != null && mSections.length > 0 && mShowScrollerBar) {

            /*
             * Paint indexbarPaint = new Paint();
             * indexbarPaint.setColor(0x999999); indexbarPaint.setAlpha(0);
             * indexbarPaint.setAntiAlias(true);
             */
            canvas.drawRoundRect(mIndexbarRect, 5 * mDensity, 5 * mDensity,
                    indexbarPaint);
            /*
             * Paint indexPaint = new Paint(); indexPaint.setAntiAlias(true);
             * indexPaint.setTextSize(15 * mScaledDensity);
             */

            float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMargin)
                    / mSections.length;
            float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint
                    .ascent())) / 2;
            for (int i = 0; i < mSections.length; i++) {
                if (i == mCurrentSection) {
                    indexPaint.setColor(mContext.getResources().getColor(
                            R.color.contacts_list_sd));
                } else {
                    indexPaint.setColor(mScrollerBarTextColor);
                }
                float paddingLeft = (mIndexbarWidth - indexPaint
                        .measureText(mSections[i])) / 2;
                canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft,
                        mIndexbarRect.top + mIndexbarMargin + sectionHeight * i
                                + paddingTop - indexPaint.ascent(), indexPaint);
            }
            /*
             * Paint previewPaint = new Paint();
             * previewPaint.setColor(Color.BLACK);
             * 
             * Paint previewTextPaint = new Paint();
             * previewTextPaint.setColor(Color.WHITE);
             * previewTextPaint.setAntiAlias(true);
             * previewTextPaint.setTextSize(45 * mDensity);
             */
            float previewSize = 2 * mPreviewPadding
                    + previewTextPaint.descent() - previewTextPaint.ascent();
            RectF previewRect = new RectF((mListViewWidth - previewSize) / 2,
                    (mListViewHeight - previewSize) / 2,
                    (mListViewWidth - previewSize) / 2 + previewSize,
                    (mListViewHeight - previewSize) / 2 + previewSize);
            /*
             * BitmapDrawable drawable = (BitmapDrawable)
             * mContext.getResources()
             * .getDrawable(R.drawable.fastscroll_label_hct_light); Bitmap
             * bitmap = drawable.getBitmap();
             */
            /*
             * String thumbText = null; int listIndexInCenter =
             * getListIndexInCenter(); if(listIndexInCenter ==
             * lastListIndexInCenter){ thumbText = strThumbText; }else{
             * thumbText = getItemTextThumbs(listIndexInCenter);
             * lastListIndexInCenter = listIndexInCenter; strThumbText =
             * thumbText; }
             */
            String thumbText = getItemTextThumbs(getListIndexInCenter());
            if (mToastTextDisPlay) {
                if (mTouchScroller) {
                    if (mCurrentSection >= 0 && mIsIndexing) {
                        float previewTextWidth = previewTextPaint
                                .measureText(mSections[mCurrentSection]);
                        // canvas.drawRoundRect(previewRect, 5 * mDensity, 5 *
                        // mDensity, previewPaint);
                        canvas.drawBitmap(bitmap, null, previewRect,
                                previewPaint);
                        canvas.drawText(mSections[mCurrentSection],
                                previewRect.left
                                        + (previewSize - previewTextWidth) / 2
                                        - 1, previewRect.top + mPreviewPadding
                                        - previewTextPaint.ascent() + 1,
                                previewTextPaint);
                        // canvas.drawText(mSections[mCurrentSection],
                        // previewRect.left + (mPreviewSize - previewTextWidth)
                        // / 2
                        // - 1
                        // , previewRect.top + (mPreviewSize-previewTextWidth)/2
                        // -
                        // previewTextPaint.ascent()-30, previewTextPaint);
                    }
                } else if ((mIsShowPreview || mIsScrolling)
                        && thumbText != null && mShowPreviewNotOnScrollBar) {
                    // Log.i(TAG,"gengbin,mCurrentListIndex = "+
                    // mCurrentListIndex);
                    // String thumbText =
                    // mListContents[getListIndexInCenter()].substring(0,1);
                    float previewTextWidth = previewTextPaint
                            .measureText(thumbText);
                    // canvas.drawRoundRect(previewRect, 5 * mDensity, 5 *
                    // mDensity,
                    // previewPaint);
                    canvas.drawBitmap(bitmap, null, previewRect, previewPaint);
                    canvas.drawText(thumbText, previewRect.left
                            + (previewSize - previewTextWidth) / 2 - 1,
                            previewRect.top + mPreviewPadding
                                    - previewTextPaint.ascent() + 1,
                            previewTextPaint);
                    // canvas.drawText(thumbText, previewRect.left +
                    // (mPreviewSize -
                    // previewTextWidth) / 2 - 1
                    // , previewRect.top + (mPreviewSize-previewTextWidth)/2 -
                    // previewTextPaint.ascent()-10, previewTextPaint);
                }
            }
        }
    }

    public boolean invalidTouchEvent(MotionEvent ev) {
        if (contains(ev.getX(), ev.getY())) {
            if (mTouchScroller) {
                if (MotionEvent.ACTION_UP == ev.getAction()) {
                    Log.i(TAG, "onInvalidTouchEvent MotionEvent.ACTION_UP");
                    mIsShowPreview = false;
                    mIsIndexing = false;
                    setState(STATE_HIDING);
                }
                int Section = getSectionByPoint(ev.getY());
                if (!checkSectionExistContacts(Section)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkSectionExistContacts(int section) {
        if (mIndexer == null) {
            return false;
        }

        int currentSectionPos = mIndexer.getPositionForSection(section);
        int nextSectionPos = mIndexer.getPositionForSection(section + 1);

        if ((currentSectionPos != nextSectionPos) && (nextSectionPos != -1)) {
            Log.i(TAG, "checkSectionExistContacts true, section = " + section
                    + ", currentSectionPos = " + currentSectionPos
                    + ", nextSectionPos = " + nextSectionPos);
            return true;
        } else {
            if (nextSectionPos == -1
                    && section == mIndexer
                            .getSectionForPosition(currentSectionPos + 1)) {
                Log.i(TAG,
                        "checkSectionExistContacts true, nextSectionPos = "
                                + nextSectionPos
                                + ", mIndexer.getSectionForPosition(currentSectionPos+1) = "
                                + mIndexer
                                        .getSectionForPosition(currentSectionPos + 1)
                                + ", section = " + section);
                return true;
            } else {
                Log.i(TAG, "checkSectionExistContacts false, section = "
                        + section);
                return false;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // If down event occurs inside index bar region, start indexing
            if (contains(ev.getX(), ev.getY())) {
                // It demonstrates that the motion event started from index
                // bar
                mIsShowPreview = true;
                // Determine which section the point is in, and move the
                // list to that section
                if (mTouchScroller) {
                    mIsIndexing = true;
                    mCurrentSection = getSectionByPoint(ev.getY());
                    mListView.setSelection(mIndexer
                            .getPositionForSection(mCurrentSection));
                    Log.i(TAG,
                            "gengbin,onTouchEvent():MotionEvent.ACTION_DOWN mTouchScroller=true");
                    return true;
                } else {
                    mIsShowPreview = false;
                    mCurrentSection = getSectionByListPos(mListView
                            .getFirstVisiblePosition() + 4);
                    Log.i(TAG,
                            "gengbin,onTouchEvent():MotionEvent.ACTION_DOWN mTouchScroller=false1");
                }
            }
            Log.i(TAG,
                    "gengbin,onTouchEvent():MotionEvent.ACTION_DOWN mTouchScroller=false2");
            break;
        case MotionEvent.ACTION_MOVE:
            if (contains(ev.getX(), ev.getY())) {
                if (mTouchScroller) {
                    mIsIndexing = true;
                    mCurrentSection = getSectionByPoint(ev.getY());
                    mListView.setSelection(mIndexer
                            .getPositionForSection(mCurrentSection));
                } else {
                    mGestureDetector = new GestureDetector(mContext,
                            new GestureDetector.SimpleOnGestureListener() {
                                @Override
                                public boolean onFling(MotionEvent e1,
                                        MotionEvent e2, float velocityX,
                                        float velocityY) {
                                    if (e2.getY() - e1.getY() > 100) {
                                        mCurrentSection = getSectionByListPos(mListView
                                                .getFirstVisiblePosition() + 4);
                                        mIsShowPreview = true;
                                    } else {
                                        mIsShowPreview = false;
                                    }
                                    return super.onFling(e1, e2, velocityX,
                                            velocityY);
                                }
                            });
                    mGestureDetector.onTouchEvent(ev);
                }
            }
            Log.i(TAG, "gengbin,onTouchEvent():MotionEvent.ACTION_MOVE");
            return false;
        case MotionEvent.ACTION_UP:
            mIsShowPreview = false;
            mIsIndexing = false;
            setState(STATE_HIDING);
            if (mTouchScroller) {
                return true;
            }
            Log.i(TAG, "gengbin,onTouchEvent():MotionEvent.ACTION_UP");
            break;
        case MotionEvent.ACTION_CANCEL:
            Log.i(TAG, "gengbin,onTouchEvent():MotionEvent.ACTION_CANCEL");
            mIsShowPreview = false;
            mIsIndexing = false;
            setState(STATE_HIDING);
            break;
        }
        Log.i(TAG, "gengbin,onTouchEvent():nocase");
        return false;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexbarRect = new RectF(w - mIndexbarMargin - mIndexbarWidth,
                mIndexbarMargin + mIndexBarTopMargin, w - mIndexbarMargin, h
                        - mIndexbarMargin);
    }

    public void show() {
        mHideScrollBar = true;
        if (mState == STATE_HIDDEN)
            setState(STATE_SHOWING);
    }

    public void hide() {
        mHideScrollBar = false;
    }

    public void setAdapter(Adapter adapter) {
        if (adapter instanceof SectionIndexer) {
            mIndexer = (SectionIndexer) adapter;
            mSections = (String[]) mIndexer.getSections();
            mAdapter = adapter;
        }
    }

    private void setState(int state) {
        if (state < STATE_HIDDEN || state > STATE_HIDING)
            return;

        mState = state;
        Log.i(TAG, "setState mState = " + mState);
        switch (mState) {
        case STATE_HIDDEN:
            // Cancel any fade effect
            mHandler.removeMessages(0);
            break;
        case STATE_SHOWING:
            // Start to fade in

            mAlphaRate = 0;
            fade(0);
            break;
        case STATE_SHOWN:
            // Cancel any fade effect
            mHandler.removeMessages(0);
            break;
        case STATE_HIDING:
            // Start to fade out after three seconds
            mAlphaRate = 1;
            mCurrentListIndex = -1;
            fade(500);
            break;
        }
    }

    // set touched point is in scroll bar.
    private boolean contains(float x, float y) {
        if (mIndexbarRect == null || y < mIndexbarRect.top
                && y > mIndexbarRect.top + mIndexbarRect.height())
            return false;
        // Determine if the point is in index bar region, which includes the
        // right margin of the bar
        if (mShowScrollerBar && x >= mIndexbarRect.left) {
            mTouchScroller = true;
        } else {
            mTouchScroller = false;
        }
        return true;
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMargin)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMargin)
            return mSections.length - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMargin) / ((mIndexbarRect
                .height() - 2 * mIndexbarMargin) / mSections.length));
    }

    private int getListIndexInCenter() {
        // modify by xianyi 2014.12.23 begin
        // if(mListView.getFirstVisiblePosition() >= mAdapter.getCount())
        // return mAdapter.getCount() - 1;
        // else
        // return mListView.getFirstVisiblePosition();//+4;
        int pos = mListView.getFirstVisiblePosition()
                - mListView.getHeaderViewsCount();
        if (pos < 0)
            pos = 0;
        return pos;
        // modify by xianyi 2014.12.23 end
    }

    private int getSectionByListPos(int position) {
        return mIndexer != null ? mIndexer.getSectionForPosition(position) : -1;
    }

    private void fade(long delay) {
        mHandler.removeMessages(0);
        mHandler.sendEmptyMessageAtTime(0, SystemClock.uptimeMillis() + delay);
    }

    public int getCurrentSection(float x, float y) {
        if (!mIndexbarRect.contains(x, y)) {
            return -1;
        } else {
            return mCurrentSection;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (mState) {
            case STATE_SHOWING:
                // Fade in effect
                mAlphaRate = 1;
                setState(STATE_SHOWN);

                // modify by xianyi 2014.12.23 begin
                // mCurrentSection
                // =getSectionByListPos(mListView.getFirstVisiblePosition()+4)
                // ;
                if (mTouchScroller == false) {
                    int pos = mListView.getFirstVisiblePosition()
                            - mListView.getHeaderViewsCount();
                    if (pos < 0)
                        pos = 0;
                    mCurrentSection = getSectionByListPos(pos);
                }
                // modify by xianyi 2014.12.23 end

                mListView.invalidate();
                // fade(10);
                break;
            case STATE_SHOWN:
                // If no action, hide automatically
                setState(STATE_HIDING);
                break;
            case STATE_HIDING:
                // Fade out effect
                if (isStopedScroll) {
                    mIsScrolling = false;
                }
                mAlphaRate = 0;
                setState(STATE_HIDDEN);
                mListView.invalidate();
                fade(10);
                break;
            }
        }
    };
}
