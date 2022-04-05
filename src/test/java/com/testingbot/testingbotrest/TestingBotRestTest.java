package com.testingbot.testingbotrest;

import com.testingbot.models.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import junit.framework.TestCase;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class TestingBotRestTest extends TestCase {
    private TestingbotREST api;
    
    @Before
    @Override
    public void setUp() throws Exception {
        this.api = new TestingbotREST(System.getenv("TB_KEY"), System.getenv("TB_SECRET"));
    }
    
    @Test
    public void testUpdateTest() throws Exception {
        String sessionID = "6344353dcee24694bf39d5ee5e6e5b11";
        TestingbotTest test = this.api.getTest(sessionID);
        String randomMessage = UUID.randomUUID().toString();
        HashMap<String, Object> details = new HashMap<String, Object>();
        details.put("success", true);
        details.put("status_message", randomMessage);
        boolean success = this.api.updateTest(sessionID, details);
        assertEquals(success, true);
        TestingbotTest newTest = this.api.getTest(sessionID);
        assertNotEquals(newTest.getStatusMessage(), test.getStatusMessage());
    }
    
    @Test
    public void testUpdateTestViaObject() throws Exception {
        TestingbotTest test = this.api.getTest("6344353dcee24694bf39d5ee5e6e5b11");
        TestingbotTest obj = new TestingbotTest();
        obj.setSessionId("6344353dcee24694bf39d5ee5e6e5b11");
        obj.setStatusMessage(UUID.randomUUID().toString());
        boolean success = this.api.updateTest(obj);
        assertEquals(success, true);
        TestingbotTest newTest = this.api.getTest(obj.getSessionId());
        assertNotEquals(newTest.getStatusMessage(), test.getStatusMessage());
    }
    
    @Test
    public void testGetTest() throws Exception {
        String sessionID = "6344353dcee24694bf39d5ee5e6e5b11";
        TestingbotTest test = this.api.getTest(sessionID);
        assertNotNull(test);
        assertEquals(test.getSessionId(), sessionID);
    }
    
    @Test
    public void testUnknownTest() throws Exception {
        try {
            String sessionID = "unknown";
            TestingbotTest test = this.api.getTest(sessionID);
            assertEquals(true, false);
        } catch (TestingbotApiException e) {
            assertEquals(true, true);
        }
    }
    
    @Test
    public void testDeleteUnknownTest() throws Exception {
        try {
            boolean success = this.api.deleteTest("unknown");
            assertEquals(true, false);
        } catch (TestingbotApiException e) {
            assertEquals(true, true);
        }
    }
    
    @Test
    public void testGetTests() throws Exception {
        TestingbotTestCollection tests = this.api.getTests(0, 10);
        System.out.println(tests.getData().get(0).getSessionId());
        assertNotNull(tests.getData());
        assertEquals(tests.getData().size(), 10);
    }
    
    @Test
    public void testUnauthorized() throws Exception {
        HashMap<String, Object> details = new HashMap<String, Object>();
        try {
            TestingbotREST apiUnknown = new TestingbotREST("unknown", "unknown");
            apiUnknown.updateTest("unknown", details);
            assertEquals(true, false);
        } catch (TestingbotUnauthorizedException e) {
            assertEquals(true, true);
        }
    }
    
    @Test
    public void testGetUser() throws Exception {
        TestingbotUser user = this.api.getUserInfo();
        assertNotNull(user);
        assertTrue(user instanceof TestingbotUser);
        assertEquals(user.getLastName(), "bot");
    }
    
    @Test
    public void testGetDevices() throws Exception {
        List<TestingbotDevice> devices = this.api.getDevices(0, 10);
        assertNotNull(devices);
        assertTrue(!devices.isEmpty());
    }
    
    @Test
    public void getGetDevice() throws Exception {
        TestingbotDevice device = this.api.getDevice(1);
        assertNotNull(device);
        assertTrue(device instanceof TestingbotDevice);
    }
    
    @Test
    public void testGetBrowsers() throws Exception {
        ArrayList<TestingbotBrowser> browsers = this.api.getBrowsers();
        assertEquals(browsers.size() > 0, true);
    }
    
    @Test
    public void testCalculateAuthentication() throws Exception {
        assertEquals(this.api.getAuthenticationHash("test"), "344ebf07233168c4882adf953a8a8c9b");
    }

    @Test
    public void testUploadFileToStorage() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample.apk").getFile());

        TestingbotStorageUploadResponse response = this.api.uploadToStorage(file);
        assertTrue(response.getAppUrl().length() > 0);
    }

    @Test
    public void testUploadFileToStorageViaURL() throws Exception {
        TestingbotStorageUploadResponse response = this.api.uploadToStorage("https://testingbot.com/appium/sample.apk");
        assertTrue(response.getAppUrl().length() > 0);
    }

    @Test(expected = TestingbotApiException.class)
    public void testUploadAndDelete() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("sample.apk").getFile());

        TestingbotStorageUploadResponse response = this.api.uploadToStorage(file);
        assertTrue(response.getAppUrl().length() > 0);

        TestingBotStorageFile fileData = this.api.getStorageFile(response.getAppUrl().replace("tb://", ""));
        assertEquals(fileData.getAppUrl(), response.getAppUrl());

        this.api.deleteStorageFile(response.getAppUrl().replace("tb://", ""));
        try {
            TestingBotStorageFile newFileData = this.api.getStorageFile(response.getAppUrl().replace("tb://", ""));
            assertEquals(true, false);
        } catch (TestingbotApiException e) {
            assertEquals(true, true);
        }
    }
}
