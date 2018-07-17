package info.ludwikowski.es.user

import java.util.UUID

import com.softwaremill.tagging.{@@, Tagger}

sealed trait OperationIdTag
sealed trait UserIdTag
sealed trait EmailTag

package object domain {

  type OperationId = UUID @@ OperationIdTag
  type UserId      = UUID @@ UserIdTag
  type Email       = String @@ EmailTag

  object OperationId {
    def apply(value: UUID): OperationId = value.taggedWith[OperationIdTag]
  }

  object UserId {
    def apply(value: UUID): UserId = value.taggedWith[UserIdTag]
  }

  object Email {
    def apply(value: String): Email = value.taggedWith[EmailTag]
  }
}
