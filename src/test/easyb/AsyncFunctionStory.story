description "support for making synchronous calls with a return value over an asynchronous medium"

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

import static com.moscona.test.easyb.TestHelper.*

import com.moscona.util.async.AsyncFunctionCall
import com.moscona.util.async.AsyncFunctionFutureResults
import com.moscona.util.testing.TestCoordinator

import java.util.concurrent.TimeUnit

class MyTestFunctionCall extends AsyncFunctionCall<String> {
  String arg;
  int callCounter;

  public MyTestFunctionCall(String retval) {
    super();
    this.arg = retval;
    callCounter = 0;
  }

  void asyncCall() {
    callCounter++;
  }

  protected String computeArgumentsSignature() {
    return arg;
  }
}

before_each "scenario", {
  given "a result placeholder", { result = null }
  and "and a coordinator", {
    coord = new TestCoordinator()
    coord2 = new TestCoordinator()
  }

  given "an asynchronous function call", {
    function = new MyTestFunctionCall("value")
  }
  and "a future results store", {
    myResults = new AsyncFunctionFutureResults()
    function.use(myResults)
  }
  and "a brittle timing parameter for delays", {
    brittleDelay = 500
  }
}




if (shouldRunUnsafeScenarios()) scenario "creating an asynch function call", {
  when "I make the call", {
    call = {
      result = function.call()
      coord.finishTest()
    }
    new Thread(call).start()
  }
  and "produce a result", {
    function.returnToCallers("return $function.arg")
  }
  and "wait a tiny little bit", {
    coord.delayTestFinish(brittleDelay)
  }
  then "the call will return and produce the result (1)", {
    result.shouldBe "return value"
  }
  and "the async call should execute once", {
    function.callCounter.shouldBe 1
  }
}

scenario "creating two asynch function calls with different argument signatures", {
  given "two async function calls with different signatures", {
    f1 = new MyTestFunctionCall("value 1").use(myResults)
    f2 = new MyTestFunctionCall("value 2").use(myResults)
    tag1="'f1 did not return'"
    tag2="'f2 did not return'"
  }
  when "I make the calls", {
    call = {
      tag1 = f1.call()
      tag2 = f2.call()
      coord.finishTest()
    }
    new Thread(call).start()
  }
  and "produce a result for the first one via the results store", {
    myResults.awaitCallers(f1.computeArgumentsSignature(),100,2000)
    myResults.returnToCallers(f1.computeArgumentsSignature(), "return $f1.arg")
  }
  and "wait a tiny little bit", {
    sleep(50)
  }
  then "the call will return and produce the result (2)", {
    tag1.shouldBe "return value 1"
  }
  and "the other call would not have returned yet", {
    tag2.shouldBe "'f2 did not return'"
  }
  and "when I produce the second result the other call returns and assigns the result", {
    myResults.awaitCallers(f2.computeArgumentsSignature(),100,2000)
    myResults.returnToCallers(f2.computeArgumentsSignature(), "return $f2.arg")
    coord.delayTestFinish(brittleDelay)
    tag2.shouldBe "return value 2"
  }
}

scenario "creating two asynch function call with the same argument signature", {
  given "two async function calls with the same signatures", {
    f1 = new MyTestFunctionCall("value").use(myResults)
    f2 = new MyTestFunctionCall("value").use(myResults)
    tag1="'f1 did not return'"
    tag2="'f2 did not return'"
  }
  when "I make the calls", {
    call = {
      tag1 = f1.call()
      coord.finishTest()
    }
    new Thread(call).start()
    call = {
      // separate thread - otherwise the calls get serialized
      tag2 = f2.call()
      coord2.finishTest()
    }
    new Thread(call).start()
  }
  and "produce a result for one", {
    sleep(500)
    f1.returnToCallers("return value")
  }
  and "wait a tiny little bit", {
    f1.awaitReturn()
    f2.awaitReturn()
    coord.delayTestFinish(brittleDelay)
    coord2.delayTestFinish(brittleDelay)
  }
  then "the both calls will return and produce the same result", {
    tag1.shouldBe "return value"
    tag2.shouldBe "return value"
  }
  and "the f1 async call should execute once", {
    f1.callCounter.shouldBe 1
  }
//  and "the f2 async call should not have happened", {
//    f2.callCounter.shouldBe 0
//  }
}

