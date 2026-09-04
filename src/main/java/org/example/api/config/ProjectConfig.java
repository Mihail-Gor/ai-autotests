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
}
