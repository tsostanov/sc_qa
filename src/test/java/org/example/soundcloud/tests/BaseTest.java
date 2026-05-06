package org.example.soundcloud.tests;

import java.util.stream.Stream;
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
        openBrowser(browserType);
        return new HomePage(driver).open();
    }

    protected SearchPage openSearchResults(BrowserType browserType, String query) {
        HomePage homePage = openHomePage(browserType);
        SearchPage searchPage = homePage.search(query);
        searchPage.waitUntilOpened();
        return searchPage;
    }

    protected TrackPage openFirstTrack(BrowserType browserType, String query) {
        SearchPage searchPage = openSearchResults(browserType, query);
        if (!searchPage.hasResults()) {
            throw new IllegalStateException("Search results were not found for query: " + query);
        }

        return searchPage.openFirstTrack();
    }

    protected TrackPage openDefaultTrack(BrowserType browserType) {
        return openFirstTrack(browserType, TestData.PRIMARY_QUERY);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
