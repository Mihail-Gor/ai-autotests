package org.example.api.filters;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CustomLogFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext ctx) {
        log.info("--> {} {}", requestSpec.getMethod(), requestSpec.getURI());

        if (requestSpec.getBody() != null) {
            log.debug("Request Body:\n{}", requestSpec.getBody().toString());
        }

        long startTime = System.currentTimeMillis();
        Response response = ctx.next(requestSpec, responseSpec);
        long duration = System.currentTimeMillis() - startTime;

        log.info("<-- {} {} ({} ms)", response.getStatusCode(), response.getStatusLine(), duration);
        if (response.getBody() != null && !response.getBody().asString().isEmpty()) {
            log.debug("Response Body:\n{}", response.getBody().asString());
        }

        return response;
    }
}
