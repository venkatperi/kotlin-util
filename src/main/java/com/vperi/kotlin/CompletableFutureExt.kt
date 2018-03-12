package com.vperi.kotlin

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch

fun <T, X> CompletableFuture<T>.then(
  success: (T) -> X,
  failure: ((Throwable) -> X)?): CompletableFuture<X> {
  return CompletableFuture<X>().apply {
    this@then.handle { value, error: Throwable? ->
      when (error) {
        null -> complete(success(value))
        else -> {
          if (failure != null)
            complete(failure(error))
          else
            completeExceptionally(error)
        }
      }
    }
  }
}

fun <T, X> CompletableFuture<T>.thenAsync(
  success: (T) -> X,
  failure: ((Throwable) -> X)?): CompletableFuture<X> {
  return CompletableFuture<X>().apply {
    this@thenAsync.handle { value, error: Throwable? ->
      when (error) {
        null -> complete(success(value))
        else -> {
          if (failure != null)
            complete(failure(error))
          else
            completeExceptionally(error)
        }
      }
    }
  }
}

fun <T, X> CompletableFuture<T>.then(success: (T) -> X):
  CompletableFuture<X> =
  then(success, null)


fun <T, X> CompletableFuture<T>.thenAsync(success: (T) -> X):
  CompletableFuture<X> =
  thenAsync(success, null)

fun <T> CompletableFuture<T>.catch(
  failure: ((Throwable) -> T)): CompletableFuture<T> =
  then({ it }, failure)

fun <T> CompletableFuture<T>.catchAsync(
  failure: ((Throwable) -> T)): CompletableFuture<T> =
  thenAsync({ it }, failure)

@JvmName("catchUnit")
fun CompletableFuture<*>.catch(
  failure: ((Throwable) -> Unit)): CompletableFuture<Unit> =
  then({ }, failure)

@JvmName("catchAsyncUnit")
fun CompletableFuture<*>.catchAsync(
  failure: ((Throwable) -> Unit)): CompletableFuture<Unit> =
  thenAsync({ }, failure)

inline fun <reified T> List<CompletableFuture<out T>>.all():
  CompletableFuture<List<T>> {
  val count = CountDownLatch(size)

  val future = CompletableFuture.supplyAsync {
    count.await()
    map { it.join() }
  }

  this.forEach { item ->
    item.then({
      count.countDown()
    }, {
      future.completeExceptionally(it)
    })
  }

  // cancel pending items on failure
  future.catch {
    forEach { it.cancel(true) }
  }

  return future
}
