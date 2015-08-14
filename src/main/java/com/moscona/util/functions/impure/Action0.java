package com.moscona.util.functions.impure;

/**
 * An impure action with no arguments
 */
@FunctionalInterface
public interface Action0 extends ImpureAction {
    void call() throws Exception;
}
