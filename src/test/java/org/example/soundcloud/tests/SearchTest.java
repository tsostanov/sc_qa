package org.example.soundcloud.tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.core.TestData;
import org.example.soundcloud.pages.HomePage;
import org.example.soundcloud.pages.SearchPage;
import org.example.soundcloud.pages.TrackPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class SearchTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] should find отвратительный король after searching темный принц in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenSearchResultsPage(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);
        SearchPage searchPage = homePage.searchViaUi(TestData.PRIMARY_QUERY);

        assertTrue(searchPage.isSearchPageOpened(), "Search results page should be opened");
        assertTrue(searchPage.hasResults(), "Search should return results for темный принц");
        assertTrue(searchPage.revealResultMatchingAll("отвратительный", "король"),
                "Search results should contain the track отвратительный король");
    }

    @ParameterizedTest(name = "[{index}] should show search results in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldDisplaySearchResults(BrowserType browserType) {
        SearchPage searchPage = openSearchResults(browserType, TestData.SPECIFIC_TRACK_QUERY);

        assertTrue(searchPage.hasResults(), "Search results should be displayed");
        assertFalse(searchPage.getFirstTrackTitle().isBlank(), "The first track title should not be blank");
    }

    @ParameterizedTest(name = "[{index}] should find губы темный принц in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldFindSpecificTrack(BrowserType browserType) {
        SearchPage searchPage = openSearchResults(browserType, TestData.SPECIFIC_TRACK_QUERY);

        assertTrue(searchPage.hasResults(), "Search should return results for the specific track query");
        assertTrue(searchPage.hasResultMatchingAll("губы", "tyomnyy-prints"),
                "Search results should contain the requested track");
    }

    @ParameterizedTest(name = "[{index}] should open first search result in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenFirstTrackFromResults(BrowserType browserType) {
        SearchPage searchPage = openSearchResults(browserType, TestData.SPECIFIC_TRACK_QUERY);

        assertTrue(searchPage.hasResults(), "Search should return at least one result");

        TrackPage trackPage = searchPage.openFirstResultMatchingAll("губы", "tyomnyy-prints");

        assertTrue(trackPage.isTrackPageOpened(), "Track page should be opened from search results");
    }
}
