package com.testingbot.models;

import java.io.Serializable;

/**
 * Team-level concurrency information: allowed vs. current parallel sessions
 * for VMs and physical devices.
 *
 * @since 1.1.0
 */
public class TestingbotTeam implements Serializable {
    private static final long serialVersionUID = 1L;
    private TestingbotTeamConcurrencyBlock concurrency;

    public TestingbotTeamConcurrencyBlock getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(TestingbotTeamConcurrencyBlock concurrency) {
        this.concurrency = concurrency;
    }
}
