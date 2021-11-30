package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginRegistryInfo {

    PluginWrapper getPluginWrapper();

    PluginSpringApplication getPluginSpringApplication();

    ConfigurableApplicationContext getMainApplicationContext();
}
