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
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.Before;

import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.*;

/**
 * Created by Arnon Moscona on 10/21/2015.
 * Unit tests for Function0
 */
public class Function0Test extends ImpureFunctionTestBase {
    @Before
    public void setUp() {
        clearEvidence();
        soft = new SoftAssertions();
    }

    @Test
    public void testCall() throws Exception {
        assertThat(makeFunction0("result").call()).isEqualTo("result");
    }

    @Test
    public void testApply() throws Exception {
        assertThat(makeFunction0("result").apply()).isEqualTo("result");
    }

    @Test
    public void testApplyException() throws Exception {
        try {
            makeFunction0(EXCEPTION)
                    .apply();
        } catch (Exception e) {
            appendEvidence("caught");
        }
        assertThat(evidence.get()).isEqualTo("cleared; caught");
    }

    @Test
    public void testFromBooleanSupplierTrue() throws Exception {
        assertThat(Function0.from(() -> true).apply()).isTrue();
    }

    @Test
    public void testFromBooleanSupplierFalse() throws Exception {
        assertThat(Function0.from(() -> false).apply()).isFalse();
    }

    @Test
    public void testFromSupplier() throws Exception {
        assertThat(Function0.from(makeSupplier(false)).apply()).isFalse();
    }

    @Test
    public void testFrom2() throws Exception {
        assertThat(Function0.from(makeCallable(false)).apply()).isFalse();
    }

    @Test
    public void testFromValue() throws Exception {
        assertThat(Function0.fromValue("val").call()).isEqualTo("val");
    }


    @Test
    public void testAndThen() throws Exception {
        makeFunction0("main")
                .andThen(makeAction0("action"))
                .apply();
        assertThat(evidence.get()).isEqualTo("cleared; main; action");
    }

    @Test(expected = AssertionError.class)
    public void testAndThenNull() throws Exception {
        makeFunction0("main")
                .andThen((Action0) null)
                .apply();
    }

    @Test(expected = Exception.class)
    public void testAndThenException() throws Exception {
        makeFunction0(EXCEPTION)
                .andThen(makeAction0("action"))
                .apply();
    }

    @Test
    public void testChain() throws Exception {
        String r = makeFunction0("main")
                .chain(result -> "util.function("+result+")")
                .apply();
        assertThat(r).isEqualTo("util.function(main)");
    }

    @Test(expected = AssertionError.class)
    public void testChainNull() throws Exception {
        makeFunction0("main")
                .chain((Function<String, String>) null);
    }

    @Test(expected = Exception.class)
    public void testChainException() throws Exception {
        makeFunction0(EXCEPTION)
                .chain(result -> "util.function("+result+")")
                .apply();
    }

    @Test
    public void testAndThenUtilFunction() throws Exception {
        String r = makeFunction0("main")
                .andThen(result -> "util.function("+result+")")
                .apply();
        assertThat(r).isEqualTo("util.function(main)");
    }

    @Test(expected = AssertionError.class)
    public void testAndThenUtilFunctionNull() throws Exception {
        makeFunction0("main")
                .andThen((Function<String, String>) null);
    }

    @Test(expected = Exception.class)
    public void testAndThenUtilFunctionException() throws Exception {
        makeFunction0(EXCEPTION)
                .andThen(result -> "util.function("+result+")")
                .apply();
    }

    @Test
    public void testThenApply() throws Exception {
        Pair<String, String> r = makeFunction0("main")
                .thenApply(makeFunction0("second Function0"))
                .call();
        assertThat(r.toString()).isEqualTo("<main , second Function0>");
    }

    @Test(expected = AssertionError.class)
    public void testThenApplyNull() throws Exception {
        makeFunction0("main")
                .thenApply((Function0<String>) null);
    }

    @Test(expected = Exception.class)
    public void testThenApplyException() throws Exception {
        makeFunction0(EXCEPTION)
                .thenApply(makeFunction0("second Function0"))
                .call();
    }

    @Test
    public void testCompose() throws Exception {
        String r = makeFunction0("main")
                .compose(makeAction0("action"))
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; action; main");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testComposeNull() throws Exception {
        makeFunction0("main")
                .compose(null);
    }

    @Test(expected = Exception.class)
    public void testComposeException() throws Exception {
        makeFunction0(EXCEPTION)
                .compose(makeAction0("action"))
                .apply();
    }

    @Test
    public void testAfterRunning() throws Exception {
        String r = makeFunction0("main")
                .afterRunning(makeAction0("action"))
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; action; main");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testAfterRunningNull() throws Exception {
        makeFunction0("main")
                .afterRunning(null);
    }

    @Test(expected = Exception.class)
    public void testAfterRunningException() throws Exception {
        makeFunction0(EXCEPTION)
                .afterRunning(makeAction0("action"))
                .apply();
    }

    @Test
    public void testAdviseBefore() throws Exception {
        String r = makeFunction0("main")
                .adviseBefore(makeAction0("action"))
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; action; main");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testAdviseBeforeNull() throws Exception {
        makeFunction0("main")
                .adviseBefore(null);
    }

    @Test
    public void testAdviseAround() throws Exception {
        String r = makeFunction0("main")
                .adviseAround(makeAction0("before"), ex -> appendEvidence("after("+ex+")"))
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; before; main; after(Optional.empty)");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testAdviseAroundNull1() throws Exception {
        makeFunction0("main")
                .adviseAround(null, ex -> appendEvidence("after("+ex+")"));
    }

    @Test(expected = AssertionError.class)
    public void testAdviseAroundNull2() throws Exception {
        makeFunction0("main")
                .adviseAround(makeAction0("before"), null);
    }

