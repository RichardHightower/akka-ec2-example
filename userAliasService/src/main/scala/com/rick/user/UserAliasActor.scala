package com.rick.user

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object UserAliasActor {
  def apply(system: ActorSystem, localOnly: Boolean = false): ActorRef = {

    if (localOnly) {
      system.actorOf(props())
    } else {
      system.actorOf(props(), "userAlias")
    }
  }

  def props(): Props = Props[UserAliasActor]
}

class UserAliasActor extends Actor {

  /** Map that holds mappings to response data from user id. */
  val userIdToResponseMap: Map[String, UserResponse] = Map(
    ("1234", UserResponse("ABC", Seq()))
  )

  /** Map that holds mappings to userId from userAlias. */
  val userAliasMap: Map[String, String] = Map(
    ("ABC", "1234")
  )

  def receive: Receive = {
    //Grab the sender (for tell and ask), and send a response from the map.
    // sender refers to actor who sent the message.
    case UserId(userId) =>
      sender ! userIdToResponseMap(userId)

    case UserAlias(userAlias) =>
      sender ! userIdToResponseMap(userAliasMap(userAlias))
  }
}
