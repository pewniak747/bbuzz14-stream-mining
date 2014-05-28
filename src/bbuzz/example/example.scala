package bbuzz
package example

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
object ElasticsearchPrinter extends TweetStreaming with PrintHashTags
with ElasticsearchScanTweets {

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
  def host = "127.0.0.1"

  /**
   * The port of the publisher socket.
   * They should implement a [[http://api.zeromq.org/3-2:zmq-socket#toc9 ZMQ_PUB]] socket.
   */
  def port = 5561

  /**
   * The ZMQ channel to subscribe to.
   */
  def channel = "tweet.stream"
}
