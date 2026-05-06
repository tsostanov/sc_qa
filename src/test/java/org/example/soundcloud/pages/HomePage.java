package org.example.soundcloud.pages;

import org.example.soundcloud.core.TestData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {

    private final By searchInput = By.xpath(
            "//input[@type='search' or contains(@placeholder,'Search') or contains(@aria-label,'Search')]");
    private final By uploadButton = By.xpath(
            "//a[contains(@href,'upload')][normalize-space()='Upload' or .//*[normalize-space()='Upload']]"
                    + " | //button[normalize-space()='Upload' or .//*[normalize-space()='Upload']]");
    private final By signInButton = By.xpath(
            "//button[normalize-space()='Sign in' or .//*[normalize-space()='Sign in']]"
                    + " | //a[normalize-space()='Sign in' or contains(@href,'login') or contains(@href,'signin')]");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public HomePage open() {
        openUrl(TestData.BASE_URL);
        dismissCookieBannerIfPresent();
        waitForVisible(searchInput);
        return this;
    }

    public SearchPage search(String query) {
        type(searchInput, query);
        submitWithEnter(searchInput);
        return new SearchPage(driver);
    }

    public UploadPage goToUpload() {
        click(uploadButton);
        return new UploadPage(driver);
    }

    public LoginPage openLoginForm() {
        click(signInButton);
        return new LoginPage(driver);
    }

    public boolean isPageOpened() {
        return currentUrl().startsWith(TestData.BASE_URL) && isVisible(searchInput)
                && pageTitle().toLowerCase().contains("soundcloud");
    }

    public boolean isSearchInputVisible() {
        return isVisible(searchInput);
    }
}
