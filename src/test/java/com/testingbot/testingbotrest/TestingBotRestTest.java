package com.testingbot.testingbotrest;

import com.testingbot.models.*;

import java.io.File;
import java.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import static org.junit.Assert.*;

public class TestingBotRestTest {
    private TestingbotREST api;
    
    @Before
    public void setUp() {
        this.api = new TestingbotREST(System.getenv("TB_KEY"), System.getenv("TB_SECRET"));
    }
    
    @Test
    public void testUpdateTest() {
        String sessionID = "6344353dcee24694bf39d5ee5e6e5b11";
        TestingbotTest test = this.api.getTest(sessionID);
        String randomMessage = UUID.randomUUID().toString();
        HashMap<String, Object> details = new HashMap<>();
        details.put("success", true);
        details.put("status_message", randomMessage);
        ArrayList<String> groups = new ArrayList<>();
        groups.add("group1");
        details.put("groups", groups);
        boolean success = this.api.updateTest(sessionID, details);
        assertTrue(success);
        TestingbotTest newTest = this.api.getTest(sessionID);
        assertNotEquals(newTest.getStatusMessage(), test.getStatusMessage());
        assertThat(newTest.getGroups(), hasItem("group1"));
    }
    
    @Test
    public void testUpdateTestViaObject() {
        TestingbotTest test = this.api.getTest("6344353dcee24694bf39d5ee5e6e5b11");
        TestingbotTest obj = new TestingbotTest();
        obj.setSessionId("6344353dcee24694bf39d5ee5e6e5b11");
        obj.setStatusMessage(UUID.randomUUID().toString());
        ArrayList<String> groups = new ArrayList<>();
        groups.add("group2");
        groups.add("group3");
        obj.setGroups(groups);
        boolean success = this.api.updateTest(obj);
        assertTrue(success);
        TestingbotTest newTest = this.api.getTest(obj.getSessionId());
        assertNotEquals(newTest.getStatusMessage(), test.getStatusMessage());
    }
    
    @Test
    public void testGetTest() {
        String sessionID = "6344353dcee24694bf39d5ee5e6e5b11";
        TestingbotTest test = this.api.getTest(sessionID);
        assertNotNull(test);
        Assert.assertEquals(test.getSessionId(), sessionID);
    }
    
    @Test
    public void testUnknownTest() {
        try {
            String sessionID = "unknown";
            this.api.getTest(sessionID);
            Assert.fail();
        } catch (TestingbotApiException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testDeleteUnknownTest() {
        try {
            this.api.deleteTest("unknown");
            Assert.fail();
        } catch (TestingbotApiException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testGetTests() {
        TestingbotTestCollection tests = this.api.getTests(0, 10);
        assertNotNull(tests.getData());
        Assert.assertEquals(tests.getData().size(), 10);
    }
    
    @Test
    public void testUnauthorized() {
        HashMap<String, Object> details = new HashMap<>();
        try {
            TestingbotREST apiUnknown = new TestingbotREST("unknown", "unknown");
            apiUnknown.updateTest("unknown", details);
            Assert.fail();
        } catch (TestingbotUnauthorizedException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testGetUser() {
        TestingbotUser user = this.api.getUserInfo();
        assertNotNull(user);
        Assert.assertEquals(user.getLastName(), "bot");
    }
    
    @Test
    public void testGetDevices() {
        List<TestingbotDevice> devices = this.api.getDevices(0, 10);
        assertNotNull(devices);
        Assert.assertFalse(devices.isEmpty());
    }
    
    @Test
    public void getGetDevice() {
        TestingbotDevice device = this.api.getDevice(1);
        assertNotNull(device);
    }
    
    @Test
    public void testGetBrowsers() {
        ArrayList<TestingbotBrowser> browsers = this.api.getBrowsers();
        assertTrue(browsers.size() > 0);
    }
    
    @Test
    public void testCalculateAuthentication() {
        Assert.assertEquals(this.api.getAuthenticationHash("test"), "344ebf07233168c4882adf953a8a8c9b");
    }

    @Test
    public void testUploadFileToStorage() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("sample.apk")).getFile());

        TestingbotStorageUploadResponse response = this.api.uploadToStorage(file);
        assertTrue(response.getAppUrl().length() > 0);
    }

    @Test
    public void testUploadFileToStorageViaURL() {
        TestingbotStorageUploadResponse response = this.api.uploadToStorage("https://testingbot.com/appium/sample.apk");
        assertTrue(response.getAppUrl().length() > 0);
    }

    @Test(expected = TestingbotApiException.class)
    public void testUploadAndDelete() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("sample.apk")).getFile());

        TestingbotStorageUploadResponse response = this.api.uploadToStorage(file);
        assertTrue(response.getAppUrl().length() > 0);

        TestingBotStorageFile fileData = this.api.getStorageFile(response.getAppUrl().replace("tb://", ""));
        Assert.assertEquals(fileData.getAppUrl(), response.getAppUrl());

        boolean successDelete = this.api.deleteStorageFile(response.getAppUrl().replace("tb://", ""));
        assertTrue(successDelete);
        this.api.getStorageFile(response.getAppUrl().replace("tb://", ""));
    }
}
