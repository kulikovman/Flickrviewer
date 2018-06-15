package ru.kulikovman.flickrviewer.models;

import java.util.HashMap;
import java.util.Map;

public class Photo {
    private String mId;
    private String mOwner;
    private String mSecret;
    private String mServer;
    private Integer mFarm;
    private String mTitle;
    private Integer mIsPublic;
    private Integer mIsFriend;
    private Integer mIsFamily;
    private String mUrlN;
    private Integer mHeightN;
    private String mWidthN;

    private Map<String, Object> mAdditionalProperties = new HashMap<String, Object>();

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        this.mOwner = owner;
    }

    public String getSecret() {
        return mSecret;
    }

    public void setSecret(String secret) {
        this.mSecret = secret;
    }

    public String getServer() {
        return mServer;
    }

    public void setServer(String server) {
        this.mServer = server;
    }

    public Integer getFarm() {
        return mFarm;
    }

    public void setFarm(Integer farm) {
        this.mFarm = farm;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Integer getIsPublic() {
        return mIsPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.mIsPublic = isPublic;
    }

    public Integer getIsFriend() {
        return mIsFriend;
    }

    public void setIsFriend(Integer isFriend) {
        this.mIsFriend = isFriend;
    }

    public Integer getIsFamily() {
        return mIsFamily;
    }

    public void setIsFamily(Integer isFamily) {
        this.mIsFamily = isFamily;
    }

    public String getUrlN() {
        return mUrlN;
    }

    public void setUrlN(String urlN) {
        this.mUrlN = urlN;
    }

    public Integer getHeightN() {
        return mHeightN;
    }

    public void setHeightN(Integer heightN) {
        this.mHeightN = heightN;
    }

    public String getWidthN() {
        return mWidthN;
    }

    public void setWidthN(String widthN) {
        this.mWidthN = widthN;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.mAdditionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.mAdditionalProperties.put(name, value);
    }
}