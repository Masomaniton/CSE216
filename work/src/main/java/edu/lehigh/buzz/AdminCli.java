package edu.lehigh.buzz;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.SQLException;

public final class AdminCli {
    private static final String USAGE = "Usage: create-table | drop-table | "
            + "add-account <name> <email> | list-accounts";

    private final AccountsRepository accounts;

    public AdminCli(Connection connection) {
        accounts = new AccountsRepository(connection);
    }

    public void run(String[] arguments, PrintStream output) throws SQLException {
        if (arguments == null || arguments.length == 0) {
            throw new IllegalArgumentException(USAGE);
        }

        switch (arguments[0]) {
            case "create-table" -> {
                requireArgumentCount(arguments, 1);
                accounts.createTable();
                output.println("Created accounts table.");
            }
            case "drop-table" -> {
                requireArgumentCount(arguments, 1);
                accounts.dropTable();
                output.println("Dropped accounts table.");
            }
            case "add-account" -> {
                requireArgumentCount(arguments, 3);
                Account account = accounts.createAccount(arguments[1], arguments[2]);
                output.printf("Created account: %d | %s | %s%n",
                        account.id(), account.name(), account.email());
            }
            case "list-accounts" -> {
                requireArgumentCount(arguments, 1);
                var rows = accounts.listAccounts();
                if (rows.isEmpty()) {
                    output.println("No accounts found.");
                } else {
                    rows.forEach(account -> output.printf("%d | %s | %s%n",
                            account.id(), account.name(), account.email()));
                }
            }
            default -> throw new IllegalArgumentException(
                    "Unknown command: " + arguments[0] + System.lineSeparator() + USAGE);
        }
    }

    private static void requireArgumentCount(String[] arguments, int expected) {
        if (arguments.length != expected) {
            throw new IllegalArgumentException(USAGE);
        }
    }
}
