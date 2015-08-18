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

import com.moscona.util.monitoring.stats.StatValue;
import com.moscona.util.collections.CappedArrayBuffer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * Created: Aug 4, 2010 11:26:24 AM
 * By: Arnon Moscona
 * A class to help debug GC related problems. Best use in conjunction with jvisualvm, jconsole or another JMX management client
 * although you should be aware that the presence of a JMX client by itself will change application behavior, presumably
 * due to the overhead of communicating with the JMX console.
 */
public class MemoryStateHistory {
    private CappedArrayBuffer<MemoryStateMarker> buffer;
    private String[] garbageCollectors;

    public MemoryStateHistory(int bufferSize) {
        buffer = new CappedArrayBuffer<MemoryStateMarker>(bufferSize);
        detectGarbageCollectors();
    }

    private void detectGarbageCollectors() {
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ArrayList<String> beanNames = new ArrayList<String>();
            for (ObjectName objInst: server.queryNames(new ObjectName("java.lang:*"),null)) {
                if(objInst.toString().contains("type=GarbageCollector")) {
                    beanNames.add(objInst.toString());
                }
            }
            garbageCollectors = new String[beanNames.size()];
            int i = 0;
            for (String name: beanNames) {
                garbageCollectors[i++] = name;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            garbageCollectors = new String[0];
        }
    }

    public synchronized void sample() {
        if (garbageCollectors==null ||garbageCollectors.length == 0) {
            return;
        }
        buffer.add(new MemoryStateMarker(garbageCollectors));
    }

    public synchronized void sample(long performanceIndicator) {
        if (garbageCollectors==null ||garbageCollectors.length == 0) {
            return;
        }
        buffer.add(new MemoryStateMarker(garbageCollectors, performanceIndicator));
    }

    public String[] getGarbageCollectors() {
        return garbageCollectors;
    }

    public synchronized void print(PrintStream writer) {
        if (buffer.size() == 0) {
            return;
        }

        PrintStream out = writer==null ? System.out : writer;
        out.println("MEMORY HISTORY (start): ==================================================");
        out.println(buffer.get(0).headers());
        for (MemoryStateMarker state: buffer) {
            out.println(state.toString());
        }
        out.println("MEMORY HISTORY (end):   ==================================================");
    }

    public void print(File dumpFile) throws FileNotFoundException {
        if (dumpFile.exists()) {
            dumpFile.delete();
        }
        print(new PrintStream(dumpFile));
    }

    public synchronized MemoryStateMarker getLastSample() {
        return buffer.get(buffer.size()-1);
    }

    /**
     * Returns a stats value with descriptive stats for GC times for distinct GCs in the buffer.
     * Could return null in the timing stats if the bugger only includes one GC
     * (the time can only be calculated if we can see at least
     * two GC examples as the GC times reported are cumulative.
     * @return the stats
     */
    public synchronized GcTimeStats getGcTimeStats() {
        MemoryStateMarker first = buffer.get(0);
        StatValue retval = new StatValue(first.getGcTime());
        retval.attachDescriptiveStats();
        long gcCount = first.getGcCount(); // we only accumulate the stat when the GC count changes
        long lastGCCumulativeTime = first.getGcTime();
        long gcEstimatedTimestamp = -1;
        long gcTime = 0;

        for (MemoryStateMarker state: buffer) {
            long thisCount = state.getGcCount();
            long thisGCCumulativeTime = state.getGcTime();

            if (thisCount != gcCount) {
                gcCount = thisCount;
                gcTime = thisGCCumulativeTime - lastGCCumulativeTime;
                lastGCCumulativeTime = thisGCCumulativeTime;
                if (retval == null) {
                    retval = new StatValue(gcTime);
                }
                else {
                    retval.setAndAccumulate(gcTime);
                }
                gcEstimatedTimestamp = state.getMeasuredAt();
            }
        }

        return (retval==null) ? null : new GcTimeStats(gcEstimatedTimestamp, gcTime, retval);
    }

    public class GcTimeStats {
        private StatValue gcTime;
        private long gcEstimatedTimestamp;
        private long lastCgTime;

        public GcTimeStats(long gcEstimatedTimestamp, long lastGcTime, StatValue gcTime) {
            this.gcEstimatedTimestamp = gcEstimatedTimestamp;
            this.gcTime = gcTime;
        }

        public long getGcEstimatedTimestamp() {
            return gcEstimatedTimestamp;
        }

        public StatValue getGcTime() {
            return gcTime;
        }

        public long getLastCgTime() {
            return lastCgTime;
        }
    }
}

