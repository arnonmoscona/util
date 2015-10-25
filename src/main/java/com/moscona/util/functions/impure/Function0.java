/*
 * Copyright (c) 2015. Arnon Moscona
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moscona.util.functions.impure;

import com.moscona.util.collections.Pair;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * An impure function with no arguments
 */
@FunctionalInterface
public interface Function0<R> extends ImpureFunction {
    /**
     * calls the function
     * @return the result
     * @throws Exception if there are any problems
     */
    R call() throws Exception;

    // Sugar ===============================================================================================

    /**
     * Just an alias to call() (JDK convention)
     * @return the result
     * @throws Exception if there are any problems
     */
    default R apply() throws Exception { return call(); }

    // Converters ==========================================================================================

    //<editor-fold desc="Converters">
    /**
     * Makes a <pre>Function0</pre> from a <pre>Callable</pre>
     * @param callable the callable to convert
     * @param <R> the type parameter
     * @return a new <pre>Function0</pre> that simply calls the callable
     */
    static <R> Function0<R> from(final Callable<R> callable) {
        assert callable != null : "may not pass nulls as parameters";

        return callable::call;
    }

    /**
     * Makes a <pre>Function0</pre> from a <pre>Supplier</pre>
     * @param supplier the supplier to convert
     * @param <R> the type parameter
     * @return a new <pre>Function0</pre> that simply calls the supplier
     */
    static <R> Function0<R> from(final Supplier<R> supplier) {
        assert supplier != null : "may not pass nulls as parameters";

        return supplier::get;
    }

    /**
     * Creates a boolean function from a BooleanSupplier
     * @param predicate the BooleanSupplier to convert from (may not be null)
     * @return a new function that resolves to the supplier's <pre>getAsBoolean()</pre> method
     */
    static Function0<Boolean> from(BooleanSupplier predicate) {
        assert predicate != null : "may not pass nulls as parameters";
        return predicate::getAsBoolean;
    }

    /**
     * Makes a <pre>Function0</pre> from a value
     * @param value the value to convert
     * @param <R> the type parameter
     * @return a new <pre>Function0</pre> that simply calls the value
     */
    static <R> Function0<R> fromValue(final R value) {
        return () -> value;
    }


    //</editor-fold>

    // thenApply() / chain() =================================================================================

    //<editor-fold desc="thenApply / chain">
    /**
     * Composes with a single parameter function to create a new function that first calls this function
     * and then calls the other function with the return value of this function as a parameter.
     * @param after the function to chain after
     * @param <V> the return value of the chained function
     * @return the return value of the other function
     */
    default <V> Function0<V> andThen(Function<R,V> after) {
        assert after != null: "may not pass null as the other function";
        Function0<R> thisFunction = this;
        return () -> after.apply(thisFunction.call());
    }

    /**
     * Just an alias of <pre>thenApply(Function1&lt;R,V&gt; after)</pre>
     * Composes with a single parameter function to create a new function that first calls this function
     * and then calls the other function with the return value of this function as a parameter.
     * @param after the function to chain after
     * @param <V> the return value of the chained function
     * @return the return value of the other function
     */
    default <V> Function0<V> chain(Function<R,V> after) {
        return andThen(after);
    }

    /**
     * Perform a simple action after calling the function
     * @param after the action to perform after this function is called
     * @return a new function that first calls this function, then calls the action,
     * and finally returns the return value of this function
     */
    default Function0<R> andThen(Action0 after) {
        assert after != null : "may not pass nulls as parameters";
        return () -> {
            R result = call();
            after.call();
            return result;
        };
    }

    /**
     * Calls another <pre>Function0</pre> after successfully calling this function
     * @param after the function to run after this function
     * @param <V> the return type of the second function
     * @return a Pair of the results from both functions (if both succeeded)
     */
    default <V> Function0<Pair<R,V>> thenApply(Function0<V> after) {
        assert after != null : "may not pass nulls as parameters";
        final Function0<R> thisFunction = this;
        return () -> new Pair<>(thisFunction.call(), after.call());
    }

    //</editor-fold>

    // compose() / afterCalling() ==========================================================================

    //<editor-fold desc="afterCalling / compose">
    /**
     * Performs an action before
     * @param before an action to perform before calling the function
     * @return a new function that first calls the action and only then calls the function
     */
    default Function0<R> compose(Action0 before) {
        assert before != null : "may not pass nulls as parameters";
        return () -> {
            before.call();
            return call();
        };
    }

    /**
     * Performs an action before . An alias to compose.
     * @param before an action to perform before calling the function
     * @return a new function that first calls the action and only then calls the function
     */
    default Function0<R> afterRunning(Action0 before) {
        return compose(before);
    }

    //</editor-fold>

    // Advise ==============================================================================================

