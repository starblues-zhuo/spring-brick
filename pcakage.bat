REM windows上打包的脚本

REM package
call mvn clean install -Dmaven.test.skip=true

REM del example-dist
rmdir example-dist /s /q

REM create example-dist
mkdir example-dist
mkdir example-dist\plugins
mkdir example-dist\pluginConfig

REM copy main program and config
xcopy plugin-example\plugin-example-start\target\plugin-example-start-*-exec.jar example-dist /s /i
xcopy plugin-example\plugin-example-start\src\main\resources\application-prod.yml example-dist /s

REM copy plugin and config
xcopy plugin-example\plugins\plugin-example-plugin1\target\plugin-example-plugin1-*-jar-with-dependencies.jar example-dist\plugins /s
xcopy plugin-example\plugins\plugin-example-plugin1\src\main\resources\plugin1.yml example-dist\pluginConfig /s

xcopy plugin-example\plugins\plugin-example-plugin2\target\plugin-example-plugin2-*-jar-with-dependencies.jar example-dist\plugins /s
xcopy plugin-example\plugins\plugin-example-plugin2\src\main\resources\plugin2.yml example-dist\pluginConfig /s

cd example-dist

REM run main
rename plugin-example-start-*-exec.jar example-start.jar
rename application-prod.yml application.yml
java -jar example-start.jar --spring.config.location=application.yml