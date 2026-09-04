---
sessionId: session-260904-232351-o8zm
---

# Requirements

### Overview & Goals
The goal is to design and implement an enterprise-grade CI/CD and Database Testing extension for the Java REST API test automation repository (Java 21, REST Assured, JUnit 5, Allure). 

This plan addresses two core enterprise capabilities:
1. **On-Demand Modular CI/CD Pipelines (GitHub Actions):** Build automated workflows for Pull Request validation, on-demand parameterized test execution, regression runs with Allure report publication to GitHub Pages, and security dependency scanning. All pipeline runs are triggered strictly **on-demand** (push, PR, manual dispatch) without active background cron schedules.
2. **PostgreSQL Database Testing & Hermetic Testcontainers:** Establish direct database connectivity, automated schema initialization and seed data provisioning, connection pooling via HikariCP, transparent Testcontainers PostgreSQL management for isolated test runs, and sample database integration tests validating data integrity.

### Scope
- **In Scope:**
  - **PostgreSQL Database Infrastructure:**
    - HikariCP connection pooling, PostgreSQL JDBC driver, and Owner config integration (`db.url`, `db.user`, `db.password`, etc.).
    - Testcontainers PostgreSQL container integration for zero-friction hermetic execution in local and CI environments.
    - SQL schema definitions (`init-schema.sql`) and sample seed datasets (`seed-data.sql`) for products, users, and orders.
    - `DatabaseManager` / `DbClient` helper for executing queries, updates, table resets, and mapping results to strongly typed models.
    - Sample database integration tests (`DatabaseIntegrationTest`, `UserDatabaseTest`) with Allure step reporting and AssertJ assertions.
  - **On-Demand GitHub Actions Workflows:**
    - **PR Validation (`.github/workflows/pr-checks.yml`):** Fast compile verification, smoke test execution, and Step Summary reporting.
    - **Regression & Allure Reporting (`.github/workflows/regression-and-reporting.yml`):** On-push to `main` and on-demand `workflow_dispatch` execution of the full regression suite (API + DB tests), generating historical Allure reports published to GitHub Pages (cron schedule disabled/on-demand).
    - **Parameterized Manual Runner (`.github/workflows/manual-test-run.yml`):** On-demand execution with environment, tag filter (`smoke`, `regression`, `db`, `products`, `users`), thread parallelism, and custom URL overrides.
    - **Security & Dependency Audit (`.github/workflows/security-and-dependency-scan.yml`):** On-demand and PR-triggered scanning of Maven dependencies for CVE vulnerabilities.
  - **Documentation & Configuration (`README.md`, `config.properties`):**
    - Database configuration guide, local and containerized execution instructions, workflow documentation, and status badges.
- **Out of Scope:**
  - Active background cron triggers that consume runner minutes automatically without user request.
  - Modifying external public DummyJSON endpoints.

### User Stories
- **As a QA Automation Engineer**, I want direct database access to verify that test data states match expected database records without relying solely on API responses.
- **As a QA Engineer**, I want tests to automatically spin up a clean PostgreSQL container via Testcontainers so that tests run hermetically without requiring a pre-existing local database.
- **As a Developer**, I want automated PR checks that run fast compile and smoke tests on every pull request to ensure code quality before merging.
- **As a QA Lead**, I want on-demand regression test execution and interactive Allure reports deployed to GitHub Pages with preserved historical trends.
- **As an Engineer**, I want to trigger customized test runs on-demand with specific tag filters (e.g., `-Dgroups="db"`) through GitHub Actions UI.

### Functional Requirements
1. **PostgreSQL Connection & Testcontainers Management:**
   - Provide `ProjectConfig` properties for database connection settings (`db.url`, `db.user`, `db.password`, `db.pool.size`, `db.use.testcontainers`).
   - When running against external DB or Testcontainers, `DatabaseManager` initializes a `HikariDataSource` connection pool.
   - When `db.use.testcontainers=true` (default for hermetic test execution), Testcontainers starts a `postgres:16-alpine` container, exposes the dynamic JDBC URL, and initializes the schema.
