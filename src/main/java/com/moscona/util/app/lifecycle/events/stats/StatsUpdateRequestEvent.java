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
 * Created: 2/17/14 11:56 AM
 * By: Arnon Moscona
 * fired when someone wants to collect stats from everybody who knows how to publish stats
 */
public class StatsUpdateRequestEvent extends EventBase {
    public StatsUpdateRequestEvent() {
        super("STATS_UPDATE_REQUEST", "Stats update request");
    }
}
