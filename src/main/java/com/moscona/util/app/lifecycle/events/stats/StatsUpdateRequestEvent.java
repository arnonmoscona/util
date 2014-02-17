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
