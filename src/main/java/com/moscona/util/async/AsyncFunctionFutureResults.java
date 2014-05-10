package com.moscona.util.async;

import com.moscona.exceptions.InvalidStateException;
import com.moscona.util.monitoring.stats.IStatsService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created: Jun 4, 2010 12:53:19 PM By: Arnon Moscona A results store for AsyncFunctionCall. We cannot use a type safe
 * singleton results because you cannot reference the generic type in static fields. So the use patter will be a factory
 * method.
 */
public class AsyncFunctionFutureResults<V> extends ConcurrentHashMap<String, FutureValue<V>> {
    private static final long serialVersionUID = 7610198264459502507L;

    public AsyncFunctionFutureResults() {
        super();
    }

    /**
     * Notifies all callers to retrieve the results and removes the signature and future from the list
     *
     * @param signature
     * @param result
     */
    public void returnToCallers(String signature, V result) {
        returnToCallers(signature, result, null, null);
    }

    public void returnToCallers(String signature, V result, IStatsService stats, String prefix) {
        FutureValue<V> future = getAndRemoveFutureValue(signature);
        if (future != null) {
            future.set(result);
            markCompleteTimeStamp(future, stats, prefix);
        }
        // HOLD if there was a retriable exception - should we remove it here?
    }

    private void markCompleteTimeStamp(FutureValue<V> future, IStatsService stats, String prefix) {
        if (future != null && FutureValueWithTimeStamps.class.isAssignableFrom(future.getClass())) {
            FutureValueWithTimeStamps valueWithTimeStamps = (FutureValueWithTimeStamps) future;
            valueWithTimeStamps.markCompleteTimeStamp(stats, prefix);
        }
    }

    private FutureValue<V> getAndRemoveFutureValue(String signature) {
        FutureValue<V> future;
        synchronized (this) {
            future = get(signature);
            if (future != null) {
                remove(signature);
            }
        }
        return future;
    }

    public void throwException(String signature, Throwable ex) {
        throwException(signature, ex, null, null);
    }

    public void throwException(String signature, Throwable ex, IStatsService stats, String prefix) {
        FutureValue<V> future = getAndRemoveFutureValue(signature);
        markCompleteTimeStamp(future, stats, prefix);
        if (future != null) {
            future.setException(ex);
        }
    }

    /**
     * For testability
     *
     * @param signature
     * @param pollMillis
     * @param timeoutMillis
     */
    public void awaitCallers(String signature, int pollMillis, int timeoutMillis) throws InterruptedException, InvalidStateException {
        long started = System.currentTimeMillis();
        boolean found = false;
        while (!found && (System.currentTimeMillis() - started) <= timeoutMillis) {
            found = containsKey(signature);
            if (!found) {
                Thread.sleep(pollMillis);
            }
        }
        if (!found) {
            throw new InvalidStateException("Timed out while waiting for callers on signature: '" + signature + "'");
        }
    }

    /**
     * Cancels all pending calls on the given signature
     *
     * @param signature
     */
    public synchronized void cancelAll(String signature) {
        FutureValue<V> future = getAndRemoveFutureValue(signature);
        if (future != null) {
            future.cancel(true);
        }
    }

    /**
     * Retries all the pending calls. This is useful when the call medium becomes unavailable (e.g. network connection
     * lost), is reestablished, and we want to try and complete all the pending calls.
     */
    public void retryAllCalls() {
        for (FutureValue<V> value : values()) {
            try {
                value.getCreator().asyncCall();
            } catch (Exception e) {
                value.setException(e);
            }
        }
    }


    private synchronized void markCompleteTimeStamp(String signature) {
        markCompleteTimeStamp(signature, null, null);
    }

    private synchronized void markCompleteTimeStamp(String signature, IStatsService stats, String prefix) {
        markCompleteTimeStamp(get(signature), stats, prefix);
    }


    public synchronized void setDataSize(String signature, long dataSize) {
        FutureValue value = get(signature);
        if (value != null && FutureValueWithTimeStamps.class.isAssignableFrom(value.getClass())) {
            ((FutureValueWithTimeStamps) value).setDataSize(dataSize);
        }
    }

    public synchronized void markFirstResponseTimeStamp(String signature) {
        FutureValue value = get(signature);
        if (value != null && FutureValueWithTimeStamps.class.isAssignableFrom(value.getClass())) {
            ((FutureValueWithTimeStamps) value).markFirstResponseTimeStamp();
        }
    }

    public synchronized void setPostResponseProcessingTime(String signature, long postResponseProcessingTime) {
        FutureValue value = get(signature);
        if (value != null && FutureValueWithTimeStamps.class.isAssignableFrom(value.getClass())) {
            ((FutureValueWithTimeStamps) value).setPostResponseProcessingTime(postResponseProcessingTime);
        }
    }

    public synchronized void markRequestTimeStamp(String signature) {
        FutureValue value = get(signature);
        if (value != null && FutureValueWithTimeStamps.class.isAssignableFrom(value.getClass())) {
            ((FutureValueWithTimeStamps) value).markRequestTimeStamp();
        }
    }

    public void markLastByteTimeStamp(String signature) {
        FutureValue value = get(signature);
        if (value != null && FutureValueWithTimeStamps.class.isAssignableFrom(value.getClass())) {
            ((FutureValueWithTimeStamps) value).markLastByteTimeStamp();
        }
    }
}

