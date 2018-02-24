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

typealias  EventHandler<T> = Event<T>.(T?) -> Unit

class Event<T : Any?> {
  private val handlers = arrayListOf<EventHandler<T>>()

  operator fun plusAssign(handler: EventHandler<T>) {
    handlers += handler
  }

  operator fun minusAssign(handler: EventHandler<T>) {
    handlers.remove(handler)
  }

  operator fun invoke(value: T? = null) =
      handlers.forEach { it(value) }
}
