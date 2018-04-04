/*
 *add by hct.gengbin
 *
 */

package com.hct.gios.widget;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;

public abstract class HCTCompositeCursorAdapter {// extends
                                                 // com.android.common.widget.CompositeCursorAdapter
                                                 // {

    public static final String TAG = "HCTCompositeCursorAdapter";

    public HCTCompositeCursorAdapter(Context context) {
        // super(context);
    }

    public HCTCompositeCursorAdapter(Context context, int initialCapacity) {
        // super(context, initialCapacity);
    }

    public abstract String getItemTitle(int position);
}
