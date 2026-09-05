package org.example.ui.tests;

import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.example.common.annotations.Component;
import org.example.common.annotations.JiraIssue;
import org.example.common.annotations.Layer;
import org.example.common.annotations.Layers;
import org.example.common.annotations.Microservice;
import org.example.ui.data.UserCredentials;
import org.example.ui.pages.CartPage;
import org.example.ui.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.example.ui.assertions.CartAssert.assertThatCart;
import static org.example.ui.assertions.InventoryAssert.assertThatInventory;

@Epic("UI Automation")
@Feature("Shopping Cart")
@Layer(Layers.UI)
@Microservice("frontend-web-application")
@Component("Cart")
@Owner("QA Automation Team")
@Tag("ui")
public class CartUiTest extends BaseUiTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        inventoryPage = loginPage().open().loginAs(UserCredentials.STANDARD_USER);
    }

    @Test
    @AllureId("2020")
    @TmsLink("TMS-2020")
    @JiraIssue("CART-101")
    @Story("Add to Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Add single product to cart updates badge and cart items")
    @Description("Verify that clicking Add to Cart for a product increments the header badge count and adds the item into the cart.")
    void testAddSingleProductToCart() {
        String product = "Sauce Labs Backpack";

        inventoryPage.addProductToCart(product);

        assertThatInventory(inventoryPage).hasCartBadgeCount(1);

        CartPage cartPage = inventoryPage.openCart();

        assertThatCart(cartPage)
                .hasItemCount(1)
                .containsProduct(product);
    }

    @Test
    @AllureId("2021")
    @TmsLink("TMS-2021")
    @JiraIssue("CART-102")
    @Story("Add to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @DisplayName("Add multiple products to cart updates badge count appropriately")
    @Description("Verify that adding multiple items correctly increments badge count and lists all items in cart.")
    void testAddMultipleProductsToCart() {
        String product1 = "Sauce Labs Backpack";
        String product2 = "Sauce Labs Bike Light";

        inventoryPage.addProductToCart(product1)
                .addProductToCart(product2);

        assertThatInventory(inventoryPage).hasCartBadgeCount(2);

        CartPage cartPage = inventoryPage.openCart();

        assertThatCart(cartPage)
                .hasItemCount(2)
                .containsProduct(product1)
                .containsProduct(product2);
    }

    @Test
    @AllureId("2022")
    @TmsLink("TMS-2022")
    @JiraIssue("CART-103")
    @Story("Remove from Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @DisplayName("Remove product directly from catalog page removes badge")
    @Description("Verify that removing an added product from the inventory page resets the badge.")
    void testRemoveProductFromInventory() {
        String product = "Sauce Labs Bolt T-Shirt";

        inventoryPage.addProductToCart(product);
        assertThatInventory(inventoryPage).hasCartBadgeCount(1);

        inventoryPage.removeProductFromCart(product);
        assertThatInventory(inventoryPage).hasCartBadgeHidden();
    }

    @Test
    @AllureId("2023")
    @TmsLink("TMS-2023")
    @JiraIssue("CART-104")
    @Story("Remove from Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @DisplayName("Remove product from cart page updates item list")
    @Description("Verify that removing an item inside the shopping cart page updates the cart and badge.")
    void testRemoveProductFromCartPage() {
        String product1 = "Sauce Labs Fleece Jacket";
        String product2 = "Sauce Labs Onesie";

        inventoryPage.addProductToCart(product1)
                .addProductToCart(product2);

        CartPage cartPage = inventoryPage.openCart();
        assertThatCart(cartPage).hasItemCount(2);

        cartPage.removeItem(product1);

        assertThatCart(cartPage)
                .hasItemCount(1)
                .containsProduct(product2)
                .doesNotContainProduct(product1);
    }

    @Test
    @AllureId("2024")
    @TmsLink("TMS-2024")
    @JiraIssue("CART-105")
    @Story("Cart Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Clicking 'Continue Shopping' returns from cart to catalog")
    @Description("Verify that clicking the Continue Shopping button in cart navigates back to inventory.")
    void testContinueShoppingFromCart() {
        CartPage cartPage = inventoryPage.openCart();

        InventoryPage returnedPage = cartPage.continueShopping();

        assertThat(returnedPage.getUrl())
                .as("Should return to inventory page")
                .contains("/inventory.html");

        assertThatInventory(returnedPage).hasProductCount(6);
    }
}
