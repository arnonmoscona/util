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

import com.moscona.util.collections.Pair;
import com.moscona.util.functions.impure.Action0;
import lombok.experimental.Delegate;
import org.fest.reflect.core.Reflection;
import org.fest.reflect.reference.TypeRef;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Arnon Moscona on 10/27/2015.
 * Implementation of ExtendedCompletionStage
 */
class ExtendedCompletionStageImpl<T> implements ExtendedCompletionStage<T> {

    @Delegate(types = CompletionStage.class)
    private final CompletionStage<T> delegate;
    private AtomicBoolean wasCanceled = new AtomicBoolean(false);
    private AtomicBoolean wasCompleted = new AtomicBoolean(false);
    private AtomicBoolean wasCompletedExceptionally = new AtomicBoolean(false);
    private CountDownLatch completionSignal = new CountDownLatch(1);
    private CountDownLatch whenCompleteSignal = new CountDownLatch(1); // used to make sure that thw trigger is installed before checking for completion

    public ExtendedCompletionStageImpl(CompletionStage<T> stage) {
//        delegate = stage;

        // attempt to refactor
        //<editor-fold desc="attempt to refactor such that the normal (CF case) is always invoked">
        if (CompletableFuture.class.isAssignableFrom(stage.getClass())) {
            delegate = stage;
        } else {
            // wrap it with a regular CompletableFuture
            CompletableFuture<T> wrapper = new CompletableFuture<>();
            stage.whenComplete((t,ex) -> {
                System.err.println("wrapper whenComplete() invoked"); //fixme  DELETE ME
                if (ex != null) {
                    wrapper.completeExceptionally(ex);
                    System.err.println("wrapper completeExceptionally() invoked"); //fixme  DELETE ME
                }
                else {
                    wrapper.complete(t); // we don't care whether it's null or not
                    System.err.println("wrapper complete() invoked"); //fixme  DELETE ME
                }
                System.err.println("installed hook for wrapper"); //fixme  DELETE ME
            });
            delegate = wrapper;
        }
        //</editor-fold>

        delegate.whenComplete((result, ex) -> {
            System.err.println("calling whenComplete() on "+Thread.currentThread()); // FIXME DELETE ME
            wasCompleted.set(true);
            if (ex != null) {
                wasCompletedExceptionally.set(true);
            }
            completionSignal.countDown();
            System.err.println("released latch on "+Thread.currentThread()); // FIXME DELETE ME
        });
        whenCompleteSignal.countDown();
        delegate.thenRun(() -> {
            System.err.println("invoking thenRun() action on "+Thread.currentThread()); // FIXME DELETE ME
            completionSignal.countDown();
        });
        delegate.exceptionally(ex -> {
            System.err.println("invoking exceptionally() action on "+Thread.currentThread()); // FIXME DELETE ME
            completionSignal.countDown();
            return null;
        });
    }

    private void awaitCompletionIndefinitely() throws InterruptedException {
        completionSignal.await();
    }

    private void awaitWhenCompleteSignal() throws InterruptedException {
        whenCompleteSignal.await();
    }

    /**
     * <p>When the delegate is in fact a {@link Future} then this will call the {@link Future#cancel(boolean)}
     * method of the delegate. This means that if the delegate is in fact a {@link CompletableFuture}
     * then the behavior is exactly like {@link CompletableFuture#cancel(boolean)}. In any other case, the method
     * will try to invoke a compatible method via reflection. </p>
     * <p><b>The assumption here is that there is no safe way to impose a cancellation if the implementation of the
     * {@link CompletionStage} does not cooperate to do so.</b></p>
     * <p>From {@link Future}:</p>
     * <i>
     * Attempts to cancel execution of this task.  This attempt will
     * fail if the task has already completed, has already been cancelled,
     * or could not be cancelled for some other reason. If successful,
     * and this task has not started when {@code cancel} is called,
     * this task should never run.  If the task has already started,
     * then the {@code mayInterruptIfRunning} parameter determines
     * whether the thread executing this task should be interrupted in
     * an attempt to stop the task.
     * <p/>
     * <p>After this method returns, subsequent calls to {@link #isDone} will
     * always return {@code true}.  Subsequent calls to {@link #isCancelled}
     * will always return {@code true} if this method returned {@code true}.
     * </i>
     * @param mayInterruptIfRunning {@code true} if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        wasCanceled.set(true);

        if (delegateIsFuture()) {
            return ((Future)delegate).cancel(mayInterruptIfRunning);
        }

        return Reflection.method("cancel")
                .withReturnType(boolean.class)
                .withParameterTypes(boolean.class)
                .in(delegate)
                .invoke(mayInterruptIfRunning);
    }

    /**
     * Returns {@code true} if this task was cancelled before it completed
     * normally.
     *
     * @return {@code true} if this task was cancelled before it completed
     */
    @Override
    public boolean isCancelled() {
        return wasCanceled.get();
    }

