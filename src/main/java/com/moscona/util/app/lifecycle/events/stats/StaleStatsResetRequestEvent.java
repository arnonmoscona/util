package com.moscona.util.app.lifecycle.events.stats;

import com.moscona.util.app.lifecycle.EventBase;

/**
 * Created: 2/17/14 11:59 AM
 * By: Arnon Moscona
 * fired when someone wants to reset the stale stats (facilitates GUI control)
 */
public class StaleStatsResetRequestEvent extends EventBase {
    public StaleStatsResetRequestEvent() {
        super("STALE_STATS_RESET_REQUEST", "Stale stats reset request");
    }
}
