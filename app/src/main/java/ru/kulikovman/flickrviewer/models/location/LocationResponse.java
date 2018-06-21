package ru.kulikovman.flickrviewer.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationResponse {
    @SerializedName("photo")
    @Expose
    private PhotoWithLocation photo;
    @SerializedName("stat")
    @Expose
    private String stat;

    public PhotoWithLocation getPhoto() {
        return photo;
    }

    public void setPhoto(PhotoWithLocation photo) {
        this.photo = photo;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

}