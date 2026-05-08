package org.example.soundcloud.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);
    protected static final Duration SHORT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration COOKIE_TIMEOUT = Duration.ofSeconds(2);
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
        try {
            driver.get(url);
        } catch (TimeoutException ignored) {
        }

        waitForDocumentReady();
    }

    protected void waitForDocumentReady() {
        try {
            wait.until(webDriver -> {
                Object readyState = ((JavascriptExecutor) webDriver).executeScript("return document.readyState");
                return "interactive".equals(readyState) || "complete".equals(readyState);
            });
        } catch (WebDriverException ignored) {
        }
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
                clickVisibleElement(locator);
                return;
            }
        }

        throw new NoSuchElementException("None of the provided locators is visible");
    }

    protected void clickVisibleElement(By locator) {
        dismissCookieBannerIfPresent();

        WebElement visibleElement = new WebDriverWait(driver, DEFAULT_TIMEOUT).until(webDriver -> {
                for (WebElement element : webDriver.findElements(locator)) {
                    try {
                        if (element.isDisplayed()) {
                            return element;
                        }
                    } catch (StaleElementReferenceException ignored) {
                    }
                }

            return null;
        });

        scrollIntoView(visibleElement);

        try {
            visibleElement.click();
        } catch (ElementClickInterceptedException exception) {
            dismissCookieBannerIfPresent();
            jsClick(visibleElement);
        } catch (StaleElementReferenceException exception) {
            click(locator);
        }
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
        } catch (TimeoutException | NoSuchWindowException | NoSuchSessionException exception) {
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
        int visibleCount = 0;

        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    visibleCount++;
                }
            } catch (StaleElementReferenceException ignored) {
            }
        }

        return visibleCount;
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

    protected String attribute(By locator, String attributeName) {
        return waitForVisible(locator).getAttribute(attributeName);
    }

    protected void dismissCookieBannerIfPresent() {
        try {
            driver.switchTo().defaultContent();
            if (dismissCookieBannerInCurrentContext()) {
                return;
            }

            dismissCookieBannerInChildFrames();
        } catch (NoSuchWindowException | NoSuchSessionException ignored) {
        }
    }

    private boolean dismissCookieBannerInCurrentContext() {
        try {
            new WebDriverWait(driver, COOKIE_TIMEOUT).until(webDriver -> hasVisibleCookieAction());
        } catch (WebDriverException exception) {
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
                } catch (WebDriverException exception) {
                    continue;
                }

                waitForCookieBannerToDisappear();
                return true;
            }
        }

        return false;
    }

    private boolean dismissCookieBannerInChildFrames() {
        List<WebElement> frames = driver.findElements(By.xpath("//iframe"));

        for (WebElement frame : frames) {
            try {
                driver.switchTo().frame(frame);

                if (dismissCookieBannerInCurrentContext() || dismissCookieBannerInChildFrames()) {
                    return true;
                }
            } catch (WebDriverException ignored) {
            } finally {
                driver.switchTo().parentFrame();
            }
        }

        return false;
    }

    private boolean hasVisibleElement(By... locators) {
        for (By locator : locators) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed()) {
                        return true;
                    }
                }
            } catch (WebDriverException exception) {
                return false;
            }
        }

        return false;
    }

    private boolean hasVisibleCookieAction() {
        for (By locator : COOKIE_ACTIONS) {
            try {
                for (WebElement element : driver.findElements(locator)) {
                    if (element.isDisplayed()) {
                        return true;
                    }
                }
            } catch (WebDriverException exception) {
                return false;
            }
        }

        return false;
    }

    private void waitForCookieBannerToDisappear() {
        try {
            new WebDriverWait(driver, COOKIE_TIMEOUT).until(webDriver ->
                    !hasVisibleCookieAction());
        } catch (TimeoutException ignored) {
        }
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
    }

    protected void jsClick(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    protected void pause(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread was interrupted while waiting", exception);
        }
    }
}
