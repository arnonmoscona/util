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

package com.moscona.util.app.lifecycle.events.stats;

import com.moscona.util.app.lifecycle.EventBase;

/**
 * Created: 2/17/14 12:00 PM
 * By: Arnon Moscona
 * fired when a component wants to publish a stat.
 */
public class StatsUpdateResponseEvent extends EventBase {
    /**
     * Create a stats update. Receivers are expected to know how to handle the stats objects.
     * No limit is set on the semantics of published stats.
     * You can create specialized subclasses to express a more type-safe approach
     * @param statsObjectIdentifier identifies the stats object ("name"). Used when the class of the stats object is insufficient to identify what it is.
     * @param stats the concrete stats object
     */
    public StatsUpdateResponseEvent(String statsObjectIdentifier, Object stats) {
        super("STATS_UPDATE_RESPONSE", "Stats update response", statsObjectIdentifier, stats);
    }
}
