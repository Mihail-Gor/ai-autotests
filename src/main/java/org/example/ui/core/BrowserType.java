package org.example.ui.core;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BrowserType {
    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit");

    private final String value;

    public static BrowserType fromString(String text) {
        return Arrays.stream(values())
                .filter(b -> b.value.equalsIgnoreCase(text) || b.name().equalsIgnoreCase(text))
                .findFirst()
                .orElse(CHROMIUM);
    }
}
