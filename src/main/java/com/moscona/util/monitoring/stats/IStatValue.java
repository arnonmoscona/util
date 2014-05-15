package com.moscona.util.monitoring.stats;

import com.moscona.util.ISimpleDescriptiveStatistic;

/**
 * Created: Apr 1, 2010 3:43:03 PM
 * By: Arnon Moscona
 * The values returned from a IStatsService
 */
public interface IStatValue {
    /**
     * What is the type of the value?
     * @return one of: Long, Double
     */
    public Class getType();

    /**
     * Get the value as an long
     * @return the value as an long. If the underlying value is converted to double either by truncating or rounding
     */
    public long getLong();

    /**
     * Get the value as a double
     * @return the value, converted to double
     */
    public double getDouble();

    /**
     * Sets the stat to a new value.
     * If descriptive statistics are attached then THIS HAS NO AFFECT ON THEM.
     * @param value the value to set the stat to.
     */
    void set(long value);

    /**
     * Sets the stat to a new value.
     * If descriptive statistics are attached then THIS HAS NO AFFECT ON THEM.
     * @param value the value to set the stat to.
     */
    void set(double value);


    /**
     * Increments the stat by a value.
     * If descriptive statistics are attached then THIS HAS NO AFFECT ON THEM.
     * @param value the value to increment the stat by.
     */
    void inc(long value);

    /**
     * Increments the stat by a value.
     * If descriptive statistics are attached then THIS HAS NO AFFECT ON THEM.
     * @param value the value to increment the stat by.
     */
    void inc(double value);

    /**
     * The same as set(value), but this version also accumulates descriptive stats, but only if they are attached already.
     * @param value the new value for the stat
     */
    void setAndAccumulate(long value);

    /**
     * The same as set(value), but this version also accumulates descriptive stats, but only if they are attached already.
     * @param value the new value for the stat
     */
    void setAndAccumulate(double value);

    boolean isTiming();

    /**
     * @return a string representing the value
     */
    public String toBareString();

    /**
     * @return a formatted string for human consumption.
     */
    public String toFormattedString();

    /**
     * Returns the descriptive stats if they exist. Otherwise returns null.
     * @return descriptive stats, if they exist for the value.
     */
    public ISimpleDescriptiveStatistic getDescriptiveStatistics();

    /**
     * Attaches descriptive statistics to the value if the implementation supports it.
     * If unsupported, then the descriptive stats should remain null.
     * If it is supported and descriptive stats already exist they will be reset. 
     */
    void attachDescriptiveStats();

}
