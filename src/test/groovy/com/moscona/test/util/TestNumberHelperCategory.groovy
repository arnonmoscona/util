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

package com.moscona.test.util

import lombok.experimental.UtilityClass
import org.assertj.core.data.Offset

/**
 * Created by Arnon Moscona on 12/4/2015.
 *
 */
@Category(Number)
// fixme This class should be in its own project
class TestNumberHelperCategory {

    /**
     * Tests approximate equality for floating point numbers
     * @param expected the expected value
     * @param tolerance The allowed tolerance
     * @return true if the value is within the tolerance
     */
    boolean isCloseTo(Number expected, Number tolerance) {
        return Math.abs(expected - this) <= tolerance
    }

    /**
     * Tests approximate equality for floating point numbers
     * @param expected the expected value
     * @param tolerance The allowed tolerance, expressed as and assertj Offset
     * @return true if the value is within the tolerance
     */
    boolean isCloseTo(Number expected, Offset<Number> tolerance) {
        return Math.abs(expected - this) <= tolerance.value
    }

    // fixme also should support Percentage
}
