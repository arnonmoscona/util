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

import com.moscona.exceptions.InvalidStateException;
import com.moscona.util.SafeRunnable;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created: Apr 1, 2010 1:56:48 PM
 * By: Arnon Moscona
 * A very simple implementation of a stats service
 */
public class SimpleStatsService implements IStatsService {
    private boolean isOn;
    private ConcurrentHashMap<String, IStatValue> stats;
    private ConcurrentHashMap<String, StopWatch> timers;

    public SimpleStatsService() {
        isOn = true;
        stats = new ConcurrentHashMap<String, IStatValue>();
        timers = new ConcurrentHashMap<String, StopWatch>();
    }

    @Override
    public void turnOff() {
        isOn = false;
    }

    @Override
    public void turnOn() {
        isOn = true;
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    /**
     * Sets the value of a named stat. The old value is lost.
     *
     * @param name  the name of the statistic to set
     * @param value new value.
     */
    @Override
    public void setStat(String name, long value) {
        if (isOn) {
            IStatValue statValue = stats.get(name);
            if(statValue == null) {
                statValue = new StatValue(value);
                stats.put(name, statValue);
            } else {
                statValue.set(value);
            }
            LongSampleAccumulator desc = (LongSampleAccumulator)statValue.getDescriptiveStatistics();
            if (desc != null) {
                desc.addSample(value);
            }
        }
    }

    /**
     * Increments the value by a given amount. If does not exist, it is assumed the old value was 0.
     *
     * @param name  name the name of the statistic to set
     * @param value increment value.
     */
    @Override
    public void incStat(String name, long value) {
        if (isOn) {
            IStatValue statValue = null;
            if (stats.containsKey(name)) {
                statValue = stats.get(name);
                statValue.inc(value);
            } else {
                stats.put(name, new StatValue(value));
                statValue = stats.get(name);
            }

            LongSampleAccumulator desc = (LongSampleAccumulator)statValue.getDescriptiveStatistics();
            if (desc != null) {
                desc.addSample(value);
            }
        }
    }

    /**
     * == incStat(name,1)
     *
     * @param name name the name of the statistic to set
     */
    @Override
    public void incStat(String name) {
        if (isDouble(name)) {
            incStat(name,1.0);
        }
        else {
            incStat(name, 1);
        }
    }

    private boolean isDouble(String name) {
        return(stats.containsKey(name) && stats.get(name).getType() == Double.class);
    }

    /**
     * Initializes the statistic (and its type) and attaches descriptive statistics to it from the outset.
     * Wipes out whatever previous data existed for the statistic.
     * The initial value is included in the descriptive stats.
     *
     * @param name  the name of the statistic
     * @param value the initial value of it
     */
    @Override
    public void initStatWithDescriptiveStats(String name, long value) {
        if (isOn) {
            if (stats.containsKey(name)) {
                // wipe it out
                stats.remove(name);
            }
            stats.put(name, new StatValue(value));
            IStatValue statValue = stats.get(name);
            statValue.attachDescriptiveStats();
        }
    }

    /**
     * Initializes the statistic (and its type) and attaches descriptive statistics to it from the outset.
     * Wipes out whatever previous data existed for the statistic.
     * The initial value is included in the descriptive stats.
     *
     * @param name  the name of the statistic
     * @param value the initial value of it
     */
    @Override
    public void initStatWithDescriptiveStats(String name, double value) {
        if (isOn) {
            if (stats.containsKey(name)) {
                // wipe it out
                stats.remove(name);
            }
            stats.put(name, new StatValue(value));
            IStatValue statValue = stats.get(name);
            statValue.attachDescriptiveStats();
        }
    }

    /**
     * Sets the value of a named stat. The old value is lost.
     *
     * @param name  the name of the statistic to set
     * @param value new value.
     */
    @Override
    public void setStat(String name, double value) {
        if (isOn) {
            if (stats.containsKey(name)) {
                stats.get(name).set(value);
            } else {
                stats.put(name, new StatValue(value));
            }
        }
    }

    /**
     * Increments the value by a given amount. If does not exist, it is assumed the old value was 0.0f.
     *
     * @param name  name the name of the statistic to set
     * @param value increment value.
     */
    @Override
    public void incStat(String name, double value) {
        if (isOn) {
            IStatValue statValue = null;
            if (stats.containsKey(name)) {
                statValue = stats.get(name);
                statValue.inc(value);
            } else {
                stats.put(name, new StatValue(value));
                statValue = stats.get(name);
            }

            DoubleSampleAccumulator desc = (DoubleSampleAccumulator)statValue.getDescriptiveStatistics();
            if (desc != null) {
                desc.addSample(value);
            }
        }
    }

    /**
     * Demarcates the starting of measuring the names time measure. In effect until stopTimerFor(name) is called.
     * Resets the timer. If a measurement was in effect and stopTimerFor(name) was not called, then that measurement
     * is lost.
     *
     * @param name of the timing measure to use.
     */
    @Override
    public void startTimerFor(String name) {
        if (isOn) {
            StopWatch sw = new StopWatch();
            timers.put(name, sw);
            sw.start();
        }
    }

    /**
     * Used to exclude the time between this call and and a call to resumeTimerFor(name). In effect until stopTimerFor(name) or
     * resumeTimerFor(name) is called.
     *
     * @param name of the timing measure to use.
     */
    @Override
    public void pauseTimerFor(String name) {
        if (isOn) {
            StopWatch sw = timers.get(name);
            if (sw != null) {
                sw.suspend();
            } else {
                startTimerFor(name);
                timers.get(name).suspend();
            }
        }
    }

    /**
     * Cancels the effect of pauseTimerFor(name). The clock continues ticking until stopTimerFor(name) is called.
     *
     * @param name of the timing measure to use.
     */
    @Override
    public void resumeTimerFor(String name) {
        if (isOn) {
            StopWatch sw = timers.get(name);
            if (sw != null) {
                sw.resume();
            } else {
                startTimerFor(name);
            }
        }
    }

    /**
     * Stops the timer for the names timing measure and registers the result. Without this call you get no results.
     *
     * @param name of the timing measure to use.
     * @throws com.moscona.exceptions.InvalidStateException if timing is longer than integer capacity
     */
    @Override
    public void stopTimerFor(String name) throws InvalidStateException {
        if (isOn) {
            StopWatch stopWatch = timers.get(name);
            try {
                stopWatch.stop();
            }
            catch (Throwable e) {
                System.err.println("Error with timer " + name + ": "+e+"\n(ignoring)");
                e.printStackTrace(System.err);  //To change body of catch statement use File | Settings | File Templates.
            }
            long timing = stopWatch.getTime();
            addTimingSampleFor(name, timing);
            LongSampleAccumulator acc = (LongSampleAccumulator) stats.get(name).getDescriptiveStatistics();
            if (acc.getCount()==0) {
                acc.addSample(timing);
            }
        }
    }

    /**
     * Allows a convenient measuring of a whole block of code, at the expense of creating a new Runnable object.
     *
     * @param name name of the timing measure to use.
     * @param code the Runnable that whose run() method time will be measured.
     * @throws InvalidStateException if timing is longer than integer capacity
     */
    @Override
    public void measureTiming(String name, Runnable code) throws InvalidStateException {
        startTimerFor(name);
        code.run();
        stopTimerFor(name);
        if (code instanceof SafeRunnable) {
            Exception e = ((SafeRunnable) code).getException();
            if (e != null) {
                throw new InvalidStateException("Exception thrown in safe runnable", e);
            }
        }
    }

    /**
     * Allows adding a timing sample without all the hustle of starting and stopping.
     * Note that timing samples are cumulative.
     * Useful if you already have the timing handy.
     *
     * @param name   name of the timing measure to use.
     * @param millis the number of milliseconds to add
     * @throws InvalidStateException if timing is longer than integer capacity
     */
    @Override
    public void addTimingSampleFor(String name, long millis) throws InvalidStateException {
        if (isOn) {
            if (millis > Integer.MAX_VALUE) {
                throw new InvalidStateException("Timer was left to run too long and is now more than integer capacity");
            }
            IStatValue iStatValue = stats.get(name);
            if(iStatValue == null) {
                IStatValue statValue = new StatValue(millis, true);
                stats.put(name, statValue);
                statValue.attachDescriptiveStats();
                statValue.getDescriptiveStatistics().clear();
            } else {
                iStatValue.setAndAccumulate(millis);
            }
        }
    }

    /**
     * Retrieves the value of a statistic
     *
     * @param name name of the stat to use.
     * @return an IStatValue for the stat or null, if it does not exist
     */
    @Override
    public IStatValue getStat(String name) {
        return stats.get(name);
    }

    @Override
    public Set<String> getStatNames() {
        return stats.keySet();
    }

    @SuppressWarnings({"UseOfSystemOutOrSystemErr"})
    public void print() {
        System.out.println("\nStats:");
        final Set<String> nameSet = getStatNames();
        ArrayList<String> names = new ArrayList<String>(nameSet);
        Collections.sort(names);
        for (String stat : names) {
            System.out.println("\t"+stat+": "+getStat(stat).toFormattedString());
        }
    }
}

