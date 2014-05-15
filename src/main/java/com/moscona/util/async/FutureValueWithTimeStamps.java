package com.moscona.util.async;

import com.moscona.exceptions.InvalidStateException;
import com.moscona.util.monitoring.stats.IStatsService;

/**
 * Created: 8/14/11 1:14 PM By: Arnon Moscona
 */
public class FutureValueWithTimeStamps<V> extends FutureValue<V> {

    private long creationTimeStamp = -1;
    private long requestTimeStamp = -1;
    private long firstResponseTimeStamp = -1;
    private long completeTimeStamp = -1;
    private long postResponseProcessingTime = -1;
    private long lastByteTimeStamp = -1; // clients can report processing time directly or report boundaries only
    private long dataSize = -1;

    protected FutureValueWithTimeStamps(String name, AsyncFunctionCall<V> creator) {
        super(name, creator);
        creationTimeStamp = System.currentTimeMillis();
    }

    public void markCompleteTimeStamp() {
        completeTimeStamp = System.currentTimeMillis();
    }

    public void markCompleteTimeStamp(IStatsService stats, String prefix) {
        markCompleteTimeStamp();
        if (stats != null && prefix != null && requestTimeStamp >= 0 && completeTimeStamp >= 0) {
            synchronized (this) {
                try {
                    long totalResponseTime = completeTimeStamp - requestTimeStamp;
                    String p = "async_call_" + prefix + "_";
                    stats.addTimingSampleFor(p + "total_response_time", totalResponseTime);
                    if (firstResponseTimeStamp >= 0) {
                        stats.addTimingSampleFor(p + "first_response_time", firstResponseTimeStamp - requestTimeStamp);
                    }
                    if (postResponseProcessingTime < 0 && lastByteTimeStamp >= 0) {
                        postResponseProcessingTime = completeTimeStamp - lastByteTimeStamp;
                    }
                    if (postResponseProcessingTime >= 0) {
                        stats.addTimingSampleFor(p + "response_processing_time", postResponseProcessingTime);
                        stats.addTimingSampleFor(p + "remote_response_time", totalResponseTime - postResponseProcessingTime);
                    } else if (firstResponseTimeStamp >= 0) {
                        // post response processing time was not reported but we can estimate it without knowing exactly when the last byte was received
                        stats.addTimingSampleFor(p + "first_byte_to_finish_time", completeTimeStamp - firstResponseTimeStamp);
                    }
                    if (dataSize >= 0) {
                        stats.incStat(p + "total_data_size", dataSize);
                    }
                } catch (InvalidStateException e) {
                    // do nothing
                }
            }
        }
    }

    public long getCompleteTimeStamp() {
        return completeTimeStamp;
    }

    public long getCreationTimeStamp() {
        return creationTimeStamp;
    }

    public long getDataSize() {
        return dataSize;
    }

    public void setDataSize(long dataSize) {
        this.dataSize = dataSize;
    }

    public long getFirstResponseTimeStamp() {
        return firstResponseTimeStamp;
    }

    public void markFirstResponseTimeStamp() {
        if (firstResponseTimeStamp >= 0) {
            return; // only the first one takes
        }
        firstResponseTimeStamp = System.currentTimeMillis();
    }

    public long getPostResponseProcessingTime() {
        return postResponseProcessingTime;
    }

    public void setPostResponseProcessingTime(long postResponseProcessingTime) {
        this.postResponseProcessingTime = postResponseProcessingTime;
    }

    public long getRequestTimeStamp() {
        return requestTimeStamp;
    }

    public void markRequestTimeStamp() {
        requestTimeStamp = System.currentTimeMillis();
    }

    public void markLastByteTimeStamp() {
        lastByteTimeStamp = System.currentTimeMillis();
    }
}

