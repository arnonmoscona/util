package com.moscona.util;

import java.util.Calendar;

/**
 * Created: Apr 5, 2010 5:33:47 PM
 * By: Arnon Moscona
 * The interface for TimeHelper delegates. Used for time simulation.
 */
public interface ITimeHelperDelegate {
    /**
     * now in terms of internal timestamp
     * @param lastMidnightInMillis the value for last midnight
     * @return an internal timestamp defining whatever the class thinks now is
     */
    public int now(long lastMidnightInMillis);

    /**
     * today in the proper time zone
     * @return whatever the class considers today is. Should be set to midnight.
     */
    public Calendar today();
}
