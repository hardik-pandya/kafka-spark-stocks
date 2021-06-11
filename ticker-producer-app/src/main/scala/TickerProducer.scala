import java.util.Properties
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.duration.Duration
import com.typesafe.scalalogging.Logger

object TickerProducer extends App{


  val log = Logger(TickerProducer.getClass.getName)
  val TOPIC_NAME = "stocks"
  val BROKERS = "kafka:9092"

  val properties = new Properties
  properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS)
  properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)
  properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName)

  val producer = new KafkaProducer[String, String](properties)

  val actorSystem = ActorSystem()
  val scheduler = actorSystem.scheduler


  val task = new Runnable {
    def run() {

      val baseTickers: TickerList = TickerList(List(Ticker("AMZN",1902),Ticker("MSFT",107),Ticker("AAPL",257)))
      val generatedTickers: TickerList = TickerList(baseTickers.generatedTickers(baseTickers))
      val data = new ProducerRecord[String, String](TOPIC_NAME, generatedTickers.asJson.noSpaces)
      log.info("[Producer] Sending Data To Topic:: " + data.topic() + " with " + data.value())
      producer.send(data)
      producer.flush()
      log.info("[Producer] Sent Data To:: " + data.topic())

    }
  }
  implicit val executor = actorSystem.dispatcher

  scheduler.schedule(
    initialDelay = Duration(5, TimeUnit.SECONDS),
    interval = Duration(10, TimeUnit.SECONDS),
    runnable = task)

}
