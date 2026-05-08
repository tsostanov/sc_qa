package org.example.soundcloud.pages;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import org.example.soundcloud.core.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SearchPage extends BasePage {

    private static final Duration RESULTS_TIMEOUT = Duration.ofSeconds(25);
    private final By firstTrackTitle = By.xpath(
            "(//a[@href and normalize-space()"
                    + " and (contains(@class,'trackItem__trackTitle') or contains(@class,'soundTitle__title'))"
                    + " and not(contains(@href,'/sets/'))"
                    + " and not(contains(@href,'/people/'))])[1]");
    private final By searchResultItems = By.xpath(
            "//a[@href and normalize-space()"
                    + " and (contains(@class,'trackItem__trackTitle') or contains(@class,'soundTitle__title'))"
                    + " and not(contains(@href,'/sets/'))"
                    + " and not(contains(@href,'/people/'))]");
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
        ensureResultsLoaded();
        return hasResultLinks();
    }

    public TrackPage openFirstTrack() {
        String href = firstResultLink().getAttribute("href");
        openUrl(href);
        dismissCookieBannerIfPresent();
        return new TrackPage(driver);
    }

    public TrackPage openFirstResultMatchingAll(String... fragments) {
        WebElement targetResult = firstResultMatchingAll(fragments);
        String href = targetResult.getAttribute("href");
        openUrl(href);
        dismissCookieBannerIfPresent();
        return new TrackPage(driver);
    }

    public String getFirstTrackTitle() {
        WebElement firstResult = firstResultLink();
        return extractResultText(firstResult);
    }

    public boolean hasResultMatchingAll(String... fragments) {
        ensureResultsLoaded();
        List<String> normalizedFragments = List.of(fragments).stream()
                .map(fragment -> fragment.toLowerCase(Locale.ROOT).trim())
                .filter(fragment -> !fragment.isBlank())
                .toList();

        return findResultLinks().stream()
                .map(this::extractSearchableText)
                .map(text -> text.toLowerCase(Locale.ROOT))
                .anyMatch(text -> normalizedFragments.stream().allMatch(text::contains));
    }

    private void waitForResultsState() {
        try {
            new WebDriverWait(driver, RESULTS_TIMEOUT).until(webDriver -> hasResultLinks() || hasVisibleNoResultsBlock());
        } catch (TimeoutException ignored) {
        }
    }

    private void ensureResultsLoaded() {
        for (int attempt = 0; attempt < 3; attempt++) {
            waitForResultsState();

            if (hasResultLinks()) {
                return;
            }

            if (attempt == 2) {
                return;
            }

            driver.navigate().refresh();
            waitForDocumentReady();
            dismissCookieBannerIfPresent();
            waitUntilOpened();
        }
    }

    private boolean hasResultLinks() {
        return !findResultLinks().isEmpty();
    }

    private boolean hasVisibleNoResultsBlock() {
        for (WebElement element : driver.findElements(noResultsBlock)) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        return false;
    }

    private WebElement firstResultLink() {
        ensureResultsLoaded();

        List<WebElement> results = findResultLinks();
        if (results.isEmpty()) {
            throw new NoSuchElementException("No track links were found on the search results page");
        }

        return results.get(0);
    }

    private WebElement firstResultMatchingAll(String... fragments) {
        ensureResultsLoaded();

        List<String> normalizedFragments = List.of(fragments).stream()
                .map(fragment -> fragment.toLowerCase(Locale.ROOT).trim())
                .filter(fragment -> !fragment.isBlank())
                .toList();

        if (normalizedFragments.isEmpty()) {
            return firstResultLink();
        }

        return findResultLinks().stream()
                .filter(resultLink -> {
                    String searchableText = extractSearchableText(resultLink).toLowerCase(Locale.ROOT);
                    return normalizedFragments.stream().allMatch(searchableText::contains);
                })
                .findFirst()
                .orElseGet(this::firstResultLink);
    }

    private List<WebElement> findResultLinks() {
        return driver.findElements(searchResultItems).stream()
                .filter(element -> {
                    try {
                        String href = element.getAttribute("href");
                        return href != null && !href.isBlank();
                    } catch (StaleElementReferenceException exception) {
                        return false;
                    }
                })
                .toList();
    }

    private String extractResultText(WebElement resultLink) {
        String text = resultLink.getText().trim();
        if (!text.isBlank()) {
            return text;
        }

        String title = resultLink.getAttribute("title");
        if (title != null && !title.isBlank()) {
            return title.trim();
        }

        String ariaLabel = resultLink.getAttribute("aria-label");
        return ariaLabel == null ? "" : ariaLabel.trim();
    }

    private String extractSearchableText(WebElement resultLink) {
        String href = resultLink.getAttribute("href");
        return (extractResultText(resultLink) + " " + (href == null ? "" : href)).trim();
    }
}
