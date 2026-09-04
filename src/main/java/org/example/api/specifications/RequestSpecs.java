package org.example.api.specifications;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.api.config.ConfigManager;
import org.example.api.filters.CustomLogFilter;
import org.apache.http.HttpStatus;

public final class RequestSpecs {

    private RequestSpecs() {
    }

    public static RequestSpecification defaultRequestSpec() {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getConfig().baseUrl())
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new AllureRestAssured());

        if (ConfigManager.getConfig().loggingEnabled()) {
            builder.addFilter(new CustomLogFilter());
        }

        return builder.build();
    }

    public static RequestSpecification authenticatedRequestSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(defaultRequestSpec())
                .addHeader("Authorization", "Bearer " + token)
                .build();
    }
}
