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

}
