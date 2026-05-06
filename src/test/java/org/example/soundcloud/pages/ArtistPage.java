package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ArtistPage extends BasePage {

    private final By artistName = By.xpath(
            "//h1[normalize-space()] | //header//*[self::h1 or self::h2][normalize-space()]");

    public ArtistPage(WebDriver driver) {
        super(driver);
    }

    public boolean isArtistPageOpened() {
        waitForDocumentReady();
        return !currentUrl().contains("/search") && !currentUrl().contains("/upload") && hasArtistName();
    }

    public boolean hasArtistName() {
        return isVisible(artistName);
    }
}
