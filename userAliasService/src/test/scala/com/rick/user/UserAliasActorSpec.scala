package com.rick.user

import akka.actor.{ActorRef, ActorSystem}

//Import ask pattern
import akka.pattern.ask
import akka.testkit.{TestKit, TestProbe}
//Timeout needed for ask pattern
import akka.util.Timeout

//Import scala test see http://www.scalatest.org/user_guide/using_matchers
import org.scalatest._

import scala.concurrent.duration._

//Import Future and Await
import scala.concurrent.{Await, Future}

//Enable Scala postfix operations
import scala.language.postfixOps


class UserAliasActorSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {

  implicit val timeout = Timeout(5 seconds)

  def this() = this(ActorSystem("UserService"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A User Service" should
    """respond with an
    empty set of tags and alias 'ABC' for user id '1234'""" in {

    val testProbe = TestProbe()
    //mocks an actor
    val responseActor = testProbe.ref
    //response actor
    val userAliasActorRef: ActorRef = UserAliasActor(system, localOnly = true)

    // Pass the UserId 1234 and expect UserResponse ("ABC", empty)
    userAliasActorRef.tell(UserId("1234"), responseActor)

    // Expects an async response in 500 millis of UserResponse ("ABC", empty)
    testProbe.expectMsg(500 millis, UserResponse("ABC", Seq.empty[String]))

  }

  "A User Service" should
    """respond with an
    empty set of tags and alias 'ABC' for user alias 'ABC'""" in {

    val userAliasActorRef: ActorRef = UserAliasActor(system, localOnly = true)

    // Pass the UserAlias ABC and expect UserResponse ("ABC", empty)

    //Use ask pattern
    val future = userAliasActorRef ? UserAlias("ABC")
    //Convert Future[Any] to Future[UserResponse]
    val responseFuture: Future[UserResponse] = future.mapTo[UserResponse]

    // Expects an async response in 500 millis of UserResponse ("ABC", empty)
    val result = Await.result(responseFuture, 500 millis)
    result.tags should be('empty)
    result.userAlias should equal("ABC")

  }

}