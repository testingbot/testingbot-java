package com.testingbot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Paginated list of Codeless tests ({@code data} + {@code meta}).
 *
 * @since 1.1.0
 */
public class TestingbotLabTestCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<TestingbotLabTest> data = new ArrayList<>();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    public ArrayList<TestingbotLabTest> getData() {
        return data;
    }

    public void setData(ArrayList<TestingbotLabTest> data) {
        this.data = data;
    }

    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
