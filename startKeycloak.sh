#!/usr/bin/env bash

docker stop keycloak && docker rm keycloak
docker run -d \
  --name keycloak \
  --net cryptowatch \
  -e KEYCLOAK_USER=admin \
  -e KEYCLOAK_PASSWORD=admin \
  -e KAFKA_ADDRESS=kafka:9092 \
  -p 8280:8080 \
  -v ~/keycloakdata/:/opt/jboss/keycloak/standalone/data \
  khinkali/keycloak:0.0.46
