package org.example.soundcloud.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.example.soundcloud.core.BrowserType;
import org.example.soundcloud.pages.ArtistPage;
import org.example.soundcloud.pages.TrackPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class PlayerTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] should start playback in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldStartPlayback(BrowserType browserType) {
        TrackPage trackPage = openDefaultTrack(browserType);

        assertTrue(trackPage.isTrackPageOpened(), "Track page should be opened before playback");

        trackPage.play();

        assertTrue(trackPage.isPlaying(), "Track should switch to the playing state");
    }

    @ParameterizedTest(name = "[{index}] should pause playback in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldPausePlayback(BrowserType browserType) {
        TrackPage trackPage = openDefaultTrack(browserType);
        trackPage.play();

        assertTrue(trackPage.isPlaying(), "Track should be playing before pause");

        trackPage.pause();

        assertTrue(trackPage.isPaused(), "Track should switch back to the paused state");
    }

    @ParameterizedTest(name = "[{index}] should open artist page in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenArtistPage(BrowserType browserType) {
        TrackPage trackPage = openDefaultTrack(browserType);

        ArtistPage artistPage = trackPage.openArtistPage();

        assertTrue(artistPage.isArtistPageOpened(), "Artist page should be opened");
        assertTrue(artistPage.hasArtistName(), "Artist page should display artist name");
    }

    @ParameterizedTest(name = "[{index}] should open share dialog in {0}")
    @MethodSource("org.example.soundcloud.tests.BaseTest#browsers")
    void shouldOpenShareDialog(BrowserType browserType) {
        TrackPage trackPage = openDefaultTrack(browserType);

        trackPage.openShareDialog();

        assertTrue(trackPage.isShareDialogOpened(), "Share dialog should be opened");
    }
}
