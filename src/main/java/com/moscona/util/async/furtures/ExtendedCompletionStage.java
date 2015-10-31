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
 * Extends A {@link java.util.concurrent.CompletionStage}
 * to include all methods of {@link java.util.concurrent.CompletableFuture},
 * plus a few additional ones. Should work with any object that is actually a {@link java.util.concurrent.CompletionStage}
 * regardless of whether or not it is also a {@link java.util.concurrent.CompletableFuture}
 */
public interface ExtendedCompletionStage<T> extends Future<T>, CompletionStage<T> {
    /**
     * Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * To better conform with the use of common functional forms, if a computation involved in the completion
     * of this {@code ExtendedCompletionStage} threw an exception,
     * this method throws an (unchecked) {@link CompletionException}
     * with the underlying exception as its cause.
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException if this future completed exceptionally or a completion computation threw an exception
     */
    Object join() throws CancellationException, CompletionException;

//    /**
//     * Waits for the computation to complete.
//     * @param timeout the maximum time to wait
//     * @param unit    the time unit of the timeout argument
//     * @throws CancellationException if the computation was cancelled
//     * @throws ExecutionException    if the computation threw an
//     *                               exception
//     * @throws InterruptedException  if the current thread was interrupted
//     *                               while waiting
//     * @throws TimeoutException      if the wait timed out
//     */
//    void awaitCompletion(int timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * Returns the result value (or throws any encountered exception) if completed, else returns the given valueIfAbsent.
     * @param valueIfAbsent the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException if this future completed exceptionally or a completion computation threw an exception
     */
    T getNow(T valueIfAbsent) throws CancellationException, CompletionException;

    /**
     * Returns true if this ExtendedCompletionStage completed exceptionally, in any way.
     * Possible causes include cancellation, explicit invocation of completeExceptionally,
     * and abrupt termination of a CompletionStage action.
     * <em><p>Note: relies on the original object having a corresponding method.</p></em>
     * @return true if this ExtendedCompletionStage completed exceptionally
     * @throws NoSuchMethodException if the original {@link CompletionStage} does not implement a corresponding method
     */
    boolean isCompletedExceptionally() throws NoSuchMethodException;
}
