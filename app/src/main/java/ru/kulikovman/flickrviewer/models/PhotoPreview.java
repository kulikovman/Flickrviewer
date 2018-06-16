package ru.kulikovman.flickrviewer.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PhotoPreview extends RealmObject {
    public static final String ID = "mId";
    public static final String TITLE = "mTitle";
    public static final String URL = "mUrl";

    //@PrimaryKey
    private String mId;
    private String mTitle;
    private String mUrl;

    public PhotoPreview(String id, String title, String url) {
        mId = id;
        mTitle = title;
        mUrl = url;
    }

    public PhotoPreview(String id) {
        mId = id;
    }

    public PhotoPreview() {
    }

    /*public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }*/

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
