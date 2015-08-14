package com.moscona.util.functions.impure;

/**
 * An impure function with N arguments
 */
@FunctionalInterface
public interface FunctionN<R> extends ImpureFunction {
    @FunctionalInterface
public R call(Object... args) throws Exception;
}
