#!/bin/sh

docker run -e "SPRING_PROFILES_ACTIVE=dev-docker" -p 8080:8080 -t user-service
