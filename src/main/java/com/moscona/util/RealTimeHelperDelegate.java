package com.moscona.util;

import java.util.Calendar;

/**
 * Created: Apr 5, 2010 5:30:09 PM
 * By: Arnon Moscona
 * A time helper delegate that is used in normal mode
 */
public class RealTimeHelperDelegate implements ITimeHelperDelegate {
    @Override
    public int now(long lastMidnightInMillis) {
        return (int)(System.currentTimeMillis() - lastMidnightInMillis);
    }

    /**
     * today in the proper time zone
     *
     * @return whatever the class considers today is. Should be set to midnight.
     */
    @Override
    public Calendar today() {
        Calendar cal = Calendar.getInstance(TimeHelper.INTERNAL_TIMEZONE);
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,0,0);
        return cal;
    }

}

