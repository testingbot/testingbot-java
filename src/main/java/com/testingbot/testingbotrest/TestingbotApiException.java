package com.testingbot.testingbotrest;

public class TestingbotApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode;
    private final String responseBody;

    public TestingbotApiException(String payload) {
        this(payload, 0, payload);
    }

    public TestingbotApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    /**
     * @return the HTTP status code returned by the TestingBot API, or 0 when the
     * failure was not an HTTP response (e.g. a transport or parsing error).
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the raw response body returned by the TestingBot API, if any.
     */
    public String getResponseBody() {
        return responseBody;
    }
}
