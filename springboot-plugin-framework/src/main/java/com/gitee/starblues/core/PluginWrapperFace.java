package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

import java.nio.file.Path;

/**
 * 外部 PluginWrapperFace
 * @author starBlues
 * @version 3.0.0
 */
public class PluginWrapperFace implements PluginWrapper {

    private final PluginWrapper pluginWrapper;

    public PluginWrapperFace(PluginWrapper pluginWrapper) {
        this.pluginWrapper = pluginWrapper;
    }

    @Override
    public String getPluginId() {
        return pluginWrapper.getPluginId();
    }

    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginWrapper.getPluginDescriptor();
    }

    @Override
    public Path getPluginPath() {
        return pluginWrapper.getPluginPath();
    }

    @Override
    public PluginState getPluginState() {
        return pluginWrapper.getPluginState();
    }
}
