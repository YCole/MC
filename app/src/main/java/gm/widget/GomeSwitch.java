package gm.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.CompoundButton;

import com.android.calendar.R;

/**
 * GomeSwitch
 *
 * @author wangjian
 * @since 2017-06-19
 */

public class GomeSwitch extends CompoundButton {
    public static final float DEFAULT_TRACK_MEASURE_RATIO = 2.0f;
    public static final int DEFAULT_THUMB_SIZE_DP = 16;
    public static final int DEFAULT_THUMB_MARGIN_DP = 2;
    public static final int DEFAULT_TEXT_MARGIN_DP = 2;
    public static final int DEFAULT_ANIMATION_DURATION = 250;
    public static final int DEFAULT_THUMB_ANIMATION_DURATION = 300;
    public static final int DEFAULT_TINT_COLOR = 0x327FC2;

    private static final int ENABLE_ATTR = android.R.attr.state_enabled;
    private static final int CHECKED_ATTR = android.R.attr.state_checked;
    private static final int PRESSED_ATTR = android.R.attr.state_pressed;

    private static int[] CHECKED_PRESSED_STATE = new int[]{android.R.attr.state_checked, android.R.attr.state_enabled, android.R.attr.state_pressed};
    private static int[] UNCHECKED_PRESSED_STATE = new int[]{-android.R.attr.state_checked, android.R.attr.state_enabled, android.R.attr.state_pressed};

    private Drawable mThumbDrawable, mTrackDrawable;
    private ColorStateList mTrackColor, mThumbColor;
    private float mThumbRadius, mTrackRadius;
    private RectF mThumbMargin;
    private float mTrackMeasureRatio;
    private long mAnimationDuration;
    // fade Track drawable or color when dragging or animating
    private boolean mFadeTrack;
    private int mTintColor;
    private PointF mThumbSizeF;

    private int mCurrThumbColor, mCurrTrackColor, mNextTrackColor, mOnTextColor, mOffTextColor;
    private Drawable mCurrentTrackDrawable, mNextTrackDrawable;
    private RectF mThumbRectF, mTrackRectF, mSafeRectF, mTextOnRectF, mTextOffRectF;
    private Paint mPaint;
    // whether using Drawable for thumb or Track
    private boolean mIsThumbUseDrawable, mIsTrackUseDrawable;
    private boolean mDrawDebugRect = false;
    private ObjectAnimator mProcessAnimator;
    // animation control
    private float mProcess;
    // temp position of thumb when dragging or animating
    private RectF mPresentThumbRectF;
    private float mStartX, mStartY, mLastX;
    private int mTouchSlop;
    private int mClickTimeout;
    private Paint mRectPaint;
    private CharSequence mTextOn;
    private CharSequence mTextOff;
    private TextPaint mTextPaint;
    private Layout mOnLayout;
    private Layout mOffLayout;
    private float mTextWidth;
    private float mTextHeight;
    private float mTextMarginH;
    private boolean mAutoAdjustTextPosition = true;
    // FIX #78,#85 : When restoring saved states, setChecked() called by super. So disable
    // animation and event listening when restoring.
    private boolean mRestoring = false;
    private ObjectAnimator mThumbAnimator;
    private float mThumbOffset;

    private OnCheckedChangeListener mChildOnCheckedChangeListener;

    /**
     * Construct a new Switch with default styling.
     *
     * @param context The Context that will determine this widget's theming.
     */
    public GomeSwitch(Context context) {
        this(context, null);
    }

