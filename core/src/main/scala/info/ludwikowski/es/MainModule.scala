package info.ludwikowski.es

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.scalalogging.LazyLogging
import info.ludwikowski.es.user.UserModule

trait MainModule extends UserModule with LazyLogging {

  implicit val system: ActorSystem
  implicit val materializer: Materializer
}
