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
cp basic-example-main/target/basic-example-main-*-exec.jar dist
cp basic-example-main/src/main/resources/application-prod.yml dist

# copy plugin and config
cp plugins/basic-example-plugin1/target/*-jar-with-dependencies.jar dist/plugins
cp plugins/basic-example-plugin1/src/main/resources/plugin1.yml dist/pluginConfig

cp plugins/basic-example-plugin2/target/*-jar-with-dependencies.jar dist/plugins
cp plugins/basic-example-plugin2/src/main/resources/plugin2.yml dist/pluginConfig

cd basic

#run main
mv basic-example-main-*-exec.jar basic-example-start.jar
mv application-prod.yml application.yml
java -jar basic-example-start.jar --spring.config.location=application.yml