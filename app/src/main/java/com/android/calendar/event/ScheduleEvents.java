package com.android.calendar.event;

import java.io.Serializable;

public class ScheduleEvents implements Serializable {
    public int id;
    public String title;
    public String eventLocation;
    public String description;
    public int eventColor;
    public long dtstart;
    public long dtend;
    private int type;
    private String rrule;
    private int allDay;

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public String getRrule() {
        return rrule;
    }

    public void setRrule(String rrule) {
        this.rrule = rrule;
    }

    public void setDtstart(long dtstart) {
        this.dtstart = dtstart;
    }

    public void setDtend(long dtend) {
        this.dtend = dtend;
    }

    // fushuo begin
    private boolean isShow;
    private boolean isChecked;

    // fushuo end

    // fushuo begin
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean isShow) {
        this.isShow = isShow;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    // fushuo end

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ScheduleEvents(int id, String title, String eventLocation,
            String description, int eventColor, long dtstart, long dtend,
            String rrule, int all) {
        super();
        this.id = id;
        this.title = title;
        this.eventLocation = eventLocation;
        this.description = description;
        this.eventColor = eventColor;
        this.dtstart = dtstart;
        this.dtend = dtend;
        this.rrule = rrule;
        this.allDay = all;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEventColor() {
        return eventColor;
    }

    public void setEventColor(int eventColor) {
        this.eventColor = eventColor;
    }

    public long getDtstart() {
        return dtstart;
    }

    public void setDtstart(int dtstart) {
        this.dtstart = dtstart;
    }

    public long getDtend() {
        return dtend;
    }

    public void setDtend(int dtend) {
        this.dtend = dtend;
    }

}
