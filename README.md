# springboot插件式开发框架

### 介绍
该框架主要是集成于springboot项目，用于开发插件式应用的集成框架。

### 核心功能
1. 插件配置式插拔于springboot项目。
2. 在springboot上可以进行插件式开发, 扩展性极强, 可以针对不同项目开发不同插件, 进行不同插件jar包的部署。
3. 可通过配置文件指定要启用或者禁用插件。
4. 支持上传插件和插件配置文件到服务器, 并且无需重启主程序, 动态部署插件、更新插件。
5. 支持查看插件运行状态, 查看插件安装位置。
6. 无需重启主程序, 动态的安装插件、卸载插件、启用插件、停止插件、备份插件、删除插件。
7. 在插件应用模块上可以使用Spring注解定义组件, 进行依赖注入。
8. 支持在插件中开发Rest接口。
9. 支持在插件中单独定义持久层访问等需求。
10. 可以遵循主程序提供的插件接口开发任意扩展功能。
11. 插件可以自定义配置文件。目前只支持yml文件。
12. 支持自定义扩展开发接口, 使用者可以在预留接口上扩展额外功能。
13. 利用扩展机制, 定制了SpringBoot-Mybatis扩展包。使用该扩展包, 使用者可以在插件中自定义Mapper接口、Mapper xml 以及对应的实体bean。


### 运行环境
1. jdk1.8+
2. apache maven 3.6

### maven 仓库地址

[https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework)

### 快速入门

#### 新建项目。
Maven目录结构下所示
```
-example
    - example-runner
        - pom.xml
    - example-main
        - pom.xml
    - example-plugin-parent
        - pom.xml
    - plugins
        - example-plugin1
            - pom.xml
            - plugin.properties
        - example-plugin2
            - pom.xml
            - plugin.properties
        - pom.xml
    - pom.xml
```

结构说明:

1. pom.xml 代表maven的pom.xml
2. plugin.properties 为开发环境下, 插件的元信息配置文件, 配置内容详见下文。
3. example 为项目的总Maven目录。
4. example-runner 在运行环境下启动的模块。主要依赖example-main模块和插件中使用到的依赖包。
5. example-main 该模块为项目的主程序模块。
6. example-plugin-parent 该模块为插件的父级maven pom 模块, 主要定义插件中公共用到的依赖, 以及插件的打包配置。
7. plugins 该文件夹下主要存储插件模块。上述模块中主要包括example-plugin1、example-plugin2 两个插件。
8. example-plugin1、example-plugin2 分别为两个插件Maven包。

#### 主程序集成步骤

主程序为上述目录结构中的 example-main 模块。

1. 在主程序中新增maven依赖包

```xml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework</artifactId>
    <version>${springboot-plugin-framework.version}</version>
</dependency>

最新版本:
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework</artifactId>
    <version>2.0.3-RELEASE</version>
</dependency>

```

2. 实现并定义配置

实现 **com.plugin.development.integration.IntegrationConfiguration** 接口。

```java
import com.gitee.starblues.integration.DefaultIntegrationConfiguration;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "plugin")
public class PluginConfiguration extends DefaultIntegrationConfiguration {

    /**
     * 运行模式
     *  开发环境: development、dev
     *  生产/部署 环境: deployment、prod
     */
    @Value("${runMode:dev}")
    private String runMode;

    /**
     * 插件的路径
     */
    @Value("${pluginPath:plugins}")
    private String pluginPath;

    /**
     * 插件文件的路径
     */
    @Value("${pluginConfigFilePath:pluginConfigs}")
    private String pluginConfigFilePath;


    @Override
    public RuntimeMode environment() {
        return RuntimeMode.byName(runMode);
    }

    @Override
    public String pluginPath() {
        return pluginPath;
    }

    @Override
    public String pluginConfigFilePath() {
        return pluginConfigFilePath;
    }

    /**
     * 重写上传插件包的临时存储路径。只适用于生产环境
     * @return String
     */
    @Override
    public String uploadTempPath() {
        return "temp";
    }

    /**
     * 重写插件备份路径。只适用于生产环境
     * @return String
     */
    @Override
    public String backupPath() {
        return "backupPlugin";
    }

    /**
     * 重写插件RestController请求的路径前缀
     * @return String
     */
    @Override
    public String pluginRestControllerPathPrefix() {
        return "/api/plugins";
    }

    /**
     * 重写是否启用插件id作为RestController请求的路径前缀。
     * 启动则插件id会作为二级路径前缀。即: /api/plugins/pluginId/**
     * @return String
     */
    @Override
    public boolean enablePluginIdRestControllerPathPrefix() {
        return true;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }


    public String getPluginPath() {
        return pluginPath;
    }

    public void setPluginPath(String pluginPath) {
        this.pluginPath = pluginPath;
    }

    public String getPluginConfigFilePath() {
        return pluginConfigFilePath;
    }

    public void setPluginConfigFilePath(String pluginConfigFilePath) {
        this.pluginConfigFilePath = pluginConfigFilePath;
    }

    @Override
    public String toString() {
        return "PluginArgConfiguration{" +
                "runMode='" + runMode + '\'' +
                ", pluginPath='" + pluginPath + '\'' +
                ", pluginConfigFilePath='" + pluginConfigFilePath + '\'' +
                '}';
    }
}
```

