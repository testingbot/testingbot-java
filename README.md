[![Maven Central](https://img.shields.io/maven-central/v/com.testingbot/testingbotrest.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/com.testingbot/testingbotrest)
[![Javadoc](https://javadoc.io/badge2/com.testingbot/testingbotrest/javadoc.svg)](https://javadoc.io/doc/com.testingbot/testingbotrest)
[![Tests](https://github.com/testingbot/testingbot-java/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/testingbot/testingbot-java/actions/workflows/test.yml)
[![CodeQL](https://github.com/testingbot/testingbot-java/actions/workflows/codeql-analysis.yml/badge.svg?branch=master)](https://github.com/testingbot/testingbot-java/actions/workflows/codeql-analysis.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Java](https://img.shields.io/badge/Java-1.7%2B-blue.svg)](https://www.oracle.com/java/)

TestingBotREST Java
====================

A Java client for TestingBot.com's REST API.

Using this client you can interact with the TestingBot API; update Test info, including pass/fail status and other metadata.

<https://testingbot.com/support/api>

Getting Started
----------------

Sign up for an account on TestingBot.com - in the member area you will find the `key` and `secret` required to authenticate with the TestingBot API.

```java
TestingbotREST restApi = new TestingbotREST("key", "secret");
```

*All API methods can throw these exceptions:*

```java
TestingbotApiException(String json)
TestingbotUnauthorizedException
```

### getBrowsers
Retrieves collection of available browsers
<https://testingbot.com/support/api>


```java
ArrayList<TestingbotBrowser> devices = restApi.getBrowsers();
```

### getDevices
Retrieves collection of available devices
<https://testingbot.com/support/api#devices>


```java
ArrayList<TestingbotDevice> devices = restApi.getDevices();
```

### getAvailableDevices
Retrieves collection of available devices
<https://testingbot.com/support/api#available-devices>


```java
ArrayList<TestingbotDevice> devices = restApi.getAvailableDevices();
```

### getDevice
Retrieves information for a specific device
<https://testingbot.com/support/api#devicedetails>


```java
TestingbotDevice device = restApi.getDevice(int deviceId);
```

### updateTest
Update meta-data for a test
<https://testingbot.com/support/api#updatetest>

- `String` status_message
- `boolean` success
- `String` build
- `String` name


```java
boolean success = restApi.updateTest(TestingbotTest test);
boolean success = restApi.updateTest(String sessionId, Map<String, Object> details);
```

### stopTest
Stops a running test
<https://testingbot.com/support/api#stoptest>


```java
boolean success = restApi.stopTest(String sessionId);
```

### deleteTest
Deletes a test from TestingBot
<https://testingbot.com/support/api#deletetest>


```java
boolean success = restApi.deleteTest(String sessionId);
```

### getTest
Retrieves information regarding a test
<https://testingbot.com/support/api#singletest>


```java
TestingbotTest test = restApi.getTest(String sessionId);
```

### getTests
Retrieves a collection of tests
<https://testingbot.com/support/api#tests>


```java
TestingbotTestCollection test = restApi.getTests(int offset, int count);
```

### getBuilds
Retrieves a collection of builds
<https://testingbot.com/support/api#builds>


```java
TestingbotBuildCollection builds = restApi.getBuilds(int offset, int count);
```

### getTestsForBuild
Retrieves a collection of tests for a specific build
<https://testingbot.com/support/api#singlebuild>


```java
TestingbotTestBuildCollection tests = restApi.getTestsForBuild(String buildIdentifier);
```

### getUserConfig
Retrieves information about the current user
<https://testingbot.com/support/api#user>


```java
TestingbotUser user = restApi.getUserInfo();
```

### updateUserConfig
Updates information about the current user
<https://testingbot.com/support/api#useredit>


```java
TestingbotUser user = restApi.updateUserInfo(TestingBotUser);
```

### getTunnels
Retrieves tunnels for the current user
<https://testingbot.com/support/api#apitunnellist>


```java
ArrayList<TestingbotTunnel> tunnels = restApi.getTunnels();
```

### deleteTunnel
Deletes/stops a specific tunnel for the current user
<https://testingbot.com/support/api#apitunneldelete>


```java
boolean success = restApi.deleteTunnel(String tunnelID);
```

### uploadToStorage - Local File
Uploads a local file to TestingBot Storage
<https://testingbot.com/support/api#upload>


```java
TestingbotStorageUploadResponse uploadResponse = restApi.uploadToStorage(File file);
```

### uploadToStorage - Remote File
Uploads a remote file to TestingBot Storage
<https://testingbot.com/support/api#upload>


```java
TestingbotStorageUploadResponse uploadResponse = restApi.uploadToStorage(String fileUrl);
```

### getStorageFile
Retrieves meta-data from a previously stored file
<https://testingbot.com/support/api#uploadfile>


```java
TestingBotStorageFile storedFile = restApi.getStorageFile(String appUrl);
```

### getStorageFiles
Retrieves meta-data from previously stored files
<https://testingbot.com/support/api#filelist>


```java
TestingBotStorageFileCollection fileList = restApi.getStorageFiles(int offset, int count);
```

### deleteStorageFile
Deletes a file previously stored in TestingBot Storage
<https://testingbot.com/support/api#filedelete>


```java
boolean success = restApi.deleteStorageFile(String appUrl);
```

### getAuthenticationHash
Calculates the authenticationHash necessary to share tests
<https://testingbot.com/support/other/sharing>


```java
String hash = restApi.getAuthenticationHash(String identifier);
```

### Closing the client
`TestingbotREST` holds a pooled HTTP client and implements `Closeable`. Call `close()` when you are done (or use try-with-resources) to release connections.

```java
try (TestingbotREST restApi = new TestingbotREST("key", "secret")) {
    restApi.getUserInfo();
}
```

Tests
-----

### getTests with filters
Retrieves a collection of tests with optional server-side filters (`since`, `browser_id`, `group`, `build`, `skip_fields`).

```java
Map<String, String> filters = new HashMap<>();
filters.put("group", "smoke");
TestingbotTestCollection tests = restApi.getTests(int offset, int count, filters);
```

### createTest
Creates a new test.

```java
boolean success = restApi.createTest(Map<String, Object> testFields);
```

Devices
-------

### getDevices with filters
Retrieves devices filtered by platform (Android, iOS, REAL_ANDROID, REAL_IOS) and web capability.

```java
List<TestingbotDevice> devices = restApi.getDevices(String platform, Boolean web);
```

### getAvailableDevices
Retrieves the currently available real devices.

```java
List<TestingbotDevice> devices = restApi.getAvailableDevices();
```

Tunnels
-------

### getTunnel
Gets the currently active tunnel, or a specific tunnel by id.

```java
TestingbotTunnel tunnel = restApi.getTunnel();
TestingbotTunnel tunnel = restApi.getTunnel(int tunnelId);
```

### createTunnel
Boots a new tunnel.

```java
TestingbotTunnel tunnel = restApi.createTunnel();
```

### deleteTunnel / isTunnelAlive
Stops the active tunnel, or checks whether the tunnel is alive.

```java
boolean success = restApi.deleteTunnel();
boolean alive = restApi.isTunnelAlive();
```

Account & configuration
-----------------------

### getJob
Gets the status of a job (e.g. a Codeless test run).

```java
TestingbotJob job = restApi.getJob(String jobId);
```

### getUserKeys / getIpRanges
Retrieves your API keys, or the TestingBot IP ranges (raw JSON).

```java
com.google.gson.JsonElement keys = restApi.getUserKeys();
com.google.gson.JsonElement ranges = restApi.getIpRanges();
```

Team management
---------------

```java
TestingbotTeam team = restApi.getTeam();
TestingbotTeamMemberCollection members = restApi.getTeamMembers();
TestingbotTeamMember member = restApi.createTeamMember(Map<String, Object> params);
TestingbotTeamMember member = restApi.getTeamMember(int userId);
TestingbotTeamMember member = restApi.updateTeamMember(int userId, Map<String, Object> params);
com.google.gson.JsonElement clientKey = restApi.getTeamMemberClientKey(int userId);
TestingbotTeamCredentialReset reset = restApi.resetTeamMemberKeys(int userId);
```

Screenshots
-----------

```java
TestingbotScreenshotCollection screenshots = restApi.getScreenshots();
TestingbotScreenshot batch = restApi.createScreenshots(Map<String, Object> params); // url, resolution, browsers (List)
TestingbotScreenshot batch = restApi.getScreenshot(int screenshotId);
```

Manual sessions
---------------

```java
boolean success = restApi.updateManualSession(int sessionId, Map<String, Object> fields);
boolean success = restApi.pingManualSession(List<Integer> sessionIds);
```

Codeless Lab
------------

```java
TestingbotLabTestCollection labTests = restApi.getLabTests();
TestingbotLabCreateAck created = restApi.createLabTest(Map<String, Object> testFields);
TestingbotLabTest labTest = restApi.getLabTest(int labTestId);
boolean success = restApi.updateLabTest(int labTestId, Map<String, Object> testFields);
boolean success = restApi.deleteLabTest(int labTestId);
TestingbotLabTestStepCollection steps = restApi.getLabTestSteps(int labTestId);
boolean success = restApi.setLabTestSteps(int labTestId, List<String> steps);
List<TestingbotBrowser> browsers = restApi.getLabTestBrowsers(int labTestId);
boolean success = restApi.setLabTestBrowsers(int labTestId, String browserIds);
TestingbotLabRunAck run = restApi.triggerLabTest(int labTestId);
boolean success = restApi.stopLabTest(int labTestId);
boolean success = restApi.addLabTestAlert(int labTestId, Map<String, Object> params); // kind, level, content
boolean success = restApi.updateLabTestAlert(int labTestId, Map<String, Object> params);
boolean success = restApi.createLabTestReport(int labTestId, Map<String, Object> params);
boolean success = restApi.updateLabTestReport(int labTestId, Map<String, Object> params);
boolean success = restApi.scheduleLabTest(int labTestId, Map<String, Object> params); // type, day, hour, cronFormat
TestingbotLabRunAck run = restApi.triggerAllLabTests();
```

Codeless Lab suites
-------------------

```java
TestingbotLabSuiteCollection suites = restApi.getLabSuites();
TestingbotLabSuiteCreateAck created = restApi.createLabSuite(Map<String, Object> suiteFields);
TestingbotLabSuite suite = restApi.getLabSuite(int suiteId);
boolean success = restApi.deleteLabSuite(int suiteId);
TestingbotLabTestCollection tests = restApi.getLabSuiteTests(int suiteId);
boolean success = restApi.addLabSuiteTests(int suiteId, String testIds);
boolean success = restApi.removeLabSuiteTest(int suiteId, int testId);
List<TestingbotBrowser> browsers = restApi.getLabSuiteBrowsers(int suiteId);
boolean success = restApi.setLabSuiteBrowsers(int suiteId, String browserIds);
TestingbotLabRunAck run = restApi.triggerLabSuite(int suiteId);
```

Test
-----

`TestingbotRestOfflineTest` (auth-hash + Gson mapping) and `TestingbotRestMockTest` (every endpoint, against a local `HttpServer`) run with no credentials:

```java
mvn -Dtest='TestingbotRestOfflineTest,TestingbotRestMockTest' test
```

`TestingBotRestTest` runs against the live API and needs your key and secret:

```java
mvn -DTB_KEY=... -DTB_SECRET=... test
```

Maven
-----

```xml
<dependencies>
  <dependency>
    <groupId>com.testingbot</groupId>
    <artifactId>testingbotrest</artifactId>
    <version>1.0.8</version>
    <scope>test</scope>
  </dependency>
</dependencies
```
