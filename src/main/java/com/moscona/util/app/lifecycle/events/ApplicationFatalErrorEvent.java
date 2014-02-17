package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.ApplicationEvent;

/**
 * Created: 2/17/14 11:08 AM
 * By: Arnon Moscona
 */
public class ApplicationFatalErrorEvent extends ApplicationEvent {

    public static final String NAME = "FATAL_ERROR_RESTART";
    public static final String DESCRIPTION = "Fatal error requiring application restart";
    public static final String METADATA_KEY_EXCEPTION = "exception";

    public ApplicationFatalErrorEvent() {
        super("FATAL_ERROR_RESTART", "Fatal error requiring application restart");
    }

    public ApplicationFatalErrorEvent(Throwable e) {
        super(NAME, DESCRIPTION, METADATA_KEY_EXCEPTION, e);
    }
}
