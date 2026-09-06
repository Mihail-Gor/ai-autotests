package org.example.ui.assertions;

import org.assertj.core.api.AbstractAssert;
import org.example.ui.pages.InventoryPage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class InventoryAssert extends AbstractAssert<InventoryAssert, InventoryPage> {

    public InventoryAssert(InventoryPage actual) {
        super(actual, InventoryAssert.class);
    }

    public static InventoryAssert assertThatInventory(InventoryPage actual) {
        return new InventoryAssert(actual);
    }

    public InventoryAssert hasProductCount(int expectedCount) {
        isNotNull();
        int actualCount = actual.getProductCount();
        if (actualCount != expectedCount) {
            failWithMessage("Expected product count to be <%s> but was <%s>", expectedCount, actualCount);
        }
        return this;
    }

    public InventoryAssert containsProduct(String expectedProductName) {
        isNotNull();
        List<String> names = actual.getProductNames();
        if (!names.contains(expectedProductName)) {
            failWithMessage("Expected catalog to contain product <%s> but found: %s", expectedProductName, names);
        }
        return this;
    }

    public InventoryAssert hasProductsSortedByNameAscending() {
        isNotNull();
        List<String> actualNames = actual.getProductNames();
        List<String> sortedNames = new ArrayList<>(actualNames);
        sortedNames.sort(Comparator.naturalOrder());
        if (!Objects.equals(actualNames, sortedNames)) {
            failWithMessage("Expected products to be sorted by name A-Z, but was: %s (expected: %s)",
                    actualNames, sortedNames);
        }
        return this;
    }

    public InventoryAssert hasProductsSortedByNameDescending() {
        isNotNull();
        List<String> actualNames = actual.getProductNames();
        List<String> sortedNames = new ArrayList<>(actualNames);
        sortedNames.sort(Comparator.reverseOrder());
        if (!Objects.equals(actualNames, sortedNames)) {
            failWithMessage("Expected products to be sorted by name Z-A, but was: %s (expected: %s)",
                    actualNames, sortedNames);
        }
        return this;
    }

    public InventoryAssert hasProductsSortedByPriceAscending() {
        isNotNull();
        List<Double> actualPrices = actual.getProductPrices();
        List<Double> sortedPrices = new ArrayList<>(actualPrices);
        sortedPrices.sort(Comparator.naturalOrder());
        if (!Objects.equals(actualPrices, sortedPrices)) {
            failWithMessage("Expected products to be sorted by price low-to-high, but was: %s (expected: %s)",
                    actualPrices, sortedPrices);
        }
        return this;
    }

    public InventoryAssert hasProductsSortedByPriceDescending() {
        isNotNull();
        List<Double> actualPrices = actual.getProductPrices();
        List<Double> sortedPrices = new ArrayList<>(actualPrices);
        sortedPrices.sort(Comparator.reverseOrder());
        if (!Objects.equals(actualPrices, sortedPrices)) {
            failWithMessage("Expected products to be sorted by price high-to-low, but was: %s (expected: %s)",
                    actualPrices, sortedPrices);
        }
        return this;
    }

    public InventoryAssert hasCartBadgeCount(int expectedCount) {
        isNotNull();
        int actualCount = actual.getHeader().getCartBadgeCount();
        if (actualCount != expectedCount) {
            failWithMessage("Expected cart badge count to be <%s> but was <%s>", expectedCount, actualCount);
        }
        return this;
    }

    public InventoryAssert hasCartBadgeHidden() {
        isNotNull();
        if (actual.getHeader().isCartBadgeVisible()) {
            failWithMessage("Expected cart badge to be hidden, but it was visible with count <%s>",
                    actual.getHeader().getCartBadgeCount());
        }
        return this;
    }
}
