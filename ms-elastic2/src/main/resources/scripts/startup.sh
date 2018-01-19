#!/bin/bash

ps -ef|grep elastic-246|awk '{print $2}'|xargs kill -9

rm ./*.log

nohup java -jar -Dspring.profiles.active=development -Dserver.port=10021 ./elastic-246.jar > ./server_1.log &

nohup java -jar -Dspring.profiles.active=development -Dserver.port=10022 ./elastic-246.jar > ./server_2.log &
