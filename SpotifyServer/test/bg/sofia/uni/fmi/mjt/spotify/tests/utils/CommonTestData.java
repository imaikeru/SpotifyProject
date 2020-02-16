package bg.sofia.uni.fmi.mjt.spotify.tests.utils;

import java.util.List;

@SuppressWarnings({ "nls", "javadoc" })
public class CommonTestData {
    public static final String VALID_REGISTER_COMMAND = "   register  ivancho@abv.bg ivanchoazsum ";
    public static final String INVALID_REGISTER_COMMAND = "  register ivancho@abv.bg";

    public static final List<String> PARSED_VALID_REGISTER_COMMAND = List.of("register", "ivancho@abv.bg",
            "ivanchoazsum");
    public static final List<String> PARSED_INVALID_REGISTER_COMMAND = List.of("register", "ivancho@abv.bg");

    public static final String VALID_LOGIN_COMMAND = "   login  ivancho@abv.bg ivanchoazsum ";
    public static final String INVALID_LOGIN_COMMAND = "  login ivancho@abv.bg ivanchoazsum ama ne sum";

    public static final List<String> PARSED_VALID_LOGIN_COMMAND = List.of("login", "ivancho@abv.bg", "ivanchoazsum");
    public static final List<String> PARSED_INVALID_LOGIN_COMMAND = List.of("login", "ivancho@abv.bg", "ama", "ne",
            "sum");

    public static final String VALID_AUDIO_LOGIN_COMMAND = " audio-login  ivancho@abv.bg ivanchoazsum ";
    public static final String INVALID_AUDIO_LOGIN_COMMAND = "  audio-login ivancho@abv.bg ivanchoazsum ama ne sum";
    public static final List<String> PARSED_VALID_AUDIO_LOGIN_COMMAND = List.of("audio-login", "ivancho@abv.bg",
            "ivanchoazsum");
    public static final List<String> PARSED_INVALID_AUDIO_LOGIN_COMMAND = List.of("audio-login", "ivancho@abv.bg",
            "ama", "ne", "sum");

    public static final String VALID_DISCONNECT_COMMAND = "     disconnect  ";
    public static final String INVALID_DISCONNECT_COMMAND = "  disconnect opa";

    public static final List<String> PARSED_VALID_DISCONNECT_COMMAND = List.of("disconnect");
    public static final List<String> PARSED_INVALID_DISCONNECT_COMMAND = List.of("disconnect", "opa");

    public static final String VALID_SEARCH_SONGS_COMMAND = " search detelini vetrove ";
    public static final String INVALID_SEARCH_SONGS_COMMAND = " search ";

    public static final List<String> PARSED_VALID_SEARCH_SONGS_COMMAND = List.of("search", "detelini", "vetrove");
    public static final List<String> PARSED_INVALID_SEARCH_SONGS_COMMAND = List.of("search");

    public static final String VALID_TOP_LISTENED_SONGS_COMMAND = " top      100 ";
    public static final String INVALID_TOP_LISTENED_SONGS_COMMAND = " top -100 ";

    public static final List<String> PARSED_VALID_TOP_LISTENED_SONGS_COMMAND = List.of("top", "100");
    public static final List<String> PARSED_INVALID_TOP_LISTENED_SONGS_COMMAND = List.of("top", "-100");
    public static final List<String> SECOND_PARSED_INVALID_TOP_LISTENED_SONGS_COMMAND = List.of("top", "fail");

    public static final String VALID_CREATE_PLAYLIST_COMMAND = " create-playlist      \"   Chimi changa\" ";
    public static final String INVALID_CREATE_PLAYLIST_COMMAND = " create-playlist \"ala\" \"bala\" ";

    public static final List<String> PARSED_VALID_CREATE_PLAYLIST_COMMAND = List.of("create-playlist", "Chimi changa");
    public static final List<String> PARSED_INVALID_CREATE_PLAYLIST_COMMAND = List.of("create-playlist", "ala", "bala");

    public static final String VALID_ADD_SONG_TO_PLAYLIST_COMMAND = "  add-song-to          \"Chimi\" \"Changa\"  ";
    public static final String INVALID_ADD_SONG_TO_PLAYLIST_COMMAND = "add-song-to \"neshto\"         ";

    public static final List<String> PARSED_VALID_ADD_SONG_TO_PLAYLIST_COMMAND = List.of("add-song-to", "Chimi",
            "Changa");
    public static final List<String> PARSED_INVALID_ADD_SONG_TO_PLAYLIST_COMMAND = List.of("add-song-to", "neshto");

    public static final String VALID_SHOW_PLAYLIST_COMMAND = " show-playlist \"   muzika  \" ";
    public static final String INVALID_SHOW_PLAYLIST_COMMAND = "show-playlist \"a       \" \"     b\"  ";

    public static final List<String> PARSED_VALID_SHOW_PLAYLIST_COMMAND = List.of("show-playlist", "muzika");
    public static final List<String> PARSED_INVALID_SHOW_PLAYLIST_COMMAND = List.of("show-playlist", "a", "b");

    public static final String VALID_PLAY_SONG_COMMAND = " play \"  Last Resort\" ";
    public static final String INVALID_PLAY_SONG_COMMAND = " play \"muzika\" \"za dushata     \" ";

    public static final List<String> PARSED_VALID_PLAY_SONG_COMMAND = List.of("play", "Last Resort");
    public static final List<String> PARSED_INVALID_PLAY_SONG_COMMAND = List.of("play", "muzika", "za dushata");

    public static final String VALID_STOP_SONG_COMMAND = "     stop";
    public static final String INVALID_STOP_SONG_COMMAND = " stop kazah ";

    public static final List<String> PARSED_VALID_STOP_SONG_COMMAND = List.of("stop");
    public static final List<String> PARSED_INVALID_STOP_SONG_COMMAND = List.of("stop", "kazah");

    public static final String EMPTY_COMMAND = "                         ";
    public static final List<String> EMPTY_LIST = List.of();

    public static final String INVALID_COMMAND = "  neshto";

    public static final List<String> PARSED_INVALID_COMMAND = List.of("neshto");
}
