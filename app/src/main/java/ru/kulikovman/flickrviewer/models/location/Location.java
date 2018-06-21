package ru.kulikovman.flickrviewer.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Location {
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("accuracy")
    @Expose
    private String accuracy;
    @SerializedName("context")
    @Expose
    private String context;
    @SerializedName("locality")
    @Expose
    private Locality locality;
    @SerializedName("county")
    @Expose
    private County county;
    @SerializedName("region")
    @Expose
    private Region region;
    @SerializedName("country")
    @Expose
    private Country country;
    @SerializedName("place_id")
    @Expose
    private String placeId;
    @SerializedName("woeid")
    @Expose
    private String woeid;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getWoeid() {
        return woeid;
    }

    public void setWoeid(String woeid) {
        this.woeid = woeid;
    }

}