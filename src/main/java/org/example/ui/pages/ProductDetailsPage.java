package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;

public class ProductDetailsPage extends BasePage {

    private final HeaderComponent header;
    private final Locator backToProductsButton;
    private final Locator productTitle;
    private final Locator productDescription;
    private final Locator productPrice;
    private final Locator actionButton;

    public ProductDetailsPage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.backToProductsButton = page.locator("[data-test='back-to-products']");
        this.productTitle = page.locator(".inventory_details_name");
        this.productDescription = page.locator(".inventory_details_desc");
        this.productPrice = page.locator(".inventory_details_price");
        this.actionButton = page.locator(".inventory_details_container button");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Click 'Back to products' button")
    public InventoryPage clickBackToProducts() {
        log.info("Clicking 'Back to products' button");
        backToProductsButton.click();
        return new InventoryPage(page);
    }

    @Step("Get product title on details page")
    public String getTitle() {
        return productTitle.innerText().trim();
    }

    @Step("Get product description on details page")
    public String getDescription() {
        return productDescription.innerText().trim();
    }

    @Step("Get product price text on details page")
    public String getPriceText() {
        return productPrice.innerText().trim();
    }

    @Step("Get numeric product price on details page")
    public double getPrice() {
        return Double.parseDouble(getPriceText().replace("$", "").trim());
    }

    @Step("Click action button on details page (Add to Cart / Remove)")
    public ProductDetailsPage clickActionButton() {
        log.info("Clicking action button on details page");
        actionButton.click();
        return this;
    }

    @Step("Get action button text on details page")
    public String getActionButtonText() {
        return actionButton.innerText().trim();
    }
}
