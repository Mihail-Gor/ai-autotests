package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;
import org.example.ui.components.ProductCardComponent;
import org.example.ui.data.SortOption;

import java.util.ArrayList;
import java.util.List;

public class InventoryPage extends BasePage {

    private final HeaderComponent header;
    private final Locator sortDropdown;
    private final Locator inventoryItems;

    public InventoryPage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.sortDropdown = page.locator("[data-test='product-sort-container']");
        this.inventoryItems = page.locator(".inventory_item");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Open inventory / catalog page")
    public InventoryPage open() {
        navigate("/inventory.html");
        return this;
    }

    @Step("Get count of visible products in catalog")
    public int getProductCount() {
        return inventoryItems.count();
    }

    @Step("Get list of all product card components")
    public List<ProductCardComponent> getProductCards() {
        List<ProductCardComponent> cards = new ArrayList<>();
        int count = inventoryItems.count();
        for (int i = 0; i < count; i++) {
            cards.add(new ProductCardComponent(page, inventoryItems.nth(i)));
        }
        return cards;
    }

    @Step("Get list of all product names in catalog")
    public List<String> getProductNames() {
        return inventoryItems.locator(".inventory_item_name").allInnerTexts();
    }

    @Step("Get list of all product prices in catalog")
    public List<Double> getProductPrices() {
        List<String> priceTexts = inventoryItems.locator(".inventory_item_price").allInnerTexts();
        List<Double> prices = new ArrayList<>();
        for (String text : priceTexts) {
            prices.add(Double.parseDouble(text.replace("$", "").trim()));
        }
        return prices;
    }

    @Step("Select sorting option: {option}")
    public InventoryPage selectSortOption(SortOption option) {
        log.info("Selecting sort option: {} ({})", option.name(), option.getValue());
        sortDropdown.selectOption(option.getValue());
        return this;
    }

    @Step("Get product card by name: {productName}")
    public ProductCardComponent getProductCard(String productName) {
        Locator item = inventoryItems.filter(new Locator.FilterOptions().setHasText(productName)).first();
        return new ProductCardComponent(page, item);
    }

    @Step("Add product to cart: {productName}")
    public InventoryPage addProductToCart(String productName) {
        log.info("Adding product to cart: {}", productName);
        getProductCard(productName).clickActionButton();
        return this;
    }

    @Step("Remove product from cart: {productName}")
    public InventoryPage removeProductFromCart(String productName) {
        log.info("Removing product from cart: {}", productName);
        getProductCard(productName).clickActionButton();
        return this;
    }

    @Step("Click product title to open details: {productName}")
    public ProductDetailsPage openProductDetails(String productName) {
        log.info("Opening product details for: {}", productName);
        getProductCard(productName).clickTitle();
        return new ProductDetailsPage(page);
    }

    @Step("Open shopping cart page")
    public CartPage openCart() {
        header.openCart();
        return new CartPage(page);
    }
}
