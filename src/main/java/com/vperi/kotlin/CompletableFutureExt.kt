/*
 * Copyright 2018 Venkat Peri. All rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

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
