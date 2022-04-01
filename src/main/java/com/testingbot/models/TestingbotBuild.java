package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingbotBuild implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @SerializedName("build_identifier") private String buildIdentifier;
    private String createdAtDate;
    private String updatedAtDate;

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
     * @return the buildIdentifier
     */
    public String getBuildIdentifier() {
        return buildIdentifier;
    }

    /**
     * @param buildIdentifier the buildIdentifier to set
     */
    public void setBuildIdentifier(String buildIdentifier) {
        this.buildIdentifier = buildIdentifier;
    }

    /**
     * @return the createdAtDate
     */
    public String getCreatedAtDate() {
        return createdAtDate;
    }

    /**
     * @param createdAtDate the createdAtDate to set
     */
    public void setCreatedAtDate(String createdAtDate) {
        this.createdAtDate = createdAtDate;
    }

    /**
     * @return the updatedAtDate
     */
    public String getUpdatedAtDate() {
        return updatedAtDate;
    }

    /**
     * @param updatedAtDate the updatedAtDate to set
     */
    public void setUpdatedAtDate(String updatedAtDate) {
        this.updatedAtDate = updatedAtDate;
    }
}
