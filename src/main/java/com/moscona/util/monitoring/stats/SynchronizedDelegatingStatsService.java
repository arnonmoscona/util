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

import java.util.Set;

/**
 * Created: 8/15/11 12:51 PM
 * By: Arnon Moscona
 */
public class SynchronizedDelegatingStatsService implements IStatsService {
    private IStatsService delegate;

    public SynchronizedDelegatingStatsService(IStatsService delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized void addTimingSampleFor(String name, long millis) throws InvalidStateException {
        delegate.addTimingSampleFor(name, millis);
    }

    @Override
    public IStatValue getStat(String name) {
        return delegate.getStat(name);
    }

    @Override
    public Set<String> getStatNames() {
        return delegate.getStatNames();
    }

    @Override
    public void incStat(String name) {
        delegate.incStat(name);
    }

    @Override
    public void incStat(String name, double value) {
        delegate.incStat(name, value);
    }

    @Override
    public void incStat(String name, long value) {
        delegate.incStat(name, value);
    }

    @Override
    public void initStatWithDescriptiveStats(String name, double value) {
        delegate.initStatWithDescriptiveStats(name, value);
    }

    @Override
    public void initStatWithDescriptiveStats(String name, long value) {
        delegate.initStatWithDescriptiveStats(name, value);
    }

    @Override
    public boolean isOn() {
        return delegate.isOn();
    }

    @Override
    public void measureTiming(String name, Runnable code) throws InvalidStateException {
        delegate.measureTiming(name, code);
    }

    @Override
    public void pauseTimerFor(String name) {
        delegate.pauseTimerFor(name);
    }

    @Override
    public void resumeTimerFor(String name) {
        delegate.resumeTimerFor(name);
    }

    @Override
    public void setStat(String name, double value) {
        delegate.setStat(name, value);
    }

    @Override
    public void setStat(String name, long value) {
        delegate.setStat(name, value);
    }

    @Override
    public void startTimerFor(String name) {
        delegate.startTimerFor(name);
    }

    @Override
    public void stopTimerFor(String name) throws InvalidStateException {
        delegate.stopTimerFor(name);
    }

    @Override
    public void turnOff() {
        delegate.turnOff();
    }

    @Override
    public void turnOn() {
        delegate.turnOn();
    }
}
