package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.components.HeaderComponent;
import org.example.ui.data.CheckoutInfo;

public class CheckoutStepOnePage extends BasePage {

    private final HeaderComponent header;
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator postalCodeInput;
    private final Locator continueButton;
    private final Locator cancelButton;
    private final Locator errorMessage;

    public CheckoutStepOnePage(Page page) {
        super(page);
        this.header = new HeaderComponent(page);
        this.firstNameInput = page.locator("[data-test='firstName']");
        this.lastNameInput = page.locator("[data-test='lastName']");
        this.postalCodeInput = page.locator("[data-test='postalCode']");
        this.continueButton = page.locator("[data-test='continue']");
        this.cancelButton = page.locator("[data-test='cancel']");
        this.errorMessage = page.locator("[data-test='error']");
    }

    public HeaderComponent getHeader() {
        return header;
    }

    @Step("Enter first name: {firstName}")
    public CheckoutStepOnePage fillFirstName(String firstName) {
        log.info("Entering first name: {}", firstName);
        firstNameInput.fill(firstName);
        return this;
    }

    @Step("Enter last name: {lastName}")
    public CheckoutStepOnePage fillLastName(String lastName) {
        log.info("Entering last name: {}", lastName);
        lastNameInput.fill(lastName);
        return this;
    }

    @Step("Enter postal code: {postalCode}")
    public CheckoutStepOnePage fillPostalCode(String postalCode) {
        log.info("Entering postal code: {}", postalCode);
        postalCodeInput.fill(postalCode);
        return this;
    }

    @Step("Fill customer information: {info}")
    public CheckoutStepOnePage fillInformation(CheckoutInfo info) {
        if (info.getFirstName() != null) {
            fillFirstName(info.getFirstName());
        }
        if (info.getLastName() != null) {
            fillLastName(info.getLastName());
        }
        if (info.getPostalCode() != null) {
            fillPostalCode(info.getPostalCode());
        }
        return this;
    }

    @Step("Click 'Continue' to step two")
    public CheckoutStepTwoPage clickContinue() {
        log.info("Clicking 'Continue' to overview");
        continueButton.click();
        return new CheckoutStepTwoPage(page);
    }

    @Step("Click 'Cancel' and return to cart")
    public CartPage clickCancel() {
        log.info("Canceling checkout step one");
        cancelButton.click();
        return new CartPage(page);
    }

    @Step("Get checkout step one error message text")
    public String getErrorMessageText() {
        return errorMessage.innerText().trim();
    }

    @Step("Check if error message is visible on checkout step one")
    public boolean isErrorMessageVisible() {
        return errorMessage.isVisible();
    }
}
