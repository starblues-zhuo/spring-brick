package com.gitee.starblues.spring;

import com.gitee.starblues.core.loader.PluginWrapper;
import com.gitee.starblues.factory.PluginFactory;

import java.util.Map;

/**
 * @author starBlues
 * @version 3.0.0
 */
public interface SpringPlugin {

    /**
     * 注册插件
     * @param pluginWrapper pluginWrapper
     * @throws Exception Exception
     */
    void registry(PluginWrapper pluginWrapper) throws Exception;

    /**
     * 卸载插件
     * @param pluginId 插件id
     * @throws Exception Exception
     */
    void unRegistry(String pluginId) throws Exception;

    /**
     * 获取当前注册的插件信息
     * @return Map
     */
    Map<String, SpringPluginRegistryInfo> getPluginRegistryInfos();


}
