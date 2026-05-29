package com.testingbot.models;

import java.io.Serializable;

/**
 * Alert configuration for a Codeless test ({@code kind}, {@code level}, destination).
 *
 * @since 1.1.0
 */
public class TestingbotLabAlert implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;
    private String value;
    private String level;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
