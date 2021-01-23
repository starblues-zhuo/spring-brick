#扩展包 - 集成SpringBoot Mybatis

### maven 仓库地址

https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework-extension-mybatis

### 集成步骤

#### 主程序集成步骤

一. 引入依赖
```xmml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework-extension-resources</artifactId>
    <version>${latest.version}</version>
</dependency>
```

二. 配置扩展

定义PluginApplication bean时, 新增该扩展。
```java
@Bean
public PluginApplication pluginApplication(){
    PluginApplication pluginApplication = new AutoPluginApplication();
    // 新增静态资源扩展
    StaticResourceExtension staticResourceExtension = new StaticResourceExtension();
    // 插件静态资源Http访问前缀
    staticResourceExtension.setPathPrefix("static");
    // 设置静态资源缓存策略
    staticResourceExtension.setCacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic());
    // 添加扩展
    pluginApplication.addExtension(staticResourceExtension);
}
```

**插件的http资源url访问规则为: http://ip:port/pathPrefix(上述说明配置的值)/插件id/具体插件的资源路径**

#### 插件程序集成步骤

一. 以provided方式引入主程序依赖, 例如：
```xml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>artifact-name</artifactId>
    <version>${project.version}</version>
    <scope>provided</scope>
</dependency>
```

二. 进行配置

1. 配置集成静态资源

如果当前插件需要集成静态资源的访问, 则需要实现接口`com.gitee.starblues.extension.resources.StaticResourceConfig` 进行配置。

例如: 
```java
@ConfigDefinition
public class ResourceConfig implements StaticResourceConfig {
    @Override
    public Set<String> locations() {
        Set<String> locations = new HashSet<>();
        locations.add("classpath:static");
        return locations; 
    }
}
```

2. 配置集成`Thymeleaf`

- 首先`主程序`中需要引入如下依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```
- `插件`中进行如下配置

实现接口 `com.gitee.starblues.extension.resources.thymeleaf.SpringBootThymeleafConfig`

例如:
``` java
@ConfigDefinition
public class ResourceConfig implements SpringBootThymeleafConfig {

    @Override
    public void config(ThymeleafConfig thymeleafConfig) {
        thymeleafConfig.setPrefix("tf");
        thymeleafConfig.setSuffix(".html");
    }
}

```


### 版本升级

#### 2.4.0 版本
- 修改扩展功能中配置实现类，必须新增`@ConfigDefinition` 注解
- 修复插件中的静态资源和主程序冲突的bug

#### 2.2.5 版本
1. 新增`Thymeleaf`模板引擎
2. 修改插件中扩展的配置方式

#### 2.2.1 版本
修改该扩展导致无法卸载插件的bug
    
#### 2.1.1 版本
新增当前扩展


