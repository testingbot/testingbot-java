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
    @SerializedName("platform_name") private String platformName;
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
