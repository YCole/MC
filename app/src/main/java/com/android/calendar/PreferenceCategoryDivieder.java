package com.android.calendar;

import android.content.Context;
import android.util.AttributeSet;

import com.hct.gios.preference.Preference;

public class PreferenceCategoryDivieder extends Preference {

    public PreferenceCategoryDivieder(final Context context,
            final AttributeSet attrs, final int defStyle) {
        super(context, attrs);

        this.setLayoutResource(R.layout.perference_category_background);
    }

    public PreferenceCategoryDivieder(final Context context,
            final AttributeSet attrs) {
        this(context, attrs, 0);
    }
}