配置说明:

**runMode**：运行项目时的模式。分为开发环境(dev)、生产环境(prod)

**pluginPath**: 插件的路径。开发环境建议直接配置为插件模块的父级目录。例如: plugins。如果启动主程序时, 插件为加载, 请检查该配置是否正确。

**pluginConfigFilePath**: 在生产环境下, 插件的配置文件路径。在生产环境下， 请将所有插件使用到的配置文件统一放到该路径下管理。如果启动主程序时, 报插件的配置文件加载错误, 有可能是该该配置不合适导致的。

**uploadTempPath**: 上传插件包时使用。上传插件包存储的临时路径。默认 temp(相对于主程序jar路径)

**backupPath**: 备份插件包时使用。备份插件包的路径。默认: backupPlugin(相对于主程序jar路径)

**pluginRestControllerPathPrefix**: 插件RestController请求的路径前缀

**enablePluginIdRestControllerPathPrefix**: 是否启用插件id作为RestController请求的路径前缀。启动则插件id会作为二级路径前缀。即: /api/plugins/pluginId/**
    

3. 配置bean
  
```
import com.gitee.starblues.integration.*;
import com.gitee.starblues.integration.initialize.AutoPluginInitializer;
import com.gitee.starblues.integration.initialize.PluginInitializer;
import org.pf4j.PluginException;
import org.pf4j.PluginManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PluginBeanConfig {

  /**
     * 通过默认的集成工厂返回 PluginManager
     * @param integrationConfiguration 集成的配置文件
     * @return
     * @throws PluginException
     */
    @Bean
    public PluginManager pluginManager(IntegrationConfiguration integrationConfiguration) throws PluginException {
        IntegrationFactory integrationFactory = new DefaultIntegrationFactory();
        return integrationFactory.getPluginManager(integrationConfiguration);
    }

    /**
     * 定义默认的插件应用。使用可以注入它操作插件。
     * @return
     */
    @Bean
    public PluginApplication pluginApplication(){
        return new DefaultPluginApplication();
    }

    /**
     * 初始化插件。此处定义可以在系统启动时自动加载插件。
     *  如果想手动加载插件, 则可以使用 com.plugin.development.integration.initialize.ManualPluginInitializer 来初始化插件。
     * @param pluginApplication
     * @return
     */
    @Bean
    public PluginInitializer pluginInitializer(PluginApplication pluginApplication){
        AutoPluginInitializer autoPluginInitializer = new AutoPluginInitializer(pluginApplication);
        return autoPluginInitializer;
    }

}

```

#### 插件包集成步骤

1. 插件包pom.xml配置说明


以 `<scope>provided</scope>` 方式引入springboot-plugin-framework包

```xml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework</artifactId>
    <version>${springboot-plugin-framework.version}</version>
    <scope>provided</scope>
</dependency>
```

定义打包配置.主要用途是将 `Plugin-Id、Plugin-Version、Plugin-Provider、Plugin-Class、Plugin-Dependencies`的配置值定义到`META-INF\MANIFEST.MF`文件中
```xml
<properties>
    <plugin.id>springboot-plugin-example-plugin1</plugin.id>
    <plugin.class>com.plugin.example.plugin1.DefinePlugin</plugin.class>
    <plugin.version>${project.version}</plugin.version>
    <plugin.provider>StarBlues</plugin.provider>
    <plugin.dependencies></plugin.dependencies>

    <java.version>1.8</java.version>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>

    <maven-compiler-plugin.version>3.7.0</maven-compiler-plugin.version>
    <maven-assembly-plugin.version>3.1.1</maven-assembly-plugin.version>
