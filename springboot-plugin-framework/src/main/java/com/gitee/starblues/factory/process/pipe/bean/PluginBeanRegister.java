package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;

/**
 * 插件bean注册者
 *
 * @author zhangzhuo
 * @version 2.1.0
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
     * @throws Exception 插件bean工厂异常
     */
    T registry(PluginRegistryInfo registerPluginInfo) throws Exception;

    /**
     * 卸载插件中的bean
     * @param registerPluginInfo 插件信息
     * @param t 注册时返回的参数
     * @throws Exception 插件bean工厂异常
     */
    void unRegistry(PluginRegistryInfo registerPluginInfo, T t) throws Exception;


}
