package com.testingbot.testingbotrest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import net.iharder.Base64;
import java.util.ArrayList;
import java.util.List;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * Simple Java API that invokes the TestingBot REST API.  The full list of the TestingBot API functionality is available from
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
     * The secret key to use when performing HTTP requests to the TestingBot REST API.
     */
    protected String secret;
    
     /**
     * Constructs a new instance of the TestingBotREST class.
     *
     * @param key  The key to use when performing HTTP requests to the TestingBot REST API
     * @param secret The access key to use when performing HTTP requests to the TestingBot REST API
     */
    public TestingbotREST(String key, String secret) {
        this.key = key;
        this.secret = secret;
    }
    
    /**
     * Updates a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @param details The meta-data to send to the TestingBot API. See https://testingbot.com/support/api#updatetest
     * @return response The API response
     */
    public String updateTest(String sessionID, Map<String, String> details)
    {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpPut putRequest = new HttpPut("https://api.testingbot.com/v1/tests/" + sessionID);
            putRequest.setHeader("Authorization", "Basic " + encoding);
            putRequest.setHeader("User-Agent", "TestingBotRest/1.0");

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            for (Map.Entry<String,String> entry : details.entrySet())
            {
                nameValuePairs.add(new BasicNameValuePair("test[" + entry.getKey().toString() + "]", entry.getValue().toString()));
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
            
            return sb.toString();
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    /**
     * Deletes a Test with sessionID (Selenium sessionID)
     *
     * @param sessionID The sessionID of the test to delete from TestingBot
     */
    public void deleteTest(String sessionID)
    {
        try {
            URL url = new URL("https://api.testingbot.com/v1/tests/" + sessionID);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            String userpass = this.key + ":" + this.secret;
            String auth = Base64.encodeBytes(userpass.getBytes("UTF-8"));
            httpCon.setRequestProperty("Authorization", auth);
            httpCon.setRequestProperty(
                "Content-Type", "application/x-www-form-urlencoded" );
            httpCon.setRequestMethod("DELETE");
            httpCon.connect();
            httpCon.getInputStream();
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets list of available browsers from TestingBot
     * for automationApi (rc or webdriver)
     *
     * @param automationApi rc or webdriver
     * @return response The API response
     */
    public String getSupportedPlatforms(String automationApi) {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/browsers?type=" + automationApi);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", "TestingBotRest/1.0");

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                     new InputStreamReader((response.getEntity().getContent()), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                    sb.append(output);
            }
            
            return sb.toString();
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public String getTests(int offset, int count)
    {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/tests/?offset=" + offset + "&count=" + count);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", "TestingBotRest/1.0");

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                     new InputStreamReader((response.getEntity().getContent()), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                    sb.append(output);
            }
            
            return sb.toString();
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    /**
     * Gets information from TestingBot for a test with sessionID
     *
     * @param sessionID The sessionID retrieved from Selenium WebDriver/RC
     * @return response The API response
     */
    public String getTest(String sessionID)
    {
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String userpass = this.key + ":" + this.secret;
            String encoding = Base64.encodeBytes(userpass.getBytes("UTF-8"));

            HttpGet getRequest = new HttpGet("https://api.testingbot.com/v1/tests/" + sessionID);
            getRequest.setHeader("Authorization", "Basic " + encoding);
            getRequest.setHeader("User-Agent", "TestingBotRest/1.0");

            HttpResponse response = httpClient.execute(getRequest);
            BufferedReader br = new BufferedReader(
                     new InputStreamReader((response.getEntity().getContent()), "UTF8"));
            String output;
            StringBuilder sb = new StringBuilder();
            while ((output = br.readLine()) != null) {
                    sb.append(output);
            }
            
            return sb.toString();
        } catch (Exception ex) {
            Logger.getLogger(TestingbotREST.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
}
