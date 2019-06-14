package com.plugin.development.context.configuration;

import com.plugin.development.exception.ConfigurationParseException;

/**
 * 配置解析者
 * @author zhangzhuo
 * @version 1.0
 */
public interface ConfigurationParser {

    /**
     * 配置解析
     * @param pluginConfigDefinition 插件配置定义
     * @return 解析后映射值的对象
     * @throws ConfigurationParseException 抛出配置解析异常
     */
    Object parse(PluginConfigDefinition pluginConfigDefinition) throws ConfigurationParseException;

}
