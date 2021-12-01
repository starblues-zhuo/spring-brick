package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPluginRegistryInfo extends RegistryInfo{

    /**
     * 得到插件 PluginWrapper
     * @return PluginWrapper
     */
    PluginWrapper getPluginWrapper();

    /**
     * 得到当前插件的 PluginSpringApplication 实现
     * @return PluginSpringApplication
     */
    PluginSpringApplication getPluginSpringApplication();

    /**
     * 得到主程序的 ApplicationContext
     * @return ConfigurableApplicationContext
     */
    ConfigurableApplicationContext getMainApplicationContext();

    /**
     * 得到集成的配置
     * @return IntegrationConfiguration
     */
    IntegrationConfiguration getConfiguration();

}
