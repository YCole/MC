package com.android.calendar.event;

import java.util.List;

/**
 * <pre>
 *  .--,       .--,
 * ( (  \.---./  ) )
 *  '.__/o   o\__.'
 *     {=  ^  =}
 *      >  -  <
 *     /       \
 *    //       \\
 *   //|   .   |\\
 *   "'\       /'"_.-~^`'-.
 *      \  _  /--'         `
 *    ___)( )(___
 *   (((__) (__)))
 * </pre>
 */
public class EventBean {
    public String title = "";
    public String desc = ""; // remark
    public String location = "";
    public long start; // ms
    public long end; // ms
    public int allDay; // 1,0
    public int reminder; // minute
    public String sync;
    // fushuo begin
    public String attend = "";
    public int color = -1;
    public String rule = null;
    public int selectItem;
    public List<MeetingPerson> meetingPersonList;

    @Override
    public String toString() {
        return "EventBean [title=" + title + ", desc=" + desc + ", location="
                + location + ", start=" + start + ", end=" + end + ", allDay="
                + allDay + ", reminder=" + reminder + ", attend=" + attend
                + ", color=" + color + "]";
    }

    // fushuo end
}
