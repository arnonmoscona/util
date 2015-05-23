package com.moscona.exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created: Apr 6, 2010 5:33:10 PM
 * By: Arnon Moscona
 * A simple helper to help construct error messages
 */
public class StackTraceHelper {
    final private ArrayList<StackTraceSequenceElement> debugSequence;
    private String tag = null; // if not null will auto-filter
    private static StackTraceHelper instance = null;
    private String[] filterSubstrings = {

    };

    private StackTraceHelper() {
        debugSequence = new ArrayList<>();
    }

    private StackTraceHelper(String... substrings) {
        this();
        filterSubstrings = Arrays.copyOf(substrings, substrings.length);
    }

    public static synchronized StackTraceHelper instance() {
        if (instance == null) {
            instance = new StackTraceHelper();
        }
        return instance;
    }

    /**
     * A variant that allows the caller to specify a list of substrings to exclude in filtered stack trace dumps
     * @param substrings
     * @return the static instance of StackTraceHelper, with the new filter substring list
     */
    public static synchronized StackTraceHelper instance(String... substrings) {
        if (instance == null) {
            instance = new StackTraceHelper(substrings);
        }
        else {
            instance.filterSubstrings = Arrays.copyOf(substrings, substrings.length);
        }
        return instance;
    }

    public static String thisLineLocation(int offset,String message) {
        StringBuilder retval = new StringBuilder();
        StackTraceElement element = Thread.currentThread().getStackTrace()[2+offset];
        retval.append(element.getClassName()).append(".");
        retval.append(element.getMethodName()).append("(");
        retval.append(element.getFileName()).append(":");
        retval.append(element.getLineNumber()).append(")");
        retval.append("  |  [").append(Thread.currentThread().getName()).append("]");
        retval.append(" @ ").append(new Date().toString());
        if (message != null) {
            retval.append("\n    ").append(message);
        }
        return retval.toString();
    }

    public static String thisLineLocation() {
        return thisLineLocation(1,null);
    }

    public static String thisLineLocation(String message) {
        return thisLineLocation(1,message);
    }

    public static String notImplementedError(int offset) {
        return "ERROR: method not implemented\n\tat: "+thisLineLocation(1+offset,null);
    }

    public static String notImplementedError() {
        return notImplementedError(1);
    }

    /**
     * Prints a stack trace that only includes intellitrade stack trace elements
     * @param thread
     * @param out
     */
    public void dumpFilteredStackTrace(Thread thread, PrintStream out) {
        out.println("\nThread dump for thread #"+thread.getId()+": "+thread);
        StackTraceElement[] trace = thread.getStackTrace();
        for (StackTraceElement element: trace) {
            String clazz = element.getClassName();
            if (passesStandardFilter(clazz)) {
                out.println("  at "+clazz+"."+element.getMethodName()+"("+element.getFileName()+":"+element.getLineNumber()+")");
            }
        }
    }

    public void debugMark(String context, String tag) {
        StackTraceSequenceElement element = new StackTraceSequenceElement(context, tag);
        synchronized (debugSequence) {
            debugSequence.add(element);
        }
    }

    public void debugClearList() {
        synchronized (debugSequence) {
            debugSequence.clear();
        }
    }

    public void debugDump(String tag, boolean andClear) {
        debugDump(tag, andClear, System.err);
    }

    public void debugDump(boolean andClear, File file) {
        debugDump(tag, andClear, file);
    }

    public void debugDump(String tag, boolean andClear, File file) {
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream os = new FileOutputStream(file);
            PrintStream out = new PrintStream(os);
            try {
                debugDump(tag, andClear, out);
            }
            finally {
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
    public void debugDump(String tag, boolean andClear, PrintStream out) {
        synchronized (debugSequence) {
            out.println("\n\n=====================================================");
            out.println("Dump of stack trace history: ");
            out.println("=====================================================\n\n");

            if (debugSequence.size()==0) {
                out.println("Nothing to print...");
                return;
            }

            int i=0;
            long start = debugSequence.get(0).timestamp;
            for (StackTraceSequenceElement element: debugSequence) {
                if (tag==null || element.tag != null && element.tag.equals(tag)) {
                    out.println("........................");
                    out.println(Integer.toString(i++)+": "+element.context+"  tag "+element.tag+"\n");
                    out.println("at "+(element.timestamp-start)+"  thread: "+element.thread);
                    for (StackTraceElement e: element.trace) {
                        String clazz = e.getClassName();
                        if (passesStandardFilter(clazz)) {
                            out.println("  at "+clazz+"."+e.getMethodName()+"("+e.getFileName()+":"+e.getLineNumber()+")");
                        }
                    }
                }
            }
            out.println("\n\n\n");
            if(andClear) {
                debugClearList();
            }
        }
    }

    private boolean passesStandardFilter(String clazz) {
        if (clazz.contains("StackTraceHelper")) {
            return false; // always filter out this class
        }
        for (String substring: filterSubstrings) {
            if (clazz.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    public synchronized String getTag() {
        return tag;
    }

    public synchronized void setTag(String tag) {
        this.tag = tag;
    }

    public static class StackTraceSequenceElement {
        private String context;
        private String tag;
        private long timestamp;
        protected StackTraceElement[] trace;
        public String thread;

        private StackTraceSequenceElement(String context, String tag) {
            this.context = context;
            this.tag = tag;
            trace = Thread.currentThread().getStackTrace();
            timestamp = System.currentTimeMillis();
            thread = Thread.currentThread().toString()+" ("+Thread.currentThread().getId()+")";
        }
    }
}
