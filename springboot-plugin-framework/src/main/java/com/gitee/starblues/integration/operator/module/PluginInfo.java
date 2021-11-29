package com.gitee.starblues.integration.operator.module;


import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * 插件信息
 * @author starBlues
 * @version 1.0
 */
public class PluginInfo {

    /**
     * 插件基本信息
     */
    private final PluginDescriptor pluginDescriptor;

    /**
     * 插件状态
     */
    private final PluginState pluginState;

    /**
     * 插件路径
     */
    private final String path;

    /**
     * 运行模式
     */
    private final String runMode;


    public PluginInfo(PluginDescriptor pluginDescriptor,
                      PluginState pluginState,
                      String path,
                      String runMode) {
        this.pluginDescriptor = pluginDescriptor;
        this.pluginState = pluginState;
        this.path = path;
        this.runMode = runMode;
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

    public String getRunMode() {
        return runMode;
    }

    @Override
    public String toString() {
        return "PluginInfo{" +
                "pluginDescriptor=" + pluginDescriptor +
                ", pluginState=" + pluginState +
                ", path='" + path + '\'' +
                ", runMode='" + runMode + '\'' +
                '}';
    }
}
