# Tutorial: test The Buzz with PostgreSQL and Testcontainers

In this tutorial, you will complete the integration tests for a small Java application. The application is The Buzz, an internal social platform with an `accounts` table containing `id`, `name`, and `email`. Its admin CLI can create the table, drop it, add an account, and list accounts.

The app code is already complete. You will work only in the `work/` Maven project, adding Testcontainers dependencies and replacing the unfinished integration tests. The separate `solution/` project contains a completed copy for comparison.

## The scenario

The Buzz production database is PostgreSQL, and it is currently unavailable. Tests that depend on that database would fail. Pointing tests at production is also unsafe: one of the admin commands drops the `accounts` table. A local SQLite database might be convenient, but using a different database engine can hide differences from PostgreSQL. The Twelve-Factor App recommends keeping development and production backing services similar; this exercise uses PostgreSQL for tests as well. See [Dev/prod parity](https://12factor.net/dev-prod-parity).

Testcontainers starts a temporary PostgreSQL database for the test run. The tests receive its connection URL and credentials, so they exercise real PostgreSQL without using the unavailable production database. The first run downloads the PostgreSQL image if Docker does not already have it.

## 1. Install and start Docker

Install Docker Desktop for your operating system, or Docker Engine on Linux, using [Docker’s download page](https://docs.docker.com/get-started/get-docker/). Follow the official instructions for [Mac](https://docs.docker.com/desktop/setup/install/mac-install/), [Windows](https://docs.docker.com/desktop/setup/install/windows-install/), or [Linux](https://docs.docker.com/engine/install/). Start Docker Desktop or the Docker Engine service before continuing.

Open a terminal in the repository, then check that Java, Maven, and Docker are available:

```sh
java --version
mvn --version
docker version
```

`docker version` should show both a client and a server section. If it cannot connect to the server, start Docker Desktop or the Docker Engine service and try again. You can also check that Docker can run an image:

```sh
docker run --rm hello-world
```

## 2. Run the unfinished tests

Change to the exercise project and run its tests:

```sh
cd work
mvn test
```

The four integration tests fail with `UnsupportedOperationException`. These exceptions mark the test code you will complete. The app’s database and CLI code is already implemented, and the starter tests do not connect to any database.

## 3. Add Testcontainers to Maven

Open `work/pom.xml`. Near the existing JUnit and PostgreSQL dependencies, find the comment marking the missing Testcontainers dependencies. Add these two test-scoped dependencies inside `<dependencies>`:

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <version>2.0.5</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId>
    <version>2.0.5</version>
    <scope>test</scope>
</dependency>
```

The PostgreSQL module provides the container class. The JUnit Jupiter module connects container startup and shutdown to JUnit’s test lifecycle. The PostgreSQL JDBC driver is already in the project; Testcontainers manages the server, while JDBC lets the application connect to it. The two Testcontainers modules are separate test dependencies in the [PostgreSQL module guide](https://java.testcontainers.org/modules/databases/postgres/) and [JUnit 5 integration guide](https://java.testcontainers.org/test_framework_integration/junit_5/).

## 4. Replace the unfinished integration tests

Open `work/src/test/java/edu/lehigh/buzz/AdminCliIntegrationTest.java`. Replace its contents with the following code:

```java
package edu.lehigh.buzz;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class AdminCliIntegrationTest {
    @Container
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:16.15-alpine3.24")
                    .withDatabaseName("buzz")
                    .withUsername("buzz")
                    .withPassword("buzz");

    private Connection connection;
    private AdminCli adminCli;
    private ByteArrayOutputStream capturedOutput;
    private PrintStream output;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
        adminCli = new AdminCli(connection);
        capturedOutput = new ByteArrayOutputStream();
        output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);

        new AccountsRepository(connection).dropTable();
    }

    @AfterEach
    void tearDown() throws SQLException {
        try {
            if (connection != null) {
                new AccountsRepository(connection).dropTable();
            }
        } finally {
            if (output != null) {
                output.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Test
    void createTableCommandCreatesAccountsTable() throws SQLException {
        adminCli.run(new String[] {"create-table"}, output);

        assertTrue(accountsTableExists());
        assertTrue(outputText().contains("Created accounts table."));
    }

    @Test
    void dropTableCommandDropsAccountsTable() throws SQLException {
        adminCli.run(new String[] {"create-table"}, output);
        assertTrue(accountsTableExists());

        adminCli.run(new String[] {"drop-table"}, output);

        assertFalse(accountsTableExists());
        assertTrue(outputText().contains("Dropped accounts table."));
    }

    @Test
    void addAccountCommandInsertsAnAccount() throws SQLException {
        adminCli.run(new String[] {"create-table"}, output);

        adminCli.run(new String[] {
                "add-account", "Ada Lovelace", "ada@buzz.example"
        }, output);

        List<Account> accounts = new AccountsRepository(connection).listAccounts();
        assertTrue(accounts.stream().anyMatch(account ->
                account.name().equals("Ada Lovelace")
                        && account.email().equals("ada@buzz.example")));
        assertTrue(outputText().contains("Created account:"));
    }

    @Test
    void listAccountsCommandDisplaysAccounts() throws SQLException {
        adminCli.run(new String[] {"create-table"}, output);
        adminCli.run(new String[] {
                "add-account", "Grace Hopper", "grace@buzz.example"
        }, output);
        capturedOutput.reset();

        adminCli.run(new String[] {"list-accounts"}, output);

        assertTrue(outputText().contains("Grace Hopper"));
        assertTrue(outputText().contains("grace@buzz.example"));
    }

    private boolean accountsTableExists() throws SQLException {
        try (ResultSet tables = connection.getMetaData()
                .getTables(null, "public", "accounts", new String[] {"TABLE"})) {
            return tables.next();
        }
    }

    private String outputText() {
        return capturedOutput.toString(StandardCharsets.UTF_8);
    }
}
```

Here is what the test setup does:

1. `@Testcontainers` enables Testcontainers’ JUnit extension. The static `@Container` field is started once for this test class and stopped after the class finishes.
2. `PostgreSQLContainer` starts PostgreSQL 16.15. The image tag includes the PostgreSQL patch version and Alpine version so repeated runs use the same image tag. `getJdbcUrl()`, `getUsername()`, and `getPassword()` provide the connection details for that temporary database. The test never uses production credentials or a production URL.
3. `setUp()` opens one connection before each test and removes any leftover `accounts` table. `tearDown()` removes the table again and closes the connection. This lets every test exercise create/drop operations from a predictable starting state.
4. Each test invokes the same `AdminCli` commands a person would use. The create and drop tests inspect PostgreSQL’s metadata; the add and list tests verify that a row is written and displayed.

The table cleanup is especially important here: `drop-table` is deliberately destructive. It is safe because the test connection comes from the disposable container.

## 5. Run the completed tests

From the `work/` directory, run:

```sh
mvn test
```

On the first run, Maven downloads the Java dependencies and Docker downloads the `postgres:16.15-alpine3.24` image. Testcontainers starts the PostgreSQL server, Maven runs the four integration tests, and the container is removed after the test class finishes. Later runs can reuse the downloaded image.

## 6. Compare your work with the reference

From the repository root, inspect your changes with:

```sh
git diff -- work/pom.xml work/src/test/java/edu/lehigh/buzz/AdminCliIntegrationTest.java
```

The completed Maven project is in `solution/`. To compare the two integration test files directly:

```sh
diff -u work/src/test/java/edu/lehigh/buzz/AdminCliIntegrationTest.java \
  solution/src/test/java/edu/lehigh/buzz/AdminCliIntegrationTest.java
```

You can also run the completed reference project with `mvn test` from `solution/` if you want to confirm the end state before finishing the exercise.

The exercise is complete when `mvn test` passes from `work/`.

## What to carry into another project

Testcontainers is useful when tests depend on a service such as PostgreSQL and you want each test run to use a known, temporary instance. Keep the test database engine aligned with production, use the connection settings supplied by the container, and isolate or clean up state between tests. This approach is especially helpful when tests include destructive operations.
