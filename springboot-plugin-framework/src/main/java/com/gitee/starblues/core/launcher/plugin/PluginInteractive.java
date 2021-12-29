package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.processor.invoke.InvokeSupperCache;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginInteractive {

    PluginDescriptor getPluginDescriptor();

    MainApplicationContext getMainApplicationContext();

    IntegrationConfiguration getConfiguration();

    InvokeSupperCache getInvokeSupperCache();

}
