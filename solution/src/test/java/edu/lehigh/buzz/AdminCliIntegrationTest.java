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
