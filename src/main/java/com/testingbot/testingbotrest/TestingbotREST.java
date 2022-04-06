package com.testingbot.testingbotrest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.testingbot.BuildUtils;
import com.testingbot.models.*;

import java.io.*;
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
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

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
public class TestingbotREST {

    /**
     * The key to use when performing HTTP requests to the TestingBot REST API.
     */
    protected String key;
    /**
     * The secret key to use when performing HTTP requests to the TestingBot
     * REST API.
     */
    protected String secret;
    
    private final Gson gson;

    /**
     * Constructs a new instance of the TestingBotREST class.
     *
     * @param key The key to use when performing HTTP requests to the TestingBot
     * REST API
     * @param secret The access key to use when performing HTTP requests to the
     * TestingBot REST API
     */
    public TestingbotREST(String key, String secret) {
        this.key = key;
        this.secret = secret;
        this.gson = new GsonBuilder().create();
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
        return this.apiUpdate("https://api.testingbot.com/v1/tests/" + sessionID, details);
    }

    /**
     * Stops a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return Boolean response The API response
     */
    public boolean stopTest(String sessionID) {
        return this.apiUpdate("https://api.testingbot.com/v1/tests/" + sessionID + "/stop", null);
    }

    /**
     * Deletes a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID of the test to delete from TestingBot
     * @return Boolean success
     */
    public boolean deleteTest(String sessionID) {
        return this.apiDelete("https://api.testingbot.com/v1/tests/" + sessionID);
    }

    /**
     * Gets list of available browsers from TestingBot
     *
     * @return ArrayList<TestingbotBrowser>
     */
    public ArrayList<TestingbotBrowser> getBrowsers() {
        return this.apiGet("https://api.testingbot.com/v1/browsers", TypeToken.getParameterized(ArrayList.class, TestingbotBrowser.class).getType());
    }

    /**
     * Get latest tests
     *
     * @param offset where to begin
     * @param count number of tests
     * @return TestingbotTestCollection
     */
    public TestingbotTestCollection getTests(int offset, int count) {
        return this.apiGet("https://api.testingbot.com/v1/tests/?offset=" + offset + "&count=" + count, new TypeToken<TestingbotTestCollection>(){}.getType());
    }

    /**
     * Gets information from TestingBot for a test with sessionID
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return TestingbotTest
     */
    public TestingbotTest getTest(String sessionID) {
        return this.apiGet("https://api.testingbot.com/v1/tests/" + sessionID, new TypeToken<TestingbotTest>(){}.getType());
    }

    /**
     * Gets information from TestingBot for your user account
     *
     * @return response The API response
     */
    public TestingbotUser getUserInfo() {
        return this.apiGet("https://api.testingbot.com/v1/user", new TypeToken<TestingbotUser>(){}.getType());
    }
    
