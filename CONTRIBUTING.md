# Contributing to testingbot-java

Thanks for considering a contribution. This is a small library wrapping
the TestingBot REST API; PRs that improve correctness, add missing
endpoints, or tighten the documentation are welcome.

## Building locally

The project targets **Java 1.7 bytecode** so it remains usable by older
consumers, but it requires a JDK that still supports `-target 1.7`
(JDK 8, 11, or 17 — JDK 20+ dropped support).

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 11)   # macOS
mvn install -Dgpg.skip=true -B -V                  # what CI runs
```

## Running tests

There are three test classes:

- **`TestingbotRestOfflineTest`** — unit tests; no credentials needed.
- **`TestingbotRestMockTest`** — every endpoint, against a local
  `HttpServer`; no credentials needed.
- **`TestingBotRestTest`** — **live integration tests** against the real
  TestingBot API. Requires `TB_KEY` and `TB_SECRET` environment
  variables for a valid TestingBot account, and depends on real account
  state (e.g. `testGetTests` expects ≥10 existing tests).

Run only the offline tests:

```bash
mvn -Dtest='TestingbotRestOfflineTest,TestingbotRestMockTest' \
    -DfailIfNoTests=false test
```

Run a single test method:

```bash
mvn test -Dtest=TestingBotRestTest#testGetTest
```

## Coding guidelines

- **Keep new code Java 1.7-compatible** (try-with-resources is fine; no
  `var`, no `Optional`, no `java.util.Base64`, no Map.of).
- **SpotBugs** runs at the compile phase with effort=Max / threshold=Low
  and **fails the build** on findings. Suppressions live in
  `findbugs-exclude.xml` — only add a `<Match>` for genuine false
  positives, not to mask new issues.
- **Backward compatibility.** This is a published Maven Central library;
  do not change existing public method signatures, field types, or model
  field names. Add new methods/fields rather than altering old ones.
- **Tests for every new endpoint.** New public methods should land with
  a matching test in `TestingbotRestMockTest` asserting the HTTP method,
  path, and any form/query encoding.

## Submitting a PR

1. Open an issue first for anything beyond a small fix, so we can agree
   on the approach.
2. Keep PRs focused — one logical change per PR.
3. Update `README.md` if you add or change public methods.

## Releasing (maintainers only)

Releases are tag-driven via `.github/workflows/release.yml`. Push a tag
(`v1.x.y` or `testingbotrest-1.x.y`) and the workflow sets the version,
runs the live test suite, signs and publishes to Maven Central via the
Sonatype Central Portal, and creates a GitHub Release with the jars
attached. See `CLAUDE.md` for the list of required GitHub secrets.
