package org.example.ui.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import org.example.api.config.ConfigManager;
import org.example.api.config.ProjectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlaywrightDriverManager {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightDriverManager.class);
    private static final ProjectConfig config = ConfigManager.getConfig();

    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    private PlaywrightDriverManager() {
    }

    public static Page initDriver() {
        return initDriver(BrowserType.fromString(config.uiBrowser()));
    }

    public static Page initDriver(BrowserType browserType) {
        log.info("Initializing Playwright driver for thread: {}", Thread.currentThread().getName());

        Playwright playwright = Playwright.create();
        playwrightThreadLocal.set(playwright);

        Browser browser = BrowserFactory.createBrowser(playwright, browserType);
        browserThreadLocal.set(browser);

        NewContextOptions contextOptions = new NewContextOptions()
                .setBaseURL(config.uiBaseUrl())
                .setViewportSize(1920, 1080)
                .setIgnoreHTTPSErrors(true);

        BrowserContext context = browser.newContext(contextOptions);
        contextThreadLocal.set(context);

        if (config.uiTracingEnabled()) {
            log.info("Starting Playwright trace recording for thread: {}", Thread.currentThread().getName());
            context.tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        Page page = context.newPage();
        page.setDefaultTimeout(config.uiTimeoutMs());
        page.setDefaultNavigationTimeout(config.uiTimeoutMs());
        pageThreadLocal.set(page);

        return page;
    }

    public static Page getPage() {
        if (pageThreadLocal.get() == null) {
            initDriver();
        }
        return pageThreadLocal.get();
    }

    public static BrowserContext getContext() {
        return contextThreadLocal.get();
    }

    public static Browser getBrowser() {
        return browserThreadLocal.get();
    }

    public static Playwright getPlaywright() {
        return playwrightThreadLocal.get();
    }

    public static void closeDriver() {
        try {
            Page page = pageThreadLocal.get();
            if (page != null) {
                page.close();
            }
        } catch (Exception e) {
            log.warn("Error closing Page: {}", e.getMessage());
        } finally {
            pageThreadLocal.remove();
        }

        try {
            BrowserContext context = contextThreadLocal.get();
            if (context != null) {
                context.close();
            }
        } catch (Exception e) {
            log.warn("Error closing BrowserContext: {}", e.getMessage());
        } finally {
            contextThreadLocal.remove();
        }

        try {
            Browser browser = browserThreadLocal.get();
            if (browser != null) {
                browser.close();
            }
        } catch (Exception e) {
            log.warn("Error closing Browser: {}", e.getMessage());
        } finally {
            browserThreadLocal.remove();
        }

        try {
            Playwright playwright = playwrightThreadLocal.get();
            if (playwright != null) {
                playwright.close();
            }
        } catch (Exception e) {
            log.warn("Error closing Playwright: {}", e.getMessage());
        } finally {
            playwrightThreadLocal.remove();
        }

        log.info("Playwright driver closed for thread: {}", Thread.currentThread().getName());
    }
}
