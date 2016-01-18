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

package com.moscona.util.monitoring.stats

import spock.lang.*
import static org.assertj.core.api.Assertions.*;

/**
 * Created by Arnon Moscona on 12/4/2015.
 *
 */
@Subject(SimpleStatsService)
@Title("SimpleStatsService specification")

@Narrative("""
This specification replaces parts of the EasyB story StatsSetviceStory.story

It was written as part of migrating all EasyB stories to Spock.
As such, it is not always idiomatic.
""")

class StatsServiceSpec extends Specification {
    SimpleStatsService service
    def value

    def setup() {
        given: "a stats service"
        service = new SimpleStatsService()

        and: "a test variable"
        value = null

    }

    def "immediately after creating"() {
        expect: "that the service is on"
        service.isOn()
    }

    def "setting a stat to an explicit value"() {
        when: "I set stat1 to the value 2"
        service.setStat("stat1", 2 as long)

        then: "I should be able to retrieve it"
        def value = service.getStat("stat1")

        and: "its should be an Long value"
        value.type == Long

        and: "its value should be 2"
        value.getLong() == 2
        Math.abs(value.getDouble() - 2.0f) < 0.0000001

    }

    def "setting a stat to an explicit value after setting it to another value"() {
        when: "I set stat1 to the value 3"
        service.setStat("stat1", 3 as long)

        and: "then I set stat1 to the value 2"
        service.setStat("stat1", 2 as long)

        then: "I should be able to retrieve it"
        def value = service.getStat("stat1")

        and: "its should be an Long value"
        value.type == Long

        and: "its value should be 2"
        value.getLong() == 2
        Math.abs(value.getDouble() - 2.0f) < 0.0000001 // fixme there must be a built-in Spock thing to compare floating point values

    }

    def "incrementing a stat by an explicit value"() {
        when: "I set stat1 to the value 3.0"
        service.setStat("stat1", 3 as double)

        and: "then I increment stat1 by the value 2"
        service.incStat("stat1", 2 as long)

        then: "I should be able to retrieve it"
        def value = service.getStat("stat1")

        and: "its should be an Long value"
        value.type == Double

        and: "its value should be 5"
        value.getLong().equals(5L)
        Math.abs(value.getDouble() - 5.0f) < (0.0000001)

    }

    def "incrementing an int stat by 1"() {
        when: "I set stat1 to 3"
        service.setStat("stat1", 3 as long)

        and: "increment it"
        service.incStat("stat1")

        then: "I should get 4"
        service.getStat("stat1").getLong().equals(4L)
    }

    def "incrementing a Double stat by 1"() {
        when: "I set stat1 to 3.1"
        service.setStat("stat1", 3.1 as double)

        and: "increment it"
        service.incStat("stat1")

        then: "I should get 4"
        service.getStat("stat1").getLong().equals(4L)

        and: "I should get 4.1 as double"
        Math.round(service.getStat("stat1").getDouble() - 4.1) < (0.0000001)
    }

    def "incrementing a stat that wasn't there before"() {
        when: "I increment a stat that was not previously defined"
        service.incStat("new")

        then: "I should simply get the value I provided"
        service.getStat("new").getLong().equals(1L)
    }

    def "when there is no such thing"() {
        when: "I request a stat that did not exist"
        value = service.getStat("huh?")

        then: "I should get a null"
        value == null
    }

    def "simple timing measurement"() {
        when: "I start a timing measurement, wait a bit, and stop"
        service.startTimerFor("fetch")
        Thread.sleep(10)
        service.stopTimerFor("fetch")

        then: "I should have a timing stat for that timing"
        def value = service.getStat("fetch")
        value.isTiming()
        value.type == Long
        Math.abs(value.getLong() - 10) < 150 // fixme this cannot be accurate in a test environment...
    }

// looks like I have to implement it because for some reason I cannot pass a simple Closure and I don't really want to bother
//    public class TestRunnable implements Runnable {
//        public void run() {}
//    }

    def "timing a runnable"() {
        when: "I time a Runnable"
        service.measureTiming("runnable", {})

        then: "I should get a timing stat for it"
        def value = service.getStat("runnable")
        Math.abs(value.getLong() - 10) < 100
        value.isTiming()
    }

    def "pausing and resuming timing"() {
        given: "a session where measurement was paused before a long wait"

        def stat = "pause test"
        service.startTimerFor(stat)
        service.pauseTimerFor(stat)
        Thread.sleep(100)
        service.resumeTimerFor(stat)
        service.stopTimerFor(stat)

        expect: "the long wait should not show up in the timing"
        service.getStat(stat).getLong() < 20 // FIXME: this test is timing sensitive and should be fixed to be more deterministic
    }

    def "adding stuff when the service is off"() {
        given: "that I turn the service off"
        service.turnOff()

        and: "I add a stat"
        service.incStat("new stat")

        expect: "it should have no effect"
        service.getStat("new stat") == null

    }

    def "adding stuff when the service is off and then turning back on"() {
        given: "that I turn the service off"
        service.turnOff()

        and: "I add a stat"
        service.incStat("new stat")

        and: "that I turn the service on again"
        service.turnOn()

        and: "I add the stat"
        service.incStat("new stat")

        expect: "it should have no effect"
        service.getStat("new stat").getLong().equals(1L)
    }

}