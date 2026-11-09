package edu.lehigh.buzz;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Main {

    private Main() {}

    public static void main(String[] arguments) throws SQLException {
        String url = setting(
            "BUZZ_JDBC_URL",
            "jdbc:postgresql://localhost:5432/buzz"
        );
        String username = setting("BUZZ_DB_USER", "buzz");
        String password = setting("BUZZ_DB_PASSWORD", "buzz");

        try (
            Connection connection = DriverManager.getConnection(
                url,
                username,
                password
            )
        ) {
            new AdminCli(connection).run(arguments, System.out);
        }
    }

    private static String setting(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
