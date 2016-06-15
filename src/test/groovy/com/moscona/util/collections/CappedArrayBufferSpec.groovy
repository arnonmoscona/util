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

package com.moscona.util.collections

import spock.lang.*
import static org.assertj.core.api.Assertions.*;

/**
 * Created by Arnon Moscona on 1/18/2016.
 *
 */
@Subject(CappedArrayBuffer)
@Title("basic tests for CappedArrayBuffer")

@Narrative("""
migrated from EasyB
""")

class CappedArrayBufferSpec extends Specification {
    CappedArrayBuffer buffer

    def setup() {
        given: "a capped array buffer with size 3"
        buffer = new CappedArrayBuffer(3)
    }
    
    def "adding a couple of items"() {
        when: "I add two items"
            buffer << 1
            buffer << 2

        then: "they should be in the buffer"
            "$buffer".toString().equals "[1, 2]"

    }

    def "adding 3 items"() {
        when: "I add three items"
            buffer << 1
            buffer << 2
            buffer << 3

        then: "they should be in the buffer"
            "$buffer".toString().equals "[1, 2, 3]"

    }

    def "adding 4 items"() {
        when: "I add four items"
            buffer << 1
            buffer << 2
            buffer << 3
            buffer << 4

        then: "the last three should be in the buffer"
            "$buffer".toString().equals "[2, 3, 4]"

    }

    def "adding 5 items"() {
        when: "I add two items"
            buffer << 1
            buffer << 2
            buffer << 3
            buffer << 4
            buffer << 5

        then: "the last three should be in the buffer"
            "$buffer".toString().equals "[3, 4, 5]"

    }

    def "adding a list"() {
        when: "I add two items"
        buffer << 1
        buffer.addAll([2, 3, 4, 5])

        then: "the last three should be in the buffer"
        "$buffer".toString().equals "[3, 4, 5]"
    }
}