package org.example.soundcloud.core;

import java.time.Duration;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver(BrowserType browserType) {
        return switch (browserType) {
            case CHROME -> configureDriver(createChromeDriver());
            case FIREFOX -> configureDriver(createFirefoxDriver());
        };
    }

    private static WebDriver createChromeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-search-engine-choice-screen");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--lang=en-US");
        options.addArguments("--autoplay-policy=no-user-gesture-required");

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver() {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("intl.accept_languages", "en-US");
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("media.autoplay.default", 0);

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("-headless");
        }

        return new FirefoxDriver(options);
    }

    private static WebDriver configureDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        driver.manage().window().setSize(new Dimension(1920, 1080));
        return driver;
    }
}
