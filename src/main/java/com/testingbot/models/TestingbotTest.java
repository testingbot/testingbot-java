package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class TestingbotTest implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("created_at") private String createdDate;
    @SerializedName("completed_at") private String completedDate;
    private String extra;
    private String name;
    @SerializedName("session_id") private String sessionId;
    private boolean success;
    @SerializedName("status_message") private String statusMessage;
    private String state;
    private String browser;
    @SerializedName("browser_version") private String browserVersion;
    private String os;
    private int duration;
    private String build;
    private String video;
    private ArrayList<String> thumbs;
    private Map<String, String> logs;
    private ArrayList<String> groups;
    private String type;

    /**
     * @return the createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the completedDate
     */
    public String getCompletedDate() {
        return completedDate;
    }

    /**
     * @param completedDate the completedDate to set
     */
    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * @return the extra
     */
    public String getExtra() {
        return extra;
    }

    /**
     * @param extra the extra to set
     */
    public void setExtra(String extra) {
        this.extra = extra;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the browser
     */
    public String getBrowser() {
        return browser;
    }

    /**
     * @param browser the browser to set
     */
    public void setBrowser(String browser) {
        this.browser = browser;
    }

    /**
     * @return the browserVersion
     */
    public String getBrowserVersion() {
        return browserVersion;
    }

    /**
     * @param browserVersion the browserVersion to set
     */
    public void setBrowserVersion(String browserVersion) {
        this.browserVersion = browserVersion;
    }

    /**
     * @return the os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return the duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * @return the build
     */
    public String getBuild() {
        return build;
    }

    /**
     * @param build the build to set
     */
    public void setBuild(String build) {
        this.build = build;
    }

    /**
     * @return the groups
     */
    public ArrayList<String> getGroups() {
        return groups;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(ArrayList<String> groups) {
        this.groups = groups;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the video
     */
    public String getVideo() {
        return video;
    }

    /**
     * @param video the video to set
     */
    public void setVideo(String video) {
        this.video = video;
    }

    /**
     * @return the thumbs
     */
    public ArrayList<String> getThumbs() {
        return thumbs;
    }

    /**
     * @param thumbs the thumbs to set
     */
    public void setThumbs(ArrayList<String> thumbs) {
        this.thumbs = thumbs;
    }

    /**
     * @return the logs
     */
    public Map<String, String> getLogs() {
        return logs;
    }

    /**
     * @param logs the logs to set
     */
    public void setLogs(Map<String, String> logs) {
        this.logs = logs;
    }

    public String toString() {
        return "TestingBotTest - ID=" + sessionId;
    }
}