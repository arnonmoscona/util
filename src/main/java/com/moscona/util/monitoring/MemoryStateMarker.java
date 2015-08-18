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

package com.moscona.util.monitoring;

import com.moscona.util.TimeHelper;
import com.sun.management.GarbageCollectorMXBean;
import com.sun.management.GcInfo;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created: Aug 3, 2010 1:05:34 PM
 * By: Arnon Moscona
 * Helps track memory utilization
 * Normally used via MemoryStateHistory
 * Best use in conjunction with jvisualvm, jconsole or another JMX management client.
 */
public class MemoryStateMarker {
//    private static final String GC_BEAN_NAME = "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";
    private static final String GC_BEAN_NAME = "java.lang:type=GarbageCollector,name=ParNew"; // FIXME this should be determined externally or automatically
//    private static final String GC_BEAN_NAME = "java.lang:type=GarbageCollector,name=PS MarkSweep";
//    private static final String GC_BEAN_NAME = "java.lang:type=GarbageCollector,name=PS Scavenge";

    private long measuredAt;
    private long freeMemory;
    private long maxMemory;
    private long totalMemory;
    private long performanceIndicator =0L;
    private GcInfo[] gci = null;
    private long gcCount = 0;
    private long gcTime = 0;
    private String[] garbageCollectors;

    public MemoryStateMarker(String[] garbageCollectors) {
        this.garbageCollectors = garbageCollectors;
        measuredAt = TimeHelper.now();
        Runtime runtime = Runtime.getRuntime();
        freeMemory = runtime.freeMemory();
        maxMemory = runtime.maxMemory();
        totalMemory = runtime.totalMemory();
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            if (garbageCollectors!=null) {
                int i=0;
                gci = new GcInfo[garbageCollectors.length];
                for (String gc: garbageCollectors) {
                    GarbageCollectorMXBean bean = ManagementFactory.newPlatformMXBeanProxy(server,gc,GarbageCollectorMXBean.class);
                    gcCount = bean.getCollectionCount();
                    gcTime = bean.getCollectionTime();
                    gci[i++] = bean.getLastGcInfo();
                }
            }
        }
        catch(Exception e) {
            // ignore
        }
    }

    public MemoryStateMarker(String[] garbageCollectors, long performanceIndicator) {
        this(garbageCollectors);
        this.performanceIndicator = performanceIndicator;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public long getMeasuredAt() {
        return measuredAt;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public long getPerformanceIndicator() {
        return performanceIndicator;
    }

    public long getGcCount() {
        return gcCount;
    }

    /**
     * The *cumulative* GC time so far
     * @return total GC time
     */
    public long getGcTime() {
        return gcTime;
    }

    public Map<String,GcInfo> getGci() {
        HashMap<String,GcInfo> retval = new HashMap<String,GcInfo>();
        if (garbageCollectors != null) {
            int i=0;
            for (String gc: garbageCollectors) {
                retval.put(gc,gci[i++]);
            }
        }
        return retval;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        String delimiter = ",";
        s.append(measuredAt).append(delimiter);
        s.append(totalMemory).append(delimiter);
        s.append(freeMemory).append(delimiter);
        s.append(maxMemory).append(delimiter);
        s.append(performanceIndicator).append(delimiter);
        s.append(gcTime).append(delimiter);
        s.append(gcCount).append(delimiter);

        if (gci!=null) {
            for (GcInfo gcii : gci) {
                if (gcii!=null) {
                    s.append(gcii.getId()).append(delimiter);
                    s.append(gcii.getStartTime()).append(delimiter);
                    s.append(gcii.getEndTime()).append(delimiter);
                    s.append(gcii.getDuration()).append(delimiter);

                    Map<String, MemoryUsage> usage = gcii.getMemoryUsageBeforeGc();

                    long memUsedBefore = 0;
                    for (String key : usage.keySet()) {
                        memUsedBefore += usage.get(key).getUsed();
                    }
                    s.append(memUsedBefore).append(delimiter);

                    usage = gcii.getMemoryUsageAfterGc();
                    long memUsedAfter = 0;
                    for (String key : usage.keySet()) {
                        memUsedAfter += usage.get(key).getUsed();
                    }
                    s.append(memUsedAfter).append(delimiter);
                    s.append(memUsedBefore - memUsedAfter).append(delimiter);
                }
                else {
                    for(int i=0; i<7; i++){
                        s.append(delimiter);
                    }
                }
            }
        }
        return s.toString();
    }

    public String headers() {
        String gcHeaders = ",lastGcId,lastGCStart,lastGCEnd,lastGCDuration,lastCGMemBefore,lastCGMemAfter,lastGCMemFreed";
        StringBuilder s = new StringBuilder("measuredAt,totalMemory,freeMemory,maxMemory,performanceIndicator,cumulativeGCTime,cumulativeGCCount");
        if (garbageCollectors != null && garbageCollectors.length>0) {
            for (String gc: garbageCollectors) {
                s.append(gcHeaders.replaceAll(",",","+gc.replaceAll(".*name=","")+" "));
            }
        }
        return s.toString();
    }
}
