package com.moscona.util;

/**
 * A simple value class to represent a message that was sen.
 * Used in alert service interface
 */
public class LogItem {
    private String message;
    private String messageType;
    private Throwable exception;

    public LogItem(String message, String messageType, Throwable exception) {
        this.message = message;
        this.messageType = messageType;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageType() {
        return messageType;
    }

    public Throwable getException() {
        return exception;
    }
}
