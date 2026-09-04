package org.example.api.config;

import org.aeonbits.owner.ConfigCache;

public final class ConfigManager {

    private ConfigManager() {
    }

    public static ProjectConfig getConfig() {
        return ConfigCache.getOrCreate(ProjectConfig.class);
    }
}
