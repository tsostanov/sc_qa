package org.example.soundcloud.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UploadPage extends BasePage {

    private final By uploadMarker = By.xpath(
            "//*[self::h1 or self::h2][contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'upload')]");
    private final By authRequiredMarker = By.xpath(
            "//div[@role='dialog'][.//*[normalize-space()='Sign in']]"
                    + " | //form[.//input[@type='email']]"
                    + " | //input[@type='email']"
                    + " | //*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign in to upload')]"
                    + " | //*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'continue with email')]");

    public UploadPage(WebDriver driver) {
        super(driver);
    }

    public boolean isUploadPageOpened() {
        waitForDocumentReady();
        return currentUrl().contains("upload") || isVisible(uploadMarker) || isAuthorizationRequired();
    }

    public boolean isAuthorizationRequired() {
        return isVisible(authRequiredMarker);
    }
}
