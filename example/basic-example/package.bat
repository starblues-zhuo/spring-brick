REM windows package

REM package
call mvn clean install -Dmaven.test.skip=true

REM del example-dist
rmdir dist /s /q

REM create example-dist
mkdir dist
mkdir dist\plugins
mkdir dist\pluginConfig

REM copy main program and config
xcopy basic-example-main\target\basic-example-main-*-exec.jar dist /s /i
xcopy basic-example-main\src\main\resources\application-prod.yml dist /s

REM copy plugin and config
xcopy plugins\basic-example-plugin1\target\*-jar-with-dependencies.jar dist\plugins /s
xcopy plugins\basic-example-plugin1\src\main\resources\plugin1.yml dist\pluginConfig /s

xcopy plugins\basic-example-plugin2\target\*-jar-with-dependencies.jar dist\plugins /s
xcopy plugins\basic-example-plugin2\src\main\resources\plugin2.yml dist\pluginConfig /s

cd dist

REM run main
rename basic-example-main-*-exec.jar basic-example-start.jar
rename application-prod.yml application.yml
java -jar basic-example-start.jar --spring.config.location=application.yml