2. **Database Schema & Seed Data Initialization:**
   - Create SQL schema scripts defining tables: `users` (id, username, email, first_name, last_name, role, status), `products` (id, title, price, category, stock, created_at), and `orders` (id, user_id, product_id, quantity, total_amount, status).
   - Provide SQL seed data script populating initial sample datasets for test assertions.
   - Provide helper methods in `DatabaseManager` to reset tables, insert fixtures, and query typed records.
3. **Database Integration Test Suite:**
   - Implement `DatabaseIntegrationTest` verifying database connectivity, record querying, CRUD verification, and cross-checking data consistency.
   - Implement custom DB assertions with AssertJ (`assertThatUserDb(user).existsInDb()`, `assertThatProductDb(product)...`).
   - Log all database queries and validation checks into Allure report steps (`@Step("DB Query: ...")`).
4. **On-Demand GitHub Actions CI/CD Workflows:**
   - **PR Checks:** Triggered on PRs to `main`/`master`, runs compile check and smoke tests with concurrency cancellation.
   - **Regression & Allure Reporting:** Triggered on push to `main` and on-demand `workflow_dispatch` (no automatic cron). Runs full test suite (API + DB with Testcontainers on GitHub runner's Docker daemon), generates Allure Report with historical trend merging from `gh-pages`, and deploys to GitHub Pages.
   - **Manual Dynamic Runner:** Parameterized `workflow_dispatch` accepting `environment`, `test_tags` (`regression`, `smoke`, `db`, `products`, `users`), `threads`, and `base_url`.
   - **Security Scan:** On-demand `workflow_dispatch` and PR trigger when `pom.xml` changes.

### Non-Functional & Enterprise Best Practices
- **Hermetic & Isolated Testing:** Zero external state pollution; Testcontainers ensures a clean database per test run.
- **Connection Pool Efficiency:** HikariCP connection pooling ensures fast query execution and clean resource teardown.
- **Least Privilege CI Permissions:** Explicit permissions (`contents: read`, `pages: write`, `id-token: write`) in all workflows.
- **Maven Dependency Caching:** `actions/setup-java@v4` with `cache: 'maven'` minimizes build times in GitHub Actions.
- **Fail-Safe Artifact Retention:** Surefire XML and Allure results archived on all runs (`if: always()`).

# Technical Design

### Current Implementation
- Java 21, REST Assured 5.5.0, JUnit 5.11.0, Allure 2.29.0, Maven Surefire with AspectJ Weaver.
- Tests organized with JUnit 5 tags: `@Tag("regression")`, `@Tag("products")`, `@Tag("users")`.
- Configuration managed via Owner library (`ProjectConfig`) supporting CLI system property overrides (`-Dbase.url=...`).
- Currently no `.github/` workflows directory and no database integration modules exist.

### Key Decisions
1. **HikariCP Connection Pool + Lightweight JDBC Wrapper:**
   - Use `HikariCP` for high-performance connection pooling with a clean `DatabaseManager` utility executing SQL queries and mapping results.
   - *Rationale:* Lightweight, zero unnecessary ORM overhead, fast execution for test assertions, fully compatible with Allure step logging.
2. **Testcontainers PostgreSQL Integration:**
   - Use `testcontainers-postgresql` with JUnit 5 extension support for ephemeral database provisioning.
   - *Rationale:* Allows tests to run anywhere (local dev machines, developer laptops without local Postgres installed, and GitHub Actions runners) with 100% isolation.
3. **On-Demand Modular CI/CD Architecture:**
   - GitHub Actions workflows configured strictly for event-driven (PR, Push) and on-demand (`workflow_dispatch`) execution without active cron jobs.
   - *Rationale:* Conforms to user requirement to avoid automatic background scheduled runs while keeping full manual on-demand execution capability.
4. **GitHub Pages Allure Reporting with Historical Trends:**
   - Deploy generated Allure reports to `gh-pages` branch while pulling previous test history for trend analysis.
   - *Rationale:* Provides rich interactive visibility into test health and execution trends without requiring external SaaS dashboards.

### Architecture Diagram

```mermaid
graph TD
    subgraph GitHub Actions CI/CD (On-Demand)
        PR[PR Event] --> W1[pr-checks.yml]
        Push[Push to Main] --> W2[regression-and-reporting.yml]
        Manual[Manual Dispatch] --> W3[manual-test-run.yml]
    end

    subgraph Test Execution Layer
        W1 & W2 & W3 --> JUnit[JUnit 5 Test Suite]
        JUnit --> API_Tests[REST API Tests (ProductCrud, UserCrud)]
        JUnit --> DB_Tests[Database Integration Tests]
    end

    subgraph Service & Data Access Layer
        API_Tests --> RestAssured[REST Assured Client Layer]
        DB_Tests --> DbManager[DatabaseManager / HikariCP]
        RestAssured --> DummyJSON[DummyJSON Remote API]
        DbManager --> PG[(PostgreSQL / Testcontainers)]
    end

    subgraph Reporting & Artifacts
        JUnit --> AllureResults[Allure Results & Steps]
        AllureResults --> AllureReport[Allure HTML Report + Trends]
        AllureReport --> GhPages[GitHub Pages Deployment]
    end
```

### Proposed File Structure
```
.github/
└── workflows/
    ├── pr-checks.yml                     # PR validation (Compile & Smoke tests)
    ├── regression-and-reporting.yml      # On-demand/Push regression + Allure gh-pages
    ├── manual-test-run.yml               # On-demand parameterized runner
    └── security-and-dependency-scan.yml  # On-demand/PR dependency security audit
src/
├── main/java/org/example/api/
│   ├── config/
│   │   ├── ProjectConfig.java            # Extended with DB properties
│   │   └── ConfigManager.java
│   └── database/
│       ├── DatabaseManager.java          # HikariCP DataSource & query executor
│       ├── TestcontainersManager.java    # Singleton Testcontainers PostgreSQL lifecycle
│       └── models/                       # Typed DB entity records (UserRecord, ProductRecord)
└── test/
    ├── java/org/example/api/tests/
    │   ├── BaseTest.java                 # Base test class with DB & Client setup
    │   ├── DatabaseIntegrationTest.java  # Sample PostgreSQL integration test suite
    │   ├── ProductCrudTest.java
    │   └── UserCrudTest.java
    └── resources/
        ├── config.properties             # DB configuration properties
        └── db/
            ├── init-schema.sql           # DDL: users, products, orders tables
            └── seed-data.sql             # DML: initial sample dataset
```

### Database Schema Specification (`init-schema.sql`)
```sql
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(10, 2) NOT NULL,
    category VARCHAR(100) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INT NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1,
    total_amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
```

### Workflow Specifications (On-Demand & Triggered)

#### 1. `.github/workflows/pr-checks.yml`
- **Triggers:** `pull_request` on `[main, master]`.
- **Execution:** Compile check (`mvn test-compile`), execute smoke suite (`mvn test -Dgroups="smoke"`), publish GitHub Step Summary.

#### 2. `.github/workflows/regression-and-reporting.yml`
- **Triggers:**
  - `push: branches: [main, master]`
  - `workflow_dispatch` (On-demand trigger)
  - *(Cron schedule omitted / available on-demand)*
- **Execution:**
  - `test-regression`: Runs full test suite (`mvn clean test -B -Dgroups="regression,db"`) with Testcontainers PostgreSQL.
  - `publish-allure-report`: Merges test history from `gh-pages`, compiles Allure HTML report, and deploys to GitHub Pages.

#### 3. `.github/workflows/manual-test-run.yml`
- **Triggers:** `workflow_dispatch` with inputs:
  - `environment`: `[staging, dev, prod]` (default: `staging`)
  - `test_tags`: `[regression, smoke, db, products, users]` (default: `regression`)
  - `threads`: integer `[1, 2, 4, 8]` (default: `4`)
  - `base_url`: string (optional API override)
  - `publish_report`: boolean (default: `true`)
- **Execution:** Executes targeted Maven command with dynamic system properties.

#### 4. `.github/workflows/security-and-dependency-scan.yml`
- **Triggers:** `pull_request: paths: ['pom.xml']`, `workflow_dispatch`.
- **Execution:** Scans dependencies for known CVEs.

# Testing

### Validation Approach
- **Database Integration Verification:**
  - Verify PostgreSQL container startup and schema initialization via Testcontainers.
  - Execute queries, record insertions, and assertions against seeded tables in `DatabaseIntegrationTest`.
  - Validate clean connection release back to HikariCP pool.
- **Workflow & Pipeline Validation:**
  - Verify YAML workflow syntax adheres to GitHub Actions specification.
  - Verify headless Maven execution locally (`mvn clean test -Dgroups="db"`, `mvn clean test -Dgroups="smoke"`).
  - Verify Allure step annotations for both HTTP API calls and SQL DB queries.

### Key Scenarios
- **DB Connection & Seeding Scenario:** Verify `DatabaseManager` connects to PostgreSQL, creates schema, and populates seed records.
- **DB CRUD & Query Scenario:** Verify `SELECT`, `INSERT`, `UPDATE`, and `DELETE` operations on `users`, `products`, and `orders` tables.
- **On-Demand Manual Workflow Scenario:** Verify `workflow_dispatch` trigger accepts parameters (`test_tags="db"`) and executes only DB tests.
- **PR Smoke Scenario:** Verify pull request workflow runs compile and smoke tests with step summary output.

# Delivery Steps

### ✓ Step 1: Add PostgreSQL, HikariCP, and Testcontainers dependencies
Configure Maven `pom.xml` with required database and container libraries.

- Add PostgreSQL JDBC Driver (`org.postgresql:postgresql`) and HikariCP connection pool dependencies to `pom.xml`.
- Add Testcontainers BOM and modules (`testcontainers`, `postgresql`, `junit-jupiter`) to `pom.xml`.
- Extend `ProjectConfig` and `config.properties` with database connection keys (`db.url`, `db.user`, `db.password`, `db.driver`, `db.pool.size`, `db.use.testcontainers`).

### ✓ Step 2: Implement DatabaseManager, schema DDL, and seed SQL scripts
Build database connection management and database initialization scripts.

- Create `src/test/resources/db/init-schema.sql` defining `users`, `products`, and `orders` tables.
- Create `src/test/resources/db/seed-data.sql` with sample test data records.
- Implement `DatabaseManager` providing HikariCP connection pooling, SQL execution methods, table seeding, and query result mapping.
- Implement `TestcontainersManager` for managing the lifecycle of the singleton PostgreSQL test container.

### ✓ Step 3: Implement sample database integration tests and assertions
Create database test suites and custom assertions with Allure step logging.

- Create entity records/models (`UserRecord`, `ProductRecord`, `OrderRecord`) for typed DB mapping.
- Implement `DatabaseIntegrationTest` with tests for connection verification, seed data validation, transactional insertions, and updates.
- Add Allure `@Step` annotations on all database operations to ensure query transparency in reports.
- Update `BaseTest` to provide shared database access for test suites.

### ✓ Step 4: Implement on-demand GitHub Actions CI/CD workflows
Create modular GitHub Actions workflows with on-demand triggers and Allure Pages deployment.

- Create `.github/workflows/pr-checks.yml` for pull request quality gates and smoke tests.
- Create `.github/workflows/regression-and-reporting.yml` for on-push and on-demand `workflow_dispatch` regression runs with GitHub Pages Allure reporting (no active cron).
- Create `.github/workflows/manual-test-run.yml` for parameterized on-demand test execution (`db`, `smoke`, `regression`).
- Create `.github/workflows/security-and-dependency-scan.yml` for on-demand/PR dependency scanning.

### ✓ Step 5: Update documentation with DB testing guide and CI/CD instructions
Update `README.md` with complete documentation for database testing and GitHub Actions.

- Document database testing architecture, Testcontainers usage, and custom PostgreSQL connection configuration in `README.md`.
- Add documentation for GitHub Actions workflows, on-demand dispatch parameters, and GitHub Pages setup.
- Add status badges for workflows in `README.md`.