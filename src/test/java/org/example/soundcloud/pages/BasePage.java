package org.example.soundcloud.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);
    protected static final Duration SHORT_TIMEOUT = Duration.ofSeconds(5);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    protected void openUrl(String url) {
        driver.get(url);
        waitForDocumentReady();
    }

    protected void waitForDocumentReady() {
        wait.until(webDriver -> "complete".equals(
                ((JavascriptExecutor) webDriver).executeScript("return document.readyState")));
    }

    protected WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        WebElement element = waitForClickable(locator);
        scrollIntoView(element);

        try {
            element.click();
        } catch (StaleElementReferenceException | TimeoutException | NoSuchElementException exception) {
            WebElement refreshedElement = waitForClickable(locator);
            scrollIntoView(refreshedElement);
            jsClick(refreshedElement);
        } catch (RuntimeException exception) {
            jsClick(element);
        }
    }

    protected void clickFirstVisible(By... locators) {
        for (By locator : locators) {
            if (isVisible(locator, SHORT_TIMEOUT)) {
                click(locator);
                return;
            }
        }

        throw new NoSuchElementException("None of the provided locators is visible");
    }

    protected void type(By locator, String value) {
        WebElement element = waitForVisible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(value);
    }

    protected void submitWithEnter(By locator) {
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    protected boolean isVisible(By locator) {
        return isVisible(locator, SHORT_TIMEOUT);
    }

    protected boolean isVisible(By locator, Duration timeout) {
        try {
            new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
            return true;
        } catch (TimeoutException exception) {
            return false;
        }
    }

    protected int countVisibleElements(By locator) {
        try {
            return waitForAllVisible(locator).size();
        } catch (TimeoutException exception) {
            return 0;
        }
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    protected String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected String pageTitle() {
        return driver.getTitle();
    }

    protected void dismissCookieBannerIfPresent() {
        By[] consentButtons = new By[] {
                By.xpath("//button[normalize-space()='Accept all cookies']"),
                By.xpath("//button[normalize-space()='Accept all']"),
                By.xpath("//button[normalize-space()='I accept']"),
                By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]")
        };

        for (By locator : consentButtons) {
            if (isVisible(locator, Duration.ofSeconds(2))) {
                click(locator);
                return;
            }
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }
}
