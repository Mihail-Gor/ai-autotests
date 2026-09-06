package org.example.ui.core;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import org.example.api.config.ConfigManager;
import org.example.api.config.ProjectConfig;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class PlaywrightAllureExtension implements BeforeEachCallback, AfterEachCallback, TestWatcher {

    private static final Logger log = LoggerFactory.getLogger(PlaywrightAllureExtension.class);
    private static final ProjectConfig config = ConfigManager.getConfig();

    @Override
    public void beforeEach(ExtensionContext context) {
        log.info("Starting UI test: {}", context.getDisplayName());
        PlaywrightDriverManager.initDriver();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        log.error("UI test failed: {} - {}", context.getDisplayName(), cause.getMessage());
        try {
            Page page = PlaywrightDriverManager.getPage();
            if (page != null && !page.isClosed()) {
                if (config.uiScreenshotOnFailure()) {
                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                    Allure.addAttachment("Failure Screenshot - " + context.getDisplayName(),
                            "image/png", new ByteArrayInputStream(screenshot), "png");
                }

                if (config.uiPageSourceOnFailure()) {
                    String pageSource = page.content();
                    Allure.addAttachment("Page HTML Source - " + context.getDisplayName(),
                            "text/html", pageSource, "html");
                }
            }

            BrowserContext browserContext = PlaywrightDriverManager.getContext();
            if (browserContext != null && config.uiTracingEnabled()) {
                Path tracePath = Files.createTempFile("playwright-trace-", ".zip");
                browserContext.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                try (InputStream is = Files.newInputStream(tracePath)) {
                    Allure.addAttachment("Playwright Trace - " + context.getDisplayName(),
                            "application/zip", is, "zip");
                }
                Files.deleteIfExists(tracePath);
            }
        } catch (Exception e) {
            log.warn("Failed to capture failure artifacts for test [{}]: {}", context.getDisplayName(), e.getMessage());
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("UI test passed successfully: {}", context.getDisplayName());
        try {
            BrowserContext browserContext = PlaywrightDriverManager.getContext();
            if (browserContext != null && config.uiTracingEnabled()) {
                browserContext.tracing().stop();
            }
        } catch (Exception e) {
            log.debug("Tracing stop ignored on success: {}", e.getMessage());
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.warn("UI test aborted: {}", context.getDisplayName());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.info("UI test disabled: {} (reason: {})", context.getDisplayName(), reason.orElse("none"));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        log.info("Finishing UI test: {}", context.getDisplayName());
        PlaywrightDriverManager.closeDriver();
    }
}
