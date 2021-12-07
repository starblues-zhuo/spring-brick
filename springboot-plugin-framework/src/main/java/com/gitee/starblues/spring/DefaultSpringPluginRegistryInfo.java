package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultSpringPluginRegistryInfo extends DefaultRegistryInfo implements SpringPluginRegistryInfo{

    private final PluginWrapper pluginWrapper;
    private final PluginSpringApplication pluginSpringApplication;
    private final GenericApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;

    public DefaultSpringPluginRegistryInfo(PluginWrapper pluginWrapper,
                                           GenericApplicationContext mainApplicationContext) {
        this.pluginWrapper = pluginWrapper;
        this.pluginSpringApplication = new DefaultPluginSpringApplication(mainApplicationContext, this);
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);
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

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

}
