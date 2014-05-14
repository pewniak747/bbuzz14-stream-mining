package bbuzz

import twitter4j._
import rx.lang.scala.Subject

trait TwitterApiTweets extends TweetProvider {

  def OAuthConsumerKey: String
  def OAuthConsumerSecret: String
  def OAuthAccessToken: String
  def OAuthAccessTokenSecret: String


  private lazy val config = new twitter4j.conf.ConfigurationBuilder().
    setOAuthConsumerKey(OAuthConsumerKey).
    setOAuthConsumerSecret(OAuthConsumerSecret).
    setOAuthAccessToken(OAuthAccessToken).
    setOAuthAccessTokenSecret(OAuthAccessTokenSecret).
    setJSONStoreEnabled(true).
    build

  private def statusListener(o: TweetObserver) = new StatusListener() {
    def onStatus(status: Status) { o.onNext(status) }
    def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice) {}
    def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
    def onException(ex: Exception) { o.onError(ex) }
    def onScrubGeo(arg0: Long, arg1: Long) {}
    def onStallWarning(warning: StallWarning) {}
  }

  private def openStream(sl: StatusListener): TwitterStream = {
    val twitterStream = new TwitterStreamFactory(config).getInstance

    sys.addShutdownHook {
      twitterStream.cleanUp()
      twitterStream.shutdown()
    }

    twitterStream.addListener(sl)
    twitterStream
  }

  def tweets: TweetObservable = {
    val p = Subject[Tweet]()
    val sl = statusListener(p)

    val twitterStream = openStream(sl)
    twitterStream.sample()

    p
  }
}
