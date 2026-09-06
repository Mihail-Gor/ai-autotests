package org.example.performance.config;

public final class PerformanceConfig {

    private PerformanceConfig() {
    }

    public static final String DUMMYJSON_URL = System.getProperty("gatling.baseUrl", "https://dummyjson.com");
    public static final String GATLING_TESTBED_URL = System.getProperty("gatling.testbedUrl", "https://computer-database.gatling.io");
    public static final String K6_TESTBED_URL = System.getProperty("gatling.k6TestbedUrl", "https://test.k6.io");

    public static final int USERS = Integer.getInteger("gatling.users", 5);
    public static final int RAMP_DURATION = Integer.getInteger("gatling.rampDuration", 10);
    public static final int DURATION = Integer.getInteger("gatling.duration", 15);
    public static final int MAX_RESPONSE_TIME_MS = Integer.getInteger("gatling.maxResponseTime", 3000);
    public static final double SUCCESS_PERCENTAGE = Double.parseDouble(System.getProperty("gatling.successPercentage", "95.0"));
}
