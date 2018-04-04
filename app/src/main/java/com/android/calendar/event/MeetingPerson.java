package com.android.calendar.event;

import java.util.Calendar;

/**
 * Created by Administrator on 2017/6/24.
 */

public class MeetingPerson {
    private String personName;

    private String personPhone;
    private Calendar mStart;
    private Calendar mEnd;
    private String place;
    private String title;
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public MeetingPerson(String personName, String personPhone) {
        super();
        this.personName = personName;
        this.personPhone = personPhone;
    }

    public MeetingPerson(String personName, String personPhone,
            Calendar mStart, Calendar mEnd, String place, String title) {
        super();
        this.personName = personName;
        this.personPhone = personPhone;
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.place = place;
        this.title = title;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public void setPersonPhone(String personPhone) {
        this.personPhone = personPhone;
    }

    public String getPersonName() {
        return personName;
    }

    public Calendar getmStart() {
        return mStart;
    }

    public void setmStart(Calendar mStart) {
        this.mStart = mStart;
    }

    public Calendar getmEnd() {
        return mEnd;
    }

    public void setmEnd(Calendar mEnd) {
        this.mEnd = mEnd;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPersonPhone() {
        return personPhone;
    }

    public String getTimeFrom(Calendar calendar) {
        String startDateStr = calendar.get(Calendar.YEAR) + "-"
                + (calendar.get(Calendar.MONTH) + 1) + "-"
                + calendar.get(Calendar.DAY_OF_MONTH) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE);
        return startDateStr;
    }

    @Override
    public String toString() {
        return "MeetingPerson [personName=" + personName + ", personPhone="
                + personPhone + ", mStart=" + mStart + ", mEnd=" + mEnd
                + ", place=" + place + ", title=" + title + ", index=" + index
                + "]";
    }

}
