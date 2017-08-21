package com.rick.user

import akka.actor.{ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.util.Timeout
//Import ask pattern
import akka.pattern.ask

import scala.concurrent.duration._
import scala.io.StdIn

//Enable Scala postfix operations
import scala.language.postfixOps


object RestServer extends App with JsonSupport {

  implicit val system = ActorSystem("user-microservice")
  implicit val materializer = ActorMaterializer()

  val userAliasActorRef: ActorRef = UserAliasActor(system)
  implicit val timeout = Timeout(5 seconds)


  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher


  val route = path("tags") {
    post {
      entity(as[UserId]) { userId =>
        val future = (userAliasActorRef ? userId).mapTo[UserResponse]
        onSuccess(future) { response: UserResponse =>
          complete(response)
        }
      }
    }
  } ~ path("atags") {
    post {
      entity(as[UserAlias]) { userAlias =>
        val future = (userAliasActorRef ? userAlias).mapTo[UserResponse]
        onSuccess(future) { response: UserResponse =>
          complete(response)
        }
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
