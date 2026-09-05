package org.example.ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.example.ui.data.SortOption;
import org.example.ui.data.UserCredentials;
import org.example.ui.pages.InventoryPage;
import org.example.ui.pages.ProductDetailsPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.ui.assertions.InventoryAssert.assertThatInventory;

@Epic("UI Automation")
@Feature("Product Catalog & Inventory")
@Owner("QA Automation Team")
@Tag("ui")
public class InventoryUiTest extends BaseUiTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        inventoryPage = loginPage().open().loginAs(UserCredentials.STANDARD_USER);
    }

    @Test
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Product catalog displays all available items correctly")
    @Description("Verify that the inventory page displays all 6 default products with name, price, and Add to cart buttons.")
    void testProductCatalogDisplay() {
        assertThatInventory(inventoryPage)
                .hasProductCount(6)
                .containsProduct("Sauce Labs Backpack")
                .containsProduct("Sauce Labs Bike Light")
                .containsProduct("Sauce Labs Bolt T-Shirt")
                .containsProduct("Sauce Labs Fleece Jacket")
                .containsProduct("Sauce Labs Onesie")
                .containsProduct("Test.allTheThings() T-Shirt (Red)");
    }

    @ParameterizedTest(name = "Sort products by: {0}")
    @Story("Catalog Sorting")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @EnumSource(SortOption.class)
    @DisplayName("Sorting catalog products changes item order appropriately")
    @Description("Verify that selecting each sort dropdown option correctly reorders products.")
    void testProductSorting(SortOption sortOption) {
        inventoryPage.selectSortOption(sortOption);

        switch (sortOption) {
            case NAME_AZ -> assertThatInventory(inventoryPage).hasProductsSortedByNameAscending();
            case NAME_ZA -> assertThatInventory(inventoryPage).hasProductsSortedByNameDescending();
            case PRICE_LOW_HIGH -> assertThatInventory(inventoryPage).hasProductsSortedByPriceAscending();
            case PRICE_HIGH_LOW -> assertThatInventory(inventoryPage).hasProductsSortedByPriceDescending();
        }
    }

    @Test
    @Story("Product Details")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Navigate to product details page and return back to catalog")
    @Description("Verify that clicking on product title opens the detailed product view, and Back to products returns to catalog.")
    void testNavigateToProductDetailsAndBack() {
        String targetProduct = "Sauce Labs Backpack";

        ProductDetailsPage detailsPage = inventoryPage.openProductDetails(targetProduct);

        assertThat(detailsPage.getTitle())
                .as("Product details title should match clicked item")
                .isEqualTo(targetProduct);

        assertThat(detailsPage.getPrice())
                .as("Product price on details page should be positive")
                .isEqualTo(29.99);

        assertThat(detailsPage.getActionButtonText())
                .as("Action button should initially show 'Add to cart'")
                .isEqualTo("Add to cart");

        InventoryPage returnedPage = detailsPage.clickBackToProducts();

        assertThat(returnedPage.getUrl())
                .as("Should be back on inventory page")
                .contains("/inventory.html");

        assertThatInventory(returnedPage).hasProductCount(6);
    }
}
