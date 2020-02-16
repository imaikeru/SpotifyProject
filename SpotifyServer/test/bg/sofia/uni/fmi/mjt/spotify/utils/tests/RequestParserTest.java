package bg.sofia.uni.fmi.mjt.spotify.utils.tests;

import static bg.sofia.uni.fmi.mjt.spotify.server.utils.RequestParser.parseRequest;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import bg.sofia.uni.fmi.mjt.spotify.tests.utils.CommonTestData;

public class RequestParserTest {

    private void assertListEquals(final List<String> expected, final List<String> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

    @Test
    public void parseEmptyCommandTest() {
        assertListEquals(CommonTestData.EMPTY_LIST, parseRequest(CommonTestData.EMPTY_COMMAND));
    }

    @Test
    public void parseInvalidCommandTest() {
        assertListEquals(CommonTestData.PARSED_INVALID_COMMAND, parseRequest(CommonTestData.INVALID_COMMAND));
    }

    @Test
    public void parseValidLoginCommandTest() {
        assertListEquals(CommonTestData.PARSED_VALID_LOGIN_COMMAND, parseRequest(CommonTestData.VALID_LOGIN_COMMAND));
    }

    @Test
    public void parseValidAudioLoginCommandTest() {
        assertListEquals(CommonTestData.PARSED_VALID_AUDIO_LOGIN_COMMAND,
                parseRequest(CommonTestData.VALID_AUDIO_LOGIN_COMMAND));
    }

    @Test
    public void parseValidRegisterCommandTest() {
        assertListEquals(CommonTestData.PARSED_VALID_REGISTER_COMMAND,

                parseRequest(CommonTestData.VALID_REGISTER_COMMAND));
    }

    @Test
    public void parseValidDisconnectCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_DISCONNECT_COMMAND,

                parseRequest(CommonTestData.VALID_DISCONNECT_COMMAND));
    }

    @Test
    public void parseValidSearchSongsCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_SEARCH_SONGS_COMMAND,
                parseRequest(CommonTestData.VALID_SEARCH_SONGS_COMMAND));
    }

    @Test
    public void parseValidTopListenedSongsCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_TOP_LISTENED_SONGS_COMMAND,
                parseRequest(CommonTestData.VALID_TOP_LISTENED_SONGS_COMMAND));
    }

    @Test
    public void parseValidCreatePlaylistCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_CREATE_PLAYLIST_COMMAND,
                parseRequest(CommonTestData.VALID_CREATE_PLAYLIST_COMMAND));
    }

    @Test
    public void parseValidAddSongToPlaylistCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_ADD_SONG_TO_PLAYLIST_COMMAND,
                parseRequest(CommonTestData.VALID_ADD_SONG_TO_PLAYLIST_COMMAND));
    }

    @Test
    public void parseValidShowPlaylistCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_SHOW_PLAYLIST_COMMAND,
                parseRequest(CommonTestData.VALID_SHOW_PLAYLIST_COMMAND));
    }

    @Test
    public void parseValidPlaySongCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_PLAY_SONG_COMMAND,
                parseRequest(CommonTestData.VALID_PLAY_SONG_COMMAND));
    }

    @Test
    public void parseValidStopSongCommand() {
        assertListEquals(CommonTestData.PARSED_VALID_STOP_SONG_COMMAND,
                parseRequest(CommonTestData.VALID_STOP_SONG_COMMAND));
    }
}
