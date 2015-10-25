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

import com.moscona.util.TimeHelper

description "some of the functionality of the TimeHelper static library"

scenario "calculating a future time stamp from a HH:mm string", {
  given "a Calendar instance", {
    now = Calendar.instance
  }
  when "nextFutureTimeInLocalTime(16:03) is called", {
    result = TimeHelper.nextFutureTimeInLocalTime("16:03")
    cal = Calendar.instance
    cal.setTime(result)
  }
  then "the resulting time should be 16", {
    cal.get(Calendar.HOUR_OF_DAY).shouldBe 16
  }
  and "the resulting minute should be 3", {
    cal.get(Calendar.MINUTE).shouldBe 3
  }
  and "the resulting second should be 0", {
    cal.get(Calendar.SECOND).shouldBe 0
  }
  and "the result should be in the future", {
    cal.timeInMillis.shouldBeGreaterThan now.timeInMillis
  }
  and "the result should be no more than a day and one hour in the future", {
    (cal.timeInMillis - now.timeInMillis).shouldBeLessThan (25*3600000 as long)
  }
}

