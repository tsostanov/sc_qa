package org.example.soundcloud.pages;

import org.example.soundcloud.core.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By searchInput = By.xpath(
            "//input[@type='search' or @name='q'"
                    + " or contains(translate(@placeholder, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')"
                    + " or contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'search')]");
    private final By searchEntryPoint = By.xpath(
            "//a[contains(@href,'/search')"
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
                waitForAnyVisible(DEFAULT_TIMEOUT, logoLink, popularSearchesLink, searchInput, searchEntryPoint,
                        uploadButton, signInButton);
            } catch (TimeoutException exception) {
                throw new TimeoutException("Home page did not expose any stable navigation markers", exception);
            }
        }

        return this;
    }

    public SearchPage search(String query) {
        SearchPage searchPage = new SearchPage(driver);

        if (isVisible(searchInput, SHORT_TIMEOUT)) {
            type(searchInput, query);
            submitWithEnter(searchInput);
            searchPage.waitUntilOpened();
            return searchPage;
        }

        return searchPage.open(query);
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
                && isAnyVisible(SHORT_TIMEOUT, logoLink, popularSearchesLink, searchInput, searchEntryPoint,
                        uploadButton, signInButton);
    }

    public boolean isSearchInputVisible() {
        dismissCookieBannerIfPresent();
        return isAnyVisible(DEFAULT_TIMEOUT, searchInput, searchEntryPoint);
    }
}
