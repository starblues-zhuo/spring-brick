#!/bin/sh

# linux、mac上打包的脚本
mvn clean install -Dmaven.test.skip=true
# del example-dist
rm -rf dist

# create example-dist
mkdir dist
mkdir dist/plugins
mkdir dist/pluginConfig

# copy main program and config
cp integration-mybatisplus-main/target/*-exec.jar dist
cp integration-mybatisplus-main\src\main\resources\application-prod.yml dist

cd dist

#run main
mv *-exec.jar integration-mybatisplus-start.jar
mv application-prod.yml application.yml
java -jar integration-mybatisplus-start.jar --spring.config.location=application.yml
