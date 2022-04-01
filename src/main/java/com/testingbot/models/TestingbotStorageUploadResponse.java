package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


public class TestingbotStorageUploadResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("app_url") private String appUrl;

    public String getAppUrl() {
        return appUrl;
    }
    /**
     * @param appUrl the data to set
     */
    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }
}
