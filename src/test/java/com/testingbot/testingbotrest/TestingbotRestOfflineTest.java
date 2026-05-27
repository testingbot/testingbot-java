package com.testingbot.testingbotrest;

import com.google.gson.Gson;
import com.testingbot.models.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Offline unit tests that do not hit the TestingBot API. These guard the
 * authentication-hash bug fixes and the Gson field mappings.
 */
public class TestingbotRestOfflineTest {

    private TestingbotREST api;
    private final Gson gson = new Gson();

    @Before
    public void setUp() {
        this.api = new TestingbotREST("a", "b");
    }

    @After
    public void tearDown() {
        this.api.close();
    }

    @Test
    public void authHashIsZeroPaddedTo32Chars() {
        // md5("a:b:c") starts with a zero byte; BigInteger.toString(16) used to
        // drop the leading zero and return only 31 chars.
        String hash = api.getAuthenticationHash("c");
        Assert.assertEquals("02cc8f08398a4f3113b554e8105ebe4c", hash);
        Assert.assertEquals(32, hash.length());
    }

    @Test
    public void noArgAuthHashIsZeroPadded() {
        TestingbotREST other = new TestingbotREST("k", "8");
        try {
            String hash = other.getAuthenticationHash();
            Assert.assertEquals("0c8b7d4e79eae48050865e30d332faee", hash);
        } finally {
            other.close();
        }
    }

    @Test
    public void authHashUsesFullUtf8ByteLength() {
        // "café" encodes to 5 UTF-8 bytes but 4 chars; the old code digested
        // only String.length() bytes, producing the wrong hash.
        String hash = api.getAuthenticationHash("café");
        Assert.assertEquals("ced8a8ff11bcbe487704dddb9dcef214", hash);
    }

    @Test
    public void buildDeserializesSnakeCaseFields() {
        String json = "{\"id\":5,\"build_id\":\"my-build\",\"created_at\":\"2026-01-01\","
                + "\"completed_at\":\"2026-01-02\",\"status\":\"done\",\"total_tests\":3,"
                + "\"passed_tests\":2,\"failed_tests\":1}";
        TestingbotBuild build = gson.fromJson(json, TestingbotBuild.class);
        Assert.assertEquals("my-build", build.getBuildId());
        Assert.assertEquals("2026-01-01", build.getCreatedAtDate());
        Assert.assertEquals("2026-01-02", build.getCompletedAt());
        Assert.assertEquals("done", build.getStatus());
        Assert.assertEquals(3, build.getTotalTests());
        Assert.assertEquals(2, build.getPassedTests());
        Assert.assertEquals(1, build.getFailedTests());
    }

    @Test
    public void deviceDeserializesNewFields() {
        String json = "{\"id\":1,\"name\":\"iPhone 15\",\"model\":\"iPhone15,2\","
                + "\"manufacturer\":\"Apple\",\"platform_version\":\"17.0\","
                + "\"screen_size\":\"6.1\",\"screen_resolution\":\"1179x2556\"}";
        TestingbotDevice device = gson.fromJson(json, TestingbotDevice.class);
        Assert.assertEquals("iPhone15,2", device.getModel());
        Assert.assertEquals("Apple", device.getManufacturer());
        Assert.assertEquals("17.0", device.getPlatformVersion());
        Assert.assertEquals("6.1", device.getScreenSize());
        Assert.assertEquals("1179x2556", device.getScreenResolution());
    }

    @Test
    public void browserDeserializesSeleniumName() {
        TestingbotBrowser browser = gson.fromJson("{\"selenium_name\":\"chrome\",\"name\":\"Chrome\"}", TestingbotBrowser.class);
        Assert.assertEquals("chrome", browser.getSeleniumName());
        Assert.assertEquals("Chrome", browser.getName());
    }

    @Test
    public void userDeserializesEmailAndMobileConcurrency() {
        TestingbotUser user = gson.fromJson("{\"email\":\"x@y.com\",\"max_concurrent_mobile\":4}", TestingbotUser.class);
        Assert.assertEquals("x@y.com", user.getEmail());
        Assert.assertEquals(4, user.getMaxConcurrentMobile());
    }

    @Test
    public void tunnelDeserializesNewFields() {
        TestingbotTunnel tunnel = gson.fromJson("{\"tunnel_id\":\"abc\",\"identifier\":\"my-tunnel\",\"launched\":\"2026-01-01T00:00:00Z\"}", TestingbotTunnel.class);
        Assert.assertEquals("abc", tunnel.getTunnelId());
        Assert.assertEquals("my-tunnel", tunnel.getIdentifier());
        Assert.assertEquals("2026-01-01T00:00:00Z", tunnel.getLaunched());
    }

    @Test
    public void storageFileDeserializesNewFields() {
        TestingBotStorageFile file = gson.fromJson("{\"filename\":\"app.apk\",\"min_device_version\":\"10\",\"sim_only\":true}", TestingBotStorageFile.class);
        Assert.assertEquals("app.apk", file.getFilename());
        Assert.assertEquals("10", file.getMinDeviceVersion());
        assertTrue(file.isSimOnly());
    }

    @Test
    public void labRunAckDeserializesJobId() {
        TestingbotLabRunAck ack = gson.fromJson("{\"success\":true,\"job_id\":42}", TestingbotLabRunAck.class);
        assertTrue(ack.isSuccess());
        Assert.assertEquals(42, ack.getJobId());
    }
}
