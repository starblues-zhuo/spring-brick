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
cp integration-mybatis-main/target/*-exec.jar dist
cp integration-mybatis-main/src/main/resources/application-prod.yml dist

# copy plugin and config
cp plugins/integration-mybatis-plugin1/target/*-jar-with-dependencies.jar dist/plugins
cp plugins/integration-mybatis-plugin1/src/main/resources/plugin1.yml dist/pluginConfig

cp plugins/integration-mybatis-plugin2/target/*-jar-with-dependencies.jar dist/plugins
cp plugins/integration-mybatis-plugin2/src/main/resources/plugin2.yml dist/pluginConfig

cd dist

#run main
mv *-exec.jar integration-mybatis-start.jar
mv application-prod.yml application.yml
java -jar integration-mybatis-start.jar --spring.config.location=application.yml