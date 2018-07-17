package info.ludwikowski.es.user.domain

import java.time.LocalDateTime

import info.ludwikowski.es.user.domain.UserCommand.{CreateUser, UpdateEmail, UpdateName}
import info.ludwikowski.es.user.domain.UserEvent.{EmailUpdated, NameUpdated, UserCreated}

import scala.util.{Failure, Success, Try}

final case class User private[domain] (userId: UserId, createdAt: LocalDateTime, name: String, email: Email) {

  def applyEvent(userEvent: UserEvent): Try[User] = userEvent match {
    case _: UserCreated      => Failure(new IllegalStateException("User already created. Event cannot be applied."))
    case event: NameUpdated  => Success(copy(name = event.newName))
    case event: EmailUpdated => Success(copy(email = event.newEmail))
  }

  def process(userCommand: UserCommand): Try[List[UserEvent]] = userCommand match {
    case _: CreateUser        => Failure(new IllegalStateException("User already created. Command cannot be processed."))
    case command: UpdateName  => updateName(command)
    case command: UpdateEmail => updateEmail(command)
  }

  def updateName(command: UpdateName): Try[List[UserEvent]] = {
    //complex business logic that might failed
    Success(List(command.toNameUpdated()))
  }

  def updateEmail(command: UpdateEmail): Try[List[UserEvent]] = {
    //complex business logic that might failed
    Success(List(command.toEmailUpdated()))
  }
}

object User {
  def from(userCreated: UserCreated): User =
    User(userCreated.userId, userCreated.createdAt, userCreated.name, userCreated.email)
}
