package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class SpringPluginRegistryInfo {

    private final PluginWrapper pluginWrapper;
    private final PluginSpringApplication pluginSpringApplication;
    private final GenericApplicationContext mainApplicationContext;

    public SpringPluginRegistryInfo(PluginWrapper pluginWrapper, PluginSpringApplication springApplication,
                                    GenericApplicationContext mainApplicationContext) {
        this.pluginWrapper = pluginWrapper;
        this.pluginSpringApplication = springApplication;
        this.mainApplicationContext = mainApplicationContext;
    }

    public PluginWrapper getPluginWrapper() {
        return pluginWrapper;
    }

    public PluginSpringApplication getPluginSpringApplication() {
        return pluginSpringApplication;
    }
}