    @Test
    public void testAdviseAfter() throws Exception {
        String r = makeFunction0("main")
                .adviseAfter(ex -> appendEvidence("after("+ex+")"))
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; main; after(Optional.empty)");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testAdviseAfterNull() throws Exception {
        makeFunction0("main")
                .adviseAfter(null);
    }

    @Test
    public void testAdviseAfterTrue() throws Exception {
        String r = makeFunction0("main")
                .adviseAfter(ex -> appendEvidence("after("+ex+")"), true)
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; main; after(Optional.empty)");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test
    public void testAdviseAfterExceptionTrue() throws Exception {
        String r = "not initialized";
        try {
            r = makeFunction0(EXCEPTION)
                    .adviseAfter(ex -> appendEvidence("after("+ex+")"), true)
                    .apply();
        } catch (Exception e) {
            appendEvidence("caught");
        }

        soft.assertThat(evidence.get()).isEqualTo("cleared; after(Optional[java.lang.Exception: exception thrown]); caught");
        soft.assertThat(r).isEqualTo("not initialized");
        soft.assertAll();
    }

    @Test
    public void testAdviseAfterExceptionFalse() throws Exception {
        String r = "not initialized";
        try {
            r = makeFunction0(EXCEPTION)
                    .adviseAfter(ex -> appendEvidence("after("+ex+")"), false)
                    .apply();
        } catch (Exception e) {
            appendEvidence("caught");
        }

        soft.assertThat(evidence.get()).isEqualTo("cleared; caught"); // after not called due to exception
        soft.assertThat(r).isEqualTo("not initialized");
        soft.assertAll();
    }

    @Test
    public void testAdviseAroundTrue() throws Exception {
        String r = makeFunction0("main")
                .adviseAround(makeAction0("before"), ex -> appendEvidence("after("+ex+")"), true)
                .apply();

        soft.assertThat(evidence.get()).isEqualTo("cleared; before; main; after(Optional.empty)");
        soft.assertThat(r).isEqualTo("main");
        soft.assertAll();
    }

    @Test
    public void testAdviseAroundExceptionTrue() throws Exception {
        String r = "not initialized";
        try {
            r = makeFunction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), ex -> appendEvidence("after("+ex+")"), true)
                    .apply();
        } catch (Exception e) {
            appendEvidence("caught");
        }

        soft.assertThat(evidence.get()).isEqualTo("cleared; before; after(Optional[java.lang.Exception: exception thrown]); caught");
        soft.assertThat(r).isEqualTo("not initialized");
        soft.assertAll();
    }

    @Test
    public void testAdviseAroundExceptionFalse() throws Exception {
        String r = "not initialized";
        try {
            r = makeFunction0(EXCEPTION)
                    .adviseAround(makeAction0("before"), ex -> appendEvidence("after("+ex+")"), false)
                    .apply();
        } catch (Exception e) {
            appendEvidence("caught");
        }

        soft.assertThat(evidence.get()).isEqualTo("cleared; before; caught"); // after not called due to exception
        soft.assertThat(r).isEqualTo("not initialized");
        soft.assertAll();
    }

    @Test
    public void testGuardCallableTrue() throws Exception {
        String result = makeFunction0("main")
                .guard((Callable<Boolean>) () -> true, "default")
                .apply();

        soft.assertThat(result).isEqualTo("main");
        soft.assertThat(evidence.get()).isEqualTo("cleared; main");
        soft.assertAll();
    }

    @Test
    public void testGuardCallableFalse() throws Exception {
        String result = makeFunction0("main")
                .guard((Callable<Boolean>) () -> false, "default")
                .apply();

        soft.assertThat(result).isEqualTo("default");
        soft.assertThat(evidence.get()).isEqualTo("cleared");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testGuardCallableNull() throws Exception {
        makeFunction0("main")
                .guard((Callable<Boolean>) null, "default");
    }

    @Test
    public void testGuardFunction0True() throws Exception {
        String result = makeFunction0("main")
                .guard((Function0<Boolean>) () -> true, "default")
                .apply();

        soft.assertThat(result).isEqualTo("main");
        soft.assertThat(evidence.get()).isEqualTo("cleared; main");
        soft.assertAll();
    }

    @Test
    public void testGuardFunction0False() throws Exception {
        String result = makeFunction0("main")
                .guard((Function0<Boolean>) () -> false, "default")
                .apply();

        soft.assertThat(result).isEqualTo("default");
        soft.assertThat(evidence.get()).isEqualTo("cleared");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testGuardFunction0Null() throws Exception {
        makeFunction0("main")
                .guard((Function0<Boolean>) null, "default");
    }


    @Test
    public void testGuardSupplierTrue() throws Exception {
        String result = makeFunction0("main")
                .guard((Supplier<Boolean>) () -> true, "default")
                .apply();

        soft.assertThat(result).isEqualTo("main");
        soft.assertThat(evidence.get()).isEqualTo("cleared; main");
        soft.assertAll();
    }

    @Test
    public void testGuardSupplierFalse() throws Exception {
        String result = makeFunction0("main")
                .guard((Supplier<Boolean>) () -> false, "default")
                .apply();

        soft.assertThat(result).isEqualTo("default");
        soft.assertThat(evidence.get()).isEqualTo("cleared");
        soft.assertAll();
    }

    @Test(expected = AssertionError.class)
    public void testGuardSupplierNull() throws Exception {
        makeFunction0("main")
                .guard((Supplier<Boolean>) null, "default");
    }
}