package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Status of a job (e.g. a Codeless test run) on TestingBot.
 * See <a href="https://testingbot.com/support/api">https://testingbot.com/support/api</a>.
 *
 * @since 1.1.0
 */
public class TestingbotJob implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;
    private boolean success;
    @SerializedName("test_ids") private List<Integer> testIds;
    private List<String> errors;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Integer> getTestIds() {
        return testIds;
    }

    public void setTestIds(List<Integer> testIds) {
        this.testIds = testIds;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
