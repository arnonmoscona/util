package com.moscona.util.async.furtures; 

import com.moscona.util.functions.impure.Action0;
import com.moscona.util.functions.impure.Function0;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;

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

    @Before
    public void before() throws Exception {
        soft = new SoftAssertions();
        executor = Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "custom"));
        evidence.set("not set");
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
        CompletionStage<?> future = Futures.newEagerAsyncCompletableFuture(() -> {
            evidence.set("action done");
        });

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


} 
