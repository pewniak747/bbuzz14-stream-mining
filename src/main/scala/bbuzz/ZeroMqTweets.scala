package bbuzz

import org.zeromq.ZMQ
import org.zeromq.ZMQ.Socket
import rx.lang.scala.Subject
import scala.annotation.tailrec
import scala.util.control.Exception.handling


trait ZeroMqTweets extends TweetProvider {

  def host: String
  def port: Int
  def channel: String

  lazy val context = ZMQ.context(1)
  lazy val socket: Socket = {
    val socketUrl = s"tcp://$host:$port"
    val s = context.socket(ZMQ.SUB)
    s.connect(socketUrl)
    s.subscribe(channel.getBytes(ZMQ.CHARSET))
    s
  }

  def tweets: TweetObservable = {
    val p = Subject[Tweet]()

    new Thread(new Runnable {

      private final val ch = channel

      @tailrec
      def recv(): String = {
        val t = socket.recvStr().trim
        if (t == ch) recv()
        else t
      }

      def run() =
        (handling(classOf[Throwable]) by p.onError)
          (doRun())

      def doRun() = while (true) {
        val tweet = recv()
        createTweet(tweet).foreach(p.onNext)
      }
    }).start()

    p
  }
}
