#!/bin/bash

chmod +x gradlew

./gradlew clean build -x test

docker-compose up --build