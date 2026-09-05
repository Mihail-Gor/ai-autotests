package org.example.ui.tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.example.ui.data.UserCredentials;
import org.example.ui.pages.InventoryPage;
import org.example.ui.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("UI Automation")
@Feature("Authentication & Authorization")
@Owner("QA Automation Team")
@Tag("ui")
public class LoginUiTest extends BaseUiTest {

    @Test
    @Story("User Login")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Successful login with standard user credentials")
    @Description("Verify that a standard user can successfully log in and is redirected to the product inventory catalog.")
    void testSuccessfulLogin() {
        LoginPage loginPage = loginPage().open();

        InventoryPage inventoryPage = loginPage.loginAs(UserCredentials.STANDARD_USER);

        assertThat(inventoryPage.getUrl())
                .as("User should be redirected to inventory page")
                .contains("/inventory.html");

        assertThat(inventoryPage.getHeader().getPageTitle())
                .as("Inventory header title should be displayed")
                .isEqualTo("Products");

        assertThat(inventoryPage.getProductCount())
                .as("Products catalog should not be empty")
                .isGreaterThan(0);
    }

    @Test
    @Story("User Login")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @DisplayName("Login attempt with locked-out user displays access error banner")
    @Description("Verify that attempting to log in as a locked-out user shows the proper lockout error message.")
    void testLockedOutUserLogin() {
        LoginPage loginPage = loginPage().open();

        loginPage.loginAs(UserCredentials.LOCKED_OUT_USER);

        assertThat(loginPage.isErrorMessageVisible())
                .as("Error message banner should be visible")
                .isTrue();

        assertThat(loginPage.getErrorMessageText())
                .as("Error message should indicate that the user is locked out")
                .contains("Epic sadface: Sorry, this user has been locked out.");
    }

    @ParameterizedTest(name = "Login with invalid username: \"{0}\", password: \"{1}\"")
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @CsvSource({
            "invalid_user, secret_sauce, Epic sadface: Username and password do not match any user in this service",
            "standard_user, wrong_pass, Epic sadface: Username and password do not match any user in this service"
    })
    @DisplayName("Login attempt with invalid credentials displays error message")
    @Description("Verify that entering invalid credentials shows the appropriate mismatch error message.")
    void testInvalidCredentialsLogin(String username, String password, String expectedError) {
        LoginPage loginPage = loginPage().open();

        loginPage.login(username, password);

        assertThat(loginPage.isErrorMessageVisible())
                .as("Error banner should be visible")
                .isTrue();

        assertThat(loginPage.getErrorMessageText())
                .as("Error text should match expected message")
                .contains(expectedError);
    }

    @Test
    @Story("User Login")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Login attempt with empty username displays validation error")
    @Description("Verify that submitting empty username field displays 'Username is required' validation error.")
    void testEmptyUsernameValidation() {
        LoginPage loginPage = loginPage().open();

        loginPage.clickLogin();

        assertThat(loginPage.getErrorMessageText())
                .as("Error should specify that username is required")
                .contains("Epic sadface: Username is required");
    }

    @Test
    @Story("User Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Successful logout redirects back to login page")
    @Description("Verify that logging out from sidebar menu redirects user back to login page and clears session.")
    void testSuccessfulLogout() {
        LoginPage loginPage = loginPage().open();
        InventoryPage inventoryPage = loginPage.loginAs(UserCredentials.STANDARD_USER);

        inventoryPage.getHeader().openMenu().clickLogout();

        assertThat(loginPage.getUrl())
                .as("User should be redirected to login root")
                .doesNotContain("/inventory.html");

        assertThat(loginPage.isLoginFormDisplayed())
                .as("Login form should be displayed again after logout")
                .isTrue();
    }
}
