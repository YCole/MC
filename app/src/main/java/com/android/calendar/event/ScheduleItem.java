package com.android.calendar.event;

/**
 * Created by Administrator on 2017/6/30.
 */

public class ScheduleItem {
    private int typeId;

    public ScheduleItem(int typeId) {
        this.typeId = typeId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
