package com.hct.calendar.domain;

import java.util.List;

/**
 * Created by cat on 2017/6/6.
 */

public class LocationBody {

    public int Version;
    public String Key;
    public String Type;
    public int Rank;
    public String LocalizedName;
    public String EnglishName;
    public String PrimaryPostalCode;

    public RegionBean Region;

    public CountryBean Country;

    public AdministrativeAreaBean AdministrativeArea;
    /**
     * Code : CST Name : Asia/Shanghai GmtOffset : 8 IsDaylightSaving : false
     * NextOffsetChange : null
     */

    public TimeZoneBean TimeZone;
    /**
     * Latitude : 31.22 Longitude : 121.549 Elevation :
     * {"Metric":{"Value":6,"Unit"
     * :"m","UnitType":5},"Imperial":{"Value":19,"Unit":"ft","UnitType":0}}
     */

    public GeoPositionBean GeoPosition;
    public boolean IsAlias;
    /**
     * Key : 74761 StationCode : CNPUDG StationGmtOffset : 8 BandMap : CIMO
     * Climo : ZA80 LocalRadar : MediaRegion : null Metar : ZSSS NXMetro :
     * NXState : Population : 5044430 PrimaryWarningCountyCode :
     * PrimaryWarningZoneCode : Satellite : ASIA Synoptic : 58367 MarineStation
     * : MarineStationGMTOffset : null VideoCode : PartnerID : null Sources :
     * [{"DataType"
     * :"Alerts","Source":"CMA Public Meteorological Service Center",
     * "SourceId":5},{"DataType":"CurrentConditions","Source":
     * "CMA Public Meteorological Service Center"
     * ,"SourceId":5},{"DataType":"CurrentConditions"
     * ,"Source":"China Weather","SourceId"
     * :47},{"DataType":"DailyForecast","Source"
     * :"China Weather","SourceId":47},{
     * "DataType":"HourlyForecast","Source":"China Weather"
     * ,"SourceId":47},{"DataType"
     * :"AirQuality","Source":"MEP","SourceId":48},{"DataType"
     * :"PremiumAirQuality","Source":"MEP","SourceId":48}] CanonicalPostalCode :
     * CanonicalLocationKey : 106577
     */

    public DetailsBean Details;
    public List<?> SupplementalAdminAreas;
    public List<String> DataSets;

    public static class RegionBean {
        public String ID;
        public String LocalizedName;
        public String EnglishName;
    }

    public static class CountryBean {
        public String ID;
        public String LocalizedName;
        public String EnglishName;
    }

    public static class AdministrativeAreaBean {
        public String ID;
        public String LocalizedName;
        public String EnglishName;
        public int Level;
        public String LocalizedType;
        public String EnglishType;
        public String CountryID;
    }

    public static class TimeZoneBean {
        public String Code;
        public String Name;
        public int GmtOffset;
        public boolean IsDaylightSaving;
        public Object NextOffsetChange;
    }

    public static class GeoPositionBean {
        public double Latitude;
        public double Longitude;
        /**
         * Metric : {"Value":6,"Unit":"m","UnitType":5} Imperial :
         * {"Value":19,"Unit":"ft","UnitType":0}
         */

        public ElevationBean Elevation;

        public static class ElevationBean {
            /**
             * Value : 6 Unit : m UnitType : 5
             */

            public MetricBean Metric;
            /**
             * Value : 19 Unit : ft UnitType : 0
             */

            public ImperialBean Imperial;

            public static class MetricBean {
                public int Value;
                public String Unit;
                public int UnitType;
            }

            public static class ImperialBean {
                public int Value;
                public String Unit;
                public int UnitType;
            }
        }
    }

    public static class DetailsBean {
        public String Key;
        public String StationCode;
        public int StationGmtOffset;
        public String BandMap;
        public String Climo;
        public String LocalRadar;
        public Object MediaRegion;
        public String Metar;
        public String NXMetro;
        public String NXState;
        public int Population;
        public String PrimaryWarningCountyCode;
        public String PrimaryWarningZoneCode;
        public String Satellite;
        public String Synoptic;
        public String MarineStation;
        public Object MarineStationGMTOffset;
        public String VideoCode;
        public Object PartnerID;
        public String CanonicalPostalCode;
        public String CanonicalLocationKey;
        /**
         * DataType : Alerts Source : CMA Public Meteorological Service Center
         * SourceId : 5
         */

        public List<SourcesBean> Sources;

        public static class SourcesBean {
            public String DataType;
            public String Source;
            public int SourceId;
        }
    }
}
