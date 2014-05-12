package bbuzz

import bbuzz.support.{ElasticsearchScanIterable, RedisScanIterable}
import java.net.InetSocketAddress
import rx.lang.scala.{Subject, Observable}
import twitter4j._


sealed trait TweetProvider {

  def tweets: TweetObservable
}

trait StringIterProvider extends TweetProvider {

  def iterable: Iterable[String]

  def tweets: TweetObservable = {
    val iter = iterable

    Observable.from(iter).
      map(createTweetAsStream).
      flatMap(s => Observable.from(s))
  }
}


trait RedisTweetProvider extends StringIterProvider {

  def host: String
  def port: Int
  def db: Int

  def iterable: Iterable[String] = RedisScanIterable(new InetSocketAddress(host, port), db)
}

trait ElasticsearchTweetProvider extends StringIterProvider {

  def host: String
  def port: Int
  def index: String

  def iterable: Iterable[String] = {
    val esAddr = new InetSocketAddress(host, port)
    val es = s"http://${esAddr.getHostString}:${esAddr.getPort}"
    ElasticsearchScanIterable(es, index)
  }
}


trait TwitterApiProvider extends TweetProvider {

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

  def tweets: TweetObservable = {
    val p = Subject[Tweet]()
    val sl = statusListener(p)

    val twitterStream = new TwitterStreamFactory(config).getInstance

    twitterStream.addListener(sl)
    twitterStream.sample()

    sys.addShutdownHook {
      twitterStream.cleanUp()
      twitterStream.shutdown()
    }

    p
  }
}
