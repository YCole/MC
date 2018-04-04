package com.android.calendar.weather;

import java.util.List;

public interface LocationTextCallBack {
    /**
     * The response was successful
     */
    void onLocationReqSuccess(List<LocationText> result);

    /**
     * The response failed
     */
    void onLocationReqFailed(String errorMsg);
}
