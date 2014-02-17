package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.ApplicationEvent;

/**
 * Created: 2/17/14 11:15 AM
 * By: Arnon Moscona
 * An event notifying that the process of shutting down has started
 */
public class ApplicationShutDownInProgressEvent extends ApplicationEvent {
    public ApplicationShutDownInProgressEvent() {
        super("APP_SHUTDOWN_IN_PROGRESS", "Application shutdown initiated");
    }
}
