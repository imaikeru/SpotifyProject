package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class PlaylistNotFoundException extends RuntimeException {

    public PlaylistNotFoundException() {
        super();
    }

    public PlaylistNotFoundException(final String message) {
        super(message);
    }
}
