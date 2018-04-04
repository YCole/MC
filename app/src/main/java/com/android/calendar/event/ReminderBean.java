package com.android.calendar.event;

/**
 * 
 * 
 * @author cat
 * 
 */
public class ReminderBean {

    public int minute;
    public String text;

    public ReminderBean() {
        super();
    }

    public ReminderBean(int minute, String text) {
        super();
        this.minute = minute;
        this.text = text;
    }

}
