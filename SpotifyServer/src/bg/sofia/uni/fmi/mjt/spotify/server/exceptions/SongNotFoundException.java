package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class SongNotFoundException extends RuntimeException {

    public SongNotFoundException() {
        super();
    }

    public SongNotFoundException(final String message) {
        super(message);
    }

}
