package com.android.calendar.weather;

/**
 * Created by wyl on 2017/3/9.
 */

public class CurrentCondition {

    private String LocalObservationDateTime;
    private int EpochTime;
    private String WeatherText;
    private int WeatherIcon;
    private LocalSourceBean LocalSource;
    private boolean IsDayTime;
    private TemperatureBean Temperature;
    private RealFeelTemperatureBean RealFeelTemperature;
    private RealFeelTemperatureShadeBean RealFeelTemperatureShade;
    private int RelativeHumidity;
    private DewPointBean DewPoint;
    private WindBean Wind;
    private WindGustBean WindGust;
    private int UVIndex;
    private String UVIndexText;
    private VisibilityBean Visibility;
    private String ObstructionsToVisibility;
    private int CloudCover;
    private CeilingBean Ceiling;
    private PressureBean Pressure;
    private PressureTendencyBean PressureTendency;
    private Past24HourTemperatureDepartureBean Past24HourTemperatureDeparture;
    private ApparentTemperatureBean ApparentTemperature;
    private WindChillTemperatureBean WindChillTemperature;
    private WetBulbTemperatureBean WetBulbTemperature;
    private Precip1hrBean Precip1hr;
    private PrecipitationSummaryBean PrecipitationSummary;
    private TemperatureSummaryBean TemperatureSummary;
    private String MobileLink;
    private String Link;

    public String getLocalObservationDateTime() {
        return LocalObservationDateTime;
    }

    public void setLocalObservationDateTime(String LocalObservationDateTime) {
        this.LocalObservationDateTime = LocalObservationDateTime;
    }

    public int getEpochTime() {
        return EpochTime;
    }

    public void setEpochTime(int EpochTime) {
        this.EpochTime = EpochTime;
    }

    public String getWeatherText() {
        return WeatherText;
    }

    public void setWeatherText(String WeatherText) {
        this.WeatherText = WeatherText;
    }

    public int getWeatherIcon() {
        return WeatherIcon;
    }

    public void setWeatherIcon(int WeatherIcon) {
        this.WeatherIcon = WeatherIcon;
    }

    public LocalSourceBean getLocalSource() {
        return LocalSource;
    }

    public void setLocalSource(LocalSourceBean LocalSource) {
        this.LocalSource = LocalSource;
    }

    public boolean isIsDayTime() {
        return IsDayTime;
    }

    public void setIsDayTime(boolean IsDayTime) {
        this.IsDayTime = IsDayTime;
    }

    public TemperatureBean getTemperature() {
        return Temperature;
    }

    public void setTemperature(TemperatureBean Temperature) {
        this.Temperature = Temperature;
    }

    public RealFeelTemperatureBean getRealFeelTemperature() {
        return RealFeelTemperature;
    }

    public void setRealFeelTemperature(
            RealFeelTemperatureBean RealFeelTemperature) {
        this.RealFeelTemperature = RealFeelTemperature;
    }

    public RealFeelTemperatureShadeBean getRealFeelTemperatureShade() {
        return RealFeelTemperatureShade;
    }

    public void setRealFeelTemperatureShade(
            RealFeelTemperatureShadeBean RealFeelTemperatureShade) {
        this.RealFeelTemperatureShade = RealFeelTemperatureShade;
    }

    public int getRelativeHumidity() {
        return RelativeHumidity;
    }

    public void setRelativeHumidity(int RelativeHumidity) {
        this.RelativeHumidity = RelativeHumidity;
    }

    public DewPointBean getDewPoint() {
        return DewPoint;
    }

    public void setDewPoint(DewPointBean DewPoint) {
        this.DewPoint = DewPoint;
    }

    public WindBean getWind() {
        return Wind;
    }

    public void setWind(WindBean Wind) {
        this.Wind = Wind;
    }

    public WindGustBean getWindGust() {
        return WindGust;
    }

    public void setWindGust(WindGustBean WindGust) {
        this.WindGust = WindGust;
    }

    public int getUVIndex() {
        return UVIndex;
    }

    public void setUVIndex(int UVIndex) {
        this.UVIndex = UVIndex;
    }

    public String getUVIndexText() {
        return UVIndexText;
    }

