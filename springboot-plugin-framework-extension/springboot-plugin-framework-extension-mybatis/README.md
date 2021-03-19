### 集成Mybatis扩展

## 包含集成
- 可集成`Mybatis`
- 可集成`Mybatis-Plus`
- 可集成`Tk-Mybatis`

## maven 仓库地址

[maven 仓库地址](https://mvnrepository.com/artifact/com.gitee.starblues/springboot-plugin-framework-extension-mybatis)

## 主程序集成步骤
#### 引入依赖
```xmml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>springboot-plugin-framework-extension-mybatis</artifactId>
    <version>2.4.0-RELEASE</version>
</dependency>

<!--  如果使用mybatis, 则自行引入如下依赖 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>

<!--  如果使用mybatis-plus, 则自行引入如下依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>${version}</version>
</dependency>

<!--  如果使用tk-mybatis, 则自行引入如下依赖 -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>${version}</version>
</dependency>

```

#### 配置扩展

定义PluginApplication bean时, 新增该扩展。
```java
@Bean
public PluginApplication pluginApplication(){
    PluginApplication pluginApplication = new AutoPluginApplication();
    // 根据当前环境所集成的框架来选择类型
    pluginApplication.addExtension(new SpringBootMybatisExtension(
                   SpringBootMybatisExtension.Type.MYBATIS));
    return defaultPluginApplication;
}
```

注意：根据当前环境所集成的框架来选择类型
- Mybatis类型为：`SpringBootMybatisExtension.Type.MYBATIS`
- Mybatis-Plus类型为：`SpringBootMybatisExtension.Type.MYBATIS_PLUS`
- Tk-Mybatis类型为：`SpringBootMybatisExtension.Type.TK_MYBATIS`

## 插件集成步骤

#### 以provided方式引入主程序依赖, 例如：
```xml
<dependency>
    <groupId>com.gitee.starblues</groupId>
    <artifactId>integration-mybatis-main</artifactId>
    <version>${project.version}</version>
    <scope>provided</scope>
</dependency>
```

#### 进行配置

- 如果集成`Mybatis`, 则实现接口：`com.gitee.starblues.extension.mybatis.SpringBootMybatisConfig`
- 如果集成`Mybatis-Plus`, 则实现接口：`com.gitee.starblues.extension.mybatis.SpringBootMybatisPlusConfig`
- 如果集成`TkMybatis`, 则实现接口：`com.gitee.starblues.extension.mybatis.SpringBootTkMybatisConfig`

以上实现类添加注解`@ConfigDefinition` 

例如集成`Mybatis-Plus`:
```java

@ConfigDefinition
public class MybatisConfig implements SpringBootMybatisConfig {

    @Override
    public Set<String> entityPackage() {
        Set<String> typeAliasesPackage = new HashSet<>();
        typeAliasesPackage.add("com.mybatis.plugin1.entity");
        return typeAliasesPackage;
    }

    @Override
    public Set<String> xmlLocationsMatch() {
        Set<String> xmlLocationsMatch = new HashSet<>();
        xmlLocationsMatch.add("classpath:mapper/*Mapper.xml");
        return xmlLocationsMatch;
    }
}

```

该步骤主要定义插件中的Mapper xml的位置。该位置的定义规则如下:
- 注意: 插件中的xml路径不能和主程序中的xml路径在`resources`相对一致, 比如文件名都为`mapper`, 建议使用不同名称区分开
``` text
xmlLocationsMatch:
? 匹配一个字符
* 匹配零个或多个字符
** 匹配路径中的零或多个目录

例如:
文件路径-> file: D://xml/*PluginMapper.xml
classpath路径-> classpath: xml/mapper/*PluginMapper.xml
包路径-> package: com.plugin.xml.mapper.*PluginMapper.xml

```

#### 定义的Mapper 接口需要加上注解 @Mapper

注解位置: org.apache.ibatis.annotations.Mapper

例如:
```java

import com.gitee.starblues.extension.mybatis.annotation.PluginMapper;
import Plugin1;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
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
#### 如果插件不想使用主程序的配置或者数据源, 插件可自定义配置, 配置说明如下: 
1. 实现`enableOneselfConfig`方法, 并设置返回值为true
2. 实现`oneselfConfig(xx)`方法进行独立配置
- Mybatis独立配置:
```java
/**
 * 插件自主配置Mybatis的 SqlSessionFactoryBean
 * SqlSessionFactoryBean 具体配置说明参考 Mybatis 官网
 */
@Override
public void oneselfConfig(SqlSessionFactoryBean sqlSessionFactoryBean) {
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    mysqlDataSource.setURL("jdbc:mysql://127.0.0.1:3306/xxx?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC");
    mysqlDataSource.setUser("root");
    mysqlDataSource.setPassword("root");
    sqlSessionFactoryBean.setDataSource(mysqlDataSource);
}
```

- Mybatis-Plus独立配置:
```java
/**
 * 插件自主配置Mybatis的 SqlSessionFactoryBean
 * MybatisSqlSessionFactoryBean 具体配置说明参考 Mybatis-plus 官网
 */
@Override
public void oneselfConfig(MybatisSqlSessionFactoryBean sqlSessionFactoryBean) {
    ...
}
```

- TkMybatis独立配置:
```java
/**
 * 插件自主配置Mybatis的 SqlSessionFactoryBean
 * SqlSessionFactoryBean 具体配置说明参考 Mybatis 官网
 */
@Override
public void oneselfConfig(SqlSessionFactoryBean sqlSessionFactoryBean) {
    ...
}
/**
 * 插件自主配置tk的 Config
 * Config 具体配置说明参考 https://gitee.com/free/Mapper/wikis/1.1-java?sort_id=208196
 * @param config Config
 */
public void oneselfConfig(Config config){
}

```

#### 重新主程序配置
1. 实现各个配置的 `reSetMainConfig` 方法进行重写
2. 重写后不影响主程序的配置, 只在当前插件中起作用

### 集成Mybatis-Plus说明

- 集成mybatis-plus后，在插件中无法使用 `LambdaQueryWrapper` 条件构造器


## 版本说明
### `2.4.2-RELEASE` 版本
1. 新增`Mybatis`、`Mybatis-Plus`、`Tk-Mybatis`扩展新增可重写覆盖主程序的配置(重写后会当前插件私有, 不影响主程序)
2. 修复`Mybatis-Plus`批量插入的bug

### `2.4.0-RELEASE` 版本
1. 修改扩展功能中配置实现类，必须新增`@ConfigDefinition` 注解

### `2.2.5-RELEASE` 版本
全新升级该扩展
1. 对 `Mybatis`、`Mybatis-Plus`、`Tk-Mybatis` 进行支持
2. 支持动态卸载和安装
3. 支持插件可独立进行配置, 与主程序和其他插件进行环境隔离

### `2.1.3-RELEASE` 版本
跟随 springboot-plugin-framework 版本的部分类修改。升级到 2.1.3

### `2.1.1-RELEASE` 版本
1. 新增支持 Mybatis-Plus ServiceImpl的包装类。ServiceImplWrapper。使用详见`集成Mybatis-Plus说明`
2. 修复 Mapper.xml 中定义的 resultType 类型无法定义的bug。
    
### `2.0.3-RELEASE` 版本
1. 修复Mapper无法注入的bug. (由于springboot-plugin-framework 2.0.3 版本升级导致)