scenario "a call timing out", {
  given "an exception placeholder", {
    ex = null
  }
  when "I make the call", {
    call = {
      try {
        result = function.call(10, TimeUnit.MILLISECONDS)
      } catch (Exception e) {
        ex = e
      }
      coord.finishTest()
    }
    new Thread(call).start()
    sleep(500)
  }
  and "wait too long", {
    sleep(50)
  }
  and "produce a result", {
    function.returnToCallers("return $function.arg")
  }
  and "wait a tiny little bit", {
    coord.delayTestFinish(brittleDelay)
  }
  then "the call will throw a timeout exception", {
    ex.getClass().name.shouldBe "java.util.concurrent.TimeoutException"
  }
  and "the exception should have a meanungful message", {
    ex.message.shouldBe "Timeout while waiting for return value in MyTestFunctionCall(value)"
  }
}

scenario "a call producing an exception", {
  given "an exception placeholder", {
    function = new MyTestFunctionCall("should blow up")
    function.use(myResults)
    ex = null
  }
  when "I make the call", {
    call = {
      try {
        result = function.call(1000, TimeUnit.MILLISECONDS)
      } catch (Exception e) {
        ex = e
      }
      finally {
        coord.finishTest()
      }
    }
    new Thread(call).start()
  }
  and "produce an exception before producing a result", {
    ensureDoesNotThrow(Exception) {
      myResults.awaitCallers(function.computeArgumentsSignature(), 100,2000)
      myResults.throwException(function.computeArgumentsSignature(), new Exception("boom!"))
      function.returnToCallers("return $function.arg")
    }
  }
  and "wait a tiny little bit", {
    coord.delayTestFinish(brittleDelay)
  }
  then "the call will throw an exception", {
    ex.getClass().name.shouldBe "java.util.concurrent.ExecutionException"
  }
  and "the reason for it will be the exception that was originally thrown", {
    ex.cause.message.shouldBe "boom!"
  }
  and "the exception should have the right message", {
    ex.message.shouldBe "Exception while executing asynchronous function MyTestFunctionCall(should blow up) :java.lang.Exception: boom!"
  }
}

scenario "cancelling all pending calls", {
  given "two async function calls with different signatures", {
    f1 = new MyTestFunctionCall("value 1").use(myResults)
    f2 = new MyTestFunctionCall("value 2").use(myResults)
    tag1="'f1 did not return'"
    tag2="'f2 did not return'"
  }
  when "I make the calls", {
    call = {
      tag1 = f1.call()
      coord.finishTest()
    }
    new Thread(call).start()
    call2 = {
      tag2 = f2.call()
      coord2.finishTest()
    }
    new Thread(call2).start()
  }
  and "cancel the calls before they are done", {
    myResults.awaitCallers(f2.computeArgumentsSignature(),100,2000)
    myResults.cancelAll(f2.computeArgumentsSignature())
  }
  and "wait a tiny little bit", {
    myResults.awaitCallers(f1.computeArgumentsSignature(),100,2000)
    sleep(50)
  }
  and "produce a result for the first one via the results store", {
    myResults.returnToCallers(f1.computeArgumentsSignature(), "return $f1.arg")
  }
  then "the call will return and produce the result (3)", {
    f1.awaitReturn()
    // Fail count: 6
    tag1.shouldBe "return value 1"
  }
  and "the other call should return null", {
    tag2.shouldBe null
  }
  and "the second function should report it was canceled", {
    f2.isCanceled().shouldBe true
  }
}

if (shouldRunUnsafeScenarios()) scenario "retrying all pending calls", {
  when "I make the call", {
    call = {
      result = function.call()
      coord.finishTest()
    }
    new Thread(call).start()
  }
  and "issue a retry before producing a result", {
    myResults.retryAllCalls()
  }
  and "produce a result", {
    function.returnToCallers("return $function.arg")
  }
  and "wait a tiny little bit", {
    coord.delayTestFinish(brittleDelay)
  }
  then "the call will return and produce the result (4)", {
    result.shouldBe "return value"
  }
  and "the async call should execute once", {
    function.callCounter.shouldBe 2
  }
}
