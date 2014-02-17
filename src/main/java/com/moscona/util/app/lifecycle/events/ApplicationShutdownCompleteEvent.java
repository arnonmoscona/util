package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.ApplicationEvent;

/**
 * Created: 2/17/14 11:17 AM
 * By: Arnon Moscona
 * An event notifying that the shutdown process has completed - about to exit. All resources must be cleaned up.
 */
public class ApplicationShutdownCompleteEvent extends ApplicationEvent {
    public ApplicationShutdownCompleteEvent() {
        super("APP_SHUTDOWN_COMPLETE", "Shutdown complete");
    }
}
