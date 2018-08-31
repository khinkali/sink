#!/usr/bin/env bash

echo "stop and rm zipkin"
docker stop zipkin && docker rm zipkin

echo "start zookeeper"
docker run --name zipkin -d \
--net cryptowatch \
-p 9411:9411 \
openzipkin/zipkin:2.8.4
