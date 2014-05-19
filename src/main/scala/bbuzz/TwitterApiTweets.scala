package bbuzz

import twitter4j._
import rx.lang.scala.Subject

/**
 * Provides Tweets by streaming from the Twitter API.
 *
 * You should obtain developer keys from Twitter.
 * The steps are roughly
 * 1. create an App at [[https://apps.twitter.com/]]
 * 2. create access tokens for your app as per [[https://dev.twitter.com/docs/auth/tokens-devtwittercom]]
 * 3. follow the steps at [[https://dev.twitter.com/docs/auth/authorizing-request]] to obtain a consumer key/secret pair.
 *
 * This provider will use a sampled stream from the Firehose ([[https://dev.twitter.com/docs/api/1.1/get/statuses/sample Twitter docs]])
 *
 * You could also extend or modify this to use a filtered stream.
 */
trait TwitterApiTweets extends TweetProvider {

  /**
   * @return the OAuth Consumer Key (per authenticated request)
   */
  def OAuthConsumerKey: String

  /**
   * @return the OAuth Consumer Secret (per authenticated request)
   */
  def OAuthConsumerSecret: String

  /**
   * @return the OAuth Access Token (per authorized app)
   */
  def OAuthAccessToken: String

  /**
   * @return the OAuth Access Token Secret (per authorized app)
   */
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

  /**
   * Hooks into the sampled public stream and pushed the Tweets down to the [[bbuzz.TweetConsumer]]
   *
   * @return an `Observable` of `Tweets`
   */
  final def tweets: TweetObservable = {
    val p = Subject[Tweet]()
    val sl = statusListener(p)

    val twitterStream = openStream(sl)
    twitterStream.sample()

    p
  }
}
