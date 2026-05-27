package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TestingbotScreenshot implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String url;
    private String state;
    @SerializedName("wait_time") private int waitTime;
    private String resolution;
    private List<Object> screenshots;
    @SerializedName("created_at") private String createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public List<Object> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<Object> screenshots) {
        this.screenshots = screenshots;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
