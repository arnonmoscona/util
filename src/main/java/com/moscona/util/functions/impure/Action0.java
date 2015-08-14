package com.moscona.util.functions.impure;

/**
 * An impure action with no arguments
 */
public interface Action0 extends ImpureAction {
    void call() throws Exception;
}
