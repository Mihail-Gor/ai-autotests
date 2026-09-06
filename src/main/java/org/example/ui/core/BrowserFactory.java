package org.example.ui.core;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Playwright;
import org.example.api.config.ConfigManager;
import org.example.api.config.ProjectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public final class BrowserFactory {

    private static final Logger log = LoggerFactory.getLogger(BrowserFactory.class);
    private static final ProjectConfig config = ConfigManager.getConfig();

    private BrowserFactory() {
    }

    public static Browser createBrowser(Playwright playwright, BrowserType browserType) {
        LaunchOptions launchOptions = new LaunchOptions()
                .setHeadless(config.uiHeadless())
                .setSlowMo(config.uiSlowMo());

        log.info("Launching browser [{}] with headless={}, slowMo={}ms",
                browserType, config.uiHeadless(), config.uiSlowMo());

        return switch (browserType) {
            case CHROMIUM -> {
                launchOptions.setArgs(List.of(
                        "--no-sandbox",
                        "--disable-dev-shm-usage",
                        "--disable-gpu",
                        "--window-size=1920,1080"
                ));
                yield playwright.chromium().launch(launchOptions);
            }
            case FIREFOX -> playwright.firefox().launch(launchOptions);
            case WEBKIT -> playwright.webkit().launch(launchOptions);
        };
    }
}
