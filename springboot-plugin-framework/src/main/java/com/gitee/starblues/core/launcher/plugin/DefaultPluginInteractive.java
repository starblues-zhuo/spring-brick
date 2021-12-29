package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.processor.invoke.InvokeSupperCache;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultPluginInteractive implements PluginInteractive{

    private final PluginDescriptor pluginDescriptor;
    private final MainApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;
    private final InvokeSupperCache invokeSupperCache;

    public DefaultPluginInteractive(PluginDescriptor pluginDescriptor,
                                    MainApplicationContext mainApplicationContext,
                                    IntegrationConfiguration configuration,
                                    InvokeSupperCache invokeSupperCache) {
        this.pluginDescriptor = pluginDescriptor;
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = configuration;
        this.invokeSupperCache = invokeSupperCache;
    }


    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public InvokeSupperCache getInvokeSupperCache() {
        return invokeSupperCache;
    }
}
