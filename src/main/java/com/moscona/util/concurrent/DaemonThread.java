package com.moscona.util.concurrent;

/**
 * Created: May 19, 2010 5:43:53 PM
 * By: Arnon Moscona
 * A simple class for the creation of new named daemon threads
 */
public class DaemonThread extends Thread {
    public DaemonThread(Runnable code, String name) {
        super(code,name);
        setDaemon(true);
    }
}
