package com.testingbot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Paginated list of Codeless test steps ({@code data} + {@code meta}).
 *
 * @since 1.1.0
 */
public class TestingbotLabTestStepCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<TestingbotLabTestStep> data = new ArrayList<>();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    public ArrayList<TestingbotLabTestStep> getData() {
        return data;
    }

    public void setData(ArrayList<TestingbotLabTestStep> data) {
        this.data = data;
    }

    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
