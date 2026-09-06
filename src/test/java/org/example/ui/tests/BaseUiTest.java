package org.example.ui.tests;

import com.microsoft.playwright.Page;
import org.example.ui.core.PlaywrightAllureExtension;
import org.example.ui.core.PlaywrightDriverManager;
import org.example.ui.pages.CartPage;
import org.example.ui.pages.CheckoutCompletePage;
import org.example.ui.pages.CheckoutStepOnePage;
import org.example.ui.pages.CheckoutStepTwoPage;
import org.example.ui.pages.InventoryPage;
import org.example.ui.pages.LoginPage;
import org.example.ui.pages.ProductDetailsPage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(PlaywrightAllureExtension.class)
public abstract class BaseUiTest {

    protected Page getPage() {
        return PlaywrightDriverManager.getPage();
    }

    protected LoginPage loginPage() {
        return new LoginPage(getPage());
    }

    protected InventoryPage inventoryPage() {
        return new InventoryPage(getPage());
    }

    protected ProductDetailsPage productDetailsPage() {
        return new ProductDetailsPage(getPage());
    }

    protected CartPage cartPage() {
        return new CartPage(getPage());
    }

    protected CheckoutStepOnePage checkoutStepOnePage() {
        return new CheckoutStepOnePage(getPage());
    }

    protected CheckoutStepTwoPage checkoutStepTwoPage() {
        return new CheckoutStepTwoPage(getPage());
    }

    protected CheckoutCompletePage checkoutCompletePage() {
        return new CheckoutCompletePage(getPage());
    }
}
