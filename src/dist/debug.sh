#!/bin/bash

AUTOREDISTRICT_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8888"
export AUTOREDISTRICT_OPTS

./bin/autoredistrict