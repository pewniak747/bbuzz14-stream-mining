package bbuzz
package example

import scala.collection.{mutable, SortedMap}
import scala.collection.immutable.SortedSet

/**
 * Prints a Tweets text onto stdout.
 */
trait PrintText extends TweetConsumer {

  /**
   * Gets called for every tweet that is pulled from the [[bbuzz.TweetProvider]].
   * This method should not block and complete quickly.
   *
   * @param tweet a new [[bbuzz.Tweet]]
   */
  def onTweet(tweet: bbuzz.Tweet): Unit = println(tweet.getText)

  /**
   * Gets called when an exception occurred during providing the Tweets.
   * This will be a fatal exception, and tweets will not continue to flow after
   * this exception occurred.
   *
   * @param exception the fatal exception
   */
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}

class TopK(size: Int) {
  val map = mutable.HashMap[String, Int]()

  def +=(key: String) = increment(key)

  def increment(key: String) = {
    if(map.keySet.contains(key))
      map += (key -> (map(key) + 1))
    else if(map.size < size)
      map += (key -> 1)
    else {
      val minimumElement = map.minBy({case (key: String, value: Int) => value})
      map -= minimumElement._1
      map += (key -> (minimumElement._2 + 1))
    }
  }

  def topList: List[(String, Int)] = map.toList.sortBy(-1 * _._2)
}

trait  TopHashTags extends TweetConsumer {

  val topBuckets: mutable.HashMap[String, TopK] = mutable.HashMap()
  var counter = 0

  /**
   * Gets called for every tweet that is pulled from the [[bbuzz.TweetProvider]].
   * This method should not block and complete quickly.
   *
   * @param tweet a new [[Tweet]]
   */
  override def onTweet(tweet: bbuzz.Tweet): Unit = {
    val createdAt = tweet.getCreatedAt
    val formattedCreatedAt = createdAt.getYear + "-" + ("%02d".format(createdAt.getMonth)) + "-" + ("%02d".format(createdAt.getDate)) +
      " " + ("%02d".format(createdAt.getHours))

    val topK: TopK = topBuckets.getOrElse(formattedCreatedAt, new TopK(100))
    topBuckets(formattedCreatedAt) = topK

    tweet.getHashtagEntities.foreach(hashTag => topK.increment(hashTag.getText))
    counter += 1

    if(counter % 500 == 0) {
      println("After " + counter + " tweets")
      topBuckets.toList.sortBy(_._1).drop(topBuckets.size - 3).foreach({ case (time: String, topK: TopK) =>
        println("---------- Top 10 (" + time + ")------------")
        val topList = topK.topList.zipWithIndex

        for (((hashTag: String, count: Int), index: Int) <- topList.take(10)) {
          println(s"${index + 1}. $hashTag ($count)")
        }
      }
      )
      println("\n\n\n")
    }
  }

  /**
   * Gets called when an exception occurred during providing the Tweets.
   * This will be a fatal exception, and tweets will not continue to flow after
   * this exception occurred.
   *
   * @param exception the fatal exception
   */
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}
/**
 * Prints the Tweets hash tags onto stdout.
 */
trait PrintHashTags extends TweetConsumer {

  /**
   * Gets called for every tweet that is pulled from the [[bbuzz.TweetProvider]].
   * This method should not block and complete quickly.
   *
   * @param tweet a new [[bbuzz.Tweet]]
   */
  def onTweet(tweet: Tweet): Unit =
    Some(tweet.getHashtagEntities.map(_.getText)).
      getOrElse(Array("-- no hash tags --")).
      foreach(println)

  /**
   * Gets called when an exception occurred during providing the Tweets.
   * This will be a fatal exception, and tweets will not continue to flow after
   * this exception occurred.
   *
   * @param exception the fatal exception
   */
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}


/**
 * Example of connecting to Redis and printing the Tweets text.
 */
object RedisPrinter extends TweetStreaming with PrintText
with RedisScanTweets {

  /**
   * The hostname of the Redis server.
   */
  def host = "localhost"

  /**
   * The port of the Redis server.
   */
  def port = 6379

  /**
   * The Redis db.
   */
  def db = 0
}


/**
 * Example of connecting to Elasticsearch and printing the Tweets text.
 */
object ElasticsearchPrinter extends TweetStreaming with TopHashTags with ElasticsearchScanTweets {

  /**
   * The hostname of the Elasticsearch server.
   */
  def host = "es01.geekthink.de"

  /**
   * The port of the HTTP endpoint of the Elasticsearch server.
   */
  def port = 80

  /**
   * The index that contains the tweets.
   */
  def index = "bbuzz-hackday"
}


/**
 * Example of connection to the Twitter streaming API and printing the Tweets text.
 *
 * You have to provide your credentials, though.
 */
object TwitterPrinter extends TweetStreaming with PrintText
with TwitterApiTweets {

  /**
   * @return the OAuth Consumer Key (per authenticated request)
   */
  def OAuthConsumerKey = ???

  /**
   * @return the OAuth Consumer Secret (per authenticated request)
   */
  def OAuthConsumerSecret = ???

  /**
   * @return the OAuth Access Token (per authorized app)
   */
  def OAuthAccessToken = ???

  /**
   * @return the OAuth Access Token Secret (per authorized app)
   */
  def OAuthAccessTokenSecret = ???
}


/**
 * Example of connection to ZeroMQ and printing the Tweets hash tags.
 */
object ZeromqPrinter extends TweetStreaming with PrintHashTags
with ZeroMqTweets {

  /**
   * The hostname of the publisher socket.
   * They should implement a [[http://api.zeromq.org/3-2:zmq-socket#toc9 ZMQ_PUB]] socket.
   *
   * If you connect too localhost, do not use `localhost` but `127.0.0.1` instead.
   */
  def host = "144.76.187.43"

  /**
   * The port of the publisher socket.
   * They should implement a [[http://api.zeromq.org/3-2:zmq-socket#toc9 ZMQ_PUB]] socket.
   */
  def port = 5555

  /**
   * The ZMQ channel to subscribe to.
   */
  def channel = "tweet.stream"
}
