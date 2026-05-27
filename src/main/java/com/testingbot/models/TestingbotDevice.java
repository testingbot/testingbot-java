package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingbotDevice implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String resolution;
    private String cpu;
    @SerializedName("model_number") private String modelNumber;
    private String name;
    private String model;
    private String manufacturer;
    @SerializedName("platform_name") private String platformName;
    @SerializedName("platform_version") private String platformVersion;
    @SerializedName("screen_size") private String screenSize;
    @SerializedName("screen_resolution") private String screenResolution;
    @SerializedName("free_trial") private boolean freeTrial;
    private boolean available;

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
     * @return the resolution
     */
    public String getResolution() {
        return resolution;
    }

    /**
     * @param resolution the resolution to set
     */
    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    /**
     * @return the cpu
     */
    public String getCpu() {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    /**
     * @return the modelNumber
     */
    public String getModelNumber() {
        return modelNumber;
    }

    /**
     * @param modelNumber the modelNumber to set
     */
    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
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
     * @return the model identifier
     */
    public String getModel() {
        return model;
    }

    /**
     * @param model the model identifier to set
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * @return the manufacturer (e.g. Apple, Samsung)
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * @param manufacturer the manufacturer to set
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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
     * @return the platform (OS) version
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    /**
     * @param platformVersion the platform version to set
     */
    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /**
     * @return the screen size (inches)
     */
    public String getScreenSize() {
        return screenSize;
    }

    /**
     * @param screenSize the screen size to set
     */
    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    /**
     * @return the screen resolution
     */
    public String getScreenResolution() {
        return screenResolution;
    }

    /**
     * @param screenResolution the screen resolution to set
     */
    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    /**
     * @return the freeTrial
     */
    public boolean isFreeTrial() {
        return freeTrial;
    }

    /**
     * @param freeTrial the freeTrial to set
     */
    public void setFreeTrial(boolean freeTrial) {
        this.freeTrial = freeTrial;
    }

    /**
     * @return the available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * @param available the available to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
   
}
