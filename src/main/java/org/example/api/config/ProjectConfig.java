package org.example.api.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
        "system:properties",
        "system:env",
        "classpath:config.properties"
})
public interface ProjectConfig extends Config {

    @Key("base.url")
    @DefaultValue("https://dummyjson.com")
    String baseUrl();

    @Key("logging.enabled")
    @DefaultValue("true")
    boolean loggingEnabled();

    @Key("api.timeout.ms")
    @DefaultValue("10000")
    long timeoutMs();

    @Key("db.url")
    @DefaultValue("jdbc:postgresql://localhost:5432/testdb")
    String dbUrl();

    @Key("db.user")
    @DefaultValue("postgres")
    String dbUser();

    @Key("db.password")
    @DefaultValue("postgres")
    String dbPassword();

    @Key("db.driver")
    @DefaultValue("org.postgresql.Driver")
    String dbDriver();

    @Key("db.pool.size")
    @DefaultValue("10")
    int dbPoolSize();

    @Key("db.use.testcontainers")
    @DefaultValue("true")
    boolean dbUseTestcontainers();

    // UI (Playwright) Configuration
    @Key("ui.base.url")
    @DefaultValue("https://www.saucedemo.com")
    String uiBaseUrl();

    @Key("ui.browser")
    @DefaultValue("chromium")
    String uiBrowser();

    @Key("ui.headless")
    @DefaultValue("true")
    boolean uiHeadless();

    @Key("ui.slow.mo")
    @DefaultValue("0")
    double uiSlowMo();

    @Key("ui.timeout.ms")
    @DefaultValue("15000")
    double uiTimeoutMs();

    @Key("ui.tracing.enabled")
    @DefaultValue("true")
    boolean uiTracingEnabled();

    @Key("ui.video.enabled")
    @DefaultValue("false")
    boolean uiVideoEnabled();

    @Key("ui.screenshot.on.failure")
    @DefaultValue("true")
    boolean uiScreenshotOnFailure();

    @Key("ui.page.source.on.failure")
    @DefaultValue("true")
    boolean uiPageSourceOnFailure();
}
