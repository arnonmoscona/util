package com.moscona.exceptions;

/**
 * Created: Mar 23, 2010 5:50:16 PM
 * By: Arnon Moscona
 */
public class InvalidStateException extends DecoratedException {
    private static final long serialVersionUID = 2866919244262295837L;

    public InvalidStateException(String message) {
        super(message);
    }
    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStateException(String message, Object decoration) {
        super(message, decoration);
    }

    public InvalidStateException(String message, Throwable cause, Object decoration) {
        super(message, cause, decoration);
    }
}
