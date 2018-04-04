package com.hct.calendar.domain;

/**
 * Created by cat on 2017/6/6.
 */

public class WeatherBody {

    public String LocalObservationDateTime;
    public int EpochTime;
    public String WeatherText;
    public int WeatherIcon;
    /**
     * Id : 7 Name : Huafeng WeatherCode : 01
     */

    public LocalSourceBean LocalSource;
    public boolean IsDayTime;
    /**
     * Metric : {"Value":26.1,"Unit":"C","UnitType":17} Imperial :
     * {"Value":79,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean Temperature;
    /**
     * Metric : {"Value":30.4,"Unit":"C","UnitType":17} Imperial :
     * {"Value":87,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean RealFeelTemperature;
    /**
     * Metric : {"Value":26.4,"Unit":"C","UnitType":17} Imperial :
     * {"Value":79,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean RealFeelTemperatureShade;
    public int RelativeHumidity;
    /**
     * Metric : {"Value":22,"Unit":"C","UnitType":17} Imperial :
     * {"Value":72,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean DewPoint;

    public WindBean Wind;
    /**
     * Speed :
     * {"Metric":{"Value":20.4,"Unit":"km/h","UnitType":7},"Imperial":{"Value"
     * :12.7,"Unit":"mi/h","UnitType":9}}
     */

    public WindGustBean WindGust;
    public int UVIndex;
    public String UVIndexText;
    /**
     * Metric : {"Value":8,"Unit":"km","UnitType":6} Imperial :
     * {"Value":5,"Unit":"mi","UnitType":2}
     */

    public Past6HourRangeBean Visibility;
    public String ObstructionsToVisibility;
    public int CloudCover;
    /**
     * Metric : {"Value":488,"Unit":"m","UnitType":5} Imperial :
     * {"Value":1600,"Unit":"ft","UnitType":0}
     */

    public Past6HourRangeBean Ceiling;
    /**
     * Metric : {"Value":1007,"Unit":"mb","UnitType":14} Imperial :
     * {"Value":29.74,"Unit":"inHg","UnitType":12}
     */

    public Past6HourRangeBean Pressure;

    public PressureTendencyBean PressureTendency;
    /**
     * Metric : {"Value":7.2,"Unit":"C","UnitType":17} Imperial :
     * {"Value":13,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean Past24HourTemperatureDeparture;
    /**
     * Metric : {"Value":28.3,"Unit":"C","UnitType":17} Imperial :
     * {"Value":83,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean ApparentTemperature;
    /**
     * Metric : {"Value":26.1,"Unit":"C","UnitType":17} Imperial :
     * {"Value":79,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean WindChillTemperature;
    /**
     * Metric : {"Value":23.3,"Unit":"C","UnitType":17} Imperial :
     * {"Value":74,"Unit":"F","UnitType":18}
     */

    public Past6HourRangeBean WetBulbTemperature;
    /**
     * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
     * {"Value":0.01,"Unit":"in","UnitType":1}
     */

    public Past6HourRangeBean Precip1hr;
    /**
     * Precipitation :
     * {"Metric":{"Value":0,"Unit":"mm","UnitType":3},"Imperial":
     * {"Value":0.01,"Unit":"in","UnitType":1}} PastHour :
     * {"Metric":{"Value":0,"Unit"
     * :"mm","UnitType":3},"Imperial":{"Value":0.01,"Unit":"in","UnitType":1}}
     * Past3Hours :
     * {"Metric":{"Value":0,"Unit":"mm","UnitType":3},"Imperial":{"Value"
     * :0.01,"Unit":"in","UnitType":1}} Past6Hours :
     * {"Metric":{"Value":0,"Unit":
     * "mm","UnitType":3},"Imperial":{"Value":0.01,"Unit":"in","UnitType":1}}
     * Past9Hours :
     * {"Metric":{"Value":0,"Unit":"mm","UnitType":3},"Imperial":{"Value"
     * :0.01,"Unit":"in","UnitType":1}} Past12Hours :
     * {"Metric":{"Value":7,"Unit"
     * :"mm","UnitType":3},"Imperial":{"Value":0.26,"Unit":"in","UnitType":1}}
     * Past18Hours :
     * {"Metric":{"Value":11,"Unit":"mm","UnitType":3},"Imperial":{
     * "Value":0.44,"Unit":"in","UnitType":1}} Past24Hours :
     * {"Metric":{"Value":18
     * ,"Unit":"mm","UnitType":3},"Imperial":{"Value":0.72,"Unit"
     * :"in","UnitType":1}}
     */

