package com.moscona.exceptions;

/**
 * Created: 10/6/11 4:05 PM
 * By: Arnon Moscona
 */
public class DecoratedException extends Exception {
    private static final long serialVersionUID = 6329237889442808811L;
    private Object decoration = null;

    public DecoratedException(String message) {
        super(message);
    }

    public DecoratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoratedException(String message, Object decoration) {
        super(message);
        this.decoration = decoration;
    }

    public DecoratedException(String message, Throwable cause, Object decoration) {
        super(message, cause);
        this.decoration = decoration;
    }

    public Object getDecoration() {
        return decoration;
    }
}
