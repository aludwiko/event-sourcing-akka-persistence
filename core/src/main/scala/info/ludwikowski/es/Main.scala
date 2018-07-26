package info.ludwikowski.es

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

object Main extends App with MainModule {

  override lazy val config                              = ConfigFactory.load()
  override implicit lazy val system: ActorSystem        = ActorSystem("es-core")
  override implicit lazy val materializer: Materializer = ActorMaterializer()(system)

  private[this] def start(): Unit = {
    logger.info(s"es-core started")
  }

  def terminate(): Unit = {
    Try(Await.ready(system.terminate(), 15.seconds))
    ()
  }

  sys.addShutdownHook {
    terminate()
  }

  start()

}
