package com.plugin.development.integration.initialize;

import com.plugin.development.exception.PluginPlugException;

/**
 * 插件初始化者
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.integration.initialize.AbstractPluginInitializer
 */
public interface PluginInitializer {

    /**
     * 初始化
     * @throws PluginPlugException 插件安装异常
     */
    void initialize() throws PluginPlugException;


}
