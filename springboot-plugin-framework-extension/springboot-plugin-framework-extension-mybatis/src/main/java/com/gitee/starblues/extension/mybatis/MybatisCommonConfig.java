package com.gitee.starblues.extension.mybatis;

import java.util.Set;

/**
 * Springboot mybatis 公用的配置
 * @author starBlues
 * @version 2.3
 */
public interface MybatisCommonConfig {

    /**
     * 数据库表对应的实体类的包名集合。可配置多个
     * @return Set
     */
    Set<String> entityPackage();

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
    Set<String> xmlLocationsMatch();

    /**
     * 插件是否自主启用配置. 默认进行禁用, 使用主程序的配置
     * @return 返回true, 表示进行插件自主进行Mybatis相关配置
     */
    default boolean enableOneselfConfig(){
        return false;
    }


}
