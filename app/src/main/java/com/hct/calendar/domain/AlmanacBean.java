package com.hct.calendar.domain;

/**
 * 
 * @author cat
 * 
 */
public class AlmanacBean {
    public String eachDay;

    public String getEachDay() {
        return eachDay;
    }

    public void setEachDay(String eachDay) {
        this.eachDay = eachDay;
    }

    public String getDateJson() {
        return dateJson;
    }

    public void setDateJson(String dateJson) {
        this.dateJson = dateJson;
    }

    public String dateJson;

    public AlmanacBean() {
        super();
        // TODO Auto-generated constructor stub
    }

    public AlmanacBean(String eachDay, String dateJson) {
        super();
        this.eachDay = eachDay;
        this.dateJson = dateJson;
    }

}
