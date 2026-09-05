package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;

import java.util.List;

public class CartPage extends BasePage {

    private final HeaderComponent header;
    private final Locator cartItems;
    private final Locator continueShoppingButton;
    private final Locator checkoutButton;

    public CartPage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.cartItems = page.locator(".cart_item");
        this.continueShoppingButton = page.locator("[data-test='continue-shopping']");
        this.checkoutButton = page.locator("[data-test='checkout']");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Open shopping cart page")
    public CartPage open() {
        navigate("/cart.html");
        return this;
    }

    @Step("Get count of items in cart")
    public int getItemCount() {
        return cartItems.count();
    }

    @Step("Get list of item names in cart")
    public List<String> getItemNames() {
        return cartItems.locator(".inventory_item_name").allInnerTexts();
    }

    @Step("Remove item from cart: {productName}")
    public CartPage removeItem(String productName) {
        log.info("Removing item from cart: {}", productName);
        Locator item = cartItems.filter(new Locator.FilterOptions().setHasText(productName));
        item.locator("button").click();
        return this;
    }

    @Step("Click 'Continue Shopping' button")
    public InventoryPage continueShopping() {
        log.info("Clicking 'Continue Shopping'");
        continueShoppingButton.click();
        return new InventoryPage(page);
    }

    @Step("Click 'Checkout' button")
    public CheckoutStepOnePage proceedToCheckout() {
        log.info("Proceeding to checkout");
        checkoutButton.click();
        return new CheckoutStepOnePage(page);
    }
}
