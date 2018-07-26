package info.ludwikowski.es.user.application

import akka.pattern._
import info.ludwikowski.es.base.BaseActorSystemSpec
import info.ludwikowski.es.user.UserModule
import info.ludwikowski.es.user.application.UserActorResponse.CommandProcessed
import info.ludwikowski.es.user.domain.UserCommand.UpdateName
import info.ludwikowski.es.user.domain.{Email, User, UserCommand, UserDomainGenerators}
import org.scalatest.FlatSpecLike

class UserActorSpec extends BaseActorSystemSpec with UserModule with UserDomainGenerators with FlatSpecLike {

  "UserActor" should "create user" in {
    // given

    // when
    val result = (userRegion ? randomCreateUser()).mapTo[CommandProcessed].futureValue

    // then
    //TODO assertion should be more strong, e.g. validate events in details
    result.events should have size (1)
  }

  it should "update user name" in {
    // given
    val name              = "bob"
    val newName           = "alice"
    val createUserCommand = randomCreateUser().copy(name = name)
    (userRegion ? createUserCommand).mapTo[CommandProcessed].futureValue
    (userRegion ? UpdateName(createUserCommand.userId, randomOperationId(), newName)).mapTo[CommandProcessed].futureValue

    // when
    val user = (userRegion ? UserActorQuery.GetUser(createUserCommand.userId)).mapTo[User].futureValue

    // then
    user.name shouldBe newName
  }

  it should "update user email" in {
    // given
    val email             = Email("bob@domain.com")
    val newEmail          = Email("alice@domain.com")
    val createUserCommand = randomCreateUser().copy(email = email)
    (userRegion ? createUserCommand).mapTo[CommandProcessed].futureValue
    (userRegion ? UserCommand.UpdateEmail(createUserCommand.userId, randomOperationId(), newEmail)).mapTo[CommandProcessed].futureValue

    // when
    val user = (userRegion ? UserActorQuery.GetUser(createUserCommand.userId)).mapTo[User].futureValue

    // then
    user.email shouldBe newEmail
  }
}
