package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class TestingbotTunnel implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ip;
    @SerializedName("private_ip") private String privateIp;
    private String state;
    private int id;
    @SerializedName("requested_at") private String requestedAt;
    private String launched;
    @SerializedName("tunnel_id") private String tunnelId;
    private String identifier;
    private Map<String, Object> metadata;

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

    /**
     * @return the date-time the tunnel was launched
     */
    public String getLaunched() {
        return launched;
    }

    /**
     * @param launched the launched date-time to set
     */
    public void setLaunched(String launched) {
        this.launched = launched;
    }

    /**
     * @return the public tunnel identifier
     */
    public String getTunnelId() {
        return tunnelId;
    }

    /**
     * @param tunnelId the public tunnel identifier to set
     */
    public void setTunnelId(String tunnelId) {
        this.tunnelId = tunnelId;
    }

    /**
     * @return the custom identifier (from --tunnel-identifier)
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the custom identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the client metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * @param metadata the client metadata to set
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
