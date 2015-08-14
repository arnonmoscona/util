package com.moscona.util.functions.impure;

/**
 * An impure function with 7 arguments
 */
@FunctionalInterface
public interface Function7<T1,T2,T3,T4,T5,T6,T7,R> extends ImpureFunction {
    R call(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7) throws Exception;
}
