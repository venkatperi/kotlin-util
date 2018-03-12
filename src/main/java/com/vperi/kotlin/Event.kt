/*
 * Copyright 2018 Venkat Peri. All Rights Reserved.
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

package com.vperi.kotlin

import com.google.common.collect.ImmutableSet

typealias  EventHandler<T> = (T?) -> Unit

open class Event<T : Any> {
  private val handlers = mutableSetOf<EventHandler<T>>()
  private var hasSticky = false
  private var sticky: T? = null

  operator fun plusAssign(handler: EventHandler<T>) {
    synchronized(handlers) {
      handlers.add(handler)
    }
    if (hasSticky) {
      handler(sticky)
    }
  }

  operator fun minusAssign(handler: EventHandler<T>) {
    synchronized(handlers) {
      handlers.remove(handler)
    }
  }

  operator fun invoke(value: T? = null, sticky: Boolean = false) {
    if (sticky) {
      this.sticky = value
      hasSticky = true
    }
    ImmutableSet.copyOf(handlers).forEach { it(value) }
  }

}
