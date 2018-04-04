package com.hct.dom;

import android.util.Log;

public class Rawdata {
    public String[] YEAR;
    public String[] Month01;
    public String[] Month02;
    public String[] Month03;
    public String[] Month04;
    public String[] Month05;
    public String[] Month06;
    public String[] Month07;
    public String[] Month08;
    public String[] Month09;
    public String[] Month10;
    public String[] Month11;
    public String[] Month12;
    public String[] lastMonth12;

    private String TAG = "Rawdata";

    public Rawdata() {
    }

    private String[] string2array(String s) {
        int size = Integer.parseInt(s.substring(s.length() - 2, s.length()));
        String[] temp = new String[size];
        String item = "";
        Log.v(TAG, "s=" + s);
        for (int i = 0, j = 0; i != s.length(); i++) {
            item = item + s.substring(i, i + 1);
            if (s.substring(i, i + 1).equals(",")) {
                temp[j] = item.substring(0, item.length() - 1);
                item = "";
                j++;
            }
        }
        return temp;
    }

    public void setJan(String s) {
        this.Month01 = string2array(s);
    }

    public void setFeb(String s) {
        this.Month02 = string2array(s);
    }

    public void setMarch(String s) {
        this.Month03 = string2array(s);
    }

    public void setApril(String s) {
        this.Month04 = string2array(s);
    }

    public void setMay(String s) {
        this.Month05 = string2array(s);
    }

    public void setJune(String s) {
        this.Month06 = string2array(s);
    }

    public void setJuly(String s) {
        this.Month07 = string2array(s);
    }

    public void setAug(String s) {
        this.Month08 = string2array(s);
    }

    public void setSept(String s) {
        this.Month09 = string2array(s);
    }

    public void setOct(String s) {
        this.Month10 = string2array(s);
    }

    public void setNov(String s) {
        this.Month11 = string2array(s);
    }

    public void setDec(String s) {
        this.Month12 = string2array(s);
    }

    public void setlastDec(String s) {
        this.lastMonth12 = string2array(s);
    }

    public void setYear(String s) {
        this.YEAR = string2array(s);
    }
}