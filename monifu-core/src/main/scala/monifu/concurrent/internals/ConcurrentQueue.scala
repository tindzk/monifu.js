/*
 * Copyright (c) 2014 by its authors. Some rights reserved. 
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
 
package monifu.concurrent.internals

import scala.collection.mutable

private[monifu] final class ConcurrentQueue[T](elems: T*) {
  private[this] val underlying: mutable.Queue[T] =
    mutable.Queue(elems : _*)

  def offer(elem: T): Unit = {
    if (elem == null) throw null
    underlying.enqueue(elem)
  }

  def poll(): T = {
    if (underlying.isEmpty)
      null.asInstanceOf[T]
    else
      underlying.dequeue()
  }

  def isEmpty: Boolean = {
    underlying.isEmpty
  }

  def nonEmpty: Boolean = {
    !underlying.isEmpty
  }

  def clear(): Unit = {
    underlying.clear()
  }
}
