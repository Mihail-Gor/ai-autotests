package org.example.api.database;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class TestcontainersManager {

    private static final String DEFAULT_IMAGE = "postgres:16-alpine";
    private static volatile TestcontainersManager instance;
    private PostgreSQLContainer<?> postgresContainer;

    private TestcontainersManager() {
        initContainer();
    }

    public static TestcontainersManager getInstance() {
        if (instance == null) {
            synchronized (TestcontainersManager.class) {
                if (instance == null) {
                    instance = new TestcontainersManager();
                }
            }
        }
        return instance;
    }

    private void initContainer() {
        try {
            log.info("Initializing PostgreSQL Testcontainer with image: {}", DEFAULT_IMAGE);
            postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse(DEFAULT_IMAGE))
                    .withDatabaseName("testdb")
                    .withUsername("postgres")
                    .withPassword("postgres")
                    .withReuse(false);
            postgresContainer.start();
            log.info("PostgreSQL Testcontainer started at JDBC URL: {}", postgresContainer.getJdbcUrl());
        } catch (Exception e) {
            log.error("Failed to start PostgreSQL Testcontainer: {}", e.getMessage(), e);
            throw new RuntimeException("Could not initialize Testcontainers PostgreSQL instance", e);
        }
    }

    public String getJdbcUrl() {
        ensureRunning();
        return postgresContainer.getJdbcUrl();
    }

    public String getUsername() {
        ensureRunning();
        return postgresContainer.getUsername();
    }

    public String getPassword() {
        ensureRunning();
        return postgresContainer.getPassword();
    }

    public String getDriverClassName() {
        ensureRunning();
        return postgresContainer.getDriverClassName();
    }

    public boolean isRunning() {
        return postgresContainer != null && postgresContainer.isRunning();
    }

    private void ensureRunning() {
        if (postgresContainer == null || !postgresContainer.isRunning()) {
            initContainer();
        }
    }

    public synchronized void stop() {
        if (postgresContainer != null && postgresContainer.isRunning()) {
            log.info("Stopping PostgreSQL Testcontainer...");
            postgresContainer.stop();
            postgresContainer = null;
        }
    }
}
