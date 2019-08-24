package com.gitee.starblues.register.process.pipe.bean;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.register.PluginRegistryInfo;

/**
 * 插件bean注册者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginBeanRegister<T> {

    /**
     * 注册者的唯一标识
     * @return String
     */
    String key();


    /**
     * 注册插件中的bane
     * @param registerPluginInfo 插件信息
     * @return 返回注册的bean的标识。卸载时,会将该参数传入
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    T registry(PluginRegistryInfo registerPluginInfo) throws PluginBeanFactoryException;

    /**
     * 卸载插件中的bean
     * @param registerPluginInfo 插件信息
     * @param t 注册时返回的参数
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    void unRegistry(PluginRegistryInfo registerPluginInfo, T t) throws PluginBeanFactoryException;


}
