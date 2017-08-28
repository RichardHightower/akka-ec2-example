package com.rick.user

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory, ConfigResolveOptions}

import scala.concurrent.{ExecutionContextExecutor, Future}
//Import ask pattern
import akka.pattern.ask

import scala.concurrent.duration._
import scala.io.StdIn

//Enable Scala postfix operations
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

import scala.language.postfixOps

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userIdFormat: RootJsonFormat[UserId] = jsonFormat1(UserId)
  implicit val userAliasFormat: RootJsonFormat[UserAlias] = jsonFormat1(UserAlias)
  implicit val userResponseFormat: RootJsonFormat[UserResponse] = jsonFormat2(UserResponse)
}

object RestServer extends JsonSupport with App {

  val config = if (args.length > 0) {
    ConfigFactory.load(args(0))
  } else {
    ConfigFactory.load()
  }


  implicit val system = ActorSystem("user-microservice", config)
  implicit val materializer = ActorMaterializer()

  //val userAliasActorRef: ActorRef = UserAliasActor(system)
  val userAliasUrl = config.getConfig("user-alias").getString("url")
  println(s"connecting to remote server $userAliasUrl")
  val userAliasActor = system.actorSelection(userAliasUrl)
  implicit val timeout = Timeout(5 seconds)


  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val bindingFuture = start(config)



  sys.addShutdownHook({
    stop(bindingFuture)
  })


  def start(config : Config): Future[ServerBinding] = {
    val bindConf = config.getConfig("bind-rest")
    val host = bindConf.getString("host")
    val port = bindConf.getString("port")
    println(s"Starting server at http://$host:$port/")
    Http().bindAndHandle(route, host, port.toInt)
  }

  def route: Route = path("tags") {
    post {
      entity(as[UserId]) { userId =>
        val future = (userAliasActor ? userId).mapTo[UserResponse]
        onSuccess(future) { response: UserResponse =>
          complete(response)
        }
      }
    }
  } ~ path("atags") {
    post {
      entity(as[UserAlias]) { userAlias =>
        val future = (userAliasActor ? userAlias).mapTo[UserResponse]
        onSuccess(future) { response: UserResponse =>
          complete(response)
        }
      }
    }
  }

  def stop(bindingFuture: Future[ServerBinding]): Unit = {
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}
