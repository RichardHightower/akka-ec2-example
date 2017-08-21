package com.rick.user

case class UserId(userId: String)

case class UserAlias(userAlias: String)

case class Tags(tags: Seq[String])

case class UserResponse(userAlias: String, tags: Seq[String])


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json._

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userIdFormat = jsonFormat1(UserId)
  implicit val userAliasFormat = jsonFormat1(UserAlias)
  implicit val userResponseFormat = jsonFormat2(UserResponse)
}

