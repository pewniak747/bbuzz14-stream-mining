package bbuzz
package example

trait PrintText extends TweetConsumer {

  def onTweet(tweet: Tweet): Unit = println(tweet.getText)
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}

trait PrintHashTags extends TweetConsumer {

  def onTweet(tweet: Tweet): Unit = tweet.getHashtagEntities.map(_.getText).foreach(println)
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}

object RedisPrinter extends TweetStreaming with PrintText
with RedisScanTweets {

  def host = "localhost"
  def port = 6379
  def db = 0
}

object ElasticsearchPrinter extends TweetStreaming with PrintText
with ElasticsearchScanTweets {

  def host = "localhost"
  def port = 9200
  def index = "tweets"
}


object TwitterPrinter extends TweetStreaming with PrintText
with TwitterApiTweets {

  def OAuthConsumerKey = ???
  def OAuthConsumerSecret = ???
  def OAuthAccessToken = ???
  def OAuthAccessTokenSecret = ???
}

object ZeromqPrinter extends TweetStreaming with PrintHashTags
with ZeroMqTweets {

  def host: String = "127.0.0.1"
  def port: Int = 5561
  def channel: String = "tweet.stream"
}
