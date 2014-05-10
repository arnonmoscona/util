package com.moscona.util.async;

import com.moscona.exceptions.InvalidStateException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created: Jun 4, 2010 9:04:28 AM By: Arnon Moscona Support for making synchronous calls over an asynchronous medium.
 * Instances of this class represent individual calls to the function, whose body is defined by asyncCall() and whose
 * arguments are the constructor arguments. The support structures for coordination of multiple calls is implemented via
 * singleton objects that collect all state such as pending calls that have not been completed, futures, etc. Typically
 * you will create a factory method that will create an instance and associate it immediately with a results store. This
 * avoids singletons and circumvents the fact that the generic type cannot be referenced in static members. In general
 * statics and generics do not mix well in Java.
 */
public abstract class AsyncFunctionCall<V> {
    private String argumentsSignature = null;
    private FutureValueWithTimeStamps<V> future = null;
    private AsyncFunctionFutureResults<V> resultsStore = null;

    /**
     * Associates a results store with this call. Useful for factory methods to make method calls.
     *
     * @param resultsStore
     * @return this
     */
    public AsyncFunctionCall<V> use(AsyncFunctionFutureResults<V> resultsStore) {
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.resultsStore = resultsStore;
        return this;
    }

    /**
     * This method is used by the asynchronous call to set the return value and make sure that all the callers are
     * notified that the operation is complete.
     *
     * @param result
     */
    public void returnToCallers(V result) {
        resultsStore.returnToCallers(computeArgumentsSignature(), result);
    }

    /**
     * For testability
     */
    public void awaitReturn() throws ExecutionException, InterruptedException {
        if (future.isDone()) {
            return;
        } else {
            future.get();
        }
    }

    /**
     * performs a blocking synchronous call by invoking the async call and waiting for its completion Semantics are like
     * a java.util.concurrent.Future.get() except that any exception can be thrown.
     *
     * @return computed result. Note that this is obtained via a shared Future, and therefore should be considered
     * immutable. It is not safe to call mutating methods on the return value as multiple concurrent calls with the same
     * signature will share the same return value.
     * @throws Exception if unable to compute a result
     */
    public V call(AsyncFunctionFutureResults<V> results) throws Exception {
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        resultsStore = results;
        performAsyncCall();
        return future.get();
    }

    public V call() throws Exception {
        return call(resultsStore);
    }

    /**
     * Performs the async call, but first makes sure that there is a future available in the pending calls list
     *
     * @throws Exception
     */
    private void performAsyncCall() throws Exception {
        if (resultsStore == null) {
            throw new InvalidStateException("Attempt to call asynchronous function without a results store");
        }
        ConcurrentHashMap<String, FutureValue<V>> pending = resultsStore;
        StringBuilder name = new StringBuilder();
        String signature = getArgumentsSignature();
        name.append(getClass().getName()).append("(").append(signature).append(")");
        FutureValueWithTimeStamps<V> newFuture = new FutureValueWithTimeStamps<V>(name.toString(), this);
        future = (FutureValueWithTimeStamps<V>) (pending.putIfAbsent(signature, newFuture));
        if (future == null) {
            future = newFuture;
        }

        future.markRequestTimeStamp();
        asyncCall(); // should return immediately and later call returnToCallers()
    }


    /**
     * Performs a blocking synchronous call by invoking the async call and waiting for its completion with a timeout.
     * Semantics are like a java.util.concurrent.Future.get(timeout,unit)
     *
     * @param timeout
     * @param unit
     * @return computed result. Note that this is obtained via a shared Future, and therefore should be considered
     * immutable. It is not safe to call mutating methods on the return value as multiple concurrent calls with the same
     * signature will share the same return value.
     * @throws InterruptedException
     * @throws java.util.concurrent.ExecutionException
     * @throws java.util.concurrent.TimeoutException
     */
    public V call(AsyncFunctionFutureResults<V> results, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            //noinspection AssignmentToCollectionOrArrayFieldFromParameter
            resultsStore = results;
            performAsyncCall();
        } catch (Exception e) {
            throw new ExecutionException("Exception while performing the asynchronous call: " + e, e);
        }
        return future.get(timeout, unit);
    }


    public V call(long timeout, TimeUnit unit) throws Exception {
        if (resultsStore == null) {
            throw new InvalidStateException("Attempt to call asynchronous function without a results store");
        }
        return call(resultsStore, timeout, unit);
    }

    private String getArgumentsSignature() {
        if (argumentsSignature == null) {
            argumentsSignature = computeArgumentsSignature();
        }
        return argumentsSignature;
    }

    public boolean isCanceled() {
        return future.isCancelled();
    }

    // ABSTRACT METHODS ===============================================================================================

    /**
     * Performs the asynchronous call (the "body" of the function). If you need argument (as you probably do) then they
     * should be in the constructor.
     *
     * @throws Exception
     */
    protected abstract void asyncCall() throws Exception;

    /**
     * Computes the argument signature to use for call equivalence. If multiple calls are made withe the same argument
     * signature, they are coalesced into one. If there is already a pending call with the same signature then no
     * additional calls to asyncCall will be made, rather the call will simply share the future result with the other
     * calls.
     *
     * @return the arguments signature to identify equivalent calls
     */
    protected abstract String computeArgumentsSignature();

    // SUPPORTING CLASSES  ============================================================================================


}

