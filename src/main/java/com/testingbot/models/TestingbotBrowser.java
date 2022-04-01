package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingbotBrowser implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String platform;
    @SerializedName("browser_id") private int id;
    @SerializedName("long_version") private String longVersion;
    private String version;
    private String deviceName;
    private String platformName;

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
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @param deviceName the deviceName to set
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    /**
     * @return the platformName
     */
    public String getPlatformName() {
        return platformName;
    }

    /**
     * @param platformName the platformName to set
     */
    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    /**
     * @return the longVersion
     */
    public String getLongVersion() {
        return longVersion;
    }

    /**
     * @param longVersion the longVersion to set
     */
    public void setLongVersion(String longVersion) {
        this.longVersion = longVersion;
    }
}
