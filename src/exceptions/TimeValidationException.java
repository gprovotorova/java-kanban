package exceptions;

import java.io.IOException;

public class TimeValidationException extends IOException {
    public TimeValidationException(String message) {
        super(message);
    }
}
