package org.example.api.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Slf4j
public class WireMockManager {

    private static volatile WireMockManager instance;
    private final WireMockServer wireMockServer;

    private WireMockManager() {
        this.wireMockServer = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .dynamicPort()
        );
        this.wireMockServer.start();
        WireMock.configureFor("localhost", wireMockServer.port());
        log.info("WireMock server started on port {}", wireMockServer.port());
    }

    public static WireMockManager getInstance() {
        if (instance == null) {
            synchronized (WireMockManager.class) {
                if (instance == null) {
                    instance = new WireMockManager();
                }
            }
        }
        return instance;
    }

    public int getPort() {
        return wireMockServer.port();
    }

    public String getBaseUrl() {
        return "http://localhost:" + wireMockServer.port();
    }

    public WireMockServer getServer() {
        return wireMockServer;
    }

    @Step("WireMock: Reset all stubs and request logs")
    public void resetAll() {
        wireMockServer.resetAll();
        log.info("WireMock stubs and request logs reset.");
    }

    @Step("WireMock Stub: GET {path} -> Status {status}")
    public void stubGetJson(String path, int status, String responseBodyJson) {
        wireMockServer.stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
        log.info("Configured WireMock GET stub for {} with status {}", path, status);
    }

    @Step("WireMock Stub: POST {path} -> Status {status}")
    public void stubPostJson(String path, int status, String responseBodyJson) {
        wireMockServer.stubFor(post(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
        log.info("Configured WireMock POST stub for {} with status {}", path, status);
    }

    @Step("WireMock Stub: POST {path} with matching body -> Status {status}")
    public void stubPostWithMatchingBody(String path, String expectedJsonSubstring, int status, String responseBodyJson) {
        wireMockServer.stubFor(post(urlEqualTo(path))
                .withRequestBody(containing(expectedJsonSubstring))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
        log.info("Configured WireMock POST stub for {} matching body '{}' with status {}", path, expectedJsonSubstring, status);
    }

    @Step("WireMock Stub: GET {path} with delay {delayMillis}ms -> Status {status}")
    public void stubGetWithFixedDelay(String path, int status, String responseBodyJson, int delayMillis) {
        wireMockServer.stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)
                        .withFixedDelay(delayMillis)));
        log.info("Configured WireMock GET stub for {} with {}ms delay", path, delayMillis);
    }

    @Step("WireMock Stub: GET {path} with fault {fault}")
    public void stubFault(String path, Fault fault) {
        wireMockServer.stubFor(get(urlEqualTo(path))
                .willReturn(aResponse().withFault(fault)));
        log.info("Configured WireMock fault stub for {} with fault {}", path, fault);
    }

    @Step("WireMock Scenario Stub: '{scenarioName}' [{fromState} -> {toState}] on GET {path}")
    public void stubScenarioGet(String scenarioName, String fromState, String toState, String path, int status, String responseBodyJson) {
        wireMockServer.stubFor(get(urlEqualTo(path))
                .inScenario(scenarioName)
                .whenScenarioStateIs(fromState)
                .willSetStateTo(toState)
                .willReturn(aResponse()
                        .withStatus(status)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBodyJson)));
        log.info("Configured WireMock scenario stub '{}' for {} (State: {} -> {})", scenarioName, path, fromState, toState);
    }

    @Step("WireMock Verify: GET {path} called exactly {count} times")
    public void verifyGetRequestCount(String path, int count) {
        wireMockServer.verify(count, getRequestedFor(urlEqualTo(path)));
    }

    @Step("WireMock Verify: POST {path} called exactly {count} times with header '{headerName}'")
    public void verifyPostRequestWithHeader(String path, String headerName, String headerValue, int count) {
        wireMockServer.verify(count, postRequestedFor(urlEqualTo(path))
                .withHeader(headerName, equalTo(headerValue)));
    }

    public synchronized void stop() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
            log.info("WireMock server stopped.");
        }
    }
}
