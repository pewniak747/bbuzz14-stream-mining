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

  def host = "localhost"
  def port = 6379
  def db = 0
}


/**
 * Example of connecting to Elasticsearch and printing the Tweets text.
 */
object ElasticsearchPrinter extends TweetStreaming with PrintText
with ElasticsearchScanTweets {

  def host = "localhost"
  def port = 9200
  def index = "tweets"
}


/**
 * Example of connection to the Twitter streaming API and printing the Tweets text.
 *
 * You have to provide your credentials, though.
 */
object TwitterPrinter extends TweetStreaming with PrintText
with TwitterApiTweets {

  def OAuthConsumerKey = ???
  def OAuthConsumerSecret = ???
  def OAuthAccessToken = ???
  def OAuthAccessTokenSecret = ???
}


/**
 * Example of connection to ZeroMQ and printing the Tweets hash tags.
 */
object ZeromqPrinter extends TweetStreaming with PrintHashTags
with ZeroMqTweets {

  def host = "127.0.0.1"
  def port = 5561
  def channel = "tweet.stream"
}
