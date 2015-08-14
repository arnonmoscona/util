package com.moscona.util.functions.impure;

/**
 * An impure function with no arguments
 */
public interface Function0<R> extends ImpureFunction {
    R call() throws Exception;
}
