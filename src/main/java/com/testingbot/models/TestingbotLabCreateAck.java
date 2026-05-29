package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Response from creating a Codeless test — carries the new {@code lab_test_id}.
 *
 * @since 1.1.0
 */
public class TestingbotLabCreateAck implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    @SerializedName("lab_test_id") private int labTestId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getLabTestId() {
        return labTestId;
    }

    public void setLabTestId(int labTestId) {
        this.labTestId = labTestId;
    }
}
