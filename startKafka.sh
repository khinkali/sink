#!/usr/bin/env bash

echo "stop and rm kafka"
docker stop kafka && docker rm kafka

echo "stop and rm zookeeper"
docker stop zookeeper && docker rm zookeeper

echo "start zookeeper"
docker run --name zookeeper -d \
--net cryptowatch \
-p 2181:2181 \
-e ZOOKEEPER_ID="1" \
-e ZOOKEEPER_SERVER_1=kafka-zoo-svc \
-v ~/Desktop/zookeeperdataconf/:/opt/zookeeper/conf \
-v ~/Desktop/zookeeperdatalib/:/var/lib/zookeeper \
digitalwonderland/zookeeper

echo "start kafka"
docker run --name kafka -d -p 9092:9092 \
--net cryptowatch \
--hostname "kafka" \
-e ENABLE_AUTO_EXTEND="true" \
-e KAFKA_RESERVED_BROKER_MAX_ID="999999999" \
-e KAFKA_AUTO_CREATE_TOPICS_ENABLE="true" \
-e KAFKA_PORT="9092" \
-e KAFKA_ADVERTISED_PORT="9092" \
-e KAFKA_ADVERTISED_HOST_NAME="kafka" \
-e KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 \
-v ~/Desktop/kafkadata/:/kafka \
wurstmeister/kafka
