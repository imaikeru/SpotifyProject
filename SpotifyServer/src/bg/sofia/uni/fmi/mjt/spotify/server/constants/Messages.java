package bg.sofia.uni.fmi.mjt.spotify.server.constants;

public class Messages {
//    public static final String = ;
    public static final String SUCCESSFULLY_REGISTERED = "Successfully registered email ";
    public static final String SUCCESSFUL_LOGIN = "Successfully logged in as: ";
    public static final String SUCCESSFUL_AUDIO_LOGIN = "Successfully logged in audio as: ";
    public static final String UNSUCCESSFUL_LOGIN = "Unsuccessful login attempt. Wrong email or password";
    public static final String SUCCESSFULLY_CREATED_PLAYLIST = "Successfully created playlist: ";
    public static final String SUCCESSFULLY_ADDED_SONG = "Successfully added song ";
    public static final String TO_PLAYLIST = "to playlist ";

    public static final String MOST_POPULAR_SONGS = "Most popular songs are:";

    public static final String PLAYING_SONG = "Playing song: ";
    public static final String STOPPED_SONG = "Stopped song: ";

    public static final String WRONG_COMMAND = "Wrong command. ";

    public static final String[] AVAILABLE_COMMANDS_LIST = { "register <email> <password>", "login <email> <password>",
            "disconnect", "search <words>", "top <number>", "create-playlist \"name_of_the_playlist\"",
            "add-song-to \"name_of_the_playlist\" \"song\"", "show-playlist \"name_of_the_playlist\"", "play \"song\"",
            "stop" };

    public static final String AVAILABLE_COMMANDS = "Choose between:" + CommonConstants.NEW_LINE
            + String.join(CommonConstants.NEW_LINE, AVAILABLE_COMMANDS_LIST);

    public static final String CANNOT_LOGIN_WHILE_LOGGED_IN = "Invalid request. Cannot login if you are already logged in.";
    public static final String WRONG_EMAIL_OR_PASSWORD = "Wrong email or password. Try again.";

    public static final String FOUND_SONGS = "Found songs are: ";
    public static final String NO_SONGS_FOUND = "No songs matching the searched words were found.";

    public static final String PLAYLIST_NOT_FOUND = "Playlist not found: ";
    public static final String SONG_NOT_FOUND = "Song not found: ";

    public static final String PLAYLIST_ALREADY_CONTAINS_SONG = "Playlist already contains song: ";

    public static final String CANNOT_AUDIO_CONNECT_WITHOUT_MESSAGE_CONNECTION = "Cannot register audio connection if there is no message connection.";
    public static final String ILLEGAL_AUDIO_CONNECTION = "Audio login can be attempted only once, when initiating audio connection.";

    public static final String CANNOT_REGISTER_IF_LOGGED_IN = "Cannot register if already logged in.";
    public static final String CANNOT_REGISTER_THE_SAME_EMAIL_TWICE = "Cannot register the same email twice.";

    public static final String CANNOT_PLAY_SONG_IF_NOT_LOGGED_IN = "Cannot play a song if not logged in.";
    public static final String ALREADY_LISTENING_TO_MUSIC = "Already listening to music. Must stop first.";

    public static final String CANNOT_STOP_SONG_IF_NOT_LOGGED_IN = "Cannot stop a song if not logged in.";
    public static final String CANNOT_STOP_SONG_IF_NOT_LISTENING_TO_ONE = "Cannot stop a song if not listening to one.";

    public static final String CANNOT_CREATE_PLAYLIST_IF_NOT_LOGGED_IN = "Cannot create a playlist if not logged in.";
    public static final String CANNOT_CREATE_PLAYLIST_WITH_ALREADY_USED_NAME = "Cannot create a playlist with this name since it has already been used.";

    public static final String CANNOT_ADD_SONG_TO_PLAYLIST_IF_NOT_LOGGED_IN = "Cannot add a song to a playlist if not logged in.";

    public static final String CANNOT_SHOW_PLAYLIST_IF_NOT_LOGGED_IN = "Cannot show a playlist if not logged in.";

    public static final String CANNOT_SHOW_MOST_POPULAR_SONGS_IF_NOT_LOGGED_IN = "Cannot show the most popular songs if not logged in.";

    public static final String CANNOT_SEARCH_FOR_SONGS_IF_NOT_LOGGED_IN = "Cannot search for songs if not logged in.";
}
