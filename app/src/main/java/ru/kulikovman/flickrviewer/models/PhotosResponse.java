package ru.kulikovman.flickrviewer.models;

import java.util.HashMap;
import java.util.Map;

public class PhotosResponse {
    private Photos mPhotos;
    private String mStat;

    private Map<String, Object> mAdditionalProperties = new HashMap<String, Object>();

    public Photos getPhotos() {
        return mPhotos;
    }

    public void setPhotos(Photos photos) {
        this.mPhotos = photos;
    }

    public String getStat() {
        return mStat;
    }

    public void setStat(String stat) {
        this.mStat = stat;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.mAdditionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.mAdditionalProperties.put(name, value);
    }
}
