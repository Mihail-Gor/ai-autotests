package org.example.ui.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    public Page getPage() {
        return page;
    }

    @Step("Navigate to path: {path}")
    public void navigate(String path) {
        log.info("Navigating to: {}", path);
        page.navigate(path);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @Step("Get current page URL")
    public String getUrl() {
        return page.url();
    }

    @Step("Get page title")
    public String getTitle() {
        return page.title();
    }

    @Step("Reload page")
    public void reload() {
        log.info("Reloading page: {}", page.url());
        page.reload();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    @Step("Go back in browser history")
    public void goBack() {
        log.info("Navigating back from: {}", page.url());
        page.goBack();
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    public void waitForUrlContains(String text) {
        page.waitForURL(url -> url.contains(text));
    }

    public void waitForSelector(String selector, WaitForSelectorState state) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(state));
    }
}
