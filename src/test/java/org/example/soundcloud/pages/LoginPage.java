package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By signInTrigger = By.xpath(
            "//button[normalize-space()='Sign in' or @aria-label='Sign in']"
                    + " | //a[normalize-space()='Sign in' or contains(@href,'signin') or contains(@href,'login')]");
    private final By loginForm = By.xpath(
            "//div[@role='dialog'][.//*[normalize-space()='Sign in' or contains(.,'email')]]"
                    + " | //form[.//input[@type='email' or @name='email']]");
    private final By emailInput = By.xpath(
            "//input[@type='email' or @name='email' or contains(@placeholder,'Email') or contains(@aria-label,'Email')]");
    private final By passwordInput = By.xpath(
            "//input[@type='password' or @name='password' or contains(@placeholder,'Password') or contains(@aria-label,'Password')]");
    private final By continueButton = By.xpath(
            "//button[@type='submit' or normalize-space()='Continue' or normalize-space()='Sign in'"
                    + " or .//*[normalize-space()='Continue'] or .//*[normalize-space()='Sign in']]");
    private final By errorMessage = By.xpath(
            "//*[contains(@class,'error')"
                    + " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'valid email')"
                    + " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'could not sign you in')"
                    + " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage ensureLoginFormOpened() {
        dismissCookieBannerIfPresent();

        if (isVisible(loginForm, SHORT_TIMEOUT) || isVisible(emailInput, SHORT_TIMEOUT)) {
            return this;
        }

        if (isVisible(signInTrigger, SHORT_TIMEOUT)) {
            click(signInTrigger);
        }

        return this;
    }

    public boolean isLoginFormOpened() {
        ensureLoginFormOpened();
        return isVisible(loginForm) || isVisible(emailInput);
    }

    public LoginPage tryLogin(String email, String password) {
        ensureLoginFormOpened();
        type(emailInput, email);

        if (!isVisible(passwordInput, SHORT_TIMEOUT)) {
            click(continueButton);
        }

        if (isVisible(passwordInput, SHORT_TIMEOUT)) {
            type(passwordInput, password);
        }

        click(continueButton);
        return this;
    }

    public boolean hasErrorMessage() {
        return isVisible(errorMessage);
    }
}
