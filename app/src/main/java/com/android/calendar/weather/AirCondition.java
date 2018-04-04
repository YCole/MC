package com.android.calendar.weather;

/**
 * Created by wyl on 2017/3/9.
 */

public class AirCondition {

    /**
     * Date : 2017-03-21T19:00:00+08:00 EpochDate : 1490122800 Index : 139
     * ParticulateMatter2_5 : 106.0 ParticulateMatter10 : 139.0 Ozone : 105.0
     * CarbonMonoxide : 0.0 NitrogenMonoxide : null NitrogenDioxide : 35.0
     * SulfurDioxide : 7.0 Lead : null Source : MEP
     */

    private String Date;
    private int EpochDate;
    private int Index;
    private double ParticulateMatter2_5;
    private double ParticulateMatter10;
    private double Ozone;
    private double CarbonMonoxide;
    private Object NitrogenMonoxide;
    private double NitrogenDioxide;
    private double SulfurDioxide;
    private Object Lead;
    private String Source;

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        this.Date = Date;
    }

    public int getEpochDate() {
        return EpochDate;
    }

    public void setEpochDate(int EpochDate) {
        this.EpochDate = EpochDate;
    }

    public int getIndex() {
        return Index;
    }

    public void setIndex(int Index) {
        this.Index = Index;
    }

    public double getParticulateMatter2_5() {
        return ParticulateMatter2_5;
    }

    public void setParticulateMatter2_5(double ParticulateMatter2_5) {
        this.ParticulateMatter2_5 = ParticulateMatter2_5;
    }

    public double getParticulateMatter10() {
        return ParticulateMatter10;
    }

    public void setParticulateMatter10(double ParticulateMatter10) {
        this.ParticulateMatter10 = ParticulateMatter10;
    }

    public double getOzone() {
        return Ozone;
    }

    public void setOzone(double Ozone) {
        this.Ozone = Ozone;
    }

    public double getCarbonMonoxide() {
        return CarbonMonoxide;
    }

    public void setCarbonMonoxide(double CarbonMonoxide) {
        this.CarbonMonoxide = CarbonMonoxide;
    }

    public Object getNitrogenMonoxide() {
        return NitrogenMonoxide;
    }

    public void setNitrogenMonoxide(Object NitrogenMonoxide) {
        this.NitrogenMonoxide = NitrogenMonoxide;
    }

    public double getNitrogenDioxide() {
        return NitrogenDioxide;
    }

    public void setNitrogenDioxide(double NitrogenDioxide) {
        this.NitrogenDioxide = NitrogenDioxide;
    }

    public double getSulfurDioxide() {
        return SulfurDioxide;
    }

    public void setSulfurDioxide(double SulfurDioxide) {
        this.SulfurDioxide = SulfurDioxide;
    }

    public Object getLead() {
        return Lead;
    }

    public void setLead(Object Lead) {
        this.Lead = Lead;
    }

    public String getSource() {
        return Source;
    }

    public void setSource(String Source) {
        this.Source = Source;
    }
}
