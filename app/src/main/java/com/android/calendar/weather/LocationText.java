package com.android.calendar.weather;

public class LocationText {

    private int Version;
    private String Key;
    private String Type;
    private int Rank;
    private String LocalizedName;
    private CountryBean Country;
    private AdministrativeAreaBean AdministrativeArea;

    public int getVersion() {
        return Version;
    }

    public void setVersion(int Version) {
        this.Version = Version;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getType() {
        return Type;
    }

    public void setType(String Type) {
        this.Type = Type;
    }

    public int getRank() {
        return Rank;
    }

    public void setRank(int Rank) {
        this.Rank = Rank;
    }

    public String getLocalizedName() {
        return LocalizedName;
    }

    public void setLocalizedName(String LocalizedName) {
        this.LocalizedName = LocalizedName;
    }

    public CountryBean getCountry() {
        return Country;
    }

    public void setCountry(CountryBean Country) {
        this.Country = Country;
    }

    public AdministrativeAreaBean getAdministrativeArea() {
        return AdministrativeArea;
    }

    public void setAdministrativeArea(AdministrativeAreaBean AdministrativeArea) {
        this.AdministrativeArea = AdministrativeArea;
    }

    public static class CountryBean {

        private String ID;
        private String LocalizedName;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getLocalizedName() {
            return LocalizedName;
        }

        public void setLocalizedName(String LocalizedName) {
            this.LocalizedName = LocalizedName;
        }
    }

    public static class AdministrativeAreaBean {

        private String ID;
        private String LocalizedName;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getLocalizedName() {
            return LocalizedName;
        }

        public void setLocalizedName(String LocalizedName) {
            this.LocalizedName = LocalizedName;
        }
    }
}
