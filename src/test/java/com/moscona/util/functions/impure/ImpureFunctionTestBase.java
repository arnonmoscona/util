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

import org.assertj.core.api.SoftAssertions;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Created by Arnon Moscona on 10/22/2015.
 * Some utility methods for test classes in this package
 */
public class ImpureFunctionTestBase {
    public static final String EXCEPTION = "exception";
    protected AtomicReference<String> evidence = new AtomicReference<>("clear");
    protected SoftAssertions soft = new SoftAssertions();

    //<editor-fold desc="Utilities">

    protected void appendEvidence(String result) {
        evidence.set(evidence.get()+"; "+result);
    }

    @SuppressWarnings("Duplicates")
    protected Function0<String> makeFunction0(String result) {
        clearEvidence();
        return () -> {
            if (result.equals(EXCEPTION)) {
                throw new Exception("exception thrown");
            }
            else {
                appendEvidence(result);
                return result;
            }
        };
    }

    protected Function0<Boolean> makeFunction0(boolean result) {
        clearEvidence();
        return () -> result;
    }

    @SuppressWarnings("Duplicates")
    protected Callable<String> makeCallable(String result) {
        clearEvidence();
        return () -> {
            if (result.equals(EXCEPTION)) {
                throw new Exception("exception thrown");
            }
            else {
                appendEvidence(result);
                return result;
            }
        };
    }

    protected Callable<Boolean> makeCallable(boolean result) {
        clearEvidence();
        return () -> result;
    }

    protected Runnable makeRunnable(String result) {
        clearEvidence();
        return () -> appendEvidence(result);
    }

    protected Action0 makeAction0(String result) {
        clearEvidence();
        return () -> {
            if (result.equals(EXCEPTION)) {
                throw new Exception("exception thrown");
            }
            else {
                appendEvidence(result);
            }
        };
    }

    protected Supplier<Boolean> makeSupplier(boolean result) {
        clearEvidence();
        return () -> result;
    }

    protected void clearEvidence() {
        evidence.set("cleared");
    }
    //</editor-fold>
}
