package info.ludwikowski.es.user.application

import java.util.UUID

import akka.actor.{ActorRef, DiagnosticActorLogging}
import akka.event.Logging.MDC
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.softwaremill.tagging.@@
import info.ludwikowski.es.user.application.UserActorQuery.GetUser
import info.ludwikowski.es.user.application.UserActorResponse.{CommandProcessed, UserNotCreatedError}
import info.ludwikowski.es.user.domain.{User, UserCommand, UserEvent, UserId}
import info.ludwikowski.es.user.domain.UserCommand.CreateUser
import info.ludwikowski.es.user.domain.UserEvent.UserCreated

import scala.util.{Failure, Success}

trait UserActorTag

class UserActor extends PersistentActor with DiagnosticActorLogging {

  private var userState: Option[User] = None

  override def persistenceId: String = self.path.name

  log.info("Starting UserActor {}", persistenceId)

  override def receiveCommand: Receive = waitingForCreation

  private def waitingForCreation: Receive = {
    case command: CreateUser => {
      val currentSender = sender()
      val event         = command.toUserCreated()
      persist(event) { event =>
        updateState(event)
      }
      currentSender ! CommandProcessed(List(event))
      context.become(userCreated)
    }
  }

  private def userCreated: Receive = {
    case command: UserCommand => process(sender(), command)
    case _: GetUser =>
      userState.foreach { user =>
        sender() ! user
      }
  }

  private def process(currentSender: ActorRef, command: UserCommand): Unit = {
    userState.foreach { user =>
      user.process(command) match {
        case Success(events) =>
          persistAll(events) { event =>
            updateState(event)
          }
          currentSender ! CommandProcessed(events)
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

  override def unhandled(msg: Any): Unit = msg match {
    case _ =>
      if (userState.isEmpty) {
        log.error("User {} is not created, rejecting {}", persistenceId, msg)
        sender() ! UserNotCreatedError(UserId(UUID.fromString(persistenceId)))
      } else {
        log.error("Received unhandled message: {}", msg)
        super.unhandled(msg)
      }
  }

  override def mdc(currentMessage: Any): MDC =
    Map("persistenceId" -> persistenceId)
}

object UserActor {
  val Name = "UserActor"

  type UserRegion = ActorRef @@ UserActorTag
}
