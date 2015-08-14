package com.moscona.util.functions.impure;

/**
 * An impure function with 1 arguments
 */
@FunctionalInterface
public interface Function1<T1,R> extends ImpureFunction {
    R call(T1 t1) throws Exception;
}
