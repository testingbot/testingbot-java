package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * A Codeless test definition (target URL, schedule, alerts, browsers).
 *
 * @since 1.1.0
 */
public class TestingbotLabTest implements Serializable {
    private static final long serialVersionUID = 1L;
    private int id;
    private String name;
    private String url;
    private boolean enabled;
    private String cron;
    @SerializedName("created_at") private String createdAt;
    @SerializedName("updated_at") private String updatedAt;
    @SerializedName("last_run") private String lastRun;
    private List<TestingbotLabAlert> alerts;
    private List<TestingbotBrowser> browsers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
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

    public String getLastRun() {
        return lastRun;
    }

    public void setLastRun(String lastRun) {
        this.lastRun = lastRun;
    }

    public List<TestingbotLabAlert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<TestingbotLabAlert> alerts) {
        this.alerts = alerts;
    }

    public List<TestingbotBrowser> getBrowsers() {
        return browsers;
    }

    public void setBrowsers(List<TestingbotBrowser> browsers) {
        this.browsers = browsers;
    }
}
