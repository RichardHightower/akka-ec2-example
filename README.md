# Akka EC2 Example

The objective is just to create a simple Akka EC2 example. 
The example is about Terraform, Packr, EC2, Docker, ECS, Docker Compose, etc. 

## Tools 

* IA will be *Terraform* 
* Build tool will be *gradle*
* *Packr* will be used to create docker images and EC2 AMIs
* Local deployment will be possible with Docker compose 
* *Ansible* playbooks will be used to provision 
* Deployment images with be CentOS 
* MySQL / Amaon RDS 
* Memcached / AWS


## Focus
* Terraform
* Packr
* EC2
* ECS
* Amazon ELB / Amazon ALB
* Docker
* Scala/Akka


## Objectives

Build a simple Akka application that uses ***Akka remoting***, ***memcache*** and ***MySQL***. 
The application is more about the deployment than Akka per se. 
I could have used Spring Boot or Vert.x or QBit (and may in the future). 
The focuse is more on DevOps support and dev flow. 

There will be a database, an Akka remote service, an Akka microservice. 
The Akka microservice will talk to a remote service to look up tags associated with a user, and a user alias. 
It is a completely fictional application. 
The Akka microservice will live behind a load balancer. 
The Akka remote service will run on another server (or another docker instance). 
The Akka remote service will look up tags associated with the user and save them. 

Let's call the microservice the `UserService`. 
Let's call the remote service the `UserTagService`. 

### UserService

Creates aliases and saves them in `MySQL` and `Memcached`. 

* `findTagsByUserId` (`UserId`) returns `UserAlias` and `List[Tags]`
* `findTagsByUserAlias` (`UserAlias`) returns `UserAlias` and `List[Tags]`
* Available via REST
* Stateless 

The `findTagsByUserId` will check memcached for tags (`List[Tags]`) and an `UserAlias` based on `UserId`. 
If data not found, it will call `UserTagService` to get tags (`List[Tag]`) by user id. 
The `findTagsByUserId` will be represented as a `POST` in ***Akka HTTP*** because if not found, it can create tags (using `UserTagService`) and create a user alias (`UserAlias`). It is responsible for storing the user alias, and it is responsible for storing data in memcached by `UserId` and by `UserAlias`. The tags should be available for two weeks. If the tags were not found by `UserTagService` than the tags are available in the cache for two hours. Memcache will store `UserAlias` -> `UserId`, `List[Tag]`. If the user alias is not found in memcached, it is checked for in MySQL before being created. 

The `getTagsByUserAlias` will check memcached for data (`UserAlias`, `List[Tags]`) in memcached by alias. 
If found the  `UserAlias` key in memcached will result in the `UserId`. If the list of tags is empty, then `getTagsByUserAlias` will call `UserTagService`.



#### SQL Pseudocode for UserID to UserAlias mapping
```sql
create table UserAlias (
  userId varchar[30] Primary Key, 
  userAlias varchar[255] Index
)
```

#### JSON Pseudocode stored in Memcached 
```json
{
    "userId" : "12345",
    "userAlias" :  "5678",
    "tags" : ["Blue", "Fish", "California"]
}
```

#### Amazon specs (Terraform)
* Needs to be behind a load balancer (Amazon ELB or ALB). 
* Needs to be in an autoscale group (3 min, 3 max). 
* Stateless. 
* Can benefit from ECS deployment 
* Example will support ECS and EC2 
* Future: replicate logs to CloudWatch 
* Should be in same subnet as 1 UserTagService
* Needs Elastic Cache for Memcached 
* Needs RDS for MySQL 

#### Docker compose 
* Needs to run with 1 instance of `UserTagService`
* Needs 1 memcache instance

### UserTagService
The `UserTagService` will randomly generate tags. It will use Akka Actor scheduling to randomly generate tags after a few minutes. It will talk to an internal actor async using Akka Futures. 

It will store the tags locally in memory in rocksDB, in MySQL and in Memcache. It only knows about `UserIds` (not `UserAlias`). 

#### SQL Pseudocode for UserID to Tags mapping
```sql
create table UserTags (
  userId varchar[30] Primary Key, 
  tags varchar[1024] 
)
```

#### JSON Pseudocode stored in Memcached 
```json
{
    "userId" : "12345",
    "tags" : ["Blue", "Fish", "California"]
}
```

## Simple Diagram

```
+-----------------+             +----------------+
|                 |             |                |
|                 |             |                |
|  UserTagService |             | UserService    |
|                 <----+--------+                |
|                 |    ^        |                |
|                 |    |        |                |                  +---------------------+
|                 |    |        |                |                  |                     |
+-----------------+    |        +--------------^-+                  |                     |
                       |                       +--------------------+                     |
+------------------+   |                                            |     ELB             |
|                  |   |        +----------------+                  |                     |
|                  |   |        |                |                  |                     |
|   Memcached      |   |        |                |                  |                     |
|                  |   |        |  UserService   ^                  |                     |
|                  |   <--------+                |                  |                     |
|                  |   |        |                |                  |                     |
|                  |   |        |                +------------------+                     |
+------------------+   |        +----------------+                  |                     |
                       |                                            |                     |
                       |                                            +-----+---------------+
 +---------------+     |        +----------------+                        |
 |               |     |        |                |                        |
 |               |     |        |                |                        |
 |  MySQL        |     |        |  UserService   |                        |
 |               |     |        |                |                        |
 |               |     +--------+                <------------------------+
 |               |              |                |
 +---------------+              |                |
                                +----------------+

```

