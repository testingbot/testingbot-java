package com.testingbot.testingbotrest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.testingbot.models.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Offline tests that run the client against a local {@link HttpServer},
 * verifying the HTTP method, path, query/body and response parsing for every
 * endpoint — no credentials or network required.
 */
public class TestingbotRestMockTest {

    private HttpServer server;
    private TestingbotREST api;

    // captured request
    private volatile String lastMethod;
    private volatile String lastPath;
    private volatile String lastQuery;
    private volatile String lastBody;
    private volatile String lastAuth;
    private volatile String lastUserAgent;

    // configurable response
    private volatile int responseStatus = 200;
    private volatile String responseBody = "{\"success\":true}";

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange ex) throws IOException {
                lastMethod = ex.getRequestMethod();
                lastPath = ex.getRequestURI().getPath();
                lastQuery = ex.getRequestURI().getRawQuery();
                lastAuth = ex.getRequestHeaders().getFirst("Authorization");
                lastUserAgent = ex.getRequestHeaders().getFirst("User-Agent");
                lastBody = read(ex.getRequestBody());

                byte[] out = responseBody == null ? new byte[0] : responseBody.getBytes(StandardCharsets.UTF_8);
                ex.sendResponseHeaders(responseStatus, out.length == 0 ? -1 : out.length);
                if (out.length > 0) {
                    OutputStream os = ex.getResponseBody();
                    os.write(out);
                    os.close();
                } else {
                    ex.close();
                }
            }
        });
        server.start();
        int port = server.getAddress().getPort();
        api = new TestingbotREST("key", "secret", "http://127.0.0.1:" + port + "/v1");
    }

    @After
    public void tearDown() {
        api.close();
        server.stop(0);
    }

    // ------------------------------------------------------------------ helpers

    private static String read(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int n;
        while ((n = is.read(buf)) != -1) {
            bos.write(buf, 0, n);
        }
        return new String(bos.toByteArray(), StandardCharsets.UTF_8);
    }

    private void respond(int status, String body) {
        this.responseStatus = status;
        this.responseBody = body;
    }

    private void assertRequest(String method, String path) {
        Assert.assertEquals("HTTP method", method, lastMethod);
        Assert.assertEquals("path", path, lastPath);
    }

    private String bodyDecoded() {
        try {
            return URLDecoder.decode(lastBody, "UTF-8");
        } catch (IOException e) {
            return lastBody;
        }
    }

    private static Map<String, Object> map(Object... kv) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (int i = 0; i + 1 < kv.length; i += 2) {
            m.put(String.valueOf(kv[i]), kv[i + 1]);
        }
        return m;
    }

    // ------------------------------------------------------------------ plumbing

    @Test
    public void sendsBasicAuthAndUserAgent() {
        respond(200, "{}");
        api.getUserInfo();
        String expected = "Basic " + java.util.Base64.getEncoder()
                .encodeToString("key:secret".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(expected, lastAuth);
        assertTrue(lastUserAgent != null && lastUserAgent.startsWith("TestingBotRest/"));
    }

    @Test(expected = TestingbotUnauthorizedException.class)
    public void unauthorizedThrows() {
        respond(401, "{\"error\":\"nope\"}");
        api.getTest("abc");
    }

    @Test
    public void apiErrorCarriesStatusAndBody() {
        respond(500, "{\"error\":\"boom\"}");
        try {
            api.getTest("abc");
            Assert.fail("expected TestingbotApiException");
        } catch (TestingbotApiException e) {
            Assert.assertEquals(500, e.getStatusCode());
            assertTrue(e.getResponseBody().contains("boom"));
        }
    }

    // ------------------------------------------------------------------ tests resource

    @Test
    public void getTestsPaginated() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getTests(0, 10);
        assertRequest("GET", "/v1/tests/");
        assertTrue(lastQuery.contains("offset=0"));
        assertTrue(lastQuery.contains("count=10"));
    }

    @Test
    public void getTestsWithFilters() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        Map<String, String> filters = new HashMap<>();
        filters.put("group", "smoke");
        api.getTests(0, 10, filters);
        assertRequest("GET", "/v1/tests/");
        assertTrue(lastQuery.contains("group=smoke"));
    }

    @Test
    public void createTestPostsTestParams() {
        api.createTest(map("name", "Foo", "success", true));
        assertRequest("POST", "/v1/tests");
        assertTrue(bodyDecoded().contains("test[name]=Foo"));
    }

    @Test
    public void getTestParsesResponse() {
        respond(200, "{\"session_id\":\"abc\",\"status_id\":1}");
        TestingbotTest t = api.getTest("abc");
        assertRequest("GET", "/v1/tests/abc");
        Assert.assertEquals("abc", t.getSessionId());
        Assert.assertEquals(1, t.getStatusId());
    }

    @Test
    public void updateTestPutsFormWithGroups() {
        ArrayList<String> groups = new ArrayList<>(Arrays.asList("g1", "g2"));
        Map<String, Object> details = map("status_message", "ok", "groups", groups);
        boolean ok = api.updateTest("abc", details);
        assertTrue(ok);
        assertRequest("PUT", "/v1/tests/abc");
        String body = bodyDecoded();
        assertTrue(body.contains("test[status_message]=ok"));
        assertTrue(body.contains("groups[]=g1"));
        assertTrue(body.contains("groups[]=g2"));
    }

    @Test
    public void stopTest() {
        api.stopTest("abc");
        assertRequest("PUT", "/v1/tests/abc/stop");
    }

    @Test
    public void deleteTest() {
        api.deleteTest("abc");
        assertRequest("DELETE", "/v1/tests/abc");
    }

    // ------------------------------------------------------------------ browsers / devices

    @Test
    public void getBrowsersParsesSeleniumName() {
        respond(200, "[{\"selenium_name\":\"chrome\",\"name\":\"Chrome\"}]");
        ArrayList<TestingbotBrowser> browsers = api.getBrowsers();
        assertRequest("GET", "/v1/browsers");
        Assert.assertEquals("chrome", browsers.get(0).getSeleniumName());
    }

    @Test
    public void getDevicesPaginated() {
        respond(200, "[]");
        api.getDevices(0, 10);
        assertRequest("GET", "/v1/devices/");
    }

    @Test
    public void getDevicesFiltered() {
        respond(200, "[]");
        api.getDevices("Android", true);
        assertRequest("GET", "/v1/devices");
        assertTrue(lastQuery.contains("platform=Android"));
        assertTrue(lastQuery.contains("web=true"));
    }

    @Test
    public void getAvailableDevicesNoArgs() {
        respond(200, "[]");
        api.getAvailableDevices();
        assertRequest("GET", "/v1/devices/available");
    }

    @Test
    public void getAvailableDevicesPaginated() {
        respond(200, "[]");
        api.getAvailableDevices(0, 10);
        assertRequest("GET", "/v1/devices/available/");
    }

    @Test
    public void getDeviceById() {
        respond(200, "{\"id\":1,\"screen_resolution\":\"1179x2556\"}");
        TestingbotDevice d = api.getDevice(1);
        assertRequest("GET", "/v1/devices/1");
        Assert.assertEquals("1179x2556", d.getScreenResolution());
    }

    // ------------------------------------------------------------------ user / keys

    @Test
    public void getUserInfoParsesEmail() {
        respond(200, "{\"email\":\"x@y.com\",\"max_concurrent_mobile\":2}");
        TestingbotUser u = api.getUserInfo();
        assertRequest("GET", "/v1/user");
        Assert.assertEquals("x@y.com", u.getEmail());
        Assert.assertEquals(2, u.getMaxConcurrentMobile());
    }

    @Test
    public void updateUserInfoPutsUserParams() {
        TestingbotUser u = new TestingbotUser();
        u.setFirstName("Jane");
        u.setLastName("Doe");
        api.updateUserInfo(u);
        assertRequest("PUT", "/v1/user/");
        assertTrue(bodyDecoded().contains("user[first_name]=Jane"));
    }

    @Test
    public void getUserKeys() {
        respond(200, "{\"key\":\"k\",\"secret\":\"s\"}");
        Assert.assertNotNull(api.getUserKeys());
        assertRequest("GET", "/v1/user/keys");
    }

    // ------------------------------------------------------------------ tunnels

    @Test
    public void getTunnels() {
        respond(200, "[]");
        api.getTunnels();
        assertRequest("GET", "/v1/tunnel/list");
    }

    @Test
    public void getActiveTunnel() {
        respond(200, "{\"tunnel_id\":\"t1\"}");
        TestingbotTunnel t = api.getTunnel();
        assertRequest("GET", "/v1/tunnel");
        Assert.assertEquals("t1", t.getTunnelId());
    }

    @Test
    public void getTunnelById() {
        respond(200, "{\"id\":5}");
        api.getTunnel(5);
        assertRequest("GET", "/v1/tunnel/5");
    }

    @Test
    public void createTunnel() {
        respond(200, "{\"id\":9,\"state\":\"booting\"}");
        TestingbotTunnel t = api.createTunnel();
        assertRequest("POST", "/v1/tunnel/create");
        Assert.assertEquals(9, t.getId());
    }

    @Test
    public void deleteTunnelById() {
        api.deleteTunnel("5");
        assertRequest("DELETE", "/v1/tunnel/5");
    }

    @Test
    public void deleteActiveTunnel() {
        respond(200, "");
        assertTrue(api.deleteTunnel());
        assertRequest("DELETE", "/v1/tunnel");
    }

    @Test
    public void isTunnelAlive() {
        respond(200, "{\"success\":true}");
        assertTrue(api.isTunnelAlive());
        assertRequest("GET", "/v1/tunnel/isalive-check");
    }

    // ------------------------------------------------------------------ builds

    @Test
    public void getBuilds() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getBuilds(0, 10);
        assertRequest("GET", "/v1/builds/");
    }

    @Test
    public void getTestsForBuildParsesBuildId() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getTestsForBuild("b1");
        assertRequest("GET", "/v1/builds/b1");
    }

    @Test
    public void deleteBuild() {
        api.deleteBuild("b1");
        assertRequest("DELETE", "/v1/builds/b1");
    }

    // ------------------------------------------------------------------ storage

    @Test
    public void uploadToStorageByUrl() {
        respond(200, "{\"app_url\":\"tb://x\"}");
        api.uploadToStorage("https://example.com/app.apk");
        assertRequest("POST", "/v1/storage");
        assertTrue(bodyDecoded().contains("url=https://example.com/app.apk"));
    }

    @Test
    public void getStorageFileParsesNewFields() {
        respond(200, "{\"filename\":\"app.apk\",\"sim_only\":true}");
        TestingBotStorageFile f = api.getStorageFile("tb://xyz");
        assertRequest("GET", "/v1/storage/xyz");
        Assert.assertEquals("app.apk", f.getFilename());
        assertTrue(f.isSimOnly());
    }

    @Test
    public void getStorageFiles() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getStorageFiles(0, 10);
        assertRequest("GET", "/v1/storage/");
    }

    @Test
    public void deleteStorageFile() {
        api.deleteStorageFile("xyz");
        assertRequest("DELETE", "/v1/storage/xyz");
    }

    // ------------------------------------------------------------------ jobs / config

    @Test
    public void getJob() {
        respond(200, "{\"status\":\"done\",\"success\":true}");
        TestingbotJob j = api.getJob("j1");
        assertRequest("GET", "/v1/jobs/j1");
        Assert.assertEquals("done", j.getStatus());
    }

    @Test
    public void getIpRanges() {
        respond(200, "[\"1.2.3.0/24\"]");
        Assert.assertNotNull(api.getIpRanges());
        assertRequest("GET", "/v1/configuration/ip-ranges");
    }

    // ------------------------------------------------------------------ team management

    @Test
    public void getTeam() {
        respond(200, "{\"concurrency\":{}}");
        api.getTeam();
        assertRequest("GET", "/v1/team-management");
    }

    @Test
    public void getTeamMembers() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getTeamMembers();
        assertRequest("GET", "/v1/team-management/users");
    }

    @Test
    public void createTeamMember() {
        respond(200, "{\"id\":3,\"email\":\"a@b.com\"}");
        TestingbotTeamMember m = api.createTeamMember(map("email", "a@b.com", "password", "pw"));
        assertRequest("POST", "/v1/team-management/users");
        assertTrue(bodyDecoded().contains("email=a@b.com"));
        Assert.assertEquals(3, m.getId());
    }

    @Test
    public void getTeamMember() {
        respond(200, "{\"id\":7}");
        api.getTeamMember(7);
        assertRequest("GET", "/v1/team-management/users/7");
    }

    @Test
    public void updateTeamMember() {
        respond(200, "{\"id\":7}");
        api.updateTeamMember(7, map("first_name", "New"));
        assertRequest("PUT", "/v1/team-management/users/7");
        assertTrue(bodyDecoded().contains("first_name=New"));
    }

    @Test
    public void getTeamMemberClientKey() {
        respond(200, "{\"client_key\":\"ck\"}");
        api.getTeamMemberClientKey(7);
        assertRequest("GET", "/v1/team-management/users/7/client-key");
    }

    @Test
    public void resetTeamMemberKeys() {
        respond(200, "{\"success\":true,\"client_key\":\"ck\"}");
        TestingbotTeamCredentialReset r = api.resetTeamMemberKeys(7);
        assertRequest("POST", "/v1/team-management/users/7/reset-keys");
        Assert.assertEquals("ck", r.getClientKey());
    }

    // ------------------------------------------------------------------ screenshots

    @Test
    public void getScreenshots() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getScreenshots();
        assertRequest("GET", "/v1/screenshots");
    }

    @Test
    public void createScreenshotsEncodesBrowsersList() {
        respond(200, "{\"id\":1,\"state\":\"PROCESSING\"}");
        List<String> browsers = Arrays.asList("chrome", "firefox");
        TestingbotScreenshot s = api.createScreenshots(map("url", "https://e.com", "resolution", "1920x1080", "browsers", browsers));
        assertRequest("POST", "/v1/screenshots");
        String body = bodyDecoded();
        assertTrue(body.contains("url=https://e.com"));
        assertTrue(body.contains("browsers[]=chrome"));
        assertTrue(body.contains("browsers[]=firefox"));
        Assert.assertEquals("PROCESSING", s.getState());
    }

    @Test
    public void getScreenshotById() {
        respond(200, "{\"id\":3}");
        api.getScreenshot(3);
        assertRequest("GET", "/v1/screenshots/3");
    }

    // ------------------------------------------------------------------ manual sessions

    @Test
    public void updateManualSession() {
        boolean ok = api.updateManualSession(12, map("name", "Run", "success", true));
        assertTrue(ok);
        assertRequest("PUT", "/v1/manual_session");
        String body = bodyDecoded();
        assertTrue(body.contains("id=12"));
        assertTrue(body.contains("manual_session[name]=Run"));
    }

    @Test
    public void pingManualSession() {
        boolean ok = api.pingManualSession(Arrays.asList(1, 2, 3));
        assertTrue(ok);
        assertRequest("POST", "/v1/manual_session/ping");
        String body = bodyDecoded();
        assertTrue(body.contains("ids[]=1"));
        assertTrue(body.contains("ids[]=3"));
    }

    // ------------------------------------------------------------------ codeless lab

    @Test
    public void getLabTests() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getLabTests();
        assertRequest("GET", "/v1/lab");
    }

    @Test
    public void createLabTestParsesId() {
        respond(200, "{\"success\":true,\"lab_test_id\":99}");
        TestingbotLabCreateAck ack = api.createLabTest(map("name", "T", "url", "https://e.com"));
        assertRequest("POST", "/v1/lab");
        assertTrue(bodyDecoded().contains("test[name]=T"));
        Assert.assertEquals(99, ack.getLabTestId());
    }

    @Test
    public void getLabTest() {
        respond(200, "{\"id\":9,\"name\":\"T\"}");
        TestingbotLabTest t = api.getLabTest(9);
        assertRequest("GET", "/v1/lab/9");
        Assert.assertEquals("T", t.getName());
    }

    @Test
    public void updateLabTest() {
        api.updateLabTest(9, map("name", "T2"));
        assertRequest("PUT", "/v1/lab/9");
        assertTrue(bodyDecoded().contains("test[name]=T2"));
    }

    @Test
    public void deleteLabTest() {
        respond(200, "");
        assertTrue(api.deleteLabTest(9));
        assertRequest("DELETE", "/v1/lab/9");
    }

    @Test
    public void getLabTestSteps() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getLabTestSteps(9);
        assertRequest("GET", "/v1/lab/9/steps");
    }

    @Test
    public void setLabTestSteps() {
        api.setLabTestSteps(9, Arrays.asList("open", "click"));
        assertRequest("POST", "/v1/lab/9/steps");
        String body = bodyDecoded();
        assertTrue(body.contains("steps[]=open"));
        assertTrue(body.contains("steps[]=click"));
    }

    @Test
    public void getLabTestBrowsers() {
        respond(200, "[{\"name\":\"Chrome\"}]");
        List<TestingbotBrowser> browsers = api.getLabTestBrowsers(9);
        assertRequest("GET", "/v1/lab/9/browsers");
        Assert.assertEquals("Chrome", browsers.get(0).getName());
    }

    @Test
    public void setLabTestBrowsers() {
        api.setLabTestBrowsers(9, "1,2,3");
        assertRequest("POST", "/v1/lab/9/browsers");
        assertTrue(bodyDecoded().contains("browser_ids=1,2,3"));
    }

    @Test
    public void triggerLabTestParsesJobId() {
        respond(200, "{\"success\":true,\"job_id\":7}");
        TestingbotLabRunAck ack = api.triggerLabTest(9);
        assertRequest("POST", "/v1/lab/9/trigger");
        Assert.assertEquals(7, ack.getJobId());
    }

    @Test
    public void stopLabTest() {
        api.stopLabTest(9);
        assertRequest("PUT", "/v1/lab/9/stop");
    }

    @Test
    public void addLabTestAlert() {
        api.addLabTestAlert(9, map("kind", "EMAIL", "level", "IMMEDIATELY", "content", "a@b.com"));
        assertRequest("POST", "/v1/lab/9/alert");
        assertTrue(bodyDecoded().contains("kind=EMAIL"));
    }

    @Test
    public void updateLabTestAlert() {
        api.updateLabTestAlert(9, map("level", "DAILY"));
        assertRequest("PUT", "/v1/lab/9/alert");
    }

    @Test
    public void createLabTestReport() {
        api.createLabTestReport(9, map("type", "pdf"));
        assertRequest("POST", "/v1/lab/9/report");
    }

    @Test
    public void updateLabTestReport() {
        api.updateLabTestReport(9, map("type", "pdf"));
        assertRequest("PUT", "/v1/lab/9/report");
    }

    @Test
    public void scheduleLabTest() {
        api.scheduleLabTest(9, map("type", "daily", "hour", "09:00"));
        assertRequest("POST", "/v1/lab/9/schedule");
        assertTrue(bodyDecoded().contains("type=daily"));
    }

    @Test
    public void triggerAllLabTests() {
        respond(200, "{\"success\":true,\"job_id\":1}");
        api.triggerAllLabTests();
        assertRequest("POST", "/v1/lab/trigger_all");
    }

    // ------------------------------------------------------------------ lab suites

    @Test
    public void getLabSuites() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getLabSuites();
        assertRequest("GET", "/v1/labsuites");
    }

    @Test
    public void createLabSuiteParsesId() {
        respond(200, "{\"success\":true,\"suite_id\":3}");
        TestingbotLabSuiteCreateAck ack = api.createLabSuite(map("name", "S"));
        assertRequest("POST", "/v1/labsuites");
        assertTrue(bodyDecoded().contains("suite[name]=S"));
        Assert.assertEquals(3, ack.getSuiteId());
    }

    @Test
    public void getLabSuite() {
        respond(200, "{\"id\":4,\"name\":\"S\"}");
        TestingbotLabSuite s = api.getLabSuite(4);
        assertRequest("GET", "/v1/labsuites/4");
        Assert.assertEquals("S", s.getName());
    }

    @Test
    public void deleteLabSuite() {
        respond(200, "");
        assertTrue(api.deleteLabSuite(4));
        assertRequest("DELETE", "/v1/labsuites/4");
    }

    @Test
    public void getLabSuiteTests() {
        respond(200, "{\"data\":[],\"meta\":{}}");
        api.getLabSuiteTests(4);
        assertRequest("GET", "/v1/labsuites/4/tests");
    }

    @Test
    public void addLabSuiteTests() {
        api.addLabSuiteTests(4, "10,11");
        assertRequest("POST", "/v1/labsuites/4/tests");
        assertTrue(bodyDecoded().contains("test_ids=10,11"));
    }

    @Test
    public void removeLabSuiteTest() {
        respond(200, "");
        assertTrue(api.removeLabSuiteTest(4, 2));
        assertRequest("DELETE", "/v1/labsuites/4/tests/2");
    }

    @Test
    public void getLabSuiteBrowsers() {
        respond(200, "[{\"name\":\"Chrome\"}]");
        api.getLabSuiteBrowsers(4);
        assertRequest("GET", "/v1/labsuites/4/browsers");
    }

    @Test
    public void setLabSuiteBrowsers() {
        api.setLabSuiteBrowsers(4, "1,2");
        assertRequest("POST", "/v1/labsuites/4/browsers");
        assertTrue(bodyDecoded().contains("browser_ids=1,2"));
    }

    @Test
    public void triggerLabSuite() {
        respond(200, "{\"success\":true,\"job_id\":5}");
        api.triggerLabSuite(4);
        assertRequest("POST", "/v1/labsuites/4/trigger");
    }
}
