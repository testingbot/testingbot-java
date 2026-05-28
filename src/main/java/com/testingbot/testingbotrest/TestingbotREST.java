package com.testingbot.testingbotrest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.testingbot.BuildUtils;
import com.testingbot.models.*;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.iharder.Base64;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.util.EntityUtils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Simple Java API that invokes the TestingBot REST API. The full list of the
 * TestingBot API functionality is available from
 * <a href="https://testingbot.com/support/api">https://testingbot.com/support/api</a>.
 *
 * @author TestingBot
 */
public class TestingbotREST implements Closeable {

    /**
     * Default base URL for all TestingBot REST API calls.
     */
    private static final String DEFAULT_BASE_URL = "https://api.testingbot.com/v1";

    /**
     * The key to use when performing HTTP requests to the TestingBot REST API.
     */
    protected String key;
    /**
     * The secret key to use when performing HTTP requests to the TestingBot
     * REST API.
     */
    protected String secret;

    private final String apiBase;
    private final Gson gson;
    private final CloseableHttpClient httpClient;

    /**
     * Constructs a new instance of the TestingBotREST class.
     *
     * @param key The key to use when performing HTTP requests to the TestingBot
     * REST API
     * @param secret The access key to use when performing HTTP requests to the
     * TestingBot REST API
     */
    public TestingbotREST(String key, String secret) {
        this(key, secret, DEFAULT_BASE_URL);
    }

