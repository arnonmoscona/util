package com.moscona.util.functions.impure;

/**
 * An impure action with 3 arguments
 */
@FunctionalInterface
public interface Action3<T1,T2,T3> extends ImpureAction {
    void call(T1 t1, T2 t2, T3 t3) throws Exception;
}
