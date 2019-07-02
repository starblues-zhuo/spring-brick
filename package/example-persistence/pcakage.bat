REM windows上打包的脚本

cd ../../
REM package'
mvn clean install -Dmaven.test.skip=true

REM del example-persistence-dist
rmdir example-persistence-dist /s /q

REM create example-persistence-dist
mkdir example-persistence-dist
mkdir example-persistence-dist\plugins
mkdir example-persistence-dist\pluginConfig


REM copy main program and config
xcopy plugin-example-persistence\plugin-example-persistence-main\target\plugin-example-persistence-main-*-exec.jar example-persistence-dist /s /i
xcopy plugin-example-persistence\plugin-example-persistence-main\src\main\resources\application-prod.yml example-persistence-dist /s

REM copy plugin and config
xcopy plugin-example-persistence\plugins\plugin-example-persistence-plugin1\target\plugin-example-persistence-plugin1-*-jar-with-dependencies.jar example-persistence-dist\plugins /s
xcopy plugin-example-persistence\plugins\plugin-example-persistence-plugin1\src\main\resources\plugin1.yml example-persistence-dist\pluginConfig /s

xcopy plugin-example-persistence\plugins\plugin-example-persistence-plugin2\target\plugin-example-persistence-plugin2-*-jar-with-dependencies.jar example-persistence-dist\plugins /s
xcopy plugin-example-persistence\plugins\plugin-example-persistence-plugin2\src\main\resources\plugin2.yml example-persistence-dist\pluginConfig /s

cd example-persistence-dist

REM run main
rename plugin-example-persistence-main-*-exec.jar example-persistence-start.jar
rename application-prod.yml application.yml
java -jar example-persistence-start.jar --spring.config.location=application.yml

cd ../example-persistence-dist