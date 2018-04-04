package com.android.calendar.weather;

public interface ResultCallback<T> {
    /**
     * The response was successful
     */
    void onReqSuccess(T result);

    /**
     * The response failed
     */
    void onReqFailed(String errorMsg);
}