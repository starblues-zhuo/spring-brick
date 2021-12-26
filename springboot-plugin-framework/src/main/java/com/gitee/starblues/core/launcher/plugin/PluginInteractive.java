package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.spring.MainApplicationContext;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginInteractive {

    PluginDescriptor getPluginDescriptor();

    MainApplicationContext getMainApplicationContext();

}
