package com.gitee.starblues.factory.process.post;

import com.gitee.starblues.factory.PluginRegistryInfo;

import java.util.List;

/**
 * 插件后置处理者接口
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public interface PluginPostProcessor {

    /**
     * 初始化
     * @throws Exception 初始化异常
     */
    void initialize() throws Exception;


    /**
     * 处理该插件的注册
     * @param pluginRegistryInfos 插件注册的信息
     * @throws Exception 处理异常
     */
    void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception;


    /**
     * 处理该插件的卸载
     * @param pluginRegistryInfos 插件注册的信息
     * @throws Exception 处理异常
     */
    void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception;



}
