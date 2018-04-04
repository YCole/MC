package com.hct.calendar.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    public static void show(Context c, Object text) {
        Toast.makeText(c, text.toString(), Toast.LENGTH_SHORT).show();
        
    }

}
