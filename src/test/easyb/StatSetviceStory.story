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

import com.moscona.util.monitoring.stats.DoubleSampleAccumulator
import com.moscona.util.monitoring.stats.LongSampleAccumulator
import com.moscona.util.monitoring.stats.SimpleStatsService
import static com.moscona.test.easyb.TestHelper.*

description "a simple stats collection service, plus some of the supporting classes"

before_each "scenario", {
  given "a stats service", {
    service = new SimpleStatsService();
  }
  and "a test variable", { value = null }
}

scenario "immediately after creating", {
  then "it should be on", {
    service.isOn().shouldBe true
  }
}

scenario "setting a stat to an explicit value", {
  when "I set stat1 to the value 2", { service.setStat("stat1", 2 as long) }
  then "I should be able to retrieve it", { value = service.getStat("stat1") }
  and "its should be an Long value", { value.type.shouldBe Long }
  and "its value should be 2", {
    value.getLong().shouldBe 2
    Math.abs(value.getDouble() - 2.0f).shouldBeLessThan(0.0000001)
  }
}

scenario "setting a stat to an explicit value after setting it to another value", {
  when "I set stat1 to the value 3", { service.setStat("stat1", 3 as long) }
  and "then I set stat1 to the value 2", { service.setStat("stat1", 2 as long) }
  then "I should be able to retrieve it", { value = service.getStat("stat1") }
  and "its should be an Long value", { value.type.shouldBe Long }
  and "its value should be 2", {
    value.getLong().shouldBe 2
    Math.abs(value.getDouble() - 2.0f).shouldBeLessThan(0.0000001)
  }
}

scenario "incrementing a stat by an explicit value", {
  when "I set stat1 to the value 3.0", { service.setStat("stat1", 3 as double) }
  and "then I increment stat1 by the value 2", { service.incStat("stat1", 2 as long) }
  then "I should be able to retrieve it", { value = service.getStat("stat1") }
  and "its should be an Long value", { value.type.shouldBe Double }
  and "its value should be 5", {
    value.getLong().shouldBe 5
    Math.abs(value.getDouble() - 5.0f).shouldBeLessThan(0.0000001)
  }
}

scenario "incrementing an int stat by 1", {
  when "I set stat1 to 3", { service.setStat("stat1",3 as long) }
  and "increment it", { service.incStat("stat1") }
  then "I should get 4", { service.getStat("stat1").getLong().shouldBe 4 }
}

scenario "incrementing a Double stat by 1", {
  when "I set stat1 to 3.1", { service.setStat("stat1",3.1 as double) }
  and "increment it", { service.incStat("stat1") }
  then "I should get 4", { service.getStat("stat1").getLong().shouldBe 4 }
  and "I should get 4.1 as double", {
    Math.round(service.getStat("stat1").getDouble() - 4.1).shouldBeLessThan(0.0000001)
  }
}

scenario "incrementing a stat that wsn't there before", {
  when "I increment a stat that was not previously defined", { service.incStat("new") }
  then "I should simply get the value I provided", { service.getStat("new").getLong().shouldBe 1 }
}

scenario "when there is no such thing", {
  when "I request a stat that did not exist", { value = service.getStat("huh?") }
  then "I should get a null", { value.shouldBe null }
}

scenario "simple timing measurement", {
  when "I start a timing measurement, wait a bit, and stop", {
    service.startTimerFor("fetch")
    Thread.sleep(10)
    service.stopTimerFor("fetch")
  }
  then "I should have a timing stat for that timing", {
    value = service.getStat("fetch")
    value.isTiming().shouldBe true
    value.type.shouldBe Long
    Math.abs(value.getLong() - 10).shouldBeLessThan 150 // this cannot be accurate in a test environment...
  }
}

// looks like I have to implement it because for some reason I cannot pass a simple Closure and I don't really want to bother
public class TestRunnable implements Runnable {
  public void run() {}
}

scenario "timing a runnable", {
  when "I time a Runnable", {
    service.measureTiming("runnable", new TestRunnable())
  }
  then "I should get a timing stat for it", {
    value = service.getStat("runnable")
    Math.abs(value.getLong() - 10).shouldBeLessThan 100
    value.isTiming().shouldBe true
  }
}

scenario "pausing and resuming timing", {
  given "a session where measurement was paused before a long wait", {
    stat = "pause test"
    service.startTimerFor(stat)
    service.pauseTimerFor(stat)
    Thread.sleep(100)
    service.resumeTimerFor(stat)
    service.stopTimerFor(stat)
  }
  then "the long wait should not show up in the timing", {
    service.getStat(stat).getLong().shouldBeLessThan 20 // FIXME: this test is timing sensitive and should be fixed to be more deterministic
  }
}

scenario "adding stuff when the service is off", {
  given "that I turn the service off", { service.turnOff() }
  and "I add a stat", { service.incStat("new stat")}
  then "it should have no effect", { service.getStat("new stat").shouldBe null }
}

scenario "adding stuff when the service is off and then turning back on", {
  given "that I turn the service off", { service.turnOff() }
  and "I add a stat", { service.incStat("new stat")}
  given "that I turn the service on again", { service.turnOn() }
  and "I add the stat", { service.incStat("new stat")}
  then "it should have no effect", { service.getStat("new stat").getLong().shouldBe 1 }
}

description "sample accumulators"

