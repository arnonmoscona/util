package com.moscona.util.app.lifecycle.events.task;

import com.moscona.util.app.lifecycle.TaskEvent;
import com.moscona.util.app.lifecycle.events.task.ProgressEventMetadata;

/**
 * Created: 2/17/14 11:45 AM
 * By: Arnon Moscona
 * fired when tasks make progress that some progress indicator may (or may not) show. Uses metadata compatible with ProgressEventMetadata
 */
public class TaskProgressEvent extends TaskEvent {
    public TaskProgressEvent(ProgressEventMetadata metadata) {
        super("TASK_PROGRESS", "Task progress: "+metadata.getTaskName(), metadata.getTaskName());
        attachMetadata(metadata.toMap());
    }
}
