package ru.kulikovman.photoviewer.models.location;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhotoWithLocation {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("location")
    @Expose
    private Location location;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
