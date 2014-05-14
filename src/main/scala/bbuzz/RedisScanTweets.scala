package bbuzz

import bbuzz.support.RedisScanIterable
import java.net.InetSocketAddress

trait RedisScanTweets extends TweetProvider.FromStringIterable {

  def host: String
  def port: Int
  def db: Int

  def iterable: Iterable[String] = RedisScanIterable(new InetSocketAddress(host, port), db)
}
