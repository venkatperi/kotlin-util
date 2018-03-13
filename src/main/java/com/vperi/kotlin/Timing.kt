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

package com.vperi.kotlin

class Timing(autoStart: Boolean = true) {
  private val events = HashMap<String, Long>()

  val start: Long
    get() = events["start"] ?: 0

  init {
    if (autoStart)
      mark("start")
  }

  fun clear() {
    events.clear()
    mark("start")
  }

  fun mark(name: String) {
    events[name] = System.nanoTime()
  }

  operator fun get(name: String): Long {
    return (events[name] ?: 0) - start
  }

  fun msTime(name: String): Double {
    return this[name] / 1e6
  }
}

