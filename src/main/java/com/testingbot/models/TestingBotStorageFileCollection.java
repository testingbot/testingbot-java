package com.testingbot.models;

import java.util.ArrayList;
import java.util.HashMap;

public class TestingBotStorageFileCollection {
    private ArrayList<TestingBotStorageFile> data = new ArrayList();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    public ArrayList<TestingBotStorageFile> getData() {
        return data;
    }

    public void setData(ArrayList<TestingBotStorageFile> data) {
        this.data = data;
    }

    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
