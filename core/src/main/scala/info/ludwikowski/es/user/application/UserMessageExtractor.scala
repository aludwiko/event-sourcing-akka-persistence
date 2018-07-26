package info.ludwikowski.es.user.application

import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import info.ludwikowski.es.user.domain.UserCommand

class UserMessageExtractor extends HashCodeMessageExtractor(100 /*TODO should be configurable*/ ) {
  override def entityId(message: Any): String = message match {
    case command: UserCommand  => command.userId.toString
    case query: UserActorQuery => query.userId.toString
  }
}
