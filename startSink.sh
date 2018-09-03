#!/usr/bin/env bash

echo "stop and rm sink"
docker stop sink && docker rm sink

echo "start sink"
docker run --name sink -d \
--net cryptowatch \
-p 8080:8080 \
-e KAFKA_ADDRESS=kafka:9092 \
-e KEYCLOAK_URL=http://keycloak:8280/auth \
-e VERSION=1.0.0 \
-e zipkin.uri=http://zipkin:9411 \
robertbrem/sink:1.0.0
