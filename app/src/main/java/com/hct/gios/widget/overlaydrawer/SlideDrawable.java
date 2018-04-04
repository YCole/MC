package com.hct.gios.widget.overlaydrawer;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

public class SlideDrawable extends Drawable implements Drawable.Callback {

    private Drawable mWrapped;
    private float mOffset;

    private final Rect mTmpRect = new Rect();

    public SlideDrawable(Drawable wrapped) {
        mWrapped = wrapped;
    }

    public void setOffset(float offset) {
        mOffset = offset;
        invalidateSelf();
    }

    public float getOffset() {
        return mOffset;
    }

    @Override
    public void draw(Canvas canvas) {
        mWrapped.copyBounds(mTmpRect);
        canvas.save();
        // canvas.translate(1.f / 3 * mTmpRect.width() * -mOffset, 0);
        mWrapped.draw(canvas);
        canvas.restore();
    }

    @Override
    public void setChangingConfigurations(int configs) {
        mWrapped.setChangingConfigurations(configs);
    }

    @Override
    public int getChangingConfigurations() {
        return mWrapped.getChangingConfigurations();
    }

    @Override
    public void setDither(boolean dither) {
        mWrapped.setDither(dither);
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mWrapped.setFilterBitmap(filter);
    }

    @Override
    public void setAlpha(int alpha) {
        mWrapped.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mWrapped.setColorFilter(cf);
    }

    @Override
    public void setColorFilter(int color, PorterDuff.Mode mode) {
        mWrapped.setColorFilter(color, mode);
    }

    @Override
    public void clearColorFilter() {
        mWrapped.clearColorFilter();
    }

    @Override
    public boolean isStateful() {
        return mWrapped.isStateful();
    }

    @Override
    public boolean setState(int[] stateSet) {
        return mWrapped.setState(stateSet);
    }

    @Override
    public int[] getState() {
        return mWrapped.getState();
    }

    @Override
    public Drawable getCurrent() {
        return mWrapped.getCurrent();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        return super.setVisible(visible, restart);
    }

    @Override
    public int getOpacity() {
        return mWrapped.getOpacity();
    }

    @Override
    public Region getTransparentRegion() {
        return mWrapped.getTransparentRegion();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        mWrapped.setState(state);
        return super.onStateChange(state);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        /** IUV:modify-start for bug-86955 by wenpd on 2017/01/11 */
        if (mWrapped != null) {
            mWrapped.setBounds(bounds);
        }
        /** IUV:modify-end */
    }

    @Override
    public int getIntrinsicWidth() {
        /** IUV:modify-start for bug-91485 by yukai.he on 2017/03/29 */
        if (mWrapped != null) {
            return mWrapped.getIntrinsicWidth();
        }
        return 0;
        /** IUV:modify-end */

    }

    @Override
    public int getIntrinsicHeight() {
        /** IUV:modify-start for bug-91485 by yukai.he on 2017/03/29 */
        if (mWrapped != null) {
            return mWrapped.getIntrinsicHeight();
        }
        return 0;
        /** IUV:modify-end */

    }

    @Override
    public int getMinimumWidth() {
        return mWrapped.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        return mWrapped.getMinimumHeight();
    }

    @Override
    public boolean getPadding(Rect padding) {
        return mWrapped.getPadding(padding);
    }

    @Override
    public ConstantState getConstantState() {
        return super.getConstantState();
    }

    @Override
    public void invalidateDrawable(Drawable who) {
        if (who == mWrapped) {
            invalidateSelf();
        }
    }

    @Override
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        if (who == mWrapped) {
            scheduleSelf(what, when);
        }
    }

    @Override
    public void unscheduleDrawable(Drawable who, Runnable what) {
        if (who == mWrapped) {
            unscheduleSelf(what);
        }
    }
}
