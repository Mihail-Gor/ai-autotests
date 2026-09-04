package org.example.api.tests;

import org.example.api.clients.ProductClient;
import org.example.api.clients.UserClient;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {

    protected static ProductClient productClient;
    protected static UserClient userClient;

    @BeforeAll
    public static void setUp() {
        productClient = new ProductClient();
        userClient = new UserClient();
    }
}
