package com.moscona.exceptions;

/**
 * Created: Mar 16, 2010 2:25:38 PM
 * By: Arnon Moscona
 */
public class InvalidArgumentException extends DecoratedException {
    private static final long serialVersionUID = 2558374580883931737L;

    public InvalidArgumentException(String message) {
        super(message);
    }
    public InvalidArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidArgumentException(String message, Object decoration) {
        super(message, decoration);
    }

    public InvalidArgumentException(String message, Throwable cause, Object decoration) {
        super(message, cause, decoration);
    }
}
