package com.rick.user

case class UserId(userId: String)

case class UserAlias(userAlias: String)

case class Tags(tags: Seq[String])

case class UserResponse(userAlias: String, tags: Seq[String])

