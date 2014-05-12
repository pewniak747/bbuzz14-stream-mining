package bbuzz

import rx.lang.scala.schedulers.ComputationScheduler


trait TweetConsumer extends TweetObserver {

  override def onNext(value: Tweet): Unit = onTweet(value)

  override def onError(error: Throwable): Unit = handleException(error)


  def onTweet(tweet: Tweet): Unit

  def handleException(exception: Throwable): Unit
}

abstract class TweetStreaming extends TweetConsumer {
  this: TweetProvider =>

  def main(args: Array[String]) {
    val ts = tweets
    ts.subscribe(this, ComputationScheduler())
    ts.toBlockingObservable.foreach(_ => ())
  }
}
