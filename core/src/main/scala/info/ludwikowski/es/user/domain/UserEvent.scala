package info.ludwikowski.es.user.domain

import java.time.Instant

sealed trait UserEvent {
  def userId: UserId
  def operationId: OperationId
  def createdAt: Instant
}

object UserEvent {
  final case class UserCreated(userId: UserId, operationId: OperationId, createdAt: Instant, name: String, email: Email) extends UserEvent
  final case class NameUpdated(userId: UserId, operationId: OperationId, createdAt: Instant, newName: String)            extends UserEvent
  final case class EmailUpdated(userId: UserId, operationId: OperationId, createdAt: Instant, newEmail: Email)           extends UserEvent
}
