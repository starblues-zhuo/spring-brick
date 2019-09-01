REM windows package

REM package
call mvn clean install -Dmaven.test.skip=true

REM del example-persistence-dist
rmdir dist /s /q

REM create example-persistence-dist
mkdir dist
mkdir dist\plugins
mkdir dist\pluginConfig


REM copy main program and config
xcopy integration-mybatisplus-main\target\*-exec.jar dist /s /i
xcopy integration-mybatisplus-main\src\main\resources\application-prod.yml dist /s

REM copy plugin and config
xcopy plugins\integration-mybatisplus-plugin\target\*-jar-with-dependencies.jar dist\plugins /s

cd dist

REM run main
rename *-exec.jar integration-mybatisplus-start.jar
rename application-prod.yml application.yml
java -jar integration-mybatisplus-start.jar --spring.config.location=application.yml
