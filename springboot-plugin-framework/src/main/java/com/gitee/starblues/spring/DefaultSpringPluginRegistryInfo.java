package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultSpringPluginRegistryInfo implements SpringPluginRegistryInfo{

    private final PluginWrapper pluginWrapper;
    private final PluginSpringApplication pluginSpringApplication;
    private final ConfigurableApplicationContext mainApplicationContext;

    public DefaultSpringPluginRegistryInfo(PluginWrapper pluginWrapper,
                                           PluginSpringApplication springApplication,
                                           ConfigurableApplicationContext mainApplicationContext) {
        this.pluginWrapper = pluginWrapper;
        this.pluginSpringApplication = springApplication;
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public PluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    @Override
    public PluginSpringApplication getPluginSpringApplication() {
        return pluginSpringApplication;
    }

    @Override
    public ConfigurableApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

}