    public void setUVIndexText(String UVIndexText) {
        this.UVIndexText = UVIndexText;
    }

    public VisibilityBean getVisibility() {
        return Visibility;
    }

    public void setVisibility(VisibilityBean Visibility) {
        this.Visibility = Visibility;
    }

    public String getObstructionsToVisibility() {
        return ObstructionsToVisibility;
    }

    public void setObstructionsToVisibility(String ObstructionsToVisibility) {
        this.ObstructionsToVisibility = ObstructionsToVisibility;
    }

    public int getCloudCover() {
        return CloudCover;
    }

    public void setCloudCover(int CloudCover) {
        this.CloudCover = CloudCover;
    }

    public CeilingBean getCeiling() {
        return Ceiling;
    }

    public void setCeiling(CeilingBean Ceiling) {
        this.Ceiling = Ceiling;
    }

    public PressureBean getPressure() {
        return Pressure;
    }

    public void setPressure(PressureBean Pressure) {
        this.Pressure = Pressure;
    }

    public PressureTendencyBean getPressureTendency() {
        return PressureTendency;
    }

    public void setPressureTendency(PressureTendencyBean PressureTendency) {
        this.PressureTendency = PressureTendency;
    }

    public Past24HourTemperatureDepartureBean getPast24HourTemperatureDeparture() {
        return Past24HourTemperatureDeparture;
    }

    public void setPast24HourTemperatureDeparture(
            Past24HourTemperatureDepartureBean Past24HourTemperatureDeparture) {
        this.Past24HourTemperatureDeparture = Past24HourTemperatureDeparture;
    }

    public ApparentTemperatureBean getApparentTemperature() {
        return ApparentTemperature;
    }

    public void setApparentTemperature(
            ApparentTemperatureBean ApparentTemperature) {
        this.ApparentTemperature = ApparentTemperature;
    }

    public WindChillTemperatureBean getWindChillTemperature() {
        return WindChillTemperature;
    }

    public void setWindChillTemperature(
            WindChillTemperatureBean WindChillTemperature) {
        this.WindChillTemperature = WindChillTemperature;
    }

    public WetBulbTemperatureBean getWetBulbTemperature() {
        return WetBulbTemperature;
    }

    public void setWetBulbTemperature(WetBulbTemperatureBean WetBulbTemperature) {
        this.WetBulbTemperature = WetBulbTemperature;
    }

    public Precip1hrBean getPrecip1hr() {
        return Precip1hr;
    }

    public void setPrecip1hr(Precip1hrBean Precip1hr) {
        this.Precip1hr = Precip1hr;
    }

    public PrecipitationSummaryBean getPrecipitationSummary() {
        return PrecipitationSummary;
    }

    public void setPrecipitationSummary(
            PrecipitationSummaryBean PrecipitationSummary) {
        this.PrecipitationSummary = PrecipitationSummary;
    }

    public TemperatureSummaryBean getTemperatureSummary() {
        return TemperatureSummary;
    }

    public void setTemperatureSummary(TemperatureSummaryBean TemperatureSummary) {
        this.TemperatureSummary = TemperatureSummary;
    }

    public String getMobileLink() {
        return MobileLink;
    }

    public void setMobileLink(String MobileLink) {
        this.MobileLink = MobileLink;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String Link) {
        this.Link = Link;
    }

    public static class LocalSourceBean {
        /**
         * Id : 7 Name : Huafeng WeatherCode : 00
         */

        private int Id;
        private String Name;
        private String WeatherCode;

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getWeatherCode() {
            return WeatherCode;
        }

        public void setWeatherCode(String WeatherCode) {
            this.WeatherCode = WeatherCode;
        }
    }

    public static class TemperatureBean {
        /**
         * Metric : {"Value":7,"Unit":"C","UnitType":17} Imperial :
         * {"Value":45,"Unit":"F","UnitType":18}
         */

        private MetricBean Metric;
        private ImperialBean Imperial;

        public MetricBean getMetric() {
            return Metric;
        }

        public void setMetric(MetricBean Metric) {
            this.Metric = Metric;
        }

