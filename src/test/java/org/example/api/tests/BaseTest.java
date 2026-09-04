package org.example.api.tests;

import org.example.api.clients.ProductClient;
import org.example.api.clients.UserClient;
import org.example.api.database.DatabaseManager;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    protected static ProductClient productClient;
    protected static UserClient userClient;
    protected static DatabaseManager dbManager;

    @BeforeAll
    public static void setUp() {
        productClient = new ProductClient();
        userClient = new UserClient();
        dbManager = DatabaseManager.getInstance();
    }
}
