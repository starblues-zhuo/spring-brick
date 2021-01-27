package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginInfoContainers;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginBeanRegistrar {


    /**
     * 处理该插件的注册
     * @param pluginRegistryInfo 插件注册的信息
     * @throws Exception 处理异常
     */
    void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception;

    /**
     * 处理该插件的卸载
     * @param pluginRegistryInfo 插件注册的信息
     * @throws Exception 处理异常
     */
    default void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception{}

}
