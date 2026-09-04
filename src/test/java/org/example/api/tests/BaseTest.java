package org.example.api.tests;

import org.example.api.clients.ProductClient;
import org.example.api.clients.UserClient;
import org.example.api.database.DatabaseManager;
import org.example.api.mock.WireMockManager;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    protected static ProductClient productClient;
    protected static UserClient userClient;
    protected static DatabaseManager dbManager;
    protected static WireMockManager wireMockManager;

    @BeforeAll
    public static void setUp() {
        productClient = new ProductClient();
        userClient = new UserClient();
        dbManager = DatabaseManager.getInstance();
        wireMockManager = WireMockManager.getInstance();
    }
}
