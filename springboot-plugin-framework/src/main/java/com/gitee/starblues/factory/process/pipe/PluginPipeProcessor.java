package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.factory.PluginRegistryInfo;

/**
 * 插件管道处理者接口
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface PluginPipeProcessor {


    /**
     * 初始化
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;


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
    void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception;


}
