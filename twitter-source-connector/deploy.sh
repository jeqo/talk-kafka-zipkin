#!/usr/bin/env bash
CONNECT_URL="http://localhost:8083"
curl -XPOST -H 'Content-Type:application/json' -d @twitter-source.json ${CONNECT_URL}/connectors