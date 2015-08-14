/**
 * This package includes functions and actions similar to those in RxJava's rx.functions.
 * The main difference is that these are "impure" in the sense that they may throw exceptions.
 * These are used in contexts where you want to abstract code in this way where you actually want to
 * handle exception and can call methods with exceptions without having to wrap them.
 * For example, when you want to convert a method call to a CompletableFuture, and allow
 * exceptional completion.
 */
package com.moscona.util.functions.impure;