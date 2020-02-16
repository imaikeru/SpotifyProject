package bg.sofia.uni.fmi.mjt.spotify.server.exceptions;

public class IllegalOperationException extends RuntimeException {

    public IllegalOperationException() {
        super();
    }

    public IllegalOperationException(final String message) {
        super(message);
    }

}
