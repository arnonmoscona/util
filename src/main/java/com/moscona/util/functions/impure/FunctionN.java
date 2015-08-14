package com.moscona.util.functions.impure;

/**
 * An impure function with N arguments
 */
@FunctionalInterface
public interface FunctionN<R> extends ImpureFunction {
    R call(Object... args) throws Exception;
}
