package com.moscona.util.functions.impure;

/**
 * An impure action with 2 arguments
 */
public interface Action2<T1,T2> extends ImpureAction {
    void call(T1 t1, T2 t2) throws Exception;
}
