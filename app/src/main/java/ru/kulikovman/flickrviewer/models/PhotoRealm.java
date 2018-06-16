package ru.kulikovman.flickrviewer.models;

import io.realm.RealmObject;

public class PhotoRealm extends RealmObject {
    private String mId;
    private String mTitle;
    private String mUrl;

    public PhotoRealm() {
    }

    public PhotoRealm(String id, String title, String url) {
        this.mId = id;
        this.mTitle = title;
        this.mUrl = url;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

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
