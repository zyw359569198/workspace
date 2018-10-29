#!/bin/bash
while true
do
        echo "sleep 5m"
        sleep 5m
        COUNT=`ps -ef|grep java|grep -v grep|wc -l`
        if [ ${COUNT} -eq 0 ];then
                systemctl restart mysql
                echo "restart mysql!"
                sleep 30
                nohup java -jar -Xms256m -Xmx256m -XX:+UseG1GC -XX:+PrintGCDetails  -XX:+PrintGCDateStamps -XX:MetaspaceSize=20M -XX:MaxMetaspaceSize=64m -XX:CompressedClassSpaceSize=20m -XX:MinMetaspaceFreeRatio=40 -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/root/  /root/NovelGame-0.0.1-SNAPSHOT/NovelGame-0.0.1-SNAPSHOT.jar >/root/nova.log &
                echo "restart novelGame!"
                sleep 300
                curl http://localhost:8080/collect/initData
                echo "collect start!"
        fi
        MYSQL_COUNT=`ps -ef|grep mysql|grep -v grep|wc -l`
        if [ ${MYSQL_COUNT} -eq 0 ];then
                PID=`ps -ef|grep java|grep -v grep|awk -F " " '{print $2}'`
                kill -9 ${PID}
                echo "kill java process!"
                systemctl restart mysql
                echo "restart broken mysql!"
        fi
done