</properties>
<build>
    <plugins>
        
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <encoding>${project.build.sourceEncoding}</encoding>
            </configuration>
        </plugin>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>${maven-assembly-plugin.version}</version>
            <configuration>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <archive>
                    <manifest>
                        <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                        <addDefaultSpecificationEntries>true</addDefaultSpecificationEntries>
                    </manifest>
                    <manifestEntries>
                        <Plugin-Id>${plugin.id}</Plugin-Id>
                        <Plugin-Version>${plugin.version}</Plugin-Version>
                        <Plugin-Provider>${plugin.provider}</Plugin-Provider>
                        <Plugin-Class>${plugin.class}</Plugin-Class>
                    </manifestEntries>
                </archive>
            </configuration>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

2. 在插件包的一级目录下新建plugin.properties文件(用于开发环境)
新增如下内容(属性值同步骤1中pom.xml定义的`manifestEntries`属性一致):
```
plugin.id=springboot-plugin-example-plugin1
plugin.class=com.plugin.example.plugin1.DefinePlugin
plugin.version=2.0-SNAPSHOT
plugin.provider=StarBlues
```

配置说明:
```
plugin.id: 插件id
plugin.class: 插件实现类。见步骤3说明
plugin.version: 插件版本
plugin.provider: 插件作者
```
    
3. 继承 `com.gitee.starblues.realize.BasePlugin` 包
``` java
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

public class DefinePlugin extends BasePlugin {
   public DefinePlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected void startEvent() throws PluginException {

    }

    @Override
    protected void deleteEvent() throws PluginException {

    }

    @Override
    protected void stopEvent() {

    }
}
```

并且将该类的包路径(com.plugin.example.plugin1.DefinePlugin)配置在步骤1和2的plugin.class属性中。

4. 新增HelloPlugin1 controller

此步骤主要验证环境是否加载插件成功。

```java

@RestController
@RequestMapping(path = "plugin1")
public class HelloPlugin1 {

    @GetMapping()
    public String getConfig(){
        return "hello plugin1"
    }

}

```

#### 运行配置
1. 配置模块 example-runner 的pom.xml

- 将主程序的依赖新增到pom.xml 下
- 将插件中的依赖以  `<scope>provided</scope>`  方式引入到 pom.xml 下

如下所示:

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.0.3.RELEASE</version>
        <relativePath/>
    </parent>

    <groupId>com.gitee.starblues</groupId>
    <artifactId>plugin-example-runner</artifactId>
    <version>2.0-RELEASE</version>
    <packaging>pom</packaging>

    <properties>
        <gson.version>2.8.2</gson.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.gitee.starblues</groupId>
            <artifactId>plugin-example-start</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- 此处依赖用于解决在开发环境下, 插件包找不到对应依赖包 -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
            <scope>provided</scope>
        </dependency>

    </dependencies>

</project>
```

2. 设置idea的启动

Working directory : D:\xx\xx\springboot-plugin-framework-parent\plugin-example

Use classpath of module: plugin-exampe-runner

勾选: Include dependencies with "Provided" scope

3. 启动2步骤的配置。

观察日志出现如下说明加载插件成功。

``` java
 Plugin 'springboot-plugin-example-plugin1@2.0-RELEASE' resolved
 Start plugin 'springboot-plugin-example-plugin1@2.0-RELEASE'
 Init Plugins <springboot-plugin-example-plugin1> Success
```

4. 访问插件中的Controller 验证。

浏览器输入：http://ip:port/api/plugins/springboot-plugin-example-plugin1/plugin1

响应并显示: hello plugin1 

说明集成成功！

### 使用说明

#### 插件中定义配置文件

1. 在插件包的 resources 目录下定义配置文件 plugin1.yml

```yml
name: plugin1
plugin: examplePlugin1
setString:
  - set1
  - set2
listInteger:
  - 1
  - 2
  - 3
subConfig:
  subName: subConfigName
```

2. 在代码中定义对应的bean

```java
import com.gitee.starblues.annotation.ConfigDefinition;
import java.util.List;
import java.util.Set;

@ConfigDefinition("plugin1.yml")
public class PluginConfig1 {

    private String name;
    private String plugin;
    private Set<String> setString;
    private List<Integer> listInteger;
    private String defaultValue = "defaultValue";
    private SubConfig subConfig;

    // 自行提供get set 方法

}


public class SubConfig {

    private String subName;
    public String getSubName() {
        return subName;
    }
    
    // 自行提供get set 方法
}
```

该bean必须加上 @ConfigDefinition("plugin1.yml") 注解。其中值为插件文件的名称。

3. 其他地方使用时, 可以通过注入方式使用。

例如：
``` java
@Component("plugin2HelloService")
public class HelloService {

    private final PluginConfig1 pluginConfig1;
    private final Service2 service2;

    @Autowired
    public HelloService(PluginConfig1 pluginConfig1, Service2 service2) {
        this.pluginConfig1 = pluginConfig1;
        this.service2 = service2;
    }