    /**
     * <p>Returns {@code true} if this task completed.</p>
     *
     * <p>Completion may be due to normal termination, an exception, or
     * cancellation -- in all of these cases, this method will return
     * {@code true}.</p>
     *
     * @return {@code true} if this task completed
     */
    @Override
    public boolean isDone() {
        if (delegateIsFuture()) {
            return ((Future<?>)delegate).isDone();
        }

        return wasCompleted.get();
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public T get() throws InterruptedException, ExecutionException {
        awaitCompletionIndefinitely();

        if (delegateIsFuture()) {
            return (T)((Future)delegate).get();
        }

        if(wasCanceled.get()) {
            throw new CancellationException("was canceled");
        }

        // OK. We need a custom get(), but this one can be expressed in terms of the timeout get()
        try {
            return customGet(-1, TimeUnit.NANOSECONDS, false);
        } catch (TimeoutException e) {
            throw new ExecutionException("unexpected timeout exception", e);
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    @SuppressWarnings("unchecked")
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        assert unit != null: "time unit may not be null";

        if (delegateIsFuture()) {
            return (T)((Future)delegate).get(timeout, unit);
        }

        return customGet(timeout, unit, true);
    }

    private boolean delegateIsFuture() {
        return Future.class.isAssignableFrom(delegate.getClass());
    }

    private boolean delegateIsCompletableFuture() {
        return CompletableFuture.class.isAssignableFrom(delegate.getClass());
    }


    //<editor-fold desc="internal implementation of get">
    private T customGet(long timeout, TimeUnit unit, boolean useTimeout) throws InterruptedException, ExecutionException, TimeoutException {
        // Instead of implementing our own timeout functionality we can reuse the built-in functionality of the future produced by the submit() method of an executor

        Future<Pair<Optional<T>, Optional<Throwable>>> future = ForkJoinPool.commonPool().submit(this::simpleGet);
        // we only want to propagate a timeout from the future. All the rest, we want to get to the original
        // so that things don't get double-wrapped
        Pair<Optional<T>, Optional<Throwable>> result;
        if (useTimeout) {
            result = future.get(timeout, unit);
        }
        else {
            result = simpleGet();
        }

        if (result.getFirst().isPresent()) {
            return result.getFirst().get();
        }
        else {
            Throwable ex = result.getSecond().get();
            assert ex != null : "BUG: unexpected null when an exception was expected";

            switch (ex.getClass().getSimpleName()) {
                case "ExecutionException":
                    throw (ExecutionException) ex;
                case "InterruptedException":
                    throw (InterruptedException) ex;
                default:
                    throw new ExecutionException("Unexpected exception: "+ex, ex);
            }
        }
    }

    private Pair<Optional<T>, Optional<Throwable>> simpleGet() {
        AtomicReference<T> resultReference = new AtomicReference<>();
        AtomicReference<Throwable> exceptionReference = new AtomicReference<>();
        AtomicBoolean hadException = new AtomicBoolean(false);
        CountDownLatch latch = new CountDownLatch(1);

        delegate.whenComplete((t, ex) -> {
            if (ex == null) {
                // did not have an exception
                resultReference.set(t);
                hadException.set(false);
            }
            else {
                // had an exception
                exceptionReference.set(ex);
                hadException.set(true);
            }
            latch.countDown();
        });

        try {
            latch.await();

            if (hadException.get()) {
                throw new ExecutionException(exceptionReference.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            return new Pair<>(Optional.empty(), Optional.of(e));
        } catch (Throwable e) {
            return new Pair<>(Optional.empty(), Optional.of(new ExecutionException(e)));
        }

        return new Pair<>(Optional.of(resultReference.get()), Optional.empty());
    }
    //</editor-fold>

    /**
     * Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * To better conform with the use of common functional forms, if a computation involved in the completion
     * of this {@link ExtendedCompletionStage} threw an exception,
     * this method throws an (unchecked) {@link CompletionException}
     * with the underlying exception as its cause.
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException if this future completed exceptionally or a completion computation threw an exception
     */
    @Override
    public T join() throws CancellationException, CompletionException {
        try {
            return get();
        } catch (InterruptedException e) {
            throw new CompletionException(e);
        } catch (ExecutionException e) {
            throw new CompletionException(e.getCause());
        }
    }

    /**
     * Waits for the computation to complete.
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    @Override
    public void awaitCompletion(int timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new AssertionError("method not implemented");
    }

    /**
     * Returns the result value (or throws any encountered exception) if completed, else returns the given valueIfAbsent.
     * @param valueIfAbsent the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException if this future completed exceptionally or a completion computation threw an exception
     */
    @Override
    public T getNow(T valueIfAbsent) throws CancellationException, CompletionException {
        if (delegateIsCompletableFuture()) {
            return ((CompletableFuture<T>)delegate).getNow(valueIfAbsent);
        }

        try {
            awaitWhenCompleteSignal();
            completionSignal.await(5, TimeUnit.SECONDS); // FIXME this is dead wrong
            System.err.println("finished waiting on "+Thread.currentThread()); // FIXME DELETE ME
        } catch (InterruptedException e) {
            System.out.println("*** interrupted on "+Thread.currentThread()); // FIXME DELETE ME
            throw new CompletionException("interrupted", e);
        }
        System.err.println("calling isDone()"); // FIXME DELETE ME

        if (isDone() || wasCompletedExceptionally.get()) { // FIXME should be isCompletedExceptionally
            try {
                return get();
            } catch (InterruptedException e) {
                throw new CompletionException(e);
            } catch (ExecutionException e) {
                throw new CompletionException(e.getCause());
            }
        }

        if (isCancelled()) {
            throw new CancellationException("computation canceled");
        }

        return valueIfAbsent;
    }

    /**
     * Returns true if this ExtendedCompletionStage completed exceptionally, in any way.
     * Possible causes include cancellation, explicit invocation of completeExceptionally,
     * and abrupt termination of a CompletionStage action.
     * <em><p>Note: relies on the original object having a corresponding method.</p></em>
     * @return true if this ExtendedCompletionStage completed exceptionally
     * @throws NoSuchMethodException if the original {@link CompletionStage} does not implement a corresponding method
     */
    @Override
    public boolean isCompletedExceptionally() throws NoSuchMethodException {
        throw new AssertionError("method not implemented");
    }
}