    public PrecipitationSummaryBean PrecipitationSummary;
    /**
     * Past6HourRange :
     * {"Minimum":{"Metric":{"Value":24.7,"Unit":"C","UnitType":
     * 17},"Imperial":{"Value"
     * :76,"Unit":"F","UnitType":18}},"Maximum":{"Metric":
     * {"Value":27.2,"Unit":"C"
     * ,"UnitType":17},"Imperial":{"Value":81,"Unit":"F","UnitType":18}}}
     * Past12HourRange :
     * {"Minimum":{"Metric":{"Value":21.9,"Unit":"C","UnitType"
     * :17},"Imperial":{"Value"
     * :71,"Unit":"F","UnitType":18}},"Maximum":{"Metric"
     * :{"Value":27.2,"Unit":"C"
     * ,"UnitType":17},"Imperial":{"Value":81,"Unit":"F","UnitType":18}}}
     * Past24HourRange :
     * {"Minimum":{"Metric":{"Value":20,"Unit":"C","UnitType":17
     * },"Imperial":{"Value"
     * :68,"Unit":"F","UnitType":18}},"Maximum":{"Metric":{"Value"
     * :27.2,"Unit":"C"
     * ,"UnitType":17},"Imperial":{"Value":81,"Unit":"F","UnitType":18}}}
     */

    public TemperatureSummaryBean TemperatureSummary;
    public String MobileLink;
    public String Link;

    public static class LocalSourceBean {
        public int Id;
        public String Name;
        public String WeatherCode;
    }

    public static class WindBean {

        public DirectionBean Direction;
        /**
         * Metric : {"Value":20.4,"Unit":"km/h","UnitType":7} Imperial :
         * {"Value":12.7,"Unit":"mi/h","UnitType":9}
         */

        public MaximumBean Speed;

        public static class DirectionBean {
            public int Degrees;
            public String Localized;
            public String English;
        }

    }

    public static class WindGustBean {
        /**
         * Metric : {"Value":20.4,"Unit":"km/h","UnitType":7} Imperial :
         * {"Value":12.7,"Unit":"mi/h","UnitType":9}
         */

        public MaximumBean Speed;

    }

    public static class PressureTendencyBean {
        public String LocalizedText;
        public String Code;
    }

    public static class PrecipitationSummaryBean {
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.01,"Unit":"in","UnitType":1}
         */

        public MaximumBean Precipitation;
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.01,"Unit":"in","UnitType":1}
         */

        public MaximumBean PastHour;
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.01,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past3Hours;
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.01,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past6Hours;
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.01,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past9Hours;
        /**
         * Metric : {"Value":7,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.26,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past12Hours;
        /**
         * Metric : {"Value":11,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.44,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past18Hours;
        /**
         * Metric : {"Value":18,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0.72,"Unit":"in","UnitType":1}
         */

        public MaximumBean Past24Hours;

    }

    public static class TemperatureSummaryBean {
        /**
         * Minimum :
         * {"Metric":{"Value":24.7,"Unit":"C","UnitType":17},"Imperial"
         * :{"Value":76,"Unit":"F","UnitType":18}} Maximum :
         * {"Metric":{"Value":27.2
         * ,"Unit":"C","UnitType":17},"Imperial":{"Value":
         * 81,"Unit":"F","UnitType":18}}
         */

        public Past6HourRangeBean Past6HourRange;
        /**
         * Minimum :
         * {"Metric":{"Value":21.9,"Unit":"C","UnitType":17},"Imperial"
         * :{"Value":71,"Unit":"F","UnitType":18}} Maximum :
         * {"Metric":{"Value":27.2
         * ,"Unit":"C","UnitType":17},"Imperial":{"Value":
         * 81,"Unit":"F","UnitType":18}}
         */

        public Past6HourRangeBean Past12HourRange;
        /**
         * Minimum :
         * {"Metric":{"Value":20,"Unit":"C","UnitType":17},"Imperial":{
         * "Value":68,"Unit":"F","UnitType":18}} Maximum :
         * {"Metric":{"Value":27.2
         * ,"Unit":"C","UnitType":17},"Imperial":{"Value":
         * 81,"Unit":"F","UnitType":18}}
         */

        public Past6HourRangeBean Past24HourRange;

    }

    public static class Past6HourRangeBean {
        /**
         * Metric : {"Value":24.7,"Unit":"C","UnitType":17} Imperial :
         * {"Value":76,"Unit":"F","UnitType":18}
         */

        public MaximumBean Minimum;
        /**
         * Metric : {"Value":27.2,"Unit":"C","UnitType":17} Imperial :
         * {"Value":81,"Unit":"F","UnitType":18}
         */

        public MaximumBean Maximum;

    }

    public static class MaximumBean {
        /**
         * Value : 27.2 Unit : C UnitType : 17
         */

        public MetricBean Metric;
        /**
         * Value : 81 Unit : F UnitType : 18
         */

        public MetricBean Imperial;

    }

    public static class MetricBean {
        public double Value;
        public String Unit;
        public int UnitType;
    }

}
