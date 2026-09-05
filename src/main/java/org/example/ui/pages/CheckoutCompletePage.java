package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;

public class CheckoutCompletePage extends BasePage {

    private final HeaderComponent header;
    private final Locator completeHeader;
    private final Locator completeText;
    private final Locator backHomeButton;

    public CheckoutCompletePage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.completeHeader = page.locator(".complete-header");
        this.completeText = page.locator(".complete-text");
        this.backHomeButton = page.locator("[data-test='back-to-products']");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Get order complete header text")
    public String getCompleteHeaderText() {
        return completeHeader.innerText().trim();
    }

    @Step("Get order complete description text")
    public String getCompleteText() {
        return completeText.innerText().trim();
    }

    @Step("Check if order completion banner is displayed")
    public boolean isOrderCompleted() {
        return completeHeader.isVisible() && backHomeButton.isVisible();
    }

    @Step("Click 'Back Home' button")
    public InventoryPage clickBackHome() {
        log.info("Clicking 'Back Home'");
        backHomeButton.click();
        return new InventoryPage(page);
    }
}
