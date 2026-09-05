package org.example.ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
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
@Owner("QA Automation Team")
@Tag("ui")
public class CartUiTest extends BaseUiTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        inventoryPage = loginPage().open().loginAs(UserCredentials.STANDARD_USER);
    }

    @Test
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
