package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

public class TestingbotTunnel {
    private String ip;
    @SerializedName("private_ip") private String privateIp;
    private String state;
    private int id;
    @SerializedName("requested_at") private String requestedAt;

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the privateIp
     */
    public String getPrivateIp() {
        return privateIp;
    }

    /**
     * @param privateIp the privateIp to set
     */
    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
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
     * @return the requestedAt
     */
    public String getRequestedAt() {
        return requestedAt;
    }

    /**
     * @param requestedAt the requestedAt to set
     */
    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }
}
