# SIPMS Backend

A multi-module Java (Maven) backend for the Smart Inventory and Procurement Module system.

This repository contains several Spring Boot modules (services and libraries) that together implement the backend for SIPMS.

> Assumptions: the project targets Java 21 (adjust the JDK version if your pom.xml requires a different one). If your project uses a different license or Java version, update the sections below accordingly.

Quick links
- Root pom: `pom.xml`
- Wrapper: use `./mvnw` (bundled Maven wrapper) or `mvn` if you have Maven installed

Table of contents
- Project overview
- Modules
- Prerequisites
- Build
- Run
- Test
- Troubleshooting
- Contributing
- License & Contact

## Project overview

SIPMS Backend is organized as a Maven multi-module project. Each module focuses on a bounded domain or shared functionality (APIs, background jobs, common utilities, etc.). Modules are intended to be run independently in development and packaged together in CI/CD pipelines.

## Modules (short descriptions)

- `sipms-app` — main application / API entrypoint (Spring Boot service)
- `sipms-analytics` — analytics and batch processing jobs
- `sipms-common` — shared utilities, DTOs, exceptions, and constants
- `db-migration` — database migration scripts (Flyway/Liquibase; located under `db-migration`)
- `sipms-branch` — branch management domain
- `sipms-crm` — customer relationship management features
- `sipms-finance` — finance and billing services
- `sipms-hr` — human resources services
- `sipms-inventory` — inventory and stock management
- `sipms-logistics` — logistics, shipping, and delivery workflows
- `sipms-management` — admin/management APIs
- `sipms-notification` — notifications (email/SMS/push)
- `sipms-order` — order processing and lifecycle
- `sipms-procurement` — procurement and supplier flows
- `sipms-sales` — sales domain services


## Prerequisites

- Java 17+ (adjust to your project's configured Java version)
    - If your POM/CI targets a different Java version (e.g. Java 21), update this file and CI configs accordingly.
- Maven 3.6+ (or use the included Maven wrapper: `./mvnw`)
- A running database (PostgreSQL/MySQL/etc.) for modules that require it. Provide the appropriate JDBC URL and credentials via environment variables or application profiles.
- Optional: Docker and Docker Compose if you prefer running DB and other infra locally.

## Build

From the repository root:

- Build everything (skip tests to speed up):

  ```bash
  ./mvnw -B -DskipTests clean install
  ```

- Build everything (including tests):

  ```bash
  ./mvnw -B clean install
  ```

- Build a single module and its dependencies (example: `sipms-app`):

  ```bash
  ./mvnw -pl sipms-app -am package
  ```

- Parallel build (faster on multicore machines):

  ```bash
  ./mvnw -T 1C package
  ```

## Run

Many modules are Spring Boot applications and can be run with `spring-boot:run` or by running the packaged jar.

Examples (from repository root):

- Run `sipms-app` via Maven:

  ```bash
  ./mvnw -pl sipms-app spring-boot:run
  ```

- Run packaged jar (after building):

  ```bash
  java -jar sipms-app/target/sipms-app-*.jar
  ```

- Run with a Spring profile (example `local`):

  ```bash
  SPRING_PROFILES_ACTIVE=local ./mvnw -pl sipms-app spring-boot:run
  ```

- Run other modules similarly by replacing the module name (e.g. `sipms-analytics`).

## Test

- Run all unit tests:

  ```bash
  ./mvnw test
  ```

- Run tests for a specific module (example: `sipms-procurement`):

  ```bash
  ./mvnw -pl sipms-procurement test
  ```

- Run integration tests (if configured) or the full verification lifecycle:

  ```bash
  ./mvnw verify
  ```

## Troubleshooting

- Database migration failures:
    - Check the `db-migration` module logs and the DB connection URL/credentials.
    - Ensure the DB is reachable and the user has appropriate privileges.

- Port conflicts:
    - If a Spring Boot service fails to start due to a port conflict, set `server.port` in `application-*.yml` or pass `-Dserver.port=XXXX`.

- Build memory issues:
    - For large multi-module builds you may need to increase Maven/Java heap or build modules individually:
      ```bash
      ./mvnw -pl <module> -am package
      ```

- Dependency/version conflicts:
    - Use `./mvnw dependency:tree` to inspect conflicts and the effective POM.

## Contributing

- Use feature branches: `feature/<short-description>`
- Keep changes small and focused.
- Run tests locally before opening a PR.
- Follow the project's coding style and formatting rules (IDE settings or formatter configuration may be checked in the repo).
- CI will run a full build; ensure local `./mvnw -B -DskipTests clean install` passes before pushing.

## License & Contact

- License: (update this to the project's actual license, e.g. Apache-2.0 or MIT)
- Maintainer: Update this README with your team or maintainer contact details (email, Slack, etc.)

## Appendix — Useful Maven commands

- Show effective POM for a module:

  ```bash
  ./mvnw -pl sipms-app help:effective-pom
  ```

- Show dependency tree for a module:

  ```bash
  ./mvnw -pl sipms-app dependency:tree
  ```

- Clean a module:

  ```bash
  ./mvnw -pl sipms-app clean
  ```

---

Notes:
- This README assumes Java 21 as the default target. If your project uses a different Java version (for example Java 25) or a different license, update the above sections accordingly.