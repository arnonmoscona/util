package com.moscona.util.functions.impure;

/**
 * An impure action with N arguments
 */
public interface ActionN extends ImpureAction {
    public void call(Object... args) throws Exception;
}