    public PluginConfig1 getPluginConfig1(){
        return pluginConfig1;
    }


    public String sayService2(){
        return service2.getName();
    }

}
```

4. 注意事项

*在开发环境：配置文件必须放在resources目录下。并且@ConfigDefinition("plugin1.yml")中定义的文件名和resources下配置的文件名一致。*

*在生产环境: 该文件存放在`pluginConfigFilePath`配置的目录下。*


### 集成扩展

1. SpringBoot Mybatis 扩展

文档见: [springboot-plugin-framework-extension-mybatis](https://gitee.com/starblues/springboot-plugin-framework-parent/tree/master/springboot-plugin-framework-extension/springboot-plugin-framework-extension-mybatis)


### 案例部署

普通例子运行见：package/example

windows环境下运行: package.bat

linux、mac 环境下运行: package.sh

### mybatis 案例部署

普通例子运行见：package/example-persistence

windows环境下运行: package.bat

linux、mac 环境下运行: package.sh

sql在 plugin-example-persistence/sql 文件夹下。

### 生产环境目录

```text
-main.jar

-main.yml

-plugins
  -plugin1.jar
  -plugin2.jar
  
-pluginFile
  -plugin1.yml
  -plugin2.yml

```

### 案例说明

plugin-example：插件基础功能案例。

plugin-example-persistence: 针对Mybatis集成的案例。

### 生产环境配置禁用启用功能

#### 启用功能

1.在插件目录下新建 `enabled.txt` 文件
2.enabled.txt的内容为:

```text
########################################
# - 启用的插件
########################################
example-plugin1
```
将需要启用的插件id配置到文件中。

所有注释行（以＃字符开头的行）都将被忽略。

#### 启用、禁用功能

1.在插件目录下新建 `disabled.txt` 文件
2.disabled.txt的内容为:

```text
########################################
# - 禁用的插件
########################################
example-plugin1
```
将需要启用的插件id配置到文件中。

所有注释行（以＃字符开头的行）都将被忽略。
    

### 注意事项

1. 插件中代码编写完后, 请保证在class文件下的类都是最新编译的, 再运行主程序, 否则会导致运行的插件代码不是最新的。
2. 如果启动时插件没有加载。请检查配置文件中的 pluginPath

```text
如果pluginPath 配置为相当路径，请检查是否是相对于当前工作环境的目录。

如果pluginPath配置为绝对路径，请检查路径是否正确。
```

3. 如果出现Spring包冲突。可以排除Spring包。
例如:
```xml
<dependency>
<groupId>com.gitee.starblues</groupId>
<artifactId>springboot-plugin-framework</artifactId>
<version>${springboot-plugin-framework.version}</version>
<exclusions>
    <exclusion>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
    </exclusion>
     <exclusion>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
     </exclusion>
</exclusions>
</dependency>
```
4. 以下功能只适用于生产环境下。
- 插件的上传。
- 插件的动态更新(上传并安装插件)。
- 插件的备份。
- 插件的配置文件上传。
- 删除插件


### 小技巧
1. idea 启动主程序时, 自动编译插件包的配置
选择 
File->Project Structure->Project Settings->Artifacts->点击+号->JAR->From modules whith dependencies->选择对应的插件包->确认OK
                            
启动配置: 
在Before launch 下-> 点击小+号 -> Build ->Artifacts -> 选择上一步新增的>Artifacts

### QQ交流群
859570617

### 版本更新

#### 1.1 版本
1. 新增插件注册、卸载监听器。
2. 新增可通过 PluginUser 获取插件中实现主程序中定义的接口的实现类。
3. 新增插件注册、卸载时监听器。

#### 2.0 版本(重大版本更新)
1. 重构代码。
2. 新增扩展机制。
3. 简化依赖注入注解, 保持与SpringBoot依赖注入方式一致。
4. 新增插件工厂监听器、新增插件初始化监听器(适用于第一次启动)。
5. 新增插件包Mybatis的集成, 可在插件包中独立定义Mapper接口、Mapper xml、实体bean。

#### 2.0.1 版本
1. 修复插件的Controller无法定义一级请求路径的bug。

#### 2.0.2 版本
1. 新增 com.gitee.starblues.integration.user.PluginUser

使用场景: 在主程序中定义了接口, 插件中存在实现了该接口的实现类, 通过PluginUser 的 getPluginBeans(接口Class) 可以获取所有插件中实现该接口的实现类。具体详见源码。

2. 新增插件bean刷新抽象类。继承它可动态获取接口实现类集合。

#### 2.0.3 版本
1. 修复插件动态重新安装后, 无法访问到插件中的接口的bug。