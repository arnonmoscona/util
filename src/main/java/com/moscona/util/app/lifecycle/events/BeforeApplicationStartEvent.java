package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.ApplicationEvent;

/**
 * Created: 2/17/14 11:11 AM
 * By: Arnon Moscona
 * An event designed to broadcast application bootstrap and initialization in progress
 */
public class BeforeApplicationStartEvent extends ApplicationEvent {
    public BeforeApplicationStartEvent() {
        super("APP_STARTING", "Application startup in progress");
    }
}
