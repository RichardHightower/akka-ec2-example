#!/usr/bin/env bash

set -e

cd userService
gradle clean installDist

cd ../userAliasService
gradle clean installDist

docker-compose kill
docker-compose rm
docker-compose build
docker-compose up
