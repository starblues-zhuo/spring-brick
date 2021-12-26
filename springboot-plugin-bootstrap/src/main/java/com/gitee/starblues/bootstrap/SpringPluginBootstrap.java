package com.gitee.starblues.bootstrap;

import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public abstract class SpringPluginBootstrap {

    private PluginInteractive pluginInteractive;

    public ConfigurableApplicationContext run(Class<?> primarySources, String[] args){
        return run(new Class[]{ primarySources }, args);
    }

    public ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args){
        return start(primarySources, args);
    }

    private ConfigurableApplicationContext start(Class<?>[] primarySources, String[] args){
        PluginSpringApplication springApplication = new PluginSpringApplication(pluginInteractive, primarySources);
        return springApplication.run(args);
    }

    public final void setPluginInteractive(PluginInteractive pluginInteractive) {
        this.pluginInteractive = pluginInteractive;
    }

}
