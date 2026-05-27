package com.testingbot.testingbotrest;

public class TestingbotUnauthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public TestingbotUnauthorizedException() {
        super("This request was not authorized. Please supply TestingBot key and secret");
    }
}
