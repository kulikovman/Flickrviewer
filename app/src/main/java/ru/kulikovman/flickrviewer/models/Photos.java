package ru.kulikovman.flickrviewer.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Photos {
    private Integer mPage;
    private Integer mPages;
    private Integer mPerPage;
    private Integer mTotal;
    private List<Photo> mPhoto = null;

    private Map<String, Object> mAdditionalProperties = new HashMap<String, Object>();

    public Integer getPage() {
        return mPage;
    }

    public void setPage(Integer page) {
        this.mPage = page;
    }

    public Integer getPages() {
        return mPages;
    }

    public void setPages(Integer pages) {
        this.mPages = pages;
    }

    public Integer getPerPage() {
        return mPerPage;
    }

    public void setPerPage(Integer perPage) {
        this.mPerPage = perPage;
    }

    public Integer getTotal() {
        return mTotal;
    }

    public void setTotal(Integer total) {
        this.mTotal = total;
    }

    public List<Photo> getPhoto() {
        return mPhoto;
    }

    public void setPhoto(List<Photo> photo) {
        this.mPhoto = photo;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.mAdditionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.mAdditionalProperties.put(name, value);
    }
}