    /**
     * Construct a new Switch with default styling, overriding specific style
     * attributes as requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from default styling.
     */
    public GomeSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.gomeSwitchStyle);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme attribute,
     * overriding specific style attributes as requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from the default styling.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     */
    public GomeSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    /**
     * Construct a new Switch with a default style determined by the given theme
     * attribute or style resource, overriding specific style attributes as
     * requested.
     *
     * @param context The Context that will determine this widget's theming.
     * @param attrs Specification of attributes that should deviate from the
     *        default styling.
     * @param defStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the view. Can be 0 to not look for defaults.
     * @param defStyleRes A resource identifier of a style resource that
     *        supplies default values for the view, used only if
     *        defStyleAttr is 0 or can not be found in the theme. Can be 0
     *        to not look for defaults.
     */
    public GomeSwitch (Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mClickTimeout = ViewConfiguration.getPressedStateDuration() + ViewConfiguration.getTapTimeout();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeWidth(getResources().getDisplayMetrics().density);

        mTextPaint = getPaint();

        mThumbRectF = new RectF();
        mTrackRectF = new RectF();
        mSafeRectF = new RectF();
        mThumbSizeF = new PointF();
        mThumbMargin = new RectF();
        mTextOnRectF = new RectF();
        mTextOffRectF = new RectF();

        mProcessAnimator = ObjectAnimator.ofFloat(this, "process", 0, 0).setDuration(DEFAULT_ANIMATION_DURATION);
        mProcessAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mProcessAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animateThmbState(false);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mThumbAnimator = ObjectAnimator.ofFloat(this, "thumboffset", 0, 0).setDuration(DEFAULT_ANIMATION_DURATION);
        mThumbAnimator.setInterpolator(new OvershootInterpolator(13f));

        mPresentThumbRectF = new RectF();

        Resources res = getResources();
        float density = res.getDisplayMetrics().density;

        Drawable thumbDrawable = null;
        ColorStateList thumbColor = null;
        float margin = density * DEFAULT_THUMB_MARGIN_DP;
        float marginLeft = 0;
        float marginRight = 0;
        float marginTop = 0;
        float marginBottom = 0;
        float thumbWidth = density * DEFAULT_THUMB_SIZE_DP;
        float thumbHeight = density * DEFAULT_THUMB_SIZE_DP;
        float thumbRadius = density * DEFAULT_THUMB_SIZE_DP / 2;
        float trackRadius = thumbRadius;
        Drawable trackDrawable = null;
        ColorStateList trackColor = null;
        float trackMeasureRatio = DEFAULT_TRACK_MEASURE_RATIO;
        int animationDuration = DEFAULT_ANIMATION_DURATION;
        boolean fadeTrack = true;
        int tintColor = 0;
        String textOn = null;
        String textOff = null;
        float textMarginH = density * DEFAULT_TEXT_MARGIN_DP;
        boolean autoAdjustTextPosition = true;

        TypedArray ta = context.obtainStyledAttributes(
                attrs, R.styleable.GomeSwitch, defStyleAttr, defStyleRes);
        if (ta != null) {
            thumbDrawable = ta.getDrawable(R.styleable.GomeSwitch_thumbDrawable);
            thumbDrawable = thumbDrawable != null ? thumbDrawable : res.getDrawable(R.drawable.gome_switch_thumb_selector);
            thumbColor = ta.getColorStateList(R.styleable.GomeSwitch_thumbColor);
            margin = ta.getDimension(R.styleable.GomeSwitch_thumbMargin, margin);
            marginLeft = ta.getDimension(R.styleable.GomeSwitch_thumbMarginLeft, margin);
            marginRight = ta.getDimension(R.styleable.GomeSwitch_thumbMarginRight, margin);
            marginTop = ta.getDimension(R.styleable.GomeSwitch_thumbMarginTop, margin);
            marginBottom = ta.getDimension(R.styleable.GomeSwitch_thumbMarginBottom, margin);

            thumbWidth = ta.getDimension(R.styleable.GomeSwitch_thumbWidth, thumbWidth);
            thumbHeight = ta.getDimension(R.styleable.GomeSwitch_thumbHeight, thumbHeight);
            thumbRadius = ta.getDimension(R.styleable.GomeSwitch_thumbRadius, Math.min(thumbWidth, thumbHeight) / 2.f);
            trackRadius = ta.getDimension(R.styleable.GomeSwitch_trackRadius, thumbRadius + density * 2f);
            trackDrawable = ta.getDrawable(R.styleable.GomeSwitch_trackDrawable);
            trackColor = ta.getColorStateList(R.styleable.GomeSwitch_trackColor);
            trackColor = trackColor != null ? trackColor : res.getColorStateList(R.color.gome_switch_track_color);
            trackMeasureRatio = ta.getFloat(R.styleable.GomeSwitch_trackMeasureRatio, trackMeasureRatio);
            animationDuration = ta.getInteger(R.styleable.GomeSwitch_animationDuration, animationDuration);
            fadeTrack = ta.getBoolean(R.styleable.GomeSwitch_fadeTrack, true);
            tintColor = ta.getColor(R.styleable.GomeSwitch_tintColor, tintColor);
            textOn = ta.getString(R.styleable.GomeSwitch_textOn);
            textOff = ta.getString(R.styleable.GomeSwitch_textOff);
            textMarginH = Math.max(textMarginH, trackRadius / 2);
            textMarginH = ta.getDimension(R.styleable.GomeSwitch_textMarginH, textMarginH);
            autoAdjustTextPosition = ta.getBoolean(R.styleable.GomeSwitch_autoAdjustTextPosition, autoAdjustTextPosition);
            ta.recycle();
        }

        // click
        ta = attrs == null ? null : context.obtainStyledAttributes(attrs, new int[]{android.R.attr.focusable, android.R.attr.clickable});
        if (ta != null) {
            boolean focusable = ta.getBoolean(0, true);
            //noinspection ResourceType
            boolean clickable = ta.getBoolean(1, focusable);
            setFocusable(focusable);
            setClickable(clickable);
            ta.recycle();
        } else {
            setFocusable(true);
            setClickable(true);
        }

        // text
        mTextOn = textOn;
        mTextOff = textOff;
        mTextMarginH = textMarginH;
        mAutoAdjustTextPosition = autoAdjustTextPosition;

        // thumb drawable and color
        mThumbDrawable = thumbDrawable;
        mThumbColor = thumbColor;
        mIsThumbUseDrawable = mThumbDrawable != null;
        mTintColor = tintColor;
        if (mTintColor == 0) {
            TypedValue typedValue = new TypedValue();
            boolean found = context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
            if (found) {
                mTintColor = typedValue.data;
            } else {
                mTintColor = DEFAULT_TINT_COLOR;
            }
        }
        if (!mIsThumbUseDrawable && mThumbColor == null) {
            mThumbColor = generateThumbColorWithTintColor(mTintColor);
            mCurrThumbColor = mThumbColor.getDefaultColor();
        }
        if (mIsThumbUseDrawable) {
            thumbWidth = Math.max(thumbWidth, mThumbDrawable.getMinimumWidth());
            thumbHeight = Math.max(thumbHeight, mThumbDrawable.getMinimumHeight());
        }
        mThumbSizeF.set(thumbWidth, thumbHeight);

        // track drawable and color
        mTrackDrawable = trackDrawable;
        mTrackColor = trackColor;
        mIsTrackUseDrawable = mTrackDrawable != null;
        if (!mIsTrackUseDrawable && mTrackColor == null) {
            mTrackColor = generateTrackColorWithTintColor(mTintColor);
            mCurrTrackColor = mTrackColor.getDefaultColor();
            mNextTrackColor = mTrackColor.getColorForState(CHECKED_PRESSED_STATE, mCurrTrackColor);
        }

        // margin
        mThumbMargin.set(marginLeft, marginTop, marginRight, marginBottom);
        // size & measure params must larger than 1
        mTrackMeasureRatio = mThumbMargin.width() >= 0 ? Math.max(trackMeasureRatio, 1) : trackMeasureRatio;
        mThumbRadius = thumbRadius;
        mTrackRadius = trackRadius;
        mAnimationDuration = animationDuration;
        mFadeTrack = fadeTrack;

        mProcessAnimator.setDuration(mAnimationDuration);
        // sync checked status
        if (isChecked()) {
            setProcess(1);
        }
    }


    private Layout makeLayout(CharSequence text) {
        return new StaticLayout(text, mTextPaint, (int) Math.ceil(Layout.getDesiredWidth(text, mTextPaint)), Layout.Alignment.ALIGN_CENTER, 1.f, 0, false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mOnLayout == null && mTextOn != null) {
            mOnLayout = makeLayout(mTextOn);
        }
        if (mOffLayout == null && mTextOff != null) {
            mOffLayout = makeLayout(mTextOff);
        }
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int widthMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measuredWidth;

        int minWidth = ceil(mThumbSizeF.x * mTrackMeasureRatio);
        if (mIsTrackUseDrawable) {
            minWidth = Math.max(minWidth, mTrackDrawable.getMinimumWidth());
        }
        float onWidth = mOnLayout != null ? mOnLayout.getWidth() : 0;
        float offWidth = mOffLayout != null ? mOffLayout.getWidth() : 0;
        if (onWidth != 0 || offWidth != 0) {
            mTextWidth = Math.max(onWidth, offWidth) + mTextMarginH * 2;
            float left = minWidth - mThumbSizeF.x;
            if (left < mTextWidth) {
                minWidth += mTextWidth - left;
            }
        } else {
            mTextWidth = 0;
        }
        minWidth = Math.max(minWidth, ceil(minWidth + mThumbMargin.left + mThumbMargin.right));
        minWidth = Math.max(minWidth, minWidth + getPaddingLeft() + getPaddingRight());
        minWidth = Math.max(minWidth, getSuggestedMinimumWidth());

        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = Math.max(minWidth, widthSize);
        } else {
            measuredWidth = minWidth;
            if (widthMode == MeasureSpec.AT_MOST) {
                measuredWidth = Math.min(measuredWidth, widthSize);
            }
        }

        return measuredWidth;
    }

    private int measureHeight(int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredHeight;

        int minHeight = ceil(Math.max(mThumbSizeF.y, mThumbSizeF.y + mThumbMargin.top + mThumbMargin.right));
        float onHeight = mOnLayout != null ? mOnLayout.getHeight() : 0;
        float offHeight = mOffLayout != null ? mOffLayout.getHeight() : 0;
        if (onHeight != 0 || offHeight != 0) {
            mTextHeight = Math.max(onHeight, offHeight);
            minHeight = ceil(Math.max(minHeight, mTextHeight));
        } else {
            mTextHeight = 0;
        }
        minHeight = Math.max(minHeight, getSuggestedMinimumHeight());
        minHeight = Math.max(minHeight, minHeight + getPaddingTop() + getPaddingBottom());

        if (heightMode == MeasureSpec.EXACTLY) {
            measuredHeight = Math.max(minHeight, heightSize);
        } else {
            measuredHeight = minHeight;
            if (heightMode == MeasureSpec.AT_MOST) {
                measuredHeight = Math.min(measuredHeight, heightSize);
            }
        }

        return measuredHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw || h != oldh) {
            setup();
        }
    }

    private int ceil(double dimen) {
        return (int) Math.ceil(dimen);
    }
    float thumbTop = 0;
    float thumbLeft = 0;
    /**
     * set up the rect of track and thumb
     */
    private void setup() {

        thumbTop = getPaddingTop() + Math.max(0, mThumbMargin.top);
        thumbLeft = getPaddingLeft() + Math.max(0, mThumbMargin.left);
        if (mOnLayout != null && mOffLayout != null) {
            if (mThumbMargin.top + mThumbMargin.bottom > 0) {
                // track is higher than thumb
                float addition = (getMeasuredHeight() - getPaddingBottom() - getPaddingTop() - mThumbSizeF.y - mThumbMargin.top - mThumbMargin.bottom) / 2;
                thumbTop += addition;
            }
        }

        if (mIsThumbUseDrawable) {
            mThumbSizeF.x = Math.max(mThumbSizeF.x, mThumbDrawable.getMinimumWidth());
            mThumbSizeF.y = Math.max(mThumbSizeF.y, mThumbDrawable.getMinimumHeight());
        }

        mThumbRectF.set(thumbLeft, thumbTop, thumbLeft + mThumbSizeF.x, thumbTop + mThumbSizeF.y);

        float TrackLeft = mThumbRectF.left - mThumbMargin.left;
        float textDiffWidth = Math.min(0, (Math.max(mThumbSizeF.x * mTrackMeasureRatio, mThumbSizeF.x + mTextWidth) - mThumbRectF.width() - mTextWidth) / 2);
        float textDiffHeight = Math.min(0, (mThumbRectF.height() + mThumbMargin.top + mThumbMargin.bottom - mTextHeight) / 2);
        mTrackRectF.set(TrackLeft + textDiffWidth,
                mThumbRectF.top - mThumbMargin.top + textDiffHeight,
                TrackLeft + mThumbMargin.left + Math.max(mThumbSizeF.x * mTrackMeasureRatio, mThumbSizeF.x + mTextWidth) + mThumbMargin.right - textDiffWidth,
                mThumbRectF.bottom + mThumbMargin.bottom - textDiffHeight);
        mSafeRectF.set(mThumbRectF.left, 0, mTrackRectF.right - mThumbMargin.right - mThumbRectF.width(), 0);

        float minTrackRadius = Math.min(mTrackRectF.width(), mTrackRectF.height()) / 2.f;
        mTrackRadius = Math.min(minTrackRadius, mTrackRadius);

        if (mTrackDrawable != null) {
            mTrackDrawable.setBounds((int) mTrackRectF.left, (int) mTrackRectF.top, ceil(mTrackRectF.right), ceil(mTrackRectF.bottom));
        }

        if (mOnLayout != null) {
            float marginOnX = mTrackRectF.left + (mTrackRectF.width() - mThumbRectF.width() - mThumbMargin.right - mOnLayout.getWidth()) / 2 + (mThumbMargin.left < 0 ? mThumbMargin.left * -0.5f : 0);
            if (!mIsTrackUseDrawable && mAutoAdjustTextPosition) {
                marginOnX += mTrackRadius / 4;
            }
            float marginOnY = mTrackRectF.top + (mTrackRectF.height() - mOnLayout.getHeight()) / 2;
            mTextOnRectF.set(marginOnX, marginOnY, marginOnX + mOnLayout.getWidth(), marginOnY + mOnLayout.getHeight());
        }

        if (mOffLayout != null) {
            float marginOffX = mTrackRectF.right - (mTrackRectF.width() - mThumbRectF.width() - mThumbMargin.left - mOffLayout.getWidth()) / 2 - mOffLayout.getWidth() + (mThumbMargin.right < 0 ? mThumbMargin.right * 0.5f : 0);
            if (!mIsTrackUseDrawable && mAutoAdjustTextPosition) {
                marginOffX -= mTrackRadius / 4;
            }
            float marginOffY = mTrackRectF.top + (mTrackRectF.height() - mOffLayout.getHeight()) / 2;
            mTextOffRectF.set(marginOffX, marginOffY, marginOffX + mOffLayout.getWidth(), marginOffY + mOffLayout.getHeight());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // fade Track
        if (mIsTrackUseDrawable) {
            if (mFadeTrack && mCurrentTrackDrawable != null && mNextTrackDrawable != null) {
                // fix #75, 70%A + 30%B != 30%B + 70%A, order matters when mix two layer of different alpha.
                // So make sure the order of on/off layers never change during slide from one endpoint to another.
                Drawable below = isChecked() ? mCurrentTrackDrawable : mNextTrackDrawable;
                Drawable above = isChecked() ? mNextTrackDrawable : mCurrentTrackDrawable;

                int alpha = (int) (255 * getProcess());
                below.setAlpha(alpha);
                below.draw(canvas);
                alpha = 255 - alpha;
                above.setAlpha(alpha);
                above.draw(canvas);
            } else {
                mTrackDrawable.setAlpha(255);
                mTrackDrawable.draw(canvas);
            }
        } else {
            if (mFadeTrack) {
                int alpha;
                int colorAlpha;

                // fix #75
                int belowColor = isChecked() ? mCurrTrackColor : mNextTrackColor;
                int aboveColor = isChecked() ? mNextTrackColor : mCurrTrackColor;

                // curr Track
                alpha = (int) (255 * getProcess());
                colorAlpha = Color.alpha(belowColor);
                colorAlpha = colorAlpha * alpha / 255;
                mPaint.setARGB(colorAlpha, Color.red(belowColor), Color.green(belowColor), Color.blue(belowColor));
                canvas.drawRoundRect(mTrackRectF, mTrackRadius, mTrackRadius, mPaint);
                // next Track
                alpha = 255 - alpha;
                colorAlpha = Color.alpha(aboveColor);
                colorAlpha = colorAlpha * alpha / 255;
                mPaint.setARGB(colorAlpha, Color.red(aboveColor), Color.green(aboveColor), Color.blue(aboveColor));
                canvas.drawRoundRect(mTrackRectF, mTrackRadius, mTrackRadius, mPaint);

                mPaint.setAlpha(255);
            } else {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(mCurrTrackColor);
                mPaint.setShadowLayer(2f, 1, 1, Color.BLUE);
                canvas.drawRoundRect(mTrackRectF, mTrackRadius, mTrackRadius, mPaint);
            }
        }

        // text
        Layout switchText = getProcess() > 0.5 ? mOnLayout : mOffLayout;
        RectF textRectF = getProcess() > 0.5 ? mTextOnRectF : mTextOffRectF;
        if (switchText != null && textRectF != null) {
            int alpha = (int) (255 * (getProcess() >= 0.75 ? getProcess() * 4 - 3 : (getProcess() < 0.25 ? 1 - getProcess() * 4 : 0)));
            int textColor = getProcess() > 0.5 ? mOnTextColor : mOffTextColor;
            int colorAlpha = Color.alpha(textColor);
            colorAlpha = colorAlpha * alpha / 255;
            switchText.getPaint().setARGB(colorAlpha, Color.red(textColor), Color.green(textColor), Color.blue(textColor));
            canvas.save();
            canvas.translate(textRectF.left, textRectF.top);
            switchText.draw(canvas);
            canvas.restore();
        }

        // thumb
        calcThumbAnim(mThumbOffset);
        mPresentThumbRectF.set(mThumbRectF);
        mPresentThumbRectF.offset(mProcess * mSafeRectF.width(), 0);
        if (mIsThumbUseDrawable) {
            mThumbDrawable.setBounds((int) mPresentThumbRectF.left, (int) mPresentThumbRectF.top, ceil(mPresentThumbRectF.right), ceil(mPresentThumbRectF.bottom));
            mThumbDrawable.draw(canvas);
        } else {
            mPaint.setColor(mCurrThumbColor);
            canvas.drawRoundRect(mPresentThumbRectF, mThumbRadius, mThumbRadius, mPaint);
        }

        if (mDrawDebugRect) {
            mRectPaint.setColor(Color.parseColor("#AA0000"));
            canvas.drawRect(mTrackRectF, mRectPaint);
            mRectPaint.setColor(Color.parseColor("#0000FF"));
            canvas.drawRect(mPresentThumbRectF, mRectPaint);
            mRectPaint.setColor(Color.parseColor("#00CC00"));
            canvas.drawRect(getProcess() > 0.5 ? mTextOnRectF : mTextOffRectF, mRectPaint);
        }
    }
    private void calcThumbAnim(float thumboffset) {
        if (mProcess > 0.5) {
            mThumbRectF.set(thumbLeft - thumboffset * mThumbRadius * 0.3f , thumbTop, thumbLeft + mThumbSizeF.x , thumbTop + mThumbSizeF.y);
        } else {
            mThumbRectF.set(thumbLeft, thumbTop, thumbLeft + mThumbSizeF.x + thumboffset * mThumbRadius * 0.3f, thumbTop + mThumbSizeF.y);
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        if (!mIsThumbUseDrawable && mThumbColor != null) {
            mCurrThumbColor = mThumbColor.getColorForState(getDrawableState(), mCurrThumbColor);
        } else {
            setDrawableState(mThumbDrawable);
        }

        int[] nextState = isChecked() ? UNCHECKED_PRESSED_STATE : CHECKED_PRESSED_STATE;
        ColorStateList textColors = getTextColors();
        if (textColors != null) {
            int defaultTextColor = textColors.getDefaultColor();
            mOnTextColor = textColors.getColorForState(CHECKED_PRESSED_STATE, defaultTextColor);
            mOffTextColor = textColors.getColorForState(UNCHECKED_PRESSED_STATE, defaultTextColor);
        }
        if (!mIsTrackUseDrawable && mTrackColor != null) {
            mCurrTrackColor = mTrackColor.getColorForState(getDrawableState(), mCurrTrackColor);
            mNextTrackColor = mTrackColor.getColorForState(nextState, mCurrTrackColor);
        } else {
            if (mTrackDrawable instanceof StateListDrawable && mFadeTrack) {
                mTrackDrawable.setState(nextState);
                mNextTrackDrawable = mTrackDrawable.getCurrent().mutate();
            } else {
                mNextTrackDrawable = null;
            }
            setDrawableState(mTrackDrawable);
            if (mTrackDrawable != null) {
                mCurrentTrackDrawable = mTrackDrawable.getCurrent().mutate();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!isEnabled() || !isClickable() || !isFocusable()) {
            return false;
        }

        int action = event.getAction();

        float deltaX = event.getX() - mStartX;
        float deltaY = event.getY() - mStartY;

        // status the view going to change to when finger released
        boolean nextStatus;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                catchView();
                mStartX = event.getX();
                mStartY = event.getY();
                mLastX = mStartX;
                setPressed(true);
                animateThmbState(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                setProcess(getProcess() + (x - mLastX) / mSafeRectF.width());
                mLastX = x;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                setPressed(false);
                nextStatus = getStatusBasedOnPos();
                float time = event.getEventTime() - event.getDownTime();
                if (deltaX < mTouchSlop && deltaY < mTouchSlop && time < mClickTimeout) {
                    performClick();
                } else {
                    if (nextStatus != isChecked()) {
                        playSoundEffect(SoundEffectConstants.CLICK);
                        setChecked(nextStatus);
                        animateThmbState(false);
                    } else {
                        animateToState(nextStatus);
                        animateThmbState(false);
                    }
                }
                break;

            default:
                break;
        }
        return true;
    }


    /**
     * return the status based on position of thumb
     *
     * @return
     */
    private boolean getStatusBasedOnPos() {
        return getProcess() > 0.5f;
    }

    public final float getProcess() {
        return mProcess;
    }

    public final void setProcess(final float process) {
        float tp = process;
        if (tp > 1) {
            tp = 1;
        } else if (tp < 0) {
            tp = 0;
        }
        this.mProcess = tp;
        invalidate();
    }

    //add begin by wangjian
    public final float getThumboffset() {
        return mThumbOffset;
    }

    public final void setThumboffset(float thumboffset) {
        if (thumboffset > 1) {
            thumboffset = 1;
        } else if (thumboffset < -1){
            thumboffset = -1f;
        }
        this.mThumbOffset = thumboffset;
        invalidate();
    }
    //add end by wangjian

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * processing animation
     *
     * @param checked checked or unChecked
     */
    protected void animateToState(boolean checked) {
        if (mProcessAnimator == null) {
            return;
        }
        if (mProcessAnimator.isRunning()) {
            mProcessAnimator.cancel();
        }
        mProcessAnimator.setDuration(mAnimationDuration);
        if (checked) {
            mProcessAnimator.setFloatValues(mProcess, 1f);
        } else {
            mProcessAnimator.setFloatValues(mProcess, 0);
        }
        mProcessAnimator.start();
    }

    /**
     * thumb animation
     *
     * @param pressed or unPressed
     */
    protected void animateThmbState(boolean pressed) {
        if (mThumbAnimator == null) {
            return;
        }
        if (mThumbAnimator.isRunning()) {
            mThumbAnimator.cancel();
        }
        mThumbAnimator.setDuration(DEFAULT_THUMB_ANIMATION_DURATION);
        if (pressed) {
            mThumbAnimator.setFloatValues(mThumbOffset,1f);
        } else {
            mThumbAnimator.setFloatValues(mThumbOffset ,0f);
        }
        mThumbAnimator.start();
    }

    private void catchView() {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
    }

    @Override
    public void setChecked(final boolean checked) {
        // animate before super.setChecked() become user may call setChecked again in OnCheckedChangedListener
        if (isChecked() != checked) {
            animateToState(checked);
            animateThmbState(true);
        }
        if (mRestoring) {
            setCheckedImmediatelyNoEvent(checked);
        } else {
            super.setChecked(checked);
        }
    }

    public void setCheckedNoEvent(final boolean checked) {
        if (mChildOnCheckedChangeListener == null) {
            setChecked(checked);
        } else {
            super.setOnCheckedChangeListener(null);
            setChecked(checked);
            super.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
        }
    }

    public void setCheckedImmediatelyNoEvent(boolean checked) {
        if (mChildOnCheckedChangeListener == null) {
            setCheckedImmediately(checked);
        } else {
            super.setOnCheckedChangeListener(null);
            setCheckedImmediately(checked);
            super.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
        }
    }

    public void toggleNoEvent() {
        if (mChildOnCheckedChangeListener == null) {
            toggle();
        } else {
            super.setOnCheckedChangeListener(null);
            toggle();
            super.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
        }
    }

    public void toggleImmediatelyNoEvent() {
        if (mChildOnCheckedChangeListener == null) {
            toggleImmediately();
        } else {
            super.setOnCheckedChangeListener(null);
            toggleImmediately();
            super.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
        }
    }

    @Override
    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        super.setOnCheckedChangeListener(onCheckedChangeListener);
        mChildOnCheckedChangeListener = onCheckedChangeListener;
    }

    public void setCheckedImmediately(boolean checked) {
        super.setChecked(checked);
        if (mProcessAnimator != null && mProcessAnimator.isRunning()) {
            mProcessAnimator.cancel();
        }
        setProcess(checked ? 1 : 0);
        invalidate();
    }

    public void toggleImmediately() {
        setCheckedImmediately(!isChecked());
    }

    private void setDrawableState(Drawable drawable) {
        if (drawable != null) {
            int[] myDrawableState = getDrawableState();
            drawable.setState(myDrawableState);
            invalidate();
        }
    }

    public boolean isDrawDebugRect() {
        return mDrawDebugRect;
    }

    public void setDrawDebugRect(boolean drawDebugRect) {
        mDrawDebugRect = drawDebugRect;
        invalidate();
    }

    public long getAnimationDuration() {
        return mAnimationDuration;
    }

    public void setAnimationDuration(long animationDuration) {
        mAnimationDuration = animationDuration;
    }

    public Drawable getThumbDrawable() {
        return mThumbDrawable;
    }

    public void setThumbDrawable(Drawable thumbDrawable) {
        mThumbDrawable = thumbDrawable;
        mIsThumbUseDrawable = mThumbDrawable != null;
        setup();
        refreshDrawableState();
        requestLayout();
        invalidate();
    }

    public void setThumbDrawableRes(int thumbDrawableRes) {
        setThumbDrawable(getContext().getDrawable(thumbDrawableRes));
    }

    public Drawable getTrackDrawable() {
        return mTrackDrawable;
    }

    public void setTrackDrawable(Drawable TrackDrawable) {
        mTrackDrawable = TrackDrawable;
        mIsTrackUseDrawable = mTrackDrawable != null;
        setup();
        refreshDrawableState();
        requestLayout();
        invalidate();
    }

    public void setTrackDrawableRes(int TrackDrawableRes) {
        setTrackDrawable(getContext().getDrawable(TrackDrawableRes));
    }

    public ColorStateList getTrackColor() {
        return mTrackColor;
    }

    public void setTrackColor(ColorStateList TrackColor) {
        mTrackColor = TrackColor;
        if (mTrackColor != null) {
            setTrackDrawable(null);
        }
        invalidate();
    }

    public void setTrackColorRes(int TrackColorRes) {
        setTrackColor(getContext().getColorStateList(TrackColorRes));
    }

    public ColorStateList getThumbColor() {
        return mThumbColor;
    }

    public void setThumbColor(ColorStateList thumbColor) {
        mThumbColor = thumbColor;
        if (mThumbColor != null) {
            setThumbDrawable(null);
        }
    }

    public void setThumbColorRes(int thumbColorRes) {
        setThumbColor(getContext().getColorStateList(thumbColorRes));
    }

    public float getTrackMeasureRatio() {
        return mTrackMeasureRatio;
    }

    public void setTrackMeasureRatio(float TrackMeasureRatio) {
        mTrackMeasureRatio = TrackMeasureRatio;
        requestLayout();
    }

    public RectF getThumbMargin() {
        return mThumbMargin;
    }

    public void setThumbMargin(RectF thumbMargin) {
        if (thumbMargin == null) {
            setThumbMargin(0, 0, 0, 0);
        } else {
            setThumbMargin(thumbMargin.left, thumbMargin.top, thumbMargin.right, thumbMargin.bottom);
        }
    }

    public void setThumbMargin(float left, float top, float right, float bottom) {
        mThumbMargin.set(left, top, right, bottom);
        requestLayout();
    }

    public void setThumbSize(float width, float height) {
        mThumbSizeF.set(width, height);
        setup();
        requestLayout();
    }

    public float getThumbWidth() {
        return mThumbSizeF.x;
    }

    public float getThumbHeight() {
        return mThumbSizeF.y;
    }

    public void setThumbSize(PointF size) {
        if (size == null) {
            float defaultSize = getResources().getDisplayMetrics().density * DEFAULT_THUMB_SIZE_DP;
            setThumbSize(defaultSize, defaultSize);
        } else {
            setThumbSize(size.x, size.y);
        }
    }

    public PointF getThumbSizeF() {
        return mThumbSizeF;
    }

    public float getThumbRadius() {
        return mThumbRadius;
    }

    public void setThumbRadius(float thumbRadius) {
        mThumbRadius = thumbRadius;
        if (!mIsThumbUseDrawable) {
            invalidate();
        }
    }

    public PointF getTrackSizeF() {
        return new PointF(mTrackRectF.width(), mTrackRectF.height());
    }

    public float getTrackRadius() {
        return mTrackRadius;
    }

    public void setTrackRadius(float TrackRadius) {
        mTrackRadius = TrackRadius;
        if (!mIsTrackUseDrawable) {
            invalidate();
        }
    }

    public boolean isFadeTrack() {
        return mFadeTrack;
    }

    public void setFadeTrack(boolean fadeTrack) {
        mFadeTrack = fadeTrack;
    }

    public int getTintColor() {
        return mTintColor;
    }

    public void setTintColor(int tintColor) {
        mTintColor = tintColor;
        mThumbColor = generateThumbColorWithTintColor(mTintColor);
        mTrackColor = generateTrackColorWithTintColor(mTintColor);
        mIsTrackUseDrawable = false;
        mIsThumbUseDrawable = false;
        // call this method to refresh color states
        refreshDrawableState();
        invalidate();
    }

    public void setText(CharSequence onText, CharSequence offText) {
        mTextOn = onText;
        mTextOff = offText;

        mOnLayout = null;
        mOffLayout = null;

        requestLayout();
        invalidate();
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.onText = mTextOn;
        ss.offText = mTextOff;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        setText(ss.onText, ss.offText);
        mRestoring = true;
        super.onRestoreInstanceState(ss.getSuperState());
        mRestoring = false;
    }

    static class SavedState extends BaseSavedState {
        CharSequence onText;
        CharSequence offText;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            onText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
            offText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            TextUtils.writeToParcel(onText, out, flags);
            TextUtils.writeToParcel(offText, out, flags);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    public static ColorStateList generateThumbColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {PRESSED_ATTR, -CHECKED_ATTR},
                {PRESSED_ATTR, CHECKED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[] {
                tintColor - 0xAA000000,
                0xFFBABABA,
                tintColor - 0x99000000,
                tintColor - 0x99000000,
                tintColor | 0xFF000000,
                0xFFEEEEEE
        };
        return new ColorStateList(states, colors);
    }

    public static ColorStateList generateTrackColorWithTintColor(final int tintColor) {
        int[][] states = new int[][]{
                {-ENABLE_ATTR, CHECKED_ATTR},
                {-ENABLE_ATTR},
                {CHECKED_ATTR, PRESSED_ATTR},
                {-CHECKED_ATTR, PRESSED_ATTR},
                {CHECKED_ATTR},
                {-CHECKED_ATTR}
        };

        int[] colors = new int[] {
                tintColor - 0xE1000000,
                0x10000000,
                tintColor - 0xD0000000,
                0x20000000,
                tintColor - 0xD0000000,
                0x20000000
        };
        return new ColorStateList(states, colors);
    }
}
