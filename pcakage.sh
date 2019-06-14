#!/bin/sh
# linux、mac上打包的脚本
mvn clean install -Dmaven.test.skip=true
# del example-dist
rm -fr example-dist

# create example-dist
mkdir example-dist
mkdir example-dist\plugins
mkdir example-dist\pluginConfig

# copy main program and config
cp plugin-example\plugin-example-start\target\plugin-example-start-*-exec.jar example-dist
cp plugin-example\plugin-example-start\src\main\resources\application-prod.yml example-dist

# copy plugin and config
cp plugin-example\plugins\plugin-example-plugin1\target\plugin-example-plugin1-*-jar-with-dependencies.jar example-dist\plugins
cp plugin-example\plugins\plugin-example-plugin1\src\main\resources\plugin1.yml example-dist\pluginConfig

cp plugin-example\plugins\plugin-example-plugin2\target\plugin-example-plugin2-*-jar-with-dependencies.jar example-dist\plugins
cp plugin-example\plugins\plugin-example-plugin2\src\main\resources\plugin2.yml example-dist\pluginConfig

cd example-dist

#run main
mv plugin-example-start-*-exec.jar example-start.jar
mv application-prod.yml application.yml
java -jar example-start.jar --spring.config.location=application.yml