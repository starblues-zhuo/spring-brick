package com.gitee.starblues.integration.operator.module;

import org.pf4j.PluginDescriptor;
import org.pf4j.PluginState;


/**
 * 插件信息
 * @author zhangzhuo
 * @version 1.0
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

    public PluginState getPluginState() {
        return pluginState;
    }

    public String getPluginStateString() {
        return pluginState.toString();
    }

    public String getPath() {
        return path;
    }
}
