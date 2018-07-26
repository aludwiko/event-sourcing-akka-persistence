package info.ludwikowski.es.user.domain

import java.util.UUID

import info.ludwikowski.es.user.domain.UserCommand.CreateUser
import org.scalacheck.Gen

trait UserDomainGenerators {

  def randomOperationId(): OperationId = OperationId(UUID.randomUUID())

  def randomUserId(): UserId = UserId(UUID.randomUUID())

  def createUserGen: Gen[CreateUser] =
    for {
      name <- Gen.listOfN(10, Gen.alphaLowerChar).map(_.mkString)
      email = Email(s"$name@domain.com")
    } yield CreateUser(randomUserId(), randomOperationId(), name, email)

  def randomCreateUser(): CreateUser = createUserGen.sample.get
}
