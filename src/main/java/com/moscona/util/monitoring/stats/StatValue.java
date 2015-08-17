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

package com.moscona.util.monitoring.stats;

import com.moscona.util.ISimpleDescriptiveStatistic;

import java.text.DecimalFormat;

/**
 * Created: Apr 1, 2010 3:53:29 PM
 * By: Arnon Moscona
 *
 * A very simple class to track stats in a stats service. used mostly for operational purposes, not for heavy
 * statistics.
 *
 * Unfortunately I didn't see how to implement this cleanly with generics and so I am doing the ugly thing for now.
 * Note that these are used for informational stats only and do not require super accuracy anyway - so I don't
 */
public class StatValue implements IStatValue {
    private boolean isDouble;
    private long longValue;
    private double doubleValue;
    private boolean isTiming;
    private LongSampleAccumulator longStats;
    private DoubleSampleAccumulator doubleStats;

    /**
     * The type of the value is determined at creation and does not change
     * @param value the initial value
     */
    public StatValue(long value) {
        init(value,0,false,false);
    }

    /**
     * The type of the value is determined at creation and does not change
     * @param value the initial value
     * @param isTiming whther this is a timing stat or not
     */
    public StatValue(long value, boolean isTiming) {
        init(value,0,false,isTiming);
    }

    /**
     * The type of the value is determined at creation and does not change
     * @param value the initial value
     */
    public StatValue(double value)  {
        init(0,value,true,false);
    }

    private void init(long longValue, double doubleValue, boolean isDouble, boolean isTiming) {
        this.longValue = longValue;
        this.doubleValue = doubleValue;
        this.isDouble = isDouble;
        this.isTiming = isTiming;
        this.longStats = null;
        this.doubleStats = null;
    }

    /**
     * What is the type of the value?
     *
     * @return one of: Long, Double
     */
    @Override
    public Class getType() {
        return isDouble ? Double.class : Long.class;
    }

    /**
     * Get the value as an Long
     *
     * @return the value as an Long. If the underlying value is converted to double either by truncating or rounding.
     */
    @Override
    public long getLong() {
        return isDouble ? Math.round(doubleValue) : longValue;
    }

    /**
     * Get the value as a double
     *
     * @return the value, converted to double
     */
    @Override
    public double getDouble() {
        return isDouble ? doubleValue : (double)longValue;
    }

    @Override
    public void set(long value) {
        if (isDouble) {
            doubleValue = value;
        }
        else {
            longValue = value;
        }
    }

    @Override
    public void set(double value) {
        if (isDouble) {
            doubleValue = value;
        }
        else {
            longValue = Math.round(value);
        }
    }

    @Override
    public void inc(long value) {
        if (isDouble) {
            doubleValue += value;
        }
        else {
            longValue += value;
        }
    }

    @Override
    public void inc(double value) {
        if (isDouble) {
            doubleValue += value;
        }
        else {
            longValue = Math.round(value);
        }
    }

    /**
     * The same as set(value), but this version also accumulates descriptive stats, but only if they are attached already.
     *
     * @param value the new value for the stat
     */
    @Override
    public void setAndAccumulate(long value) {
        set(value);
        addCurrentValueToStats();
    }

    /**
     * The same as set(value), but this version also accumulates descriptive stats, but only if they are attached already.
     *
     * @param value the new value for the stat
     */
    @Override
    public void setAndAccumulate(double value) {
        set(value);
        addCurrentValueToStats();
    }

    @Override
    public boolean isTiming() {
        return isTiming;
    }

    public Number asNumber() {
        return isDouble ? new Double(doubleValue) : new Long(longValue);
    }

    public String toString() {
        return getClass().getName()+"("+toBareString()+")";
    }

    /**
     * Like toString() but without all the class stuff.
     * @return a string representing the value
     */
    @Override
    public String toBareString() {
        return ""+(isDouble ? Double.toString(doubleValue) : Long.toString(longValue))+(isTiming ? " msec":"");
    }

    @Override
    public String toFormattedString() {
        String retval;
        String stats = null;

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.###");
        if (isDouble) {
            retval = decimalFormat.format(doubleValue);
            if (doubleStats != null && doubleStats.getCount()>0) {
                stats = doubleStats.toString(decimalFormat);
            }
        }
        else {
            retval = new DecimalFormat("#,##0").format(longValue);
            if (longStats != null && longStats.getCount()>0) {
                stats = longStats.toString(decimalFormat);
            }
        }

        if (isTiming) {
            retval += " msec";
        }

        if (stats != null) {
            retval += "  ("+stats+")";
        }

        return retval;
    }

    /**
     * Returns the descriptive stats if they exist. Otherwise returns null.
     *
     * @return descriptive stats, if they exist for the value.
     */
    @Override
    public ISimpleDescriptiveStatistic getDescriptiveStatistics() {
        if (isDouble) {
            return doubleStats;
        }
        else {
            return longStats;
        }
    }

    /**
     * Attaches descriptive statistics to the value if the implementation supports it.
     * If unsupported, then the descriptive stats should remain null.
     * If it is supported and descriptive stats already exist they will be reset.
     */
    @Override
    public void attachDescriptiveStats() {
        if (isDouble) {
            doubleStats = new DoubleSampleAccumulator();
        }
        else {
            longStats = new LongSampleAccumulator();
        }
        addCurrentValueToStats();
    }

    /**
     * Adds the current value to the stats (if attached)
     */
    private void addCurrentValueToStats() {
        if (isDouble && doubleStats != null) {
            doubleStats.addSample(doubleValue);
        }
        if (!isDouble && longStats != null) {
            longStats.addSample(longValue);
        }
    }
}
