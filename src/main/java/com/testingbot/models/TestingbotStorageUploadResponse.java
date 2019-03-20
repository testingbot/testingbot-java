package com.testingbot.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;


public class TestingbotStorageUploadResponse {
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
