package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingBotStorageFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @SerializedName("app_url") private String appUrl;
    private String url;
    private String type;
    @SerializedName("created_at") private String createdDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
