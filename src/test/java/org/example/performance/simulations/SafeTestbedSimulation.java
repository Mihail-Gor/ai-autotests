package org.example.performance.simulations;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.example.performance.config.PerformanceConfig;
import org.example.performance.scenarios.PublicTestbedScenarios;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * Load simulation targeting the official Gatling computer-database public testbed.
 * This platform is specifically designed to handle and allow load/performance tests safely.
 */
public class SafeTestbedSimulation extends Simulation {

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl(PerformanceConfig.GATLING_TESTBED_URL)
            .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .acceptLanguageHeader("en-US,en;q=0.5")
            .acceptEncodingHeader("gzip, deflate")
            .userAgentHeader("Gatling/Java21 Performance Testbed Client");

    private final ScenarioBuilder testbedScenario = PublicTestbedScenarios.gatlingComputerDatabaseScenario();

    {
        setUp(
                testbedScenario.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(5)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(10))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().successfulRequests().percent().gte(95.0),
                global().responseTime().percentile3().lte(2000)
        );
    }
}
