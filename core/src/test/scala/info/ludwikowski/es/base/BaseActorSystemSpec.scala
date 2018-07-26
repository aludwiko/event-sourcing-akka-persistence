package info.ludwikowski.es.base

import akka.actor.ActorSystem
import akka.testkit.TestKit
import akka.util.Timeout
import org.scalatest.BeforeAndAfterAll

import scala.concurrent.duration._

abstract class BaseActorSystemSpec extends TestKit(ActorSystem("es-core")) with BaseSpec with BeforeAndAfterAll {

  implicit val timeout: Timeout = Timeout(20.seconds)

  override protected def afterAll: Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }
}
