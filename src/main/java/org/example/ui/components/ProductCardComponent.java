package org.example.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class ProductCardComponent extends BaseComponent {

    private final Locator titleElement;
    private final Locator descriptionElement;
    private final Locator priceElement;
    private final Locator actionButton;
    private final Locator imageElement;

    public ProductCardComponent(Page page, Locator root) {
        super(page, root);
        this.titleElement = root.locator(".inventory_item_name");
        this.descriptionElement = root.locator(".inventory_item_desc");
        this.priceElement = root.locator(".inventory_item_price");
        this.actionButton = root.locator("button");
        this.imageElement = root.locator(".inventory_item_img img");
    }

    @Step("Get product title")
    public String getTitle() {
        return titleElement.innerText().trim();
    }

    @Step("Get product description")
    public String getDescription() {
        return descriptionElement.innerText().trim();
    }

    @Step("Get product price text")
    public String getPriceText() {
        return priceElement.innerText().trim();
    }

    @Step("Get numeric product price")
    public double getPrice() {
        String text = getPriceText().replace("$", "").trim();
        return Double.parseDouble(text);
    }

    @Step("Click product title to open details")
    public void clickTitle() {
        log.info("Clicking product title: {}", getTitle());
        titleElement.click();
    }

    @Step("Click product action button (Add to Cart / Remove)")
    public void clickActionButton() {
        log.info("Clicking action button for product: {}", getTitle());
        actionButton.click();
    }

    @Step("Get action button text")
    public String getActionButtonText() {
        return actionButton.innerText().trim();
    }

    @Step("Get product image src")
    public String getImageSrc() {
        return imageElement.getAttribute("src");
    }
}
