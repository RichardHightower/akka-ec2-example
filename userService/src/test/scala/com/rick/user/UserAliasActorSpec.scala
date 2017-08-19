package com.rick.user

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}


class UserAliasActorSpec(_system: ActorSystem)
  extends TestKit(_system)
    with Matchers
    with FlatSpecLike
    with BeforeAndAfterAll {


  def this() = this(ActorSystem("UserService"))

  override def afterAll: Unit = {
    shutdown(system)
  }

  "A User Service" should "be able to handle messages" in {

    val userAliasActorRef: ActorRef = UserAliasActor(system)
    userAliasActorRef ! UserAlias("12345")
    userAliasActorRef ! UserId("12345")
  }


}