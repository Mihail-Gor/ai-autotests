package org.example.performance.scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;

import java.time.Duration;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.css;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.feed;
import static io.gatling.javaapi.core.CoreDsl.jsonPath;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public final class ProductScenarios {

    private ProductScenarios() {
    }

    private static final Iterator<Map<String, Object>> PRODUCT_ID_FEEDER =
            Stream.generate(() -> Collections.<String, Object>singletonMap("productId", ThreadLocalRandom.current().nextInt(1, 100)))
                    .iterator();

    private static final Iterator<Map<String, Object>> SEARCH_QUERY_FEEDER =
            Stream.generate(() -> {
                String[] queries = {"phone", "laptop", "shirt", "watch", "perfume", "bag", "shoes"};
                String query = queries[ThreadLocalRandom.current().nextInt(queries.length)];
                return Collections.<String, Object>singletonMap("searchQuery", query);
            }).iterator();

    public static ScenarioBuilder dummyJsonProductBrowseScenario() {
        return scenario("DummyJSON Product Browse & Search Flow")
                .exec(
                        http("Get Products List - Page 1")
                                .get("/products?limit=10&skip=0")
                                .check(status().is(200))
                                .check(jsonPath("$.products").exists())
                                .check(jsonPath("$.total").ofInt().gt(0))
                )
                .pause(Duration.ofMillis(500), Duration.ofMillis(1500))
                .feed(PRODUCT_ID_FEEDER)
                .exec(
                        http("Get Single Product by ID - #{productId}")
                                .get("/products/#{productId}")
                                .check(status().in(200, 404))
                )
                .pause(Duration.ofMillis(300), Duration.ofMillis(1000))
                .feed(SEARCH_QUERY_FEEDER)
                .exec(
                        http("Search Products - #{searchQuery}")
                                .get("/products/search?q=#{searchQuery}")
                                .check(status().is(200))
                                .check(jsonPath("$.products").exists())
                )
                .pause(Duration.ofMillis(500))
                .exec(
                        http("Get Product Categories")
                                .get("/products/categories")
                                .check(status().is(200))
                );
    }
}
