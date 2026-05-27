package com.testingbot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class TestingbotScreenshotCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<TestingbotScreenshot> data = new ArrayList<>();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    public ArrayList<TestingbotScreenshot> getData() {
        return data;
    }

    public void setData(ArrayList<TestingbotScreenshot> data) {
        this.data = data;
    }

    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
