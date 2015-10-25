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

package com.moscona.util;

/**
 * Created: Apr 23, 2010 1:33:31 PM
 * By: Arnon Moscona
 * A runnable that handles code that throws exceptions
 */
public abstract class SafeRunnable implements Runnable {
    private Exception exception=null;
    private String name = "unnamed";

    public abstract void runWithPossibleExceptions() throws Exception;

    public Exception getException() {
        return exception;
    }

    @Override
    public void run() {
        try {
            runWithPossibleExceptions();
        }
        catch (Exception e) {
            exception = e;
        }
    }

    /**
     * Throws the exception that was captured during run() if one was thrown
     * @throws Exception the captured exception
     */
    public void rethrow() throws Exception {
        if (exception!=null)
            throw exception;
    }

    public SafeRunnable setName(String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }
}
