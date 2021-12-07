package com.gitee.starblues.spring.oneself;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.DefaultRegistryInfo;
import com.gitee.starblues.spring.PluginSpringApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class OneselfSpringPluginRegistryInfo extends DefaultRegistryInfo implements SpringPluginRegistryInfo {


    private final PluginSpringApplication pluginSpringApplication;
    private final PluginWrapper pluginWrapper;
    private final ConfigurableApplicationContext applicationContext;
    private final IntegrationConfiguration configuration;

    public OneselfSpringPluginRegistryInfo(PluginWrapper pluginWrapper,
                                           PluginSpringApplication pluginSpringApplication,
                                           ConfigurableApplicationContext applicationContext,
                                           IntegrationConfiguration configuration) {
        this.pluginWrapper = pluginWrapper;
        this.pluginSpringApplication = pluginSpringApplication;
        this.applicationContext = applicationContext;
        this.configuration = configuration;
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
        return applicationContext;
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }
}
