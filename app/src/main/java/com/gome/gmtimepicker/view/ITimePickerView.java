package com.gome.gmtimepicker.view;

import java.util.Calendar;

/**
 * @author Felix.Liang
 */
public interface ITimePickerView {

    void onTimeChanged(Calendar min, Calendar max, Calendar current);
}