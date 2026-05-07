package org.example.soundcloud.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TrackPage extends BasePage {

    private final By playButton = By.xpath(
            "(//a[contains(@class,'playButton') or contains(@class,'sc-button-play')"
                    + " or contains(@title,'Play') or contains(@aria-label,'Play')]"
                    + "[not(@disabled) and not(contains(@class,'disabled'))]"
                    + " | //button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Play') or contains(@aria-label,'Play')]"
                    + "[not(@disabled) and not(contains(@class,'disabled'))])[1]");
    private final By pauseButton = By.xpath(
            "(//a[contains(@class,'playButton') or contains(@class,'sc-button-play')"
                    + " or contains(@title,'Pause') or contains(@aria-label,'Pause')]"
                    + "[not(@disabled) and not(contains(@class,'disabled'))]"
                    + " | //button[contains(@class,'playButton') or contains(@class,'playControls__control')]"
                    + "[contains(@title,'Pause') or contains(@aria-label,'Pause')]"
                    + "[not(@disabled) and not(contains(@class,'disabled'))])[1]");
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
    private final By timePassed = By.xpath("//*[contains(@class,'playbackTimeline__timePassed')][1]");
    private final By durationLabel = By.xpath("//*[contains(@class,'playbackTimeline__duration')][1]");
    private final By authGate = By.xpath(
            "//div[contains(@class,'auth-modal')]"
                    + " | //div[@role='dialog'][.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in or create an account')]]"
                    + " | //button[normalize-space()='Sign in' and contains(@class,'loginButton')]"
                    + " | //button[@aria-label='Sign in' and contains(@class,'loginButton')]"
                    + " | //button[@aria-label='Create a SoundCloud account']");
    private final By authGateCloseButton = By.xpath(
            "(//div[contains(@class,'auth-modal')]"
                    + " | //div[@role='dialog'][.//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in or create an account')]])"
                    + "//button[@aria-label='Close' or contains(@class,'closeButton') or contains(@class,'modalClose')"
                    + " or contains(@title,'Close') or normalize-space()='Close']"
                    + " | //button[@aria-label='Close' and ancestor::div[@role='dialog']]"
                    + " | //button[contains(@class,'closeButton') and ancestor::div[@role='dialog']]"
                    + " | //button[contains(@class,'modalClose') and ancestor::div[@role='dialog']]");

    public TrackPage(WebDriver driver) {
        super(driver);
    }

    public boolean isTrackPageOpened() {
        waitForDocumentReady();
        dismissAuthGateIfPresent();
        return !currentUrl().contains("/search") && !currentUrl().contains("/upload") && isVisible(trackTitle)
                && pageTitle().toLowerCase().contains("soundcloud");
    }

    public void play() {
        dismissAuthGateIfPresent();
        click(playButton);

        if (isVisible(authGate, SHORT_TIMEOUT)) {
            dismissAuthGateIfPresent();

            if (isVisible(playButton, SHORT_TIMEOUT)) {
                click(playButton);
            }
        }
    }

    public void pause() {
        dismissAuthGateIfPresent();
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
        dismissAuthGateIfPresent();
        click(artistLink);
        return new ArtistPage(driver);
    }

    public void openShareDialog() {
        dismissAuthGateIfPresent();

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

    public boolean waitUntilPlaybackFinishes(Duration timeout) {
        dismissAuthGateIfPresent();

        String startingTime = readOptionalText(timePassed);
        String totalDuration = readOptionalText(durationLabel);

        try {
            play();

            boolean playbackStarted = new WebDriverWait(driver, Duration.ofSeconds(20)).until(webDriver -> {
                dismissAuthGateIfPresent();
                String currentTime = readOptionalText(timePassed);
                return isVisible(pauseButton, SHORT_TIMEOUT)
                        || (!currentTime.isBlank() && !currentTime.equals(startingTime));
            });

            if (!playbackStarted) {
                return false;
            }

            return new WebDriverWait(driver, timeout).until(webDriver -> {
                dismissAuthGateIfPresent();

                String currentTime = readOptionalText(timePassed);
                if (!totalDuration.isBlank() && !currentTime.isBlank()
                        && parseClockTime(currentTime) >= parseClockTime(totalDuration)) {
                    return true;
                }

                return isVisible(playButton, SHORT_TIMEOUT)
                        && !currentTime.isBlank()
                        && !"0:00".equals(currentTime)
                        && !currentTime.equals(startingTime);
            });
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void dismissAuthGateIfPresent() {
        if (!isVisible(authGate, SHORT_TIMEOUT)) {
            return;
        }

        for (WebElement closeButton : driver.findElements(authGateCloseButton)) {
            try {
                if (!closeButton.isDisplayed()) {
                    continue;
                }

                scrollIntoView(closeButton);

                try {
                    closeButton.click();
                } catch (ElementClickInterceptedException exception) {
                    jsClick(closeButton);
                }

                return;
            } catch (StaleElementReferenceException ignored) {
                // Auth overlays are short-lived and can re-render while closing.
            }
        }

        driver.switchTo().activeElement().sendKeys(Keys.ESCAPE);
    }

    private String readOptionalText(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    return element.getText().trim();
                }
            } catch (StaleElementReferenceException ignored) {
                // Player widgets are frequently re-rendered while playback state changes.
            }
        }

        return "";
    }

    private int parseClockTime(String rawTime) {
        String value = rawTime.replace("-", "").trim();
        if (value.isBlank()) {
            return -1;
        }

        try {
            String[] parts = value.split(":");
            if (parts.length == 2) {
                return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
            }

            if (parts.length == 3) {
                return Integer.parseInt(parts[0]) * 3600
                        + Integer.parseInt(parts[1]) * 60
                        + Integer.parseInt(parts[2]);
            }
        } catch (NumberFormatException ignored) {
            return -1;
        }

        return -1;
    }
}
