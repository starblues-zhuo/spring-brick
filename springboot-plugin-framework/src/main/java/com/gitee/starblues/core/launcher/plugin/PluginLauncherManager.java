package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginLauncherManager {


    void start(PluginDescriptor pluginDescriptor) throws Exception;


    void stop(String pluginId) throws Exception;


}
