package com.testingbot.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TestingbotTestBuildCollection {

    private ArrayList<TestingbotTest> data = new ArrayList();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    /**
     * @return the data
     */
    public ArrayList<TestingbotTest> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(ArrayList<TestingbotTest> data) {
        this.data = data;
    }

    /**
     * @return the meta
     */
    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    /**
     * @param meta the meta to set
     */
    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