    //<editor-fold desc="Advise">
    /**
     * Performs an action before. An alias to compose.
     * @param advice an action to perform before calling the function
     * @return a new function that first calls the action and only then calls the function
     */
    default Function0<R> adviseBefore(Action0 advice) {
        assert advice != null : "parameter may not be null";
        return compose(advice);
    }

    /**
     * Performs an action before and another action after.
     * @param before an action to perform before calling the function
     * @param after an action to perform after calling the function
     * @return a new function that first invokes the before advise, then calls the function,
     * and finally calls the after advise
     */
    default Function0<R> adviseAround(Action0 before, Consumer<Optional<Exception>> after) {
        assert before != null : "before parameter may not be null";
        assert after != null : "after parameter may not be null";
        return adviseBefore(before).adviseAfter(after);
    }

    /**
     * Performs an action after the function is called. This is not the same as
     * <pre>thenApply(Action0 after)</pre> because you can force the advise to be performed
     * regardless of whether the function threw a an <pre>Exception</pre>. Note that f the function
     * throws an <pre>Error</pre> then the advise is not executed.
     * @param advice the action to perform after the function runs.
     * @param alwaysPerformAdvise if true then the advise will be called even if the function throws
     *                            and <pre>Exception</pre>, otherwise any exceptions will prevent the
     *                            advise from being executed
     * @return A new function that calls this function and then performs the advise.
     */
    default Function0<R> adviseAfter(Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise) {
        assert advice != null : "advice parameter may not be null";
        return () -> {
            Exception e = null;
            R result = null;
            try {
                result = call();
            } catch (Exception e1) {
                e = e1;
                if (! alwaysPerformAdvise) {
                    throw e1;
                }
            }

            advice.accept(Optional.ofNullable(e));
            if (e != null) {
                throw e;
            }
            return result;
        };
    }

    /**
     * Performs an action after the function is called. This effectively the same as
     * <pre>thenApply(Action0 after)</pre> but is exactly the same as
     * <pre>adviseAfter(action, false)</pre>.
     * @param advice the action to perform after the function runs.
     * @return A new function that calls this function and then performs the advise.
     */
    default Function0<R> adviseAfter(Consumer<Optional<Exception>> advice) {
        assert advice != null : "parameter may not be null";
        return adviseAfter(advice, false);
    }

    /**
     * Performs an action before and another action after.
     * @param before an action to perform before calling the function
     * @param after an action to perform after calling the function
     * @param alwaysPerformAfterAdvise determines whether the after advise is always called,
     *                                 regardless of any exceptions (not <pre>Error</pre>)
     *                                 that are thrown before reaching the after advise.
     * @return a new function that first invokes the before advise, then calls the function,
     * and finally calls the after advise
     */
    default Function0<R> adviseAround(Action0 before, Consumer<Optional<Exception>> after, boolean alwaysPerformAfterAdvise) {
        assert before != null : "before parameter may not be null";
        assert after != null : "after parameter may not be null";
        return adviseBefore(before).adviseAfter(after, alwaysPerformAfterAdvise);
    }
    //</editor-fold>

    // guards ==============================================================================================

    //<editor-fold desc="Guards">
    /**
     * Performs the function conditionally
     * @param predicate the function that tests the condition before. This may not return a <pre>null</pre>.
     * @param defaultValue the return value to use if the predicate returns <pre>false</pre>
     * @return an new function that first tests the predicate.
     */
    default Function0<R> guard(Function0<Boolean> predicate, R defaultValue) {
        assert predicate != null : "predicate parameter may not be null";
        Function0<R> thisFunction = this;
        return () -> {
            Boolean decision = predicate.call();
            assert decision!=null : "the predicate return a null";

            if (decision) {
                return thisFunction.call();
            }
            else {
                return defaultValue;
            }
        };
    }

    /**
     * Performs the function conditionally
     * @param predicate the Supplier that tests the condition before. This may not return a <pre>null</pre>.
     * @param defaultValue the return value to use if the predicate returns <pre>false</pre>
     * @return an new function that first tests the predicate.
     */
    default Function0<R> guard(Supplier<Boolean> predicate, R defaultValue) {
        assert predicate != null : "predicate parameter may not be null";
        return guard(Function0.from(predicate), defaultValue);
    }

    /**
     * Performs the function conditionally
     * @param predicate the Supplier that tests the condition before. This may not return a <pre>null</pre>.
     * @param defaultValue the return value to use if the predicate returns <pre>false</pre>
     * @return an new function that first tests the predicate.
     */
    default Function0<R> guard(Callable<Boolean> predicate, R defaultValue) {
        assert predicate != null : "predicate parameter may not be null";
        return guard(Function0.from(predicate), defaultValue);
    }

    //</editor-fold>
}

