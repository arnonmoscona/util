package com.moscona.util;

/**
 * Created: Apr 23, 2010 1:33:31 PM
 * By: Arnon Moscona
 * A runnable that handles code that throws exceptions
 */
public abstract class SafeRunnable implements Runnable {
    private Exception exception=null;
    private String name = "unnamed";

    public abstract void runWithPossibleExceptions() throws Exception;

    public Exception getException() {
        return exception;
    }

    @Override
    public void run() {
        try {
            runWithPossibleExceptions();
        }
        catch (Exception e) {
            exception = e;
        }
    }

    /**
     * Throws the exception that was captured during run() if one was thrown
     * @throws Exception the captured exception
     */
    public void rethrow() throws Exception {
        if (exception!=null)
            throw exception;
    }

    public SafeRunnable setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }
}
