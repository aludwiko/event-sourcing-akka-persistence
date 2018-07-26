package info.ludwikowski.es.base.application

import java.time.{Instant, ZoneOffset}

object Clock {
  val Zone = ZoneOffset.UTC

  def nowUtc(): Instant = Instant.now()
}
