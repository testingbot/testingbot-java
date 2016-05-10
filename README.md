TestingBotREST Java
==============

A java client for TestingBot.com's REST API.

Using this client you can update Test info, including pass/fail status and other metadata.

<https://testingbot.com/support/api>

Usage
-----

```java
TestingbotREST r = new TestingbotREST("key", "secret");
String tests = r.getTests();
```


Maven
-----

```xml
<dependencies>
  <dependency>
    <groupId>com.testingbot</groupId>
    <artifactId>testingbotrest</artifactId>
    <version>1.0.1</version>
    <scope>test</scope>
  </dependency>
</dependencies
```
