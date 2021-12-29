package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.spring.ApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginRegistryInfo {

    Object getApplicationContext();

    PluginLauncher getPluginLauncher();

}
