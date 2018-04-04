package com.android.calendar.view;

import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.icu.text.DateFormatSymbols;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.android.calendar.CalendarSettings;
import com.android.calendar.R;
import com.android.calendar.utils.DateUtils;
import com.gome.gmtimepicker.util.SizeTransformer;

/**
 * Month View
 * 
 * Created by liaozhenbin on 2017/6/19
 */
public class MonthView extends View {
    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    // private Paint mPaint;
    private int mDayColor = Color.parseColor("#000000");
    private int mSelectDayColor = Color.parseColor("#ffffff");
    private int mCurrentColor = Color.parseColor("#ff0000");
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize;
    // private DisplayMetrics mDisplayMetrics;
    // private int mDaySize = 10;
    private int[][] daysString = new int[NUM_ROWS][NUM_COLUMNS];
    // private int mCircleRadius = 6;
    // private DateClick dateClick;
    // private int mCircleColor = Color.parseColor("#ff0000");
    // private List<Integer> daysHasThingList;
    private final RectF mRectF = new RectF();
    private final String[] mShortWeekdays;
    private final String[] mShortMonths;
    private TextPaint mWeekdayPaint;
    private TextPaint mMonthPaint;
    private TextPaint mDayPaint;
    private Paint mTodayBgPaint;
    private float mVerticalOffSet;
    private float mWeekdayHeight;
    private final float mWeekTopMargin = dp2px(5);
    private final float mDayTopMargin = dp2px(3);
    private final float mMonthStartMargin = dp2px(3);
    private int mFirstDayOfWeek;

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // mDisplayMetrics = getResources().getDisplayMetrics();
        Calendar calendar = Calendar.getInstance();
        // mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
        final DateFormatSymbols symbols = DateFormatSymbols.getInstance(Locale
                .getDefault());
        String[] months = symbols.getShortMonths();
        mShortMonths = new String[months.length];
        for (int i = 0; i < mShortMonths.length; i++) {
            mShortMonths[i] = months[i].toUpperCase(Locale.getDefault());
        }
        mShortWeekdays = getResources().getStringArray(R.array.short_weekdays);
        initPaint();
        mFirstDayOfWeek = getFirstDayOfWeek();
        Log.e("test_", "init >> " + mFirstDayOfWeek);
    }

    private void initPaint() {
        mMonthPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mMonthPaint.setTextSize(dp2px(14));
        mMonthPaint.setColor(mCurrentColor);
        mMonthPaint.setTextAlign(Align.LEFT);
        FontMetrics metrics = mMonthPaint.getFontMetrics();
        mVerticalOffSet = -metrics.ascent + metrics.descent;
        mWeekdayPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mWeekdayPaint.setTextSize(dp2px(8));
        metrics = mWeekdayPaint.getFontMetrics();
        mWeekdayHeight = -metrics.ascent + metrics.descent;
        mDayPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mDayPaint.setTextSize(dp2px(9));
        mTodayBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTodayBgPaint.setStyle(Style.FILL);
        mTodayBgPaint.setColor(mCurrentColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize();
    }

    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = (int) ((getHeight() - mVerticalOffSet - mWeekdayHeight
                - mWeekTopMargin - mDayTopMargin - getPaddingTop() - getPaddingBottom()) / NUM_ROWS);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(0, mVerticalOffSet + getPaddingTop());
        drawMonths(canvas);
        canvas.translate(0, mWeekdayHeight + mWeekTopMargin);
        drawWeekDays(canvas);
        canvas.translate(0, mDayTopMargin);
        drawDays(canvas);
    }

    private void drawMonths(Canvas canvas) {
        canvas.drawText(mShortMonths[mSelMonth], mMonthStartMargin, 0,
                mMonthPaint);
    }

    private int getFirstDayOfWeek() {
        final SharedPreferences sp = getContext().getSharedPreferences(
                CalendarSettings.SETTINGS_SP_NAME, Context.MODE_PRIVATE);
        int firstDaySettings = sp.getInt("preferences_week_start_day",
                getDefaultFirstDayOfWeek());
        if (firstDaySettings == 0)
            return getDefaultFirstDayOfWeek();
        else
            return firstDaySettings;
    }

    private int getDefaultFirstDayOfWeek() {
        int weekday = Calendar.getInstance(Locale.getDefault())
                .getFirstDayOfWeek();
        return weekday % 7 + 1;
    }

    private void drawWeekDays(Canvas canvas) {
        final int firstDayOfWeek = mFirstDayOfWeek;
        for (int i = 0; i < mShortWeekdays.length; i++) {
            String weekday = mShortWeekdays[(i + firstDayOfWeek + 5) % 7];
            int startX = (int) (mColumnSize * i + (mColumnSize - mWeekdayPaint
                    .measureText(weekday)) / 2);
            canvas.drawText(weekday, startX, 0, mWeekdayPaint);
        }
    }

    private void drawDays(Canvas canvas) {
        final int firstDayOfWeek = mFirstDayOfWeek;
        String dayString;
        int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
        if (firstDayOfWeek == 3) {
            weekNumber = weekNumber == 1 ? 7 : weekNumber - 1;
        } else if (firstDayOfWeek == 1) {
            weekNumber = weekNumber == 7 ? 1 : weekNumber + 1;
        }
        for (int day = 0; day < mMonthDays; day++) {
            dayString = (day + 1) + "";
            int column = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            daysString[row][column] = day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mDayPaint
                    .measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mDayPaint
                    .ascent() + mDayPaint.descent()) / 2);
            if (dayString.equals(mCurrDay + "") && mSelYear == mCurrYear
                    && mSelMonth == mCurrMonth) {
                // draw current day background
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                mRectF.set(startRecX, startRecY, endRecX, endRecY);
                canvas.drawRoundRect(mRectF, 8, 8, mTodayBgPaint);
            }
            if (dayString.equals(mCurrDay + "") && mCurrDay != mSelDay
                    && mCurrMonth == mSelMonth && mCurrYear == mSelYear) {
                // set the current day color
                mDayPaint.setColor(mSelectDayColor);
            } else {
                mDayPaint.setColor(mDayColor);
            }
            canvas.drawText(dayString, startX, startY, mDayPaint);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            mFirstDayOfWeek = getFirstDayOfWeek();
        }
    }

    /**
     * set
     * 
     * @param year
     * @param month
     */
    public void setSelectYearMonth(int year, int month, int day) {
        mSelYear = year;
        mSelMonth = month;
        mSelDay = day;
    }

    /**
     * get current year
     * 
     * @return
     */
    public int getmSelYear() {
        return mSelYear;
    }

    /**
     * get current month
     * 
     * @return
     */
    public int getmSelMonth() {
        return mSelMonth;
    }

    /**
     * get current day
     */
    public int getmSelDay() {
        return this.mSelDay;
    }

    /**
     * set day color
     * 
     * @param mDayColor
     */
    public void setmDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }

    /**
     * set current day color
     * 
     * @param mSelectDayColor
     */
    public void setmSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    // /**
    // * set
    // *
    // * @param mSelectBGColor
    // */
    // public void setmSelectBGColor(int mSelectBGColor) {
    // this.mSelectBGColor = mSelectBGColor;
    // }

    // /**
    // * set day size
    // *
    // * @param mDaySize
    // */
    // public void setmDaySize(int mDaySize) {
    // this.mDaySize = mDaySize;
    // }

    private int dp2px(float dpValue) {
        return SizeTransformer.dp2px(getContext(), dpValue);
    }

}
