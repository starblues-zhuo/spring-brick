package com.gitee.starblues.factory;

/**
 * 插件bean监听者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public interface PluginListener {


    /**
     * 注册插件中的bean事件
     * @param factoryType 通知的工厂类型
     * @param pluginId 插件id
     * @throws Exception 抛出异常
     */
    void registryEvent(FactoryType factoryType, String pluginId) throws Exception;

    /**
     * 卸载插件中的bean事件
     * @param factoryType 通知的工厂类型
     * @param pluginId 插件id
     * @throws Exception 抛出异常
     */
    void unRegistryEvent(FactoryType factoryType, String pluginId) throws Exception;



}
