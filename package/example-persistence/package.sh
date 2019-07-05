#!/bin/sh

cd ../../
# linux、mac上打包的脚本
mvn clean install -Dmaven.test.skip=true
# del example-dist
rm -rf example-persistence-dist

# create example-dist
mkdir example-persistence-dist
mkdir example-persistence-dist/plugins
mkdir example-persistence-dist/pluginConfig

# copy main program and config
cp plugin-example-persistence/plugin-example-persistence-main/target/plugin-example-persistence-main-*-exec.jar example-persistence-dist
cp plugin-example-persistence/plugin-example-persistence-main/src/main/resources/application-prod.yml example-persistence-dist

# copy plugin and config
cp plugin-example-persistence/plugin-example-persistence-main/target/plugin-example-persistence-main-*-exec.jar example-persistence-dist
cp plugin-example-persistence/plugin-example-persistence-main/src/main/resources/application-prod.yml example-persistence-dist

cp plugin-example-persistence/plugins/plugin-example-persistence-plugin2/target/plugin-example-persistence-plugin2-*-jar-with-dependencies.jar example-persistence-dist/plugins
cp plugin-example-persistence/plugins/plugin-example-persistence-plugin2/src/main/resources/plugin2.yml example-persistence-dist/pluginConfig

cd example-persistence-dist

#run main
mv plugin-example-persistence-main-*-exec.jar example-persistence-start.jar
mv application-prod.yml application.yml
java -jar example-persistence-start.jar --spring.config.location=application.yml

cd ../example-persistence-dist