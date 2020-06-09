#!/bin/bash

chmod +x gradlew

./gradlew clean build

docker-compose up --build