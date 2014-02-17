package com.moscona.util.app.lifecycle.events.task;

import com.moscona.util.app.lifecycle.TaskEvent;

/**
 * Created: 2/17/14 11:35 AM
 * By: Arnon Moscona
 * fired when tasks that later will send progress updates via TASK_PROGRESS start. Uses "taskName" in metadata to denote which task.
 */
public class TaskStartedEvent extends TaskEvent {
    public TaskStartedEvent(String taskName) {
        super("TASK_STARTED", "Task started: " + taskName, taskName);
    }
}
