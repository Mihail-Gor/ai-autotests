package org.example.api.assertions;

import org.assertj.core.api.AbstractAssert;
import org.example.api.models.product.ProductDto;

import java.util.Objects;

public class ProductAssert extends AbstractAssert<ProductAssert, ProductDto> {

    public ProductAssert(ProductDto actual) {
        super(actual, ProductAssert.class);
    }

    public static ProductAssert assertThatProduct(ProductDto actual) {
        return new ProductAssert(actual);
    }

    public ProductAssert hasId(Integer expectedId) {
        isNotNull();
        if (!Objects.equals(actual.getId(), expectedId)) {
            failWithMessage("Expected product id to be <%s> but was <%s>", expectedId, actual.getId());
        }
        return this;
    }

    public ProductAssert hasTitle(String expectedTitle) {
        isNotNull();
        if (!Objects.equals(actual.getTitle(), expectedTitle)) {
            failWithMessage("Expected product title to be <%s> but was <%s>", expectedTitle, actual.getTitle());
        }
        return this;
    }

    public ProductAssert hasPrice(Double expectedPrice) {
        isNotNull();
        if (actual.getPrice() == null || Math.abs(actual.getPrice() - expectedPrice) > 0.001) {
            failWithMessage("Expected product price to be <%s> but was <%s>", expectedPrice, actual.getPrice());
        }
        return this;
    }

    public ProductAssert hasCategory(String expectedCategory) {
        isNotNull();
        if (!Objects.equals(actual.getCategory(), expectedCategory)) {
            failWithMessage("Expected product category to be <%s> but was <%s>", expectedCategory, actual.getCategory());
        }
        return this;
    }

    public ProductAssert isDeleted() {
        isNotNull();
        if (!Boolean.TRUE.equals(actual.getIsDeleted())) {
            failWithMessage("Expected product to be marked as deleted, but isDeleted was <%s>", actual.getIsDeleted());
        }
        if (actual.getDeletedOn() == null || actual.getDeletedOn().isBlank()) {
            failWithMessage("Expected product deletion timestamp (deletedOn) to be present, but was empty");
        }
        return this;
    }

    public ProductAssert matchesCreatedRequest(ProductDto request) {
        isNotNull();
        if (request.getTitle() != null) {
            hasTitle(request.getTitle());
        }
        if (request.getPrice() != null) {
            hasPrice(request.getPrice());
        }
        if (request.getCategory() != null) {
            hasCategory(request.getCategory());
        }
        return this;
    }
}
