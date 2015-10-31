package com.moscona.util.async.furtures; 

import com.moscona.util.functions.impure.Action0;
import com.moscona.util.functions.impure.Function0;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.*;

/** 
 * Futures Tester. 
 * 
 * @author Arnon Moscona 
 * @since <pre>Oct 23, 2015</pre> 
 * @version 1.0 
 */ 
public class FuturesTest { 
    SoftAssertions soft = new SoftAssertions();
    ExecutorService executor = null;
    AtomicReference<String> evidence = new AtomicReference<>("not set");
    CountDownLatch signal = new CountDownLatch(1);

    @Before
    public void before() throws Exception {
        soft = new SoftAssertions();
        executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "custom"));
        evidence.set("not set");
        signal = new CountDownLatch(1);
    } 

    @After
    public void after() throws Exception { 
    } 

    /** 
     * 
     * Method: newEagerAsyncCompletableFuture(Function0<T> function)
     * 
     */ 
    @Test
    public void testNewAsyncCompletableFutureFunction() throws Exception {
        CompletionStage<String> future = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("good result"));
        String r = ((CompletableFuture<String>)future).get();
        assertThat(r).isEqualTo("good result");
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Function0<T> function)
     * Throwing exception
     *
     */
    @Test
    public void testNewAsyncCompletableFutureFunctionException() throws Exception {
        Throwable exception = null;
        CompletionStage<String> future = null;

        try {
                future = Futures.newEagerAsyncCompletableFuture((Function0<String>) () -> {
                        throw new Exception("exception");
                    }
            );
            String r = ((CompletableFuture<String>)future).get();
        } catch (Exception e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception.getClass().getSimpleName()).isEqualTo("ExecutionException");
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("exception");
        soft.assertThat(((CompletableFuture<String>)future).isCompletedExceptionally()).isTrue();

        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Function0<T> function)
     * Throwing RuntimeException (should behave the same as Exception)
     *
     */
    @Test
    public void testNewAsyncCompletableFutureFunctionRuntimeException() throws Exception {
        Throwable exception = null;
        CompletionStage<String> future = null;

        try {
            future = Futures.newEagerAsyncCompletableFuture((Function0<String>) () -> {
                        throw new RuntimeException("exception");
                    }
            );
            String r = ((CompletableFuture<String>)future).get();
        } catch (Exception e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception.getClass().getSimpleName()).isEqualTo("ExecutionException");
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("exception");
        soft.assertThat(((CompletableFuture<String>)future).isCompletedExceptionally()).isTrue();

        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Function0<T> function)
     * Constructing with null
     *
     */
    @Test(expected = AssertionError.class)
    public void testNewAsyncCompletableFutureFunctionNull() throws Exception {
        Futures.newEagerAsyncCompletableFuture((Function0<String>)null);
    }

    /** 
     * 
     * Method: newEagerAsyncCompletableFuture(Executor executor, Function0<T> function)
     * 
     */ 
    @Test
    public void testNewAsyncCompletableFutureForExecutorFunction() throws Exception {
        CompletionStage<String> future = Futures.newEagerAsyncCompletableFuture(executor, ()-> {
            evidence.set(Thread.currentThread().getName());
            return "good result";
        });
        String r = ((CompletableFuture<String>)future).get();

        soft.assertThat(r).isEqualTo("good result");
        soft.assertThat(evidence.get()).isEqualTo("custom");
        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Executor executor, Function0<T> function)
     * Throwing exception
     */
    @Test
    public void testNewAsyncCompletableFutureForExecutorFunctionException() throws Exception {
        CompletionStage<String> future = Futures.newEagerAsyncCompletableFuture(executor, (Function0<String>) ()-> {
            evidence.set(Thread.currentThread().getName());
            throw new RuntimeException("boom!");
        });

        Throwable exception = null;
        try {
            ((CompletableFuture<String>) future).get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(evidence.get()).isEqualTo("custom");
        soft.assertThat(((CompletableFuture<String>)future).isCompletedExceptionally()).isTrue();

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom!");
        soft.assertThat(exception.getClass().getSimpleName()).isEqualTo("ExecutionException");
        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Executor executor, Function0<T> function)
     * Constructing with null
     */
    @Test(expected = AssertionError.class)
    public void testNewAsyncCompletableFutureForExecutorFunctionNull() throws Exception {
        Futures.newEagerAsyncCompletableFuture(executor, (Function0<String>) null);
    }

    /** 
     * 
     * Method: newEagerAsyncCompletableFuture(Action0 action)
     * 
     */ 
    @Test
    public void testNewAsyncCompletableFutureAction() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(() -> evidence.set("action done"));

        ((CompletableFuture<?>)future).get(); // wait to finish

        assertThat(evidence.get()).isEqualTo("action done");
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Action0 action)
     * Throwing exception
     */
    @Test
    public void testNewAsyncCompletableFutureActionException() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture((Action0) () -> {
            throw new Exception("boom!");
        });

        Throwable exception = null;
        try {
            ((CompletableFuture<?>)future).get(); // wait to finish
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(((CompletableFuture<String>)future).isCompletedExceptionally()).isTrue();

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom!");
        soft.assertThat(exception.getClass().getSimpleName()).isEqualTo("ExecutionException");
        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Action0 action)
     * Constructing with null
     */
    @Test(expected = AssertionError.class)
    public void testNewAsyncCompletableFutureActionNull() throws Exception {
        Futures.newEagerAsyncCompletableFuture((Action0) null);
    }

    /** 
     * 
     * Method: newEagerAsyncCompletableFuture(Executor executor, Action0 action)
     * 
     */ 
    @Test
    public void testNewAsyncCompletableFutureForExecutorAction() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(executor, ()-> {
            evidence.set(Thread.currentThread().getName());
        });

        ((CompletableFuture<?>)future).get();

        soft.assertThat(evidence.get()).isEqualTo("custom");
        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Executor executor, Action0 action)
     * Throwing exception
     */
    @Test
    public void testNewAsyncCompletableFutureForExecutorActionException() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(executor, (Action0) ()-> {
            evidence.set(Thread.currentThread().getName());
            throw new RuntimeException("boom!");
        });

        Throwable exception = null;
        try {
            ((CompletableFuture<?>) future).get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(evidence.get()).isEqualTo("custom");
        soft.assertThat(((CompletableFuture<String>)future).isCompletedExceptionally()).isTrue();

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom!");
        soft.assertThat(exception.getClass().getSimpleName()).isEqualTo("ExecutionException");
        soft.assertAll();
    }

    /**
     *
     * Method: newEagerAsyncCompletableFuture(Executor executor, Action0 action)
     * Constructing with Null
     */
    @Test(expected = AssertionError.class)
    public void testNewAsyncCompletableFutureForExecutorActionNull() throws Exception {
        Futures.newEagerAsyncCompletableFuture(executor, (Action0) null);
    }

    /**
     * Tests construction with a null
     */
    @Test(expected = AssertionError.class)
    public void testExtendedCompletionStageNull() throws Exception {
        Futures.extend(null);
    }

    //<editor-fold desc="Future tests">
    //<editor-fold desc="extended: get()">
    /**
     * Tests the ExtendedCompletionStage get() functionality
     */
    @Test
    public void testExtendedCompletionStageGetNormal() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called"));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.get();

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests the ExtendedCompletionStage get() functionality
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageGetException() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called")
        .afterRunning(brokenAction("boom")));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.get();
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(ExecutionException.class);
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }

    /**
     * Tests ExtendedCompletionStageGetNonCF : when the delegate is not a CompletableFuture
     */
    @Test
    public void testExtendedCompletionStageGetNonCFNormal() throws Exception {
        CompletionStage<String> stage = makeNonCompletableFutureCompletionStage("called");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.get();

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests get() : when the delegate is not a CompletableFuture
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageGetNonCFException() throws Exception {
        CompletionStage<String> stage = makeBrokenNonCompletableFutureCompletionStage("boom");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.get();
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(ExecutionException.class);
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }
    //</editor-fold>

    //<editor-fold desc="extended: get(timeout)">

    /**
     * Tests the ExtendedCompletionStage get(timeout) functionality
     * passing null units
     */
    @Test(expected = AssertionError.class)
    public void testExtendedCompletionStageGetTimeoutNull() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called"));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        extended.get(100, null);
    }

    /**
     * Tests the ExtendedCompletionStage get(timeout) functionality
     */
    @Test
    public void testExtendedCompletionStageGetTimeoutNormal() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called"));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.get(5, TimeUnit.SECONDS);

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests the ExtendedCompletionStage get(timeout) functionality
     * timing out
     */
    @Test(expected = TimeoutException.class)
    public void testExtendedCompletionStageGetTimeoutTimeoutException() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(slowFunction("call", 1, TimeUnit.SECONDS));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        extended.get(5, TimeUnit.MILLISECONDS);
    }


    /**
     * Tests the ExtendedCompletionStage get() functionality
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageGetTimeoutWithException() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called")
                .afterRunning(brokenAction("boom")));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.get(5, TimeUnit.SECONDS);
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ExecutionException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }

    /**
     * Tests ExtendedCompletionStageGetNonCF : when the delegate is not a CompletableFuture
     */
    @Test
    public void testExtendedCompletionStageGetTimeoutNonCFNormal() throws Exception {
        CompletionStage<String> stage = makeNonCompletableFutureCompletionStage("called");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.get(1, TimeUnit.SECONDS);

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests ExtendedCompletionStageGetNonCF : when the delegate is not a CompletableFuture
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageGetNonCFTimeoutWithException() throws Exception {
        CompletionStage<String> stage = makeBrokenNonCompletableFutureCompletionStage("boom");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.get(1, TimeUnit.SECONDS);
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(ExecutionException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }

    //</editor-fold>

    //<editor-fold desc="extended: cancel(), isCanceled()">

    /**
     * Tests cancel() when interruption is allowed
     */
    @Test
    public void testCancelWithInterrupt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ExtendedCompletionStage<String> stage = Futures.extend(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", latch)));
        soft.assertThat(evidence.get()).isEqualTo("not set");
        soft.assertThat(stage.isCancelled()).isFalse();

        boolean response = stage.cancel(true);
        soft.assertThat(response).isTrue();

        soft.assertThat(evidence.get()).isEqualTo("not set"); // interrupt not allowed
        latch.countDown(); // can now complete
        Throwable exception = null;

        try {
            stage.get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(CancellationException.class);

        String result = evidence.get();
        soft.assertThat(result).isIn("not set", "interrupted");
        soft.assertThat(stage.isCancelled()).isTrue();

        soft.assertAll();
    }

    /**
     * Tests cancel() long task with no interrupt allowed
     */
    @Test
    public void testCancelLongWithNoInterrupt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ExtendedCompletionStage<String> stage = Futures.extend(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", latch)));
        soft.assertThat(evidence.get()).isEqualTo("not set");
        soft.assertThat(stage.isCancelled()).isFalse();

        boolean response = stage.cancel(false);
        soft.assertThat(response).isTrue();

        soft.assertThat(evidence.get()).isEqualTo("not set"); // interrupt not allowed
        latch.countDown(); // can now complete
        Throwable exception = null;

        try {
            stage.get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(CancellationException.class);
        String result = evidence.get();
        soft.assertThat(result).isIn("not set", "done");
        soft.assertThat(stage.isCancelled()).isTrue();
        soft.assertAll();
    }

    /**
     * Tests cancel() with interrupt - when delegate is not a CompletableFuture
     */
    @Test//(expected = Throwable.class) // delegate does not have a cancel method
    public void testCancelNonCFWithInterrupt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        ExtendedCompletionStage<String> stage = Futures.extend(makeNonCompletableFutureCompletionStage(functionAwaitingSignal("done", latch)));

        soft.assertThat(evidence.get()).isEqualTo("not set");
        soft.assertThat(stage.isCancelled()).isFalse();

        boolean response = stage.cancel(true);
        soft.assertThat(response).isTrue();

        soft.assertThat(evidence.get()).isEqualTo("not set"); // interrupt not allowed
        latch.countDown(); // can now complete
        Throwable exception = null;

        try {
            stage.get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(CancellationException.class);

        String result = evidence.get();
        soft.assertThat(result).isIn("not set", "interrupted");
        soft.assertThat(stage.isCancelled()).isTrue();

        soft.assertAll();
//        assertThat(evidence.get()).isEqualTo("not set");
//
//        stage.cancel(true); // should blow up here
    }

    /**
     * Tests cancel() with interrupt - when delegate is not a CompletableFuture but has a cancel method
     */
    @Test
    public void testCancelNonCFCancellableWithInterrupt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        TestingStageWithCancel<String> cs = new TestingStageWithCancel<>(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", latch)));
        ExtendedCompletionStage<String> stage = Futures.extend(cs);
        soft.assertThat(evidence.get()).isEqualTo("not set");
        soft.assertThat(stage.isCancelled()).isFalse();

        stage.cancel(true);

        soft.assertThat(evidence.get()).isEqualTo("not set"); // interrupt not allowed
        latch.countDown(); // can now complete
        Throwable exception = null;

        try {
            stage.get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull().as("expected a cancellation exception");
        soft.assertThat(exception).isInstanceOf(CancellationException.class).as("expected a cancellation exception");

//        soft.assertThat(evidence.get()).isEqualTo("not set"); // this is unreliable and does not seem to be required in CompletableFuture either
//        soft.assertThat(cs.isCanceled()).isTrue().as("cs should have been canceled"); // canceled was called
//        soft.assertThat(cs.getMayInterruptIfRunningSet().get()).isTrue(); // the parameter to cancel was correct
        soft.assertThat(stage.isCancelled()).isTrue();

        soft.assertAll();
    }

    /**
     * Tests cancel() long running with no interrupt - when delegate is not a CompletableFuture
     */
    @Test
    public void testCancelNonCFLongWithNoInterrupt() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        TestingStageWithCancel<String> cs = new TestingStageWithCancel<>(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", latch)));
        ExtendedCompletionStage<String> stage = Futures.extend(cs);
        soft.assertThat(evidence.get()).isEqualTo("not set");
        soft.assertThat(stage.isCancelled()).isFalse();

        stage.cancel(false);

        soft.assertThat(evidence.get()).isEqualTo("not set"); // interrupt not allowed
        latch.countDown(); // can now complete
        Throwable exception = null;

        try {
            stage.get();
        }
        catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(exception).isNotNull().as("expected a cancellation exception");
        soft.assertThat(exception).isInstanceOf(CancellationException.class).as("expected a cancellation exception");

        //soft.assertThat(evidence.get()).isEqualTo("not set"); // this is unreliable and does not seem to be required in CompletableFuture either
//        soft.assertThat(cs.isCanceled()).isTrue().as("cs should have been canceled"); // canceled was called
//        soft.assertThat(cs.getMayInterruptIfRunningSet().get()).isFalse(); // the parameter to cancel was correct
        soft.assertThat(stage.isCancelled()).isTrue();

        soft.assertAll();
    }
    //</editor-fold>

    //<editor-fold desc=" extended: isDone()">
    /**
     * Tests isDone() when finished normally
     */
    @Test
    public void testIsDoneNormalTermination() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(() -> "done");
        ExtendedCompletionStage<?> extended = Futures.extend(future);

        // wait until done
        extended.get();

        boolean isDone = extended.isDone();
        assertThat(isDone).isTrue().as("expected future to be done");
    }

    /**
     * Tests isDone() when terminated exceptionally
     */
    @Test
    public void testIsDoneExceptionalTermination() throws Exception {
        CompletionStage<?> future = makeBrokenCompletionStage("boom!");
        ExtendedCompletionStage<?> extended = Futures.extend(future);

        // wait until done
        try {
            extended.get();
        } catch (Throwable e) {
            // do nothing
        }

        boolean isDone = extended.isDone();
        assertThat(isDone).isTrue().as("expected future to be done");
    }

    /**
     * Tests isDone() when not finished
     */
    @Test
    public void testIsDoneNotFinished() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal));
        ExtendedCompletionStage<?> extended = Futures.extend(future);

        boolean isDone = extended.isDone();
        soft.assertThat(isDone).isFalse().as("expecting future not done yet");
        signal.countDown();

        // wait until done
        extended.get();

        soft.assertThat(extended.isDone()).isTrue().as("expected future to be done");
        soft.assertAll();
    }

    /**
     * Tests isDone() when finished normally
     * Not a CompletableFuture
     */
    @Test
    public void testIsDoneNormalTerminationNonCF() throws Exception {
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(() -> "done");
        ExtendedCompletionStage<?> extended = Futures.extend(new TestingStage<>(future));

        // wait until done
        extended.get();

        boolean isDone = extended.isDone();
        assertThat(isDone).isTrue().as("expected future to be done");
    }

    /**
     * Tests isDone() when terminated exceptionally
     * Not a CompletableFuture
     */
    @Test
    public void testIsDoneExceptionalTerminationNonCF() throws Exception {
        CompletionStage<?> future = makeBrokenCompletionStage("boom!");
        ExtendedCompletionStage<?> extended = Futures.extend(new TestingStage<>(future));

        // wait until done
        try {
            extended.get();
        } catch (Throwable e) {
            // do nothing
        }

        boolean isDone = extended.isDone();
        assertThat(isDone).isTrue().as("expected future to be done");
    }

    /**
     * Tests isDone() when not finished
     * Not a CompletableFuture
     */
    @Test
    public void testIsDoneNotFinishedNonCF() throws Exception {
        assert signal.getCount() == 1: "signal must be at 1 was at "+signal.getCount();

        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal));
        ExtendedCompletionStage<?> extended = Futures.extend(new TestingStage<>(future));

        boolean isDone = extended.isDone();
        soft.assertThat(isDone).isFalse().as("expecting future not done yet");
        signal.countDown();

        // wait until done
        extended.get();

        soft.assertThat(extended.isDone()).isTrue().as("expected future to be done");
        soft.assertAll();
    }
    //</editor-fold>
    //</editor-fold>

    //<editor-fold desc="test methods in CompletableFuture but not in Future or CompletionStage">
    //<editor-fold desc="extended: join()">
    /**
     * Tests the ExtendedCompletionStage join() functionality
     */
    @Test
    public void testExtendedCompletionStageJoinNormal() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called"));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.join().toString(); // FIXME why is it not returning String anyway?

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests the ExtendedCompletionStage join() functionality
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageJoinException() throws Exception {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("called")
                .afterRunning(brokenAction("boom")));
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.join().toString(); // FIXME why not returning String anyway?
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        soft.assertThat(exception).isInstanceOf(CompletionException.class);
        soft.assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }

    /**
     * Tests join() : when the delegate is not a CompletableFuture
     */
    @Test
    public void testExtendedCompletionStageJoinNonCFNormal() throws Exception {
        CompletionStage<String> stage = makeNonCompletableFutureCompletionStage("called");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = extended.join().toString(); // FIXME why not returning String anyway?

        soft.assertThat(result).isEqualTo("called");
        soft.assertThat(extended);
        soft.assertAll();
    }

    /**
     * Tests join() : when the delegate is not a CompletableFuture
     * throwing exception
     */
    @Test
    public void testExtendedCompletionStageJoinNonCFException() throws Exception {
        CompletionStage<String> stage = makeBrokenNonCompletableFutureCompletionStage("boom");
        ExtendedCompletionStage<String> extended = Futures.extend(stage);
        String result = null;
        Throwable exception = null;
        try {
            result = extended.join().toString(); // FIXME why not returning String anyway?
        } catch (Throwable e) {
            exception = e;
        }

        soft.assertThat(result).isNull();
        soft.assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(CompletionException.class);
        assertThat(exception.getCause().getMessage()).isEqualTo("boom");
        soft.assertAll();
    }
    //</editor-fold>

    //<editor-fold desc="extended: getNow()">
    /**
     * Tests getNow()
     */
    @Test
    public void testGetNowNormal() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(Futures.newEagerAsyncCompletableFuture(Function0.fromValue("done")));
        // ensure that completed using functionality tested so far
        stage.get();
        String result = stage.getNow("alternate");
        assertThat(result).isEqualTo("done");
    }

    /**
     * Tests getNow() when task not completed
     */
    @Test
    public void testGetNowIncomplete() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal)));
        String result = stage.getNow("alternate");
        signal.countDown();
        assertThat(result).isEqualTo("alternate");
    }

    /**
     * Tests getNow() when an exception is thrown
     */
    @Test
    public void testGetNowException() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(makeBrokenCompletionStage("boom"));

        try {
            stage.get(); // ensure that finished
        } catch (ExecutionException e) {
            // expecting this. Just ignore
        }

        Throwable exception = null; // we want to verify the right cause

        try {
            stage.getNow("alternate");
        } catch (Throwable e) {
            exception = e;
        }

        assertThat(exception).isNotNull();
        assertThat(exception.getCause().getMessage()).isEqualTo("boom").as("expected the thrown exception to wrap the true cause");
    }

    /**
     * Tests getNow() when task canceled before completion
     */
    @Test
    public void testGetNowCanceled() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal)));
        stage.cancel(true);
        String result = null;

        boolean caught = false;
        try {
            result = stage.getNow("alternate");
        } catch (CancellationException e) {
            caught = true; // must catch in order to let the function cleanly complete at the end
        }
        signal.countDown();
        assertThat(caught).isTrue().as("should have thrown CancellationException");
    }

    //=============================== nonCF variants

    /**
     * Tests getNow()
     * when delegate is not a CompletableFuture
     */
    @Test
    public void testGetNowNonCFNormal() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(
                new TestingStage<>(Futures.newEagerAsyncCompletableFuture(Function0.fromValue("done"))));
        try {
            stage.get(); // ensure complete with only functionality tested so far
        } catch (Throwable e) {
            // do nothing. we expect this and just need to wait it out
        }
        String result = stage.getNow("alternate");
        assertThat(result).isEqualTo("done");
    }

    /**
     * Tests getNow() when task not completed
     * when delegate is not a CompletableFuture
     */
    @Test
    public void testGetNowNonCFIncomplete() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(
                new TestingStage<>(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal))));
        String result = stage.getNow("alternate");
        signal.countDown();
        assertThat(result).isEqualTo("alternate");
    }

    /**
     * Tests getNow() when an exception is thrown
     * when delegate is not a CompletableFuture
     */
    @Test
    public void testGetNowNonCFException() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(new TestingStage<>(makeBrokenCompletionStage("boom")));

        try {
            stage.get(); // ensure that finished
        } catch (ExecutionException e) {
            // expecting this. Just ignore
        }

        CompletionException exception = null; // we want to verify the right cause
        try {
            stage.getNow("alternate");
        } catch (CompletionException e) {
            exception = e;
        }

        assertThat(exception).isNotNull().as("should have thrown an exception");
        assertThat(exception.getCause().getMessage()).isEqualTo("boom").as("expected the thrown exception to wrap the true cause");
    }

    /**
     * Tests getNow() when task canceled before completion
     * when delegate is not a CompletableFuture
     */
    @Test
    public void testGetNowNonCFCanceled() throws Exception {
        ExtendedCompletionStage<String> stage = Futures.extend(
                new TestingStageWithCancel<>(Futures.newEagerAsyncCompletableFuture(functionAwaitingSignal("done", signal))));
        stage.cancel(true);
        String result = null;

        boolean caught = false;
        try {
            result = stage.getNow("alternate");
        } catch (CancellationException e) {
            caught = true; // must catch in order to let the function cleanly complete at the end
        }
        signal.countDown();
        assertThat(caught).isTrue().as("should have thrown CancellationException");
    }
    //</editor-fold>


    //<editor-fold desc="extended: isCompletedExceptionally()">
    /**
     * Tests isCompletedExceptionally() - no exception
     */
    @Test
    public void testIsCompletedExceptionallyNoException() throws Exception {
        throw new AssertionError("testIsCompletedExceptionallyNoExceptionNoException() not implemented");
    }
    //</editor-fold>

    //<editor-fold desc="extended: awaitCompletion()">
    /**
     * Tests awaitCompletion() - no exception
     */
    @Test
    public void testAwaitCompletionNoException() throws Exception {
        throw new AssertionError("testAwaitCompletionNoExceptionNoException() not implemented");
    }
    //</editor-fold>
    //</editor-fold>


    // helper stuff ===================================================================================================

    //<editor-fold desc="helper stuff">

    private Function0<String> slowFunction(String value, int timeToWait, TimeUnit unit) {
        return () -> {
            unit.sleep(timeToWait);
            evidence.set(value);
            return value;
        };
    }

    private Function0<String> functionAwaitingSignal(String value, CountDownLatch signal) {
        return () -> {
            try {
                signal.await();
                return slowFunction(value, 100, TimeUnit.MILLISECONDS).apply();
            } catch (InterruptedException e) {
                evidence.set("interrupted");
                throw e;
            }
        };
    }


    @SuppressWarnings("unchecked")
    private <T> CompletionStage<T> makeNonCompletableFutureCompletionStage(T value) {
        CompletionStage<T> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue(value));
        return TestingStage.of(stage);
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<String> makeNonCompletableFutureCompletionStage(Function0<String> function) {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(function);
        return TestingStage.of(stage);
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<String> makeBrokenNonCompletableFutureCompletionStage(String message) {
        CompletionStage<String> stage = Futures.newEagerAsyncCompletableFuture(Function0.fromValue("should not have been called")
        .afterRunning(brokenAction(message)));
        return TestingStage.of(stage);
    }

    @SuppressWarnings("unchecked")
    private CompletionStage<String> makeBrokenCompletionStage(String message) {
        return Futures.newEagerAsyncCompletableFuture(Function0.fromValue("should not have been called")
                .afterRunning(brokenAction(message)));
    }

    private Action0 brokenAction(String message) {
        return () -> {
            throw new Exception(message);
        };
    }

    @Data(staticConstructor = "of")
    private static class TestingStage<T> implements CompletionStage<T> {
        @Delegate
        private final CompletionStage<T> delegate;

    }

    private static  class TestingStageWithCancel<T> extends TestingStage<T> {
        @Getter
        private boolean canceled = false;
        @Getter
        private Optional<Boolean> mayInterruptIfRunningSet = Optional.empty();

        private TestingStageWithCancel(CompletionStage delegate) {
            super(delegate);
        }

        public boolean cancel(boolean mayInterruptIfRunning) {
            mayInterruptIfRunningSet = Optional.of(mayInterruptIfRunning);
            canceled = true;
            return true;
        }
    }

    //</editor-fold>
}
