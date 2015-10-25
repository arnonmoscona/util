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

package com.moscona.util.app.lifecycle.events;

import com.moscona.util.app.lifecycle.EventBase;

/**
 * Created: 2/17/14 11:19 AM
 * By: Arnon Moscona
 * An alert (usually to be routed to logging, or UI)
 */
public class ApplicationAlertEvent extends EventBase {

    public static final String METADATA_KEY_MESSAGE = "message";
    public static final String DESCRIPTION = "Alert";
    public static final String NAME = "APP_ALERT";

    public ApplicationAlertEvent() {
        super("APP_ALERT", "Alert");
    }

    public ApplicationAlertEvent(String message) {
        super(NAME, DESCRIPTION, METADATA_KEY_MESSAGE, message);
    }
}
