/**
 * Entry point for the TestingBot REST API Java client.
 *
 * <p>The only class consumers typically use directly is
 * {@link com.testingbot.testingbotrest.TestingbotREST}. It is thread-safe
 * and implements {@link java.io.Closeable}; create one instance per
 * credential pair and reuse it.
 *
 * <p>Two unchecked exceptions can be thrown by every API call:
 * {@link com.testingbot.testingbotrest.TestingbotApiException} (any 4xx/5xx
 * response — carries the HTTP status code and raw response body) and
 * {@link com.testingbot.testingbotrest.TestingbotUnauthorizedException}
 * (specifically a 401).
 *
 * <p>See <a href="https://testingbot.com/support/api">https://testingbot.com/support/api</a>
 * for the full REST API reference.
 */
package com.testingbot.testingbotrest;
