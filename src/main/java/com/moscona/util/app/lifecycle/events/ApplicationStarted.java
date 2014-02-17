package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.ApplicationEvent;

/**
 * Created: 2/17/14 11:13 AM
 * By: Arnon Moscona
 * An event to be published after initialization is done
 */
public class ApplicationStarted extends ApplicationEvent {
    public ApplicationStarted() {
        super("APP_START", "Application initialization complete");
    }
}
