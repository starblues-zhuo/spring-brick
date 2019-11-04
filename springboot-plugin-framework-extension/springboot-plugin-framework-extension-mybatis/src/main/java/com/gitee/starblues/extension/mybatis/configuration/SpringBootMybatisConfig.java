package com.gitee.starblues.extension.mybatis.configuration;

import java.util.Map;
import java.util.Set;

/**
 * spring boot mybatis 配置接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface SpringBootMybatisConfig {


    /**
     * mybatis xml mapper 匹配规则 <br>
     * ? 匹配一个字符 <br>
     * * 匹配零个或多个字符 <br>
     * ** 匹配路径中的零或多个目录 <br>
     * 例如: <br>
     *  文件路径配置为 <p>file: D://xml/*PluginMapper.xml<p> <br>
     *  resources路径配置为 <p>classpath: xml/mapper/*PluginMapper.xml<p> <br>
     *  包路径配置为 <p>package: com.plugin.xml.mapper.*PluginMapper.xml<p> <br>
     * @return Set
     */
    Set<String> mybatisMapperXmlLocationsMatch();

    /**
     * 别名自定义映射。
     * 如果同时配置了typeAliasesPackage, 则此处配置会覆盖typeAliasesPackage、或者注解@Alias('') 的配置。
     * 别名生效优先级别: aliasMapping > @Alias('') > typeAliasesPackage。如果同时配置了三个地方, 并且key一样, 则最终优先级别高的生效。
     * @return 映射Map
     */
    default Map<String, Class> aliasMapping(){
        return null;
    }

    /**
     * 别名类型包集合。别名规则为包名首字母小写。只支持当前包下的类, 不支持包递归。
     * 如果从该包下的类使用了别名注解@Alias. 则优先使用@Alias中定义的别名。
     * @return 包集合
     */
    default Set<String> typeAliasesPackage(){
        return null;
    }


}
