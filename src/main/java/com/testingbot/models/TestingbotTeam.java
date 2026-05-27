package com.testingbot.models;

import java.io.Serializable;

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
