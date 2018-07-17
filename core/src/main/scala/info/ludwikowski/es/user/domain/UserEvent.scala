package info.ludwikowski.es.user.domain

import java.time.LocalDateTime

sealed trait UserEvent {
  def userId: UserId
  def operationId: OperationId
  def createdAt: LocalDateTime
}

object UserEvent {
  final case class UserCreated(userId: UserId, operationId: OperationId, createdAt: LocalDateTime, name: String, email: Email)
      extends UserEvent
  final case class NameUpdated(userId: UserId, operationId: OperationId, createdAt: LocalDateTime, newName: String)  extends UserEvent
  final case class EmailUpdated(userId: UserId, operationId: OperationId, createdAt: LocalDateTime, newEmail: Email) extends UserEvent
}
