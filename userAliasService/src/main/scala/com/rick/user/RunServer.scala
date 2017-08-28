package com.rick.user

import akka.actor.{ActorRef, ActorSystem}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn


object RunServer extends App {


  val config = if (args.length > 0) {
    ConfigFactory.load(args(0))
  } else {
    ConfigFactory.load("user-alias-application.conf")
  }

  implicit val system = ActorSystem("UserAliasServer", config)
  implicit val materializer = ActorMaterializer()

  println("Starting user alias service")


  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val userAliasActorRef: ActorRef = UserAliasActor(system)


  sys.addShutdownHook({
    system.terminate()
  })

}
