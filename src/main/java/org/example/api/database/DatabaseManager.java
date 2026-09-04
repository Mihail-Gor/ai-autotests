package org.example.api.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import org.example.api.config.ConfigManager;
import org.example.api.config.ProjectConfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class DatabaseManager {

    private static volatile DatabaseManager instance;
    private volatile HikariDataSource dataSource;
    private final ProjectConfig config;

    private DatabaseManager() {
        this.config = ConfigManager.getConfig();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private HikariDataSource getDataSource() {
        if (dataSource == null) {
            synchronized (this) {
                if (dataSource == null) {
                    dataSource = initDataSource();
                }
            }
        }
        return dataSource;
    }

    private HikariDataSource initDataSource() {
        HikariConfig hikariConfig = new HikariConfig();

        if (config.dbUseTestcontainers()) {
            log.info("Configuring DataSource using Testcontainers PostgreSQL...");
            TestcontainersManager tc = TestcontainersManager.getInstance();
            hikariConfig.setJdbcUrl(tc.getJdbcUrl());
            hikariConfig.setUsername(tc.getUsername());
            hikariConfig.setPassword(tc.getPassword());
            hikariConfig.setDriverClassName(tc.getDriverClassName());
        } else {
            log.info("Configuring DataSource using direct JDBC URL: {}", config.dbUrl());
            hikariConfig.setJdbcUrl(config.dbUrl());
            hikariConfig.setUsername(config.dbUser());
            hikariConfig.setPassword(config.dbPassword());
            hikariConfig.setDriverClassName(config.dbDriver());
        }

        hikariConfig.setMaximumPoolSize(config.dbPoolSize());
        hikariConfig.setMinimumIdle(2);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(20000);
        hikariConfig.setMaxLifetime(600000);
        hikariConfig.setPoolName("Test-HikariCP-Pool");

        return new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    @Step("DB: Execute SQL Script '{resourcePath}'")
    public void executeScript(String resourcePath) {
        log.info("Executing database script from classpath: {}", resourcePath);
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Script resource not found in classpath: " + resourcePath);
            }
            String content;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                content = reader.lines().collect(Collectors.joining("\n"));
            }

            List<String> statements = Arrays.stream(content.split(";"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();

            try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    stmt.execute(sql);
                }
            }
            log.info("Successfully executed database script '{}' ({} statements)", resourcePath, statements.size());
        } catch (Exception e) {
            log.error("Failed to execute database script '{}': {}", resourcePath, e.getMessage(), e);
            throw new RuntimeException("Database script execution failed: " + resourcePath, e);
        }
    }

    @Step("DB: Initialize Schema and Seed Data")
    public void initSchemaAndSeed() {
        executeScript("db/init-schema.sql");
        executeScript("db/seed-data.sql");
    }

    @Step("DB: Reset all tables")
    public void resetTables() {
        log.info("Resetting all database tables...");
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("TRUNCATE TABLE orders, products, users RESTART IDENTITY CASCADE;");
            log.info("All tables truncated and identity restarted successfully");
        } catch (SQLException e) {
            log.error("Failed to reset tables: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to reset tables", e);
        }
    }

    @Step("DB: Execute Update - {sql}")
    public int executeUpdate(String sql, Object... params) {
        log.debug("Executing SQL update: {} with params: {}", sql, Arrays.toString(params));
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bindParameters(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("Database update failed for SQL: {} params: {}", sql, Arrays.toString(params), e);
            throw new RuntimeException("DB update failed: " + sql, e);
        }
    }

    @Step("DB: Execute Insert and Return Generated ID - {sql}")
    public long executeInsertAndReturnId(String sql, Object... params) {
        log.debug("Executing SQL insert: {} with params: {}", sql, Arrays.toString(params));
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindParameters(pstmt, params);
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                throw new SQLException("Insert succeeded but no generated ID was returned.");
            }
        } catch (SQLException e) {
            log.error("Database insert failed for SQL: {} params: {}", sql, Arrays.toString(params), e);
            throw new RuntimeException("DB insert failed: " + sql, e);
        }
    }

    @Step("DB: Query List - {sql}")
    public <T> List<T> queryForList(String sql, ResultSetMapper<T> mapper, Object... params) {
        log.debug("Executing SQL query list: {} with params: {}", sql, Arrays.toString(params));
        List<T> resultList = new ArrayList<>();
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bindParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    resultList.add(mapper.map(rs));
                }
            }
            return resultList;
        } catch (SQLException e) {
            log.error("Database query failed for SQL: {} params: {}", sql, Arrays.toString(params), e);
            throw new RuntimeException("DB query failed: " + sql, e);
        }
    }

    @Step("DB: Query Single Object - {sql}")
    public <T> Optional<T> queryForObject(String sql, ResultSetMapper<T> mapper, Object... params) {
        List<T> list = queryForList(sql, mapper, params);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        if (list.size() > 1) {
            throw new IllegalStateException("Expected single row result but got " + list.size() + " rows for SQL: " + sql);
        }
        return Optional.of(list.getFirst());
    }

    @Step("DB: Query Scalar - {sql}")
    public <T> Optional<T> queryForScalar(String sql, Class<T> requiredType, Object... params) {
        return queryForObject(sql, rs -> {
            Object obj = rs.getObject(1);
            if (obj == null) {
                return null;
            }
            return requiredType.cast(obj);
        }, params);
    }

    private void bindParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    public synchronized void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("Closing HikariCP DataSource...");
            dataSource.close();
        }
        if (config.dbUseTestcontainers()) {
            TestcontainersManager.getInstance().stop();
        }
    }
}
