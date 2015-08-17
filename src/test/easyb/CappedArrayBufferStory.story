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

// FixMe EasyB tests came from older code. Over time they should be replaced by Cucumber, spock, JUnit, or some other framework that is still alive. EasyB appers to be a dead project

import com.moscona.util.collections.CappedArrayBuffer

description "basic tests for CappedArrayBuffer"

before_each "scenario", {
  given "a capped array buffer with size 3", {
    buffer = new CappedArrayBuffer(3)
  }
}

scenario "adding a couple of items", {
  when "I add two items", {
    buffer << 1
    buffer << 2
  }
  then "they should be in the buffer", {
    "$buffer".shouldBe "[1, 2]"
  }
}

scenario "adding 3 items", {
  when "I add two items", {
    buffer << 1
    buffer << 2
    buffer << 3
  }
  then "they should be in the buffer", {
    "$buffer".shouldBe "[1, 2, 3]"
  }
}

scenario "adding 4 items", {
  when "I add two items", {
    buffer << 1
    buffer << 2
    buffer << 3
    buffer << 4
  }
  then "the last three should be in the buffer", {
    "$buffer".shouldBe "[2, 3, 4]"
  }
}

scenario "adding 5 items", {
  when "I add two items", {
    buffer << 1
    buffer << 2
    buffer << 3
    buffer << 4
    buffer << 5
  }
  then "the last three should be in the buffer", {
    "$buffer".shouldBe "[3, 4, 5]"
  }
}

scenario "adding a list", {
  when "I add two items", {
    buffer << 1
    buffer.addAll([2,3,4,5])
  }
  then "the last three should be in the buffer", {
    "$buffer".shouldBe "[3, 4, 5]"
  }
}
