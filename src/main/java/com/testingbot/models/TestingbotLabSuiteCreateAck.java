package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Response from creating a Codeless test suite — carries the new {@code suite_id}.
 *
 * @since 1.1.0
 */
public class TestingbotLabSuiteCreateAck implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    @SerializedName("suite_id") private int suiteId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getSuiteId() {
        return suiteId;
    }

    public void setSuiteId(int suiteId) {
        this.suiteId = suiteId;
    }
}
