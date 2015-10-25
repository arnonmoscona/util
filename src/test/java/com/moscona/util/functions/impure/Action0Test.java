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


import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Action0Test extends ImpureFunctionTestBase {



    @Before
    public void setup() {
        evidence.set("cleared");
    }

    @Test
    public void fromTestFunction0(/* Function0<?> function */) throws Exception {
        Action0 action = Action0.from(makeFunction0("called"));
        action.call();
        assertThat(evidence.get()).isEqualTo("cleared; called");
    }

    @Test(expected = AssertionError.class)
    public void fromTestFunction0Null(/* Function0<?> function */) throws Exception {
        Action0 action = Action0.from((Function0<?>) null);
    }

    @Test
    public void fromTestCallable(/* Callable<?> callable */) throws Exception {
        Action0 action = Action0.from(makeCallable("callable called"));
        action.call();
        assertThat(evidence.get()).isEqualTo("cleared; callable called");
    }

    @Test(expected = AssertionError.class)
    public void fromTestCallableNull(/* Callable<?> function */) throws Exception {
        Action0 action = Action0.from((Callable<?>) null);
    }

    @Test
    public void fromTestRunnable(/* Runnable function */) throws Exception {
        Action0 action = Action0.from(makeRunnable("runnable called"));
        action.call();
        assertThat(evidence.get()).isEqualTo("cleared; runnable called");
    }

    @Test(expected = AssertionError.class)
    public void fromTestRunnableNull(/* Runnable function */) throws Exception {
        Action0 action = Action0.from((Runnable) null);
    }

    @Test
    public void thenRunTestAction0(/* Action0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun(makeAction0("after executed"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; main executed; after executed");
    }

    @Test(expected = Exception.class)
    public void thenRunTestAction0Exception(/* Action0 after */) throws Exception {
        makeAction0(EXCEPTION)
                .thenRun(makeAction0("after executed"))
                .call();
    }

    @Test(expected = AssertionError.class)
    public void thenRunTestAction0Null(/* Action0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun((Action0)null);
    }

    @Test
    public void thenRunTestFunction0(/* Function0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun(makeFunction0("after executed"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; main executed; after executed");
    }

    @Test(expected = Exception.class)
    public void thenRunTestFunction0Exception(/* Function0 after */) throws Exception {
        makeAction0(EXCEPTION)
                .thenRun(makeFunction0("after executed"))
                .call();
    }

    @Test(expected = AssertionError.class)
    public void thenRunTestFunction0Null(/* Action0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun((Function0)null);
    }

    @Test
    public void thenRunTestCallable(/* Callable<?> after */) throws Exception {
        makeAction0("main executed")
                .thenRun(makeCallable("after executed"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; main executed; after executed");
    }

    @Test(expected = Exception.class)
    public void thenRunTestCallableException(/* Callable<?> after */) throws Exception {
        makeAction0(EXCEPTION)
                .thenRun(makeCallable("after executed"))
                .call();
    }

    @Test(expected = AssertionError.class)
    public void thenRunTestCallableNull(/* Action0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun((Callable<?>) null);
    }

    @Test
    public void thenRunTestRunnable(/* Runnable after */) throws Exception {
        makeAction0("main executed")
                .thenRun(makeRunnable("after executed"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; main executed; after executed");
    }

    @Test(expected = Exception.class)
    public void thenRunTestRunnableException(/* Runnable after */) throws Exception {
        makeAction0(EXCEPTION)
                .thenRun(makeRunnable("after executed"))
                .call();
    }

    @Test(expected = AssertionError.class)
    public void thenRunTestRunnableNull(/* Action0 after */) throws Exception {
        makeAction0("main executed")
                .thenRun((Runnable) null);
    }

    @Test
    public void composeTestAction0(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose(makeAction0("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test
    public void composeTestAction0Exception(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .compose(makeAction0("before"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test(expected = AssertionError.class)
    public void composeTestAction0Null(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose((Action0)null);
    }

    @Test
    public void afterRunningTestAction0(/* Action0 before */) throws Exception {
        makeAction0("action")
                .afterRunning(makeAction0("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test(expected = AssertionError.class)
    public void afterRunningTestAction0Null(/* Action0 before */) throws Exception {
        makeAction0("action")
                .afterRunning((Action0)null);
    }

    @Test
    public void composeTestFunction0(/* Function0<?> before */) throws Exception {
        makeAction0("action")
                .compose(makeFunction0("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test
    public void composeTestFunction0Exception(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .compose(makeFunction0("before"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test(expected = AssertionError.class)
    public void composeTestFunction0Null(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose((Function0<?>) null);
    }

    @Test
    public void composeTestCallable(/* Callable<?> before */) throws Exception {
        makeAction0("action")
                .compose(makeCallable("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test
    public void composeTestCallableException(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .compose(makeCallable("before"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test(expected = AssertionError.class)
    public void composeTestCallableNull(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose((Callable<?>) null);
    }

    @Test
    public void composeTestRunnable(/* Runnable before */) throws Exception {
        makeAction0("action")
                .compose(makeRunnable("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test
    public void composeTestRunnableException(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .compose(makeRunnable("before"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test(expected = AssertionError.class)
    public void composeTestRunnableNull(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose((Runnable) null);
    }

    @Test
    public void afterRunningTestFunction0(/* Function0<?> before */) throws Exception {
        makeAction0("action")
                .afterRunning(makeFunction0("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test(expected = AssertionError.class)
    public void afterRunningTestFunction0Null(/* Action0 before */) throws Exception {
        makeAction0("action")
                .afterRunning((Function0<?>) null);
    }

    @Test
    public void afterRunningTestCallable(/* Callable<?> before */) throws Exception {
        makeAction0("action")
                .afterRunning(makeCallable("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test(expected = AssertionError.class)
    public void afterRunningTestCallableNull(/* Action0 before */) throws Exception {
        makeAction0("action")
                .compose((Callable<?>) null);
    }

    @Test
    public void afterRunningTestRunnable(/* Runnable before */) throws Exception {
        makeAction0("action")
                .afterRunning(makeRunnable("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test(expected = AssertionError.class)
    public void afterRunningTestRunnableNull(/* Action0 before */) throws Exception {
        makeAction0("action")
                .afterRunning((Runnable) null);
    }

    @Test
    public void adviseBeforeTestAction0(/* Action0 advice */) throws Exception {
        makeAction0("action")
                .adviseBefore(makeAction0("before"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action");
    }

    @Test
    public void adviseBeforeTestAction0Exception(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseBefore(makeAction0("before"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test(expected = AssertionError.class)
    public void adviseBeforeTestAction0Null(/* Action0 before */) throws Exception {
        makeAction0("action")
                .adviseBefore(null);
    }

    @Test
    public void adviseAroundTestAction0(/* Action0 before, Consumer<Optional<Exception>> after */) throws Exception {
        makeAction0("action")
                .adviseAround(makeAction0("before"), ex -> appendEvidence("after ("+ ex.toString() +")"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action; after (Optional.empty)");
    }

    @Test
    public void adviseAroundTestAction0Exception(/* Action0 before */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), ex -> appendEvidence("after ("+ ex.toString() +")"))
                    .call();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; caught");
    }

    @Test
    public void adviseAroundTestAction0ExceptionTrue(/* Action0 before, Consumer<Optional<Exception>> after */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), ex -> appendEvidence("after ("+ ex.toString() +")"), true)
                    .call();
        } catch (Exception e) {
            appendEvidence("expected exception caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; after (Optional[java.lang.Exception: exception thrown]); expected exception caught");
    }

    @Test
    public void adviseAroundTestAction0ExceptionFalse(/* Action0 before, Consumer<Optional<Exception>> after */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), ex -> appendEvidence("after ("+ ex.toString() +")"), false)
                    .call();
        } catch (Exception e) {
            appendEvidence("expected exception caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; before; expected exception caught");
    }

    @Test(expected = AssertionError.class)
    public void adviseAroundTestAction0Null1(/* Action0 before, Consumer<Optional<Exception>> after */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAround(null, ex -> appendEvidence("after ("+ ex.toString() +")"), false)
                    .call();
        } catch (Exception e) {
            appendEvidence("should not get here");
        }
    }

    @Test(expected = AssertionError.class)
    public void adviseAroundTestAction0Null2(/* Action0 before, Consumer<Optional<Exception>> after */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), null, false)
                    .call();
        } catch (Exception e) {
            appendEvidence("should not get here");
        }
    }

    @Test
    public void adviseAfterTestConsumerBoolean(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        makeAction0("action")
                .adviseAfter(ex -> appendEvidence("after ("+ ex.toString() +")"), true)
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; action; after (Optional.empty)");
    }

    @Test(expected = AssertionError.class)
    public void adviseAfterTestConsumerBooleanNull(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        makeAction0("action")
                .adviseAfter(null, true)
                .call();
    }

    @Test
    public void adviseAfterTestConsumerBooleanExceptionTrue(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAfter(ex -> appendEvidence("after ("+ ex.toString() +")"), true)
                    .call();
        } catch (Exception e) {
            appendEvidence("expected exception caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; after (Optional[java.lang.Exception: exception thrown]); expected exception caught");
    }

    @Test
    public void adviseAfterTestConsumerBooleanExceptionFalse(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAfter(ex -> appendEvidence("after ("+ ex.toString() +")"), false)
                    .call();
        } catch (Exception e) {
            appendEvidence("expected exception caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; expected exception caught");
    }

    @Test
    public void adviseAfterTestConsumer(/* Consumer<Optional<Exception>> advice */) throws Exception {
        makeAction0("action")
                .adviseAfter(ex -> appendEvidence("after ("+ ex.toString() +")"))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; action; after (Optional.empty)");
    }

    @Test(expected = AssertionError.class)
    public void adviseAfterTestConsumerNull(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        makeAction0("action")
                .adviseAfter(null)
                .call();
    }

    @Test
    public void adviseAfterTestConsumerException(/* Consumer<Optional<Exception>> advice, boolean alwaysPerformAdvise */) throws Exception {
        try {
            makeAction0(EXCEPTION)
                    .adviseAfter(ex -> appendEvidence("after ("+ ex.toString() +")"))
                    .call();
        } catch (Exception e) {
            appendEvidence("expected exception caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; expected exception caught");
    }

    @Test
    public void adviseAroundTestAction0Boolean(/* Action0 before, Consumer<Optional<Exception>> after, boolean alwaysPerformAfterAdvise */) throws Exception {
        makeAction0("action")
                .adviseAround(makeAction0("before"), ex -> appendEvidence("after ("+ ex.toString() +")"), true)
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; before; action; after (Optional.empty)");
    }

    @Test
    public void guardTestFunction0True(/* Function0<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeFunction0(true))
                .call();
        assertThat(evidence.get()).isEqualTo("cleared; action");
    }

    @Test
    public void guardTestFunction0False(/* Function0<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeFunction0(false))
                .call();
        appendEvidence("continue");
        assertThat(evidence.get()).isEqualTo("cleared; continue");
    }

    @Test(expected = AssertionError.class)
    public void guardTestFunction0Null(/* Function0<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard((Function0<Boolean>) null)
                .call();
    }

    @Test
    public void guardTestSupplierTrue(/* Supplier<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeSupplier(true))
                .call();
        appendEvidence("continue");
        assertThat(evidence.get()).isEqualTo("cleared; action; continue");
    }

    @Test
    public void guardTestSupplierFalse(/* Supplier<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeSupplier(false))
                .call();
        appendEvidence("continue");
        assertThat(evidence.get()).isEqualTo("cleared; continue");
    }

    @Test(expected = AssertionError.class)
    public void guardTestSupplierNull(/* Function0<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard((Supplier<Boolean>) null)
                .call();
    }

    @Test
    public void guardTestCallableTrue(/* Callable<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeCallable(true))
                .call();
        appendEvidence("continue");
        assertThat(evidence.get()).isEqualTo("cleared; action; continue");
    }

    @Test
    public void guardTestCallableFalse(/* Callable<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard(makeCallable(false))
                .call();
        appendEvidence("continue");
        assertThat(evidence.get()).isEqualTo("cleared; continue");
    }

    @Test(expected = AssertionError.class)
    public void guardTestCallableNull(/* Function0<Boolean> predicate */) throws Exception {
        makeAction0("action")
                .guard((Callable<Boolean>) null)
                .call();
    }
}
