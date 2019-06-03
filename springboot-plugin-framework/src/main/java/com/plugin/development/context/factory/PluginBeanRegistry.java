package com.plugin.development.context.factory;

import com.plugin.development.exception.PluginBeanFactoryException;

/**
 * @Description:
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-28 15:29
 * @Update Date Time:
 * @see
 */

public interface PluginBeanRegistry<T> {

    /**
     * 注册插件bean
     * @param object
     * @return 返回beanName
     * @throws PluginBeanFactoryException
     */
    T registry(Object object) throws PluginBeanFactoryException;

    /**
     * 卸载bean
     * @param t  卸载bean的标识
     * @throws PluginBeanFactoryException
     */
    void unRegistry(T t) throws PluginBeanFactoryException;


}
