package org.example.soundcloud.pages;

import org.example.soundcloud.core.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage extends BasePage {

    private static final java.time.Duration SEARCH_UI_TIMEOUT = java.time.Duration.ofSeconds(7);
    private final By searchForm = By.cssSelector("form.headerSearch");
    private final By searchInput = By.cssSelector(
            "form.headerSearch input.headerSearch__input[name='q'], "
                    + "form.headerSearch input[name='q'][type='search'], "
                    + "form.headerSearch input[aria-label='Search']");
    private final By searchSubmitButton = By.cssSelector(
            "form.headerSearch button.headerSearch__submit[type='submit'], "
                    + "form.headerSearch button[type='submit']");
    private final By searchEntryPoint = By.xpath(
            "//a[(contains(@href,'/search?') or @href='/search' or starts-with(@href, '/search/'))"
                    + " and not(contains(@href,'/popular/searches'))"
                    + " and (contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')"
                    + " or contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')"
                    + " or contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search'))]"
                    + " | //button[normalize-space()='Search']"
                    + " | //button[contains(translate(@title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')"
                    + " or contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')]");
    private final By uploadButton = By.xpath(
            "//a[contains(@href,'upload')][normalize-space()='Upload' or .//*[normalize-space()='Upload']]"
                    + " | //button[normalize-space()='Upload' or .//*[normalize-space()='Upload']]");
    private final By signInButton = By.xpath(
            "//button[normalize-space()='Sign in' or .//*[normalize-space()='Sign in']]"
                    + " | //a[normalize-space()='Sign in' or contains(@href,'login') or contains(@href,'signin')]");
    private final By logoLink = By.xpath("//a[contains(@class,'header__logoLink') or @title='Home']");
    private final By popularSearchesLink = By.xpath(
            "//a[@title='Popular searches' or normalize-space()='Popular searches']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        openUrl(TestData.BASE_URL);
        dismissCookieBannerIfPresent();

        if (!isPageOpened()) {
            try {
                waitForAnyVisible(DEFAULT_TIMEOUT, logoLink, popularSearchesLink,
                        uploadButton, signInButton);
            } catch (TimeoutException exception) {
                throw new TimeoutException("Home page did not expose any stable navigation markers", exception);
            }
        }

        return this;
    }

    public SearchPage search(String query) {
        return searchThroughUi(query, true);
    }

    public SearchPage searchViaUi(String query) {
        return searchThroughUi(query, false);
    }

    private SearchPage searchThroughUi(String query, boolean allowFallback) {
        SearchPage searchPage = new SearchPage(driver);
        dismissCookieBannerIfPresent();

        WebElement searchUiElement = null;
        try {
            searchUiElement = waitForAnyVisible(SEARCH_UI_TIMEOUT, searchInput, searchForm, searchEntryPoint);
        } catch (TimeoutException ignored) {
        }

        if (searchUiElement != null && trySearchUsingVisibleElement(searchUiElement, query, searchPage)) {
            return searchPage;
        }

        if (allowFallback) {
            return searchPage.open(query);
        }

        throw new IllegalStateException("Search could not be performed through the home page UI");
    }

    private boolean trySearchUsingVisibleElement(WebElement searchUiElement, String query, SearchPage searchPage) {
        try {
            scrollIntoView(searchUiElement);

            if ("input".equalsIgnoreCase(searchUiElement.getTagName())) {
                submitSearchFromInput(searchUiElement, query, searchPage);
                return true;
            }

            if (isVisible(searchInput, SHORT_TIMEOUT)) {
                submitSearchFromInput(waitForVisible(searchInput), query, searchPage);
                return true;
            }

            try {
                searchUiElement.click();
            } catch (ElementClickInterceptedException exception) {
                dismissCookieBannerIfPresent();
                jsClick(searchUiElement);
            }

            dismissCookieBannerIfPresent();

            if (isVisible(searchInput, SEARCH_UI_TIMEOUT)) {
                submitSearchFromInput(waitForVisible(searchInput), query, searchPage);
                return true;
            }
        } catch (StaleElementReferenceException ignored) {
        }

        return false;
    }

    private void submitSearchFromInput(WebElement searchInputElement, String query, SearchPage searchPage) {
        dismissCookieBannerIfPresent();
        scrollIntoView(searchInputElement);
        searchInputElement.clear();
        searchInputElement.sendKeys(query);

        if (isVisible(searchSubmitButton, SHORT_TIMEOUT)) {
            clickVisibleElement(searchSubmitButton);
        } else {
            searchInputElement.sendKeys(Keys.ENTER);
        }

        searchPage.waitUntilOpened();
    }

    public UploadPage goToUpload() {
        if (isVisible(uploadButton, SHORT_TIMEOUT)) {
            clickVisibleElement(uploadButton);
        } else {
            openUrl(TestData.BASE_URL + "upload");
            dismissCookieBannerIfPresent();
        }

        return new UploadPage(driver);
    }

    public LoginPage openLoginForm() {
        LoginPage loginPage = new LoginPage(driver);

        if (isVisible(signInButton, SHORT_TIMEOUT)) {
            clickVisibleElement(signInButton);
        } else {
            openUrl(TestData.BASE_URL + "signin");
            dismissCookieBannerIfPresent();
        }

        return loginPage.ensureLoginFormOpened();
    }

    public boolean isPageOpened() {
        return currentUrl().startsWith(TestData.BASE_URL)
                && pageTitle().toLowerCase().contains("soundcloud")
                && isAnyVisible(SHORT_TIMEOUT, logoLink, popularSearchesLink, searchForm, searchInput, searchEntryPoint,
                        uploadButton, signInButton);
    }

    public boolean isSearchInputVisible() {
        dismissCookieBannerIfPresent();
        return isAnyVisible(SEARCH_UI_TIMEOUT, searchForm, searchInput, searchEntryPoint);
    }
}
