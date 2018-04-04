package com.hct.calendar.utils;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtils {
    private DensityUtils() {
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * @param context
     * 
     * @param val
     * 
     * @return
     * 
     */

    public static int dp2px(Context context, float dpVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,

        dpVal, context.getResources().getDisplayMetrics());

    }

    /**
     * 
     * 
     * 
     * 
     * 
     * @param context
     * 
     * @param val
     * 
     * @return
     * 
     */

    public static int sp2px(Context context, float spVal)

    {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,

        spVal, context.getResources().getDisplayMetrics());

    }

    /**
     * 
     * 
     * 
     * 
     * 
     * @param context
     * 
     * @param pxVal
     * 
     * @return
     * 
     */

    public static float px2dp(Context context, float pxVal)

    {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (pxVal / scale);

    }

    /**
     * 
     * 
     * 
     * 
     * 
     * @param fontScale
     * 
     * @param pxVal
     * 
     * @return
     * 
     */

    public static float px2sp(Context context, float pxVal)

    {

        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);

    }

}
