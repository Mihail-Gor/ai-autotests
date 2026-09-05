package org.example.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.example.ui.data.UserCredentials;

public class LoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;
    private final Locator errorMessage;
    private final Locator errorCloseButton;

    public LoginPage(Page page) {
        super(page);
        this.usernameInput = page.locator("[data-test='username']");
        this.passwordInput = page.locator("[data-test='password']");
        this.loginButton = page.locator("[data-test='login-button']");
        this.errorMessage = page.locator("[data-test='error']");
        this.errorCloseButton = page.locator("[data-test='error'] button");
    }

    @Step("Open Login page")
    public LoginPage open() {
        navigate("/");
        return this;
    }

    @Step("Enter username: {username}")
    public LoginPage fillUsername(String username) {
        log.info("Entering username: {}", username);
        usernameInput.fill(username);
        return this;
    }

    @Step("Enter password")
    public LoginPage fillPassword(String password) {
        log.info("Entering password");
        passwordInput.fill(password);
        return this;
    }

    @Step("Click Login button")
    public void clickLogin() {
        log.info("Clicking Login button");
        loginButton.click();
    }

    @Step("Log in with username: {username}")
    public InventoryPage login(String username, String password) {
        fillUsername(username);
        fillPassword(password);
        clickLogin();
        return new InventoryPage(page);
    }

    @Step("Log in with preset user: {credentials}")
    public InventoryPage loginAs(UserCredentials credentials) {
        log.info("Logging in as preset: {}", credentials.name());
        return login(credentials.getUsername(), credentials.getPassword());
    }

    @Step("Get login error message text")
    public String getErrorMessageText() {
        return errorMessage.innerText().trim();
    }

    @Step("Check if login error message is visible")
    public boolean isErrorMessageVisible() {
        return errorMessage.isVisible();
    }

    @Step("Close login error message")
    public void closeErrorMessage() {
        log.info("Closing login error message");
        errorCloseButton.click();
    }

    @Step("Check if login form is displayed")
    public boolean isLoginFormDisplayed() {
        return usernameInput.isVisible() && passwordInput.isVisible() && loginButton.isVisible();
    }
}
