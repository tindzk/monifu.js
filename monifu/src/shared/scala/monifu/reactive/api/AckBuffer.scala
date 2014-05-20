package monifu.reactive.api

import monifu.concurrent.extensions._
import monifu.concurrent.atomic.padded.Atomic
import monifu.reactive.api.Ack.{Done, Continue}
import scala.concurrent.{ExecutionContext, Promise, Future}
import scala.util.{Success, Failure}

/**
 * Internal class used in `Observable.merge`
 */
private[reactive] final class AckBuffer {
  private[this] val lastResponse = Atomic(Continue : Future[Ack])

  def scheduleNext(f: => Future[Ack])(implicit ec: ExecutionContext): Future[Ack] = {
    val promise = Promise[Ack]()
    val oldResponse = lastResponse.getAndSet(promise.future)
    oldResponse.unsafeOnComplete {
      case Failure(ex) => promise.failure(ex)
      case Success(Done) => promise.success(Done)
      case Success(Continue) =>
        f match {
          case Continue =>
            promise.success(Continue)
          case Done =>
            promise.success(Done)
          case other =>
            promise.completeWith(other)
        }
    }
    promise.future
  }

  def scheduleDone(f: => Future[Done])(implicit ec: ExecutionContext): Future[Done] = {
    val promise = Promise[Done]()
    val oldResponse = lastResponse.getAndSet(promise.future)
    oldResponse.unsafeOnComplete {
      case Failure(ex) => promise.failure(ex)
      case Success(Done) => promise.success(Done)
      case Success(Continue) =>
        f match {
          case Done =>
            promise.success(Done)
          case other =>
            promise.completeWith(other)
        }
    }
    promise.future
  }
}
