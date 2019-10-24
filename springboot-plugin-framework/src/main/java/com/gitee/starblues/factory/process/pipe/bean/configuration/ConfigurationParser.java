package com.gitee.starblues.factory.process.pipe.bean.configuration;

import com.gitee.starblues.realize.BasePlugin;

/**
 * 配置解析者
 * @author zhangzhuo
 * @version 1.0
 */
public interface ConfigurationParser {

    /**
     * 配置解析
     * @param basePlugin 插件信息
     * @param pluginConfigDefinition 插件配置定义
     * @return 解析后映射值的对象
     * @throws Exception 抛出配置解析异常
     */
    Object parse(BasePlugin basePlugin, PluginConfigDefinition pluginConfigDefinition) throws Exception;

}
