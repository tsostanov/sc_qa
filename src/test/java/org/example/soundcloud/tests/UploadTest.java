package org.example.soundcloud.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.pages.HomePage;
import org.example.soundcloud.pages.UploadPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class UploadTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] should navigate to upload area in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenUploadPage(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);

        UploadPage uploadPage = homePage.goToUpload();

        assertTrue(uploadPage.isUploadPageOpened(), "Upload page or upload gateway should be opened");
    }

    @ParameterizedTest(name = "[{index}] should require authorization for upload in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldRequireAuthorizationForUpload(BrowserType browserType) {
        HomePage homePage = openHomePage(browserType);
        UploadPage uploadPage = homePage.goToUpload();

        assertTrue(uploadPage.isAuthorizationRequired(),
                "Upload flow should require authorization for anonymous users");
    }
}
