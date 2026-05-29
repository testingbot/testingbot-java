package com.testingbot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Paginated list of team users ({@code data} + {@code meta}).
 *
 * @since 1.1.0
 */
public class TestingbotTeamMemberCollection implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<TestingbotTeamMember> data = new ArrayList<>();
    private HashMap<String, Integer> meta = new HashMap<String, Integer>();

    public ArrayList<TestingbotTeamMember> getData() {
        return data;
    }

    public void setData(ArrayList<TestingbotTeamMember> data) {
        this.data = data;
    }

    public HashMap<String, Integer> getMeta() {
        return meta;
    }

    public void setMeta(HashMap<String, Integer> meta) {
        this.meta = meta;
    }
}
