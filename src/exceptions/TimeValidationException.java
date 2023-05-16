package exceptions;

import java.io.IOException;

public class TimeValidationException extends RuntimeException {
    public TimeValidationException(String message) {
        super(message);
    }

    public TimeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeValidationException(Throwable cause) {
        super(cause);
    }
}
