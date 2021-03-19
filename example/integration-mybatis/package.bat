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
xcopy integration-mybatis-main\target\*-RELEASE.jar dist /s /i
xcopy integration-mybatis-main\src\main\resources\application-prod.yml dist /s

REM copy plugin and config
xcopy plugins\integration-mybatis-plugin1\target\*-jar-with-dependencies.jar dist\plugins /s
xcopy plugins\integration-mybatis-plugin1\src\main\resources\plugin1.yml dist\pluginConfig /s

xcopy plugins\integration-mybatis-plugin2\target\*-jar-with-dependencies.jar dist\plugins /s
xcopy plugins\integration-mybatis-plugin2\src\main\resources\plugin2.yml dist\pluginConfig /s

cd dist

REM run main
rename *-RELEASE.jar integration-mybatis-start.jar
rename application-prod.yml application.yml
java -jar integration-mybatis-start.jar --spring.config.location=application.yml
