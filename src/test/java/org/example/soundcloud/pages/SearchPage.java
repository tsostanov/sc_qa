package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SearchPage extends BasePage {

    private final By firstTrackTitle = By.xpath(
            "(//li[contains(@class,'searchList__item')]//a[contains(@class,'soundTitle__title')])[1]");
    private final By searchResultItems = By.xpath(
            "//li[contains(@class,'searchList__item')]//a[contains(@class,'soundTitle__title')]");
    private final By noResultsBlock = By.xpath(
            "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'no results')]");

    public SearchPage(WebDriver driver) {
        super(driver);
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

        return !isVisible(noResultsBlock);
    }

    public TrackPage openFirstTrack() {
        click(firstTrackTitle);
        return new TrackPage(driver);
    }

    public String getFirstTrackTitle() {
        return getText(firstTrackTitle);
    }
}
