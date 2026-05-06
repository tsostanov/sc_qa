package org.example.soundcloud.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.pages.HomePage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class HomePageTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] should open home page in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenHomePage(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);

        assertTrue(homePage.isPageOpened(), "Home page should be opened");
    }

    @ParameterizedTest(name = "[{index}] should display search input in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldDisplaySearchInput(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);

        assertTrue(homePage.isSearchInputVisible(), "Search input should be visible on the home page");
    }
}
