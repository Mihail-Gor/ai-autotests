package org.example.performance.simulations;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import org.example.performance.scenarios.ProductScenarios;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;

/**
 * 100% Hermetic Local Performance Simulation using WireMock.
 * Safe for offline CI runs, local development, and benchmarking without spamming external servers.
 */
public class LocalWireMockSimulation extends Simulation {

    private static final int PORT = 8089;
    private static WireMockServer server;

    @Override
    public void before() {
        if (server == null || !server.isRunning()) {
            server = new WireMockServer(WireMockConfiguration.wireMockConfig().port(PORT));
            server.start();

            // Stub product list
            server.stubFor(get(urlMatching("/products.*"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"products\":[{\"id\":1,\"title\":\"Test Phone\",\"price\":499}],\"total\":100,\"skip\":0,\"limit\":10}")));

            // Stub single product
            server.stubFor(get(urlMatching("/products/[0-9]+"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("{\"id\":1,\"title\":\"Test Phone\",\"price\":499}")));

            // Stub categories
            server.stubFor(get(urlMatching("/products/categories"))
                    .willReturn(aResponse()
                            .withStatus(200)
                            .withHeader("Content-Type", "application/json")
                            .withBody("[\"smartphones\",\"laptops\",\"fragrances\"]")));
        }
    }

    @Override
    public void after() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
    }

    private final HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:" + PORT)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling/Java21 Local Benchmark");

    private final ScenarioBuilder productScenario = ProductScenarios.dummyJsonProductBrowseScenario();

    {
        setUp(
                productScenario.injectOpen(
                        rampUsers(10).during(Duration.ofSeconds(3)),
                        constantUsersPerSec(5).during(Duration.ofSeconds(5))
                )
        )
        .protocols(httpProtocol)
        .assertions(
                global().successfulRequests().percent().gte(99.0),
                global().responseTime().percentile3().lte(500)
        );
    }
}
