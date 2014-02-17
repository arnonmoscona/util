package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.EventBase;

/**
 * Created: 2/17/14 11:52 AM
 * By: Arnon Moscona
 */
public class StatusUpdateEvent extends EventBase {

    public static final String METADATA_KEY_MESSAGE = "message";

    public StatusUpdateEvent(String message) {
        super("STATUS_UPDATE", "Status update", METADATA_KEY_MESSAGE, message);
    }
}
