package com.moscona.util.functions.impure;

/**
 * An impure function with N arguments
 */
public interface FunctionN<R> extends ImpureFunction {
    public R call(Object... args) throws Exception;
}
