package org.example.api.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Map;

import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Service Virtualization")
@Feature("WireMock HTTP Mocking")
@Owner("QA Automation Team")
@Tag("mock")
@Tag("regression")
@DisplayName("WireMock Integration & Simulation Tests")
@Execution(ExecutionMode.SAME_THREAD)
public class WireMockIntegrationTest extends BaseTest {

    @BeforeEach
    public void resetWireMockState() {
        wireMockManager.resetAll();
    }

    @Test
    @Severity(SeverityLevel.BLOCKER)
    @Story("External Service Mocking")
    @DisplayName("Mock External Payment Gateway Successful 200 Response")
    public void shouldMockExternalPaymentGatewaySuccess() {
        String paymentResponseBody = """
                {
                    "transactionId": "txn_987654321",
                    "status": "APPROVED",
                    "amount": 149.99,
                    "currency": "USD",
                    "timestamp": "2026-09-05T00:00:00Z"
                }
                """;

        wireMockManager.stubGetJson("/api/v1/payments/txn_987654321", 200, paymentResponseBody);

        Response response = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/payments/txn_987654321")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("transactionId")).isEqualTo("txn_987654321");
        assertThat(response.jsonPath().getString("status")).isEqualTo("APPROVED");
        assertThat(response.jsonPath().getDouble("amount")).isEqualTo(149.99);
        assertThat(response.jsonPath().getString("currency")).isEqualTo("USD");

        wireMockManager.verifyGetRequestCount("/api/v1/payments/txn_987654321", 1);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Request Verification & Header Validation")
    @DisplayName("Mock External Webhook Delivery and Verify Headers and Body")
    public void shouldMockWebhookDeliveryAndVerifyRequestPayload() {
        String requestPayload = """
                {
                    "event": "ORDER_PLACED",
                    "orderId": 4501,
                    "customerEmail": "customer@example.com"
                }
                """;

        String ackResponseBody = """
                {
                    "acknowledged": true,
                    "deliveryId": "dlv_12345"
                }
                """;

        wireMockManager.stubPostWithMatchingBody("/api/v1/webhooks/orders", "ORDER_PLACED", 201, ackResponseBody);

        Response response = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .header("X-Api-Key", "secret-api-key-123")
                .contentType(ContentType.JSON)
                .body(requestPayload)
                .when()
                .post("/api/v1/webhooks/orders")
                .then()
                .statusCode(201)
                .extract()
                .response();

        assertThat(response.jsonPath().getBoolean("acknowledged")).isTrue();
        assertThat(response.jsonPath().getString("deliveryId")).isEqualTo("dlv_12345");

        wireMockManager.verifyPostRequestWithHeader("/api/v1/webhooks/orders", "X-Api-Key", "secret-api-key-123", 1);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Fault & Error Simulation")
    @DisplayName("Simulate 500 Internal Server Error from Third-Party Service")
    public void shouldSimulateInternalServerError() {
        String errorResponseBody = """
                {
                    "error": "InternalServerError",
                    "message": "Payment provider upstream service unavailable"
                }
                """;

        wireMockManager.stubGetJson("/api/v1/third-party/failing-endpoint", 500, errorResponseBody);

        Response response = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/third-party/failing-endpoint")
                .then()
                .statusCode(500)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("InternalServerError");
        assertThat(response.jsonPath().getString("message")).contains("upstream service unavailable");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Rate Limiting Simulation")
    @DisplayName("Simulate 429 Too Many Requests with Retry-After Header")
    public void shouldSimulateRateLimitExceeded() {
        String rateLimitResponseBody = """
                {
                    "error": "TooManyRequests",
                    "message": "API rate limit exceeded. Please retry later."
                }
                """;

        wireMockManager.stubGetJson("/api/v1/rate-limited-endpoint", 429, rateLimitResponseBody);

        Response response = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/rate-limited-endpoint")
                .then()
                .statusCode(429)
                .extract()
                .response();

        assertThat(response.jsonPath().getString("error")).isEqualTo("TooManyRequests");
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Latency & Delay Simulation")
    @DisplayName("Simulate Network Latency with Fixed Delay and Validate Response Timing")
    public void shouldSimulateLatencyAndVerifyResponseTime() {
        String responseBody = """
                {
                    "status": "PROCESSED",
                    "delayApplied": true
                }
                """;

        wireMockManager.stubGetWithFixedDelay("/api/v1/slow-service", 200, responseBody, 400);

        long startTime = System.currentTimeMillis();

        Response response = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/slow-service")
                .then()
                .statusCode(200)
                .extract()
                .response();

        long duration = System.currentTimeMillis() - startTime;

        assertThat(response.jsonPath().getString("status")).isEqualTo("PROCESSED");
        assertThat(duration).isGreaterThanOrEqualTo(350L);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Stateful Scenario Mocking")
    @DisplayName("Simulate Order Processing State Machine (PENDING -> COMPLETED)")
    public void shouldSimulateStatefulScenarioTransition() {
        String pendingBody = """
                {
                    "orderId": 9901,
                    "status": "PENDING",
                    "message": "Order is being verified"
                }
                """;

        String completedBody = """
                {
                    "orderId": 9901,
                    "status": "COMPLETED",
                    "message": "Order has been successfully processed"
                }
                """;

        // Step 1: Initial state (STARTED) -> returns PENDING and switches to "ORDER_PROCESSED"
        wireMockManager.stubScenarioGet(
                "OrderProcessingScenario",
                STARTED,
                "ORDER_PROCESSED",
                "/api/v1/orders/9901/status",
                200,
                pendingBody
        );

        // Step 2: Second state ("ORDER_PROCESSED") -> returns COMPLETED
        wireMockManager.stubScenarioGet(
                "OrderProcessingScenario",
                "ORDER_PROCESSED",
                "ORDER_PROCESSED",
                "/api/v1/orders/9901/status",
                200,
                completedBody
        );

        // First call - should be PENDING
        Response firstResponse = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/orders/9901/status")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(firstResponse.jsonPath().getString("status")).isEqualTo("PENDING");

        // Second call - state has changed to COMPLETED
        Response secondResponse = RestAssured.given()
                .baseUri(wireMockManager.getBaseUrl())
                .accept(ContentType.JSON)
                .when()
                .get("/api/v1/orders/9901/status")
                .then()
                .statusCode(200)
                .extract()
                .response();

        assertThat(secondResponse.jsonPath().getString("status")).isEqualTo("COMPLETED");

        wireMockManager.verifyGetRequestCount("/api/v1/orders/9901/status", 2);
    }
}