        public ImperialBean getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBean Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBean {
            /**
             * Value : 7.0 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBean {
            /**
             * Value : 45.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class RealFeelTemperatureBean {
        /**
         * Metric : {"Value":3,"Unit":"C","UnitType":17} Imperial :
         * {"Value":37,"Unit":"F","UnitType":18}
         */

        private MetricBeanX Metric;
        private ImperialBeanX Imperial;

        public MetricBeanX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanX {
            /**
             * Value : 3.0 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanX {
            /**
             * Value : 37.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class RealFeelTemperatureShadeBean {
        /**
         * Metric : {"Value":3,"Unit":"C","UnitType":17} Imperial :
         * {"Value":37,"Unit":"F","UnitType":18}
         */

        private MetricBeanXX Metric;
        private ImperialBeanXX Imperial;

        public MetricBeanXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXX {
            /**
             * Value : 3.0 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXX {
            /**
             * Value : 37.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class DewPointBean {
        /**
         * Metric : {"Value":-1.5,"Unit":"C","UnitType":17} Imperial :
         * {"Value":29,"Unit":"F","UnitType":18}
         */

        private MetricBeanXXX Metric;
        private ImperialBeanXXX Imperial;

        public MetricBeanXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXX {
            /**
             * Value : -1.5 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXX {
            /**
             * Value : 29.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class WindBean {

        private DirectionBean Direction;
        private SpeedBean Speed;

        public DirectionBean getDirection() {
            return Direction;
        }

        public void setDirection(DirectionBean Direction) {
            this.Direction = Direction;
        }

        public SpeedBean getSpeed() {
            return Speed;
        }

        public void setSpeed(SpeedBean Speed) {
            this.Speed = Speed;
        }

        public static class DirectionBean {

            private int Degrees;
            private String Localized;
            private String English;

            public int getDegrees() {
                return Degrees;
            }

            public void setDegrees(int Degrees) {
                this.Degrees = Degrees;
            }

            public String getLocalized() {
                return Localized;
            }

            public void setLocalized(String Localized) {
                this.Localized = Localized;
            }

            public String getEnglish() {
                return English;
            }

            public void setEnglish(String English) {
                this.English = English;
            }
        }

        public static class SpeedBean {
            /**
             * Metric : {"Value":17.1,"Unit":"km/h","UnitType":7} Imperial :
             * {"Value":10.6,"Unit":"mi/h","UnitType":9}
             */

            private MetricBeanXXXX Metric;
            private ImperialBeanXXXX Imperial;

            public MetricBeanXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXX {
                /**
                 * Value : 17.1 Unit : km/h UnitType : 7
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXX {
                /**
                 * Value : 10.6 Unit : mi/h UnitType : 9
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }
    }

    public static class WindGustBean {
        /**
         * Speed :
         * {"Metric":{"Value":26.1,"Unit":"km/h","UnitType":7},"Imperial"
         * :{"Value":16.2,"Unit":"mi/h","UnitType":9}}
         */

        private SpeedBeanX Speed;

        public SpeedBeanX getSpeed() {
            return Speed;
        }

        public void setSpeed(SpeedBeanX Speed) {
            this.Speed = Speed;
        }

        public static class SpeedBeanX {
            /**
             * Metric : {"Value":26.1,"Unit":"km/h","UnitType":7} Imperial :
             * {"Value":16.2,"Unit":"mi/h","UnitType":9}
             */

            private MetricBeanXXXXX Metric;
            private ImperialBeanXXXXX Imperial;

            public MetricBeanXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXX {
                /**
                 * Value : 26.1 Unit : km/h UnitType : 7
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXX {
                /**
                 * Value : 16.2 Unit : mi/h UnitType : 9
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }
    }

    public static class VisibilityBean {
        /**
         * Metric : {"Value":16.1,"Unit":"km","UnitType":6} Imperial :
         * {"Value":10,"Unit":"mi","UnitType":2}
         */

        private MetricBeanXXXXXX Metric;
        private ImperialBeanXXXXXX Imperial;

        public MetricBeanXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXX {
            /**
             * Value : 16.1 Unit : km UnitType : 6
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXX {
            /**
             * Value : 10.0 Unit : mi UnitType : 2
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class CeilingBean {
        /**
         * Metric : {"Value":9144,"Unit":"m","UnitType":5} Imperial :
         * {"Value":30000,"Unit":"ft","UnitType":0}
         */

        private MetricBeanXXXXXXX Metric;
        private ImperialBeanXXXXXXX Imperial;

        public MetricBeanXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXX {
            /**
             * Value : 9144.0 Unit : m UnitType : 5
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXX {
            /**
             * Value : 30000.0 Unit : ft UnitType : 0
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class PressureBean {
        /**
         * Metric : {"Value":1026.3,"Unit":"mb","UnitType":14} Imperial :
         * {"Value":30.31,"Unit":"inHg","UnitType":12}
         */

        private MetricBeanXXXXXXXX Metric;
        private ImperialBeanXXXXXXXX Imperial;

        public MetricBeanXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXX {
            /**
             * Value : 1026.3 Unit : mb UnitType : 14
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXX {
            /**
             * Value : 30.31 Unit : inHg UnitType : 12
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class PressureTendencyBean {
        /**
         * LocalizedText : Unknown Code : U
         */

        private String LocalizedText;
        private String Code;

        public String getLocalizedText() {
            return LocalizedText;
        }

        public void setLocalizedText(String LocalizedText) {
            this.LocalizedText = LocalizedText;
        }

        public String getCode() {
            return Code;
        }

        public void setCode(String Code) {
            this.Code = Code;
        }
    }

    public static class Past24HourTemperatureDepartureBean {
        /**
         * Metric : {"Value":-1.1,"Unit":"C","UnitType":17} Imperial :
         * {"Value":-2,"Unit":"F","UnitType":18}
         */

        private MetricBeanXXXXXXXXX Metric;
        private ImperialBeanXXXXXXXXX Imperial;

        public MetricBeanXXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXXX {
            /**
             * Value : -1.1 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXXX {
            /**
             * Value : -2.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class ApparentTemperatureBean {
        /**
         * Metric : {"Value":7.8,"Unit":"C","UnitType":17} Imperial :
         * {"Value":46,"Unit":"F","UnitType":18}
         */

        private MetricBeanXXXXXXXXXX Metric;
        private ImperialBeanXXXXXXXXXX Imperial;

        public MetricBeanXXXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXXXX {
            /**
             * Value : 7.8 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXXXX {
            /**
             * Value : 46.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class WindChillTemperatureBean {
        /**
         * Metric : {"Value":3.9,"Unit":"C","UnitType":17} Imperial :
         * {"Value":39,"Unit":"F","UnitType":18}
         */

        private MetricBeanXXXXXXXXXXX Metric;
        private ImperialBeanXXXXXXXXXXX Imperial;

        public MetricBeanXXXXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXXXXX {
            /**
             * Value : 3.9 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXXXXX {
            /**
             * Value : 39.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class WetBulbTemperatureBean {
        /**
         * Metric : {"Value":3.4,"Unit":"C","UnitType":17} Imperial :
         * {"Value":38,"Unit":"F","UnitType":18}
         */

        private MetricBeanXXXXXXXXXXXX Metric;
        private ImperialBeanXXXXXXXXXXXX Imperial;

        public MetricBeanXXXXXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXXXXXX {
            /**
             * Value : 3.4 Unit : C UnitType : 17
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXXXXXX {
            /**
             * Value : 38.0 Unit : F UnitType : 18
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class Precip1hrBean {
        /**
         * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
         * {"Value":0,"Unit":"in","UnitType":1}
         */

        private MetricBeanXXXXXXXXXXXXX Metric;
        private ImperialBeanXXXXXXXXXXXXX Imperial;

        public MetricBeanXXXXXXXXXXXXX getMetric() {
            return Metric;
        }

        public void setMetric(MetricBeanXXXXXXXXXXXXX Metric) {
            this.Metric = Metric;
        }

        public ImperialBeanXXXXXXXXXXXXX getImperial() {
            return Imperial;
        }

        public void setImperial(ImperialBeanXXXXXXXXXXXXX Imperial) {
            this.Imperial = Imperial;
        }

        public static class MetricBeanXXXXXXXXXXXXX {
            /**
             * Value : 0.0 Unit : mm UnitType : 3
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }

        public static class ImperialBeanXXXXXXXXXXXXX {
            /**
             * Value : 0.0 Unit : in UnitType : 1
             */

            private double Value;
            private String Unit;
            private int UnitType;

            public double getValue() {
                return Value;
            }

            public void setValue(double Value) {
                this.Value = Value;
            }

            public String getUnit() {
                return Unit;
            }

            public void setUnit(String Unit) {
                this.Unit = Unit;
            }

            public int getUnitType() {
                return UnitType;
            }

            public void setUnitType(int UnitType) {
                this.UnitType = UnitType;
            }
        }
    }

    public static class PrecipitationSummaryBean {
        /**
         * Precipitation :
         * {"Metric":{"Value":0,"Unit":"mm","UnitType":3},"Imperial"
         * :{"Value":0,"Unit":"in","UnitType":1}} PastHour :
         * {"Metric":{"Value":0
         * ,"Unit":"mm","UnitType":3},"Imperial":{"Value":0,"Unit"
         * :"in","UnitType":1}} Past3Hours :
         * {"Metric":{"Value":0,"Unit":"mm","UnitType"
         * :3},"Imperial":{"Value":0,"Unit":"in","UnitType":1}} Past6Hours :
         * {"Metric"
         * :{"Value":0,"Unit":"mm","UnitType":3},"Imperial":{"Value":0,"Unit"
         * :"in","UnitType":1}} Past9Hours :
         * {"Metric":{"Value":0,"Unit":"mm","UnitType"
         * :3},"Imperial":{"Value":0,"Unit":"in","UnitType":1}} Past12Hours :
         * {"Metric"
         * :{"Value":0,"Unit":"mm","UnitType":3},"Imperial":{"Value":0,"Unit"
         * :"in","UnitType":1}} Past18Hours :
         * {"Metric":{"Value":0,"Unit":"mm","UnitType"
         * :3},"Imperial":{"Value":0,"Unit":"in","UnitType":1}} Past24Hours :
         * {"Metric"
         * :{"Value":0,"Unit":"mm","UnitType":3},"Imperial":{"Value":0,"Unit"
         * :"in","UnitType":1}}
         */

        private PrecipitationBean Precipitation;
        private PastHourBean PastHour;
        private Past3HoursBean Past3Hours;
        private Past6HoursBean Past6Hours;
        private Past9HoursBean Past9Hours;
        private Past12HoursBean Past12Hours;
        private Past18HoursBean Past18Hours;
        private Past24HoursBean Past24Hours;

        public PrecipitationBean getPrecipitation() {
            return Precipitation;
        }

        public void setPrecipitation(PrecipitationBean Precipitation) {
            this.Precipitation = Precipitation;
        }

        public PastHourBean getPastHour() {
            return PastHour;
        }

        public void setPastHour(PastHourBean PastHour) {
            this.PastHour = PastHour;
        }

        public Past3HoursBean getPast3Hours() {
            return Past3Hours;
        }

        public void setPast3Hours(Past3HoursBean Past3Hours) {
            this.Past3Hours = Past3Hours;
        }

        public Past6HoursBean getPast6Hours() {
            return Past6Hours;
        }

        public void setPast6Hours(Past6HoursBean Past6Hours) {
            this.Past6Hours = Past6Hours;
        }

        public Past9HoursBean getPast9Hours() {
            return Past9Hours;
        }

        public void setPast9Hours(Past9HoursBean Past9Hours) {
            this.Past9Hours = Past9Hours;
        }

        public Past12HoursBean getPast12Hours() {
            return Past12Hours;
        }

        public void setPast12Hours(Past12HoursBean Past12Hours) {
            this.Past12Hours = Past12Hours;
        }

        public Past18HoursBean getPast18Hours() {
            return Past18Hours;
        }

        public void setPast18Hours(Past18HoursBean Past18Hours) {
            this.Past18Hours = Past18Hours;
        }

        public Past24HoursBean getPast24Hours() {
            return Past24Hours;
        }

        public void setPast24Hours(Past24HoursBean Past24Hours) {
            this.Past24Hours = Past24Hours;
        }

        public static class PrecipitationBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class PastHourBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past3HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past6HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past9HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past12HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past18HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }

        public static class Past24HoursBean {
            /**
             * Metric : {"Value":0,"Unit":"mm","UnitType":3} Imperial :
             * {"Value":0,"Unit":"in","UnitType":1}
             */

            private MetricBeanXXXXXXXXXXXXXXXXXXXXX Metric;
            private ImperialBeanXXXXXXXXXXXXXXXXXXXXX Imperial;

            public MetricBeanXXXXXXXXXXXXXXXXXXXXX getMetric() {
                return Metric;
            }

            public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXXX Metric) {
                this.Metric = Metric;
            }

            public ImperialBeanXXXXXXXXXXXXXXXXXXXXX getImperial() {
                return Imperial;
            }

            public void setImperial(ImperialBeanXXXXXXXXXXXXXXXXXXXXX Imperial) {
                this.Imperial = Imperial;
            }

            public static class MetricBeanXXXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : mm UnitType : 3
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }

            public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXX {
                /**
                 * Value : 0.0 Unit : in UnitType : 1
                 */

                private double Value;
                private String Unit;
                private int UnitType;

                public double getValue() {
                    return Value;
                }

                public void setValue(double Value) {
                    this.Value = Value;
                }

                public String getUnit() {
                    return Unit;
                }

                public void setUnit(String Unit) {
                    this.Unit = Unit;
                }

                public int getUnitType() {
                    return UnitType;
                }

                public void setUnitType(int UnitType) {
                    this.UnitType = UnitType;
                }
            }
        }
    }

    public static class TemperatureSummaryBean {
        /**
         * Past6HourRange :
         * {"Minimum":{"Metric":{"Value":7,"Unit":"C","UnitType"
         * :17},"Imperial":{
         * "Value":45,"Unit":"F","UnitType":18}},"Maximum":{"Metric"
         * :{"Value":11,
         * "Unit":"C","UnitType":17},"Imperial":{"Value":52,"Unit":"F"
         * ,"UnitType":18}}} Past12HourRange :
         * {"Minimum":{"Metric":{"Value":5.4,
         * "Unit":"C","UnitType":17},"Imperial"
         * :{"Value":42,"Unit":"F","UnitType"
         * :18}},"Maximum":{"Metric":{"Value":11
         * ,"Unit":"C","UnitType":17},"Imperial"
         * :{"Value":52,"Unit":"F","UnitType":18}}} Past24HourRange :
         * {"Minimum":
         * {"Metric":{"Value":2,"Unit":"C","UnitType":17},"Imperial":{
         * "Value":36,
         * "Unit":"F","UnitType":18}},"Maximum":{"Metric":{"Value":11,
         * "Unit":"C",
         * "UnitType":17},"Imperial":{"Value":52,"Unit":"F","UnitType":18}}}
         */

        private Past6HourRangeBean Past6HourRange;
        private Past12HourRangeBean Past12HourRange;
        private Past24HourRangeBean Past24HourRange;

        public Past6HourRangeBean getPast6HourRange() {
            return Past6HourRange;
        }

        public void setPast6HourRange(Past6HourRangeBean Past6HourRange) {
            this.Past6HourRange = Past6HourRange;
        }

        public Past12HourRangeBean getPast12HourRange() {
            return Past12HourRange;
        }

        public void setPast12HourRange(Past12HourRangeBean Past12HourRange) {
            this.Past12HourRange = Past12HourRange;
        }

        public Past24HourRangeBean getPast24HourRange() {
            return Past24HourRange;
        }

        public void setPast24HourRange(Past24HourRangeBean Past24HourRange) {
            this.Past24HourRange = Past24HourRange;
        }

        public static class Past6HourRangeBean {
            /**
             * Minimum :
             * {"Metric":{"Value":7,"Unit":"C","UnitType":17},"Imperial"
             * :{"Value":45,"Unit":"F","UnitType":18}} Maximum :
             * {"Metric":{"Value"
             * :11,"Unit":"C","UnitType":17},"Imperial":{"Value"
             * :52,"Unit":"F","UnitType":18}}
             */

            private MinimumBean Minimum;
            private MaximumBean Maximum;

            public MinimumBean getMinimum() {
                return Minimum;
            }

            public void setMinimum(MinimumBean Minimum) {
                this.Minimum = Minimum;
            }

            public MaximumBean getMaximum() {
                return Maximum;
            }

            public void setMaximum(MaximumBean Maximum) {
                this.Maximum = Maximum;
            }

            public static class MinimumBean {
                /**
                 * Metric : {"Value":7,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":45,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 7.0 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 45.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }

            public static class MaximumBean {
                /**
                 * Metric : {"Value":11,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":52,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 11.0 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 52.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }
        }

        public static class Past12HourRangeBean {
            /**
             * Minimum :
             * {"Metric":{"Value":5.4,"Unit":"C","UnitType":17},"Imperial"
             * :{"Value":42,"Unit":"F","UnitType":18}} Maximum :
             * {"Metric":{"Value"
             * :11,"Unit":"C","UnitType":17},"Imperial":{"Value"
             * :52,"Unit":"F","UnitType":18}}
             */

            private MinimumBeanX Minimum;
            private MaximumBeanX Maximum;

            public MinimumBeanX getMinimum() {
                return Minimum;
            }

            public void setMinimum(MinimumBeanX Minimum) {
                this.Minimum = Minimum;
            }

            public MaximumBeanX getMaximum() {
                return Maximum;
            }

            public void setMaximum(MaximumBeanX Maximum) {
                this.Maximum = Maximum;
            }

            public static class MinimumBeanX {
                /**
                 * Metric : {"Value":5.4,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":42,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 5.4 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 42.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }

            public static class MaximumBeanX {
                /**
                 * Metric : {"Value":11,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":52,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 11.0 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 52.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }
        }

        public static class Past24HourRangeBean {
            /**
             * Minimum :
             * {"Metric":{"Value":2,"Unit":"C","UnitType":17},"Imperial"
             * :{"Value":36,"Unit":"F","UnitType":18}} Maximum :
             * {"Metric":{"Value"
             * :11,"Unit":"C","UnitType":17},"Imperial":{"Value"
             * :52,"Unit":"F","UnitType":18}}
             */

            private MinimumBeanXX Minimum;
            private MaximumBeanXX Maximum;

            public MinimumBeanXX getMinimum() {
                return Minimum;
            }

            public void setMinimum(MinimumBeanXX Minimum) {
                this.Minimum = Minimum;
            }

            public MaximumBeanXX getMaximum() {
                return Maximum;
            }

            public void setMaximum(MaximumBeanXX Maximum) {
                this.Maximum = Maximum;
            }

            public static class MinimumBeanXX {
                /**
                 * Metric : {"Value":2,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":36,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(
                        MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 2.0 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 36.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }

            public static class MaximumBeanXX {
                /**
                 * Metric : {"Value":11,"Unit":"C","UnitType":17} Imperial :
                 * {"Value":52,"Unit":"F","UnitType":18}
                 */

                private MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX Metric;
                private ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX Imperial;

                public MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX getMetric() {
                    return Metric;
                }

                public void setMetric(
                        MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX Metric) {
                    this.Metric = Metric;
                }

                public ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX getImperial() {
                    return Imperial;
                }

                public void setImperial(
                        ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX Imperial) {
                    this.Imperial = Imperial;
                }

                public static class MetricBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 11.0 Unit : C UnitType : 17
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }

                public static class ImperialBeanXXXXXXXXXXXXXXXXXXXXXXXXXXX {
                    /**
                     * Value : 52.0 Unit : F UnitType : 18
                     */

                    private double Value;
                    private String Unit;
                    private int UnitType;

                    public double getValue() {
                        return Value;
                    }

                    public void setValue(double Value) {
                        this.Value = Value;
                    }

                    public String getUnit() {
                        return Unit;
                    }

                    public void setUnit(String Unit) {
                        this.Unit = Unit;
                    }

                    public int getUnitType() {
                        return UnitType;
                    }

                    public void setUnitType(int UnitType) {
                        this.UnitType = UnitType;
                    }
                }
            }
        }
    }
}
