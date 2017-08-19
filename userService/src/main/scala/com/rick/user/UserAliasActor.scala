package com.rick.user

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object UserAliasActor {
  def apply(system: ActorSystem): ActorRef = {
    system.actorOf(props())
  }

  def props(): Props = Props[UserAliasActor]
}

class UserAliasActor extends Actor {
  def receive = {
    case UserId(userId) =>
      println(s"$userId")
    case UserAlias(userAlias) =>
      println(s"$userAlias")
  }
}
