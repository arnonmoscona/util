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

package com.moscona.util;

import java.util.Map;

/**
 * Created: Mar 30, 2010 4:08:55 PM
 * By: Arnon Moscona
 * A service that channels operational alerts from the system to any user notification mechanism
 * (e.g. log, GUI, IM or whatever)
 */
public interface IAlertService {
    /**
     * The simplest way to use this. Just alert with a simple message.
     * Warning: avoid using this form for alerts that may come at a high frequency and may need to be filtered.
     * @param message - the alert message
     */
    public void sendAlert(String message);

    /**
     * The common form of this alert. More frequently issued alert types should have a fixed messageType.
     * @param message - the alert message
     * @param messageType - the "type" of message. While the text of the message may vary a lot, for the
     *                      same type of message (e.g. stale record alert) the type parameters should remain
     *                      fixed for easier classification and filtering.
     */
    public void sendAlert(String message, String messageType);
    /**
     * Warning: avoid using this form for alerts that may come at a high frequency and may need to be filtered.
     * @param message - the alert message
     * @param ex - an exception that is associated with this alert
     */
    public void sendAlert(String message, Throwable ex);
    /**
     * @param message - the alert message
     * @param messageType - the "type" of message. While the text of the message may vary a lot, for the
     *                      same type of message (e.g. stale record alert) the type parameters should remain
     *                      fixed for easier classification and filtering.
     * @param ex - an exception that is associated with this alert
     */
    public void sendAlert(String message, String messageType, Throwable ex);

    /**
     * Type-safe conversion of the map that this class dumps to the event publisher.
     * @param metadata the metadata to translate (presumably produced by this class)
     * @return either null (if argument was null) or a translated metadata
     */
    public LogItem getEventMetadata(Map<String, Object> metadata);
}
