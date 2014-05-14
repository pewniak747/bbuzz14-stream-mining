package bbuzz

import java.net.InetSocketAddress
import bbuzz.support.ElasticsearchScanIterable

trait ElasticsearchScanTweets extends TweetProvider.FromStringIterable {

  def host: String
  def port: Int
  def index: String

  def iterable: Iterable[String] = {
    val esAddr = new InetSocketAddress(host, port)
    val es = s"http://${esAddr.getHostString}:${esAddr.getPort}"
    ElasticsearchScanIterable(es, index)
  }
}
