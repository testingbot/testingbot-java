package com.testingbot.testingbotrest;

import com.testingbot.models.TestingbotBrowser;
import com.testingbot.models.TestingbotTest;
import com.testingbot.models.TestingbotTestCollection;
import com.testingbot.models.TestingbotTunnel;
import com.testingbot.models.TestingbotUser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import junit.framework.TestCase;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.Test;

public class TestingBotRestTest extends TestCase {
    private TestingbotREST api;
    
    @Before
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
        assertEquals(user.getLastName(), "bot");
    }
    
    @Test
    public void testGetTunnels() throws Exception {
        ArrayList<TestingbotTunnel> tunnels = this.api.getTunnels();
        assertEquals(tunnels.size(), 0);
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
}
