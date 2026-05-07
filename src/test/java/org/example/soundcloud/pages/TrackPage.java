package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class TrackPage extends BasePage {

    private final By playButton = By.xpath(
            "(//a[contains(@class,'playButton') or contains(@class,'sc-button-play')"
                    + " or contains(@title,'Play') or contains(@aria-label,'Play')]"
                    + " | //button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Play') or contains(@aria-label,'Play')])[1]");
    private final By pauseButton = By.xpath(
            "(//a[contains(@class,'playButton') or contains(@class,'sc-button-play')"
                    + " or contains(@title,'Pause') or contains(@aria-label,'Pause')]"
                    + " | //button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Pause') or contains(@aria-label,'Pause')])[1]");
    private final By playerVisibleMarker = By.xpath(
            "//div[contains(@class,'playControls')] | //div[contains(@class,'playbackTimeline')]"
                    + " | //button[contains(@class,'playControls__control')]"
                    + " | //a[contains(@class,'playButton') or contains(@class,'sc-button-play')]"
                    + " | //button[normalize-space()='Share']"
                    + " | //button[normalize-space()='Copy Link']");
    private final By trackTitle = By.xpath(
            "//h1[contains(@class,'soundTitle__title') and normalize-space()] | //main//h1[normalize-space()]");
    private final By artistLink = By.xpath(
            "(//a[contains(@class,'soundTitle__usernameHero')"
                    + " or contains(@class,'soundTitle__username')"
                    + " or contains(@class,'userBadge')"
                    + " or contains(@class,'trackItem__username')"
                    + " or .//*[contains(@class,'soundTitle__usernameHero')]])[1]");
    private final By shareButton = By.xpath(
            "(//button[normalize-space()='Share' or .//*[normalize-space()='Share']"
                    + " or contains(@title,'Share') or contains(@aria-label,'Share')]"
                    + " | //a[normalize-space()='Share' or .//*[normalize-space()='Share']"
                    + " or contains(@title,'Share') or contains(@aria-label,'Share')])[1]");
    private final By copyLinkButton = By.xpath(
            "//button[normalize-space()='Copy Link' or contains(@title,'Copy Link') or contains(@aria-label,'Copy Link')]");
    private final By shareDialog = By.xpath(
            "//div[@role='dialog'][.//*[normalize-space()='Share' or contains(.,'Copy link')]]"
                    + " | //div[contains(@class,'shareModal')]"
                    + " | //button[normalize-space()='Copy Link']");
    private final By authGate = By.xpath(
            "//div[contains(@class,'auth-modal')]"
                    + " | //button[normalize-space()='Sign in' and contains(@class,'loginButton')]"
                    + " | //button[@aria-label='Sign in' and contains(@class,'loginButton')]"
                    + " | //button[@aria-label='Create a SoundCloud account']");

    public TrackPage(WebDriver driver) {
        super(driver);
    }

    public boolean isTrackPageOpened() {
        waitForDocumentReady();
        return !currentUrl().contains("/search") && !currentUrl().contains("/upload") && isVisible(trackTitle)
                && pageTitle().toLowerCase().contains("soundcloud");
    }

    public void play() {
        click(playButton);
    }

    public void pause() {
        click(pauseButton);
    }

    public boolean isPlayerVisible() {
        return isVisible(playerVisibleMarker) || isVisible(trackTitle);
    }

    public boolean isPlaying() {
        return isVisible(pauseButton) || isVisible(authGate);
    }

    public boolean isPaused() {
        return isVisible(playButton) || isVisible(authGate);
    }

    public ArtistPage openArtistPage() {
        click(artistLink);
        return new ArtistPage(driver);
    }

    public void openShareDialog() {
        if (isAnyVisible(DEFAULT_TIMEOUT, copyLinkButton, shareDialog)) {
            return;
        }

        if (isAnyVisible(DEFAULT_TIMEOUT, shareButton)) {
            click(shareButton);
        }
    }

    public boolean isShareDialogOpened() {
        return isVisible(shareDialog) || isVisible(copyLinkButton);
    }
}
