package com.vperi.kotlin

import net.jodah.concurrentunit.Waiter
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeoutException

/**
 * Created by venkat on 2/28/18.
 */
class EventTest {
  var event: Event<Void>? = null
  private var waiter: Waiter? = null

  @Before
  fun beforeEach() {
    event = Event()
    waiter = Waiter()
  }

  @Test
  fun fire_multiple_events() {
    event!! += { waiter!!.resume() }
    event!!()
    event!!()
    event!!()
    event!!()
    waiter!!.await(10000, 4)
  }

  @Test
  fun late_handlers_fire_when_sticky() {
    event!!(sticky = true)
    event!! += { waiter!!.resume() }
    waiter!!.await(1000)
  }

  @Test(expected = TimeoutException::class)
  fun late_handlers_wont_fire_without_sticky() {
    event!!()
    event!! += { waiter!!.resume() }
    waiter!!.await(1000)
  }

}