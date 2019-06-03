package com.plugin.development.context.configuration;

import com.plugin.development.exception.ConfigurationParseException;
import com.plugin.development.realize.PluginConfigDefinition;

/**
 * @Description: 配置解析者
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-29 13:47
 * @Update Date Time:
 * @see
 */
public interface ConfigurationParser {

    /**
     * 配置解析
     * @param pluginConfigDefinition 插件配置定义
     * @return
     * @throws ConfigurationParseException
     */
    Object parse(PluginConfigDefinition pluginConfigDefinition) throws ConfigurationParseException;

}
