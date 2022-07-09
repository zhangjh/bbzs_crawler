#!/bin/bash
work_dir=`pwd`
## 0. 启动crawler
jps | grep -q StartApplication
if [ $? -ne 0 ];then
    mvn clean package -Dmaven.test.skip && java -jar -Dspring.profiles.active=prod start/target/start-*.jar &
fi
## 1. 启动admin
jps | grep -q XxlJobAdminApplication
if [ $? -ne 0 ];then
    cd ${work_dir}/ext_libs/xxl-job-2.3.1/xxl-job-admin
    mvn clean compile && mvn spring-boot:run &
fi