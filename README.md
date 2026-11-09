### overview and tools

This is a tutorial for how to use docker testcontainer to run a PostgreSQL server in order to have highly reproducible unit tests. This tutorial is for my classmates of software engineering. This tutorial uses Maven. The job of the tutorial taker is to "fill in the blanks" at every place the code comments indicate missing code. All code not relevant to the testcontainer is already written. The tutorial taker will have to update the `pom.xml`. The tutorial taker should come away with enough understanding to use this technique in other projects.

### expected work and `tutorial.md`

The tutorial taker will follow along with `tutorial.md`. This file tells the tutorial taker every required task, including downloading docker and every missing place in the code to complete, in order. Every missing piece of code is included as part of `tutorial.md` in a triple back tick block. That is, the tutorial taker is not expected to write any code. For each piece of code, there is enough explanation for someone competent in Java to fully understand without referencing documentation.

### motivation and completion criteria

In the fictional scenario of the tutorial, the production database is down, and so calling `mvn package` and `java -jar target/...`. Initially, calling `mvn test` should result in unimplemented exceptions. The tutorial explains how, while the user could use a local SQLite file, this would fail the dev/prod parity principle of the 12-factor app, motitivating the need for docker testcontainers. The tutorial taker is complete when `mvn test` passes. This tutorial includes completed versions of every file so that the tutorial taker can compare to make sure everything matches. The tutorial explains how to do this with git diff, which is useful in case the tutorial taker wants to verify partial work.

### scope of the Web App

The tutorial should have a purposely simplified web app. The application is an internal fictional social media platform called The Buzz. The simplified schema only has a single table. This table is called `accounts` and has `id`, `name`, and `email` fields. For the sake of simplicity, this tutorial omits the web apps `frontend` and `backend`, only including an `admin-cli`. The tutorial explains that using the prod DB for testing is particular important for the `admin-cli` because we would like to test functionality which includes dropping tables. The `admin-cli` should enable functions to create the `accounts` table, drop the `accounts` table, create a row in the `accounts` tables, and view the `acounts` table.
