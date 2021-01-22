package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;

/**
 * @author starBlues
 * @version 1.0
 */
public interface PluginBeanRegistrar {

    /**
     * 初始化
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;


    /**
     * 处理该插件的注册
     * @throws Exception 处理异常
     */
    void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception;


}
