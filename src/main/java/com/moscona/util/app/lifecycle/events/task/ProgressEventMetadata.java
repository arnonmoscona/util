package com.moscona.util.app.lifecycle.events.task;

import com.moscona.exceptions.InvalidArgumentException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created: Jun 14, 2010 4:20:41 PM
 * By: Arnon Moscona
 * A simple bean to make standard templates for progress events
 */
public class ProgressEventMetadata {
    private String taskName;
    private int progress; // 0-100
    private String progressString;
    public static final String TASK_NAME = "taskName";
    public static final String PROGRESS = "progress";
    public static final String PROGRESS_STRING = "progressString";

    public ProgressEventMetadata(String taskName, int progress) throws InvalidArgumentException {
        this(taskName, progress, null);
    }

    public ProgressEventMetadata(String taskName, int progress, String progressString) throws InvalidArgumentException {
        this.taskName = taskName;
        this.progress = progress;
        this.progressString = progressString;
        validate();
    }

    /**
     *
     * @param eventMetaData
     * @throws InvalidArgumentException
     * @throws NumberFormatException
     */
    public ProgressEventMetadata(Map<String, Object> eventMetaData) throws InvalidArgumentException, NumberFormatException {
        this(eventMetaData.get(TASK_NAME).toString(),
                Integer.parseInt(eventMetaData.get(PROGRESS).toString()),
                eventMetaData.get(PROGRESS_STRING).toString());
    }

    public Map<String, Object> toMap() {
        Map<String, Object> retval = new HashMap<String, Object>();
        retval.put(TASK_NAME,taskName);
        retval.put(PROGRESS, progress);
        retval.put(PROGRESS_STRING, progressString);
        return retval;
    }

    private void validate() throws InvalidArgumentException {
        if (progress < 0 || progress > 100) {
            throw new InvalidArgumentException("the progress value must be an integer in the range 0..100");
        }
        if (StringUtils.isBlank(taskName)) {
            throw new InvalidArgumentException("the task name may not be blank.");
        }
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getProgressString() {
        return progressString;
    }

    public void setProgressString(String progressString) {
        this.progressString = progressString;
    }
}
