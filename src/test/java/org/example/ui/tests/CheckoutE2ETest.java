package org.example.ui.tests;

import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.qameta.allure.TmsLink;
import org.example.common.annotations.Component;
import org.example.common.annotations.JiraIssue;
import org.example.common.annotations.Layer;
import org.example.common.annotations.Layers;
import org.example.common.annotations.Microservice;
import org.example.ui.data.CheckoutInfo;
import org.example.ui.data.UserCredentials;
import org.example.ui.pages.CartPage;
import org.example.ui.pages.CheckoutCompletePage;
import org.example.ui.pages.CheckoutStepOnePage;
import org.example.ui.pages.CheckoutStepTwoPage;
import org.example.ui.pages.InventoryPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@Epic("UI Automation")
@Feature("Order Checkout E2E")
@Layer(Layers.UI)
@Microservice("frontend-web-application")
@Component("Checkout")
@Owner("QA Automation Team")
@Tag("ui")
public class CheckoutE2ETest extends BaseUiTest {

    private InventoryPage inventoryPage;

    @BeforeEach
    void setUp() {
        inventoryPage = loginPage().open().loginAs(UserCredentials.STANDARD_USER);
    }

    @Test
    @AllureId("2030")
    @TmsLink("TMS-2030")
    @JiraIssue("CHECKOUT-101")
    @Story("End-to-End Order Placement")
    @Severity(SeverityLevel.BLOCKER)
    @Tag("smoke")
    @Tag("regression")
    @DisplayName("Complete end-to-end checkout purchase flow")
    @Description("Verify that user can select products, fill shipping info, review tax and totals, finish order and view confirmation.")
    void testFullE2ECheckoutFlow() {
        String product1 = "Sauce Labs Backpack";
        String product2 = "Sauce Labs Bike Light";

        // 1. Add items to cart
        inventoryPage.addProductToCart(product1)
                .addProductToCart(product2);

        // 2. Open cart and proceed to checkout
        CartPage cartPage = inventoryPage.openCart();
        CheckoutStepOnePage stepOnePage = cartPage.proceedToCheckout();

        // 3. Fill customer info
        CheckoutInfo customerInfo = CheckoutInfo.random();
        stepOnePage.fillInformation(customerInfo);
        CheckoutStepTwoPage stepTwoPage = stepOnePage.clickContinue();

        // 4. Verify checkout overview
        assertThat(stepTwoPage.getItemNames())
                .as("Checkout overview should contain selected products")
                .containsExactly(product1, product2);

        double itemTotal = stepTwoPage.getItemTotal();
        double tax = stepTwoPage.getTax();
        double total = stepTwoPage.getTotal();

        assertThat(itemTotal)
                .as("Item total should be $39.98 (29.99 + 9.99)")
                .isCloseTo(39.98, within(0.01));

        assertThat(tax)
                .as("Tax should be positive")
                .isGreaterThan(0.0);

        assertThat(total)
                .as("Total should equal item total + tax")
                .isCloseTo(itemTotal + tax, within(0.01));

        // 5. Finish order
        CheckoutCompletePage completePage = stepTwoPage.clickFinish();

        assertThat(completePage.isOrderCompleted())
                .as("Order completion screen should be visible")
                .isTrue();

        assertThat(completePage.getCompleteHeaderText())
                .as("Confirmation header text should match")
                .isEqualTo("Thank you for your order!");

        // 6. Back home
        InventoryPage homePage = completePage.clickBackHome();
        assertThat(homePage.getUrl()).contains("/inventory.html");
        assertThat(homePage.getHeader().getCartBadgeCount()).isEqualTo(0);
    }

    @ParameterizedTest(name = "Validation error when missing: {0}")
    @AllureId("2031")
    @TmsLink("TMS-2031")
    @JiraIssue("CHECKOUT-102")
    @Story("Checkout Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Tag("regression")
    @MethodSource("invalidCheckoutDataProvider")
    @DisplayName("Checkout step one mandatory fields validation")
    @Description("Verify that missing required customer information triggers appropriate error messages.")
    void testCheckoutStepOneValidationErrors(String missingField, CheckoutInfo info, String expectedError) {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.openCart();
        CheckoutStepOnePage stepOnePage = cartPage.proceedToCheckout();

        stepOnePage.fillInformation(info);
        stepOnePage.clickContinue();

        assertThat(stepOnePage.isErrorMessageVisible())
                .as("Validation error should be displayed")
                .isTrue();

        assertThat(stepOnePage.getErrorMessageText())
                .as("Error text should indicate missing field")
                .contains(expectedError);
    }

    static Stream<Arguments> invalidCheckoutDataProvider() {
        return Stream.of(
                Arguments.of("First Name", CheckoutInfo.builder().firstName("").lastName("Doe").postalCode("12345").build(), "Error: First Name is required"),
                Arguments.of("Last Name", CheckoutInfo.builder().firstName("John").lastName("").postalCode("12345").build(), "Error: Last Name is required"),
                Arguments.of("Postal Code", CheckoutInfo.builder().firstName("John").lastName("Doe").postalCode("").build(), "Error: Postal Code is required")
        );
    }

    @Test
    @AllureId("2032")
    @TmsLink("TMS-2032")
    @JiraIssue("CHECKOUT-103")
    @Story("Checkout Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Cancel from checkout step one returns to cart")
    @Description("Verify that clicking Cancel on checkout step one navigates back to cart.")
    void testCancelCheckoutStepOneReturnsToCart() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.openCart();
        CheckoutStepOnePage stepOnePage = cartPage.proceedToCheckout();

        CartPage returnedCart = stepOnePage.clickCancel();

        assertThat(returnedCart.getUrl()).contains("/cart.html");
        assertThat(returnedCart.getItemCount()).isEqualTo(1);
    }

    @Test
    @AllureId("2033")
    @TmsLink("TMS-2033")
    @JiraIssue("CHECKOUT-104")
    @Story("Checkout Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Tag("regression")
    @DisplayName("Cancel from checkout step two returns to inventory")
    @Description("Verify that clicking Cancel on checkout overview returns to inventory page.")
    void testCancelCheckoutStepTwoReturnsToInventory() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        CartPage cartPage = inventoryPage.openCart();
        CheckoutStepOnePage stepOnePage = cartPage.proceedToCheckout();

        stepOnePage.fillInformation(CheckoutInfo.standard());
        CheckoutStepTwoPage stepTwoPage = stepOnePage.clickContinue();

        InventoryPage returnedInventory = stepTwoPage.clickCancel();

        assertThat(returnedInventory.getUrl()).contains("/inventory.html");
        assertThat(returnedInventory.getProductCount()).isEqualTo(6);
    }
}
