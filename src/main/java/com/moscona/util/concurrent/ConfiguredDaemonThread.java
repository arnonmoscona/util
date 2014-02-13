package com.moscona.util.concurrent;

/**
 * Created: Jul 5, 2010 11:33:44 AM
 * By: Arnon Moscona
 */
public class ConfiguredDaemonThread<Config> extends DaemonThread {
    protected Config config;
    private boolean safeToKill = false;

    public ConfiguredDaemonThread(Runnable code, String name, Config config) {
        super(code, name);
        this.config = config;
    }

    /**
     * A variant of the constructor that allows flagging this instance as safe to kill
     * @param code
     * @param name
     * @param config
     * @param safeToKill if true, then the server thread will be killed if it becomes a zombie
     */
    public ConfiguredDaemonThread(Runnable code, String name, Config config, boolean safeToKill) {
        this(code,name,config);
        this.safeToKill = safeToKill;
    }

    public boolean isSafeToKill() {
        return safeToKill;
    }
}

