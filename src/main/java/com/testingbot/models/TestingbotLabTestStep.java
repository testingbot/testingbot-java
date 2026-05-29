package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * A single step within a Codeless test (command + locator + value).
 *
 * @since 1.1.0
 */
public class TestingbotLabTestStep implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("test_order") private int testOrder;
    private String cmd;
    private String locator;
    private String value;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;

    public int getTestOrder() {
        return testOrder;
    }

    public void setTestOrder(int testOrder) {
        this.testOrder = testOrder;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
