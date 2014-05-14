package bbuzz

import rx.lang.scala.{Observable, schedulers}


trait TweetProvider {

  def tweets: TweetObservable
}

object TweetProvider {
  def apply(os: TweetObservable): TweetProvider = new TweetProvider {
    def tweets: TweetObservable = os
  }

  def apply(it: Iterable[String]): TweetProvider = new FromStringIterable {
    def iterable: Iterable[String] = it
  }

  trait FromStringIterable extends TweetProvider {
    def iterable: Iterable[String]

    final def tweets: TweetObservable = {
      val it = iterable
      Observable.from(it, schedulers.IOScheduler()).
        map(createTweet).
        flatMap(s => Observable.from(s))
    }
  }
}
