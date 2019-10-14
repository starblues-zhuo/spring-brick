#扩展包 - 集成SpringBoot Mybatis

### maven 仓库地址

https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework-extension-mybatis

### 集成步骤

#### 主程序配置

1. 引入依赖
```xmml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework-extension-mybatis</artifactId>
    <version>${springboot-plugin-framework-extension-mybatis.version}</version>
</dependency>

<!--  自行引入 mybatis-spring-boot-starter 依赖 -->
 <dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>${mybatis-spring-boot-starter.version}</version>
</dependency>

```

2. 集成

定义PluginApplication bean时, 新增该扩展。
```java
@Bean
public PluginApplication pluginApplication(){
    DefaultPluginApplication defaultPluginApplication = new DefaultPluginApplication();
    defaultPluginApplication.addExtension(new SpringBootMybatisExtension());
    return defaultPluginApplication;
}
```

#### 插件程序配置

1. 引入依赖
```xmml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework-extension-mybatis</artifactId>
    <version>${springboot-plugin-framework-extension-mybatis.version}</version>
</dependency>

<!-- 自行引入 mybatis-spring-boot-starter 依赖。可用于自定义注解Sql。该依赖非必须 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>${mybatis-spring-boot-starter.version}</version>
</dependency>

```

2. 继承BasePlugin的类, 实现接口 com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig 

例如:
```java
import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginException;
import org.pf4j.PluginWrapper;

import java.util.HashSet;
import java.util.Set;

public class PersistenceExamplePlugin1 extends BasePlugin implements SpringBootMybatisConfig {

    private final Set<String> mybatisMapperXmlLocationsMatch = new HashSet<>();


    public PersistenceExamplePlugin1(PluginWrapper wrapper) {
        super(wrapper);
        mybatisMapperXmlLocationsMatch.add("classpath:mapper/*PluginMapper.xml");
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

    @Override
    public Set<String> mybatisMapperXmlLocationsMatch() {
        return mybatisMapperXmlLocationsMatch;
    }
}

```

该步骤主要定义插件中的Mapper xml的位置。该位置的定义规则如下:

``` text
? 匹配一个字符
* 匹配零个或多个字符
** 匹配路径中的零或多个目录

例如:
文件路径-> file: D://xml/*PluginMapper.xml
classpath路径-> classpath: xml/mapper/*PluginMapper.xml
包路径-> package: com.plugin.xml.mapper.*PluginMapper.xml

```

3. 定义的Mapper 接口需要加上注解 @PluginMapper

注解位置: com.gitee.starblues.extension.mybatis.annotation.PluginMapper

例如:
```java

import com.gitee.starblues.extension.mybatis.annotation.PluginMapper;
import Plugin1;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@PluginMapper
public interface Plugin1Mapper {


    /**
     * 得到角色列表
     * @return List
     */
    List<Plugin1> getList();

    /**
     * 通过id获取数据
     * @param id id
     * @return Plugin2
     */
    Plugin1 getById(@Param("id") String id);

}

```

具体案例参考: example/integration-mybatis 模块。

#### 集成Mybatis-Plus说明

由于原生 Mybatis-Plus Service 层集成的 ServiceImpl<M, T> 无法在插件中注入 BaseMapper。

因此针对此问题, 该扩展新增 ServiceImpl<M, T> 的包装类
`com.gitee.starblues.extension.mybatis.support.mybatisplus.ServiceImplWrapper` 来解决该问题, 该包装类的功能和 ServiceImpl<M, T> 功能一模一样。

用法如下:
```java
@Component
public class PluginDataServiceImpl extends ServiceImplWrapper<PluginDataMapper, PluginData>
        implements PluginDataService{


    public PluginDataServiceImpl(PluginDataMapper baseMapper) {
        super(baseMapper);
    }

}

```

集成Mybatis-plus案例见 `example/integration-mybatisplus` 模块。集成的 mybatis-plus 版本为: 3.2.0

### 版本升级

#### 2.1.3 版本
跟随 springboot-plugin-framework 版本的部分类修改。升级到 2.1.3

#### 2.1.1 版本
1. 新增支持 Mybatis-Plus ServiceImpl的包装类。ServiceImplWrapper。使用详见`集成Mybatis-Plus说明`
2. 修复 Mapper.xml 中定义的 resultType 类型无法定义的bug。
    
#### 2.0.3 版本
1. 修复Mapper无法注入的bug. (由于springboot-plugin-framework 2.0.3 版本升级导致)


