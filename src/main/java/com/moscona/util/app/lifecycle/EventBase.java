/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
