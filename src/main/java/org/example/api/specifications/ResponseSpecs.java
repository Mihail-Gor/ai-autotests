package org.example.api.specifications;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;

public final class ResponseSpecs {

    private ResponseSpecs() {
    }

    public static ResponseSpecification statusOk() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification statusCreated() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification statusNotFound() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification statusBadRequest() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification entityDeleted() {
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .expectBody("isDeleted", Matchers.equalTo(true))
                .expectBody("deletedOn", Matchers.notNullValue())
                .build();
    }
}
