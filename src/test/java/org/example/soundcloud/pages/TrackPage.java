package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TrackPage extends BasePage {

    private final By playButton = By.xpath(
            "(//button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Play') or contains(@aria-label,'Play')])[1]");
    private final By pauseButton = By.xpath(
            "(//button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Pause') or contains(@aria-label,'Pause')])[1]");
    private final By playerVisibleMarker = By.xpath(
            "//div[contains(@class,'playControls')] | //div[contains(@class,'playbackTimeline')]"
                    + " | //button[contains(@class,'playControls__control')]");
    private final By artistLink = By.xpath(
            "(//a[contains(@class,'soundTitle__username') or contains(@class,'userBadge')"
                    + " or contains(@class,'trackItem__username')])[1]");
    private final By shareButton = By.xpath(
            "//button[normalize-space()='Share' or .//*[normalize-space()='Share'] or contains(@title,'Share')]");
    private final By shareDialog = By.xpath(
            "//div[@role='dialog'][.//*[normalize-space()='Share' or contains(.,'Copy link')]]"
                    + " | //div[contains(@class,'shareModal')]");

    public TrackPage(WebDriver driver) {
        super(driver);
    }

    public boolean isTrackPageOpened() {
        waitForDocumentReady();
        return !currentUrl().contains("/search") && isPlayerVisible()
                && pageTitle().toLowerCase().contains("soundcloud");
    }

    public void play() {
        click(playButton);
    }

    public void pause() {
        click(pauseButton);
    }

    public boolean isPlayerVisible() {
        return isVisible(playerVisibleMarker);
    }

    public boolean isPlaying() {
        return isVisible(pauseButton);
    }

    public boolean isPaused() {
        return isVisible(playButton);
    }

    public ArtistPage openArtistPage() {
        click(artistLink);
        return new ArtistPage(driver);
    }

    public void openShareDialog() {
        click(shareButton);
    }

    public boolean isShareDialogOpened() {
        return isVisible(shareDialog);
    }
}
