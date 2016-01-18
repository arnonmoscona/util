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

import com.moscona.test.util.TestNumberHelperCategory
import org.assertj.core.data.Offset
import spock.lang.*
import static spock.util.matcher.HamcrestMatchers.closeTo

/**
 * Created by Arnon Moscona on 12/4/2015.
 *
 */
@Subject(LongSampleAccumulator)
@Title("Specification for sample accumulators: LongSampleAccumulator, DoubleSampleAccumulator")

@Narrative("""
This specification replaces parts of the EasyB story StatsSetviceStory.story

It was written as part of migrating all EasyB stories to Spock.
As such, it is not always idiomatic.
""")

class SampleAccumulatorsSpec extends Specification {
    public static final Offset<BigDecimal> DEFAULT_DOUBLE_OFFSET = Offset.offset(0.00001)
    LongSampleAccumulator longAccumulator
    DoubleSampleAccumulator doubleAccumulator
    SimpleStatsService service
    def longVector
    def doubleVector

    def setup() {
        Number.mixin(TestNumberHelperCategory)

        given: "an long accumulator"
        longAccumulator = new LongSampleAccumulator()

        and: "a vector of longs"
        longVector = (0..10).collect { it as long }

        and: "a double accumulator"
        doubleAccumulator = new DoubleSampleAccumulator()

        and: " a vector of doubles"
        doubleVector = (0..10).collect { it * 2 + 0.1 as double }

        and: "I feed the int vector"
        longVector.each { longAccumulator.addSample(it as long) }

        and: "I feed the double vector"
        doubleVector.each { doubleAccumulator.addSample(it as double) }

        and: "a SimpleStatsService"
        service = new SimpleStatsService()

    }

    def "counting"() {
        expect: "the int accumulator should count 11"
        longAccumulator.count.equals(11L)

        and: "the double accumulator should count 11"
        doubleAccumulator.count.equals(11L)
    }

    def "averaging"() {
        expect: "the int accumulator should yield mean 5"
        longAccumulator.mean().isCloseTo(5.0, DEFAULT_DOUBLE_OFFSET)

        and: "the double accumulator should yield mean 10.1"
        doubleAccumulator.mean().isCloseTo(10.1, DEFAULT_DOUBLE_OFFSET)
    }

    def "variance"() {
        expect: "the int accumulator should yield variance 10"
 Math.abs(10-(longAccumulator.variance())) < 0.00001

        and: "the double accumulator should yield variance 40"
 Math.abs(40-(doubleAccumulator.variance())) < 0.00001

    }

    def "stdev"() {
        expect: "the int accumulator should yield stdev 3.162"
 Math.abs(3.16227766-(longAccumulator.stdev())) < 0.00001

        and: "the double accumulator should yield stdev 6.324"
 Math.abs(6.32455532-(doubleAccumulator.stdev())) < 0.00001

    }

    def "min"() {
        expect: "the int accumulator should yield min 0.0"
 Math.abs(0.0 -(longAccumulator.min())) < 0.00001

        and: "the double accumulator should yield min 0.1"
 Math.abs(0.1-(doubleAccumulator.min())) < 0.00001

    }

    def "max"() {
        expect: "the int accumulator should yield max 10.0"
 Math.abs(10.0-(longAccumulator.max())) < 0.00001

        and: "the double accumulator should yield max 20.1"
 Math.abs(20.1-(doubleAccumulator.max())) < 0.00001

    }


    def "clear()"() {
        when: "clear() is called, and after creating a fresh instance"
        doubleAccumulator.clear()
        longAccumulator.clear()

        then: "all metrics, including min and max reset to 0"

        def newLongAccumulator = new LongSampleAccumulator()
        def newDoubleAccumulator = new DoubleSampleAccumulator()

        ["count","min","max","sum","mean","stdev","variance"].each { m->
            ("longAccumulator.$m="+(longAccumulator."$m"() as double)).equals "longAccumulator.$m=0.0".toString()
            ("doubleAccumulator.$m="+(doubleAccumulator."$m"() as double)).equals "doubleAccumulator.$m=0.0".toString()
            ("newLongAccumulator.$m="+(newLongAccumulator."$m"() as double)).equals "newLongAccumulator.$m=0.0".toString()
            ("newDoubleAccumulator.$m="+(newDoubleAccumulator."$m"() as double)).equals "newDoubleAccumulator.$m=0.0".toString()
        }
    }

//    def "usage of sample accumulators in stats service and the associated stats values"()

    def "using addTimingSampleFor() results in having descriptive stats on the timing value"() {
        given: "that the service is on"
        service.turnOn()

        when: "I call addTimingSampleFor(statName)"
        service.addTimingSampleFor("timing",10l)

        then: "the resulting stat value will have descriptive stats"
        service.getStat("timing").descriptiveStatistics != null
    }

    def "using addTimingSampleFor() several times and obtaining timing statistics"() {
        given: "that the service is on"
        service.turnOn()

        when: "I call addTimingSampleFor(statName)"
            service.addTimingSampleFor("timing",210)
            service.addTimingSampleFor("timing",211)
            service.addTimingSampleFor("timing",212)

        then: "the resulting stat value will have descriptive stats"
            service.getStat("timing").descriptiveStatistics != null

        and: "it will count 3 values"
            service.getStat("timing").descriptiveStatistics.count.equals(3L)

        and: "the mean would be 11"
            service.getStat("timing").descriptiveStatistics.mean().isCloseTo(211.0, DEFAULT_DOUBLE_OFFSET)

    }

    def "Attaching descriptive statistics to an arbitrary stat"() {
        given: "that I attach descriptive statistics to myCounter when I initialize it"

        service.initStatWithDescriptiveStats("myCounter", 1 as int)
        service.initStatWithDescriptiveStats("myDouble", 1.0 as double)

        when: "I increment it by specific values"
        service.incStat("myCounter")
        service.incStat("myDouble")
        service.incStat("myCounter", 4)
        service.incStat("myDouble", 4.0 as double)

        then: "I should be able to get descriptive stats on myCounter"

        def stat = service.getStat("myCounter")
        stat.getType() == Long
        stat.getLong().equals(6L)
        stat.descriptiveStatistics != null
        stat.descriptiveStatistics.mean().isCloseTo(2.0, DEFAULT_DOUBLE_OFFSET)

        and: "I should be able to get descriptive stats on myDouble"

        def stat2 = service.getStat("myDouble")
        stat2.getType() == Double
        stat2.getDouble().isCloseTo(6.0, DEFAULT_DOUBLE_OFFSET)
        stat2.descriptiveStatistics != null
        stat2.descriptiveStatistics.mean().isCloseTo(2.0, DEFAULT_DOUBLE_OFFSET)
    }
}