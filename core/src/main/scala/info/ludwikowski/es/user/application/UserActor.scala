package info.ludwikowski.es.user.application

import akka.persistence.{PersistentActor, RecoveryCompleted}
import info.ludwikowski.es.user.domain.{User, UserCommand, UserEvent}
import info.ludwikowski.es.user.domain.UserCommand.CreateUser
import info.ludwikowski.es.user.domain.UserEvent.UserCreated

import scala.util.{Failure, Success}

class UserActor extends PersistentActor {

  private var userState: Option[User] = None

  override def persistenceId: String = self.path.name

  override def receiveCommand: Receive = waitingForCreation

  private def waitingForCreation: Receive = {
    case command: CreateUser => {
      persist(command.toUserCreated()) { event =>
        updateState(event)
      }
      context.become(userCreated)
    }
  }

  private def userCreated: Receive = {
    case command: UserCommand => process(command)
  }

  private def process(command: UserCommand): Unit = {
    userState.foreach { user =>
      user.process(command) match {
        case Success(events) =>
          persistAll(events) { event =>
            updateState(event)
          }
        case Failure(_) => //TODO handling exception
      }
    }
  }

  private def updateState(userEvent: UserEvent): Unit = userEvent match {
    case userCreated: UserCreated =>
      if (userState.isEmpty) {
        userState = Some(User.from(userCreated))
      } else {
        //TODO handling wrong behaviour
      }
    case _ =>
      userState = userState.map { user =>
        user.applyEvent(userEvent) match {
          case Success(user) => user
          case Failure(ex)   =>
            //TODO exception in applying event indicates a major problem
            throw ex
        }
      }
  }

  override def receiveRecover: Receive = {
    case event: UserEvent => updateState(event)
    case _: RecoveryCompleted =>
      if (userState.isEmpty) {
        context.become(waitingForCreation)
      } else {
        context.become(userCreated)
      }
  }
}
