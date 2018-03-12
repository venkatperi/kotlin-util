package com.vperi.kotlin

import net.jodah.concurrentunit.Waiter
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CompletableFuture

class CompletableExt {
  private var waiter = Waiter()
  var timing = Timing()

  @Before
  fun beforeEach() {
    waiter = Waiter()
  }

  fun getList(count: Int): List<CompletableFuture<Void>> {
    return (0..count).map {
      CompletableFuture.runAsync {
        Thread.sleep(it + 100L)
      }
    }
  }

  @Test
  fun custom_all_ok() {
    timing.clear()
    getList(10).all()
      .then {
        timing.mark("done")
        println("custom: %6.2fms".format(timing.msTime("done")))
        waiter.resume()
      }

    waiter.await(10000)
  }

  @Test
  fun default_all_ok() {
    val list = getList(10)
    timing.clear()
    CompletableFuture.allOf(*list.toTypedArray())
      .then {
        val results = list.map { it.join() }
        timing.mark("done")
        println("default: %6.2fms".format(timing.msTime("done")))
        waiter.resume()
      }

    waiter.await(10000)
  }
}