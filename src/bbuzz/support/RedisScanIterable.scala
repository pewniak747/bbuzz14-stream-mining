package bbuzz.support

import java.net.InetSocketAddress
import redis.clients.jedis._
import scala.collection.immutable.Iterable

class RedisScanIterable private (redis: InetSocketAddress, db: Int) extends Iterable[String] {
  import RedisScanIterable.RedisScanIterator

  private[this] final val client = {
    val config = new JedisPoolConfig
    config.setTestOnBorrow(true)
    new JedisPool(config, redis.getHostString, redis.getPort, Protocol.DEFAULT_TIMEOUT, null, db, null)
  }

  def iterator: Iterator[String] = new RedisScanIterator(client)
}

object RedisScanIterable {
  private class RedisScanIterator(pool: JedisPool) extends Iterator[String] {

    private[this] final def execute[T](body: Jedis => T): T = {
      val r = pool.getResource
      try {
        val returnValue = body(r)
        pool.returnResource(r)
        returnValue
      } catch {
        case ex: Throwable =>
          pool.returnResource(r)
          throw ex
      }
    }

    private[this] final val firstResult: ScanResult[String] = execute(_.scan("0"))

    private[this] var result = firstResult
    private[this] def nextCursor = result.getStringCursor

    private[this] var resultIterator = result.getResult.iterator()

    def hasNext: Boolean = resultIterator.hasNext || nextCursor != "0"

    def next(): String = {
      if (!hasNext) Iterator.empty.next()
      doNext()
    }

    private[this] def doNext() = {
      if (!resultIterator.hasNext) fetchNext()
      execute(_.get(resultIterator.next()))
    }

    private[this] def fetchNext() = {
      result = execute(_.scan(nextCursor))
      resultIterator = result.getResult.iterator()
    }
  }

  def apply(r: InetSocketAddress, db: Int = 0): Iterable[String] = new RedisScanIterable(r, db)
}



