package org.example.soundcloud.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
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
    private static final Duration COOKIE_TIMEOUT = Duration.ofSeconds(2);
    private static final By COOKIE_DIALOG = By.xpath(
            "//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cookies & tracking')"
                    + " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'manage preferences')"
                    + " or contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'personalized ads')]");
    private static final By[] COOKIE_ACTIONS = new By[] {
            By.xpath("//button[normalize-space()='Reject All']"),
            By.xpath("//button[normalize-space()='Reject all']"),
            By.xpath("//button[normalize-space()='I Accept']"),
            By.xpath("//button[normalize-space()='Accept all cookies']"),
            By.xpath("//button[normalize-space()='Accept all']"),
            By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'reject')]"),
            By.xpath("//button[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]"),
            By.xpath("//a[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept')]")
    };

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

    protected WebElement waitForAnyVisible(Duration timeout, By... locators) {
        return new WebDriverWait(driver, timeout).until(webDriver -> {
            for (By locator : locators) {
                for (WebElement element : webDriver.findElements(locator)) {
                    if (element.isDisplayed()) {
                        return element;
                    }
                }
            }

            return null;
        });
    }

    protected WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected List<WebElement> waitForAllVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        dismissCookieBannerIfPresent();
        WebElement element = waitForClickable(locator);
        scrollIntoView(element);

        try {
            element.click();
        } catch (StaleElementReferenceException | TimeoutException | NoSuchElementException exception) {
            WebElement refreshedElement = waitForClickable(locator);
            scrollIntoView(refreshedElement);
            jsClick(refreshedElement);
        } catch (ElementClickInterceptedException exception) {
            dismissCookieBannerIfPresent();
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
        dismissCookieBannerIfPresent();
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

    protected boolean isAnyVisible(Duration timeout, By... locators) {
        try {
            waitForAnyVisible(timeout, locators);
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
        driver.switchTo().defaultContent();
        if (dismissCookieBannerInCurrentContext()) {
            return;
        }

        dismissCookieBannerInChildFrames();
    }

    private boolean dismissCookieBannerInCurrentContext() {
        try {
            new WebDriverWait(driver, COOKIE_TIMEOUT).until(webDriver ->
                    hasVisibleElement(COOKIE_DIALOG) || hasVisibleElement(COOKIE_ACTIONS));
        } catch (TimeoutException exception) {
            return false;
        }

        for (By locator : COOKIE_ACTIONS) {
            for (WebElement element : driver.findElements(locator)) {
                if (!element.isDisplayed()) {
                    continue;
                }

                scrollIntoView(element);

                try {
                    element.click();
                } catch (ElementClickInterceptedException exception) {
                    jsClick(element);
                }

                waitForCookieBannerToDisappear();
                return true;
            }
        }

        return false;
    }

    private boolean dismissCookieBannerInChildFrames() {
        List<WebElement> frames = driver.findElements(By.tagName("iframe"));

        for (WebElement frame : frames) {
            try {
                driver.switchTo().frame(frame);

                if (dismissCookieBannerInCurrentContext() || dismissCookieBannerInChildFrames()) {
                    return true;
                }
            } catch (NoSuchElementException | StaleElementReferenceException ignored) {
                // Consent iframes are dynamic and can be re-rendered while the page loads.
            } finally {
                driver.switchTo().parentFrame();
            }
        }

        return false;
    }

    private boolean hasVisibleElement(By... locators) {
        for (By locator : locators) {
            for (WebElement element : driver.findElements(locator)) {
                if (element.isDisplayed()) {
                    return true;
                }
            }
        }

        return false;
    }

    private void waitForCookieBannerToDisappear() {
        try {
            new WebDriverWait(driver, COOKIE_TIMEOUT).until(webDriver ->
                    !hasVisibleElement(COOKIE_DIALOG) && !hasVisibleElement(COOKIE_ACTIONS));
        } catch (TimeoutException ignored) {
            // Some CMPs keep hidden controls mounted in the DOM after closing the banner.
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
