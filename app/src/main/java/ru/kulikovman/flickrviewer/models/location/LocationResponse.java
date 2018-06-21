package ru.kulikovman.flickrviewer.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationResponse {
    @SerializedName("photoWithLocation")
    @Expose
    private PhotoWithLocation photoWithLocation;
    @SerializedName("stat")
    @Expose
    private String stat;

    public PhotoWithLocation getPhotoWithLocation() {
        return photoWithLocation;
    }

    public void setPhotoWithLocation(PhotoWithLocation photoWithLocation) {
        this.photoWithLocation = photoWithLocation;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

}