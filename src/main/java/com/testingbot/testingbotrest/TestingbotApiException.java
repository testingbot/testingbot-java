package com.testingbot.testingbotrest;

public class TestingbotApiException extends RuntimeException {

    TestingbotApiException(String payload) {
        super(payload);
    }
}
