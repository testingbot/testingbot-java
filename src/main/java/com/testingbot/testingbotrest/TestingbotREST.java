package com.testingbot.testingbotrest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.testingbot.models.*;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.iharder.Base64;
import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
     * @return boolean success response The API response
     */
    public boolean updateTest(TestingbotTest test) {
        HashMap<String, Object> details = new HashMap<String, Object>();
        details.put("status_message", test.getStatusMessage());
        details.put("success", test.isSuccess());
        details.put("build", test.getBuild());
        details.put("extra", test.getExtra());
        details.put("name", test.getName());
        return this.updateTest(test.getSessionId(), details);
    }

    /**
     * Updates a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @param details The meta-data to send to the TestingBot API. See
     * https://testingbot.com/support/api#updatetest
     * @return boolean success response The API response
     */
    public boolean updateTest(String sessionID, Map<String, Object> details) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpPut putRequest = new HttpPut("https://api.testingbot.com/v1/tests/" + sessionID);
            putRequest.setHeader("Authorization", "Basic " + encoding);
            putRequest.setHeader("User-Agent", getUserAgent());

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            for (Map.Entry<String, Object> entry : details.entrySet()) {
                if (entry.getValue() != null) {
                    nameValuePairs.add(new BasicNameValuePair("test[" + entry.getKey() + "]", entry.getValue().toString()));
                }
            }

            putRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(putRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * Stops a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return boolean response The API response
     */
    public boolean stopTest(String sessionID) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpPut putRequest = new HttpPut("https://api.testingbot.com/v1/tests/" + sessionID + "/stop");
            putRequest.setHeader("Authorization", "Basic " + encoding);
            putRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(putRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            JSONObject json = new JSONObject(sb.toString());
            return json.getBoolean("success");
        } catch (JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Deletes a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID of the test to delete from TestingBot
     * @return boolean success
     */
    public boolean deleteTest(String sessionID) {
        try {
            URL url = new URL("https://api.testingbot.com/v1/tests/" + sessionID);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            String userpass = this.key + ":" + this.secret;
            String auth = Base64.encodeBytes(userpass.getBytes("UTF-8"));
            httpCon.setRequestProperty("Authorization", auth);
            httpCon.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty(
                    "User-Agent", getUserAgent());
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            httpCon.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream(), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            
            if (httpCon.getResponseCode() > 200) {
                if (httpCon.getResponseCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }
            
            JSONObject json = new JSONObject(sb.toString());
            return json.getBoolean("success");
        } catch (ProtocolException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (IOException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (JSONException ex) {
            throw new TestingbotApiException(ex.getMessage());
        }
    }

    /**
     * Gets list of available browsers from TestingBot
     *
     * @return 
     */
    public ArrayList<TestingbotBrowser> getBrowsers() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/browsers");
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            
            Type listType = new TypeToken<ArrayList<TestingbotBrowser>>(){}.getType();

            return gson.fromJson(sb.toString(), listType);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new ArrayList<TestingbotBrowser>();
    }

    /**
     * Get latest tests
     *
     * @param offset
     * @param count
     * @return TestingbotTestCollection
     */
    public TestingbotTestCollection getTests(int offset, int count) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/tests/?offset=" + offset + "&count=" + count);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingbotTestCollection.class);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotTestCollection();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotTestCollection();
        }
    }

    /**
     * Gets information from TestingBot for a test with sessionID
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return response The API response
     */
    public TestingbotTest getTest(String sessionID) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/tests/" + sessionID);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingbotTest.class);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotTest();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotTest();
        }
    }

    /**
     * Gets information from TestingBot for your user account
     *
     * @return response The API response
     */
    public TestingbotUser getUserInfo() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/user");
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingbotUser.class);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Gets assigned tunnels for your account on TestingBot
     *
     * @return response The API response
     */
    public ArrayList<TestingbotTunnel> getTunnels() {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/tunnel/list");
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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
            
            Type listType = new TypeToken<ArrayList<TestingbotTunnel>>(){}.getType();

            return gson.fromJson(sb.toString(), listType);
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<TestingbotTunnel>();
        }
    }

    /**
     * Deletes a Tunnel with tunnel ID
     *
     * @param tunnelID The tunnelID of the tunnel to delete from TestingBot
     * @return boolean
     */
    public boolean deleteTunnel(String tunnelID) {
        try {
            URL url = new URL("https://api.testingbot.com/v1/tunnel/" + tunnelID);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            String userpass = this.key + ":" + this.secret;
            String auth = Base64.encodeBytes(userpass.getBytes("UTF-8"));
            httpCon.setRequestProperty("Authorization", auth);
            httpCon.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty(
                    "User-Agent", getUserAgent());
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            httpCon.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream(), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            if (httpCon.getResponseCode() > 200) {
                if (httpCon.getResponseCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }
            
            JSONObject json = new JSONObject(sb.toString());
            return json.getBoolean("success");
        } catch (ProtocolException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Gets tests for a specific build from TestingBot
     *
     * @param buildIdentifier
     * @return response The API response
     */
    public TestingbotTestBuildCollection getTestsForBuild(String buildIdentifier) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/builds/" + buildIdentifier);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingbotTestBuildCollection.class);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Get test builds
     *
     * @param offset
     * @param count
     * @return TestingbotBuildCollection
     */
    public TestingbotBuildCollection getBuilds(int offset, int count) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/builds/?offset=" + offset + "&count=" + count);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingbotBuildCollection.class);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotBuildCollection();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotBuildCollection();
        }
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param file
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(File file) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpPost post = new HttpPost("https://api.testingbot.com/v1/storage");
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.getName());
            HttpEntity entity = builder.build();

            post.setHeader("Authorization", "Basic " + encoding);
            post.setHeader("User-Agent", getUserAgent());

            post.setEntity(entity);

            HttpResponse response = httpClient.execute(post);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        }
    }

    /**
     * Upload file to TestingBot Storage
     *
     * @param url to the file (apk/ipa)
     * @return TestingbotStorageUploadResponse
     */
    public TestingbotStorageUploadResponse uploadToStorage(String url) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpPost post = new HttpPost("https://api.testingbot.com/v1/storage");
            post.setHeader("Authorization", "Basic " + encoding);
            post.setHeader("User-Agent", getUserAgent());

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("url", url));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(post);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        } catch (IOException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return new TestingbotStorageUploadResponse();
        }
    }

    /**
     * Retrieves meta-data from a TestingBot Storage file
     *
     * @param appUrl of the file
     * @return TestingBotStorageFile file
     */
    public TestingBotStorageFile getStorageFile(String appUrl) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/storage/" + appUrl.replace("tb://", ""));
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingBotStorageFile.class);
        } catch (ProtocolException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (IOException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (JSONException ex) {
            throw new TestingbotApiException(ex.getMessage());
        }
    }

    /**
     * Retrieves meta-data for TestingBot Storage files
     *
     * @param offset
     * @param count
     * @return TestingBotStorageFileCollection files
     */
    public TestingBotStorageFileCollection getStorageFiles(int offset, int count) {
        try {
            HttpClient httpClient = HttpClientBuilder.create().build();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/storage/?offset=" + offset + "&count=" + count);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", getUserAgent());

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent()), "UTF8"));
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

            return gson.fromJson(sb.toString(), TestingBotStorageFileCollection.class);
        } catch (ProtocolException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (IOException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (JSONException ex) {
            throw new TestingbotApiException(ex.getMessage());
        }
    }

    /**
     * Delete a file previously uploaded TestingBot Storage
     *
     * @param appUrl of the file
     * @return boolean success
     */
    public boolean deleteStorageFile(String appUrl) {
        try {
            URL url = new URL("https://api.testingbot.com/v1/storage/" + appUrl);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            String userpass = this.key + ":" + this.secret;
            String auth = Base64.encodeBytes(userpass.getBytes("UTF-8"));
            httpCon.setRequestProperty("Authorization", auth);
            httpCon.setRequestProperty(
                    "Content-Type", "application/x-www-form-urlencoded");
            httpCon.setRequestProperty(
                    "User-Agent", getUserAgent());
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            httpCon.getInputStream();
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(httpCon.getInputStream(), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            if (httpCon.getResponseCode() > 200) {
                if (httpCon.getResponseCode() == 401) {
                    throw new TestingbotUnauthorizedException();
                }
                throw new TestingbotApiException(sb.toString());
            }

            JSONObject json = new JSONObject(sb.toString());
            return json.getBoolean("success");
        } catch (ProtocolException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (IOException ex) {
            throw new TestingbotApiException(ex.getMessage());
        } catch (JSONException ex) {
            throw new TestingbotApiException(ex.getMessage());
        }
    }


    /**
     * Calculates the authentication hash for a specific identifier (sessionId/build-identifier)
     * https://testingbot.com/support/other/sharing
     * 
     * @param identifier
     * @return String hash
     */
    public String getAuthenticationHash(String identifier) {
        try {
            MessageDigest m=MessageDigest.getInstance("MD5");
            String s = this.key + ":" + this.secret + ":" + identifier;
            m.update(s.getBytes(), 0, s.length());
            String md5 = new BigInteger(1, m.digest()).toString(16);
            return md5;
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
            MessageDigest m=MessageDigest.getInstance("MD5");
            String s = this.key + ":" + this.secret;
            m.update(s.getBytes(), 0, s.length());
            String md5 = new BigInteger(1, m.digest()).toString(16);
            return md5;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private String getUserAgent() {
        return "TestingBotRest/1.1";
    }
}
