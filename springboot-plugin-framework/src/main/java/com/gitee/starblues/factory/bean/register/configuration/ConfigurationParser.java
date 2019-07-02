package com.gitee.starblues.factory.bean.register.configuration;

import com.gitee.starblues.exception.ConfigurationParseException;
import com.gitee.starblues.realize.BasePlugin;

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
    Object parse(BasePlugin basePlugin, PluginConfigDefinition pluginConfigDefinition) throws ConfigurationParseException;

}
