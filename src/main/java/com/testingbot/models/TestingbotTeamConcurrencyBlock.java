package com.testingbot.models;

import java.io.Serializable;

/**
 * Allowed vs. current concurrency caps for a team.
 *
 * @since 1.1.0
 */
public class TestingbotTeamConcurrencyBlock implements Serializable {
    private static final long serialVersionUID = 1L;
    private TestingbotTeamConcurrency allowed;
    private TestingbotTeamConcurrency current;

    public TestingbotTeamConcurrency getAllowed() {
        return allowed;
    }

    public void setAllowed(TestingbotTeamConcurrency allowed) {
        this.allowed = allowed;
    }

    public TestingbotTeamConcurrency getCurrent() {
        return current;
    }

    public void setCurrent(TestingbotTeamConcurrency current) {
        this.current = current;
    }
}
