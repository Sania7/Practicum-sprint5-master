package exception;

public class TimeErrorException extends RuntimeException {
    public TimeErrorException(final String message) {
        super(message);
    }
}
