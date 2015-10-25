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

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * An impure action with no arguments
 */
@FunctionalInterface
public interface Action0 extends ImpureAction {
    /**
     * calls the action
     * @throws Exception if there are any problems
     */
    void call() throws Exception;

    // Conversions

    //<editor-fold desc="Conversions">
    /**
     * Creates an <pre>Action0</pre> from a <pre>Function0</pre>
     * @param function the function tro use as an action
     * @return a new <pre>Action0</pre> that simply calls the function
     */
    static Action0 from(Function0<?> function) {
        assert function != null : "parameter may not be null";
        return function::call;
    }

    /**
     * Creates an <pre>Action0</pre> from a <pre>Callable</pre>
     * @param callable the callable tro use as an action
     * @return a new <pre>Action0</pre> that simply calls the callable
     */
    static Action0 from(Callable<?> callable) {
        assert callable != null : "parameter may not be null";
        return callable::call;
    }

    /**
     * Creates an <pre>Action0</pre> from a <pre>Runnable</pre>
     * @param function the function tro use as an action
     * @return a new <pre>Action0</pre> that simply calls the function
     */
    static Action0 from(Runnable function) {
        assert function != null : "parameter may not be null";
        return function::run;
    }
    //</editor-fold>

    // thenApply(...) =========================================================================================

    //<editor-fold desc="thenApply variants">
    /**
     * Creates a composed <pre>Action0</pre> that first calls this action, and then calls the <pre>after</pre> action if no exception was thrown by this action.
     * @param after the action to conditionally perform after.
     * @return the composed action.
     */
    default Action0 thenRun(Action0 after) {
        assert after != null : "parameter may not be null";
        final Action0 thisAction = Action0.this;
        return () -> {
            thisAction.call();
            after.call();
        };
    }

    /**
     * Creates a composed <pre>Action0</pre> that first calls this action, and then calls the <pre>after</pre> action if no exception was thrown by this action.
     * @param after the function to conditionally perform after.
     * @return the composed action.
     */
    default Action0 thenRun(Function0 after) {
        assert after != null : "parameter may not be null";
        return thenRun(Action0.from(after));
    }

    /**
     * Creates a composed <pre>Action0</pre> that first calls this action, and then calls the <pre>after</pre> callable if no exception was thrown by this action.
     * @param after the callable to conditionally call after.
     * @return the composed action.
     */
    default Action0 thenRun(Callable<?> after) {
        return thenRun(Action0.from(after));
    }

    /**
     * Creates a composed <pre>Action0</pre> that first calls this action, and then calls the <pre>after</pre> runnable if no exception was thrown by this action.
     * @param after the runnable to conditionally run after.
     * @return the composed action.
     */
    default Action0 thenRun(Runnable after) {
        return thenRun(Action0.from(after));
    }
    //</editor-fold>

    // afterRunning(...) =======================================================================================

    //<editor-fold desc="afterRunning() and compose() variants">
    /**
     * Returns a composed action that first calls the <pre>before</pre> action, and then calls this action, provided that no exception occurred.
     * @param before the action to call first
     * @return the new, composed action
     */
    default Action0 compose(Action0 before) {
        assert before != null : "may not pass nulls as parameters";

        final Action0 thisAction = this;
        return () -> {
            before.call();
            thisAction.call();
        };
    }

    /**
     * A more readable alias to the corresponding <pre>compose</pre> method.
     * (the name <pre>compose</pre> is used for source level "conceptual compatibility" with <pre>java.util.function.Function</pre>)
     * @param before the action to call first
     * @return the new, composed action
     */
    default Action0 afterRunning(Action0 before) { return compose(before); }

    /**
     * Returns a composed action that first calls the <pre>before</pre> function, and then calls this action, provided that no exception occurred.
     * @param before the function to call first
     * @return the new, composed action
     */
    default Action0 compose(Function0<?> before) {
        return compose(Action0.from(before));
    }

