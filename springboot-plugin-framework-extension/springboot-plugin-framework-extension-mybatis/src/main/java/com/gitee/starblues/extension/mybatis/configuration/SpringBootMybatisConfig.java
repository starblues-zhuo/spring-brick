package com.gitee.starblues.extension.mybatis.configuration;

import java.util.Set;

/**
 * spring boot mybatis 配置接口
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface SpringBootMybatisConfig {


    /**
     * mybatis xml mapper 匹配规则
     * ? 匹配一个字符
     * * 匹配零个或多个字符
     * ** 匹配路径中的零或多个目录
     * 例如:
     *      文件路径-> file: D://xml/*Mapper.xml
     *      classpath路径-> classpath: xml/mapper/*Mapper.xml
     *      包路径-> package: com.plugin.xml.mapper.*Mapper.xml
     * @return Set
     */
    Set<String> mybatisMapperXmlLocationsMatch();

}
