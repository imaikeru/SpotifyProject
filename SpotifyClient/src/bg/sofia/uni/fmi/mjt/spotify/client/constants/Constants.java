/**
 *
 */
package bg.sofia.uni.fmi.mjt.spotify.client.constants;

public class Constants {
    public static final int BUFFER_SIZE = 1024;
    public static final int SERVER_PORT = 6664;
    public static final String SERVER_HOST = "localhost";
    public static final int AUDIO_FORMAT_ARGUMENTS = 7;

    public static final String WHITESPACE_DELIMITER = "\\s+";
    public static final String SPACE = " ";

    public static final String INVALID_AUDIO_FORMAT_MESSAGE = "Invalid audio format received.";
    public static final String SUCCESSFUL_LOGIN_MESSAGE = "Successfully logged in as:";
    public static final String SUCCESSFUL_AUDIO_LOGIN_MESSAGE = "Successfully logged in audio as:";
    public static final String STOPPED_SONG_MESSAGE = "Stopped song:";

    public static final String DISCONNECT_COMMAND = "disconnect";
    public static final String LOGIN_COMMAND = "login";
    public static final String AUDIO_LOGIN_COMMAND = "audio-login";

    public static final int ENCODING_INDEX = 0;
    public static final int SAMPLE_RATE_INDEX = 1;
    public static final int SAMPLE_SIZE_IN_BITS_INDEX = 2;
    public static final int CHANNELS_INDEX = 3;
    public static final int FRAME_SIZE_INDEX = 4;
    public static final int FRAME_RATE_INDEX = 5;
    public static final int BIG_ENDIAN_INDEX = 6;
}
