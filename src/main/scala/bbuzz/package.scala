import rx.lang.scala.{Observable, Observer}
import scala.util.Try
import twitter4j.{TwitterObjectFactory, Status}

package object bbuzz {

  type Tweet = Status

  type TweetObserver = Observer[Tweet]

  type TweetObservable = Observable[Tweet]

  def createTweet(json: String) =
    tryCreateTweet(json).toOption.toIterable

  def tryCreateTweet(json: String) =
    Try(TwitterObjectFactory.createStatus(json))

}
