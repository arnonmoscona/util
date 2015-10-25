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

package com.moscona.exceptions;

/**
 * Created: 10/6/11 4:05 PM
 * By: Arnon Moscona
 */
public class DecoratedException extends Exception {
    private static final long serialVersionUID = 6329237889442808811L;
    private Object decoration = null;

    public DecoratedException(String message) {
        super(message);
    }

    public DecoratedException(String message, Throwable cause) {
        super(message, cause);
    }

    public DecoratedException(String message, Object decoration) {
        super(message);
        this.decoration = decoration;
    }

    public DecoratedException(String message, Throwable cause, Object decoration) {
        super(message, cause);
        this.decoration = decoration;
    }

    public Object getDecoration() {
        return decoration;
    }
}
