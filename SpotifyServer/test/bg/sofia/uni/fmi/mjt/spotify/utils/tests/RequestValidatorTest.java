package bg.sofia.uni.fmi.mjt.spotify.utils.tests;

import static bg.sofia.uni.fmi.mjt.spotify.server.utils.RequestValidator.isValidRequest;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.spotify.tests.utils.CommonTestData;

public class RequestValidatorTest {

    @Test
    public void validateEmptyCommandTest() {
        assertFalse(isValidRequest(CommonTestData.EMPTY_LIST));
    }

    @Test
    public void validateInvalidCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_COMMAND));
    }

    @Test
    public void validateValidRegisterCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_REGISTER_COMMAND));
    }

    @Test
    public void validateInvalidRegisterCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_REGISTER_COMMAND));
    }

    @Test
    public void validateValidLoginCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_LOGIN_COMMAND));
    }

    @Test
    public void validateInvalidLoginCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_LOGIN_COMMAND));
    }

    @Test
    public void validateValidAudioLoginCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_AUDIO_LOGIN_COMMAND));
    }

    @Test
    public void validateInvalidAudioLoginCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_AUDIO_LOGIN_COMMAND));
    }

    @Test
    public void validateValidDisconnectCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_DISCONNECT_COMMAND));
    }

    @Test
    public void validateInvalidDisconnectCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_DISCONNECT_COMMAND));
    }

    @Test
    public void validateValidSearchSongsCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_SEARCH_SONGS_COMMAND));
    }

    @Test
    public void validateInvalidSearchSongsCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_SEARCH_SONGS_COMMAND));
    }

    @Test
    public void validateValidTopListenedSongsCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_TOP_LISTENED_SONGS_COMMAND));
    }

    @Test
    public void validateInvalidTopListenedSongsCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_TOP_LISTENED_SONGS_COMMAND));
    }

    @Test
    public void validateSecondInvalidTopListenedSongsCommandTest() {
        assertFalse(isValidRequest(CommonTestData.SECOND_PARSED_INVALID_TOP_LISTENED_SONGS_COMMAND));
    }

    @Test
    public void validateValidCreatePlaylistCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_CREATE_PLAYLIST_COMMAND));
    }

    @Test
    public void validateInvalidCreatePlaylistCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_CREATE_PLAYLIST_COMMAND));
    }

    @Test
    public void validateValidAddSongToPlaylistCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_ADD_SONG_TO_PLAYLIST_COMMAND));
    }

    @Test
    public void validateInvalidAddSongToPlaylistCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_ADD_SONG_TO_PLAYLIST_COMMAND));
    }

    @Test
    public void validateValidShowPlaylistCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_SHOW_PLAYLIST_COMMAND));
    }

    @Test
    public void validateInvalidShowPlaylistCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_SHOW_PLAYLIST_COMMAND));
    }

    @Test
    public void validateValidPlaySongCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_PLAY_SONG_COMMAND));
    }

    @Test
    public void validateInvalidPlaySongCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_PLAY_SONG_COMMAND));
    }

    @Test
    public void validateValidStopSongCommandTest() {
        assertTrue(isValidRequest(CommonTestData.PARSED_VALID_STOP_SONG_COMMAND));
    }

    @Test
    public void validateInvalidStopSongCommandTest() {
        assertFalse(isValidRequest(CommonTestData.PARSED_INVALID_STOP_SONG_COMMAND));
    }
}
