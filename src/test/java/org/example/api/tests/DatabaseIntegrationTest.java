package org.example.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.example.api.database.models.OrderRecord;
import org.example.api.database.models.ProductRecord;
import org.example.api.database.models.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Database Management")
@Feature("PostgreSQL Database Integration")
@Owner("QA Automation Team")
@Tag("db")
@Tag("regression")
@DisplayName("PostgreSQL Database Integration Tests")
public class DatabaseIntegrationTest extends BaseTest {

    @BeforeEach
    public void setupDatabaseState() {
        dbManager.resetTables();
        dbManager.initSchemaAndSeed();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("Connection & Seed Verification")
    @DisplayName("Verify Database Connectivity and Seed Data Record Counts")
    public void shouldVerifyDatabaseConnectivityAndSeedData() {
        Optional<Long> userCount = dbManager.queryForScalar("SELECT COUNT(*) FROM users", Long.class);
        Optional<Long> productCount = dbManager.queryForScalar("SELECT COUNT(*) FROM products", Long.class);
        Optional<Long> orderCount = dbManager.queryForScalar("SELECT COUNT(*) FROM orders", Long.class);

        assertThat(userCount).isPresent().contains(4L);
        assertThat(productCount).isPresent().contains(5L);
        assertThat(orderCount).isPresent().contains(3L);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("User Entity Verification")
    @DisplayName("Query User by Username and Verify Mapped Record")
    public void shouldQueryUserByUsernameSuccessfully() {
        Optional<UserRecord> userOpt = dbManager.queryForObject(
                "SELECT * FROM users WHERE username = ?",
                UserRecord.MAPPER,
                "emilys"
        );

        assertThat(userOpt).isPresent();
        UserRecord user = userOpt.get();
        assertThat(user.username()).isEqualTo("emilys");
        assertThat(user.email()).isEqualTo("emily.johnson@x.dummyjson.com");
        assertThat(user.firstName()).isEqualTo("Emily");
        assertThat(user.lastName()).isEqualTo("Johnson");
        assertThat(user.role()).isEqualTo("ADMIN");
        assertThat(user.status()).isEqualTo("ACTIVE");
        assertThat(user.createdAt()).isNotNull();
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Product Management")
    @DisplayName("Insert New Product and Verify Query Persistence")
    public void shouldInsertNewProductAndVerifyRecord() {
        String insertSql = "INSERT INTO products (title, description, price, category, stock) VALUES (?, ?, ?, ?, ?)";
        long generatedId = dbManager.executeInsertAndReturnId(
                insertSql,
                "Wireless Gaming Mouse",
                "Ergonomic RGB 16000 DPI gaming mouse",
                new BigDecimal("59.90"),
                "electronics",
                150
        );

        assertThat(generatedId).isPositive();

        Optional<ProductRecord> productOpt = dbManager.queryForObject(
                "SELECT * FROM products WHERE id = ?",
                ProductRecord.MAPPER,
                generatedId
        );

        assertThat(productOpt).isPresent();
        ProductRecord product = productOpt.get();
        assertThat(product.id()).isEqualTo(generatedId);
        assertThat(product.title()).isEqualTo("Wireless Gaming Mouse");
        assertThat(product.price()).isEqualByComparingTo(new BigDecimal("59.90"));
        assertThat(product.category()).isEqualTo("electronics");
        assertThat(product.stock()).isEqualTo(150);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("User Status Management")
    @DisplayName("Update User Status and Verify Change in Database")
    public void shouldUpdateUserStatusSuccessfully() {
        int updatedRows = dbManager.executeUpdate(
                "UPDATE users SET status = ? WHERE username = ?",
                "BLOCKED",
                "sophiab"
        );

        assertThat(updatedRows).isEqualTo(1);

        Optional<UserRecord> updatedUser = dbManager.queryForObject(
                "SELECT * FROM users WHERE username = ?",
                UserRecord.MAPPER,
                "sophiab"
        );

        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().status()).isEqualTo("BLOCKED");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Order Relational Verification")
    @DisplayName("Query User Orders and Verify Aggregate Calculations")
    public void shouldQueryUserOrdersAndVerifyTotals() {
        List<OrderRecord> userOrders = dbManager.queryForList(
                "SELECT * FROM orders WHERE user_id = ? ORDER BY id ASC",
                OrderRecord.MAPPER,
                1L
        );

        assertThat(userOrders).hasSize(2);
        assertThat(userOrders)
                .extracting(OrderRecord::status)
                .containsExactly("COMPLETED", "PROCESSING");

        BigDecimal totalSum = userOrders.stream()
                .map(OrderRecord::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertThat(totalSum).isEqualByComparingTo(new BigDecimal("69.97"));
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Cascade Deletion")
    @DisplayName("Delete User and Verify Cascading Order Removal")
    public void shouldCascadeDeleteOrdersWhenUserIsDeleted() {
        Optional<Long> initialOrdersCount = dbManager.queryForScalar(
                "SELECT COUNT(*) FROM orders WHERE user_id = ?",
                Long.class,
                2L
        );
        assertThat(initialOrdersCount).isPresent().contains(1L);

        int deletedUsers = dbManager.executeUpdate("DELETE FROM users WHERE id = ?", 2L);
        assertThat(deletedUsers).isEqualTo(1);

        Optional<Long> remainingOrdersCount = dbManager.queryForScalar(
                "SELECT COUNT(*) FROM orders WHERE user_id = ?",
                Long.class,
                2L
        );
        assertThat(remainingOrdersCount).isPresent().contains(0L);
    }
}
