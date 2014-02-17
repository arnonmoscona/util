package com.moscona.util.app.lifecycle;

/**
 * Created: 2/17/14 11:05 AM
 * By: Arnon Moscona
 * Application-wide lifecycle events
 */
public abstract class ApplicationEvent extends EventBase {
    protected ApplicationEvent(String name, String description) {
        super(name, description);
    }

    protected ApplicationEvent(String name, String description, String key, Object value) {
        super(name, description, key, value);
    }
}
