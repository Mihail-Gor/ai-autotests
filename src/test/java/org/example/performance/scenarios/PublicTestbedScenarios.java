package org.example.performance.scenarios;

import io.gatling.javaapi.core.ScenarioBuilder;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.css;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.pause;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public final class PublicTestbedScenarios {

    private PublicTestbedScenarios() {
    }

    /**
     * Scenario for the official Gatling testbed: https://computer-database.gatling.io
     * Designed by the Gatling team specifically for learning and stress/load testing.
     */
    public static ScenarioBuilder gatlingComputerDatabaseScenario() {
        return scenario("Gatling Official Computer Database Flow")
                .exec(
                        http("Browse Computers - Page 0")
                                .get("/computers")
                                .check(status().is(200))
                                .check(css("#main h1").exists())
                )
                .pause(Duration.ofMillis(500), Duration.ofMillis(1200))
                .exec(
                        http("Filter Computers - Apple")
                                .get("/computers?f=Apple")
                                .check(status().is(200))
                )
                .pause(Duration.ofMillis(500))
                .exec(
                        http("View Computer Details - MacBook Pro")
                                .get("/computers/381")
                                .check(status().is(200))
                );
    }

    /**
     * Scenario for the official k6/Grafana public testbed: https://test.k6.io
     * Designed by Grafana/k6 specifically for high-load and performance benchmarking.
     */
    public static ScenarioBuilder k6PublicTestbedScenario() {
        return scenario("Grafana k6 Public Testbed Flow")
                .exec(
                        http("Home Page")
                                .get("/")
                                .check(status().is(200))
                )
                .pause(Duration.ofMillis(300), Duration.ofMillis(1000))
                .exec(
                        http("Contacts Page")
                                .get("/contacts.php")
                                .check(status().is(200))
                )
                .pause(Duration.ofMillis(300))
                .exec(
                        http("News Page")
                                .get("/news.php")
                                .check(status().is(200))
                );
    }
}
