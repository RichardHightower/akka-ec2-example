# README

## Testing server

#### Send userId
```sh
curl -v -H "Content-Type: application/json" -X POST -d '{"userId":"1234"}' \
http://localhost:8080/tags
```
#### Send userAlias
```sh
curl -v -H "Content-Type: application/json" -X POST -d '{"userAlias":"ABC"}' \
http://localhost:8080/atags
```
#### Output
```javascript
{"userAlias":"ABC","tags":[]}`
```

### Docs of note


This shows getting responses from an actor

http://doc.akka.io/docs/akka/current/scala/guide/tutorial_3.html

An actor has references to its sender.


Akka HTTP Go through DSL
http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/directives/marshalling-directives/entity.html#entity


Swagger Akka
https://blog.codecentric.de/en/2016/04/swagger-akka-http/



DSL for testing
http://doc.akka.io/docs/akka-http/current/scala/http/routing-dsl/testkit.html

Examples
https://github.com/akka/akka-http/tree/v10.0.9/akka-http-tests/src/test/scala/akka/http/scaladsl/server/directives/

DSL
http://www.scalatest.org/

Scala test matchers
http://www.scalatest.org/user_guide/using_matchers

Remoting
http://doc.akka.io/docs/akka/current/scala/remoting.html
http://doc.akka.io/docs/akka/current/scala/remoting-artery.html

Using futures
http://doc.akka.io/docs/akka/current/scala/futures.html

Spray
http://doc.akka.io/docs/akka-http/current/scala/http/common/json-support.html#akka-http-spray-json

Testing examples
http://blog.madhukaraphatak.com/akka-http-testing/

Remoting example
https://alvinalexander.com/scala/simple-akka-actors-remote-example
