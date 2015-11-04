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

package com.moscona.util.async.furtures;

import java.util.concurrent.*;

/**
 * Created by Arnon Moscona on 10/27/2015.
 * Implementation of ExtendedCompletionStage
 */
class ExtendedCompletionStageImpl<T> extends ExtendedCompletionStageBase<T> implements ExtendedCompletionStage<T> {

    public ExtendedCompletionStageImpl(CompletionStage<T> stage) {
        super(stage);
    }

    /**
     * Waits for the computation to complete.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @throws InterruptedException if the current thread was interrupted
     *                              while waiting
     * @throws TimeoutException     if the wait timed out
     */
    @Override
    public void awaitCompletion(int timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        try {
            delegate.get(timeout, unit);
        } catch (ExecutionException | CancellationException e) {
            // do nothing
        }
    }

    /**
     * Waits if necessary for at most the given time for this future
     * to complete, and then returns its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws CancellationException if this future was cancelled
     * @throws ExecutionException    if this future completed exceptionally
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        assert unit!=null : "the time unit may not be null";
        return super.get(timeout, unit);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
