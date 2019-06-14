package com.plugin.development.context.factory;

import com.plugin.development.exception.PluginBeanFactoryException;

/**
 * 插件bean注册者接口
 * @author zhangzhuo
 * @version 1.0
 * @see com.plugin.development.context.factory.PluginComponentBeanRegistry
 * @see com.plugin.development.context.factory.PluginControllerBeanRegistry
 */

public interface PluginBeanRegistry<T> {

    /**
     * 注册插件bean
     * @param object 注册的bean对象
     * @return 返回beanName
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    T registry(Object object) throws PluginBeanFactoryException;

    /**
     * 卸载bean
     * @param t  卸载bean的标识
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void unRegistry(T t) throws PluginBeanFactoryException;


}
