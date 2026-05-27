package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TestingbotBuild implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    @SerializedName("build_id") private String buildId;
    @SerializedName("build_identifier") private String buildIdentifier;
    @SerializedName("created_at") private String createdAtDate;
    @SerializedName("completed_at") private String completedAt;
    private String updatedAtDate;
    private String status;
    @SerializedName("session_id") private String sessionId;
    private int duration;
    private boolean success;
    @SerializedName("total_tests") private int totalTests;
    @SerializedName("failed_tests") private int failedTests;
    @SerializedName("passed_tests") private int passedTests;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the build_id (user-supplied build identifier)
     */
    public String getBuildId() {
        return buildId;
    }

    /**
     * @param buildId the build_id to set
     */
    public void setBuildId(String buildId) {
        this.buildId = buildId;
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

    /**
     * @return the completed_at date
     */
    public String getCompletedAt() {
        return completedAt;
    }

    /**
     * @param completedAt the completed_at date to set
     */
    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    /**
     * @return the aggregate build status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the build status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the session_id of the first test in the build
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the session_id to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * @return the total duration (seconds) of all tests in the build
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
     * @return whether the build succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success the success flag to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the total number of tests in the build
     */
    public int getTotalTests() {
        return totalTests;
    }

    /**
     * @param totalTests the total number of tests to set
     */
    public void setTotalTests(int totalTests) {
        this.totalTests = totalTests;
    }

    /**
     * @return the number of failed tests in the build
     */
    public int getFailedTests() {
        return failedTests;
    }

    /**
     * @param failedTests the number of failed tests to set
     */
    public void setFailedTests(int failedTests) {
        this.failedTests = failedTests;
    }

    /**
     * @return the number of passed tests in the build
     */
    public int getPassedTests() {
        return passedTests;
    }

    /**
     * @param passedTests the number of passed tests to set
     */
    public void setPassedTests(int passedTests) {
        this.passedTests = passedTests;
    }
}
