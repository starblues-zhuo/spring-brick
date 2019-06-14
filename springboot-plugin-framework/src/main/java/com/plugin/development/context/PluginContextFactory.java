package com.plugin.development.context;

import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.realize.PluginApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * 插件 ApplicationContext 工厂
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginContextFactory extends PluginContextListener{

    /**
     * 注册插件
     * @param pluginId 插件id
     * @param pluginApplicationContext 插件实现的 PluginApplicationContext 接口
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void registry(String pluginId, PluginApplicationContext pluginApplicationContext) throws PluginBeanFactoryException;

    /**
     * 卸载插件
     * @param pluginId 插件id
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void unRegistry(String pluginId) throws PluginBeanFactoryException;

    /**
     * 当前工厂支持的ApplicationContext
     * @return 支持的插件上下文
     */
    Class<? extends ApplicationContext> supportApplicationContextClass();

}
