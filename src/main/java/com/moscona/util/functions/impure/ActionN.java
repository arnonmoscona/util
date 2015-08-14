package com.moscona.util.functions.impure;

/**
 * An impure action with N arguments
 */
@FunctionalInterface
public interface ActionN extends ImpureAction {
    @FunctionalInterface
public void call(Object... args) throws Exception;
}
