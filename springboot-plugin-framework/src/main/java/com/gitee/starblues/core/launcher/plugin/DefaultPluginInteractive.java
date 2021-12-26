package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.MainApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultPluginInteractive implements PluginInteractive{

    private final PluginDescriptor pluginDescriptor;
    private final MainApplicationContext mainApplicationContext;

    public DefaultPluginInteractive(PluginDescriptor pluginDescriptor, MainApplicationContext mainApplicationContext) {
        this.pluginDescriptor = pluginDescriptor;
        this.mainApplicationContext = mainApplicationContext;
    }


    @Override
    public PluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }
}
