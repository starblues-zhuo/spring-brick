package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

import java.nio.file.Path;

/**
 * 内部的
 * @author starBlues
 */
public class PluginWrapperInside implements PluginWrapper {

    private final String pluginId;
    private final PluginDescriptor pluginDescriptor;
    private final Path pluginPath;
    private PluginState pluginState;

    public PluginWrapperInside(PluginDescriptor pluginDescriptor) {
        this.pluginId = pluginDescriptor.getPluginId();
        this.pluginDescriptor = pluginDescriptor;
        this.pluginPath = pluginDescriptor.getPluginPath();
    }

    public void setPluginState(PluginState pluginState) {
        this.pluginState = pluginState;
    }

    @Override
    public String getPluginId() {
        return pluginId;
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public Path getPluginPath() {
        return pluginPath;
    }

    @Override
    public PluginState getPluginState() {
        return pluginState;
    }


}
