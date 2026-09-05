package org.example.ui.components;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

public class SidebarMenuComponent extends BaseComponent {

    private final Locator menuContainer;
    private final Locator allItemsLink;
    private final Locator aboutLink;
    private final Locator logoutLink;
    private final Locator resetAppStateLink;
    private final Locator closeButton;

    public SidebarMenuComponent(Page page) {
        super(page);
        this.menuContainer = page.locator(".bm-menu-wrap");
        this.allItemsLink = page.locator("#inventory_sidebar_link");
        this.aboutLink = page.locator("#about_sidebar_link");
        this.logoutLink = page.locator("#logout_sidebar_link");
        this.resetAppStateLink = page.locator("#reset_sidebar_link");
        this.closeButton = page.locator("#react-burger-cross-btn");
    }

    @Step("Wait for sidebar menu to open")
    public void waitForOpen() {
        menuContainer.waitFor();
        logoutLink.waitFor();
    }

    @Step("Click 'All Items' in sidebar menu")
    public void clickAllItems() {
        log.info("Clicking 'All Items' link");
        allItemsLink.click();
    }

    @Step("Click 'About' in sidebar menu")
    public void clickAbout() {
        log.info("Clicking 'About' link");
        aboutLink.click();
    }

    @Step("Click 'Logout' in sidebar menu")
    public void clickLogout() {
        log.info("Clicking 'Logout' link");
        logoutLink.click();
    }

    @Step("Click 'Reset App State' in sidebar menu")
    public void clickResetAppState() {
        log.info("Clicking 'Reset App State' link");
        resetAppStateLink.click();
    }

    @Step("Close sidebar menu")
    public void close() {
        log.info("Closing sidebar menu");
        closeButton.click();
    }
}
