package com.plugin.development.integration.operator.module;

import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;


/**
 * @Description: 插件信息
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-31 11:12
 * @Update Date Time:
 * @see
 */
public class PluginInfo {



    /**
     * 插件基本信息
     */
    private PluginDescriptor pluginDescriptor;

    /**
     * 插件状态
     */
    private PluginState pluginState;

    /**
     * 插件路径
     */
    private String path;

    public PluginInfo(PluginDescriptor pluginDescriptor, PluginState pluginState, String path) {
        this.pluginDescriptor = pluginDescriptor;
        this.pluginState = pluginState;
        this.path = path;
    }

    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    public String getPluginState() {
        return pluginState.toString();
    }

    public String getPath() {
        return path;
    }
}
