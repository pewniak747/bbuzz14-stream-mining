package bbuzz
package example

trait PrintHandler extends TweetConsumer {

  def onTweet(tweet: Tweet): Unit = println(tweet.getText)
  def handleException(exception: Throwable): Unit = exception.printStackTrace()
}


object RedisPrinter extends TweetStreaming with PrintHandler
with RedisTweetProvider {

  def host = "localhost"
  def port = 6379
  def db = 0
}

object ElasticsearchPrinter extends TweetStreaming with PrintHandler
with ElasticsearchTweetProvider {

  def host = "localhost"
  def port = 9200
  def index = "tweets"
}


object TwitterPrinter extends TweetStreaming with PrintHandler
with TwitterApiProvider {

  def OAuthConsumerKey = ???
  def OAuthConsumerSecret = ???
  def OAuthAccessToken = ???
  def OAuthAccessTokenSecret = ???
}
