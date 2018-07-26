package info.ludwikowski.es.base

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.scalatest._
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}

trait BaseSpec
    extends Suite
    with Matchers
    with ScalaFutures
    with OptionValues
    with TryValues
    with EitherValues
    with MockitoSugar
    with LazyLogging
    with LoneElement
    with BeforeAndAfterAll
    with Eventually {

  val config = ConfigFactory.load()

  implicit val defaultPatience =
    PatienceConfig(timeout = Span(10, Seconds), interval = Span(5, Millis))

  // Testkit methods do not return Assertions, but instead throw exceptions.
  // If such method returns we can assume it is successful assertion.
  implicit class AsAssertion[T](t: T) {
    def asAssertion: Assertion = 1 shouldBe 1
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()
  }

}
