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

import com.moscona.util.functions.impure.Action0;
import com.moscona.util.functions.impure.Function0;

import java.util.concurrent.*;

/**
 * A utility class for helping with the construction of futures
 * Created by Arnon Moscona on 8/22/2015.
 */
public interface Futures {

    /**
     * Takes an (impure) function and runs it asynchronously, resulting in a new CompletableFuture
     * @param function the function to call asynchronously. Asynchronous execution is done using the ForkJoinPool.
     * @param <T> the return value type of the function and the completion type of the resulting future
     * @return a new CompletableFuture that completes normally if the function runs without exceptions
     * and completes exceptionally if the functions throws anything.
     * The returned completion stage uses the default thread pool of {@link java.util.concurrent.CompletableFuture}: {@link ForkJoinPool#commonPool()}
     */
    static <T>CompletionStage<T> newEagerAsyncCompletableFuture(Function0<T> function) {
        return newEagerAsyncCompletableFuture(ForkJoinPool.commonPool(), function);
    }

    /**
     * Takes an (impure) function and runs it asynchronously, resulting in a new CompletableFuture
     * @param executor an executor that is used to run the function asynchronously
     * @param function the function to call asynchronously
     * @param <T> the return value type of the function and the completion type of the resulting future
     * @return a new CompletableFuture that completes normally if the function runs without exceptions
     * and completes exceptionally if the function throws anything
     */
    static <T>CompletionStage<T> newEagerAsyncCompletableFuture(Executor executor, Function0<T> function) {
        assert executor != null : "executor may not be null";
        assert function != null : "function may not be null";

        CompletableFuture<T> future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                T result = function.call();
                future.complete(result);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }


    /**
     * Takes an (impure) action and runs it asynchronously, resulting in a new CompletableFuture
     * @param action the action to call asynchronously. Asynchronous execution is done using the ForkJoinPool.
     * @return a new CompletableFuture that completes normally if the action runs without exceptions
     * and completes exceptionally if the action throws anything.
     * The returned completion stage uses the default thread pool of {@link java.util.concurrent.CompletableFuture}: {@link ForkJoinPool#commonPool()}
     */
    @SuppressWarnings("unchecked")
    static CompletionStage<?> newEagerAsyncCompletableFuture(Action0 action) {
        return newEagerAsyncCompletableFuture(ForkJoinPool.commonPool(), action);
    }

    /**
     * Takes a vararg (impure) action and runs it asynchronously, resulting in a new CompletableFuture
     * @param executor an executor that is used to run the action asynchronously
     * @param action the action to call asynchronously
     * @return a new CompletableFuture that completes normally if the action runs without exceptions
     * and completes exceptionally if the action throws anything
     */
    @SuppressWarnings("unchecked")
    static CompletionStage<?> newEagerAsyncCompletableFuture(Executor executor, Action0 action) {
        assert executor != null : "executor may not be null";
        assert action != null : "action may not be null";

        CompletableFuture future = new CompletableFuture<>();
        executor.execute(() -> {
            try {
                action.call();
                future.complete(null);
            }
            catch (Throwable t) {
                future.completeExceptionally(t);
            }
        });
        return future;
    }
}
