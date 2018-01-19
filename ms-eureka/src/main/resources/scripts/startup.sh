#!/bin/bash

ps -ef|grep ms-eureka|awk '{print $2}'|xargs kill -9

rm ./*.log

nohup java -jar -Dspring.profiles.active=server1 ./ms-eureka.jar > ./server1.log &

nohup java -jar -Dspring.profiles.active=server2 ./ms-eureka.jar > ./server2.log &
