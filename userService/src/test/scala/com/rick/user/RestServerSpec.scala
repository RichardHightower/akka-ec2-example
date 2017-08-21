package com.rick.user

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.Future


class RestServerSpec extends WordSpec with Matchers with ScalatestRouteTest
  with JsonSupport with BeforeAndAfterAll {

  var webServerFuture: Future[ServerBinding] = null

  override def beforeAll: Unit = {
    webServerFuture = RestServer.start()
  }

  override def afterAll: Unit = {
    RestServer.stop(webServerFuture)
  }

  def createPostRequest(path: String, jsonRequest: String): HttpRequest = {
    HttpRequest(
      HttpMethods.POST,
      uri = path,
      entity = HttpEntity(MediaTypes.`application/json`, jsonRequest))
  }

  "The service" should {

    "accept a post of userAlias and return a UserResponse" in {

      val jsonRequest ="""{"userAlias":"ABC"}"""

      val postRequest = createPostRequest("/atags", jsonRequest)

      val route = Route.seal(RestServer.route)

      postRequest ~> route ~> check {
        status should equal(StatusCodes.OK)
        responseAs[UserResponse] should equal(UserResponse("ABC", Seq.empty[String]))
      }

    }
  }

  "The service" should {

    "accept a post of userId and return a UserResponse" in {

      val jsonRequest ="""{"userId":"1234"}"""

      val postRequest = createPostRequest("/tags", jsonRequest)

      val route = Route.seal(RestServer.route)

      postRequest ~> route ~> check {
        status should equal(StatusCodes.OK)
        responseAs[UserResponse] should equal(UserResponse("ABC", Seq.empty[String]))
      }

    }
  }

}
