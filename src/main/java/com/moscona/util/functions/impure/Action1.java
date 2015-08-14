package com.moscona.util.functions.impure;

/**
 * An impure action with 1 arguments
 */
@FunctionalInterface
public interface Action1<T1> extends ImpureAction {
    void call(T1 t1) throws Exception;
}
