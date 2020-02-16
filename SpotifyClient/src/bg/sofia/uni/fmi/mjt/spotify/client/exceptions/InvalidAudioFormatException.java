package bg.sofia.uni.fmi.mjt.spotify.client.exceptions;

public class InvalidAudioFormatException extends RuntimeException {

    public InvalidAudioFormatException() {
        super();
    }

    public InvalidAudioFormatException(final String message) {
        super(message);
    }

}
