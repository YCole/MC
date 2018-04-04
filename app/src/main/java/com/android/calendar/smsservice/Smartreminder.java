package com.android.calendar.smsservice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Smartreminder {
    public String Address;
    public String Message;
    public List<Date> datestartlist = new ArrayList<Date>();
    public List<Date> dateendlist = new ArrayList<Date>();

    public Smartreminder(String address, String message) {
        Address = address;
        Message = message;
    }

    public void addDate(List<Date> datestart, List<Date> dateend) {
        for (int i = 0; i < datestart.size(); i++) {
            Date dtstart = (Date) datestart.get(i).clone();
            Date dtend = (Date) dateend.get(i).clone();
            datestartlist.add(dtstart);
            dateendlist.add(dtend);
        }
    }

}
