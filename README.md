# The Buzz: PostgreSQL integration testing with Testcontainers

This repository is a hands-on tutorial for software engineering students. It shows how to run PostgreSQL in a disposable Docker container while Maven tests exercise a Java admin CLI.

The fictional app is **The Buzz**, an internal social platform. Its small schema has one table, `accounts`, with `id`, `name`, and `email` columns. The CLI supports four operations:

- `create-table`
- `drop-table`
- `add-account <name> <email>`
- `list-accounts`

The `drop-table` command makes the example useful for discussing test isolation: tests must never point destructive operations at a production database. SQLite could provide a lightweight local database, but using PostgreSQL in tests keeps the test database closer to the production database and avoids differences between database engines.

## Work through the tutorial

Read [tutorial.md](tutorial.md) and do the exercises in the `work/` Maven project. The application and CLI are already implemented. The integration test methods intentionally throw `UnsupportedOperationException` until you add Testcontainers and complete the tests. Run `mvn test` from `work/` as the final check.

The `solution/` directory is a separate, completed Maven project for comparison. Do not edit it while doing the exercise.

Both projects target Java 17. Docker must be installed and running to execute the completed tests.
