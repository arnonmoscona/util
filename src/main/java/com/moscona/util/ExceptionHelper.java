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
 * Created: Jun 15, 2010 10:45:20 AM
 * By: Arnon Moscona
 */
public class ExceptionHelper {
    @SuppressWarnings("unchecked")
    public static Throwable fishOutOf(Throwable exception, Class typeToFishOut, int maxCauseDepth) {
        if (maxCauseDepth<0) {
            return null;
        }
        if (typeToFishOut.isAssignableFrom(exception.getClass())) {
            return exception;
        }
        Throwable cause = exception.getCause();
        if (cause==null) {
            return null;
        }
        return fishOutOf(cause, typeToFishOut ,maxCauseDepth-1);
    }

    public static Throwable findRootCause(Throwable exception, int maxCauseDepth) {
        if (maxCauseDepth<0) {
            return null;
        }
        Throwable cause = exception.getCause();
        if (cause == null) {
            return exception; // it is the root cause
        }
        return findRootCause(cause, maxCauseDepth-1);
    }
}
