package com.moscona.util.functions.impure;

/**
 * An impure function with 8 arguments
 */
public interface Function8<T1,T2,T3,T4,T5,T6,T7,T8,R> extends ImpureFunction {
    R call(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7,T8 t8) throws Exception;
}
