package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.EventBase;

/**
 * Created: 2/17/14 11:19 AM
 * By: Arnon Moscona
 * An alert (usually to be routed to logging, or UI)
 */
public class ApplicationAlertEvent extends EventBase {

    public static final String METADATA_KEY_MESSAGE = "message";
    public static final String DESCRIPTION = "Alert";
    public static final String NAME = "APP_ALERT";

    public ApplicationAlertEvent() {
        super("APP_ALERT", "Alert");
    }

    public ApplicationAlertEvent(String message) {
        super(NAME, DESCRIPTION, METADATA_KEY_MESSAGE, message);
    }
}
