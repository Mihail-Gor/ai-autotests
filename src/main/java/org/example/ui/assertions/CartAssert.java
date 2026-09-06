package org.example.ui.assertions;

import org.assertj.core.api.AbstractAssert;
import org.example.ui.pages.CartPage;

import java.util.List;

public class CartAssert extends AbstractAssert<CartAssert, CartPage> {

    public CartAssert(CartPage actual) {
        super(actual, CartAssert.class);
    }

    public static CartAssert assertThatCart(CartPage actual) {
        return new CartAssert(actual);
    }

    public CartAssert hasItemCount(int expectedCount) {
        isNotNull();
        int actualCount = actual.getItemCount();
        if (actualCount != expectedCount) {
            failWithMessage("Expected cart to contain <%s> items but found <%s>", expectedCount, actualCount);
        }
        return this;
    }

    public CartAssert containsProduct(String expectedProductName) {
        isNotNull();
        List<String> names = actual.getItemNames();
        if (!names.contains(expectedProductName)) {
            failWithMessage("Expected cart to contain product <%s> but found: %s", expectedProductName, names);
        }
        return this;
    }

    public CartAssert doesNotContainProduct(String unexpectedProductName) {
        isNotNull();
        List<String> names = actual.getItemNames();
        if (names.contains(unexpectedProductName)) {
            failWithMessage("Expected cart NOT to contain product <%s> but it was present", unexpectedProductName);
        }
        return this;
    }

    public CartAssert isEmpty() {
        return hasItemCount(0);
    }
}
