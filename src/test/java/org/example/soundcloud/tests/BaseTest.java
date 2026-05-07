package org.example.soundcloud.tests;

import java.util.stream.Stream;
import java.util.function.Supplier;
import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.core.DriverFactory;
import org.example.soundcloud.core.TestData;
import org.example.soundcloud.pages.HomePage;
import org.example.soundcloud.pages.SearchPage;
import org.example.soundcloud.pages.TrackPage;
import org.junit.jupiter.api.AfterEach;
import org.openqa.selenium.WebDriver;

public abstract class BaseTest {

    protected WebDriver driver;

    public static Stream<BrowserType> browsers() {
        String browserProperty = System.getProperty("browser", "chrome");
        return BrowserType.resolveRequestedBrowsers(browserProperty).stream();
    }

    protected void openBrowser(BrowserType browserType) {
        driver = DriverFactory.createDriver(browserType);
    }

    protected HomePage openHomePage(BrowserType browserType) {
        return openWithFreshBrowserRetry(browserType, () -> new HomePage(driver).open());
    }

    protected SearchPage openSearchResults(BrowserType browserType, String query) {
        return openWithFreshBrowserRetry(browserType, () -> new SearchPage(driver).open(query));
    }

    protected TrackPage openFirstTrack(BrowserType browserType, String query) {
        SearchPage searchPage = openSearchResults(browserType, query);
        if (!searchPage.hasResults()) {
            throw new IllegalStateException("Search results were not found for query: " + query);
        }

        return searchPage.openFirstTrack();
    }

    protected TrackPage openDefaultTrack(BrowserType browserType) {
        SearchPage searchPage = openSearchResults(browserType, TestData.PRIMARY_QUERY);
        if (searchPage.hasResults()) {
            return searchPage.openFirstTrack();
        }

        driver.quit();
        driver = null;

        return openFirstTrack(browserType, TestData.SECONDARY_QUERY);
    }

    private <T> T openWithFreshBrowserRetry(BrowserType browserType, Supplier<T> action) {
        RuntimeException lastException = null;

        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                openBrowser(browserType);
                return action.get();
            } catch (RuntimeException exception) {
                lastException = exception;
                closeDriverQuietly();
            }
        }

        throw lastException;
    }

    private void closeDriverQuietly() {
        if (driver == null) {
            return;
        }

        try {
            driver.quit();
        } catch (RuntimeException ignored) {
            // Browser processes can already be gone when Selenium reports a broken session.
        } finally {
            driver = null;
        }
    }

    @AfterEach
    void tearDown() {
        closeDriverQuietly();
    }
}
