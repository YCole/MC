package com.android.calendar.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import android.annotation.IntDef;

public class RepeatBean {
    //
    public static final int ONCE = 0;
    public static final int EVERY_DAY = 1;
    public static final int EVERY_HOUR = 2;
    public static final int EVERY_WEEK = 3;
    public static final int EVERY_MONTH = 4;
    public static final int EVERY_YEAR = 5;

    @IntDef({ ONCE, EVERY_DAY, EVERY_HOUR, EVERY_WEEK, EVERY_MONTH, EVERY_YEAR })
    @Retention(RetentionPolicy.SOURCE)
    public @interface REPEAT {
    }

    // ########################################
    @REPEAT
    public int repeate = ONCE;
    public String text; //

    public RepeatBean(int repeate, String text) {
        super();
        this.repeate = repeate;
        this.text = text;
    }

    public RepeatBean() {
        super();
    }

}