    /**
     * Updates information for a TestingBot User
     *
     * @param testingbotUser the user you are updating
     * @return boolean success
     */
    public boolean updateUserInfo(TestingbotUser testingbotUser) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));

            HttpPut putRequest = new HttpPut("https://api.testingbot.com/v1/user/");
            putRequest.setHeader("Authorization", "Basic " + encoding);
            putRequest.setHeader("User-Agent", getUserAgent());

            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("user[first_name]", testingbotUser.getFirstName()));
            nameValuePairs.add(new BasicNameValuePair("user[last_name]", testingbotUser.getLastName()));
            
            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(putRequest);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            String payload = sb.toString();

            if (response.getStatusLine().getStatusCode() > 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(payload);
            }

            JSONObject json = new JSONObject(payload);

            return json.getBoolean("success");
        } catch (JSONException | IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Gets assigned tunnels for your account on TestingBot
     *
     * @return response The API response
     */
    public ArrayList<TestingbotTunnel> getTunnels() {
        return this.apiGet("https://api.testingbot.com/v1/tunnel/list", TypeToken.getParameterized(ArrayList.class, TestingbotTunnel.class).getType());
    }

    /**
     * Deletes a Tunnel with tunnel ID
     *
     * @param tunnelID The tunnelID of the tunnel to delete from TestingBot
     * @return boolean
     */
    public boolean deleteTunnel(String tunnelID) {
        return this.apiDelete("https://api.testingbot.com/v1/tunnel/" + tunnelID);
    }
    
    /**
     * Gets tests for a specific build from TestingBot
     *
     * @param buildIdentifier the identifier (string) for the build
     * @return response The API response
     */
    public TestingbotTestBuildCollection getTestsForBuild(String buildIdentifier) {
        return this.apiGet("https://api.testingbot.com/v1/builds/" + buildIdentifier, new TypeToken<TestingbotTestBuildCollection>(){}.getType());
    }
    
    /**
     * Get test builds
     *
     * @param offset where to begin
     * @param count number of builds
     * @return TestingbotBuildCollection
     */
    public TestingbotBuildCollection getBuilds(int offset, int count) {
        return this.apiGet("https://api.testingbot.com/v1/builds/?offset=" + offset + "&count=" + count, new TypeToken<TestingbotBuildCollection>(){}.getType());
    }
    
    /**
     * Delete a specific build
     *
     * @param buildId the build identifier you want to delete
     * @return boolean
     */
    public boolean deleteBuild(String buildId) {
        return this.apiDelete("https://api.testingbot.com/v1/builds/" + buildId);
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param file The path to the local file you want to upload
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(File file) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));

            HttpPost post = new HttpPost("https://api.testingbot.com/v1/storage");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();

            post.setHeader("Authorization", "Basic " + encoding);
            post.setHeader("User-Agent", getUserAgent());

            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (response.getStatusLine().getStatusCode() >= 400) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }

            return gson.fromJson(sb.toString(), TestingbotStorageUploadResponse.class);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param url to the file (apk/ipa)
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(String url) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));

            HttpPost post = new HttpPost("https://api.testingbot.com/v1/storage");
            post.setHeader("Authorization", "Basic " + encoding);
            post.setHeader("User-Agent", getUserAgent());

            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("url", url));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(post);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (response.getStatusLine().getStatusCode() >= 400) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }

            return gson.fromJson(sb.toString(), TestingbotStorageUploadResponse.class);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Retrieves meta-data from a TestingBot Storage file
     *
     * @param appUrl of the file
     * @return TestingBotStorageFile file
     */
    public TestingBotStorageFile getStorageFile(String appUrl) {
        return this.apiGet("https://api.testingbot.com/v1/storage/" + appUrl.replace("tb://", ""), new TypeToken<TestingBotStorageFile>(){}.getType());
    }

    /**
     * Retrieves meta-data for TestingBot Storage files
     *
     * @param offset where to begin
     * @param count number of files
     * @return TestingBotStorageFileCollection files
     */
    public TestingBotStorageFileCollection getStorageFiles(int offset, int count) {
        return this.apiGet("https://api.testingbot.com/v1/storage/?offset=" + offset + "&count=" + count, new TypeToken<TestingBotStorageFileCollection>(){}.getType());
    }
    
    /**
     * Retrieves available Real Mobile Devices on TestingBot
     *
     * @param offset where to begin
     * @param count number of devices
     * @return List<TestingbotDevice> devices
     */
    public List<TestingbotDevice> getAvailableDevices(int offset, int count) {
        return this.apiGet("https://api.testingbot.com/v1/devices/available/?offset=" + offset + "&count=" + count, TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }
    
    /**
     * Retrieves Real Mobile Devices on TestingBot
     * This includes devices not currently available
     *
     * @param offset where to begin
     * @param count number of real devices
     * @return List<TestingbotDevice> devices
     */
    public List<TestingbotDevice> getDevices(int offset, int count) {
        return this.apiGet("https://api.testingbot.com/v1/devices/?offset=" + offset + "&count=" + count, TypeToken.getParameterized(List.class, TestingbotDevice.class).getType());
    }
    
    /**
     * Retrieves Real Mobile Devices on TestingBot
     *
     * @param deviceId - id of the Device
     * @return TestingbotDevice device
     */
    public TestingbotDevice getDevice(int deviceId) {
        return this.apiGet("https://api.testingbot.com/v1/devices/" + deviceId, new TypeToken<TestingbotDevice>(){}.getType());
    }

    /**
     * Delete a file previously uploaded TestingBot Storage
     *
     * @param appUrl of the file
     * @return boolean success
     */
    public boolean deleteStorageFile(String appUrl) {
        return this.apiDelete("https://api.testingbot.com/v1/storage/" + appUrl);
    }


    /**
     * Calculates the authentication hash for a specific identifier (sessionId/build-identifier)
     * https://testingbot.com/support/other/sharing
     * 
     * @param identifier the sessionId or buildIdentifier
     * @return String hash
     */
    public String getAuthenticationHash(String identifier) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            String s = this.key + ":" + this.secret + ":" + identifier;
            m.update(s.getBytes(StandardCharsets.UTF_8), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    /**
     * Calculates the authentication hash for the current user
     * https://testingbot.com/support/other/sharing
     * 
     * @return String hash
     */
    public String getAuthenticationHash() {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            String s = this.key + ":" + this.secret;
            m.update(s.getBytes(StandardCharsets.UTF_8), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private String getUserAgent() {
        return "TestingBotRest/" + BuildUtils.getCurrentVersion();
    }

    private <T> T apiGet(String url, Type returnType) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));

            HttpGet getRequest = new HttpGet(url);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (response.getStatusLine().getStatusCode() > 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }

            return gson.fromJson(sb.toString(), returnType);
        } catch (IOException | JSONException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean apiUpdate(String url, Map<String, Object> details) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));

            HttpPut putRequest = new HttpPut(url);
            putRequest.setHeader("Authorization", "Basic " + encoding);
            putRequest.setHeader("User-Agent", getUserAgent());

            if (details != null) {
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                for (Map.Entry<String, Object> entry : details.entrySet()) {
                    if (entry.getKey().equals("groups")) {
                        for (int i = 0; i < ((List) entry.getValue()).size(); i++) {
                            nameValuePairs.add(new BasicNameValuePair("groups[]", ((List) entry.getValue()).get(i).toString()));
                        }
                    } else if (entry.getValue() != null) {
                        nameValuePairs.add(new BasicNameValuePair("test[" + entry.getKey() + "]", entry.getValue().toString()));
                    }
                }

                putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            }

            HttpResponse response = httpClient.execute(putRequest);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            String payload = sb.toString();

            if (response.getStatusLine().getStatusCode() > 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(payload);
            }

            JSONObject json = new JSONObject(payload);

            return json.getBoolean("success");
        } catch (IOException | JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean apiDelete(String url) {
        BufferedReader br = null;
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpDelete deleteRequest = new HttpDelete(url);
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes(StandardCharsets.UTF_8));
            deleteRequest.setHeader("Authorization", "Basic " + encoding);
            deleteRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(deleteRequest);
            br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), StandardCharsets.UTF_8));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            String payload = sb.toString();

            if (response.getStatusLine().getStatusCode() > 200) {
                if (response.getStatusLine().getStatusCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(payload);
            }

            JSONObject json = new JSONObject(payload);

            return json.getBoolean("success");
        } catch (IOException | JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
