package com.plugin.development.context;

import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.realize.PluginApplicationContext;
import org.springframework.context.ApplicationContext;

/**
 * @Description: 插件 ApplicationContext 工厂
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-28 16:52
 * @Update Date Time:
 * @see
 */
public interface PluginContextFactory extends PluginContextListener{

    /**
     * 注册插件
     * @param pluginId 插件id
     * @param pluginApplicationContext 插件实现的 PluginApplicationContext 接口
     * @throws PluginBeanFactoryException
     */
    void registry(String pluginId, PluginApplicationContext pluginApplicationContext) throws PluginBeanFactoryException;

    /**
     * 卸载插件
     * @param pluginId 插件id
     * @throws PluginBeanFactoryException
     */
    void unRegistry(String pluginId) throws PluginBeanFactoryException;

    /**
     * 当前工厂支持的ApplicationContext
     * @return
     */
    Class<? extends ApplicationContext> supportApplicationContextClass();

}
