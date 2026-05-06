package org.example.soundcloud.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.core.TestData;
import org.example.soundcloud.pages.HomePage;
import org.example.soundcloud.pages.LoginPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class AuthTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] should open login form in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenLoginForm(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);

        LoginPage loginPage = homePage.openLoginForm();

        assertTrue(loginPage.isLoginFormOpened(), "Login form should be opened");
    }

    @ParameterizedTest(name = "[{index}] should reject invalid credentials in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldRejectInvalidCredentials(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);
        LoginPage loginPage = homePage.openLoginForm();

        assertTrue(loginPage.isLoginFormOpened(), "Login form should be opened before submitting credentials");

        loginPage.tryLogin(TestData.INVALID_EMAIL, TestData.INVALID_PASSWORD);

        assertTrue(loginPage.hasErrorMessage() || loginPage.isLoginFormOpened(),
                "User should remain unauthorized after invalid login attempt");
    }
}
