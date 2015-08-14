package com.moscona.util.functions.impure;

/**
 * An impure function with 5 arguments
 */
@FunctionalInterface
public interface Function5<T1,T2,T3,T4,T5,R> extends ImpureFunction {
    R call(T1 t1,T2 t2,T3 t3,T4 t4, T5 t5) throws Exception;
}
