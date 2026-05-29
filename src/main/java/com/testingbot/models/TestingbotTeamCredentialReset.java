package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Response from resetting a team user's API keys; carries the new
 * {@code client_key}.
 *
 * @since 1.1.0
 */
public class TestingbotTeamCredentialReset implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success;
    @SerializedName("client_key") private String clientKey;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }
}