    /**
     * Constructs a new instance with an overridable base URL. Package-private:
     * intended for tests that point the client at a local server.
     *
     * @param key the API key
     * @param secret the API secret
     * @param apiBase the base URL (without trailing slash), e.g. {@code https://api.testingbot.com/v1}
     */
    TestingbotREST(String key, String secret, String apiBase) {
        this.key = key;
        this.secret = secret;
        this.apiBase = apiBase;
        this.gson = new GsonBuilder().create();
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(10000)
                .setSocketTimeout(30000)
                .setConnectionRequestTimeout(10000)
                .build();
        this.httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Releases the underlying HTTP client and its connections. Call this when
     * you are done with the client. Safe to call multiple times.
     */
    @Override
    public void close() {
        try {
            this.httpClient.close();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    /**
     * Updates a Test with sessionID (Selenium sessionID)
     *
     * @param test The meta-data to send to the TestingBot API in a
     * TestingBotTest class. See https://testingbot.com/support/api#updatetest
     * @return Boolean success response The API response
     */
    public boolean updateTest(TestingbotTest test) {
        HashMap<String, Object> details = new HashMap<>();
        details.put("status_message", test.getStatusMessage());
        details.put("success", test.isSuccess());
        details.put("build", test.getBuild());
        details.put("extra", test.getExtra());
        details.put("name", test.getName());
        details.put("groups", test.getGroups());
        return this.updateTest(test.getSessionId(), details);
    }

    /**
     * Updates a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @param details The meta-data to send to the TestingBot API. See
     * https://testingbot.com/support/api#updatetest
     * @return Boolean success response The API response
     */
    public boolean updateTest(String sessionID, Map<String, Object> details) {
        List<NameValuePair> form = new ArrayList<>();
        if (details != null) {
            for (Map.Entry<String, Object> entry : details.entrySet()) {
                if (entry.getKey().equals("groups") && entry.getValue() instanceof List<?>) {
                    for (Object group : (List<?>) entry.getValue()) {
                        form.add(new BasicNameValuePair("groups[]", String.valueOf(group)));
                    }
                } else if (entry.getValue() != null) {
                    form.add(new BasicNameValuePair("test[" + entry.getKey() + "]", entry.getValue().toString()));
                }
            }
        }
        return this.apiBoolean(new HttpPut(apiBase + "/tests/" + enc(sessionID)), form);
    }

    /**
     * Stops a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return Boolean response The API response
     */
    public boolean stopTest(String sessionID) {
        return this.apiBoolean(new HttpPut(apiBase + "/tests/" + enc(sessionID) + "/stop"), null);
    }

    /**
     * Deletes a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID of the test to delete from TestingBot
     * @return Boolean success
     */
    public boolean deleteTest(String sessionID) {
        return this.apiBoolean(new HttpDelete(apiBase + "/tests/" + enc(sessionID)), null);
    }

    /**
     * Gets list of available browsers from TestingBot
     *
     * @return ArrayList containing TestingbotBrowser objects.
     */
    public ArrayList<TestingbotBrowser> getBrowsers() {
        return this.apiGet(apiBase + "/browsers", TypeToken.getParameterized(ArrayList.class, TestingbotBrowser.class).getType());
    }

    /**
     * Get latest tests
     *
     * @param offset where to begin
     * @param count number of tests
     * @return TestingbotTestCollection
     */
    public TestingbotTestCollection getTests(int offset, int count) {
        return this.apiGet(apiBase + "/tests/?offset=" + offset + "&count=" + count, new TypeToken<TestingbotTestCollection>(){}.getType());
    }

    /**
     * Gets information from TestingBot for a test with sessionID
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return TestingbotTest
     */
    public TestingbotTest getTest(String sessionID) {
        return this.apiGet(apiBase + "/tests/" + enc(sessionID), new TypeToken<TestingbotTest>(){}.getType());
    }

    /**
     * Gets information from TestingBot for your user account
     *
     * @return response The API response
     */
    public TestingbotUser getUserInfo() {
        return this.apiGet(apiBase + "/user", new TypeToken<TestingbotUser>(){}.getType());
    }

    /**
     * Updates information for a TestingBot User
     *
     * @param testingbotUser the user you are updating
     * @return boolean success
     */
    public boolean updateUserInfo(TestingbotUser testingbotUser) {
        List<NameValuePair> form = new ArrayList<>(2);
        form.add(new BasicNameValuePair("user[first_name]", testingbotUser.getFirstName()));
        form.add(new BasicNameValuePair("user[last_name]", testingbotUser.getLastName()));
        return this.apiBoolean(new HttpPut(apiBase + "/user/"), form);
    }

    /**
     * Gets assigned tunnels for your account on TestingBot
     *
     * @return response The API response
     */
    public ArrayList<TestingbotTunnel> getTunnels() {
        return this.apiGet(apiBase + "/tunnel/list", TypeToken.getParameterized(ArrayList.class, TestingbotTunnel.class).getType());
    }

    /**
     * Deletes a Tunnel with tunnel ID
     *
     * @param tunnelID The tunnelID of the tunnel to delete from TestingBot
     * @return boolean
     */
    public boolean deleteTunnel(String tunnelID) {
        return this.apiBoolean(new HttpDelete(apiBase + "/tunnel/" + enc(tunnelID)), null);
    }

    /**
     * Gets tests for a specific build from TestingBot
     *
     * @param buildIdentifier the identifier (string) for the build
     * @return response The API response
     */
    public TestingbotTestBuildCollection getTestsForBuild(String buildIdentifier) {
        return this.apiGet(apiBase + "/builds/" + enc(buildIdentifier), new TypeToken<TestingbotTestBuildCollection>(){}.getType());
    }

    /**
     * Get test builds
     *
     * @param offset where to begin
     * @param count number of builds
     * @return TestingbotBuildCollection
     */
    public TestingbotBuildCollection getBuilds(int offset, int count) {
        return this.apiGet(apiBase + "/builds/?offset=" + offset + "&count=" + count, new TypeToken<TestingbotBuildCollection>(){}.getType());
    }

    /**
     * Delete a specific build
     *
     * @param buildId the build identifier you want to delete
     * @return boolean
     */
    public boolean deleteBuild(String buildId) {
        return this.apiBoolean(new HttpDelete(apiBase + "/builds/" + enc(buildId)), null);
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param file The path to the local file you want to upload
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(File file) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());

        HttpPost post = new HttpPost(apiBase + "/storage");
        post.setEntity(builder.build());
        try {
            return gson.fromJson(doExecute(post), TestingbotStorageUploadResponse.class);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } catch (JsonSyntaxException ex) {
            throw new TestingbotApiException("Could not parse TestingBot API response: " + ex.getMessage());
        }
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param url to the file (apk/ipa)
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(String url) {
        HttpPost post = new HttpPost(apiBase + "/storage");
        List<NameValuePair> form = new ArrayList<>(1);
        form.add(new BasicNameValuePair("url", url));
        post.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        try {
            return gson.fromJson(doExecute(post), TestingbotStorageUploadResponse.class);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } catch (JsonSyntaxException ex) {
            throw new TestingbotApiException("Could not parse TestingBot API response: " + ex.getMessage());
        }
    }

    /**
     * Retrieves meta-data from a TestingBot Storage file
     *
     * @param appUrl of the file
     * @return TestingBotStorageFile file
     */
    public TestingBotStorageFile getStorageFile(String appUrl) {
        return this.apiGet(apiBase + "/storage/" + enc(appUrl.replace("tb://", "")), new TypeToken<TestingBotStorageFile>(){}.getType());
    }

    /**
     * Retrieves meta-data for TestingBot Storage files
     *
     * @param offset where to begin
     * @param count number of files
     * @return TestingBotStorageFileCollection files
     */
    public TestingBotStorageFileCollection getStorageFiles(int offset, int count) {
        return this.apiGet(apiBase + "/storage/?offset=" + offset + "&count=" + count, new TypeToken<TestingBotStorageFileCollection>(){}.getType());
    }

    /**
     * Retrieves available Real Mobile Devices on TestingBot
     *
     * @param offset where to begin
     * @param count number of devices
     * @return List containing TestingbotDevice objects.
     */
    public List<TestingbotDevice> getAvailableDevices(int offset, int count) {
        return this.apiGet(apiBase + "/devices/available/?offset=" + offset + "&count=" + count, TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }

    /**
     * Retrieves Real Mobile Devices on TestingBot
     * This includes devices not currently available
     *
     * @param offset where to begin
     * @param count number of real devices
     * @return List containing TestingbotDevice objects.
     */
    public List<TestingbotDevice> getDevices(int offset, int count) {
        return this.apiGet(apiBase + "/devices/?offset=" + offset + "&count=" + count, TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }

    /**
     * Retrieves Real Mobile Devices on TestingBot
     *
     * @param deviceId - id of the Device
     * @return TestingbotDevice device
     */
    public TestingbotDevice getDevice(int deviceId) {
        return this.apiGet(apiBase + "/devices/" + deviceId, new TypeToken<TestingbotDevice>(){}.getType());
    }

    /**
     * Delete a file previously uploaded TestingBot Storage
     *
     * @param appUrl of the file
     * @return boolean success
     */
    public boolean deleteStorageFile(String appUrl) {
        return this.apiBoolean(new HttpDelete(apiBase + "/storage/" + enc(appUrl)), null);
    }


    /**
     * Get latest tests, with optional server-side filters
     * (e.g. {@code since}, {@code browser_id}, {@code group}, {@code build}, {@code skip_fields}).
     *
     * @param offset where to begin
     * @param count number of tests
     * @param filters additional query filters (may be null or empty)
     * @return TestingbotTestCollection
     */
    public TestingbotTestCollection getTests(int offset, int count, Map<String, String> filters) {
        StringBuilder url = new StringBuilder(apiBase + "/tests/?offset=" + offset + "&count=" + count);
        if (filters != null) {
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                if (entry.getValue() != null) {
                    url.append('&').append(enc(entry.getKey())).append('=').append(enc(entry.getValue()));
                }
            }
        }
        return this.apiGet(url.toString(), new TypeToken<TestingbotTestCollection>(){}.getType());
    }

    /**
     * Lazily iterates over all tests, paging through the API behind the scenes.
     * Iteration stops when the API returns a page smaller than {@code pageSize}.
     *
     * <p>Each call to {@link Iterable#iterator()} starts a fresh walk from offset 0.
     *
     * @param pageSize the number of tests to fetch per request (must be {@code > 0})
     * @return an {@link Iterable} that walks every test in the account
     */
    public Iterable<TestingbotTest> iterateTests(int pageSize) {
        return iterateTests(pageSize, null);
    }

    /**
     * Lazily iterates over all tests matching the given filters, paging behind the scenes.
     *
     * @param pageSize the number of tests to fetch per request (must be {@code > 0})
     * @param filters optional server-side filters (may be null)
     * @return an {@link Iterable} that walks every matching test
     */
    public Iterable<TestingbotTest> iterateTests(final int pageSize, final Map<String, String> filters) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be > 0");
        }
        return new Iterable<TestingbotTest>() {
            @Override
            public Iterator<TestingbotTest> iterator() {
                return new Iterator<TestingbotTest>() {
                    private int offset = 0;
                    private Iterator<TestingbotTest> page = Collections.<TestingbotTest>emptyList().iterator();
                    private boolean exhausted = false;

                    private void advanceIfNeeded() {
                        if (exhausted || page.hasNext()) {
                            return;
                        }
                        TestingbotTestCollection collection = filters == null
                                ? getTests(offset, pageSize)
                                : getTests(offset, pageSize, filters);
                        List<TestingbotTest> items = collection != null && collection.getData() != null
                                ? collection.getData()
                                : Collections.<TestingbotTest>emptyList();
                        offset += items.size();
                        page = items.iterator();
                        if (items.size() < pageSize) {
                            exhausted = true;
                        }
                    }

                    @Override
                    public boolean hasNext() {
                        advanceIfNeeded();
                        return page.hasNext();
                    }

                    @Override
                    public TestingbotTest next() {
                        advanceIfNeeded();
                        if (!page.hasNext()) {
                            throw new NoSuchElementException();
                        }
                        return page.next();
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Creates a new test on TestingBot.
     * See https://testingbot.com/support/api
     *
     * @param testFields the test fields to set (sent as {@code test[...]} parameters)
     * @return boolean success
     */
    public boolean createTest(Map<String, Object> testFields) {
        List<NameValuePair> form = new ArrayList<>();
        if (testFields != null) {
            for (Map.Entry<String, Object> entry : testFields.entrySet()) {
                if (entry.getValue() != null) {
                    form.add(new BasicNameValuePair("test[" + entry.getKey() + "]", entry.getValue().toString()));
                }
            }
        }
        return this.apiPostBoolean(apiBase + "/tests", form);
    }

    /**
     * Retrieves Real Mobile Devices on TestingBot, filtered by platform.
     *
     * @param platform one of Android, iOS, REAL_ANDROID, REAL_IOS (may be null)
     * @param web when non-null, filters on web-testing capable devices
     * @return List containing TestingbotDevice objects.
     */
    public List<TestingbotDevice> getDevices(String platform, Boolean web) {
        String url = apiBase + "/devices" + query("platform", platform, "web", web == null ? null : String.valueOf(web));
        return this.apiGet(url, TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }

    /**
     * Retrieves the currently available Real Mobile Devices on TestingBot.
     *
     * @return List containing TestingbotDevice objects.
     */
    public List<TestingbotDevice> getAvailableDevices() {
        return this.apiGet(apiBase + "/devices/available", TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }

    /**
     * Gets the currently active tunnel for your account.
     *
     * @return TestingbotTunnel
     */
    public TestingbotTunnel getTunnel() {
        return this.apiGet(apiBase + "/tunnel", new TypeToken<TestingbotTunnel>(){}.getType());
    }

    /**
     * Gets a specific tunnel by its id.
     *
     * @param tunnelId the id of the tunnel
     * @return TestingbotTunnel
     */
    public TestingbotTunnel getTunnel(int tunnelId) {
        return this.apiGet(apiBase + "/tunnel/" + tunnelId, new TypeToken<TestingbotTunnel>(){}.getType());
    }

    /**
     * Creates (boots) a new tunnel.
     *
     * @return TestingbotTunnel
     */
    public TestingbotTunnel createTunnel() {
        return this.apiPost(apiBase + "/tunnel/create", null, new TypeToken<TestingbotTunnel>(){}.getType());
    }

    /**
     * Stops the currently active tunnel for your account.
     *
     * @return boolean success
     */
    public boolean deleteTunnel() {
        return this.apiStatusBoolean(new HttpDelete(apiBase + "/tunnel"), null);
    }

    /**
     * Checks whether the tunnel is alive.
     *
     * @return boolean alive
     */
    public boolean isTunnelAlive() {
        return this.apiBoolean(new HttpGet(apiBase + "/tunnel/isalive-check"), null);
    }

    /**
     * Gets the status of a job (e.g. a Codeless test run).
     *
     * @param jobId the id of the job
     * @return TestingbotJob
     */
    public TestingbotJob getJob(String jobId) {
        return this.apiGet(apiBase + "/jobs/" + enc(jobId), new TypeToken<TestingbotJob>(){}.getType());
    }

    /**
     * Retrieves your API key and secret.
     *
     * @return the raw JSON response
     */
    public JsonElement getUserKeys() {
        return this.apiGet(apiBase + "/user/keys", new TypeToken<JsonElement>(){}.getType());
    }

    /**
     * Retrieves the TestingBot IP ranges for firewall whitelisting.
     *
     * @return the raw JSON response
     */
    public JsonElement getIpRanges() {
        return this.apiGet(apiBase + "/configuration/ip-ranges", new TypeToken<JsonElement>(){}.getType());
    }

    // ----------------------------------------------------------------- Team management

    /**
     * Gets your team's concurrency information.
     *
     * @return TestingbotTeam
     */
    public TestingbotTeam getTeam() {
        return this.apiGet(apiBase + "/team-management", new TypeToken<TestingbotTeam>(){}.getType());
    }

    /**
     * Lists the users in your team.
     *
     * @return TestingbotTeamMemberCollection
     */
    public TestingbotTeamMemberCollection getTeamMembers() {
        return this.apiGet(apiBase + "/team-management/users", new TypeToken<TestingbotTeamMemberCollection>(){}.getType());
    }

    /**
     * Creates a new user in your team. Required fields: {@code email}, {@code password}.
     *
     * @param params the new user's attributes
     * @return TestingbotTeamMember
     */
    public TestingbotTeamMember createTeamMember(Map<String, Object> params) {
        return this.apiPost(apiBase + "/team-management/users", formFromMap(null, params), new TypeToken<TestingbotTeamMember>(){}.getType());
    }

    /**
     * Gets a specific team user.
     *
     * @param userId the id of the team user
     * @return TestingbotTeamMember
     */
    public TestingbotTeamMember getTeamMember(int userId) {
        return this.apiGet(apiBase + "/team-management/users/" + userId, new TypeToken<TestingbotTeamMember>(){}.getType());
    }

    /**
     * Updates a team user.
     *
     * @param userId the id of the team user
     * @param params the attributes to update
     * @return TestingbotTeamMember
     */
    public TestingbotTeamMember updateTeamMember(int userId, Map<String, Object> params) {
        return this.apiPut(apiBase + "/team-management/users/" + userId, formFromMap(null, params), new TypeToken<TestingbotTeamMember>(){}.getType());
    }

    /**
     * Gets the client key of a team user.
     *
     * @param userId the id of the team user
     * @return the raw JSON response
     */
    public JsonElement getTeamMemberClientKey(int userId) {
        return this.apiGet(apiBase + "/team-management/users/" + userId + "/client-key", new TypeToken<JsonElement>(){}.getType());
    }

    /**
     * Resets the API keys of a team user.
     *
     * @param userId the id of the team user
     * @return TestingbotTeamCredentialReset
     */
    public TestingbotTeamCredentialReset resetTeamMemberKeys(int userId) {
        return this.apiPost(apiBase + "/team-management/users/" + userId + "/reset-keys", null, new TypeToken<TestingbotTeamCredentialReset>(){}.getType());
    }

    // ----------------------------------------------------------------- Screenshots

    /**
     * Lists your screenshot batches.
     *
     * @return TestingbotScreenshotCollection
     */
    public TestingbotScreenshotCollection getScreenshots() {
        return this.apiGet(apiBase + "/screenshots", new TypeToken<TestingbotScreenshotCollection>(){}.getType());
    }

    /**
     * Captures a new screenshot batch. Required fields: {@code url},
     * {@code resolution}, {@code browsers} (a List).
     *
     * @param params the screenshot batch parameters
     * @return TestingbotScreenshot
     */
    public TestingbotScreenshot createScreenshots(Map<String, Object> params) {
        return this.apiPost(apiBase + "/screenshots", formFromMap(null, params), new TypeToken<TestingbotScreenshot>(){}.getType());
    }

    /**
     * Gets a specific screenshot batch.
     *
     * @param screenshotId the id of the screenshot batch
     * @return TestingbotScreenshot
     */
    public TestingbotScreenshot getScreenshot(int screenshotId) {
        return this.apiGet(apiBase + "/screenshots/" + screenshotId, new TypeToken<TestingbotScreenshot>(){}.getType());
    }

    // ----------------------------------------------------------------- Manual sessions

    /**
     * Updates a manual session.
     *
     * @param sessionId the numeric manual-session id
     * @param fields the fields to update (name, success, status_message)
     * @return boolean success
     */
    public boolean updateManualSession(int sessionId, Map<String, Object> fields) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("id", String.valueOf(sessionId)));
        form.addAll(formFromMap("manual_session", fields));
        return this.apiBoolean(new HttpPut(apiBase + "/manual_session"), form);
    }

    /**
     * Sends a keepalive ping for the given manual sessions.
     *
     * @param sessionIds the numeric ids of the manual sessions to ping
     * @return boolean success
     */
    public boolean pingManualSession(List<Integer> sessionIds) {
        List<NameValuePair> form = new ArrayList<>();
        if (sessionIds != null) {
            for (Integer id : sessionIds) {
                form.add(new BasicNameValuePair("ids[]", String.valueOf(id)));
            }
        }
        return this.apiPostBoolean(apiBase + "/manual_session/ping", form);
    }

    // ----------------------------------------------------------------- Codeless Lab tests

    /**
     * Lists your Codeless tests.
     *
     * @return TestingbotLabTestCollection
     */
    public TestingbotLabTestCollection getLabTests() {
        return this.apiGet(apiBase + "/lab", new TypeToken<TestingbotLabTestCollection>(){}.getType());
    }

    /**
     * Creates a new Codeless test.
     *
     * @param testFields the test attributes (sent as {@code test[...]})
     * @return TestingbotLabCreateAck
     */
    public TestingbotLabCreateAck createLabTest(Map<String, Object> testFields) {
        return this.apiPost(apiBase + "/lab", formFromMap("test", testFields), new TypeToken<TestingbotLabCreateAck>(){}.getType());
    }

    /**
     * Gets a specific Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return TestingbotLabTest
     */
    public TestingbotLabTest getLabTest(int labTestId) {
        return this.apiGet(apiBase + "/lab/" + labTestId, new TypeToken<TestingbotLabTest>(){}.getType());
    }

    /**
     * Updates a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param testFields the attributes to update (sent as {@code test[...]})
     * @return boolean success
     */
    public boolean updateLabTest(int labTestId, Map<String, Object> testFields) {
        return this.apiBoolean(new HttpPut(apiBase + "/lab/" + labTestId), formFromMap("test", testFields));
    }

    /**
     * Deletes a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return boolean success
     */
    public boolean deleteLabTest(int labTestId) {
        return this.apiStatusBoolean(new HttpDelete(apiBase + "/lab/" + labTestId), null);
    }

    /**
     * Gets the steps of a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return TestingbotLabTestStepCollection
     */
    public TestingbotLabTestStepCollection getLabTestSteps(int labTestId) {
        return this.apiGet(apiBase + "/lab/" + labTestId + "/steps", new TypeToken<TestingbotLabTestStepCollection>(){}.getType());
    }

    /**
     * Replaces the steps of a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param steps the ordered list of steps
     * @return boolean success
     */
    public boolean setLabTestSteps(int labTestId, List<String> steps) {
        List<NameValuePair> form = new ArrayList<>();
        if (steps != null) {
            for (String step : steps) {
                form.add(new BasicNameValuePair("steps[]", step));
            }
        }
        return this.apiPostBoolean(apiBase + "/lab/" + labTestId + "/steps", form);
    }

    /**
     * Gets the browsers configured for a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return List containing TestingbotBrowser objects.
     */
    public List<TestingbotBrowser> getLabTestBrowsers(int labTestId) {
        return this.apiGet(apiBase + "/lab/" + labTestId + "/browsers", TypeToken.getParameterized(List.class, TestingbotBrowser.class).getType());
    }

    /**
     * Updates the browsers a Codeless test runs on.
     *
     * @param labTestId the id of the Codeless test
     * @param browserIds comma-separated list of browser_ids
     * @return boolean success
     */
    public boolean setLabTestBrowsers(int labTestId, String browserIds) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("browser_ids", browserIds));
        return this.apiPostBoolean(apiBase + "/lab/" + labTestId + "/browsers", form);
    }

    /**
     * Runs a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return TestingbotLabRunAck
     */
    public TestingbotLabRunAck triggerLabTest(int labTestId) {
        return this.apiPost(apiBase + "/lab/" + labTestId + "/trigger", null, new TypeToken<TestingbotLabRunAck>(){}.getType());
    }

    /**
     * Stops a running Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @return boolean success
     */
    public boolean stopLabTest(int labTestId) {
        return this.apiBoolean(new HttpPut(apiBase + "/lab/" + labTestId + "/stop"), null);
    }

    /**
     * Adds an alert to a Codeless test. Required fields: {@code kind},
     * {@code level}, {@code content}.
     *
     * @param labTestId the id of the Codeless test
     * @param params the alert parameters
     * @return boolean success
     */
    public boolean addLabTestAlert(int labTestId, Map<String, Object> params) {
        return this.apiPostBoolean(apiBase + "/lab/" + labTestId + "/alert", formFromMap(null, params));
    }

    /**
     * Updates the alert of a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param params the alert parameters
     * @return boolean success
     */
    public boolean updateLabTestAlert(int labTestId, Map<String, Object> params) {
        return this.apiBoolean(new HttpPut(apiBase + "/lab/" + labTestId + "/alert"), formFromMap(null, params));
    }

    /**
     * Creates a report for a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param params the report parameters
     * @return boolean success
     */
    public boolean createLabTestReport(int labTestId, Map<String, Object> params) {
        return this.apiPostBoolean(apiBase + "/lab/" + labTestId + "/report", formFromMap(null, params));
    }

    /**
     * Updates a report for a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param params the report parameters
     * @return boolean success
     */
    public boolean updateLabTestReport(int labTestId, Map<String, Object> params) {
        return this.apiBoolean(new HttpPut(apiBase + "/lab/" + labTestId + "/report"), formFromMap(null, params));
    }

    /**
     * Sets or updates the schedule of a Codeless test.
     *
     * @param labTestId the id of the Codeless test
     * @param params the schedule parameters (type, day, hour, cronFormat)
     * @return boolean success
     */
    public boolean scheduleLabTest(int labTestId, Map<String, Object> params) {
        return this.apiPostBoolean(apiBase + "/lab/" + labTestId + "/schedule", formFromMap(null, params));
    }

    /**
     * Runs all Codeless tests.
     *
     * @return TestingbotLabRunAck
     */
    public TestingbotLabRunAck triggerAllLabTests() {
        return this.apiPost(apiBase + "/lab/trigger_all", null, new TypeToken<TestingbotLabRunAck>(){}.getType());
    }

    // ----------------------------------------------------------------- Codeless Lab suites

    /**
     * Lists your Codeless test suites.
     *
     * @return TestingbotLabSuiteCollection
     */
    public TestingbotLabSuiteCollection getLabSuites() {
        return this.apiGet(apiBase + "/labsuites", new TypeToken<TestingbotLabSuiteCollection>(){}.getType());
    }

    /**
     * Creates a new Codeless test suite. Required field: {@code name}.
     *
     * @param suiteFields the suite attributes (sent as {@code suite[...]})
     * @return TestingbotLabSuiteCreateAck
     */
    public TestingbotLabSuiteCreateAck createLabSuite(Map<String, Object> suiteFields) {
        return this.apiPost(apiBase + "/labsuites", formFromMap("suite", suiteFields), new TypeToken<TestingbotLabSuiteCreateAck>(){}.getType());
    }

    /**
     * Gets a specific Codeless test suite.
     *
     * @param suiteId the id of the suite
     * @return TestingbotLabSuite
     */
    public TestingbotLabSuite getLabSuite(int suiteId) {
        return this.apiGet(apiBase + "/labsuites/" + suiteId, new TypeToken<TestingbotLabSuite>(){}.getType());
    }

    /**
     * Deletes a Codeless test suite.
     *
     * @param suiteId the id of the suite
     * @return boolean success
     */
    public boolean deleteLabSuite(int suiteId) {
        return this.apiStatusBoolean(new HttpDelete(apiBase + "/labsuites/" + suiteId), null);
    }

    /**
     * Gets the tests in a Codeless suite.
     *
     * @param suiteId the id of the suite
     * @return TestingbotLabTestCollection
     */
    public TestingbotLabTestCollection getLabSuiteTests(int suiteId) {
        return this.apiGet(apiBase + "/labsuites/" + suiteId + "/tests", new TypeToken<TestingbotLabTestCollection>(){}.getType());
    }

    /**
     * Adds tests to a Codeless suite.
     *
     * @param suiteId the id of the suite
     * @param testIds comma-separated list of Codeless test ids
     * @return boolean success
     */
    public boolean addLabSuiteTests(int suiteId, String testIds) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("test_ids", testIds));
        return this.apiPostBoolean(apiBase + "/labsuites/" + suiteId + "/tests", form);
    }

    /**
     * Removes a test from a Codeless suite.
     *
     * @param suiteId the id of the suite
     * @param testId the id of the test to remove
     * @return boolean success
     */
    public boolean removeLabSuiteTest(int suiteId, int testId) {
        return this.apiStatusBoolean(new HttpDelete(apiBase + "/labsuites/" + suiteId + "/tests/" + testId), null);
    }

    /**
     * Gets the browsers configured for a Codeless suite.
     *
     * @param suiteId the id of the suite
     * @return List containing TestingbotBrowser objects.
     */
    public List<TestingbotBrowser> getLabSuiteBrowsers(int suiteId) {
        return this.apiGet(apiBase + "/labsuites/" + suiteId + "/browsers", TypeToken.getParameterized(List.class, TestingbotBrowser.class).getType());
    }

    /**
     * Updates the browsers a Codeless suite runs on.
     *
     * @param suiteId the id of the suite
     * @param browserIds comma-separated list of browser_ids
     * @return boolean success
     */
    public boolean setLabSuiteBrowsers(int suiteId, String browserIds) {
        List<NameValuePair> form = new ArrayList<>();
        form.add(new BasicNameValuePair("browser_ids", browserIds));
        return this.apiPostBoolean(apiBase + "/labsuites/" + suiteId + "/browsers", form);
    }

    /**
     * Runs a Codeless suite.
     *
     * @param suiteId the id of the suite
     * @return TestingbotLabRunAck
     */
    public TestingbotLabRunAck triggerLabSuite(int suiteId) {
        return this.apiPost(apiBase + "/labsuites/" + suiteId + "/trigger", null, new TypeToken<TestingbotLabRunAck>(){}.getType());
    }

    /**
     * Calculates the authentication hash for a specific identifier (sessionId/build-identifier)
     * https://testingbot.com/support/other/sharing
     *
     * @param identifier the sessionId or buildIdentifier
     * @return String hash
     */
    public String getAuthenticationHash(String identifier) {
        return md5Hex(this.key + ":" + this.secret + ":" + identifier);
    }

    /**
     * Calculates the authentication hash for the current user
     * https://testingbot.com/support/other/sharing
     *
     * @return String hash
     */
    public String getAuthenticationHash() {
        return md5Hex(this.key + ":" + this.secret);
    }

    private static String md5Hex(String s) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            m.update(bytes, 0, bytes.length);
            return String.format("%032x", new BigInteger(1, m.digest()));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private String getUserAgent() {
        return "TestingBotRest/" + BuildUtils.getCurrentVersion();
    }

    private String authHeader() {
        String userpass = this.key + ":" + this.secret;
        return "Basic " + Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * URL-encodes a single path/query segment. TestingBot identifiers are
     * normally alphanumeric, but this guards against accidental breakage.
     */
    private static String enc(String value) {
        if (value == null) {
            return "";
        }
        try {
            return URLEncoder.encode(value, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException ex) {
            return value;
        }
    }

    /**
     * Executes a request (adding auth and User-Agent headers), reads the body
     * and validates the HTTP status. Throws {@link TestingbotUnauthorizedException}
     * on a 401 and {@link TestingbotApiException} on any other 4xx/5xx response.
     *
     * @return the response body as a String
     * @throws IOException on a transport-level failure
     */
    private String doExecute(HttpUriRequest request) throws IOException {
        request.setHeader("Authorization", authHeader());
        request.setHeader("User-Agent", getUserAgent());

        HttpResponse response = httpClient.execute(request);
        HttpEntity entity = response.getEntity();
        String payload = entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
        int status = response.getStatusLine().getStatusCode();
        if (status >= 400) {
            if (status == 401) {
                throw new TestingbotUnauthorizedException();
            }
            throw new TestingbotApiException(payload, status, payload);
        }
        return payload;
    }

    private <T> T apiGet(String url, Type returnType) {
        return apiRead(new HttpGet(url), returnType);
    }

    private <T> T apiRead(HttpUriRequest request, Type returnType) {
        try {
            return gson.fromJson(doExecute(request), returnType);
        } catch (IOException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (JsonSyntaxException ex) {
            throw new TestingbotApiException("Could not parse TestingBot API response: " + ex.getMessage());
        }
    }

    /**
     * Executes a request expected to return a {@code {"success": ...}} body.
     * HTTP-status errors (401/4xx/5xx) propagate as runtime exceptions; a
     * transport or parse failure is logged and reported as {@code false}.
     */
    private boolean apiBoolean(HttpUriRequest request, List<NameValuePair> form) {
        if (form != null && request instanceof org.apache.http.client.methods.HttpEntityEnclosingRequestBase) {
            ((org.apache.http.client.methods.HttpEntityEnclosingRequestBase) request)
                    .setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        }
        try {
            JSONObject json = new JSONObject(doExecute(request));
            return json.getBoolean("success");
        } catch (IOException | JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    private <T> T apiPost(String url, List<NameValuePair> form, Type returnType) {
        HttpPost post = new HttpPost(url);
        if (form != null) {
            post.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        }
        return apiRead(post, returnType);
    }

    private <T> T apiPut(String url, List<NameValuePair> form, Type returnType) {
        HttpPut put = new HttpPut(url);
        if (form != null) {
            put.setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        }
        return apiRead(put, returnType);
    }

    private boolean apiPostBoolean(String url, List<NameValuePair> form) {
        return apiBoolean(new HttpPost(url), form);
    }

    /**
     * Executes a request whose success is indicated purely by the HTTP status
     * code (used for endpoints that return no {@code success} body). HTTP-status
     * errors (401/4xx/5xx) propagate as runtime exceptions; a transport failure
     * is logged and reported as {@code false}.
     */
    private boolean apiStatusBoolean(HttpUriRequest request, List<NameValuePair> form) {
        if (form != null && request instanceof org.apache.http.client.methods.HttpEntityEnclosingRequestBase) {
            ((org.apache.http.client.methods.HttpEntityEnclosingRequestBase) request)
                    .setEntity(new UrlEncodedFormEntity(form, StandardCharsets.UTF_8));
        }
        try {
            doExecute(request);
            return true;
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Builds a query string from the given name/value pairs, skipping null
     * values. Returns an empty string when nothing needs to be appended.
     */
    private static String query(String... kv) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            String value = kv[i + 1];
            if (value == null) {
                continue;
            }
            sb.append(sb.length() == 0 ? "?" : "&")
              .append(enc(kv[i])).append('=').append(enc(value));
        }
        return sb.toString();
    }

    /**
     * Converts a map of fields into form parameters. When {@code prefix} is
     * non-null, each key is nested as {@code prefix[key]} (Rails-style). List
     * values are emitted as repeated {@code key[]} parameters.
     */
    private static List<NameValuePair> formFromMap(String prefix, Map<String, Object> map) {
        List<NameValuePair> form = new ArrayList<>();
        if (map == null) {
            return form;
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            String name = prefix == null ? entry.getKey() : prefix + "[" + entry.getKey() + "]";
            if (entry.getValue() instanceof List<?>) {
                for (Object item : (List<?>) entry.getValue()) {
                    form.add(new BasicNameValuePair(name + "[]", String.valueOf(item)));
                }
            } else {
                form.add(new BasicNameValuePair(name, entry.getValue().toString()));
            }
        }
        return form;
    }
}
