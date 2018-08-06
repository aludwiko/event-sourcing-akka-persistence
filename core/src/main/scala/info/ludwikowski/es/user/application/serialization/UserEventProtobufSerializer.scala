package info.ludwikowski.es.user.application.serialization

import java.time.Instant
import java.util.UUID

import akka.serialization.SerializerWithStringManifest
import info.ludwikowski.es.user.domain.{Email, OperationId, UserEvent, UserId}

@SuppressWarnings(Array("org.wartremover.warts.Throw"))
class UserEventProtobufSerializer extends SerializerWithStringManifest with ToDomain with ToSerializable {

  override def identifier: Int = 10000

  override def manifest(obj: AnyRef): String = obj.getClass.getName

  override def toBinary(obj: AnyRef): Array[Byte] = obj match {
    case marketEvent: UserEvent =>
      marketEvent match {
        case event: UserEvent.UserCreated  => UserCreatedEvent.toByteArray(toSerializable(event))
        case event: UserEvent.NameUpdated  => NameUpdatedEvent.toByteArray(toSerializable(event))
        case event: UserEvent.EmailUpdated => EmailUpdatedEvent.toByteArray(toSerializable(event))
      }
    case _ =>
      throw new IllegalStateException(s"Serialization for $obj not supported. Check toBinary in ${this.getClass.getName}.")
  }

  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {
    if (manifest == classOf[UserEvent.UserCreated].getName) {
      toDomain(UserCreatedEvent.parseFrom(bytes))
    } else if (manifest == classOf[UserEvent.NameUpdated].getName) {
      toDomain(NameUpdatedEvent.parseFrom(bytes))
    } else if (manifest == classOf[UserEvent.NameUpdated].getName) {
      toDomain(EmailUpdatedEvent.parseFrom(bytes))
    } else {
      throw new IllegalStateException(
        s"Deserialization for $manifest not supported. Check fromBinary method in ${this.getClass.getName} class."
      )
    }
  }

}

trait ToSerializable {
  def toSerializable(event: UserEvent.UserCreated): UserCreatedEvent = {
    UserCreatedEvent(event.userId.toString, event.operationId.toString, event.createdAt.toEpochMilli, event.name, event.email)
  }

  def toSerializable(event: UserEvent.NameUpdated): NameUpdatedEvent = {
    NameUpdatedEvent(event.userId.toString, event.operationId.toString, event.createdAt.toEpochMilli, event.newName)
  }

  def toSerializable(event: UserEvent.EmailUpdated): EmailUpdatedEvent = {
    EmailUpdatedEvent(event.userId.toString, event.operationId.toString, event.createdAt.toEpochMilli, event.newEmail)
  }
}

trait ToDomain {
  def toDomain(event: UserCreatedEvent): UserEvent.UserCreated = {
    UserEvent.UserCreated(
      UserId(UUID.fromString(event.userId)),
      OperationId(UUID.fromString(event.operationId)),
      Instant.ofEpochMilli(event.createdAt),
      event.name,
      Email(event.email)
    )
  }

  def toDomain(event: NameUpdatedEvent): UserEvent.NameUpdated = {
    UserEvent.NameUpdated(
      UserId(UUID.fromString(event.userId)),
      OperationId(UUID.fromString(event.operationId)),
      Instant.ofEpochMilli(event.createdAt),
      event.newName
    )
  }

  def toDomain(event: EmailUpdatedEvent): UserEvent.EmailUpdated = {
    UserEvent.EmailUpdated(
      UserId(UUID.fromString(event.userId)),
      OperationId(UUID.fromString(event.operationId)),
      Instant.ofEpochMilli(event.createdAt),
      Email(event.newEmail)
    )
  }
}
