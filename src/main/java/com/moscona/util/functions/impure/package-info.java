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

/**
 * <p>This package includes functions and actions similar to some of those in RxJava's rx.functions.
 * The main difference is that these are "impure" in the sense that they may throw exceptions.
 * These are used in contexts where you want to abstract code in this way where you actually want to
 * handle exception and can call methods with exceptions without having to wrap them.
 * For example, when you want to convert a method call to a CompletableFuture, and allow
 * exceptional completion.
 * </p>
 */
package com.moscona.util.functions.impure;