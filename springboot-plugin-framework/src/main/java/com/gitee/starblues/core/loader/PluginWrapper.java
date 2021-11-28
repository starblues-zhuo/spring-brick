package com.gitee.starblues.core.loader;

import com.gitee.starblues.core.PluginState;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.spring.PluginSpringApplication;

import java.nio.file.Path;

/**
 * 插件包装
 * @author starBlues
 * @version 3.0.0
 */
public interface PluginWrapper {

    /**
     * 得到插件id
     * @return String
     */
    String getPluginId();

    /**
     * 得到插件描述
     * @return PluginDescriptor
     */
    PluginDescriptor getPluginDescriptor();

    /**
     * 得到插件classLoader
     * @return ClassLoader
     */
    ClassLoader getPluginClassLoader();

    /**
     * 得到插件引导类
     * @return Class
     */
    Class<?> getPluginClass();

    /**
     * 得到插件路径
     * @return Path
     */
    Path getPluginPath();

    /**
     * 得到插件 SpringApplication
     * @return PluginSpringApplication
     */
    PluginSpringApplication getPluginApplicationContext();

    /**
     * 得到插件状态
     * @return PluginState
     */
    PluginState getPluginState();

}
