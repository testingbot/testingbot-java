package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingBotStorageFile implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @SerializedName("app_url") private String appUrl;
    private String url;
    private String filename;
    private String type;
    private String version;
    @SerializedName("min_device_version") private String minDeviceVersion;
    private String thumb;
    private String state;
    @SerializedName("sim_only") private boolean simOnly;
    @SerializedName("created_at") private String createdDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMinDeviceVersion() {
        return minDeviceVersion;
    }

    public void setMinDeviceVersion(String minDeviceVersion) {
        this.minDeviceVersion = minDeviceVersion;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isSimOnly() {
        return simOnly;
    }

    public void setSimOnly(boolean simOnly) {
        this.simOnly = simOnly;
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
