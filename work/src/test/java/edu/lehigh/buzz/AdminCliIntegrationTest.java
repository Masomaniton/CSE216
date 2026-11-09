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

// TODO: Add the @Testcontainers annotation above this class.
class AdminCliIntegrationTest {

    // TODO: Add the PostgreSQLContainer field from tutorial.md.

    private Connection connection;
    private AdminCli adminCli;
    private ByteArrayOutputStream capturedOutput;
    private PrintStream output;

    @BeforeEach
    void setUp() throws SQLException {
        // TODO: Initialize the connection and output.
        throw new UnsupportedOperationException("Initialize test setup.");
    }

    @AfterEach
    void tearDown() throws SQLException {
        // TODO: Clean up the table and close resources.
        throw new UnsupportedOperationException("Clean up test setup.");
    }

    @Test
    void createTableCommandCreatesAccountsTable() throws SQLException {
        // TODO: Complete this test using a PostgreSQL Testcontainer.
        throw new UnsupportedOperationException(
            "Test create-table with PostgreSQL."
        );
    }

    @Test
    void dropTableCommandDropsAccountsTable() throws SQLException {
        // TODO: Complete this test using a PostgreSQL Testcontainer.
        throw new UnsupportedOperationException(
            "Test drop-table with PostgreSQL."
        );
    }

    @Test
    void addAccountCommandInsertsAnAccount() throws SQLException {
        // TODO: Complete this test using a PostgreSQL Testcontainer.
        throw new UnsupportedOperationException(
            "Test add-account with PostgreSQL."
        );
    }

    @Test
    void listAccountsCommandDisplaysAccounts() throws SQLException {
        // TODO: Complete this test using a PostgreSQL Testcontainer.
        throw new UnsupportedOperationException(
            "Test list-accounts with PostgreSQL."
        );
    }

    private boolean accountsTableExists() throws SQLException {
        // TODO: Check PostgreSQL metadata for the accounts table.
        throw new UnsupportedOperationException("Check accounts table.");
    }

    private String outputText() {
        // TODO: Return the captured output as UTF-8 text.
        throw new UnsupportedOperationException("Read captured output.");
    }
}
