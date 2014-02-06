package com.moscona.util;

import com.moscona.exceptions.InvalidStateException;

import java.util.Set;

/**
 * Created: Mar 30, 2010 4:08:05 PM
 * By: Arnon Moscona
 * A service that helps with collecting and reporting on statistics, counters, and timing measurements
 */
public interface IStatsService {
    /**
     * Stops the service, preventing further stats collection (data is not lost).
     * The main reason to turn it off is performance. But this also allows temporary shut-offs when you want to do
     * untracked activity.
     */
    public void turnOff();

    /**
     * Turns the service back on if it is off, allowing it to collect more data
     */
    public void turnOn();

    /**
     * Tells whether the service is on of off
     *
     * @return true if it's on
     */
    public boolean isOn();

    /**
     * Sets the value of a named stat. The old value is lost.
     *
     * @param name  the name of the statistic to set
     * @param value new value.
     */
    public void setStat(String name, long value);

    /**
     * Increments the value by a given amount. If does not exist, it is assumed the old value was 0.
     *
     * @param name  name the name of the statistic to set
     * @param value increment value.
     */
    public void incStat(String name, long value);

    /**
     * == incStat(name,1)
     *
     * @param name name the name of the statistic to set
     */
    public void incStat(String name);

    /**
     * Initializes the statistic (and its type) and attaches descriptive statistics to it from the outset.
     * Wipes out whatever previous data existed for the statistic.
     * The initial value is included in the descriptive stats.
     *
     * @param name  the name of the statistic
     * @param value the initial value of it
     */
    public void initStatWithDescriptiveStats(String name, long value);

    /**
     * Initializes the statistic (and its type) and attaches descriptive statistics to it from the outset.
     * Wipes out whatever previous data existed for the statistic.
     * The initial value is included in the descriptive stats.
     *
     * @param name  the name of the statistic
     * @param value the initial value of it
     */
    public void initStatWithDescriptiveStats(String name, double value);

    /**
     * Sets the value of a named stat. The old value is lost.
     *
     * @param name  the name of the statistic to set
     * @param value new value.
     */
    public void setStat(String name, double value);

    /**
     * Increments the value by a given amount. If does not exist, it is assumed the old value was 0.0.
     *
     * @param name  name the name of the statistic to set
     * @param value increment value.
     */
    public void incStat(String name, double value);

    /**
     * Demarcates the starting of measuring the names time measure. In effect until stopTimerFor(name) is called.
     * Resets the timer. If a measurement was in effect and stopTimerFor(name) was not called, then that measurement
     * is lost.
     *
     * @param name of the timing measure to use.
     */
    public void startTimerFor(String name);

    /**
     * Used to exclude the time between this call and and a call to resumeTimerFor(name). In effect until stopTimerFor(name) or
     * resumeTimerFor(name) is called.
     *
     * @param name of the timing measure to use.
     */
    public void pauseTimerFor(String name);

    /**
     * Cancels the effect of pauseTimerFor(name). The clock continues ticking until stopTimerFor(name) is called.
     *
     * @param name of the timing measure to use.
     */
    public void resumeTimerFor(String name);

    /**
     * Stops the timer for the names timing measure and registers the result. Without this call you get no results.
     *
     * @param name of the timing measure to use.
     * @throws com.moscona.exceptions.InvalidStateException if timing is longer than integer capacity
     */
    public void stopTimerFor(String name) throws InvalidStateException;

    /**
     * Allows a convenient measuring of a whole block of code, at the expense of creating a new Runnable object.
     *
     * @param name name of the timing measure to use.
     * @param code the Runnable that whose run() method time will be measured.
     * @throws InvalidStateException if timing is longer than integer capacity
     */
    public void measureTiming(String name, Runnable code) throws InvalidStateException;

    /**
     * Allows adding a timing sample without all the hustle of starting ans stopping.
     * Useful if you already have the timing handy.
     * Note: This may add descriptive statistics to the value, if they are supported
     * in the implementation.
     *
     * @param name   name of the timing measure to use.
     * @param millis the number of milliseconds to add
     * @throws InvalidStateException if timing is longer than integer capacity
     */
    public void addTimingSampleFor(String name, long millis) throws InvalidStateException;

    /**
     * Retrieves the value of a statistic
     *
     * @param name name of the stat to use.
     * @return an IStatValue for the stat or null, if it does not exist
     */
    public IStatValue getStat(String name);

    Set<String> getStatNames();
}
