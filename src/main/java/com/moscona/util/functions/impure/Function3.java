package com.moscona.util.functions.impure;

/**
 * An impure function with 3 arguments
 */
@FunctionalInterface
public interface Function3<T1,T2,T3,R> extends ImpureFunction {
    R call(T1 t1, T2 t2, T3 t3) throws Exception;
}
