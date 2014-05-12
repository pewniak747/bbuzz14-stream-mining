import rx.lang.scala.{Observable, Observer}
import scala.util.Try
import twitter4j.{TwitterObjectFactory, Status}

package object bbuzz {

  type Tweet = Status

  type TweetObserver = Observer[Tweet]

  type TweetObservable = Observable[Tweet]

  def createTweetAsStream(json: String) =
    createTweet(json).toOption.toIterable

  def createTweet(json: String) =
    Try(TwitterObjectFactory.createStatus(json))

}
