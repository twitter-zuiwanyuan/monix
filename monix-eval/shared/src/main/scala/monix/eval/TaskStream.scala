/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
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

package monix.eval

import monix.eval.Task.nondeterminism

/** A `TaskStream` represents a [[Task]]-based [[Streamable]], that
  * has potentially lazy behavior and that also supports
  * asynchronous behavior.
  *
  * A `TaskStream` has the following characteristics:
  *
  *  1. it can be infinite
  *  1. it can be lazy
  *  1. it can be asynchronous
  *
  * It's very similar to other lazy types in Scala's standard
  * library, like `Iterator`, however the execution model is more
  * flexible, as it is controlled by [[Task]]. This means that:
  *
  *  - you can have the equivalent of an `Iterable` if the
  *    `Task` tails are built with [[Task.eval]]
  *  - you can have the equivalent of a Scala `Stream`, caching
  *    elements as the stream is getting traversed, if the
  *    `Task` tails are built with [[Task.evalOnce]]
  *  - it can be completely strict and thus equivalent with
  *    `List`, if the tails are built with [[Task.now]]
  *  - it supports asynchronous behavior and can also replace
  *    `Observable` for simple use-cases - for example the
  *    elements produced can be the result of asynchronous
  *    HTTP requests
  *
  * The implementation is practically wrapping the generic
  * [[Streamable]], materialized with the [[Task]] type.
  */
final case class TaskStream[+A](stream: Streamable[Task,A])
  extends Streamable.Like[A,Task,TaskStream]() {

  protected def transform[B](f: (Streamable[Task, A]) => Streamable[Task, B]): TaskStream[B] =
    TaskStream(f(stream))
}

object TaskStream extends Streamable.Builders[Task, TaskStream] {
  /** Wraps a [[Streamable]] into a [[TaskStream]]. */
  def fromStream[A](stream: Streamable[Task, A]): TaskStream[A] =
    TaskStream(stream)
}