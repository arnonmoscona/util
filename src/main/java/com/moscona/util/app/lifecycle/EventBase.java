package com.moscona.util.app.lifecycle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: 2/14/14 8:25 AM
 * By: Arnon Moscona
 * A base class for events.
 * The design of the events is intended to be used with in-memory event publishing frameworks like Google Guava
 * EventBus or Mbassador. This could probably work with Spring ApplicationEvent as well, but it would require additional
 * work. One way to adapt this to Spring is to create an ApplicationEvent subclass that contains one of the events here.
 */
public abstract class EventBase {
    private Map<String, Object> transientMetadata = null;
    private String name;
    private String description;
    private boolean isConstructorMetadataAttached = false;

    protected EventBase(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected EventBase(String name, String description, String key, Object value) {
        HashMap<String, Object> metaData  = new HashMap<>();
        metaData.put(key, value);
        attachMetadata(metaData);
        isConstructorMetadataAttached = true;
    }

    /**
     * Attaches transient metadata to the event
     * @param transientMetadata the metadata to attach
     */
    public synchronized void attachMetadata(Map<String, Object> transientMetadata) {
        if (isConstructorMetadataAttached) {
            HashMap<String, Object> metaData  = new HashMap<>();
            metaData.putAll(transientMetadata);
            metaData.putAll(this.transientMetadata);
            this.transientMetadata = metaData;
        }
        else  {
            this.transientMetadata = transientMetadata;
        }
    }

    public synchronized Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(transientMetadata);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