    /**
     * Returns a composed action that first calls the <pre>before</pre> callable, and then calls this action, provided that no exception occurred.
     * @param before the callable to call first
     * @return the new, composed action
     */
    default Action0 compose(Callable<?> before) {
        return compose(Action0.from(before));
    }

    /**
     * Returns a composed action that first calls the <pre>before</pre> runnable, and then calls this action, provided that no exception occurred.
     * @param before the runnable to run first
     * @return the new, composed action
     */
    default Action0 compose(Runnable before) {
        return compose(Action0.from(before));
    }

    /**
     * A more readable alias to the corresponding <pre>compose</pre> method.
     * (the name <pre>compose</pre> is used for source level "conceptual compatibility" with <pre>java.util.function.Function</pre>)
     * @param before the function to call first
     * @return the new, composed action
     */
    default Action0 afterRunning(Function0<?> before) { return compose(before); }

    /**
     * A more readable alias to the corresponding <pre>compose</pre> method.
     * (the name <pre>compose</pre> is used for source level "conceptual compatibility" with <pre>java.util.function.Function</pre>)
     * @param before the callable to call first
     * @return the new, composed action
     */
    default Action0 afterRunning(Callable<?> before) { return compose(before); }

    /**
     * A more readable alias to the corresponding <pre>compose</pre> method.
     * (the name <pre>compose</pre> is used for source level "conceptual compatibility" with <pre>java.util.function.Function</pre>)
     * @param before the runnable to run first
     * @return the new, composed action
     */
    default Action0 afterRunning(Runnable before) { return compose(before); }
    //</editor-fold>

    // Advise ================================================================================================

    //<editor-fold desc="Advise">
    /**
     * Performs an action before. An alias to compose.
     * @param advice an action to perform before calling the function
     * @return a new function that first calls the action and only then calls the function
     */
    default Action0 adviseBefore(Action0 advice) {
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
    default Action0 adviseAround(Action0 before, Consumer<Optional<Exception>> after) {
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
    default Action0 adviseAfter(Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise) {
        assert advice != null : "advice parameter may not be null";
        return () -> {
            Exception e = null;
            try {
                call();
            }
            catch (Exception e1) {
                e = e1;
                if (! alwaysPerformAdvise) {
                    throw e1;
                }
            }

            advice.accept(Optional.ofNullable(e));
            if (e != null) {
                throw e;
            }
        };
    }

    /**
     * Performs an action after the function is called. This effectively the same as
     * <pre>thenApply(Action0 after)</pre> but is exactly the same as
     * <pre>adviseAfter(action, false)</pre>.
     * @param advice the action to perform after the function runs.
     * @return A new function that calls this function and then performs the advise.
     */
    default Action0 adviseAfter(Consumer<Optional<Exception>> advice) {
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
    default Action0 adviseAround(Action0 before, Consumer<Optional<Exception>> after, boolean alwaysPerformAfterAdvise) {
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
     * @return an new function that first tests the predicate.
     */
    default Action0 guard(Function0<Boolean> predicate) {
        assert predicate != null : "parameter may not be null";
        Action0 thisAction = this;
        return () -> {
            Boolean decision = predicate.call();
            assert decision!=null : "the predicate return a null";

            if (decision) {
                thisAction.call();
            }
        };
    }

    /**
     * Performs the function conditionally
     * @param predicate the Supplier that tests the condition before. This may not return a <pre>null</pre>.
     * @return an new function that first tests the predicate.
     */
    default Action0 guard(Supplier<Boolean> predicate) {
        return guard(Function0.from(predicate));
    }

    /**
     * Performs the function conditionally
     * @param predicate the Supplier that tests the condition before. This may not return a <pre>null</pre>.
     * @return an new function that first tests the predicate.
     */
    default Action0 guard(Callable<Boolean> predicate) {
        return guard(Function0.from(predicate));
    }

    //</editor-fold>
}
