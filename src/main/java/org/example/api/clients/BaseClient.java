package org.example.api.clients;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.example.api.specifications.RequestSpecs;

public abstract class BaseClient {

    protected RequestSpecification getRequestSpec() {
        return RestAssured.given().spec(RequestSpecs.defaultRequestSpec());
    }

    protected RequestSpecification getAuthenticatedRequestSpec(String token) {
        return RestAssured.given().spec(RequestSpecs.authenticatedRequestSpec(token));
    }
}
