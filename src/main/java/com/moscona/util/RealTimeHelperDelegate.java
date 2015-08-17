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

