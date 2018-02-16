#!/usr/bin/env bash

export KC_REALM=cryptowatch
export KC_USERNAME=rob
export KC_PASSWORD=TODO
export KC_CLIENT=sink-frontend
export KC_SERVER=localhost:8280
export KC_CONTEXT=auth

# Request Tokens for credentials
export KC_RESPONSE=$( \
   curl -k -v -X POST \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "username=$KC_USERNAME" \
        -d "password=$KC_PASSWORD" \
        -d 'grant_type=password' \
        -d "client_id=$KC_CLIENT" \
        -d "client_secret=$KC_CLIENT_SECRET" \
        http://$KC_SERVER/$KC_CONTEXT/realms/$KC_REALM/protocol/openid-connect/token \
        |jq .
)


echo Response=$KC_RESPONSE

export KC_ACCESS_TOKEN=$(echo $KC_RESPONSE| jq -r .access_token)
echo $KC_ACCESS_TOKEN