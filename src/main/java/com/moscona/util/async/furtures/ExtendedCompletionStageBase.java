/*
 *  Copyright (c) 2015. Arnon Moscona
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Lesser General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moscona.util.async.furtures;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Delegate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Created by Arnon Moscona on 11/2/2015.
 * A based class implementing only the delegation part
 */
@EqualsAndHashCode
@ToString
abstract class ExtendedCompletionStageBase<T> implements ExtendedCompletionStage<T> {
    @Delegate
    protected final CompletableFuture<T> delegate;

    public ExtendedCompletionStageBase(CompletionStage<T> stage) {
        if (CompletableFuture.class.isAssignableFrom(stage.getClass())) {
            delegate = (CompletableFuture<T>) stage;
        } else {
            // wrap it with a regular CompletableFuture
            delegate = stage.toCompletableFuture();
        }
    }
}
