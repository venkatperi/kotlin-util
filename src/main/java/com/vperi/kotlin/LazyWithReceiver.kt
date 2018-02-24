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

import java.util.WeakHashMap
import kotlin.reflect.KProperty

class LazyWithReceiver<in T, out R>(private val initializer: T.() -> R) {
  private val values = WeakHashMap<T, R>()

  @Suppress("UNCHECKED_CAST")
  operator fun getValue(thisRef: Any, property: KProperty<*>): R =
      synchronized(values)
      {
        thisRef as T
        return values.getOrPut(thisRef) { thisRef.initializer() }
      }
}