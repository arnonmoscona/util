package com.moscona.util.app.lifecycle.events.task;

import com.moscona.util.app.lifecycle.TaskEvent;

/**
 * Created: 2/17/14 11:40 AM
 * By: Arnon Moscona
 * fired after a task has finished and no more progress events are expected
 */
public class TaskFinishedEvent extends TaskEvent {
    public TaskFinishedEvent(String taskName) {
        super("TASK_FINISHED", "Task finished: "+taskName, taskName);
    }
}
