package com.gm.internal.menu;

import android.content.Context;

import com.android.calendar.R;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class ActionMenuPolicy {
    private Context mContext;

    public static ActionMenuPolicy get(Context context) {
        return new ActionMenuPolicy(context);
    }

    private ActionMenuPolicy(Context context) {
        mContext = context;
    }

    /**
     * Returns the maximum number of action buttons that should be permitted within an action
     * bar/action mode. This will be used to determine how many showAsAction="ifRoom" items can fit.
     * "always" items can override this.
     */
    public int getMaxActionButtons() {
        return mContext.getResources().getInteger(R.integer.gome_max_float_action_num);
    }
    public boolean showsOverflowMenuButton() {
        return true;
    }

}
