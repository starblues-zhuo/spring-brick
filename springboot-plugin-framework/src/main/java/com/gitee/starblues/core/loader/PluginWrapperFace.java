package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import org.apache.catalina.core.ApplicationContext;

import java.nio.file.Path;

/**
 * 外部 PluginWrapperFace
 * @author starBlues
 * @version 3.0.0
 */
public class PluginWrapperFace implements PluginWrapper{

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
    public ClassLoader getPluginClassLoader() {
        return pluginWrapper.getPluginClassLoader();
    }

    @Override
    public Class<?> getPluginClass() {
        return pluginWrapper.getPluginClass();
    }

    @Override
    public Path getPluginPath() {
        return pluginWrapper.getPluginPath();
    }

    @Override
    public ApplicationContext getPluginApplicationContext() {
        return pluginWrapper.getPluginApplicationContext();
    }

    @Override
    public PluginState getPluginState() {
        return pluginWrapper.getPluginState();
    }
}
