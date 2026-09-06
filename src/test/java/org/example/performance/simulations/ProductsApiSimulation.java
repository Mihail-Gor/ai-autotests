package org.example.performance.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.example.performance.config.PerformanceConfig;
import org.example.performance.scenarios.ProductScenarios;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load simulation for DummyJSON Products API.
 * Uses a gentle ramp-up profile to respect public API rate limits.
 */
public class ProductsApiSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(PerformanceConfig.DUMMYJSON_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/Java21 LoadTest Client");

    private final ScenarioBuilder productScenario = ProductScenarios.dummyJsonProductBrowseScenario();

    {
        setUp(
                productScenario.injectOpen(
                        rampUsers(PerformanceConfig.USERS).during(Duration.ofSeconds(PerformanceConfig.RAMP_DURATION))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().successfulRequests().percent().gte(PerformanceConfig.SUCCESS_PERCENTAGE),
                global().responseTime().percentile3().lte(PerformanceConfig.MAX_RESPONSE_TIME_MS)
        );
    }
}
