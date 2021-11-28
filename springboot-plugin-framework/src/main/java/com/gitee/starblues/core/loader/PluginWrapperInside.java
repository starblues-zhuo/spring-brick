package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.spring.PluginSpringApplication;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.nio.file.Path;

/**
 * 内部的
 * @author starBlues
 */
public class PluginWrapperInside implements PluginWrapper{

    private final String pluginId;
    private final PluginDescriptor pluginDescriptor;
    private final ClassLoader pluginClassLoader;
    private final Class<?> pluginClass;
    private final Path pluginPath;
    private final PluginSpringApplication pluginSpringApplication;
    private PluginState pluginState;

    public PluginWrapperInside(String pluginId, PluginDescriptor pluginDescriptor,
                               ClassLoader pluginClassLoader, Class<?> pluginClass,
                               Path pluginPath, PluginSpringApplication pluginSpringApplication) {
        this.pluginId = pluginId;
        this.pluginDescriptor = pluginDescriptor;
        this.pluginClassLoader = pluginClassLoader;
        this.pluginClass = pluginClass;
        this.pluginPath = pluginPath;
        this.pluginSpringApplication = pluginSpringApplication;
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
    public ClassLoader getPluginClassLoader() {
        return pluginClassLoader;
    }

    @Override
    public Class<?> getPluginClass() {
        return pluginClass;
    }

    @Override
    public Path getPluginPath() {
        return pluginPath;
    }

    @Override
    public PluginSpringApplication getPluginApplicationContext() {
        return pluginSpringApplication;
    }

    @Override
    public PluginState getPluginState() {
        return pluginState;
    }


}
