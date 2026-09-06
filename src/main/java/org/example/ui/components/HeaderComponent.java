package org.example.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import io.qameta.allure.Step;

public class HeaderComponent extends BaseComponent {

    private final Locator menuButton;
    private final Locator appLogo;
    private final Locator cartLink;
    private final Locator cartBadge;
    private final Locator pageTitle;

    public HeaderComponent(Page page) {
        super(page);
        this.menuButton = page.locator("#react-burger-menu-btn");
        this.appLogo = page.locator(".app_logo");
        this.cartLink = page.locator(".shopping_cart_link");
        this.cartBadge = page.locator(".shopping_cart_badge");
        this.pageTitle = page.locator(".title");
    }

    @Step("Open sidebar navigation menu")
    public SidebarMenuComponent openMenu() {
        log.info("Opening sidebar navigation menu");
        menuButton.click();
        SidebarMenuComponent sidebar = new SidebarMenuComponent(page);
        sidebar.waitForOpen();
        return sidebar;
    }

    @Step("Click shopping cart icon in header")
    public void openCart() {
        log.info("Clicking shopping cart icon");
        cartLink.click();
    }

    @Step("Get shopping cart badge count")
    public int getCartBadgeCount() {
        if (cartBadge.isVisible()) {
            String text = cartBadge.innerText().trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    @Step("Check if shopping cart badge is displayed")
    public boolean isCartBadgeVisible() {
        return cartBadge.isVisible();
    }

    @Step("Get header page title text")
    public String getPageTitle() {
        return pageTitle.innerText();
    }

    @Step("Get application logo text")
    public String getAppLogoText() {
        return appLogo.innerText();
    }
}
