package com.moscona.util;

/**
 * Created: Jun 15, 2010 10:45:20 AM
 * By: Arnon Moscona
 */
public class ExceptionHelper {
    public static Throwable fishOutOf(Throwable exception, Class typeToFishOut, int maxCauseDepth) {
        if (maxCauseDepth<0) {
            return null;
        }
        if (typeToFishOut.isAssignableFrom(exception.getClass())) {
            return exception;
        }
        Throwable cause = exception.getCause();
        if (cause==null) {
            return null;
        }
        return fishOutOf(cause, typeToFishOut ,maxCauseDepth-1);
    }

    public static Throwable findRootCause(Throwable exception, int maxCauseDepth) {
        if (maxCauseDepth<0) {
            return null;
        }
        Throwable cause = exception.getCause();
        if (cause == null) {
            return exception; // it is the root cause
        }
        return findRootCause(cause, maxCauseDepth-1);
    }
}
