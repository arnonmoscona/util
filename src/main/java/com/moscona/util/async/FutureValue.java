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

package com.moscona.util.async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created: Jun 4, 2010 12:57:19 PM By: Arnon Moscona
 */
public class FutureValue<V> implements Future<V> {
    // FIXME IT-185 can this be replaced by java.util.concurrent.FutureTask?
    private V value = null;
    private AtomicBoolean isCanceled;
    private AtomicBoolean isDone;
    private final ReentrantLock lock;
    private final Condition doneCondition;
    private String name;
    private Throwable exception;
    private AsyncFunctionCall<V> creator;

    protected FutureValue(String name, AsyncFunctionCall<V> creator) {
        this.creator = creator;
        value = null;
        exception = null;
        isCanceled = new AtomicBoolean(false);
        isDone = new AtomicBoolean(false);
        lock = new ReentrantLock();
        doneCondition = lock.newCondition();
        this.name = name;
    }

    /**
     * Attempts to cancel execution of this task.  This attempt will fail if the task has already completed, has already
     * been cancelled, or could not be cancelled for some other reason. If successful, and this task has not started
     * when <tt>cancel</tt> is called, this task should never run.  If the task has already started, then the
     * <tt>mayInterruptIfRunning</tt> parameter determines whether the thread executing this task should be interrupted
     * in an attempt to stop the task.
     *
     * <p>After this method returns, subsequent calls to {@link #isDone} will always return <tt>true</tt>.  Subsequent
     * calls to {@link #isCancelled} will always return <tt>true</tt> if this method returned <tt>true</tt>.
     *
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this task should be interrupted; otherwise,
     *                              in-progress tasks are allowed to complete
     * @return <tt>false</tt> if the task could not be cancelled, typically because it has already completed normally;
     * <tt>true</tt> otherwise
     */
    @Override
    public synchronized boolean cancel(boolean mayInterruptIfRunning) {
        isCanceled.set(true);
        signalDone();
        return true;
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed normally.
     *
     * @return <tt>true</tt> if this task was cancelled before it completed
     */
    @Override
    public boolean isCancelled() {
        return isCanceled.get();
    }

    /**
     * Returns <tt>true</tt> if this task completed.
     *
     * Completion may be due to normal termination, an exception, or cancellation -- in all of these cases, this method
     * will return <tt>true</tt>.
     *
     * @return <tt>true</tt> if this task completed
     */
    @Override
    public boolean isDone() {
        return isDone.get();
    }

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     * @throws java.util.concurrent.CancellationException if the computation was cancelled
     * @throws java.util.concurrent.ExecutionException    if the computation threw an exception
     * @throws InterruptedException                       if the current thread was interrupted while waiting
     */
    @Override
    @SuppressWarnings({"MethodWithMultipleReturnPoints"})
    public V get() throws InterruptedException, ExecutionException {
        if (isDone()) {
            return getValueOrThrowException();
        }

        lock.lock();
        try {
            while (!isDone()) {
                if (doneCondition.await(100, TimeUnit.MILLISECONDS) || isDone()) {
                    return getValueOrThrowException();
                }
            }
            return getValueOrThrowException();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws java.util.concurrent.CancellationException if the computation was cancelled
     * @throws java.util.concurrent.ExecutionException    if the computation threw an exception
     * @throws InterruptedException                       if the current thread was interrupted while waiting
     * @throws java.util.concurrent.TimeoutException      if the wait timed out
     */
    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (isDone()) {
            return getValueOrThrowException();
        }

        lock.lock();
        try {
            if (doneCondition.await(timeout, unit)) {
                return getValueOrThrowException();
            } else {
                throw new TimeoutException("Timeout while waiting for return value in " + name);
            }
        } finally {
            lock.unlock();
        }
    }

    private V getValueOrThrowException() throws ExecutionException {
        if (exception != null) {
            throw new ExecutionException("Exception while executing asynchronous function " + name + " :" + exception, exception);
        }
        if (isCancelled()) {
            return null;
        }
        return value;
    }

    /**
     * Sets the value of the instance and then signals waiters that the value is ready
     *
     * @param result the Future result
     */
    public void set(V result) {
        isDone.set(true);
        synchronized (this) {
            value = result;
        }
        signalDone();
    }

    private void signalDone() {
        lock.lock();
        try {
            isDone.set(true);
            doneCondition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Causes this future to report an ExecutionException with the given throwable as its cause, unless this Future has
     * already been set or has been cancelled.
     *
     * @param ex the exception that the caller should get
     */
    public void setException(Throwable ex) {
        if (!isDone.get()) {
            exception = ex;
        }
        signalDone();
    }

    @Override
    public String toString() {
        return getClass().getName() + " for " + name;
    }

    public AsyncFunctionCall getCreator() {
        return creator;
    }
}