[![Build Status](https://travis-ci.org/testingbot/testingbot-java.svg?branch=master)](https://travis-ci.org/testingbot/testingbot-java)

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
ArrayList<TestingbotBrowser> browsers = restApi.getBrowsers();
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
TestingbotTest test = restApi.getTests(int offset, int count);
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

### getAuthenticationHash
Calculates the authenticationHash necessary to share tests
<https://testingbot.com/support/other/sharing>


```java
String hash = restApi.getAuthenticationHash(String identifier);
```

Test
-----

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
    <version>1.0.2</version>
    <scope>test</scope>
  </dependency>
</dependencies
```
