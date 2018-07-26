package info.ludwikowski.es.user.application

import info.ludwikowski.es.user.domain.{UserEvent, UserId}

sealed trait UserActorResponse

object UserActorResponse {
  final case class CommandProcessed(events: List[UserEvent]) extends UserActorResponse
  final case class UserNotCreatedError(userId: UserId)       extends UserActorResponse
}
