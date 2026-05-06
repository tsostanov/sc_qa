package org.example.soundcloud.core;

import java.util.List;
import java.util.Locale;

public enum BrowserType {
    CHROME,
    FIREFOX;

    public static List<BrowserType> resolveRequestedBrowsers(String browserProperty) {
        if (browserProperty == null || browserProperty.isBlank()) {
            return List.of(CHROME);
        }

        return switch (browserProperty.trim().toLowerCase(Locale.ROOT)) {
            case "chrome" -> List.of(CHROME);
            case "firefox" -> List.of(FIREFOX);
            case "all" -> List.of(CHROME, FIREFOX);
            default -> throw new IllegalArgumentException(
                    "Unsupported browser value: " + browserProperty + ". Use chrome, firefox or all.");
        };
    }
}
