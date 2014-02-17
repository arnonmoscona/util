package com.moscona.util.app.lifecycle;

/**
 * Created: 2/17/14 11:05 AM
 * By: Arnon Moscona
 * Event for application tasks
 */
public abstract class TaskEvent extends EventBase {

    public static final String METADATA_KEY_TASK_NAME = "taskName";

    protected TaskEvent(String name, String description, String taskName) {
        super(name, description, METADATA_KEY_TASK_NAME, taskName);
    }
}
