package bbuzz

import bbuzz.support.RedisScanIterable
import java.net.InetSocketAddress
import scala.collection.immutable.Iterable

/**
 * Provides tweets by scanning a Redis db.
 *
 * This uses the [[http://redis.io/commands/scan 2.8 SCAN feature]].
 * All entries in the provided db should only be simple values (SET/GET) and contain valid tweets.
 */
trait RedisScanTweets extends TweetProvider.FromStringIterable {

  /**
   * The hostname of the Redis server.
   */
  def host: String

  /**
   * The port of the Redis server.
   */
  def port: Int

  /**
   * The Redis db.
   */
  def db: Int

  /**
   * Uses the [[http://redis.io/commands/scan 2.8 Redis SCAN API]] to obtain Tweets.
   *
   * @return an Iterable of Strings, where every String is the JSON representation on a [[bbuzz.Tweet]]
   */
  final def iterable: Iterable[String] = {
    val redisServer = new InetSocketAddress(host, port)
    RedisScanIterable(redisServer, db)
  }
}
