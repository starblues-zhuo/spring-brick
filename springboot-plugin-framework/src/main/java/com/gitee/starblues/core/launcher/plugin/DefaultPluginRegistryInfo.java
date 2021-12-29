package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.spring.GenericApplicationContextReflection;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultPluginRegistryInfo extends CacheRegistryInfo implements PluginRegistryInfo{

    private final ApplicationContext applicationContext;
    private final PluginLauncher pluginLauncher;

    public DefaultPluginRegistryInfo(GenericApplicationContext pluginApplicationContext,
                                     PluginLauncher pluginLauncher) {
        this.applicationContext = new GenericApplicationContextReflection(pluginApplicationContext);
        this.pluginLauncher = pluginLauncher;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public PluginLauncher getPluginLauncher() {
        return pluginLauncher;
    }

    @Override
    public void clearRegistryInfo() {
        super.clearRegistryInfo();
        pluginLauncher.clear();
    }
}
