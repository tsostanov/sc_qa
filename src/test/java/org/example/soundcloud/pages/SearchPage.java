package org.example.soundcloud.pages;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.example.soundcloud.core.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchPage extends BasePage {

    private final By firstTrackTitle = By.xpath(
            "(//li[contains(@class,'searchList__item')]//a[normalize-space()"
                    + " and not(contains(@class,'soundTitle__username'))"
                    + " and not(contains(@class,'userBadge'))])[1]");
    private final By searchResultItems = By.xpath(
            "//li[contains(@class,'searchList__item')]//a[normalize-space()"
                    + " and not(contains(@class,'soundTitle__username'))"
                    + " and not(contains(@class,'userBadge'))]");
    private final By noResultsBlock = By.xpath(
            "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'no results')]");

    public SearchPage(WebDriver driver) {
        super(driver);
    }

    public SearchPage open(String query) {
        openUrl(TestData.BASE_URL + "search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
        dismissCookieBannerIfPresent();
        waitUntilOpened();
        return this;
    }

    public boolean isSearchPageOpened() {
        waitForDocumentReady();
        return currentUrl().contains("search");
    }

    public void waitUntilOpened() {
        waitForUrlContains("search");
    }

    public boolean hasResults() {
        if (countVisibleElements(searchResultItems) > 0) {
            return true;
        }

        return false;
    }

    public TrackPage openFirstTrack() {
        click(firstTrackTitle);
        return new TrackPage(driver);
    }

    public String getFirstTrackTitle() {
        return getText(firstTrackTitle);
    }
}
