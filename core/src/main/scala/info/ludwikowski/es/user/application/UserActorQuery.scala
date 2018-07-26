package info.ludwikowski.es.user.application

import info.ludwikowski.es.user.domain.UserId

sealed trait UserActorQuery {
  def userId: UserId
}

object UserActorQuery {
  case class GetUser(userId: UserId) extends UserActorQuery
}