before_each "sample accumulator scenario", {
  given "an long accumulator", { longAccumulator = new LongSampleAccumulator() }
  and "a vector of longs", {
    longVector = (0..10).collect{it as long}
  }
  and "a double accumulator", { doubleAccumulator = new DoubleSampleAccumulator() }
  and " a vector of doubles", {
    doubleVector = (0..10).collect{it*2+0.1 as double}
  }
  when "I feed the int vector", {
    longVector.each{ longAccumulator.addSample(it as long)}
  }
  when "I feed the double vector", {
    doubleVector.each{ doubleAccumulator.addSample(it as double)}
  }
}

scenario "counting", {
  then "the int accumulator should count 11", { longAccumulator.count.shouldBe 11 }
  and "the double accumulator should count 11", { doubleAccumulator.count.shouldBe 11 }
}

scenario "averaging", {
  then "the int accumulator should yield mean 5", { longAccumulator.mean().shouldBe 5 }
  and "the double accumulator should yield mean 10.1", { doubleAccumulator.mean().shouldBe 10.1 }
}

scenario "variance", {
  then "the int accumulator should yield variance 10", { Math.abs(10-(longAccumulator.variance())).shouldBeLessThan 0.00001 }
  and "the double accumulator should yield variance 40", { Math.abs(40-(doubleAccumulator.variance())).shouldBeLessThan 0.00001 }
}

scenario "stdev", {
  then "the int accumulator should yield stdev 3.162", { Math.abs(3.16227766-(longAccumulator.stdev())).shouldBeLessThan 0.00001 }
  and "the double accumulator should yield stdev 6.324", { Math.abs(6.32455532-(doubleAccumulator.stdev())).shouldBeLessThan 0.00001 }
}

scenario "min", {
  then "the int accumulator should yield min 0.0", { Math.abs(0.0 -(longAccumulator.min())).shouldBeLessThan 0.00001 }
  and "the double accumulator should yield min 0.1", { Math.abs(0.1-(doubleAccumulator.min())).shouldBeLessThan 0.00001 }
}

scenario "max", {
  then "the int accumulator should yield max 10.0", { Math.abs(10.0-(longAccumulator.max())).shouldBeLessThan 0.00001 }
  and "the double accumulator should yield max 20.1", { Math.abs(20.1-(doubleAccumulator.max())).shouldBeLessThan 0.00001 }
}


scenario "clear()", {
  when "clear() is called, and afeter creating a frech instance", {
    doubleAccumulator.clear()
    longAccumulator.clear()
  }
  then "all metrics, including min and max reset to 0", {
    newLongAccumulator = new LongSampleAccumulator()
    newDoubleAccumulator = new DoubleSampleAccumulator()

    ["count","min","max","sum","mean","stdev","variance"].each { m->
      ("longAccumulator.$m="+(longAccumulator."$m"() as double)).shouldBe "longAccumulator.$m=0.0"
      ("doubleAccumulator.$m="+(doubleAccumulator."$m"() as double)).shouldBe "doubleAccumulator.$m=0.0"
      ("newLongAccumulator.$m="+(newLongAccumulator."$m"() as double)).shouldBe "newLongAccumulator.$m=0.0"
      ("newDoubleAccumulator.$m="+(newDoubleAccumulator."$m"() as double)).shouldBe "newDoubleAccumulator.$m=0.0"
    }
  }
}

description "usage of sample accumulators in stats service and the associated stats values"

scenario "using addTimingSampleFor() results in having descriptive stats on the timing value", {
  given "that the service is on", { service.turnOn() }
  when "I call addTimingSampleFor(statName)", {
    service.addTimingSampleFor("timing",10l)
  }
  then "the resulting stat value will have descriptive stats", {
    service.getStat("timing").descriptiveStatistics.shouldNotBe null
  }
}

scenario "using addTimingSampleFor() several times and obtaining timing statistics", {
  given "that the service is on", { service.turnOn() }
  when "I call addTimingSampleFor(statName)", {
    service.addTimingSampleFor("timing",210)
    service.addTimingSampleFor("timing",211)
    service.addTimingSampleFor("timing",212)
  }
  then "the resulting stat value will have descriptive stats", {
    service.getStat("timing").descriptiveStatistics.shouldNotBe null
  }
  and "it will count 3 values", {
    service.getStat("timing").descriptiveStatistics.count.shouldBe 3
  }
  and "the mean would be 11", {
    service.getStat("timing").descriptiveStatistics.mean().shouldBe 211.0
  }
}

scenario "Attaching descriptive statistics to an arbitrary stat", {
  given "that I attach descriptive statistics to myCounter when I initialize it", {
    service.initStatWithDescriptiveStats("myCounter",1 as int)
    service.initStatWithDescriptiveStats("myDouble",1.0 as double)
  }
  when "I increment it by specific values", {
    service.incStat("myCounter")
    service.incStat("myDouble")
    service.incStat("myCounter", 4)
    service.incStat("myDouble", 4.0 as double)
  }
  then "I should be able to get descriptive stats on myCounter", {
    def stat = service.getStat("myCounter")
    stat.getType().shouldBe Long.class
    stat.getLong().shouldBe 6
    stat.descriptiveStatistics.shouldNotBe null
    stat.descriptiveStatistics.mean().shouldBe 2
  }
  and "I should be able to get descriptive stats on myDouble", {
    def stat = service.getStat("myDouble")
    stat.getType().shouldBe Double.class
    stat.getDouble().shouldBe 6.0
    stat.descriptiveStatistics.shouldNotBe null
    stat.descriptiveStatistics.mean().shouldBe 2.0
  }
}

// todo descriptive statistics: count, mean, sum, sum of squares, min, max : IT-64
