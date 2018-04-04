package com.android.calendar.weather;

import java.util.List;

public interface CurrentConditionCallBack {
    /**
     * The response was successful
     */
    void onLocationReqSuccess(List<CurrentCondition> result);

    /**
     * The response failed
     */
    void onLocationReqFailed(String errorMsg);
}
