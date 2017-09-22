package com.testingbot.testingbotrest;

public class TestingbotUnauthorizedException extends RuntimeException {
    public TestingbotUnauthorizedException() {
        super("This request was not authorized. Please supply TestingBot key and secret");
    }
}
