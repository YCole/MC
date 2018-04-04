package com.hct.calendar.data;

public interface Action<T> {
    void next(final T data);
}