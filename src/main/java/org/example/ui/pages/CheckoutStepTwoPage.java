package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;

import java.util.List;

public class CheckoutStepTwoPage extends BasePage {

    private final HeaderComponent header;
    private final Locator cartItems;
    private final Locator paymentInfo;
    private final Locator shippingInfo;
    private final Locator itemTotalLabel;
    private final Locator taxLabel;
    private final Locator totalLabel;
    private final Locator finishButton;
    private final Locator cancelButton;

    public CheckoutStepTwoPage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.cartItems = page.locator(".cart_item");
        this.paymentInfo = page.locator(".summary_value_label").first();
        this.shippingInfo = page.locator(".summary_value_label").nth(1);
        this.itemTotalLabel = page.locator(".summary_subtotal_label");
        this.taxLabel = page.locator(".summary_tax_label");
        this.totalLabel = page.locator(".summary_total_label");
        this.finishButton = page.locator("[data-test='finish']");
        this.cancelButton = page.locator("[data-test='cancel']");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Get list of overview item names")
    public List<String> getItemNames() {
        return cartItems.locator(".inventory_item_name").allInnerTexts();
    }

    @Step("Get payment information")
    public String getPaymentInfo() {
        return paymentInfo.innerText().trim();
    }

    @Step("Get shipping information")
    public String getShippingInfo() {
        return shippingInfo.innerText().trim();
    }

    @Step("Get subtotal (item total) amount")
    public double getItemTotal() {
        String text = itemTotalLabel.innerText();
        return parseAmount(text);
    }

    @Step("Get tax amount")
    public double getTax() {
        String text = taxLabel.innerText();
        return parseAmount(text);
    }

    @Step("Get total order amount")
    public double getTotal() {
        String text = totalLabel.innerText();
        return parseAmount(text);
    }

    @Step("Click 'Finish' order button")
    public CheckoutCompletePage clickFinish() {
        log.info("Finishing order checkout");
        finishButton.click();
        return new CheckoutCompletePage(page);
    }

    @Step("Click 'Cancel' and return to inventory")
    public InventoryPage clickCancel() {
        log.info("Canceling checkout step two");
        cancelButton.click();
        return new InventoryPage(page);
    }

    private double parseAmount(String text) {
        // e.g. "Item total: $29.99" -> 29.99
        int dollarIndex = text.indexOf('$');
        if (dollarIndex >= 0) {
            String num = text.substring(dollarIndex + 1).trim();
            return Double.parseDouble(num);
        }
        return 0.0;
    }
}
